package org.example.algo;

import org.example.cnf_util.CNFRules;
import org.example.domain.Connective;
import org.example.domain.SatisfiabilityType;
import org.example.domain.sentence.*;
import org.example.domain.Premise;
import org.example.domain.supplementary.PremiseAndConclusion;
import org.example.exception.TautologyException;
import org.example.exception.ContradictionException;
import org.example.inference_rules.AndEliminationInferenceRule;
import java.util.*;

/**
 * @author aram.azatyan | 3/20/2024 12:39 PM
 */
//-- if the value of a key in knowledgeBase map is null, means separate known sentences are given
//-- e.g. Set<Sentence> == A & B & C and Sentence == null, add A, B and C to the knowledge base
public class ForwardChaining {

    private final Set<Sentence> known;
    private final Map<Premise, Sentence> implications;

    public ForwardChaining(Map<Premise, Sentence> knowledgeBase) {
        if (knowledgeBase == null || knowledgeBase.isEmpty()) throw
                new IllegalArgumentException("null param");
        knowledgeBase.remove(null);
        if (knowledgeBase.isEmpty()) throw new IllegalArgumentException("null param");

        preprocessKnowledgeBase(knowledgeBase);

        known = new HashSet<>();
        implications = new HashMap<>();

        knowledgeBase.forEach((k, v) -> {
            if (v == null) known.addAll(k.getPremiseSentences());
            else implications.put(k, v);
        });

        if (known.size() == 0 || implications.size() == 0) throw
                new IllegalArgumentException("unable to perform Forward Checking");
    }

    public boolean isEntail(Sentence toBeEntailed) throws ContradictionException, TautologyException {
        if (toBeEntailed == null) throw new IllegalArgumentException("null param");
        toBeEntailed.minimalCNF();

        Map<PremiseAndConclusion, Integer> countMap = constructCount();
        Map<Sentence, Boolean> inferredMap = constructInferred();

        Queue<Sentence> queue = new LinkedList<>();
        known.forEach(queue::offer);
        while (!queue.isEmpty()) {
            Sentence sentence = queue.poll();
            if (sentence.equals(toBeEntailed)) {
                return true;
            }

            if (!inferredMap.get(sentence)) {
                inferredMap.put(sentence, true);
                implications.forEach((premise, conclusion) -> {
                    if (premise.contains(sentence)) {
                        PremiseAndConclusion obj = new PremiseAndConclusion(premise, conclusion);
                        int count = countMap.get(obj);
                        countMap.put(obj, --count);
                        // TODO: 3/29/2024 ete arder inferred a, chqcel queue
                        if (count == 0) AndEliminationInferenceRule.infer(conclusion).forEach(queue::offer);
                    }
                });
            }
        }
        return false;
    }

    private void preprocessKnowledgeBase(Map<Premise, Sentence> kb) {
        Set<Sentence> inferredOrGiven = new HashSet<>();
        Set<PremiseAndConclusion> kbRefinedSentences = new HashSet<>();

        KB_LOOP: for (var iterator = kb.entrySet().iterator(); iterator.hasNext(); iterator.remove()) {
            var premiseAndConclusion = iterator.next();
            LinkedHashSet<Sentence> tempSet = new LinkedHashSet<>();
            for (Sentence sentence : premiseAndConclusion.getKey().getPremiseSentences()) {
                switch (sentence.satisfiabilityType()) {
                    case CONTRADICTION -> {continue KB_LOOP;}
                    case CONTINGENCY -> tempSet.add(sentence);
                }
            }

            if (tempSet.isEmpty()) {
                if (premiseAndConclusion.getValue() != null &&
                        premiseAndConclusion.getValue().satisfiabilityType() == SatisfiabilityType.CONTINGENCY) {
                    inferredOrGiven.add(premiseAndConclusion.getValue());
                }
            } else {
                Premise premise = new Premise(tempSet);

                switch (premise.satisfiabilityType()) {
                    case TAUTOLOGY -> {
                        if (premiseAndConclusion.getValue() != null &&
                                premiseAndConclusion.getValue().satisfiabilityType() == SatisfiabilityType.CONTINGENCY) {
                            inferredOrGiven.add(premiseAndConclusion.getValue());
                        }
                    }
                    case CONTINGENCY -> {
                        if (premiseAndConclusion.getValue() == null) {
                            inferredOrGiven.addAll(tempSet);
                        } else {
                            switch (premiseAndConclusion.getValue().satisfiabilityType()) {
                                case CONTRADICTION -> {
                                    try {
                                        inferredOrGiven.add(CNFRules.negateCNF(premise.minimalCNF()));
                                    } catch (TautologyException | ContradictionException ignored) {} //-- certain that won't throw
                                }
                                case CONTINGENCY -> {
                                    GenericComplexSentence possNotContingentGeneric =
                                            new GenericComplexSentence(premise.getCombinedSentence(),
                                            premiseAndConclusion.getValue(), Connective.IMPLICATION);

                                    if (possNotContingentGeneric.satisfiabilityType() == SatisfiabilityType.CONTINGENCY) {
                                        kbRefinedSentences.add(new PremiseAndConclusion(premise,
                                                premiseAndConclusion.getValue()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        inferredOrGiven.forEach(s -> {
            try {
                kb.put(new Premise(s.minimalCNF()), null);
            } catch (TautologyException | ContradictionException ignored) {} //-- certain that won't throw
        });

        kbRefinedSentences.forEach(s -> {
            try {
                kb.put(new Premise(s.premise().minimalCNF()), s.conclusion().minimalCNF());
            } catch (TautologyException | ContradictionException ignored) {} //-- certain that won't throw
        });
    }

    private Map<PremiseAndConclusion, Integer> constructCount() {
        Map<PremiseAndConclusion, Integer> countMap = new HashMap<>();
        implications.forEach((k, v) -> countMap.put(new PremiseAndConclusion(k, v), k.size()));
        return countMap;
    }

    private Map<Sentence, Boolean> constructInferred() {
        Map<Sentence, Boolean> inferredMap = new HashMap<>();
        known.forEach(s -> inferredMap.put(s, false));
        implications.forEach((k, v) -> {
            k.getPremiseSentences().forEach(i -> inferredMap.put(i, false));
            AndEliminationInferenceRule.infer(v).forEach(i -> inferredMap.put(i, false));
        });
        return inferredMap;
    }

    public static void main(String[] args) throws Exception {
        Map<Premise, Sentence> kb = new HashMap<>();

        kb.put(new Premise(new Literal("P")), new Literal("Q"));
        kb.put(new Premise(new CNFSentence("L & M")), new GenericComplexSentence("P & U"));
        kb.put(new Premise(new GenericComplexSentence("B & !!L")), new Clause("M"));
        kb.put(new Premise(new Literal("A"), new Clause("P")), new CNFSentence("L"));
        kb.put(new Premise(new CNFSentence("A"), new Literal("B")), new Literal("L"));
        kb.put(new Premise(new Literal("A")), null);
        kb.put(new Premise(new CNFSentence("B")), null);

        System.out.println(new ForwardChaining(kb).isEntail(new Literal("Q")));

//        kb.put(new Premise(new CNFSentence("(a | b) & (a | b | c)")), new CNFSentence("(a | b | !a | c)"));
//        kb.put(new Premise(new CNFSentence("(b | c)")), new Literal("Q"));
//        kb.put(new Premise(new CNFSentence("(a | b) & (a | b | c)")), null);

//        kb.put(new Premise(new Literal("S")), null);
//        kb.put(new Premise(new Literal("L")), null);
//        kb.put(new Premise(new Literal("E")), null);
//        kb.put(new Premise(new Literal("L"), new Literal("E")), new Literal("C"));
//        kb.put(new Premise(new Literal("E")), new Literal("D"));
//        kb.put(new Premise(new Literal("S")), new Literal("X"));
//        kb.put(new Premise(new GenericComplexSentence("(E | S) & L")), new Literal("Y"));
//        kb.put(new Premise(new CNFSentence("C & D")), new Literal("B"));
//        kb.put(new Premise(new GenericComplexSentence("C & (Y <=> X)")), new Clause("A"));
//        kb.put(new Premise(new GenericComplexSentence("A => B")), new Literal("T"));
//
//        System.out.println(new ForwardChaining(kb).isEntail(new Literal("X")));
//        System.out.println(new ForwardChaining(kb).isEntail(new Literal("Y")));
    }
}
