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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import bgit.model.GitTag;
import bgit.model.Project;

@SuppressWarnings("serial")
public class TagEditorDialog extends AbstractDialog {

    private final Project project;

    private GitTag gitTag;

    private final JTextField tagNameTextField;

    private final JTextArea messageTextArea;

    public TagEditorDialog(Project project) {
        this.project = project;

        setTitle("Tag Editor");
        setSize(new Dimension(500, 200));
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

        JPanel tagPanel = new JPanel();
        getContentPane().add(tagPanel, BorderLayout.CENTER);
        GridBagLayout gbl_tagPanel = new GridBagLayout();
        gbl_tagPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_tagPanel.rowHeights = new int[] { 0, 0, 0 };
        gbl_tagPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gbl_tagPanel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        tagPanel.setLayout(gbl_tagPanel);

        JLabel tagNameLabel = new JLabel("Tag name");
        GridBagConstraints gbc_tagNameLabel = new GridBagConstraints();
        gbc_tagNameLabel.anchor = GridBagConstraints.EAST;
        gbc_tagNameLabel.insets = new Insets(0, 0, 5, 5);
        gbc_tagNameLabel.gridx = 0;
        gbc_tagNameLabel.gridy = 0;
        tagPanel.add(tagNameLabel, gbc_tagNameLabel);

        tagNameTextField = new JTextField();
        GridBagConstraints gbc_tagNameTextField = new GridBagConstraints();
        gbc_tagNameTextField.insets = new Insets(0, 0, 5, 0);
        gbc_tagNameTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_tagNameTextField.gridx = 1;
        gbc_tagNameTextField.gridy = 0;
        tagPanel.add(tagNameTextField, gbc_tagNameTextField);
        tagNameTextField.setColumns(10);

        JLabel messageLabel = new JLabel("Message");
        GridBagConstraints gbc_messageLabel = new GridBagConstraints();
        gbc_messageLabel.anchor = GridBagConstraints.EAST;
        gbc_messageLabel.insets = new Insets(0, 0, 0, 5);
        gbc_messageLabel.gridx = 0;
        gbc_messageLabel.gridy = 1;
        tagPanel.add(messageLabel, gbc_messageLabel);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 1;
        gbc_scrollPane.gridy = 1;
        tagPanel.add(scrollPane, gbc_scrollPane);

        messageTextArea = new JTextArea();
        scrollPane.setViewportView(messageTextArea);
    }

    private void handleWindowOpened() {
    }

    private void handleOkActionPerformed() {
        String tagName = tagNameTextField.getText().trim();

        if (tagName.isEmpty()) {
            return;
        }

        String message = messageTextArea.getText().trim();
        gitTag = project.createTag(tagName, message);
        fireWindowClosing();
    }

    public GitTag getGitTag() {
        return gitTag;
    }
}
