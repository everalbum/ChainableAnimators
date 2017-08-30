package com.everalbum.chainableanimators;

@SuppressWarnings("WeakerAccess")
public interface Cancellable {
    /**
     * @return Whether or not the animation has been cancelled
     */
    boolean isCancelled();

    /**
     * Cancels any in-flight animations.
     */
    void cancel();
}
