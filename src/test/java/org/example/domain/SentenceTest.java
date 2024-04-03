package org.example.domain;

import org.example.domain.sentence.*;

import static org.example.domain.SentenceType.*;

import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;
import org.junit.jupiter.api.Test;
import static org.example.domain.sentence.Sentence.optimizedParse;
import static org.example.util.SentenceUtils.convertOnlineCalculatorString;
import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.util.LinkedHashSet;

/**
 * @author aram.azatyan | 3/29/2024 4:31 PM
 */
public class SentenceTest {

    @Test
    void isEquivalentTest() {

    }

    @Test
    void parseAbnormal() {
        assertThrows(IllegalArgumentException.class, () -> optimizedParse(""));
        assertThrows(IllegalArgumentException.class, () -> optimizedParse(null));
        assertThrows(IllegalArgumentException.class, () -> optimizedParse("    "));

        assertThrows(ParseException.class, () -> optimizedParse("!"));
        assertThrows(ParseException.class, () -> optimizedParse("=>aa"));
        assertThrows(ParseException.class, () -> optimizedParse("<=>"));
        assertThrows(ParseException.class, () -> optimizedParse("|"));
        assertThrows(ParseException.class, () -> optimizedParse("&&|"));
        assertThrows(ParseException.class, () -> optimizedParse("!&"));
        assertThrows(ParseException.class, () -> optimizedParse("(a | b) &&& (b | d)"));
        assertThrows(ParseException.class, () -> optimizedParse("!(!(!A!))"));
        assertThrows(ParseException.class, () -> optimizedParse("A!B"));
        assertThrows(ParseException.class, () -> optimizedParse("(A & B)("));
        assertThrows(ParseException.class, () -> optimizedParse("A! & =C> | D"));
    }

    @Test
    void parseTest() {
        try {
            Sentence sentence = optimizedParse("A");
            assertEquals(LITERAL, sentence.type());
            assertEquals(new Literal("A"), sentence);

            sentence = optimizedParse("!A");
            assertEquals(LITERAL, sentence.type());
            assertEquals(new Literal("!A"), sentence);

            sentence = optimizedParse("!(!(!A))");
            assertEquals(LITERAL, sentence.type());
            assertEquals(new Literal("!(!(!A))"), sentence);

            sentence = optimizedParse("(a | b)");
            assertEquals(CLAUSE, sentence.type());
            assertEquals(new Clause("(a | b)"), sentence);

            sentence = optimizedParse("a | b");
            assertEquals(CLAUSE, sentence.type());
            assertEquals(new Clause("a | b"), sentence);

            sentence = optimizedParse("(!(!(!a)) | !!!b | g)");
            assertEquals(CLAUSE, sentence.type());
            assertEquals(new Clause("(!(!(!a)) | !!!b | g)"), sentence);

            sentence = optimizedParse("!(!(!a)) | !!!b | g");
            assertEquals(CLAUSE, sentence.type());
            assertEquals(new Clause("!(!(!a)) | !!!b | g"), sentence);

            sentence = optimizedParse("a | b | c");
            assertEquals(CLAUSE, sentence.type());
            assertEquals(new Clause("a | b | c"), sentence);

            sentence = optimizedParse("A | B | C | A");
            assertEquals(CLAUSE, sentence.type());
            assertEquals(new Clause("A | B | C | A"), sentence);

            sentence = optimizedParse("a | !a | b");
            assertEquals(LITERAL, sentence.type());
            assertEquals(Literal.TRUE, sentence);

            sentence = optimizedParse("(a | b) & (c | d)");
            assertEquals(CNF, sentence.type());
            assertEquals(new CNFSentence("(a | b) & (c | d)"), sentence);

            sentence = optimizedParse("a & b & c & d");
            assertEquals(CNF, sentence.type());
            assertEquals(new CNFSentence("a & b & c & d"), sentence);

            sentence = optimizedParse("(a) & (b) & (c) & (d)");
            assertEquals(CNF, sentence.type());
            assertEquals(new CNFSentence("(a) & (b) & (c) & (d)"), sentence);

            sentence = optimizedParse("(a | b | c) & (D | !e | f)");
            assertEquals(CNF, sentence.type());
            assertEquals(new CNFSentence("(a | b | c) & (D | !e | f)"), sentence);

            sentence = optimizedParse("(a | B | c) & (g | !D | c) & (a | B | c)");
            assertEquals(CNF, sentence.type());
            assertEquals(new CNFSentence("(a | B | c) & (g | !D | c) & (a | B | c)"), sentence);

            sentence = optimizedParse("(a | b) & (b | c | a)");
            assertEquals(CLAUSE, sentence.type());
            assertEquals(new Clause("a | b"), sentence);

            sentence = optimizedParse("(a | !a | b) & (!c | e | f | c)");
            assertEquals(LITERAL, sentence.type());
            assertEquals(Literal.TRUE, sentence);

            sentence = optimizedParse("!a & (!b) & !(c) & (a | b | c)");
            assertEquals(LITERAL, sentence.type());
            assertEquals(Literal.FALSE, sentence);

            sentence = optimizedParse("(A | !A) & (B | !B | C)");
            assertEquals(LITERAL, sentence.type());
            assertEquals(Literal.TRUE, sentence);

            sentence = optimizedParse("A & !A");
            assertEquals(LITERAL, sentence.type());
            assertEquals(Literal.FALSE, sentence);

            sentence = optimizedParse("(a | b | c) & (d | e) & (f) & f");
            assertEquals(CNF, sentence.type());
            assertEquals(new CNFSentence("(a | b | c) & (d | e) & (f)"), sentence);

            sentence = optimizedParse("(a | b) & (!a | c | d) & (b)");
            assertEquals(CNF, sentence.type());
            assertEquals(new CNFSentence("(!a | c | d) & (b)"), sentence);

            sentence = optimizedParse("(a | b | c) & (!a | b | !c) & (a | !b | !c)");
            assertEquals(CNF, sentence.type());
            assertEquals(new CNFSentence("(a | b | c) & (!a | b | !c) & (a | !b | !c)"), sentence);

            sentence = optimizedParse("(a | b | c | d) & (!a | b | !c) & (a | !b | !c)");
            assertEquals(CNF, sentence.type());
            assertEquals(new CNFSentence("(a | b | c | d) & (!a | b | !c) & (a | !b | !c)"), sentence);

            sentence = optimizedParse("(a | b | c) & (!a | b | !c) & (a | !b)");
            assertEquals(CNF, sentence.type());
            assertEquals(new CNFSentence("(a | b | c) & (!a | b | !c) & (a | !b)"), sentence);

            sentence = optimizedParse("(a | b | c) => (e | f | g)");
            assertEquals(GENERIC_COMPLEX, sentence.type());
            assertSame(Connective.IMPLICATION, ((GenericComplexSentence) sentence).getConnective());
            assertEquals(CLAUSE, ((GenericComplexSentence) sentence).getLeftSentence().type());
            assertEquals(CLAUSE, ((GenericComplexSentence) sentence).getRightSentence().type());

            sentence = optimizedParse("((a) & (a | b | c)) => ((e | f | g) & (h <=> u))");
            assertEquals(GENERIC_COMPLEX, sentence.type());
            assertEquals(LITERAL, ((GenericComplexSentence) sentence).getLeftSentence().type());
            assertEquals(GENERIC_COMPLEX, ((GenericComplexSentence) sentence).getRightSentence().type());
            assertSame(Connective.IMPLICATION, ((GenericComplexSentence) sentence).getConnective());
            assertEquals(CLAUSE, ((GenericComplexSentence) ((GenericComplexSentence) sentence).getRightSentence()).getLeftSentence().type());
            assertEquals(GENERIC_COMPLEX, ((GenericComplexSentence) ((GenericComplexSentence) sentence).getRightSentence()).getRightSentence().type());
            assertSame(Connective.AND, ((GenericComplexSentence) ((GenericComplexSentence) sentence).getRightSentence()).getConnective());
            assertEquals(LITERAL, ((GenericComplexSentence) ((GenericComplexSentence) ((GenericComplexSentence) sentence).getRightSentence()).getRightSentence()).getLeftSentence().type());
            assertEquals(LITERAL, ((GenericComplexSentence) ((GenericComplexSentence) ((GenericComplexSentence) sentence).getRightSentence()).getRightSentence()).getRightSentence().type());
            assertSame(Connective.BICONDITIONAL, ((GenericComplexSentence) ((GenericComplexSentence) ((GenericComplexSentence) sentence).getRightSentence()).getRightSentence()).getConnective());

            sentence = optimizedParse("(A) & !(B | C) & D");
            assertEquals(GENERIC_COMPLEX, sentence.type());

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

            compareMyGenericWithTheirs("!B | A | D | E | C & (X | Y | !Z)", "(C ∨ ¬B ∨ A ∨ D ∨ E) ∧ (X ∨ Y ∨ ¬Z ∨ ¬B ∨ A ∨ D ∨ E)");
            compareMyGenericWithTheirs("!(!(!(B))) | D | !!E | !!!!A | C & !!X | Y | !!!Z", "(C ∨ ¬B ∨ D ∨ E ∨ A ∨ Y ∨ ¬Z) ∧ (X ∨ ¬B ∨ D ∨ E ∨ A ∨ Y ∨ ¬Z)");
            compareMyGenericWithTheirs("A & B | C & (D | E | G)", "(C ∨ A) ∧ (D ∨ E ∨ G ∨ A) ∧ (C ∨ B) ∧ (D ∨ E ∨ G ∨ B)");
            compareMyGenericWithTheirs("A & B | C & (D | E | false)", "(C ∨ A) ∧ (D ∨ E ∨ A) ∧ (C ∨ B) ∧ (D ∨ E ∨ B)");
        } catch (Exception e) {
            fail("shouldn't have thrown an exception");
        }
    }

    private void compareMyGenericWithTheirs(String strMy, String strOther) {
        try {
            CNFSentence myCNF = Sentence.optimizedParse(strMy).minimalCNF();
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
            CNFSentence cnfSentence = Sentence.optimizedParse(str).minimalCNF();
            fail("not VERUM");
        } catch (Exception e) {
            assertTrue(e instanceof TautologyException);
        }
    }

    private void contradiction(String str) {
        try {
            CNFSentence cnfSentence = Sentence.optimizedParse(str).minimalCNF();
            fail("not FALSUM");
        } catch (Exception e) {
            assertTrue(e instanceof ContradictionException);
        }
    }
}
