package com.compliance.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.compliance.contracts.RegulationContract;
import com.compliance.states.Regulation;
import com.compliance.states.Rule;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.node.services.vault.QueryCriteriaUtils;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.*;

public class DeprecateRegulation {

    @InitiatingFlow
    @StartableByRPC
    public static class DeprecateRegulationInitiator extends FlowLogic<SignedTransaction>{

        private final UniqueIdentifier linearId;

        //public constructor
        public DeprecateRegulationInitiator(UniqueIdentifier linearId) {
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

            final StateAndRef<Regulation> input = getServiceHub().getVaultService().queryBy(Regulation.class, inputCriteria).getStates().get(0);
            Regulation originalRegulation = input.getState().getData();


            // Use the same linearID as the input Regulation
            final Regulation output = new Regulation(linearId,
                    originalRegulation.getName(),
                    originalRegulation.getDescription(),
                    originalRegulation.getVersion(),
                    originalRegulation.getReleaseDate(),
                    this.getOurIdentity(),
                    true);

            final TransactionBuilder builder = new TransactionBuilder(notary);

            QueryCriteria rulesInputCriteria = new QueryCriteria.LinearStateQueryCriteria()
                    .withStatus(Vault.StateStatus.UNCONSUMED)
                    .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT);

            // Find related rules
            final List<StateAndRef<Rule>> relatedRules = getServiceHub().getVaultService().queryBy(Rule.class, rulesInputCriteria).getStates();

            for (StateAndRef<Rule> tempRule : relatedRules) {
                Rule originalRule = tempRule.getState().getData();
                Regulation parentRegulation = originalRule.getParentRegulation().resolve(getServiceHub()).getState().getData();

                // Deprecate rules related to the given regulation
                if (parentRegulation.getLinearId().equals(linearId))
                {
                    subFlow(new DeprecateRule.DeprecateRuleInitiator(originalRule.getLinearId()));
                }
            }

            builder.addInputState(input);
            builder.addOutputState(output);

            builder.addCommand(new RegulationContract.Commands.DeprecateRegulation(), getOurIdentity().getOwningKey());

            // Verify that the transaction is valid.
            builder.verify(getServiceHub());

            final SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(builder);

            return subFlow(new FinalityFlow(signedTransaction, Collections.emptyList()));
        }
    }

}