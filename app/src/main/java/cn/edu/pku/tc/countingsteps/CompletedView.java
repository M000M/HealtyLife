package cn.edu.pku.tc.countingsteps;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.transition.CircularPropagation;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by 18012 on 2019/1/19.
 */
public class CompletedView extends View {

    private Paint rPaint; //绘制举行的画笔

    private Paint progressPaint; //绘制圆弧的画笔

    private float sweepAngle; //圆弧经过的角度

    private Animation anim;

    public CompletedView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        rPaint = new Paint();
        rPaint.setStyle(Paint.Style.STROKE); //只描边
        rPaint.setColor(Color.RED);

        progressPaint = new Paint();
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setColor(Color.BLUE);
        progressPaint.setAntiAlias(true);

        anim = new CircleBarAnim();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        float x = 50;
        float y = 50;
        RectF rectF = new RectF(x, y, x + 300, y + 300);

        canvas.drawArc(rectF, 0, sweepAngle, false, progressPaint);
        canvas.drawRect(rectF, rPaint);
    }

    public class CircleBarAnim extends Animation {

        public CircleBarAnim(){

        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t){
            super.applyTransformation(interpolatedTime, t);
            sweepAngle = interpolatedTime * 360;
            postInvalidate();
        }
    }

    //写个方法给外部调用，用来设置动画时间
    public void setProgressNum(int time){
        anim.setDuration(time);
        this.startAnimation(anim);
    }
}












