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
import java.sql.Timestamp;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import bgit.model.GitTag;

@SuppressWarnings("serial")
public class TagViewerDialog extends AbstractDialog {

    private final GitTag gitTag;

    private final JTextField tagNameTextField;

    private final JTextField taggerTextField;

    private final JTextField dateTextField;

    private final JTextArea messageTextArea;

    public TagViewerDialog(GitTag gitTag) {
        this.gitTag = gitTag;

        setTitle("Tag Viewer");
        setSize(new Dimension(500, 300));
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
        GridBagLayout gbl_tagPanel = new GridBagLayout();
        gbl_tagPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_tagPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
        gbl_tagPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gbl_tagPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0,
                Double.MIN_VALUE };
        tagPanel.setLayout(gbl_tagPanel);

        JLabel tagNameLabel = new JLabel("Tag name");
        GridBagConstraints gbc_tagNameLabel = new GridBagConstraints();
        gbc_tagNameLabel.anchor = GridBagConstraints.EAST;
        gbc_tagNameLabel.insets = new Insets(0, 0, 5, 5);
        gbc_tagNameLabel.gridx = 0;
        gbc_tagNameLabel.gridy = 0;
        tagPanel.add(tagNameLabel, gbc_tagNameLabel);

        tagNameTextField = new JTextField();
        tagNameTextField.setEditable(false);
        GridBagConstraints gbc_tagNameTextField = new GridBagConstraints();
        gbc_tagNameTextField.insets = new Insets(0, 0, 5, 0);
        gbc_tagNameTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_tagNameTextField.gridx = 1;
        gbc_tagNameTextField.gridy = 0;
        tagPanel.add(tagNameTextField, gbc_tagNameTextField);
        tagNameTextField.setColumns(10);

        JLabel lblTagger = new JLabel("Tagger");
        GridBagConstraints gbc_lblTagger = new GridBagConstraints();
        gbc_lblTagger.anchor = GridBagConstraints.EAST;
        gbc_lblTagger.insets = new Insets(0, 0, 5, 5);
        gbc_lblTagger.gridx = 0;
        gbc_lblTagger.gridy = 1;
        tagPanel.add(lblTagger, gbc_lblTagger);

        taggerTextField = new JTextField();
        taggerTextField.setEditable(false);
        GridBagConstraints gbc_taggerTextField = new GridBagConstraints();
        gbc_taggerTextField.insets = new Insets(0, 0, 5, 0);
        gbc_taggerTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_taggerTextField.gridx = 1;
        gbc_taggerTextField.gridy = 1;
        tagPanel.add(taggerTextField, gbc_taggerTextField);
        taggerTextField.setColumns(10);

        JLabel lblDate = new JLabel("Date");
        GridBagConstraints gbc_lblDate = new GridBagConstraints();
        gbc_lblDate.anchor = GridBagConstraints.EAST;
        gbc_lblDate.insets = new Insets(0, 0, 5, 5);
        gbc_lblDate.gridx = 0;
        gbc_lblDate.gridy = 2;
        tagPanel.add(lblDate, gbc_lblDate);

        dateTextField = new JTextField();
        dateTextField.setEditable(false);
        GridBagConstraints gbc_dateTextField = new GridBagConstraints();
        gbc_dateTextField.insets = new Insets(0, 0, 5, 0);
        gbc_dateTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_dateTextField.gridx = 1;
        gbc_dateTextField.gridy = 2;
        tagPanel.add(dateTextField, gbc_dateTextField);
        dateTextField.setColumns(10);

        JLabel messageLabel = new JLabel("Message");
        GridBagConstraints gbc_messageLabel = new GridBagConstraints();
        gbc_messageLabel.anchor = GridBagConstraints.EAST;
        gbc_messageLabel.insets = new Insets(0, 0, 0, 5);
        gbc_messageLabel.gridx = 0;
        gbc_messageLabel.gridy = 3;
        tagPanel.add(messageLabel, gbc_messageLabel);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 1;
        gbc_scrollPane.gridy = 3;
        tagPanel.add(scrollPane, gbc_scrollPane);

        messageTextArea = new JTextArea();
        messageTextArea.setEditable(false);
        scrollPane.setViewportView(messageTextArea);
    }

    private void handleWindowOpened() {
        tagNameTextField.setText(gitTag.getName());
        String tagger = gitTag.getTagger();

        if (tagger != null) {
            taggerTextField.setText(tagger);
        }

        Timestamp date = gitTag.getDateTagged();

        if (date != null) {
            dateTextField.setText(date.toString());
        }

        String message = gitTag.getFullMessage();

        if (message != null) {
            messageTextArea.setText(message);
        }
    }
}
