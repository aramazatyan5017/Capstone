package org.example.domain.sentence.propositional;

import org.example.domain.LogicType;
import org.example.domain.PropositionalSentenceType;
import org.example.domain.Sentences;
import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Clause;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;
import org.example.util.SentenceUtils;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author aram.azatyan | 2/28/2024 5:20 PM
 */
public final class PropositionalCNFSentence extends AbstractPropositionalSentence implements CNFSentence {
    private final LinkedHashSet<PropositionalClause> clauses;
    private String stringRepresentation;
    private Boolean isCanonical;

    public PropositionalCNFSentence(LinkedHashSet<PropositionalClause> clauses) {
        if (clauses == null || clauses.isEmpty()) throw new IllegalArgumentException("null param");
        clauses.remove(null);
        if (clauses.size() == 0) throw new IllegalArgumentException("null clause passed");
        this.clauses = clauses;
    }

    public PropositionalCNFSentence(PropositionalClause... clauses) {
        if (clauses == null || clauses.length == 0) throw new IllegalArgumentException("null param");
        LinkedHashSet<PropositionalClause> clauseSet = Arrays.stream(clauses)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (clauseSet.size() == 0) throw new IllegalArgumentException("null clauses passed");
        this.clauses = clauseSet;
    }

    public PropositionalCNFSentence(String expression) throws ParseException {
        PropositionalCNFSentence cnfSentence = Sentences.parseCNFExpression(expression);
        this.clauses = cnfSentence.getClauses();
    }

    //-- returns true if and only if really canonical (will mostly be used for minimal CNFs)
    public boolean isCanonical() {
        if (isCanonical != null) return isCanonical;

        Set<String> clauseLiteralNames = null;
        OUTER: for (PropositionalClause clause : clauses) {
            if (clause.getLiterals().contains(Literal.TRUE) || clause.getLiterals().contains(Literal.FALSE)) {
                isCanonical = false;
                break;
            }

            if (clauseLiteralNames == null) {
                clauseLiteralNames = clause.getLiterals().stream()
                        .map(Literal::getName)
                        .collect(Collectors.toCollection(LinkedHashSet::new));

                if (clauseLiteralNames.size() != clause.size()) {
                    isCanonical = false;
                    break;
                }
                continue;
            }

            if (clauseLiteralNames.size() != clause.size()) {
                isCanonical = false;
                break;
            }

            LinkedHashSet<Literal> currentLiterals = clause.getLiterals();
            if (currentLiterals.size() != clauseLiteralNames.size()) {
                isCanonical = false;
                break;
            }
            for (Literal literal : currentLiterals) {
                if (!clauseLiteralNames.contains(literal.getName())) {
                    isCanonical = false;
                    break OUTER;
                }
            }
        }

        if (isCanonical == null) isCanonical = true;
        return isCanonical;
    }

    public List<PropositionalClause> getClauseList() {
        return getClauses().stream().toList();
    }

    @Override
    public PropositionalSentenceType type() {
        return PropositionalSentenceType.CNF;
    }

    @Override
    public LogicType logicType() {
        return LogicType.PROPOSITIONAL;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LinkedHashSet<Clause> clauses() {
        LinkedHashSet<? extends Clause> temp = getClauses();
        return (LinkedHashSet<Clause>) temp;
    }

    public int size() {
        return clauses.size();
    }

    @Override
    public PropositionalCNFSentence convertToMinimalCNF() throws TautologyException, ContradictionException {
//        return Sentences.optimizeCNF(this);
        PropositionalCNFSentence possCNF = (PropositionalCNFSentence) Sentences.optimizeCNF(this);
        if (possCNF.size() == 1) return possCNF;
        return possCNF.isCanonical() ? (PropositionalCNFSentence) Sentences.optimizeCanonicalCNF(possCNF) : possCNF;
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
        if (!(other instanceof PropositionalCNFSentence that)) return false;
        return clauses.equals(that.getClauses());
    }

    @Override
    public int hashCode() {
        return clauses.hashCode();
    }

    public LinkedHashSet<PropositionalClause> getClauses() {
        return new LinkedHashSet<>(clauses);
    }
}