package org.example.cnf_util;

import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Clause;
import org.example.domain.sentence.Literal;
import org.example.domain.sentence.Sentences;
import org.example.exception.TautologyException;
import org.example.exception.UnsatisfiableException;

import java.util.*;

/**
 * @author aram.azatyan | 3/4/2024 2:59 PM
 */
public enum CNFRules {
    AND {
        @Override
        public CNFSentence apply(CNFSentence cnf1, CNFSentence cnf2) throws UnsatisfiableException, TautologyException {
            if (cnf1 == null || cnf2 == null) throw new IllegalArgumentException("null param");
            return new CNFSentence(combineClauses(cnf1.getClauses(), cnf2.getClauses()));
        }
    },
    OR {
        @Override
        public CNFSentence apply(CNFSentence cnf1, CNFSentence cnf2) throws UnsatisfiableException, TautologyException {
            if (cnf1 == null || cnf2 == null) throw new IllegalArgumentException("null param");
            LinkedHashSet<Clause> s1 = cnf1.getClauses();
            LinkedHashSet<Clause> s2 = cnf2.getClauses();
            LinkedHashSet<Clause> combined = new LinkedHashSet<>();
            for (Clause c1 : s1) {
                LinkedHashSet<Literal> l1 = c1.getLiterals();
                for (Clause c2 : s2) {
                    LinkedHashSet<Literal> possCombined = combineLiterals(l1, c2.getLiterals());
                    if (!possCombined.isEmpty()) combined.add(new Clause(possCombined));
                }
            }

            if (combined.isEmpty()) throw new TautologyException();
            return Sentences.optimizeCNF(new CNFSentence(combined));
        }
    },
    IMPLIES {
        @Override
        public CNFSentence apply(CNFSentence cnf1, CNFSentence cnf2) throws UnsatisfiableException, TautologyException {
            if (cnf1 == null || cnf2 == null) throw new IllegalArgumentException("null param");
            return OR.apply(negateCNF(cnf1), cnf2);
        }
    },
    BICONDITIONAL {
        @Override
        public CNFSentence apply(CNFSentence cnf1, CNFSentence cnf2) throws UnsatisfiableException, TautologyException {
            if (cnf1 == null || cnf2 == null) throw new IllegalArgumentException("null param");
            return AND.apply(IMPLIES.apply(cnf1, cnf2), IMPLIES.apply(cnf2, cnf1));
        }
    },
    NEGATED_AND {
        @Override
        public CNFSentence apply(CNFSentence cnf1, CNFSentence cnf2) throws UnsatisfiableException, TautologyException {
            if (cnf1 == null || cnf2 == null) throw new IllegalArgumentException("null param");
            return OR.apply(negateCNF(cnf1), negateCNF(cnf2));
        }
    },
    NEGATED_OR {
        @Override
        public CNFSentence apply(CNFSentence cnf1, CNFSentence cnf2) throws UnsatisfiableException, TautologyException {
            if (cnf1 == null || cnf2 == null) throw new IllegalArgumentException("null param");
            return AND.apply(negateCNF(cnf1), negateCNF(cnf2));
        }
    },
    NEGATED_IMPLIES {
        @Override
        public CNFSentence apply(CNFSentence cnf1, CNFSentence cnf2) throws UnsatisfiableException, TautologyException {
            if (cnf1 == null || cnf2 == null) throw new IllegalArgumentException("null param");
//            return negateCNF(IMPLIES.apply(cnf1, cnf2));
            return AND.apply(cnf1, negateCNF(cnf2));
        }
    },
    NEGATED_BICONDITIONAL {
        @Override
        public CNFSentence apply(CNFSentence cnf1, CNFSentence cnf2) throws UnsatisfiableException, TautologyException {
            if (cnf1 == null || cnf2 == null) throw new IllegalArgumentException("null param");
//            return negateCNF(BICONDITIONAL.apply(cnf1, cnf2));
            return BICONDITIONAL.apply(negateCNF(cnf1), cnf2);
        }
    };

    private static class ClauseState {
        private int index;
        private LinkedHashSet<Literal> clause;

        public ClauseState(int setIndex, LinkedHashSet<Literal> combination) {
            this.index = setIndex;
            this.clause = combination;
        }
    }

    public abstract CNFSentence apply(CNFSentence cnf1, CNFSentence cnf2) throws UnsatisfiableException, TautologyException;

    private static LinkedHashSet<Literal> combineLiterals(LinkedHashSet<Literal> s1, LinkedHashSet<Literal> s2) {
        LinkedHashSet<Literal> combined = new LinkedHashSet<>();
        combined.addAll(s1);
        combined.addAll(s2);
        try {
            return Sentences.optimizeClause(new Clause(combined)).getLiterals();
        } catch (TautologyException e) {
            return new LinkedHashSet<>();
        }
    }

    private static LinkedHashSet<Clause> combineClauses(LinkedHashSet<Clause> s1, LinkedHashSet<Clause> s2) throws
            UnsatisfiableException, TautologyException {
        LinkedHashSet<Clause> combined = new LinkedHashSet<>();
        combined.addAll(s1);
        combined.addAll(s2);
        return Sentences.optimizeCNF(new CNFSentence(combined)).getClauses();
    }

    public static CNFSentence negateCNF(CNFSentence sentence) throws UnsatisfiableException, TautologyException {
        LinkedHashSet<Clause> combined = new LinkedHashSet<>();
        List<LinkedHashSet<Literal>> clauseList = new ArrayList<>();
        sentence.getClauses().forEach(c -> clauseList.add(c.getLiterals()));
        Queue<ClauseState> queue = new ArrayDeque<>();
        queue.offer(new ClauseState(0, new LinkedHashSet<>()));

        while (!queue.isEmpty()) {
            ClauseState currentState = queue.poll();

            if (currentState.index == clauseList.size()) {
                combined.add(new Clause(currentState.clause));
            } else {
                for (Literal literal : clauseList.get(currentState.index)) {
                    LinkedHashSet<Literal> newClause = new LinkedHashSet<>(currentState.clause);
                    newClause.add(new Literal(literal.getName(), !literal.isNegated()));
                    queue.offer(new ClauseState(currentState.index + 1, newClause));
                }
            }
        }
        return Sentences.optimizeCNF(new CNFSentence(combined));
    }
}
