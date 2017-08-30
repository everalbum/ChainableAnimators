package com.everalbum.chainableanimators;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.os.Handler;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows chaining of multiple complex animations using a fluent-api:
 * <pre>
 *     ChainableAnimator.with(textView1, textView2)
                         .alpha(0, 1)
                         .translationX(0, 100)
                         .then(button1)
                         .translationY(0, 100)
                         .alpha(0, 1)
                         .then(button2)
                         .translationY(0, 100)
                         .alpha(0, 1)
                         .start();
 * </pre><
 */
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

    /**
     * Starts an animation chain with the provided animator
     * @param a Animator with which to start the chain
     * @return an instance of {@link ChainableAnimator}
     */
    public static ChainableAnimator with(Animator a) {
        ChainableAnimator c = new ChainableAnimator(new State());
        c.currentAnimator.play(a);
        return c;
    }

    /**
     * Starts an animation chain with the provided views. If multiple views are supplied, the
     * ensuing animations will be played in parallel.
     * @param v one or more views to start an animation chain with.
     * @return an instance of {@link ChainableViewAnimator} which allows view properties to be
     * animated
     */
    public static ChainableViewAnimator with(View... v) {
        return new ChainableViewAnimator(new State(), v);
    }

    /**
     * Adds the provided views to be played in series, after the current animation finishes. Note that
     * if multiple views are provided, they will be added to the animation chain collectively (in
     * parallel with each other).
     * @param v one or more views to play in series
     * @return an instance of {@link ChainableAnimator}
     */
    public ChainableViewAnimator then(View... v) {
        state.addSet(currentAnimator);
        return new ChainableViewAnimator(this.state, v);
    }

    /**
     * Adds the provided views to be played in parallel, with the current animation.
     * @param v one or more views to play in parallel
     * @return an instance of {@link ChainableAnimator}
     */
    public ChainableViewAnimator inParallelWith(View... v) {
        return new ParallelChainableViewAnimator(currentAnimator, this.state, v);
    }

    /**
     * Adds the provided animator to the animation chain, to be played in series
     * @param a Animator to play next
     * @return an instance of {@link ChainableAnimator}
     */
    public ChainableAnimator then(Animator a) {
        state.addSet(currentAnimator);
        ChainableAnimator c = new ChainableAnimator(this.state);
        c.currentAnimator.play(a);
        return c;
    }

    /**
     * Adds the provided animator to be played in parallel, with the current animation
     * @param a Animator to play in parallel
     * @return an instance of {@link ChainableAnimator}
     */
    public ChainableAnimator inParallelWith(Animator a) {
        return new ParallelChainableAnimator(currentAnimator, a, this.state);
    }

    /**
     * Sets the duration of the current set of animations
     * @param duration duration of the animation in ms
     * @return an instance of {@link ChainableAnimator}
     */
    public ChainableAnimator setDuration(long duration) {
        currentAnimator.setDuration(duration);
        return this;
    }

    /**
     * Sets the start delay for the current set of animations
     * @param startDelay start delay in ms
     * @return an instance of {@link ChainableAnimator}
     */
    public ChainableAnimator setStartDelay(long startDelay) {
        currentAnimator.setStartDelay(startDelay);
        return this;
    }

    /**
     * Sets the interpolator for the current set of animations
     * @param interpolator {@link TimeInterpolator}
     * @return an instance of {@link ChainableAnimator}
     */
    public ChainableAnimator setInterpolator(TimeInterpolator interpolator) {
        currentAnimator.setInterpolator(interpolator);
        return this;
    }

    /**
     * Runs the provided runnable at the start of the current set of animations
     * @param r {@link Runnable} to run at animation start
     * @return an instance of {@link ChainableAnimator}
     */
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

    /**
     * Runs the provided runnable if the current set of animations (or the overall chain) gets
     * cancelled
     * @param r {@link Runnable} to run at animation cancel
     * @return an instance of {@link ChainableAnimator}
     */
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

    /**
     * Runs the provided runnable at the end of the current set of animations
     * @param r {@link Runnable} to run at animation end
     * @return an instance of {@link ChainableAnimator}
     */
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


    /**
     * Runs the provided runnable at the end of the current set of animations, with a delay.
     * @param r {@link Runnable} to run at animation end
     * @param delay delay in ms
     * @return an instance of {@link ChainableAnimator}
     */
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

    /**
     * Sets the duration for the entire chain of animations
     * @param duration duration of the animation in ms
     * @return an instance of {@link ChainableAnimator}
     */
    public ChainableAnimator setOverallDuration(long duration) {
        if(chainedAnimators == null) {
            chainedAnimators = new AnimatorSet();
        }
        chainedAnimators.setDuration(duration);
        return this;
    }

    /**
     * Sets the start delay for the entire chain of animations
     * @param startDelay delay in ms
     * @return an instance of {@link ChainableAnimator}
     */
    public ChainableAnimator setOverallStartDelay(long startDelay) {
        if(chainedAnimators == null) {
            chainedAnimators = new AnimatorSet();
        }
        chainedAnimators.setStartDelay(startDelay);
        return this;
    }

    /**
     * Sets the interpolator for the entire chain of animations
     * @param interpolator {@link TimeInterpolator}
     * @return an instance of {@link ChainableAnimator}
     */
    public ChainableAnimator setOverallInterpolator(TimeInterpolator interpolator) {
        if(chainedAnimators == null) {
            chainedAnimators = new AnimatorSet();
        }
        chainedAnimators.setInterpolator(interpolator);
        return this;
    }

    /**
     * Runs the provided runnable at the start of the entire chain of animations. In other words,
     * when the very first animation runs.
     * @param r {@link Runnable} to run at animation start
     * @return an instance of {@link ChainableAnimator}
     */
    public ChainableAnimator doOnOverallAnimationStart(final Runnable r) {
        if(chainedAnimators == null) {
            chainedAnimators = new AnimatorSet();
        }
        chainedAnimators.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                r.run();
            }
        });
        return this;
    }

    /**
     * Runs the provided runnable if the overall chain gets cancelled
     * @param r {@link Runnable} to run at animation cancel
     * @return an instance of {@link ChainableAnimator}
     */
    public ChainableAnimator doOnOverallAnimationCancel(final Runnable r) {
        if(chainedAnimators == null) {
            chainedAnimators = new AnimatorSet();
        }
        chainedAnimators.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                r.run();
            }
        });
        return this;
    }

    /**
     * Runs the provided runnable at the end of the entire chain of animations. In other words,
     * when the very last animation finishes.
     * @param r {@link Runnable} to run at animation end
     * @return an instance of {@link ChainableAnimator}
     */
    public ChainableAnimator doOnOverallAnimationEnd(final Runnable r) {
        if(chainedAnimators == null) {
            chainedAnimators = new AnimatorSet();
        }
        chainedAnimators.addListener(new AnimatorListenerAdapter() {
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

    /**
     * Runs the provided runnable at the end of the entire chain of animations, with a delay.
     * @param r {@link Runnable} to run at animation end
     * @param delay delay in ms
     * @return an instance of {@link ChainableAnimator}
     */
    public ChainableAnimator doOnOverallAnimationEndDelayed(final Runnable r, final long delay) {
        if(chainedAnimators == null) {
            chainedAnimators = new AnimatorSet();
        }
        chainedAnimators.addListener(new AnimatorListenerAdapter() {
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

    /**
     * Starts the entire chain of animations. Returns a {@link Cancellable} that can be used to
     * cancel in-flight animations.
     * @return {@link Cancellable} to cancel any current and future animations
     */
    public Cancellable start() {
        state.addSet(currentAnimator);
        startAnimations();
        return this;
    }

    protected void startAnimations() {
        if(chainedAnimators == null) {
            chainedAnimators = new AnimatorSet();
        }
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
