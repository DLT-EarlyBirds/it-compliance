package com.template.contracts;

import com.template.states.ClaimTemplate;
import com.template.states.SpecificClaim;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.transactions.LedgerTransaction;

import java.util.List;
import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireThat;

// ************
// * Contract *
// ************
public class SpecificClaimContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.SpecificClaimContract";

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(LedgerTransaction tx) {

        /* We can use the requireSingleCommand function to extract command data from transaction.
         * However, it is possible to have multiple commands in a signle transaction.*/
        //final CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(), Commands.class);
        final CommandData commandData = tx.getCommands().get(0).getValue();

        // Retrieve the output state of the transaction
        SpecificClaim output = tx.outputsOfType(SpecificClaim.class).get(0);

        StateAndRef<ClaimTemplate> claimTemplate = output.getClaimTemplate().resolve(tx);
        List<StateAndRef<SpecificClaim>> supportingClaims = output.getSupportingClaims().stream().map(claimTemplateLinearPointer -> claimTemplateLinearPointer.resolve(tx)).collect(Collectors.toList());


        if (commandData instanceof Commands.CreateClaim) {
            // Using Corda DSL function requireThat to replicate conditions-checks
            requireThat(require -> {
                require.using("The transaction should have exactly one Claim as output", tx.getOutputs().size() == 1);
                require.using("The output of Claim must have a name value", !output.getName().equals(""));
                return null;
            });

        } else if (commandData instanceof Commands.ResubmitClaim) {

            requireThat(require -> {
                return null;
            });
        } else if (commandData instanceof Commands.UpdateSpecificClaim) {

            requireThat(require -> {
                return null;
            });
        }
    }

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        //In our hello-world app, We will only have one command.
        class CreateClaim implements Commands {
        }
        class ResubmitClaim implements Commands {
        }
        class UpdateSpecificClaim implements Commands {
        }
    }
}