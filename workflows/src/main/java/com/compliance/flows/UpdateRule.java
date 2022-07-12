package com.compliance.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.compliance.contracts.RuleContract;
import com.compliance.states.Rule;
import com.compliance.states.Regulation;
import net.corda.core.contracts.LinearPointer;
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

public class UpdateRule {

    @InitiatingFlow
    @StartableByRPC
    public static class UpdateRuleInitiator extends FlowLogic<SignedTransaction> {
        @NotNull
        // Name of the rule
        private final UniqueIdentifier linearId;

        private final String name;

        // The specification that details what need to be fulfilled
        private final String ruleSpecification;

        private final UniqueIdentifier parentRegulationLinearId;


        public UpdateRuleInitiator(UniqueIdentifier linearId, String name, String ruleSpecification, UniqueIdentifier parentRegulationLinearId) {
            this.name = name;
            this.linearId = linearId;
            this.parentRegulationLinearId = parentRegulationLinearId;
            this.ruleSpecification = ruleSpecification;
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

                final StateAndRef<Rule> input = getServiceHub().getVaultService().queryBy(Rule.class, inputCriteria).getStates().get(0);
                Rule originalRule = input.getState().getData();
                builder.addInputState(input);


                // Add all parties in the network
                final List<Party> involvedParties = getServiceHub()
                        .getNetworkMapCache()
                        .getAllNodes()
                        .stream()
                        .map(NodeInfo::getLegalIdentities)
                        .collect(Collectors.toList())
                        .stream()
                        .flatMap(List::stream).collect(Collectors.toList());

                // Remove yourself
                involvedParties.remove(getOurIdentity());
                // Remove notaries
                involvedParties.removeAll(getServiceHub().getNetworkMapCache().getNotaryIdentities());

                final Rule output = new Rule(linearId, name, ruleSpecification, this.getOurIdentity(), involvedParties, new LinearPointer<>(parentRegulationLinearId, Regulation.class), originalRule.getIsDeprecated());


                builder.addOutputState(output);
                builder.addCommand(new RuleContract.Commands.UpdateRule(),
                        involvedParties.stream().map(Party::getOwningKey).collect(Collectors.toList())
                );


                // Verify that the transaction is valid.
                builder.verify(getServiceHub());

                final SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(builder);

                involvedParties.remove(getOurIdentity());

                List<FlowSession> sessions = involvedParties.stream().map(this::initiateFlow).collect(Collectors.toList());

                SignedTransaction stx = subFlow(new CollectSignaturesFlow(signedTransaction, sessions));

                return subFlow(new FinalityFlow(stx, sessions));

            } catch (IndexOutOfBoundsException e) {
                throw new FlowException("ERROR: No Rule with provided ID found!");
            }
        }
    }


    @InitiatedBy(UpdateRule.UpdateRuleInitiator.class)
    public static class UpdateRuleResponder extends FlowLogic<Void> {
        //private variable
        private final FlowSession counterpartySession;

        //Constructor
        public UpdateRuleResponder(FlowSession counterpartySession) {
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
