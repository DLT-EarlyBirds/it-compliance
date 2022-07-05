package com.template.contracts;

import com.template.states.ClaimTemplate;
import com.template.states.Regulation;
import com.template.states.Rule;
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
public class ClaimTemplateContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.ClaimTemplateContract";

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(LedgerTransaction tx) {

        /* We can use the requireSingleCommand function to extract command data from transaction.
         * However, it is possible to have multiple commands in a signle transaction.*/
        //final CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(), Commands.class);
        final CommandData commandData = tx.getCommands().get(0).getValue();



        if (commandData instanceof Commands.CreateClaimTemplate) {
            //Retrieve the output state of the transaction
            ClaimTemplate output = tx.outputsOfType(ClaimTemplate.class).get(0);
            StateAndRef<Rule> rule = output.getRule().resolve(tx);
            requireThat(require -> {
                require.using("The regulation is not empty", !Objects.equals(output.getName(), "") && !Objects.equals(output.getTemplateDescription(), ""));
                return null;
            });
        } else if (commandData instanceof Commands.UpdateClaimTemplate) {
            //Retrieve the output state of the transaction
            ClaimTemplate output = tx.outputsOfType(ClaimTemplate.class).get(0);
            StateAndRef<Rule> rule = output.getRule().resolve(tx);
            ClaimTemplate input = tx.inputsOfType(ClaimTemplate.class).get(0);

            requireThat(require -> {
                require.using("The transaction is only allowed to modify the input claim template", output.getLinearId().equals(input.getLinearId()));
                require.using("The claim template is not empty", !Objects.equals(output.getName(), "") && !Objects.equals(output.getTemplateDescription(), ""));
                return null;
            });
        } else if (commandData instanceof Commands.DeprecateClaimTemplate) {
            ClaimTemplate input = tx.inputsOfType(ClaimTemplate.class).get(0);

            requireThat(require -> {

                return null;
            });
        }
    }

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        class CreateClaimTemplate implements Commands {
        }
        class UpdateClaimTemplate implements Commands {
        }
        class DeprecateClaimTemplate implements Commands {

        }
    }
}