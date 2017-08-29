# ChainableAnimators

[![](https://jitpack.io/v/everalbum/ChainableAnimators.svg)](https://jitpack.io/#everalbum/ChainableAnimators)

A fluent-api library to make multiple Android animations a breeze. 

## Problem 

Chaining multiple animations in series and/or parallel becomes exponentially cumbersome and challenging the more views are involved.
Every set of parallel or series animations requires an AnimatorSet. That quickly becomes unwieldly to manage.
For example, if we had 2 TextViews that animated in parallel and 2 buttons that animated in series after them:
```java
AnimatorSet parallel = new AnimatorSet();
Animator textView1AnimIn = ObjectAnimator.ofFloat(textView1, "translationX", 0, 100);
Animator textView2AnimIn = ObjectAnimator.ofFloat(textView2, "translationX", 0, 100);
parallel.playTogether(textView1AnimIn, textView2AnimIn);
AnimatorSet seq = new AnimatorSet();
Animator button1In = ObjectAnimator.ofFloat(button1, "translationY", 0, 100);
Animator button2In = ObjectAnimator.ofFloat(button2, "translationY", 0, 100);
seq.playSequentially(parallel, button1In, button2In);
```
This is already hard to maintain, but what if we wanted to animate multiple properties at the same time?
```java
AnimatorSet parallel = new AnimatorSet();
Animator textView1AnimInX = ObjectAnimator.ofFloat(textView1, "translationX", 0, 100);
Animator textView2AnimInX = ObjectAnimator.ofFloat(textView2, "translationX", 0, 100);
Animator textView1AnimInAlpha = ObjectAnimator.ofFloat(textView1, "alpha", 0, 1);
Animator textView2AnimInAlpha = ObjectAnimator.ofFloat(textView2, "alpha", 0, 1);
parallel.playTogether(textView1AnimInX, textView2AnimInX, textView1AnimInAlpha, textView2AnimInAlpha);
AnimatorSet seq = new AnimatorSet();
Animator button1In = ObjectAnimator.ofFloat(button1, "translationY", 0, 100);
Animator button2In = ObjectAnimator.ofFloat(button1, "translationY", 0, 100);
Animator button1InAlpha = ObjectAnimator.ofFloat(button1, "alpha", 0, 1);
Animator button2InAlpha = ObjectAnimator.ofFloat(button1, "alpha", 0, 1);
AnimatorSet button1Set = new AnimatorSet();
button1Set.playTogether(button1In, button1InAlpha);
AnimatorSet button2Set = new AnimatorSet();
button2Set.playTogether(button2In, button2InAlpha);
seq.playSequentially(parallel, button1Set, button2Set);
```
Boy, did that get out of hand.

## Solution: ChainableAnimators

ChainableAnimators provides an easy-to-use, fluent api that eliminates all the junk boilerplate:

```java
ChainableAnimator.with(textView1, textView2)
                 .alpha(0, 1)
                 .translationX(0, 100)
                 .then(button1)
                 .translationY(0, 100)
                 .alpha(0, 1)
                 .then(button2)
                 .translationY(0, 100)
                 .alpha(0, 1)
                 .start();              
```

Phew, my eyes can rest easy now.

## Installing

Add this to your root build.gradle file:
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

and this to your dependencies:

`compile 'com.github.everalbum:ChainableAnimators:v1.0.0'`

### Getting Started

The ChainableAnimator class provides two static entry points to get started:
1) `ChainableAnimator.with(Animator a)` which takes an existing animator
2) `ChainableAnimator.with(View... views)` which takes one or more views. If multiple views are supplied, their animations will be played in parallel.

### Animations made easy

ChainableAnimator provides convenient methods for nearly all view properties to make complex animations seem easy. `translationX`, `y`, `alpha`, `rotation`, `scale` and many more, as well as their `By` counterparts (e.g. `rotationBy`) are included by default. There's even support for `z` (or elevation) animations, which work for api > 21 but are no-ops for older versions.

### Animation Lifecycle Hooks

The ChainableAnimator class provides 4 animation lifecycle hooks: `doOnAnimationStart(Runnable)`, `doOnAnimationCancelled(Runnable)`, `doOnAnimationEnd(Runnable)`
and `doOnAnimationEndDelayed(Runnable, long)`. It is often times advantageous to make a view invisible/gone 100 ms or so after the animation is done, to prevent
jumpiness. 
These lifecycle hooks work per set of animations:
```java
ChainableAnimator.with(view1)
                 .doOnStart( () -> view1.setVisibility(VISIBLE))
                 .alpha(0, 1)
                 .setDuration(300)
                 .then(view2)
                 .alpha(0, 1)
                 .doOnStart( () -> view2.setVisiblity(VISIBLE))
                 .start();
```
`view1` will be set to visible when its alpha animation starts, and `view2` will be set to visible as soon as its alpha animation starts.
This applies to parallel animations as well.

ChaniableAnimator also provides hooks for the total of all animations: `doOnOverallAnimationStart()`, `doOnOverallAnimationCancel()`,
`doOnOverallAnimationEnd()` and `doOnOverallAnimationEndDelayed()`. `doOnOverallAnimationStart()` is called when the first animation runs, and `doOnOverallAnimationEnd()` is called
when the last animation finishes. 

### Clean up

The `start()` method returns a instance of the `Cancellable` interface. Calling `cancel()` on this object will cancel any in flight animations and
prevent any future ones from starting. As well, it will also clean up all animation lifecycle hooks, to prevent memory leaks.
The [CancellableSet](https://github.com/everalbum/ChainableAnimators/blob/master/src/main/java/com/everalbum/chainableanimators/CancellableSet.java) class 
can be used to group up multiple animations and cancel them all at once when the activity/fragment goes out of view.

