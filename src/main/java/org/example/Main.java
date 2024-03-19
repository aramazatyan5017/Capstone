package org.example;

import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.GenericComplexSentence;
import org.example.exception.TautologyException;
import org.example.exception.UnsatisfiableException;
import org.example.truth_table.TruthTable;
import org.example.util.SentenceUtils;

import java.text.ParseException;

/**
 * @author aram.azatyan | 2/2/2024 11:58 AM
 */
public class Main {
    public static void main(String[] args) throws ParseException, TautologyException, UnsatisfiableException {
        CNFSentence cnf1 = new GenericComplexSentence("(B & C) <=> (A | T | G)").convertToCNF();
        CNFSentence cnf2 = new GenericComplexSentence("!(!(A | T | G) <=> (B & C))").convertToCNF();
        CNFSentence cnf3 = new GenericComplexSentence("((B & C) => (A | T | G)) & ((G | T | A) => (C & B))").convertToCNF();
//        CNFSentence cnf4 = new CNFSentence("(C | !C)").convertToCNF();
        System.out.println(cnf1.equals(cnf2));
        System.out.println(cnf2.equals(cnf3));
        System.out.println(cnf1.equals(cnf3));


        CNFSentence cnf5 = new GenericComplexSentence(SentenceUtils.convertOnlineCalculatorString("(¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ E) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (A ∨ B ∨ ¬C ∨ ¬D ∨ E) ∧ (A ∨ B ∨ ¬C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ ¬C ∨ D ∨ E) ∧ (A ∨ B ∨ C ∨ ¬D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ ¬D ∨ E) ∧ (A ∨ B ∨ C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ D ∨ E)")).convertToCNF();
        CNFSentence cnf6 = new GenericComplexSentence("!((!A | B | C) & (!A | B | D | E) & (!B | A) & (!C | !D | A) & (!C | !E | A))").convertToCNF();
        System.out.println(cnf6);

        System.out.println(cnf6.equals(cnf5));


        new TruthTable(cnf5).print();
        new TruthTable(cnf6).print();


//        System.out.println(cnf5);
//        System.out.println(cnf6);

        System.out.println(new String(
                "1 1 1 1 1  │  0\n" +
                        "1 1 1 1 0  │  0\n" +
                        "1 1 1 0 1  │  0\n" +
                        "1 1 1 0 0  │  0\n" +
                        "1 1 0 1 1  │  0\n" +
                        "1 1 0 1 0  │  0\n" +
                        "1 1 0 0 1  │  0\n" +
                        "1 1 0 0 0  │  0\n" +
                        "1 0 1 1 1  │  0\n" +
                        "1 0 1 1 0  │  0\n" +
                        "1 0 1 0 1  │  0\n" +
                        "1 0 1 0 0  │  1\n" +
                        "1 0 0 1 1  │  1\n" +
                        "1 0 0 1 0  │  1\n" +
                        "1 0 0 0 1  │  1\n" +
                        "1 0 0 0 0  │  1\n" +
                        "0 1 1 1 1  │  1\n" +
                        "0 1 1 1 0  │  1\n" +
                        "0 1 1 0 1  │  1\n" +
                        "0 1 1 0 0  │  1\n" +
                        "0 1 0 1 1  │  1\n" +
                        "0 1 0 1 0  │  1\n" +
                        "0 1 0 0 1  │  1\n" +
                        "0 1 0 0 0  │  1\n" +
                        "0 0 1 1 1  │  0\n" +
                        "0 0 1 1 0  │  0\n" +
                        "0 0 1 0 1  │  0\n" +
                        "0 0 1 0 0  │  0\n" +
                        "0 0 0 1 1  │  0\n" +
                        "0 0 0 1 0  │  0\n" +
                        "0 0 0 0 1  │  0\n" +
                        "0 0 0 0 0  │  0\n")
                .equals("1 1 1 1 1  │  0\n" +
                        "1 1 1 1 0  │  0\n" +
                        "1 1 1 0 1  │  0\n" +
                        "1 1 1 0 0  │  0\n" +
                        "1 1 0 1 1  │  0\n" +
                        "1 1 0 1 0  │  0\n" +
                        "1 1 0 0 1  │  0\n" +
                        "1 1 0 0 0  │  0\n" +
                        "1 0 1 1 1  │  0\n" +
                        "1 0 1 1 0  │  0\n" +
                        "1 0 1 0 1  │  0\n" +
                        "1 0 1 0 0  │  1\n" +
                        "1 0 0 1 1  │  1\n" +
                        "1 0 0 1 0  │  1\n" +
                        "1 0 0 0 1  │  1\n" +
                        "1 0 0 0 0  │  1\n" +
                        "0 1 1 1 1  │  1\n" +
                        "0 1 1 1 0  │  1\n" +
                        "0 1 1 0 1  │  1\n" +
                        "0 1 1 0 0  │  1\n" +
                        "0 1 0 1 1  │  1\n" +
                        "0 1 0 1 0  │  1\n" +
                        "0 1 0 0 1  │  1\n" +
                        "0 1 0 0 0  │  1\n" +
                        "0 0 1 1 1  │  1\n" +
                        "0 0 1 1 0  │  1\n" +
                        "0 0 1 0 1  │  1\n" +
                        "0 0 1 0 0  │  0\n" +
                        "0 0 0 1 1  │  0\n" +
                        "0 0 0 1 0  │  0\n" +
                        "0 0 0 0 1  │  0\n" +
                        "0 0 0 0 0  │  0\n"));

        new TruthTable(cnf6).print();

        // TODO: 3/15/2024 stex sxal a ashxatum
        CNFSentence cnf7 = new CNFSentence(SentenceUtils.convertOnlineCalculatorString("(¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ E) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ ¬C ∨ D ∨ E) ∧ (A ∨ B ∨ C ∨ ¬D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ ¬D ∨ E) ∧ (A ∨ B ∨ C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ D ∨ E)"));
        new TruthTable(cnf7).print();
        new TruthTable(cnf6).print();
        System.out.println(cnf7.equals(cnf6));
        System.out.println(cnf6.convertToCNF());
    }
}

