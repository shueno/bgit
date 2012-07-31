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
public class ProjectDialog extends AbstractDialog {

    private final Application application;

    private Project project;

    private final JTextField pathTextField;

    private final JTextField nameTextField;

    public ProjectDialog(Application application) {
        this.application = application;

        setTitle("Project");
        setSize(new Dimension(500, 140));
        setLocationRelativeTo(null);

        JPanel footerPanel = new JPanel();
        getContentPane().add(footerPanel, BorderLayout.SOUTH);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleOkButtonActionPerformed();
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
        gbl_projectPanel.rowHeights = new int[] { 0, 0, 0 };
        gbl_projectPanel.columnWeights = new double[] { 0.0, 1.0, 0.0,
                Double.MIN_VALUE };
        gbl_projectPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
        projectPanel.setLayout(gbl_projectPanel);

        JLabel lblProjectPath = new JLabel("Project path");
        GridBagConstraints gbc_lblProjectPath = new GridBagConstraints();
        gbc_lblProjectPath.anchor = GridBagConstraints.EAST;
        gbc_lblProjectPath.insets = new Insets(0, 0, 5, 5);
        gbc_lblProjectPath.gridx = 0;
        gbc_lblProjectPath.gridy = 0;
        projectPanel.add(lblProjectPath, gbc_lblProjectPath);

        pathTextField = new JTextField();
        GridBagConstraints gbc_pathTextField = new GridBagConstraints();
        gbc_pathTextField.insets = new Insets(0, 0, 5, 5);
        gbc_pathTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_pathTextField.gridx = 1;
        gbc_pathTextField.gridy = 0;
        projectPanel.add(pathTextField, gbc_pathTextField);
        pathTextField.setColumns(10);

        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleBrowseButtonActionPerformed();
            }
        });
        GridBagConstraints gbc_browseButton = new GridBagConstraints();
        gbc_browseButton.insets = new Insets(0, 0, 5, 0);
        gbc_browseButton.gridx = 2;
        gbc_browseButton.gridy = 0;
        projectPanel.add(browseButton, gbc_browseButton);

        JLabel lblProjectName = new JLabel("Project name");
        GridBagConstraints gbc_lblProjectName = new GridBagConstraints();
        gbc_lblProjectName.anchor = GridBagConstraints.EAST;
        gbc_lblProjectName.insets = new Insets(0, 0, 0, 5);
        gbc_lblProjectName.gridx = 0;
        gbc_lblProjectName.gridy = 1;
        projectPanel.add(lblProjectName, gbc_lblProjectName);

        nameTextField = new JTextField();
        GridBagConstraints gbc_nameTextField = new GridBagConstraints();
        gbc_nameTextField.insets = new Insets(0, 0, 0, 5);
        gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_nameTextField.gridx = 1;
        gbc_nameTextField.gridy = 1;
        projectPanel.add(nameTextField, gbc_nameTextField);
        nameTextField.setColumns(10);
    }

    private void handleBrowseButtonActionPerformed() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File absolutePath = fileChooser.getSelectedFile();
        pathTextField.setText(absolutePath.toString());
        nameTextField.setText(absolutePath.getName());
    }

    private void handleOkButtonActionPerformed() {
        File absolutePath = new File(pathTextField.getText().trim());

        if (!absolutePath.isDirectory()) {
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

        project = application.createProject(absolutePath, name);
        fireWindowClosing();
    }

    public Project getProject() {
        return project;
    }
}
