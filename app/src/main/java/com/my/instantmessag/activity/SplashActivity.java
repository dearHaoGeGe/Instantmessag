package com.my.instantmessag.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.my.instantmessag.R;

import java.util.Random;

/**
 * 闪屏页activity
 *
 * Created by YJH onDetailClick 16/2/29.
 */
public class SplashActivity extends AppCompatActivity{
    /**
     * 三个切换的动画
     */
    private Animation mFadeIn;
    private Animation mFadeInScale;
    private Animation mFadeOut;

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mImageView = (ImageView) findViewById(R.id.image);
        int index = new Random().nextInt(2);
        if (index == 1) {
            mImageView.setImageResource(R.mipmap.entrance3);
        } else {
            mImageView.setImageResource(R.mipmap.entrance2);
        }
        initAnim();
        setListener();
    }


    private void initAnim() {
        mFadeIn = AnimationUtils.loadAnimation(this, R.anim.welcome_fade_in);
        mFadeIn.setDuration(500);
        mFadeInScale = AnimationUtils.loadAnimation(this,
                R.anim.welcome_fade_in_scale);
        mFadeInScale.setDuration(2000);
        mFadeOut = AnimationUtils.loadAnimation(this, R.anim.welcome_fade_out);
        mFadeOut.setDuration(500);
        mImageView.startAnimation(mFadeIn);
    }


    /**
     * 监听事件
     */
    public void setListener() {
        /**
         * 动画切换原理:开始时是用第一个渐现动画,当第一个动画结束时开始第二个放大动画,当第二个动画结束时调用第三个渐隐动画,
         * 第三个动画结束时修改显示的内容并且重新调用第一个动画,从而达到循环效果
         */
        mFadeIn.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                mImageView.startAnimation(mFadeInScale);
            }
        });
        mFadeInScale.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent();
                intent.setClass(getApplication(), LoginActivity.class);     //跳到登录页面
                startActivity(intent);
                finish();
                // mImageView.startAnimation(mFadeOut);
            }
        });
        mFadeOut.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                // startActivity(MainActivity.class);
            }
        });
    }
}
