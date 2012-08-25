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
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import bgit.model.Application;
import bgit.model.GitConfig;
import bgit.model.Project;

@SuppressWarnings("serial")
public class ShareDialog extends AbstractDialog {

    private final Project project;

    private boolean succeeded;

    private final JTextField repositoryPathTextField;

    private final JCheckBox connectCheckbox;

    public ShareDialog(Application application, Project project) {
        super(application);
        this.project = project;

        setTitle("Share");
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

        JLabel repositoryPathLabel = new JLabel("New repository path");
        GridBagConstraints gbc_repositoryPathLabel = new GridBagConstraints();
        gbc_repositoryPathLabel.anchor = GridBagConstraints.EAST;
        gbc_repositoryPathLabel.insets = new Insets(0, 0, 5, 5);
        gbc_repositoryPathLabel.gridx = 0;
        gbc_repositoryPathLabel.gridy = 0;
        projectPanel.add(repositoryPathLabel, gbc_repositoryPathLabel);

        repositoryPathTextField = new JTextField();
        GridBagConstraints gbc_repositoryPathTextField = new GridBagConstraints();
        gbc_repositoryPathTextField.insets = new Insets(0, 0, 5, 5);
        gbc_repositoryPathTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_repositoryPathTextField.gridx = 1;
        gbc_repositoryPathTextField.gridy = 0;
        projectPanel.add(repositoryPathTextField, gbc_repositoryPathTextField);
        repositoryPathTextField.setColumns(10);

        JButton repositoryPathFolderButton = new JButton("Folder");
        repositoryPathFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRepositoryPathFolderActionPerformed();
            }
        });
        GridBagConstraints gbc_repositoryPathFolderButton = new GridBagConstraints();
        gbc_repositoryPathFolderButton.insets = new Insets(0, 0, 5, 0);
        gbc_repositoryPathFolderButton.gridx = 2;
        gbc_repositoryPathFolderButton.gridy = 0;
        projectPanel.add(repositoryPathFolderButton,
                gbc_repositoryPathFolderButton);

        connectCheckbox = new JCheckBox("Connect");
        connectCheckbox.setSelected(true);
        GridBagConstraints gbc_connectCheckbox = new GridBagConstraints();
        gbc_connectCheckbox.anchor = GridBagConstraints.WEST;
        gbc_connectCheckbox.insets = new Insets(0, 0, 0, 5);
        gbc_connectCheckbox.gridx = 1;
        gbc_connectCheckbox.gridy = 1;
        projectPanel.add(connectCheckbox, gbc_connectCheckbox);
    }

    private void handleRepositoryPathFolderActionPerformed() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File path = fileChooser.getSelectedFile();
        repositoryPathTextField.setText(path.toString());
    }

    private void handleOkActionPerformed() {
        String repositoryPathString = repositoryPathTextField.getText().trim();

        if (!repositoryPathString.endsWith(".git")) {
            JOptionPane.showMessageDialog(null,
                    "New repository path doesn't end with '.git'.");
            repositoryPathTextField.requestFocusInWindow();
            return;
        }

        File repositoryPath = new File(repositoryPathString);

        if (!repositoryPath.isDirectory()) {
            JOptionPane.showMessageDialog(null, "Repository path is invalid.");
            repositoryPathTextField.requestFocusInWindow();
            return;
        }

        project.bare(repositoryPath);

        if (connectCheckbox.isSelected()) {
            GitConfig gitConfig = project.findGitConfig();
            String branchName = project.getCurrentBranchName();
            String remoteName = gitConfig.getConnectionRemoteName(branchName);
            gitConfig.setConnection(remoteName, repositoryPathString,
                    branchName);
            gitConfig.save();
        }

        succeeded = true;
        fireWindowClosing();
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}
