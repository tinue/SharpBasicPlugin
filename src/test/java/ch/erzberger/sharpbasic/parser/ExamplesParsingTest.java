package ch.erzberger.sharpbasic.parser;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.ParsingTestCase;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import org.junit.Test;

public class ExamplesParsingTest extends ParsingTestCase {
    public ExamplesParsingTest() {
        super("", "bas", new SharpBasicParserDefinition());
    }

    @Override
    protected String getTestDataPath() {
        return "examples";
    }

    @Override
    protected boolean skipSpaces() {
        return true;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }

    public void testAllExamples() throws IOException {
        File examplesDir = new File(getTestDataPath());
        File[] files = examplesDir.listFiles((dir, name) -> name.endsWith(".bas"));
        if (files == null) return;

        for (File file : files) {
            String content = Files.readString(file.toPath(), StandardCharsets.ISO_8859_1);
            System.out.println("[DEBUG_LOG] Parsing " + file.getName());
            try {
                PsiFile psiFile = createPsiFile(file.getName(), content);
                Collection<PsiErrorElement> errors = PsiTreeUtil.findChildrenOfType(psiFile, PsiErrorElement.class);
                if (!errors.isEmpty()) {
                    PsiErrorElement first = errors.iterator().next();
                    throw new AssertionError("Parse error in " + file.getName() + ": " + first.getErrorDescription());
                }
                System.out.println("[DEBUG_LOG] Successfully parsed " + file.getName());
            } catch (Throwable e) {
                System.out.println("[DEBUG_LOG] Failed to parse " + file.getName() + ": " + e.getMessage());
                throw e;
            }
        }
    }
}
