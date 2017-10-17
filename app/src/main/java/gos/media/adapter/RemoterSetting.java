package gos.media.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import gos.media.R;


/**
 * Created by lp on 2017/10/29.
 */

public class RemoterSetting extends View {

        private static final String TAG = "View";
        int width = 100;
        int height = 100;
        Paint paintW;
        Paint paintB;
        Paint paintText;
        Path path;   //画三角
        RectF rectf;
        String ok = "OK";
        int blue = Color.parseColor("#d7d7d7");
        int text = Color.parseColor("#626262");
        int bg_color = Color.parseColor("#f0f0f0");
        int bg_change = Color.parseColor("#bdbdbd");

        //当前点击位置 0中间，3左，4右，1上，2下
        int clickP = -1;

        //private onClickItemListener onClickItemListener;
        private onTouchListener onTouchListener;

        public RemoterSetting(Context context) {
            this(context, null);
        }

        public RemoterSetting(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public RemoterSetting(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            paintB = new Paint();
            paintW = new Paint();
            paintText = new Paint();
            path=new Path();

            paintW.setColor(bg_color);
            paintText.setColor(text);
            paintText.setTextSize(80f);
            // DEFAULT_BOLD :黑体字体类型,SANS_SERIF:sans serif字体类型,Typeface.BOLD:粗体
            Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
            paintText.setTypeface(font);
            paintB.setAntiAlias(true);
            paintW.setAntiAlias(true);
            paintText.setAntiAlias(true);
            rectf = new RectF();

        }

       // View在屏幕上显示出来要先经过measure（计算）和layout（布局）.
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);

            if (widthMode == MeasureSpec.EXACTLY) {
                width = widthSize;
            }
            if (heightMode == MeasureSpec.EXACTLY) {
                height = heightSize;
            }
            rectf.set(5, 5, width - 5, height - 5);
            setMeasuredDimension(width, height);// 传递View的高度和宽度，高速父布局其大小
        }

        @Override
        protected void onDraw(Canvas canvas) {
            paintB.setStrokeWidth(8); //线宽
            paintB.setColor(text);
            paintB.setStyle(Paint.Style.FILL);
            paintW.setStyle(Paint.Style.FILL);
            switch (clickP) {
                case 0:
                    /**由于没有现成的箭头图片，一个笨办法，画8个线段吧！*/
                    //右箭头和扇形
                    //椭圆对象、起始角度、所画角度,为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    path.moveTo(width - 90, height / 2 - 30);
                    path.lineTo(width - 50, height / 2);
                    path.lineTo(width - 90, height / 2 + 30);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    path.moveTo(width / 2, 50);
                    path.lineTo(width / 2 + 30, 90);
                    path.lineTo(width / 2 - 30, 90);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    path.moveTo(50, height / 2);
                    path.lineTo(90, height / 2 - 30);
                    path.lineTo(90, height / 2 + 30);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    path.moveTo(width / 2 - 30, height - 90);
                    path.lineTo(width / 2 + 30, height - 90);
                    path.lineTo(width / 2, height - 50);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //中间圆和字
                    //paintText.setColor(Color.WHITE);
                    paintB.setColor(bg_change);
                    canvas.drawCircle(width / 2, height / 2, width / 5 - 7, paintB);
                    canvas.drawText(ok, (width / 2) - 55, (height / 2) + 30, paintText);
                    //paintText.setColor(blue);
                    break;
                case 1:
                    //右箭头和扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    path.moveTo(width - 90, height / 2 - 30);
                    path.lineTo(width - 50, height / 2);
                    path.lineTo(width - 90, height / 2 + 30);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //上箭头和扇形
                    paintW.setColor(bg_change);
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    path.moveTo(width / 2, 50);
                    path.lineTo(width / 2 + 30, 90);
                    path.lineTo(width / 2 - 30, 90);
                    path.close();
                    canvas.drawPath(path, paintB);
                    paintW.setColor(bg_color);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    path.moveTo(50, height / 2);
                    path.lineTo(90, height / 2 - 30);
                    path.lineTo(90, height / 2 + 30);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    path.moveTo(width / 2 - 30, height - 90);
                    path.lineTo(width / 2 + 30, height - 90);
                    path.lineTo(width / 2, height - 50);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //中间圆和字
                    canvas.drawCircle(width / 2, height / 2, width / 5 - 7, paintW);
                    canvas.drawText(ok, (width / 2) - 55, (height / 2) + 30, paintText);

                    break;
                case 2:
                    //右箭头和扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    path.moveTo(width - 90, height / 2 - 30);
                    path.lineTo(width - 50, height / 2);
                    path.lineTo(width - 90, height / 2 + 30);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    path.moveTo(width / 2, 50);
                    path.lineTo(width / 2 + 30, 90);
                    path.lineTo(width / 2 - 30, 90);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    path.moveTo(50, height / 2);
                    path.lineTo(90, height / 2 - 30);
                    path.lineTo(90, height / 2 + 30);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //下箭头和扇形
                    paintW.setColor(bg_change);
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    path.moveTo(width / 2 - 30, height - 90);
                    path.lineTo(width / 2 + 30, height - 90);
                    path.lineTo(width / 2, height - 50);
                    path.close();
                    canvas.drawPath(path, paintB);
                    paintW.setColor(bg_color);
                    //中间圆和字
                    canvas.drawCircle(width / 2, height / 2, width / 5 - 7, paintW);
                    canvas.drawText(ok, (width / 2) - 55, (height / 2) + 30, paintText);

                    break;
                case 3:
                    //右箭头和扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    path.moveTo(width - 90, height / 2 - 30);
                    path.lineTo(width - 50, height / 2);
                    path.lineTo(width - 90, height / 2 + 30);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    path.moveTo(width / 2, 50);
                    path.lineTo(width / 2 + 30, 90);
                    path.lineTo(width / 2 - 30, 90);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //左箭头和扇形
                    paintW.setColor(bg_change);
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    path.moveTo(50, height / 2);
                    path.lineTo(90, height / 2 - 30);
                    path.lineTo(90, height / 2 + 30);
                    path.close();
                    canvas.drawPath(path, paintB);
                    paintW.setColor(bg_color);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    path.moveTo(width / 2 - 30, height - 90);
                    path.lineTo(width / 2 + 30, height - 90);
                    path.lineTo(width / 2, height - 50);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //中间圆和字
                    canvas.drawCircle(width / 2, height / 2, width / 5 - 7, paintW);
                    canvas.drawText(ok, (width / 2) - 55, (height / 2) + 30, paintText);

                    break;
                case 4:
                    //右箭头和扇形
                    paintW.setColor(bg_change);
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    path.moveTo(width - 90, height / 2 - 30);
                    path.lineTo(width - 50, height / 2);
                    path.lineTo(width - 90, height / 2 + 30);
                    path.close();
                    canvas.drawPath(path, paintB);
                    paintW.setColor(bg_color);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    path.moveTo(width / 2, 50);
                    path.lineTo(width / 2 + 30, 90);
                    path.lineTo(width / 2 - 30, 90);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    path.moveTo(50, height / 2);
                    path.lineTo(90, height / 2 - 30);
                    path.lineTo(90, height / 2 + 30);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    path.moveTo(width / 2 - 30, height - 90);
                    path.lineTo(width / 2 + 30, height - 90);
                    path.lineTo(width / 2, height - 50);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //中间圆和字
                    canvas.drawCircle(width / 2, height / 2, width / 5 - 7, paintW);
                    canvas.drawText(ok, (width / 2) - 55, (height / 2) + 30, paintText);

                    break;
                default:
                    //右箭头和扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    //canvas.drawLine(width - 50, height / 2 + 3, width - 80, height / 2 - 28, paintB);
                    //canvas.drawLine(width - 50, height / 2 - 3, width - 80, height / 2 + 28, paintB);
                     /*画一个实心三角形*/
                    path.moveTo(width - 90, height / 2 - 30);
                    path.lineTo(width - 50, height / 2);
                    path.lineTo(width - 90, height / 2 + 30);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    //canvas.drawLine(width / 2 + 3, 50, width / 2 - 28, 80, paintB);
                    //canvas.drawLine(width / 2 - 3, 50, width / 2 + 28, 80, paintB);
                    path.moveTo(width / 2, 50);
                    path.lineTo(width / 2 + 30, 90);
                    path.lineTo(width / 2 - 30, 90);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    //canvas.drawLine(50, height / 2 + 3, 80, height / 2 - 28, paintB);
                    //canvas.drawLine(50, height / 2 - 3, 80, height / 2 + 28, paintB);
                    path.moveTo(50, height / 2);
                    path.lineTo(90, height / 2 - 30);
                    path.lineTo(90, height / 2 + 30);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    //canvas.drawLine(width / 2 + 3, height - 50, height / 2 - 28, height - 80, paintB);
                    //canvas.drawLine(width / 2 - 3, height - 50, height / 2 + 28, height - 80, paintB);
                    path.moveTo(width / 2 - 30, height - 90);
                    path.lineTo(width / 2 + 30, height - 90);
                    path.lineTo(width / 2, height - 50);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //中间圆和字
                    paintW.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(width / 2, height / 2, width / 5 - 7, paintW);
                    canvas.drawText(ok, (width / 2) - 55, (height / 2) + 30, paintText);

                    break;
            }

            paintB.setStyle(Paint.Style.STROKE); //空心效果
            paintB.setStrokeWidth(20);  //线宽
            //canvas.drawColor(bg_color); //画布背景
            paintB.setColor(blue);
            canvas.drawCircle(width / 2, height / 2, width / 2 - 10, paintB);
            paintB.setStrokeWidth(5);
            canvas.drawCircle(width / 2, height / 2, width / 5 - 7, paintB);
            paintB.setStrokeWidth(3);
            paintB.setColor(getResources().getColor(R.color.white));
            canvas.drawCircle(width / 2, height / 2, width / 2 - 22, paintB);

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_MOVE:

                    return true;
                case MotionEvent.ACTION_DOWN:
                    if (x < 2 * width / 3 && x > width / 3 && y > 0 && y < height / 3) {
                        clickP = 1;
                    } else if (x < 2 * width / 3 && x > width / 3 && y < height && y > 2 * height / 3) {
                        clickP = 2;
                    } else if (x < width / 3 && x > 0 && y < 2 * height / 3 && y > height / 3) {
                        clickP = 3;
                    } else if (x < width && x > 2 * width / 3 && y > height / 3 && y < 2 * height / 3) {
                        clickP = 4;
                    } else if (x > width / 3 && x < 2 * width / 3 && y > height / 3 && y < 2 * height / 3) {
                        clickP = 0;
                    }
                    // onDraw之中调用invalidate()，会再触发onDraw，从而不停刷新显示
                    invalidate();

                    return true;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "onTouchEvent: " + clickP);
                    invalidate();
                    //当前点击位置 0中间，3左，4右，1上，2下
                    if (null != onTouchListener) {
                        switch (clickP) {
                            case 0:
                                onTouchListener.ok();
                                break;
                            case 1:
                                onTouchListener.up();
                                break;
                            case 2:
                                onTouchListener.down();
                                break;
                            case 3:
                                onTouchListener.left();
                                break;
                            case 4:
                                onTouchListener.right();
                                break;
                        }
                    }
                    clickP = -1;
                    return true;
            }
            return false;
        }

        interface onTouchListener {
            void up();
            void down();
            void left();
            void right();
            void ok();
        }

        public void setOnTouchListener(RemoterSetting.onTouchListener onTouchListener) {
            this.onTouchListener = onTouchListener;
        }

}
