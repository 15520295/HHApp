//package com.example.huydaoduc.hieu.chi.hhapp;
//
//import android.content.Context;
//import android.util.AttributeSet;
//
//import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
//
//public class CusMaterialProgressBar extends MaterialProgressBar{
//    ProgressValueChangeListener listener;
//
//    public CusMaterialProgressBar(Context context) {
//        super(context);
//    }
//
//    public CusMaterialProgressBar(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public CusMaterialProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    public CusMaterialProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }
//
//    @Override
//    public synchronized void setProgress(int progress) {
//        super.setProgress(progress);
//        if(listener != null)
//            listener.onProgressValueChange(progress);
//    }
//
//    public interface ProgressValueChangeListener {
//        public void onProgressValueChange(int value);
//    }
//
//    public void setListener(ProgressValueChangeListener listener) {
//        this.listener = listener;
//    }
//
//}
