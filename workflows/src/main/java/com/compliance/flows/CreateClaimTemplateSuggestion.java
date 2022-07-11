package com.compliance.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.compliance.contracts.ClaimTemplateSuggestionContract;
import com.compliance.states.ClaimTemplateSuggestion;
import com.compliance.states.Rule;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.NodeInfo;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

//Initiate this flow:
//flow start CreateRegulation description: "test_description", supervisoryAuthority: Supervisory Authority

//Check if added to ledger:
//run vaultQuery contractStateType: com.template.states.Regulation

public class CreateClaimTemplateSuggestion {

    @InitiatingFlow
    @StartableByRPC
    public static class CreateClaimTemplateSuggestionInitiator extends FlowLogic<SignedTransaction> {
        // Private variables
        private final String name;
        private final String templateDescription;
        private final UniqueIdentifier ruleLinearId;
        private final Party supervisoryAuthority;

        //public constructor
        public CreateClaimTemplateSuggestionInitiator(String name, String description, Party supervisoryAuthority, UniqueIdentifier ruleLinearId) {
            this.templateDescription = description;
            this.name = name;
            this.ruleLinearId = ruleLinearId;
            this.supervisoryAuthority = supervisoryAuthority;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);


            final ClaimTemplateSuggestion output = new ClaimTemplateSuggestion(
                    new UniqueIdentifier(),
                    name,
                    templateDescription,
                    this.getOurIdentity(),
                    supervisoryAuthority,
                    new LinearPointer<Rule>(ruleLinearId, Rule.class)
            );

            final TransactionBuilder builder = new TransactionBuilder(notary);

            builder.addOutputState(output);

            // Reuse involved parties list for signing
            builder.addCommand(new ClaimTemplateSuggestionContract.Commands.CreateClaimTemplateSuggestion(),
                    Arrays.asList(
                            getOurIdentity().getOwningKey(),
                            supervisoryAuthority.getOwningKey()
                    )
            );

            // Verify that the transaction is valid.
            builder.verify(getServiceHub());

            // Sign the transaction.
            final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(builder);

            // Send the state to the counterparty, and receive it back with their signature.
            FlowSession otherPartySession = initiateFlow(supervisoryAuthority);
            final SignedTransaction fullySignedTx = subFlow(
                    new CollectSignaturesFlow(partSignedTx, Collections.singletonList(otherPartySession)));

            // Notarise and record the transaction in both parties' vaults.
            return subFlow(new FinalityFlow(fullySignedTx, Collections.singletonList(otherPartySession)));
        }
    }

    @InitiatedBy(CreateClaimTemplateSuggestionInitiator.class)
    public static class CreateClaimTemplateResponder extends FlowLogic<Void> {
        //private variable
        private final FlowSession counterpartySession;

        //Constructor
        public CreateClaimTemplateResponder(FlowSession counterpartySession) {
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