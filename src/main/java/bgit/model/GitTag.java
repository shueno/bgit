package bgit.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.DeleteTagCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.PushResult;

import bgit.GitUtils;

public class GitTag {

    private final Project project;

    private final Ref ref;

    private final RevTag revTag;

    private final GitCommit gitCommit;

    GitTag(Project project, Ref ref) {
        this.project = project;
        this.ref = ref;

        Repository repository = project.getGit().getRepository();
        RevWalk revWalk = new RevWalk(repository);
        ObjectId id = ref.getObjectId();
        RevObject revObject = GitUtils.parseAny(revWalk, id);

        if (revObject instanceof RevTag) {
            revTag = (RevTag) revObject;
            revObject = revTag.getObject();
            GitUtils.parseHeaders(revWalk, revObject);

        } else {
            revTag = null;
        }

        if (revObject instanceof RevCommit) {
            RevCommit revCommit = (RevCommit) revObject;
            gitCommit = new GitCommit(project, revCommit);

        } else {
            gitCommit = null;
        }
    }

    public GitCommit findGitCommit() {
        return gitCommit;
    }

    public List<GitPushResult> push() {
        PushCommand pushCommand = project.getGit().push();
        pushCommand.add(ref);
        Iterable<PushResult> pushResults = GitUtils.call(pushCommand);
        List<GitPushResult> gitPushResults = new ArrayList<GitPushResult>();

        for (PushResult pushResult : pushResults) {
            gitPushResults.add(new GitPushResult(pushResult));
        }

        return gitPushResults;
    }

    public void delete() {
        DeleteTagCommand deleteTagCommand = project.getGit().tagDelete();
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
