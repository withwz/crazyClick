package com.livs.crazyClick;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_OVERLAY_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 处理系统状态栏与布局
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button buttonDetect = findViewById(R.id.button);
        buttonDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 检查悬浮窗权限
                    if (!Settings.canDrawOverlays(MainActivity.this)) {
                        requestOverlayPermission();
                    } else {
                        // 如果已经有权限，启动服务
                        startFloatingButtonService();
                    }
                } else {
                    startFloatingButtonService();
                }
            }
        });
    }

    // 请求悬浮窗权限
    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    // 用户授予了权限，启动服务
                    startFloatingButtonService();
                } else {
                    // 用户拒绝了权限
                    Toast.makeText(this, "无法显示悬浮窗口权限，程序功能将受到限制", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 启动悬浮球服务
    private void startFloatingButtonService() {
        Intent intent = new Intent(MainActivity.this, FloatingButtonService.class);
        startService(intent);
    }
}