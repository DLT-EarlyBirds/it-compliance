package com.template.contracts;

import com.template.states.Regulation;
import com.template.states.Rule;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.transactions.LedgerTransaction;

import java.util.Objects;
import java.util.stream.Stream;

import static net.corda.core.contracts.ContractsDSL.requireThat;

// ************
// * Contract *
// ************
public class RuleContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.RuleContract";

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(LedgerTransaction tx) {

        /* We can use the requireSingleCommand function to extract command data from transaction.
         * However, it is possible to have multiple commands in a signle transaction.*/
        //final CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(), Commands.class);
        final CommandData commandData = tx.getCommands().get(0).getValue();



        if (commandData instanceof Commands.CreateRule) {
            // Retrieve the output state of the transaction
            Rule output = tx.outputsOfType(Rule.class).get(0);
            // Fetch the parent regulation via the linear pointer.
            StateAndRef<Regulation> parentRegulation =  output.getParentRegulation().resolve(tx);

            requireThat(require -> {
                require.using("The rule is not empty", !Objects.equals(output.getName(), "") && !Objects.equals(output.getRuleSpecification(), ""));
                require.using("The rule needs a parent regulation", !parentRegulation.getState().component1().getName().equals(""));
                return null;
            });

        } else if (commandData instanceof Commands.UpdateRule) {
            // Retrieve the output state of the transaction
            Rule output = tx.outputsOfType(Rule.class).get(0);
            // Fetch the parent regulation via the linear pointer.
            StateAndRef<Regulation> parentRegulation =  output.getParentRegulation().resolve(tx);
            Rule input = tx.inputsOfType(Rule.class).get(0);

            requireThat(require -> {
                require.using("The transaction is only allowed to modify the input rule", output.getLinearId().equals(input.getLinearId()));
                require.using("The rule is not empty", !Objects.equals(output.getName(), "") && !Objects.equals(output.getRuleSpecification(), ""));
                require.using("The rule needs a parent regulation", !parentRegulation.getState().component1().getName().equals(""));
                return null;
            });
        } else if (commandData instanceof Commands.DeprecateRule) {
            Rule input = tx.inputsOfType(Rule.class).get(0);
            requireThat(require -> {

               return null;
            });
        }
    }

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        class CreateRule implements Commands {
        }
        class UpdateRule implements Commands {
        }
        class DeprecateRule implements Commands {
        }
    }
}