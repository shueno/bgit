package bgit.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;

import bgit.ApplicationException;

public class GitFile {

    private final Project project;

    private final ObjectId objectId;

    private final String relativePathString;

    private File temporaryPath;

    GitFile(Project project, ObjectId objectId, String relativePathString) {
        this.project = project;
        this.objectId = objectId;
        this.relativePathString = relativePathString;
    }

    // TODO Review method name.
    public File getTemporaryPath() {

        if (temporaryPath != null && temporaryPath.isFile()) {
            return temporaryPath;
        }

        String suffix = null;
        int lastIndex = relativePathString.lastIndexOf('.');

        if (lastIndex >= 0) {
            suffix = relativePathString.substring(lastIndex);
        }

        // TODO Use IOUtils.copy and handle IOExcetpion on each method
        try {
            temporaryPath = File.createTempFile("git_" + objectId.getName()
                    + "_", suffix);

            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(temporaryPath));

            try {
                BufferedInputStream in = new BufferedInputStream(project
                        .getGit().getRepository().open(objectId).openStream());

                try {
                    byte[] buf = new byte[1024];
                    int len = 0;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                } finally {
                    in.close();
                }

            } finally {
                out.flush();
            }

            return temporaryPath;

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public String toString() {
        return String.format("%s - %s", getShortIdString(), relativePathString);
    }

    public String getShortIdString() {
        return objectId.name().substring(0, 8);
    }
}
