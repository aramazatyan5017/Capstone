package org.example.old;

/**
 * @author aram.azatyan | 2/14/2024 1:00 AM
 */
public class EvaluationOfUnknownSentenceException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Unable to evaluate a sentence: unknown subsentence present";
    }
}
