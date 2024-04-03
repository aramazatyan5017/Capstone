package org.example.domain;

import org.example.SentenceCommon;
import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Clause;
import org.example.domain.sentence.Literal;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author aram.azatyan | 3/14/2024 10:03 AM
 */
class LiteralTest extends SentenceCommon {

    @Test
    public void createAbnormal() {
        assertThrows(IllegalArgumentException.class, () -> new Literal(""));
        assertThrows(IllegalArgumentException.class, () -> new Literal(null));
        assertThrows(IllegalArgumentException.class, () -> new Literal("!", true));
        assertThrows(IllegalArgumentException.class, () -> new Literal("=>aa"));
        assertThrows(IllegalArgumentException.class, () -> new Literal("<=>"));
        assertThrows(IllegalArgumentException.class, () -> new Literal("a <=> b"));
        assertThrows(IllegalArgumentException.class, () -> new Literal("|"));
        assertThrows(IllegalArgumentException.class, () -> new Literal("&&|"));
        assertThrows(IllegalArgumentException.class, () -> new Literal("    "));
        assertThrows(IllegalArgumentException.class, () -> new Literal("!&"));
        assertThrows(IllegalArgumentException.class, () -> new Literal("trUE", false));
        assertThrows(IllegalArgumentException.class, () -> new Literal("fAlSE", true));
        assertThrows(IllegalArgumentException.class, () -> new Literal("false"));
    }

    @Test
    public void createNormal() {
        try {
            Literal l = new Literal("aaaa");
            l = new Literal("<>=aaaa");

            l = new Literal("!a");
            assertEquals("a", l.getName());
            assertTrue(l.isNegated());

            l = new Literal("!(!a)");
            assertEquals("a", l.getName());
            assertFalse(l.isNegated());

            l = new Literal("ttt", false);
            l = new Literal("ttt", true);

            l = Literal.TRUE;
            l = Literal.FALSE;
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void sentenceTypeTest() {
        Literal l = new Literal("A", true);
        assertSame(l.type(), SentenceType.LITERAL);
    }

    @Test
    public void minimalCNFTest() {
        try {
            Literal l = new Literal("A", true);
            CNFSentence cnfSentence = l.minimalCNF();
            assertEquals(cnfSentence.getClauses().size(), 1);
            Clause clause = cnfSentence.getClauseList().get(0);
            assertEquals(clause.size(), 1);
            assertEquals(clause.getLiteralList().get(0), l);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }

        try {
            Literal l = Literal.TRUE;
            l.minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof TautologyException)) {
                fail("should have thrown a TautologyException");
            }
        }

        try {
            Literal l = Literal.FALSE;
            l.minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof ContradictionException)) {
                fail("should have thrown a ContradictionException");
            }
        }
    }

    @Test
    public void satisfiabilityTypeTest() {
        Literal l = new Literal("!A");
        assertSame(l.satisfiabilityType(), SatisfiabilityType.CONTINGENCY);

        l = new Literal("B");
        assertSame(l.satisfiabilityType(), SatisfiabilityType.CONTINGENCY);

        l = Literal.TRUE;
        assertSame(l.satisfiabilityType(), SatisfiabilityType.TAUTOLOGY);

        l = Literal.FALSE;
        assertSame(l.satisfiabilityType(), SatisfiabilityType.CONTRADICTION);
    }

    @Test
    public void truthTableTest() {
        try {
            Literal l = new Literal("!A");
            assertNotNull(l.truthTable());

            l = new Literal("B");
            assertNotNull(l.truthTable());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }

        try {
            Literal.TRUE.truthTable();
        } catch (Exception e) {
            if (!(e instanceof TautologyException)) {
                fail("should have thrown a TautologyException");
            }
        }

        try {
            Literal.FALSE.truthTable();
        } catch (Exception e) {
            if (!(e instanceof ContradictionException)) {
                fail("should have thrown a ContradictionException");
            }
        }
    }

    @Test
    public void toStringTest() {
        Literal l = new Literal("B", false);
        assertEquals("B", l.toString());

        l = new Literal("kkkk", true);
        assertEquals("!kkkk", l.toString());

        l = Literal.TRUE;
        assertEquals("TRUE", l.toString());

        l = Literal.FALSE;
        assertEquals("FALSE", l.toString());
    }

    @Test
    public void equalsTest() {
        Literal l1 = new Literal("B", false);
        Literal l2 = new Literal("C", false);
        assertNotEquals(l1, l2);

        l1 = new Literal("B", true);
        l2 = new Literal("B", false);
        assertNotEquals(l1, l2);

        l1 = new Literal("A", true);
        l2 = new Literal("A", true);
        assertEquals(l1, l2);

        l1 = Literal.TRUE;
        l2 = Literal.FALSE;
        assertNotEquals(l1, l2);

        l1 = Literal.TRUE;
        l2 = Literal.TRUE;
        assertEquals(l1, l2);

        l1 = Literal.FALSE;
        l2 = Literal.FALSE;
        assertEquals(l1, l2);
    }

    @Test
    public void equalsIgnoreNegationTest() {
        Literal l1 = new Literal("A", true);
        Literal l2 = new Literal("B", true);
        assertFalse(l1.equalsIgnoreNegation(l2));

        l1 = new Literal("A", true);
        l2 = new Literal("A", false);
        assertTrue(l1.equalsIgnoreNegation(l2));

        l1 = Literal.TRUE;
        l2 = Literal.FALSE;
        assertFalse(l1.equalsIgnoreNegation(l2));

        l1 = Literal.TRUE;
        l2 = Literal.TRUE;
        assertTrue(l1.equalsIgnoreNegation(l2));

        l1 = Literal.FALSE;
        l2 = Literal.FALSE;
        assertTrue(l1.equalsIgnoreNegation(l2));
    }

    @Test
    public void hashCodeTest() {
        Literal l1 = new Literal("A", false);
        Literal l2 = new Literal("A", false);
        assertEquals(l1.hashCode(), l2.hashCode());

        l1 = new Literal("A", false);
        l2 = new Literal("A", true);
        assertNotEquals(l1.hashCode(), l2.hashCode());

        l1 = Literal.TRUE;
        l2 = Literal.FALSE;
        assertNotEquals(l1.hashCode(), l2.hashCode());

        l1 = Literal.TRUE;
        l2 = Literal.TRUE;
        assertEquals(l1.hashCode(), l2.hashCode());

        l1 = Literal.FALSE;
        l2 = Literal.FALSE;
        assertEquals(l1.hashCode(), l2.hashCode());
    }
}