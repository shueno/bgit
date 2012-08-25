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

    private Project project;

    private final JTextField projectPathTextField;

    private final JTextField nameTextField;

    public ProjectDialog(Application application) {
        super(application);

        setTitle("Project");
        setSize(new Dimension(500, 140));
        bindWindowSettings();

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
        gbl_projectPanel.rowHeights = new int[] { 0, 0, 0 };
        gbl_projectPanel.columnWeights = new double[] { 0.0, 1.0, 0.0,
                Double.MIN_VALUE };
        gbl_projectPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
        projectPanel.setLayout(gbl_projectPanel);

        JLabel projectPathLabel = new JLabel("Project path");
        GridBagConstraints gbc_projectPathLabel = new GridBagConstraints();
        gbc_projectPathLabel.anchor = GridBagConstraints.EAST;
        gbc_projectPathLabel.insets = new Insets(0, 0, 5, 5);
        gbc_projectPathLabel.gridx = 0;
        gbc_projectPathLabel.gridy = 0;
        projectPanel.add(projectPathLabel, gbc_projectPathLabel);

        projectPathTextField = new JTextField();
        GridBagConstraints gbc_projectPathTextField = new GridBagConstraints();
        gbc_projectPathTextField.insets = new Insets(0, 0, 5, 5);
        gbc_projectPathTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_projectPathTextField.gridx = 1;
        gbc_projectPathTextField.gridy = 0;
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
        gbc_projectPathFolderButton.gridy = 0;
        projectPanel.add(projectPathFolderButton, gbc_projectPathFolderButton);

        JLabel projectNameLabel = new JLabel("Project name");
        GridBagConstraints gbc_projectNameLabel = new GridBagConstraints();
        gbc_projectNameLabel.anchor = GridBagConstraints.EAST;
        gbc_projectNameLabel.insets = new Insets(0, 0, 0, 5);
        gbc_projectNameLabel.gridx = 0;
        gbc_projectNameLabel.gridy = 1;
        projectPanel.add(projectNameLabel, gbc_projectNameLabel);

        nameTextField = new JTextField();
        GridBagConstraints gbc_nameTextField = new GridBagConstraints();
        gbc_nameTextField.insets = new Insets(0, 0, 0, 5);
        gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_nameTextField.gridx = 1;
        gbc_nameTextField.gridy = 1;
        projectPanel.add(nameTextField, gbc_nameTextField);
        nameTextField.setColumns(10);
    }

    private void handleProjectPathFolderActionPerformed() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File absolutePath = fileChooser.getSelectedFile();
        projectPathTextField.setText(absolutePath.toString());
        nameTextField.setText(absolutePath.getName());
    }

    private void handleOkActionPerformed() {
        File absolutePath = new File(projectPathTextField.getText().trim());

        if (!absolutePath.isDirectory()) {
            JOptionPane.showMessageDialog(null, "Project path is invalid.");
            projectPathTextField.requestFocusInWindow();
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
