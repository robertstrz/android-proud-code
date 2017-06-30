package com.example.rostrzep.proudcode;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class MainActivity extends AppCompatActivity {

    @BindViews({R.id.circle_item_1, R.id.circle_item_2, R.id.circle_item_3, R.id.circle_item_4, R.id.circle_item_5, R.id.circle_item_6, R.id.circle_item_7, R.id.circle_item_8, R.id.circle_item_9, R.id.circle_item_10})
    List<View> logoSingleCircleItems;
    @BindViews({R.id.star_one, R.id.star_two, R.id.star_three})
    List<View> stars;
    @BindView(R.id.rounded_score)
    ImageView roundedScore;
    @BindView(R.id.points_label)
    TextView poinstLabel;
    @BindView(R.id.score_amount)
    TextView scoreAmount;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private Random random;
    private int maxDistance = 350;
    private int minDistance = 150;
    private int difference = maxDistance - minDistance;
    int duration = 300;

    private int percentage;
    private int starAmount = 0;
    private String score;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        random = new Random();

        percentage = 100;
        score = "333";

        if (percentage >= 50 && percentage < 80) {
            starAmount = 1;
        } else if (percentage >= 80 && percentage <= 99) {
            starAmount = 2;
        } else if (percentage > 99) {
            starAmount = 3;
        }

        scoreAmount.setText(score);

        setUpAnimation();

        progressBar.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(0);
            }
        });
    }

    public void setUpAnimation() {
        final LinkedHashMap<View, Point> mapImagePoint = new LinkedHashMap<>();
        for (View view : logoSingleCircleItems) {
            mapImagePoint.put(view, getRandomPoint());
            int signX = random.nextInt(10) > 5 ? 1 : -1;
            int signY = random.nextInt(10) > 5 ? 1 : -1;

            int x = mapImagePoint.get(view).x * signX;
            int y = mapImagePoint.get(view).y * signY;

            view.setTranslationX(x);
            view.setTranslationY(y);
            view.setAlpha(0);
            view.setScaleX(0);
            view.setScaleY(0);

            final AnimatorSet animatorSet = new AnimatorSet();
            final List<Animator> scaleAnimatorWithAlpha = getScaleAnimatorWithAlpha(view, true, 0, 1.3f, 1);
            animatorSet.playTogether(scaleAnimatorWithAlpha);

            final int startDelay = random.nextInt(800 - 200) + 200;
            animatorSet.setStartDelay(startDelay);
            animatorSet.start();

            final TranslateAnimation translateAnimation = new TranslateAnimation(0, -x, 0, -y);
            translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            translateAnimation.setFillAfter(true);
            translateAnimation.setDuration(duration);
            translateAnimation.setStartOffset(startDelay + 200);
            duration += 50;
            view.startAnimation(translateAnimation);
        }

        Observable.interval(2600, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .limit(1)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        startResizeCircleAnimaiton();
                    }
                });
    }

    public void startResizeCircleAnimaiton() {

        for (View view : logoSingleCircleItems) {
            final AnimatorSet animatorSet = new AnimatorSet();
            final Point randomPoint = getRandomPoint();
            int signX = random.nextInt(10) > 5 ? 1 : -1;
            int signY = random.nextInt(10) > 5 ? 1 : -1;
            final List<Animator> scaleAnimatorWithAlphaCircleLogo = getScaleAnimatorWithAlpha(view, false, 1, 1.2f);
            scaleAnimatorWithAlphaCircleLogo.add(ObjectAnimator.ofFloat(view, View.TRANSLATION_X, view.getTranslationX(), randomPoint.x * signX));
            scaleAnimatorWithAlphaCircleLogo.add(ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.getTranslationY(), randomPoint.y * signY));
            animatorSet.playTogether(scaleAnimatorWithAlphaCircleLogo);
            animatorSet.setDuration(getRandomDuration());
            animatorSet.start();
        }

        final AnimatorSet animatorCircle = new AnimatorSet();
        final List<Animator> scaleAnimatorWithAlphaRounedCircle = getScaleAnimatorWithAlpha(roundedScore, true, 0, 3);
        animatorCircle.playTogether(scaleAnimatorWithAlphaRounedCircle);
        animatorCircle.setDuration(400);
        animatorCircle.start();
        animatorCircle.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startTextAndStarAnimation();
            }
        });
    }

    public void startTextAndStarAnimation() {

        int delay = 0;
        if (starAmount == 0) {
            for (View star : stars) {
                star.setVisibility(View.GONE);
            }
        }
        for (int i = 0; i < starAmount; i++) {
            final View star = stars.get(i);
            final AnimatorSet animatorSet = new AnimatorSet();
            final List<Animator> scaleAnimatorStarWithRotation = getScaleAnimator(star, 0, 1.5f, 1);
            scaleAnimatorStarWithRotation.add(ObjectAnimator.ofFloat(star, View.ROTATION, -144f, 0));
            animatorSet.playTogether(scaleAnimatorStarWithRotation);
            animatorSet.setDuration(250);
            animatorSet.setStartDelay(delay += 200);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.start();
        }

        final AnimatorSet scoreAmountAnimation = new AnimatorSet();
        final List<Animator> scaleAnimatorScoreAmount = getScaleAnimator(scoreAmount, 0, 1.3f, 1);
        scoreAmountAnimation.playTogether(scaleAnimatorScoreAmount);

        scoreAmountAnimation.setStartDelay(700);
        scoreAmountAnimation.setDuration(250);
        scoreAmountAnimation.start();

        final AnimatorSet pointLabelAnim = new AnimatorSet();
        final List<Animator> scaleAnimatorPointsLable = getScaleAnimator(poinstLabel, 0, 1.3f, 1);
        pointLabelAnim.playTogether(scaleAnimatorPointsLable);

        pointLabelAnim.setStartDelay(950);
        pointLabelAnim.setDuration(250);
        pointLabelAnim.start();

        final ObjectAnimator progressAnimation = ObjectAnimator.ofInt(progressBar, "progress", 0, percentage);
        progressAnimation.setDuration(2000);
        progressAnimation.setInterpolator(new DecelerateInterpolator());
        progressAnimation.setStartDelay(950);
        progressAnimation.start();

        final ImageView imageView = (ImageView) stars.get(0);
        final TransitionDrawable animationDrawableStage1 = (TransitionDrawable) imageView.getBackground();
        final ImageView imageView2 = (ImageView) stars.get(1);
        final TransitionDrawable animationDrawableStage2 = (TransitionDrawable) imageView2.getBackground();
        final ImageView imageView3 = (ImageView) stars.get(2);
        final TransitionDrawable animationDrawableStage3 = (TransitionDrawable) imageView3.getBackground();
        Observable.interval(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long counter) {
                        switch ((int) (counter % 2)) {
                            case 0:
                                animationDrawableStage1.startTransition(700);
                                animationDrawableStage2.startTransition(700);
                                animationDrawableStage3.startTransition(700);
                                break;
                            case 1:
                                animationDrawableStage1.reverseTransition(700);
                                animationDrawableStage2.reverseTransition(700);
                                animationDrawableStage3.reverseTransition(700);
                                break;
                        }
                    }
                });
    }

    @NonNull
    private List<Animator> getScaleAnimator(View view, float... values) {
        final List<Animator> animators = new ArrayList<>();
        animators.add(ObjectAnimator.ofFloat(view, View.SCALE_X, values));
        animators.add(ObjectAnimator.ofFloat(view, View.SCALE_Y, values));
        return animators;
    }

    @NonNull
    private List<Animator> getScaleAnimatorWithAlpha(View view, boolean isVisibleAlpha, float... values) {
        final List<Animator> animators = new ArrayList<>();
        animators.add(ObjectAnimator.ofFloat(view, View.SCALE_X, values));
        animators.add(ObjectAnimator.ofFloat(view, View.SCALE_Y, values));
        animators.add(ObjectAnimator.ofFloat(view, View.ALPHA,  (isVisibleAlpha) ? 0 : 1, (isVisibleAlpha) ? 1 : 0));
        return animators;
    }

    public Point getRandomPoint() {
        return new Point(random.nextInt(difference) + minDistance, random.nextInt(difference) + minDistance);
    }

    public int getRandomDuration() {
        return random.nextInt(100) + 100;
    }
}
