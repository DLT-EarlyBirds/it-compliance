package com.template.contracts;

import com.template.states.RegulationGraph;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;
import java.util.List;

// ************
// * Contract *
// ************
public class RegulationGraphContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.RegulationGraphContract";

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(LedgerTransaction tx) {

        /* We can use the requireSingleCommand function to extract command data from transaction.
         * However, it is possible to have multiple commands in a signle transaction.*/
        //final CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(), Commands.class);
        final CommandData commandData = tx.getCommands().get(0).getValue();

        if (commandData instanceof Commands.CreateRegulationGraph) {
            //Retrieve the output state of the transaction
            RegulationGraph output = tx.outputsOfType(RegulationGraph.class).get(0);

            //Reference state transaction --> No verrification required!
            requireThat(require -> {
                return null;
            });
        }
    }

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        //In our hello-world app, We will only have one command.
        class CreateRegulationGraph implements Commands {}
    }
}