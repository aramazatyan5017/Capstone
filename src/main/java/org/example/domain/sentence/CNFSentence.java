package org.example.domain.sentence;

import org.example.domain.SatisfiabilityType;
import org.example.domain.SentenceType;
import org.example.domain.Sentences;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;
import org.example.util.SentenceUtils;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author aram.azatyan | 2/28/2024 5:20 PM
 */
public final class CNFSentence extends AbstractSentence {
    private final LinkedHashSet<Clause> clauses;
    private String stringRepresentation;

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
        CNFSentence cnfSentence = Sentences.parseCNFExpression(expression, true);
        this.clauses = cnfSentence.getClauses();
    }

    //-- returns true if and only if really canonical (will mostly be used for minimal CNFs)
//    public boolean isCanonical() {
//        if (satisfiabilityType() != SatisfiabilityType.CONTINGENCY) return false;
//
//        if (size() == 1) return satisfiabilityType() != SatisfiabilityType.TAUTOLOGY;
//        LinkedHashSet<String> clauseLiteralNames = null;
//        for (Clause clause : clauses) {
//            if (satisfiabilityType() != SatisfiabilityType.CONTINGENCY) return false;
//
//            if (clauseLiteralNames == null) {
//                clauseLiteralNames = clause.getLiterals().stream()
//                        .map(Literal::getName)
//                        .collect(Collectors.toCollection(LinkedHashSet::new));
//                continue;
//            }
//
//            LinkedHashSet<Literal> currentLiterals = clause.getLiterals();
//            if (currentLiterals.size() != clauseLiteralNames.size()) return false;
//            for (Literal literal : currentLiterals) {
//                if (!clauseLiteralNames.contains(literal.getName())) return false;
//            }
//        }
//        return true;
//    }

    public boolean isCanonical() {
        Set<String> clauseLiteralNames = null;
        for (Clause clause : clauses) {
            if (clause.getLiterals().contains(Literal.TRUE) || clause.getLiterals().contains(Literal.FALSE)) return false;

            if (clauseLiteralNames == null) {
                clauseLiteralNames = clause.getLiterals().stream()
                        .map(Literal::getName)
                        .collect(Collectors.toCollection(LinkedHashSet::new));

                if (clauseLiteralNames.size() != clause.size()) return false;
                continue;
            }

            if (clauseLiteralNames.size() != clause.size()) return false;

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
    public SentenceType type() {
        return SentenceType.CNF;
    }

    @Override
    protected CNFSentence convertToMinimalCNF() throws TautologyException, ContradictionException {
//        return Sentences.optimizeCNF(this);
        CNFSentence possCNF = Sentences.optimizeCNF(this);
        if (possCNF.size() == 1) return possCNF;
        return possCNF.isCanonical() ? Sentences.optimizeCanonicalCNF(possCNF) : possCNF;
    }

    @Override
    public String toString() {
        if (stringRepresentation == null) {
            StringBuilder str = new StringBuilder();
            clauses.forEach(c -> str.append(clauses.size() > 1 ? SentenceUtils.OPENING_PARENTHESES : "")
                    .append(c.toString())
                    .append(clauses.size() > 1 ? SentenceUtils.CLOSING_PARENTHESES : "")
                    .append(String.join("", " ", SentenceUtils.AND, " ")));
            stringRepresentation = str.replace(str.length() - 3, str.length(), "").toString();
        }

        return stringRepresentation;
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

    public static void main(String[] args) throws Exception {
        System.out.println(new CNFSentence("(A | B | C) & (!A | B | !C)").convertToMinimalCNF());
    }
}