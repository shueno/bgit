package bgit.model;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;

import bgit.GitUtils;
import bgit.JdkUtils;

public class Application {

    private final Preferences preferences;

    private final Preferences projectsPreferences;

    public Application(Preferences preferences) {
        this.preferences = preferences;
        this.projectsPreferences = preferences.node("projects");
    }

    public WindowSettings findWindowSettings(String name) {
        return new WindowSettings(preferences.node(name));
    }

    public GitConfig findGitConfig() {
        FS fs = FS.DETECTED;
        File userHomePath = fs.userHome();

        if (userHomePath == null) {
            return null;
        }

        File configPath = new File(userHomePath, ".gitconfig");
        FileBasedConfig config = new FileBasedConfig(configPath, fs);
        GitUtils.load(config);
        return new GitConfig(config);
    }

    public Iterable<Project> findProjects() {
        List<Project> projects = new ArrayList<Project>();

        for (String idString : JdkUtils.childrenNames(projectsPreferences)) {
            Long id = new Long(idString);
            Preferences projectPreferences = getProjectPreferences(id);
            File projectPath = new File(projectPreferences.get("path", ""));

            if (!new File(projectPath, ".git").exists()) {
                continue;
            }

            String name = projectPreferences.get("name", "");
            Project project = new Project(this, id, projectPath, name);
            projects.add(project);
        }

        Collections.sort(projects);
        return projects;
    }

    public Project createProject(File projectPath, String name) {
        InitCommand initCommand = Git.init();
        initCommand.setDirectory(projectPath);
        GitUtils.call(initCommand);
        return writeProject(projectPath, name);
    }

    public static boolean isRepositoryUrlValid(String repositoryUrlString) {

        try {
            new URIish(repositoryUrlString);

        } catch (URISyntaxException e) {
            return false;
        }

        return true;
    }

    public Project cloneProject(String repositoryUrlString, File projectPath,
            String name) {
        CloneCommand cloneCommand = Git.cloneRepository();
        cloneCommand.setURI(repositoryUrlString);
        cloneCommand.setDirectory(projectPath);
        GitUtils.call(cloneCommand);
        return writeProject(projectPath, name);
    }

    private Project writeProject(File projectPath, String name) {
        long id = projectsPreferences.getLong("lastId", 0L) + 1L;
        projectsPreferences.putLong("lastId", id);
        Preferences projectPreferences = getProjectPreferences(id);
        projectPreferences.put("path", projectPath.toString());
        projectPreferences.put("name", name);
        JdkUtils.flush(projectsPreferences);
        return new Project(this, id, projectPath, name);
    }

    Preferences getProjectPreferences(long id) {
        return projectsPreferences.node(String.valueOf(id));
    }
}
