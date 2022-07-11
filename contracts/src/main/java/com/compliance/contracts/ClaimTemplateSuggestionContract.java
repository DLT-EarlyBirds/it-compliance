package com.compliance.contracts;

import com.compliance.states.ClaimTemplateSuggestion;
import com.compliance.states.Rule;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.transactions.LedgerTransaction;

import java.util.Objects;

import static net.corda.core.contracts.ContractsDSL.requireThat;

// ************
// * Contract *
// ************
public class ClaimTemplateSuggestionContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.ClaimTemplateSuggestionContract";

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(LedgerTransaction tx) {

        /* We can use the requireSingleCommand function to extract command data from transaction.
         * However, it is possible to have multiple commands in a signle transaction.*/
        //final CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(), Commands.class);
        final CommandData commandData = tx.getCommands().get(0).getValue();

        if (commandData instanceof Commands.CreateClaimTemplateSuggestion) {
            //Retrieve the output state of the transaction
            ClaimTemplateSuggestion output = tx.outputsOfType(ClaimTemplateSuggestion.class).get(0);
            StateAndRef<Rule> rule = output.getRule().resolve(tx);
            requireThat(require -> {
                require.using("The regulation is not empty", !Objects.equals(output.getName(), "") && !Objects.equals(output.getTemplateDescription(), ""));
                return null;
            });
        }
    }

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        class CreateClaimTemplateSuggestion implements Commands {
        }
        class UpdateClaimTemplateSuggestion implements Commands {
        }
        class DeprecateClaimTemplateSuggestion implements Commands {

        }
    }
}