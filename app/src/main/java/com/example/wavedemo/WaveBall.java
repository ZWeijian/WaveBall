package com.example.wavedemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
public class WaveBall extends View {
    private int during = 1500;                         //动画持续时间
    private Paint mWavePaint;                          //波浪画笔
    private Path newPath;                              //画圆和裁剪圆形
    private Path mPath;                                //画波浪
    private int mWaveLength = 1000;                    //波浪长度
    private Paint textPaint;                           //文字画笔
    private int dx;
    private int radius;                                //圆半径
    private float radiusX;                             //圆心X坐标
    private float radiusY;                             //圆心Y坐标
    private int originY;                               //波浪初始位置
    private int totalY;                                //屏幕高度
    private int totalX;                                //屏幕宽度
    private int percent = 0;                           //百分比
    private int speed = -5;                            //控制波浪上升速度（负值）或者下降速度（正值）
    private Paint mCirclePaint;                        //画圆的画笔
    private boolean FLAG_START = false;                //控制波浪是否运动(上升/下降)
    private int textColor = Color.BLACK;               //字体颜色
    private int textSize = 100;                        //字体大小
    private int circleColor = Color.BLACK;             //圆形颜色
    private int waveColor = Color.GREEN;               //波浪颜色
    private boolean isCenterInParent = true;           //圆心是否需要位于屏幕中央
    private int marginRight = 100;                     //圆距离屏幕最右侧的距离(isCenterInParent为true时生效)
    private int marginBottom = 100;                    //圆距离屏幕最下方的距离(isCenterInParent为true时生效)


    public WaveBall(Context context) {
        super(context);
    }

    public WaveBall(Context context, AttributeSet attrs) {
        super(context, attrs);
        mWavePaint = new Paint();
        mPath = new Path();
        newPath = new Path();
        mWavePaint.setColor(waveColor);
        mWavePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCirclePaint = new Paint();
        mCirclePaint.setColor(circleColor);
        mCirclePaint.setStrokeWidth(10);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        totalX = w;
        radius = totalX / 4;
        totalY = h;
        if (isCenterInParent) {
            radiusX = totalX / 2;
            radiusY = totalY / 2;
            originY = totalY / 2 + radius;
        } else {
            radiusX = totalX - marginRight - radius;
            radiusY = totalY - marginBottom - radius;
            originY = totalY - marginBottom;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        newPath.addCircle(radiusX, radiusY, radius, Path.Direction.CCW);
        canvas.drawCircle(radiusX, radiusY, radius, mCirclePaint);
        canvas.clipPath(newPath);
        mPath.reset();
        if (percent < 100 && FLAG_START && percent >= 0) {
            originY += speed;
        }
        if (percent > 100) {
            percent = 100;
        }
        if (percent < 0) {
            percent = 0;
        }
        int HalfWaveLength = mWaveLength / 2;
        mPath.moveTo(-mWaveLength + dx, originY);
        for (int i = -mWaveLength; i <= getWidth() + mWaveLength; i += mWaveLength) {
            mPath.rQuadTo(HalfWaveLength / 2, -100, HalfWaveLength, 0);
            mPath.rQuadTo(HalfWaveLength / 2, 100, HalfWaveLength, 0);
        }
        mPath.lineTo(radiusX+radius, radiusY + radius);
        mPath.lineTo(radiusX-radius, radiusY + radius);
        mPath.close();
        percent = 100 - (int) (((float) (originY - (radiusY - radius)) / (float) (radius * 2)) * 100);
        canvas.drawPath(mPath, mWavePaint);
        if(percent!=100){
            canvas.drawText(percent + "%", radiusX - 75, radiusY, textPaint);
        }else if(percent>0){
            canvas.drawText(percent + "%", radiusX - 100, radiusY, textPaint);
        }else if(percent==0){
            canvas.drawText(percent + "%", radiusX , radiusY, textPaint);
        }
    }

    public void startAnim() {
        ValueAnimator animator = ValueAnimator.ofInt(0, mWaveLength);
        animator.setDuration(during);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dx = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.start();
    }

    //开始
    public void start() {
        FLAG_START = true;
    }

    //停止
    public void stop() {
        FLAG_START = false;
    }

    public void setDuring(int during) {
        this.during = during;
    }

    public int getDuring() {
        return during;
    }

    public int getPercent() {
        return percent;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {    //设置速度，正值下降，负值上升,单位像素
        this.speed = speed;
    }

    public void setTextColor(int color) {
        this.textColor = color;
    }

    public void setWaveColor(int color) {
        this.waveColor = color;
    }

    public void setTextSize(int size) {
        this.textSize = size;
    }
    public void setCircleColor(int circleColor){
        this.circleColor=circleColor;
    }

    public void setRadius(int radius){
        this.radius=radius;
    }
    public int getRadius(){
        return radius;
    }
    public void setCenterInParent(boolean centerInParent) {
        this.isCenterInParent = centerInParent;
        if (centerInParent) {
            radiusX = totalX / 2;
            radiusY = totalY / 2;
            originY = totalY / 2 + radius;
            postInvalidate();
        } else {
            radiusX = totalX - marginRight - radius;
            radiusY = totalY - marginBottom - radius;
            originY = totalY - marginBottom;
            postInvalidate();
        }

    }

    public boolean getCenterInParent() {
        return isCenterInParent;
    }

    public void setWaveLength(int waveLength) {
        this.mWaveLength = waveLength;
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                return true;
//            case MotionEvent.ACTION_MOVE:
//                break;
//            case MotionEvent.ACTION_UP:
//                break;
//            default:
//                break;
//        }
//        return false;
//    }
}
