package bgit.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.TreeWalk;

import bgit.GitUtils;

public class GitFolder extends GitNode {

    GitFolder(Project project, ObjectId objectId, String relativePathString) {
        super(project, objectId, relativePathString);
    }

    public Iterable<GitFolder> findChildGitFolders() {
        Repository repository = project.getGit().getRepository();
        TreeWalk treeWalk = new TreeWalk(repository);
        GitUtils.addTree(treeWalk, objectId);
        List<GitFolder> gitFolders = new ArrayList<GitFolder>();

        while (GitUtils.next(treeWalk)) {

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
        GitUtils.addTree(treeWalk, objectId);
        List<GitFile> gitFiles = new ArrayList<GitFile>();

        while (GitUtils.next(treeWalk)) {

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
}
