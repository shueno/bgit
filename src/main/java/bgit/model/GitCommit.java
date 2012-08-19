package bgit.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;

import bgit.GitUtils;

public class GitCommit {

    private final Project project;

    private final RevCommit revCommit;

    GitCommit(Project project, RevCommit revCommit) {
        this.project = project;
        this.revCommit = revCommit;
    }

    public GitFolder findRootGitFolder() {
        RevTree revTree = revCommit.getTree();
        return new GitFolder(project, revTree, "");
    }

    public List<GitCommit> findParentGitCommits() {
        Repository repository = project.getGit().getRepository();
        RevWalk revWalk = new RevWalk(repository);
        List<GitCommit> gitCommits = new ArrayList<GitCommit>();

        for (int i = 0; i < revCommit.getParentCount(); i++) {
            RevCommit parentRevCommit = revCommit.getParent(i);
            GitUtils.parseHeaders(revWalk, parentRevCommit);
            GitCommit gitCommit = new GitCommit(project, parentRevCommit);
            gitCommits.add(gitCommit);
        }

        return gitCommits;
    }

    public List<GitDiffEntry> diff(GitCommit parentGitCommit) {
        Git git = project.getGit();
        AbstractTreeIterator treeIterator = null;

        if (parentGitCommit == null) {
            treeIterator = new EmptyTreeIterator();

        } else {
            treeIterator = GitUtils.newCanonicalTreeParser(null, git
                    .getRepository().newObjectReader(),
                    parentGitCommit.revCommit.getTree().getId());
        }

        DiffCommand diffCommand = git.diff();
        diffCommand.setShowNameAndStatusOnly(true);
        diffCommand.setNewTree(GitUtils
                .newCanonicalTreeParser(null, git.getRepository()
                        .newObjectReader(), revCommit.getTree().getId()));
        diffCommand.setOldTree(treeIterator);
        List<GitDiffEntry> gitDiffEntries = new ArrayList<GitDiffEntry>();

        for (DiffEntry diffEntry : GitUtils.call(diffCommand)) {
            GitDiffEntry gitDiffEntry = new GitDiffEntry(project, diffEntry);
            gitDiffEntries.add(gitDiffEntry);
        }

        return gitDiffEntries;
    }

    public String getOneline() {
        return String.format("%s '%s' by %s on %tF", getShortIdString(),
                getShortMessage(), getAuthorName(), getDateAuthored());
    }

    public String getShortIdString() {
        return revCommit.getName().substring(0, 8);
    }

    public String getFullIdString() {
        return revCommit.getName();
    }

    public String getAuthor() {
        return String.format("%s <%s>", getAuthorName(),
                getAuthorEmailAddress());
    }

    public String getAuthorName() {
        return revCommit.getAuthorIdent().getName();
    }

    public String getAuthorEmailAddress() {
        return revCommit.getAuthorIdent().getEmailAddress();
    }

    public Timestamp getDateAuthored() {
        return new Timestamp(revCommit.getAuthorIdent().getWhen().getTime());
    }

    public final String getShortMessage() {
        return revCommit.getShortMessage();
    }

    public final String getFullMessage() {
        return revCommit.getFullMessage();
    }

    ObjectId getTreeId() {
        return revCommit.getTree().getId();
    }

    public Project getProject() {
        return project;
    }
}
