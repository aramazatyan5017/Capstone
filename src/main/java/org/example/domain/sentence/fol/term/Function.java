package org.example.domain.sentence.fol.term;

import org.example.domain.Sentences;
import org.example.util.SentenceUtils;
import org.example.util.Utils;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author aram.azatyan | 4/15/2024 4:17 PM
 */
public final class Function implements Term {
    private final String name;
    private final List<Term> terms;

    private String stringRepresentation;

    public Function(String name, List<Term> terms) {
        if (Utils.isNullOrBlank(name)) throw new IllegalArgumentException("null param");
        if (terms == null || terms.isEmpty()) throw new IllegalArgumentException("null param");
        terms.removeIf(Objects::isNull);
        if (terms.size() == 0) throw new IllegalArgumentException("null clause(s) passed");

        this.name = name;
        this.terms = terms;
    }

    public Function(String name, Term... terms) {
        if (Utils.isNullOrBlank(name)) throw new IllegalArgumentException("null param");
        if (terms == null || terms.length == 0) throw new IllegalArgumentException("null param");
        List<Term> tempList = Arrays.stream(terms)
                .filter(Objects::nonNull)
                .toList();
        if (tempList.isEmpty()) throw new IllegalArgumentException("null clause(s) passed");

        this.name = name;
        this.terms = tempList;
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
        if (!this.name.equals(that.name)) return false;
        if (this.terms.size() != that.terms.size()) return false;

        for (int i = 0; i < terms.size(); i++) {
            Term thisTerm = this.terms.get(i);
            Term thatTerm = that.terms.get(i);

            if (thisTerm.type() != thatTerm.type()) return false;
            if (!thisTerm.equals(thatTerm)) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, terms);
    }

    public String getName() {
        return name;
    }

    public List<Term> getTerms() {
        return new ArrayList<>(terms);
    }

    public void substitute(Variable variable, Constant constant) {
        if (variable == null || constant == null) throw new IllegalArgumentException("null param");
        for (int i = 0; i < terms.size(); i++) {
            if (terms.get(i).equals(variable)) {
                terms.set(i, constant);
            } else if (terms.get(i).type() == TermType.FUNCTION) {
                ((Function) terms.get(i)).substitute(variable, constant);
            }
        }
    }

    public Set<Constant> getConstants() {
        Set<Constant> constants = new HashSet<>();

        getTerms().forEach(t -> {
            if (t.type() == TermType.CONSTANT) {
                constants.add((Constant) t);
            } else if (t.type() == TermType.FUNCTION) {
                constants.addAll(((Function) t).getConstants());
            }
        });

        return constants;
    }

    public Set<Variable> getVariables() {
        Set<Variable> variables = new HashSet<>();

        getTerms().forEach(t -> {
            if (t.type() == TermType.VARIABLE) {
                variables.add((Variable) t);
            } else if (t.type() == TermType.FUNCTION) {
                variables.addAll(((Function) t).getVariables());
            }
        });

        return variables;
    }
}
