package dev.louis.promotedserver.api;

public interface PromotedServerInfo {
    default void promotedserver$markAsPromoted() {
        throw new UnsupportedOperationException("BEEP BOOP MIXIN CALLED. THIS IS BIG BIG NONO");
    };

    default boolean promotedserver$isPromoted() {
        throw new UnsupportedOperationException("BEEP BOOP MIXIN CALLED. THIS IS BIG BIG NONO");
    };
}
