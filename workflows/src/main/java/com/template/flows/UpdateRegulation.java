package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.RegulationContract;
import com.template.states.Regulation;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.*;

//Initiate this flow:
//flow start CreateRegulation description: "test_description", supervisoryAuthority: Supervisory Authority

//Check if added to ledger:
//run vaultQuery contractStateType: com.template.states.Regulation

public class UpdateRegulation {

    @InitiatingFlow
    @StartableByRPC
    public static class UpdateRegulationInitiator extends FlowLogic<SignedTransaction>{
        // Private variables

        private final UniqueIdentifier linearId;
        private final String name;
        private final String description;
        private final String version;
        private final Date releaseDate;
        private final boolean isDeprecated;

        //public constructor
        public UpdateRegulationInitiator(UniqueIdentifier linearId, String name, String description, String version, Date releaseDate, boolean isDeprecated) {
            this.linearId = linearId;
            this.version = version;
            this.releaseDate = releaseDate;
            this.description = description;
            this.name = name;
            this.isDeprecated = isDeprecated;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            final Regulation output = new Regulation(name, description, version, releaseDate, this.getOurIdentity(), isDeprecated);

            QueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria()
                    .withUuid(Collections.singletonList(UUID.fromString(linearId.toString())))
                    .withStatus(Vault.StateStatus.UNCONSUMED)
                    .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT);

            final StateAndRef<Regulation> input = getServiceHub().getVaultService().queryBy(Regulation.class, inputCriteria).getStates().get(0);

            final TransactionBuilder builder = new TransactionBuilder(notary);

            builder.addInputState(input);
            builder.addOutputState(output);
            builder.addCommand(new RegulationContract.Commands.CreateRegulation(), getOurIdentity().getOwningKey());

            // Verify that the transaction is valid.
            builder.verify(getServiceHub());

            final SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(builder);

            return subFlow(new FinalityFlow(signedTransaction, Collections.emptyList()));
        }
    }

}