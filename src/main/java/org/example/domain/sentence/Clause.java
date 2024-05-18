package org.example.domain.sentence;

import org.example.domain.LogicType;
import org.example.domain.sentence.fol.FOLCNFSentence;
import org.example.domain.sentence.fol.FOLClause;
import org.example.domain.sentence.fol.Predicate;
import org.example.domain.sentence.propositional.Literal;
import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.sentence.propositional.PropositionalClause;

import java.util.LinkedHashSet;

/**
 * @author aram.azatyan | 5/8/2024 6:02 PM
 */
public interface Clause extends Sentence {
    LinkedHashSet<BasicLogicElement> basicElements();

    int size();

    @SuppressWarnings("unchecked")
    static Clause createClause(LinkedHashSet<BasicLogicElement> elements) {
        if (elements == null || elements.isEmpty()) throw new IllegalArgumentException("null param");
        elements.remove(null);
        if (elements.isEmpty()) throw new IllegalArgumentException("null clause(s) present");
        if ((int) elements.stream().map(BasicLogicElement::logicType).distinct().count() != 1) throw
                new IllegalArgumentException("clauses of different logic types passed");

        LinkedHashSet<? extends BasicLogicElement> temp = elements;

        switch (elements.iterator().next().logicType()) {
            case PROPOSITIONAL -> {return new PropositionalClause((LinkedHashSet<Literal>) temp);}
            case FOL -> {return new FOLClause((LinkedHashSet<Predicate>) temp);}
            default -> {return null;}
        }
    }
}
