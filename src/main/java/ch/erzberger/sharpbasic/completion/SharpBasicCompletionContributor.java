package ch.erzberger.sharpbasic.completion;

import ch.erzberger.sharpbasic.SharpBasicLanguage;
import ch.erzberger.sharpbasic.core.keyword.BasicKeyword;
import ch.erzberger.sharpbasic.core.keyword.KeywordCategory;
import ch.erzberger.sharpbasic.core.keyword.KeywordRegistry;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

/**
 * Provides code completion for Sharp BASIC keywords.
 */
public class SharpBasicCompletionContributor extends CompletionContributor {

    private static final KeywordRegistry REGISTRY = KeywordRegistry.forDevice(
            EnumSet.allOf(KeywordCategory.class));

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
        List<BasicKeyword> allKeywords = REGISTRY.allKeywords();

        for (BasicKeyword keyword : allKeywords) {
            LookupElementBuilder element = LookupElementBuilder.create(keyword.name())
                    .withCaseSensitivity(false)
                    .withBoldness(true);

            String abbrev = keyword.getAbbreviatedForm();
            if (!abbrev.equals(keyword.name())) {
                element = element.withTailText(" (" + abbrev + ")", true);
            }

            String categoryText = getCategoryDisplayText(keyword.category());
            element = element.withTypeText(categoryText, true);

            double priority = getPriority(keyword.category());
            result.addElement(PrioritizedLookupElement.withPriority(element, priority));

            // Also add abbreviation as a separate completion if it's different
            if (keyword.hasAbbreviation()) {
                String rawAbbrev = keyword.abbreviation();
                LookupElementBuilder abbrevElement = LookupElementBuilder.create(rawAbbrev + ".")
                        .withCaseSensitivity(false)
                        .withPresentableText(abbrev)
                        .withTailText(" \u2192 " + keyword.name(), true)
                        .withTypeText(categoryText, true);

                result.addElement(PrioritizedLookupElement.withPriority(abbrevElement, priority - 10.0));
            }
        }
    }

    private double getPriority(KeywordCategory category) {
        return switch (category) {
            case PC1500 -> 100.0;
            case CE150_EXTENSION -> 50.0;
            case CE158_EXTENSION -> 25.0;
            case PC1600 -> 75.0;
        };
    }

    private String getCategoryDisplayText(KeywordCategory category) {
        return switch (category) {
            case PC1500 -> "[PC-1500]";
            case CE150_EXTENSION -> "[CE-150]";
            case CE158_EXTENSION -> "[CE-158]";
            case PC1600 -> "[PC-1600]";
        };
    }
}
