package org.example.inference_rules;

import org.example.domain.Connective;
import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.sentence.propositional.PropositionalClause;
import org.example.domain.sentence.propositional.GenericComplexPropositionalSentence;
import org.example.domain.sentence.Sentence;
import org.example.domain.sentence.propositional.PropositionalSentence;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author aram.azatyan | 3/27/2024 4:03 PM
 */
// TODO: 3/27/2024 im karciqov, esi chpiti patasxanatu lini satisfiablitii hamar
public class PropositionalAndEliminationInferenceRule {

    public static Set<PropositionalSentence> infer(PropositionalSentence sentence) {
        if (sentence == null) throw new IllegalArgumentException("null param");

        Set<PropositionalSentence> inferred = new HashSet<>();

        switch (sentence.type()) {
            case LITERAL -> inferred.add(sentence);
            case CLAUSE -> inferred.add(((PropositionalClause) sentence).size() == 1 ? ((PropositionalClause) sentence).getLiterals().iterator().next() : sentence);
            case CNF -> ((PropositionalCNFSentence) sentence).getClauses().forEach(clause -> inferred
                        .add(clause.size() == 1 ? clause.getLiterals().iterator().next() : clause));
            case GENERIC_COMPLEX -> {
                GenericComplexPropositionalSentence complex = (GenericComplexPropositionalSentence) sentence;

                if (complex.getConnective() == Connective.AND) {
                    try {
                        inferred.addAll(infer(PropositionalSentence.optimizedParse(complex.getLeftSentence().toString())));
                        inferred.addAll(infer(PropositionalSentence.optimizedParse(complex.getRightSentence().toString())));
                    } catch (ParseException ignored) {}
                } else {
                    inferred.add(complex);
                }
            }
        }

        return inferred;
    }
}
