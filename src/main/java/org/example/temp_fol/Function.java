package org.example.temp_fol;

import org.example.domain.Sentences;
import org.example.util.SentenceUtils;
import org.example.util.Utils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author aram.azatyan | 4/15/2024 4:17 PM
 */
public final class Function implements Term {
    private final String name;
    private final LinkedHashSet<Term> terms;

    private String stringRepresentation;

    public Function(String name, LinkedHashSet<Term> terms) {
        if (Utils.isNullOrBlank(name)) throw new IllegalArgumentException("null param");
        if (terms == null || terms.isEmpty()) throw new IllegalArgumentException("null param");
        terms.remove(null);
        if (terms.size() == 0) throw new IllegalArgumentException("null clause passed");

        this.name = name;
        this.terms = terms;
    }

    public Function(String name, Term... terms) {
        if (Utils.isNullOrBlank(name)) throw new IllegalArgumentException("null param");
        if (terms == null || terms.length == 0) throw new IllegalArgumentException("null param");
        LinkedHashSet<Term> termSet = Arrays.stream(terms)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (termSet.isEmpty()) throw new IllegalArgumentException("null clauses passed");

        this.name = name;
        this.terms = termSet;
    }

    public Function(String expression) throws ParseException {
        if (Utils.isNullOrBlank(expression)) throw new IllegalArgumentException("null param");

        Function function = Sentences.parseFunctionExpression(expression);

//        if (literal.equals(TRUE) || literal.equals(FALSE)) throw
//                new IllegalArgumentException("only one instance of TRUE and FALSE available");

        this.name = function.getName();
        this.terms = function.getTerms();
    }

    @Override
    public TermType type() {
        return TermType.FUNCTION;
    }

    @Override
    public String toString() {
        if (stringRepresentation == null) {
            stringRepresentation = String.join("",
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
        if (!(other instanceof Function that)) return false;
        return this.name.equals(that.name) && this.terms.equals(that.terms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, terms);
    }

    public List<Term> getTermList() {
        return terms.stream().toList();
    }

    public String getName() {
        return name;
    }

    public LinkedHashSet<Term> getTerms() {
        return new LinkedHashSet<>(terms);
    }
}
