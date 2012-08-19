package bgit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

public class CommonsUtils {

    public static FileSystemManager getManager() {

        try {
            return VFS.getManager();

        } catch (FileSystemException e) {
            throw new ApplicationException(e);
        }
    }

    public static FileObject resolveFile(FileSystemManager fileSystemManager,
            String name) {

        try {
            return fileSystemManager.resolveFile(name);

        } catch (FileSystemException e) {
            throw new ApplicationException(e);
        }
    }

    public static URL getURL(FileObject fileObject) {

        try {
            return fileObject.getURL();

        } catch (FileSystemException e) {
            throw new ApplicationException(e);
        }
    }

    public static void writeStringToFile(File path, String data) {

        try {
            FileUtils.writeStringToFile(path, data, true);

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static void deleteDirectory(File path) {

        try {
            FileUtils.deleteDirectory(path);

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static void copyInputStreamToFile(InputStream source,
            File destination) {

        try {
            FileUtils.copyInputStreamToFile(source, destination);

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }
}
