package bgit.model;

import java.io.File;

public interface WorkNodeListener {

    void workNodeCreated(File absolutePath);

    void workNodeDeleted(File absolutePath);

    void workNodeChanged(File absolutePath);
}
