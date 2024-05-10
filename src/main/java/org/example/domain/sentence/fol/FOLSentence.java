package org.example.domain.sentence.fol;

import org.example.domain.FOLSentenceType;
import org.example.domain.sentence.Sentence;
import org.example.domain.sentence.propositional.PropositionalCNFSentence;
import org.example.exception.ContradictionException;
import org.example.exception.TautologyException;

/**
 * @author aram.azatyan | 4/17/2024 1:33 PM
 */
public sealed interface FOLSentence extends Sentence permits Predicate, FOLClause, FOLCNFSentence,
        GenericComplexFOLSentence {

    FOLSentenceType type();

    FOLCNFSentence minimalCNF() throws ContradictionException, TautologyException;
}
