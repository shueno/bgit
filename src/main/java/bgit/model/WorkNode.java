package bgit.model;

import java.io.File;
import java.sql.Timestamp;

import bgit.CommonsUtils;

public abstract class WorkNode {

    public static final String GIT_IGNORE_NAME = ".gitignore";

    protected final Project project;

    protected final File absolutePath;

    protected boolean ignored;

    WorkNode(Project project, File absolutePath, boolean ignored) {
        this.project = project;
        this.absolutePath = absolutePath;
        this.ignored = ignored;
    }

    public abstract boolean delete();

    public boolean rename(String name) {
        return absolutePath.renameTo(new File(absolutePath.getParent(), name));
    }

    public void ignore() {
        File path = new File(absolutePath.getParent(), GIT_IGNORE_NAME);
        String data = absolutePath.toString().substring(
                absolutePath.getParent().toString().length())
                + "\n";
        CommonsUtils.writeStringToFile(path, data);
    }

    public boolean isExists() {
        return absolutePath.exists();
    }

    public boolean isAltered() {
        return false;
    }

    public String getRelativePathString() {
        return Project.convertToRelativePathString(project.getAbsolutePath()
                .toString(), absolutePath.toString());
    }

    public String getDisplayName() {
        StringBuilder sb = new StringBuilder();

        if (isAltered()) {
            sb.append("> ");
        }

        sb.append(absolutePath.getName());

        if (isIgnored()) {
            sb.append(" #");
        }

        return sb.toString();
    }

    public String getStatus() {
        return "";
    }

    public Timestamp getDateLastModified() {
        return new Timestamp(absolutePath.lastModified());
    }

    public Long getSize() {
        return absolutePath.length();
    }

    public File getAbsolutePath() {
        return absolutePath;
    }

    public boolean isIgnored() {
        return ignored;
    }

    void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }
}
