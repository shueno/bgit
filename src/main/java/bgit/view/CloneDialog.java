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

    private final JTextField urlTextField;

    private final JTextField nameTextField;
    private final JTextField pathTextField;

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

        JLabel lblProjectPath = new JLabel("Git URL");
        GridBagConstraints gbc_lblProjectPath = new GridBagConstraints();
        gbc_lblProjectPath.anchor = GridBagConstraints.EAST;
        gbc_lblProjectPath.insets = new Insets(0, 0, 5, 5);
        gbc_lblProjectPath.gridx = 0;
        gbc_lblProjectPath.gridy = 0;
        projectPanel.add(lblProjectPath, gbc_lblProjectPath);

        urlTextField = new JTextField();
        GridBagConstraints gbc_urlTextField = new GridBagConstraints();
        gbc_urlTextField.insets = new Insets(0, 0, 5, 5);
        gbc_urlTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_urlTextField.gridx = 1;
        gbc_urlTextField.gridy = 0;
        projectPanel.add(urlTextField, gbc_urlTextField);
        urlTextField.setColumns(10);

        JButton urlBrowseButton = new JButton("Browse");
        urlBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUrlBrowseActionPerformed();
            }
        });
        GridBagConstraints gbc_urlButton = new GridBagConstraints();
        gbc_urlButton.insets = new Insets(0, 0, 5, 0);
        gbc_urlButton.gridx = 2;
        gbc_urlButton.gridy = 0;
        projectPanel.add(urlBrowseButton, gbc_urlButton);

        JLabel lblProjectPath_1 = new JLabel("Project path");
        GridBagConstraints gbc_lblProjectPath_1 = new GridBagConstraints();
        gbc_lblProjectPath_1.anchor = GridBagConstraints.EAST;
        gbc_lblProjectPath_1.insets = new Insets(0, 0, 5, 5);
        gbc_lblProjectPath_1.gridx = 0;
        gbc_lblProjectPath_1.gridy = 1;
        projectPanel.add(lblProjectPath_1, gbc_lblProjectPath_1);

        pathTextField = new JTextField();
        GridBagConstraints gbc_pathTextField = new GridBagConstraints();
        gbc_pathTextField.insets = new Insets(0, 0, 5, 5);
        gbc_pathTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_pathTextField.gridx = 1;
        gbc_pathTextField.gridy = 1;
        projectPanel.add(pathTextField, gbc_pathTextField);
        pathTextField.setColumns(10);

        JButton pathBrowseButton = new JButton("Browse");
        pathBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePathBrowseActionPerformed();
            }
        });
        GridBagConstraints gbc_pathButton = new GridBagConstraints();
        gbc_pathButton.insets = new Insets(0, 0, 5, 0);
        gbc_pathButton.gridx = 2;
        gbc_pathButton.gridy = 1;
        projectPanel.add(pathBrowseButton, gbc_pathButton);

        JLabel lblProjectName = new JLabel("Project name");
        GridBagConstraints gbc_lblProjectName = new GridBagConstraints();
        gbc_lblProjectName.anchor = GridBagConstraints.EAST;
        gbc_lblProjectName.insets = new Insets(0, 0, 0, 5);
        gbc_lblProjectName.gridx = 0;
        gbc_lblProjectName.gridy = 2;
        projectPanel.add(lblProjectName, gbc_lblProjectName);

        nameTextField = new JTextField();
        GridBagConstraints gbc_nameTextField = new GridBagConstraints();
        gbc_nameTextField.insets = new Insets(0, 0, 0, 5);
        gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_nameTextField.gridx = 1;
        gbc_nameTextField.gridy = 2;
        projectPanel.add(nameTextField, gbc_nameTextField);
        nameTextField.setColumns(10);
    }

    private void handleUrlBrowseActionPerformed() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File path = fileChooser.getSelectedFile();
        urlTextField.setText(path.toString());
    }

    private void handlePathBrowseActionPerformed() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File path = fileChooser.getSelectedFile();
        pathTextField.setText(path.toString());
        nameTextField.setText(path.getName());
    }

    private void handleOkActionPerformed() {
        String gitUrlString = urlTextField.getText().trim();

        if (!Application.isGitUrlValid(gitUrlString)) {
            JOptionPane.showMessageDialog(null, "Git URL is invalid.");
            urlTextField.requestFocusInWindow();
            return;
        }

        File projectPath = new File(pathTextField.getText().trim());

        if (!projectPath.isDirectory()) {
            JOptionPane.showMessageDialog(null, "Project path is invalid.");
            pathTextField.requestFocusInWindow();
            return;
        }

        String name = nameTextField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Project name is invalid.");
            nameTextField.requestFocusInWindow();
            return;
        }

        project = application.cloneProject(gitUrlString, projectPath, name);
        fireWindowClosing();
    }

    public Project getProject() {
        return project;
    }
}
