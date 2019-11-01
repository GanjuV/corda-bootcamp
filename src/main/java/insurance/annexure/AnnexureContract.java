package insurance.annexure;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

import java.util.List;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.transactions.LedgerTransaction;

public class AnnexureContract implements Contract {
    public static String ID = "insurance.annexure.AnnexureContract";

    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {

        CommandWithParties<AnnexureContract.Commands> command = requireSingleCommand(tx.getCommands(), AnnexureContract.Commands.class);

        List<ContractState> inputs = tx.getInputStates();
        List<ContractState> outputs = tx.getOutputStates();

        if (command.getValue() instanceof AnnexureContract.Commands.Issue) {
            requireThat(req -> {
                req.using("Transaction must have no input states.", inputs.isEmpty());
                req.using("Transaction must have exactly one output.", outputs.size() == 1);
                req.using("Output must be a AnnexureState.", outputs.get(0) instanceof AnnexureState);
                AnnexureState output = (AnnexureState) outputs.get(0);
                req.using("Issuer must be required singer.", command.getSigners().contains(output.getIssuer().getOwningKey()));
                req.using("Owner must be required singer.", command.getSigners().contains(output.getOwner().getOwningKey()));
                req.using("Status must be draft.", output.getStatus().equalsIgnoreCase("draft"));
                return null;
            });
        } else {
            throw new IllegalArgumentException("Unrecognized command");
        }
    }

    public interface Commands extends CommandData {
        class Issue implements Commands {
        }
    }
}