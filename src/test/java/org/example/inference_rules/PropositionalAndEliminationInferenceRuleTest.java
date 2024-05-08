package org.example.inference_rules;

import org.example.domain.sentence.*;
import org.example.domain.sentence.propositional.GenericComplexPropositionalSentence;
import org.example.domain.sentence.propositional.Literal;
import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.sentence.propositional.PropositionalClause;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.example.inference_rules.PropositionalAndEliminationInferenceRule.infer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author aram.azatyan | 3/29/2024 2:20 PM
 */
class PropositionalAndEliminationInferenceRuleTest {
    @Test
    void inferTest() {
        try {
            Set<Sentence> set = new HashSet<>();
            set.add(new Literal("A"));
            set.add(new Literal("B"));
            set.add(new Literal("C"));
            assertEquals(set, infer(new GenericComplexPropositionalSentence("A & B & C")));


            assertThrows(IllegalArgumentException.class, () -> infer(null));

            set = new HashSet<>();
            set.add(new Literal("A"));
            assertEquals(set, infer(new Literal("A")));

            set = new HashSet<>();
            set.add(new Literal("A"));
            assertEquals(set, infer(new PropositionalClause("A")));

            set = new HashSet<>();
            set.add(new Literal("A"));
            assertEquals(set, infer(new PropositionalCNFSentence("A")));

            set = new HashSet<>();
            set.add(new PropositionalClause("A | B | C | !D"));
            assertEquals(set, infer(new PropositionalClause("A | B | C | !D")));

            set = new HashSet<>();
            set.add(new PropositionalClause("A | B | !A | C | D"));
            assertEquals(set, infer(new PropositionalClause("A | B | !A | C | D")));

            set = new HashSet<>();
            set.add(new PropositionalClause("A | B | C"));
            assertEquals(set, infer(new PropositionalCNFSentence("A | B | C")));

            set = new HashSet<>();
            set.add(new PropositionalClause("A | B | C"));
            set.add(new Literal("!A"));
            set.add(new PropositionalClause("B | C"));
            assertEquals(set, infer(new PropositionalCNFSentence("(A | B | C) & (B | C) & !A")));

            set = new HashSet<>();
            set.add(new GenericComplexPropositionalSentence("(A | B => C)"));
            set.add(new Literal("E"));
            set.add(new PropositionalClause("A | C"));
            assertEquals(set, infer(new GenericComplexPropositionalSentence("(A | B => C) & ((A | C) & E)")));

        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }

    }
}