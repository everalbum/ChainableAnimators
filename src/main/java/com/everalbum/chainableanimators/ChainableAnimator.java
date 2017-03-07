package com.everalbum.chainableanimators;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.os.Handler;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ChainableAnimator implements Cancellable {
    AnimatorSet chainedAnimators;
    final AnimatorSet currentAnimator;
    final State state;
    boolean isCancelled;

    ChainableAnimator(State state) {
        this.state = state;
        currentAnimator = new AnimatorSet();
        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                cancel();
            }
        });
    }

    public static ChainableAnimator with(Animator a) {
        ChainableAnimator c = new ChainableAnimator(new State());
        c.currentAnimator.play(a);
        return c;
    }

    public static ChainableViewAnimator with(View v) {
        return new ChainableViewAnimator(v, new State());
    }

    public ChainableViewAnimator then(View v) {
        state.addSet(currentAnimator);
        return new ChainableViewAnimator(v, this.state);
    }

    public ChainableViewAnimator inParallelWith(View v) {
        return new ParallelChainableViewAnimator(currentAnimator, v, this.state);
    }

    public ChainableAnimator then(Animator a) {
        state.addSet(currentAnimator);
        ChainableAnimator c = new ChainableAnimator(this.state);
        c.currentAnimator.play(a);
        return c;
    }

    public ChainableAnimator inParallelWith(Animator a) {
        return new ParallelChainableAnimator(currentAnimator, a, this.state);
    }

    public ChainableAnimator setDuration(long duration) {
        currentAnimator.setDuration(duration);
        return this;
    }

    public ChainableAnimator setStartDelay(long startDelay) {
        currentAnimator.setStartDelay(startDelay);
        return this;
    }

    public ChainableAnimator setInterpolator(TimeInterpolator interpolator) {
        currentAnimator.setInterpolator(interpolator);
        return this;
    }

    public ChainableAnimator doOnAnimationStart(final Runnable r) {
        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                r.run();
            }
        });
        return this;
    }

    public ChainableAnimator doOnAnimationCancel(final Runnable r) {
        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                r.run();
            }
        });
        return this;
    }

    public Cancellable start() {
        state.addSet(currentAnimator);
        startAnimations();
        return this;
    }

    protected void startAnimations() {
        chainedAnimators = new AnimatorSet();
        if (state.animatorSets.size() == 1) {
            chainedAnimators.play(state.animatorSets.get(0));
        } else {
            for (int i = 0; i < state.animatorSets.size() - 1; ++i) {
                chainedAnimators.play(state.animatorSets.get(i))
                                .before(state.animatorSets.get(i + 1));
            }
        }
        chainedAnimators.start();
    }

    @Override
    public void cancel() {
        if(isCancelled()) {
            return;
        }
        isCancelled = true;
        clearCallbacks();
        if (chainedAnimators != null) {
            chainedAnimators.cancel();
            chainedAnimators.removeAllListeners();
        }
        state.animatorSets.clear();

    }

    private void clearCallbacks() {
        state.delayHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    public ChainableAnimator doOnAnimationEnd(final Runnable r) {
        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!isCancelled()) {
                    r.run();
                }
            }
        });
        return this;
    }

    public ChainableAnimator doOnAnimationEndDelayed(final Runnable r, final long delay) {
        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!isCancelled()) {
                    state.delayHandler.postDelayed(r, delay);
                }
            }
        });
        return this;
    }

    protected static class State {
        final List<AnimatorSet> animatorSets;
        final Handler           delayHandler;


        private State() {
            animatorSets = new ArrayList<>();
            delayHandler = new Handler();
        }

        protected void addSet(AnimatorSet set) {
            if (!animatorSets.contains(set)) {
                animatorSets.add(set);
            }
        }
    }

}
