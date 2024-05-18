package org.example.domain.sentence.fol;

import org.example.domain.FOLSentenceType;
import org.example.domain.LogicType;
import org.example.domain.Sentences;
import org.example.domain.sentence.BasicLogicElement;
import org.example.domain.sentence.fol.term.*;
import org.example.domain.sentence.propositional.Literal;
import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.sentence.propositional.PropositionalClause;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;
import org.example.util.SentenceUtils;
import org.example.util.Utils;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author aram.azatyan | 4/14/2024 8:42 PM
 */
public final class Predicate implements FOLSentence, BasicLogicElement {
    private final String name;
    private final boolean negated;
    private final List<Term> terms;

    private String stringRepresentation;

    public static final Predicate TRUE;
    public static final Predicate FALSE;

    static {
        TRUE = new Predicate(true);
        FALSE = new Predicate(false);
    }

    public Predicate(String name, boolean negated, List<Term> terms) {
        if (Utils.isNullOrBlank(name)) throw new IllegalArgumentException("null param");
        if (name.equalsIgnoreCase("true") || name.equalsIgnoreCase("false"))
            throw new IllegalArgumentException("only one instance of TRUE and FALSE available");
        if (terms == null || terms.isEmpty()) throw new IllegalArgumentException("null param");
        terms.remove(null);
        if (terms.size() == 0) throw new IllegalArgumentException("null clause passed");

        this.name = name;
        this.negated = negated;
        this.terms = terms;
    }

    public Predicate(String name, boolean negated, Term... terms) {
        if (Utils.isNullOrBlank(name)) throw new IllegalArgumentException("null param");
        if (name.equalsIgnoreCase("true") || name.equalsIgnoreCase("false"))
            throw new IllegalArgumentException("only one instance of TRUE and FALSE available");
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

        Predicate predicate = Sentences.parsePredicateExpression(expression);

//        if (literal.equals(TRUE) || literal.equals(FALSE)) throw
//                new IllegalArgumentException("only one instance of TRUE and FALSE available");

        this.name = predicate.getName();
        this.negated = predicate.isNegated();
        this.terms = predicate.getTerms();
    }

    private Predicate(boolean value) {
        this.name = value ? "TRUE" : "FALSE";
        this.negated = false;
        this.terms = null;
    }

    @Override
    public FOLSentenceType type() {
        return FOLSentenceType.PREDICATE;
    }

    @Override
    public LogicType logicType() {
        return LogicType.FOL;
    }

    @Override
    public BasicLogicElement getFalse() {
        return Predicate.FALSE;
    }

    @Override
    public BasicLogicElement getTrue() {
        return Predicate.TRUE;
    }

    @Override
    public BasicLogicElement getNegated() {
        // true or false
        return new Predicate(this.name, !this.negated, new ArrayList<>(this.terms));
    }

    @Override
    public boolean equalsIgnoreNegation(BasicLogicElement other) {
        if (other == null) return false;
        if (other.logicType() != LogicType.FOL) return false;

        Predicate that = (Predicate) other;

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
    public FOLCNFSentence minimalCNF() throws TautologyException, ContradictionException {
        if (this == Predicate.TRUE) throw new TautologyException();
        if (this == Predicate.FALSE) throw new ContradictionException();
        return new FOLCNFSentence(new FOLClause(this));
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

        if (!this.name.equals(that.name)) return false;
        if (this.negated != that.negated) return false;
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
        return Objects.hash(name, negated, terms);
    }

    public String getName() {
        return name;
    }

    public boolean isNegated() {
        return negated;
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

    public List<Term> getTerms() {
        return new ArrayList<>(terms);
    }
}
