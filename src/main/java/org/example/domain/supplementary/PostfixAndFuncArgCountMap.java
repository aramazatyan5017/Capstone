package org.example.domain.supplementary;

import org.example.parser.supplementary.Token;

import java.util.List;
import java.util.Map;

/**
 * @author aram.azatyan | 4/17/2024 5:10 PM
 */
public record PostfixAndFuncArgCountMap(List<Token> postfix, Map<String, Integer> argCountMap) {}
