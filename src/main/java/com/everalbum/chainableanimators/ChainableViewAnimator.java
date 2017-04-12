package com.everalbum.chainableanimators;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ChainableViewAnimator extends ChainableAnimator {
    private       View[]        views;
    final List<Animator> animators = new ArrayList<>();

    ChainableViewAnimator(State state, View... v) {
        super(state);
        if(v == null || v.length == 0) {
            throw new IllegalArgumentException("Require at least one view to be able to animate");
        }
        views = v;
    }

    @Override
    public ChainableViewAnimator then(View... v) {
        if (!animators.isEmpty()) {
            currentAnimator.playTogether(animators);
        }
        return super.then(v);
    }

    @Override
    public ChainableViewAnimator inParallelWith(View... v) {
        if (!animators.isEmpty()) {
            currentAnimator.playTogether(animators);
        }
        return super.inParallelWith(v);
    }

    @Override
    public ChainableAnimator then(Animator a) {
        if (!animators.isEmpty()) {
            currentAnimator.playTogether(animators);
        }
        return super.then(a);
    }

    @Override
    public ChainableAnimator inParallelWith(Animator a) {
        if (!animators.isEmpty()) {
            currentAnimator.playTogether(animators);
        }
        return super.inParallelWith(a);
    }

    @Override
    public ChainableViewAnimator setDuration(long duration) {
        return (ChainableViewAnimator) super.setDuration(duration);
    }

    @Override
    public ChainableViewAnimator setStartDelay(long startDelay) {
        return (ChainableViewAnimator) super.setStartDelay(startDelay);
    }

    @Override
    public ChainableViewAnimator setInterpolator(TimeInterpolator interpolator) {
        return (ChainableViewAnimator) super.setInterpolator(interpolator);
    }

    @Override
    public ChainableViewAnimator doOnAnimationStart(Runnable r) {
        return (ChainableViewAnimator) super.doOnAnimationStart(r);
    }

    @Override
    public ChainableViewAnimator doOnAnimationCancel(Runnable r) {
        return (ChainableViewAnimator) super.doOnAnimationCancel(r);
    }

    @Override
    public ChainableViewAnimator doOnAnimationEnd(Runnable r) {
        return (ChainableViewAnimator) super.doOnAnimationEnd(r);
    }

    /**
     * This method will cause the View's <code>x</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @see View#setX(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator x(float... values) {
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "x", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>x</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setX(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator xBy(float value) {
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "x", view.getX(), view.getX() + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>y</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @see View#setY(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator y(float... values) {
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "y", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>y</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setY(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator yBy(float value) {
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "y", view.getY(), view.getY() + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>z</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @see View#setZ(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator z(float... values) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return this;
        }
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "z", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>z</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setZ(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ChainableViewAnimator zBy(float value) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return this;
        }
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "z", view.getZ(), view.getZ() + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>rotation</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @see View#setRotation(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator rotation(float... values) {
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "rotation", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>rotation</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setRotation(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator rotationBy(float value) {
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view,
                                                 "rotation",
                                                 view.getRotation(),
                                                 view.getRotation() + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>rotationX</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @see View#setRotationX(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator rotationX(float... values) {
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "rotationX", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>rotationX</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setRotationX(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator rotationXBy(float value) {
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view,
                                                 "rotationX",
                                                 view.getRotationX(),
                                                 view.getRotationX() + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>rotationY</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @see View#setRotationY(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator rotationY(float... values) {
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "rotationY", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>rotationY</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setRotationY(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator rotationYBy(float value) {
        for(View view : views) {
        animators.add(ObjectAnimator.ofFloat(view, "rotationY", view.getRotationY(), view.getRotationY() + value));}

        return this;
    }

    /**
     * This method will cause the View's <code>translationX</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @see View#setTranslationX(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator translationX(float... values) {
        for(View view : views) {
            if (values != null && values.length > 1) {
                // set the starting value
                view.setTranslationX(values[0]);
            }
            animators.add(ObjectAnimator.ofFloat(view, "translationX", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>translationX</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setTranslationX(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator translationXBy(float value) {
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view,
                                                 "translationX",
                                                 view.getTranslationX(),
                                                 view.getTranslationX() + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>translationY</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @see View#setTranslationY(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator translationY(float... values) {
        for(View view : views) {
            if (values != null && values.length > 1) {
                // set the starting value
                view.setTranslationY(values[0]);
            }
            animators.add(ObjectAnimator.ofFloat(view, "translationY", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>translationY</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setTranslationY(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator translationYBy(float value) {
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view,
                                                 "translationY",
                                                 view.getTranslationY(),
                                                 view.getTranslationY() + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>translationZ</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @see View#setTranslationZ(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator translationZ(float... values) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return this;
        }
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "translationZ", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>translationZ</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setTranslationZ(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ChainableViewAnimator translationZBy(float value) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return this;
        }
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view,
                                                 "translationZ",
                                                 ViewCompat.getTranslationZ(view),
                                                 ViewCompat.getTranslationZ(view) + value));
        }
        return this;
    }
    /**
     * This method will cause the View's <code>scaleX</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @see View#setScaleX(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator scaleX(float... values) {
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "scaleX", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>scaleX</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setScaleX(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator scaleXBy(float value) {
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view,
                                                 "scaleX",
                                                 view.getScaleX(),
                                                 view.getScaleX() + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>scaleY</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @see View#setScaleY(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator scaleY(float... values) {
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "scaleY", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>scaleY</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setScaleY(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator scaleYBy(float value) {
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view,
                                                 "scaleY",
                                                 view.getScaleX(),
                                                 view.getScaleY() + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>alpha</code> property to be animated to the
     * specified value. Animations already running on the property will be canceled.
     *
     * @see View#setAlpha(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator alpha(float... values) {
        for(View view : views) {
            if (values != null && values.length > 1) {
                // set the starting alpha
                view.setAlpha(values[0]);
            }
            animators.add(ObjectAnimator.ofFloat(view, "alpha", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>alpha</code> property to be animated by the
     * specified value. Animations already running on the property will be canceled.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @see View#setAlpha(float)
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator alphaBy(float value) {
        for(View view : views) {
            animators.add(ObjectAnimator.ofFloat(view,
                                                 "alpha",
                                                 view.getAlpha(),
                                                 view.getAlpha() + value));
        }
        return this;
    }

    @Override
    public Cancellable start() {
        currentAnimator.playTogether(animators);
        return super.start();
    }
}
