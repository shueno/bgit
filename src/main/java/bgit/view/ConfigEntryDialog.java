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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import bgit.model.GitConfig;

@SuppressWarnings("serial")
public class ConfigEntryDialog extends AbstractDialog {

    private final GitConfig gitConfig;

    private String key;

    private final String oldValue;

    private String newValue;

    private final JTextField keyTextField;

    private final JTextField valueTextField;

    public ConfigEntryDialog(GitConfig gitConfig, String key, String oldValue) {
        this.gitConfig = gitConfig;
        this.key = key;
        this.oldValue = oldValue;

        setTitle("Config Entry");
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

        JLabel keyLabel = new JLabel("Config key ");
        GridBagConstraints gbc_keyLabel = new GridBagConstraints();
        gbc_keyLabel.anchor = GridBagConstraints.EAST;
        gbc_keyLabel.insets = new Insets(0, 0, 5, 5);
        gbc_keyLabel.gridx = 0;
        gbc_keyLabel.gridy = 0;
        configEntryPanel.add(keyLabel, gbc_keyLabel);

        keyTextField = new JTextField();
        GridBagConstraints gbc_keyTextField = new GridBagConstraints();
        gbc_keyTextField.insets = new Insets(0, 0, 5, 0);
        gbc_keyTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_keyTextField.gridx = 1;
        gbc_keyTextField.gridy = 0;
        configEntryPanel.add(keyTextField, gbc_keyTextField);
        keyTextField.setColumns(10);

        JLabel valueLabel = new JLabel("Config value");
        GridBagConstraints gbc_valueLabel = new GridBagConstraints();
        gbc_valueLabel.anchor = GridBagConstraints.EAST;
        gbc_valueLabel.insets = new Insets(0, 0, 0, 5);
        gbc_valueLabel.gridx = 0;
        gbc_valueLabel.gridy = 1;
        configEntryPanel.add(valueLabel, gbc_valueLabel);

        valueTextField = new JTextField();
        GridBagConstraints gbc_valueTextField = new GridBagConstraints();
        gbc_valueTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_valueTextField.gridx = 1;
        gbc_valueTextField.gridy = 1;
        configEntryPanel.add(valueTextField, gbc_valueTextField);
        valueTextField.setColumns(10);
    }

    private void handleWindowOpened() {

        if (key != null) {
            keyTextField.setText(key);
            keyTextField.setEditable(false);

            if (oldValue != null) {
                valueTextField.setText(oldValue);
                valueTextField.selectAll();
            }

            valueTextField.requestFocusInWindow();
        }
    }

    private void handleOkActionPerformed() {
        String key = keyTextField.getText().trim();

        if (!GitConfig.isKeyValid(key)) {
            JOptionPane.showMessageDialog(null, "Config key is invalid.");
            keyTextField.requestFocusInWindow();
            return;
        }

        String value = valueTextField.getText().trim();
        gitConfig.set(key, value);
        gitConfig.save();
        this.key = key;
        this.newValue = value;
        fireWindowClosing();
    }

    public String getKey() {
        return key;
    }

    public String getNewValue() {
        return newValue;
    }
}
