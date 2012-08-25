package bgit.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import bgit.model.Application;

@SuppressWarnings("serial")
public class MessageDialog extends AbstractDialog {

    public MessageDialog(Application application, String message) {
        super(application);

        setTitle("Message");
        setSize(new Dimension(600, 400));
        bindWindowSettings();

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

        JScrollPane scrollPane = new JScrollPane();
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JTextArea messageTextArea = new JTextArea(message);
        messageTextArea.setEditable(false);
        scrollPane.setViewportView(messageTextArea);
    }
}
