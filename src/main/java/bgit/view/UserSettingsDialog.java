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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import bgit.model.GitConfig;

@SuppressWarnings("serial")
public class UserSettingsDialog extends AbstractDialog {

    private final GitConfig gitConfig;

    private final JTextField nameTextField;

    private final JTextField emailTextField;

    public UserSettingsDialog(GitConfig gitConfig) {
        this.gitConfig = gitConfig;

        setTitle("User Settings");
        setSize(new Dimension(500, 140));
        setLocationRelativeTo(null);
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

        JPanel configEntryPanel = new JPanel();
        getContentPane().add(configEntryPanel, BorderLayout.CENTER);
        GridBagLayout gbl_configEntryPanel = new GridBagLayout();
        gbl_configEntryPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_configEntryPanel.rowHeights = new int[] { 0, 0, 0 };
        gbl_configEntryPanel.columnWeights = new double[] { 0.0, 1.0,
                Double.MIN_VALUE };
        gbl_configEntryPanel.rowWeights = new double[] { 0.0, 0.0,
                Double.MIN_VALUE };
        configEntryPanel.setLayout(gbl_configEntryPanel);

        JLabel nameLabel = new JLabel("User name");
        GridBagConstraints gbc_nameLabel = new GridBagConstraints();
        gbc_nameLabel.anchor = GridBagConstraints.EAST;
        gbc_nameLabel.insets = new Insets(0, 0, 5, 5);
        gbc_nameLabel.gridx = 0;
        gbc_nameLabel.gridy = 0;
        configEntryPanel.add(nameLabel, gbc_nameLabel);

        nameTextField = new JTextField();
        GridBagConstraints gbc_nameTextField = new GridBagConstraints();
        gbc_nameTextField.insets = new Insets(0, 0, 5, 0);
        gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_nameTextField.gridx = 1;
        gbc_nameTextField.gridy = 0;
        configEntryPanel.add(nameTextField, gbc_nameTextField);
        nameTextField.setColumns(10);

        JLabel emailLabel = new JLabel("User email");
        GridBagConstraints gbc_emailLabel = new GridBagConstraints();
        gbc_emailLabel.anchor = GridBagConstraints.EAST;
        gbc_emailLabel.insets = new Insets(0, 0, 0, 5);
        gbc_emailLabel.gridx = 0;
        gbc_emailLabel.gridy = 1;
        configEntryPanel.add(emailLabel, gbc_emailLabel);

        emailTextField = new JTextField();
        GridBagConstraints gbc_emailTextField = new GridBagConstraints();
        gbc_emailTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_emailTextField.gridx = 1;
        gbc_emailTextField.gridy = 1;
        configEntryPanel.add(emailTextField, gbc_emailTextField);
        emailTextField.setColumns(10);
    }

    private void handleWindowOpened() {
        nameTextField.setText(gitConfig.getUserName());
        emailTextField.setText(gitConfig.getUserEmail());
    }

    private void handleOkActionPerformed() {
        String name = nameTextField.getText().trim();
        String email = emailTextField.getText().trim();
        gitConfig.setUser(name, email);
        gitConfig.save();
        fireWindowClosing();
    }
}
