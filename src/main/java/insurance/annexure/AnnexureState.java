package insurance.annexure;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;

/* Our state, defining a shared fact on the ledger.
 * See src/main/java/examples/ArtState.java for an example. */
@BelongsToContract(AnnexureContract.class)
public class AnnexureState implements ContractState {

    private final Party issuer;
    private final Party owner;
    private final int id;
    private final int policyId;
    private final String siteName;
    private final String status;
    private final String creationDate;
    
    private List<AbstractParty> participants;

    public AnnexureState(
        Party issuer, Party owner, int id, int policyId, String siteName, String status, String creationDate) {
        this.issuer = issuer;
        this.owner = owner;
        this.id = id;
        this.policyId = policyId;
        this.siteName = siteName;
        this.status = status;
        this.creationDate = creationDate;
        this.participants = new ArrayList<>();
        participants.add(issuer);
        participants.add(owner);
    }

    public Party getIssuer() {
        return issuer;
    }

    public Party getOwner() {
        return owner;
    }

    public int getId() {
        return this.id;
    }
    
    public int getPolicyId() {
        return this.policyId;
    }

    public String getSiteName() {
        return this.siteName;
    }

    public String getStatus() {
        return this.status;
    }

    public String getCreationDate() {
        return this.creationDate;
    }

    @Override
    @NotNull
    public List<AbstractParty> getParticipants() {
        return participants;
    }
}