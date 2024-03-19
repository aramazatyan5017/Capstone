package org.example.truth_table;

import org.example.domain.*;
import org.example.domain.sentence.*;
import org.example.exception.TautologyException;
import org.example.exception.UnsatisfiableException;
import org.example.util.SentenceUtils;
import org.example.util.Utils;

import java.util.*;

/**
 * @author aram.azatyan | 3/12/2024 7:42 PM
 */
public class TruthTable {
    private final Sentence sentence;
    private final LinkedHashSet<String> literals;
    private final LinkedHashMap<boolean[], Boolean> table;

    public TruthTable(Sentence sentence) throws UnsatisfiableException, TautologyException {
        if (sentence == null) throw new IllegalArgumentException("null param");

        Sentences.optimizeCNF(sentence.convertToCNF());
        this.sentence = sentence;
        literals = new LinkedHashSet<>();
        table = new LinkedHashMap<>();

        if (sentence.isLiteral()) constructLiteralTable((Literal) sentence);
        else if (sentence.isClause()) constructClauseTable((Clause) sentence);
        else if (sentence.isCnf()) constructCNFTable((CNFSentence) sentence);
        else if (sentence.isGenericComplex()) constructGenericComplexTable((GenericComplexSentence) sentence);
    }

    private void constructLiteralTable(Literal literal) {
        literals.add(literal.getName());
        table.put(new boolean[] {false}, evaluateLiteral(false, literal));
        table.put(new boolean[] {true}, evaluateLiteral(true, literal));
    }

    private void constructClauseTable(Clause clause) {
        clause.getLiterals().forEach(l -> literals.add(l.getName()));
        List<boolean[]> literalValues = Utils.getTrueAndFalseCombinations(literals.size());
        for (boolean[] literalValuesInstance : literalValues) {
            table.put(literalValuesInstance, evaluateClause(getLiteralValueMap(literalValuesInstance), clause));
        }
    }

    private void constructCNFTable(CNFSentence cnf) {
        cnf.getClauses().forEach(c -> c.getLiterals().forEach(l -> literals.add(l.getName())));
        List<boolean[]> literalValues = Utils.getTrueAndFalseCombinations(literals.size());
        for (boolean[] literalValuesInstance : literalValues) {
            table.put(literalValuesInstance, evaluateCNF(getLiteralValueMap(literalValuesInstance), cnf));
        }
    }

    private void constructGenericComplexTable(GenericComplexSentence sentence) {
        sentence.getLiterals().stream()
                .map(Literal::getName)
                .forEach(literals::add);
        List<boolean[]> literalValues = Utils.getTrueAndFalseCombinations(literals.size());
        for (boolean[] literalValuesInstance : literalValues) {
            table.put(literalValuesInstance, evaluateSentence(getLiteralValueMap(literalValuesInstance), sentence));
        }
    }

    public void print() {
        List<Integer> lengths = literals.stream()
                .map(String::length)
                .toList();
        String sentenceStr = sentence.toString();
        literals.forEach(l -> System.out.print(l + " "));
        System.out.print(" │  ");
        System.out.println(sentenceStr);
        printDash(lengths, sentenceStr.length());

        List<String> list = new ArrayList<>();

        for (Map.Entry<boolean[], Boolean> entry : table.entrySet()) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < entry.getKey().length; i++) {
                str.append(String.format("%" + lengths.get(i) + "d", entry.getKey()[i] ? 1 : 0));
                str.append(" ");
            }
            str.append(" │  ");
            str.append(entry.getValue() ? 1 : 0);
            list.add(str.toString());
        }
        Collections.reverse(list);
        list.forEach(System.out::println);
    }

    private void printDash(List<Integer> lengths, int sentenceLength) {
        System.out.print("-".repeat(lengths.stream()
                .reduce(Integer::sum)
                .get() + lengths.size() + 1));
        System.out.print("│-");
        System.out.println("-".repeat(sentenceLength + 1));
    }

    private Map<String, Boolean> getLiteralValueMap(boolean[] literalValuesInstance) {
        Map<String, Boolean> literalValueMap = new HashMap<>();
        int i = 0;
        for (String literal : literals) literalValueMap.put(literal, literalValuesInstance[i++]);
        return literalValueMap;
    }

    // TODO: 3/15/2024 kareli a hetagayaum cnf ov sarqel
    private boolean evaluateSentence(Map<String, Boolean> literalValueMap, Sentence sentence) {
        if (sentence.isLiteral()) {
            Literal literal = (Literal) sentence;
            return evaluateLiteral(literalValueMap.get(literal.getName()), literal);
        }
        if (sentence.isClause()) {
            return evaluateClause(literalValueMap, (Clause) sentence);
        }
        if (sentence.isCnf()) {
            return evaluateCNF(literalValueMap, (CNFSentence) sentence);
        }
        if (sentence.isGenericComplex()) {
            GenericComplexSentence complex = (GenericComplexSentence) sentence;
            Boolean result = null;
            boolean left = evaluateSentence(literalValueMap, complex.getLeftSentence());
            if (complex.getConnective() == Connective.AND && !left) result = false;
            else if (complex.getConnective() == Connective.OR && left) result = true;
            else if (complex.getConnective() == Connective.IMPLICATION && !left) result = true;

            if (result == null) {
                result = complex.getConnective().evaluate(left,
                        evaluateSentence(literalValueMap, complex.getRightSentence()));
            }

            return complex.isNegated() != result;
        }
        return false;
    }

    private boolean evaluateLiteral(boolean value, Literal literal) {
        return value != literal.isNegated();
    }

    private boolean evaluateClause(Map<String, Boolean> literalValueMap, Clause clause) {
        for (Literal literal : clause.getLiterals()) {
            if (literal.isNegated() != literalValueMap.get(literal.getName())) return true;
        }
        return false;
    }

    private boolean evaluateCNF(Map<String, Boolean> literalValueMap, CNFSentence cnf) {
        OVER_CLAUSES: for (Clause clause : cnf.getClauses()) {
            OVER_LITERALS: for (Literal literal : clause.getLiterals()) {
                if (literal.isNegated() != literalValueMap.get(literal.getName())) continue OVER_CLAUSES;
            }
            return false;
        }
        return true;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public LinkedHashSet<String> getLiterals() {
        return literals;
    }

    public LinkedHashMap<boolean[], Boolean> getTable() {
        return table;
    }

    public static void main(String[] args) throws Exception {

//        CNFSentence cnf1 = new GenericComplexSentence("(A <=> B | C & (D | E))").convertToCNF();
//        CNFSentence cnf2 = new GenericComplexSentence("(A | !B) & (Q | G | H) & (E)").convertToCNF();
//        System.out.println(cnf1);
//        System.out.println(cnf2);


//        System.out.println(new CNFSentence("(A | B) & (!B | !A) & (!C | !D | B) & (!C | !D | !A) & (!C | !E | B) & (!C | !E | !A)").convertToCNF());
//
        new TruthTable(new CNFSentence("(A | B) & (!B | !A) & (!C | !D | B) & (!C | !D | !A) & (!C | !E | B) & (!C | !E | !A)")).print();
//
//        new TruthTable(new GenericComplexSentence("((!A | B | C) & (!A | B | D | E) & (!B | A))")).print();

//


        GenericComplexSentence sentence1 = new GenericComplexSentence(new GenericComplexSentence("(A <=> B | C & (D | E))"),
                new CNFSentence("(A | !B) & (Q | G | H) & (E)"), Connective.BICONDITIONAL, true);
        new TruthTable(sentence1).print(); // TODO: 3/15/2024 petq a compare anel

        System.out.println(SentenceUtils.convertOnlineCalculatorString("(¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ ¬C ∨ D ∨ E) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ ¬D ∨ E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ ¬E) ∧ (¬A ∨ ¬B ∨ C ∨ D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ ¬E) ∧ (¬A ∨ B ∨ ¬C ∨ ¬D ∨ E) ∧ (¬A ∨ B ∨ ¬C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ ¬C ∨ D ∨ E) ∧ (A ∨ B ∨ C ∨ ¬D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ ¬D ∨ E) ∧ (A ∨ B ∨ C ∨ D ∨ ¬E) ∧ (A ∨ B ∨ C ∨ D ∨ E)"));

    }
}
