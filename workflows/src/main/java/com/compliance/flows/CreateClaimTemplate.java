package com.compliance.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.compliance.contracts.ClaimTemplateContract;
import com.compliance.states.ClaimTemplate;
import com.compliance.states.Rule;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.NodeInfo;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.List;
import java.util.stream.Collectors;


/**
 * This Flow is used to create a claim template
 */
public class CreateClaimTemplate {

    @InitiatingFlow
    @StartableByRPC
    public static class CreateClaimTemplateInitiator extends FlowLogic<SignedTransaction> {
        private final String name;
        private final String templateDescription;
        private final UniqueIdentifier ruleLinearId;

        public CreateClaimTemplateInitiator(String name, String description, UniqueIdentifier ruleLinearId) {
            this.templateDescription = description;
            this.name = name;
            this.ruleLinearId = ruleLinearId;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            // Add all parties in the network
            final List<Party> involvedParties = getServiceHub().getNetworkMapCache().getAllNodes().stream().map(NodeInfo::getLegalIdentities).collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList());
            // Remove yourself
            involvedParties.remove(getOurIdentity());
            // Remove notaries
            involvedParties.removeAll(getServiceHub().getNetworkMapCache().getNotaryIdentities());


            final ClaimTemplate output = new ClaimTemplate(
                    new UniqueIdentifier(),
                    name,
                    templateDescription,
                    this.getOurIdentity(),
                    involvedParties,
                    new LinearPointer<>(ruleLinearId, Rule.class)
            );

            final TransactionBuilder builder = new TransactionBuilder(notary);

            builder.addOutputState(output);

            // Reuse involved parties list for signing
            involvedParties.add(getOurIdentity());
            builder.addCommand(new ClaimTemplateContract.Commands.CreateClaimTemplate(),
                    involvedParties.stream().map(Party::getOwningKey).collect(Collectors.toList())
            );

            // Verify that the transaction is valid.
            builder.verify(getServiceHub());

            final SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(builder);


            List<Party> otherParties = output.getParticipants().stream().map(el -> (Party) el).collect(Collectors.toList());

            otherParties.remove(getOurIdentity());

            List<FlowSession> sessions = otherParties.stream().map(this::initiateFlow).collect(Collectors.toList());

            SignedTransaction stx = subFlow(new CollectSignaturesFlow(signedTransaction, sessions));

            return subFlow(new FinalityFlow(stx, sessions));
        }
    }

    @InitiatedBy(CreateClaimTemplateInitiator.class)
    public static class CreateClaimTemplateResponder extends FlowLogic<Void> {
        private final FlowSession counterpartySession;

        public CreateClaimTemplateResponder(FlowSession counterpartySession) {
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