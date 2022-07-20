package com.compliance.contracts;

import com.compliance.states.Regulation;
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
public class RuleContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.RuleContract";


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


        if (commandData instanceof Commands.CreateRule) {
            Rule output = tx.outputsOfType(Rule.class).get(0);
            // Fetch the parent regulation via the linear pointer.
            StateAndRef<Regulation> parentRegulationStateAndRef = output.getParentRegulation().resolve(tx);
            Regulation parentRegulation = parentRegulationStateAndRef.getState().getData();

            requireThat(require -> {
                require.using("The rule is not empty", !Objects.equals(output.getName(), "") && !Objects.equals(output.getRuleSpecification(), ""));
                require.using("The parent regulation should not be deprecated", !parentRegulation.getIsDeprecated());
                require.using("The rule is not already deprecated", !output.getIsDeprecated());
                return null;
            });

        } else if (commandData instanceof Commands.UpdateRule) {
            Rule output = tx.outputsOfType(Rule.class).get(0);
            // Fetch the parent regulation via the linear pointer.
            StateAndRef<Regulation> parentRegulationStateAndRef = output.getParentRegulation().resolve(tx);
            Regulation parentRegulation = parentRegulationStateAndRef.getState().getData();

            Rule input = tx.inputsOfType(Rule.class).get(0);

            requireThat(require -> {
                require.using("The parent regulation should not be deprecated", !parentRegulation.getIsDeprecated());
                require.using("The transaction is only allowed to modify the input rule", output.getLinearId().equals(input.getLinearId()));
                require.using("The rule is not empty", !Objects.equals(output.getName(), "") && !Objects.equals(output.getRuleSpecification(), ""));
                return null;
            });
        } else if (commandData instanceof Commands.DeprecateRule) {
            Rule input = tx.inputsOfType(Rule.class).get(0);
            Rule output = tx.outputsOfType(Rule.class).get(0);

            requireThat(require -> {
                require.using("The rule input is not deprecated", !input.getIsDeprecated());
                require.using("The rule output is deprecated", output.getIsDeprecated());
                require.using("The transaction is only allowed to modify the input rule", output.getLinearId().equals(input.getLinearId()));
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