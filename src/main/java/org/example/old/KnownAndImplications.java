package org.example.old;

import java.util.Set;

/**
 * @author aram.azatyan | 2/15/2024 1:08 PM
 */
public record KnownAndImplications(Set<OldSentence> known, Set<OldSentence> implications) {}
