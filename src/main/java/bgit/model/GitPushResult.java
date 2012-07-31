package bgit.model;

import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;

public class GitPushResult {

    private final PushResult pushResult;

    public GitPushResult(PushResult pushResult) {
        this.pushResult = pushResult;
    }

    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("uri: ");
        sb.append(pushResult.getURI());
        sb.append('\n');
        sb.append("messages: ");
        sb.append(pushResult.getMessages());
        sb.append('\n');

        for (RemoteRefUpdate remoteRefUpdate : pushResult.getRemoteUpdates()) {
            sb.append("remoteRefUpdateStatus: ");
            sb.append(remoteRefUpdate.getStatus());
            sb.append('\n');
            sb.append("remoteRefUpdateMessage: ");
            sb.append(remoteRefUpdate.getMessage());
            sb.append('\n');
        }

        return sb.toString();
    }
}
