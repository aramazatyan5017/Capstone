package org.example.inference_rules;

import org.example.domain.sentence.*;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.example.inference_rules.AndEliminationInferenceRule.infer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author aram.azatyan | 3/29/2024 2:20 PM
 */
class AndEliminationInferenceRuleTest {
    @Test
    void inferTest() {
        try {
            assertThrows(IllegalArgumentException.class, () -> infer(null));

            Set<Sentence> set = new HashSet<>();
            set.add(new Literal("A"));
            assertEquals(set, infer(new Literal("A")));

            set = new HashSet<>();
            set.add(new Literal("A"));
            assertEquals(set, infer(new Clause("A")));

            set = new HashSet<>();
            set.add(new Literal("A"));
            assertEquals(set, infer(new CNFSentence("A")));

            set = new HashSet<>();
            set.add(new Clause("A | B | C | !D"));
            assertEquals(set, infer(new Clause("A | B | C | !D")));

            set = new HashSet<>();
            set.add(new Clause("A | B | !A | C | D"));
            assertEquals(set, infer(new Clause("A | B | !A | C | D")));

            set = new HashSet<>();
            set.add(new Clause("A | B | C"));
            assertEquals(set, infer(new CNFSentence("A | B | C")));

            set = new HashSet<>();
            set.add(new Clause("A | B | C"));
            set.add(new Literal("!A"));
            set.add(new Clause("B | C"));
            assertEquals(set, infer(new CNFSentence("(A | B | C) & (B | C) & !A")));

            set = new HashSet<>();
            set.add(new Literal("A"));
            set.add(new Literal("B"));
            set.add(new Literal("C"));
            assertEquals(set, infer(new GenericComplexSentence("A & B & C")));

            set = new HashSet<>();
            set.add(new GenericComplexSentence("(A | B => C)"));
            set.add(new Literal("E"));
            set.add(new Clause("A | C"));
            assertEquals(set, infer(new GenericComplexSentence("(A | B => C) & ((A | C) & E)")));

        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }

    }
}