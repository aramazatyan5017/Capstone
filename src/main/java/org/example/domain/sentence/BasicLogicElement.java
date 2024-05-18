package org.example.domain.sentence;

import org.example.domain.LogicType;

/**
 * @author aram.azatyan | 5/8/2024 6:48 PM
 */
public interface BasicLogicElement extends Sentence {
    BasicLogicElement getFalse();

    BasicLogicElement getTrue();

    BasicLogicElement getNegated();

    String getName();

    boolean equalsIgnoreNegation(BasicLogicElement other);
}
