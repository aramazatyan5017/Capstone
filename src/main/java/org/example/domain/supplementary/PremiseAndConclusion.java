package org.example.domain.supplementary;

import org.example.domain.Premise;
import org.example.domain.sentence.Sentence;

/**
 * @author aram.azatyan | 3/25/2024 4:48 PM
 */
public record PremiseAndConclusion(Premise premise, Sentence conclusion) {

    public PremiseAndConclusion {
        if (premise == null) throw new IllegalArgumentException("null param");
    }
}
