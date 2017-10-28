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

import java.util.Timer;
import java.util.TimerTask;

import gos.media.R;


/**
 * Created by lp on 2017/10/29.
 */

public class RemoterSetting extends View {

        private static final String TAG = "View";
        private static final int DEFAULT_HEIGHT = 100;
        private static final int DEFAULT_WIDTH = 100;
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
        boolean isLongClick = false;

        int upKey;
        int downKey;
        int leftKey;
        int rightKey;
        int okKey;

        int curKeyValue = -1;//保存当前按下时键位值

        //当前点击位置 0中间，3左，4右，1上，2下
        int clickP = -1;

        private onTouchListener onTouchListener;

        public Timer timer = new Timer(); //定时器，计时器,判断是否长按
        public TimerTask timerTask = null;//监听长按的任务
        class LongTimerTask extends TimerTask{
            int keyValue;
            public LongTimerTask(int keyValue){
                super();
                this.keyValue = keyValue;
            }

            @Override
            public void run() {
                Log.e("status", "长按位置:" + keyValue);
                onTouchListener.longClick(keyValue);
                isLongClick = true;
            }
        }

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
            paintText.setTextSize(50f);
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
            width = getMeasuredLength(widthMeasureSpec, true);
            height = getMeasuredLength(heightMeasureSpec, false);
            int num = Math.abs(width - height);
            //width = Math.max(height, width);

            rectf.set(num / 2, num / 3, width - num / 2, height - num / 3);
            setMeasuredDimension(width, height);// 传递View的高度和宽度，高速父布局其大小
        }

        private int getMeasuredLength(int length, boolean isWidth) {
            int specMode = MeasureSpec.getMode(length);
            int specSize = MeasureSpec.getSize(length);
            int size;
            int padding = isWidth ? getPaddingLeft() + getPaddingRight()
                    : getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.EXACTLY) {
                size = specSize;
            } else {
                size = isWidth ? padding + DEFAULT_WIDTH : DEFAULT_HEIGHT + padding;
                if (specMode == MeasureSpec.AT_MOST) {
                    size = Math.max(size, specSize);
                }
            }
            return size;
        }

        /*protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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
        }*/

        @Override
        protected void onDraw(Canvas canvas) {
            paintB.setStrokeWidth(8); //线宽
            paintB.setColor(text);
            paintB.setStyle(Paint.Style.FILL);
            paintW.setStyle(Paint.Style.FILL);
            int mHeight = Math.min(height, width);// 宽度与高度可能不相等

            path.reset(); //重置
            switch (clickP) {
                case 0:
                    /**没有现成的箭头图片，画四个三角形箭头path()*/
                    //右箭头和扇形
                    //椭圆对象、起始角度、所画角度,为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    path.moveTo((width / 4) * 3, height / 2 - 29);
                    path.lineTo((width / 4) * 3 + 40, height / 2);
                    path.lineTo((width / 4) * 3, height / 2 + 29);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    path.moveTo(width / 2, (height / 4) - 40);
                    path.lineTo(width / 2 + 29, height / 4);
                    path.lineTo(width / 2 - 29, height / 4);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    path.moveTo((width / 4) - 40, height / 2);
                    path.lineTo(width / 4, height / 2 - 29);
                    path.lineTo(width / 4, height / 2 + 29);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    path.moveTo(width / 2 - 29, (height / 4) * 3);
                    path.lineTo(width / 2 + 29, (height / 4) * 3);
                    path.lineTo(width / 2, (height / 4) * 3 + 40);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //中间圆和字
                    //paintText.setColor(Color.WHITE);
                    paintB.setColor(bg_change);
                    canvas.drawCircle(width / 2, height / 2, mHeight / 5 - 15, paintB);
                    canvas.drawText(ok, (width / 2) - 35, (height / 2) + 20, paintText);
                    //paintText.setColor(blue);
                    break;
                case 1:
                    //右箭头和扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    path.moveTo((width / 4) * 3, height / 2 - 29);
                    path.lineTo((width / 4) * 3 + 40, height / 2);
                    path.lineTo((width / 4) * 3, height / 2 + 29);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //上箭头和扇形
                    paintW.setColor(bg_change);
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    path.moveTo(width / 2, (height / 4) - 40);
                    path.lineTo(width / 2 + 29, height / 4);
                    path.lineTo(width / 2 - 29, height / 4);
                    path.close();
                    canvas.drawPath(path, paintB);
                    paintW.setColor(bg_color);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    path.moveTo((width / 4) - 40, height / 2);
                    path.lineTo(width / 4, height / 2 - 29);
                    path.lineTo(width / 4, height / 2 + 29);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    path.moveTo(width / 2 - 29, (height / 4) * 3);
                    path.lineTo(width / 2 + 29, (height / 4) * 3);
                    path.lineTo(width / 2, (height / 4) * 3 + 40);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //中间圆和字
                    canvas.drawCircle(width / 2, height / 2, mHeight / 5 - 15, paintW);
                    canvas.drawText(ok, (width / 2) - 35, (height / 2) + 20, paintText);
                    break;
                case 2:
                    //右箭头和扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    path.moveTo((width / 4) * 3, height / 2 - 29);
                    path.lineTo((width / 4) * 3 + 40, height / 2);
                    path.lineTo((width / 4) * 3, height / 2 + 29);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    path.moveTo(width / 2, (height / 4) - 40);
                    path.lineTo(width / 2 + 29, height / 4);
                    path.lineTo(width / 2 - 29, height / 4);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    path.moveTo((width / 4) - 40, height / 2);
                    path.lineTo(width / 4, height / 2 - 29);
                    path.lineTo(width / 4, height / 2 + 29);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //下箭头和扇形
                    paintW.setColor(bg_change);
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    path.moveTo(width / 2 - 29, (height / 4) * 3);
                    path.lineTo(width / 2 + 29, (height / 4) * 3);
                    path.lineTo(width / 2, (height / 4) * 3 + 40);
                    path.close();
                    canvas.drawPath(path, paintB);
                    paintW.setColor(bg_color);
                    //中间圆和字
                    canvas.drawCircle(width / 2, height / 2, mHeight / 5 - 15, paintW);
                    canvas.drawText(ok, (width / 2) - 35, (height / 2) + 20, paintText);
                    break;
                case 3:
                    //右箭头和扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    path.moveTo((width / 4) * 3, height / 2 - 29);
                    path.lineTo((width / 4) * 3 + 40, height / 2);
                    path.lineTo((width / 4) * 3, height / 2 + 29);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    path.moveTo(width / 2, (height / 4) - 40);
                    path.lineTo(width / 2 + 29, height / 4);
                    path.lineTo(width / 2 - 29, height / 4);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //左箭头和扇形
                    paintW.setColor(bg_change);
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    path.moveTo((width / 4) - 40, height / 2);
                    path.lineTo(width / 4, height / 2 - 29);
                    path.lineTo(width / 4, height / 2 + 29);
                    path.close();
                    canvas.drawPath(path, paintB);
                    paintW.setColor(bg_color);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    path.moveTo(width / 2 - 29, (height / 4) * 3);
                    path.lineTo(width / 2 + 29, (height / 4) * 3);
                    path.lineTo(width / 2, (height / 4) * 3 + 40);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //中间圆和字
                    canvas.drawCircle(width / 2, height / 2, mHeight / 5 - 15, paintW);
                    canvas.drawText(ok, (width / 2) - 35, (height / 2) + 20, paintText);
                    break;
                case 4:
                    //右箭头和扇形
                    paintW.setColor(bg_change);
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    path.moveTo((width / 4) * 3, height / 2 - 29);
                    path.lineTo((width / 4) * 3 + 40, height / 2);
                    path.lineTo((width / 4) * 3, height / 2 + 29);
                    path.close();
                    canvas.drawPath(path, paintB);
                    paintW.setColor(bg_color);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    path.moveTo(width / 2, (height / 4) - 40);
                    path.lineTo(width / 2 + 29, height / 4);
                    path.lineTo(width / 2 - 29, height / 4);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    path.moveTo((width / 4) - 40, height / 2);
                    path.lineTo(width / 4, height / 2 - 29);
                    path.lineTo(width / 4, height / 2 + 29);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    path.moveTo(width / 2 - 29, (height / 4) * 3);
                    path.lineTo(width / 2 + 29, (height / 4) * 3);
                    path.lineTo(width / 2, (height / 4) * 3 + 40);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //中间圆和字
                    canvas.drawCircle(width / 2, height / 2, mHeight / 5 - 15, paintW);
                    canvas.drawText(ok, (width / 2) - 35, (height / 2) + 20, paintText);
                    break;
                default:
                    //右箭头和扇形
                    canvas.drawArc(rectf, 315, 90, true, paintW);
                    //canvas.drawLine(width - 50, height / 2 + 3, width - 80, height / 2 - 28, paintB);
                    //canvas.drawLine(width - 50, height / 2 - 3, width - 80, height / 2 + 28, paintB);
                     /*画一个实心三角形*/
                    path.moveTo((width / 4) * 3, height / 2 - 29);
                    path.lineTo((width / 4) * 3 + 40, height / 2);
                    path.lineTo((width / 4) * 3, height / 2 + 29);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //上箭头和扇形
                    canvas.drawArc(rectf, 225, 90, true, paintW);
                    //canvas.drawLine(width / 2 + 3, 50, width / 2 - 28, 80, paintB);
                    //canvas.drawLine(width / 2 - 3, 50, width / 2 + 28, 80, paintB);
                    path.moveTo(width / 2, (height / 4) - 40);
                    path.lineTo(width / 2 + 29, height / 4);
                    path.lineTo(width / 2 - 29, height / 4);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //左箭头和扇形
                    canvas.drawArc(rectf, 135, 90, true, paintW);
                    //canvas.drawLine(50, height / 2 + 3, 80, height / 2 - 28, paintB);
                    //canvas.drawLine(50, height / 2 - 3, 80, height / 2 + 28, paintB);
                    path.moveTo((width / 4) - 40, height / 2);
                    path.lineTo(width / 4, height / 2 - 29);
                    path.lineTo(width / 4, height / 2 + 29);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //下箭头和扇形
                    canvas.drawArc(rectf, 45, 90, true, paintW);
                    //canvas.drawLine(width / 2 + 3, height - 50, height / 2 - 28, height - 80, paintB);
                    //canvas.drawLine(width / 2 - 3, height - 50, height / 2 + 28, height - 80, paintB);
                    path.moveTo(width / 2 - 29, (height / 4) * 3);
                    path.lineTo(width / 2 + 29, (height / 4) * 3);
                    path.lineTo(width / 2, (height / 4) * 3 + 40);
                    path.close();
                    canvas.drawPath(path, paintB);
                    //中间圆和字
                    paintW.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(width / 2, height / 2, mHeight / 5 - 15, paintW);
                    canvas.drawText(ok, (width / 2) - 35, (height / 2) + 20, paintText);
                    break;
            }
            paintB.setStyle(Paint.Style.STROKE); //空心效果
            paintB.setStrokeWidth(20);  //线宽
            //canvas.drawColor(bg_color); //画布背景
            paintB.setColor(blue);
            canvas.drawCircle(width / 2, height / 2, mHeight / 2 - 10, paintB);
            Log.e("宽度", width + "");
            Log.e("高度", height + "");
            paintB.setStrokeWidth(5);
            canvas.drawCircle(width / 2, height / 2, mHeight / 5 - 15, paintB);
            paintB.setStrokeWidth(3);
            paintB.setColor(getResources().getColor(R.color.white));
            canvas.drawCircle(width / 2, height / 2, mHeight / 2 - 22, paintB);

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            Log.e(TAG, "onTouchEvent: " + event.getAction());

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_MOVE:
                    return true;
                case MotionEvent.ACTION_DOWN:
                    if (x < 2 * width / 3 && x > width / 3 && y > 0 && y < height / 3) {
                        clickP = 1;
                        curKeyValue = upKey;
                    } else if (x < 2 * width / 3 && x > width / 3 && y < height && y > 2 * height / 3) {
                        clickP = 2;
                        curKeyValue = downKey;
                    } else if (x < width / 3 && x > 0 && y < 2 * height / 3 && y > height / 3) {
                        clickP = 3;
                        curKeyValue = leftKey;
                    } else if (x < width && x > 2 * width / 3 && y > height / 3 && y < 2 * height / 3) {
                        clickP = 4;
                        curKeyValue = rightKey;
                    } else if (x > width / 3 && x < 2 * width / 3 && y > height / 3 && y < 2 * height / 3) {
                        clickP = 0;
                    }
                    // onDraw之中调用invalidate()，会再触发onDraw，从而不停刷新显示
                    invalidate();
                    if(0 !=clickP){//ok键无法触发长按
                        timerTask = new LongTimerTask(curKeyValue);
                        timer.schedule(timerTask,800);
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    //当前点击位置 0中间，3左，4右，1上，2下
                    if (null != onTouchListener) {
                        switch (clickP) {
                            case 0:
                                curKeyValue = okKey;
                                break;
                            case 1:
                                curKeyValue = upKey;
                                break;
                            case 2:
                                curKeyValue = downKey;
                                break;
                            case 3:
                                curKeyValue = leftKey;
                                break;
                            case 4:
                                curKeyValue = rightKey;
                                break;
                        }
                        if ( ! isLongClick) {//“是”--取消
                            onTouchListener.click(curKeyValue);
                            Log.e("status", "点击位置:" + curKeyValue);
                        }
                        cancelLongTask();
                    }
                    clickP = -1;
                    invalidate();
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    clickP = -1;
                    cancelLongTask();
                    invalidate();
                    return true;
            }

            return false;
        }

        public void cancelLongTask(){
            if(null != timerTask){
                if (isLongClick) {//“是”--取消
                    Log.e("status","取消长按:"+curKeyValue);
                    onTouchListener.cancelLong(curKeyValue);
                }
                timerTask.cancel();
                timerTask = null;
                isLongClick = false;

            }
        }

        public interface onTouchListener {

            void click(int keyValue);
            void longClick(int keyValue);//长按接口
            void cancelLong(int keyValue);

        }

        public void setOnTouchListener(onTouchListener onTouchListener) {
            this.onTouchListener = onTouchListener;
        }

        //设置键值
        public void setKeyValue(int upKey, int downKey, int leftKey, int rightKey, int okKey) {
            this.upKey = upKey;
            this.downKey = downKey;
            this.leftKey = leftKey;
            this.rightKey = rightKey;
            this.okKey = okKey;
        }

}
