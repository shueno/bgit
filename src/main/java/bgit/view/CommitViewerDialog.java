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
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import bgit.model.Application;
import bgit.model.GitCommit;
import bgit.model.GitDiffEntry;

@SuppressWarnings("serial")
public class CommitViewerDialog extends AbstractDialog {

    private final GitCommit gitCommit;

    private List<GitCommit> parentGitCommits;

    private final JTextField idTextField;

    private final JTextField authorTextField;

    private final JTextField dateTextField;

    private final JTextArea messagetextArea;

    private final DiffTableModel diffTableModel;

    private final JTable diffTable;

    private final JLabel parentCountLabel;

    private final JComboBox parentComboBox;

    private final JTextField diffTextField;

    private final Action diffAction = new DiffAction();

    public CommitViewerDialog(Application application, GitCommit gitCommit) {
        super(application);
        this.gitCommit = gitCommit;

        setTitle("Commit Viewer");
        setSize(new Dimension(700, 500));
        bindWindowSettings();

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

        JButton treeButton = new JButton("Tree");
        treeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleTreeActionPerformed();
            }
        });
        footerPanel.add(treeButton);
        footerPanel.add(closeButton);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        splitPane.setLeftComponent(panel);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 0, 0, 0 };
        gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
        gbl_panel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0,
                Double.MIN_VALUE };
        panel.setLayout(gbl_panel);

        JLabel lblId = new JLabel("Commit ID");
        GridBagConstraints gbc_lblId = new GridBagConstraints();
        gbc_lblId.anchor = GridBagConstraints.EAST;
        gbc_lblId.insets = new Insets(0, 0, 5, 5);
        gbc_lblId.gridx = 0;
        gbc_lblId.gridy = 0;
        panel.add(lblId, gbc_lblId);

        idTextField = new JTextField();
        idTextField.setEditable(false);
        GridBagConstraints gbc_idTextField = new GridBagConstraints();
        gbc_idTextField.insets = new Insets(0, 0, 5, 0);
        gbc_idTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_idTextField.gridx = 1;
        gbc_idTextField.gridy = 0;
        panel.add(idTextField, gbc_idTextField);
        idTextField.setColumns(10);

        JLabel lblAuthor = new JLabel("Author");
        GridBagConstraints gbc_lblAuthor = new GridBagConstraints();
        gbc_lblAuthor.anchor = GridBagConstraints.EAST;
        gbc_lblAuthor.insets = new Insets(0, 0, 5, 5);
        gbc_lblAuthor.gridx = 0;
        gbc_lblAuthor.gridy = 1;
        panel.add(lblAuthor, gbc_lblAuthor);

        authorTextField = new JTextField();
        authorTextField.setEditable(false);
        GridBagConstraints gbc_authorTextField = new GridBagConstraints();
        gbc_authorTextField.insets = new Insets(0, 0, 5, 0);
        gbc_authorTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_authorTextField.gridx = 1;
        gbc_authorTextField.gridy = 1;
        panel.add(authorTextField, gbc_authorTextField);
        authorTextField.setColumns(10);

        JLabel lblDate = new JLabel("Date");
        GridBagConstraints gbc_lblDate = new GridBagConstraints();
        gbc_lblDate.anchor = GridBagConstraints.EAST;
        gbc_lblDate.insets = new Insets(0, 0, 5, 5);
        gbc_lblDate.gridx = 0;
        gbc_lblDate.gridy = 2;
        panel.add(lblDate, gbc_lblDate);

        dateTextField = new JTextField();
        dateTextField.setEditable(false);
        GridBagConstraints gbc_dateTextField = new GridBagConstraints();
        gbc_dateTextField.insets = new Insets(0, 0, 5, 0);
        gbc_dateTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_dateTextField.gridx = 1;
        gbc_dateTextField.gridy = 2;
        panel.add(dateTextField, gbc_dateTextField);
        dateTextField.setColumns(10);

        JLabel lblMessage = new JLabel("Message");
        GridBagConstraints gbc_lblMessage = new GridBagConstraints();
        gbc_lblMessage.insets = new Insets(0, 0, 0, 5);
        gbc_lblMessage.gridx = 0;
        gbc_lblMessage.gridy = 3;
        panel.add(lblMessage, gbc_lblMessage);

        JScrollPane messageTextAreaScrollPane = new JScrollPane();
        GridBagConstraints gbc_messageTextAreaScrollPane = new GridBagConstraints();
        gbc_messageTextAreaScrollPane.fill = GridBagConstraints.BOTH;
        gbc_messageTextAreaScrollPane.gridx = 1;
        gbc_messageTextAreaScrollPane.gridy = 3;
        panel.add(messageTextAreaScrollPane, gbc_messageTextAreaScrollPane);

        messagetextArea = new JTextArea();
        messagetextArea.setEditable(false);
        messageTextAreaScrollPane.setViewportView(messagetextArea);

        JPanel diffPanel = new JPanel();
        splitPane.setRightComponent(diffPanel);
        diffPanel.setLayout(new BorderLayout(0, 0));

        JPanel diffHeaderPanel = new JPanel();
        diffPanel.add(diffHeaderPanel, BorderLayout.NORTH);
        GridBagLayout gbl_diffHeaderPanel = new GridBagLayout();
        gbl_diffHeaderPanel.columnWidths = new int[] { 0, 0, 0, 0 };
        gbl_diffHeaderPanel.rowHeights = new int[] { 0, 0 };
        gbl_diffHeaderPanel.columnWeights = new double[] { 0.0, 0.0, 1.0,
                Double.MIN_VALUE };
        gbl_diffHeaderPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        diffHeaderPanel.setLayout(gbl_diffHeaderPanel);

        JLabel lblNewLabel = new JLabel("Parent");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 0;
        diffHeaderPanel.add(lblNewLabel, gbc_lblNewLabel);

        parentCountLabel = new JLabel("(0)");
        GridBagConstraints gbc_parentCountLabel = new GridBagConstraints();
        gbc_parentCountLabel.anchor = GridBagConstraints.EAST;
        gbc_parentCountLabel.insets = new Insets(0, 0, 0, 5);
        gbc_parentCountLabel.gridx = 1;
        gbc_parentCountLabel.gridy = 0;
        diffHeaderPanel.add(parentCountLabel, gbc_parentCountLabel);

        parentComboBox = new JComboBox();
        parentComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleParentComboBoxActionPerformed();
            }
        });
        GridBagConstraints gbc_parentComboBox = new GridBagConstraints();
        gbc_parentComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_parentComboBox.gridx = 2;
        gbc_parentComboBox.gridy = 0;
        diffHeaderPanel.add(parentComboBox, gbc_parentComboBox);

        JScrollPane diffTableScrollPane = new JScrollPane();
        diffPanel.add(diffTableScrollPane, BorderLayout.CENTER);

        diffTableModel = new DiffTableModel();
        diffTable = new JTable(diffTableModel);
        diffTable.setShowGrid(false);
        diffTable.setFillsViewportHeight(true);
        diffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        diffTable.setDefaultRenderer(GitDiffEntry.class,
                new DiffTableCellRenderer());
        diffTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {

                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        handleDiffTableValueChanged();
                    }
                });
        diffTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() != 2) {
                    super.mouseClicked(e);
                    return;
                }

                handleDiffActionPerformed();
            }
        });
        InputMap diffTableInputMap = diffTable
                .getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        diffTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "diff");
        ActionMap workNodeTableActionMap = diffTable.getActionMap();
        workNodeTableActionMap.put("diff", diffAction);
        diffTableScrollPane.setViewportView(diffTable);

        JPanel diffFooterPanel = new JPanel();
        diffPanel.add(diffFooterPanel, BorderLayout.SOUTH);
        GridBagLayout gbl_diffFooterPanel = new GridBagLayout();
        gbl_diffFooterPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_diffFooterPanel.rowHeights = new int[] { 0, 0 };
        gbl_diffFooterPanel.columnWeights = new double[] { 1.0, 0.0,
                Double.MIN_VALUE };
        gbl_diffFooterPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        diffFooterPanel.setLayout(gbl_diffFooterPanel);

        diffTextField = new JTextField();
        diffTextField.setEditable(false);
        GridBagConstraints gbc_diffTextField = new GridBagConstraints();
        gbc_diffTextField.insets = new Insets(0, 0, 0, 5);
        gbc_diffTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_diffTextField.gridx = 0;
        gbc_diffTextField.gridy = 0;
        diffFooterPanel.add(diffTextField, gbc_diffTextField);
        diffTextField.setColumns(10);

        JButton diffButton = new JButton(diffAction);
        GridBagConstraints gbc_diffButton = new GridBagConstraints();
        gbc_diffButton.gridx = 1;
        gbc_diffButton.gridy = 0;
        diffFooterPanel.add(diffButton, gbc_diffButton);
    }

    private void handleWindowOpened() {
        idTextField.setText(gitCommit.getFullIdString());
        authorTextField.setText(gitCommit.getAuthor());
        dateTextField.setText(gitCommit.getDateAuthored().toString());
        messagetextArea.setText(gitCommit.getFullMessage());

        parentGitCommits = gitCommit.findParentGitCommits();
        parentCountLabel
                .setText(String.format("(%d)", parentGitCommits.size()));

        for (GitCommit parentGitCommit : parentGitCommits) {
            parentComboBox.addItem(parentGitCommit.getOneline());
        }

        updateDiffTable();
    }

    private void handleTreeActionPerformed() {
        RevisionTreeDialog revisionTreeDialog = new RevisionTreeDialog(
                application, gitCommit);
        revisionTreeDialog.setVisible(true);
    }

    private void handleParentComboBoxActionPerformed() {
        updateDiffTable();
    }

    private void updateDiffTable() {
        diffTableModel.setRowCount(0);
        GitCommit parentGitCommit = getCurrentParentGitCommit();

        for (GitDiffEntry gitDiffEntry : gitCommit.diff(parentGitCommit)) {
            diffTableModel.addGitDiffEntry(gitDiffEntry);
        }
    }

    private void handleDiffTableValueChanged() {
        GitDiffEntry gitDiffEntry = getCurrentGitDiffEntry();

        if (gitDiffEntry == null) {
            diffTextField.setText("");
            return;
        }

        diffTextField.setText(gitDiffEntry.getDisplayPathString());
    }

    private void handleDiffActionPerformed() {
        GitDiffEntry gitDiffEntry = getCurrentGitDiffEntry();

        if (gitDiffEntry == null) {
            return;
        }

        GitCommit parentGitCommit = getCurrentParentGitCommit();
        DiffDialog diffDialog = new DiffDialog(application,
                gitCommit.getProject(), gitDiffEntry.getDisplayPathString(),
                parentGitCommit, gitCommit);
        diffDialog.setVisible(true);
    }

    private GitCommit getCurrentParentGitCommit() {
        int index = parentComboBox.getSelectedIndex();

        if (index < 0) {
            return null;
        }

        return parentGitCommits.get(index);
    }

    private GitDiffEntry getCurrentGitDiffEntry() {
        int viewRowIndex = diffTable.getSelectedRow();

        if (viewRowIndex < 0) {
            return null;
        }

        int modelRowIndex = diffTable.convertRowIndexToModel(viewRowIndex);
        return (GitDiffEntry) diffTableModel.getValueAt(modelRowIndex, 0);
    }

    private class DiffTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        protected void setValue(Object value) {
            GitDiffEntry gitDiffEntry = (GitDiffEntry) value;
            super.setValue(gitDiffEntry.getDisplayPathString());
        }
    }

    private class DiffTableModel extends DefaultTableModel {

        private final Class<?>[] columnTypes = new Class[] {
                GitDiffEntry.class, Object.class };

        private DiffTableModel() {
            super(new String[] { "Path", "ChangeType" }, 0);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnTypes[columnIndex];
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        public void addGitDiffEntry(GitDiffEntry gitDiffEntry) {
            addRow(new Object[] { gitDiffEntry, gitDiffEntry.getChangeType() });
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
