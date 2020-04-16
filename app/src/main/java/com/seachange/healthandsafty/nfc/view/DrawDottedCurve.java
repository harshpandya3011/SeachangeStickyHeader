package com.seachange.healthandsafty.nfc.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.seachange.healthandsafty.R;

public class DrawDottedCurve extends View {

    Path path = new Path();
    Path drawingPath = new Path();
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    float x1;
    float y1;
    float x3;
    float y3;

    public int arrowLength = 10;
    public int arrowWidth = 10;

    CountDownTimer timer;

    public DrawDottedCurve(Context context) {
        super(context);
    }

    public DrawDottedCurve(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DrawDottedCurve(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void init(Context ctx, int a, int b, int c, int d) {

        x1 = a;
        y1 = b;
        x3 = c;
        y3 = d;

        paint.setAlpha(255);
        paint.setStrokeWidth(1);
        paint.setColor(ContextCompat.getColor(ctx, R.color.colorDefaultGreen));
        paint.setStyle(Style.STROKE);

        paint.setAntiAlias(true);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

        linePaint.setAlpha(255);
        linePaint.setAntiAlias(true);
        linePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(1);
        linePaint.setColor(Color.GRAY);
        linePaint.setStyle(Style.STROKE);
        linePaint.setPathEffect(new DashPathEffect(new float[] { 10.0f, 5.0f }, 0));


        path.moveTo(x1, y1); //
        path.quadTo((x3 - x1) / 2, (y3 - y1) / 2, x3, y3); // Calculate Bezier Curve

        final long DRAW_TIME = 2000;
        stopTimer();
        timer = new CountDownTimer(DRAW_TIME, 100) {
            PathMeasure measure = new PathMeasure();
            Path segmentPath = new Path();
            float start = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                measure.setPath(path, false);
                float percent = ((float) (DRAW_TIME - millisUntilFinished))
                        / (float) DRAW_TIME;
                float length = measure.getLength() * percent;
                measure.getSegment(start, length, segmentPath, true);
                start = length;
                drawingPath.addPath(segmentPath);
                Log.d("draw curve", "path");
                invalidate();
            }

            @Override
            public void onFinish() {
                measure.getSegment(start, measure.getLength(), segmentPath,
                        true);
                drawingPath.addPath(segmentPath);
                drawingPath.reset();
                invalidate();
                init(ctx, a, b, c, d);

            }
        };
        timer.start();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, linePaint);
        drawArrow(canvas);
        drawRect(canvas);
        canvas.drawPath(drawingPath, paint);
    }


    private void drawArrow(Canvas canvas) {
        double angle = calculateAngle(x1, y1, x3, y3);
        float final_angle = (float) (180 - angle + 30);
        Path arrow_path = new Path();
        Matrix arrow_matrix = new Matrix();
        arrow_matrix.postRotate(final_angle, x3, y3);
        arrow_path.moveTo(x3, y3);
        arrow_path.lineTo(x3 - arrowWidth, y3 + arrowLength);
        arrow_path.moveTo(x3, y3);
        arrow_path.lineTo(x3 + arrowWidth, y3 + arrowLength);
//        arrow_path.lineTo(x3 - (arrowWidth), y3 + arrowLength);
        arrow_path.transform(arrow_matrix);
        canvas.drawPath(arrow_path, initPaint());
    }

    private void drawRect(Canvas canvas) {

        double angle = calculateAngle(x1, y1, x3, y3);
        float final_angle = (float) (180 - angle + 48);
        Path arrow_path = new Path();
        Matrix arrow_matrix = new Matrix();
        arrow_matrix.postRotate(final_angle, x1-5, y1+5);

        arrow_path.moveTo(x1, y1);
        arrow_path.lineTo(x1 - arrowWidth, y1);

        arrow_path.moveTo(x1 - arrowWidth, y1);
        arrow_path.lineTo(x1 - arrowWidth, y1+arrowWidth);

        arrow_path.moveTo(x1 - arrowWidth, y1+arrowWidth);
        arrow_path.lineTo(x1 , y1 + arrowWidth);

        arrow_path.moveTo(x1 , y1 + arrowWidth);
        arrow_path.lineTo(x1 , y1);

        arrow_path.transform(arrow_matrix);
        canvas.drawPath(arrow_path, initPaintRect());
    }

    private Paint initPaintRect() {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(1);
        return mPaint;
    }

    private Paint initPaint() {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeJoin(Paint.Join.ROUND);
//        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
        return mPaint;
    }

    public double calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        angle = angle + Math.ceil(-angle / 360) * 360; //Keep angle between 0 and 360
        return angle;
    }

    public void stopTimer() {
        if (timer !=null) {
            timer.cancel();
        }
    }
}