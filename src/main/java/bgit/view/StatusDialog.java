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
import java.util.EnumSet;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import bgit.model.GitBranchTrackingStatus;
import bgit.model.GitCommit;
import bgit.model.Project;
import bgit.model.StatusResult;
import bgit.model.WorkNodeStatus;

@SuppressWarnings("serial")
public class StatusDialog extends AbstractDialog {

    private final Project project;

    private File absolutePath;

    private final JTable statusTable;

    private final StatusTableModel statusTableModel;

    private JTextField currentBranchTextField;

    private JTextField remoteTranckingBranchTextField;

    private JTextField statusTextField;

    public StatusDialog(Project project) {
        setTitle("Status");
        this.project = project;

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

        JPanel statusPanel = new JPanel();
        getContentPane().add(statusPanel, BorderLayout.CENTER);
        statusPanel.setLayout(new BorderLayout(0, 0));

        JPanel statusHeaderPanel = new JPanel();
        statusPanel.add(statusHeaderPanel, BorderLayout.NORTH);
        GridBagLayout gbl_statusHeaderPanel = new GridBagLayout();
        gbl_statusHeaderPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_statusHeaderPanel.rowHeights = new int[] { 0, 0, 0 };
        gbl_statusHeaderPanel.columnWeights = new double[] { 0.0, 1.0,
                Double.MIN_VALUE };
        gbl_statusHeaderPanel.rowWeights = new double[] { 0.0, 0.0,
                Double.MIN_VALUE };
        statusHeaderPanel.setLayout(gbl_statusHeaderPanel);

        JLabel lblCurrentBranch = new JLabel("Current branch");
        GridBagConstraints gbc_lblCurrentBranch = new GridBagConstraints();
        gbc_lblCurrentBranch.anchor = GridBagConstraints.EAST;
        gbc_lblCurrentBranch.insets = new Insets(0, 0, 5, 5);
        gbc_lblCurrentBranch.gridx = 0;
        gbc_lblCurrentBranch.gridy = 0;
        statusHeaderPanel.add(lblCurrentBranch, gbc_lblCurrentBranch);

        currentBranchTextField = new JTextField();
        currentBranchTextField.setEditable(false);
        GridBagConstraints gbc_currentBranchTextField = new GridBagConstraints();
        gbc_currentBranchTextField.insets = new Insets(0, 0, 5, 0);
        gbc_currentBranchTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_currentBranchTextField.gridx = 1;
        gbc_currentBranchTextField.gridy = 0;
        statusHeaderPanel.add(currentBranchTextField,
                gbc_currentBranchTextField);
        currentBranchTextField.setColumns(10);

        JLabel lblRemoteTrackingBranch = new JLabel("Remote tracking branch");
        GridBagConstraints gbc_lblRemoteTrackingBranch = new GridBagConstraints();
        gbc_lblRemoteTrackingBranch.anchor = GridBagConstraints.EAST;
        gbc_lblRemoteTrackingBranch.insets = new Insets(0, 0, 0, 5);
        gbc_lblRemoteTrackingBranch.gridx = 0;
        gbc_lblRemoteTrackingBranch.gridy = 1;
        statusHeaderPanel.add(lblRemoteTrackingBranch,
                gbc_lblRemoteTrackingBranch);

        remoteTranckingBranchTextField = new JTextField();
        remoteTranckingBranchTextField.setEditable(false);
        GridBagConstraints gbc_remoteTranckingBranchTextField = new GridBagConstraints();
        gbc_remoteTranckingBranchTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_remoteTranckingBranchTextField.gridx = 1;
        gbc_remoteTranckingBranchTextField.gridy = 1;
        statusHeaderPanel.add(remoteTranckingBranchTextField,
                gbc_remoteTranckingBranchTextField);
        remoteTranckingBranchTextField.setColumns(10);

        JPanel statusFooterPanel = new JPanel();
        statusPanel.add(statusFooterPanel, BorderLayout.SOUTH);
        GridBagLayout gbl_statusFooterPanel = new GridBagLayout();
        gbl_statusFooterPanel.columnWidths = new int[] { 0, 0, 0, 0 };
        gbl_statusFooterPanel.rowHeights = new int[] { 0, 0 };
        gbl_statusFooterPanel.columnWeights = new double[] { 1.0, 0.0, 0.0,
                Double.MIN_VALUE };
        gbl_statusFooterPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        statusFooterPanel.setLayout(gbl_statusFooterPanel);

        statusTextField = new JTextField();
        statusTextField.setEditable(false);
        GridBagConstraints gbc_statusTextField = new GridBagConstraints();
        gbc_statusTextField.insets = new Insets(0, 0, 0, 5);
        gbc_statusTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_statusTextField.gridx = 0;
        gbc_statusTextField.gridy = 0;
        statusFooterPanel.add(statusTextField, gbc_statusTextField);
        statusTextField.setColumns(10);

        JumpAction jumpAction = new JumpAction();
        JButton jumpButton = new JButton("Jump");
        GridBagConstraints gbc_jumpButton = new GridBagConstraints();
        gbc_jumpButton.insets = new Insets(0, 0, 0, 5);
        gbc_jumpButton.gridx = 1;
        gbc_jumpButton.gridy = 0;
        statusFooterPanel.add(jumpButton, gbc_jumpButton);
        jumpButton.setAction(jumpAction);

        JButton diffbutton = new JButton("Diff");
        diffbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDiffActionPerformed();
            }
        });
        GridBagConstraints gbc_diffbutton = new GridBagConstraints();
        gbc_diffbutton.gridx = 2;
        gbc_diffbutton.gridy = 0;
        statusFooterPanel.add(diffbutton, gbc_diffbutton);

        JScrollPane statusTableScrollPane = new JScrollPane();
        statusPanel.add(statusTableScrollPane, BorderLayout.CENTER);

        statusTableModel = new StatusTableModel();
        statusTable = new JTable(statusTableModel);
        statusTable.setShowGrid(false);
        statusTable.setFillsViewportHeight(true);
        statusTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        statusTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() != 2) {
                    super.mouseClicked(e);
                    return;
                }

                handleJumpActionPerformed();
            }
        });
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
        InputMap statusTableInputMap = statusTable
                .getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        statusTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "jump");
        ActionMap workNodeTableActionMap = statusTable.getActionMap();
        workNodeTableActionMap.put("jump", jumpAction);
        statusTableScrollPane.setViewportView(statusTable);
    }

    private void handleWindowOpened() {
        String currentBranchName = project.getCurrentBranchName();
        GitBranchTrackingStatus gitBranchTrackingStatus = project
                .findGitBranchTrackingStatus(currentBranchName);

        if (gitBranchTrackingStatus != null) {
            currentBranchName += String.format(" [ahead %d, behind %d]",
                    gitBranchTrackingStatus.getAheadCount(),
                    gitBranchTrackingStatus.getBehindCount());
            remoteTranckingBranchTextField.setText(gitBranchTrackingStatus
                    .getRemoteTrackingBranchName());
        }

        currentBranchTextField.setText(currentBranchName);
        StatusResult statusResult = project.status();
        String projectPathString = project.getAbsolutePath().toString();

        for (Map.Entry<String, EnumSet<WorkNodeStatus>> entry : statusResult
                .getStatusSetMap().entrySet()) {
            String relativePathString = Project.convertToRelativePathString(
                    projectPathString, entry.getKey());
            statusTableModel.addRow(new Object[] { relativePathString,
                    entry.getValue() });
        }

        statusTable.requestFocusInWindow();
    }

    private void handleStatusTableValueChanged() {
        String relativePathString = findRelativePathString();

        if (relativePathString == null) {
            statusTextField.setText("");
            return;
        }

        File absolutePath = new File(project.getAbsolutePath(),
                relativePathString);
        statusTextField.setText(absolutePath.toString());
    }

    private void handleJumpActionPerformed() {
        String relativePathString = findRelativePathString();

        if (relativePathString == null) {
            return;
        }

        absolutePath = new File(project.getAbsolutePath(), relativePathString);
        fireWindowClosing();
    }

    private void handleDiffActionPerformed() {
        String relativePathString = findRelativePathString();

        if (relativePathString == null) {
            return;
        }

        GitCommit newestGitCommit = project
                .findNewestGitCommit(relativePathString);
        DiffDialog diffDialog = new DiffDialog(project, relativePathString,
                newestGitCommit, null);
        diffDialog.setVisible(true);
    }

    private String findRelativePathString() {
        int viewRowIndex = statusTable.getSelectedRow();

        if (viewRowIndex < 0) {
            return null;
        }

        int modelRowIndex = statusTable.convertRowIndexToModel(viewRowIndex);
        return (String) statusTableModel.getValueAt(modelRowIndex, 0);
    }

    public File getAbsolutePath() {
        return absolutePath;
    }

    private class StatusTableModel extends DefaultTableModel {

        private StatusTableModel() {
            super(new String[] { "Path", "Status" }, 0);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    private class JumpAction extends AbstractAction {

        public JumpAction() {
            super("Jump");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            handleJumpActionPerformed();
        }
    }
}
