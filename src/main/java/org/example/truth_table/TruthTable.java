package org.example.truth_table;

import org.example.domain.Connective;
import org.example.domain.SatisfiabilityType;
import org.example.domain.sentence.*;
import org.example.domain.supplementary.TruthTableRow;
import org.example.exception.TautologyException;
import org.example.exception.ContradictionException;
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

    public TruthTable(Sentence sentence) throws ContradictionException, TautologyException {
        if (sentence == null) throw new IllegalArgumentException("null param");
        if (sentence.satisfiabilityType() == SatisfiabilityType.TAUTOLOGY) throw new TautologyException();
        if (sentence.satisfiabilityType() == SatisfiabilityType.CONTRADICTION) throw new ContradictionException();

        this.sentence = sentence;
        literals = new LinkedHashSet<>();
        table = new LinkedHashMap<>();

        switch (sentence.type()) {
            case LITERAL -> constructLiteralTable((Literal) sentence);
            case CLAUSE -> constructClauseTable((Clause) sentence);
            case CNF -> constructCNFTable((CNFSentence) sentence);
            case GENERIC_COMPLEX -> constructGenericComplexTable((GenericComplexSentence) sentence);
        }
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

    private void constructCNFTable(CNFSentence cnfSentence) {
        cnfSentence.getClauses().stream()
                        .flatMap(clause -> clause.getLiterals().stream().map(Literal::getName))
                        .sorted(Comparator.naturalOrder())
                        .forEach(literals::add);

        List<boolean[]> literalValues = Utils.getTrueAndFalseCombinations(literals.size());
        for (boolean[] literalValuesInstance : literalValues) {
            table.put(literalValuesInstance, evaluateCNF(getLiteralValueMap(literalValuesInstance), cnfSentence));
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

    public CNFSentence getCanonicalCNF() {
        LinkedHashSet<Clause> clauses = new LinkedHashSet<>();
        Map<String, Literal[]> literalObjectPreservationMap = new HashMap<>();
        for (String litStr : literals) {
            Literal[] literalArr = new Literal[2];
            literalArr[0] = new Literal(litStr, false);
            literalArr[1] = new Literal(litStr, true);
            literalObjectPreservationMap.put(litStr, literalArr);
        }

        table.entrySet().stream()
                .filter(entry -> !entry.getValue())
                .forEach(entry -> {
                    LinkedHashSet<Literal> literalSet = new LinkedHashSet<>();
                    boolean[] literalValues = entry.getKey();
                    int i = 0;
                    for (String literal : literals) {
                        literalSet.add(literalObjectPreservationMap.get(literal)[literalValues[i++] ? 1 : 0]);
                    }
                    clauses.add(new Clause(literalSet));
                });

        return new CNFSentence(clauses);
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
        switch (sentence.type()) {
            case LITERAL -> {
                Literal literal = (Literal) sentence;
                return evaluateLiteral(literalValueMap.get(literal.getName()), literal);
            }
            case CLAUSE -> {
                return evaluateClause(literalValueMap, (Clause) sentence);
            }
            case CNF -> {
                return evaluateCNF(literalValueMap, (CNFSentence) sentence);
            }
            case GENERIC_COMPLEX -> {
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

    private boolean evaluateCNF(Map<String, Boolean> literalValueMap, CNFSentence cnfSentence) {
        OVER_CLAUSES: for (Clause clause : cnfSentence.getClauses()) {
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
        return new LinkedHashSet<>(literals);
    }

    public LinkedHashMap<boolean[], Boolean> getTable() {
        return new LinkedHashMap<>(table);
    }
}
