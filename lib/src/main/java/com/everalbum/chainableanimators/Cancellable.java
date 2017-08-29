package com.everalbum.chainableanimators;

interface Cancellable {
    boolean isCancelled();
    void cancel();
}
