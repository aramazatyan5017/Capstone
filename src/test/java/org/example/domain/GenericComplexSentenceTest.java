package org.example.domain;

import org.example.SentenceCommon;
import org.example.domain.sentence.*;
import org.example.exception.TautologyException;
import org.example.exception.ContradictionException;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.LinkedHashSet;

import static org.example.util.SentenceUtils.convertOnlineCalculatorString;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author aram.azatyan | 3/18/2024 3:14 PM
 */
class GenericComplexSentenceTest extends SentenceCommon {

    @Test
    public void createAbnormal() {
        assertThrows(IllegalArgumentException.class, () -> new GenericComplexSentence(null, null, null));
        assertThrows(IllegalArgumentException.class, () -> new GenericComplexSentence(new Literal("A"), new Literal("B"), null, false));
        assertThrows(IllegalArgumentException.class, () -> new GenericComplexSentence(new Literal("A"), null, Connective.OR, false));
        assertThrows(IllegalArgumentException.class, () -> new GenericComplexSentence(null, new Literal("A"), Connective.BICONDITIONAL, true));

        assertThrows(ParseException.class, () -> new GenericComplexSentence(null));
        assertThrows(ParseException.class, () -> new GenericComplexSentence("  "));
        assertThrows(ParseException.class, () -> new GenericComplexSentence("A"));
        assertThrows(ParseException.class, () -> new GenericComplexSentence("!A"));
        assertThrows(ParseException.class, () -> new GenericComplexSentence("!(!(!A))"));
        assertThrows(ParseException.class, () -> new GenericComplexSentence("!(!(!A!))"));
        assertThrows(ParseException.class, () -> new GenericComplexSentence("A!B"));
        assertThrows(ParseException.class, () -> new GenericComplexSentence("A B"));
        assertThrows(ParseException.class, () -> new GenericComplexSentence("(A & B)("));
        assertThrows(ParseException.class, () -> new GenericComplexSentence("(A & B)("));
        assertThrows(ParseException.class, () -> new GenericComplexSentence("A! & =C> | D"));

        LinkedHashSet<Sentence> sentences = new LinkedHashSet<>();

        try {
            sentences.add(new Literal("a"));
            sentences.add(new Literal("b"));
            sentences.add(new Literal("c"));
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }

        assertThrows(IllegalArgumentException.class, () -> new GenericComplexSentence(sentences, Connective.IMPLICATION, false));
    }

    @Test
    public void createNormal() {
        try {
            GenericComplexSentence sentence = new GenericComplexSentence("A & B");
            sentence = new GenericComplexSentence("A & B | C");
            sentence = new GenericComplexSentence("A & (B | C)");
            sentence = new GenericComplexSentence("A & !(B | C)");
            sentence = new GenericComplexSentence("A & (B | C) <=> (C | R | T)");
            sentence = new GenericComplexSentence("!A <=> !B <=> C");

            LinkedHashSet<Sentence> sentences = new LinkedHashSet<>();
            sentences.add(new Literal("a"));
            sentences.add(new Literal("b"));
            sentences.add(new Literal("c"));
            sentences.add(new Literal("d"));
            sentence = new GenericComplexSentence(sentences, Connective.OR, false);

            sentence = new GenericComplexSentence("true & true");
            assertEquals(Literal.TRUE, sentence.getLeftSentence());
            assertEquals(Literal.TRUE, sentence.getRightSentence());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void getLiterals() {
        try {
            GenericComplexSentence sentence = new GenericComplexSentence("A => (B | !C) & !(D <=> (E | F))");
            LinkedHashSet<Literal> literals = new LinkedHashSet<>();
            literals.add(new Literal("A"));
            literals.add(new Literal("B"));
            literals.add(new Literal("C", true));
            literals.add(new Literal("D"));
            literals.add(new Literal("E"));
            literals.add(new Literal("F"));
            assertEquals(literals, sentence.getLiterals());

            sentence = new GenericComplexSentence(new CNFSentence("(A | B | C) & (D | E | F)"),
                    new Clause("(!A | G | F)"), Connective.OR, true);
            literals = new LinkedHashSet<>();
            literals.add(new Literal("A"));
            literals.add(new Literal("B"));
            literals.add(new Literal("C"));
            literals.add(new Literal("D"));
            literals.add(new Literal("E"));
            literals.add(new Literal("F"));
            literals.add(new Literal("A", true));
            literals.add(new Literal("G"));
            assertEquals(literals, sentence.getLiterals());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void sentenceTypeTest() {
        try {
            GenericComplexSentence complex = new GenericComplexSentence("(A | B | C) & (E | F | G) => G");
            assertSame(complex.type(), SentenceType.GENERIC_COMPLEX);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void satisfiabilityTypeTest() {
        try {
            GenericComplexSentence complex = new GenericComplexSentence("(A & !A) & (B & !B & C)");
            assertSame(complex.satisfiabilityType(), SatisfiabilityType.CONTRADICTION);

            complex = new GenericComplexSentence("A | !A");
            assertSame(complex.satisfiabilityType(), SatisfiabilityType.TAUTOLOGY);

            complex = new GenericComplexSentence("(a & b & c) & (d & e) & (f)");
            assertSame(complex.satisfiabilityType(), SatisfiabilityType.CONTINGENCY);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void truthTableTest() {
        try {
            GenericComplexSentence generic = new GenericComplexSentence("(A | !A) & (B | !B | C)");
            assertThrows(TautologyException.class, generic::truthTable);

            generic = new GenericComplexSentence("A & !A");
            assertThrows(ContradictionException.class, generic::truthTable);

            generic = new GenericComplexSentence("(a & b & c) & (d & e) & (f)");
            assertNotNull(generic.truthTable());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void toStringTest() {
        try {
            GenericComplexSentence sentence = new GenericComplexSentence("A => B <=> C & !(!C | !D)");
            assertEquals("(A => B) <=> (C & !(!C | !D))", sentence.toString());

            sentence = new GenericComplexSentence(new Clause("a | b"), new Clause("c | d"),
                    Connective.BICONDITIONAL);
            assertEquals("(a | b) <=> (c | d)", sentence.toString());

            sentence = new GenericComplexSentence(new Clause("(a | b)"),
                    new CNFSentence("(c | d |!e) & (f | g)"), Connective.IMPLICATION, true);
            assertEquals("!((a | b) => ((c | d | !e) & (f | g)))", sentence.toString());

            sentence = new GenericComplexSentence(new Literal("A", true),
                    new CNFSentence("a | b | c"), Connective.BICONDITIONAL);
            assertEquals("!A <=> (a | b | c)", sentence.toString());

            sentence = new GenericComplexSentence(new Literal("A", true),
                    new CNFSentence("(a | b | c) & (D | E)"), Connective.BICONDITIONAL);
            assertEquals("!A <=> ((a | b | c) & (D | E))", sentence.toString());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void equalsTest() {
        try {
            GenericComplexSentence sentence1 = new GenericComplexSentence("a & b & c & d & e & f");
            GenericComplexSentence sentence2 = new GenericComplexSentence("b & d & a & c & f & e");
            assertEquals(sentence1, sentence2);

            sentence1 = new GenericComplexSentence("a | !a");
            sentence2 = new GenericComplexSentence("b & !b");
            assertNotEquals(sentence1, sentence2);

            sentence1 = new GenericComplexSentence("a | !a");
            sentence2 = new GenericComplexSentence("b | !b");
            assertEquals(sentence1, sentence2);

            sentence1 = new GenericComplexSentence("a & !a");
            sentence2 = new GenericComplexSentence("b & !b");
            assertEquals(sentence1, sentence2);

            sentence1 = new GenericComplexSentence("a | !a");
            sentence2 = new GenericComplexSentence("b & c => d");
            assertNotEquals(sentence1, sentence2);
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void hashCodeTest() {
        try {
            GenericComplexSentence sentence1 = new GenericComplexSentence("a & b & c & d & e & f");
            GenericComplexSentence sentence2 = new GenericComplexSentence("b & d & a & c & f & e");
            assertEquals(sentence1.hashCode(), sentence2.hashCode());

            sentence1 = new GenericComplexSentence("a | !a");
            sentence2 = new GenericComplexSentence("b & !b");
            assertNotEquals(sentence1.hashCode(), sentence2.hashCode());

            sentence1 = new GenericComplexSentence("a | !a");
            sentence2 = new GenericComplexSentence("b | !b");
            assertEquals(sentence1.hashCode(), sentence2.hashCode());

            sentence1 = new GenericComplexSentence("a & !a");
            sentence2 = new GenericComplexSentence("b & !b");
            assertEquals(sentence1.hashCode(), sentence2.hashCode());

            sentence1 = new GenericComplexSentence("a | !a");
            sentence2 = new GenericComplexSentence("b & c => d");
            assertNotEquals(sentence1.hashCode(), sentence2.hashCode());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    @Test
    public void minimalCNFTest() {
        try {
            GenericComplexSentence sentence = new GenericComplexSentence("(A & B) | ((C | FALSE) & D)");
            CNFSentence cnfSentence = sentence.minimalCNF();
            assertEquals(4, cnfSentence.size());

            LinkedHashSet<Clause> clauseSet = new LinkedHashSet<>();
            clauseSet.add(new Clause("C | A"));
            clauseSet.add(new Clause("D | A"));
            clauseSet.add(new Clause("C | B"));
            clauseSet.add(new Clause("D | B"));
            assertEquals(clauseSet, cnfSentence.getClauses());

            sentence = new GenericComplexSentence("(A & B) | ((FALSE) & D)");
            cnfSentence = sentence.minimalCNF();
            assertEquals(2, cnfSentence.size());

            clauseSet = new LinkedHashSet<>();
            clauseSet.add(new Clause("A"));
            clauseSet.add(new Clause("B"));
            assertEquals(clauseSet, cnfSentence.getClauses());

            sentence = new GenericComplexSentence("(A & B) | (TRUE & ((FALSE) & (D | TRUE)))");
            cnfSentence = sentence.minimalCNF();
            assertEquals(2, cnfSentence.size());

            clauseSet = new LinkedHashSet<>();
            clauseSet.add(new Clause("A"));
            clauseSet.add(new Clause("B"));
            assertEquals(clauseSet, cnfSentence.getClauses());
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }


        tautology("(A | B | !A | C | D | !C)");
        tautology("A | B | !A | C");


        contradiction("(!A) & (!C) & (A | C)");
        contradiction("(!A) & (A) & (A | B) & (B) & (A | B | C) & (A | B | C | D)");


        compareMyGenericWithTheirs("A | B | C", "A ∨ B ∨ C");

        compareMyGenericWithTheirs("!((!A | B | C) & (!A | B | D | E) & (!B | A) & (!C | !D | A) & (!C | !E | A))",
                "(A ∨ B ∨ C) ∧ (¬D ∨ A ∨ B ∨ C) ∧ (¬E ∨ A ∨ B ∨ C) ∧ (A ∨ B ∨ D ∨ C) ∧ " +
                        "(¬E ∨ A ∨ B ∨ D ∨ C) ∧ (A ∨ B ∨ D ∨ E) ∧ (A ∨ ¬C ∨ B ∨ D ∨ E) ∧ (¬E ∨ ¬C ∨ B ∨ D ∨ ¬A) ∧ (¬D ∨ ¬C ∨ B ∨ ¬A) ∧ " +
                        "(¬E ∨ ¬C ∨ B ∨ ¬A) ∧ (¬B ∨ ¬A) ∧ (¬D ∨ ¬B ∨ ¬A) ∧ (¬E ∨ ¬B ∨ ¬A) ∧ (¬B ∨ ¬C ∨ ¬A) ∧ (¬D ∨ ¬C ∨ ¬A) ∧ (¬E ∨ ¬C ∨ ¬A)");

        compareMyGenericWithTheirs("(B | C) & (!B | D | E)", "(B ∨ C) ∧ (¬B ∨ D ∨ E)");

        compareMyGenericWithTheirs("(B | C) & (A | D | E)", "(B ∨ C) ∧ (A ∨ D ∨ E)");

        compareMyGenericWithTheirs("!(B | C & (D | E)) | A", "(¬B ∨ A) ∧ (¬D ∨ ¬C ∨ A) ∧ (¬E ∨ ¬C ∨ A)");

        compareMyGenericWithTheirs("(A <=> !!B | C & (!!!!D | !(!(E))))", "(¬B ∨ A) ∧ (¬D ∨ ¬C ∨ A) ∧ (¬E ∨ ¬C ∨ A) ∧ (C ∨ B ∨ ¬A) ∧ (D ∨ E ∨ B ∨ ¬A)");

        compareMyGenericWithTheirs("(B | C) & (!B | D | E) & (B)", "(D ∨ E) ∧ B");

        compareMyGenericWithTheirs("!((B | C) & (!B | D | E) & (B))", "(¬D ∨ ¬B) ∧ (¬E ∨ ¬B) ∧ (¬D ∨ ¬C ∨ ¬B) ∧ (¬E ∨ ¬C ∨ ¬B)");

        compareMyGenericWithTheirs("(!A & !B & C) | (!C & B) | (A)", "(¬C ∨ ¬B ∨ A) ∧ (B ∨ C ∨ A)");

        compareMyGenericWithTheirs("(!A & !B & C) | (!C & B)", "(¬C ∨ ¬A) ∧ (B ∨ ¬A) ∧ (¬C ∨ ¬B) ∧ (B ∨ C)");

        compareMyGenericWithTheirs("!((B | C) & (D | E) & (B))", "(¬D ∨ ¬B) ∧ (¬E ∨ ¬B) ∧ (¬D ∨ ¬C ∨ ¬B) ∧ (¬E ∨ ¬C ∨ ¬B)");

        compareMyGenericWithTheirs("(B | !C) & (C | !B) & !A", "(B ∨ ¬C) ∧ (C ∨ ¬B) ∧ ¬A");

        compareMyGenericWithTheirs("(B | C) & (B | C | D | A | !A) & (!B)", "C ∧ ¬B");

        compareMyGenericWithTheirs("(!A) & (C) & (A | !C | D)", "¬A ∧ C ∧ D");

        compareMyGenericWithTheirs("(!A) & (!C) & (A | C | D)", "¬A ∧ ¬C ∧ D");

        compareMyGenericWithTheirs("(!A) & (A | B) & (B) & (A | B | C) & (A | B | C | D)", "¬A ∧ B");

        compareMyGenericWithTheirs("A & (b | !b)", "A");

        compareMyGenericWithTheirs("((B & C) => (A | T | G)) & ((G | T | A) => (C & B))",
                "(¬B ∨ ¬C ∨ A ∨ T ∨ G) ∧ (C ∨ ¬G) ∧ (B ∨ ¬G) ∧ (C ∨ ¬T) ∧ (B ∨ ¬T) ∧ (C ∨ ¬A) ∧ (B ∨ ¬A)");

        compareMyGenericWithTheirs("(A&B&C) | (A&!B&C) | (!A&!B&C)", "(A ∨ ¬B) ∧ C");

        compareMyGenericWithTheirs("!(!(!(B))) | D | !!E | !!!!A | C & !!X | Y | !!!Z",
                "(C ∨ ¬B ∨ D ∨ E ∨ A ∨ Y ∨ ¬Z) ∧ (X ∨ ¬B ∨ D ∨ E ∨ A ∨ Y ∨ ¬Z)");

        compareMyGenericWithTheirs("!!(!!!A | !!!!B | !C)", "¬A ∨ B ∨ ¬C");

        compareMyGenericWithTheirs("(!!!!(!!!(!(!(A))) & B => C <=> D))", "(¬D ∨ A ∨ ¬B ∨ C) ∧ (¬A ∨ D) ∧ (B ∨ D) ∧ (¬C ∨ D)");

        compareMyGenericWithTheirs("!(!(!(A & B)))", "¬A ∨ ¬B");

        compareMyGenericWithTheirs("(A | !(!(!(B))) | !C | (D | E)) | (H | G)", "A ∨ ¬B ∨ ¬C ∨ D ∨ E ∨ H ∨ G");

        compareMyGenericWithTheirs("!B | A | D | E | !C | H | !(!(!(!G)))", "¬B ∨ A ∨ D ∨ E ∨ ¬C ∨ H ∨ G");

        compareMyGenericWithTheirs("!B | A | D | E | !C | H | !(!(!(!G))) | G", "¬B ∨ A ∨ D ∨ E ∨ ¬C ∨ H ∨ G");

        compareMyGenericWithTheirs("!((A | B | C) & (D | E | G) & (H | P | Q))",
                "(¬D ∨ ¬A ∨ ¬H) ∧ (¬E ∨ ¬A ∨ ¬H) ∧ (¬G ∨ ¬A ∨ ¬H) ∧ (¬D ∨ ¬B ∨ ¬H) ∧ (¬E ∨ ¬B ∨ ¬H) ∧ " +
                        "(¬G ∨ ¬B ∨ ¬H) ∧ (¬D ∨ ¬C ∨ ¬H) ∧ (¬E ∨ ¬C ∨ ¬H) ∧ (¬G ∨ ¬C ∨ ¬H) ∧ (¬D ∨ ¬A ∨ ¬P) ∧ " +
                        "(¬E ∨ ¬A ∨ ¬P) ∧ (¬G ∨ ¬A ∨ ¬P) ∧ (¬D ∨ ¬B ∨ ¬P) ∧ (¬E ∨ ¬B ∨ ¬P) ∧ (¬G ∨ ¬B ∨ ¬P) ∧ " +
                        "(¬D ∨ ¬C ∨ ¬P) ∧ (¬E ∨ ¬C ∨ ¬P) ∧ (¬G ∨ ¬C ∨ ¬P) ∧ (¬D ∨ ¬A ∨ ¬Q) ∧ (¬E ∨ ¬A ∨ ¬Q) ∧ " +
                        "(¬G ∨ ¬A ∨ ¬Q) ∧ (¬D ∨ ¬B ∨ ¬Q) ∧ (¬E ∨ ¬B ∨ ¬Q) ∧ (¬G ∨ ¬B ∨ ¬Q) ∧ (¬D ∨ ¬C ∨ ¬Q) ∧ " +
                        "(¬E ∨ ¬C ∨ ¬Q) ∧ (¬G ∨ ¬C ∨ ¬Q)");

        compareMyGenericWithTheirs("(A) => (B | C & (D | E))", "(C ∨ B ∨ ¬A) ∧ (D ∨ E ∨ B ∨ ¬A)");

        compareMyGenericWithTheirs("!(B | C & (D | E))", "¬B ∧ (¬D ∨ ¬C) ∧ (¬E ∨ ¬C)");

        compareMyGenericWithTheirs("!B | A | D | E | C & (X | Y | !Z)", "(C ∨ ¬B ∨ A ∨ D ∨ E) ∧ (X ∨ Y ∨ ¬Z ∨ ¬B ∨ A ∨ D ∨ E)");

        compareMyGenericWithTheirs("A & B => !A <=> C | (E | !B) & (H)",
                "(¬C ∨ ¬A ∨ ¬B) ∧ (¬E ∨ ¬H ∨ ¬A ∨ ¬B) ∧ (E ∨ ¬B ∨ C ∨ A) ∧ (H ∨ C ∨ A) ∧ (H ∨ C ∨ B)");

        compareMyGenericWithTheirs("!((!A | B | C) & (!A | B | D | E) & (!B | A))",
                "(A ∨ B) ∧ (¬D ∨ A ∨ B) ∧ (¬E ∨ A ∨ B) ∧ (A ∨ ¬C ∨ B) ∧ (¬D ∨ ¬C ∨ B) ∧ (¬E ∨ ¬C ∨ B) ∧ " +
                        "(¬B ∨ ¬A) ∧ (¬D ∨ ¬B ∨ ¬A) ∧ (¬E ∨ ¬B ∨ ¬A) ∧ (¬B ∨ ¬C ∨ ¬A) ∧ (¬D ∨ ¬C ∨ ¬A) ∧ (¬E ∨ ¬C ∨ ¬A)");

        compareMyGenericWithTheirs("(A & !B & !C) | (A & !B & !D & !E) | (B & !A) | (C & D & !A) | (C & E & !A)",
                "(A ∨ B ∨ C) ∧ (¬D ∨ A ∨ B ∨ C) ∧ (¬E ∨ A ∨ B ∨ C) ∧ (A ∨ B ∨ D ∨ C) ∧ (¬E ∨ A ∨ B ∨ D ∨ C) ∧ " +
                        "(A ∨ B ∨ D ∨ E) ∧ (A ∨ ¬C ∨ B ∨ D ∨ E) ∧ (¬E ∨ ¬C ∨ B ∨ D ∨ ¬A) ∧ (¬D ∨ ¬C ∨ B ∨ ¬A) ∧ " +
                        "(¬E ∨ ¬C ∨ B ∨ ¬A) ∧ (¬B ∨ ¬A) ∧ (¬D ∨ ¬B ∨ ¬A) ∧ (¬E ∨ ¬B ∨ ¬A) ∧ (¬B ∨ ¬C ∨ ¬A) ∧ (¬D ∨ ¬C ∨ ¬A) ∧ " +
                        "(¬E ∨ ¬C ∨ ¬A)");

        compareMyGenericWithTheirs("((!A | B | C) & (!A | B | D | E) & (!B | A))",
                "(¬A ∨ B ∨ C) ∧ (¬A ∨ B ∨ D ∨ E) ∧ (¬B ∨ A)");
    }

    private void compareMyGenericWithTheirs(String strMy, String strOther) {
        try {
            CNFSentence myCNF = new GenericComplexSentence(strMy).minimalCNF();
            LinkedHashSet<Clause> myClauses = myCNF.getClauses();
            CNFSentence theirCNF = new CNFSentence(convertOnlineCalculatorString(strOther)).minimalCNF();
            LinkedHashSet<Clause> theirClauses = theirCNF.getClauses();
            assertEquals(myClauses, theirClauses);
        } catch (Exception ex) {
            fail("shouldn't have thrown an exception");
        }
    }

    private void tautology(String str) {
        try {
            CNFSentence cnfSentence = new GenericComplexSentence(str).minimalCNF();
            fail("not a tautology");
        } catch (Exception e) {
            assertTrue(e instanceof TautologyException);
        }
    }

    private void contradiction(String str) {
        try {
            CNFSentence cnfSentence = new GenericComplexSentence(str).minimalCNF();
            fail("not a contradiction");
        } catch (Exception e) {
            assertTrue(e instanceof ContradictionException);
        }
    }

}