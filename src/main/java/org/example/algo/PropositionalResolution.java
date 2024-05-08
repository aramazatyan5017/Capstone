package org.example.algo;

import org.example.domain.Sentences;
import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.sentence.propositional.PropositionalClause;
import org.example.exception.ContradictionException;
import org.example.exception.NothingToInferException;
import org.example.exception.TautologyException;
import org.example.inference_rules.PropositionalResolutionInferenceRule;
import org.example.util.SentenceUtils;

import java.util.*;

/**
 * @author aram.azatyan | 3/27/2024 2:50 PM
 */
public class PropositionalResolution {

    private final Set<PropositionalClause> knowledgeBase;

    public PropositionalResolution(Set<PropositionalCNFSentence> knowledgeBase, boolean shouldIgnoreContradictions)
            throws TautologyException, ContradictionException {
        if (knowledgeBase == null || knowledgeBase.isEmpty()) throw new IllegalArgumentException("null param");
        knowledgeBase.remove(null);
        if (knowledgeBase.isEmpty()) throw new IllegalArgumentException("null param");

        this.knowledgeBase = getKBClauses(knowledgeBase, shouldIgnoreContradictions);
    }

    private Set<PropositionalClause> getKBClauses(Set<PropositionalCNFSentence> knowledgeBase, boolean shouldIgnoreContradictions)
            throws TautologyException, ContradictionException {
        Set<PropositionalCNFSentence> preprocessedKnowledgeBase = new HashSet<>();

        for (Iterator<PropositionalCNFSentence> iterator = knowledgeBase.iterator(); iterator.hasNext(); iterator.remove()) {
            PropositionalCNFSentence cnfSentence = iterator.next();

            try {
                preprocessedKnowledgeBase.add(Sentences.optimizeCNF(cnfSentence));
            } catch (ContradictionException e) {
                if (!shouldIgnoreContradictions) throw new ContradictionException();
            } catch (TautologyException ignored) {}

//            if (cnfSentence.satisfiabilityType() == SatisfiabilityType.CONTRADICTION) {
//                if (!shouldIgnoreContradictions) throw new ContradictionException();
//            } else if (cnfSentence.satisfiabilityType() == SatisfiabilityType.CONTINGENCY) {
//                preprocessedKnowledgeBase.add(cnfSentence.minimalCNF());
//            }
        }

        if (preprocessedKnowledgeBase.isEmpty()) throw new TautologyException();

        LinkedHashSet<PropositionalClause> combinedClauses = new LinkedHashSet<>();
        preprocessedKnowledgeBase.forEach(s -> combinedClauses.addAll(s.getClauses()));

        return Sentences.optimizeCNF(new PropositionalCNFSentence(combinedClauses)).getClauses();
    }

    public Set<PropositionalClause> resolveAndGet() {
        Set<PropositionalClause> resolved = new HashSet<>();

        while (true) {
            for (PropositionalClause compared : knowledgeBase) {
                for (PropositionalClause current : knowledgeBase) {
                    if (!compared.equals(current)) {
                        try {
                            resolved.add(PropositionalResolutionInferenceRule.resolve(compared, current));
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
        PropositionalCNFSentence cnf = new PropositionalCNFSentence(SentenceUtils.convertOnlineCalculatorString("(¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ E) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ ¬C ∨ D ∨ E) ∧ (A ∨ B ∨ C ∨ ¬D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ ¬D ∨ E) ∧ (A ∨ B ∨ C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ D ∨ E)"));
        Set<PropositionalCNFSentence> kb = new HashSet<>();
        kb.add(cnf);
        new PropositionalResolution(kb, true).resolveAndGet().forEach(System.out::println);

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

        LinkedHashSet<PropositionalClause> clauses = new LinkedHashSet<>();
        List<String> list = Arrays.stream(sentence.split("\n")).map(String::trim).toList();
        for (String s : list) {
            clauses.add(new PropositionalClause(s));
        }

        System.out.println(new PropositionalCNFSentence(clauses).minimalCNF());
    }
}
