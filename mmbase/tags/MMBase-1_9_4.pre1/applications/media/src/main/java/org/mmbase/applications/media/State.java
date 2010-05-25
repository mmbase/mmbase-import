package org.mmbase.applications.media;

public enum State {
    /**
     * The state is 'requested', which means that the media source does not yet have any data.
     */
    REQUEST(1),
    /**
     * The media source is currently being generated, typically being transcoded.
     */
    BUSY(2),
    /**
     * The media source is generated, typically transcoded, and is ready.
     */
    DONE(3),
    /**
     * This means that the media source is an original.
     */
    SOURCE(4),
    /**
     * Generation of other media from this media source is not supported by this system, the source
     * has an unknown or unsupported format or codec.
     */
    SOURCE_UNSUPPORTED(9),
    UNDEFINED(-1),
    REMOVED(10),
    /**
     * Transcoding of the media source failed.
     */
    FAILED(19),
    /**
     * Transcoding of the media source is interrupted.
     */
    INTERRUPTED(20);

    private final int value;

    State(int v) {
        value = v;
    }
    public int getValue() {
        return value;
    }

    public static State get(int value) {
        for (State et : State.values()) {
            if (et.value == value) {
                return et;
            }
        }
        throw new IllegalArgumentException("" + value + " is not a state");
    }
}
