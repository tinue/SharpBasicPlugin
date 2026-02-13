package ch.erzberger.sharpbasic.keywords;

/**
 * Types of Sharp BASIC keywords based on their function.
 */
public enum KeywordType {
    /** Statement keywords (PRINT, GOTO, FOR, etc.) */
    STATEMENT,

    /** Function keywords (ABS, SIN, CHR$, etc.) */
    FUNCTION,

    /** Operator keywords (AND, OR, NOT) */
    OPERATOR,

    /** Other keywords (THEN, TO, STEP, etc.) */
    KEYWORD
}
