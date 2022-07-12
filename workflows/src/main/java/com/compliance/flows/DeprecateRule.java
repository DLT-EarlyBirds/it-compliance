package com.compliance.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.compliance.contracts.RuleContract;
import com.compliance.states.Rule;
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

public class DeprecateRule {

    @InitiatingFlow
    @StartableByRPC
    public static class DeprecateRuleInitiator extends FlowLogic<SignedTransaction> {

        @NotNull
        private final UniqueIdentifier linearId;

        public DeprecateRuleInitiator(UniqueIdentifier linearId) {
            this.linearId = linearId;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            QueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria()
                    .withUuid(Collections.singletonList(UUID.fromString(linearId.toString())))
                    .withStatus(Vault.StateStatus.UNCONSUMED)
                    .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT);

            final StateAndRef<Rule> input = getServiceHub().getVaultService().queryBy(Rule.class, inputCriteria).getStates().get(0);
            Rule originalRule = input.getState().getData();

            final List<Party> involvedParties = getServiceHub().getNetworkMapCache().getAllNodes().stream().map(NodeInfo::getLegalIdentities).collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList());

            // Remove yourself
            involvedParties.remove(getOurIdentity());
            // Remove notaries
            involvedParties.removeAll(getServiceHub().getNetworkMapCache().getNotaryIdentities());

            final Rule output = new Rule(linearId, originalRule.getName(), originalRule.getRuleSpecification(), this.getOurIdentity(), involvedParties, originalRule.getParentRegulation(), true);

            final TransactionBuilder builder = new TransactionBuilder(notary);

            builder.addInputState(input);
            builder.addOutputState(output);
            builder.addCommand(new RuleContract.Commands.DeprecateRule(),
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
    }


    @InitiatedBy(DeprecateRule.DeprecateRuleInitiator.class)
    public static class DeprecateRuleResponder extends FlowLogic<Void> {
        //private variable
        private final FlowSession counterpartySession;

        //Constructor
        public DeprecateRuleResponder(FlowSession counterpartySession) {
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
