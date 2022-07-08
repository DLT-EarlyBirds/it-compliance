package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.RuleContract;
import com.template.states.Rule;
import com.template.states.Regulation;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UpdateRule {

    @InitiatingFlow
    @StartableByRPC
    public static class UpdateRuleInitiator extends FlowLogic<SignedTransaction> {
        // Private variables
        @NotNull
        // Name of the rule
        private final UniqueIdentifier linearId;

        private final String name;

        // The specification that details what need to be fulfilled
        private final String ruleSpecification;

        private final List<Party> involvedParties;
        private final LinearPointer<Regulation> parentRegulation;


        //public constructor
        public UpdateRuleInitiator(UniqueIdentifier linearId, String name, String ruleSpecification, List<Party> involvedParties, LinearPointer<Regulation> parentRegulation) {
            this.name = name;
            this.linearId = linearId;
            this.parentRegulation = parentRegulation;
            this.ruleSpecification = ruleSpecification;
            this.involvedParties = involvedParties;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            final Rule output = new Rule(name, ruleSpecification, this.getOurIdentity(), involvedParties, parentRegulation);

            QueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria()
                    .withUuid(Collections.singletonList(UUID.fromString(linearId.toString())))
                    .withStatus(Vault.StateStatus.UNCONSUMED)
                    .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT);

            final StateAndRef<Rule> input = getServiceHub().getVaultService().queryBy(Rule.class, inputCriteria).getStates().get(0);

            final TransactionBuilder builder = new TransactionBuilder(notary);

            builder.addInputState(input);
            builder.addOutputState(output);
            builder.addCommand(new RuleContract.Commands.UpdateRule(), getOurIdentity().getOwningKey());

            // Verify that the transaction is valid.
            builder.verify(getServiceHub());

            final SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(builder);

            return subFlow(new FinalityFlow(signedTransaction, Collections.emptyList()));
        }
    }

}
