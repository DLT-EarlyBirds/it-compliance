package com.compliance.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.compliance.contracts.SpecificClaimContract;
import com.compliance.states.ClaimTemplate;
import com.compliance.states.SpecificClaim;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This flow creates a new specific claim, and sends it to the other parties involved in the claim
 */
public class CreateSpecificClaim {

    @InitiatingFlow
    @StartableByRPC
    public static class CreateSpecificClaimInitiator extends FlowLogic<SignedTransaction> {

        private final String name;

        private final Party auditor;
        private SecureHash attachmentID;
        private final String description;
        private final Party supervisoryAuthority;
        private final UniqueIdentifier claimTemplateLinearId;
        private final List<UniqueIdentifier> supportingClaimsLinearIds;

        public CreateSpecificClaimInitiator(
                String name,
                String description,
                Party supervisoryAuthority,
                Party auditor,
                UniqueIdentifier claimTemplateLinearId,
                List<UniqueIdentifier> supportingClaimsLinearIds
        ) {
            this.name = name;
            this.description = description;
            this.auditor = auditor;
            this.supervisoryAuthority = supervisoryAuthority;
            this.claimTemplateLinearId = claimTemplateLinearId;
            this.supportingClaimsLinearIds = supportingClaimsLinearIds;
        }

        public CreateSpecificClaimInitiator(
                String name,
                String description,
                Party supervisoryAuthority,
                Party auditor,
                UniqueIdentifier claimTemplateLinearId,
                List<UniqueIdentifier> supportingClaimsLinearIds,
                SecureHash attachmentID
        ) {
            this.name = name;
            this.description = description;
            this.attachmentID = attachmentID;
            this.auditor = auditor;
            this.supervisoryAuthority = supervisoryAuthority;
            this.claimTemplateLinearId = claimTemplateLinearId;
            this.supportingClaimsLinearIds = supportingClaimsLinearIds;

        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
            TransactionBuilder txBuilder = new TransactionBuilder(notary);
            SpecificClaim output;
            if (this.attachmentID != null) {
                 output = new SpecificClaim(
                        new UniqueIdentifier(),
                        this.name,
                        this.description,
                        this.getOurIdentity(),
                        this.supervisoryAuthority,
                        this.auditor,
                        new LinearPointer<>(claimTemplateLinearId, ClaimTemplate.class),
                        this.supportingClaimsLinearIds.stream().map(claimLinearId -> new LinearPointer<>(claimLinearId, SpecificClaim.class)).collect(Collectors.toList()),
                        this.attachmentID
                );
                        txBuilder.addAttachment(this.attachmentID);
            }

            else{
                output = new SpecificClaim(
                        new UniqueIdentifier(),
                        this.name,
                        this.description,
                        this.getOurIdentity(),
                        this.supervisoryAuthority,
                        this.auditor,
                        new LinearPointer<>(claimTemplateLinearId, ClaimTemplate.class),
                        this.supportingClaimsLinearIds.stream().map(claimLinearId -> new LinearPointer<>(claimLinearId, SpecificClaim.class)).collect(Collectors.toList())
                );
            }
            txBuilder.addOutputState(output);

            txBuilder.addCommand(
                    new SpecificClaimContract.Commands.CreateClaim(),
                    Arrays.asList(
                            getOurIdentity().getOwningKey(),
                            supervisoryAuthority.getOwningKey(),
                            auditor.getOwningKey()
                    )
            );

            // Verify that the transaction is valid.
            txBuilder.verify(getServiceHub());

            // Sign the transaction.
            final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(txBuilder);

            List<Party> otherParties = output.getParticipants().stream().map(el -> (Party) el).collect(Collectors.toList());
            otherParties.remove(getOurIdentity());

            // Send the state to the counterparty, and receive it back with their signature.
            List<FlowSession> otherPartySessions = otherParties.stream().map(this::initiateFlow).collect(Collectors.toList());
            final SignedTransaction fullySignedTx = subFlow(
                    new CollectSignaturesFlow(partSignedTx, otherPartySessions));

            // Notarise and record the transaction in both parties' vaults.
            return subFlow(new FinalityFlow(fullySignedTx, otherPartySessions));
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
                protected void checkTransaction(@NotNull SignedTransaction stx) {
                }
            });

            //Stored the transaction into data base.
            subFlow(new ReceiveFinalityFlow(counterpartySession, signedTransaction.getId()));
            return null;
        }
    }
}