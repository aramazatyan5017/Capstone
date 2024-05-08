package org.example.domain;

import org.example.SentenceCommon;
import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.sentence.propositional.PropositionalClause;
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
class PropositionalCNFSentenceTest extends SentenceCommon {

    @Test
    public void createAbnormal() {
        LinkedHashSet<PropositionalClause> set = null;
        assertThrows(IllegalArgumentException.class, () -> new PropositionalCNFSentence(set));

        assertThrows(IllegalArgumentException.class, () -> new PropositionalCNFSentence(new LinkedHashSet<>()));

        LinkedHashSet<PropositionalClause> nullSet = new LinkedHashSet<>();
        nullSet.add(null);
        nullSet.add(null);
        assertThrows(IllegalArgumentException.class, () -> new PropositionalCNFSentence(nullSet));

        PropositionalClause[] clauses = null;
        assertThrows(IllegalArgumentException.class, () -> new PropositionalCNFSentence(clauses));

        assertThrows(IllegalArgumentException.class, () -> new PropositionalCNFSentence(null, null, null));

        String str = null;
        assertThrows(ParseException.class, () -> new PropositionalCNFSentence(str));

        assertThrows(ParseException.class, () -> new PropositionalCNFSentence("  "));

        assertThrows(ParseException.class, () -> new PropositionalCNFSentence("a => ab"));
        assertThrows(ParseException.class, () -> new PropositionalCNFSentence("a <=> c | d"));
        assertThrows(ParseException.class, () -> new PropositionalCNFSentence("!(a | b | c)"));
        assertThrows(ParseException.class, () -> new PropositionalCNFSentence("!(a | b | c)(("));
        assertThrows(ParseException.class, () -> new PropositionalCNFSentence("(a | b | c) => (!a | d | e)"));
        assertThrows(ParseException.class, () -> new PropositionalCNFSentence("(a & b & c) | (d & e & f)"));
        assertThrows(ParseException.class, () -> new PropositionalCNFSentence("(a | b) &&& (b | d)"));
        assertThrows(ParseException.class, () -> new PropositionalCNFSentence("!(a | b) & (b | d)"));
        assertThrows(ParseException.class, () -> new PropositionalCNFSentence("(a | b & c | d)"));
        assertThrows(ParseException.class, () -> new PropositionalCNFSentence("!(!(!(A & B)))"));
    }

    @Test
    public void createNormal() {
        try {
            LinkedHashSet<PropositionalClause> set = new LinkedHashSet<>();
            set.add(new PropositionalClause("A"));
            set.add(new PropositionalClause("B | C | !D"));
            PropositionalCNFSentence cnfSentence = new PropositionalCNFSentence(set);
            assertEquals(2, cnfSentence.size());

            set = new LinkedHashSet<>();
            set.add(new PropositionalClause("A | C | D"));
            set.add(null);
            set.add(new PropositionalClause("B"));
            set.add(null);
            cnfSentence = new PropositionalCNFSentence(set);
            assertEquals(2, cnfSentence.size());

            cnfSentence = new PropositionalCNFSentence(new PropositionalClause("A | B"), null, new PropositionalClause("C | D"));
            assertEquals(2, cnfSentence.size());

            cnfSentence = new PropositionalCNFSentence("a");
            assertEquals(1, cnfSentence.size());

            cnfSentence = new PropositionalCNFSentence("(a)");
            assertEquals(1, cnfSentence.size());

            cnfSentence = new PropositionalCNFSentence("a | b");
            assertEquals(1, cnfSentence.size());

            cnfSentence = new PropositionalCNFSentence("(a | b)");
            assertEquals(1, cnfSentence.size());

            cnfSentence = new PropositionalCNFSentence("(a | b) & (c | d)");
            assertEquals(2, cnfSentence.size());

            cnfSentence = new PropositionalCNFSentence("a & b & c & d");
            assertEquals(4, cnfSentence.size());

            cnfSentence = new PropositionalCNFSentence("(a & b & c & d)");
            assertEquals(4, cnfSentence.size());

            cnfSentence = new PropositionalCNFSentence("(a) & (b) & (c) & (d)");
            assertEquals(4, cnfSentence.size());

            cnfSentence = new PropositionalCNFSentence("a & (true) & (false) & (c | d)");
            assertEquals(4, cnfSentence.size());

            cnfSentence = new PropositionalCNFSentence("!!(c | !!!d) & !(!(a | b))");
            assertEquals(2, cnfSentence.size());

            cnfSentence = new PropositionalCNFSentence("((!!!!(c | d) & !(!(!(e))) & !(!(!!!a | !!b))))");
            assertEquals(3, cnfSentence.size());

            cnfSentence = new PropositionalCNFSentence("(!(!((A | C) & (B | E))) & D)");
            assertEquals(3, cnfSentence.size());

            cnfSentence = new PropositionalCNFSentence("(!(!(A & B)) & C)");
            assertEquals(3, cnfSentence.size());

            cnfSentence = new PropositionalCNFSentence("((a | b) | c) & d");
            assertEquals(2, cnfSentence.size());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void clauseListTest() {
        try {
            PropositionalCNFSentence cnfSentence = new PropositionalCNFSentence("(a | b | c) & (D | !e | f)");
            List<PropositionalClause> clauseList = new ArrayList<>();
            clauseList.add(new PropositionalClause("a | b | c"));
            clauseList.add(new PropositionalClause("(D | !e | f)"));
            assertEquals(clauseList, cnfSentence.getClauseList());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void sentenceTypeTest() {
        try {
            PropositionalCNFSentence cnfSentence = new PropositionalCNFSentence("A | B | C");
            assertSame(cnfSentence.type(), PropositionalSentenceType.CNF);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void minimalCNFTest() {
        try {
            PropositionalCNFSentence cnfNotConverted = new PropositionalCNFSentence("(a | B | c) & (g | !D | c)");
            PropositionalCNFSentence cnfSentence = cnfNotConverted.minimalCNF();
            assertEquals(2, cnfSentence.size());

            LinkedHashSet<PropositionalClause> clauseSet = new LinkedHashSet<>();
            clauseSet.add(new PropositionalClause("a | B | c"));
            clauseSet.add(new PropositionalClause("(g | !D | c)"));
            assertEquals(clauseSet, cnfSentence.getClauses());

            cnfNotConverted = new PropositionalCNFSentence("(a | b) & (b | c | a)");
            cnfSentence = cnfNotConverted.minimalCNF();
            assertEquals(1, cnfSentence.size());

            clauseSet = new LinkedHashSet<>();
            clauseSet.add(new PropositionalClause("a | b"));
            assertEquals(clauseSet, cnfSentence.getClauses());

            cnfNotConverted = new PropositionalCNFSentence("a & (b | c | TRUE) & (false | e)");
            cnfSentence = cnfNotConverted.minimalCNF();
            assertEquals(2, cnfSentence.size());

            clauseSet = new LinkedHashSet<>();
            clauseSet.add(new PropositionalClause("a"));
            clauseSet.add(new PropositionalClause("e"));
            assertEquals(clauseSet, cnfSentence.getClauses());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }

        try {
            PropositionalCNFSentence cnfNotConverted = new PropositionalCNFSentence("(a | !a | b) & (!c | e | f | c)");
            PropositionalCNFSentence cnfSentence = cnfNotConverted.minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof TautologyException)) {
                fail("should have thrown a TautologyException");
            }
        }

        try {
            PropositionalCNFSentence cnfNotConverted = new PropositionalCNFSentence("!a & (!b) & !(c) & (a | b | c)");
            PropositionalCNFSentence cnfSentence = cnfNotConverted.minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof ContradictionException)) {
                fail("should have thrown a ContradictionException");
            }
        }

        try {
            PropositionalCNFSentence cnfNotConverted = new PropositionalCNFSentence("TRUE & (b | c | TRUE) & (!false | E)");
            PropositionalCNFSentence cnfSentence = cnfNotConverted.minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof TautologyException)) {
                fail("should have thrown a TautologyException");
            }
        }

        try {
            PropositionalCNFSentence cnfNotConverted = new PropositionalCNFSentence("a & (b | c | TRUE) & (false) & (false | E)");
            PropositionalCNFSentence cnfSentence = cnfNotConverted.minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof ContradictionException)) {
                fail("should have thrown a ContradictionException");
            }
        }
    }

    @Test
    public void satisfiabilityTypeTest() {
        try {
            PropositionalCNFSentence cnfSentence = new PropositionalCNFSentence("(A | !A) & (B | !B | C)");
            assertSame(cnfSentence.satisfiabilityType(), SatisfiabilityType.TAUTOLOGY);

            cnfSentence = new PropositionalCNFSentence("A & !A");
            assertSame(cnfSentence.satisfiabilityType(), SatisfiabilityType.CONTRADICTION);

            cnfSentence = new PropositionalCNFSentence("(a | b | c) & (d | e) & (f)");
            assertSame(cnfSentence.satisfiabilityType(), SatisfiabilityType.CONTINGENCY);

            cnfSentence = new PropositionalCNFSentence("true");
            assertSame(cnfSentence.satisfiabilityType(), SatisfiabilityType.TAUTOLOGY);

            cnfSentence = new PropositionalCNFSentence("false");
            assertSame(cnfSentence.satisfiabilityType(), SatisfiabilityType.CONTRADICTION);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void truthTableTest() {
        try {
            PropositionalCNFSentence cnfSentence = new PropositionalCNFSentence("(A | !A) & (B | !B | C)");
            assertThrows(TautologyException.class, cnfSentence::truthTable);

            cnfSentence = new PropositionalCNFSentence("A & !A");
            assertThrows(ContradictionException.class, cnfSentence::truthTable);

            cnfSentence = new PropositionalCNFSentence("(a | b | c) & (d | e) & (f)");
            assertNotNull(cnfSentence.truthTable());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void isCanonicalTest() {
        try {
            PropositionalCNFSentence cnfSentence = new PropositionalCNFSentence("(a | b) & (!a | c | d) & (b)");
            assertFalse(cnfSentence.isCanonical());

            cnfSentence = new PropositionalCNFSentence("a");
            assertTrue(cnfSentence.isCanonical());

            cnfSentence = new PropositionalCNFSentence("(a | b | c) & (!a | b | !c) & (a | !b | !c)");
            assertTrue(cnfSentence.isCanonical());

            cnfSentence = new PropositionalCNFSentence("(a | b | c | d) & (!a | b | !c) & (a | !b | !c)");
            assertFalse(cnfSentence.isCanonical());

            cnfSentence = new PropositionalCNFSentence("(a | b | c) & (!a | b | !c) & (a | !b)");
            assertFalse(cnfSentence.isCanonical());

            cnfSentence = new PropositionalCNFSentence("(a | !a | b | c)");
            assertFalse(cnfSentence.isCanonical());

            cnfSentence = new PropositionalCNFSentence("(a | b | c | false) & (!a | b | !c) & (a | !b | !c)");
            assertFalse(cnfSentence.isCanonical());

            cnfSentence = new PropositionalCNFSentence("(a | b | c) & (!a | b | !c) & (a | !b | !c) & true");
            assertFalse(cnfSentence.isCanonical());

            cnfSentence = new PropositionalCNFSentence("(a | b | c | false) & (!a | b | !c) & (a | !b | !c) & true & (g | e | true)");
            assertTrue(cnfSentence.minimalCNF().isCanonical());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void toStringTest() {
        try {
            PropositionalCNFSentence cnfSentence = new PropositionalCNFSentence("a | b | c");
            assertEquals("a | b | c", cnfSentence.toString());

            cnfSentence = new PropositionalCNFSentence("(a | b | c)");
            assertEquals("a | b | c", cnfSentence.toString());

            cnfSentence = new PropositionalCNFSentence("(a | b | c) & (c | d | e)");
            assertEquals("(a | b | c) & (c | d | e)", cnfSentence.toString());

            cnfSentence = new PropositionalCNFSentence("a & b & c");
            assertEquals("(a) & (b) & (c)", cnfSentence.toString());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void equalsTest() {
        try {
            PropositionalCNFSentence cnfSentence1 = new PropositionalCNFSentence("a | b");
            PropositionalCNFSentence cnfSentence2 = new PropositionalCNFSentence("b | a");
            assertEquals(cnfSentence1, cnfSentence2);

            cnfSentence1 = new PropositionalCNFSentence("(a | c) & (c | a | d) & (e | f)");
            cnfSentence2 = new PropositionalCNFSentence("(e | f) & (a | c | d) & (c | a)");
            assertEquals(cnfSentence1, cnfSentence2);

            cnfSentence1 = new PropositionalCNFSentence("a | b | c");
            cnfSentence2 = new PropositionalCNFSentence("a | b | d");
            assertNotEquals(cnfSentence1, cnfSentence2);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void hashCodeTest() {
        try {
            PropositionalCNFSentence cnfSentence1 = new PropositionalCNFSentence("a | b");
            PropositionalCNFSentence cnfSentence2 = new PropositionalCNFSentence("b | a");
            assertEquals(cnfSentence1.hashCode(), cnfSentence2.hashCode());

            cnfSentence1 = new PropositionalCNFSentence("(a | c) & (c | a | d) & (e | f)");
            cnfSentence2 = new PropositionalCNFSentence("(e | f) & (a | c | d) & (c | a)");
            assertEquals(cnfSentence1.hashCode(), cnfSentence2.hashCode());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }
}