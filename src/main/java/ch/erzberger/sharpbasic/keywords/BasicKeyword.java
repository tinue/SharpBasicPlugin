package ch.erzberger.sharpbasic.keywords;

/**
 * Represents a Sharp BASIC keyword with its name, abbreviation, category, type, and token code.
 */
public class BasicKeyword {
    private final String name;
    private final String abbreviation;
    private final int code;
    private final KeywordCategory category;
    private final KeywordType type;

    public BasicKeyword(String name, String abbreviation, int code, KeywordCategory category, KeywordType type) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.code = code;
        this.category = category;
        this.type = type;
    }

    public BasicKeyword(String name, int code, KeywordCategory category, KeywordType type) {
        this(name, name, code, category, type);
    }

    public String getName() {
        return name;
    }

    public String getRawAbbreviation() {
        return abbreviation;
    }

    /**
     * Returns the abbreviation with a dot suffix if it's actually shorter than the full name.
     * Otherwise returns the full name.
     */
    public String getAbbreviation() {
        String candidate = abbreviation + ".";
        // If the abbreviation plus the dot is not actually shorter, then return the original.
        if (candidate.length() >= name.length()) {
            return name;
        } else {
            return candidate;
        }
    }

    public int getCode() {
        return code;
    }

    public KeywordCategory getCategory() {
        return category;
    }

    public KeywordType getType() {
        return type;
    }

    @Override
    public String toString() {
        return name + " (" + getAbbreviation() + ")";
    }
}
