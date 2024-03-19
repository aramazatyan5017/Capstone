package org.example.old;

import org.example.domain.Connective;
import org.example.domain.sentence.Sentences;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.example.util.SentenceUtils.NOT;

/**
 * @author aram.azatyan | 2/13/2024 9:42 PM
 */

/*
    im mot petq a lini misht sentenceLHS - connective - sentenceRHS forma bolor
    sentenceneri hamar. ete yndhameny mi hat simvol a, uremn connective u sentenceRHS
    are null 
 */
public class OldSentence {
    private OldSentence lhs;
    private OldSentence rhs;
    private Connective connective;
    private boolean negatedLeft;
    private boolean negatedRight;
    
    //-- if basic sentence
    // TODO: 2/14/2024 hyly vor tox mna name
    private String name;
    private boolean negated;

    //-- util fields
    private Boolean isDefinite;
    private Boolean isDefiniteTypeOfImplication;
    private Boolean isCnf;

    //-- basic true sentence
    public OldSentence(String name) {
        this.name = name;
    }
    
    //-- basic sentence with negation option
    public OldSentence(String name, boolean negated) {
        this.name = name;
        this.negated = negated;
    }
    
    //-- complex sentence with no outer negation
    public OldSentence(OldSentence lhs, OldSentence rhs, Connective connective) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.connective = connective;
    }
    
    //-- complex sentence with outer negation option
    public OldSentence(OldSentence lhs, OldSentence rhs, Connective connective,
                       boolean negatedLeft, boolean negatedRight) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.connective = connective;
        this.negatedLeft = negatedLeft;
        this.negatedRight = negatedRight;
    }

    // TODO: 2/21/2024 der im mot chi ogtagorcvum, henc ogtagorcvi, knowni pahov kmtacem
//    public boolean evaluate() {
//        if (isBasicSentence()) {
//            if (!isKnown()) throw new EvaluationOfUnknownSentenceException();
//            return !negated;
//        }
//        return connective.apply(negatedLeft != lhs.evaluate(), negatedRight != rhs.evaluate());
//    }

    @Override
    public String toString() {
        if (isBasicSentence()) return formatSentenceString(name, negated, true);
        return String.join(" ", formatSentenceString(lhs.toString(), negatedLeft, lhs.isBasicSentence()),
                                           connective.toString(),
                                           formatSentenceString(rhs.toString(), negatedRight, rhs.isBasicSentence()));
    }
    
    private String formatSentenceString(String sentenceString, boolean isNegated, boolean isBasic) {
        var optNegSign = isNegated ? NOT: "";
        if (isBasic) return optNegSign + sentenceString;
        return optNegSign + "(" + sentenceString + ")";
    }
    
    public boolean isBasicSentence() {
        return lhs == null && rhs == null && connective == null;
    }

    // ete mi hat literal a, eli hamarvum a vor type implication a
    public boolean isDefiniteClause() {
        if (isDefinite != null) return isDefinite;
        var view = SentenceUtil.getDefiniteClauseView(this);
        isDefinite = view.isDefinite();
        isDefiniteTypeOfImplication = view.type() == Connective.IMPLICATION;
        return isDefinite;
    }

    public boolean toDefiniteOfTypeImplication() {
        if (isDefinite == null) isDefiniteClause();
        if (isDefinite) {
            if (!isDefiniteTypeOfImplication) {
                var basicSentences = getBasicSentences();
                for (var iterator = basicSentences.iterator(); iterator.hasNext();) {
                    var currentSentence = iterator.next();
                    if (!currentSentence.isNegated()) {
                        rhs = currentSentence;
                        iterator.remove();
                        break;
                    }
                }

                basicSentences.forEach(s -> s.negated = false);

                lhs = Sentences.combineBasics(basicSentences, Connective.AND);
                connective = Connective.IMPLICATION;
                negatedLeft = false;
                negatedRight = false;
                isDefiniteTypeOfImplication = true;
            }
            return true;
        }
        return false;
    }

    public Set<OldSentence> getBasicSentences() {
        Set<OldSentence> set = new HashSet<>();
        if (isBasicSentence()) {
            set.add(this);
            return set;
        }
        set.addAll(lhs.getBasicSentences());
        set.addAll(rhs.getBasicSentences());
        return set;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof OldSentence that)) return false;
        if (this.isBasicSentence() != that.isBasicSentence()) return false;
        if (isBasicSentence()) return this.name.equals(that.name) && this.negated == that.negated;
        if (this.connective != that.connective) return false;
        if (Connective.getCommutatives().contains(connective)) {
            return (((this.lhs.equals(that.lhs) && this.negatedLeft == that.negatedLeft)
                            && (this.rhs.equals(that.rhs) && this.negatedRight == that.negatedRight))
                    ||
                    ((this.lhs.equals(that.rhs) && this.negatedLeft == that.negatedRight)
                            && (this.rhs.equals(that.lhs) && this.negatedRight == that.negatedLeft)));
        } else {
            return this.negatedLeft == that.negatedLeft && this.negatedRight == that.negatedRight
                    && this.lhs.equals(that.lhs) && this.rhs.equals(that.rhs);
        }
    }

    @Override
    public int hashCode() {
        if (isBasicSentence()) return Objects.hash(name, negated);
        return Objects.hash(lhs) + Objects.hash(rhs) + connective.hashCode() +
                Objects.hash(negatedLeft) + Objects.hash(negatedRight);
    }

    public OldSentence getLhs() {
        return lhs;
    }

    public Connective getConnective() {
        return connective;
    }

    public OldSentence getRhs() {
        return rhs;
    }

    public boolean isNegatedLeft() {
        return negatedLeft;
    }

    public boolean isNegatedRight() {
        return negatedRight;
    }

    public String getName() {
        return name;
    }

    public boolean isNegated() {
        return negated;
    }
}
