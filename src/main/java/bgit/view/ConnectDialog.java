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
import bgit.model.GitConfig;

@SuppressWarnings("serial")
public class ConnectDialog extends AbstractDialog {

    private final GitConfig gitConfig;

    private final String branchName;

    private final JTextField urlTextField;

    private String gitUrlString;

    public ConnectDialog(GitConfig gitConfig, String branchName) {
        this.gitConfig = gitConfig;
        this.branchName = branchName;

        setTitle("Connect");
        setSize(new Dimension(500, 110));
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
        gbl_projectPanel.rowHeights = new int[] { 0, 0 };
        gbl_projectPanel.columnWeights = new double[] { 0.0, 1.0, 0.0,
                Double.MIN_VALUE };
        gbl_projectPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        projectPanel.setLayout(gbl_projectPanel);

        JLabel lblProjectPath = new JLabel("Git URL");
        GridBagConstraints gbc_lblProjectPath = new GridBagConstraints();
        gbc_lblProjectPath.anchor = GridBagConstraints.EAST;
        gbc_lblProjectPath.insets = new Insets(0, 0, 0, 5);
        gbc_lblProjectPath.gridx = 0;
        gbc_lblProjectPath.gridy = 0;
        projectPanel.add(lblProjectPath, gbc_lblProjectPath);

        urlTextField = new JTextField();
        GridBagConstraints gbc_urlTextField = new GridBagConstraints();
        gbc_urlTextField.insets = new Insets(0, 0, 0, 5);
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
        gbc_urlButton.gridx = 2;
        gbc_urlButton.gridy = 0;
        projectPanel.add(urlBrowseButton, gbc_urlButton);
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

    private void handleOkActionPerformed() {
        String gitUrlString = urlTextField.getText().trim();

        if (!Application.isGitUrlValid(gitUrlString)) {
            JOptionPane.showMessageDialog(null, "Git URL is invalid.");
            urlTextField.requestFocusInWindow();
            return;
        }

        gitConfig.saveRemoteOrigin(branchName, gitUrlString);
        this.gitUrlString = gitUrlString;
        fireWindowClosing();
    }

    public String getGitUrlString() {
        return gitUrlString;
    }
}
