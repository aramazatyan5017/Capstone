package org.example.algo;

import org.example.Samp;
import org.example.domain.FOLPremise;
import org.example.domain.PropositionalPremise;
import org.example.domain.sentence.fol.FOLSentence;
import org.example.domain.sentence.fol.Predicate;
import org.example.domain.sentence.fol.term.Constant;
import org.example.domain.sentence.fol.term.Variable;
import org.example.domain.sentence.propositional.Literal;
import org.example.domain.sentence.propositional.PropositionalSentence;
import org.example.domain.supplementary.FOLPremiseAndConclusion;
import org.example.domain.supplementary.IntAndVariable;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;
import org.example.util.SentenceUtils;

import java.util.*;

/**
 * @author aram.azatyan | 5/13/2024 1:46 PM
 */
public class FOLForwardChaining {
    private final LinkedHashSet<FOLSentence> known;
    private final LinkedHashMap<FOLPremise, FOLSentence> implications;

    public FOLForwardChaining(LinkedHashMap<FOLPremise, FOLSentence> knowledgeBase) {
        if (knowledgeBase == null || knowledgeBase.isEmpty()) throw
                new IllegalArgumentException("null param");
        knowledgeBase.remove(null);
        if (knowledgeBase.isEmpty()) throw new IllegalArgumentException("null param");

        known = new LinkedHashSet<>();
        implications = new LinkedHashMap<>();

        knowledgeBase.forEach((k, v) -> {
            if (v == null) known.addAll(k.getPremiseSentences());
            else implications.put(k, v);
        });

        if (known.size() == 0 || implications.size() == 0) throw
                new IllegalArgumentException("unable to perform Forward Checking");
    }

    public boolean isEntail(FOLSentence toBeEntailed) {
        if (toBeEntailed == null) throw new IllegalArgumentException("null param");

        Set<Constant> consts = new HashSet<>();

        known.forEach(k -> consts.addAll(((Predicate) k).getConstants()));
        implications.forEach((k, v) -> {
            k.getPremiseSentences().forEach(p -> consts.addAll(((Predicate) p).getConstants()));
            consts.addAll(((Predicate) v).getConstants());
        });

        Constant[] constants = consts.toArray(new Constant[0]);

        List<Map.Entry<FOLPremise, FOLSentence>> list =  implications.entrySet().stream().filter(e ->
                e.getKey().getPremiseSentences().stream()
                .map(s -> (Predicate) s)
                .anyMatch(p -> !p.getVariables().isEmpty())
                ||
                !((Predicate) e.getValue()).getVariables().isEmpty())
                .toList();

        List<IntAndVariable> intAndVariables = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Set<Variable> variables = new HashSet<>();

            list.get(i).getKey().getPremiseSentences().stream()
                    .map(s -> (Predicate) s)
                    .forEach(p -> variables.addAll(p.getVariables()));
            variables.addAll(((Predicate) list.get(i).getValue()).getVariables());

            for (Variable variable : variables) {
                intAndVariables.add(new IntAndVariable(i, variable));
            }
        }

        IntAndVariable[] infoArr = intAndVariables.toArray(new IntAndVariable[0]);

        List<LinkedHashMap<IntAndVariable, Constant>> results = Samp.generateSubstitutions(infoArr, constants);

        int count = 0;
        for (LinkedHashMap<IntAndVariable, Constant> result : results) {
            System.out.println(result + String.valueOf(count++));
            LinkedHashMap<PropositionalPremise, PropositionalSentence> kb = new LinkedHashMap<>();
            known.forEach(k -> {
                kb.put(new PropositionalPremise(predicateToLiteral((Predicate) k)), null);
            });

            List<FOLPremiseAndConclusion> copyList = list.stream().map(l -> {
                FOLPremise key = l.getKey();
                Predicate value = (Predicate) l.getValue();

                Set<FOLSentence> set = new HashSet<>();

                key.getPremiseSentences().forEach(s -> {
                    Predicate predicate = (Predicate) s;
                    set.add(new Predicate(predicate.getName(), predicate.isNegated(), predicate.getTerms()));
                });

                return new FOLPremiseAndConclusion(new FOLPremise(set),
                        new Predicate(value.getName(), value.isNegated(), value.getTerms()));
            }).toList();

            result.forEach((k, v) -> {
                FOLPremiseAndConclusion sentence = copyList.get(k.row());
                sentence.premise().getPremiseSentences().forEach(s -> {
                    Predicate predicate = (Predicate) s;
                    predicate.substitute(k.variable(), v);
                });
                ((Predicate) sentence.conclusion()).substitute(k.variable(), v);
            });

            copyList.forEach(s -> {
                Set<PropositionalSentence> set = new HashSet<>();
                s.premise().getPremiseSentences().forEach(c -> {
                    Predicate predicate = (Predicate) c;
                    set.add(predicateToLiteral(predicate));
                });

                kb.put(new PropositionalPremise(set), predicateToLiteral((Predicate) s.conclusion()));
            });

            try {
                boolean isEntail = new PropositionalForwardChaining(kb).isEntail(predicateToLiteral((Predicate) toBeEntailed));
                if (isEntail) return true;
            } catch (ContradictionException e) {
                continue;
            } catch (TautologyException e) {
                continue;
            }
        }

        return false;
    }

    private Literal predicateToLiteral(Predicate predicate) {
        return new Literal(predicate.toString()
                .replaceAll("\\s", "")
                .replaceAll("\\" + SentenceUtils.OPENING_PARENTHESES, "")
                .replaceAll("\\" + SentenceUtils.CLOSING_PARENTHESES, "")
                .replaceAll(String.valueOf(SentenceUtils.COMMA), ""));
    }

    public Set<FOLSentence> getKnown() {
        return new HashSet<>(known);
    }

    public Map<FOLPremise, FOLSentence> getImplications() {
        return new HashMap<>(implications);
    }

    public static void main(String[] args) throws Exception {
        LinkedHashMap<FOLPremise, FOLSentence> kb = new LinkedHashMap<>();

        kb.put(new FOLPremise("American(x) & Weapon(y) & Sells(x, y, z) & Hostile(z)"), new Predicate("Criminal(x)"));
        kb.put(new FOLPremise("Missile(x) & Owns(NONO, x)"), new Predicate("Sells(WEST, x, NONO)"));
        kb.put(new FOLPremise("Missile(x)"), new Predicate("Weapon(x)"));
        kb.put(new FOLPremise("Enemy(x, AMERICA)"), new Predicate("Hostile(x)"));
        kb.put(new FOLPremise("∃x | Owns(NONO, x) & Missile(x)"), null);
        kb.put(new FOLPremise("American(WEST)"), null);
        kb.put(new FOLPremise("Enemy(NONO, AMERICA)"), null);

        System.out.println(new FOLForwardChaining(kb).isEntail(new Predicate("Criminal(WEST)")));
    }
}
