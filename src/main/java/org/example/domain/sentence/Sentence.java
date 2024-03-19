package org.example.domain.sentence;

import org.example.domain.sentence.CNFSentence;
import org.example.exception.TautologyException;
import org.example.exception.UnsatisfiableException;

/**
 * @author aram.azatyan | 2/27/2024 5:58 PM
 */
public sealed interface Sentence permits Literal, Clause, CNFSentence, GenericComplexSentence {
    default boolean isLiteral() {
        return false;
    };

    default boolean isClause() {
        return false;
    };

    default boolean isCnf() {
        return false;
    };

    default boolean isGenericComplex() {
        return false;
    };

    CNFSentence convertToCNF() throws UnsatisfiableException, TautologyException;
}
