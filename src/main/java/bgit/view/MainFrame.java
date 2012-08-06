package bgit.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bgit.model.Application;
import bgit.model.GitConfig;
import bgit.model.Project;

// TODO Save and restore window size.
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    private final Application application;

    private final JMenuBar menuBar;

    private final JTabbedPane tabbedPane;

    private final Action exitAction = new ExitAction();

    private final Action newProjectAction = new NewProjectAction();

    private final Action cloneProjectAction = new CloneProject();

    private final Action renameProjectAction = new RenameProjectAction();

    private final Action deleteProjectAction = new DeleteProjectAction();

    private final Action configApplicationAction = new ConfigApplicationAction();

    private final Action userSettingsAction = new UserSettingsAction();

    private final Map<String, Action> menuActionMap = new HashMap<String, Action>();

    public MainFrame(Application application) {
        this.application = application;

        setTitle("BGit");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1000, 500));
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                handleWindowOpened();
            }
        });

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        fileMenu.add(exitAction);

        JMenu projectMenu = new JMenu("Project");
        menuBar.add(projectMenu);
        projectMenu.add(newProjectAction);
        projectMenu.add(cloneProjectAction);
        projectMenu.add(renameProjectAction).setVisible(false);
        projectMenu.add(deleteProjectAction).setVisible(false);

        JMenu applicationMenuItem = new JMenu("Application");
        menuBar.add(applicationMenuItem);
        applicationMenuItem.add(userSettingsAction);
        applicationMenuItem.add(configApplicationAction);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                handleTabbedPaneStateChanged();
            }
        });
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    private void handleWindowOpened() {

        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);

            for (int j = 0; j < menu.getItemCount(); j++) {
                JMenuItem menuItem = menu.getItem(j);

                if (menuItem == null) {
                    continue;
                }

                menuActionMap.put(convertToKey(menu, menuItem),
                        menuItem.getAction());
            }
        }

        for (Project project : application.findProjects()) {
            ProjectPanel projectPanel = new ProjectPanel(project);
            insertProjectPanel(projectPanel);
        }
    }

    private String convertToKey(JMenu menu, JMenuItem menuItem) {
        return String.format("%s/%s", menu.getText(), menuItem.getText());
    }

    private void insertProjectPanel(ProjectPanel newProjectPanel) {
        Project newProject = newProjectPanel.getProject();
        int insertIndex = 0;

        for (int i = tabbedPane.getTabCount() - 1; i >= 0; i--) {
            ProjectPanel projectPanel = (ProjectPanel) tabbedPane
                    .getComponentAt(i);

            if (newProject.compareTo(projectPanel.getProject()) >= 0) {
                insertIndex = i + 1;
                break;
            }
        }

        tabbedPane.insertTab(newProject.getName(), null, newProjectPanel,
                newProject.getAbsolutePath().toString(), insertIndex);
    }

    private void handleTabbedPaneStateChanged() {
        ProjectPanel projectPanel = (ProjectPanel) tabbedPane
                .getSelectedComponent();

        if (projectPanel != null) {
            JMenuBar panelMenuBar = projectPanel.getMenuBar();

            for (int i = 0; i < panelMenuBar.getMenuCount(); i++) {
                JMenu panelMenu = panelMenuBar.getMenu(i);

                for (int j = 0; j < panelMenu.getItemCount(); j++) {
                    JMenuItem panelMenuItem = panelMenu.getItem(j);

                    if (panelMenuItem == null) {
                        continue;
                    }

                    Action action = menuActionMap.get(convertToKey(panelMenu,
                            panelMenuItem));

                    if (action == null) {
                        continue;
                    }

                    panelMenuItem.setAction(action);
                }
            }

            setJMenuBar(panelMenuBar);

        } else {
            setJMenuBar(menuBar);
        }
    }

    private void handleExitActionPerformed() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private class ExitAction extends AbstractAction {

        public ExitAction() {
            putValue(NAME, "Exit");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            handleExitActionPerformed();
        }
    }

    // TODO Review to distinguish NewProject and OpenProject
    private class NewProjectAction extends AbstractAction {

        public NewProjectAction() {
            putValue(NAME, "New project");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ProjectDialog projectDialog = new ProjectDialog(application);
            projectDialog.setVisible(true);
            Project project = projectDialog.getProject();

            if (project == null) {
                return;
            }

            ProjectPanel projectPanel = new ProjectPanel(project);
            insertProjectPanel(projectPanel);
            tabbedPane.setSelectedComponent(projectPanel);
        }
    }

    private class CloneProject extends AbstractAction {

        public CloneProject() {
            putValue(NAME, "Clone from");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CloneDialog cloneDialog = new CloneDialog(application);
            cloneDialog.setVisible(true);
            Project project = cloneDialog.getProject();

            if (project == null) {
                return;
            }

            ProjectPanel projectPanel = new ProjectPanel(project);
            insertProjectPanel(projectPanel);
            tabbedPane.setSelectedComponent(projectPanel);
        }
    }

    private class RenameProjectAction extends AbstractAction {

        public RenameProjectAction() {
            putValue(NAME, "Rename");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ProjectPanel projectPanel = (ProjectPanel) tabbedPane
                    .getSelectedComponent();

            if (projectPanel == null) {
                return;
            }

            Project project = projectPanel.getProject();
            String name = null;

            while (true) {
                name = JOptionPane.showInputDialog("Project name",
                        project.getName());

                if (name == null) {
                    return;
                }

                name = name.trim();

                if (!name.isEmpty()) {
                    break;
                }

                JOptionPane.showMessageDialog(null, "Project name is invalid.");
            }

            project.rename(name);
            tabbedPane.remove(projectPanel);
            insertProjectPanel(projectPanel);
            tabbedPane.setSelectedComponent(projectPanel);
        }
    }

    private class DeleteProjectAction extends AbstractAction {

        public DeleteProjectAction() {
            putValue(NAME, "Delete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ProjectPanel projectPanel = (ProjectPanel) tabbedPane
                    .getSelectedComponent();

            if (projectPanel == null) {
                return;
            }

            int option = JOptionPane.showConfirmDialog(null, "Delete?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            if (option != JOptionPane.YES_OPTION) {
                return;
            }

            projectPanel.getProject().delete();
            tabbedPane.remove(projectPanel);
        }
    }

    private class ConfigApplicationAction extends AbstractAction {

        public ConfigApplicationAction() {
            putValue(NAME, "Config");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            GitConfig gitConfig = application.findGitConfig();

            if (gitConfig == null) {
                JOptionPane.showMessageDialog(null, "Not found");
                return;
            }

            ConfigDialog configDialog = new ConfigDialog(gitConfig);
            configDialog.setVisible(true);
        }
    }

    private class UserSettingsAction extends AbstractAction {

        public UserSettingsAction() {
            putValue(NAME, "User settings");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            GitConfig gitConfig = application.findGitConfig();

            if (gitConfig == null) {
                JOptionPane.showMessageDialog(null, "Not found");
                return;
            }

            UserSettingsDialog userSettingsDialog = new UserSettingsDialog(
                    gitConfig);
            userSettingsDialog.setVisible(true);
        }
    }
}
