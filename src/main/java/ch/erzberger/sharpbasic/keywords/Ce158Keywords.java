package ch.erzberger.sharpbasic.keywords;

import java.util.ArrayList;
import java.util.List;

import static ch.erzberger.sharpbasic.keywords.KeywordCategory.CE158_EXTENSION;
import static ch.erzberger.sharpbasic.keywords.KeywordType.*;

/**
 * CE-158 communications extension keywords (11 keywords).
 */
public class Ce158Keywords {
    private Ce158Keywords() {
        // Prevent instantiation
    }

    public static List<BasicKeyword> getKeywords() {
        List<BasicKeyword> keywords = new ArrayList<>();

        // Communication functions
        keywords.add(new BasicKeyword("COM$", 0xE858, CE158_EXTENSION, FUNCTION));
        keywords.add(new BasicKeyword("DEV$", 0xE857, CE158_EXTENSION, FUNCTION));
        keywords.add(new BasicKeyword("INSTAT", 0xE859, CE158_EXTENSION, FUNCTION));
        keywords.add(new BasicKeyword("OUTSTAT", 0xE880, CE158_EXTENSION, FUNCTION));
        keywords.add(new BasicKeyword("RINKEY$", 0xE85A, CE158_EXTENSION, FUNCTION));

        // Communication statements
        keywords.add(new BasicKeyword("DTE", 0xE884, CE158_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("PROTOCOL", 0xE881, CE158_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("RMT", "RM", 0xE7A9, CE158_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("SETCOM", 0xE882, CE158_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("SETDEV", 0xE886, CE158_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("TERMINAL", 0xE883, CE158_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("TRANSMIT", 0xE885, CE158_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("TEST", "TE", 0xF0BC, CE158_EXTENSION, STATEMENT));

        return keywords;
    }
}
