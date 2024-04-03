package org.example.inference_rules;

import org.example.domain.Connective;
import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Clause;
import org.example.domain.sentence.GenericComplexSentence;
import org.example.domain.sentence.Sentence;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author aram.azatyan | 3/27/2024 4:03 PM
 */
// TODO: 3/27/2024 im karciqov, esi chpiti patasxanatu lini satisfiablitii hamar
public class AndEliminationInferenceRule {

    public static Set<Sentence> infer(Sentence sentence) {
        if (sentence == null) throw new IllegalArgumentException("null param");

        Set<Sentence> inferred = new HashSet<>();

        switch (sentence.type()) {
            case LITERAL -> inferred.add(sentence);
            case CLAUSE -> inferred.add(((Clause) sentence).size() == 1 ? ((Clause) sentence).getLiteralList().get(0) : sentence);
            case CNF -> ((CNFSentence) sentence).getClauses().forEach(clause -> inferred
                        .add(clause.size() == 1 ? clause.getLiteralList().get(0) : clause));
            case GENERIC_COMPLEX -> {
                GenericComplexSentence complex = (GenericComplexSentence) sentence;

                if (complex.getConnective() == Connective.AND) {
                    try {
                        inferred.addAll(infer(Sentence.optimizedParse(complex.getLeftSentence().toString())));
                        inferred.addAll(infer(Sentence.optimizedParse(complex.getRightSentence().toString())));
                    } catch (ParseException ignored) {}
                } else {
                    inferred.add(complex);
                }
            }
        }

        return inferred;
    }
}
