package org.example.domain;

import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.sentence.propositional.GenericComplexPropositionalSentence;
import org.example.domain.sentence.Sentence;
import org.example.domain.sentence.propositional.PropositionalSentence;
import org.example.exception.TautologyException;
import org.example.exception.ContradictionException;
import org.example.inference_rules.PropositionalAndEliminationInferenceRule;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author aram.azatyan | 3/25/2024 4:02 PM
 */
public class PropositionalPremise {
    private final Set<PropositionalSentence> premise;
    private final PropositionalSentence combinedSentence;
    private PropositionalCNFSentence minimalCNF;
    private SatisfiabilityType satisfiabilityType;

    public PropositionalPremise(Set<PropositionalSentence> premise) {
        if (premise == null) throw new IllegalArgumentException("null param");
        premise.remove(null);
        if (premise.isEmpty()) throw new IllegalArgumentException("null param");

        this.premise = getInferredSet(premise);
        this.combinedSentence = combineSentences(this.premise);
    }

    public PropositionalPremise(PropositionalSentence... premise) {
        if (premise == null) throw new IllegalArgumentException("null param");
        Set<PropositionalSentence> temp = Arrays.stream(premise)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));
        if (temp.isEmpty()) throw new IllegalArgumentException("null param");

        this.premise = getInferredSet(temp);
        this.combinedSentence = combineSentences(this.premise);
    }

    public boolean contains(PropositionalSentence sentence) {
        return premise.contains(sentence);
    }

    public int size() {
        return premise.size();
    }

    public PropositionalCNFSentence minimalCNF() throws TautologyException, ContradictionException {
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

    public PropositionalSentence getCombinedSentence() {
        return combinedSentence;
    }

    public Set<PropositionalSentence> getPremiseSentences() {
        return new HashSet<>(premise);
    }

    private Set<PropositionalSentence> getInferredSet(Set<PropositionalSentence> set) {
        Set<PropositionalSentence> inferred = new HashSet<>();
        set.forEach(s -> inferred.addAll(PropositionalAndEliminationInferenceRule.infer(s)));


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

    private PropositionalSentence combineSentences(Set<PropositionalSentence> sentences) {
        return sentences.size() == 1
                ? sentences.iterator().next()
                : new GenericComplexPropositionalSentence(new LinkedHashSet<>(sentences), Connective.AND, false);
    }
}
