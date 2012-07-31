package bgit.model;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.RebaseResult;

public class GitPullResult {

    private final PullResult pullResult;

    public GitPullResult(PullResult pullResult) {
        this.pullResult = pullResult;
    }

    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("successful: ");
        sb.append(pullResult.isSuccessful());
        sb.append('\n');
        sb.append("fetchedFrom: ");
        sb.append(pullResult.getFetchedFrom());
        sb.append('\n');
        sb.append("fetchResultUri: ");
        sb.append(pullResult.getFetchResult().getURI());
        sb.append('\n');
        sb.append("fetchResultMessages: ");
        sb.append(pullResult.getFetchResult().getMessages());
        sb.append('\n');

        MergeResult mergeResult = pullResult.getMergeResult();

        if (mergeResult != null) {
            sb.append("mergeResultMergeStatus: ");
            sb.append(mergeResult.getMergeStatus());
            sb.append('\n');
            sb.append("mergeResultConflicts: ");
            sb.append(mergeResult.getConflicts());
            sb.append('\n');

        }

        RebaseResult rebaseResult = pullResult.getRebaseResult();

        if (rebaseResult != null) {
            sb.append("rebaseResultStatus: ");
            sb.append(rebaseResult.getStatus());
            sb.append('\n');
        }

        return sb.toString();
    }

    public boolean isSuccessful() {
        return pullResult.isSuccessful();
    }
}
