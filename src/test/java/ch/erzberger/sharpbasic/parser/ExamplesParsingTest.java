package ch.erzberger.sharpbasic.parser;

import com.intellij.testFramework.ParsingTestCase;
import ch.erzberger.sharpbasic.SharpBasicLanguage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
            String content = Files.readString(file.toPath());
            System.out.println("[DEBUG_LOG] Parsing " + file.getName());
            try {
                // ParsingTestCase expect a file named like the test method, 
                // but we can use createPsiFile
                createPsiFile(file.getName(), content);
                // ensure no error elements
                ensureNoErrorElements();
                System.out.println("[DEBUG_LOG] Successfully parsed " + file.getName());
            } catch (Throwable e) {
                System.out.println("[DEBUG_LOG] Failed to parse " + file.getName() + ": " + e.getMessage());
                throw e;
            }
        }
    }
}
