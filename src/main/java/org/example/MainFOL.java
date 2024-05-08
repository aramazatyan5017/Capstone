package org.example;

import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.sentence.propositional.PropositionalClause;
import org.example.parser.FOLCNFExpressionParser;
import org.example.domain.sentence.fol.term.Function;
import org.example.domain.sentence.fol.GenericComplexFOLSentence;
import org.example.domain.sentence.fol.Predicate;

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
//        System.out.println(FOLCNFExpressionParser.parseClause("!!!(!Missile(x) | !(Sells((Good(x, y)), NONO, Wow(x, KUKU, NONO))) => Criminal(NONO))"));
        System.out.println(FOLCNFExpressionParser.parseClause("(!(!(!Missile(x))))"));

        System.out.println(new PropositionalClause("(a | b | c)"));
        System.out.println(new PropositionalClause("((a | b) | c)"));
        new PropositionalCNFSentence("A");

        System.out.println(new PropositionalCNFSentence("(a | b | c) & d"));
        System.out.println(new PropositionalCNFSentence("((a | b) | c) & d"));
        new PropositionalCNFSentence("(b | (!!c | !!(d | g))) & a & (e | f)");

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
