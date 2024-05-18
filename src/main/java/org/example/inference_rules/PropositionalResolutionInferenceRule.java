package org.example.inference_rules;

import org.example.domain.LogicType;
import org.example.domain.SatisfiabilityType;
import org.example.domain.sentence.BasicLogicElement;
import org.example.domain.sentence.Clause;
import org.example.domain.sentence.fol.FOLClause;
import org.example.domain.sentence.fol.Predicate;
import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.sentence.propositional.PropositionalClause;
import org.example.domain.sentence.propositional.Literal;
import org.example.exception.NothingToInferException;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author aram.azatyan | 3/27/2024 4:04 PM
 */
public class PropositionalResolutionInferenceRule {

    @SuppressWarnings("unchecked")
    public static Clause resolve(Clause clause1, Clause clause2) throws NothingToInferException {
        if (clause1 == null || clause2 == null) throw new IllegalArgumentException("null param");
        if (clause1.logicType() != clause2.logicType()) throw new IllegalArgumentException("clauses of different logic types");
        if ((clause1.logicType() == LogicType.PROPOSITIONAL && ((PropositionalClause) clause1).satisfiabilityType() != SatisfiabilityType.CONTINGENCY)
                ||
            (clause2.logicType() == LogicType.PROPOSITIONAL && ((PropositionalClause) clause2).satisfiabilityType() != SatisfiabilityType.CONTINGENCY)) {
            throw new NothingToInferException();
        }

        Set<BasicLogicElement> set = new HashSet<>();
        LinkedHashSet<BasicLogicElement> possClauseLiterals = clause1.basicElements();

        clause1.basicElements().forEach(l -> set.add(l.getNegated()));

        StringBuilder complementaryLiteralName = new StringBuilder();
        for (BasicLogicElement element : clause2.basicElements()) {
            if (set.contains(element) && !complementaryLiteralName.isEmpty()) throw new NothingToInferException();
            if (set.contains(element)) {
                complementaryLiteralName.append(element.getName());
                possClauseLiterals.removeIf(l -> l.getName().contentEquals(complementaryLiteralName));
                continue;
            }
            possClauseLiterals.add(element);
        }

        if (complementaryLiteralName.isEmpty()) throw new NothingToInferException();

        LinkedHashSet<? extends BasicLogicElement> temp = possClauseLiterals;

        switch (clause1.logicType()) {
            case PROPOSITIONAL -> {return new PropositionalClause((LinkedHashSet<Literal>) temp);}
            case FOL -> {return new FOLClause((LinkedHashSet<Predicate>) temp);}
            default -> {return null;}
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(resolve(new PropositionalClause("A | !B | C"), new PropositionalClause("!A | B | C")));
    }
}
