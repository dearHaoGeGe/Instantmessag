package com.my.instantmessag.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.my.instantmessag.R;
import com.my.instantmessag.base.BaseApplication;

/**
 * 自定义的像微信通讯录带字母的索引条
 *
 * Created by YJH onDetailClick 16/3/2.
 */
public class SidebarView extends View {
    public String[] arrLetters = {"⬆", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};
    private OnLetterClickedListener listener = null;
    private OnHeadClickedListener onHeadClickedListener = null;
    private TextView textView_dialog;
    private int isChoosedPosition = -1;

    public void setTextView(TextView textView) {
        textView_dialog = textView;
    }

    public SidebarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 当前view的宽度
        int width = getWidth();
        // 当前view的高度
        int height = getHeight();
        // 当前view中每个字母所占的高度
        int singleTextHeight = height / arrLetters.length;
       // Toast.makeText(BaseApplication.getContext(), "singleTextHeight:" + singleTextHeight, Toast.LENGTH_SHORT).show();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.DEFAULT);
        for (int i = 0; i < arrLetters.length; i++) {
            paint.setTextSize((int) (singleTextHeight*0.9));          //这是文字大小
            paint.setColor(0xFF000000);     //设置文字的颜色
            if (i == isChoosedPosition) {
                paint.setColor(Color.WHITE);
                paint.setFakeBoldText(true);
            }
            float x = (width - paint.measureText(arrLetters[i])) / 2;
            float y = singleTextHeight * (i + 1);
            canvas.drawText(arrLetters[i], x, y, paint);
            paint.reset();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        int position = (int) (y / getHeight() * arrLetters.length);
        int lastChoosedPosition = isChoosedPosition;
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                setBackgroundColor(0x00FFFFFF);   //索引条默认的背景色
                if (textView_dialog != null) {
                    textView_dialog.setVisibility(View.GONE);
                }
                isChoosedPosition = -1;
                invalidate();
                break;
            default:
                // 触摸边框背景颜色改变
                setBackgroundColor(R.drawable.voip_toast_bg);
                setAlpha(0.5f);
                if (lastChoosedPosition != position) {
                    if (position >= 0 && position < arrLetters.length) {
                        if (listener != null) {
                            if (position == 0) {
                                if (onHeadClickedListener != null) {
                                    onHeadClickedListener.onHeadClickedClicked();
                                }

                            } else {
                                listener.onLetterClicked(arrLetters[position]);
                            }

                        }
                        if (textView_dialog != null) {
                            textView_dialog.setVisibility(View.VISIBLE);
                            textView_dialog.setText(arrLetters[position]);
                        }
                        isChoosedPosition = position;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }


    public interface OnHeadClickedListener {
        void onHeadClickedClicked();
    }

    public interface OnLetterClickedListener {
        void onLetterClicked(String str);
    }

    public void setOnHeadClickedListener(OnHeadClickedListener onHeadClickedListener) {
        this.onHeadClickedListener = onHeadClickedListener;
    }

    public void setOnLetterClickedListener(OnLetterClickedListener listener) {
        this.listener = listener;
    }

}
