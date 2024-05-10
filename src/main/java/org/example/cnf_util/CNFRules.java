package org.example.cnf_util;

import org.example.domain.sentence.BasicLogicElement;
import org.example.domain.sentence.Clause;
import org.example.domain.Sentences;
import org.example.exception.TautologyException;
import org.example.exception.ContradictionException;

import java.util.*;

/**
 * @author aram.azatyan | 3/4/2024 2:59 PM
 */
public enum CNFRules {
    AND {
        @Override
        public CNFSentence apply(CNFSentence cnfSentence1, CNFSentence cnfSentence2) throws ContradictionException, TautologyException {
            validateCNFSentences(cnfSentence1, cnfSentence2);
            return CNFSentence.createCNF(combineClauses(cnfSentence1.clauses(), cnfSentence2.clauses()));
        }
    },
    OR {
        @Override
        public CNFSentence apply(CNFSentence cnfSentence1, CNFSentence cnfSentence2) throws ContradictionException, TautologyException {
            validateCNFSentences(cnfSentence1, cnfSentence2);
            LinkedHashSet<Clause> s1 = cnfSentence1.clauses();
            LinkedHashSet<Clause> s2 = cnfSentence2.clauses();
            LinkedHashSet<Clause> combined = new LinkedHashSet<>();
            for (Clause c1 : s1) {
                LinkedHashSet<BasicLogicElement> l1 = c1.basicElements();
                for (Clause c2 : s2) {
                    LinkedHashSet<BasicLogicElement> possCombined = combineBasicElements(l1, c2.basicElements());
                    if (!possCombined.isEmpty()) combined.add(Clause.createClause(possCombined));
                }
            }

            if (combined.isEmpty()) throw new TautologyException();
            return Sentences.optimizeCNF(CNFSentence.createCNF(combined));
        }
    },
    IMPLIES {
        @Override
        public CNFSentence apply(CNFSentence cnfSentence1, CNFSentence cnfSentence2) throws ContradictionException, TautologyException {
            validateCNFSentences(cnfSentence1, cnfSentence2);
            return OR.apply(negateCNF(cnfSentence1), cnfSentence2);
        }
    },
    BICONDITIONAL {
        @Override
        public CNFSentence apply(CNFSentence cnfSentence1, CNFSentence cnfSentence2) throws ContradictionException, TautologyException {
            validateCNFSentences(cnfSentence1, cnfSentence2);
            return AND.apply(IMPLIES.apply(cnfSentence1, cnfSentence2), IMPLIES.apply(cnfSentence2, cnfSentence1));
        }
    },
    NEGATED_AND {
        @Override
        public CNFSentence apply(CNFSentence cnfSentence1, CNFSentence cnfSentence2) throws ContradictionException, TautologyException {
            validateCNFSentences(cnfSentence1, cnfSentence2);
            return OR.apply(negateCNF(cnfSentence1), negateCNF(cnfSentence2));
        }
    },
    NEGATED_OR {
        @Override
        public CNFSentence apply(CNFSentence cnfSentence1, CNFSentence cnfSentence2) throws ContradictionException, TautologyException {
            validateCNFSentences(cnfSentence1, cnfSentence2);
            return AND.apply(negateCNF(cnfSentence1), negateCNF(cnfSentence2));
        }
    },
    NEGATED_IMPLIES {
        @Override
        public CNFSentence apply(CNFSentence cnfSentence1, CNFSentence cnfSentence2) throws ContradictionException, TautologyException {
            validateCNFSentences(cnfSentence1, cnfSentence2);
//            return negateCNF(IMPLIES.apply(cnf1, cnf2));
            return AND.apply(cnfSentence1, negateCNF(cnfSentence2));
        }
    },
    NEGATED_BICONDITIONAL {
        @Override
        public CNFSentence apply(CNFSentence cnfSentence1, CNFSentence cnfSentence2) throws ContradictionException, TautologyException {
            validateCNFSentences(cnfSentence1, cnfSentence2);
//            return negateCNF(BICONDITIONAL.apply(cnf1, cnf2));
            return BICONDITIONAL.apply(negateCNF(cnfSentence1), cnfSentence2);
        }
    };

    private static class ClauseState {
        private int index;
        private LinkedHashSet<BasicLogicElement> clause;

        public ClauseState(int setIndex, LinkedHashSet<BasicLogicElement> combination) {
            this.index = setIndex;
            this.clause = combination;
        }
    }

    public abstract CNFSentence apply(CNFSentence cnfSentence1, CNFSentence cnfSentence2) throws ContradictionException, TautologyException;

    private static LinkedHashSet<BasicLogicElement> combineBasicElements(LinkedHashSet<BasicLogicElement> s1, LinkedHashSet<BasicLogicElement> s2) {
        LinkedHashSet<BasicLogicElement> combined = new LinkedHashSet<>();
        combined.addAll(s1);
        combined.addAll(s2);
        try {
            return Sentences.optimizeClause(Clause.createClause(combined)).getLiterals();
        } catch (TautologyException | ContradictionException e) {
            return new LinkedHashSet<>();
        }
    }

    private static LinkedHashSet<Clause> combineClauses(LinkedHashSet<Clause> s1, LinkedHashSet<Clause> s2) throws
            ContradictionException, TautologyException {
        LinkedHashSet<Clause> combined = new LinkedHashSet<>();
        combined.addAll(s1);
        combined.addAll(s2);
        return Sentences.optimizeCNF(CNFSentence.createCNF(combined)).getClauses();
    }

    private static void validateCNFSentences(CNFSentence cnf1, CNFSentence cnf2) {
        if (cnf1 == null || cnf2 == null) throw new IllegalArgumentException("null param");
        if (cnf1.logicType() != cnf2.logicType()) throw new IllegalArgumentException("cnf sentences of different logic types");
    }

    public static CNFSentence negateCNF(CNFSentence sentence) throws ContradictionException, TautologyException {
        sentence = sentence.isCanonical() ? Sentences.optimizeCanonicalCNF(sentence) : Sentences.optimizeCNF(sentence);

        LinkedHashSet<Clause> combined = new LinkedHashSet<>();
        List<LinkedHashSet<BasicLogicElement>> clauseList = new ArrayList<>();
        sentence.clauses().forEach(c -> clauseList.add(c.basicElements()));
        Queue<ClauseState> queue = new ArrayDeque<>();
        queue.offer(new ClauseState(0, new LinkedHashSet<>()));

        while (!queue.isEmpty()) {
            ClauseState currentState = queue.poll();

            if (currentState.index == clauseList.size()) {
                combined.add(Clause.createClause(currentState.clause));
            } else {
                for (BasicLogicElement element : clauseList.get(currentState.index)) {
                    LinkedHashSet<BasicLogicElement> newClause = new LinkedHashSet<>(currentState.clause);
                    newClause.add(element.getNegated());
                    queue.offer(new ClauseState(currentState.index + 1, newClause));
                }
            }
        }
        return Sentences.optimizeCNF(CNFSentence.createCNF(combined));
    }
}
