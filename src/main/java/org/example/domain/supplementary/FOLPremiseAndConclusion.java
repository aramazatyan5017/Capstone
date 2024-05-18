package org.example.domain.supplementary;

import org.example.domain.FOLPremise;
import org.example.domain.sentence.fol.FOLSentence;

/**
 * @author aram.azatyan | 5/15/2024 2:14 PM
 */
public record FOLPremiseAndConclusion(FOLPremise premise, FOLSentence conclusion) {

    public FOLPremiseAndConclusion {
        if (premise == null) throw new IllegalArgumentException("null param");
    }
}
