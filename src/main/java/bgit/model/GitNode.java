package bgit.model;

import org.eclipse.jgit.lib.ObjectId;

public abstract class GitNode {

    protected final Project project;

    protected final ObjectId objectId;

    protected final String relativePathString;

    GitNode(Project project, ObjectId objectId, String relativePathString) {
        this.project = project;
        this.objectId = objectId;
        this.relativePathString = relativePathString;
    }

    public boolean isRoot() {
        return relativePathString.isEmpty();
    }

    public String getDisplayName() {

        if (isRoot()) {
            return project.getAbsolutePath().getName();
        }

        return relativePathString
                .substring(relativePathString.lastIndexOf("/") + 1);
    }

    public String getRelativePathString() {
        return relativePathString;
    }

    public String getParentRelativePathString() {

        if (isRoot()) {
            return null;
        }

        int index = relativePathString.lastIndexOf("/");

        if (index < 0) {
            return "";
        }

        return relativePathString.substring(0, index);
    }

    public String getShortIdString() {
        return objectId.name().substring(0, 8);
    }
}
