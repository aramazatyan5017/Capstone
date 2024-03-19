package org.example.domain.sentence;

import org.example.exception.TautologyException;
import org.example.exception.UnsatisfiableException;
import org.example.util.SentenceUtils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author aram.azatyan | 2/28/2024 5:20 PM
 */
public final class CNFSentence implements Sentence {
    private final LinkedHashSet<Clause> clauses;

    public CNFSentence(LinkedHashSet<Clause> clauses) {
        if (clauses == null || clauses.isEmpty()) throw new IllegalArgumentException("null param");
        clauses.remove(null);
        if (clauses.size() == 0) throw new IllegalArgumentException("null clause passed");
        this.clauses = clauses;
    }

    public CNFSentence(Clause... clauses) {
        if (clauses == null || clauses.length == 0) throw new IllegalArgumentException("null param");
        LinkedHashSet<Clause> clauseSet = Arrays.stream(clauses)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (clauseSet.size() == 0) throw new IllegalArgumentException("null clauses passed");
        this.clauses = clauseSet;
    }

    public CNFSentence(String expression) throws ParseException {
        CNFSentence cnf = Sentences.parseCNFExpression(expression);
        this.clauses = cnf.getClauses();
    }

    public boolean isCanonical() {
        if (size() == 1) {
            try {
                Sentences.optimizeClause(getClauseList().get(0));
            } catch (TautologyException e) {
                return false;
            }
            return true;
        }
        LinkedHashSet<String> clauseLiteralNames = null;
        for (Clause clause : clauses) {
            if (clauseLiteralNames == null) {
                try {
                    Sentences.optimizeClause(clause);
                } catch (TautologyException e) {
                    return false;
                }
                clauseLiteralNames = clause.getLiterals().stream()
                        .map(Literal::getName)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
                continue;
            }

            try {
                Sentences.optimizeClause(clause);
            } catch (TautologyException e) {
                return false;
            }

            LinkedHashSet<Literal> currentLiterals = clause.getLiterals();
            if (currentLiterals.size() != clauseLiteralNames.size()) return false;
            for (Literal literal : currentLiterals) {
                if (!clauseLiteralNames.contains(literal.getName())) return false;
            }
        }
        return true;
    }

    public List<Clause> getClauseList() {
        return getClauses().stream().toList();
    }

    @Override
    public CNFSentence convertToCNF() throws UnsatisfiableException, TautologyException {
        return Sentences.optimizeCNF(this);
    }

    @Override
    public boolean isCnf() {
        return true;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        clauses.forEach(c -> str.append(clauses.size() > 1 ? SentenceUtils.OPENING_PARENTHESES : "")
                .append(c.toString())
                .append(clauses.size() > 1 ? SentenceUtils.CLOSING_PARENTHESES : "")
                .append(String.join("", " ", SentenceUtils.AND, " ")));
        return str.replace(str.length() - 3, str.length(), "").toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof CNFSentence that)) return false;
        return clauses.equals(that.getClauses());
    }

    @Override
    public int hashCode() {
        return clauses.hashCode();
    }

    public LinkedHashSet<Clause> getClauses() {
        return new LinkedHashSet<>(clauses);
    }

    public int size() {
        return clauses.size();
    }
}