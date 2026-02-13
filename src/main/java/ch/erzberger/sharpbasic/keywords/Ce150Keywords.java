package ch.erzberger.sharpbasic.keywords;

import java.util.ArrayList;
import java.util.List;

import static ch.erzberger.sharpbasic.keywords.KeywordCategory.CE150_EXTENSION;
import static ch.erzberger.sharpbasic.keywords.KeywordType.*;

/**
 * CE-150 graphics and printer extension keywords (14 keywords).
 */
public class Ce150Keywords {
    private Ce150Keywords() {
        // Prevent instantiation
    }

    public static List<BasicKeyword> getKeywords() {
        List<BasicKeyword> keywords = new ArrayList<>();

        // Graphics and printer statements
        keywords.add(new BasicKeyword("COLOR", "COL", 0xF0B5, CE150_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("CSIZE", "CSI", 0xE680, CE150_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("GCURSOR", "GCU", 0xF093, CE150_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("GLCURSOR", "GL", 0xE682, CE150_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("GPRINT", "GP", 0xF09F, CE150_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("GRAPH", "GRAP", 0xE681, CE150_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("LCURSOR", "LCU", 0xE683, CE150_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("LINE", "LIN", 0xF0B7, CE150_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("LLIST", "LL", 0xF0B8, CE150_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("LPRINT", "LP", 0xF0B9, CE150_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("RLINE", "RL", 0xF0BA, CE150_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("ROTATE", "RO", 0xE685, CE150_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("SORGN", "SO", 0xE684, CE150_EXTENSION, STATEMENT));
        keywords.add(new BasicKeyword("TEXT", "TEX", 0xE686, CE150_EXTENSION, STATEMENT));

        return keywords;
    }
}
