package bgit.model;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bgit.CommonsUtils;
import bgit.JdkUtils;

public class WorkFolder extends WorkNode {

    private final boolean altered;

    WorkFolder(Project project, File absolutePath, boolean ignored,
            boolean altered) {
        super(project, absolutePath, ignored);
        this.altered = altered;
    }

    @Override
    public boolean delete() {
        CommonsUtils.deleteDirectory(absolutePath);
        return true;
    }

    public boolean createNewFolder(String name) {
        return new File(absolutePath, name).mkdir();
    }

    public boolean createNewFile(String name) {
        return JdkUtils.createNewFile(new File(absolutePath, name));
    }

    public boolean isRoot() {
        return absolutePath.toString().equals(
                project.getAbsolutePath().toString());
    }

    public Iterable<WorkFolder> findChildWorkFolders() {
        List<WorkFolder> childWorkFolders = new ArrayList<WorkFolder>();
        File[] childPaths = absolutePath.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        Arrays.sort(childPaths);

        for (File childPath : childPaths) {
            WorkFolder childWorkFolder = project.findWorkFolder(childPath);

            if (childWorkFolder == null) {
                continue;
            }

            childWorkFolders.add(childWorkFolder);
        }

        return childWorkFolders;
    }

    public Iterable<WorkFile> findChildWorkFiles() {
        List<WorkFile> childWorkFiles = new ArrayList<WorkFile>();
        File[] childPaths = absolutePath.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
        Arrays.sort(childPaths);

        for (File childPath : childPaths) {
            WorkFile childWorkFile = project.findWorkFile(childPath);

            if (childWorkFile == null) {
                continue;
            }

            childWorkFiles.add(childWorkFile);
        }

        return childWorkFiles;
    }

    @Override
    public boolean isAltered() {
        return altered;
    }
}
