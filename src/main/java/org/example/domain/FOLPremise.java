package org.example.domain;

import org.example.domain.sentence.fol.FOLSentence;
import org.example.domain.sentence.fol.GenericComplexFOLSentence;
import org.example.domain.sentence.fol.Predicate;
import org.example.domain.sentence.fol.term.Constant;
import org.example.domain.sentence.fol.term.Variable;
import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.util.SentenceUtils;
import org.example.util.Utils;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.example.inference_rules.FOLAndEliminationInferenceRule.infer;

/**
 * @author aram.azatyan | 5/13/2024 1:51 PM
 */
public class FOLPremise {
    private final Set<FOLSentence> premise;
    private final FOLSentence combinedSentence;

    public FOLPremise(Set<FOLSentence> premise) {
        if (premise == null) throw new IllegalArgumentException("null param");
        premise.remove(null);
        if (premise.isEmpty()) throw new IllegalArgumentException("null param");

        this.premise = getInferredSet(premise);
        this.combinedSentence = combineSentences(this.premise);
    }

    public FOLPremise(FOLSentence... premise) {
        if (premise == null) throw new IllegalArgumentException("null param");
        Set<FOLSentence> temp = Arrays.stream(premise)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));
        if (temp.isEmpty()) throw new IllegalArgumentException("null param");

        this.premise = getInferredSet(temp);
        this.combinedSentence = combineSentences(this.premise);
    }

    public FOLPremise(String expression) {
        if (Utils.isNullOrBlank(expression)) throw new IllegalArgumentException("null param");

        this.premise = parseExpression(expression);
        this.combinedSentence = combineSentences(this.premise);
    }

    @SuppressWarnings("unchecked")
    private Set<FOLSentence> parseExpression(String expression) {
        expression = expression.replaceAll("\\s+", "");

        if (expression.contains("|")) {
            String[] arr = expression.split("\\|");
            String quantifierPart = arr[0];
            String folPart = arr[1];

            Set<Predicate> predicates = Arrays.stream(folPart.split(SentenceUtils.AND))
                    .map(s -> {
                        try {
                            return new Predicate(s);
                        } catch (ParseException ignored) {
                            return null;
                        }
                    }).collect(Collectors.toSet());

            Set<Variable> variables = getVariables(quantifierPart);

            AtomicInteger count = new AtomicInteger(1);

            variables.forEach(v -> {
                predicates.forEach(p -> {
                    p.substitute(v, new Constant("C" + count.get()));
                });
                count.incrementAndGet();
            });

            return (Set<FOLSentence>) (Set<? extends FOLSentence>) predicates;
        } else {
            return Arrays.stream(expression.split(SentenceUtils.AND))
                    .map(s -> {
                        try {
                            return new Predicate(s);
                        } catch (ParseException ignored) {
                            return null;
                        }
                    }).collect(Collectors.toSet());
        }
    }

    private Set<Variable> getVariables(String quantifierPart) {
        Set<Variable> set = new HashSet<>();
        boolean isExistential = false;

        StringBuilder varName = new StringBuilder();
        for (char c : quantifierPart.toCharArray()) {
            if (c == SentenceUtils.EXISTENTIAL || c == SentenceUtils.UNIVERSAL) {
                if (!varName.isEmpty() && isExistential) {
                    set.add(new Variable(varName.toString()));
                    varName = new StringBuilder();
                }
                isExistential = c == SentenceUtils.EXISTENTIAL;
            } else {
                varName.append(c);
            }
        }

        if (!varName.isEmpty() && isExistential) {
            set.add(new Variable(varName.toString()));
        }

        return set;
    }


    public boolean contains(FOLSentence sentence) {
        return premise.contains(sentence);
    }

    public int size() {
        return premise.size();
    }

    public FOLSentence getCombinedSentence() {
        return combinedSentence;
    }

    public Set<FOLSentence> getPremiseSentences() {
        return new HashSet<>(premise);
    }

    private Set<FOLSentence> getInferredSet(Set<FOLSentence> set) {
        Set<FOLSentence> inferred = new HashSet<>();
        set.forEach(s -> inferred.addAll(infer(s)));

        return inferred;
    }

    private FOLSentence combineSentences(Set<FOLSentence> sentences) {
        return sentences.size() == 1
                ? sentences.iterator().next()
                : new GenericComplexFOLSentence(new LinkedHashSet<>(sentences), Connective.AND, false);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof FOLPremise that)) return false;

        return this.premise.equals(that.premise);
    }

    @Override
    public int hashCode() {
        return premise.hashCode();
    }
}
