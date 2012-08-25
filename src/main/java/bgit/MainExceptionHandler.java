package bgit;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import bgit.model.Application;
import bgit.view.MessageDialog;

public class MainExceptionHandler implements UncaughtExceptionHandler {

    private final Application application;

    public MainExceptionHandler(Application application) {
        this.application = application;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        handle(e);
    }

    public void handle(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        try {
            e.printStackTrace(printWriter);

        } finally {
            printWriter.flush();
        }

        MessageDialog messageDialog = new MessageDialog(application,
                stringWriter.toString());
        messageDialog.setVisible(true);
    }
}