package com.compliance.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.compliance.contracts.RegulationContract;
import com.compliance.states.Regulation;
import java.lang.IndexOutOfBoundsException;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.*;



/**
 * This flow is used to update a Regulation
 */
public class UpdateRegulation {

    @InitiatingFlow
    @StartableByRPC
    public static class UpdateRegulationInitiator extends FlowLogic<SignedTransaction>{

        private final UniqueIdentifier linearId;
        private final String name;
        private final String description;
        private final String version;
        private final Date releaseDate;

        public UpdateRegulationInitiator(UniqueIdentifier linearId, String name, String description, String version, Date releaseDate) {
            this.linearId = linearId;
            this.version = version;
            this.releaseDate = releaseDate;
            this.description = description;
            this.name = name;
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

                final StateAndRef<Regulation> input = getServiceHub().getVaultService().queryBy(Regulation.class, inputCriteria).getStates().get(0);
                Regulation originalRegulation = input.getState().getData();

                // Use the same linearID as the input Regulation
                final Regulation output = new Regulation(linearId, name, description, version, releaseDate, this.getOurIdentity(), originalRegulation.getIsDeprecated());
                builder.addInputState(input);
                builder.addOutputState(output);
            }
            catch (IndexOutOfBoundsException e) {
                throw new FlowException("ERROR: No Regulation with provided ID found!");
            }



            builder.addCommand(new RegulationContract.Commands.UpdateRegulation(), getOurIdentity().getOwningKey());

            // Verify that the transaction is valid.
            builder.verify(getServiceHub());

            final SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(builder);

            return subFlow(new FinalityFlow(signedTransaction, Collections.emptyList()));
        }
    }
}