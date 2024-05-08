package org.example;

import org.example.algo.PropositionalResolution;
import org.example.domain.Sentences;
import org.example.domain.sentence.propositional.GenericComplexPropositionalSentence;
import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.sentence.propositional.PropositionalSentence;
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
        PropositionalSentence s1 = new GenericComplexPropositionalSentence("!(a => ((b <=> c) | (e & d & !f)))");
        PropositionalSentence s2 = new GenericComplexPropositionalSentence("!(!a | (((b => c) & (c => b)) | (!(!e | !d | f))))");
        System.out.println(s1.minimalCNF().equals(s2.minimalCNF()));

        GenericComplexPropositionalSentence generic = new GenericComplexPropositionalSentence("!true => !(!!false)");
        System.out.println(generic);

        System.out.println(PropositionalSentence.optimizedParse("(!(a => ((b <=> c) & (e & f & !f)))) | (!(!a | (((b => c) & (c => b)) | (!(!e | !d | f)))))"));
        System.out.println(PropositionalSentence.optimizedParse("(!(!a => ((b <=> c) & (e & f & !f))))"));
        System.out.println(PropositionalSentence.optimizedParse("(a => ((a | b) & (a | c) & (a | d)))"));
        System.out.println(PropositionalSentence.optimizedParse("c => ((a | b) & (a | b | c))"));
        System.out.println(PropositionalSentence.optimizedParse("((b) & (a & b => l) & (a & p => l) & (b & l => m) & (l & m => p)" +
                " & (p => q) & (a)) => q"));
        PropositionalSentence sentence = PropositionalSentence.optimizedParse("(b) & (a & b => l) & (a & p => l) & (b & l => m) & (l & m => p) & (p => q) & (a)");
        System.out.println(sentence);
        System.out.println(PropositionalSentence.optimizedParse(String.join("", "(", sentence.toString(), ")", "=>!q")));
        System.out.println(PropositionalSentence.optimizedParse(SentenceUtils.convertOnlineCalculatorString("((¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ E) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ ¬C ∨ D ∨ E) ∧ (A ∨ B ∨ C ∨ ¬D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ ¬D ∨ E) ∧ (A ∨ B ∨ C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ D ∨ E))") + "=>" +
                "((A | B | C) & (A | B | D | E) & (!B | !A) & (!C | !D | !A) & (!C | !E | !A))"));
        System.out.println(PropositionalSentence.optimizedParse("(" + sentence.toString() + ")=>(" + sentence.minimalCNF() + ")"));

        String str1 = SentenceUtils.convertOnlineCalculatorString("(¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ E) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ ¬C ∨ D ∨ E) ∧ (A ∨ B ∨ C ∨ ¬D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ ¬D ∨ E) ∧ (A ∨ B ∨ C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ D ∨ E)");
        String str2 = "(A | B | C) & (A | B | D | E) & (!B | !A) & (!C | !D | !A) & (!C | !E | !A)";
        System.out.println(PropositionalSentence.optimizedParse(str1));
        System.out.println(PropositionalSentence.optimizedParse(str2));

        System.out.println(PropositionalSentence.isEquivalent(new PropositionalCNFSentence(str1), new PropositionalCNFSentence(str2)));

        String andur = "((!A | (!B | (!C | (!D | !E)))) & ((!A | (!B | (!C | (!D | E)))) & ((!A | (!B | (!C | (D | !E)))) & ((!A | (!B | (!C | (D | E)))) & ((!A | (!B | (C | (!D | !E)))) & ((!A | (!B | (C | (!D | E)))) & ((!A | (!B | (C | (D | !E)))) & ((!A | (!B | (C | (D | E)))) & ((!A | (B | (!C | (!D | !E)))) & ((!A | (B | (!C | (!D | E)))) & ((!A | (B | (!C | (D | !E)))) & ((A | (B | (!C | (D | E)))) & ((A | (B | (C | (!D | !E)))) & ((A | (B | (C | (!D | E)))) & ((A | (B | (C | (D | !E)))) & (A | (B | (C | (D | E)))))))))))))))))))";
        System.out.println(new PropositionalCNFSentence(andur));

        System.out.println(new GenericComplexPropositionalSentence(str1).satisfiabilityType());
        System.out.println(new GenericComplexPropositionalSentence(str2).satisfiabilityType());

        System.out.println(Sentences.optimizeCanonicalCNF(new PropositionalCNFSentence(str1)));

        try {
            System.out.println(new GenericComplexPropositionalSentence("(" + str1 + ")=>(" + str2 + ")").minimalCNF());
        } catch (TautologyException e) {
            System.out.println("TRUE");
        } catch (ContradictionException e) {
            System.out.println("FALSE");
        }

        System.out.println(PropositionalSentence.optimizedParse("(" + str1 + ")=>(" + str2 + ")"));

        Set<PropositionalCNFSentence> set = new HashSet<>();
        set.add(new GenericComplexPropositionalSentence("((B & C) => (A | T | G)) & ((G | T | A) => (C & B))").minimalCNF());
        System.out.println(set.iterator().next().size());
        System.out.println(new PropositionalResolution(set, false).resolveAndGet().size());

        System.out.println(new GenericComplexPropositionalSentence(str1).minimalCNF());

        System.out.println(new GenericComplexPropositionalSentence(str1).equals(new GenericComplexPropositionalSentence(str2)));
    }
}

