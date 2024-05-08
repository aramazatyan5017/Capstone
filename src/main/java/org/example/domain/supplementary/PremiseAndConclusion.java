package org.example.domain.supplementary;

import org.example.domain.PropositionalPremise;
import org.example.domain.sentence.Sentence;
import org.example.domain.sentence.propositional.PropositionalSentence;

/**
 * @author aram.azatyan | 3/25/2024 4:48 PM
 */
public record PremiseAndConclusion(PropositionalPremise premise, PropositionalSentence conclusion) {

    public PremiseAndConclusion {
        if (premise == null) throw new IllegalArgumentException("null param");
    }
}
