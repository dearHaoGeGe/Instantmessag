package com.my.instantmessag.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import com.my.instantmessag.R;
import com.my.instantmessag.utils.CreateQRCode;


/**
 * 创建二维码
 *
 * 本类由: Risky57 创建于: 16/3/9.
 */
public class CreateQRCodeAty extends AppCompatActivity {

    private AppCompatEditText etContent;
    private AppCompatButton btnCreate;
    private AppCompatImageView imgShow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create2dcode);

        etContent = (AppCompatEditText) findViewById(R.id.input_content);
        btnCreate = (AppCompatButton) findViewById(R.id.btn_create);
        imgShow = (AppCompatImageView) findViewById(R.id.img_show);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etContent.getText().toString();
                createQRImage(content);
            }
        });
    }

    private void createQRImage(String content) {
        CreateQRCode.createQRImage(content, "test", new CreateQRCode.OnCreateQRListener() {
            @Override
            public void onSuccess(Bitmap qrImage) {
                imgShow.setImageBitmap(qrImage);
            }
        });
    }
}
