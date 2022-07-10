package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.SpecificClaimContract;
import com.template.states.ClaimTemplate;
import com.template.states.SpecificClaim;
import com.template.states.Rule;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class UpdateSpecificClaim {

    @InitiatingFlow
    @StartableByRPC
    public static class UpdateSpecificClaimInitiator extends FlowLogic<SignedTransaction> {
        // Private variables
        
        // Name of the SpecificClaim
        private final String name;

        private SecureHash attachmentID;
        private final LinearPointer<Rule> rule;
        @NotNull
        private final UniqueIdentifier specificClaimLinearId;

        private final List<UniqueIdentifier> supportingClaimsLinearIds;

        private final Party supervisorAuthority;
        
        // The specification that details what need to be fulfilled
        private final String templateDescription;

        //public constructor
        public UpdateSpecificClaimInitiator(UniqueIdentifier specificClaimLinearId,
                                            String name,
                                            Party supervisoryAuthority,
                                            String templateDescription, 
                                            LinearPointer<Rule> rule,
                                            List<UniqueIdentifier> supportingClaimsLinearIds
                                            ) {
            this.rule = rule;
            this.name = name;
            this.specificClaimLinearId = specificClaimLinearId;
            this.templateDescription = templateDescription;
            this.supervisorAuthority = supervisoryAuthority;
            this.supportingClaimsLinearIds = supportingClaimsLinearIds;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);


            QueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria()
                    .withUuid(Collections.singletonList(UUID.fromString(specificClaimLinearId.toString())))
                    .withStatus(Vault.StateStatus.UNCONSUMED)
                    .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT);

            final StateAndRef<SpecificClaim> input = getServiceHub().getVaultService().queryBy(SpecificClaim.class, inputCriteria).getStates().get(0);
            final TransactionBuilder builder = new TransactionBuilder(notary);

            SpecificClaim output = new SpecificClaim(
                    this.name,
                    this.getOurIdentity(),
                    this.supervisorAuthority,
                    new LinearPointer<>(specificClaimLinearId, ClaimTemplate.class),
                    this.supportingClaimsLinearIds.stream().map(claimLinearId -> new LinearPointer<>(claimLinearId, SpecificClaim.class)).collect(Collectors.toList())
            );
            builder.addInputState(input);
            builder.addOutputState(output);
            builder.addCommand(
                    new SpecificClaimContract.Commands.CreateClaim(),
                    Arrays.asList(
                            getOurIdentity().getOwningKey(),
                            supervisorAuthority.getOwningKey()
                    )
            );

            builder.addCommand(new SpecificClaimContract.Commands.UpdateSpecificClaim(), getOurIdentity().getOwningKey());

            // Verify that the transaction is valid.
            builder.verify(getServiceHub());

            // Sign the transaction.
            final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(builder);

            // Send the state to the counterparty, and receive it back with their signature.
            FlowSession otherPartySession = initiateFlow(supervisorAuthority);
            final SignedTransaction fullySignedTx = subFlow(
                    new CollectSignaturesFlow(partSignedTx, Collections.singletonList(otherPartySession)));

            // Notarise and record the transaction in both parties' vaults.
            return subFlow(new FinalityFlow(fullySignedTx, Collections.singletonList(otherPartySession)));
        }
    }



}