package org.example.domain.supplementary;

import org.example.domain.FOLPremise;
import org.example.domain.sentence.fol.FOLSentence;
import org.example.domain.sentence.fol.term.Variable;

import java.util.Objects;

/**
 * @author aram.azatyan | 5/14/2024 5:27 PM
 */
public record SentenceAndVariable(FOLPremise premise, FOLSentence conclusion, Variable variable) {
    public SentenceAndVariable {
        if (premise == null || variable == null) throw new IllegalArgumentException("null param");
    }
}
