package org.example;

import org.example.domain.sentence.GenericComplexSentence;
import org.example.domain.sentence.Sentence;
import org.example.exception.TautologyException;
import org.example.exception.ContradictionException;

import java.text.ParseException;

/**
 * @author aram.azatyan | 2/2/2024 11:58 AM
 */
public class Main {
    public static void main(String[] args) throws ParseException, TautologyException, ContradictionException {
        Sentence s1 = new GenericComplexSentence("!(a => ((b <=> c) | (e & d & !f)))");
        Sentence s2 = new GenericComplexSentence("!(!a | (((b => c) & (c => b)) | (!(!e | !d | f))))");
        System.out.println(s1.minimalCNF().equals(s2.minimalCNF()));

        GenericComplexSentence generic = new GenericComplexSentence("!true => !(!!false)");
        System.out.println(generic);
    }
}

