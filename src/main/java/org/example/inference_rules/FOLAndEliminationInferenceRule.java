package org.example.inference_rules;

import org.example.domain.Connective;
import org.example.domain.sentence.fol.FOLCNFSentence;
import org.example.domain.sentence.fol.FOLClause;
import org.example.domain.sentence.fol.FOLSentence;
import org.example.domain.sentence.fol.GenericComplexFOLSentence;
import org.example.domain.sentence.propositional.GenericComplexPropositionalSentence;
import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.sentence.propositional.PropositionalClause;
import org.example.domain.sentence.propositional.PropositionalSentence;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author aram.azatyan | 5/13/2024 1:56 PM
 */
public class FOLAndEliminationInferenceRule {
    public static Set<FOLSentence> infer(FOLSentence sentence) {
        if (sentence == null) throw new IllegalArgumentException("null param");

        Set<FOLSentence> inferred = new HashSet<>();

        switch (sentence.type()) {
            case PREDICATE -> inferred.add(sentence);
            case CLAUSE -> inferred.add(((FOLClause) sentence).size() == 1 ? ((FOLClause) sentence).getPredicates().iterator().next() : sentence);
            case CNF -> ((FOLCNFSentence) sentence).getClauses().forEach(clause -> inferred
                    .add(clause.size() == 1 ? clause.getPredicates().iterator().next() : clause));
            case GENERIC_COMPLEX -> {
                GenericComplexFOLSentence complex = (GenericComplexFOLSentence) sentence;

                if (complex.getConnective() == Connective.AND) {
                    inferred.addAll(infer(complex.getLeftSentence()));
                    inferred.addAll(infer(complex.getRightSentence()));
                } else {
                    inferred.add(complex);
                }
            }
        }

        return inferred;
    }
}
