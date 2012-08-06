package bgit.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import bgit.model.Application;
import bgit.model.Project;

@SuppressWarnings("serial")
public class CloneDialog extends AbstractDialog {

    private final Application application;

    private Project project;

    private final JTextField repositoryUrlTextField;

    private final JTextField projectNameTextField;

    private final JTextField projectPathTextField;

    public CloneDialog(Application application) {
        this.application = application;

        setTitle("Clone");
        setSize(new Dimension(500, 180));
        setLocationRelativeTo(null);

        JPanel footerPanel = new JPanel();
        getContentPane().add(footerPanel, BorderLayout.SOUTH);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleOkActionPerformed();
            }
        });
        footerPanel.add(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireWindowClosing();
            }
        });
        footerPanel.add(cancelButton);

        JPanel projectPanel = new JPanel();
        getContentPane().add(projectPanel, BorderLayout.CENTER);
        GridBagLayout gbl_projectPanel = new GridBagLayout();
        gbl_projectPanel.columnWidths = new int[] { 0, 0, 0, 0 };
        gbl_projectPanel.rowHeights = new int[] { 0, 0, 0, 0 };
        gbl_projectPanel.columnWeights = new double[] { 0.0, 1.0, 0.0,
                Double.MIN_VALUE };
        gbl_projectPanel.rowWeights = new double[] { 0.0, 0.0, 0.0,
                Double.MIN_VALUE };
        projectPanel.setLayout(gbl_projectPanel);

        JLabel repositoryUrlLabel = new JLabel("Repository URL");
        GridBagConstraints gbc_repositoryUrlLabel = new GridBagConstraints();
        gbc_repositoryUrlLabel.anchor = GridBagConstraints.EAST;
        gbc_repositoryUrlLabel.insets = new Insets(0, 0, 5, 5);
        gbc_repositoryUrlLabel.gridx = 0;
        gbc_repositoryUrlLabel.gridy = 0;
        projectPanel.add(repositoryUrlLabel, gbc_repositoryUrlLabel);

        repositoryUrlTextField = new JTextField();
        GridBagConstraints gbc_repositoryUrlTextField = new GridBagConstraints();
        gbc_repositoryUrlTextField.insets = new Insets(0, 0, 5, 5);
        gbc_repositoryUrlTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_repositoryUrlTextField.gridx = 1;
        gbc_repositoryUrlTextField.gridy = 0;
        projectPanel.add(repositoryUrlTextField, gbc_repositoryUrlTextField);
        repositoryUrlTextField.setColumns(10);

        JButton repositoryUrlFolderButton = new JButton("Folder");
        repositoryUrlFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRepositoryUrlFolderActionPerformed();
            }
        });
        GridBagConstraints gbc_repositoryUrlFolderButton = new GridBagConstraints();
        gbc_repositoryUrlFolderButton.insets = new Insets(0, 0, 5, 0);
        gbc_repositoryUrlFolderButton.gridx = 2;
        gbc_repositoryUrlFolderButton.gridy = 0;
        projectPanel.add(repositoryUrlFolderButton,
                gbc_repositoryUrlFolderButton);

        JLabel projectPathLabel = new JLabel("New project path");
        GridBagConstraints gbc_projectPathLabel = new GridBagConstraints();
        gbc_projectPathLabel.anchor = GridBagConstraints.EAST;
        gbc_projectPathLabel.insets = new Insets(0, 0, 5, 5);
        gbc_projectPathLabel.gridx = 0;
        gbc_projectPathLabel.gridy = 1;
        projectPanel.add(projectPathLabel, gbc_projectPathLabel);

        projectPathTextField = new JTextField();
        GridBagConstraints gbc_projectPathTextField = new GridBagConstraints();
        gbc_projectPathTextField.insets = new Insets(0, 0, 5, 5);
        gbc_projectPathTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_projectPathTextField.gridx = 1;
        gbc_projectPathTextField.gridy = 1;
        projectPanel.add(projectPathTextField, gbc_projectPathTextField);
        projectPathTextField.setColumns(10);

        JButton projectPathFolderButton = new JButton("Folder");
        projectPathFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleProjectPathFolderActionPerformed();
            }
        });
        GridBagConstraints gbc_projectPathFolderButton = new GridBagConstraints();
        gbc_projectPathFolderButton.insets = new Insets(0, 0, 5, 0);
        gbc_projectPathFolderButton.gridx = 2;
        gbc_projectPathFolderButton.gridy = 1;
        projectPanel.add(projectPathFolderButton, gbc_projectPathFolderButton);

        JLabel projectNameLabel = new JLabel("Project name");
        GridBagConstraints gbc_projectNameLabel = new GridBagConstraints();
        gbc_projectNameLabel.anchor = GridBagConstraints.EAST;
        gbc_projectNameLabel.insets = new Insets(0, 0, 0, 5);
        gbc_projectNameLabel.gridx = 0;
        gbc_projectNameLabel.gridy = 2;
        projectPanel.add(projectNameLabel, gbc_projectNameLabel);

        projectNameTextField = new JTextField();
        GridBagConstraints gbc_projectNameTextField = new GridBagConstraints();
        gbc_projectNameTextField.insets = new Insets(0, 0, 0, 5);
        gbc_projectNameTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_projectNameTextField.gridx = 1;
        gbc_projectNameTextField.gridy = 2;
        projectPanel.add(projectNameTextField, gbc_projectNameTextField);
        projectNameTextField.setColumns(10);
    }

    private void handleRepositoryUrlFolderActionPerformed() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File path = fileChooser.getSelectedFile();
        repositoryUrlTextField.setText(path.toString());
    }

    private void handleProjectPathFolderActionPerformed() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File path = fileChooser.getSelectedFile();
        projectPathTextField.setText(path.toString());
        projectNameTextField.setText(path.getName());
    }

    private void handleOkActionPerformed() {
        String repositoryUrlString = repositoryUrlTextField.getText().trim();

        if (!Application.isRepositoryUrlValid(repositoryUrlString)) {
            JOptionPane.showMessageDialog(null, "Repository URL is invalid.");
            repositoryUrlTextField.requestFocusInWindow();
            return;
        }

        File projectPath = new File(projectPathTextField.getText().trim());

        if (!projectPath.isDirectory()) {
            JOptionPane.showMessageDialog(null, "Project path is invalid.");
            projectPathTextField.requestFocusInWindow();
            return;
        }

        String projectName = projectNameTextField.getText().trim();

        if (projectName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Project name is invalid.");
            projectNameTextField.requestFocusInWindow();
            return;
        }

        project = application.cloneProject(repositoryUrlString, projectPath,
                projectName);
        fireWindowClosing();
    }

    public Project getProject() {
        return project;
    }
}
