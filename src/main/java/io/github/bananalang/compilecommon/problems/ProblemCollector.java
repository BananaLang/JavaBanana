package io.github.bananalang.compilecommon.problems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.bananalang.compilecommon.problems.Problem.ProblemLevel;


public final class ProblemCollector {
    private int notices = 0, warnings = 0, errors = 0;
    private final List<Problem> problems = new ArrayList<>();
    private final List<Problem> unmodifiableProblems = Collections.unmodifiableList(problems);

    public ProblemCollector() {
    }

    public ProblemCollector addProblem(Problem problem) {
        switch (problem.getLevel()) {
            case NOTICE:
                notices++;
                break;
            case WARNING:
                notices++;
                break;
            case ERROR:
                errors++;
                break;
        }
        problems.add(problem);
        return this;
    }

    public ProblemCollector addProblem(ProblemLevel level, String message, int row, int column) {
        return addProblem(new Problem(level, message, row, column));
    }

    public ProblemCollector addProblem(ProblemLevel level, String message) {
        return addProblem(new Problem(level, message));
    }

    public ProblemCollector notice(String message, int row, int column) {
        return addProblem(Problem.notice(message, row, column));
    }

    public ProblemCollector notice(String message) {
        return addProblem(Problem.notice(message));
    }

    public ProblemCollector warning(String message, int row, int column) {
        return addProblem(Problem.warning(message, row, column));
    }

    public ProblemCollector warning(String message) {
        return addProblem(Problem.warning(message));
    }

    public ProblemCollector error(String message, int row, int column) {
        return addProblem(Problem.error(message, row, column));
    }

    public ProblemCollector error(String message) {
        return addProblem(Problem.error(message));
    }

    public List<Problem> getProblems() {
        return unmodifiableProblems;
    }

    public int getNotices() {
        return notices;
    }

    public int getWarnings() {
        return warnings;
    }

    public int getErrors() {
        return errors;
    }

    public boolean isFailing() {
        return errors > 0;
    }

    public void throwIfFailing() {
        if (isFailing()) {
            throw new GenericCompilationFailureException(this);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder()
            .append(notices)
            .append(" notice").append(notices != 1 ? "s" : "")
            .append(", ")
            .append(warnings)
            .append(" warning").append(warnings != 1 ? "s" : "")
            .append(", ")
            .append(errors)
            .append(" error").append(errors != 1 ? "s" : "")
            .append("\n");
        for (Problem problem : problems) {
            result.append("\n").append(problem);
        }
        return result.toString();
    }

    public String ansiFormattedString() {
        StringBuilder result = new StringBuilder("\u001b[1m")
            .append(notices)
            .append(" notice").append(notices != 1 ? "s" : "")
            .append(", \u001b[33m")
            .append(warnings)
            .append(" warning").append(warnings != 1 ? "s" : "")
            .append(", \u001b[31m")
            .append(errors)
            .append(" error").append(errors != 1 ? "s" : "")
            .append("\u001b[0m\n");
        for (Problem problem : problems) {
            result.append("\n\u001b[1m");
            switch (problem.getLevel()) {
                case NOTICE:
                    result.append("notice");
                    break;
                case WARNING:
                    result.append("\u001b[33mwarning");
                    break;
                case ERROR:
                    result.append("\u001b[31merror");
                    break;
            }
            result.append(":\u001b[0m ")
                .append(problem.getMessage());
            if (problem.getRow() > 0) {
                result.append(" at \u001b[1m[")
                    .append(problem.getRow())
                    .append(',')
                    .append(problem.getColumn())
                    .append(']')
                    .append("\u001b[0m");
            }
        }
        return result.toString();
    }
}
