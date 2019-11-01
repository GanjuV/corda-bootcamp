package insurance.annexure;

import static java.util.Collections.singletonList;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.CommandData;
import net.corda.core.flows.CollectSignaturesFlow;
import net.corda.core.flows.FinalityFlow;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

@InitiatingFlow
@StartableByRPC
public class AnnexureIssueFlowInitiator extends FlowLogic<SignedTransaction> {
    private final Party owner;
    private final int id;
    private final int policyId;
    private final String siteName;
    private final String status;
    private final String creationDate;

    public AnnexureIssueFlowInitiator(
        Party owner, int id, int policyId, String siteName, String status, String creationDate) {
        this.owner = owner;
        this.id = id;
        this.policyId = policyId;
        this.siteName = siteName;
        this.status = status;
        this.creationDate = creationDate;
    }

    private final ProgressTracker progressTracker = new ProgressTracker();

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        Party issuer = getOurIdentity();

        AnnexureState AnnexureState = new AnnexureState(issuer, owner, id, policyId, siteName, status, creationDate);

        TransactionBuilder transactionBuilder = new TransactionBuilder(notary);

        CommandData commandData = new AnnexureContract.Commands.Issue();

        transactionBuilder.addCommand(commandData, issuer.getOwningKey(), owner.getOwningKey());

        transactionBuilder.addOutputState(AnnexureState, AnnexureContract.ID);

        transactionBuilder.verify(getServiceHub());

        FlowSession session = initiateFlow(owner);

        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

        SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(signedTransaction, singletonList(session)));

        return subFlow(new FinalityFlow(fullySignedTransaction, singletonList(session)));
    }
}