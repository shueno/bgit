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
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import bgit.model.GitCommit;
import bgit.model.GitConfig;
import bgit.model.Project;
import bgit.model.StatusResult;
import bgit.model.WorkNodeStatus;

@SuppressWarnings("serial")
public class CommitEditorDialog extends AbstractDialog {

    private final Project project;

    private final StatusResult statusResult;

    private boolean succeeded;

    private final JTextField authorTextField;

    private final JTextArea messageTextArea;

    private final JTextField statusTextField;

    private final JTable statusTable;

    private final StatusTableModel statusTableModel;

    public CommitEditorDialog(Project project, StatusResult statusResult) {
        this.project = project;
        this.statusResult = statusResult;

        setTitle("Commit Editor");
        setSize(new Dimension(700, 500));
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

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.5);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        JPanel commitPanel = new JPanel();
        splitPane.setLeftComponent(commitPanel);
        GridBagLayout gbl_commitPanel = new GridBagLayout();
        gbl_commitPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_commitPanel.rowHeights = new int[] { 0, 0, 0 };
        gbl_commitPanel.columnWeights = new double[] { 0.0, 1.0,
                Double.MIN_VALUE };
        gbl_commitPanel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        commitPanel.setLayout(gbl_commitPanel);

        JLabel lblAuthor = new JLabel("Author");
        GridBagConstraints gbc_lblAuthor = new GridBagConstraints();
        gbc_lblAuthor.anchor = GridBagConstraints.EAST;
        gbc_lblAuthor.insets = new Insets(0, 0, 5, 5);
        gbc_lblAuthor.gridx = 0;
        gbc_lblAuthor.gridy = 0;
        commitPanel.add(lblAuthor, gbc_lblAuthor);

        authorTextField = new JTextField();
        authorTextField.setEditable(false);
        GridBagConstraints gbc_authorTextField = new GridBagConstraints();
        gbc_authorTextField.insets = new Insets(0, 0, 5, 0);
        gbc_authorTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_authorTextField.gridx = 1;
        gbc_authorTextField.gridy = 0;
        commitPanel.add(authorTextField, gbc_authorTextField);
        authorTextField.setColumns(10);

        JLabel messageLabel = new JLabel("Message");
        GridBagConstraints gbc_messageLabel = new GridBagConstraints();
        gbc_messageLabel.insets = new Insets(0, 0, 0, 5);
        gbc_messageLabel.gridx = 0;
        gbc_messageLabel.gridy = 1;
        commitPanel.add(messageLabel, gbc_messageLabel);

        JScrollPane messageTextAreaScrollPane = new JScrollPane();
        GridBagConstraints gbc_messageTextAreaScrollPane = new GridBagConstraints();
        gbc_messageTextAreaScrollPane.fill = GridBagConstraints.BOTH;
        gbc_messageTextAreaScrollPane.gridx = 1;
        gbc_messageTextAreaScrollPane.gridy = 1;
        commitPanel.add(messageTextAreaScrollPane,
                gbc_messageTextAreaScrollPane);

        messageTextArea = new JTextArea();
        messageTextAreaScrollPane.setViewportView(messageTextArea);

        JPanel statusPanel = new JPanel();
        splitPane.setRightComponent(statusPanel);
        statusPanel.setLayout(new BorderLayout(0, 0));

        JPanel statusFooterPanel = new JPanel();
        statusPanel.add(statusFooterPanel, BorderLayout.SOUTH);
        GridBagLayout gbl_statusFooterPanel = new GridBagLayout();
        gbl_statusFooterPanel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gbl_statusFooterPanel.rowHeights = new int[] { 0, 0 };
        gbl_statusFooterPanel.columnWeights = new double[] { 0.0, 0.0, 1.0,
                0.0, Double.MIN_VALUE };
        gbl_statusFooterPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        statusFooterPanel.setLayout(gbl_statusFooterPanel);

        JButton checkButton = new JButton("Check");
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCheckActionPerformed();
            }
        });
        GridBagConstraints gbc_checkButton = new GridBagConstraints();
        gbc_checkButton.insets = new Insets(0, 0, 0, 5);
        gbc_checkButton.gridx = 0;
        gbc_checkButton.gridy = 0;
        statusFooterPanel.add(checkButton, gbc_checkButton);

        JButton uncheckButton = new JButton("Uncheck");
        uncheckButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUncheckActionPerformed();
            }
        });
        GridBagConstraints gbc_uncheckButton = new GridBagConstraints();
        gbc_uncheckButton.insets = new Insets(0, 0, 0, 5);
        gbc_uncheckButton.gridx = 1;
        gbc_uncheckButton.gridy = 0;
        statusFooterPanel.add(uncheckButton, gbc_uncheckButton);

        statusTextField = new JTextField();
        statusTextField.setEditable(false);
        GridBagConstraints gbc_statusTextField = new GridBagConstraints();
        gbc_statusTextField.insets = new Insets(0, 0, 0, 5);
        gbc_statusTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_statusTextField.gridx = 2;
        gbc_statusTextField.gridy = 0;
        statusFooterPanel.add(statusTextField, gbc_statusTextField);
        statusTextField.setColumns(10);

        Action diffAction = new DiffAction();
        JButton diffButton = new JButton(diffAction);
        GridBagConstraints gbc_diffButton = new GridBagConstraints();
        gbc_diffButton.gridx = 3;
        gbc_diffButton.gridy = 0;
        statusFooterPanel.add(diffButton, gbc_diffButton);

        JScrollPane statusTableScrollPane = new JScrollPane();
        statusPanel.add(statusTableScrollPane, BorderLayout.CENTER);

        statusTableModel = new StatusTableModel();
        statusTable = new JTable(statusTableModel);
        statusTable.setShowGrid(false);
        statusTable.setFillsViewportHeight(true);
        statusTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        statusTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {

                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        handleStatusTableValueChanged();
                    }
                });
        statusTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() != 2) {
                    super.mouseClicked(e);
                    return;
                }

                handleDiffActionPerformed();
            }
        });
        InputMap statusTableInputMap = statusTable
                .getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        statusTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "diff");
        ActionMap workNodeTableActionMap = statusTable.getActionMap();
        workNodeTableActionMap.put("diff", diffAction);
        statusTableScrollPane.setViewportView(statusTable);
    }

    private void handleWindowOpened() {
        GitConfig gitConfig = project.findGitConfig();
        StringBuilder sb = new StringBuilder();
        String userName = gitConfig.getUserName();

        if (!(userName == null || userName.isEmpty())) {
            sb.append(userName);
        }

        String userEmail = gitConfig.getUserEmail();

        if (!(userEmail == null || userEmail.isEmpty())) {
            sb.append(" <");
            sb.append(userEmail);
            sb.append(">");
        }

        authorTextField.setText(sb.toString());
        String projectPathString = statusResult.getProjectPath().toString();

        for (Map.Entry<String, EnumSet<WorkNodeStatus>> entry : statusResult
                .getStatusSetMap().entrySet()) {
            String relativePathString = Project.convertToRelativePathString(
                    projectPathString, entry.getKey());
            statusTableModel.addRow(new Object[] { true, relativePathString,
                    entry.getValue() });
        }

        messageTextArea.requestFocusInWindow();
    }

    private void handleStatusTableValueChanged() {
        String relativePathString = getCurrentRelativePathString();

        if (relativePathString == null) {
            statusTextField.setText("");
            return;
        }

        statusTextField.setText(new File(project.getAbsolutePath(),
                relativePathString).toString());
    }

    private void handleCheckActionPerformed() {
        statusTableModel.setCheckedAll(true);
    }

    private void handleUncheckActionPerformed() {
        statusTableModel.setCheckedAll(false);
    }

    private void handleDiffActionPerformed() {
        String relativePathString = getCurrentRelativePathString();

        if (relativePathString == null) {
            return;
        }

        GitCommit newestGitCommit = project
                .findNewestGitCommit(relativePathString);
        DiffDialog diffDialog = new DiffDialog(project, relativePathString,
                newestGitCommit, null);
        diffDialog.setVisible(true);
    }

    private String getCurrentRelativePathString() {
        int viewRowIndex = statusTable.getSelectedRow();

        if (viewRowIndex < 0) {
            return null;
        }

        int modelRowIndex = statusTable.convertRowIndexToModel(viewRowIndex);
        return (String) statusTableModel.getValueAt(modelRowIndex, 1);
    }

    private void handleOkActionPerformed() {
        String message = messageTextArea.getText().trim();

        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Message is invalid.");
            messageTextArea.requestFocusInWindow();
            return;
        }

        List<String> relativePathStrings = new ArrayList<String>();

        for (int i = 0; i < statusTableModel.getRowCount(); i++) {

            if (!((Boolean) statusTableModel.getValueAt(i, 0))) {
                continue;
            }

            relativePathStrings.add((String) statusTable.getValueAt(i, 1));
        }

        if (relativePathStrings.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Checked is invalid.");
            statusTable.requestFocusInWindow();
            return;
        }

        project.commit(message, relativePathStrings);
        succeeded = true;
        fireWindowClosing();
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    private class StatusTableModel extends DefaultTableModel {

        private final Class<?>[] columnTypes = new Class[] { Boolean.class,
                Object.class, Object.class };

        private StatusTableModel() {
            super(new String[] { "Checked", "Path", "Status" }, 0);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnTypes[columnIndex];
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 0;
        }

        public void setCheckedAll(boolean checked) {

            for (int i = 0; i < getRowCount(); i++) {
                setValueAt(checked, i, 0);
            }
        }
    }

    private class DiffAction extends AbstractAction {

        public DiffAction() {
            putValue(NAME, "Diff");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            handleDiffActionPerformed();
        }
    }
}
