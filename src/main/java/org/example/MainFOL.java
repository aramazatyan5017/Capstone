package org.example;

import org.example.domain.sentence.fol.term.Constant;
import org.example.domain.sentence.fol.term.Variable;
import org.example.domain.sentence.propositional.Literal;
import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.domain.sentence.propositional.PropositionalClause;
import org.example.parser.FOLCNFExpressionParser;
import org.example.domain.sentence.fol.term.Function;
import org.example.domain.sentence.fol.GenericComplexFOLSentence;
import org.example.domain.sentence.fol.Predicate;
import org.example.parser.FOLInfixExpressionParser;

import java.util.Arrays;

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

        System.out.println(FOLInfixExpressionParser.parseGeneric("Wow(x, y) | Wow(a, b, c) | Wow(x, y, z, e) | Wow(x, y)"));

//        System.out.println(new GenericComplexFOLSentence("Wow(x, y) & a => c"));
//        System.out.println(FOLCNFExpressionParser.parseCNF("(Missile(x) & Sells(x)) | Kuku(NONO)"));


        System.out.println(new Function("Wow(x, y)").equals(new Function("Wow(y, x)")));
        System.out.println(new Function("Wow(x)").equals(new Predicate("Wow(x)")));
        System.out.println(new Function("Wow(x, Wow(z, u), Wow(x))").equals(new Function("Wow(x, Wow(z, u), Wow(x))")));
        System.out.println(new Predicate("Wow(x, Wow(z, u), Wow(x))").equals(new Predicate("Wow(x, Wow(z, u), Wow(x))")));


        // (a & !a)
        // (Wow(x, y) & !Wow(y, x))
        // Wow(a, b, c) & !Wow(d, e, f)

        System.out.println((int) '∀');
        System.out.println((int) '∃');

        System.out.println((char) 8704);
        System.out.println((char) 8707);

        Arrays.stream("5A6ABC7GGG".split("[0-9][A-Z]+")).forEach(System.out::println);

        Predicate pred = new Predicate("Sells(x, y, Missile(x, Kuku(x, y)), x)");
        pred.substitute(new Variable("x"), new Constant("ARAM"));
        System.out.println(pred);


    }
}
