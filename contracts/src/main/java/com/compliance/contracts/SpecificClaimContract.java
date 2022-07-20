package com.compliance.contracts;

import com.compliance.states.ClaimTemplate;
import com.compliance.states.Rule;
import com.compliance.states.SpecificClaim;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.transactions.LedgerTransaction;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireThat;

// ************
// * Contract *
// ************
public class SpecificClaimContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.SpecificClaimContract";


    /**
     * The verify function is called by the Corda node to check that the transaction is valid
     * <p>
     * A transaction is valid if the verify() function of the contract of all the transaction's input and output states
     * does not throw an exception.
     *
     * @param tx The transaction that is being verified.
     */
    @Override
    public void verify(LedgerTransaction tx) {
        final CommandData commandData = tx.getCommands().get(0).getValue();

        SpecificClaim output = tx.outputsOfType(SpecificClaim.class).get(0);

        StateAndRef<ClaimTemplate> claimTemplate = output.getClaimTemplate().resolve(tx);
        if (!output.getSupportingClaims().isEmpty()) {
            List<StateAndRef<SpecificClaim>> supportingClaims = output.getSupportingClaims().stream().map(claimTemplateLinearPointer -> claimTemplateLinearPointer.resolve(tx)).collect(Collectors.toList());
        }

        if (commandData instanceof Commands.CreateClaim) {
            requireThat(require -> {
                require.using("The transaction should have exactly one Claim as output", tx.getOutputs().size() == 1);
                require.using("The specific claim must have a name value", !output.getName().equals(""));
                return null;
            });


        } else if (commandData instanceof Commands.UpdateSpecificClaim) {
            SpecificClaim input = tx.inputsOfType(SpecificClaim.class).get(0);

            requireThat(require -> {
                require.using("The transaction is only allowed to modify the input specific claim", output.getLinearId().equals(input.getLinearId()));
                require.using("The specific claim is not empty", !Objects.equals(output.getName(), "") && !Objects.equals(output.getDescription(), ""));
                return null;
            });
        }
    }

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        //In our hello-world app, We will only have one command.
        class CreateClaim implements Commands {
        }

        class UpdateSpecificClaim implements Commands {
        }
    }
}