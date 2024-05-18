package org.example.inference_rules;

import org.example.domain.sentence.propositional.PropositionalClause;
import org.example.exception.NothingToInferException;
import static org.example.inference_rules.PropositionalResolutionInferenceRule.resolve;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author aram.azatyan | 3/29/2024 2:45 PM
 */
class PropositionalResolutionInferenceRuleTest {
    @Test
    void resolveAbnormal() {
        assertThrows(IllegalArgumentException.class, () -> resolve(null, null));
        assertThrows(IllegalArgumentException.class, () -> resolve(new PropositionalClause("A | B | C"), null));
        assertThrows(IllegalArgumentException.class, () -> resolve(null, new PropositionalClause("A | B | C")));
        assertThrows(NothingToInferException.class, () -> resolve(new PropositionalClause("A | B | !A"), new PropositionalClause("A | B | C")));
        assertThrows(NothingToInferException.class, () -> resolve(new PropositionalClause("A | B | C"), new PropositionalClause("A | B | !A")));
        assertThrows(NothingToInferException.class, () -> resolve(new PropositionalClause("A | B | !B | D"), new PropositionalClause("A | B | !A")));
        assertThrows(NothingToInferException.class, () -> resolve(new PropositionalClause("A | B | D"), new PropositionalClause("F | !B | !A")));
        assertThrows(NothingToInferException.class, () -> resolve(new PropositionalClause("!A | B | D"), new PropositionalClause("F | !B | A")));
        assertThrows(NothingToInferException.class, () -> resolve(new PropositionalClause("A | B | C"), new PropositionalClause("D | E | F")));
        assertThrows(NothingToInferException.class, () -> resolve(new PropositionalClause("A | B | C"), new PropositionalClause("D | E | A")));
    }

    @Test
    void resolveNormal() {
        try {
            assertEquals(new PropositionalClause("A | B | C | E | F"), resolve(new PropositionalClause("A | B | !D | C"), new PropositionalClause("D | A | E | F")));
            assertEquals(new PropositionalClause("A | B | C"), resolve(new PropositionalClause("A | B | !D | C"), new PropositionalClause("D")));
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }
}