package org.example.domain;

import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.GenericComplexSentence;
import org.example.domain.sentence.Sentence;
import org.example.exception.TautologyException;
import org.example.exception.ContradictionException;
import org.example.inference_rules.AndEliminationInferenceRule;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author aram.azatyan | 3/25/2024 4:02 PM
 */
public class Premise {
    private final Set<Sentence> premise;
    private final Sentence combinedSentence;
    private CNFSentence minimalCNF;
    private SatisfiabilityType satisfiabilityType;

    public Premise(Set<Sentence> premise) {
        if (premise == null) throw new IllegalArgumentException("null param");
        premise.remove(null);
        if (premise.isEmpty()) throw new IllegalArgumentException("null param");

        this.premise = getInferredSet(premise);
        this.combinedSentence = combineSentences(this.premise);
    }

    public Premise(Sentence... premise) {
        if (premise == null) throw new IllegalArgumentException("null param");
        Set<Sentence> temp = Arrays.stream(premise)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));
        if (temp.isEmpty()) throw new IllegalArgumentException("null param");

        this.premise = getInferredSet(temp);
        this.combinedSentence = combineSentences(this.premise);
    }

    public boolean contains(Sentence sentence) {
        return premise.contains(sentence);
    }

    public int size() {
        return premise.size();
    }

    public CNFSentence minimalCNF() throws TautologyException, ContradictionException {
        if (satisfiabilityType == null) {
            try {
                minimalCNF = combinedSentence.minimalCNF();
                satisfiabilityType = SatisfiabilityType.CONTINGENCY;
                return minimalCNF;
            } catch (TautologyException e) {
                satisfiabilityType = SatisfiabilityType.TAUTOLOGY;
                throw new TautologyException();
            } catch (ContradictionException e) {
                satisfiabilityType = SatisfiabilityType.CONTRADICTION;
                throw new ContradictionException();
            }
        } else if (satisfiabilityType == SatisfiabilityType.TAUTOLOGY) {
            throw new TautologyException();
        } else if (satisfiabilityType == SatisfiabilityType.CONTRADICTION) {
            throw new ContradictionException();
        }

        return minimalCNF;
    }

    public SatisfiabilityType satisfiabilityType() {
        if (satisfiabilityType == null) {
            try {
                minimalCNF();
            } catch (TautologyException | ContradictionException ignored) {}
        }
        return satisfiabilityType;
    }

    public Sentence getCombinedSentence() {
        return combinedSentence;
    }

    public Set<Sentence> getPremiseSentences() {
        return new HashSet<>(premise);
    }

    private Set<Sentence> getInferredSet(Set<Sentence> set) {
        Set<Sentence> inferred = new HashSet<>();
        set.forEach(s -> inferred.addAll(AndEliminationInferenceRule.infer(s)));


//
//        if (inferred.isEmpty()) {
//            throw new Verum();
//        } else if (inferred.size() == 1) {
//            inferred.stream().iterator().next().convertToCNF();
//        } else {
//            new GenericComplexSentence(new LinkedHashSet<>(inferred),
//                    Connective.AND, false).convertToCNF();
//        }

        return inferred;
    }

    private Sentence combineSentences(Set<Sentence> sentences) {
        return sentences.size() == 1
                ? sentences.iterator().next()
                : new GenericComplexSentence(new LinkedHashSet<>(sentences), Connective.AND, false);
    }
}
