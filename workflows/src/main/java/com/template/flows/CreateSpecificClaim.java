package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.SpecificClaimContract;
import com.template.states.ClaimTemplate;
import com.template.states.SpecificClaim;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CreateSpecificClaim {

    @InitiatingFlow
    @StartableByRPC
    public static class CreateSpecificClaimInitiator extends FlowLogic<SignedTransaction> {

        private final String name;
        private final Party supervisorAuthority;

        private final UniqueIdentifier claimTemplateLinearId;

        private final List<UniqueIdentifier> supportingClaimsLinearIds;

        public CreateSpecificClaimInitiator(
                String name,
                Party supervisoryAuthority,
                UniqueIdentifier claimTemplateLinearId,
                List<UniqueIdentifier> supportingClaimsLinearIds
        ) {
            this.name = name;
            this.supervisorAuthority = supervisoryAuthority;
            this.claimTemplateLinearId = claimTemplateLinearId;
            this.supportingClaimsLinearIds = supportingClaimsLinearIds;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            SpecificClaim newClaim = new SpecificClaim(
                    this.name,
                    this.getOurIdentity(),
                    this.supervisorAuthority,
                    new LinearPointer<>(claimTemplateLinearId, ClaimTemplate.class),
                    this.supportingClaimsLinearIds.stream().map(claimLinearId -> new LinearPointer<>(claimLinearId, SpecificClaim.class)).collect(Collectors.toList())
            );

            TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addOutputState(newClaim)
                    .addCommand(
                            new SpecificClaimContract.Commands.CreateClaim(),
                            Arrays.asList(
                                    getOurIdentity().getOwningKey(),
                                    supervisorAuthority.getOwningKey()
                            )
                    );

            // Verify that the transaction is valid.
            txBuilder.verify(getServiceHub());

            // Sign the transaction.
            final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(txBuilder);

            // Send the state to the counterparty, and receive it back with their signature.
            FlowSession otherPartySession = initiateFlow(supervisorAuthority);
            final SignedTransaction fullySignedTx = subFlow(
                    new CollectSignaturesFlow(partSignedTx, Collections.singletonList(otherPartySession)));

            // Notarise and record the transaction in both parties' vaults.
            return subFlow(new FinalityFlow(fullySignedTx, Collections.singletonList(otherPartySession)));
        }
    }

    @InitiatedBy(CreateSpecificClaimInitiator.class)
    public static class CreateSpecificClaimInitiatorResponder extends FlowLogic<Void> {

        //private variable
        private final FlowSession counterpartySession;

        public CreateSpecificClaimInitiatorResponder(FlowSession counterpartySession) {
            this.counterpartySession = counterpartySession;
        }

        @Override
        @Suspendable
        public Void call() throws FlowException {
            SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(counterpartySession) {
                @Override
                @Suspendable
                protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException {
                }
            });

            //Stored the transaction into data base.
            subFlow(new ReceiveFinalityFlow(counterpartySession, signedTransaction.getId()));
            return null;
        }
    }

}