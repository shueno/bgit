package bgit.view;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bgit.ApplicationException;
import bgit.JdkUtils;
import bgit.model.Application;
import bgit.model.GitCommit;
import bgit.model.GitConfig;
import bgit.model.GitPullResult;
import bgit.model.GitPushResult;
import bgit.model.GitRepositoryState;
import bgit.model.Project;
import bgit.model.StatusResult;
import bgit.model.WorkFile;
import bgit.model.WorkFolder;
import bgit.model.WorkNode;
import bgit.model.WorkNodeListener;

@SuppressWarnings("serial")
public class ProjectPanel extends JPanel {

    private static final Log log = LogFactory.getLog(ProjectPanel.class);

    private final Application application;

    private final Project project;

    private final JTextField workNodeTextField;

    private final JTable workNodeTable;

    private final JMenuBar menuBar;

    private final WorkFolderTreeModel workFolderTreeModel;

    private final JTree workFolderTree;

    private boolean shown;

    private final WorkNodeTableModel workNodeTableModel;

    private final WorkNodeListener workNodeListener;

    private final JTextField footerTextField;

    private File jumpWorkNodePath;

    private final Icon directoryIcon = UIManager
            .getIcon("FileView.directoryIcon");

    private final Action newFolderAction = new NewFolderAction();

    private final Action newFileAction = new NewFileAction();

    private final Action openAction = new OpenAction();

    private final Action parentAction = new ParentAction();

    private final Action childAction = new ChildAction();

    private final Action refreshAction = new RefreshAction();

    private final Action statusAction = new StatusAction();

    private final Action logAction = new LogAction();

    private final Action tagsAction = new TagsAction();

    private final Action archiveAction = new ArchiveAction();

    private final Action renameAction = new RenameAction();

    private final Action deleteAction = new DeleteAction();

    private final Action diffAction = new DiffAction();

    private final Action ignoreAction = new IgnoreAction();

    private final Action commitAction = new CommitAction();

    private final Action rollbackAction = new RollbackAction();

    private final Action discardLastCommitAction = new DiscardLastCommitAction();

    private final Action pullAction = new PullAction();

    private final Action pushAction = new PushAction();

    private final Action shareAction = new ShareAction();

    private final Action connectAction = new ConnectAction();

    private final Action disconnectAction = new DisconnectAction();

    private final Action configAction = new ConfigAction();

    private final Set<Action> conditionalActions = new HashSet<Action>(
            Arrays.asList(newFolderAction, newFileAction, openAction,
                    parentAction, renameAction, deleteAction, diffAction,
                    ignoreAction, logAction, archiveAction, rollbackAction,
                    discardLastCommitAction, pullAction, pushAction));

    public ProjectPanel(Application application, Project project) {
        this.application = application;
        this.project = project;
        this.workNodeListener = new ProjectPanelWorkNodeListener();

        setLayout(new BorderLayout(0, 0));
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                handleComponentShown();
            }
        });

        menuBar = new JMenuBar();
        add(menuBar, BorderLayout.NORTH);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        fileMenu.add(newFolderAction);
        fileMenu.add(newFileAction);
        fileMenu.addSeparator();
        fileMenu.add(openAction);
        fileMenu.add(parentAction);
        fileMenu.addSeparator();
        fileMenu.add(renameAction);
        fileMenu.add(deleteAction);
        fileMenu.addSeparator();
        fileMenu.add(diffAction);
        fileMenu.add(ignoreAction);
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem("Exit"));

        JMenu projectMenu = new JMenu("Project");
        menuBar.add(projectMenu);
        projectMenu.add(new JMenuItem("New project"));
        projectMenu.add(new JMenuItem("Clone from"));
        projectMenu.addSeparator();
        projectMenu.add(refreshAction);
        projectMenu.add(statusAction);
        projectMenu.add(logAction);
        projectMenu.add(tagsAction);
        projectMenu.add(archiveAction);
        projectMenu.addSeparator();
        projectMenu.add(new JMenuItem("Rename"));
        projectMenu.add(new JMenuItem("Close"));
        projectMenu.addSeparator();
        projectMenu.add(commitAction);
        projectMenu.add(rollbackAction);
        projectMenu.add(discardLastCommitAction);
        projectMenu.addSeparator();
        projectMenu.add(pullAction);
        projectMenu.add(pushAction);
        projectMenu.addSeparator();
        projectMenu.add(shareAction);
        projectMenu.add(connectAction);
        projectMenu.add(disconnectAction);
        projectMenu.addSeparator();
        projectMenu.add(configAction);

        JMenu applicationMenuItem = new JMenu("Application");
        menuBar.add(applicationMenuItem);
        applicationMenuItem.add(new JMenuItem("User settings"));
        applicationMenuItem.add(new JMenuItem("Config"));

        JPanel contentPanel = new JPanel();
        add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));

        JPanel headerPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) headerPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        JButton statusButton = new JButton(statusAction);
        headerPanel.add(statusButton);

        JButton logButton = new JButton(logAction);
        headerPanel.add(logButton);

        JButton commitButton = new JButton(commitAction);
        headerPanel.add(commitButton);

        JButton rollbackButton = new JButton(rollbackAction);
        headerPanel.add(rollbackButton);

        JButton pullButton = new JButton(pullAction);
        headerPanel.add(pullButton);

        JButton pushButton = new JButton(pushAction);
        headerPanel.add(pushButton);

        JPanel footerPanel = new JPanel();
        contentPanel.add(footerPanel, BorderLayout.SOUTH);
        GridBagLayout gbl_footerPanel = new GridBagLayout();
        gbl_footerPanel.columnWidths = new int[] { 0, 0 };
        gbl_footerPanel.rowHeights = new int[] { 0, 0 };
        gbl_footerPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_footerPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        footerPanel.setLayout(gbl_footerPanel);

        footerTextField = new JTextField();
        footerTextField.setEditable(false);
        GridBagConstraints gbc_footerTextField = new GridBagConstraints();
        gbc_footerTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_footerTextField.gridx = 0;
        gbc_footerTextField.gridy = 0;
        footerPanel.add(footerTextField, gbc_footerTextField);
        footerTextField.setColumns(10);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.5);
        contentPanel.add(splitPane, BorderLayout.CENTER);

        JScrollPane workFolderTreeScrollPane = new JScrollPane();
        splitPane.setLeftComponent(workFolderTreeScrollPane);

        workFolderTreeModel = new WorkFolderTreeModel();
        workFolderTree = new JTree(workFolderTreeModel);
        workFolderTree.setDragEnabled(true);
        workFolderTree.setTransferHandler(new WorkFolderTreeTransferHandler());
        workFolderTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        DefaultTreeCellRenderer cellRenderer = (DefaultTreeCellRenderer) workFolderTree
                .getCellRenderer();
        cellRenderer.setLeafIcon(directoryIcon);
        cellRenderer.setOpenIcon(directoryIcon);
        cellRenderer.setClosedIcon(directoryIcon);
        workFolderTree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                handleWorkFolderTreeValueChanged();
            }
        });
        InputMap workFolderTreeInputMap = workFolderTree
                .getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        workFolderTreeInputMap.put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "table");
        ActionMap workFolderTreeActionMap = workFolderTree.getActionMap();
        workFolderTreeActionMap.put("startEditing", renameAction);
        workFolderTreeActionMap.put("table", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (workFolderTree.isSelectionEmpty()) {
                    return;
                }

                workNodeTable.requestFocusInWindow();
            }
        });
        workFolderTreeScrollPane.setViewportView(workFolderTree);

        JPanel workNodePanel = new JPanel();
        splitPane.setRightComponent(workNodePanel);
        workNodePanel.setLayout(new BorderLayout(0, 0));

        JPanel workNodeHeaderPanel = new JPanel();
        workNodePanel.add(workNodeHeaderPanel, BorderLayout.NORTH);
        GridBagLayout gbl_workNodeHeaderPanel = new GridBagLayout();
        gbl_workNodeHeaderPanel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gbl_workNodeHeaderPanel.rowHeights = new int[] { 0, 0 };
        gbl_workNodeHeaderPanel.columnWeights = new double[] { 0.0, 1.0, 0.0,
                0.0, Double.MIN_VALUE };
        gbl_workNodeHeaderPanel.rowWeights = new double[] { 0.0,
                Double.MIN_VALUE };
        workNodeHeaderPanel.setLayout(gbl_workNodeHeaderPanel);

        JButton parentButton = new JButton(parentAction);
        GridBagConstraints gbc_parentButton = new GridBagConstraints();
        gbc_parentButton.insets = new Insets(0, 0, 0, 5);
        gbc_parentButton.gridx = 0;
        gbc_parentButton.gridy = 0;
        workNodeHeaderPanel.add(parentButton, gbc_parentButton);

        workNodeTextField = new JTextField();
        workNodeTextField.setEditable(false);
        GridBagConstraints gbc_workNodeTextField = new GridBagConstraints();
        gbc_workNodeTextField.insets = new Insets(0, 0, 0, 5);
        gbc_workNodeTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_workNodeTextField.gridx = 1;
        gbc_workNodeTextField.gridy = 0;
        workNodeHeaderPanel.add(workNodeTextField, gbc_workNodeTextField);
        workNodeTextField.setColumns(10);

        JButton openButton = new JButton(openAction);
        GridBagConstraints gbc_openButton = new GridBagConstraints();
        gbc_openButton.insets = new Insets(0, 0, 0, 5);
        gbc_openButton.gridx = 2;
        gbc_openButton.gridy = 0;
        workNodeHeaderPanel.add(openButton, gbc_openButton);

        JButton diffButton = new JButton(diffAction);
        GridBagConstraints gbc_diffButton = new GridBagConstraints();
        gbc_diffButton.gridx = 3;
        gbc_diffButton.gridy = 0;
        workNodeHeaderPanel.add(diffButton, gbc_diffButton);

        JScrollPane workNodeTableScrollPane = new JScrollPane();
        workNodePanel.add(workNodeTableScrollPane, BorderLayout.CENTER);

        workNodeTableModel = new WorkNodeTableModel();
        workNodeTable = new JTable(workNodeTableModel);
        workNodeTable.setShowGrid(false);
        workNodeTable.setFillsViewportHeight(true);
        workNodeTable.setDefaultRenderer(WorkNode.class,
                new WorkNodeTableCellRenderer());
        workNodeTable.setDragEnabled(true);
        workNodeTable.setDropMode(DropMode.INSERT_ROWS);
        workNodeTable.setTransferHandler(new WorkNodeTableTransferHandler());
        workNodeTable.setFocusTraversalKeys(
                KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                new HashSet<AWTKeyStroke>(Arrays.asList(KeyStroke.getKeyStroke(
                        KeyEvent.VK_TAB, 0))));
        workNodeTable.setFocusTraversalKeys(
                KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                new HashSet<AWTKeyStroke>(Arrays.asList(KeyStroke.getKeyStroke(
                        KeyEvent.VK_TAB, InputEvent.SHIFT_MASK))));
        workNodeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() != 2) {
                    super.mouseClicked(e);
                    return;
                }

                handleOpenActionPerformed();
            }
        });
        workNodeTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {

                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        handleWorkNodeTableValueChanged();
                    }
                });
        InputMap workNodeTableInputMap = workNodeTable
                .getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        workNodeTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "open");
        workNodeTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
                "parent");
        workNodeTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
                "child");
        workNodeTableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),
                "rename");
        workNodeTableInputMap.put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_MASK),
                "tree");
        ActionMap workNodeTableActionMap = workNodeTable.getActionMap();
        workNodeTableActionMap.put("open", openAction);
        workNodeTableActionMap.put("parent", parentAction);
        workNodeTableActionMap.put("child", childAction);
        workNodeTableActionMap.put("rename", renameAction);
        workNodeTableActionMap.put("tree", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                workFolderTree.requestFocusInWindow();
            }
        });
        workNodeTableScrollPane.setViewportView(workNodeTable);
    }

    private void handleComponentShown() {

        if (shown) {
            return;
        }

        doRefresh();
        workFolderTree.expandRow(0);
        workFolderTree.requestFocusInWindow();
        project.startMonitor();
        shown = true;
    }

    private void doRefresh() {
        long time = System.currentTimeMillis();

        try {
            ViewHelper.setWaitCursor(this);
            project.removeWorkNodeListener(workNodeListener);
            WorkNode jumpWorkNode = getCurrentWorkNode();
            WorkFolder jumpWorkFolder = null;

            if (jumpWorkNode == null) {
                jumpWorkFolder = getCurrentWorkFolder();
            }

            workFolderTreeModel.clear();
            project.status();
            WorkFolder rootWorkFolder = project.findRootWorkFolder();

            if (rootWorkFolder == null) {
                return;
            }

            addWorkFolderToWorkFolderTreeRecursively(rootWorkFolder);

            if (jumpWorkNode != null) {
                jump(jumpWorkNode.getAbsolutePath());

            } else if (jumpWorkFolder != null) {
                selectWorkFolderTreeNode(jumpWorkFolder.getAbsolutePath());
            }

        } finally {
            String s = String.format("Refresh: %dms",
                    System.currentTimeMillis() - time);
            footerTextField.setText(s);
            ViewHelper.setDefaultCursor(this);
            project.addWorkNodeListener(workNodeListener);
            updateActions();
        }
    }

    private void addWorkFolderToWorkFolderTreeRecursively(WorkFolder workFolder) {
        workFolderTreeModel.addWorkFolder(workFolder);

        for (WorkFolder childWorkFolder : workFolder.findChildWorkFolders()) {
            addWorkFolderToWorkFolderTreeRecursively(childWorkFolder);
        }
    }

    private void handleWorkNodeCreated(File absolutePath) {
        handleWorkNodeChanged(absolutePath);
    }

    private void handleWorkNodeDeleted(File absolutePath) {

        if (absolutePath.getName().equals(WorkNode.GIT_IGNORE_NAME)) {
            doRefresh();
            return;
        }

        workFolderTreeModel.removeWorkFolder(absolutePath);

        if (!isInCurrentWorkFolder(absolutePath)) {
            return;
        }

        workNodeTableModel.removeWorkNode(absolutePath);
    }

    private void handleWorkNodeChanged(File absolutePath) {

        if (absolutePath.getName().equals(WorkNode.GIT_IGNORE_NAME)) {
            doRefresh();
            return;
        }

        WorkNode workNode = null;

        if (absolutePath.isDirectory()) {
            WorkFolder workFolder = project.findWorkFolder(absolutePath);
            workFolderTreeModel.addWorkFolder(workFolder);
            workNode = workFolder;
        }

        if (isInCurrentWorkFolder(absolutePath)) {

            if (absolutePath.isFile()) {
                workNode = project.findWorkFile(absolutePath);
            }

            if (workNode == null) {
                return;
            }

            workNodeTableModel.addWorkNode(workNode);
        }
    }

    private boolean isInCurrentWorkFolder(File absolutePath) {
        WorkFolder currentWorkFolder = getCurrentWorkFolder();

        if (currentWorkFolder == null) {
            return false;
        }

        return absolutePath.getParent().equals(
                currentWorkFolder.getAbsolutePath().toString());
    }

    private void handleWorkFolderTreeValueChanged() {
        updateWorkNodeTable();
        updateWorkNodeTextField();
        updateActions();
    }

    private void updateWorkNodeTable() {
        workNodeTableModel.clear();
        WorkFolder currentWorkFolder = getCurrentWorkFolder();

        if (currentWorkFolder == null) {
            return;
        }

        for (WorkFolder childWorkFolder : currentWorkFolder
                .findChildWorkFolders()) {
            workNodeTableModel.addWorkNode(childWorkFolder);
        }

        for (WorkFile childWorkFile : currentWorkFolder.findChildWorkFiles()) {
            workNodeTableModel.addWorkNode(childWorkFile);
        }

        if (jumpWorkNodePath != null) {
            selectWorkNodeTableRow(jumpWorkNodePath);
            jumpWorkNodePath = null;
        }
    }

    private void selectWorkNodeTableRow(File workNodePath) {
        int modelRowIndex = workNodeTableModel.findModelRowIndex(workNodePath);

        if (modelRowIndex < 0) {
            return;
        }

        int viewRowIndex = workNodeTable.convertRowIndexToView(modelRowIndex);
        workNodeTable.changeSelection(viewRowIndex, 0, false, false);
    }

    private void handleWorkNodeTableValueChanged() {
        updateWorkNodeTextField();
        updateActions();
    }

    private void updateWorkNodeTextField() {
        WorkFolder currentWorkFolder = getCurrentWorkFolder();

        if (currentWorkFolder == null) {
            workNodeTextField.setText("");
            return;
        }

        WorkNode currentWorkNode = getCurrentWorkNode();

        if (currentWorkNode == null) {
            workNodeTextField.setText(currentWorkFolder.getAbsolutePath()
                    .toString() + File.separator);
            return;
        }

        workNodeTextField.setText(currentWorkNode.getAbsolutePath().toString());
    }

    private void updateActions() {
        Set<Action> enabledActions = new HashSet<Action>();
        GitCommit headGitCommit = project.findHeadGitCommit();

        if (headGitCommit != null) {
            enabledActions.add(logAction);
            enabledActions.add(archiveAction);
            enabledActions.add(rollbackAction);

            if (project.isConnected()) {
                enabledActions.add(pullAction);
                enabledActions.add(pushAction);
            }

            if (!headGitCommit.findParentGitCommits().isEmpty()) {
                enabledActions.add(discardLastCommitAction);
            }
        }

        WorkFolder currentWorkFolder = getCurrentWorkFolder();

        if (currentWorkFolder != null) {
            enabledActions.add(newFolderAction);
            enabledActions.add(newFileAction);

            WorkNode currentWorkNode = getCurrentWorkNode();

            if (!(currentWorkFolder.isRoot() && currentWorkNode == null)) {
                enabledActions.add(parentAction);
                enabledActions.add(renameAction);
                enabledActions.add(deleteAction);
                enabledActions.add(ignoreAction);
            }

            if (currentWorkNode != null) {
                enabledActions.add(openAction);

                if (currentWorkNode instanceof WorkFile) {
                    enabledActions.add(diffAction);
                }
            }
        }

        for (Action conditionalAction : conditionalActions) {
            conditionalAction.setEnabled(enabledActions
                    .contains(conditionalAction));
        }
    }

    private void handleOpenActionPerformed() {
        WorkNode currentWorkNode = getCurrentWorkNode();

        if (currentWorkNode == null) {
            return;
        }

        if (currentWorkNode instanceof WorkFolder) {
            selectWorkFolderTreeNode(currentWorkNode.getAbsolutePath());
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        JdkUtils.open(desktop, currentWorkNode.getAbsolutePath());
    }

    private void jump(File workNodePath) {
        File parentWorkFolderPath = workNodePath.getParentFile();
        WorkFolder currentWorkFolder = getCurrentWorkFolder();

        if (currentWorkFolder != null
                && currentWorkFolder.getAbsolutePath().equals(
                        parentWorkFolderPath)) {
            selectWorkNodeTableRow(workNodePath);

        } else {
            jumpWorkNodePath = workNodePath;
            selectWorkFolderTreeNode(parentWorkFolderPath);
        }

        workNodeTable.requestFocusInWindow();
    }

    private void selectWorkFolderTreeNode(File workFolderPath) {
        TreePath treePath = workFolderTreeModel.getTreePath(workFolderPath);

        if (treePath == null) {
            return;
        }

        workFolderTree.setSelectionPath(treePath);
    }

    private WorkFolder getCurrentWorkFolder() {
        TreePath treePath = workFolderTree.getSelectionPath();

        if (treePath == null) {
            return null;
        }

        WorkFolderTreeNode treeNode = (WorkFolderTreeNode) treePath
                .getLastPathComponent();
        return treeNode.getWorkFolder();
    }

    private WorkNode getCurrentWorkNode() {
        int viewRowIndex = workNodeTable.getSelectionModel()
                .getLeadSelectionIndex();

        if (viewRowIndex < 0) {
            return null;
        }

        if (!workNodeTable.getSelectionModel().isSelectedIndex(viewRowIndex)) {
            return null;
        }

        int modelRowIndex = workNodeTable.convertRowIndexToModel(viewRowIndex);
        return workNodeTableModel.getWorkNode(modelRowIndex);
    }

    private boolean transfer(TransferSupport support, File destinationFolderPath) {
        List<File> sourceNodePaths = getSourceNodePaths(support);
        boolean moved = support.isDrop()
                && support.getDropAction() == TransferHandler.MOVE;

        if (moved) {

            for (File sourceNodePath : sourceNodePaths) {
                File sourceParentPath = sourceNodePath.getParentFile();

                if (destinationFolderPath.toString().equals(
                        sourceParentPath.toString())) {
                    return false;
                }

                if (destinationFolderPath.toString().startsWith(
                        sourceNodePath.toString())) {
                    return false;
                }

                File destinationNodePath = new File(destinationFolderPath,
                        sourceNodePath.getName());

                if (destinationNodePath.exists()) {
                    JOptionPane.showMessageDialog(null, "Already exists: "
                            + destinationNodePath);
                    return false;
                }
            }
        }

        for (File sourceNodePath : sourceNodePaths) {
            File destinationNodePath = new File(destinationFolderPath,
                    sourceNodePath.getName());
            int number = 0;

            while (destinationNodePath.exists()) {
                number++;
                String name = sourceNodePath.getName();
                int index = name.indexOf(".");

                if (index < 0) {
                    index = name.length();
                }

                name = String.format("%s_%d%s", name.substring(0, index),
                        number, name.substring(index));
                destinationNodePath = new File(destinationFolderPath, name);
            }

            if (moved) {
                sourceNodePath.renameTo(destinationNodePath);
                continue;
            }

            if (sourceNodePath.isDirectory()) {

                try {
                    FileUtils
                            .copyDirectory(sourceNodePath, destinationNodePath);

                } catch (IOException e) {
                    throw new ApplicationException(e);
                }

            } else if (sourceNodePath.isFile()) {

                try {
                    FileUtils.copyFile(sourceNodePath, destinationNodePath);

                } catch (IOException e) {
                    throw new ApplicationException(e);
                }
            }
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    private List<File> getSourceNodePaths(TransferSupport support) {
        List<File> paths = new ArrayList<File>();

        for (File path : (List<File>) JdkUtils.getTransferData(
                support.getTransferable(), DataFlavor.javaFileListFlavor)) {

            if (!path.exists()) {
                continue;
            }

            paths.add(path);
        }

        return paths;
    }

    public Project getProject() {
        return project;
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    private class WorkFolderTreeNode extends DefaultMutableTreeNode {

        private WorkFolder workFolder;

        public WorkFolderTreeNode(WorkFolder workFolder) {
            super(workFolder.getDisplayName());
            this.workFolder = workFolder;
        }

        public WorkFolder getWorkFolder() {
            return workFolder;
        }

        public void setWorkFolder(WorkFolder workFolder) {
            this.workFolder = workFolder;
            setUserObject(workFolder.getDisplayName());
        }
    }

    private class WorkFolderTreeModel extends DefaultTreeModel {

        private final Map<String, WorkFolderTreeNode> treeNodeMap = new HashMap<String, WorkFolderTreeNode>();

        public WorkFolderTreeModel() {
            super(null);
        }

        public void clear() {
            setRoot(null);
            treeNodeMap.clear();
        }

        public void addWorkFolder(WorkFolder workFolder) {
            WorkFolderTreeNode treeNode = getWorkFolderTreeNode(workFolder
                    .getAbsolutePath());

            if (treeNode != null) {
                treeNode.setWorkFolder(workFolder);
                nodeChanged(treeNode);
                return;
            }

            treeNode = new WorkFolderTreeNode(workFolder);

            if (workFolder.isRoot()) {
                setRoot(treeNode);
                treeNodeMap.clear();
                treeNodeMap.put(treeNode.getWorkFolder().getAbsolutePath()
                        .toString(), treeNode);
                return;
            }

            WorkFolderTreeNode parentTreeNode = getWorkFolderTreeNode(workFolder
                    .getAbsolutePath().getParentFile());

            if (parentTreeNode == null) {
                return;
            }

            parentTreeNode.add(treeNode);
            treeNodeMap.put(workFolder.getAbsolutePath().toString(), treeNode);
            nodesWereInserted(parentTreeNode,
                    new int[] { parentTreeNode.getChildCount() - 1 });
        }

        public void removeWorkFolder(File absolutePath) {
            WorkFolderTreeNode treeNode = getWorkFolderTreeNode(absolutePath);

            if (treeNode == null) {
                return;
            }

            removeNodeFromParent(treeNode);
            removeNodeFromMapRecursively(treeNode);
        }

        private void removeNodeFromMapRecursively(WorkFolderTreeNode treeNode) {
            treeNodeMap.remove(treeNode.getWorkFolder().getAbsolutePath()
                    .toString());

            for (Enumeration<?> enumeration = treeNode.children(); enumeration
                    .hasMoreElements();) {
                removeNodeFromMapRecursively((WorkFolderTreeNode) enumeration
                        .nextElement());
            }
        }

        public TreePath getTreePath(File absolutePath) {
            WorkFolderTreeNode treeNode = getWorkFolderTreeNode(absolutePath);

            if (treeNode == null) {
                return null;
            }

            return new TreePath(getPathToRoot(treeNode));
        }

        public WorkFolderTreeNode getWorkFolderTreeNode(File absolutePath) {
            return treeNodeMap.get(absolutePath.toString());
        }
    }

    private class WorkFolderTreeTransferHandler extends TransferHandler {

        @Override
        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            WorkFolder workFolder = getCurrentWorkFolder();

            if (workFolder == null) {
                return null;
            }

            List<File> paths = new ArrayList<File>();
            paths.add(workFolder.getAbsolutePath());
            return new JavaFileListTransferable(paths);
        }

        @Override
        protected void exportDone(JComponent source, Transferable data,
                int action) {
            log.info(action);
        }

        @Override
        public boolean canImport(TransferSupport support) {

            if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }

            return true;
        }

        @Override
        public boolean importData(TransferSupport support) {

            if (!canImport(support)) {
                return false;
            }

            File destinationFolderPath = getDestinationFolderPath(support);

            if (destinationFolderPath == null) {
                return false;
            }

            return transfer(support, destinationFolderPath);
        }

        private File getDestinationFolderPath(TransferSupport support) {
            TreePath treePath = null;

            if (support.isDrop()) {
                Point point = support.getDropLocation().getDropPoint();
                treePath = workFolderTree.getPathForLocation(
                        (int) point.getX(), (int) point.getY());

            } else {
                treePath = workFolderTree.getSelectionPath();
            }

            if (treePath == null) {
                return null;
            }

            return ((WorkFolderTreeNode) treePath.getLastPathComponent())
                    .getWorkFolder().getAbsolutePath();
        }
    }

    private class WorkNodeTableModel extends DefaultTableModel {

        private final Class<?>[] columnTypes = new Class[] { WorkNode.class,
                Object.class, Object.class, Long.class };

        public WorkNodeTableModel() {
            super(new String[] { "Name", "Status", "Date", "Size" }, 0);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnTypes[columnIndex];
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        public WorkNode getWorkNode(int modelRowIndex) {
            return (WorkNode) getValueAt(modelRowIndex, 0);
        }

        public void addWorkNode(WorkNode workNode) {
            int modelRowIndex = findModelRowIndex(workNode.getAbsolutePath());

            if (modelRowIndex >= 0) {
                setValueAt(workNode, modelRowIndex, 0);
                setValueAt(workNode.getStatus(), modelRowIndex, 1);
                setValueAt(workNode.getDateLastModified(), modelRowIndex, 2);
                setValueAt(workNode.getSize(), modelRowIndex, 3);
                return;
            }

            // TODO Insert at the ordered position.
            addRow(new Object[] { workNode, workNode.getStatus(),
                    workNode.getDateLastModified(), workNode.getSize() });
        }

        public void clear() {
            setRowCount(0);
        }

        public void removeWorkNode(File absolutePath) {
            int modelRowIndex = findModelRowIndex(absolutePath);

            if (modelRowIndex < 0) {
                return;
            }

            removeRow(modelRowIndex);
        }

        // TODO Improve search algorithm.
        public int findModelRowIndex(File absolutePath) {

            for (int i = getRowCount() - 1; i >= 0; i--) {

                if (getWorkNode(i).getAbsolutePath().toString()
                        .equals(absolutePath.toString())) {
                    return i;
                }
            }

            return -1;
        }
    }

    private class WorkNodeTableCellRenderer extends DefaultTableCellRenderer {

        private final FileSystemView fileSystemView = FileSystemView
                .getFileSystemView();

        @Override
        protected void setValue(Object value) {
            WorkNode workNode = (WorkNode) value;
            Icon icon = null;

            if (workNode instanceof WorkFolder) {
                icon = directoryIcon;

            } else {
                icon = fileSystemView.getSystemIcon(workNode.getAbsolutePath());
            }

            setIcon(icon);
            super.setValue(workNode.getDisplayName());
        }
    }

    private class WorkNodeTableTransferHandler extends TransferHandler {

        @Override
        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            List<File> paths = new ArrayList<File>();

            for (int viewRowIndex : workNodeTable.getSelectedRows()) {
                int modelRowIndex = workNodeTable
                        .convertRowIndexToModel(viewRowIndex);
                WorkNode workNode = workNodeTableModel
                        .getWorkNode(modelRowIndex);
                paths.add(workNode.getAbsolutePath());
            }

            return new JavaFileListTransferable(paths);
        }

        @Override
        protected void exportDone(JComponent source, Transferable data,
                int action) {
            log.info(action);
        }

        // TODO Cannot use javaFileListFlavor in Linux
        // TODO Cannot check droppable because fail to call getTransferData
        @Override
        public boolean canImport(TransferSupport support) {

            if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }

            return true;
        }

        // TODO Cannot get whether the action is cut by clipboard
        @Override
        public boolean importData(TransferSupport support) {

            if (!canImport(support)) {
                return false;
            }

            File destinationFolderPath = getDestinationFolderPath();

            if (destinationFolderPath == null) {
                return false;
            }

            return transfer(support, destinationFolderPath);
        }

        private File getDestinationFolderPath() {
            WorkFolder destinationWorkFolder = getCurrentWorkFolder();

            if (destinationWorkFolder == null) {
                return null;
            }

            return destinationWorkFolder.getAbsolutePath();
        }
    }

    private class ProjectPanelWorkNodeListener implements WorkNodeListener {

        @Override
        public void workNodeCreated(final File absolutePath) {
            JdkUtils.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    handleWorkNodeCreated(absolutePath);
                }
            });
        }

        @Override
        public void workNodeDeleted(final File absolutePath) {
            JdkUtils.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    handleWorkNodeDeleted(absolutePath);
                }
            });
        }

        @Override
        public void workNodeChanged(final File absolutePath) {
            JdkUtils.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    handleWorkNodeChanged(absolutePath);
                }
            });
        }
    }

    private class RefreshAction extends AbstractAction {

        public RefreshAction() {
            putValue(NAME, "Refresh");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doRefresh();
        }
    }

    private class StatusAction extends AbstractAction {

        public StatusAction() {
            putValue(NAME, "Status");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            StatusDialog statusDialog = new StatusDialog(application, project);
            statusDialog.setVisible(true);
            File absolutePath = statusDialog.getAbsolutePath();

            if (absolutePath == null) {
                return;
            }

            jump(absolutePath);
        }
    }

    private class ParentAction extends AbstractAction {

        public ParentAction() {
            putValue(NAME, "Parent");
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            WorkFolder workFolder = getCurrentWorkFolder();

            if (workFolder == null) {
                return;
            }

            jump(workFolder.getAbsolutePath());
        }
    }

    private class ChildAction extends AbstractAction {

        public ChildAction() {
            putValue(NAME, "Child");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            WorkNode currentWorkNode = getCurrentWorkNode();

            if (!(currentWorkNode instanceof WorkFolder)) {
                return;
            }

            selectWorkFolderTreeNode(currentWorkNode.getAbsolutePath());
        }
    }

    private class OpenAction extends AbstractAction {

        public OpenAction() {
            putValue(NAME, "Open");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            handleOpenActionPerformed();
        }
    }

    private class RollbackAction extends AbstractAction {

        public RollbackAction() {
            putValue(NAME, "Rollback");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            GitRepositoryState gitRepositoryState = project
                    .findGitRepositoryState();

            if (!gitRepositoryState.canRollback()) {
                JOptionPane.showMessageDialog(null,
                        "Repository state is invalid");
                return;
            }

            GitCommit headGitCommit = project.findHeadGitCommit();

            if (headGitCommit == null) {
                JOptionPane.showMessageDialog(null, "No commit");
                return;
            }

            StatusResult statusResult = project.status();

            if (statusResult.isClean()) {
                JOptionPane.showMessageDialog(null, "Not altered");
                return;
            }

            int option = JOptionPane.showConfirmDialog(null, "Rollback?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            if (option != JOptionPane.YES_OPTION) {
                return;
            }

            project.rollback();
        }
    }

    private class CommitAction extends AbstractAction {

        public CommitAction() {
            putValue(NAME, "Commit");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            GitRepositoryState gitRepositoryState = project
                    .findGitRepositoryState();

            if (!gitRepositoryState.canCommit()) {
                JOptionPane.showMessageDialog(null,
                        "Repository state is invalid");
                return;
            }

            StatusResult statusResult = project.status();

            if (statusResult.isClean()) {
                JOptionPane.showMessageDialog(null, "Not altered");
                return;
            }

            CommitEditorDialog commitEditorDialog = new CommitEditorDialog(
                    application, project, statusResult);
            commitEditorDialog.setVisible(true);

            if (!commitEditorDialog.isSucceeded()) {
                return;
            }

            doRefresh();
        }
    }

    private class IgnoreAction extends AbstractAction {

        public IgnoreAction() {
            putValue(NAME, "Ignore");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            WorkNode workNode = getCurrentWorkNode();

            if (workNode == null) {
                WorkFolder workFolder = getCurrentWorkFolder();

                if (workFolder == null || workFolder.isRoot()) {
                    JOptionPane.showMessageDialog(null, "Not found");
                    return;
                }

                workNode = workFolder;
            }

            if (workNode instanceof WorkFile) {
                File workFilePath = workNode.getAbsolutePath();
                StatusResult statusResult = project.status(workFilePath);

                if (!statusResult.isUntracked(workFilePath)) {
                    JOptionPane.showMessageDialog(null, "Not untracked");
                    return;
                }
            }

            workNode.ignore();
        }
    }

    private class NewFolderAction extends AbstractAction {

        public NewFolderAction() {
            putValue(NAME, "New folder");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            WorkFolder currentWorkFolder = getCurrentWorkFolder();

            if (currentWorkFolder == null) {
                JOptionPane.showMessageDialog(null, "Not found");
                return;
            }

            String name = null;

            while (true) {
                name = JOptionPane.showInputDialog("Folder name", name);

                if (name == null) {
                    return;
                }

                name = name.trim();

                if (!new File(currentWorkFolder.getAbsolutePath(), name)
                        .exists()) {
                    break;
                }

                JOptionPane.showMessageDialog(null, "Folder name is invalid.");
            }

            currentWorkFolder.createNewFolder(name);
        }
    }

    private class NewFileAction extends AbstractAction {

        public NewFileAction() {
            putValue(NAME, "New file");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            WorkFolder currentWorkFolder = getCurrentWorkFolder();

            if (currentWorkFolder == null) {
                return;
            }

            String name = null;

            while (true) {
                name = JOptionPane.showInputDialog("File name", name);

                if (name == null) {
                    return;
                }

                name = name.trim();

                if (!new File(currentWorkFolder.getAbsolutePath(), name)
                        .exists()) {
                    break;
                }

                JOptionPane.showMessageDialog(null, "File name is invalid.");
            }

            currentWorkFolder.createNewFile(name);
        }
    }

    private class RenameAction extends AbstractAction {

        public RenameAction() {
            putValue(NAME, "Rename");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            WorkNode workNode = getCurrentWorkNode();

            if (workNode == null) {
                WorkFolder workFolder = getCurrentWorkFolder();

                if (workFolder == null || workFolder.isRoot()) {
                    JOptionPane.showMessageDialog(null, "Not found");
                    return;
                }

                workNode = workFolder;
            }

            String title = workNode instanceof WorkFolder ? "Folder name"
                    : "File name";
            String name = workNode.getAbsolutePath().getName();

            while (true) {
                name = JOptionPane.showInputDialog(title, name);

                if (name == null) {
                    return;
                }

                name = name.trim();

                if (!new File(workNode.getAbsolutePath().getParentFile(), name)
                        .exists()) {
                    break;
                }

                JOptionPane.showMessageDialog(null,
                        String.format("%s is invalid.", title));
            }

            workNode.rename(name);
        }
    }

    private class DeleteAction extends AbstractAction {

        public DeleteAction() {
            putValue(NAME, "Delete");
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            List<WorkNode> workNodes = new ArrayList<WorkNode>();

            for (int viewRowIndex : workNodeTable.getSelectedRows()) {
                int modelRowIndex = workNodeTable
                        .convertRowIndexToModel(viewRowIndex);
                WorkNode workNode = workNodeTableModel
                        .getWorkNode(modelRowIndex);
                workNodes.add(workNode);
            }

            if (workNodes.isEmpty()) {
                WorkFolder workFolder = getCurrentWorkFolder();

                if (workFolder != null && !workFolder.isRoot()) {
                    workNodes.add(workFolder);
                }
            }

            if (workNodes.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Not found");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Delete ");

            if (workNodes.size() == 1) {
                sb.append("'");
                sb.append(workNodes.get(0).getAbsolutePath().getName());
                sb.append("'");

            } else {
                sb.append(workNodes.size());
                sb.append(" items");
            }

            sb.append("?");

            int option = JOptionPane.showConfirmDialog(null, sb.toString(),
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            if (option != JOptionPane.YES_OPTION) {
                return;
            }

            for (WorkNode workNode : workNodes) {
                workNode.delete();
            }
        }
    }

    private class LogAction extends AbstractAction {

        public LogAction() {
            putValue(NAME, "Log");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            GitCommit headGitCommit = project.findHeadGitCommit();

            if (headGitCommit == null) {
                JOptionPane.showMessageDialog(null, "No commit");
                return;
            }

            Iterable<GitCommit> gitCommits = project.log();
            LogDialog logDialog = new LogDialog(application, gitCommits);
            logDialog.setVisible(true);
        }
    }

    private class TagsAction extends AbstractAction {

        public TagsAction() {
            putValue(NAME, "Tags");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            TagsDialog tagsDialog = new TagsDialog(application, project);
            tagsDialog.setVisible(true);
        }
    }

    private class ArchiveAction extends AbstractAction {

        public ArchiveAction() {
            putValue(NAME, "Archive");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            GitCommit headGitCommit = project.findHeadGitCommit();

            if (headGitCommit == null) {
                JOptionPane.showMessageDialog(null, "No commit");
                return;
            }

            ViewHelper.commandArchive(headGitCommit);
        }
    }

    private class DiffAction extends AbstractAction {

        public DiffAction() {
            putValue(NAME, "Diff");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            WorkNode workNode = getCurrentWorkNode();

            if (!(workNode instanceof WorkFile)) {
                JOptionPane.showMessageDialog(null, "Not found");
                return;
            }

            String relativePathString = workNode.getRelativePathString();
            GitCommit newestGitCommit = project
                    .findNewestGitCommit(relativePathString);
            DiffDialog diffDialog = new DiffDialog(application, project,
                    relativePathString, newestGitCommit, null);
            diffDialog.setVisible(true);
        }
    }

    private class DiscardLastCommitAction extends AbstractAction {

        public DiscardLastCommitAction() {
            putValue(NAME, "Discard last commit");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            GitRepositoryState gitRepositoryState = project
                    .findGitRepositoryState();

            if (!gitRepositoryState.canDiscardLastCommit()) {
                JOptionPane.showMessageDialog(null,
                        "Repository state is invalid");
                return;
            }

            GitCommit headGitCommit = project.findHeadGitCommit();

            if (headGitCommit == null
                    || headGitCommit.findParentGitCommits().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No parent commit");
                return;
            }

            int option = JOptionPane.showConfirmDialog(null,
                    "Discard last commit?", "Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (option != JOptionPane.YES_OPTION) {
                return;
            }

            project.discardLastCommit();
            doRefresh();
        }
    }

    private class ShareAction extends AbstractAction {

        public ShareAction() {
            putValue(NAME, "Share");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ShareDialog shareDialog = new ShareDialog(application, project);
            shareDialog.setVisible(true);

            if (!shareDialog.isSucceeded()) {
                return;
            }

            JOptionPane.showMessageDialog(null, "Shared");
            updateActions();
        }
    }

    private class ConnectAction extends AbstractAction {

        public ConnectAction() {
            putValue(NAME, "Connect");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            GitConfig gitConfig = project.findGitConfig();
            String branchName = project.getCurrentBranchName();
            ConnectDialog connectDialog = new ConnectDialog(application,
                    gitConfig, branchName);
            connectDialog.setVisible(true);

            if (!connectDialog.isSucceeded()) {
                return;
            }

            JOptionPane.showMessageDialog(null, "Connected");
            updateActions();
        }
    }

    private class ConfigAction extends AbstractAction {

        public ConfigAction() {
            putValue(NAME, "Config");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            GitConfig gitConfig = project.findGitConfig();
            ConfigDialog configDialog = new ConfigDialog(application, gitConfig);
            configDialog.setVisible(true);
        }
    }

    private class DisconnectAction extends AbstractAction {

        public DisconnectAction() {
            putValue(NAME, "Disconnect");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int option = JOptionPane.showConfirmDialog(null, "Disconnect?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            if (option != JOptionPane.YES_OPTION) {
                return;
            }

            GitConfig gitConfig = project.findGitConfig();
            String branchName = project.getCurrentBranchName();
            String remoteName = gitConfig.getConnectionRemoteName(branchName);
            gitConfig.unsetConnection(remoteName, branchName);
            gitConfig.save();
            JOptionPane.showMessageDialog(null, "Disconnected");
            updateActions();
        }
    }

    private class PullAction extends AbstractAction {

        public PullAction() {
            putValue(NAME, "Pull from");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            GitRepositoryState gitRepositoryState = project
                    .findGitRepositoryState();

            if (!gitRepositoryState.canPull()) {
                JOptionPane.showMessageDialog(null,
                        "Repository state is invalid");
                return;
            }

            if (!project.isConnected()) {
                JOptionPane.showMessageDialog(null, "Not connected");
                return;
            }

            int option = JOptionPane.showConfirmDialog(null, "Pull?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            if (option != JOptionPane.YES_OPTION) {
                return;
            }

            GitPullResult gitPullResult = project.pull();
            JOptionPane.showMessageDialog(null, gitPullResult.getMessage());

            if (!gitPullResult.isSuccessful()) {
                return;
            }

            doRefresh();
        }
    }

    private class PushAction extends AbstractAction {

        public PushAction() {
            putValue(NAME, "Push to");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (!project.isConnected()) {
                JOptionPane.showMessageDialog(null, "Not connected");
                return;
            }

            int option = JOptionPane.showConfirmDialog(null, "Push?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            if (option != JOptionPane.YES_OPTION) {
                return;
            }

            List<GitPushResult> gitPushResults = project.push();

            for (GitPushResult gitPushResult : gitPushResults) {
                JOptionPane.showMessageDialog(null, gitPushResult.getMessage());
            }
        }
    }
}
