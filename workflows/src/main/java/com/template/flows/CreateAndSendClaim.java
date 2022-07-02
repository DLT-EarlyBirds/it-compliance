package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.ClaimContract;
import com.template.states.ClaimState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateAndSendClaim {

    @InitiatingFlow
    @StartableByRPC
    public static class CreateAndSendClaimInitiator  extends FlowLogic<SignedTransaction>{

        private String hashValue;
        private Party supervisorAuthority;

        public CreateAndSendClaimInitiator(String hashValue,  Party supervisorAuthority) {
            this.hashValue = hashValue;
            this.supervisorAuthority = supervisorAuthority;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0); 

            ClaimState newClaim = new ClaimState(this.hashValue,this.getOurIdentity(),this.supervisorAuthority);

            TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addOutputState(newClaim)
                    .addCommand(new ClaimContract.Commands.SendClaim(),
                            Arrays.asList(getOurIdentity().getOwningKey(),supervisorAuthority.getOwningKey()));

            // Verify that the transaction is valid.
            txBuilder.verify(getServiceHub());

            // Sign the transaction.
            final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(txBuilder);

            // Send the state to the counterparty, and receive it back with their signature.
            FlowSession otherPartySession = initiateFlow(supervisorAuthority);
            final SignedTransaction fullySignedTx = subFlow(
                    new CollectSignaturesFlow(partSignedTx, Arrays.asList(otherPartySession)));

            // Notarise and record the transaction in both parties' vaults.
            return subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession)));
        }
    }

    @InitiatedBy(CreateAndSendClaimInitiator.class)
    public static class CreateAndSendClaimInitiatorResponder extends FlowLogic<Void>{

        //private variable
        private FlowSession counterpartySession;

        public CreateAndSendClaimInitiatorResponder(FlowSession counterpartySession) {
            this.counterpartySession = counterpartySession;
        }

        @Override
        @Suspendable
        public Void call() throws FlowException {
            SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(counterpartySession) {
                @Override
                @Suspendable
                protected void checkTransaction(SignedTransaction stx) throws FlowException {
                }
            });

            //Stored the transaction into data base.
            subFlow(new ReceiveFinalityFlow(counterpartySession, signedTransaction.getId()));
            return null;
        }
    }

}