package org.example.inference_rules;

import org.example.domain.SatisfiabilityType;
import org.example.domain.sentence.Clause;
import org.example.domain.sentence.Literal;
import org.example.exception.NothingToInferException;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author aram.azatyan | 3/27/2024 4:04 PM
 */
public class ResolutionInferenceRule {

    public static Clause resolve(Clause clause1, Clause clause2) throws NothingToInferException {
        if (clause1 == null || clause2 == null) throw new IllegalArgumentException("null param");
        if (clause1.satisfiabilityType() != SatisfiabilityType.CONTINGENCY ||
                clause2.satisfiabilityType() != SatisfiabilityType.CONTINGENCY) throw new NothingToInferException();

        Set<Literal> set = new HashSet<>();
        LinkedHashSet<Literal> possClauseLiterals = clause1.getLiterals();

        clause1.getLiterals().forEach(l -> {
            set.add(new Literal(l.getName(), !l.isNegated()));
        });

        StringBuilder complementaryLiteralName = new StringBuilder();
        for (Literal literal : clause2.getLiterals()) {
            if (set.contains(literal) && !complementaryLiteralName.isEmpty()) throw new NothingToInferException();
            if (set.contains(literal)) {
                complementaryLiteralName.append(literal.getName());
                possClauseLiterals.removeIf(l -> l.getName().contentEquals(complementaryLiteralName));
                continue;
            }
            possClauseLiterals.add(literal);
        }

        if (complementaryLiteralName.isEmpty()) throw new NothingToInferException();

        return new Clause(possClauseLiterals);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(resolve(new Clause("A | !B | C"), new Clause("!A | B | C")));
    }
}