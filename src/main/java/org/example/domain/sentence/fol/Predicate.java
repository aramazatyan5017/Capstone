package org.example.domain.sentence.fol;

import org.example.domain.FOLSentenceType;
import org.example.domain.Sentences;
import org.example.domain.sentence.fol.term.Term;
import org.example.util.SentenceUtils;
import org.example.util.Utils;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author aram.azatyan | 4/14/2024 8:42 PM
 */
public final class Predicate implements FOLSentence {
    private final String name;
    private final boolean negated;
    private final List<Term> terms;

    private String stringRepresentation;

    public Predicate(String name, boolean negated, List<Term> terms) {
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
        List<Term> termList = Arrays.stream(terms)
                .filter(Objects::nonNull)
                .toList();
        if (termList.isEmpty()) throw new IllegalArgumentException("null clauses passed");

        this.name = name;
        this.negated = negated;
        this.terms = termList;
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

    public String getName() {
        return name;
    }

    public boolean isNegated() {
        return negated;
    }

    public List<Term> getTerms() {
        return new ArrayList<>(terms);
    }
}
