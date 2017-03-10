package com.everalbum.chainableanimators;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

class ParallelChainableViewAnimator extends ChainableViewAnimator {
    final         List<Animator> inParallel;
    ParallelChainableViewAnimator(AnimatorSet set, State state, View... v) {
        this(state, new ArrayList<Animator>(), v);
        inParallel.add(set);
    }

    ParallelChainableViewAnimator(State state, List<Animator> parallel, View... v) {
        super(state, v);
        this.inParallel = parallel;
    }

    @Override
    public ChainableViewAnimator then(View... v) {
        AnimatorSet parallel = buildParallelAnimatorAndClear();
        state.addSet(parallel);
        return new ChainableViewAnimator(this.state, v);
    }

    @Override
    public ChainableViewAnimator inParallelWith(View... v) {
        if (!animators.isEmpty()) {
            currentAnimator.playTogether(animators);
        }
        inParallel.add(currentAnimator);
        return new ParallelChainableViewAnimator(state, inParallel, v);
    }

    @Override
    public ChainableAnimator then(Animator a) {
        AnimatorSet parallel = buildParallelAnimatorAndClear();
        state.addSet(parallel);
        ChainableAnimator c = new ChainableAnimator(this.state);
        c.currentAnimator.play(a);
        return c;
    }

    @Override
    public ChainableAnimator inParallelWith(Animator a) {
        if (!animators.isEmpty()) {
            currentAnimator.playTogether(animators);
        }
        inParallel.add(currentAnimator);
        return new ParallelChainableAnimator(this.state, a, inParallel);
    }

    @Override
    public Cancellable start() {
        AnimatorSet parallel = buildParallelAnimatorAndClear();
        state.addSet(parallel);
        startAnimations();
        return this;
    }



    private AnimatorSet buildParallelAnimatorAndClear() {
        if(!animators.isEmpty()) {
            currentAnimator.playTogether(animators);
        }
        inParallel.add(currentAnimator);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(inParallel);
        inParallel.clear();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                cancel();
            }

        });
        return set;
    }

}
