package bgit.model;

import java.io.File;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectStream;

import bgit.CommonsUtils;
import bgit.GitUtils;
import bgit.JdkUtils;

public class GitFile extends GitNode {

    private File temporaryPath;

    GitFile(Project project, ObjectId objectId, String relativePathString) {
        super(project, objectId, relativePathString);
    }

    public File createTemporaryFile() {

        if (temporaryPath != null && temporaryPath.isFile()) {
            return temporaryPath;
        }

        String suffix = null;
        int lastIndex = relativePathString.lastIndexOf('.');

        if (lastIndex >= 0) {
            suffix = relativePathString.substring(lastIndex);
        }

        String prefix = "git_" + objectId.getName() + "_";
        temporaryPath = JdkUtils.createTempFile(prefix, suffix);

        ObjectLoader objectLoader = GitUtils.open(project.getGit()
                .getRepository(), objectId);
        ObjectStream openStream = GitUtils.openStream(objectLoader);

        CommonsUtils.copyInputStreamToFile(openStream, temporaryPath);
        return temporaryPath;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", getShortIdString(), relativePathString);
    }

    public boolean isTracked() {
        return project.getGit().getRepository().hasObject(objectId);
    }
}
