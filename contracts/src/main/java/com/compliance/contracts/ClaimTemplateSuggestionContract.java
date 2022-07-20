package com.compliance.contracts;

import com.compliance.states.ClaimTemplate;
import com.compliance.states.ClaimTemplateSuggestion;
import com.compliance.states.Rule;
import com.compliance.states.SpecificClaim;
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

        if (commandData instanceof Commands.CreateClaimTemplateSuggestion) {
            ClaimTemplateSuggestion output = tx.outputsOfType(ClaimTemplateSuggestion.class).get(0);
            StateAndRef<Rule> rule = output.getRule().resolve(tx);

            requireThat(require -> {
                require.using("The regulation is not empty", !Objects.equals(output.getName(), "") && !Objects.equals(output.getTemplateDescription(), ""));
                return null;
            });
        } else if (commandData instanceof ClaimTemplateSuggestionContract.Commands.AcceptClaimTemplateSuggestion) {
            ClaimTemplate output = tx.outputsOfType(ClaimTemplate.class).get(0);

            requireThat(require -> {
                require.using("The transaction should have exactly one Claim as output", tx.getOutputs().size() == 1);
                require.using("The specific claim must have a name value", !output.getName().equals(""));

                return null;
            });
        } else if (commandData instanceof ClaimTemplateSuggestionContract.Commands.RejectClaimTemplateSuggestion) {

            requireThat(require -> {
                require.using("The transaction should consume exactly one ClaimTemplateSuggestion ", tx.getInputs().size() == 1);
                require.using("The transaction should have no outputs", tx.getOutputs().size() == 0);
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

        class AcceptClaimTemplateSuggestion implements ClaimTemplateSuggestionContract.Commands {
        }

        class RejectClaimTemplateSuggestion implements ClaimTemplateSuggestionContract.Commands {
        }

    }
}