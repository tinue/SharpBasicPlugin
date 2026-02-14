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
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Action to reformat Sharp BASIC code using compact formatting.
 * Minimizes code size by removing spaces and comments, using abbreviations.
 */
public class ReformatAsCompactPC1500Action extends AnAction {
    private static final Logger LOG = Logger.getInstance(ReformatAsCompactPC1500Action.class);

    public ReformatAsCompactPC1500Action() {
        super("Reformat as Compact PC-1500 BASIC");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        LOG.info("ReformatAsCompactPC1500Action triggered");

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

            // Reformat using compact rules
            String reformattedText = SharpBasicCompactReformatter.reformat(currentText);
            LOG.info("Reformatted text length: " + reformattedText.length());

            final String finalReformattedText = reformattedText;

            // ALWAYS create a scratch file for compact code to preserve the original clean source
            // Compact code is primarily for emulators and should not overwrite the readable source
            LOG.info("Creating scratch file with compact code (never overwrite original)");

            String fileName = psiFile.getName();
            String scratchFileName = fileName.replaceFirst("(\\.[^.]+)?$", "_compact$1");

            VirtualFile scratchFile = ScratchRootType.getInstance().createScratchFile(
                project,
                scratchFileName,
                SharpBasicFileType.INSTANCE.getLanguage(),
                finalReformattedText,
                ScratchFileService.Option.create_if_missing
            );

            if (scratchFile != null) {
                // Open the scratch file in the editor
                FileEditor[] editors = FileEditorManager.getInstance(project).openFile(scratchFile, true);

                // Set right margin at 79 characters to show PC-1500 input buffer limit
                if (editors.length > 0 && editors[0] instanceof TextEditor) {
                    Editor editor1 = ((TextEditor) editors[0]).getEditor();
                    if (editor1 instanceof EditorEx) {
                        EditorEx editorEx = (EditorEx) editor1;
                        // Set right margin at 79 characters (PC-1500 input buffer limit)
                        editorEx.getSettings().setRightMargin(79);
                        editorEx.getSettings().setRightMarginShown(true);
                        LOG.info("Set right margin at 79 characters for compact scratch file");
                    }
                }

                showNotification(project,
                    "Compact code opened in scratch file: " + scratchFileName + " (79-char margin shown)",
                    NotificationType.INFORMATION);
            } else {
                showNotification(project,
                    "Failed to create scratch file",
                    NotificationType.ERROR);
            }

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
                "PC-1500 Compact Formatter",
                message,
                type
            );
            Notifications.Bus.notify(notification, project);
        }
    }
}
