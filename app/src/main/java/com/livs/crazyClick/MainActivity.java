package com.livs.crazyClick;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {

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
        ImageView wxPayImageView = findViewById(R.id.wxPay);
        ImageView aliPayImageView = findViewById(R.id.aliPay);

        wxPayImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog(R.drawable.wx_pay);
            }
        });

        aliPayImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog(R.drawable.ali_pay);
            }
        });


        buttonDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // 检查悬浮窗权限
                if (!isCanDrawOverlaysEnabled()) {
                    requestOverlayPermission();
                    return;
                }
                // 检查辅助访问权限
                if (!isAccessibilityServiceEnabled()) {
                    requestAccessibilityAuth();
                    return;
                }
                // 如果都已经有权限，启动服务
                startFloatingButtonService();
            }
        });
        setList();
    }

    // 显示放大的图片
    private void showImageDialog(int imageResource) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_image); // 创建一个新布局文件
        ImageView imageView = dialog.findViewById(R.id.dialogImageView);
        imageView.setImageResource(imageResource);
        dialog.show();
    }

    // 请求悬浮窗权限
    private void requestOverlayPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("需要悬浮窗权限");
        builder.setMessage("应用需要悬浮窗权限来正常工作，请前往设置开启权限。");

        builder.setPositiveButton("去设置", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        });

        builder.setNegativeButton("取消", (dialog, which) -> {
            dialog.dismiss();
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                    "未授予悬浮窗权限，应用功能可能会受到限制",
                    Snackbar.LENGTH_SHORT);
        });

        builder.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 检查悬浮窗权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isCanDrawOverlaysEnabled()) {
                Toast.makeText(this, "无法显示悬浮窗口权限，程序功能将受到限制", Toast.LENGTH_SHORT).show();
            }
        }

        // 检查辅助访问权限
        if (!isAccessibilityServiceEnabled()) {
            Toast.makeText(this, "未启动辅助访问权限，程序功能将受到限制", Toast.LENGTH_SHORT).show();
        }

        setList(); // 更新列表显示
    }


    // 启动悬浮球服务
    private void startFloatingButtonService() {
        Intent intent = new Intent(MainActivity.this, FloatingButtonService.class);
        startService(intent);
    }


    // 悬浮窗权限
    private boolean isCanDrawOverlaysEnabled() {
        return Settings.canDrawOverlays(this);
    }

    // 检查启动了辅助控制功能
    private boolean isAccessibilityServiceEnabled() {
        String service = getPackageName() + "/" + MyAccessibilityService.class.getName();
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED
            );
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (accessibilityEnabled == 1) {
            String enabledServices = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            );
            if (enabledServices != null && enabledServices.contains(service)) {
                return true;
            }
        }
        return false;
    }


    private void setList() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<String> names = Arrays.asList("悬浮球", "辅助访问");
        List<String> status = Arrays.asList(isCanDrawOverlaysEnabled() ? "已授权" : "未授权去设置", isAccessibilityServiceEnabled() ? "已授权" : "未授权");
        List<Integer> icons = Arrays.asList(isCanDrawOverlaysEnabled() ? R.drawable.circle_green_background : R.drawable.circle_red_background,
                isAccessibilityServiceEnabled() ? R.drawable.circle_green_background : R.drawable.circle_red_background);
        SimpleListAdapter adapter = new SimpleListAdapter(names, status, icons, this);
        recyclerView.setAdapter(adapter);
    }


    // 请求授权辅助控制
    public void requestAccessibilityAuth() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("需要辅助功能权限");
        builder.setMessage("应用需要辅助功能权限来正常工作，请前往设置开启权限。");

        builder.setPositiveButton("去设置", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        builder.setNegativeButton("取消", (dialog, which) -> {
            dialog.dismiss();
            Toast.makeText(MainActivity.this, "未授予辅助功能权限，应用功能可能会受到限制", Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }


    @Override
    public void onItemClick(int position, String name, String status, int icon) {
        if (Objects.equals(status, "已授权")) {
            return;
        }
        if (position == 0) {
            requestOverlayPermission();
        }
        if (position == 1) {
            requestAccessibilityAuth();
        }


    }

}