package bgit.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectStream;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;

import bgit.ApplicationException;
import bgit.GitUtils;

public class GitCommit {

    private final Project project;

    private final RevCommit revCommit;

    GitCommit(Project project, RevCommit revCommit) {
        this.project = project;
        this.revCommit = revCommit;
    }

    // TODO Move throwable methods to Utils.
    public void zip(File zipPath) {

        try {
            Repository repository = project.getGit().getRepository();
            RevTree revTree = new RevWalk(repository).parseTree(revCommit);
            TreeWalk treeWalk = new TreeWalk(repository);
            GitUtils.addTree(treeWalk, revTree);
            treeWalk.setRecursive(true);
            ZipOutputStream zipOutputStream = new ZipOutputStream(
                    new BufferedOutputStream(new FileOutputStream(zipPath)));

            try {

                while (GitUtils.next(treeWalk)) {
                    String pathString = treeWalk.getPathString();
                    ZipEntry zipEntry = new ZipEntry(pathString);
                    zipOutputStream.putNextEntry(zipEntry);
                    ObjectLoader objectLoader = GitUtils.open(repository,
                            treeWalk.getObjectId(0));
                    ObjectStream openStream = GitUtils.openStream(objectLoader);
                    IOUtils.copy(openStream, zipOutputStream);
                    zipOutputStream.closeEntry();
                }

                zipOutputStream.flush();

            } finally {
                zipOutputStream.close();
            }

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
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
