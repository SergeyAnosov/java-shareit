package ru.practicum.shareitserver.booking;

import java.util.Optional;

public enum State {
    ALL, FUTURE, WAITING, REJECTED, PAST, CURRENT;

    public static Optional<State> from(String stringState) {
        for (State state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
