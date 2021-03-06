package com.compliance.contracts;

import com.compliance.states.Regulation;
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

        if (commandData instanceof Commands.CreateRegulation) {
            Regulation output = tx.outputsOfType(Regulation.class).get(0);

            requireThat(require -> {
                require.using(
                        "The regulation is not empty",
                        !Objects.equals(output.getName(), "") && !Objects.equals(output.getDescription(), "")
                );
                require.using(
                        "The regulation is not already deprecated",
                        !output.getIsDeprecated()
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
                require.using(
                        "The releaseDate for updated regulation must be after the releaseDate for old regulation",
                        output.getReleaseDate().after(input.getReleaseDate())
                );

                return null;
            });
        } else if (commandData instanceof Commands.DeprecateRegulation) {
            Regulation input = tx.inputsOfType(Regulation.class).get(0);
            Regulation output = tx.outputsOfType(Regulation.class).get(0);

            requireThat(require -> {
                require.using("The regulation input is not deprecated", !input.getIsDeprecated());
                require.using("The regulation output is deprecated", output.getIsDeprecated());
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