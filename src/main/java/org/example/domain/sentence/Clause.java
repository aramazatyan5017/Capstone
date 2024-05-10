package org.example.domain.sentence;

import org.example.domain.LogicType;

import java.util.LinkedHashSet;

/**
 * @author aram.azatyan | 5/8/2024 6:02 PM
 */
public interface Clause {
    LogicType logicType();

    LinkedHashSet<BasicLogicElement> basicElements();

    static Clause createClause(LinkedHashSet<BasicLogicElement> elements) {

    }
}
