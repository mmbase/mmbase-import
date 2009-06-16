package org.mmbase.applications.media;

public enum State {
    /**
     * The state is 'requested', which means that the media source does not yet have any data.
     */
    REQUEST(1),
    /**
     * This means that the media source is currently being generated.
     */
    BUSY(2),
    /**
     * This means that the media source is  generated and that is ready.
     */
    DONE(3),
    /**
     * This means that the media source is an original.
     */
    SOURCE(4),
    UNDEFINED(-1),
    REMOVED(10);

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
