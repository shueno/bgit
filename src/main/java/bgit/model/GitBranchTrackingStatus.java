package bgit.model;

import org.eclipse.jgit.lib.BranchTrackingStatus;

public class GitBranchTrackingStatus {

    private final BranchTrackingStatus branchTrackingStatus;

    GitBranchTrackingStatus(BranchTrackingStatus branchTrackingStatus) {
        this.branchTrackingStatus = branchTrackingStatus;
    }

    public String getRemoteTrackingBranchName() {
        return branchTrackingStatus.getRemoteTrackingBranch();
    }

    public int getAheadCount() {
        return branchTrackingStatus.getAheadCount();
    }

    public int getBehindCount() {
        return branchTrackingStatus.getBehindCount();
    }
}
