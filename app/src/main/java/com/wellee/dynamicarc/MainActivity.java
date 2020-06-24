package com.wellee.dynamicarc;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DynamicArc darc = findViewById(R.id.darc);

        ValueAnimator animator = ObjectAnimator.ofFloat(0, 9000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                darc.setCurrentStep((int) value);
            }
        });
        animator.start();
    }
}
