package org.example;

import org.example.algo.Resolution;
import org.example.domain.Sentences;
import org.example.domain.sentence.*;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;
import org.example.util.SentenceUtils;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author aram.azatyan | 2/2/2024 11:58 AM
 */
public class Main {
    public static void main(String[] args) throws ParseException, TautologyException, ContradictionException {
        Sentence s1 = new GenericComplexSentence("!(a => ((b <=> c) | (e & d & !f)))");
        Sentence s2 = new GenericComplexSentence("!(!a | (((b => c) & (c => b)) | (!(!e | !d | f))))");
        System.out.println(s1.minimalCNF().equals(s2.minimalCNF()));

        GenericComplexSentence generic = new GenericComplexSentence("!true => !(!!false)");
        System.out.println(generic);

        System.out.println(Sentence.optimizedParse("(!(a => ((b <=> c) & (e & f & !f)))) | (!(!a | (((b => c) & (c => b)) | (!(!e | !d | f)))))"));
        System.out.println(Sentence.optimizedParse("(!(!a => ((b <=> c) & (e & f & !f))))"));
        System.out.println(Sentence.optimizedParse("(a => ((a | b) & (a | c) & (a | d)))"));
        System.out.println(Sentence.optimizedParse("c => ((a | b) & (a | b | c))"));
        System.out.println(Sentence.optimizedParse("((b) & (a & b => l) & (a & p => l) & (b & l => m) & (l & m => p)" +
                " & (p => q) & (a)) => q"));
        Sentence sentence = Sentence.optimizedParse("(b) & (a & b => l) & (a & p => l) & (b & l => m) & (l & m => p) & (p => q) & (a)");
        System.out.println(sentence);
        System.out.println(Sentence.optimizedParse(String.join("", "(", sentence.toString(), ")", "=>!q")));
        System.out.println(Sentence.optimizedParse(SentenceUtils.convertOnlineCalculatorString("((¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ E) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ ¬C ∨ D ∨ E) ∧ (A ∨ B ∨ C ∨ ¬D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ ¬D ∨ E) ∧ (A ∨ B ∨ C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ D ∨ E))") + "=>" +
                "((A | B | C) & (A | B | D | E) & (!B | !A) & (!C | !D | !A) & (!C | !E | !A))"));
        System.out.println(Sentence.optimizedParse("(" + sentence.toString() + ")=>(" + sentence.minimalCNF() + ")"));

        String str1 = SentenceUtils.convertOnlineCalculatorString("(¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ E) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ ¬C ∨ D ∨ E) ∧ (A ∨ B ∨ C ∨ ¬D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ ¬D ∨ E) ∧ (A ∨ B ∨ C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ D ∨ E)");
        String str2 = "(A | B | C) & (A | B | D | E) & (!B | !A) & (!C | !D | !A) & (!C | !E | !A)";
        System.out.println(Sentence.optimizedParse(str1));
        System.out.println(Sentence.optimizedParse(str2));

        System.out.println(Sentence.isEquivalent(new CNFSentence(str1), new CNFSentence(str2)));

        String andur = "((!A | (!B | (!C | (!D | !E)))) & ((!A | (!B | (!C | (!D | E)))) & ((!A | (!B | (!C | (D | !E)))) & ((!A | (!B | (!C | (D | E)))) & ((!A | (!B | (C | (!D | !E)))) & ((!A | (!B | (C | (!D | E)))) & ((!A | (!B | (C | (D | !E)))) & ((!A | (!B | (C | (D | E)))) & ((!A | (B | (!C | (!D | !E)))) & ((!A | (B | (!C | (!D | E)))) & ((!A | (B | (!C | (D | !E)))) & ((A | (B | (!C | (D | E)))) & ((A | (B | (C | (!D | !E)))) & ((A | (B | (C | (!D | E)))) & ((A | (B | (C | (D | !E)))) & (A | (B | (C | (D | E)))))))))))))))))))";
        System.out.println(new CNFSentence(andur));

        System.out.println(new GenericComplexSentence(str1).satisfiabilityType());
        System.out.println(new GenericComplexSentence(str2).satisfiabilityType());

        System.out.println(Sentences.optimizeCanonicalCNF(new CNFSentence(str1)));

        try {
            System.out.println(new GenericComplexSentence("(" + str1 + ")=>(" + str2 + ")").minimalCNF());
        } catch (TautologyException e) {
            System.out.println("TRUE");
        } catch (ContradictionException e) {
            System.out.println("FALSE");
        }

        System.out.println(Sentence.optimizedParse("(" + str1 + ")=>(" + str2 + ")"));

        Set<CNFSentence> set = new HashSet<>();
        set.add(new GenericComplexSentence("((B & C) => (A | T | G)) & ((G | T | A) => (C & B))").minimalCNF());
        System.out.println(set.iterator().next().size());
        System.out.println(new Resolution(set, false).resolveAndGet().size());

        System.out.println(new GenericComplexSentence(str1).minimalCNF());

        System.out.println(new GenericComplexSentence(str1).equals(new GenericComplexSentence(str2)));
    }
}

