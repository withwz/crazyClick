package com.livs.crazyClick;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class FloatingButtonService extends Service {
    private WindowManager windowManager;
    private CustomImageView floatingButton;
    private WindowManager.LayoutParams params;

    @Override
    public void onCreate() {
        super.onCreate();

        // 获取 WindowManager
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // 创建悬浮球按钮
        floatingButton = new CustomImageView(this);
        floatingButton.setImageResource(R.drawable.click_icon); // 设置悬浮球图标

        // 设置悬浮球参数
        params = new WindowManager.LayoutParams(
                234,
                234,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        // 设置悬浮球位置
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 100;
        params.y = 100;

        // 添加悬浮球到窗口
        windowManager.addView(floatingButton, params);

        // 设置悬浮球的触摸事件
        floatingButton.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        float deltaX = event.getRawX() - initialTouchX;
                        float deltaY = event.getRawY() - initialTouchY;

                        // 判断是否为点击操作（位移量小于阈值）
                        if (Math.abs(deltaX) < 10 && Math.abs(deltaY) < 10) {
                            v.performClick();
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingButton, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingButton != null) {
            windowManager.removeView(floatingButton);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 自定义的 ImageView 子类
    private class CustomImageView extends androidx.appcompat.widget.AppCompatImageView {
        public CustomImageView(Service context) {
            super(context);
            // 初始化点击事件监听器
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    performMultipleClicksAboveButton();
                }
            });
        }

        @Override
        public boolean performClick() {
            // 确保进行点击事件处理并保持可访问性
            super.performClick();
            // 调用实际的点击处理逻辑
            performMultipleClicksAboveButton();
            return true;
        }
    }

    // 点击悬浮按钮上方的位置
    private void performMultipleClicksAboveButton() {
        // 获取当前悬浮按钮的位置
        int x = params.x + 100;
        int y = params.y + 10; // 设置y轴上方偏移，调整点击的高度

        MyAccessibilityService service = MyAccessibilityService.getInstance();
        if (service != null) {
            service.performMultipleClicks(x, y, 10); // 点击悬浮按钮上方的10次
        }
    }
}