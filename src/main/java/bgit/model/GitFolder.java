package bgit.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.TreeWalk;

import bgit.ApplicationException;

public class GitFolder extends GitNode {

    GitFolder(Project project, ObjectId objectId, String relativePathString) {
        super(project, objectId, relativePathString);
    }

    public Iterable<GitFolder> findChildGitFolders() {
        Repository repository = project.getGit().getRepository();
        TreeWalk treeWalk = new TreeWalk(repository);
        addTree(treeWalk, objectId);
        List<GitFolder> gitFolders = new ArrayList<GitFolder>();

        while (next(treeWalk)) {

            if (treeWalk.getFileMode(0).getObjectType() != Constants.OBJ_TREE) {
                continue;
            }

            ObjectId objectId = treeWalk.getObjectId(0);
            StringBuilder sb = new StringBuilder();

            if (!relativePathString.isEmpty()) {
                sb.append(relativePathString);
                sb.append("/");
            }

            sb.append(treeWalk.getNameString());
            GitFolder gitFolder = new GitFolder(project, objectId,
                    sb.toString());
            gitFolders.add(gitFolder);
        }

        return gitFolders;
    }

    public Iterable<GitFile> findChildGitFiles() {
        Repository repository = project.getGit().getRepository();
        TreeWalk treeWalk = new TreeWalk(repository);
        addTree(treeWalk, objectId);
        List<GitFile> gitFiles = new ArrayList<GitFile>();

        while (next(treeWalk)) {

            if (treeWalk.getFileMode(0).getObjectType() != Constants.OBJ_BLOB) {
                continue;
            }

            ObjectId objectId = treeWalk.getObjectId(0);
            StringBuilder sb = new StringBuilder();

            if (!relativePathString.isEmpty()) {
                sb.append(relativePathString);
                sb.append("/");
            }

            sb.append(treeWalk.getNameString());
            GitFile gitFile = new GitFile(project, objectId, sb.toString());
            gitFiles.add(gitFile);
        }

        return gitFiles;
    }

    // TODO Move to GitUtils
    private static int addTree(TreeWalk treeWalk, AnyObjectId id) {

        try {
            return treeWalk.addTree(id);

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    // TODO Move to GitUtils
    private static boolean next(TreeWalk treeWalk) {

        try {
            return treeWalk.next();

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }
}
