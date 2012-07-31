package bgit.model;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class ProjectTest {

    @Test
    public void testIsInProject() {
        assertTrue(Project.isInProject(new File("/p1"), new File("/p1")));
        assertTrue(Project.isInProject(new File("/p1"), new File(
                "/p1/.gitignore")));

        assertFalse(Project.isInProject(new File("/p1"), new File("/p1/.git")));
        assertFalse(Project.isInProject(new File("/p1"), new File(
                "/p1/.git/HEAD")));
        assertFalse(Project.isInProject(new File("/p1"),
                new File("/p1/p11.git")));
    }

    @Test
    public void testConvertToRelativePathString() {
        assertEquals("", Project.convertToRelativePathString("/p1", "/p1"));
        assertEquals("d1", Project.convertToRelativePathString("/p1", "/p1/d1"));
        assertEquals("f1.txt",
                Project.convertToRelativePathString("/p1", "/p1/f1.txt"));
        assertEquals("d1/f11.txt",
                Project.convertToRelativePathString("/p1", "/p1/d1/f11.txt"));
    }
}
