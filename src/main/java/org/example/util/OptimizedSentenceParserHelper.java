package org.example.util;

import org.example.domain.Connective;
import org.example.domain.Sentences;
import org.example.domain.sentence.*;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;

import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.List;

import static org.example.domain.SentenceType.*;

/**
 * @author aram.azatyan | 4/3/2024 3:00 PM
 */
public class OptimizedSentenceParserHelper {

    //-- OR start
    public static Sentence literalOrLiteral(Sentence left, Sentence right) {
        Literal l1 = (Literal) left;
        Literal l2 = (Literal) right;

        if (l1 == Literal.TRUE || l2 == Literal.TRUE) return Literal.TRUE;
        if (l1.equals(l2)) return l1;
        if (l1.equalsIgnoreNegation(l2)) return Literal.TRUE;
        return new Clause(l1, l2);
    }

    public static Sentence literalOrClause(Sentence left, Sentence right) {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        Clause clause = (Clause) (left.type() == CLAUSE ? left : right);

        if (literal == Literal.TRUE) return Literal.TRUE;
        if (literal == Literal.FALSE) return clause;

        LinkedHashSet<Literal> literals = clause.getLiterals();
        literals.add(literal);

        try {
            Clause optClause = Sentences.optimizeClause(new Clause(literals));
            return optClause.size() == 1 ? optClause.getLiterals().iterator().next() : optClause;
        } catch (TautologyException e) {
            return Literal.TRUE;
        } catch (ContradictionException e) {
            return Literal.FALSE;
        }
    }

    public static Sentence literalOrCNF(Sentence left, Sentence right) {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        CNFSentence cnf = (CNFSentence) (left.type() == CNF ? left : right);

        if (literal == Literal.TRUE) return Literal.TRUE;
        if (literal == Literal.FALSE) return cnf;

        // TODO: 4/3/2024 chi kara cnf lini, vori size y 1 a

        return new GenericComplexSentence(left, right, Connective.OR);
    }

    public static Sentence literalOrGeneric(Sentence left, Sentence right) {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        GenericComplexSentence generic = (GenericComplexSentence) (left.type() == GENERIC_COMPLEX ? left : right);

        if (literal == Literal.TRUE) return Literal.TRUE;
        if (literal == Literal.FALSE) return generic;

        return new GenericComplexSentence(left, right, Connective.OR);
    }

    public static Sentence clauseOrClause(Sentence left, Sentence right) {
        Clause c1 = (Clause) left;
        Clause c2 = (Clause) right;

        LinkedHashSet<Literal> literals = new LinkedHashSet<>();
        literals.addAll(c1.getLiterals());
        literals.addAll(c2.getLiterals());

        try {
            Clause optClause = Sentences.optimizeClause(new Clause(literals));
            return optClause.size() == 1 ? optClause.getLiterals().iterator().next() : optClause;
        } catch (TautologyException e) {
            return Literal.TRUE;
        } catch (ContradictionException e) {
            return Literal.FALSE;
        }
    }

    public static Sentence clauseOrCNF(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.OR);
    }

    public static Sentence clauseOrGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.OR);
    }

    public static Sentence cnfOrCNF(Sentence left, Sentence right) {
        CNFSentence cnfLeft = (CNFSentence) left;
        CNFSentence cnfRight = (CNFSentence) right;

        if (cnfLeft.equals(cnfRight)) return cnfLeft;
        return new GenericComplexSentence(left, right, Connective.OR);
    }

    public static Sentence cnfOrGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.OR);
    }

    public static Sentence genericOrGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.OR);
    }
    //-- OR end

    //-- NegatedOR start
    public static Sentence literalNegatedOrLiteral(Sentence left, Sentence right) {
        Literal l1 = (Literal) left;
        Literal l2 = (Literal) right;

        if (l1 == Literal.TRUE || l2 == Literal.TRUE) return Literal.FALSE;
        if (l1 == Literal.FALSE && l2 == Literal.FALSE) return Literal.TRUE;

        if (l1.equals(l2)) return new Literal(l1.getName(), !l1.isNegated());
        if (l1.equalsIgnoreNegation(l2)) return Literal.FALSE;

        return new GenericComplexSentence(left, right, Connective.OR, true);
    }

    public static Sentence literalNegatedOrClause(Sentence left, Sentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        Clause clause = (Clause) (left.type() == CLAUSE ? left : right);

        if (literal == Literal.TRUE) return Literal.FALSE;
        if (literal == Literal.FALSE) {
            if (clause.size() == 2) {
                List<Literal> literals = clause.getLiteralList();
                return new GenericComplexSentence(literals.get(0), literals.get(1),
                        Connective.OR, true);
            }
            return new GenericComplexSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + clause + SentenceUtils.CLOSING_PARENTHESES);
        }

        LinkedHashSet<Literal> literals = clause.getLiterals();
        literals.add(literal);

        try {
            Clause optClause = Sentences.optimizeClause(new Clause(literals));
            if (optClause.size() == 1) {
                Literal l = optClause.getLiterals().iterator().next();
                return new Literal(l.getName(), !l.isNegated());
            } else if (optClause.size() == 2) {
                List<Literal> literalList = optClause.getLiteralList();
                return new GenericComplexSentence(literalList.get(0), literalList.get(1),
                        Connective.OR, true);
            }
            return new GenericComplexSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + optClause + SentenceUtils.CLOSING_PARENTHESES);
        } catch (TautologyException e) {
            return Literal.FALSE;
        } catch (ContradictionException e) {
            return Literal.TRUE;
        }
    }

    public static Sentence literalNegatedOrCNF(Sentence left, Sentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        CNFSentence cnf = (CNFSentence) (left.type() == CNF ? left : right);

        if (literal == Literal.TRUE) return Literal.FALSE;
        if (literal == Literal.FALSE) {
            return new GenericComplexSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + cnf + SentenceUtils.CLOSING_PARENTHESES);
        }

        return new GenericComplexSentence(left, right, Connective.OR, true);
    }

    public static Sentence literalNegatedOrGeneric(Sentence left, Sentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        GenericComplexSentence generic = (GenericComplexSentence) (left.type() == GENERIC_COMPLEX ? left : right);

        if (literal == Literal.TRUE) return Literal.FALSE;
        if (literal == Literal.FALSE) return new GenericComplexSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + generic + SentenceUtils.CLOSING_PARENTHESES);

        return new GenericComplexSentence(left, right, Connective.OR, true);
    }

    public static Sentence clauseNegatedOrClause(Sentence left, Sentence right) throws ParseException {
        Clause c1 = (Clause) left;
        Clause c2 = (Clause) right;

        LinkedHashSet<Literal> literals = new LinkedHashSet<>();
        literals.addAll(c1.getLiterals());
        literals.addAll(c2.getLiterals());

        try {
            Clause optClause = Sentences.optimizeClause(new Clause(literals));
            if (optClause.size() == 1) {
                Literal l = optClause.getLiterals().iterator().next();
                return new Literal(l.getName(), !l.isNegated());
            } else if (optClause.size() == 2) {
                List<Literal> literalList = optClause.getLiteralList();
                return new GenericComplexSentence(literalList.get(0), literalList.get(1),
                        Connective.OR, true);
            }
            return new GenericComplexSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + optClause + SentenceUtils.CLOSING_PARENTHESES);
        } catch (TautologyException e) {
            return Literal.FALSE;
        } catch (ContradictionException e) {
            return Literal.TRUE;
        }
    }

    public static Sentence clauseNegatedOrCNF(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.OR, true);
    }

    public static Sentence clauseNegatedOrGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.OR, true);
    }

    public static Sentence cnfNegatedOrCNF(Sentence left, Sentence right) throws ParseException {
        CNFSentence cnfLeft = (CNFSentence) left;
        CNFSentence cnfRight = (CNFSentence) right;

        if (cnfLeft.equals(cnfRight)) return new GenericComplexSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + cnfLeft + SentenceUtils.CLOSING_PARENTHESES);
        return new GenericComplexSentence(left, right, Connective.OR, true);
    }

    public static Sentence cnfNegatedOrGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.OR, true);
    }

    public static Sentence genericNegatedOrGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.OR, true);
    }
    //-- NegatedOR end

    //-- AND start
    public static Sentence literalAndLiteral(Sentence left, Sentence right) {
        Literal l1 = (Literal) left;
        Literal l2 = (Literal) right;

        if (l1 == Literal.FALSE || l2 == Literal.FALSE) return Literal.FALSE;
        if (l1 == Literal.TRUE && l2 == Literal.TRUE) return Literal.TRUE;
        if (l1.equals(l2)) return l1;
        if (l1.equalsIgnoreNegation(l2)) return Literal.FALSE;

        return new CNFSentence(new Clause(l1), new Clause(l2));
    }

    public static Sentence literalAndClause(Sentence left, Sentence right) {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        Clause clause = (Clause) (left.type() == CLAUSE ? left : right);

        if (literal == Literal.FALSE) return Literal.FALSE;
        if (literal == Literal.TRUE) return clause;

        try {
            CNFSentence cnf = Sentences.optimizeCNF(new CNFSentence(new Clause(literal), clause));
            if (cnf.size() == 1) {
                Clause c = cnf.getClauses().iterator().next();
                if (c.size() == 1) return c.getLiterals().iterator().next();
                return c;
            }
            return cnf;
        } catch (TautologyException e) {
            return Literal.TRUE;
        } catch (ContradictionException e) {
            return Literal.FALSE;
        }
    }

    public static Sentence literalAndCNF(Sentence left, Sentence right) {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        CNFSentence cnf = (CNFSentence) (left.type() == CNF ? left : right);

        if (literal == Literal.FALSE) return Literal.FALSE;
        if (literal == Literal.TRUE) return cnf;

        try {
            LinkedHashSet<Clause> clauses = cnf.getClauses();
            clauses.add(new Clause(literal));
            CNFSentence optCnf = Sentences.optimizeCNF(new CNFSentence(clauses));
            if (optCnf.size() == 1) {
                Clause c = optCnf.getClauses().iterator().next();
                if (c.size() == 1) return c.getLiterals().iterator().next();
                return c;
            }
            return optCnf;
        } catch (TautologyException e) {
            return Literal.TRUE;
        } catch (ContradictionException e) {
            return Literal.FALSE;
        }
    }

    public static Sentence literalAndGeneric(Sentence left, Sentence right) {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        GenericComplexSentence generic = (GenericComplexSentence) (left.type() == GENERIC_COMPLEX ? left : right);

        if (literal == Literal.FALSE) return Literal.FALSE;
        if (literal == Literal.TRUE) return generic;

        return new GenericComplexSentence(left, right, Connective.AND);
    }

    public static Sentence clauseAndClause(Sentence left, Sentence right) {
        if (left.equals(right)) return left;
        try {
            CNFSentence cnf = Sentences.optimizeCNF(new CNFSentence((Clause) left, (Clause) right));
            if (cnf.size() == 1) {
                Clause c = cnf.getClauses().iterator().next();
                if (c.size() == 1) return c.getLiterals().iterator().next();
                return c;
            }
            return cnf;
        } catch (TautologyException e) {
            return Literal.TRUE;
        } catch (ContradictionException e) {
            return Literal.FALSE;
        }
    }

    public static Sentence clauseAndCNF(Sentence left, Sentence right) {
        Clause clause = (Clause) (left.type() == CLAUSE ? left : right);
        CNFSentence cnf = (CNFSentence) (left.type() == CNF ? left : right);

        LinkedHashSet<Clause> clauses = cnf.getClauses();
        clauses.add(clause);

        try {
            CNFSentence optCnf = Sentences.optimizeCNF(new CNFSentence(clauses));
            if (optCnf.size() == 1) {
                Clause c = optCnf.getClauses().iterator().next();
                if (c.size() == 1) return c.getLiterals().iterator().next();
                return c;
            }
            return optCnf;
        } catch (TautologyException e) {
            return Literal.TRUE;
        } catch (ContradictionException e) {
            return Literal.FALSE;
        }
    }

    public static Sentence clauseAndGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.AND);
    }

    public static Sentence cnfAndCNF(Sentence left, Sentence right) {
        CNFSentence leftCNF = (CNFSentence) left;
        CNFSentence rightCNF = (CNFSentence) right;

        if (leftCNF.equals(rightCNF)) return leftCNF;

        LinkedHashSet<Clause> clauses = new LinkedHashSet<>();
        clauses.addAll(leftCNF.getClauses());
        clauses.addAll(rightCNF.getClauses());

        try {
            CNFSentence optCnf = Sentences.optimizeCNF(new CNFSentence(clauses));
            if (optCnf.size() == 1) {
                Clause c = optCnf.getClauses().iterator().next();
                if (c.size() == 1) return c.getLiterals().iterator().next();
                return c;
            }
            return optCnf;
        } catch (TautologyException e) {
            return Literal.TRUE;
        } catch (ContradictionException e) {
            return Literal.FALSE;
        }
    }

    public static Sentence cnfAndGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.AND);
    }

    public static Sentence genericAndGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.AND);
    }
    //-- AND end

    //-- NegatedAND start
    public static Sentence literalNegatedAndLiteral(Sentence left, Sentence right) {
        Literal l1 = (Literal) left;
        Literal l2 = (Literal) right;

        if (l1 == Literal.FALSE || l2 == Literal.FALSE) return Literal.TRUE;
        if (l1 == Literal.TRUE && l2 == Literal.TRUE) return Literal.FALSE;
        if (l1.equals(l2)) return new Literal(l1.getName(), !l1.isNegated());
        if (l1.equalsIgnoreNegation(l2)) return Literal.TRUE;

        return new GenericComplexSentence(l1, l2, Connective.AND, true);
    }

    public static Sentence literalNegatedAndClause(Sentence left, Sentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        Clause clause = (Clause) (left.type() == CLAUSE ? left : right);

        if (literal == Literal.FALSE) return Literal.TRUE;
        if (literal == Literal.TRUE) return new GenericComplexSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + clause + SentenceUtils.CLOSING_PARENTHESES);

        try {
            CNFSentence cnf = Sentences.optimizeCNF(new CNFSentence(new Clause(literal), clause));
            if (cnf.size() == 1) {
                Clause c = cnf.getClauses().iterator().next();
                if (c.size() == 1) {
                    Literal l = c.getLiterals().iterator().next();
                    return new Literal(l.getName(), !l.isNegated());
                } else if (c.size() == 2) {
                    List<Literal> literals = c.getLiteralList();
                    Literal l1 = literals.get(0);
                    Literal l2 = literals.get(1);
                    return new GenericComplexSentence(l1, l2, Connective.OR, true);
                }
            } else if (cnf.size() == 2) {
                List<Clause> clauses = cnf.getClauseList();
                Clause clause1 = clauses.get(0);
                Clause clause2 = clauses.get(1);

                Literal literal1 = clause1.size() == 1 ? clause1.getLiterals().iterator().next() : null;
                Literal literal2 = clause2.size() == 1 ? clause2.getLiterals().iterator().next() : null;

                if (literal1 == null || literal2 == null) return
                        new GenericComplexSentence(clause1, clause2, Connective.AND, true);

                new GenericComplexSentence(literal1, literal2, Connective.AND, true);
            }

            return new GenericComplexSentence(left, right, Connective.AND, true);
        } catch (TautologyException e) {
            return Literal.FALSE;
        } catch (ContradictionException e) {
            return Literal.TRUE;
        }
    }

    public static Sentence literalNegatedAndCNF(Sentence left, Sentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        CNFSentence cnf = (CNFSentence) (left.type() == CNF ? left : right);

        if (literal == Literal.FALSE) return Literal.TRUE;
        if (literal == Literal.TRUE) {
            if (cnf.size() == 1) {
                Clause c = cnf.getClauses().iterator().next();
                if (c.size() == 1) {
                    Literal l = c.getLiterals().iterator().next();
                    return new Literal(l.getName(), !l.isNegated());
                } else if (c.size() == 2) {
                    List<Literal> literals = c.getLiteralList();
                    Literal l1 = literals.get(0);
                    Literal l2 = literals.get(1);
                    return new GenericComplexSentence(l1, l2, Connective.OR, true);
                }
            } else if (cnf.size() == 2) {
                List<Clause> clauses = cnf.getClauseList();
                Clause clause1 = clauses.get(0);
                Clause clause2 = clauses.get(1);

                Literal literal1 = clause1.size() == 1 ? clause1.getLiterals().iterator().next() : null;
                Literal literal2 = clause2.size() == 1 ? clause2.getLiterals().iterator().next() : null;

                if (literal1 == null || literal2 == null) return
                        new GenericComplexSentence(clause1, clause2, Connective.AND, true);

                new GenericComplexSentence(literal1, literal2, Connective.AND, true);
            }

            return new GenericComplexSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + cnf + SentenceUtils.CLOSING_PARENTHESES);
        }

        LinkedHashSet<Clause> clauses = cnf.getClauses();
        clauses.add(new Clause(literal));

        try {
            CNFSentence optCnf = Sentences.optimizeCNF(new CNFSentence(clauses));
            if (optCnf.size() == 1) {
                Clause c = optCnf.getClauses().iterator().next();
                if (c.size() == 1) {
                    Literal l = c.getLiterals().iterator().next();
                    return new Literal(l.getName(), !l.isNegated());
                } else if (c.size() == 2) {
                    List<Literal> literals = c.getLiteralList();
                    Literal l1 = literals.get(0);
                    Literal l2 = literals.get(1);
                    return new GenericComplexSentence(l1, l2, Connective.OR, true);
                }
            } else if (optCnf.size() == 2) {
                List<Clause> clauseList = optCnf.getClauseList();
                Clause clause1 = clauseList.get(0);
                Clause clause2 = clauseList.get(1);

                Literal literal1 = clause1.size() == 1 ? clause1.getLiterals().iterator().next() : null;
                Literal literal2 = clause2.size() == 1 ? clause2.getLiterals().iterator().next() : null;

                if (literal1 == null || literal2 == null) return
                        new GenericComplexSentence(clause1, clause2, Connective.AND, true);

                new GenericComplexSentence(literal1, literal2, Connective.AND, true);
            }

            return new GenericComplexSentence(left, right, Connective.AND, true);
        } catch (TautologyException e) {
            return Literal.FALSE;
        } catch (ContradictionException e) {
            return Literal.TRUE;
        }
    }

    public static Sentence literalNegatedAndGeneric(Sentence left, Sentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        GenericComplexSentence generic = (GenericComplexSentence) (left.type() == GENERIC_COMPLEX ? left : right);

        if (literal == Literal.FALSE) return Literal.TRUE;
        if (literal == Literal.TRUE) return new GenericComplexSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + generic + SentenceUtils.CLOSING_PARENTHESES);

        return new GenericComplexSentence(left, right, Connective.AND, true);
    }

    public static Sentence clauseNegatedAndClause(Sentence left, Sentence right) throws ParseException {
        Clause clause1 = (Clause) left;
        Clause clause2 = (Clause) right;

        if (clause1.equals(clause2)) return new GenericComplexSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + clause1 + SentenceUtils.CLOSING_PARENTHESES);

        try {
            CNFSentence cnf = Sentences.optimizeCNF(new CNFSentence(clause1, clause2));
            if (cnf.size() == 1) {
                Clause c = cnf.getClauses().iterator().next();
                if (c.size() == 1) {
                    Literal l = c.getLiterals().iterator().next();
                    return new Literal(l.getName(), !l.isNegated());
                } else if (c.size() == 2) {
                    List<Literal> literals = c.getLiteralList();
                    Literal l1 = literals.get(0);
                    Literal l2 = literals.get(1);
                    return new GenericComplexSentence(l1, l2, Connective.OR, true);
                }
            } else if (cnf.size() == 2) {
                List<Clause> clauses = cnf.getClauseList();
                Clause c1 = clauses.get(0);
                Clause c2 = clauses.get(1);

                Literal literal1 = c1.size() == 1 ? c1.getLiterals().iterator().next() : null;
                Literal literal2 = c2.size() == 1 ? c2.getLiterals().iterator().next() : null;

                if (literal1 == null || literal2 == null) return
                        new GenericComplexSentence(c1, c2, Connective.AND, true);

                new GenericComplexSentence(literal1, literal2, Connective.AND, true);
            }

            return new GenericComplexSentence(left, right, Connective.AND, true);
        } catch (TautologyException e) {
            return Literal.FALSE;
        } catch (ContradictionException e) {
            return Literal.TRUE;
        }
    }

    public static Sentence clauseNegatedAndCNF(Sentence left, Sentence right) {
        Clause clause = (Clause) (left.type() == CLAUSE ? left : right);
        CNFSentence cnf = (CNFSentence) (left.type() == CNF ? left : right);

        LinkedHashSet<Clause> clauses = cnf.getClauses();
        clauses.add(clause);

        try {
            CNFSentence optCnf = Sentences.optimizeCNF(new CNFSentence(clauses));
            if (optCnf.size() == 1) {
                Clause c = optCnf.getClauses().iterator().next();
                if (c.size() == 1) {
                    Literal l = c.getLiterals().iterator().next();
                    return new Literal(l.getName(), !l.isNegated());
                } else if (c.size() == 2) {
                    List<Literal> literals = c.getLiteralList();
                    Literal l1 = literals.get(0);
                    Literal l2 = literals.get(1);
                    return new GenericComplexSentence(l1, l2, Connective.OR, true);
                }
            } else if (optCnf.size() == 2) {
                List<Clause> clauseList = optCnf.getClauseList();
                Clause clause1 = clauseList.get(0);
                Clause clause2 = clauseList.get(1);

                Literal literal1 = clause1.size() == 1 ? clause1.getLiterals().iterator().next() : null;
                Literal literal2 = clause2.size() == 1 ? clause2.getLiterals().iterator().next() : null;

                if (literal1 == null || literal2 == null) return
                        new GenericComplexSentence(clause1, clause2, Connective.AND, true);

                new GenericComplexSentence(literal1, literal2, Connective.AND, true);
            }

            return new GenericComplexSentence(left, right, Connective.AND, true);
        } catch (TautologyException e) {
            return Literal.FALSE;
        } catch (ContradictionException e) {
            return Literal.TRUE;
        }
    }

    public static Sentence clauseNegatedAndGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.AND, true);
    }

    public static Sentence cnfNegatedAndCNF(Sentence left, Sentence right) throws ParseException {
        CNFSentence cnf1 = (CNFSentence) left;
        CNFSentence cnf2 = (CNFSentence) right;

        if (cnf1.equals(cnf2)) {
            if (cnf1.size() == 1) {
                Clause c = cnf1.getClauses().iterator().next();
                if (c.size() == 1) {
                    Literal l = c.getLiterals().iterator().next();
                    return new Literal(l.getName(), !l.isNegated());
                } else if (c.size() == 2) {
                    List<Literal> literals = c.getLiteralList();
                    Literal l1 = literals.get(0);
                    Literal l2 = literals.get(1);
                    return new GenericComplexSentence(l1, l2, Connective.OR, true);
                }
            } else if (cnf1.size() == 2) {
                List<Clause> clauses = cnf1.getClauseList();
                Clause c1 = clauses.get(0);
                Clause c2 = clauses.get(1);

                Literal literal1 = c1.size() == 1 ? c1.getLiterals().iterator().next() : null;
                Literal literal2 = c2.size() == 1 ? c2.getLiterals().iterator().next() : null;

                if (literal1 == null || literal2 == null) return
                        new GenericComplexSentence(c1, c2, Connective.AND, true);

                new GenericComplexSentence(literal1, literal2, Connective.AND, true);
            }

            return new GenericComplexSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + cnf1 + SentenceUtils.CLOSING_PARENTHESES);
        }

        LinkedHashSet<Clause> clauses = new LinkedHashSet<>();
        clauses.addAll(cnf1.getClauses());
        clauses.addAll(cnf2.getClauses());

        try {
            CNFSentence optCnf = Sentences.optimizeCNF(new CNFSentence(clauses));
            if (optCnf.size() == 1) {
                Clause c = optCnf.getClauses().iterator().next();
                if (c.size() == 1) {
                    Literal l = c.getLiterals().iterator().next();
                    return new Literal(l.getName(), !l.isNegated());
                } else if (c.size() == 2) {
                    List<Literal> literals = c.getLiteralList();
                    Literal l1 = literals.get(0);
                    Literal l2 = literals.get(1);
                    return new GenericComplexSentence(l1, l2, Connective.OR, true);
                }
            } else if (optCnf.size() == 2) {
                List<Clause> clauseList = optCnf.getClauseList();
                Clause clause1 = clauseList.get(0);
                Clause clause2 = clauseList.get(1);

                Literal literal1 = clause1.size() == 1 ? clause1.getLiterals().iterator().next() : null;
                Literal literal2 = clause2.size() == 1 ? clause2.getLiterals().iterator().next() : null;

                if (literal1 == null || literal2 == null) return
                        new GenericComplexSentence(clause1, clause2, Connective.AND, true);

                new GenericComplexSentence(literal1, literal2, Connective.AND, true);
            }

            return new GenericComplexSentence(left, right, Connective.AND, true);
        } catch (TautologyException e) {
            return Literal.FALSE;
        } catch (ContradictionException e) {
            return Literal.TRUE;
        }
    }

    public static Sentence cnfNegatedAndGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.AND, true);
    }

    public static Sentence genericNegatedAndGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.AND, true);
    }
    //-- NegatedAND end

    //-- IMPLICATION start
    public static Sentence literalImplicationLiteral(Sentence left, Sentence right) {
        Literal literalLeft = (Literal) left;
        Literal literalRight = (Literal) right;

        if (literalLeft == Literal.FALSE) return Literal.TRUE;
        if (literalLeft == Literal.TRUE && literalRight == Literal.FALSE) return Literal.FALSE;
        if (literalLeft.equals(literalRight)) return Literal.TRUE;
        if (literalLeft.equalsIgnoreNegation(literalRight)) return literalRight;
        if (literalLeft == Literal.TRUE) return literalRight;
        if (literalRight == Literal.FALSE) return new Literal(literalLeft.getName(), !literalLeft.isNegated());
        if (literalRight == Literal.TRUE) return Literal.TRUE;

        return new GenericComplexSentence(left, right, Connective.IMPLICATION);
    }

    public static Sentence literalImplicationClause(Sentence left, Sentence right) throws ParseException {
        if (right.type() == LITERAL) {
            Literal literal = (Literal) right;
            if (literal == Literal.TRUE) return Literal.TRUE;
            if (literal == Literal.FALSE) return new GenericComplexSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + left + SentenceUtils.CLOSING_PARENTHESES);

        } else {
            Literal literal = (Literal) left;
            if (literal == Literal.TRUE) return right;
            if (literal == Literal.FALSE) return Literal.TRUE;
            if (((Clause) right).getLiterals().contains(literal)) return Literal.TRUE;
            if (((Clause) right).getLiterals().contains(new Literal(literal.getName(), !literal.isNegated()))) return right;
        }
        return new GenericComplexSentence(left, right, Connective.IMPLICATION);
    }

    public static Sentence literalImplicationCNF(Sentence left, Sentence right) throws ParseException {
        if (right.type() == LITERAL) {
            Literal literal = (Literal) right;
            if (literal == Literal.TRUE) return Literal.TRUE;
            if (literal == Literal.FALSE) return new GenericComplexSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + left + SentenceUtils.CLOSING_PARENTHESES);

        } else {
            Literal literal = (Literal) left;
            if (literal == Literal.TRUE) return right;
            if (literal == Literal.FALSE) return Literal.TRUE;
            if (((CNFSentence) right).getClauses().stream()
                    .allMatch(c -> c.getLiterals().contains(literal))) return Literal.TRUE;
        }
        return new GenericComplexSentence(left, right, Connective.IMPLICATION);
    }

    public static Sentence literalImplicationGeneric(Sentence left, Sentence right) throws ParseException {
        if (right.type() == LITERAL) {
            Literal literal = (Literal) right;
            if (literal == Literal.TRUE) return Literal.TRUE;
            if (literal == Literal.FALSE) return new GenericComplexSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + left + SentenceUtils.CLOSING_PARENTHESES);
        } else {
            Literal literal = (Literal) left;
            if (literal == Literal.TRUE) return right;
            if (literal == Literal.FALSE) return Literal.TRUE;
        }
        return new GenericComplexSentence(left, right, Connective.IMPLICATION);
    }

    public static Sentence clauseImplicationClause(Sentence left, Sentence right) {
        if (left.equals(right)) return Literal.TRUE;
        return new GenericComplexSentence(left, right, Connective.IMPLICATION);
    }

    public static Sentence clauseImplicationCNF(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.IMPLICATION);
    }

    public static Sentence clauseImplicationGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.IMPLICATION);
    }

    public static Sentence cnfImplicationCNF(Sentence left, Sentence right) {
        if (left.equals(right)) return Literal.TRUE;
        return new GenericComplexSentence(left, right, Connective.IMPLICATION);
    }

    public static Sentence cnfImplicationGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.IMPLICATION);
    }

    public static Sentence genericImplicationGeneric(Sentence left, Sentence right) {
        if (left.equals(right)) return Literal.TRUE;
        return new GenericComplexSentence(left, right, Connective.IMPLICATION);
    }
    //-- IMPLICATION end

    //-- NegatedIMPLICATION start
    public static Sentence literalNegatedImplicationLiteral(Sentence left, Sentence right) {
        Literal literalLeft = (Literal) left;
        Literal literalRight = (Literal) right;

        if (literalLeft == Literal.FALSE) return Literal.FALSE;
        if (literalLeft == Literal.TRUE && literalRight == Literal.FALSE) return Literal.TRUE;
        if (literalLeft.equals(literalRight)) return Literal.FALSE;
        if (literalLeft.equalsIgnoreNegation(literalRight)) return new Literal(literalRight.getName(), !literalRight.isNegated());
        if (literalLeft == Literal.TRUE) return new Literal(literalRight.getName(), !literalRight.isNegated());
        if (literalRight == Literal.FALSE) return literalLeft;
        if (literalRight == Literal.TRUE) return Literal.FALSE;

        return new GenericComplexSentence(left, right, Connective.IMPLICATION, true);
    }

    public static Sentence literalNegatedImplicationClause(Sentence left, Sentence right) throws ParseException {
        if (right.type() == LITERAL) {
            Literal literal = (Literal) right;
            if (literal == Literal.TRUE) return Literal.FALSE;
            if (literal == Literal.FALSE) return left;
        } else {
            Literal literal = (Literal) left;
            if (literal == Literal.TRUE) return new GenericComplexSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + right + SentenceUtils.CLOSING_PARENTHESES);
            if (literal == Literal.FALSE) return Literal.FALSE;
            if (((Clause) right).getLiterals().contains(literal)) return Literal.FALSE;
            if (((Clause) right).getLiterals().contains(new Literal(literal.getName(), !literal.isNegated()))) {
                return new GenericComplexSentence(SentenceUtils.NOT +
                        SentenceUtils.OPENING_PARENTHESES + right + SentenceUtils.CLOSING_PARENTHESES);
            }
        }
        return new GenericComplexSentence(left, right, Connective.IMPLICATION, true);
    }

    public static Sentence literalNegatedImplicationCNF(Sentence left, Sentence right) throws ParseException {
        if (right.type() == LITERAL) {
            Literal literal = (Literal) right;
            if (literal == Literal.TRUE) return Literal.FALSE;
            if (literal == Literal.FALSE) return left;
        } else {
            Literal literal = (Literal) left;
            if (literal == Literal.TRUE) return new GenericComplexSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + right + SentenceUtils.CLOSING_PARENTHESES);
            if (literal == Literal.FALSE) return Literal.FALSE;
            if (((CNFSentence) right).getClauses().stream()
                    .allMatch(c -> c.getLiterals().contains(literal))) return Literal.FALSE;
        }
        return new GenericComplexSentence(left, right, Connective.IMPLICATION, true);
    }

    public static Sentence literalNegatedImplicationGeneric(Sentence left, Sentence right) throws ParseException {
        if (right.type() == LITERAL) {
            Literal literal = (Literal) right;
            if (literal == Literal.TRUE) return Literal.FALSE;
            if (literal == Literal.FALSE) return left;
        } else {
            Literal literal = (Literal) left;
            if (literal == Literal.TRUE) return new GenericComplexSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + right + SentenceUtils.CLOSING_PARENTHESES);
            if (literal == Literal.FALSE) return Literal.FALSE;
        }
        return new GenericComplexSentence(left, right, Connective.IMPLICATION, true);
    }

    public static Sentence clauseNegatedImplicationClause(Sentence left, Sentence right) throws ParseException {
        if (left.equals(right)) return Literal.FALSE;
        return new GenericComplexSentence(left, right, Connective.IMPLICATION, true);
    }

    public static Sentence clauseNegatedImplicationCNF(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.IMPLICATION, true);
    }

    public static Sentence clauseNegatedImplicationGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.IMPLICATION, true);
    }

    public static Sentence cnfNegatedImplicationCNF(Sentence left, Sentence right) throws ParseException {
        if (left.equals(right)) return Literal.FALSE;
        return new GenericComplexSentence(left, right, Connective.IMPLICATION, true);
    }

    public static Sentence cnfNegatedImplicationGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.IMPLICATION, true);
    }

    public static Sentence genericNegatedImplicationGeneric(Sentence left, Sentence right) {
        if (left.equals(right)) return Literal.FALSE;
        return new GenericComplexSentence(left, right, Connective.IMPLICATION, true);
    }
    //-- NegatedIMPLICATION end

    //-- BICONDITIONAL start
    public static Sentence literalBiconditionalLiteral(Sentence left, Sentence right) {
        Literal leftLiteral = (Literal) left;
        Literal rightLiteral = (Literal) right;

        if (leftLiteral.equals(rightLiteral)) return Literal.TRUE;
        if (leftLiteral.equalsIgnoreNegation(rightLiteral)) return Literal.FALSE;
        if (leftLiteral == Literal.TRUE && rightLiteral == Literal.FALSE) return Literal.FALSE;
        if (leftLiteral == Literal.FALSE && rightLiteral == Literal.TRUE) return Literal.FALSE;
        if (leftLiteral == Literal.TRUE) return rightLiteral;
        if (leftLiteral == Literal.FALSE) return new Literal(rightLiteral.getName(), !rightLiteral.isNegated());
        if (rightLiteral == Literal.TRUE) return leftLiteral;
        if (rightLiteral == Literal.FALSE) return new Literal(leftLiteral.getName(), !leftLiteral.isNegated());

        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL);
    }

    public static Sentence literalBiconditionalClause(Sentence left, Sentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        Clause clause = (Clause) (left.type() == CLAUSE ? left : right);

        if (literal == Literal.TRUE) return clause;
        if (literal == Literal.FALSE) return new GenericComplexSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + clause + SentenceUtils.CLOSING_PARENTHESES);

        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL);
    }

    public static Sentence literalBiconditionalCNF(Sentence left, Sentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        CNFSentence cnf = (CNFSentence) (left.type() == CNF ? left : right);

        if (literal == Literal.TRUE) return cnf;
        if (literal == Literal.FALSE) return new GenericComplexSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + cnf + SentenceUtils.CLOSING_PARENTHESES);

        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL);
    }

    public static Sentence literalBiconditionalGeneric(Sentence left, Sentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        GenericComplexSentence generic = (GenericComplexSentence) (left.type() == GENERIC_COMPLEX ? left : right);

        if (literal == Literal.TRUE) return generic;
        if (literal == Literal.FALSE) return new GenericComplexSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + generic + SentenceUtils.CLOSING_PARENTHESES);

        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL);
    }

    public static Sentence clauseBiconditionalClause(Sentence left, Sentence right) throws ParseException {
        Clause clause1 = (Clause) left;
        Clause clause2 = (Clause) right;

        if (clause1.equals(clause2)) return Literal.TRUE;
        if (Sentence.isEquivalent(clause1, new GenericComplexSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + clause2 + SentenceUtils.CLOSING_PARENTHESES))) return Literal.FALSE;

        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL);
    }

    public static Sentence clauseBiconditionalCNF(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL);
    }

    public static Sentence clauseBiconditionalGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL);
    }

    public static Sentence cnfBiconditionalCNF(Sentence left, Sentence right) throws ParseException {
        CNFSentence cnf1 = (CNFSentence) left;
        CNFSentence cnf2 = (CNFSentence) right;

        if (cnf1.equals(cnf2)) return Literal.TRUE;
        if (Sentence.isEquivalent(cnf1, new GenericComplexSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + cnf2 + SentenceUtils.CLOSING_PARENTHESES))) return Literal.FALSE;

        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL);
    }

    public static Sentence cnfBiconditionalGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL);
    }

    public static Sentence genericBiconditionalGeneric(Sentence left, Sentence right) throws ParseException {
        GenericComplexSentence generic1 = (GenericComplexSentence) left;
        GenericComplexSentence generic2 = (GenericComplexSentence) right;

        if (generic1.equals(generic2)) return Literal.TRUE;
        if (generic1.equals(new GenericComplexSentence(SentenceUtils.NOT + generic2.toString()))) return Literal.FALSE;

        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL);
    }
    //-- BICONDITIONAL end

    //-- NegatedBICONDITIONAL start
    public static Sentence literalNegatedBiconditionalLiteral(Sentence left, Sentence right) {
        Literal leftLiteral = (Literal) left;
        Literal rightLiteral = (Literal) right;

        if (leftLiteral.equals(rightLiteral)) return Literal.FALSE;
        if (leftLiteral.equalsIgnoreNegation(rightLiteral)) return Literal.TRUE;
        if (leftLiteral == Literal.TRUE && rightLiteral == Literal.FALSE) return Literal.TRUE;
        if (leftLiteral == Literal.FALSE && rightLiteral == Literal.TRUE) return Literal.TRUE;
        if (leftLiteral == Literal.TRUE) return new Literal(rightLiteral.getName(), !rightLiteral.isNegated());
        if (leftLiteral == Literal.FALSE) return rightLiteral;
        if (rightLiteral == Literal.TRUE) return new Literal(leftLiteral.getName(), !leftLiteral.isNegated());
        if (rightLiteral == Literal.FALSE) return leftLiteral;

        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL, true);
    }

    public static Sentence literalNegatedBiconditionalClause(Sentence left, Sentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        Clause clause = (Clause) (left.type() == CLAUSE ? left : right);

        if (literal == Literal.FALSE) return clause;
        if (literal == Literal.TRUE) return new GenericComplexSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + clause + SentenceUtils.CLOSING_PARENTHESES);

        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL, true);
    }

    public static Sentence literalNegatedBiconditionalCNF(Sentence left, Sentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        CNFSentence cnf = (CNFSentence) (left.type() == CNF ? left : right);

        if (literal == Literal.FALSE) return cnf;
        if (literal == Literal.TRUE) return new GenericComplexSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + cnf + SentenceUtils.CLOSING_PARENTHESES);

        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL, true);
    }

    public static Sentence literalNegatedBiconditionalGeneric(Sentence left, Sentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        GenericComplexSentence generic = (GenericComplexSentence) (left.type() == GENERIC_COMPLEX ? left : right);

        if (literal == Literal.FALSE) return generic;
        if (literal == Literal.TRUE) return new GenericComplexSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + generic + SentenceUtils.CLOSING_PARENTHESES);

        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL, true);
    }

    public static Sentence clauseNegatedBiconditionalClause(Sentence left, Sentence right) throws ParseException {
        Clause clause1 = (Clause) left;
        Clause clause2 = (Clause) right;

        if (clause1.equals(clause2)) return Literal.FALSE;
        if (Sentence.isEquivalent(clause1, new GenericComplexSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + clause2 + SentenceUtils.CLOSING_PARENTHESES))) return Literal.TRUE;

        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL, true);
    }

    public static Sentence clauseNegatedBiconditionalCNF(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL, true);
    }

    public static Sentence clauseNegatedBiconditionalGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL, true);
    }

    public static Sentence cnfNegatedBiconditionalCNF(Sentence left, Sentence right) throws ParseException {
        CNFSentence cnf1 = (CNFSentence) left;
        CNFSentence cnf2 = (CNFSentence) right;

        if (cnf1.equals(cnf2)) return Literal.FALSE;
        if (Sentence.isEquivalent(cnf1, new GenericComplexSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + cnf2 + SentenceUtils.CLOSING_PARENTHESES))) return Literal.TRUE;

        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL, true);
    }

    public static Sentence cnfNegatedBiconditionalGeneric(Sentence left, Sentence right) {
        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL, true);
    }

    public static Sentence genericNegatedBiconditionalGeneric(Sentence left, Sentence right) throws ParseException {
        GenericComplexSentence generic1 = (GenericComplexSentence) left;
        GenericComplexSentence generic2 = (GenericComplexSentence) right;

        if (generic1.equals(generic2)) return Literal.FALSE;
        if (generic1.equals(new GenericComplexSentence(SentenceUtils.NOT + generic2.toString()))) return Literal.TRUE;

        return new GenericComplexSentence(left, right, Connective.BICONDITIONAL, true);
    }
    //-- NegatedBICONDITIONAL end
}
