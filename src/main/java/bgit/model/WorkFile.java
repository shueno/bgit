package bgit.model;

import java.io.File;
import java.util.EnumSet;

public class WorkFile extends WorkNode {

    private final EnumSet<WorkNodeStatus> statusSet;

    WorkFile(Project project, File absolutePath, boolean ignored,
            EnumSet<WorkNodeStatus> statusSet) {
        super(project, absolutePath, ignored);
        this.statusSet = statusSet;
    }

    @Override
    public boolean delete() {
        return absolutePath.delete();
    }

    public EnumSet<WorkNodeStatus> getStatusSet() {
        return statusSet;
    }

    @Override
    public boolean isAltered() {
        return !statusSet.isEmpty();
    }

    @Override
    public String getStatus() {

        if (statusSet.isEmpty()) {
            return "";
        }

        return statusSet.toString();
    }
}
