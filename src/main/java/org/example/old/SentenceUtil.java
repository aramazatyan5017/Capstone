package org.example.old;

import org.example.domain.Connective;
import org.example.old.DefiniteClauseView;
import org.example.old.OldSentence;

/**
 * @author aram.azatyan | 2/14/2024 12:06 PM
 */
// TODO: 2/14/2024 stex sentence - i validacia petq a lini
public class SentenceUtil {

    private static class Counter {
        int count;

        public Counter() {}

        public Counter(int count) {
            this.count = count;
        }

        public int get() {
            return count;
        }

        public int increment() {
            return ++count;
        }

        public int decrement() {
            return --count;
        }
    }

    public static DefiniteClauseView getDefiniteClauseView(OldSentence oldSentence) {
        if (oldSentence == null) throw new IllegalArgumentException("null sentence");
        if (oldSentence.isBasicSentence()) return new DefiniteClauseView(true, Connective.IMPLICATION);

        return switch (oldSentence.getConnective()) {
            case OR -> {
                var counter = new Counter();
                var result = isOnlyOnePositiveWithOr(oldSentence, counter);
                if (counter.get() != 1) yield new DefiniteClauseView(false, null);
                yield new DefiniteClauseView(result, result ? Connective.OR : null);
            }
            case IMPLICATION -> {
                if (oldSentence.getRhs().isBasicSentence()) {
                    yield isAllPositiveWithAnd(oldSentence.getLhs())
                            ? new DefiniteClauseView(true, Connective.IMPLICATION)
                            : new DefiniteClauseView(false, null);
                }
                yield new DefiniteClauseView(false, null);
            }
            default -> new DefiniteClauseView(false, null);
        };
    }

    private static boolean isOnlyOnePositiveWithOr(OldSentence oldSentence, Counter positiveLiteralCount) {
        if (oldSentence.isBasicSentence()) {
            if (!oldSentence.isNegated()) {
                if (positiveLiteralCount.get() > 0) return false;
                else {
                    positiveLiteralCount.increment();
                    return true;
                }
            }
            return true;
        } else {
            if (oldSentence.getConnective() != Connective.OR) return false;
            return isOnlyOnePositiveWithOr(oldSentence.getLhs(), positiveLiteralCount) &&
                    isOnlyOnePositiveWithOr(oldSentence.getRhs(), positiveLiteralCount);
        }
    }

    private static boolean isAllPositiveWithAnd(OldSentence oldSentence) {
        if (oldSentence.isBasicSentence()) return !oldSentence.isNegated();
        else {
            if (oldSentence.getConnective() != Connective.AND) return false;
            return isAllPositiveWithAnd(oldSentence.getLhs()) && isAllPositiveWithAnd(oldSentence.getRhs());
        }
    }
}
