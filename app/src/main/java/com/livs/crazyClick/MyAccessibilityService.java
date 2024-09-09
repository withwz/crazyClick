package com.livs.crazyClick;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {


    private static MyAccessibilityService instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;  // 存储当前实例
    }


    public static MyAccessibilityService getInstance() {
        return instance;
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 处理辅助功能事件
    }


    @Override
    public void onInterrupt() {
        // 处理服务中断
    }

    // 模拟点击功能
    public void performClick(int x, int y) {
        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        Path path = new Path();
        path.moveTo(x, y);

        // 创建点击手势
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 0, 50)); // 延迟0ms，持续50ms
        dispatchGesture(gestureBuilder.build(), null, null);
    }

    // 模拟多次点击
    public void performMultipleClicks(int x, int y, int count) {
        for (int i = 0; i < count; i++) {
            performClick(x, y);
            try {
                Thread.sleep(50); // 每次点击之间的延迟时间
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}