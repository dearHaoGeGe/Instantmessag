package com.my.instantmessag.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.google.zxing.Result;
import com.my.instantmessag.R;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * 扫码
 * <p>
 * 本类由: Risky57 创建于: 16/3/8.
 */
public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final String TAG = "ScanActivity";
    private ZXingScannerView scanView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // API23之后需要手动调用获取权限
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA}, 1);
//        }
        scanView = new ZXingScannerView(this);
        setContentView(scanView);
    }

    @Override
    public void onResume() {
        super.onResume();
        scanView.setResultHandler(this);
        scanView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scanView.stopCamera();
    }

    private Handler mHandler = new Handler();

    // 扫描成功后会回调此方法
    @Override
    public void handleResult(Result result) {
        Log.v(TAG, result.getText());
        Log.v(TAG, result.getBarcodeFormat().toString());

        //if (null != result.getText() && result.getText().equals("")) {
            showDialog(result.getText());   //扫码成功后弹出一个Dialog
        //}

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scanView.resumeCameraPreview(ScanActivity.this);

            }
        }, 2000);
    }

    /**
     * 扫码的成功后的Dialog
     *
     * @param user 扫码出的对方用户名
     */
    private void showDialog(final String user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.weixin);
        builder.setTitle("添加好友");
        builder.setMessage("您将添加<" + user + ">为好友");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //参数为要添加的好友的username和添加理由
                try {
                    EMContactManager.getInstance().addContact(user, "通过扫码加好友");     //添加好友(需异步处理)
                    Toast.makeText(ScanActivity.this, "添加好友成功~", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
