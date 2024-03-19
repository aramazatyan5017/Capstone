package org.example.domain.sentence;

import org.example.domain.*;
import org.example.exception.TautologyException;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author aram.azatyan | 3/1/2024 2:19 PM
 */

//--  a disjunction of literals
public final class Clause implements Sentence {
    private final LinkedHashSet<Literal> literals;
    private int nonNegatedLiteralCount = 0;
    private int negatedLiteralCount = 0;

    public Clause(LinkedHashSet<Literal> literals) {
        if (literals == null || literals.isEmpty()) throw new IllegalArgumentException("null param");
        literals.remove(null);
        if (literals.size() == 0) throw new IllegalArgumentException("null literal passed");
        setLiteralCounts(literals);
        this.literals = literals;
    }

    public Clause(Literal... literals) {
        if (literals == null || literals.length == 0) throw new IllegalArgumentException("null param");
        LinkedHashSet<Literal> literalSet = Arrays.stream(literals)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (literalSet.size() == 0) throw new IllegalArgumentException("null literals passed");
        setLiteralCounts(literalSet);
        this.literals = literalSet;
    }

    public Clause(String expression) throws ParseException {
        Clause clause = Sentences.parseClauseExpression(expression);
        this.literals = clause.getLiterals();
        this.negatedLiteralCount = clause.getNegatedLiteralCount();
        this.nonNegatedLiteralCount = clause.getNonNegatedLiteralCount();
    }

    public int size() {
        return nonNegatedLiteralCount + negatedLiteralCount;
    }

    public List<Literal> getLiteralList() {
        return getLiterals().stream().toList();
    }

    @Override
    public CNFSentence convertToCNF() throws TautologyException {
        Clause clause = Sentences.optimizeClause(this);
        return new CNFSentence(clause);
    }

    @Override
    public boolean isClause() {
        return true;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        literals.forEach(l -> str.append(l.toString())
                .append(" ")
                .append(Connective.OR)
                .append(" "));
        return str.replace(str.length() - 3, str.length(), "").toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof Clause that)) return false;

        return this.literals.equals(that.getLiterals());
    }

    @Override
    public int hashCode() {
        return literals.hashCode();
    }

    private void setLiteralCounts(Set<Literal> literals) {
        for (Literal literal : literals) {
            if (literal.isNegated()) negatedLiteralCount++;
            else nonNegatedLiteralCount++;
        }
    }

    public LinkedHashSet<Literal> getLiterals() {
        return new LinkedHashSet<>(literals);
    }

    public int getNonNegatedLiteralCount() {
        return nonNegatedLiteralCount;
    }

    public int getNegatedLiteralCount() {
        return negatedLiteralCount;
    }
}
