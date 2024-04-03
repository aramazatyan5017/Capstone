package org.example;

import org.junit.jupiter.api.Test;

/**
 * @author aram.azatyan | 3/28/2024 12:52 AM
 */
public abstract class SentenceCommon {

    @Test
    public abstract void createAbnormal();

    @Test
    public abstract void createNormal();

    @Test
    public abstract void sentenceTypeTest();

    @Test
    public abstract void minimalCNFTest();

    @Test
    public abstract void satisfiabilityTypeTest();

    @Test
    public abstract void truthTableTest();

    @Test
    public abstract void toStringTest();

    @Test
    public abstract void equalsTest();

    @Test
    public abstract void hashCodeTest();
}
