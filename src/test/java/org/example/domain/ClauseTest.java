package org.example.domain;

import org.example.SentenceCommon;
import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Clause;
import org.example.domain.sentence.Literal;
import org.example.exception.ContradictionException;
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
class ClauseTest extends SentenceCommon {

    @Test
    public void createAbnormal() {
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
    public void createNormal() {
        try {
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

            clause = new Clause("true | A | !B");
            assertEquals(3, clause.size());

            clause = new Clause("true | false | !!false");
            assertEquals(2, clause.size());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void literalListTest() {
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
    public void sentenceTypeTest() {
        try {
            Clause clause = new Clause("A | B | C");
            assertSame(clause.type(), SentenceType.CLAUSE);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void minimalCNFTest() {
        try {
            Clause clause = new Clause("a | B | c");
            CNFSentence cnfSentence = clause.minimalCNF();
            assertEquals(1, cnfSentence.size());

            LinkedHashSet<Clause> clauseSet = new LinkedHashSet<>();
            clauseSet.add(new Clause("a | B | c"));
            assertEquals(clauseSet, cnfSentence.getClauses());

            clause = new Clause("false | !true | A | B");
            cnfSentence = clause.minimalCNF();
            assertEquals(1, cnfSentence.size());

            clauseSet = new LinkedHashSet<>();
            clauseSet.add(new Clause("A | B"));
            assertEquals(clauseSet, cnfSentence.getClauses());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }

        try {
            Clause clause = new Clause("a | !a | b");
            CNFSentence cnfSentence = clause.minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof TautologyException)) {
                fail("should have thrown a TautologyException");
            }
        }

        try {
            Clause clause = new Clause("true | false | A");
            CNFSentence cnfSentence = clause.minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof TautologyException)) {
                fail("should have thrown a TautologyException");
            }
        }

        try {
            Clause clause = new Clause("false | !true | !!(!true)");
            CNFSentence cnfSentence = clause.minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof ContradictionException)) {
                fail("should have thrown a ContradictionException");
            }
        }

        try {
            new Clause("truE").minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof TautologyException)) {
                fail("should have thrown a TautologyException");
            }
        }

        try {
            new Clause("FalSe").minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof ContradictionException)) {
                fail("should have thrown a TautologyException");
            }
        }
    }

    @Test
    public void satisfiabilityTypeTest() {
        try {
            Clause clause = new Clause("A | !A | B");
            assertSame(clause.satisfiabilityType(), SatisfiabilityType.TAUTOLOGY);

            clause = new Clause("A | B | C");
            assertSame(clause.satisfiabilityType(), SatisfiabilityType.CONTINGENCY);

            clause = new Clause("true | false | A");
            assertSame(clause.satisfiabilityType(), SatisfiabilityType.TAUTOLOGY);

            clause = new Clause("false | !true");
            assertSame(clause.satisfiabilityType(), SatisfiabilityType.CONTRADICTION);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void truthTableTest() {
        try {
            Clause clause = new Clause("A | !A | B");
            assertThrows(TautologyException.class, clause::truthTable);

            clause = new Clause("A | B | C");
            assertNotNull(clause.truthTable());

            clause = new Clause("true | false | A");
            assertThrows(TautologyException.class, clause::truthTable);

            clause = new Clause("false | !true");
            assertThrows(ContradictionException.class, clause::truthTable);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void toStringTest() {
        try {
            Clause clause = new Clause("a");
            assertEquals("a", clause.toString());

            clause = new Clause("a | b | c");
            assertEquals("a | b | c", clause.toString());

            clause = new Clause(new Literal("a"), new Literal("b", true));
            assertEquals("a | !b", clause.toString());

            clause = new Clause("trUE | fAlse | A");
            assertEquals("TRUE | FALSE | A", clause.toString());

            clause = new Clause("faLSe | !!!true");
            assertEquals("FALSE", clause.toString());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void equalsTest() {
        try {
            Clause clause1 = new Clause("a | b");
            Clause clause2 = new Clause("b | a");
            assertEquals(clause1, clause2);

            clause1 = new Clause("a | b | c");
            clause2 = new Clause("a | b | d");
            assertNotEquals(clause1, clause2);

            clause1 = new Clause("a | b | trUe");
            clause2 = new Clause("TRue | b | a");
            assertEquals(clause1, clause2);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void hashCodeTest() {
        try {
            Clause clause1 = new Clause("a | b");
            Clause clause2 = new Clause("b | a");
            assertEquals(clause1.hashCode(), clause2.hashCode());

            clause1 = new Clause("a | b | trUe");
            clause2 = new Clause("TRue | b | a");
            assertEquals(clause1.hashCode(), clause2.hashCode());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }
}