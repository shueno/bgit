package bgit.model;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;

public class GitDiffEntry {

    private final Project project;

    private final DiffEntry diffEntry;

    GitDiffEntry(Project project, DiffEntry diffEntry) {
        this.project = project;
        this.diffEntry = diffEntry;
    }

    public GitFile findOldGitFile() {
        return new GitFile(project, diffEntry.getOldId().toObjectId(),
                diffEntry.getOldPath());
    }

    public GitFile findNewGitFile() {
        return new GitFile(project, diffEntry.getNewId().toObjectId(),
                diffEntry.getNewPath());
    }

    public String getDisplayPathString() {
        String pathString = diffEntry.getNewPath();

        if (pathString.equals(DiffEntry.DEV_NULL)) {
            return diffEntry.getOldPath();
        }

        return pathString;
    }

    public String getOldPathString() {
        return diffEntry.getOldPath();
    }

    public String getNewPathString() {
        return diffEntry.getNewPath();
    }

    public ChangeType getChangeType() {
        return diffEntry.getChangeType();
    }

    public String getOldIdString() {
        return diffEntry.getOldId().name();
    }

    public String getNewIdString() {
        return diffEntry.getNewId().name();
    }
}
