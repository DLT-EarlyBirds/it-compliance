package com.compliance.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.compliance.contracts.ClaimTemplateContract;
import com.compliance.states.ClaimTemplate;
import com.compliance.states.ClaimTemplateSuggestion;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class AcceptClaimTemplateSuggestion {

    @InitiatingFlow
    @StartableByRPC
    public static class AcceptClaimTemplateSuggestionInitiator extends FlowLogic<SignedTransaction> {

        @NotNull
        private final UniqueIdentifier linearId;

        public AcceptClaimTemplateSuggestionInitiator(@NotNull UniqueIdentifier linearId) {
            this.linearId = linearId;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
            final TransactionBuilder builder = new TransactionBuilder(notary);
            try {
                QueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria()
                        .withUuid(Collections.singletonList(UUID.fromString(linearId.toString())))
                        .withStatus(Vault.StateStatus.UNCONSUMED)
                        .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT);

                final StateAndRef<ClaimTemplateSuggestion> input = getServiceHub().getVaultService().queryBy(ClaimTemplateSuggestion.class, inputCriteria).getStates().get(0);
                builder.addInputState(input);

            // Add all parties in the network
            final List<Party> involvedParties = getServiceHub().getNetworkMapCache().getAllNodes().stream().map(NodeInfo::getLegalIdentities).collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList());

            // Remove yourself
            involvedParties.remove(getOurIdentity());
            // Remove notaries
            involvedParties.removeAll(getServiceHub().getNetworkMapCache().getNotaryIdentities());

            ClaimTemplateSuggestion originalClaimTemplateSuggestion = (ClaimTemplateSuggestion) input.getState().getData();

            final ClaimTemplate output = new ClaimTemplate(originalClaimTemplateSuggestion.getName(), originalClaimTemplateSuggestion.getTemplateDescription(), this.getOurIdentity(), involvedParties, originalClaimTemplateSuggestion.getRule());


            builder.addOutputState(output);
            builder.addCommand(new ClaimTemplateContract.Commands.AcceptClaimTemplateSuggestion(),
                    involvedParties.stream().map(Party::getOwningKey).collect(Collectors.toList())
            );

            // Verify that the transaction is valid.
            builder.verify(getServiceHub());

            final SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(builder);

            involvedParties.remove(getOurIdentity());

            List<FlowSession> sessions = involvedParties.stream().map(this::initiateFlow).collect(Collectors.toList());

            SignedTransaction stx = subFlow(new CollectSignaturesFlow(signedTransaction, sessions));

            return subFlow(new FinalityFlow(stx, sessions));
            }

            catch (IndexOutOfBoundsException e) {
                throw new FlowException("ERROR: No ClaimTemplateSuggestion with provided ID found!");
            }

        }
    }

    @InitiatedBy(AcceptClaimTemplateSuggestion.AcceptClaimTemplateSuggestionInitiator.class)
    public static class AcceptClaimTemplateSuggestionResponder extends FlowLogic<Void> {
        //private variable
        private final FlowSession counterpartySession;

        //Constructor
        public AcceptClaimTemplateSuggestionResponder(FlowSession counterpartySession) {
            this.counterpartySession = counterpartySession;
        }

        @Suspendable
        @Override
        public Void call() throws FlowException {
            SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(counterpartySession) {
                @Suspendable
                @Override
                protected void checkTransaction(SignedTransaction stx) {
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