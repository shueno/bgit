package bgit.view;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import bgit.JdkUtils;
import bgit.model.GitCommit;
import bgit.model.GitFile;
import bgit.model.GitFolder;
import bgit.model.GitNode;

@SuppressWarnings("serial")
public class RevisionTreeDialog extends AbstractDialog {

    private final GitCommit gitCommit;

    private final GitFolderTreeModel gitFolderTreeModel;

    private final JTree gitFolderTree;

    private final Icon directoryIcon = UIManager
            .getIcon("FileView.directoryIcon");

    private final Icon fileIcon = UIManager.getIcon("FileView.fileIcon");

    private final GitNodeTableModel gitNodeTableModel;

    private final JTable gitNodeTable;

    public RevisionTreeDialog(GitCommit gitCommit) {
        this.gitCommit = gitCommit;

        setTitle("Revision tree");
        setSize(new Dimension(800, 400));
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                handleWindowOpened();
            }
        });

        JPanel headerPanel = new JPanel();
        getContentPane().add(headerPanel, BorderLayout.NORTH);

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

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.5);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        JScrollPane gitFolderTreeScrollPane = new JScrollPane();
        splitPane.setLeftComponent(gitFolderTreeScrollPane);

        gitFolderTreeModel = new GitFolderTreeModel();
        gitFolderTree = new JTree(gitFolderTreeModel);
        gitFolderTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        DefaultTreeCellRenderer cellRenderer = (DefaultTreeCellRenderer) gitFolderTree
                .getCellRenderer();
        cellRenderer.setLeafIcon(directoryIcon);
        cellRenderer.setOpenIcon(directoryIcon);
        cellRenderer.setClosedIcon(directoryIcon);
        gitFolderTree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                handleGitFolderTreeValueChanged();
            }
        });
        gitFolderTreeScrollPane.setViewportView(gitFolderTree);

        JPanel gitNodePanel = new JPanel();
        splitPane.setRightComponent(gitNodePanel);
        gitNodePanel.setLayout(new BorderLayout(0, 0));

        JScrollPane gitNodeTableScrollPane = new JScrollPane();
        gitNodePanel.add(gitNodeTableScrollPane, BorderLayout.CENTER);

        gitNodeTableModel = new GitNodeTableModel();
        gitNodeTable = new JTable(gitNodeTableModel);
        gitNodeTable.setShowGrid(false);
        gitNodeTable.setFillsViewportHeight(true);
        gitNodeTable.setDefaultRenderer(GitNode.class,
                new GitNodeTableCellRenderer());
        gitNodeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() != 2) {
                    super.mouseClicked(e);
                    return;
                }

                handleOpenActionPerformed();
            }
        });
        gitNodeTableScrollPane.setViewportView(gitNodeTable);
    }

    private void handleWindowOpened() {
        GitFolder rootGitFolder = gitCommit.findRootGitFolder();
        addGitFolderToTreeRecursively(rootGitFolder);
    }

    private void addGitFolderToTreeRecursively(GitFolder gitFolder) {
        gitFolderTreeModel.addGitFolder(gitFolder);

        for (GitFolder childGitFolder : gitFolder.findChildGitFolders()) {
            addGitFolderToTreeRecursively(childGitFolder);
        }
    }

    private void handleGitFolderTreeValueChanged() {
        gitNodeTableModel.clear();
        GitFolder currentGitFolder = getCurrentGitFolder();

        if (currentGitFolder == null) {
            return;
        }

        for (GitFolder childGitFolder : currentGitFolder.findChildGitFolders()) {
            gitNodeTableModel.addGitNode(childGitFolder);
        }

        for (GitFile childGitFile : currentGitFolder.findChildGitFiles()) {
            gitNodeTableModel.addGitNode(childGitFile);
        }
    }

    private GitFolder getCurrentGitFolder() {
        TreePath treePath = gitFolderTree.getSelectionPath();

        if (treePath == null) {
            return null;
        }

        GitFolderTreeNode treeNode = (GitFolderTreeNode) treePath
                .getLastPathComponent();
        return treeNode.getGitFolder();
    }

    private void handleOpenActionPerformed() {
        GitNode currentGitNode = getCurrentGitNode();

        if (currentGitNode == null) {
            return;
        }

        if (currentGitNode instanceof GitFolder) {
            GitFolder gitFolder = (GitFolder) currentGitNode;
            selectGitFolderTreeNode(gitFolder);
            return;
        }

        GitFile gitFile = (GitFile) currentGitNode;
        Desktop desktop = Desktop.getDesktop();
        JdkUtils.open(desktop, gitFile.createTemporaryFile());
    }

    private GitNode getCurrentGitNode() {
        int viewRowIndex = gitNodeTable.getSelectedRow();

        if (viewRowIndex < 0) {
            return null;
        }

        int modelRowIndex = gitNodeTable.convertRowIndexToModel(viewRowIndex);
        return gitNodeTableModel.findGitNode(modelRowIndex);
    }

    private void selectGitFolderTreeNode(GitFolder gitFolder) {
        TreePath treePath = gitFolderTreeModel.getTreePath(gitFolder);

        if (treePath == null) {
            return;
        }

        gitFolderTree.setSelectionPath(treePath);
    }

    private class GitFolderTreeNode extends DefaultMutableTreeNode {

        private final GitFolder gitFolder;

        public GitFolderTreeNode(GitFolder gitFolder) {
            super(gitFolder.getDisplayName());
            this.gitFolder = gitFolder;
        }

        public GitFolder getGitFolder() {
            return gitFolder;
        }
    }

    private class GitFolderTreeModel extends DefaultTreeModel {

        private final Map<String, GitFolderTreeNode> treeNodeMap = new HashMap<String, GitFolderTreeNode>();

        public GitFolderTreeModel() {
            super(null);
        }

        public void addGitFolder(GitFolder gitFolder) {
            GitFolderTreeNode treeNode = null;

            if (gitFolder.isRoot()) {
                treeNode = new GitFolderTreeNode(gitFolder);
                setRoot(treeNode);

            } else {
                GitFolderTreeNode parentTreeNode = treeNodeMap.get(gitFolder
                        .getParentRelativePathString());
                treeNode = new GitFolderTreeNode(gitFolder);
                parentTreeNode.add(treeNode);
                nodeStructureChanged(parentTreeNode);
            }

            treeNodeMap.put(gitFolder.getRelativePathString(), treeNode);
        }

        public TreePath getTreePath(GitFolder gitFolder) {
            GitFolderTreeNode treeNode = treeNodeMap.get(gitFolder
                    .getRelativePathString());

            if (treeNode == null) {
                return null;
            }

            return new TreePath(getPathToRoot(treeNode));
        }
    }

    private class GitNodeTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        protected void setValue(Object value) {
            GitNode gitNode = (GitNode) value;

            if (gitNode instanceof GitFolder) {
                setIcon(directoryIcon);

            } else {
                setIcon(fileIcon);
            }

            super.setValue(gitNode.getDisplayName());
        }
    }

    private class GitNodeTableModel extends DefaultTableModel {

        private final Class<?>[] columnTypes = new Class[] { GitNode.class,
                String.class };

        public GitNodeTableModel() {
            super(new String[] { "Name", "ID" }, 0);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnTypes[columnIndex];
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        public void addGitNode(GitNode gitNode) {
            addRow(new Object[] { gitNode, gitNode.getShortIdString() });
        }

        public void clear() {
            setRowCount(0);
        }

        public GitNode findGitNode(int modelRowIndex) {
            return (GitNode) getValueAt(modelRowIndex, 0);
        }
    }
}
