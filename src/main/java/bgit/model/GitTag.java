package bgit.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.DeleteTagCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.transport.PushResult;

import bgit.ApplicationException;
import bgit.GitUtils;

public class GitTag {

    private final Git git;

    private final Ref ref;

    private final RevTag revTag;

    GitTag(Git git, Ref ref) {
        this.git = git;
        this.ref = ref;
        Repository repository = git.getRepository();
        ObjectId objectId = ref.getObjectId();
        ObjectLoader objectLoader = GitUtils.open(repository, objectId);

        if (objectLoader.getType() == Constants.OBJ_TAG) {
            revTag = parse(objectLoader.getBytes());

        } else {
            revTag = null;
        }
    }

    private static RevTag parse(byte[] bytes) {

        try {
            return RevTag.parse(bytes);

        } catch (CorruptObjectException e) {
            throw new ApplicationException(e);
        }
    }

    public List<GitPushResult> push() {
        PushCommand pushCommand = git.push();
        pushCommand.add(ref);
        Iterable<PushResult> pushResults = GitUtils.call(pushCommand);
        List<GitPushResult> gitPushResults = new ArrayList<GitPushResult>();

        for (PushResult pushResult : pushResults) {
            gitPushResults.add(new GitPushResult(pushResult));
        }

        return gitPushResults;
    }

    public void delete() {
        DeleteTagCommand deleteTagCommand = git.tagDelete();
        deleteTagCommand.setTags(getPathString());
        GitUtils.call(deleteTagCommand);
    }

    public String getOneline() {
        return getPathString();
    }

    public String getPathString() {
        return ref.getName();
    }

    public String getName() {
        return ref.getName().substring("refs/tags/".length());
    }

    public String getTagger() {

        if (revTag == null) {
            return null;
        }

        return String.format("%s <%s>", revTag.getTaggerIdent().getName(),
                revTag.getTaggerIdent().getEmailAddress());
    }

    public Timestamp getDateTagged() {

        if (revTag == null) {
            return null;
        }

        return new Timestamp(revTag.getTaggerIdent().getWhen().getTime());
    }

    public String getFullMessage() {

        if (revTag == null) {
            return null;
        }

        return revTag.getFullMessage();
    }
}
