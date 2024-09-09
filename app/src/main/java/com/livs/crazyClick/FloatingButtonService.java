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
    private ImageView floatingButton;
    private WindowManager.LayoutParams params;

    @Override
    public void onCreate() {
        super.onCreate();

        // 获取 WindowManager
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // 创建悬浮球按钮
        floatingButton = new ImageView(this);
        floatingButton.setImageResource(R.drawable.ic_launcher_background); // 设置悬浮球图标

        // 设置悬浮球参数
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
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

        // 设置悬浮球的点击事件
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
                        // 调用 performClick 方法
                        performMultipleClicksAboveButton(); // 调用点击悬浮按钮上方的方法
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

    // 点击悬浮按钮上方的位置
    private void performMultipleClicksAboveButton() {
        // 获取当前悬浮按钮的位置
        int x = params.x;
        int y = params.y - 50; // 设置y轴上方偏移，调整点击的高度

        MyAccessibilityService service = MyAccessibilityService.getInstance();
        if (service != null) {
            service.performMultipleClicks(x, y, 10); // 点击悬浮按钮上方的10次
        } else {
            // 处理service为null的情况，可以提示用户启用AccessibilityService
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}