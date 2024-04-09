package org.example.domain;

import org.example.SentenceCommon;
import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Clause;
import org.example.exception.TautologyException;
import org.example.exception.ContradictionException;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author aram.azatyan | 3/15/2024 10:22 PM
 */
class CNFSentenceTest extends SentenceCommon {

    @Test
    public void createAbnormal() {
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
        assertThrows(ParseException.class, () -> new CNFSentence("(a | b & c | d)"));
        assertThrows(ParseException.class, () -> new CNFSentence("!(!(!(A & B)))"));
    }

    @Test
    public void createNormal() {
        try {
            LinkedHashSet<Clause> set = new LinkedHashSet<>();
            set.add(new Clause("A"));
            set.add(new Clause("B | C | !D"));
            CNFSentence cnfSentence = new CNFSentence(set);
            assertEquals(2, cnfSentence.size());

            set = new LinkedHashSet<>();
            set.add(new Clause("A | C | D"));
            set.add(null);
            set.add(new Clause("B"));
            set.add(null);
            cnfSentence = new CNFSentence(set);
            assertEquals(2, cnfSentence.size());

            cnfSentence = new CNFSentence(new Clause("A | B"), null, new Clause("C | D"));
            assertEquals(2, cnfSentence.size());

            cnfSentence = new CNFSentence("a");
            assertEquals(1, cnfSentence.size());

            cnfSentence = new CNFSentence("(a)");
            assertEquals(1, cnfSentence.size());

            cnfSentence = new CNFSentence("a | b");
            assertEquals(1, cnfSentence.size());

            cnfSentence = new CNFSentence("(a | b)");
            assertEquals(1, cnfSentence.size());

            cnfSentence = new CNFSentence("(a | b) & (c | d)");
            assertEquals(2, cnfSentence.size());

            cnfSentence = new CNFSentence("a & b & c & d");
            assertEquals(4, cnfSentence.size());

            cnfSentence = new CNFSentence("(a & b & c & d)");
            assertEquals(4, cnfSentence.size());

            cnfSentence = new CNFSentence("(a) & (b) & (c) & (d)");
            assertEquals(4, cnfSentence.size());

            cnfSentence = new CNFSentence("a & (true) & (false) & (c | d)");
            assertEquals(4, cnfSentence.size());

            cnfSentence = new CNFSentence("!!(c | !!!d) & !(!(a | b))");
            assertEquals(2, cnfSentence.size());

            cnfSentence = new CNFSentence("((!!!!(c | d) & !(!(!(e))) & !(!(!!!a | !!b))))");
            assertEquals(3, cnfSentence.size());

            cnfSentence = new CNFSentence("(!(!((A | C) & (B | E))) & D)");
            assertEquals(3, cnfSentence.size());

            cnfSentence = new CNFSentence("(!(!(A & B)) & C)");
            assertEquals(3, cnfSentence.size());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void clauseListTest() {
        try {
            CNFSentence cnfSentence = new CNFSentence("(a | b | c) & (D | !e | f)");
            List<Clause> clauseList = new ArrayList<>();
            clauseList.add(new Clause("a | b | c"));
            clauseList.add(new Clause("(D | !e | f)"));
            assertEquals(clauseList, cnfSentence.getClauseList());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void sentenceTypeTest() {
        try {
            CNFSentence cnfSentence = new CNFSentence("A | B | C");
            assertSame(cnfSentence.type(), SentenceType.CNF);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void minimalCNFTest() {
        try {
            CNFSentence cnfNotConverted = new CNFSentence("(a | B | c) & (g | !D | c)");
            CNFSentence cnfSentence = cnfNotConverted.minimalCNF();
            assertEquals(2, cnfSentence.size());

            LinkedHashSet<Clause> clauseSet = new LinkedHashSet<>();
            clauseSet.add(new Clause("a | B | c"));
            clauseSet.add(new Clause("(g | !D | c)"));
            assertEquals(clauseSet, cnfSentence.getClauses());

            cnfNotConverted = new CNFSentence("(a | b) & (b | c | a)");
            cnfSentence = cnfNotConverted.minimalCNF();
            assertEquals(1, cnfSentence.size());

            clauseSet = new LinkedHashSet<>();
            clauseSet.add(new Clause("a | b"));
            assertEquals(clauseSet, cnfSentence.getClauses());

            cnfNotConverted = new CNFSentence("a & (b | c | TRUE) & (false | e)");
            cnfSentence = cnfNotConverted.minimalCNF();
            assertEquals(2, cnfSentence.size());

            clauseSet = new LinkedHashSet<>();
            clauseSet.add(new Clause("a"));
            clauseSet.add(new Clause("e"));
            assertEquals(clauseSet, cnfSentence.getClauses());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }

        try {
            CNFSentence cnfNotConverted = new CNFSentence("(a | !a | b) & (!c | e | f | c)");
            CNFSentence cnfSentence = cnfNotConverted.minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof TautologyException)) {
                fail("should have thrown a TautologyException");
            }
        }

        try {
            CNFSentence cnfNotConverted = new CNFSentence("!a & (!b) & !(c) & (a | b | c)");
            CNFSentence cnfSentence = cnfNotConverted.minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof ContradictionException)) {
                fail("should have thrown a ContradictionException");
            }
        }

        try {
            CNFSentence cnfNotConverted = new CNFSentence("TRUE & (b | c | TRUE) & (!false | E)");
            CNFSentence cnfSentence = cnfNotConverted.minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof TautologyException)) {
                fail("should have thrown a TautologyException");
            }
        }

        try {
            CNFSentence cnfNotConverted = new CNFSentence("a & (b | c | TRUE) & (false) & (false | E)");
            CNFSentence cnfSentence = cnfNotConverted.minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof ContradictionException)) {
                fail("should have thrown a ContradictionException");
            }
        }
    }

    @Test
    public void satisfiabilityTypeTest() {
        try {
            CNFSentence cnfSentence = new CNFSentence("(A | !A) & (B | !B | C)");
            assertSame(cnfSentence.satisfiabilityType(), SatisfiabilityType.TAUTOLOGY);

            cnfSentence = new CNFSentence("A & !A");
            assertSame(cnfSentence.satisfiabilityType(), SatisfiabilityType.CONTRADICTION);

            cnfSentence = new CNFSentence("(a | b | c) & (d | e) & (f)");
            assertSame(cnfSentence.satisfiabilityType(), SatisfiabilityType.CONTINGENCY);

            cnfSentence = new CNFSentence("true");
            assertSame(cnfSentence.satisfiabilityType(), SatisfiabilityType.TAUTOLOGY);

            cnfSentence = new CNFSentence("false");
            assertSame(cnfSentence.satisfiabilityType(), SatisfiabilityType.CONTRADICTION);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void truthTableTest() {
        try {
            CNFSentence cnfSentence = new CNFSentence("(A | !A) & (B | !B | C)");
            assertThrows(TautologyException.class, cnfSentence::truthTable);

            cnfSentence = new CNFSentence("A & !A");
            assertThrows(ContradictionException.class, cnfSentence::truthTable);

            cnfSentence = new CNFSentence("(a | b | c) & (d | e) & (f)");
            assertNotNull(cnfSentence.truthTable());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void isCanonicalTest() {
        try {
            CNFSentence cnfSentence = new CNFSentence("(a | b) & (!a | c | d) & (b)");
            assertFalse(cnfSentence.isCanonical());

            cnfSentence = new CNFSentence("a");
            assertTrue(cnfSentence.isCanonical());

            cnfSentence = new CNFSentence("(a | b | c) & (!a | b | !c) & (a | !b | !c)");
            assertTrue(cnfSentence.isCanonical());

            cnfSentence = new CNFSentence("(a | b | c | d) & (!a | b | !c) & (a | !b | !c)");
            assertFalse(cnfSentence.isCanonical());

            cnfSentence = new CNFSentence("(a | b | c) & (!a | b | !c) & (a | !b)");
            assertFalse(cnfSentence.isCanonical());

            cnfSentence = new CNFSentence("(a | !a | b | c)");
            assertFalse(cnfSentence.isCanonical());

            cnfSentence = new CNFSentence("(a | b | c | false) & (!a | b | !c) & (a | !b | !c)");
            assertFalse(cnfSentence.isCanonical());

            cnfSentence = new CNFSentence("(a | b | c) & (!a | b | !c) & (a | !b | !c) & true");
            assertFalse(cnfSentence.isCanonical());

            cnfSentence = new CNFSentence("(a | b | c | false) & (!a | b | !c) & (a | !b | !c) & true & (g | e | true)");
            assertTrue(cnfSentence.minimalCNF().isCanonical());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void toStringTest() {
        try {
            CNFSentence cnfSentence = new CNFSentence("a | b | c");
            assertEquals("a | b | c", cnfSentence.toString());

            cnfSentence = new CNFSentence("(a | b | c)");
            assertEquals("a | b | c", cnfSentence.toString());

            cnfSentence = new CNFSentence("(a | b | c) & (c | d | e)");
            assertEquals("(a | b | c) & (c | d | e)", cnfSentence.toString());

            cnfSentence = new CNFSentence("a & b & c");
            assertEquals("(a) & (b) & (c)", cnfSentence.toString());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void equalsTest() {
        try {
            CNFSentence cnfSentence1 = new CNFSentence("a | b");
            CNFSentence cnfSentence2 = new CNFSentence("b | a");
            assertEquals(cnfSentence1, cnfSentence2);

            cnfSentence1 = new CNFSentence("(a | c) & (c | a | d) & (e | f)");
            cnfSentence2 = new CNFSentence("(e | f) & (a | c | d) & (c | a)");
            assertEquals(cnfSentence1, cnfSentence2);

            cnfSentence1 = new CNFSentence("a | b | c");
            cnfSentence2 = new CNFSentence("a | b | d");
            assertNotEquals(cnfSentence1, cnfSentence2);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void hashCodeTest() {
        try {
            CNFSentence cnfSentence1 = new CNFSentence("a | b");
            CNFSentence cnfSentence2 = new CNFSentence("b | a");
            assertEquals(cnfSentence1.hashCode(), cnfSentence2.hashCode());

            cnfSentence1 = new CNFSentence("(a | c) & (c | a | d) & (e | f)");
            cnfSentence2 = new CNFSentence("(e | f) & (a | c | d) & (c | a)");
            assertEquals(cnfSentence1.hashCode(), cnfSentence2.hashCode());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }
}