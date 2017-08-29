package com.everalbum.chainableanimators;

@SuppressWarnings("WeakerAccess")
public interface Cancellable {
    boolean isCancelled();
    void cancel();
}
