package com.everalbum.chainableanimators;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Extension of {@link ChainableAnimator} that allows view properties to be animated and chained.
 * If multiple views are supplied through {@link ChainableAnimator#with(View...)}, {@link ChainableAnimator#then(View...)}
 * or {@link ChainableAnimator#inParallelWith(View...)}, then the requested property animations will
 * be run on all given views in parallel.
 */
public class ChainableViewAnimator extends ChainableAnimator {
    private View[] views;
    final List<Animator> animators = new ArrayList<>();

    ChainableViewAnimator(State state, View... v) {
        super(state);
        if (v == null || v.length == 0) {
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
     * Animates the height of the given views to the supplied value, without affecting margins
     * WARNING: this method may not behave as expected as it directly affects the dimensions of the
     * views.
     * @param value height to animate to
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator height(int value) {
        if (value == 0) {
            return height(0, 0, 0);
        } else {
            return height(value, Integer.MIN_VALUE, Integer.MIN_VALUE);
        }
    }

    /**
     * Animates the height of the given views to the supplied value, while taking into account margins.
     * WARNING: this method may not behave as expected as it directly affects the dimensions of the
     * views.
     * @param value height to animate to
     * @param endTopMargin final top margin
     * @param endBottomMargin  final bottom margin
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator height(int value, int endTopMargin, int endBottomMargin) {
        if (value < 0) {
            throw new IllegalArgumentException("Target height must be greater than 0.");
        }
        final int endingHeight = value;
        final boolean resizeMarginTop = endTopMargin > Integer.MIN_VALUE;
        final boolean resizeMarginBottom = endBottomMargin > Integer.MIN_VALUE;
        for (final View view : views) {
            int tempTop = 0, tempBottom = 0;
            final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            Field tempTopField = null, tempBottomField = null;
            if (resizeMarginTop) {
                try {
                    tempTopField = layoutParams.getClass()
                                               .getField("topMargin");
                    tempTop = tempTopField.getInt(layoutParams);
                } catch (NoSuchFieldException ignored) {
                } catch (IllegalAccessException ignored) {
                }
            }
            if (resizeMarginBottom) {
                try {
                    tempBottomField = layoutParams.getClass()
                                                  .getField("bottomMargin");
                    tempBottom = tempBottomField.getInt(layoutParams);
                } catch (NoSuchFieldException ignored) {
                } catch (IllegalAccessException ignored) {
                }
            }
            final int topMargin = tempTop, bottomMargin = tempBottom;
            final Field topMarginField = tempTopField, bottomMarginField = tempBottomField;
            final int startingHeight = view.getHeight();
            final int distance = endingHeight - startingHeight;
            final int topMarginDistance = endTopMargin - topMargin;
            final int bottomMarginDistance = endBottomMargin - bottomMargin;
            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    Float value = (Float) animator.getAnimatedValue();
                    layoutParams.height =
                            (int) (value * distance + startingHeight);
                    if (topMarginField != null && resizeMarginTop) {
                        try {
                            topMarginField.setInt(layoutParams,
                                                  (int) (value * topMarginDistance) + topMargin);
                        } catch (IllegalAccessException ignored) {
                        }
                    }
                    if (bottomMarginField != null && resizeMarginBottom) {
                        try {
                            bottomMarginField.setInt(layoutParams,
                                                     (int) (value * bottomMarginDistance) + bottomMargin);
                        } catch (IllegalAccessException ignored) {
                        }
                    }
                    view.requestLayout();
                }
            });
            animators.add(animator);
        }
        return this;
    }

    /**
     * Animates the width of the given views to the supplied value, without affecting margins
     * WARNING: this method may not behave as expected as it directly affects the dimensions of the
     * views.
     * @param value width to animate to
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator width(int value) {
        if (value == 0) {
            return width(0, 0, 0);
        } else {
            return width(value, Integer.MIN_VALUE, Integer.MIN_VALUE);
        }
    }

    /**
     * Animates the width of the given views to the supplied value, while taking into account margins.
     * WARNING: this method may not behave as expected as it directly affects the dimensions of the
     * views.
     * @param value height to animate to
     * @param endStartMargin final start margin
     * @param endEndMargin  final end margin
     * @return This object, allowing calls to methods in this class to be chained.
     */
    public ChainableViewAnimator width(int value, int endStartMargin, final int endEndMargin) {
        if (value < 0) {
            throw new IllegalArgumentException("Target height must be greater than 0.");
        }
        final int endingWidth = value;
        final boolean resizeMarginStart = endStartMargin > Integer.MIN_VALUE;
        final boolean resizeMarginEnd = endEndMargin > Integer.MIN_VALUE;
        for (final View view : views) {
            int tempStart = 0, tempEnd = 0;
            final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            Method tempSetStart = null, tempSetEnd = null;
            if (resizeMarginStart) {
                try {
                    Method getStart = layoutParams.getClass()
                                               .getMethod("getMarginStart");
                    tempSetStart = layoutParams.getClass()
                                               .getMethod("setMarginStart", int.class);
                    tempStart = (int) getStart.invoke(layoutParams);
                } catch (NoSuchMethodException ignored) {
                } catch (IllegalAccessException ignored) {
                } catch (InvocationTargetException ignored) {
                }
            }
            if (resizeMarginEnd) {
                try {
                    Method getEnd = layoutParams.getClass()
                                                 .getMethod("getMarginEnd");
                    tempSetEnd = layoutParams.getClass()
                                               .getMethod("setMarginEnd", int.class);
                    tempEnd = (int) getEnd.invoke(layoutParams);
                } catch (IllegalAccessException ignored) {
                    ignored.printStackTrace();
                } catch (NoSuchMethodException ignored) {
                } catch (InvocationTargetException ignored) {
                }
            }
            final int startMargin = tempStart, endMargin = tempEnd;
            final Method setStartMargin = tempSetStart, setEndMargin = tempSetEnd;
            final int startingWidth = view.getWidth();
            final int distance = endingWidth - startingWidth;
            final int startMarginDistance = endStartMargin - startMargin;
            final int endMarginDistance = endEndMargin - endMargin;
            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    Float value = (Float) animator.getAnimatedValue();
                    layoutParams.width =
                            (int) (value * distance + startingWidth);
                    if (setStartMargin != null && resizeMarginStart) {
                        try {
                            setStartMargin.invoke(layoutParams, (int) (value * startMarginDistance) + startMargin);
                        } catch (IllegalAccessException ignored) {
                        } catch (InvocationTargetException ignored) {
                        }
                    }
                    if (setEndMargin != null && resizeMarginEnd) {
                        try {
                            setEndMargin.invoke(layoutParams, (int) (value * endMarginDistance) + endMargin);
                        } catch (IllegalAccessException ignored) {
                        } catch (InvocationTargetException ignored) {
                        }
                    }
                    view.requestLayout();
                }
            });
            animators.add(animator);
        }
        return this;
    }

    /**
     * This method will cause the Views' <code>x</code> property to be animated to the
     * specified value.
     *
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setX(float)
     */
    public ChainableViewAnimator x(float... values) {
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "x", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>x</code> property to be animated by the
     * specified value.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setX(float)
     */
    public ChainableViewAnimator xBy(float value) {
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "x", view.getX(), view.getX() + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>y</code> property to be animated to the
     * specified value.
     *
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setY(float)
     */
    public ChainableViewAnimator y(float... values) {
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "y", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>y</code> property to be animated by the
     * specified value.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setY(float)
     */
    public ChainableViewAnimator yBy(float value) {
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "y", view.getY(), view.getY() + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>z</code> property to be animated to the
     * specified value. This method will do nothing on api < 21.
     *
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setZ(float)
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ChainableViewAnimator z(float... values) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return this;
        }
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "z", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>z</code> property to be animated by the
     * specified value. This method will do nothing on api < 21.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setZ(float)
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ChainableViewAnimator zBy(float value) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return this;
        }
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "z", view.getZ(), view.getZ() + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>rotation</code> property to be animated to the
     * specified value.
     *
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setRotation(float)
     */
    public ChainableViewAnimator rotation(float... values) {
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "rotation", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>rotation</code> property to be animated by the
     * specified value.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setRotation(float)
     */
    public ChainableViewAnimator rotationBy(float value) {
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view,
                                                 "rotation",
                                                 view.getRotation(),
                                                 view.getRotation() + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>rotationX</code> property to be animated to the
     * specified value.
     *
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setRotationX(float)
     */
    public ChainableViewAnimator rotationX(float... values) {
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "rotationX", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>rotationX</code> property to be animated by the
     * specified value.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setRotationX(float)
     */
    public ChainableViewAnimator rotationXBy(float value) {
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view,
                                                 "rotationX",
                                                 view.getRotationX(),
                                                 view.getRotationX() + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>rotationY</code> property to be animated to the
     * specified value.
     *
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setRotationY(float)
     */
    public ChainableViewAnimator rotationY(float... values) {
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "rotationY", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>rotationY</code> property to be animated by the
     * specified value.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setRotationY(float)
     */
    public ChainableViewAnimator rotationYBy(float value) {
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view,
                                                 "rotationY",
                                                 view.getRotationY(),
                                                 view.getRotationY() + value));
        }

        return this;
    }

    /**
     * This method will cause the View's <code>translationX</code> property to be animated to the
     * specified value.
     *
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setTranslationX(float)
     */
    public ChainableViewAnimator translationX(float... values) {
        for (View view : views) {
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
     * specified value.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setTranslationX(float)
     */
    public ChainableViewAnimator translationXBy(float value) {
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view,
                                                 "translationX",
                                                 view.getTranslationX(),
                                                 view.getTranslationX() + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>translationY</code> property to be animated to the
     * specified value.
     *
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setTranslationY(float)
     */
    public ChainableViewAnimator translationY(float... values) {
        for (View view : views) {
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
     * specified value.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setTranslationY(float)
     */
    public ChainableViewAnimator translationYBy(float value) {
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view,
                                                 "translationY",
                                                 view.getTranslationY(),
                                                 view.getTranslationY() + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>translationZ</code> property to be animated to the
     * specified value. This method will do nothing on api < 21.
     *
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setTranslationZ(float)
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ChainableViewAnimator translationZ(float... values) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return this;
        }
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "translationZ", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>translationZ</code> property to be animated by the
     * specified value. This method will do nothing on api < 21.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setTranslationZ(float)
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ChainableViewAnimator translationZBy(float value) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return this;
        }
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view,
                                                 "translationZ",
                                                 ViewCompat.getTranslationZ(view),
                                                 ViewCompat.getTranslationZ(view) + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>scaleX</code> property to be animated to the
     * specified value.
     *
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setScaleX(float)
     */
    public ChainableViewAnimator scaleX(float... values) {
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "scaleX", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>scaleX</code> property to be animated by the
     * specified value.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setScaleX(float)
     */
    public ChainableViewAnimator scaleXBy(float value) {
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view,
                                                 "scaleX",
                                                 view.getScaleX(),
                                                 view.getScaleX() + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>scaleY</code> property to be animated to the
     * specified value.
     *
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setScaleY(float)
     */
    public ChainableViewAnimator scaleY(float... values) {
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view, "scaleY", values));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>scaleY</code> property to be animated by the
     * specified value.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setScaleY(float)
     */
    public ChainableViewAnimator scaleYBy(float value) {
        for (View view : views) {
            animators.add(ObjectAnimator.ofFloat(view,
                                                 "scaleY",
                                                 view.getScaleX(),
                                                 view.getScaleY() + value));
        }
        return this;
    }

    /**
     * This method will cause the View's <code>alpha</code> property to be animated to the
     * specified value.
     *
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setAlpha(float)
     */
    public ChainableViewAnimator alpha(float... values) {
        for (View view : views) {
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
     * specified value.
     *
     * @param value The amount to be animated by, as an offset from the current value.
     * @return This object, allowing calls to methods in this class to be chained.
     * @see View#setAlpha(float)
     */
    public ChainableViewAnimator alphaBy(float value) {
        for (View view : views) {
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
