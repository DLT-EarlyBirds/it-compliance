package com.compliance.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.compliance.contracts.RegulationContract;
import com.compliance.states.Regulation;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.Collections;
import java.util.Date;



/**
 * This flow creates a new regulation and adds it to the ledger
 */
public class CreateRegulation {

    @InitiatingFlow
    @StartableByRPC
    public static class CreateRegulationInitiator extends FlowLogic<SignedTransaction>{
        // Private variables
        private final String name;
        private final String description;
        private final String version;
        private final Date releaseDate;

        //public constructor
        public CreateRegulationInitiator(String name, String description, String version, Date releaseDate) {
            this.version = version;
            this.releaseDate = releaseDate;
            this.description = description;
            this.name = name;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            final Regulation output = new Regulation(name, description, version, releaseDate, this.getOurIdentity());

            final TransactionBuilder builder = new TransactionBuilder(notary);

            builder.addOutputState(output);
            builder.addCommand(new RegulationContract.Commands.CreateRegulation(), getOurIdentity().getOwningKey());


            // Verify that the transaction is valid.
            builder.verify(getServiceHub());

            final SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(builder);

            return subFlow(new FinalityFlow(signedTransaction, Collections.emptyList()));
        }
    }
}