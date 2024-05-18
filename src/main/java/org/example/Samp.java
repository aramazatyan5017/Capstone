package org.example;

import org.example.domain.sentence.fol.term.Constant;
import org.example.domain.sentence.fol.term.Variable;
import org.example.domain.supplementary.IntAndVariable;

import java.util.*;

/**
 * @author aram.azatyan | 5/14/2024 7:22 PM
 */
public class Samp {
    public static void main(String[] args) {
        IntAndVariable[] set = {
                new IntAndVariable(1, new Variable("x")),
                new IntAndVariable(1, new Variable("y")),
                new IntAndVariable(1, new Variable("z")),
                new IntAndVariable(2, new Variable("x")),
                new IntAndVariable(3, new Variable("x")),
                new IntAndVariable(4, new Variable("x"))
        };
        Constant[] arr = {
                new Constant("NONO"),
                new Constant("WEST"),
                new Constant("AMERICA"),
                new Constant("C1")
        };

        List<LinkedHashMap<IntAndVariable, Constant>> results = generateSubstitutions(set, arr);

        // Print the results
        for (LinkedHashMap<IntAndVariable, Constant> result : results) {
            for (IntAndVariable obj : set) {
                System.out.print(obj + ": " + result.get(obj) + " ");
            }
            System.out.println();
        }
    }

    public static List<LinkedHashMap<IntAndVariable, Constant>> generateSubstitutions(IntAndVariable[] set, Constant[] arr) {
        List<LinkedHashMap<IntAndVariable, Constant>> results = new ArrayList<>();
        generateSubstitutionsHelper(set, arr, new LinkedHashMap<>(), results, 0);
        return results;
    }

    private static void generateSubstitutionsHelper(IntAndVariable[] set, Constant[] arr, LinkedHashMap<IntAndVariable, Constant> currentMap,
                                                    List<LinkedHashMap<IntAndVariable, Constant>> results, int index) {
        if (index == set.length) {
            results.add(new LinkedHashMap<>(currentMap));
            return;
        }

        IntAndVariable current = set[index];
        int intValue = current.row();

        for (Constant s : arr) {
            boolean conflict = false;
            for (Map.Entry<IntAndVariable, Constant> entry : currentMap.entrySet()) {
                IntAndVariable key = entry.getKey();
                Constant value = entry.getValue();
                if (key.row() == intValue && value.equals(s) && !key.variable().equals(current.variable())) {
                    conflict = true;
                    break;
                }
            }
            if (!conflict) {
                currentMap.put(current, s);
                generateSubstitutionsHelper(set, arr, currentMap, results, index + 1);
                currentMap.remove(current);
            }
        }
    }
}
