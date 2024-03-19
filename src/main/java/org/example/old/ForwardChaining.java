package org.example.old;

import org.example.old.KnownAndImplications;
import org.example.old.OldSentence;

import java.util.*;

import static org.example.domain.Connective.*;

/**
 * @author aram.azatyan | 2/15/2024 11:00 AM
 */
public class ForwardChaining {

    public static void main(String[] args) {
        OldSentence sA = new OldSentence("A", false);
        OldSentence sB = new OldSentence("B", false);
        OldSentence sL = new OldSentence("L", false);
        OldSentence sP = new OldSentence("P", false);
        OldSentence sM = new OldSentence("M", false);
        OldSentence sQ = new OldSentence("Q", false);

        OldSentence sNotA = new OldSentence("A", true);
        OldSentence sNotB = new OldSentence("B", true);
        OldSentence sNotAorNotB = new OldSentence(sNotA, sNotB, OR);
        OldSentence sAandP = new OldSentence(sA, sP, AND);
        OldSentence sBandL = new OldSentence(sB, sL, AND);
        OldSentence sLandM = new OldSentence(sL, sM, AND);

        OldSentence sNotAorNotBorL = new OldSentence(sNotAorNotB, sL, OR);
        OldSentence sAandPimpliesL = new OldSentence(sAandP, sL, IMPLICATION);
        OldSentence sBandLimpliesM = new OldSentence(sBandL, sM, IMPLICATION);
        OldSentence sLandMimpliesP = new OldSentence(sLandM, sP, IMPLICATION);
        OldSentence sPimpliesQ = new OldSentence(sP, sQ, IMPLICATION);
        OldSentence notDefinite = new OldSentence(sA, sB, BICONDITIONAL);

        Set<OldSentence> set = new HashSet<>();
        set.add(sA);
        set.add(sB);
        set.add(sNotAorNotBorL);
        set.add(sAandPimpliesL);
        set.add(sBandLimpliesM);
        set.add(sLandMimpliesP);
        set.add(sPimpliesQ);
        set.add(notDefinite);

        set.forEach(System.out::println);
        System.out.println("----------------------------");

        System.out.println("Can Be Entailed: " + doForwardChaining(set, new OldSentence("Q", false)));
    }

    // TODO: 2/15/2024 q-n petq a basic lini?
    public static boolean doForwardChaining(Set<OldSentence> kb, OldSentence toBeEntailed) {
        preForward(kb, toBeEntailed);
        var basicsAndImplications = splitIntoBasicAndImplication(kb);
        var implications = basicsAndImplications.implications();
        var known = basicsAndImplications.known();
        if (implications.size() == 0) System.out.println("aaaa"); // mi ban petq a stex anel
        if (known.size() == 0) System.out.println("aaaa"); // im jokelov es caseum el a ban petq anel
        var countMap = constructCount(implications);
        var inferredMap = constructInferred(kb);

        Queue<OldSentence> queue = new LinkedList<>();
        known.forEach(queue::offer);
        while (!queue.isEmpty()) {
            var sentence = queue.poll();
            if (sentence.equals(toBeEntailed)) {
                // TODO: 2/15/2024 cucadrakan purpose
                kb.forEach(System.out::println);
                return true;
            }
            if (!inferredMap.get(sentence)) {
                inferredMap.put(sentence, true);
                implications.forEach(i -> {
                    if (i.getLhs().getBasicSentences().contains(sentence)) {
                        var count = countMap.get(i);
                        countMap.put(i, count - 1);
                        if (count - 1 == 0) {
                            var conclusion = i.getRhs();
                            // TODO: 2/15/2024 esela zut cucadrakan, erevi petq chi
                            kb.add(new OldSentence(conclusion.getName(), conclusion.isNegated()));
                            queue.offer(conclusion);
                        }
                    }
                });
            }
        }
        return false;
    }

    private static void preForward(Set<OldSentence> kb, OldSentence toBeEntailed) {
        if (kb == null || kb.isEmpty() || toBeEntailed == null) throw new IllegalArgumentException("null param");
        for (Iterator<OldSentence> iterator = kb.iterator(); iterator.hasNext();) {
            OldSentence s = iterator.next();
            if (!s.isDefiniteClause()) iterator.remove();
            s.toDefiniteOfTypeImplication();
        }
        if (kb.isEmpty()) throw new IllegalArgumentException("no definite clauses");
    }

    //-- stex entadrvum a, vor preForwardy arden arvel a
    private static KnownAndImplications splitIntoBasicAndImplication(Set<OldSentence> kb) {
        var basics = new HashSet<OldSentence>();
        var implications = new HashSet<OldSentence>();
        kb.forEach(s -> {
            if (s.getConnective() == null) basics.add(s);
            else implications.add(s);
        });
        return new KnownAndImplications(basics, implications);
    }

    // TODO: 2/15/2024 hima dandax a, bayc hetagayum, ete implicationneri hamar nor representation unenenam, karagana 
    private static Map<OldSentence, Integer> constructCount(Set<OldSentence> implications) {
        var countMap = new HashMap<OldSentence, Integer>();
        implications.forEach(i -> countMap.put(i, i.getLhs().getBasicSentences().size()));
        return countMap;
    }

    private static Map<OldSentence, Boolean> constructInferred(Set<OldSentence> kb) {
        var map = new HashMap<OldSentence, Boolean>();
        kb.forEach(i -> i.getBasicSentences().forEach(s -> map.put(s, false)));
        return map;
    }

}
