package io.github.bananalang.compilecommon.problems;

public class GenericCompilationFailureException extends RuntimeException {
    private final ProblemCollector problemCollector;

    public GenericCompilationFailureException(ProblemCollector problemCollector) {
        this.problemCollector = problemCollector;
    }

    public ProblemCollector getProblemCollector() {
        return problemCollector;
    }

    @Override
    public String getMessage() {
        String superMessage = super.getMessage();
        if (superMessage != null) {
            return superMessage + "\n" + problemCollector.toString();
        }
        return problemCollector.toString();
    }
}
