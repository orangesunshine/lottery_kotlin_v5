package com.bdb.lottery.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * @Description:
 * @Author: orange
 * @Date: 2020/11/12 5:31 PM
 */
public class ClearEditText extends androidx.appcompat.widget.AppCompatEditText implements View.OnFocusChangeListener, TextWatcher {
    private Drawable mClearDrawable;
    private boolean hasFoucs;

    private onVisibleListener mOnVisibleListener;

    public void setOnBlankListener(onVisibleListener onVisibleListener) {
        mOnVisibleListener = onVisibleListener;
    }

    public ClearEditText(Context context) {
        super(context);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        if (!this.isInEditMode()) {
            this.mClearDrawable = this.getCompoundDrawables()[2];
            if (this.mClearDrawable == null) {
                throw new NullPointerException("You can add drawableRight attribute in XML");
            }

            this.mClearDrawable.setBounds(0, 0, this.mClearDrawable.getIntrinsicWidth(), this.mClearDrawable.getIntrinsicHeight());
            this.setClearIconVisible(false);
            this.setOnFocusChangeListener(this);
            this.addTextChangedListener(this);
        }

    }

    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? this.mClearDrawable : null;
        this.setCompoundDrawables(this.getCompoundDrawables()[0], this.getCompoundDrawables()[1], right, this.getCompoundDrawables()[3]);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 1 && this.getCompoundDrawables()[2] != null) {
            boolean touchable = event.getX() > (float) (this.getWidth() - this.getTotalPaddingRight()) && event.getX() < (float) (this.getWidth() - this.getPaddingRight());
            if (touchable) {
                this.setText("");
            }
        }

        return super.onTouchEvent(event);
    }

    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        boolean blank = this.getText().length() <= 0;
        if (hasFocus) {
            this.setClearIconVisible(!blank);
        } else {
            this.setClearIconVisible(false);
        }
        if (null != mOnVisibleListener) mOnVisibleListener.onVisible(hasFocus && !blank);
    }

    public void onTextChanged(CharSequence s, int start, int count, int after) {
        if (this.hasFoucs) {
            boolean blank = s.length() <= 0;
            this.setClearIconVisible(!blank);
            if (null != mOnVisibleListener) mOnVisibleListener.onVisible(!blank);
        }

    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void afterTextChanged(Editable s) {
    }

    public void setShakeAnimation() {
        this.setAnimation(this.shakeAnimation(5));
    }

    public Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0.0F, 10.0F, 0.0F, 0.0F);
        translateAnimation.setInterpolator(new CycleInterpolator((float) counts));
        translateAnimation.setDuration(1000L);
        return translateAnimation;
    }

    public interface onVisibleListener {
        void onVisible(boolean visible);
    }
}
