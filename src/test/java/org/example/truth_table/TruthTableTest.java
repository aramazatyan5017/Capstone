package org.example.truth_table;

import org.example.domain.Connective;
import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Clause;
import org.example.domain.sentence.GenericComplexSentence;
import org.example.domain.sentence.Literal;
import org.example.domain.supplementary.TruthTableRow;
import org.example.exception.TautologyException;
import org.example.exception.UnsatisfiableException;
import org.example.util.SentenceUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author aram.azatyan | 3/19/2024 12:56 PM
 */
class TruthTableTest {

    @Test
    void createTruthTableAbnormal() {
        assertThrows(IllegalArgumentException.class, () -> new TruthTable(null));
        assertThrows(UnsatisfiableException.class, () -> new TruthTable(new GenericComplexSentence("!a & !b & !c & (a | b | c)")));
        assertThrows(UnsatisfiableException.class, () -> new TruthTable(new CNFSentence("!a & !b & !c & (a | b | c)")));
        assertThrows(UnsatisfiableException.class, () -> new TruthTable(new GenericComplexSentence("a & !a")));
        assertThrows(TautologyException.class, () -> new TruthTable(new GenericComplexSentence("(a | !a | b) & (c | d | !c)")));
        assertThrows(TautologyException.class, () -> new TruthTable(new CNFSentence("(a | !a | b) & (c | d | !c)")));
        assertThrows(TautologyException.class, () -> new TruthTable(new Clause("(a | !a | b)")));
    }

    @Test
    void createTruthTableNormal() {
        try {
            TruthTable table = new TruthTable(new Literal("A", true));
            table = new TruthTable(new Clause("a | b | !c"));
            table = new TruthTable(new CNFSentence("(a | b) & (c | d)"));
            table = new TruthTable(new GenericComplexSentence("a => b & c <=> d"));
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    void literalTableIteratorTest() {
        try {
            TruthTable table = new TruthTable(new Literal("A", true));
            assertTrue(compareMyTruthTableWithTheirs(table, """
                      1  │  0
                      0  │  1
                    """));
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    void clauseTableIteratorTest() {
        try {
            TruthTable table = new TruthTable(new Clause("a | c | b"));
            assertTrue(compareMyTruthTableWithTheirs(table, """
                    1 1 1  │  1
                      1 1 0  │  1
                      1 0 1  │  1
                      1 0 0  │  1
                      0 1 1  │  1
                      0 1 0  │  1
                      0 0 1  │  1
                      0 0 0  │  0   
                    """));

            table = new TruthTable(new Clause("(a | b | !d | e | !c)"));
            assertTrue(compareMyTruthTableWithTheirs(table, """
                    1 1 1 1 1  │  1
                      1 1 1 1 0  │  1
                      1 1 1 0 1  │  1
                      1 1 1 0 0  │  1
                      1 1 0 1 1  │  1
                      1 1 0 1 0  │  1
                      1 1 0 0 1  │  1
                      1 1 0 0 0  │  1
                      1 0 1 1 1  │  1
                      1 0 1 1 0  │  1
                      1 0 1 0 1  │  1
                      1 0 1 0 0  │  1
                      1 0 0 1 1  │  1
                      1 0 0 1 0  │  1
                      1 0 0 0 1  │  1
                      1 0 0 0 0  │  1
                      0 1 1 1 1  │  1
                      0 1 1 1 0  │  1
                      0 1 1 0 1  │  1
                      0 1 1 0 0  │  1
                      0 1 0 1 1  │  1
                      0 1 0 1 0  │  1
                      0 1 0 0 1  │  1
                      0 1 0 0 0  │  1
                      0 0 1 1 1  │  1
                      0 0 1 1 0  │  0
                      0 0 1 0 1  │  1
                      0 0 1 0 0  │  1
                      0 0 0 1 1  │  1
                      0 0 0 1 0  │  1
                      0 0 0 0 1  │  1
                      0 0 0 0 0  │  1
                    """));
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    void cnfTableIteratorTest() {
        try {
            TruthTable table = new TruthTable(new CNFSentence("a | b | c"));
            assertTrue(compareMyTruthTableWithTheirs(table, """
                    1 1 1  │  1
                      1 1 0  │  1
                      1 0 1  │  1
                      1 0 0  │  1
                      0 1 1  │  1
                      0 1 0  │  1
                      0 0 1  │  1
                      0 0 0  │  0
                    """));

            table = new TruthTable(new CNFSentence("(b | !c | e) & d & (a | d)"));
            assertTrue(compareMyTruthTableWithTheirs(table, """
                     1 1 1 1 1  │  1
                      1 1 1 1 0  │  1
                      1 1 1 0 1  │  0
                      1 1 1 0 0  │  0
                      1 1 0 1 1  │  1
                      1 1 0 1 0  │  1
                      1 1 0 0 1  │  0
                      1 1 0 0 0  │  0
                      1 0 1 1 1  │  1
                      1 0 1 1 0  │  0
                      1 0 1 0 1  │  0
                      1 0 1 0 0  │  0
                      1 0 0 1 1  │  1
                      1 0 0 1 0  │  1
                      1 0 0 0 1  │  0
                      1 0 0 0 0  │  0
                      0 1 1 1 1  │  1
                      0 1 1 1 0  │  1
                      0 1 1 0 1  │  0
                      0 1 1 0 0  │  0
                      0 1 0 1 1  │  1
                      0 1 0 1 0  │  1
                      0 1 0 0 1  │  0
                      0 1 0 0 0  │  0
                      0 0 1 1 1  │  1
                      0 0 1 1 0  │  0
                      0 0 1 0 1  │  0
                      0 0 1 0 0  │  0
                      0 0 0 1 1  │  1
                      0 0 0 1 0  │  1
                      0 0 0 0 1  │  0
                      0 0 0 0 0  │  0
                    """));

            table = new TruthTable(new CNFSentence("(b | c) & (b | c | e | a | d)"));
            assertTrue(compareMyTruthTableWithTheirs(table, """
                    1 1 1 1 1  │  1
                      1 1 1 1 0  │  1
                      1 1 1 0 1  │  1
                      1 1 1 0 0  │  1
                      1 1 0 1 1  │  1
                      1 1 0 1 0  │  1
                      1 1 0 0 1  │  1
                      1 1 0 0 0  │  1
                      1 0 1 1 1  │  1
                      1 0 1 1 0  │  1
                      1 0 1 0 1  │  1
                      1 0 1 0 0  │  1
                      1 0 0 1 1  │  0
                      1 0 0 1 0  │  0
                      1 0 0 0 1  │  0
                      1 0 0 0 0  │  0
                      0 1 1 1 1  │  1
                      0 1 1 1 0  │  1
                      0 1 1 0 1  │  1
                      0 1 1 0 0  │  1
                      0 1 0 1 1  │  1
                      0 1 0 1 0  │  1
                      0 1 0 0 1  │  1
                      0 1 0 0 0  │  1
                      0 0 1 1 1  │  1
                      0 0 1 1 0  │  1
                      0 0 1 0 1  │  1
                      0 0 1 0 0  │  1
                      0 0 0 1 1  │  0
                      0 0 0 1 0  │  0
                      0 0 0 0 1  │  0
                      0 0 0 0 0  │  0
                    """));
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    void genericTableIteratorTest() {
        try {
            TruthTable table = new TruthTable(new GenericComplexSentence("!A => b <=> C | D & A & E => G | P"));
            assertTrue(compareMyTruthTableWithTheirs(table, """
                     1 1 1 1 1 1 1  │  1
                      1 1 1 1 1 1 0  │  1
                      1 1 1 1 1 0 1  │  1
                      1 1 1 1 1 0 0  │  1
                      1 1 1 1 0 1 1  │  1
                      1 1 1 1 0 1 0  │  1
                      1 1 1 1 0 0 1  │  0
                      1 1 1 1 0 0 0  │  0
                      1 1 1 0 1 1 1  │  1
                      1 1 1 0 1 1 0  │  1
                      1 1 1 0 1 0 1  │  1
                      1 1 1 0 1 0 0  │  1
                      1 1 1 0 0 1 1  │  1
                      1 1 1 0 0 1 0  │  1
                      1 1 1 0 0 0 1  │  0
                      1 1 1 0 0 0 0  │  0
                      1 1 0 1 1 1 1  │  1
                      1 1 0 1 1 1 0  │  1
                      1 1 0 1 1 0 1  │  1
                      1 1 0 1 1 0 0  │  1
                      1 1 0 1 0 1 1  │  1
                      1 1 0 1 0 1 0  │  1
                      1 1 0 1 0 0 1  │  0
                      1 1 0 1 0 0 0  │  0
                      1 1 0 0 1 1 1  │  1
                      1 1 0 0 1 1 0  │  1
                      1 1 0 0 1 0 1  │  1
                      1 1 0 0 1 0 0  │  1
                      1 1 0 0 0 1 1  │  1
                      1 1 0 0 0 1 0  │  1
                      1 1 0 0 0 0 1  │  0
                      1 1 0 0 0 0 0  │  0
                      1 0 1 1 1 1 1  │  1
                      1 0 1 1 1 1 0  │  1
                      1 0 1 1 1 0 1  │  1
                      1 0 1 1 1 0 0  │  1
                      1 0 1 1 0 1 1  │  1
                      1 0 1 1 0 1 0  │  1
                      1 0 1 1 0 0 1  │  0
                      1 0 1 1 0 0 0  │  0
                      1 0 1 0 1 1 1  │  1
                      1 0 1 0 1 1 0  │  1
                      1 0 1 0 1 0 1  │  1
                      1 0 1 0 1 0 0  │  1
                      1 0 1 0 0 1 1  │  1
                      1 0 1 0 0 1 0  │  1
                      1 0 1 0 0 0 1  │  1
                      1 0 1 0 0 0 0  │  1
                      1 0 0 1 1 1 1  │  1
                      1 0 0 1 1 1 0  │  1
                      1 0 0 1 1 0 1  │  1
                      1 0 0 1 1 0 0  │  1
                      1 0 0 1 0 1 1  │  1
                      1 0 0 1 0 1 0  │  1
                      1 0 0 1 0 0 1  │  1
                      1 0 0 1 0 0 0  │  1
                      1 0 0 0 1 1 1  │  1
                      1 0 0 0 1 1 0  │  1
                      1 0 0 0 1 0 1  │  1
                      1 0 0 0 1 0 0  │  1
                      1 0 0 0 0 1 1  │  1
                      1 0 0 0 0 1 0  │  1
                      1 0 0 0 0 0 1  │  1
                      1 0 0 0 0 0 0  │  1
                      0 1 1 1 1 1 1  │  1
                      0 1 1 1 1 1 0  │  0
                      0 1 1 1 1 0 1  │  1
                      0 1 1 1 1 0 0  │  0
                      0 1 1 1 0 1 1  │  1
                      0 1 1 1 0 1 0  │  0
                      0 1 1 1 0 0 1  │  0
                      0 1 1 1 0 0 0  │  1
                      0 1 1 0 1 1 1  │  1
                      0 1 1 0 1 1 0  │  0
                      0 1 1 0 1 0 1  │  1
                      0 1 1 0 1 0 0  │  0
                      0 1 1 0 0 1 1  │  1
                      0 1 1 0 0 1 0  │  0
                      0 1 1 0 0 0 1  │  0
                      0 1 1 0 0 0 0  │  1
                      0 1 0 1 1 1 1  │  1
                      0 1 0 1 1 1 0  │  0
                      0 1 0 1 1 0 1  │  1
                      0 1 0 1 1 0 0  │  0
                      0 1 0 1 0 1 1  │  1
                      0 1 0 1 0 1 0  │  0
                      0 1 0 1 0 0 1  │  0
                      0 1 0 1 0 0 0  │  1
                      0 1 0 0 1 1 1  │  1
                      0 1 0 0 1 1 0  │  0
                      0 1 0 0 1 0 1  │  1
                      0 1 0 0 1 0 0  │  0
                      0 1 0 0 0 1 1  │  1
                      0 1 0 0 0 1 0  │  0
                      0 1 0 0 0 0 1  │  0
                      0 1 0 0 0 0 0  │  1
                      0 0 1 1 1 1 1  │  1
                      0 0 1 1 1 1 0  │  0
                      0 0 1 1 1 0 1  │  1
                      0 0 1 1 1 0 0  │  0
                      0 0 1 1 0 1 1  │  1
                      0 0 1 1 0 1 0  │  0
                      0 0 1 1 0 0 1  │  1
                      0 0 1 1 0 0 0  │  0
                      0 0 1 0 1 1 1  │  1
                      0 0 1 0 1 1 0  │  0
                      0 0 1 0 1 0 1  │  1
                      0 0 1 0 1 0 0  │  0
                      0 0 1 0 0 1 1  │  1
                      0 0 1 0 0 1 0  │  0
                      0 0 1 0 0 0 1  │  1
                      0 0 1 0 0 0 0  │  0
                      0 0 0 1 1 1 1  │  1
                      0 0 0 1 1 1 0  │  0
                      0 0 0 1 1 0 1  │  1
                      0 0 0 1 1 0 0  │  0
                      0 0 0 1 0 1 1  │  1
                      0 0 0 1 0 1 0  │  0
                      0 0 0 1 0 0 1  │  1
                      0 0 0 1 0 0 0  │  0
                      0 0 0 0 1 1 1  │  1
                      0 0 0 0 1 1 0  │  0
                      0 0 0 0 1 0 1  │  1
                      0 0 0 0 1 0 0  │  0
                      0 0 0 0 0 1 1  │  1
                      0 0 0 0 0 1 0  │  0
                      0 0 0 0 0 0 1  │  1
                      0 0 0 0 0 0 0  │  0
                    """));

            table = new TruthTable(new GenericComplexSentence("(b | !c | e) & d & (a | d)"));
            assertTrue(compareMyTruthTableWithTheirs(table, """
                    1 1 1 1 1  │  1
                      1 1 1 1 0  │  1
                      1 1 1 0 1  │  0
                      1 1 1 0 0  │  0
                      1 1 0 1 1  │  1
                      1 1 0 1 0  │  1
                      1 1 0 0 1  │  0
                      1 1 0 0 0  │  0
                      1 0 1 1 1  │  1
                      1 0 1 1 0  │  0
                      1 0 1 0 1  │  0
                      1 0 1 0 0  │  0
                      1 0 0 1 1  │  1
                      1 0 0 1 0  │  1
                      1 0 0 0 1  │  0
                      1 0 0 0 0  │  0
                      0 1 1 1 1  │  1
                      0 1 1 1 0  │  1
                      0 1 1 0 1  │  0
                      0 1 1 0 0  │  0
                      0 1 0 1 1  │  1
                      0 1 0 1 0  │  1
                      0 1 0 0 1  │  0
                      0 1 0 0 0  │  0
                      0 0 1 1 1  │  1
                      0 0 1 1 0  │  0
                      0 0 1 0 1  │  0
                      0 0 1 0 0  │  0
                      0 0 0 1 1  │  1
                      0 0 0 1 0  │  1
                      0 0 0 0 1  │  0
                      0 0 0 0 0  │  0
                    """));

            table = new TruthTable(new GenericComplexSentence(SentenceUtils.convertOnlineCalculatorString("(¬A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ E) ∧ " +
                    "(¬A ∨ ¬B ∨ C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ ¬E) ∧ " +
                    "(¬A ∨ B ∨ ¬C ∨ D ∨ E) ∧ (¬A ∨ B ∨ C ∨ D ∨ ¬E) ∧ (¬A ∨ B ∨ C ∨ D ∨ E) ∧ (A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E) ∧ " +
                    "(A ∨ ¬B ∨ ¬C ∨ D ∨ E) ∧ (A ∨ ¬B ∨ C ∨ D ∨ ¬E) ∧ (A ∨ ¬B ∨ C ∨ D ∨ E) ∧ (A ∨ B ∨ ¬C ∨ ¬D ∨ E) ∧ " +
                    "(A ∨ B ∨ ¬C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ ¬C ∨ D ∨ E) ∧ (A ∨ B ∨ C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ D ∨ E)")));
            assertTrue(compareMyTruthTableWithTheirs(table, """
                    1 1 1 1 1  │  1
                      1 1 1 1 0  │  1
                      1 1 1 0 1  │  0
                      1 1 1 0 0  │  0
                      1 1 0 1 1  │  1
                      1 1 0 1 0  │  1
                      1 1 0 0 1  │  0
                      1 1 0 0 0  │  0
                      1 0 1 1 1  │  1
                      1 0 1 1 0  │  0
                      1 0 1 0 1  │  0
                      1 0 1 0 0  │  0
                      1 0 0 1 1  │  1
                      1 0 0 1 0  │  1
                      1 0 0 0 1  │  0
                      1 0 0 0 0  │  0
                      0 1 1 1 1  │  1
                      0 1 1 1 0  │  1
                      0 1 1 0 1  │  0
                      0 1 1 0 0  │  0
                      0 1 0 1 1  │  1
                      0 1 0 1 0  │  1
                      0 1 0 0 1  │  0
                      0 1 0 0 0  │  0
                      0 0 1 1 1  │  1
                      0 0 1 1 0  │  0
                      0 0 1 0 1  │  0
                      0 0 1 0 0  │  0
                      0 0 0 1 1  │  1
                      0 0 0 1 0  │  1
                      0 0 0 0 1  │  0
                      0 0 0 0 0  │  0
                    """));

            GenericComplexSentence sentence = new GenericComplexSentence(new GenericComplexSentence("(A <=> B | C & (D | E))"),
                    new CNFSentence("(A | !B) & (Q | G | H) & (E)"), Connective.BICONDITIONAL, true);
            TruthTable genericSentenceTable = new TruthTable(sentence);
            assertTrue(compareMyTruthTableWithTheirs(genericSentenceTable, """
                     1 1 1 1 1 1 1 1  │  0
                      1 1 1 1 1 1 1 0  │  0
                      1 1 1 1 1 1 0 1  │  0
                      1 1 1 1 1 1 0 0  │  0
                      1 1 1 1 1 0 1 1  │  0
                      1 1 1 1 1 0 1 0  │  0
                      1 1 1 1 1 0 0 1  │  0
                      1 1 1 1 1 0 0 0  │  1
                      1 1 1 1 0 1 1 1  │  1
                      1 1 1 1 0 1 1 0  │  1
                      1 1 1 1 0 1 0 1  │  1
                      1 1 1 1 0 1 0 0  │  1
                      1 1 1 1 0 0 1 1  │  1
                      1 1 1 1 0 0 1 0  │  1
                      1 1 1 1 0 0 0 1  │  1
                      1 1 1 1 0 0 0 0  │  1
                      1 1 1 0 1 1 1 1  │  0
                      1 1 1 0 1 1 1 0  │  0
                      1 1 1 0 1 1 0 1  │  0
                      1 1 1 0 1 1 0 0  │  0
                      1 1 1 0 1 0 1 1  │  0
                      1 1 1 0 1 0 1 0  │  0
                      1 1 1 0 1 0 0 1  │  0
                      1 1 1 0 1 0 0 0  │  1
                      1 1 1 0 0 1 1 1  │  1
                      1 1 1 0 0 1 1 0  │  1
                      1 1 1 0 0 1 0 1  │  1
                      1 1 1 0 0 1 0 0  │  1
                      1 1 1 0 0 0 1 1  │  1
                      1 1 1 0 0 0 1 0  │  1
                      1 1 1 0 0 0 0 1  │  1
                      1 1 1 0 0 0 0 0  │  1
                      1 1 0 1 1 1 1 1  │  0
                      1 1 0 1 1 1 1 0  │  0
                      1 1 0 1 1 1 0 1  │  0
                      1 1 0 1 1 1 0 0  │  0
                      1 1 0 1 1 0 1 1  │  0
                      1 1 0 1 1 0 1 0  │  0
                      1 1 0 1 1 0 0 1  │  0
                      1 1 0 1 1 0 0 0  │  1
                      1 1 0 1 0 1 1 1  │  1
                      1 1 0 1 0 1 1 0  │  1
                      1 1 0 1 0 1 0 1  │  1
                      1 1 0 1 0 1 0 0  │  1
                      1 1 0 1 0 0 1 1  │  1
                      1 1 0 1 0 0 1 0  │  1
                      1 1 0 1 0 0 0 1  │  1
                      1 1 0 1 0 0 0 0  │  1
                      1 1 0 0 1 1 1 1  │  0
                      1 1 0 0 1 1 1 0  │  0
                      1 1 0 0 1 1 0 1  │  0
                      1 1 0 0 1 1 0 0  │  0
                      1 1 0 0 1 0 1 1  │  0
                      1 1 0 0 1 0 1 0  │  0
                      1 1 0 0 1 0 0 1  │  0
                      1 1 0 0 1 0 0 0  │  1
                      1 1 0 0 0 1 1 1  │  1
                      1 1 0 0 0 1 1 0  │  1
                      1 1 0 0 0 1 0 1  │  1
                      1 1 0 0 0 1 0 0  │  1
                      1 1 0 0 0 0 1 1  │  1
                      1 1 0 0 0 0 1 0  │  1
                      1 1 0 0 0 0 0 1  │  1
                      1 1 0 0 0 0 0 0  │  1
                      1 0 1 1 1 1 1 1  │  0
                      1 0 1 1 1 1 1 0  │  0
                      1 0 1 1 1 1 0 1  │  0
                      1 0 1 1 1 1 0 0  │  0
                      1 0 1 1 1 0 1 1  │  0
                      1 0 1 1 1 0 1 0  │  0
                      1 0 1 1 1 0 0 1  │  0
                      1 0 1 1 1 0 0 0  │  1
                      1 0 1 1 0 1 1 1  │  1
                      1 0 1 1 0 1 1 0  │  1
                      1 0 1 1 0 1 0 1  │  1
                      1 0 1 1 0 1 0 0  │  1
                      1 0 1 1 0 0 1 1  │  1
                      1 0 1 1 0 0 1 0  │  1
                      1 0 1 1 0 0 0 1  │  1
                      1 0 1 1 0 0 0 0  │  1
                      1 0 1 0 1 1 1 1  │  0
                      1 0 1 0 1 1 1 0  │  0
                      1 0 1 0 1 1 0 1  │  0
                      1 0 1 0 1 1 0 0  │  0
                      1 0 1 0 1 0 1 1  │  0
                      1 0 1 0 1 0 1 0  │  0
                      1 0 1 0 1 0 0 1  │  0
                      1 0 1 0 1 0 0 0  │  1
                      1 0 1 0 0 1 1 1  │  0
                      1 0 1 0 0 1 1 0  │  0
                      1 0 1 0 0 1 0 1  │  0
                      1 0 1 0 0 1 0 0  │  0
                      1 0 1 0 0 0 1 1  │  0
                      1 0 1 0 0 0 1 0  │  0
                      1 0 1 0 0 0 0 1  │  0
                      1 0 1 0 0 0 0 0  │  0
                      1 0 0 1 1 1 1 1  │  1
                      1 0 0 1 1 1 1 0  │  1
                      1 0 0 1 1 1 0 1  │  1
                      1 0 0 1 1 1 0 0  │  1
                      1 0 0 1 1 0 1 1  │  1
                      1 0 0 1 1 0 1 0  │  1
                      1 0 0 1 1 0 0 1  │  1
                      1 0 0 1 1 0 0 0  │  0
                      1 0 0 1 0 1 1 1  │  0
                      1 0 0 1 0 1 1 0  │  0
                      1 0 0 1 0 1 0 1  │  0
                      1 0 0 1 0 1 0 0  │  0
                      1 0 0 1 0 0 1 1  │  0
                      1 0 0 1 0 0 1 0  │  0
                      1 0 0 1 0 0 0 1  │  0
                      1 0 0 1 0 0 0 0  │  0
                      1 0 0 0 1 1 1 1  │  1
                      1 0 0 0 1 1 1 0  │  1
                      1 0 0 0 1 1 0 1  │  1
                      1 0 0 0 1 1 0 0  │  1
                      1 0 0 0 1 0 1 1  │  1
                      1 0 0 0 1 0 1 0  │  1
                      1 0 0 0 1 0 0 1  │  1
                      1 0 0 0 1 0 0 0  │  0
                      1 0 0 0 0 1 1 1  │  0
                      1 0 0 0 0 1 1 0  │  0
                      1 0 0 0 0 1 0 1  │  0
                      1 0 0 0 0 1 0 0  │  0
                      1 0 0 0 0 0 1 1  │  0
                      1 0 0 0 0 0 1 0  │  0
                      1 0 0 0 0 0 0 1  │  0
                      1 0 0 0 0 0 0 0  │  0
                      0 1 1 1 1 1 1 1  │  0
                      0 1 1 1 1 1 1 0  │  0
                      0 1 1 1 1 1 0 1  │  0
                      0 1 1 1 1 1 0 0  │  0
                      0 1 1 1 1 0 1 1  │  0
                      0 1 1 1 1 0 1 0  │  0
                      0 1 1 1 1 0 0 1  │  0
                      0 1 1 1 1 0 0 0  │  0
                      0 1 1 1 0 1 1 1  │  0
                      0 1 1 1 0 1 1 0  │  0
                      0 1 1 1 0 1 0 1  │  0
                      0 1 1 1 0 1 0 0  │  0
                      0 1 1 1 0 0 1 1  │  0
                      0 1 1 1 0 0 1 0  │  0
                      0 1 1 1 0 0 0 1  │  0
                      0 1 1 1 0 0 0 0  │  0
                      0 1 1 0 1 1 1 1  │  0
                      0 1 1 0 1 1 1 0  │  0
                      0 1 1 0 1 1 0 1  │  0
                      0 1 1 0 1 1 0 0  │  0
                      0 1 1 0 1 0 1 1  │  0
                      0 1 1 0 1 0 1 0  │  0
                      0 1 1 0 1 0 0 1  │  0
                      0 1 1 0 1 0 0 0  │  0
                      0 1 1 0 0 1 1 1  │  0
                      0 1 1 0 0 1 1 0  │  0
                      0 1 1 0 0 1 0 1  │  0
                      0 1 1 0 0 1 0 0  │  0
                      0 1 1 0 0 0 1 1  │  0
                      0 1 1 0 0 0 1 0  │  0
                      0 1 1 0 0 0 0 1  │  0
                      0 1 1 0 0 0 0 0  │  0
                      0 1 0 1 1 1 1 1  │  0
                      0 1 0 1 1 1 1 0  │  0
                      0 1 0 1 1 1 0 1  │  0
                      0 1 0 1 1 1 0 0  │  0
                      0 1 0 1 1 0 1 1  │  0
                      0 1 0 1 1 0 1 0  │  0
                      0 1 0 1 1 0 0 1  │  0
                      0 1 0 1 1 0 0 0  │  0
                      0 1 0 1 0 1 1 1  │  0
                      0 1 0 1 0 1 1 0  │  0
                      0 1 0 1 0 1 0 1  │  0
                      0 1 0 1 0 1 0 0  │  0
                      0 1 0 1 0 0 1 1  │  0
                      0 1 0 1 0 0 1 0  │  0
                      0 1 0 1 0 0 0 1  │  0
                      0 1 0 1 0 0 0 0  │  0
                      0 1 0 0 1 1 1 1  │  0
                      0 1 0 0 1 1 1 0  │  0
                      0 1 0 0 1 1 0 1  │  0
                      0 1 0 0 1 1 0 0  │  0
                      0 1 0 0 1 0 1 1  │  0
                      0 1 0 0 1 0 1 0  │  0
                      0 1 0 0 1 0 0 1  │  0
                      0 1 0 0 1 0 0 0  │  0
                      0 1 0 0 0 1 1 1  │  0
                      0 1 0 0 0 1 1 0  │  0
                      0 1 0 0 0 1 0 1  │  0
                      0 1 0 0 0 1 0 0  │  0
                      0 1 0 0 0 0 1 1  │  0
                      0 1 0 0 0 0 1 0  │  0
                      0 1 0 0 0 0 0 1  │  0
                      0 1 0 0 0 0 0 0  │  0
                      0 0 1 1 1 1 1 1  │  1
                      0 0 1 1 1 1 1 0  │  1
                      0 0 1 1 1 1 0 1  │  1
                      0 0 1 1 1 1 0 0  │  1
                      0 0 1 1 1 0 1 1  │  1
                      0 0 1 1 1 0 1 0  │  1
                      0 0 1 1 1 0 0 1  │  1
                      0 0 1 1 1 0 0 0  │  0
                      0 0 1 1 0 1 1 1  │  0
                      0 0 1 1 0 1 1 0  │  0
                      0 0 1 1 0 1 0 1  │  0
                      0 0 1 1 0 1 0 0  │  0
                      0 0 1 1 0 0 1 1  │  0
                      0 0 1 1 0 0 1 0  │  0
                      0 0 1 1 0 0 0 1  │  0
                      0 0 1 1 0 0 0 0  │  0
                      0 0 1 0 1 1 1 1  │  1
                      0 0 1 0 1 1 1 0  │  1
                      0 0 1 0 1 1 0 1  │  1
                      0 0 1 0 1 1 0 0  │  1
                      0 0 1 0 1 0 1 1  │  1
                      0 0 1 0 1 0 1 0  │  1
                      0 0 1 0 1 0 0 1  │  1
                      0 0 1 0 1 0 0 0  │  0
                      0 0 1 0 0 1 1 1  │  1
                      0 0 1 0 0 1 1 0  │  1
                      0 0 1 0 0 1 0 1  │  1
                      0 0 1 0 0 1 0 0  │  1
                      0 0 1 0 0 0 1 1  │  1
                      0 0 1 0 0 0 1 0  │  1
                      0 0 1 0 0 0 0 1  │  1
                      0 0 1 0 0 0 0 0  │  1
                      0 0 0 1 1 1 1 1  │  0
                      0 0 0 1 1 1 1 0  │  0
                      0 0 0 1 1 1 0 1  │  0
                      0 0 0 1 1 1 0 0  │  0
                      0 0 0 1 1 0 1 1  │  0
                      0 0 0 1 1 0 1 0  │  0
                      0 0 0 1 1 0 0 1  │  0
                      0 0 0 1 1 0 0 0  │  1
                      0 0 0 1 0 1 1 1  │  1
                      0 0 0 1 0 1 1 0  │  1
                      0 0 0 1 0 1 0 1  │  1
                      0 0 0 1 0 1 0 0  │  1
                      0 0 0 1 0 0 1 1  │  1
                      0 0 0 1 0 0 1 0  │  1
                      0 0 0 1 0 0 0 1  │  1
                      0 0 0 1 0 0 0 0  │  1
                      0 0 0 0 1 1 1 1  │  0
                      0 0 0 0 1 1 1 0  │  0
                      0 0 0 0 1 1 0 1  │  0
                      0 0 0 0 1 1 0 0  │  0
                      0 0 0 0 1 0 1 1  │  0
                      0 0 0 0 1 0 1 0  │  0
                      0 0 0 0 1 0 0 1  │  0
                      0 0 0 0 1 0 0 0  │  1
                      0 0 0 0 0 1 1 1  │  1
                      0 0 0 0 0 1 1 0  │  1
                      0 0 0 0 0 1 0 1  │  1
                      0 0 0 0 0 1 0 0  │  1
                      0 0 0 0 0 0 1 1  │  1
                      0 0 0 0 0 0 1 0  │  1
                      0 0 0 0 0 0 0 1  │  1
                      0 0 0 0 0 0 0 0  │  1
                    """));

            TruthTable genericEquivalentCCNFTable =
                    new TruthTable(new GenericComplexSentence(SentenceUtils.convertOnlineCalculatorString("(¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E ∨ ¬G ∨ ¬H ∨ Q) ∧ (¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E ∨ ¬G ∨ H ∨ ¬Q) ∧ (¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E ∨ ¬G ∨ H ∨ Q) ∧ (¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E ∨ G ∨ ¬H ∨ ¬Q) ∧ (¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E ∨ G ∨ ¬H ∨ Q) ∧ (¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E ∨ G ∨ H ∨ ¬Q) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E ∨ ¬G ∨ ¬H ∨ Q) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E ∨ ¬G ∨ H ∨ ¬Q) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E ∨ ¬G ∨ H ∨ Q) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E ∨ G ∨ ¬H ∨ ¬Q) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E ∨ G ∨ ¬H ∨ Q) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E ∨ G ∨ H ∨ ¬Q) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E ∨ ¬G ∨ ¬H ∨ Q) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E ∨ ¬G ∨ H ∨ ¬Q) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E ∨ ¬G ∨ H ∨ Q) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E ∨ G ∨ ¬H ∨ ¬Q) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E ∨ G ∨ ¬H ∨ Q) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E ∨ G ∨ H ∨ ¬Q) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ ¬E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ ¬E ∨ ¬G ∨ ¬H ∨ Q) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ ¬E ∨ ¬G ∨ H ∨ ¬Q) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ ¬E ∨ ¬G ∨ H ∨ Q) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ ¬E ∨ G ∨ ¬H ∨ ¬Q) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ ¬E ∨ G ∨ ¬H ∨ Q) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ ¬E ∨ G ∨ H ∨ ¬Q) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ ¬E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ ¬E ∨ ¬G ∨ ¬H ∨ Q) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ ¬E ∨ ¬G ∨ H ∨ ¬Q) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ ¬E ∨ ¬G ∨ H ∨ Q) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ ¬E ∨ G ∨ ¬H ∨ ¬Q) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ ¬E ∨ G ∨ ¬H ∨ Q) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ ¬E ∨ G ∨ H ∨ ¬Q) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ ¬E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ ¬E ∨ ¬G ∨ ¬H ∨ Q) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ ¬E ∨ ¬G ∨ H ∨ ¬Q) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ ¬E ∨ ¬G ∨ H ∨ Q) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ ¬E ∨ G ∨ ¬H ∨ ¬Q) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ ¬E ∨ G ∨ ¬H ∨ Q) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ ¬E ∨ G ∨ H ∨ ¬Q) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ E ∨ ¬G ∨ ¬H ∨ Q) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ E ∨ ¬G ∨ H ∨ ¬Q) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ E ∨ ¬G ∨ H ∨ Q) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ E ∨ G ∨ ¬H ∨ ¬Q) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ E ∨ G ∨ ¬H ∨ Q) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ E ∨ G ∨ H ∨ ¬Q) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ E ∨ G ∨ H ∨ Q) ∧ (¬A ∨ B ∨ C ∨ ¬D ∨ ¬E ∨ G ∨ H ∨ Q) ∧ (¬A ∨ B ∨ C ∨ ¬D ∨ E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (¬A ∨ B ∨ C ∨ ¬D ∨ E ∨ ¬G ∨ ¬H ∨ Q) ∧ (¬A ∨ B ∨ C ∨ ¬D ∨ E ∨ ¬G ∨ H ∨ ¬Q) ∧ (¬A ∨ B ∨ C ∨ ¬D ∨ E ∨ ¬G ∨ H ∨ Q) ∧ (¬A ∨ B ∨ C ∨ ¬D ∨ E ∨ G ∨ ¬H ∨ ¬Q) ∧ (¬A ∨ B ∨ C ∨ ¬D ∨ E ∨ G ∨ ¬H ∨ Q) ∧ (¬A ∨ B ∨ C ∨ ¬D ∨ E ∨ G ∨ H ∨ ¬Q) ∧ (¬A ∨ B ∨ C ∨ ¬D ∨ E ∨ G ∨ H ∨ Q) ∧ (¬A ∨ B ∨ C ∨ D ∨ ¬E ∨ G ∨ H ∨ Q) ∧ (¬A ∨ B ∨ C ∨ D ∨ E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (¬A ∨ B ∨ C ∨ D ∨ E ∨ ¬G ∨ ¬H ∨ Q) ∧ (¬A ∨ B ∨ C ∨ D ∨ E ∨ ¬G ∨ H ∨ ¬Q) ∧ (¬A ∨ B ∨ C ∨ D ∨ E ∨ ¬G ∨ H ∨ Q) ∧ (¬A ∨ B ∨ C ∨ D ∨ E ∨ G ∨ ¬H ∨ ¬Q) ∧ (¬A ∨ B ∨ C ∨ D ∨ E ∨ G ∨ ¬H ∨ Q) ∧ (¬A ∨ B ∨ C ∨ D ∨ E ∨ G ∨ H ∨ ¬Q) ∧ (¬A ∨ B ∨ C ∨ D ∨ E ∨ G ∨ H ∨ Q) ∧ (A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E ∨ ¬G ∨ ¬H ∨ Q) ∧ (A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E ∨ ¬G ∨ H ∨ ¬Q) ∧ (A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E ∨ ¬G ∨ H ∨ Q) ∧ (A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E ∨ G ∨ ¬H ∨ ¬Q) ∧ (A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E ∨ G ∨ ¬H ∨ Q) ∧ (A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E ∨ G ∨ H ∨ ¬Q) ∧ (A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E ∨ G ∨ H ∨ Q) ∧ (A ∨ ¬B ∨ ¬C ∨ ¬D ∨ E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (A ∨ ¬B ∨ ¬C ∨ ¬D ∨ E ∨ ¬G ∨ ¬H ∨ Q) ∧ (A ∨ ¬B ∨ ¬C ∨ ¬D ∨ E ∨ ¬G ∨ H ∨ ¬Q) ∧ (A ∨ ¬B ∨ ¬C ∨ ¬D ∨ E ∨ ¬G ∨ H ∨ Q) ∧ (A ∨ ¬B ∨ ¬C ∨ ¬D ∨ E ∨ G ∨ ¬H ∨ ¬Q) ∧ (A ∨ ¬B ∨ ¬C ∨ ¬D ∨ E ∨ G ∨ ¬H ∨ Q) ∧ (A ∨ ¬B ∨ ¬C ∨ ¬D ∨ E ∨ G ∨ H ∨ ¬Q) ∧ (A ∨ ¬B ∨ ¬C ∨ ¬D ∨ E ∨ G ∨ H ∨ Q) ∧ (A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E ∨ ¬G ∨ ¬H ∨ Q) ∧ (A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E ∨ ¬G ∨ H ∨ ¬Q) ∧ (A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E ∨ ¬G ∨ H ∨ Q) ∧ (A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E ∨ G ∨ ¬H ∨ ¬Q) ∧ (A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E ∨ G ∨ ¬H ∨ Q) ∧ (A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E ∨ G ∨ H ∨ ¬Q) ∧ (A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E ∨ G ∨ H ∨ Q) ∧ (A ∨ ¬B ∨ ¬C ∨ D ∨ E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (A ∨ ¬B ∨ ¬C ∨ D ∨ E ∨ ¬G ∨ ¬H ∨ Q) ∧ (A ∨ ¬B ∨ ¬C ∨ D ∨ E ∨ ¬G ∨ H ∨ ¬Q) ∧ (A ∨ ¬B ∨ ¬C ∨ D ∨ E ∨ ¬G ∨ H ∨ Q) ∧ (A ∨ ¬B ∨ ¬C ∨ D ∨ E ∨ G ∨ ¬H ∨ ¬Q) ∧ (A ∨ ¬B ∨ ¬C ∨ D ∨ E ∨ G ∨ ¬H ∨ Q) ∧ (A ∨ ¬B ∨ ¬C ∨ D ∨ E ∨ G ∨ H ∨ ¬Q) ∧ (A ∨ ¬B ∨ ¬C ∨ D ∨ E ∨ G ∨ H ∨ Q) ∧ (A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E ∨ ¬G ∨ ¬H ∨ Q) ∧ (A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E ∨ ¬G ∨ H ∨ ¬Q) ∧ (A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E ∨ ¬G ∨ H ∨ Q) ∧ (A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E ∨ G ∨ ¬H ∨ ¬Q) ∧ (A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E ∨ G ∨ ¬H ∨ Q) ∧ ( A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E ∨ G ∨ H ∨ ¬Q) ∧ (A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E ∨ G ∨ H ∨ Q) ∧ (A ∨ ¬B ∨ C ∨ ¬D ∨ E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (A ∨ ¬B ∨ C ∨ ¬D ∨ E ∨ ¬G ∨ ¬H ∨ Q) ∧ (A ∨ ¬B ∨ C ∨ ¬D ∨ E ∨ ¬G ∨ H ∨ ¬Q) ∧ (A ∨ ¬B ∨ C ∨ ¬D ∨ E ∨ ¬G ∨ H ∨ Q) ∧ (A ∨ ¬B ∨ C ∨ ¬D ∨ E ∨ G ∨ ¬H ∨ ¬Q) ∧ (A ∨ ¬B ∨ C ∨ ¬D ∨ E ∨ G ∨ ¬H ∨ Q) ∧ (A ∨ ¬B ∨ C ∨ ¬D ∨ E ∨ G ∨ H ∨ ¬Q) ∧ (A ∨ ¬B ∨ C ∨ ¬D ∨ E ∨ G ∨ H ∨ Q) ∧ (A ∨ ¬B ∨ C ∨ D ∨ ¬E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (A ∨ ¬B ∨ C ∨ D ∨ ¬E ∨ ¬G ∨ ¬H ∨ Q) ∧ (A ∨ ¬B ∨ C ∨ D ∨ ¬E ∨ ¬G ∨ H ∨ ¬Q) ∧ (A ∨ ¬B ∨ C ∨ D ∨ ¬E ∨ ¬G ∨ H ∨ Q) ∧ (A ∨ ¬B ∨ C ∨ D ∨ ¬E ∨ G ∨ ¬H ∨ ¬Q) ∧ (A ∨ ¬B ∨ C ∨ D ∨ ¬E ∨ G ∨ ¬H ∨ Q) ∧ (A ∨ ¬B ∨ C ∨ D ∨ ¬E ∨ G ∨ H ∨ ¬Q) ∧ (A ∨ ¬B ∨ C ∨ D ∨ ¬E ∨ G ∨ H ∨ Q) ∧ (A ∨ ¬B ∨ C ∨ D ∨ E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (A ∨ ¬B ∨ C ∨ D ∨ E ∨ ¬G ∨ ¬H ∨ Q) ∧ (A ∨ ¬B ∨ C ∨ D ∨ E ∨ ¬G ∨ H ∨ ¬Q) ∧ (A ∨ ¬B ∨ C ∨ D ∨ E ∨ ¬G ∨ H ∨ Q) ∧ (A ∨ ¬B ∨ C ∨ D ∨ E ∨ G ∨ ¬H ∨ ¬Q) ∧ (A ∨ ¬B ∨ C ∨ D ∨ E ∨ G ∨ ¬H ∨ Q) ∧ (A ∨ ¬B ∨ C ∨ D ∨ E ∨ G ∨ H ∨ ¬Q) ∧ (A ∨ ¬B ∨ C ∨ D ∨ E ∨ G ∨ H ∨ Q) ∧ (A ∨ B ∨ ¬C ∨ ¬D ∨ ¬E ∨ G ∨ H ∨ Q) ∧ (A ∨ B ∨ ¬C ∨ ¬D ∨ E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (A ∨ B ∨ ¬C ∨ ¬D ∨ E ∨ ¬G ∨ ¬H ∨ Q) ∧ (A ∨ B ∨ ¬C ∨ ¬D ∨ E ∨ ¬G ∨ H ∨ ¬Q) ∧ (A ∨ B ∨ ¬C ∨ ¬D ∨ E ∨ ¬G ∨ H ∨ Q) ∧ (A ∨ B ∨ ¬C ∨ ¬D ∨ E ∨ G ∨ ¬H ∨ ¬Q) ∧ (A ∨ B ∨ ¬C ∨ ¬D ∨ E ∨ G ∨ ¬H ∨ Q) ∧ (A ∨ B ∨ ¬C ∨ ¬D ∨ E ∨ G ∨ H ∨ ¬Q) ∧ (A ∨ B ∨ ¬C ∨ ¬D ∨ E ∨ G ∨ H ∨ Q) ∧ (A ∨ B ∨ ¬C ∨ D ∨ ¬E ∨ G ∨ H ∨ Q) ∧ (A ∨ B ∨ C ∨ ¬D ∨ ¬E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (A ∨ B ∨ C ∨ ¬D ∨ ¬E ∨ ¬G ∨ ¬H ∨ Q) ∧ (A ∨ B ∨ C ∨ ¬D ∨ ¬E ∨ ¬G ∨ H ∨ ¬Q) ∧ (A ∨ B ∨ C ∨ ¬D ∨ ¬E ∨ ¬G ∨ H ∨ Q) ∧ (A ∨ B ∨ C ∨ ¬D ∨ ¬E ∨ G ∨ ¬H ∨ ¬Q) ∧ (A ∨ B ∨ C ∨ ¬D ∨ ¬E ∨ G ∨ ¬H ∨ Q) ∧ (A ∨ B ∨ C ∨ ¬D ∨ ¬E ∨ G ∨ H ∨ ¬Q) ∧ (A ∨ B ∨ C ∨ D ∨ ¬E ∨ ¬G ∨ ¬H ∨ ¬Q) ∧ (A ∨ B ∨ C ∨ D ∨ ¬E ∨ ¬G ∨ ¬H ∨ Q) ∧ (A ∨ B ∨ C ∨ D ∨ ¬E ∨ ¬G ∨ H ∨ ¬Q) ∧ (A ∨ B ∨ C ∨ D ∨ ¬E ∨ ¬G ∨ H ∨ Q) ∧ (A ∨ B ∨ C ∨ D ∨ ¬E ∨ G ∨ ¬H ∨ ¬Q) ∧ (A ∨ B ∨ C ∨ D ∨ ¬E ∨ G ∨ ¬H ∨ Q) ∧ (A ∨ B ∨ C ∨ D ∨ ¬E ∨ G ∨ H ∨ ¬Q)")));
            assertTrue(compareMyTruthTableWithTheirs(genericEquivalentCCNFTable, """
                    1 1 1 1 1 1 1 1  │  0
                      1 1 1 1 1 1 1 0  │  0
                      1 1 1 1 1 1 0 1  │  0
                      1 1 1 1 1 1 0 0  │  0
                      1 1 1 1 1 0 1 1  │  0
                      1 1 1 1 1 0 1 0  │  0
                      1 1 1 1 1 0 0 1  │  0
                      1 1 1 1 1 0 0 0  │  1
                      1 1 1 1 0 1 1 1  │  1
                      1 1 1 1 0 1 1 0  │  1
                      1 1 1 1 0 1 0 1  │  1
                      1 1 1 1 0 1 0 0  │  1
                      1 1 1 1 0 0 1 1  │  1
                      1 1 1 1 0 0 1 0  │  1
                      1 1 1 1 0 0 0 1  │  1
                      1 1 1 1 0 0 0 0  │  1
                      1 1 1 0 1 1 1 1  │  0
                      1 1 1 0 1 1 1 0  │  0
                      1 1 1 0 1 1 0 1  │  0
                      1 1 1 0 1 1 0 0  │  0
                      1 1 1 0 1 0 1 1  │  0
                      1 1 1 0 1 0 1 0  │  0
                      1 1 1 0 1 0 0 1  │  0
                      1 1 1 0 1 0 0 0  │  1
                      1 1 1 0 0 1 1 1  │  1
                      1 1 1 0 0 1 1 0  │  1
                      1 1 1 0 0 1 0 1  │  1
                      1 1 1 0 0 1 0 0  │  1
                      1 1 1 0 0 0 1 1  │  1
                      1 1 1 0 0 0 1 0  │  1
                      1 1 1 0 0 0 0 1  │  1
                      1 1 1 0 0 0 0 0  │  1
                      1 1 0 1 1 1 1 1  │  0
                      1 1 0 1 1 1 1 0  │  0
                      1 1 0 1 1 1 0 1  │  0
                      1 1 0 1 1 1 0 0  │  0
                      1 1 0 1 1 0 1 1  │  0
                      1 1 0 1 1 0 1 0  │  0
                      1 1 0 1 1 0 0 1  │  0
                      1 1 0 1 1 0 0 0  │  1
                      1 1 0 1 0 1 1 1  │  1
                      1 1 0 1 0 1 1 0  │  1
                      1 1 0 1 0 1 0 1  │  1
                      1 1 0 1 0 1 0 0  │  1
                      1 1 0 1 0 0 1 1  │  1
                      1 1 0 1 0 0 1 0  │  1
                      1 1 0 1 0 0 0 1  │  1
                      1 1 0 1 0 0 0 0  │  1
                      1 1 0 0 1 1 1 1  │  0
                      1 1 0 0 1 1 1 0  │  0
                      1 1 0 0 1 1 0 1  │  0
                      1 1 0 0 1 1 0 0  │  0
                      1 1 0 0 1 0 1 1  │  0
                      1 1 0 0 1 0 1 0  │  0
                      1 1 0 0 1 0 0 1  │  0
                      1 1 0 0 1 0 0 0  │  1
                      1 1 0 0 0 1 1 1  │  1
                      1 1 0 0 0 1 1 0  │  1
                      1 1 0 0 0 1 0 1  │  1
                      1 1 0 0 0 1 0 0  │  1
                      1 1 0 0 0 0 1 1  │  1
                      1 1 0 0 0 0 1 0  │  1
                      1 1 0 0 0 0 0 1  │  1
                      1 1 0 0 0 0 0 0  │  1
                      1 0 1 1 1 1 1 1  │  0
                      1 0 1 1 1 1 1 0  │  0
                      1 0 1 1 1 1 0 1  │  0
                      1 0 1 1 1 1 0 0  │  0
                      1 0 1 1 1 0 1 1  │  0
                      1 0 1 1 1 0 1 0  │  0
                      1 0 1 1 1 0 0 1  │  0
                      1 0 1 1 1 0 0 0  │  1
                      1 0 1 1 0 1 1 1  │  1
                      1 0 1 1 0 1 1 0  │  1
                      1 0 1 1 0 1 0 1  │  1
                      1 0 1 1 0 1 0 0  │  1
                      1 0 1 1 0 0 1 1  │  1
                      1 0 1 1 0 0 1 0  │  1
                      1 0 1 1 0 0 0 1  │  1
                      1 0 1 1 0 0 0 0  │  1
                      1 0 1 0 1 1 1 1  │  0
                      1 0 1 0 1 1 1 0  │  0
                      1 0 1 0 1 1 0 1  │  0
                      1 0 1 0 1 1 0 0  │  0
                      1 0 1 0 1 0 1 1  │  0
                      1 0 1 0 1 0 1 0  │  0
                      1 0 1 0 1 0 0 1  │  0
                      1 0 1 0 1 0 0 0  │  1
                      1 0 1 0 0 1 1 1  │  0
                      1 0 1 0 0 1 1 0  │  0
                      1 0 1 0 0 1 0 1  │  0
                      1 0 1 0 0 1 0 0  │  0
                      1 0 1 0 0 0 1 1  │  0
                      1 0 1 0 0 0 1 0  │  0
                      1 0 1 0 0 0 0 1  │  0
                      1 0 1 0 0 0 0 0  │  0
                      1 0 0 1 1 1 1 1  │  1
                      1 0 0 1 1 1 1 0  │  1
                      1 0 0 1 1 1 0 1  │  1
                      1 0 0 1 1 1 0 0  │  1
                      1 0 0 1 1 0 1 1  │  1
                      1 0 0 1 1 0 1 0  │  1
                      1 0 0 1 1 0 0 1  │  1
                      1 0 0 1 1 0 0 0  │  0
                      1 0 0 1 0 1 1 1  │  0
                      1 0 0 1 0 1 1 0  │  0
                      1 0 0 1 0 1 0 1  │  0
                      1 0 0 1 0 1 0 0  │  0
                      1 0 0 1 0 0 1 1  │  0
                      1 0 0 1 0 0 1 0  │  0
                      1 0 0 1 0 0 0 1  │  0
                      1 0 0 1 0 0 0 0  │  0
                      1 0 0 0 1 1 1 1  │  1
                      1 0 0 0 1 1 1 0  │  1
                      1 0 0 0 1 1 0 1  │  1
                      1 0 0 0 1 1 0 0  │  1
                      1 0 0 0 1 0 1 1  │  1
                      1 0 0 0 1 0 1 0  │  1
                      1 0 0 0 1 0 0 1  │  1
                      1 0 0 0 1 0 0 0  │  0
                      1 0 0 0 0 1 1 1  │  0
                      1 0 0 0 0 1 1 0  │  0
                      1 0 0 0 0 1 0 1  │  0
                      1 0 0 0 0 1 0 0  │  0
                      1 0 0 0 0 0 1 1  │  0
                      1 0 0 0 0 0 1 0  │  0
                      1 0 0 0 0 0 0 1  │  0
                      1 0 0 0 0 0 0 0  │  0
                      0 1 1 1 1 1 1 1  │  0
                      0 1 1 1 1 1 1 0  │  0
                      0 1 1 1 1 1 0 1  │  0
                      0 1 1 1 1 1 0 0  │  0
                      0 1 1 1 1 0 1 1  │  0
                      0 1 1 1 1 0 1 0  │  0
                      0 1 1 1 1 0 0 1  │  0
                      0 1 1 1 1 0 0 0  │  0
                      0 1 1 1 0 1 1 1  │  0
                      0 1 1 1 0 1 1 0  │  0
                      0 1 1 1 0 1 0 1  │  0
                      0 1 1 1 0 1 0 0  │  0
                      0 1 1 1 0 0 1 1  │  0
                      0 1 1 1 0 0 1 0  │  0
                      0 1 1 1 0 0 0 1  │  0
                      0 1 1 1 0 0 0 0  │  0
                      0 1 1 0 1 1 1 1  │  0
                      0 1 1 0 1 1 1 0  │  0
                      0 1 1 0 1 1 0 1  │  0
                      0 1 1 0 1 1 0 0  │  0
                      0 1 1 0 1 0 1 1  │  0
                      0 1 1 0 1 0 1 0  │  0
                      0 1 1 0 1 0 0 1  │  0
                      0 1 1 0 1 0 0 0  │  0
                      0 1 1 0 0 1 1 1  │  0
                      0 1 1 0 0 1 1 0  │  0
                      0 1 1 0 0 1 0 1  │  0
                      0 1 1 0 0 1 0 0  │  0
                      0 1 1 0 0 0 1 1  │  0
                      0 1 1 0 0 0 1 0  │  0
                      0 1 1 0 0 0 0 1  │  0
                      0 1 1 0 0 0 0 0  │  0
                      0 1 0 1 1 1 1 1  │  0
                      0 1 0 1 1 1 1 0  │  0
                      0 1 0 1 1 1 0 1  │  0
                      0 1 0 1 1 1 0 0  │  0
                      0 1 0 1 1 0 1 1  │  0
                      0 1 0 1 1 0 1 0  │  0
                      0 1 0 1 1 0 0 1  │  0
                      0 1 0 1 1 0 0 0  │  0
                      0 1 0 1 0 1 1 1  │  0
                      0 1 0 1 0 1 1 0  │  0
                      0 1 0 1 0 1 0 1  │  0
                      0 1 0 1 0 1 0 0  │  0
                      0 1 0 1 0 0 1 1  │  0
                      0 1 0 1 0 0 1 0  │  0
                      0 1 0 1 0 0 0 1  │  0
                      0 1 0 1 0 0 0 0  │  0
                      0 1 0 0 1 1 1 1  │  0
                      0 1 0 0 1 1 1 0  │  0
                      0 1 0 0 1 1 0 1  │  0
                      0 1 0 0 1 1 0 0  │  0
                      0 1 0 0 1 0 1 1  │  0
                      0 1 0 0 1 0 1 0  │  0
                      0 1 0 0 1 0 0 1  │  0
                      0 1 0 0 1 0 0 0  │  0
                      0 1 0 0 0 1 1 1  │  0
                      0 1 0 0 0 1 1 0  │  0
                      0 1 0 0 0 1 0 1  │  0
                      0 1 0 0 0 1 0 0  │  0
                      0 1 0 0 0 0 1 1  │  0
                      0 1 0 0 0 0 1 0  │  0
                      0 1 0 0 0 0 0 1  │  0
                      0 1 0 0 0 0 0 0  │  0
                      0 0 1 1 1 1 1 1  │  1
                      0 0 1 1 1 1 1 0  │  1
                      0 0 1 1 1 1 0 1  │  1
                      0 0 1 1 1 1 0 0  │  1
                      0 0 1 1 1 0 1 1  │  1
                      0 0 1 1 1 0 1 0  │  1
                      0 0 1 1 1 0 0 1  │  1
                      0 0 1 1 1 0 0 0  │  0
                      0 0 1 1 0 1 1 1  │  0
                      0 0 1 1 0 1 1 0  │  0
                      0 0 1 1 0 1 0 1  │  0
                      0 0 1 1 0 1 0 0  │  0
                      0 0 1 1 0 0 1 1  │  0
                      0 0 1 1 0 0 1 0  │  0
                      0 0 1 1 0 0 0 1  │  0
                      0 0 1 1 0 0 0 0  │  0
                      0 0 1 0 1 1 1 1  │  1
                      0 0 1 0 1 1 1 0  │  1
                      0 0 1 0 1 1 0 1  │  1
                      0 0 1 0 1 1 0 0  │  1
                      0 0 1 0 1 0 1 1  │  1
                      0 0 1 0 1 0 1 0  │  1
                      0 0 1 0 1 0 0 1  │  1
                      0 0 1 0 1 0 0 0  │  0
                      0 0 1 0 0 1 1 1  │  1
                      0 0 1 0 0 1 1 0  │  1
                      0 0 1 0 0 1 0 1  │  1
                      0 0 1 0 0 1 0 0  │  1
                      0 0 1 0 0 0 1 1  │  1
                      0 0 1 0 0 0 1 0  │  1
                      0 0 1 0 0 0 0 1  │  1
                      0 0 1 0 0 0 0 0  │  1
                      0 0 0 1 1 1 1 1  │  0
                      0 0 0 1 1 1 1 0  │  0
                      0 0 0 1 1 1 0 1  │  0
                      0 0 0 1 1 1 0 0  │  0
                      0 0 0 1 1 0 1 1  │  0
                      0 0 0 1 1 0 1 0  │  0
                      0 0 0 1 1 0 0 1  │  0
                      0 0 0 1 1 0 0 0  │  1
                      0 0 0 1 0 1 1 1  │  1
                      0 0 0 1 0 1 1 0  │  1
                      0 0 0 1 0 1 0 1  │  1
                      0 0 0 1 0 1 0 0  │  1
                      0 0 0 1 0 0 1 1  │  1
                      0 0 0 1 0 0 1 0  │  1
                      0 0 0 1 0 0 0 1  │  1
                      0 0 0 1 0 0 0 0  │  1
                      0 0 0 0 1 1 1 1  │  0
                      0 0 0 0 1 1 1 0  │  0
                      0 0 0 0 1 1 0 1  │  0
                      0 0 0 0 1 1 0 0  │  0
                      0 0 0 0 1 0 1 1  │  0
                      0 0 0 0 1 0 1 0  │  0
                      0 0 0 0 1 0 0 1  │  0
                      0 0 0 0 1 0 0 0  │  1
                      0 0 0 0 0 1 1 1  │  1
                      0 0 0 0 0 1 1 0  │  1
                      0 0 0 0 0 1 0 1  │  1
                      0 0 0 0 0 1 0 0  │  1
                      0 0 0 0 0 0 1 1  │  1
                      0 0 0 0 0 0 1 0  │  1
                      0 0 0 0 0 0 0 1  │  1
                      0 0 0 0 0 0 0 0  │  1
                    """));

            assertTrue(compareMyTruthTableWithTheirs(genericSentenceTable, """
                    1 1 1 1 1 1 1 1  │  0
                      1 1 1 1 1 1 1 0  │  0
                      1 1 1 1 1 1 0 1  │  0
                      1 1 1 1 1 1 0 0  │  0
                      1 1 1 1 1 0 1 1  │  0
                      1 1 1 1 1 0 1 0  │  0
                      1 1 1 1 1 0 0 1  │  0
                      1 1 1 1 1 0 0 0  │  1
                      1 1 1 1 0 1 1 1  │  1
                      1 1 1 1 0 1 1 0  │  1
                      1 1 1 1 0 1 0 1  │  1
                      1 1 1 1 0 1 0 0  │  1
                      1 1 1 1 0 0 1 1  │  1
                      1 1 1 1 0 0 1 0  │  1
                      1 1 1 1 0 0 0 1  │  1
                      1 1 1 1 0 0 0 0  │  1
                      1 1 1 0 1 1 1 1  │  0
                      1 1 1 0 1 1 1 0  │  0
                      1 1 1 0 1 1 0 1  │  0
                      1 1 1 0 1 1 0 0  │  0
                      1 1 1 0 1 0 1 1  │  0
                      1 1 1 0 1 0 1 0  │  0
                      1 1 1 0 1 0 0 1  │  0
                      1 1 1 0 1 0 0 0  │  1
                      1 1 1 0 0 1 1 1  │  1
                      1 1 1 0 0 1 1 0  │  1
                      1 1 1 0 0 1 0 1  │  1
                      1 1 1 0 0 1 0 0  │  1
                      1 1 1 0 0 0 1 1  │  1
                      1 1 1 0 0 0 1 0  │  1
                      1 1 1 0 0 0 0 1  │  1
                      1 1 1 0 0 0 0 0  │  1
                      1 1 0 1 1 1 1 1  │  0
                      1 1 0 1 1 1 1 0  │  0
                      1 1 0 1 1 1 0 1  │  0
                      1 1 0 1 1 1 0 0  │  0
                      1 1 0 1 1 0 1 1  │  0
                      1 1 0 1 1 0 1 0  │  0
                      1 1 0 1 1 0 0 1  │  0
                      1 1 0 1 1 0 0 0  │  1
                      1 1 0 1 0 1 1 1  │  1
                      1 1 0 1 0 1 1 0  │  1
                      1 1 0 1 0 1 0 1  │  1
                      1 1 0 1 0 1 0 0  │  1
                      1 1 0 1 0 0 1 1  │  1
                      1 1 0 1 0 0 1 0  │  1
                      1 1 0 1 0 0 0 1  │  1
                      1 1 0 1 0 0 0 0  │  1
                      1 1 0 0 1 1 1 1  │  0
                      1 1 0 0 1 1 1 0  │  0
                      1 1 0 0 1 1 0 1  │  0
                      1 1 0 0 1 1 0 0  │  0
                      1 1 0 0 1 0 1 1  │  0
                      1 1 0 0 1 0 1 0  │  0
                      1 1 0 0 1 0 0 1  │  0
                      1 1 0 0 1 0 0 0  │  1
                      1 1 0 0 0 1 1 1  │  1
                      1 1 0 0 0 1 1 0  │  1
                      1 1 0 0 0 1 0 1  │  1
                      1 1 0 0 0 1 0 0  │  1
                      1 1 0 0 0 0 1 1  │  1
                      1 1 0 0 0 0 1 0  │  1
                      1 1 0 0 0 0 0 1  │  1
                      1 1 0 0 0 0 0 0  │  1
                      1 0 1 1 1 1 1 1  │  0
                      1 0 1 1 1 1 1 0  │  0
                      1 0 1 1 1 1 0 1  │  0
                      1 0 1 1 1 1 0 0  │  0
                      1 0 1 1 1 0 1 1  │  0
                      1 0 1 1 1 0 1 0  │  0
                      1 0 1 1 1 0 0 1  │  0
                      1 0 1 1 1 0 0 0  │  1
                      1 0 1 1 0 1 1 1  │  1
                      1 0 1 1 0 1 1 0  │  1
                      1 0 1 1 0 1 0 1  │  1
                      1 0 1 1 0 1 0 0  │  1
                      1 0 1 1 0 0 1 1  │  1
                      1 0 1 1 0 0 1 0  │  1
                      1 0 1 1 0 0 0 1  │  1
                      1 0 1 1 0 0 0 0  │  1
                      1 0 1 0 1 1 1 1  │  0
                      1 0 1 0 1 1 1 0  │  0
                      1 0 1 0 1 1 0 1  │  0
                      1 0 1 0 1 1 0 0  │  0
                      1 0 1 0 1 0 1 1  │  0
                      1 0 1 0 1 0 1 0  │  0
                      1 0 1 0 1 0 0 1  │  0
                      1 0 1 0 1 0 0 0  │  1
                      1 0 1 0 0 1 1 1  │  0
                      1 0 1 0 0 1 1 0  │  0
                      1 0 1 0 0 1 0 1  │  0
                      1 0 1 0 0 1 0 0  │  0
                      1 0 1 0 0 0 1 1  │  0
                      1 0 1 0 0 0 1 0  │  0
                      1 0 1 0 0 0 0 1  │  0
                      1 0 1 0 0 0 0 0  │  0
                      1 0 0 1 1 1 1 1  │  1
                      1 0 0 1 1 1 1 0  │  1
                      1 0 0 1 1 1 0 1  │  1
                      1 0 0 1 1 1 0 0  │  1
                      1 0 0 1 1 0 1 1  │  1
                      1 0 0 1 1 0 1 0  │  1
                      1 0 0 1 1 0 0 1  │  1
                      1 0 0 1 1 0 0 0  │  0
                      1 0 0 1 0 1 1 1  │  0
                      1 0 0 1 0 1 1 0  │  0
                      1 0 0 1 0 1 0 1  │  0
                      1 0 0 1 0 1 0 0  │  0
                      1 0 0 1 0 0 1 1  │  0
                      1 0 0 1 0 0 1 0  │  0
                      1 0 0 1 0 0 0 1  │  0
                      1 0 0 1 0 0 0 0  │  0
                      1 0 0 0 1 1 1 1  │  1
                      1 0 0 0 1 1 1 0  │  1
                      1 0 0 0 1 1 0 1  │  1
                      1 0 0 0 1 1 0 0  │  1
                      1 0 0 0 1 0 1 1  │  1
                      1 0 0 0 1 0 1 0  │  1
                      1 0 0 0 1 0 0 1  │  1
                      1 0 0 0 1 0 0 0  │  0
                      1 0 0 0 0 1 1 1  │  0
                      1 0 0 0 0 1 1 0  │  0
                      1 0 0 0 0 1 0 1  │  0
                      1 0 0 0 0 1 0 0  │  0
                      1 0 0 0 0 0 1 1  │  0
                      1 0 0 0 0 0 1 0  │  0
                      1 0 0 0 0 0 0 1  │  0
                      1 0 0 0 0 0 0 0  │  0
                      0 1 1 1 1 1 1 1  │  0
                      0 1 1 1 1 1 1 0  │  0
                      0 1 1 1 1 1 0 1  │  0
                      0 1 1 1 1 1 0 0  │  0
                      0 1 1 1 1 0 1 1  │  0
                      0 1 1 1 1 0 1 0  │  0
                      0 1 1 1 1 0 0 1  │  0
                      0 1 1 1 1 0 0 0  │  0
                      0 1 1 1 0 1 1 1  │  0
                      0 1 1 1 0 1 1 0  │  0
                      0 1 1 1 0 1 0 1  │  0
                      0 1 1 1 0 1 0 0  │  0
                      0 1 1 1 0 0 1 1  │  0
                      0 1 1 1 0 0 1 0  │  0
                      0 1 1 1 0 0 0 1  │  0
                      0 1 1 1 0 0 0 0  │  0
                      0 1 1 0 1 1 1 1  │  0
                      0 1 1 0 1 1 1 0  │  0
                      0 1 1 0 1 1 0 1  │  0
                      0 1 1 0 1 1 0 0  │  0
                      0 1 1 0 1 0 1 1  │  0
                      0 1 1 0 1 0 1 0  │  0
                      0 1 1 0 1 0 0 1  │  0
                      0 1 1 0 1 0 0 0  │  0
                      0 1 1 0 0 1 1 1  │  0
                      0 1 1 0 0 1 1 0  │  0
                      0 1 1 0 0 1 0 1  │  0
                      0 1 1 0 0 1 0 0  │  0
                      0 1 1 0 0 0 1 1  │  0
                      0 1 1 0 0 0 1 0  │  0
                      0 1 1 0 0 0 0 1  │  0
                      0 1 1 0 0 0 0 0  │  0
                      0 1 0 1 1 1 1 1  │  0
                      0 1 0 1 1 1 1 0  │  0
                      0 1 0 1 1 1 0 1  │  0
                      0 1 0 1 1 1 0 0  │  0
                      0 1 0 1 1 0 1 1  │  0
                      0 1 0 1 1 0 1 0  │  0
                      0 1 0 1 1 0 0 1  │  0
                      0 1 0 1 1 0 0 0  │  0
                      0 1 0 1 0 1 1 1  │  0
                      0 1 0 1 0 1 1 0  │  0
                      0 1 0 1 0 1 0 1  │  0
                      0 1 0 1 0 1 0 0  │  0
                      0 1 0 1 0 0 1 1  │  0
                      0 1 0 1 0 0 1 0  │  0
                      0 1 0 1 0 0 0 1  │  0
                      0 1 0 1 0 0 0 0  │  0
                      0 1 0 0 1 1 1 1  │  0
                      0 1 0 0 1 1 1 0  │  0
                      0 1 0 0 1 1 0 1  │  0
                      0 1 0 0 1 1 0 0  │  0
                      0 1 0 0 1 0 1 1  │  0
                      0 1 0 0 1 0 1 0  │  0
                      0 1 0 0 1 0 0 1  │  0
                      0 1 0 0 1 0 0 0  │  0
                      0 1 0 0 0 1 1 1  │  0
                      0 1 0 0 0 1 1 0  │  0
                      0 1 0 0 0 1 0 1  │  0
                      0 1 0 0 0 1 0 0  │  0
                      0 1 0 0 0 0 1 1  │  0
                      0 1 0 0 0 0 1 0  │  0
                      0 1 0 0 0 0 0 1  │  0
                      0 1 0 0 0 0 0 0  │  0
                      0 0 1 1 1 1 1 1  │  1
                      0 0 1 1 1 1 1 0  │  1
                      0 0 1 1 1 1 0 1  │  1
                      0 0 1 1 1 1 0 0  │  1
                      0 0 1 1 1 0 1 1  │  1
                      0 0 1 1 1 0 1 0  │  1
                      0 0 1 1 1 0 0 1  │  1
                      0 0 1 1 1 0 0 0  │  0
                      0 0 1 1 0 1 1 1  │  0
                      0 0 1 1 0 1 1 0  │  0
                      0 0 1 1 0 1 0 1  │  0
                      0 0 1 1 0 1 0 0  │  0
                      0 0 1 1 0 0 1 1  │  0
                      0 0 1 1 0 0 1 0  │  0
                      0 0 1 1 0 0 0 1  │  0
                      0 0 1 1 0 0 0 0  │  0
                      0 0 1 0 1 1 1 1  │  1
                      0 0 1 0 1 1 1 0  │  1
                      0 0 1 0 1 1 0 1  │  1
                      0 0 1 0 1 1 0 0  │  1
                      0 0 1 0 1 0 1 1  │  1
                      0 0 1 0 1 0 1 0  │  1
                      0 0 1 0 1 0 0 1  │  1
                      0 0 1 0 1 0 0 0  │  0
                      0 0 1 0 0 1 1 1  │  1
                      0 0 1 0 0 1 1 0  │  1
                      0 0 1 0 0 1 0 1  │  1
                      0 0 1 0 0 1 0 0  │  1
                      0 0 1 0 0 0 1 1  │  1
                      0 0 1 0 0 0 1 0  │  1
                      0 0 1 0 0 0 0 1  │  1
                      0 0 1 0 0 0 0 0  │  1
                      0 0 0 1 1 1 1 1  │  0
                      0 0 0 1 1 1 1 0  │  0
                      0 0 0 1 1 1 0 1  │  0
                      0 0 0 1 1 1 0 0  │  0
                      0 0 0 1 1 0 1 1  │  0
                      0 0 0 1 1 0 1 0  │  0
                      0 0 0 1 1 0 0 1  │  0
                      0 0 0 1 1 0 0 0  │  1
                      0 0 0 1 0 1 1 1  │  1
                      0 0 0 1 0 1 1 0  │  1
                      0 0 0 1 0 1 0 1  │  1
                      0 0 0 1 0 1 0 0  │  1
                      0 0 0 1 0 0 1 1  │  1
                      0 0 0 1 0 0 1 0  │  1
                      0 0 0 1 0 0 0 1  │  1
                      0 0 0 1 0 0 0 0  │  1
                      0 0 0 0 1 1 1 1  │  0
                      0 0 0 0 1 1 1 0  │  0
                      0 0 0 0 1 1 0 1  │  0
                      0 0 0 0 1 1 0 0  │  0
                      0 0 0 0 1 0 1 1  │  0
                      0 0 0 0 1 0 1 0  │  0
                      0 0 0 0 1 0 0 1  │  0
                      0 0 0 0 1 0 0 0  │  1
                      0 0 0 0 0 1 1 1  │  1
                      0 0 0 0 0 1 1 0  │  1
                      0 0 0 0 0 1 0 1  │  1
                      0 0 0 0 0 1 0 0  │  1
                      0 0 0 0 0 0 1 1  │  1
                      0 0 0 0 0 0 1 0  │  1
                      0 0 0 0 0 0 0 1  │  1
                      0 0 0 0 0 0 0 0  │  1
                    """));

            TruthTable cnfTable = new TruthTable(new GenericComplexSentence("!((!A | B | C) & (!A | B | D | E) & (!B | A) & (!C | !D | A) & (!C | !E | A))"));
            String equivalentCCNFTable = """
                    1 1 1 1 1  │  0
                      1 1 1 1 0  │  0
                      1 1 1 0 1  │  0
                      1 1 1 0 0  │  0
                      1 1 0 1 1  │  0
                      1 1 0 1 0  │  0
                      1 1 0 0 1  │  0
                      1 1 0 0 0  │  0
                      1 0 1 1 1  │  0
                      1 0 1 1 0  │  0
                      1 0 1 0 1  │  0
                      1 0 1 0 0  │  1
                      1 0 0 1 1  │  1
                      1 0 0 1 0  │  1
                      1 0 0 0 1  │  1
                      1 0 0 0 0  │  1
                      0 1 1 1 1  │  1
                      0 1 1 1 0  │  1
                      0 1 1 0 1  │  1
                      0 1 1 0 0  │  1
                      0 1 0 1 1  │  1
                      0 1 0 1 0  │  1
                      0 1 0 0 1  │  1
                      0 1 0 0 0  │  1
                      0 0 1 1 1  │  1
                      0 0 1 1 0  │  1
                      0 0 1 0 1  │  1
                      0 0 1 0 0  │  0
                      0 0 0 1 1  │  0
                      0 0 0 1 0  │  0
                      0 0 0 0 1  │  0
                      0 0 0 0 0  │  0
                    """;
            assertTrue(compareMyTruthTableWithTheirs(cnfTable, equivalentCCNFTable));
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    private boolean compareMyTruthTableWithTheirs(TruthTable myTable, String theirString) {
        String[] theirStringArray = theirString.split("\n");
        int i = 0;
        for (var iterator = myTable.iterator(); iterator.hasNext(); i++) {
            int compareResult = getStringFromTruthTableRow(iterator.next()).compareTo(theirStringArray[i].trim());
            if (compareResult != 0) return false;
        }
        return true;
    }

    private String getStringFromTruthTableRow(TruthTableRow row) {
        var str = new StringBuilder();
        for (boolean literalValue : row.literalValues()) {
            str.append(literalValue ? "1 " : "0 ");
        }
        return str.append(" │  ")
                .append(row.evaluation() ? "1" : "0")
                .toString();
    }
}