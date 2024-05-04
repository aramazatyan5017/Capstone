package org.example;

import org.example.domain.sentence.CNFSentence;
import org.example.domain.sentence.Clause;
import org.example.parser.FOLCNFExpressionParser;
import org.example.temp_fol.Function;
import org.example.temp_fol.GenericComplexFOLSentence;
import org.example.temp_fol.Predicate;

/**
 * @author aram.azatyan | 4/17/2024 8:55 PM
 */
public class MainFOL {
    public static void main(String[] args) throws Exception {
        System.out.println(new GenericComplexFOLSentence("!!!(!Missile(x) | !(Sells((Good(x, y)), NONO, Wow(x, KUKU, NONO))) => Criminal(NONO))"));
        System.out.println(new Predicate("!Missile(NONO, Good(x, y))"));
        System.out.println(new Function("Missile(NONO, Good(x, y))"));
        System.out.println(FOLCNFExpressionParser.parseClause("Missile(x) | Sells(x, Wow(x, y)) | !(!(Kuku(a, b)))"));
        System.out.println(FOLCNFExpressionParser.parseClause("Missile(x) | Sells(x, y) | Kuku(a, b)"));
        System.out.println(FOLCNFExpressionParser.parseClause("Missile(x) | Sells(x) | !Kuku(NONO)"));
        System.out.println(FOLCNFExpressionParser.parseClause("!!!(!Missile(x) | !(Sells((Good(x, y)), NONO, Wow(x, KUKU, NONO))) => Criminal(NONO))"));
        System.out.println(FOLCNFExpressionParser.parseClause("(!(!(!Missile(x))))"));

        System.out.println(new Clause("(a | b | c)"));
        System.out.println(new Clause("((a | b) | c)"));
        new CNFSentence("A");

        System.out.println(new CNFSentence("(a | b | c) & d"));
        System.out.println(new CNFSentence("((a | b) | c) & d"));
        new CNFSentence("(b | (!!c | !!(d | g))) & a & (e | f)");

        System.out.println(FOLCNFExpressionParser.parseClause("(!(!(!Missile(x))) | !!!Sells(x, Wow(x, y)) | Kuku(a, b))"));
        System.out.println(FOLCNFExpressionParser.parseClause("!(!(!Wow(x, y))) | !!!Missile(x) | Sells(x, Kuku(x, y))"));
        System.out.println(FOLCNFExpressionParser.parseClause("(!(!(Sells(x, Wow(x, y)) | Kuku(x, y))) | Missile(x))"));

        System.out.println(FOLCNFExpressionParser.parseCNF("((Missile(x) | Sells(x)) | Kuku(NONO)) & Wow(Gugu(a), Zuzu(b)) & (Goes(x, Meow(x, y)) | Criminal(y))"));
        System.out.println(FOLCNFExpressionParser.parseCNF("((Missile(x) & Sells(x)) & Kuku(NONO))"));
        System.out.println(FOLCNFExpressionParser.parseCNF("!!((!!!Missile(x) & !!!(!!Sells(x)) & (Kuku(NONO))))"));
        System.out.println(FOLCNFExpressionParser.parseCNF("Missile(x)"));
        System.out.println(FOLCNFExpressionParser.parseCNF("Missile(x) & Sells(y) & Kuku(NONO)"));
        System.out.println(FOLCNFExpressionParser.parseCNF("(Missile(x)) & (Sells(y)) & (Kuku(NONO))"));

//        System.out.println(FOLCNFExpressionParser.parseCNF("(Missile(x) & Sells(x)) | Kuku(NONO)"));

    }
}
