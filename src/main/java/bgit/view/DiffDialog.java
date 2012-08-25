package bgit.view;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import bgit.JdkUtils;
import bgit.model.Application;
import bgit.model.GitCommit;
import bgit.model.GitDiffEntry;
import bgit.model.GitDiffResult;
import bgit.model.GitFile;
import bgit.model.Project;

@SuppressWarnings("serial")
public class DiffDialog extends AbstractDialog {

    private final Project project;

    private final String relativePathString;

    private final GitCommit oldGitCommit;

    private final GitCommit newGitCommit;

    private GitFile oldGitFile;

    private GitFile newGitFile;

    private final JTextField pathTextField;

    private final JTextField oldCommitTextField;

    private final JTextField oldFileTextField;

    private final JButton openOldGitFileButton;

    private final JTextField newCommitTextField;

    private final JTextField newFileTextField;

    private final JButton openNewGitFileButton;

    private final JTextArea diffTextArea;

    public DiffDialog(Application application, Project project,
            String relativePathString, GitCommit oldGitCommit,
            GitCommit newGitCommit) {
        super(application);
        this.project = project;
        this.relativePathString = relativePathString;
        this.oldGitCommit = oldGitCommit;
        this.newGitCommit = newGitCommit;

        setTitle("Diff");
        setSize(new Dimension(800, 500));
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
        footerPanel.add(closeButton);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.CENTER);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 0, 0, 0, 0 };
        gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
        gbl_panel.columnWeights = new double[] { 0.0, 1.0, 0.0,
                Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
                Double.MIN_VALUE };
        panel.setLayout(gbl_panel);

        JLabel lblPath = new JLabel("Path");
        GridBagConstraints gbc_lblPath = new GridBagConstraints();
        gbc_lblPath.anchor = GridBagConstraints.EAST;
        gbc_lblPath.insets = new Insets(0, 0, 5, 5);
        gbc_lblPath.gridx = 0;
        gbc_lblPath.gridy = 0;
        panel.add(lblPath, gbc_lblPath);

        pathTextField = new JTextField();
        pathTextField.setEditable(false);
        GridBagConstraints gbc_pathTextField = new GridBagConstraints();
        gbc_pathTextField.gridwidth = 2;
        gbc_pathTextField.insets = new Insets(0, 0, 5, 0);
        gbc_pathTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_pathTextField.gridx = 1;
        gbc_pathTextField.gridy = 0;
        panel.add(pathTextField, gbc_pathTextField);
        pathTextField.setColumns(10);

        JLabel lblOld = new JLabel("Old commit");
        GridBagConstraints gbc_lblOld = new GridBagConstraints();
        gbc_lblOld.anchor = GridBagConstraints.EAST;
        gbc_lblOld.insets = new Insets(0, 0, 5, 5);
        gbc_lblOld.gridx = 0;
        gbc_lblOld.gridy = 1;
        panel.add(lblOld, gbc_lblOld);

        oldCommitTextField = new JTextField();
        oldCommitTextField.setEditable(false);
        GridBagConstraints gbc_oldCommitTextField = new GridBagConstraints();
        gbc_oldCommitTextField.gridwidth = 2;
        gbc_oldCommitTextField.insets = new Insets(0, 0, 5, 0);
        gbc_oldCommitTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_oldCommitTextField.gridx = 1;
        gbc_oldCommitTextField.gridy = 1;
        panel.add(oldCommitTextField, gbc_oldCommitTextField);
        oldCommitTextField.setColumns(10);

        JLabel lblOldFile = new JLabel("Old file");
        GridBagConstraints gbc_lblOldFile = new GridBagConstraints();
        gbc_lblOldFile.anchor = GridBagConstraints.EAST;
        gbc_lblOldFile.insets = new Insets(0, 0, 5, 5);
        gbc_lblOldFile.gridx = 0;
        gbc_lblOldFile.gridy = 2;
        panel.add(lblOldFile, gbc_lblOldFile);

        oldFileTextField = new JTextField();
        oldFileTextField.setEditable(false);
        GridBagConstraints gbc_oldFileTextField = new GridBagConstraints();
        gbc_oldFileTextField.insets = new Insets(0, 0, 5, 5);
        gbc_oldFileTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_oldFileTextField.gridx = 1;
        gbc_oldFileTextField.gridy = 2;
        panel.add(oldFileTextField, gbc_oldFileTextField);
        oldFileTextField.setColumns(10);

        openOldGitFileButton = new JButton("Open");
        openOldGitFileButton.setEnabled(false);
        openOldGitFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleOpenOldGitFileActionPerformed();
            }
        });
        GridBagConstraints gbc_btnOpen = new GridBagConstraints();
        gbc_btnOpen.insets = new Insets(0, 0, 5, 0);
        gbc_btnOpen.gridx = 2;
        gbc_btnOpen.gridy = 2;
        panel.add(openOldGitFileButton, gbc_btnOpen);

        JLabel lblNew = new JLabel("New commit");
        GridBagConstraints gbc_lblNew = new GridBagConstraints();
        gbc_lblNew.anchor = GridBagConstraints.EAST;
        gbc_lblNew.insets = new Insets(0, 0, 5, 5);
        gbc_lblNew.gridx = 0;
        gbc_lblNew.gridy = 3;
        panel.add(lblNew, gbc_lblNew);

        newCommitTextField = new JTextField();
        newCommitTextField.setEditable(false);
        GridBagConstraints gbc_newCommitTextField = new GridBagConstraints();
        gbc_newCommitTextField.gridwidth = 2;
        gbc_newCommitTextField.insets = new Insets(0, 0, 5, 0);
        gbc_newCommitTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_newCommitTextField.gridx = 1;
        gbc_newCommitTextField.gridy = 3;
        panel.add(newCommitTextField, gbc_newCommitTextField);
        newCommitTextField.setColumns(10);

        JLabel lblNewFile = new JLabel("New file");
        GridBagConstraints gbc_lblNewFile = new GridBagConstraints();
        gbc_lblNewFile.anchor = GridBagConstraints.EAST;
        gbc_lblNewFile.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewFile.gridx = 0;
        gbc_lblNewFile.gridy = 4;
        panel.add(lblNewFile, gbc_lblNewFile);

        newFileTextField = new JTextField();
        newFileTextField.setEditable(false);
        GridBagConstraints gbc_newFileTextField = new GridBagConstraints();
        gbc_newFileTextField.insets = new Insets(0, 0, 5, 5);
        gbc_newFileTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_newFileTextField.gridx = 1;
        gbc_newFileTextField.gridy = 4;
        panel.add(newFileTextField, gbc_newFileTextField);
        newFileTextField.setColumns(10);

        openNewGitFileButton = new JButton("Open");
        openNewGitFileButton.setEnabled(false);
        openNewGitFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleOpenNewGitFileActionPerformed();
            }
        });
        GridBagConstraints gbc_btnOpen_1 = new GridBagConstraints();
        gbc_btnOpen_1.insets = new Insets(0, 0, 5, 0);
        gbc_btnOpen_1.gridx = 2;
        gbc_btnOpen_1.gridy = 4;
        panel.add(openNewGitFileButton, gbc_btnOpen_1);

        JLabel lblDiff = new JLabel("Diff");
        GridBagConstraints gbc_lblDiff = new GridBagConstraints();
        gbc_lblDiff.insets = new Insets(0, 0, 0, 5);
        gbc_lblDiff.gridx = 0;
        gbc_lblDiff.gridy = 5;
        panel.add(lblDiff, gbc_lblDiff);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.gridwidth = 2;
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 1;
        gbc_scrollPane.gridy = 5;
        panel.add(scrollPane, gbc_scrollPane);

        diffTextArea = new JTextArea();
        diffTextArea.setEditable(false);
        scrollPane.setViewportView(diffTextArea);
    }

    private void handleWindowOpened() {
        pathTextField.setText(relativePathString);

        if (oldGitCommit == null) {
            oldCommitTextField.setText("Empty");

        } else {
            oldCommitTextField.setText(oldGitCommit.getOneline());
        }

        if (newGitCommit == null) {
            newCommitTextField.setText("Working tree");

        } else {
            newCommitTextField.setText(newGitCommit.getOneline());
        }

        GitDiffResult gitDiffResult = project.diff(relativePathString,
                oldGitCommit, newGitCommit);
        Iterator<GitDiffEntry> it = gitDiffResult.getGitDiffEntries()
                .iterator();

        if (!it.hasNext()) {
            return;
        }

        GitDiffEntry gitDiffEntry = it.next();
        oldGitFile = gitDiffEntry.findOldGitFile();
        oldFileTextField.setText(oldGitFile.toString());

        if (oldGitFile.isTracked()) {
            openOldGitFileButton.setEnabled(true);
        }

        newGitFile = gitDiffEntry.findNewGitFile();
        newFileTextField.setText(newGitFile.toString());

        if (newGitFile.isTracked()) {
            openNewGitFileButton.setEnabled(true);
        }

        diffTextArea.setText(gitDiffResult.getText());
        diffTextArea.setCaretPosition(0);
    }

    private void handleOpenOldGitFileActionPerformed() {
        Desktop desktop = Desktop.getDesktop();
        JdkUtils.open(desktop, oldGitFile.createTemporaryFile());
    }

    private void handleOpenNewGitFileActionPerformed() {
        Desktop desktop = Desktop.getDesktop();
        JdkUtils.open(desktop, newGitFile.createTemporaryFile());
    }
}
