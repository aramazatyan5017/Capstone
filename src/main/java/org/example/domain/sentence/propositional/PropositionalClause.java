package org.example.domain.sentence.propositional;

import org.example.domain.Connective;
import org.example.domain.LogicType;
import org.example.domain.PropositionalSentenceType;
import org.example.domain.Sentences;
import org.example.domain.sentence.Clause;
import org.example.domain.sentence.BasicLogicElement;
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
public final class PropositionalClause extends AbstractPropositionalSentence implements Clause {
    private final LinkedHashSet<Literal> literals;
    private String stringRepresentation;

    public PropositionalClause(LinkedHashSet<Literal> literals) {
        if (literals == null || literals.isEmpty()) throw new IllegalArgumentException("null param");
        literals.remove(null);
        if (literals.size() == 0) throw new IllegalArgumentException("null literal passed");
        this.literals = literals;
    }

    public PropositionalClause(Literal... literals) {
        if (literals == null || literals.length == 0) throw new IllegalArgumentException("null param");
        LinkedHashSet<Literal> literalSet = Arrays.stream(literals)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (literalSet.size() == 0) throw new IllegalArgumentException("null literals passed");
        this.literals = literalSet;
    }

    public PropositionalClause(String expression) throws ParseException {
        PropositionalClause clause = Sentences.parseClauseExpression(expression);
        this.literals = clause.getLiterals();
    }

    public int size() {
        return literals.size();
    }

    public List<Literal> getLiteralList() {
        return getLiterals().stream().toList();
    }

    @Override
    protected PropositionalCNFSentence convertToMinimalCNF() throws TautologyException, ContradictionException {
        return new PropositionalCNFSentence(Sentences.optimizeClause(this));
    }

    @Override
    public PropositionalSentenceType type() {
        return PropositionalSentenceType.CLAUSE;
    }

    @Override
    public LogicType logicType() {
        return LogicType.PROPOSITIONAL;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LinkedHashSet<BasicLogicElement> basicElements() {
        LinkedHashSet<? extends BasicLogicElement> temp = getLiterals();
        return (LinkedHashSet<BasicLogicElement>) temp;
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
        if (!(other instanceof PropositionalClause that)) return false;
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
