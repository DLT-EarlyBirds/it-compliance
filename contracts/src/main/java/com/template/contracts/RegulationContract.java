package com.template.contracts;

import com.template.states.Regulation;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;

import java.util.Objects;

import static net.corda.core.contracts.ContractsDSL.requireThat;

// ************
// * Contract *
// ************
public class RegulationContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.RegulationContract";

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(LedgerTransaction tx) {

        /* We can use the requireSingleCommand function to extract command data from transaction.
         * However, it is possible to have multiple commands in a signle transaction.*/
        //final CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(), Commands.class);
        final CommandData commandData = tx.getCommands().get(0).getValue();

        if (commandData instanceof Commands.CreateRegulation) {
            //Retrieve the output state of the transaction
            Regulation output = tx.outputsOfType(Regulation.class).get(0);

            //No verification required!
            requireThat(require -> {
                require.using(
                        "The regulation is not empty",
                        !Objects.equals(output.getName(), "") && !Objects.equals(output.getDescription(), "")
                );
                return null;
            });
        } else if (commandData instanceof Commands.UpdateRegulation) {
            Regulation output = tx.outputsOfType(Regulation.class).get(0);
            Regulation input = tx.inputsOfType(Regulation.class).get(0);

            requireThat(require -> {
                require.using(
                        "The transaction is only allowed to modify the input regulation",
                        output.getLinearId().equals(input.getLinearId())
                );
                require.using(
                        "The regulation is not empty",
                        !Objects.equals(output.getName(), "") && !Objects.equals(output.getDescription(), "")
                );
                return null;
            });
        } else if (commandData instanceof Commands.DeprecateRegulation) {
            Regulation input = tx.inputsOfType(Regulation.class).get(0);

            requireThat(require -> {

                return null;
            });
        }
    }

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        // Newly submit a regulation that doesn't exist so far
        class CreateRegulation implements Commands {
        }

        // Update an existing regulation e.g. add a new version or description
        class UpdateRegulation implements Commands {
        }

        // Deprecate an existing regulation
        class DeprecateRegulation implements Commands {

        }
    }
}