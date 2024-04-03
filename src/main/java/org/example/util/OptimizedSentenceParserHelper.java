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
}
