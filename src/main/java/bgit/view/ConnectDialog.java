package bgit.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import bgit.model.Application;
import bgit.model.GitConfig;

@SuppressWarnings("serial")
public class ConnectDialog extends AbstractDialog {

    private final GitConfig gitConfig;

    private final String branchName;

    private boolean succeeded;

    private final JTextField remoteNameTextField;

    private final JTextField repositoryUrlTextField;

    private final JTextField branchNameTextField;

    public ConnectDialog(Application application, GitConfig gitConfig,
            String branchName) {
        super(application);
        this.gitConfig = gitConfig;
        this.branchName = branchName;

        setTitle("Connect");
        setSize(new Dimension(500, 180));
        bindWindowSettings();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                handleWindowOpened();
            }
        });

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

        JLabel remoteNameLabel = new JLabel("Remote name");
        GridBagConstraints gbc_remoteNameLabel = new GridBagConstraints();
        gbc_remoteNameLabel.anchor = GridBagConstraints.EAST;
        gbc_remoteNameLabel.insets = new Insets(0, 0, 5, 5);
        gbc_remoteNameLabel.gridx = 0;
        gbc_remoteNameLabel.gridy = 0;
        projectPanel.add(remoteNameLabel, gbc_remoteNameLabel);

        remoteNameTextField = new JTextField();
        GridBagConstraints gbc_remoteNameTextField = new GridBagConstraints();
        gbc_remoteNameTextField.insets = new Insets(0, 0, 5, 5);
        gbc_remoteNameTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_remoteNameTextField.gridx = 1;
        gbc_remoteNameTextField.gridy = 0;
        projectPanel.add(remoteNameTextField, gbc_remoteNameTextField);
        remoteNameTextField.setColumns(10);

        JLabel repositoryUrlLabel = new JLabel("Repository URL");
        GridBagConstraints gbc_repositoryUrlLabel = new GridBagConstraints();
        gbc_repositoryUrlLabel.anchor = GridBagConstraints.EAST;
        gbc_repositoryUrlLabel.insets = new Insets(0, 0, 5, 5);
        gbc_repositoryUrlLabel.gridx = 0;
        gbc_repositoryUrlLabel.gridy = 1;
        projectPanel.add(repositoryUrlLabel, gbc_repositoryUrlLabel);

        repositoryUrlTextField = new JTextField();
        GridBagConstraints gbc_repositoryUrlTextField = new GridBagConstraints();
        gbc_repositoryUrlTextField.insets = new Insets(0, 0, 5, 5);
        gbc_repositoryUrlTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_repositoryUrlTextField.gridx = 1;
        gbc_repositoryUrlTextField.gridy = 1;
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
        gbc_repositoryUrlFolderButton.gridy = 1;
        projectPanel.add(repositoryUrlFolderButton,
                gbc_repositoryUrlFolderButton);

        JLabel branchNameLabel = new JLabel("Branch name");
        GridBagConstraints gbc_branchNameLabel = new GridBagConstraints();
        gbc_branchNameLabel.anchor = GridBagConstraints.EAST;
        gbc_branchNameLabel.insets = new Insets(0, 0, 0, 5);
        gbc_branchNameLabel.gridx = 0;
        gbc_branchNameLabel.gridy = 2;
        projectPanel.add(branchNameLabel, gbc_branchNameLabel);

        branchNameTextField = new JTextField();
        branchNameTextField.setEditable(false);
        GridBagConstraints gbc_branchNameTextField = new GridBagConstraints();
        gbc_branchNameTextField.insets = new Insets(0, 0, 0, 5);
        gbc_branchNameTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_branchNameTextField.gridx = 1;
        gbc_branchNameTextField.gridy = 2;
        projectPanel.add(branchNameTextField, gbc_branchNameTextField);
        branchNameTextField.setColumns(10);
    }

    private void handleWindowOpened() {
        String remoteName = gitConfig.getConnectionRemoteName(branchName);
        remoteNameTextField.setText(remoteName);
        String urlString = gitConfig.getConnectionUrlString(remoteName);

        if (urlString != null) {
            repositoryUrlTextField.setText(urlString);
            repositoryUrlTextField.selectAll();
        }

        repositoryUrlTextField.requestFocusInWindow();
        branchNameTextField.setText(branchName);
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

    private void handleOkActionPerformed() {
        String remoteName = remoteNameTextField.getText().trim();

        if (remoteName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Remote name is invalid.");
            remoteNameTextField.requestFocusInWindow();
            return;
        }

        String repositoryUrlString = repositoryUrlTextField.getText().trim();

        if (!Application.isRepositoryUrlValid(repositoryUrlString)) {
            JOptionPane.showMessageDialog(null, "Repository URL is invalid.");
            repositoryUrlTextField.requestFocusInWindow();
            return;
        }

        gitConfig.setConnection(remoteName, repositoryUrlString, branchName);
        gitConfig.save();
        this.succeeded = true;
        fireWindowClosing();
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}
