package com.compliance.contracts;

import com.compliance.states.ClaimTemplate;
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
public class ClaimTemplateContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.ClaimTemplateContract";

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

        if (commandData instanceof Commands.CreateClaimTemplate) {
            ClaimTemplate output = tx.outputsOfType(ClaimTemplate.class).get(0);
            StateAndRef<Rule> ruleStateAndRef = output.getRule().resolve(tx);
            Rule rule = ruleStateAndRef.getState().getData();

            requireThat(require -> {
                require.using("The claim template is not empty", !Objects.equals(output.getName(), "") && !Objects.equals(output.getTemplateDescription(), ""));
                require.using("The parent rule should not be deprecated", !rule.getIsDeprecated());

                return null;
            });
        } else if (commandData instanceof Commands.UpdateClaimTemplate) {
            ClaimTemplate output = tx.outputsOfType(ClaimTemplate.class).get(0);
            StateAndRef<Rule> ruleStateAndRef = output.getRule().resolve(tx);
            Rule rule = ruleStateAndRef.getState().getData();
            ClaimTemplate input = tx.inputsOfType(ClaimTemplate.class).get(0);

            requireThat(require -> {
                require.using("The transaction is only allowed to modify the input claim template", output.getLinearId().equals(input.getLinearId()));
                require.using("The claim template is not empty", !Objects.equals(output.getName(), "") && !Objects.equals(output.getTemplateDescription(), ""));
                require.using("The parent rule should not be deprecated", !rule.getIsDeprecated());
                return null;
            });
        } else if (commandData instanceof Commands.DeprecateClaimTemplate) {
            ClaimTemplate input = tx.inputsOfType(ClaimTemplate.class).get(0);
            ClaimTemplate output = tx.outputsOfType(ClaimTemplate.class).get(0);

            requireThat(require -> {
                require.using(
                        "The transaction is only allowed to modify the input regulation",
                        output.getLinearId().equals(input.getLinearId())
                );
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