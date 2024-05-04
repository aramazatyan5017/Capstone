package org.example.temp_fol;

import org.example.domain.FOLSentenceType;
import org.example.domain.Sentences;
import org.example.domain.sentence.Clause;
import org.example.domain.sentence.Literal;
import org.example.util.SentenceUtils;
import org.example.util.Utils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author aram.azatyan | 4/14/2024 8:42 PM
 */
public class Predicate implements FOLSentence {
    private final String name;
    private final boolean negated;
    private final LinkedHashSet<Term> terms;

    private String stringRepresentation;

    public Predicate(String name, boolean negated, LinkedHashSet<Term> terms) {
        if (Utils.isNullOrBlank(name)) throw new IllegalArgumentException("null param");
        if (terms == null || terms.isEmpty()) throw new IllegalArgumentException("null param");
        terms.remove(null);
        if (terms.size() == 0) throw new IllegalArgumentException("null clause passed");

        this.name = name;
        this.negated = negated;
        this.terms = terms;
    }

    public Predicate(String name, boolean negated, Term... terms) {
        if (Utils.isNullOrBlank(name)) throw new IllegalArgumentException("null param");
        if (terms == null || terms.length == 0) throw new IllegalArgumentException("null param");
        LinkedHashSet<Term> termSet = Arrays.stream(terms)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (termSet.isEmpty()) throw new IllegalArgumentException("null clauses passed");

        this.name = name;
        this.negated = negated;
        this.terms = termSet;
    }

    public Predicate(String expression) throws ParseException {
        if (Utils.isNullOrBlank(expression)) throw new IllegalArgumentException("null param");

        Predicate predicate;

        try {
            predicate = Sentences.parsePredicateExpression(expression);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
//        if (literal.equals(TRUE) || literal.equals(FALSE)) throw
//                new IllegalArgumentException("only one instance of TRUE and FALSE available");

        this.name = predicate.getName();
        this.negated = predicate.isNegated();
        this.terms = predicate.getTerms();
    }

    @Override
    public FOLSentenceType type() {
        return FOLSentenceType.PREDICATE;
    }

    @Override
    public String toString() {
        if (stringRepresentation == null) {
            stringRepresentation = String.join("", negated ? SentenceUtils.NOT : "",
                    name,
                    String.valueOf(SentenceUtils.OPENING_PARENTHESES),
                    terms.stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(SentenceUtils.COMMA + " ")),
                    String.valueOf(SentenceUtils.CLOSING_PARENTHESES));
        }

        return stringRepresentation;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof Predicate that)) return false;
        return this.name.equals(that.name) && this.negated == that.negated && this.terms.equals(that.terms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, negated, terms);
    }

    public List<Term> getTermList() {
        return terms.stream().toList();
    }

    public String getName() {
        return name;
    }

    public boolean isNegated() {
        return negated;
    }

    public LinkedHashSet<Term> getTerms() {
        return new LinkedHashSet<>(terms);
    }
}
