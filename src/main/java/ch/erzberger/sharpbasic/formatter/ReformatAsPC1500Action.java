package ch.erzberger.sharpbasic.formatter;

import ch.erzberger.sharpbasic.SharpBasicFileType;
import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.ide.scratch.ScratchRootType;
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
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
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
            VirtualFile virtualFile = psiFile.getVirtualFile();
            String currentText = document.getText();
            LOG.info("Current text length: " + currentText.length());

            // Reformat using PC-1500 rules
            String reformattedText = SharpBasicCodeReformatter.reformat(currentText);
            LOG.info("Reformatted text length: " + reformattedText.length());

            // Convert CR line endings back to the system default for display
            // (IntelliJ will handle line ending conversion when saving)
            final String finalReformattedText = reformattedText.replace("\r", "\n");

            // Check if file is read-only (check VirtualFile, not Document)
            if (virtualFile != null && !virtualFile.isWritable()) {
                LOG.info("Document is read-only, creating scratch file with reformatted content");

                // Create a scratch file with the reformatted content
                String fileName = psiFile.getName();
                String scratchFileName = fileName.replaceFirst("(\\.[^.]+)?$", "_formatted$1");

                VirtualFile scratchFile = ScratchRootType.getInstance().createScratchFile(
                    project,
                    scratchFileName,
                    SharpBasicFileType.INSTANCE.getLanguage(),
                    finalReformattedText,
                    ScratchFileService.Option.create_if_missing
                );

                if (scratchFile != null) {
                    // Open the scratch file in the editor
                    FileEditorManager.getInstance(project).openFile(scratchFile, true);
                    showNotification(project,
                        "File is read-only. Reformatted code opened in scratch file: " + scratchFileName,
                        NotificationType.INFORMATION);
                } else {
                    showNotification(project,
                        "Failed to create scratch file",
                        NotificationType.ERROR);
                }
            } else {
                // Apply the changes in a write action
                WriteCommandAction.runWriteCommandAction(project, "Reformat as PC-1500 BASIC", null, () -> {
                    document.setText(finalReformattedText);
                    PsiDocumentManager.getInstance(project).commitDocument(document);
                });

                showNotification(project, "Code reformatted as PC-1500 BASIC", NotificationType.INFORMATION);
                LOG.info("Reformatting completed successfully");
            }

        } catch (Exception ex) {
            LOG.error("Error during reformatting", ex);
            showNotification(project, "Error during reformatting: " + ex.getMessage(), NotificationType.ERROR);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // Enable only for Sharp BASIC files
        // Try multiple ways to get file type for compatibility with different IDE contexts
        boolean enabled = false;

        // Method 1: Try PSI file
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile != null && psiFile.getFileType() == SharpBasicFileType.INSTANCE) {
            enabled = true;
        }

        // Method 2: Try virtual file if PSI not available
        if (!enabled) {
            VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
            if (virtualFile != null) {
                String extension = virtualFile.getExtension();
                enabled = "bas".equalsIgnoreCase(extension) || "pc1500".equalsIgnoreCase(extension);
            }
        }

        LOG.info("Action update: enabled=" + enabled + ", psiFile=" + (psiFile != null) +
                 ", fileType=" + (psiFile != null ? psiFile.getFileType() : "null"));
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
