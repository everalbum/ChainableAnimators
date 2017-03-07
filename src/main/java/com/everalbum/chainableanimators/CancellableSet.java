package com.everalbum.chainableanimators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CancellableSet implements Cancellable {

    List<Cancellable> cancellables = null;
    private boolean isCancelled;

    public void add(final Cancellable c) {
        isCancelled = false;
        if (cancellables == null) {
            cancellables = new ArrayList<>();
        }
        cancellables.add(c);
    }

    public void addAll(final Cancellable... cancellables) {
        for (Cancellable c : cancellables) {
            add(c);
        }
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void cancel() {
        Collection<Cancellable> cancel;
        isCancelled = true;
        if (cancellables == null) {
            return;
        } else {
            cancel = cancellables;
            cancellables = null;
        }
        for (Cancellable s : cancel) {
            if(!s.isCancelled()) {
                s.cancel();
            }
        }
    }

}
