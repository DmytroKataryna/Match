package task.test.kataryna.dmytro.match.animation;

import android.animation.Animator;

/**
 * Created by dmytroKataryna on 23.01.16.
 */
public class AnimationListener implements Animator.AnimatorListener {

    private AnimationCompleteListener listener;

    public AnimationListener(AnimationCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        listener.onAnimationComplete();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }
}
