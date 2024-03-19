package org.example.domain;

import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Clause;
import org.example.exception.TautologyException;
import org.example.exception.UnsatisfiableException;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author aram.azatyan | 3/15/2024 10:22 PM
 */
class CNFSentenceTest {

    @Test
    void createCNFAbnormal() {
        LinkedHashSet<Clause> set = null;
        assertThrows(IllegalArgumentException.class, () -> new CNFSentence(set));

        assertThrows(IllegalArgumentException.class, () -> new CNFSentence(new LinkedHashSet<>()));

        LinkedHashSet<Clause> nullSet = new LinkedHashSet<>();
        nullSet.add(null);
        nullSet.add(null);
        assertThrows(IllegalArgumentException.class, () -> new CNFSentence(nullSet));

        Clause[] clauses = null;
        assertThrows(IllegalArgumentException.class, () -> new CNFSentence(clauses));

        assertThrows(IllegalArgumentException.class, () -> new CNFSentence(null, null, null));

        String str = null;
        assertThrows(ParseException.class, () -> new CNFSentence(str));

        assertThrows(ParseException.class, () -> new CNFSentence("  "));

        assertThrows(ParseException.class, () -> new CNFSentence("a => ab"));
        assertThrows(ParseException.class, () -> new CNFSentence("a <=> c | d"));
        assertThrows(ParseException.class, () -> new CNFSentence("!(a | b | c)"));
        assertThrows(ParseException.class, () -> new CNFSentence("!(a | b | c)(("));
        assertThrows(ParseException.class, () -> new CNFSentence("(a | b | c) => (!a | d | e)"));
        assertThrows(ParseException.class, () -> new CNFSentence("(a & b & c) | (d & e & f)"));
        assertThrows(ParseException.class, () -> new CNFSentence("(a | b) &&& (b | d)"));
        assertThrows(ParseException.class, () -> new CNFSentence("!(a | b) & (b | d)"));
        assertThrows(ParseException.class, () -> new CNFSentence("(a & b & c & d)"));
    }

    @Test
    void createCNFNormal() {
        try {
            LinkedHashSet<Clause> set = new LinkedHashSet<>();
            set.add(new Clause("A"));
            set.add(new Clause("B | C | !D"));
            CNFSentence cnf = new CNFSentence(set);
            assertEquals(2, cnf.size());

            set = new LinkedHashSet<>();
            set.add(new Clause("A | C | D"));
            set.add(null);
            set.add(new Clause("B"));
            set.add(null);
            cnf = new CNFSentence(set);
            assertEquals(2, cnf.size());

            cnf = new CNFSentence(new Clause("A | B"), null, new Clause("C | D"));
            assertEquals(2, cnf.size());

            cnf = new CNFSentence("a");
            assertEquals(1, cnf.size());

            cnf = new CNFSentence("(a)");
            assertEquals(1, cnf.size());

            cnf = new CNFSentence("a | b");
            assertEquals(1, cnf.size());

            cnf = new CNFSentence("(a | b)");
            assertEquals(1, cnf.size());

            cnf = new CNFSentence("(a | b) & (c | d)");
            assertEquals(2, cnf.size());

            cnf = new CNFSentence("a & b & c & d");
            assertEquals(4, cnf.size());

            cnf = new CNFSentence("(a) & (b) & (c) & (d)");
            assertEquals(4, cnf.size());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    void clauseListTest() {
        try {
            CNFSentence cnf = new CNFSentence("(a | b | c) & (D | !e | f)");
            List<Clause> clauseList = new ArrayList<>();
            clauseList.add(new Clause("a | b | c"));
            clauseList.add(new Clause("(D | !e | f)"));
            assertEquals(clauseList, cnf.getClauseList());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    void sentenceTypeTest() {
        try {
            CNFSentence cnf = new CNFSentence("A | B | C");
            assertFalse(cnf.isLiteral());
            assertFalse(cnf.isClause());
            assertTrue(cnf.isCnf());
            assertFalse(cnf.isGenericComplex());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    void convertToCNFTest() {
        try {
            CNFSentence cnfNotConverted = new CNFSentence("(a | B | c) & (g | !D | c)");
            CNFSentence cnf = cnfNotConverted.convertToCNF();
            assertEquals(2, cnf.size());

            LinkedHashSet<Clause> clauseSet = new LinkedHashSet<>();
            clauseSet.add(new Clause("a | B | c"));
            clauseSet.add(new Clause("(g | !D | c)"));
            assertEquals(clauseSet, cnf.getClauses());

            cnfNotConverted = new CNFSentence("(a | b) & (b | c | a)");
            cnf = cnfNotConverted.convertToCNF();
            assertEquals(1, cnf.size());

            clauseSet = new LinkedHashSet<>();
            clauseSet.add(new Clause("a | b"));
            assertEquals(clauseSet, cnf.getClauses());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }

        try {
            CNFSentence cnfNotConverted = new CNFSentence("(a | !a | b) & (!c | e | f | c)");
            CNFSentence cnf = cnfNotConverted.convertToCNF();
        } catch (Exception e) {
            if (!(e instanceof TautologyException)) {
                fail("should have thrown a TautologyException");
            }
        }

        try {
            CNFSentence cnfNotConverted = new CNFSentence("!a & (!b) & !(c) & (a | b | c)");
            CNFSentence cnf = cnfNotConverted.convertToCNF();
        } catch (Exception e) {
            if (!(e instanceof UnsatisfiableException)) {
                fail("should have thrown an UnsatisfiableException");
            }
        }
    }

    @Test
    void isCanonicalTest() {
        try {
            CNFSentence cnf = new CNFSentence("(a | b) & (!a | c | d) & (b)");
            assertFalse(cnf.isCanonical());

            cnf = new CNFSentence("a");
            assertTrue(cnf.isCanonical());

            cnf = new CNFSentence("(a | b | c) & (!a | b | !c) & (a | !b | !c)");
            assertTrue(cnf.isCanonical());

            cnf = new CNFSentence("(a | b | c | d) & (!a | b | !c) & (a | !b | !c)");
            assertFalse(cnf.isCanonical());

            cnf = new CNFSentence("(a | b | c) & (!a | b | !c) & (a | !b)");
            assertFalse(cnf.isCanonical());

            cnf = new CNFSentence("(a | !a | b | c)");
            assertFalse(cnf.isCanonical());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    void toStringTest() {
        try {
            CNFSentence cnf = new CNFSentence("a | b | c");
            assertEquals("a | b | c", cnf.toString());

            cnf = new CNFSentence("(a | b | c)");
            assertEquals("a | b | c", cnf.toString());

            cnf = new CNFSentence("(a | b | c) & (c | d | e)");
            assertEquals("(a | b | c) & (c | d | e)", cnf.toString());

            cnf = new CNFSentence("a & b & c");
            assertEquals("(a) & (b) & (c)", cnf.toString());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    void equalsTest() {
        try {
            CNFSentence cnf1 = new CNFSentence("a | b");
            CNFSentence cnf2 = new CNFSentence("b | a");
            assertEquals(cnf1, cnf2);

            cnf1 = new CNFSentence("(a | c) & (c | a | d) & (e | f)");
            cnf2 = new CNFSentence("(e | f) & (a | c | d) & (c | a)");
            assertEquals(cnf1, cnf2);

            cnf1 = new CNFSentence("a | b | c");
            cnf2 = new CNFSentence("a | b | d");
            assertNotEquals(cnf1, cnf2);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    void hashCodeTest() {
        try {
            CNFSentence cnf1 = new CNFSentence("a | b");
            CNFSentence cnf2 = new CNFSentence("b | a");
            assertEquals(cnf1.hashCode(), cnf2.hashCode());

            cnf1 = new CNFSentence("(a | c) & (c | a | d) & (e | f)");
            cnf2 = new CNFSentence("(e | f) & (a | c | d) & (c | a)");
            assertEquals(cnf1.hashCode(), cnf2.hashCode());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }
}