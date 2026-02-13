package ch.erzberger.sharpbasic.keywords;

import java.util.ArrayList;
import java.util.List;

import static ch.erzberger.sharpbasic.keywords.KeywordCategory.PC1500_CORE;
import static ch.erzberger.sharpbasic.keywords.KeywordType.*;

/**
 * PC-1500 core keywords (97 keywords).
 */
public class SharpPc1500Keywords {
    private SharpPc1500Keywords() {
        // Prevent instantiation
    }

    public static List<BasicKeyword> getKeywords() {
        List<BasicKeyword> keywords = new ArrayList<>();

        // Functions
        keywords.add(new BasicKeyword("ABS", "AB", 0xF170, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("ACS", "AC", 0xF174, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("ASC", 0xF160, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("ASN", "AS", 0xF173, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("ATN", "AT", 0xF175, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("CHR$", "CH", 0xF163, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("COS", 0xF17E, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("DEG", 0xF165, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("DMS", "DM", 0xF166, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("ERL", 0xF053, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("ERN", 0xF052, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("EXP", "EX", 0xF178, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("INKEY$", "INK", 0xF15C, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("INT", 0xF171, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("LEFT$", "LEF", 0xF17A, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("LEN", "LEN", 0xF164, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("LN", "LN", 0xF176, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("LOG", "LO", 0xF177, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("MEM", "M", 0xF158, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("MID$", "MI", 0xF17B, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("PEEK", 0xF16F, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("PEEK#", "PE", 0xF16E, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("PI", 0xF15D, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("POINT", "POI", 0xF168, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("RIGHT$", "RI", 0xF172, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("RND", "RN", 0xF17C, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("SGN", "SG", 0xF179, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("SIN", "SI", 0xF17D, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("SPACE$", 0xF061, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("SQR", "SQ", 0xF16B, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("STATUS", "STA", 0xF167, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("STR$", "STR", 0xF161, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("TAN", "TA", 0xF17F, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("TIME", "TI", 0xF15B, PC1500_CORE, FUNCTION));
        keywords.add(new BasicKeyword("VAL", "V", 0xF162, PC1500_CORE, FUNCTION));

        // Statements
        keywords.add(new BasicKeyword("AREAD", "A", 0xF180, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("ARUN", "ARU", 0xF181, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("BEEP", "B", 0xF182, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("BREAK", 0xF0B3, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("CALL", "CA", 0xF18A, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("CHAIN", "CHA", 0xF0B2, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("CLEAR", "CL", 0xF187, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("CLOAD", "CLO", 0xF089, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("CLS", 0xF088, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("CONSOLE", 0xF0B1, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("CONT", "C", 0xF183, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("CSAVE", "CS", 0xF095, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("CURSOR", "CU", 0xF084, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("DATA", "DA", 0xF18D, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("DEGREE", "DE", 0xF18C, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("DIM", "D", 0xF18B, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("END", "E", 0xF18E, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("ERROR", "ER", 0xF1B4, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("FEED", 0xF0B0, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("FOR", "F", 0xF1A5, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("GOSUB", "GOS", 0xF194, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("GOTO", "G", 0xF192, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("GRAD", "GR", 0xF186, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("IF", 0xF196, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("INPUT", "I", 0xF091, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("LET", "LE", 0xF198, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("LF", "LF", 0xF0B6, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("LIST", "L", 0xF090, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("LOCK", "LOC", 0xF1B5, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("MERGE", "MER", 0xF08F, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("NEW", 0xF19B, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("NEXT", "N", 0xF19A, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("OFF", 0xF19E, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("ON", "O", 0xF19C, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("OPN", 0xF19D, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("PAUSE", "PA", 0xF1A2, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("POKE", 0xF1A1, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("POKE#", "PO", 0xF1A0, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("PRINT", "P", 0xF097, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("RADIAN", "RAD", 0xF1AA, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("RANDOM", "RA", 0xF1A8, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("READ", "REA", 0xF1A6, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("REM", 0xF1AB, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("RESTORE", "RES", 0xF1A7, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("RETURN", "RE", 0xF199, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("RUN", "R", 0xF1A4, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("STOP", "S", 0xF1AC, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("TAB", 0xF0BB, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("TROFF", "TROF", 0xF1B0, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("TRON", "TR", 0xF1AF, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("UNLOCK", "UN", 0xF1B6, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("USING", "U", 0xF085, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("WAIT", "W", 0xF1B3, PC1500_CORE, STATEMENT));
        keywords.add(new BasicKeyword("ZONE", 0xF0B4, PC1500_CORE, STATEMENT));

        // Operators
        keywords.add(new BasicKeyword("AND", "AN", 0xF150, PC1500_CORE, OPERATOR));
        keywords.add(new BasicKeyword("NOT", "NO", 0xF16D, PC1500_CORE, OPERATOR));
        keywords.add(new BasicKeyword("OR", 0xF151, PC1500_CORE, OPERATOR));

        // Other keywords
        keywords.add(new BasicKeyword("STEP", "STE", 0xF1AD, PC1500_CORE, KEYWORD));
        keywords.add(new BasicKeyword("THEN", "T", 0xF1AE, PC1500_CORE, KEYWORD));
        keywords.add(new BasicKeyword("TO", 0xF1B1, PC1500_CORE, KEYWORD));

        return keywords;
    }
}
