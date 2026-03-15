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
 * Action to renumber Sharp BASIC line numbers using RENUM logic.
 * Accessible via Code menu or context menu.
 */
public class ReformatAsRenumberedAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(ReformatAsRenumberedAction.class);

    public ReformatAsRenumberedAction() {
        super("Renumber BASIC");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        if (project == null || editor == null || psiFile == null) {
            showNotification(project, "Cannot renumber: missing editor or file", NotificationType.ERROR);
            return;
        }

        try {
            Document document = editor.getDocument();
            VirtualFile virtualFile = psiFile.getVirtualFile();
            String currentText = document.getText();

            String renumberedText = SharpBasicRenumReformatter.reformat(currentText);
            final String finalText = renumberedText.replace("\r", "\n");

            if (virtualFile != null && !virtualFile.isWritable()) {
                String fileName = psiFile.getName();
                String scratchFileName = fileName.replaceFirst("(\\.[^.]+)?$", "_renumbered$1");

                VirtualFile scratchFile = ScratchRootType.getInstance().createScratchFile(
                        project,
                        scratchFileName,
                        SharpBasicFileType.INSTANCE.getLanguage(),
                        finalText,
                        ScratchFileService.Option.create_if_missing);

                if (scratchFile != null) {
                    FileEditorManager.getInstance(project).openFile(scratchFile, true);
                    showNotification(project,
                            "File is read-only. Renumbered code opened in scratch file: " + scratchFileName,
                            NotificationType.INFORMATION);
                } else {
                    showNotification(project, "Failed to create scratch file", NotificationType.ERROR);
                }
            } else {
                WriteCommandAction.runWriteCommandAction(project, "Renumber BASIC", null, () -> {
                    document.setText(finalText);
                    PsiDocumentManager.getInstance(project).commitDocument(document);
                });

                showNotification(project, "BASIC code renumbered", NotificationType.INFORMATION);
                LOG.info("Renumbering completed successfully");
            }

        } catch (Exception ex) {
            LOG.error("Error during renumbering", ex);
            showNotification(project, "Error during renumbering: " + ex.getMessage(), NotificationType.ERROR);
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
                    "PC-1500 Renumber",
                    message,
                    type);
            Notifications.Bus.notify(notification, project);
        }
    }
}
