package ch.erzberger.sharpbasic.completion;

import ch.erzberger.sharpbasic.SharpBasicLanguage;
import ch.erzberger.sharpbasic.keywords.BasicKeyword;
import ch.erzberger.sharpbasic.keywords.KeywordCategory;
import ch.erzberger.sharpbasic.keywords.KeywordRegistry;
import ch.erzberger.sharpbasic.keywords.KeywordType;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Provides code completion for Sharp BASIC keywords.
 */
public class SharpBasicCompletionContributor extends CompletionContributor {
    public SharpBasicCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(SharpBasicLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                   @NotNull ProcessingContext context,
                                                   @NotNull CompletionResultSet result) {
                        addKeywordCompletions(result);
                    }
                });
    }

    private void addKeywordCompletions(@NotNull CompletionResultSet result) {
        List<BasicKeyword> allKeywords = KeywordRegistry.getAllKeywords();

        for (BasicKeyword keyword : allKeywords) {
            // Create lookup element with full keyword name
            LookupElementBuilder element = LookupElementBuilder.create(keyword.getName())
                    .withCaseSensitivity(false)
                    .withBoldness(true);

            // Add abbreviation as tail text if different from full name
            String abbrev = keyword.getAbbreviation();
            if (!abbrev.equals(keyword.getName())) {
                element = element.withTailText(" (" + abbrev + ")", true);
            }

            // Add category as type text
            String categoryText = getCategoryDisplayText(keyword.getCategory());
            element = element.withTypeText(categoryText, true);

            // Set priority based on category (PC-1500 core first)
            double priority = 0.0;
            if (keyword.getCategory() == KeywordCategory.PC1500_CORE) {
                priority = 100.0;
            } else if (keyword.getCategory() == KeywordCategory.CE150_EXTENSION) {
                priority = 50.0;
            } else {
                priority = 25.0;
            }

            result.addElement(PrioritizedLookupElement.withPriority(element, priority));

            // Also add abbreviation as a separate completion if it's different
            if (!abbrev.equals(keyword.getName()) && !abbrev.endsWith(".")) {
                String rawAbbrev = keyword.getRawAbbreviation();
                LookupElementBuilder abbrevElement = LookupElementBuilder.create(rawAbbrev + ".")
                        .withCaseSensitivity(false)
                        .withPresentableText(abbrev)
                        .withTailText(" → " + keyword.getName(), true)
                        .withTypeText(categoryText, true);

                double abbrevPriority = 0.0;
                if (keyword.getCategory() == KeywordCategory.PC1500_CORE) {
                    abbrevPriority = 90.0;
                } else if (keyword.getCategory() == KeywordCategory.CE150_EXTENSION) {
                    abbrevPriority = 40.0;
                } else {
                    abbrevPriority = 20.0;
                }

                result.addElement(PrioritizedLookupElement.withPriority(abbrevElement, abbrevPriority));
            }
        }
    }

    private String getCategoryDisplayText(KeywordCategory category) {
        switch (category) {
            case PC1500_CORE:
                return "[PC-1500]";
            case CE150_EXTENSION:
                return "[CE-150]";
            case CE158_EXTENSION:
                return "[CE-158]";
            default:
                return "";
        }
    }
}
