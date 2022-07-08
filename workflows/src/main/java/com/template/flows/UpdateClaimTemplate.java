package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.ClaimTemplateContract;
import com.template.states.ClaimTemplate;
import com.template.states.Rule;
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

public class UpdateClaimTemplate {

    @InitiatingFlow
    @StartableByRPC
    public static class UpdateClaimTemplateInitiator extends FlowLogic<SignedTransaction> {
        // Private variables
        @NotNull
        // Name of the ClaimTemplate
        private final UniqueIdentifier linearId;

        private final String name;

        // The specification that details what need to be fulfilled
        private final String templateDescription;

        private final List<Party> involvedParties;
        private final LinearPointer<Rule> rule;


        //public constructor
        public UpdateClaimTemplateInitiator(UniqueIdentifier linearId, String name, String templateDescription, List<Party> involvedParties, LinearPointer<Rule> rule) {
            this.name = name;
            this.linearId = linearId;
            this.rule = rule;
            this.templateDescription = templateDescription;
            this.involvedParties = involvedParties;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            final ClaimTemplate output = new ClaimTemplate(name, templateDescription, this.getOurIdentity(), involvedParties, rule);

            QueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria()
                    .withUuid(Collections.singletonList(UUID.fromString(linearId.toString())))
                    .withStatus(Vault.StateStatus.UNCONSUMED)
                    .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT);

            final StateAndRef<ClaimTemplate> input = getServiceHub().getVaultService().queryBy(ClaimTemplate.class, inputCriteria).getStates().get(0);

            final TransactionBuilder builder = new TransactionBuilder(notary);

            builder.addInputState(input);
            builder.addOutputState(output);
            builder.addCommand(new ClaimTemplateContract.Commands.UpdateClaimTemplate(), getOurIdentity().getOwningKey());

            // Verify that the transaction is valid.
            builder.verify(getServiceHub());

            final SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(builder);

            return subFlow(new FinalityFlow(signedTransaction, Collections.emptyList()));
        }
    }

}