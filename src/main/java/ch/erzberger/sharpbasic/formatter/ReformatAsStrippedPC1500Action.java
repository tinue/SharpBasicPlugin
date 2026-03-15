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
 * Action to reformat Sharp BASIC code to PC-1500 canonical format with source comments stripped.
 * Suitable for device-bound output where // annotations must be removed.
 */
public class ReformatAsStrippedPC1500Action extends AnAction {
    private static final Logger LOG = Logger.getInstance(ReformatAsStrippedPC1500Action.class);

    public ReformatAsStrippedPC1500Action() {
        super("Reformat as PC-1500 BASIC (Strip Comments)");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        LOG.info("ReformatAsStrippedPC1500Action triggered");

        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        if (project == null || editor == null || psiFile == null) {
            showNotification(project, "Cannot reformat: missing editor or file", NotificationType.ERROR);
            return;
        }

        try {
            Document document = editor.getDocument();
            VirtualFile virtualFile = psiFile.getVirtualFile();
            String currentText = document.getText();

            String reformattedText = SharpBasicStrippedReformatter.reformat(currentText);
            final String finalReformattedText = reformattedText.replace("\r", "\n");

            if (virtualFile != null && !virtualFile.isWritable()) {
                String fileName = psiFile.getName();
                String scratchFileName = fileName.replaceFirst("(\\.[^.]+)?$", "_stripped$1");

                VirtualFile scratchFile = ScratchRootType.getInstance().createScratchFile(
                    project,
                    scratchFileName,
                    SharpBasicFileType.INSTANCE.getLanguage(),
                    finalReformattedText,
                    ScratchFileService.Option.create_if_missing
                );

                if (scratchFile != null) {
                    FileEditorManager.getInstance(project).openFile(scratchFile, true);
                    showNotification(project,
                        "File is read-only. Reformatted code opened in scratch file: " + scratchFileName,
                        NotificationType.INFORMATION);
                } else {
                    showNotification(project, "Failed to create scratch file", NotificationType.ERROR);
                }
            } else {
                WriteCommandAction.runWriteCommandAction(project, "Reformat as PC-1500 BASIC (Strip Comments)", null, () -> {
                    document.setText(finalReformattedText);
                    PsiDocumentManager.getInstance(project).commitDocument(document);
                });

                showNotification(project, "Code reformatted as PC-1500 BASIC (comments stripped)", NotificationType.INFORMATION);
            }

        } catch (Exception ex) {
            LOG.error("Error during reformatting", ex);
            showNotification(project, "Error during reformatting: " + ex.getMessage(), NotificationType.ERROR);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean enabled = false;

        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile != null && psiFile.getFileType() == SharpBasicFileType.INSTANCE) {
            enabled = true;
        }

        if (!enabled) {
            VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
            if (virtualFile != null) {
                String extension = virtualFile.getExtension();
                enabled = "bas".equalsIgnoreCase(extension) || "pc1500".equalsIgnoreCase(extension);
            }
        }

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
