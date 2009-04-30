package org.mmbase.applications.media;

public enum State {
    REQUEST(1),
    BUSY(2),
    DONE(3),
    SOURCE(4),
    UNDEFINED(-1),
    REMOVED(1);

    private final int value;

    State(int v) {
        value = v;
    }
    public int getValue() {
        return value;
    }

    public static State get(int ordinal) {
        for (State et : State.values()) {
            if (et.ordinal() == ordinal) {
                return et;
            }
        }
        throw new IllegalArgumentException("" + ordinal + " is not an ordinal of state");
    }
}
