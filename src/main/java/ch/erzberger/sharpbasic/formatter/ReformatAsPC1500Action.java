package ch.erzberger.sharpbasic.formatter;

import ch.erzberger.sharpbasic.SharpBasicFileType;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Action to reformat Sharp BASIC code using PC-1500 formatting rules.
 * Accessible via Code menu or context menu.
 */
public class ReformatAsPC1500Action extends AnAction {
    private static final Logger LOG = Logger.getInstance(ReformatAsPC1500Action.class);

    public ReformatAsPC1500Action() {
        super("Reformat as PC-1500 BASIC");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        LOG.info("ReformatAsPC1500Action triggered");

        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        if (project == null || editor == null || psiFile == null) {
            LOG.warn("Missing required data: project=" + project + ", editor=" + editor + ", psiFile=" + psiFile);
            showNotification(project, "Cannot reformat: missing editor or file", NotificationType.ERROR);
            return;
        }

        try {
            Document document = editor.getDocument();
            String currentText = document.getText();
            LOG.info("Current text length: " + currentText.length());

            // Reformat using PC-1500 rules
            String reformattedText = SharpBasicCodeReformatter.reformat(currentText);
            LOG.info("Reformatted text length: " + reformattedText.length());

            // Convert CR line endings back to the system default for display
            // (IntelliJ will handle line ending conversion when saving)
            final String finalReformattedText = reformattedText.replace("\r", "\n");

            // Apply the changes in a write action
            WriteCommandAction.runWriteCommandAction(project, "Reformat as PC-1500 BASIC", null, () -> {
                document.setText(finalReformattedText);
                PsiDocumentManager.getInstance(project).commitDocument(document);
            });

            showNotification(project, "Code reformatted as PC-1500 BASIC", NotificationType.INFORMATION);
            LOG.info("Reformatting completed successfully");

        } catch (Exception ex) {
            LOG.error("Error during reformatting", ex);
            showNotification(project, "Error during reformatting: " + ex.getMessage(), NotificationType.ERROR);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // Enable only for Sharp BASIC files
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        boolean enabled = psiFile != null && psiFile.getFileType() == SharpBasicFileType.INSTANCE;
        LOG.info("Action update: enabled=" + enabled + ", fileType=" + (psiFile != null ? psiFile.getFileType() : "null"));
        e.getPresentation().setEnabledAndVisible(enabled);
    }

    private void showNotification(Project project, String message, NotificationType type) {
        if (project != null) {
            Notification notification = new Notification(
                "Sharp BASIC",
                "PC-1500 Formatter",
                message,
                type
            );
            Notifications.Bus.notify(notification, project);
        }
    }
}
