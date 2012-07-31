package bgit.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import bgit.model.GitConfig;

@SuppressWarnings("serial")
public class ConfigDialog extends AbstractDialog {

    private final GitConfig gitConfig;

    private final ConfigTableModel configTableModel;

    private final JTable configTable;

    private final JTextField configTextField;

    private final Action editAction = new EditAction();

    public ConfigDialog(GitConfig gitConfig) {
        this.gitConfig = gitConfig;

        setTitle("Config");
        setSize(new Dimension(600, 400));
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                handleWindowOpened();
            }
        });

        JPanel footerPanel = new JPanel();
        getContentPane().add(footerPanel, BorderLayout.SOUTH);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireWindowClosing();
            }
        });
        footerPanel.add(closeButton);

        JPanel configPanel = new JPanel();
        getContentPane().add(configPanel, BorderLayout.CENTER);
        configPanel.setLayout(new BorderLayout(0, 0));

        JPanel configFooterPanel = new JPanel();
        configPanel.add(configFooterPanel, BorderLayout.SOUTH);
        GridBagLayout gbl_configFooterPanel = new GridBagLayout();
        gbl_configFooterPanel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gbl_configFooterPanel.rowHeights = new int[] { 0, 0 };
        gbl_configFooterPanel.columnWeights = new double[] { 0.0, 1.0, 0.0,
                0.0, Double.MIN_VALUE };
        gbl_configFooterPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        configFooterPanel.setLayout(gbl_configFooterPanel);

        JButton newButton = new JButton("New");
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleNewActionPerformed();
            }
        });
        GridBagConstraints gbc_newButton = new GridBagConstraints();
        gbc_newButton.insets = new Insets(0, 0, 0, 5);
        gbc_newButton.gridx = 0;
        gbc_newButton.gridy = 0;
        configFooterPanel.add(newButton, gbc_newButton);

        configTextField = new JTextField();
        configTextField.setEditable(false);
        GridBagConstraints gbc_configTextField = new GridBagConstraints();
        gbc_configTextField.insets = new Insets(0, 0, 0, 5);
        gbc_configTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_configTextField.gridx = 1;
        gbc_configTextField.gridy = 0;
        configFooterPanel.add(configTextField, gbc_configTextField);
        configTextField.setColumns(10);

        JButton editButton = new JButton(editAction);
        GridBagConstraints gbc_editButton = new GridBagConstraints();
        gbc_editButton.insets = new Insets(0, 0, 0, 5);
        gbc_editButton.gridx = 2;
        gbc_editButton.gridy = 0;
        configFooterPanel.add(editButton, gbc_editButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeleteActionPerformed();
            }
        });
        GridBagConstraints gbc_deleteButton = new GridBagConstraints();
        gbc_deleteButton.gridx = 3;
        gbc_deleteButton.gridy = 0;
        configFooterPanel.add(deleteButton, gbc_deleteButton);

        JScrollPane configTableScrollPane = new JScrollPane();
        configPanel.add(configTableScrollPane, BorderLayout.CENTER);

        configTableModel = new ConfigTableModel();
        configTable = new JTable(configTableModel);
        configTable.setShowGrid(false);
        configTable.setFillsViewportHeight(true);
        configTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        configTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {

                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        handleConfigTableValueChanged();
                    }
                });
        configTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() != 2) {
                    super.mouseClicked(e);
                    return;
                }

                handleEditActionPerformed();
            }
        });
        InputMap configTableInputMap = configTable
                .getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        configTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "edit");
        ActionMap workNodeTableActionMap = configTable.getActionMap();
        workNodeTableActionMap.put("edit", editAction);
        configTableScrollPane.setViewportView(configTable);
    }

    private void handleWindowOpened() {

        for (Map.Entry<String, String> entry : gitConfig.getValueMap()
                .entrySet()) {
            configTableModel.addRow(new Object[] { entry.getKey(),
                    entry.getValue() });
        }
    }

    private void handleConfigTableValueChanged() {
        updateConfigTextField();
    }

    private void updateConfigTextField() {
        int viewRowIndex = configTable.getSelectedRow();

        if (viewRowIndex < 0) {
            configTextField.setText("");
            return;
        }

        int modelRowIndex = configTable.convertRowIndexToModel(viewRowIndex);
        String key = (String) configTableModel.getValueAt(modelRowIndex, 0);
        String value = (String) configTableModel.getValueAt(modelRowIndex, 1);
        configTextField.setText(String.format("%s '%s'", key, value));
    }

    private void handleNewActionPerformed() {
        ConfigEntryDialog configEntryDialog = new ConfigEntryDialog(gitConfig,
                null, null);
        configEntryDialog.setVisible(true);
        String newValue = configEntryDialog.getNewValue();

        if (newValue == null) {
            return;
        }

        configTableModel.addRow(new Object[] { configEntryDialog.getKey(),
                newValue });
    }

    private void handleEditActionPerformed() {
        int viewRowIndex = configTable.getSelectedRow();

        if (viewRowIndex < 0) {
            return;
        }

        int modelRowIndex = configTable.convertRowIndexToModel(viewRowIndex);
        String key = (String) configTableModel.getValueAt(modelRowIndex, 0);
        String oldValue = (String) configTableModel
                .getValueAt(modelRowIndex, 1);

        ConfigEntryDialog configEntryDialog = new ConfigEntryDialog(gitConfig,
                key, oldValue);
        configEntryDialog.setVisible(true);
        String newValue = configEntryDialog.getNewValue();

        if (newValue == null) {
            return;
        }

        configTableModel.setValueAt(newValue, modelRowIndex, 1);
        updateConfigTextField();
    }

    private void handleDeleteActionPerformed() {
        int viewRowIndex = configTable.getSelectedRow();

        if (viewRowIndex < 0) {
            return;
        }

        int option = JOptionPane.showConfirmDialog(null, "Delete?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        int modelRowIndex = configTable.convertRowIndexToModel(viewRowIndex);
        String key = (String) configTableModel.getValueAt(modelRowIndex, 0);
        gitConfig.delete(key);
        configTableModel.removeRow(modelRowIndex);
    }

    private class ConfigTableModel extends DefaultTableModel {

        private final Class<?>[] columnTypes = new Class[] { String.class,
                String.class };

        private ConfigTableModel() {
            super(new String[] { "Key", "Value" }, 0);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnTypes[columnIndex];
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    private class EditAction extends AbstractAction {

        public EditAction() {
            putValue(NAME, "Edit");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            handleEditActionPerformed();
        }
    }
}
