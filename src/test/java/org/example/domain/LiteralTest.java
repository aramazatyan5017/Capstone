package org.example.domain;

import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Clause;
import org.example.domain.sentence.Literal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author aram.azatyan | 3/14/2024 10:03 AM
 */
class LiteralTest {

    @Test
    void createLiteralAbnormal() {
        assertThrows(IllegalArgumentException.class, () -> new Literal(""));
        assertThrows(IllegalArgumentException.class, () -> new Literal(null));
        assertThrows(IllegalArgumentException.class, () -> new Literal("!aa"));
        assertThrows(IllegalArgumentException.class, () -> new Literal("(a)"));
        assertThrows(IllegalArgumentException.class, () -> new Literal("!", true));
        assertThrows(IllegalArgumentException.class, () -> new Literal("=>aa"));
        assertThrows(IllegalArgumentException.class, () -> new Literal("<=>"));
        assertThrows(IllegalArgumentException.class, () -> new Literal("a <=> b"));
        assertThrows(IllegalArgumentException.class, () -> new Literal("|"));
        assertThrows(IllegalArgumentException.class, () -> new Literal("&&|"));
        assertThrows(IllegalArgumentException.class, () -> new Literal("    "));
        assertThrows(IllegalArgumentException.class, () -> new Literal("!&"));
    }

    @Test
    void createLiteralNormal() {
        try {
            Literal l = new Literal("aaaa");
            l = new Literal("<>=aaaa");
            l = new Literal("ttt", false);
            l = new Literal("ttt", true);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    void sentenceTypeTest() {
        Literal l = new Literal("A", true);
        assertTrue(l.isLiteral());
        assertFalse(l.isClause());
        assertFalse(l.isCnf());
        assertFalse(l.isGenericComplex());
    }


    @Test
    void convertToCNFTest() {
        Literal l = new Literal("A", true);
        CNFSentence cnf = l.convertToCNF();
        assertEquals(cnf.getClauses().size(), 1);
        Clause clause = cnf.getClauseList().get(0);
        assertEquals(clause.size(), 1);
        assertEquals(clause.getLiteralList().get(0), l);
    }

    @Test
    void toStringTest() {
        Literal l = new Literal("B", false);
        assertEquals("B", l.toString());

        l = new Literal("kkkk", true);
        assertEquals("!kkkk", l.toString());
    }

    @Test
    void equalsTest() {
        Literal l1 = new Literal("B", false);
        Literal l2 = new Literal("C");
        assertNotEquals(l1, l2);

        l1 = new Literal("B", true);
        l2 = new Literal("B", false);
        assertNotEquals(l1, l2);

        l1 = new Literal("A", true);
        l2 = new Literal("A", true);
        assertEquals(l1, l2);
    }

    @Test
    void equalsIgnoreNegationTest() {
        Literal l1 = new Literal("A", true);
        Literal l2 = new Literal("B", true);
        assertFalse(l1.equalsIgnoreNegation(l2));

        l1 = new Literal("A", true);
        l2 = new Literal("A", false);
        assertTrue(l1.equalsIgnoreNegation(l2));
    }

    @Test
    void hashCodeTest() {
        Literal l1 = new Literal("A", false);
        Literal l2 = new Literal("A", false);
        assertEquals(l1.hashCode(), l2.hashCode());

        l1 = new Literal("A", false);
        l2 = new Literal("A", true);
        assertNotEquals(l1.hashCode(), l2.hashCode());
    }
}