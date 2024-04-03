package org.example.domain.sentence;

import org.example.domain.Connective;
import org.example.domain.SentenceType;
import org.example.domain.Sentences;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;

import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author aram.azatyan | 3/1/2024 2:19 PM
 */

//--  a disjunction of literals
public final class Clause extends AbstractSentence {
    private final LinkedHashSet<Literal> literals;
    private String stringRepresentation;

    public Clause(LinkedHashSet<Literal> literals) {
        if (literals == null || literals.isEmpty()) throw new IllegalArgumentException("null param");
        literals.remove(null);
        if (literals.size() == 0) throw new IllegalArgumentException("null literal passed");
        this.literals = literals;
    }

    public Clause(Literal... literals) {
        if (literals == null || literals.length == 0) throw new IllegalArgumentException("null param");
        LinkedHashSet<Literal> literalSet = Arrays.stream(literals)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (literalSet.size() == 0) throw new IllegalArgumentException("null literals passed");
        this.literals = literalSet;
    }

    public Clause(String expression) throws ParseException {
        Clause clause = Sentences.parseClauseExpression(expression);
        this.literals = clause.getLiterals();
    }

    public int size() {
        return literals.size();
    }

    public List<Literal> getLiteralList() {
        return getLiterals().stream().toList();
    }

    @Override
    protected CNFSentence convertToMinimalCNF() throws TautologyException, ContradictionException {
        return new CNFSentence(Sentences.optimizeClause(this));
    }

    @Override
    public SentenceType type() {
        return SentenceType.CLAUSE;
    }

    @Override
    public String toString() {
        if (stringRepresentation == null) {
            StringBuilder str = new StringBuilder();
            literals.forEach(l -> str.append(l.toString())
                    .append(" ")
                    .append(Connective.OR)
                    .append(" "));
            stringRepresentation = str.replace(str.length() - 3, str.length(), "").toString();
        }

        return stringRepresentation;
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

    public LinkedHashSet<Literal> getLiterals() {
        return new LinkedHashSet<>(literals);
    }
}
