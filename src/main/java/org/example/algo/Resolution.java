package org.example.algo;

import org.example.domain.SatisfiabilityType;
import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Clause;
import org.example.domain.sentence.Sentence;
import org.example.exception.ContradictionException;
import org.example.exception.NothingToInferException;
import org.example.exception.TautologyException;
import org.example.inference_rules.ResolutionInferenceRule;
import org.example.util.SentenceUtils;

import java.util.*;

/**
 * @author aram.azatyan | 3/27/2024 2:50 PM
 */
public class Resolution {

    private final Set<Clause> knowledgeBase;

    public Resolution(Set<Sentence> knowledgeBase, boolean shouldIgnoreContradictions)
            throws TautologyException, ContradictionException {
        if (knowledgeBase == null || knowledgeBase.isEmpty()) throw new IllegalArgumentException("null param");
        knowledgeBase.remove(null);
        if (knowledgeBase.isEmpty()) throw new IllegalArgumentException("null param");

        this.knowledgeBase = getKBClauses(knowledgeBase, shouldIgnoreContradictions);
    }

    private Set<Clause> getKBClauses(Set<Sentence> knowledgeBase, boolean shouldIgnoreContradictions)
            throws TautologyException, ContradictionException {
        Set<CNFSentence> preprocessedKnowledgeBase = new HashSet<>();

        for (Iterator<Sentence> iterator = knowledgeBase.iterator(); iterator.hasNext(); iterator.remove()) {
            Sentence sentence = iterator.next();
            if (sentence.satisfiabilityType() == SatisfiabilityType.CONTRADICTION) {
                if (!shouldIgnoreContradictions) throw new ContradictionException();
            } else if (sentence.satisfiabilityType() == SatisfiabilityType.CONTINGENCY) {
                preprocessedKnowledgeBase.add(sentence.minimalCNF());
            }
        }

        if (preprocessedKnowledgeBase.isEmpty()) throw new TautologyException();

        LinkedHashSet<Clause> combinedClauses = new LinkedHashSet<>();
        preprocessedKnowledgeBase.forEach(s -> combinedClauses.addAll(s.getClauses()));

        return new CNFSentence(combinedClauses).minimalCNF().getClauses();
    }

    public Set<Clause> resolveAndGet() {
        Set<Clause> resolved = new HashSet<>();

        while (true) {
            for (Clause compared : knowledgeBase) {
                for (Clause current : knowledgeBase) {
                    if (!compared.equals(current)) {
                        try {
                            resolved.add(ResolutionInferenceRule.resolve(compared, current));
                        } catch (NothingToInferException ignored) {}
                    }
                }
            }

            if (knowledgeBase.containsAll(resolved)) {
                resolved.addAll(knowledgeBase);
                return resolved;
            }

            knowledgeBase.addAll(resolved);
        }
    }

    public static void main(String[] args) throws Exception {
        CNFSentence cnf = new CNFSentence(SentenceUtils.convertOnlineCalculatorString("(¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ E) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ ¬C ∨ D ∨ E) ∧ (A ∨ B ∨ C ∨ ¬D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ ¬D ∨ E) ∧ (A ∨ B ∨ C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ D ∨ E)"));
        Set<Sentence> kb = new HashSet<>();
        kb.add(cnf);
        new Resolution(kb, true).resolveAndGet().forEach(System.out::println);

        String sentence = """
                !A | !B | D | E
                !A | !B | C | D
                !A | !C | !E
                A | B | C | !D
                !A | !B | !D
                A | B | D | E
                A | B | C | D
                !A | !B | D
                !A | !C | !D | !E
                !A | !B | !C | !E
                !A | !C | !D | E
                !A | !C | D | !E
                !A | !B | !C | E
                !A | !B | C | !E
                !A | B | !C | !E
                !A | !B | C | E
                !A | !B | !C | !D | !E
                A | B | C | !E
                !A | !B | !C | !D | E
                !A | !B | !C | D | !E
                !A | !B | C | !D | !E
                !A | B | !C | !D | !E
                !A | !C | !D
                !A | !B | !E
                !A | !B | !C
                A | B | C | E
                !A | !B | !C | D | E
                !A | !B | C | !D | E
                !A | !B | C | D | !E
                !A | B | !C | !D | E
                !A | B | !C | D | !E
                !A | !B | E
                !A | !B | C
                !A | !B | C | D | E
                A | B | C | !D | !E
                !A | !B | !D | !E
                !A | !B | !C | !D
                A | B | !C | D | E
                A | B | C | !D | E
                A | B | C | D | !E
                !A | !B | !D | E
                !A | !B | D | !E
                !A | !B | !C | D
                !A | !B | C | !D
                !A | B | !C | !D
                A | B | C
                !A | !B
                A | B | C | D | E
                """;

        LinkedHashSet<Clause> clauses = new LinkedHashSet<>();
        List<String> list = Arrays.stream(sentence.split("\n")).map(String::trim).toList();
        for (String s : list) {
            clauses.add(new Clause(s));
        }

        System.out.println(new CNFSentence(clauses).minimalCNF());
    }
}
