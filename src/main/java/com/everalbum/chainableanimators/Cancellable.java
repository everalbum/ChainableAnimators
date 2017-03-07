package com.everalbum.chainableanimators;

public interface Cancellable {
    boolean isCancelled();
    void cancel();
}
