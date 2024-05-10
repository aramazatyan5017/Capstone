package org.example.domain.sentence.fol;

import org.example.domain.Connective;
import org.example.domain.FOLSentenceType;
import org.example.domain.LogicType;
import org.example.domain.Sentences;
import org.example.domain.sentence.Clause;
import org.example.domain.sentence.BasicLogicElement;

import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author aram.azatyan | 4/17/2024 8:30 PM
 */
public final class FOLClause implements FOLSentence, Clause {
    private final LinkedHashSet<Predicate> predicates;
    private String stringRepresentation;

    public FOLClause(LinkedHashSet<Predicate> predicates) {
        if (predicates == null || predicates.isEmpty()) throw new IllegalArgumentException("null param");
        predicates.remove(null);
        if (predicates.size() == 0) throw new IllegalArgumentException("null predicate passed");
        this.predicates = predicates;
    }

    public FOLClause(Predicate... predicates) {
        if (predicates == null || predicates.length == 0) throw new IllegalArgumentException("null param");
        LinkedHashSet<Predicate> predicateSet = Arrays.stream(predicates)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (predicateSet.size() == 0) throw new IllegalArgumentException("null predicate passed");
        this.predicates = predicateSet;
    }

    public FOLClause(String expression) throws ParseException {
        FOLClause clause = Sentences.parseFOLClauseExpression(expression);
        this.predicates = clause.getPredicates();
    }

    public int size() {
        return predicates.size();
    }

    public List<Predicate> getPredicateList() {
        return getPredicates().stream().toList();
    }

//    @Override
//    protected CNFSentence convertToMinimalCNF() throws TautologyException, ContradictionException {
//        return new CNFSentence(Sentences.optimizeClause(this));
//    }

    @Override
    public FOLSentenceType type() {
        return FOLSentenceType.CLAUSE;
    }

    @Override
    public LogicType logicType() {
        return LogicType.FOL;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LinkedHashSet<BasicLogicElement> basicElements() {
        LinkedHashSet<? extends BasicLogicElement> temp = getPredicates();
        return (LinkedHashSet<BasicLogicElement>) temp;
    }

    @Override
    public String toString() {
        if (stringRepresentation == null) {
            StringBuilder str = new StringBuilder();
            predicates.forEach(p -> str.append(p.toString())
                    .append(" ")
                    .append(Connective.OR)
                    .append(" "));
            stringRepresentation = str.replace(str.length() - 3, str.length(), "").toString();
        }

        return stringRepresentation;
    }

//    @Override
//    public boolean equals(Object other) {
//        if (other == this) return true;
//        if (!(other instanceof Clause that)) return false;
//        return this.literals.equals(that.getLiterals());
//    }

//    @Override
//    public int hashCode() {
//        return literals.hashCode();
//    }

    public LinkedHashSet<Predicate> getPredicates() {
        return new LinkedHashSet<>(predicates);
    }
}
