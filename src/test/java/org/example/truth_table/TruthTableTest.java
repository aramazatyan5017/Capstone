package org.example.truth_table;

import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Clause;
import org.example.domain.sentence.GenericComplexSentence;
import org.example.domain.sentence.Literal;
import org.example.exception.TautologyException;
import org.example.exception.UnsatisfiableException;
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
    void printTest() {
        try {
            TruthTable literalTable = new TruthTable(new Literal("A", true));
            literalTable.print();
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    private void compareMyTruthTableWithTheirs(TruthTable myTable, String theirString) {

    }

    @Test
    void tempTest() {

    }
}