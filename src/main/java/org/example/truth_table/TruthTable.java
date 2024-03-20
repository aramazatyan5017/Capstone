package org.example.truth_table;

import org.example.domain.Connective;
import org.example.domain.sentence.*;
import org.example.domain.supplementary.TruthTableRow;
import org.example.exception.TautologyException;
import org.example.exception.UnsatisfiableException;
import org.example.util.Utils;

import java.util.*;

/**
 * @author aram.azatyan | 3/12/2024 7:42 PM
 */
public class TruthTable implements Iterable<TruthTableRow> {
    private final Sentence sentence;
    private final LinkedHashSet<String> literals;
    private final LinkedHashMap<boolean[], Boolean> table;

    private class TruthTableIterator implements Iterator<TruthTableRow> {

        private final Iterator<Map.Entry<boolean[], Boolean>> tableIterator;

        public TruthTableIterator() {
            tableIterator = table.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return tableIterator.hasNext();
        }

        @Override
        public TruthTableRow next() {
            var entry = tableIterator.next();
            return new TruthTableRow(entry.getKey(), entry.getValue());
        }
    }

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
        table.put(new boolean[] {true}, evaluateLiteral(true, literal));
        table.put(new boolean[] {false}, evaluateLiteral(false, literal));
    }

    private void constructClauseTable(Clause clause) {
        clause.getLiterals().stream()
                            .map(Literal::getName)
                            .sorted(Comparator.naturalOrder())
                            .forEach(literals::add);

        List<boolean[]> literalValues = Utils.getTrueAndFalseCombinations(literals.size());
        for (boolean[] literalValuesInstance : literalValues) {
            table.put(literalValuesInstance, evaluateClause(getLiteralValueMap(literalValuesInstance), clause));
        }
    }

    private void constructCNFTable(CNFSentence cnf) {
        cnf.getClauses().stream()
                        .flatMap(clause -> clause.getLiterals().stream().map(Literal::getName))
                        .sorted(Comparator.naturalOrder())
                        .forEach(literals::add);

        List<boolean[]> literalValues = Utils.getTrueAndFalseCombinations(literals.size());
        for (boolean[] literalValuesInstance : literalValues) {
            table.put(literalValuesInstance, evaluateCNF(getLiteralValueMap(literalValuesInstance), cnf));
        }
    }

    private void constructGenericComplexTable(GenericComplexSentence sentence) {
        sentence.getLiterals().stream()
                .map(Literal::getName)
                .sorted(Comparator.naturalOrder())
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

        for (TruthTableRow truthTableRow : this) {
            var str = new StringBuilder();
            for (int i = 0; i < truthTableRow.literalValues().length; i++) {
                str.append(String.format("%" + lengths.get(i) + "d", truthTableRow.literalValues()[i] ? 1 : 0));
                str.append(" ");
            }
            str.append(" │  ");
            str.append(truthTableRow.evaluation() ? 1 : 0);
            System.out.println(str);
        }
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

    @Override
    public Iterator<TruthTableRow> iterator() {
        return new TruthTableIterator();
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
}
