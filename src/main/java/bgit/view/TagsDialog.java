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
import javax.swing.JOptionPane;
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

import bgit.model.GitPushResult;
import bgit.model.GitTag;
import bgit.model.Project;

@SuppressWarnings("serial")
public class TagsDialog extends AbstractDialog {

    private final Project project;

    private final JTable tagTable;

    private final TagTableModel tagTableModel;

    private final JTextField tagTextField;

    private final Action viewAction = new ViewAction();

    public TagsDialog(Project project) {
        this.project = project;

        setTitle("Tags");
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

        JPanel tagPanel = new JPanel();
        getContentPane().add(tagPanel, BorderLayout.CENTER);
        tagPanel.setLayout(new BorderLayout(0, 0));

        JScrollPane tagTableScrollPane = new JScrollPane();
        tagPanel.add(tagTableScrollPane, BorderLayout.CENTER);

        tagTableModel = new TagTableModel();
        tagTable = new JTable(tagTableModel);
        tagTable.setDefaultRenderer(GitTag.class, new TagTableCellRenderer());
        tagTable.setShowGrid(false);
        tagTable.setFillsViewportHeight(true);
        tagTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tagTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {

                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        handleTagTableValueChanged();
                    }
                });
        tagTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() != 2) {
                    super.mouseClicked(e);
                    return;
                }

                handleViewActionPerformed();
            }
        });
        InputMap statusTableInputMap = tagTable
                .getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        statusTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "view");
        ActionMap workNodeTableActionMap = tagTable.getActionMap();
        workNodeTableActionMap.put("view", viewAction);
        tagTableScrollPane.setViewportView(tagTable);

        JPanel tagFooterPanel = new JPanel();
        tagPanel.add(tagFooterPanel, BorderLayout.SOUTH);
        GridBagLayout gbl_tagFooterPanel = new GridBagLayout();
        gbl_tagFooterPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
        gbl_tagFooterPanel.rowHeights = new int[] { 0, 0 };
        gbl_tagFooterPanel.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0,
                0.0, Double.MIN_VALUE };
        gbl_tagFooterPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        tagFooterPanel.setLayout(gbl_tagFooterPanel);

        JButton newTagButton = new JButton("New tag");
        newTagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleNewTagActionPerformed();
            }
        });
        GridBagConstraints gbc_newTagButton = new GridBagConstraints();
        gbc_newTagButton.insets = new Insets(0, 0, 0, 5);
        gbc_newTagButton.gridx = 0;
        gbc_newTagButton.gridy = 0;
        tagFooterPanel.add(newTagButton, gbc_newTagButton);

        tagTextField = new JTextField();
        tagTextField.setEditable(false);
        GridBagConstraints gbc_tagTextField = new GridBagConstraints();
        gbc_tagTextField.insets = new Insets(0, 0, 0, 5);
        gbc_tagTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_tagTextField.gridx = 1;
        gbc_tagTextField.gridy = 0;
        tagFooterPanel.add(tagTextField, gbc_tagTextField);
        tagTextField.setColumns(10);

        JButton viewButton = new JButton(viewAction);
        GridBagConstraints gbc_viewButton = new GridBagConstraints();
        gbc_viewButton.insets = new Insets(0, 0, 0, 5);
        gbc_viewButton.gridx = 2;
        gbc_viewButton.gridy = 0;
        tagFooterPanel.add(viewButton, gbc_viewButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeleteActionPerformed();
            }
        });
        GridBagConstraints gbc_deleteButton = new GridBagConstraints();
        gbc_deleteButton.insets = new Insets(0, 0, 0, 5);
        gbc_deleteButton.gridx = 3;
        gbc_deleteButton.gridy = 0;
        tagFooterPanel.add(deleteButton, gbc_deleteButton);

        JButton pushButton = new JButton("Push");
        pushButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePushActionPerformed();
            }
        });
        GridBagConstraints gbc_pushButton = new GridBagConstraints();
        gbc_pushButton.gridx = 4;
        gbc_pushButton.gridy = 0;
        tagFooterPanel.add(pushButton, gbc_pushButton);
    }

    private void handleWindowOpened() {

        for (GitTag gitTag : project.findGitTags()) {
            tagTableModel.addGitTag(gitTag);
        }
    }

    private void handleTagTableValueChanged() {
        updateTagTextField();
    }

    private void updateTagTextField() {
        int viewRowIndex = tagTable.getSelectedRow();

        if (viewRowIndex < 0) {
            tagTextField.setText("");
            return;
        }

        int modelRowIndex = tagTable.convertRowIndexToModel(viewRowIndex);
        GitTag gitTag = tagTableModel.findGitTag(modelRowIndex);
        tagTextField.setText(gitTag.getOneline());
    }

    private void handleNewTagActionPerformed() {
        TagEditorDialog tagEditorDialog = new TagEditorDialog(project);
        tagEditorDialog.setVisible(true);
        GitTag gitTag = tagEditorDialog.getGitTag();

        if (gitTag == null) {
            return;
        }

        tagTableModel.addGitTag(gitTag);
    }

    private void handleViewActionPerformed() {
        int viewRowIndex = tagTable.getSelectedRow();

        if (viewRowIndex < 0) {
            return;
        }

        int modelRowIndex = tagTable.convertRowIndexToModel(viewRowIndex);
        GitTag gitTag = tagTableModel.findGitTag(modelRowIndex);
        TagViewerDialog tagViewerDialog = new TagViewerDialog(gitTag);
        tagViewerDialog.setVisible(true);
    }

    private void handleDeleteActionPerformed() {
        int viewRowIndex = tagTable.getSelectedRow();

        if (viewRowIndex < 0) {
            return;
        }

        int option = JOptionPane.showConfirmDialog(null, "Delete?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        int modelRowIndex = tagTable.convertRowIndexToModel(viewRowIndex);
        GitTag gitTag = tagTableModel.findGitTag(modelRowIndex);
        gitTag.delete();
        tagTableModel.removeRow(modelRowIndex);
    }

    private void handlePushActionPerformed() {
        int viewRowIndex = tagTable.getSelectedRow();

        if (viewRowIndex < 0) {
            return;
        }

        int option = JOptionPane.showConfirmDialog(null, "Push?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        int modelRowIndex = tagTable.convertRowIndexToModel(viewRowIndex);
        GitTag gitTag = tagTableModel.findGitTag(modelRowIndex);
        List<GitPushResult> gitPushResults = gitTag.push();

        for (GitPushResult gitPushResult : gitPushResults) {
            JOptionPane.showMessageDialog(null, gitPushResult.getMessage());
        }
    }

    private class TagTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        protected void setValue(Object value) {
            GitTag gitTag = (GitTag) value;
            super.setValue(gitTag.getName());
        }
    }

    private class TagTableModel extends DefaultTableModel {

        private final Class<?>[] columnTypes = new Class[] { GitTag.class,
                String.class, Object.class };

        private TagTableModel() {
            super(new String[] { "Name", "Tagger", "Date" }, 0);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnTypes[columnIndex];
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        public void addGitTag(GitTag gitTag) {
            addRow(new Object[] { gitTag, gitTag.getTagger(),
                    gitTag.getDateTagged() });
        }

        public GitTag findGitTag(int modelRowIndex) {
            return (GitTag) getValueAt(modelRowIndex, 0);
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
