package org.example.util;

import org.example.domain.Connective;
import org.example.domain.Sentences;
import org.example.domain.sentence.propositional.*;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;

import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.List;

import static org.example.domain.PropositionalSentenceType.*;

/**
 * @author aram.azatyan | 4/3/2024 3:00 PM
 */
public class OptimizedPropositionalSentenceParserHelper {

    //-- OR start
    public static PropositionalSentence literalOrLiteral(PropositionalSentence left, PropositionalSentence right) {
        Literal l1 = (Literal) left;
        Literal l2 = (Literal) right;

        if (l1 == Literal.TRUE || l2 == Literal.TRUE) return Literal.TRUE;
        if (l1.equals(l2)) return l1;
        if (l1.equalsIgnoreNegation(l2)) return Literal.TRUE;
        return new PropositionalClause(l1, l2);
    }

    public static PropositionalSentence literalOrClause(PropositionalSentence left, PropositionalSentence right) {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        PropositionalClause clause = (PropositionalClause) (left.type() == CLAUSE ? left : right);

        if (literal == Literal.TRUE) return Literal.TRUE;
        if (literal == Literal.FALSE) return clause;

        LinkedHashSet<Literal> literals = clause.getLiterals();
        literals.add(literal);

        try {
            PropositionalClause optClause = (PropositionalClause) Sentences.optimizeClause(new PropositionalClause(literals));
            return optClause.size() == 1 ? optClause.getLiterals().iterator().next() : optClause;
        } catch (TautologyException e) {
            return Literal.TRUE;
        } catch (ContradictionException e) {
            return Literal.FALSE;
        }
    }

    public static PropositionalSentence literalOrCNF(PropositionalSentence left, PropositionalSentence right) {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        PropositionalCNFSentence cnf = (PropositionalCNFSentence) (left.type() == CNF ? left : right);

        if (literal == Literal.TRUE) return Literal.TRUE;
        if (literal == Literal.FALSE) return cnf;

        return new GenericComplexPropositionalSentence(left, right, Connective.OR);
    }

    public static PropositionalSentence literalOrGeneric(PropositionalSentence left, PropositionalSentence right) {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        GenericComplexPropositionalSentence generic = (GenericComplexPropositionalSentence) (left.type() == GENERIC_COMPLEX ? left : right);

        if (literal == Literal.TRUE) return Literal.TRUE;
        if (literal == Literal.FALSE) return generic;

        return new GenericComplexPropositionalSentence(left, right, Connective.OR);
    }

    public static PropositionalSentence clauseOrClause(PropositionalSentence left, PropositionalSentence right) {
        PropositionalClause c1 = (PropositionalClause) left;
        PropositionalClause c2 = (PropositionalClause) right;

        LinkedHashSet<Literal> literals = new LinkedHashSet<>();
        literals.addAll(c1.getLiterals());
        literals.addAll(c2.getLiterals());

        try {
            PropositionalClause optClause = (PropositionalClause) Sentences.optimizeClause(new PropositionalClause(literals));
            return optClause.size() == 1 ? optClause.getLiterals().iterator().next() : optClause;
        } catch (TautologyException e) {
            return Literal.TRUE;
        } catch (ContradictionException e) {
            return Literal.FALSE;
        }
    }

    public static PropositionalSentence clauseOrCNF(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.OR);
    }

    public static PropositionalSentence clauseOrGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.OR);
    }

    public static PropositionalSentence cnfOrCNF(PropositionalSentence left, PropositionalSentence right) {
        PropositionalCNFSentence cnfLeft = (PropositionalCNFSentence) left;
        PropositionalCNFSentence cnfRight = (PropositionalCNFSentence) right;

        if (cnfLeft.equals(cnfRight)) return cnfLeft;
        return new GenericComplexPropositionalSentence(left, right, Connective.OR);
    }

    public static PropositionalSentence cnfOrGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.OR);
    }

    public static PropositionalSentence genericOrGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.OR);
    }
    //-- OR end

    //-- NegatedOR start
    public static PropositionalSentence literalNegatedOrLiteral(PropositionalSentence left, PropositionalSentence right) {
        Literal l1 = (Literal) left;
        Literal l2 = (Literal) right;

        if (l1 == Literal.TRUE || l2 == Literal.TRUE) return Literal.FALSE;
        if (l1 == Literal.FALSE && l2 == Literal.FALSE) return Literal.TRUE;

        if (l1.equals(l2)) return new Literal(l1.getName(), !l1.isNegated());
        if (l1.equalsIgnoreNegation(l2)) return Literal.FALSE;

        return new GenericComplexPropositionalSentence(left, right, Connective.OR, true);
    }

    public static PropositionalSentence literalNegatedOrClause(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        PropositionalClause clause = (PropositionalClause) (left.type() == CLAUSE ? left : right);

        if (literal == Literal.TRUE) return Literal.FALSE;
        if (literal == Literal.FALSE) {
            if (clause.size() == 2) {
                List<Literal> literals = clause.getLiteralList();
                return new GenericComplexPropositionalSentence(literals.get(0), literals.get(1),
                        Connective.OR, true);
            }
            return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + clause + SentenceUtils.CLOSING_PARENTHESES);
        }

        LinkedHashSet<Literal> literals = clause.getLiterals();
        literals.add(literal);

        try {
            PropositionalClause optClause = (PropositionalClause) Sentences.optimizeClause(new PropositionalClause(literals));
            if (optClause.size() == 1) {
                Literal l = optClause.getLiterals().iterator().next();
                return new Literal(l.getName(), !l.isNegated());
            } else if (optClause.size() == 2) {
                List<Literal> literalList = optClause.getLiteralList();
                return new GenericComplexPropositionalSentence(literalList.get(0), literalList.get(1),
                        Connective.OR, true);
            }
            return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + optClause + SentenceUtils.CLOSING_PARENTHESES);
        } catch (TautologyException e) {
            return Literal.FALSE;
        } catch (ContradictionException e) {
            return Literal.TRUE;
        }
    }

    public static PropositionalSentence literalNegatedOrCNF(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        PropositionalCNFSentence cnf = (PropositionalCNFSentence) (left.type() == CNF ? left : right);

        if (literal == Literal.TRUE) return Literal.FALSE;
        if (literal == Literal.FALSE) {
            return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + cnf + SentenceUtils.CLOSING_PARENTHESES);
        }

        return new GenericComplexPropositionalSentence(left, right, Connective.OR, true);
    }

    public static PropositionalSentence literalNegatedOrGeneric(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        GenericComplexPropositionalSentence generic = (GenericComplexPropositionalSentence) (left.type() == GENERIC_COMPLEX ? left : right);

        if (literal == Literal.TRUE) return Literal.FALSE;
        if (literal == Literal.FALSE) return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + generic + SentenceUtils.CLOSING_PARENTHESES);

        return new GenericComplexPropositionalSentence(left, right, Connective.OR, true);
    }

    public static PropositionalSentence clauseNegatedOrClause(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        PropositionalClause c1 = (PropositionalClause) left;
        PropositionalClause c2 = (PropositionalClause) right;

        LinkedHashSet<Literal> literals = new LinkedHashSet<>();
        literals.addAll(c1.getLiterals());
        literals.addAll(c2.getLiterals());

        try {
            PropositionalClause optClause = (PropositionalClause) Sentences.optimizeClause(new PropositionalClause(literals));
            if (optClause.size() == 1) {
                Literal l = optClause.getLiterals().iterator().next();
                return new Literal(l.getName(), !l.isNegated());
            } else if (optClause.size() == 2) {
                List<Literal> literalList = optClause.getLiteralList();
                return new GenericComplexPropositionalSentence(literalList.get(0), literalList.get(1),
                        Connective.OR, true);
            }
            return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + optClause + SentenceUtils.CLOSING_PARENTHESES);
        } catch (TautologyException e) {
            return Literal.FALSE;
        } catch (ContradictionException e) {
            return Literal.TRUE;
        }
    }

    public static PropositionalSentence clauseNegatedOrCNF(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.OR, true);
    }

    public static PropositionalSentence clauseNegatedOrGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.OR, true);
    }

    public static PropositionalSentence cnfNegatedOrCNF(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        PropositionalCNFSentence cnfLeft = (PropositionalCNFSentence) left;
        PropositionalCNFSentence cnfRight = (PropositionalCNFSentence) right;

        if (cnfLeft.equals(cnfRight)) return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + cnfLeft + SentenceUtils.CLOSING_PARENTHESES);
        return new GenericComplexPropositionalSentence(left, right, Connective.OR, true);
    }

    public static PropositionalSentence cnfNegatedOrGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.OR, true);
    }

    public static PropositionalSentence genericNegatedOrGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.OR, true);
    }
    //-- NegatedOR end

    //-- AND start
    public static PropositionalSentence literalAndLiteral(PropositionalSentence left, PropositionalSentence right) {
        Literal l1 = (Literal) left;
        Literal l2 = (Literal) right;

        if (l1 == Literal.FALSE || l2 == Literal.FALSE) return Literal.FALSE;
        if (l1 == Literal.TRUE && l2 == Literal.TRUE) return Literal.TRUE;
        if (l1.equals(l2)) return l1;
        if (l1.equalsIgnoreNegation(l2)) return Literal.FALSE;

        return new PropositionalCNFSentence(new PropositionalClause(l1), new PropositionalClause(l2));
    }

    public static PropositionalSentence literalAndClause(PropositionalSentence left, PropositionalSentence right) {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        PropositionalClause clause = (PropositionalClause) (left.type() == CLAUSE ? left : right);

        if (literal == Literal.FALSE) return Literal.FALSE;
        if (literal == Literal.TRUE) return clause;

        try {
            PropositionalCNFSentence cnf = (PropositionalCNFSentence) Sentences.optimizeCNF(new PropositionalCNFSentence(new PropositionalClause(literal), clause));
            if (cnf.size() == 1) {
                PropositionalClause c = cnf.getClauses().iterator().next();
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

    public static PropositionalSentence literalAndCNF(PropositionalSentence left, PropositionalSentence right) {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        PropositionalCNFSentence cnf = (PropositionalCNFSentence) (left.type() == CNF ? left : right);

        if (literal == Literal.FALSE) return Literal.FALSE;
        if (literal == Literal.TRUE) return cnf;

        try {
            LinkedHashSet<PropositionalClause> clauses = cnf.getClauses();
            clauses.add(new PropositionalClause(literal));
            PropositionalCNFSentence optCnf = (PropositionalCNFSentence) Sentences.optimizeCNF(new PropositionalCNFSentence(clauses));
            if (optCnf.size() == 1) {
                PropositionalClause c = optCnf.getClauses().iterator().next();
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

    public static PropositionalSentence literalAndGeneric(PropositionalSentence left, PropositionalSentence right) {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        GenericComplexPropositionalSentence generic = (GenericComplexPropositionalSentence) (left.type() == GENERIC_COMPLEX ? left : right);

        if (literal == Literal.FALSE) return Literal.FALSE;
        if (literal == Literal.TRUE) return generic;

        return new GenericComplexPropositionalSentence(left, right, Connective.AND);
    }

    public static PropositionalSentence clauseAndClause(PropositionalSentence left, PropositionalSentence right) {
        if (left.equals(right)) return left;
        try {
            PropositionalCNFSentence cnf = (PropositionalCNFSentence) Sentences.optimizeCNF(new PropositionalCNFSentence((PropositionalClause) left, (PropositionalClause) right));
            if (cnf.size() == 1) {
                PropositionalClause c = cnf.getClauses().iterator().next();
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

    public static PropositionalSentence clauseAndCNF(PropositionalSentence left, PropositionalSentence right) {
        PropositionalClause clause = (PropositionalClause) (left.type() == CLAUSE ? left : right);
        PropositionalCNFSentence cnf = (PropositionalCNFSentence) (left.type() == CNF ? left : right);

        LinkedHashSet<PropositionalClause> clauses = cnf.getClauses();
        clauses.add(clause);

        try {
            PropositionalCNFSentence optCnf = (PropositionalCNFSentence) Sentences.optimizeCNF(new PropositionalCNFSentence(clauses));
            if (optCnf.size() == 1) {
                PropositionalClause c = optCnf.getClauses().iterator().next();
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

    public static PropositionalSentence clauseAndGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.AND);
    }

    public static PropositionalSentence cnfAndCNF(PropositionalSentence left, PropositionalSentence right) {
        PropositionalCNFSentence leftCNF = (PropositionalCNFSentence) left;
        PropositionalCNFSentence rightCNF = (PropositionalCNFSentence) right;

        if (leftCNF.equals(rightCNF)) return leftCNF;

        LinkedHashSet<PropositionalClause> clauses = new LinkedHashSet<>();
        clauses.addAll(leftCNF.getClauses());
        clauses.addAll(rightCNF.getClauses());

        try {
            PropositionalCNFSentence optCnf = (PropositionalCNFSentence) Sentences.optimizeCNF(new PropositionalCNFSentence(clauses));
            if (optCnf.size() == 1) {
                PropositionalClause c = optCnf.getClauses().iterator().next();
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

    public static PropositionalSentence cnfAndGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.AND);
    }

    public static PropositionalSentence genericAndGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.AND);
    }
    //-- AND end

    //-- NegatedAND start
    public static PropositionalSentence literalNegatedAndLiteral(PropositionalSentence left, PropositionalSentence right) {
        Literal l1 = (Literal) left;
        Literal l2 = (Literal) right;

        if (l1 == Literal.FALSE || l2 == Literal.FALSE) return Literal.TRUE;
        if (l1 == Literal.TRUE && l2 == Literal.TRUE) return Literal.FALSE;
        if (l1.equals(l2)) return new Literal(l1.getName(), !l1.isNegated());
        if (l1.equalsIgnoreNegation(l2)) return Literal.TRUE;

        return new GenericComplexPropositionalSentence(l1, l2, Connective.AND, true);
    }

    public static PropositionalSentence literalNegatedAndClause(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        PropositionalClause clause = (PropositionalClause) (left.type() == CLAUSE ? left : right);

        if (literal == Literal.FALSE) return Literal.TRUE;
        if (literal == Literal.TRUE) return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + clause + SentenceUtils.CLOSING_PARENTHESES);

        try {
            PropositionalCNFSentence cnf = (PropositionalCNFSentence) Sentences.optimizeCNF(new PropositionalCNFSentence(new PropositionalClause(literal), clause));
            if (cnf.size() == 1) {
                PropositionalClause c = cnf.getClauses().iterator().next();
                if (c.size() == 1) {
                    Literal l = c.getLiterals().iterator().next();
                    return new Literal(l.getName(), !l.isNegated());
                } else if (c.size() == 2) {
                    List<Literal> literals = c.getLiteralList();
                    Literal l1 = literals.get(0);
                    Literal l2 = literals.get(1);
                    return new GenericComplexPropositionalSentence(l1, l2, Connective.OR, true);
                }
            } else if (cnf.size() == 2) {
                List<PropositionalClause> clauses = cnf.getClauseList();
                PropositionalClause clause1 = clauses.get(0);
                PropositionalClause clause2 = clauses.get(1);

                Literal literal1 = clause1.size() == 1 ? clause1.getLiterals().iterator().next() : null;
                Literal literal2 = clause2.size() == 1 ? clause2.getLiterals().iterator().next() : null;

                if (literal1 == null || literal2 == null) return
                        new GenericComplexPropositionalSentence(clause1, clause2, Connective.AND, true);

                new GenericComplexPropositionalSentence(literal1, literal2, Connective.AND, true);
            }

            return new GenericComplexPropositionalSentence(left, right, Connective.AND, true);
        } catch (TautologyException e) {
            return Literal.FALSE;
        } catch (ContradictionException e) {
            return Literal.TRUE;
        }
    }

    public static PropositionalSentence literalNegatedAndCNF(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        PropositionalCNFSentence cnf = (PropositionalCNFSentence) (left.type() == CNF ? left : right);

        if (literal == Literal.FALSE) return Literal.TRUE;
        if (literal == Literal.TRUE) {
            if (cnf.size() == 1) {
                PropositionalClause c = cnf.getClauses().iterator().next();
                if (c.size() == 1) {
                    Literal l = c.getLiterals().iterator().next();
                    return new Literal(l.getName(), !l.isNegated());
                } else if (c.size() == 2) {
                    List<Literal> literals = c.getLiteralList();
                    Literal l1 = literals.get(0);
                    Literal l2 = literals.get(1);
                    return new GenericComplexPropositionalSentence(l1, l2, Connective.OR, true);
                }
            } else if (cnf.size() == 2) {
                List<PropositionalClause> clauses = cnf.getClauseList();
                PropositionalClause clause1 = clauses.get(0);
                PropositionalClause clause2 = clauses.get(1);

                Literal literal1 = clause1.size() == 1 ? clause1.getLiterals().iterator().next() : null;
                Literal literal2 = clause2.size() == 1 ? clause2.getLiterals().iterator().next() : null;

                if (literal1 == null || literal2 == null) return
                        new GenericComplexPropositionalSentence(clause1, clause2, Connective.AND, true);

                new GenericComplexPropositionalSentence(literal1, literal2, Connective.AND, true);
            }

            return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + cnf + SentenceUtils.CLOSING_PARENTHESES);
        }

        LinkedHashSet<PropositionalClause> clauses = cnf.getClauses();
        clauses.add(new PropositionalClause(literal));

        try {
            PropositionalCNFSentence optCnf = (PropositionalCNFSentence) Sentences.optimizeCNF(new PropositionalCNFSentence(clauses));
            if (optCnf.size() == 1) {
                PropositionalClause c = optCnf.getClauses().iterator().next();
                if (c.size() == 1) {
                    Literal l = c.getLiterals().iterator().next();
                    return new Literal(l.getName(), !l.isNegated());
                } else if (c.size() == 2) {
                    List<Literal> literals = c.getLiteralList();
                    Literal l1 = literals.get(0);
                    Literal l2 = literals.get(1);
                    return new GenericComplexPropositionalSentence(l1, l2, Connective.OR, true);
                }
            } else if (optCnf.size() == 2) {
                List<PropositionalClause> clauseList = optCnf.getClauseList();
                PropositionalClause clause1 = clauseList.get(0);
                PropositionalClause clause2 = clauseList.get(1);

                Literal literal1 = clause1.size() == 1 ? clause1.getLiterals().iterator().next() : null;
                Literal literal2 = clause2.size() == 1 ? clause2.getLiterals().iterator().next() : null;

                if (literal1 == null || literal2 == null) return
                        new GenericComplexPropositionalSentence(clause1, clause2, Connective.AND, true);

                new GenericComplexPropositionalSentence(literal1, literal2, Connective.AND, true);
            }

            return new GenericComplexPropositionalSentence(left, right, Connective.AND, true);
        } catch (TautologyException e) {
            return Literal.FALSE;
        } catch (ContradictionException e) {
            return Literal.TRUE;
        }
    }

    public static PropositionalSentence literalNegatedAndGeneric(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        GenericComplexPropositionalSentence generic = (GenericComplexPropositionalSentence) (left.type() == GENERIC_COMPLEX ? left : right);

        if (literal == Literal.FALSE) return Literal.TRUE;
        if (literal == Literal.TRUE) return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + generic + SentenceUtils.CLOSING_PARENTHESES);

        return new GenericComplexPropositionalSentence(left, right, Connective.AND, true);
    }

    public static PropositionalSentence clauseNegatedAndClause(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        PropositionalClause clause1 = (PropositionalClause) left;
        PropositionalClause clause2 = (PropositionalClause) right;

        if (clause1.equals(clause2)) return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + clause1 + SentenceUtils.CLOSING_PARENTHESES);

        try {
            PropositionalCNFSentence cnf = (PropositionalCNFSentence) Sentences.optimizeCNF(new PropositionalCNFSentence(clause1, clause2));
            if (cnf.size() == 1) {
                PropositionalClause c = cnf.getClauses().iterator().next();
                if (c.size() == 1) {
                    Literal l = c.getLiterals().iterator().next();
                    return new Literal(l.getName(), !l.isNegated());
                } else if (c.size() == 2) {
                    List<Literal> literals = c.getLiteralList();
                    Literal l1 = literals.get(0);
                    Literal l2 = literals.get(1);
                    return new GenericComplexPropositionalSentence(l1, l2, Connective.OR, true);
                }
            } else if (cnf.size() == 2) {
                List<PropositionalClause> clauses = cnf.getClauseList();
                PropositionalClause c1 = clauses.get(0);
                PropositionalClause c2 = clauses.get(1);

                Literal literal1 = c1.size() == 1 ? c1.getLiterals().iterator().next() : null;
                Literal literal2 = c2.size() == 1 ? c2.getLiterals().iterator().next() : null;

                if (literal1 == null || literal2 == null) return
                        new GenericComplexPropositionalSentence(c1, c2, Connective.AND, true);

                new GenericComplexPropositionalSentence(literal1, literal2, Connective.AND, true);
            }

            return new GenericComplexPropositionalSentence(left, right, Connective.AND, true);
        } catch (TautologyException e) {
            return Literal.FALSE;
        } catch (ContradictionException e) {
            return Literal.TRUE;
        }
    }

    public static PropositionalSentence clauseNegatedAndCNF(PropositionalSentence left, PropositionalSentence right) {
        PropositionalClause clause = (PropositionalClause) (left.type() == CLAUSE ? left : right);
        PropositionalCNFSentence cnf = (PropositionalCNFSentence) (left.type() == CNF ? left : right);

        LinkedHashSet<PropositionalClause> clauses = cnf.getClauses();
        clauses.add(clause);

        try {
            PropositionalCNFSentence optCnf = (PropositionalCNFSentence) Sentences.optimizeCNF(new PropositionalCNFSentence(clauses));
            if (optCnf.size() == 1) {
                PropositionalClause c = optCnf.getClauses().iterator().next();
                if (c.size() == 1) {
                    Literal l = c.getLiterals().iterator().next();
                    return new Literal(l.getName(), !l.isNegated());
                } else if (c.size() == 2) {
                    List<Literal> literals = c.getLiteralList();
                    Literal l1 = literals.get(0);
                    Literal l2 = literals.get(1);
                    return new GenericComplexPropositionalSentence(l1, l2, Connective.OR, true);
                }
            } else if (optCnf.size() == 2) {
                List<PropositionalClause> clauseList = optCnf.getClauseList();
                PropositionalClause clause1 = clauseList.get(0);
                PropositionalClause clause2 = clauseList.get(1);

                Literal literal1 = clause1.size() == 1 ? clause1.getLiterals().iterator().next() : null;
                Literal literal2 = clause2.size() == 1 ? clause2.getLiterals().iterator().next() : null;

                if (literal1 == null || literal2 == null) return
                        new GenericComplexPropositionalSentence(clause1, clause2, Connective.AND, true);

                new GenericComplexPropositionalSentence(literal1, literal2, Connective.AND, true);
            }

            return new GenericComplexPropositionalSentence(left, right, Connective.AND, true);
        } catch (TautologyException e) {
            return Literal.FALSE;
        } catch (ContradictionException e) {
            return Literal.TRUE;
        }
    }

    public static PropositionalSentence clauseNegatedAndGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.AND, true);
    }

    public static PropositionalSentence cnfNegatedAndCNF(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        PropositionalCNFSentence cnf1 = (PropositionalCNFSentence) left;
        PropositionalCNFSentence cnf2 = (PropositionalCNFSentence) right;

        if (cnf1.equals(cnf2)) {
            if (cnf1.size() == 1) {
                PropositionalClause c = cnf1.getClauses().iterator().next();
                if (c.size() == 1) {
                    Literal l = c.getLiterals().iterator().next();
                    return new Literal(l.getName(), !l.isNegated());
                } else if (c.size() == 2) {
                    List<Literal> literals = c.getLiteralList();
                    Literal l1 = literals.get(0);
                    Literal l2 = literals.get(1);
                    return new GenericComplexPropositionalSentence(l1, l2, Connective.OR, true);
                }
            } else if (cnf1.size() == 2) {
                List<PropositionalClause> clauses = cnf1.getClauseList();
                PropositionalClause c1 = clauses.get(0);
                PropositionalClause c2 = clauses.get(1);

                Literal literal1 = c1.size() == 1 ? c1.getLiterals().iterator().next() : null;
                Literal literal2 = c2.size() == 1 ? c2.getLiterals().iterator().next() : null;

                if (literal1 == null || literal2 == null) return
                        new GenericComplexPropositionalSentence(c1, c2, Connective.AND, true);

                new GenericComplexPropositionalSentence(literal1, literal2, Connective.AND, true);
            }

            return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + cnf1 + SentenceUtils.CLOSING_PARENTHESES);
        }

        LinkedHashSet<PropositionalClause> clauses = new LinkedHashSet<>();
        clauses.addAll(cnf1.getClauses());
        clauses.addAll(cnf2.getClauses());

        try {
            PropositionalCNFSentence optCnf = (PropositionalCNFSentence) Sentences.optimizeCNF(new PropositionalCNFSentence(clauses));
            if (optCnf.size() == 1) {
                PropositionalClause c = optCnf.getClauses().iterator().next();
                if (c.size() == 1) {
                    Literal l = c.getLiterals().iterator().next();
                    return new Literal(l.getName(), !l.isNegated());
                } else if (c.size() == 2) {
                    List<Literal> literals = c.getLiteralList();
                    Literal l1 = literals.get(0);
                    Literal l2 = literals.get(1);
                    return new GenericComplexPropositionalSentence(l1, l2, Connective.OR, true);
                }
            } else if (optCnf.size() == 2) {
                List<PropositionalClause> clauseList = optCnf.getClauseList();
                PropositionalClause clause1 = clauseList.get(0);
                PropositionalClause clause2 = clauseList.get(1);

                Literal literal1 = clause1.size() == 1 ? clause1.getLiterals().iterator().next() : null;
                Literal literal2 = clause2.size() == 1 ? clause2.getLiterals().iterator().next() : null;

                if (literal1 == null || literal2 == null) return
                        new GenericComplexPropositionalSentence(clause1, clause2, Connective.AND, true);

                new GenericComplexPropositionalSentence(literal1, literal2, Connective.AND, true);
            }

            return new GenericComplexPropositionalSentence(left, right, Connective.AND, true);
        } catch (TautologyException e) {
            return Literal.FALSE;
        } catch (ContradictionException e) {
            return Literal.TRUE;
        }
    }

    public static PropositionalSentence cnfNegatedAndGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.AND, true);
    }

    public static PropositionalSentence genericNegatedAndGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.AND, true);
    }
    //-- NegatedAND end

    //-- IMPLICATION start
    public static PropositionalSentence literalImplicationLiteral(PropositionalSentence left, PropositionalSentence right) {
        Literal literalLeft = (Literal) left;
        Literal literalRight = (Literal) right;

        if (literalLeft == Literal.FALSE) return Literal.TRUE;
        if (literalLeft == Literal.TRUE && literalRight == Literal.FALSE) return Literal.FALSE;
        if (literalLeft.equals(literalRight)) return Literal.TRUE;
        if (literalLeft.equalsIgnoreNegation(literalRight)) return literalRight;
        if (literalLeft == Literal.TRUE) return literalRight;
        if (literalRight == Literal.FALSE) return new Literal(literalLeft.getName(), !literalLeft.isNegated());
        if (literalRight == Literal.TRUE) return Literal.TRUE;

        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION);
    }

    public static PropositionalSentence literalImplicationClause(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        if (right.type() == LITERAL) {
            Literal literal = (Literal) right;
            if (literal == Literal.TRUE) return Literal.TRUE;
            if (literal == Literal.FALSE) return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + left + SentenceUtils.CLOSING_PARENTHESES);

        } else {
            Literal literal = (Literal) left;
            if (literal == Literal.TRUE) return right;
            if (literal == Literal.FALSE) return Literal.TRUE;
            if (((PropositionalClause) right).getLiterals().contains(literal)) return Literal.TRUE;
            if (((PropositionalClause) right).getLiterals().contains(new Literal(literal.getName(), !literal.isNegated()))) return right;
        }
        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION);
    }

    public static PropositionalSentence literalImplicationCNF(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        if (right.type() == LITERAL) {
            Literal literal = (Literal) right;
            if (literal == Literal.TRUE) return Literal.TRUE;
            if (literal == Literal.FALSE) return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + left + SentenceUtils.CLOSING_PARENTHESES);

        } else {
            Literal literal = (Literal) left;
            if (literal == Literal.TRUE) return right;
            if (literal == Literal.FALSE) return Literal.TRUE;
            if (((PropositionalCNFSentence) right).getClauses().stream()
                    .allMatch(c -> c.getLiterals().contains(literal))) return Literal.TRUE;
        }
        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION);
    }

    public static PropositionalSentence literalImplicationGeneric(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        if (right.type() == LITERAL) {
            Literal literal = (Literal) right;
            if (literal == Literal.TRUE) return Literal.TRUE;
            if (literal == Literal.FALSE) return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + left + SentenceUtils.CLOSING_PARENTHESES);
        } else {
            Literal literal = (Literal) left;
            if (literal == Literal.TRUE) return right;
            if (literal == Literal.FALSE) return Literal.TRUE;
        }
        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION);
    }

    public static PropositionalSentence clauseImplicationClause(PropositionalSentence left, PropositionalSentence right) {
        if (left.equals(right)) return Literal.TRUE;
        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION);
    }

    public static PropositionalSentence clauseImplicationCNF(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION);
    }

    public static PropositionalSentence clauseImplicationGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION);
    }

    public static PropositionalSentence cnfImplicationCNF(PropositionalSentence left, PropositionalSentence right) {
        if (left.equals(right)) return Literal.TRUE;
        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION);
    }

    public static PropositionalSentence cnfImplicationGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION);
    }

    public static PropositionalSentence genericImplicationGeneric(PropositionalSentence left, PropositionalSentence right) {
        if (left.equals(right)) return Literal.TRUE;
        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION);
    }
    //-- IMPLICATION end

    //-- NegatedIMPLICATION start
    public static PropositionalSentence literalNegatedImplicationLiteral(PropositionalSentence left, PropositionalSentence right) {
        Literal literalLeft = (Literal) left;
        Literal literalRight = (Literal) right;

        if (literalLeft == Literal.FALSE) return Literal.FALSE;
        if (literalLeft == Literal.TRUE && literalRight == Literal.FALSE) return Literal.TRUE;
        if (literalLeft.equals(literalRight)) return Literal.FALSE;
        if (literalLeft.equalsIgnoreNegation(literalRight)) return new Literal(literalRight.getName(), !literalRight.isNegated());
        if (literalLeft == Literal.TRUE) return new Literal(literalRight.getName(), !literalRight.isNegated());
        if (literalRight == Literal.FALSE) return literalLeft;
        if (literalRight == Literal.TRUE) return Literal.FALSE;

        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION, true);
    }

    public static PropositionalSentence literalNegatedImplicationClause(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        if (right.type() == LITERAL) {
            Literal literal = (Literal) right;
            if (literal == Literal.TRUE) return Literal.FALSE;
            if (literal == Literal.FALSE) return left;
        } else {
            Literal literal = (Literal) left;
            if (literal == Literal.TRUE) return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + right + SentenceUtils.CLOSING_PARENTHESES);
            if (literal == Literal.FALSE) return Literal.FALSE;
            if (((PropositionalClause) right).getLiterals().contains(literal)) return Literal.FALSE;
            if (((PropositionalClause) right).getLiterals().contains(new Literal(literal.getName(), !literal.isNegated()))) {
                return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                        SentenceUtils.OPENING_PARENTHESES + right + SentenceUtils.CLOSING_PARENTHESES);
            }
        }
        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION, true);
    }

    public static PropositionalSentence literalNegatedImplicationCNF(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        if (right.type() == LITERAL) {
            Literal literal = (Literal) right;
            if (literal == Literal.TRUE) return Literal.FALSE;
            if (literal == Literal.FALSE) return left;
        } else {
            Literal literal = (Literal) left;
            if (literal == Literal.TRUE) return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + right + SentenceUtils.CLOSING_PARENTHESES);
            if (literal == Literal.FALSE) return Literal.FALSE;
            if (((PropositionalCNFSentence) right).getClauses().stream()
                    .allMatch(c -> c.getLiterals().contains(literal))) return Literal.FALSE;
        }
        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION, true);
    }

    public static PropositionalSentence literalNegatedImplicationGeneric(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        if (right.type() == LITERAL) {
            Literal literal = (Literal) right;
            if (literal == Literal.TRUE) return Literal.FALSE;
            if (literal == Literal.FALSE) return left;
        } else {
            Literal literal = (Literal) left;
            if (literal == Literal.TRUE) return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                    SentenceUtils.OPENING_PARENTHESES + right + SentenceUtils.CLOSING_PARENTHESES);
            if (literal == Literal.FALSE) return Literal.FALSE;
        }
        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION, true);
    }

    public static PropositionalSentence clauseNegatedImplicationClause(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        if (left.equals(right)) return Literal.FALSE;
        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION, true);
    }

    public static PropositionalSentence clauseNegatedImplicationCNF(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION, true);
    }

    public static PropositionalSentence clauseNegatedImplicationGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION, true);
    }

    public static PropositionalSentence cnfNegatedImplicationCNF(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        if (left.equals(right)) return Literal.FALSE;
        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION, true);
    }

    public static PropositionalSentence cnfNegatedImplicationGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION, true);
    }

    public static PropositionalSentence genericNegatedImplicationGeneric(PropositionalSentence left, PropositionalSentence right) {
        if (left.equals(right)) return Literal.FALSE;
        return new GenericComplexPropositionalSentence(left, right, Connective.IMPLICATION, true);
    }
    //-- NegatedIMPLICATION end

    //-- BICONDITIONAL start
    public static PropositionalSentence literalBiconditionalLiteral(PropositionalSentence left, PropositionalSentence right) {
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

        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL);
    }

    public static PropositionalSentence literalBiconditionalClause(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        PropositionalClause clause = (PropositionalClause) (left.type() == CLAUSE ? left : right);

        if (literal == Literal.TRUE) return clause;
        if (literal == Literal.FALSE) return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + clause + SentenceUtils.CLOSING_PARENTHESES);

        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL);
    }

    public static PropositionalSentence literalBiconditionalCNF(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        PropositionalCNFSentence cnf = (PropositionalCNFSentence) (left.type() == CNF ? left : right);

        if (literal == Literal.TRUE) return cnf;
        if (literal == Literal.FALSE) return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + cnf + SentenceUtils.CLOSING_PARENTHESES);

        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL);
    }

    public static PropositionalSentence literalBiconditionalGeneric(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        GenericComplexPropositionalSentence generic = (GenericComplexPropositionalSentence) (left.type() == GENERIC_COMPLEX ? left : right);

        if (literal == Literal.TRUE) return generic;
        if (literal == Literal.FALSE) return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + generic + SentenceUtils.CLOSING_PARENTHESES);

        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL);
    }

    public static PropositionalSentence clauseBiconditionalClause(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        PropositionalClause clause1 = (PropositionalClause) left;
        PropositionalClause clause2 = (PropositionalClause) right;

        if (clause1.equals(clause2)) return Literal.TRUE;
        if (PropositionalSentence.isEquivalent(clause1, new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + clause2 + SentenceUtils.CLOSING_PARENTHESES))) return Literal.FALSE;

        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL);
    }

    public static PropositionalSentence clauseBiconditionalCNF(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL);
    }

    public static PropositionalSentence clauseBiconditionalGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL);
    }

    public static PropositionalSentence cnfBiconditionalCNF(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        PropositionalCNFSentence cnf1 = (PropositionalCNFSentence) left;
        PropositionalCNFSentence cnf2 = (PropositionalCNFSentence) right;

        if (cnf1.equals(cnf2)) return Literal.TRUE;
        if (PropositionalSentence.isEquivalent(cnf1, new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + cnf2 + SentenceUtils.CLOSING_PARENTHESES))) return Literal.FALSE;

        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL);
    }

    public static PropositionalSentence cnfBiconditionalGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL);
    }

    public static PropositionalSentence genericBiconditionalGeneric(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        GenericComplexPropositionalSentence generic1 = (GenericComplexPropositionalSentence) left;
        GenericComplexPropositionalSentence generic2 = (GenericComplexPropositionalSentence) right;

        if (generic1.equals(generic2)) return Literal.TRUE;
        if (generic1.equals(new GenericComplexPropositionalSentence(SentenceUtils.NOT + generic2.toString()))) return Literal.FALSE;

        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL);
    }
    //-- BICONDITIONAL end

    //-- NegatedBICONDITIONAL start
    public static PropositionalSentence literalNegatedBiconditionalLiteral(PropositionalSentence left, PropositionalSentence right) {
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

        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL, true);
    }

    public static PropositionalSentence literalNegatedBiconditionalClause(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        PropositionalClause clause = (PropositionalClause) (left.type() == CLAUSE ? left : right);

        if (literal == Literal.FALSE) return clause;
        if (literal == Literal.TRUE) return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + clause + SentenceUtils.CLOSING_PARENTHESES);

        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL, true);
    }

    public static PropositionalSentence literalNegatedBiconditionalCNF(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        PropositionalCNFSentence cnf = (PropositionalCNFSentence) (left.type() == CNF ? left : right);

        if (literal == Literal.FALSE) return cnf;
        if (literal == Literal.TRUE) return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + cnf + SentenceUtils.CLOSING_PARENTHESES);

        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL, true);
    }

    public static PropositionalSentence literalNegatedBiconditionalGeneric(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        Literal literal = (Literal) (left.type() == LITERAL ? left : right);
        GenericComplexPropositionalSentence generic = (GenericComplexPropositionalSentence) (left.type() == GENERIC_COMPLEX ? left : right);

        if (literal == Literal.FALSE) return generic;
        if (literal == Literal.TRUE) return new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + generic + SentenceUtils.CLOSING_PARENTHESES);

        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL, true);
    }

    public static PropositionalSentence clauseNegatedBiconditionalClause(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        PropositionalClause clause1 = (PropositionalClause) left;
        PropositionalClause clause2 = (PropositionalClause) right;

        if (clause1.equals(clause2)) return Literal.FALSE;
        if (PropositionalSentence.isEquivalent(clause1, new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + clause2 + SentenceUtils.CLOSING_PARENTHESES))) return Literal.TRUE;

        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL, true);
    }

    public static PropositionalSentence clauseNegatedBiconditionalCNF(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL, true);
    }

    public static PropositionalSentence clauseNegatedBiconditionalGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL, true);
    }

    public static PropositionalSentence cnfNegatedBiconditionalCNF(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        PropositionalCNFSentence cnf1 = (PropositionalCNFSentence) left;
        PropositionalCNFSentence cnf2 = (PropositionalCNFSentence) right;

        if (cnf1.equals(cnf2)) return Literal.FALSE;
        if (PropositionalSentence.isEquivalent(cnf1, new GenericComplexPropositionalSentence(SentenceUtils.NOT +
                SentenceUtils.OPENING_PARENTHESES + cnf2 + SentenceUtils.CLOSING_PARENTHESES))) return Literal.TRUE;

        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL, true);
    }

    public static PropositionalSentence cnfNegatedBiconditionalGeneric(PropositionalSentence left, PropositionalSentence right) {
        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL, true);
    }

    public static PropositionalSentence genericNegatedBiconditionalGeneric(PropositionalSentence left, PropositionalSentence right) throws ParseException {
        GenericComplexPropositionalSentence generic1 = (GenericComplexPropositionalSentence) left;
        GenericComplexPropositionalSentence generic2 = (GenericComplexPropositionalSentence) right;

        if (generic1.equals(generic2)) return Literal.FALSE;
        if (generic1.equals(new GenericComplexPropositionalSentence(SentenceUtils.NOT + generic2.toString()))) return Literal.TRUE;

        return new GenericComplexPropositionalSentence(left, right, Connective.BICONDITIONAL, true);
    }
    //-- NegatedBICONDITIONAL end
}
