package org.example.inference_rules;

import org.example.domain.sentence.Clause;
import org.example.exception.NothingToInferException;
import static org.example.inference_rules.ResolutionInferenceRule.resolve;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author aram.azatyan | 3/29/2024 2:45 PM
 */
class ResolutionInferenceRuleTest {
    @Test
    void resolveAbnormal() {
        assertThrows(IllegalArgumentException.class, () -> resolve(null, null));
        assertThrows(IllegalArgumentException.class, () -> resolve(new Clause("A | B | C"), null));
        assertThrows(IllegalArgumentException.class, () -> resolve(null, new Clause("A | B | C")));
        assertThrows(NothingToInferException.class, () -> resolve(new Clause("A | B | !A"), new Clause("A | B | C")));
        assertThrows(NothingToInferException.class, () -> resolve(new Clause("A | B | C"), new Clause("A | B | !A")));
        assertThrows(NothingToInferException.class, () -> resolve(new Clause("A | B | !B | D"), new Clause("A | B | !A")));
        assertThrows(NothingToInferException.class, () -> resolve(new Clause("A | B | D"), new Clause("F | !B | !A")));
        assertThrows(NothingToInferException.class, () -> resolve(new Clause("!A | B | D"), new Clause("F | !B | A")));
        assertThrows(NothingToInferException.class, () -> resolve(new Clause("A | B | C"), new Clause("D | E | F")));
        assertThrows(NothingToInferException.class, () -> resolve(new Clause("A | B | C"), new Clause("D | E | A")));
    }

    @Test
    void resolveNormal() {
        try {
            assertEquals(new Clause("A | B | C | E | F"), resolve(new Clause("A | B | !D | C"), new Clause("D | A | E | F")));
            assertEquals(new Clause("A | B | C"), resolve(new Clause("A | B | !D | C"), new Clause("D")));
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }
}