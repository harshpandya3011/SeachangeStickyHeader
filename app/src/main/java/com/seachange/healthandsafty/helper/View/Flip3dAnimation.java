package com.seachange.healthandsafty.helper.View;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class Flip3dAnimation extends Animation {
    private final float mFromDegrees;
    private final float mToDegrees;
    private final float mCenterX;
    private final float mCenterY;
    private Camera mCamera;

    public Flip3dAnimation(View view) {
        mFromDegrees = 0;
        mToDegrees = 720;
        mCenterX = view.getWidth() / 2.0f;
        mCenterY = view.getHeight() / 2.0f;
    }

    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    public void applyPropertiesInRotation()
    {
        this.setDuration(1000);
        this.setFillAfter(true);
        this.setInterpolator(new AccelerateInterpolator());
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees
                + ((mToDegrees - fromDegrees) * interpolatedTime);

        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;

        final Matrix matrix = t.getMatrix();

        camera.save();

//        Log.e("Degree",""+degrees) ;
//        Log.e("centerX",""+centerX) ;
//        Log.e("centerY",""+centerY) ;

        camera.rotateY(degrees);

        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);

    }

}

