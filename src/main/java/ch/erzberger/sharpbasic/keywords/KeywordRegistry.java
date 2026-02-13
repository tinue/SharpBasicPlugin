package ch.erzberger.sharpbasic.keywords;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Central registry for all Sharp BASIC keywords (122 total).
 * Provides O(1) lookup by name or abbreviation (case-insensitive).
 */
public class KeywordRegistry {
    private static final Map<String, BasicKeyword> KEYWORD_MAP = new HashMap<>();
    private static final List<BasicKeyword> ALL_KEYWORDS = new ArrayList<>();

    static {
        // Load all keywords from the three categories
        List<BasicKeyword> pc1500 = SharpPc1500Keywords.getKeywords();
        List<BasicKeyword> ce150 = Ce150Keywords.getKeywords();
        List<BasicKeyword> ce158 = Ce158Keywords.getKeywords();

        ALL_KEYWORDS.addAll(pc1500);
        ALL_KEYWORDS.addAll(ce150);
        ALL_KEYWORDS.addAll(ce158);

        // Register all keywords by full name and abbreviation (uppercase for case-insensitive lookup)
        for (BasicKeyword keyword : ALL_KEYWORDS) {
            // Register full name
            KEYWORD_MAP.put(keyword.getName().toUpperCase(), keyword);

            // Register abbreviation if different from full name
            String rawAbbrev = keyword.getRawAbbreviation();
            if (!rawAbbrev.equals(keyword.getName())) {
                // Only register abbreviation WITH dot suffix to distinguish from variables
                // E.g., "R." is RUN keyword, but "R" is a variable
                KEYWORD_MAP.put((rawAbbrev + ".").toUpperCase(), keyword);

                // Multi-character abbreviations (2+ chars) can also be recognized without dot
                // E.g., "PR" for PRINT, "GOS" for GOSUB
                if (rawAbbrev.length() >= 2) {
                    KEYWORD_MAP.put(rawAbbrev.toUpperCase(), keyword);
                }
            }
        }
    }

    private KeywordRegistry() {
        // Prevent instantiation
    }

    /**
     * Look up a keyword by name or abbreviation (case-insensitive).
     * @param text The keyword text to look up
     * @return The keyword if found, null otherwise
     */
    public static BasicKeyword lookup(String text) {
        if (text == null) {
            return null;
        }
        return KEYWORD_MAP.get(text.toUpperCase());
    }

    /**
     * Check if a text is a keyword (case-insensitive).
     * @param text The text to check
     * @return true if the text is a keyword
     */
    public static boolean isKeyword(String text) {
        return lookup(text) != null;
    }

    /**
     * Get all keywords.
     * @return Unmodifiable list of all keywords
     */
    public static List<BasicKeyword> getAllKeywords() {
        return Collections.unmodifiableList(ALL_KEYWORDS);
    }

    /**
     * Get keywords by category.
     * @param category The category to filter by
     * @return List of keywords in the specified category
     */
    public static List<BasicKeyword> getByCategory(KeywordCategory category) {
        return ALL_KEYWORDS.stream()
                .filter(k -> k.getCategory() == category)
                .collect(Collectors.toList());
    }

    /**
     * Get keywords by type.
     * @param type The type to filter by
     * @return List of keywords of the specified type
     */
    public static List<BasicKeyword> getByType(KeywordType type) {
        return ALL_KEYWORDS.stream()
                .filter(k -> k.getType() == type)
                .collect(Collectors.toList());
    }

    /**
     * Get the total number of keywords.
     * @return Total keyword count
     */
    public static int getKeywordCount() {
        return ALL_KEYWORDS.size();
    }
}
