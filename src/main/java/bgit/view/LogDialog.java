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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import bgit.model.GitCommit;

//TODO Show ahead and behind information
@SuppressWarnings("serial")
public class LogDialog extends AbstractDialog {

    private final Iterable<GitCommit> gitCommits;

    private final JTable commitTable;

    private final CommitTableModel commitTableModel;

    private final JTextField commitTextField;

    private final Action viewAction = new ViewAction();

    public LogDialog(Iterable<GitCommit> gitCommits) {
        this.gitCommits = gitCommits;

        setTitle("Log");
        setSize(new Dimension(800, 400));
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

        JPanel commitPanel = new JPanel();
        getContentPane().add(commitPanel, BorderLayout.CENTER);
        commitPanel.setLayout(new BorderLayout(0, 0));

        JScrollPane commitTableScrollPane = new JScrollPane();
        commitPanel.add(commitTableScrollPane, BorderLayout.CENTER);

        commitTableModel = new CommitTableModel();
        commitTable = new JTable(commitTableModel);
        commitTable.setShowGrid(false);
        commitTable.setDefaultRenderer(GitCommit.class,
                new CommitTableCellRenderer());
        commitTable.setFillsViewportHeight(true);
        commitTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        commitTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {

                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        handleCommitTableValueChanged();
                    }
                });
        commitTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() != 2) {
                    super.mouseClicked(e);
                    return;
                }

                handleViewActionPerformed();
            }
        });
        InputMap statusTableInputMap = commitTable
                .getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        statusTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "view");
        ActionMap workNodeTableActionMap = commitTable.getActionMap();
        workNodeTableActionMap.put("view", viewAction);
        commitTableScrollPane.setViewportView(commitTable);

        JPanel commitFooterPanel = new JPanel();
        commitPanel.add(commitFooterPanel, BorderLayout.SOUTH);
        GridBagLayout gbl_commitFooterPanel = new GridBagLayout();
        gbl_commitFooterPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_commitFooterPanel.rowHeights = new int[] { 0, 0 };
        gbl_commitFooterPanel.columnWeights = new double[] { 1.0, 0.0,
                Double.MIN_VALUE };
        gbl_commitFooterPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        commitFooterPanel.setLayout(gbl_commitFooterPanel);

        commitTextField = new JTextField();
        commitTextField.setEditable(false);
        GridBagConstraints gbc_commitTextField = new GridBagConstraints();
        gbc_commitTextField.insets = new Insets(0, 0, 0, 5);
        gbc_commitTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_commitTextField.gridx = 0;
        gbc_commitTextField.gridy = 0;
        commitFooterPanel.add(commitTextField, gbc_commitTextField);
        commitTextField.setColumns(10);

        JButton viewButton = new JButton(viewAction);
        GridBagConstraints gbc_viewButton = new GridBagConstraints();
        gbc_viewButton.gridx = 1;
        gbc_viewButton.gridy = 0;
        commitFooterPanel.add(viewButton, gbc_viewButton);
    }

    private void handleWindowOpened() {

        for (GitCommit gitCommit : gitCommits) {
            commitTableModel.addRow(new Object[] { gitCommit,
                    gitCommit.getShortMessage(), gitCommit.getAuthor(),
                    gitCommit.getDateAuthored() });
        }
    }

    private void handleCommitTableValueChanged() {
        GitCommit gitCommit = findGitCommit();

        if (gitCommit == null) {
            commitTextField.setText("");
            return;
        }

        commitTextField.setText(gitCommit.getOneline());
    }

    private void handleViewActionPerformed() {
        GitCommit gitCommit = findGitCommit();

        if (gitCommit == null) {
            return;
        }

        CommitViewerDialog commitViewerDialog = new CommitViewerDialog(
                gitCommit);
        commitViewerDialog.setVisible(true);
    }

    private GitCommit findGitCommit() {
        int viewRowIndex = commitTable.getSelectedRow();

        if (viewRowIndex < 0) {
            return null;
        }

        int modelRowIndex = commitTable.convertRowIndexToModel(viewRowIndex);
        return (GitCommit) commitTable.getValueAt(modelRowIndex, 0);
    }

    private class CommitTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        protected void setValue(Object value) {
            GitCommit gitCommit = (GitCommit) value;
            super.setValue(gitCommit.getShortIdString());
        }
    }

    private class CommitTableModel extends DefaultTableModel {

        private final Class<?>[] columnTypes = new Class[] { GitCommit.class,
                String.class, String.class, Object.class };

        private CommitTableModel() {
            super(new String[] { "Commit ID", "Message", "Author", "Date" }, 0);
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

    private class ViewAction extends AbstractAction {

        public ViewAction() {
            putValue(NAME, "View");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            handleViewActionPerformed();
        }
    }
}
