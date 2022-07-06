package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.RuleContract;
import com.template.states.Rule;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.NodeInfo;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//Initiate this flow:
//flow start CreateRegulation description: "test_description", supervisoryAuthority: Supervisory Authority

//Check if added to ledger:
//run vaultQuery contractStateType: com.template.states.Regulation

public class CreateRule {

    @InitiatingFlow
    @StartableByRPC
    public static class CreateRuleInitiator extends FlowLogic<SignedTransaction>{
        // Private variables
        private final String name;
        private final String ruleSpecification;
        private final Party issuer;

        private final UniqueIdentifier parentRegulationLinearId;

        //public constructor
        public CreateRuleInitiator(String name, String description, Party issuer, UniqueIdentifier parentRegulationLinearId) {
            this.issuer = issuer;
            this.ruleSpecification = description;
            this.name = name;
            this.parentRegulationLinearId = parentRegulationLinearId;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            final Rule output = new Rule(name, ruleSpecification, issuer, parentRegulationLinearId);

            final TransactionBuilder builder = new TransactionBuilder(notary);

            builder.addOutputState(output);
            builder.addCommand(new RuleContract.Commands.CreateRule(),
                    Arrays.asList(getOurIdentity().getOwningKey(), this.issuer.getOwningKey()));


            // Verify that the transaction is valid.
            builder.verify(getServiceHub());

            final SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(builder);

            // Broadcast transaction to all parties in the network for signing.
            List<Party> otherParties = output.getParticipants().stream().map(el -> (Party)el).collect(Collectors.toList());

            // Add all parties in the network
            otherParties.addAll(getServiceHub().getNetworkMapCache().getAllNodes().stream().map(NodeInfo::getLegalIdentities).collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList()));
            // Remove yourself
            otherParties.remove(getOurIdentity());
            // Remove notaries
            otherParties.removeAll(getServiceHub().getNetworkMapCache().getNotaryIdentities());

            List<FlowSession> sessions = otherParties.stream().map(this::initiateFlow).collect(Collectors.toList());

            SignedTransaction stx = subFlow(new CollectSignaturesFlow(signedTransaction, sessions));

            return subFlow(new FinalityFlow(stx, sessions));
        }
    }

    @InitiatedBy(CreateRuleInitiator.class)
    public static class CreateRuleResponder extends FlowLogic<Void>{
        //private variable
        private final FlowSession counterpartySession;

        //Constructor
        public CreateRuleResponder(FlowSession counterpartySession) {
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