package org.example.domain;

import org.example.SentenceCommon;
import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.sentence.propositional.PropositionalClause;
import org.example.domain.sentence.propositional.Literal;
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
class PropositionalClauseTest extends SentenceCommon {

    @Test
    public void createAbnormal() {
        assertThrows(IllegalArgumentException.class, () -> new PropositionalClause(new LinkedHashSet<>()));

        LinkedHashSet<Literal> nullSet = new LinkedHashSet<>();
        assertThrows(IllegalArgumentException.class, () -> new PropositionalClause(nullSet));

        LinkedHashSet<Literal> set = new LinkedHashSet<>();
        set.add(null);
        set.add(null);
        set.add(null);
        assertThrows(IllegalArgumentException.class, () -> new PropositionalClause(set));

        Literal[] literals = null;
        assertThrows(IllegalArgumentException.class, () -> new PropositionalClause(literals));

        assertThrows(IllegalArgumentException.class, () -> new PropositionalClause(null, null, null));

        String str = null;
        assertThrows(ParseException.class, () -> new PropositionalClause(str));

        assertThrows(ParseException.class, () -> new PropositionalClause("   "));

        assertThrows(ParseException.class, () -> new PropositionalClause("a & b"));
        assertThrows(ParseException.class, () -> new PropositionalClause("(a &)"));
        assertThrows(ParseException.class, () -> new PropositionalClause("a => b & c"));
        assertThrows(ParseException.class, () -> new PropositionalClause("b <=> c <=> !d | f"));
        assertThrows(ParseException.class, () -> new PropositionalClause("!(a | B)"));
    }

    @Test
    public void createNormal() {
        try {
            LinkedHashSet<Literal> set = new LinkedHashSet<>();
            set.add(new Literal("A"));
            set.add(new Literal("B"));
            PropositionalClause clause = new PropositionalClause(set);
            assertEquals(2, clause.size());

            set = new LinkedHashSet<>();
            set.add(new Literal("A"));
            set.add(null);
            set.add(new Literal("B"));
            set.add(null);
            clause = new PropositionalClause(set);
            assertEquals(2, clause.size());

            clause = new PropositionalClause(new Literal("A"), null, new Literal("B"));
            assertEquals(2, clause.size());

            clause = new PropositionalClause("a");
            assertEquals(1, clause.size());

            clause = new PropositionalClause("(a)");
            assertEquals(1, clause.size());

            clause = new PropositionalClause("(a | b)");
            assertEquals(2, clause.size());

            clause = new PropositionalClause("a | b");
            assertEquals(2, clause.size());

            clause = new PropositionalClause("(!(!(!a)) | !!!b | g)");
            assertEquals(3, clause.size());

            clause = new PropositionalClause("!(!(!a)) | !!!b | g");
            assertEquals(3, clause.size());

            clause = new PropositionalClause("true | A | !B");
            assertEquals(3, clause.size());

            clause = new PropositionalClause("true | false | !!false");
            assertEquals(2, clause.size());

            clause = new PropositionalClause("(!(!(A | B)) | C)");
            assertEquals(3, clause.size());

            clause = new PropositionalClause("(a | b) | c");
            assertEquals(3, clause.size());

            clause = new PropositionalClause("!!(!!!!(a | !!b) | !c)");
            assertEquals(3, clause.size());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void literalListTest() {
        try {
            PropositionalClause clause = new PropositionalClause("a | b | c");
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
            PropositionalClause clause = new PropositionalClause("A | B | C");
            assertSame(clause.type(), PropositionalSentenceType.CLAUSE);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void minimalCNFTest() {
        try {
            PropositionalClause clause = new PropositionalClause("a | B | c");
            PropositionalCNFSentence cnfSentence = clause.minimalCNF();
            assertEquals(1, cnfSentence.size());

            LinkedHashSet<PropositionalClause> clauseSet = new LinkedHashSet<>();
            clauseSet.add(new PropositionalClause("a | B | c"));
            assertEquals(clauseSet, cnfSentence.getClauses());

            clause = new PropositionalClause("false | !true | A | B");
            cnfSentence = clause.minimalCNF();
            assertEquals(1, cnfSentence.size());

            clauseSet = new LinkedHashSet<>();
            clauseSet.add(new PropositionalClause("A | B"));
            assertEquals(clauseSet, cnfSentence.getClauses());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }

        try {
            PropositionalClause clause = new PropositionalClause("a | !a | b");
            PropositionalCNFSentence cnfSentence = clause.minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof TautologyException)) {
                fail("should have thrown a TautologyException");
            }
        }

        try {
            PropositionalClause clause = new PropositionalClause("true | false | A");
            PropositionalCNFSentence cnfSentence = clause.minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof TautologyException)) {
                fail("should have thrown a TautologyException");
            }
        }

        try {
            PropositionalClause clause = new PropositionalClause("false | !true | !!(!true)");
            PropositionalCNFSentence cnfSentence = clause.minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof ContradictionException)) {
                fail("should have thrown a ContradictionException");
            }
        }

        try {
            new PropositionalClause("truE").minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof TautologyException)) {
                fail("should have thrown a TautologyException");
            }
        }

        try {
            new PropositionalClause("FalSe").minimalCNF();
        } catch (Exception e) {
            if (!(e instanceof ContradictionException)) {
                fail("should have thrown a TautologyException");
            }
        }
    }

    @Test
    public void satisfiabilityTypeTest() {
        try {
            PropositionalClause clause = new PropositionalClause("A | !A | B");
            assertSame(clause.satisfiabilityType(), SatisfiabilityType.TAUTOLOGY);

            clause = new PropositionalClause("A | B | C");
            assertSame(clause.satisfiabilityType(), SatisfiabilityType.CONTINGENCY);

            clause = new PropositionalClause("true | false | A");
            assertSame(clause.satisfiabilityType(), SatisfiabilityType.TAUTOLOGY);

            clause = new PropositionalClause("false | !true");
            assertSame(clause.satisfiabilityType(), SatisfiabilityType.CONTRADICTION);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void truthTableTest() {
        try {
            PropositionalClause clause = new PropositionalClause("A | !A | B");
            assertThrows(TautologyException.class, clause::truthTable);

            clause = new PropositionalClause("A | B | C");
            assertNotNull(clause.truthTable());

            clause = new PropositionalClause("true | false | A");
            assertThrows(TautologyException.class, clause::truthTable);

            clause = new PropositionalClause("false | !true");
            assertThrows(ContradictionException.class, clause::truthTable);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void toStringTest() {
        try {
            PropositionalClause clause = new PropositionalClause("a");
            assertEquals("a", clause.toString());

            clause = new PropositionalClause("a | b | c");
            assertEquals("a | b | c", clause.toString());

            clause = new PropositionalClause(new Literal("a"), new Literal("b", true));
            assertEquals("a | !b", clause.toString());

            clause = new PropositionalClause("trUE | fAlse | A");
            assertEquals("TRUE | FALSE | A", clause.toString());

            clause = new PropositionalClause("faLSe | !!!true");
            assertEquals("FALSE", clause.toString());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void equalsTest() {
        try {
            PropositionalClause clause1 = new PropositionalClause("a | b");
            PropositionalClause clause2 = new PropositionalClause("b | a");
            assertEquals(clause1, clause2);

            clause1 = new PropositionalClause("a | b | c");
            clause2 = new PropositionalClause("a | b | d");
            assertNotEquals(clause1, clause2);

            clause1 = new PropositionalClause("a | b | trUe");
            clause2 = new PropositionalClause("TRue | b | a");
            assertEquals(clause1, clause2);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void hashCodeTest() {
        try {
            PropositionalClause clause1 = new PropositionalClause("a | b");
            PropositionalClause clause2 = new PropositionalClause("b | a");
            assertEquals(clause1.hashCode(), clause2.hashCode());

            clause1 = new PropositionalClause("a | b | trUe");
            clause2 = new PropositionalClause("TRue | b | a");
            assertEquals(clause1.hashCode(), clause2.hashCode());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }
}