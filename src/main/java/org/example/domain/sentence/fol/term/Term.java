package org.example.domain.sentence.fol.term;

/**
 * @author aram.azatyan | 4/15/2024 10:57 AM
 */
public sealed interface Term permits Variable, Constant, Function {
    TermType type();
}
