package io.github.bananalang.compilecommon.problems;

public final class Problem {
    public static enum ProblemLevel {
        NOTICE,
        WARNING,
        ERROR
    }

    private final ProblemLevel level;
    private final String message;
    private final int row, column;

    public Problem(ProblemLevel level, String message, int row, int column) {
        this.level = level;
        this.message = message;
        this.row = row;
        this.column = column;
    }

    public Problem(ProblemLevel level, String message) {
        this(level, message, 0, 0);
    }

    public static Problem notice(String message, int row, int column) {
        return new Problem(ProblemLevel.NOTICE, message, row, column);
    }

    public static Problem notice(String message) {
        return new Problem(ProblemLevel.NOTICE, message);
    }

    public static Problem warning(String message, int row, int column) {
        return new Problem(ProblemLevel.WARNING, message, row, column);
    }

    public static Problem warning(String message) {
        return new Problem(ProblemLevel.WARNING, message);
    }

    public static Problem error(String message, int row, int column) {
        return new Problem(ProblemLevel.ERROR, message, row, column);
    }

    public static Problem error(String message) {
        return new Problem(ProblemLevel.ERROR, message);
    }

    public ProblemLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(level.toString().toLowerCase())
            .append(": ")
            .append(message);
        if (row > 0) {
            result.append(" at [line ")
                .append(row)
                .append(", column ")
                .append(column)
                .append(']');
        }
        return result.toString();
    }
}
