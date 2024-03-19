package org.example.domain;

import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Clause;
import org.example.domain.sentence.Literal;
import org.example.exception.TautologyException;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author aram.azatyan | 3/15/2024 9:09 PM
 */
class ClauseTest {

    @Test
    void createClauseAbnormal() {
        assertThrows(IllegalArgumentException.class, () -> new Clause(new LinkedHashSet<>()));

        LinkedHashSet<Literal> nullSet = new LinkedHashSet<>();
        assertThrows(IllegalArgumentException.class, () -> new Clause(nullSet));

        LinkedHashSet<Literal> set = new LinkedHashSet<>();
        set.add(null);
        set.add(null);
        set.add(null);
        assertThrows(IllegalArgumentException.class, () -> new Clause(set));

        Literal[] literals = null;
        assertThrows(IllegalArgumentException.class, () -> new Clause(literals));

        assertThrows(IllegalArgumentException.class, () -> new Clause(null, null, null));

        String str = null;
        assertThrows(ParseException.class, () -> new Clause(str));

        assertThrows(ParseException.class, () -> new Clause("   "));

        assertThrows(ParseException.class, () -> new Clause("a & b"));
        assertThrows(ParseException.class, () -> new Clause("(a &)"));
        assertThrows(ParseException.class, () -> new Clause("a => b & c"));
        assertThrows(ParseException.class, () -> new Clause("b <=> c <=> !d | f"));
        assertThrows(ParseException.class, () -> new Clause("!(a | B)"));
    }

    @Test
    void createClauseNormal() {
        LinkedHashSet<Literal> set = new LinkedHashSet<>();
        set.add(new Literal("A"));
        set.add(new Literal("B"));
        Clause clause = new Clause(set);
        assertEquals(2, clause.size());

        set = new LinkedHashSet<>();
        set.add(new Literal("A"));
        set.add(null);
        set.add(new Literal("B"));
        set.add(null);
        clause = new Clause(set);
        assertEquals(2, clause.size());

        clause = new Clause(new Literal("A"), null, new Literal("B"));
        assertEquals(2, clause.size());

        try {
            clause = new Clause("a");
            assertEquals(1, clause.size());

            clause = new Clause("(a)");
            assertEquals(1, clause.size());

            clause = new Clause("(a | b)");
            assertEquals(2, clause.size());

            clause = new Clause("a | b");
            assertEquals(2, clause.size());

            clause = new Clause("(!(!(!a)) | !!!b | g)");
            assertEquals(3, clause.size());

            clause = new Clause("!(!(!a)) | !!!b | g");
            assertEquals(3, clause.size());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    void literalListTest() {
        try {
            Clause clause = new Clause("a | b | c");
            List<Literal> literalList = new ArrayList<>();
            literalList.add(new Literal("a"));
            literalList.add(new Literal("b"));
            literalList.add(new Literal("c"));
            assertEquals(literalList, clause.getLiteralList());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    void sentenceTypeTest() {
        try {
            Clause clause = new Clause("A | B | C");
            assertFalse(clause.isLiteral());
            assertTrue(clause.isClause());
            assertFalse(clause.isCnf());
            assertFalse(clause.isGenericComplex());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    void convertToCNFTest() {
        try {
            Clause clause = new Clause("a | B | c");
            CNFSentence cnf = clause.convertToCNF();
            assertEquals(1, cnf.size());

            LinkedHashSet<Clause> clauseSet = new LinkedHashSet<>();
            clauseSet.add(new Clause("a | B | c"));
            assertEquals(clauseSet, cnf.getClauses());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }

        try {
            Clause clause = new Clause("a | !a | b");
            CNFSentence cnf = clause.convertToCNF();
        } catch (Exception e) {
            if (!(e instanceof TautologyException)) {
                fail("should have thrown a TautologyException");
            }
        }
    }

    @Test
    void toStringTest() {
        try {
            Clause clause = new Clause("a");
            assertEquals("a", clause.toString());

            clause = new Clause("a | b | c");
            assertEquals("a | b | c", clause.toString());

            clause = new Clause(new Literal("a"), new Literal("b", true));
            assertEquals("a | !b", clause.toString());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    void equalsTest() {
        try {
            Clause clause1 = new Clause("a | b");
            Clause clause2 = new Clause("b | a");
            assertEquals(clause1, clause2);

            clause1 = new Clause("a | b | c");
            clause2 = new Clause("a | b | d");
            assertNotEquals(clause1, clause2);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    void hashCodeTest() {
        try {
            Clause clause1 = new Clause("a | b");
            Clause clause2 = new Clause("b | a");
            assertEquals(clause1.hashCode(), clause2.hashCode());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    void negatedLiteralCount() {
        try {
            Clause clause = new Clause("A | B | C");
            assertEquals(0, clause.getNegatedLiteralCount());

            clause = new Clause("A | !B | C");
            assertEquals(1, clause.getNegatedLiteralCount());

            clause = new Clause("!S");
            assertEquals(1, clause.getNegatedLiteralCount());
        } catch (Exception ex) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    void nonNegatedLiteralCount() {
        try {
            Clause clause = new Clause("A | B | C");
            assertEquals(3, clause.getNonNegatedLiteralCount());

            clause = new Clause("A | !B | C");
            assertEquals(2, clause.getNonNegatedLiteralCount());

            clause = new Clause("!S");
            assertEquals(0, clause.getNonNegatedLiteralCount());
        } catch (Exception ex) {
            fail("shouldn't have thrown an exception");
        }
    }
}