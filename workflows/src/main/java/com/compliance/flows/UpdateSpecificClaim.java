package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.SpecificClaimContract;
import com.template.states.ClaimTemplate;
import com.template.states.SpecificClaim;
import com.template.states.Rule;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class UpdateSpecificClaim {

    @InitiatingFlow
    @StartableByRPC
    public static class UpdateSpecificClaimInitiator extends FlowLogic<SignedTransaction> {
        // Private variables
        
        // Name of the SpecificClaim
        private final String name;

        private SecureHash attachmentID;
        @NotNull
        private final UniqueIdentifier specificClaimLinearId;

        private final List<UniqueIdentifier> supportingClaimsLinearIds;

        private final UniqueIdentifier claimTemplateLinearId;

        private final Party supervisorAuthority;
        
        // The specification that details what need to be fulfilled
        private final String description;

        //public constructor
        public UpdateSpecificClaimInitiator(UniqueIdentifier specificClaimLinearId,
                                            String name,
                                            String description,
                                            Party supervisoryAuthority,
                                            UniqueIdentifier claimTemplateLinearId,
                                            List<UniqueIdentifier> supportingClaimsLinearIds
                                            ) {
            this.name = name;
            this.specificClaimLinearId = specificClaimLinearId;
            this.description = description;
            this.supervisorAuthority = supervisoryAuthority;
            this.claimTemplateLinearId = claimTemplateLinearId;
            this.supportingClaimsLinearIds = supportingClaimsLinearIds;
        }
        public UpdateSpecificClaimInitiator(UniqueIdentifier specificClaimLinearId,
                                            String name,
                                            String description,
                                            Party supervisoryAuthority,
                                            UniqueIdentifier claimTemplateLinearId,
                                            List<UniqueIdentifier> supportingClaimsLinearIds,
                                            SecureHash attachmentID
        ) {
            this.name = name;
            this.specificClaimLinearId = specificClaimLinearId;
            this.description = description;
            this.supervisorAuthority = supervisoryAuthority;
            this.claimTemplateLinearId = claimTemplateLinearId;
            this.supportingClaimsLinearIds = supportingClaimsLinearIds;
            this.attachmentID = attachmentID;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);


            QueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria()
                    .withUuid(Collections.singletonList(UUID.fromString(specificClaimLinearId.toString())))
                    .withStatus(Vault.StateStatus.UNCONSUMED)
                    .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT);

            final StateAndRef<SpecificClaim> input = getServiceHub().getVaultService().queryBy(SpecificClaim.class, inputCriteria).getStates().get(0);
            final TransactionBuilder builder = new TransactionBuilder(notary);
            builder.addInputState(input);

            if (this.attachmentID != null) {
                SpecificClaim output = new SpecificClaim(
                        specificClaimLinearId,
                        this.name,
                        this.description,
                        this.getOurIdentity(),
                        this.supervisorAuthority,
                        new LinearPointer<>(claimTemplateLinearId, ClaimTemplate.class),
                        this.supportingClaimsLinearIds.stream().map(claimLinearId -> new LinearPointer<>(claimLinearId, SpecificClaim.class)).collect(Collectors.toList()),
                        this.attachmentID
                );
                builder.addOutputState(output);
                builder.addAttachment(attachmentID);
                builder.addCommand(
                        new SpecificClaimContract.Commands.CreateClaim(),
                        Arrays.asList(
                                getOurIdentity().getOwningKey(),
                                supervisorAuthority.getOwningKey()
                        )
                );
            }
            else {
                SpecificClaim output = new SpecificClaim(
                        specificClaimLinearId,
                        this.name,
                        this.description,
                        this.getOurIdentity(),
                        this.supervisorAuthority,
                        new LinearPointer<>(claimTemplateLinearId, ClaimTemplate.class),
                        this.supportingClaimsLinearIds.stream().map(claimLinearId -> new LinearPointer<>(claimLinearId, SpecificClaim.class)).collect(Collectors.toList())
                );
                builder.addOutputState(output);
                builder.addCommand(
                        new SpecificClaimContract.Commands.CreateClaim(),
                        Arrays.asList(
                                getOurIdentity().getOwningKey(),
                                supervisorAuthority.getOwningKey()
                        )
                );

            }



            // Verify that the transaction is valid.
            builder.verify(getServiceHub());

            // Sign the transaction.
            final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(builder);

            // Send the state to the counterparty, and receive it back with their signature.
            FlowSession otherPartySession = initiateFlow(supervisorAuthority);
            final SignedTransaction fullySignedTx = subFlow(
                    new CollectSignaturesFlow(partSignedTx, Collections.singletonList(otherPartySession)));

            // Notarise and record the transaction in both parties' vaults.
            return subFlow(new FinalityFlow(fullySignedTx, Collections.singletonList(otherPartySession)));
        }
    }

    @InitiatedBy(UpdateSpecificClaim.UpdateSpecificClaimInitiator.class)
    public static class UpdateSpecificClaimResponder extends FlowLogic<Void> {
        //private variable
        private final FlowSession counterpartySession;

        //Constructor
        public UpdateSpecificClaimResponder(FlowSession counterpartySession) {
            this.counterpartySession = counterpartySession;
        }

        @Suspendable
        @Override
        public Void call() throws FlowException {
            SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(counterpartySession) {
                @Suspendable
                @Override
                protected void checkTransaction(SignedTransaction stx) throws FlowException {
                    /*
                     * SignTransactionFlow will automatically verify the transaction and its signatures before signing it.
                     * However, just because a transaction is contractually valid doesn’t mean we necessarily want to sign.
                     * What if we don’t want to deal with the counterparty in question, or the value is too high,
                     * or we’re not happy with the transaction’s structure? checkTransaction
                     * allows us to define these additional checks. If any of these conditions are not met,
                     * we will not sign the transaction - even if the transaction and its signatures are contractually valid.
                     * */
                }
            });
            // Stored the transaction into database.
            subFlow(new ReceiveFinalityFlow(counterpartySession, signedTransaction.getId()));
            return null;
        }
    }


}