package com.compliance.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.compliance.contracts.SpecificClaimContract;
import com.compliance.states.ClaimTemplate;
import com.compliance.states.SpecificClaim;
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

/**
 * This flow is used to update a specific claim
 */
public class UpdateSpecificClaim {

    @InitiatingFlow
    @StartableByRPC
    public static class UpdateSpecificClaimInitiator extends FlowLogic<SignedTransaction> {

        private final String name;

        private final Party auditor;

        private SecureHash attachmentID;

        @NotNull
        private final UniqueIdentifier specificClaimLinearId;

        private final List<UniqueIdentifier> supportingClaimsLinearIds;

        private final UniqueIdentifier claimTemplateLinearId;

        private final Party supervisoryAuthority;

        // The specification that details what need to be fulfilled
        private final String description;

        public UpdateSpecificClaimInitiator(
                @NotNull UniqueIdentifier specificClaimLinearId,
                String name,
                String description,
                Party supervisoryAuthority,
                Party auditor,
                UniqueIdentifier claimTemplateLinearId,
                List<UniqueIdentifier> supportingClaimsLinearIds
        ) {
            this.name = name;
            this.auditor = auditor;
            this.specificClaimLinearId = specificClaimLinearId;
            this.description = description;
            this.supervisoryAuthority = supervisoryAuthority;
            this.claimTemplateLinearId = claimTemplateLinearId;
            this.supportingClaimsLinearIds = supportingClaimsLinearIds;
        }

        public UpdateSpecificClaimInitiator(
                @NotNull UniqueIdentifier specificClaimLinearId,
                String name,
                String description,
                Party supervisoryAuthority,
                Party auditor,
                UniqueIdentifier claimTemplateLinearId,
                List<UniqueIdentifier> supportingClaimsLinearIds,
                SecureHash attachmentID
        ) {
            this.name = name;
            this.auditor = auditor;
            this.specificClaimLinearId = specificClaimLinearId;
            this.description = description;
            this.supervisoryAuthority = supervisoryAuthority;
            this.claimTemplateLinearId = claimTemplateLinearId;
            this.supportingClaimsLinearIds = supportingClaimsLinearIds;
            this.attachmentID = attachmentID;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            final TransactionBuilder builder = new TransactionBuilder(notary);

            try {
                QueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria()
                        .withUuid(Collections.singletonList(UUID.fromString(specificClaimLinearId.toString())))
                        .withStatus(Vault.StateStatus.UNCONSUMED)
                        .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT);

                final StateAndRef<SpecificClaim> input = getServiceHub().getVaultService().queryBy(SpecificClaim.class, inputCriteria).getStates().get(0);
                builder.addInputState(input);
            } catch (IndexOutOfBoundsException e) {
                throw new FlowException("ERROR: No SpecificClaim with provided ID found!");
            }

            SpecificClaim output;

            if (this.attachmentID != null) {
                output = new SpecificClaim(
                        specificClaimLinearId,
                        this.name,
                        this.description,
                        this.getOurIdentity(),
                        this.supervisoryAuthority,
                        this.auditor,
                        new LinearPointer<>(claimTemplateLinearId, ClaimTemplate.class),
                        this.supportingClaimsLinearIds
                                .stream()
                                .map(
                                        claimLinearId -> new LinearPointer<>(
                                                claimLinearId,
                                                SpecificClaim.class
                                        )
                                )
                                .collect(Collectors.toList()),
                        this.attachmentID
                );

            } else {
                output = new SpecificClaim(
                        specificClaimLinearId,
                        this.name,
                        this.description,
                        this.getOurIdentity(),
                        this.supervisoryAuthority,
                        this.auditor,
                        new LinearPointer<>(claimTemplateLinearId, ClaimTemplate.class),
                        this.supportingClaimsLinearIds
                                .stream()
                                .map(
                                        claimLinearId -> new LinearPointer<>(
                                                claimLinearId,
                                                SpecificClaim.class
                                        )
                                )
                                .collect(Collectors.toList())
                );
            }

            builder.addOutputState(output);
            if (attachmentID != null) {
                builder.addAttachment(attachmentID);
            }
            builder.addCommand(
                    new SpecificClaimContract.Commands.UpdateSpecificClaim(),
                    Arrays.asList(
                            getOurIdentity().getOwningKey(),
                            supervisoryAuthority.getOwningKey(),
                            auditor.getOwningKey()
                    )
            );

            // Verify that the transaction is valid.
            builder.verify(getServiceHub());

            // Sign the transaction.
            final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(builder);

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

    @InitiatedBy(UpdateSpecificClaim.UpdateSpecificClaimInitiator.class)
    public static class UpdateSpecificClaimResponder extends FlowLogic<Void> {
        private final FlowSession counterpartySession;

        public UpdateSpecificClaimResponder(FlowSession counterpartySession) {
            this.counterpartySession = counterpartySession;
        }

        @Suspendable
        @Override
        public Void call() throws FlowException {
            SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(counterpartySession) {
                @Suspendable
                @Override
                protected void checkTransaction(@NotNull SignedTransaction stx) {
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