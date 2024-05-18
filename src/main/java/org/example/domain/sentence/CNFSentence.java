package org.example.domain.sentence;

import org.example.domain.LogicType;
import org.example.domain.sentence.fol.FOLCNFSentence;
import org.example.domain.sentence.fol.FOLClause;
import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.sentence.propositional.PropositionalClause;

import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * @author aram.azatyan | 5/10/2024 3:49 PM
 */
public interface CNFSentence extends Sentence {
    LinkedHashSet<Clause> clauses();

    boolean isCanonical();

    int size();

    @SuppressWarnings("unchecked")
    static CNFSentence createCNF(LinkedHashSet<Clause> clauses) {
        if (clauses == null || clauses.isEmpty()) throw new IllegalArgumentException("null param");
        clauses.remove(null);
        if (clauses.isEmpty()) throw new IllegalArgumentException("null clause(s) present");
        if ((int) clauses.stream().map(Clause::logicType).distinct().count() != 1) throw
                new IllegalArgumentException("clauses of different logic types passed");

        LinkedHashSet<? extends Clause> temp = clauses;

        switch (clauses.iterator().next().logicType()) {
            case PROPOSITIONAL -> {return new PropositionalCNFSentence((LinkedHashSet<PropositionalClause>) temp);}
            case FOL -> {return new FOLCNFSentence((LinkedHashSet<FOLClause>) temp);}
            default -> {return null;}
        }
    }
}
