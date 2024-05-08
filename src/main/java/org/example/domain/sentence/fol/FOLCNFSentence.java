package org.example.domain.sentence.fol;

import org.example.domain.FOLSentenceType;
import org.example.domain.Sentences;
import org.example.util.SentenceUtils;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author aram.azatyan | 4/17/2024 8:39 PM
 */
public final class FOLCNFSentence implements FOLSentence {
    private final LinkedHashSet<FOLClause> clauses;
    private String stringRepresentation;
    private Boolean isCanonical;

    public FOLCNFSentence(LinkedHashSet<FOLClause> clauses) {
        if (clauses == null || clauses.isEmpty()) throw new IllegalArgumentException("null param");
        clauses.remove(null);
        if (clauses.size() == 0) throw new IllegalArgumentException("null clause passed");
        this.clauses = clauses;
    }

    public FOLCNFSentence(FOLClause... clauses) {
        if (clauses == null || clauses.length == 0) throw new IllegalArgumentException("null param");
        LinkedHashSet<FOLClause> clauseSet = Arrays.stream(clauses)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (clauseSet.size() == 0) throw new IllegalArgumentException("null clauses passed");
        this.clauses = clauseSet;
    }

    public FOLCNFSentence(String expression) throws ParseException {
        FOLCNFSentence cnfSentence = Sentences.parseFOLCNFExpression(expression);
        this.clauses = cnfSentence.getClauses();
    }

//    //-- returns true if and only if really canonical (will mostly be used for minimal CNFs)
//    public boolean isCanonical() {
//        if (isCanonical != null) return isCanonical;
//
//        Set<String> clauseLiteralNames = null;
//        OUTER: for (Clause clause : clauses) {
//            if (clause.getLiterals().contains(Literal.TRUE) || clause.getLiterals().contains(Literal.FALSE)) {
//                isCanonical = false;
//                break;
//            }
//
//            if (clauseLiteralNames == null) {
//                clauseLiteralNames = clause.getLiterals().stream()
//                        .map(Literal::getName)
//                        .collect(Collectors.toCollection(LinkedHashSet::new));
//
//                if (clauseLiteralNames.size() != clause.size()) {
//                    isCanonical = false;
//                    break;
//                }
//                continue;
//            }
//
//            if (clauseLiteralNames.size() != clause.size()) {
//                isCanonical = false;
//                break;
//            }
//
//            LinkedHashSet<Literal> currentLiterals = clause.getLiterals();
//            if (currentLiterals.size() != clauseLiteralNames.size()) {
//                isCanonical = false;
//                break;
//            }
//            for (Literal literal : currentLiterals) {
//                if (!clauseLiteralNames.contains(literal.getName())) {
//                    isCanonical = false;
//                    break OUTER;
//                }
//            }
//        }
//
//        if (isCanonical == null) isCanonical = true;
//        return isCanonical;
//    }

    public List<FOLClause> getClauseList() {
        return getClauses().stream().toList();
    }

    public int size() {
        return clauses.size();
    }

    @Override
    public FOLSentenceType type() {
        return FOLSentenceType.CNF;
    }

//    @Override
//    protected CNFSentence convertToMinimalCNF() throws TautologyException, ContradictionException {
////        return Sentences.optimizeCNF(this);
//        CNFSentence possCNF = Sentences.optimizeCNF(this);
//        if (possCNF.size() == 1) return possCNF;
//        return possCNF.isCanonical() ? Sentences.optimizeCanonicalCNF(possCNF) : possCNF;
//    }

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

//    @Override
//    public boolean equals(Object other) {
//        if (other == this) return true;
//        if (!(other instanceof CNFSentence that)) return false;
//        return clauses.equals(that.getClauses());
//    }

//    @Override
//    public int hashCode() {
//        return clauses.hashCode();
//    }

    public LinkedHashSet<FOLClause> getClauses() {
        return new LinkedHashSet<>(clauses);
    }
}
