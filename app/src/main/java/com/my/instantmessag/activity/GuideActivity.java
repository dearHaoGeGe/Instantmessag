package com.my.instantmessag.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.my.instantmessag.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 引导页
 * <p/>
 * Created by YJH onDetailClick 16/3/4.
 */
public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener, View.OnTouchListener, MediaPlayer.OnCompletionListener {
    private ViewPager splashViewPager;
    private GuideViewPagerAdapter adapter;
    private List<Map<String, Object>> list = new ArrayList<>();
    private List<View> dots = new ArrayList<>();
    private int oldPosition = 0;
    private int downX = 0;
    private int currentIndex = 0;
    private int maxIndex = 0;
    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent();
            intent.setClass(GuideActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

            overridePendingTransition(R.anim.alpha_outof, R.anim.alpha_into);
            //player.stop();
        }
    };
    private int[] images = {R.mipmap.robot, R.mipmap.singleperson, R.mipmap.ship};
    private String[] texts = {"你说" + "\n" + "它一个人看着天空在想些什么", "是在想" + "\n" + "一个人为何总是如此忧伤的走着?"
            , "所以啦" + "\n" + "什么都不要想了" + "\n" + "快点拉上你的小伙伴一起来吧"};
    //private String [] texts={"1","2","3"};

    //private MediaPlayer player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        isFirstIntoApp();   //检测是否是第一次进入程序
        init();
    }

    private void init() {
//        player = new MediaPlayer();
//        player.reset();
//        player = MediaPlayer.create(this, R.raw.ainy); //放歌
//        player.start();
        dots.add(findViewById(R.id.dot_1));
        dots.add(findViewById(R.id.dot_2));
        dots.add(findViewById(R.id.dot_3));
        dots.get(oldPosition).setBackgroundResource(R.drawable.dot_focused);

        splashViewPager = (ViewPager) findViewById(R.id.splashViewPager);
        for (int i = 0; i < texts.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", images[i]);
            map.put("text", texts[i]);
            list.add(map);
        }
        adapter = new GuideViewPagerAdapter();
        splashViewPager.setAdapter(adapter);
        adapter.addData(list);
        splashViewPager.setOnPageChangeListener(this);
        maxIndex = adapter.getCount();
        handler = new Handler();
        //player.setOnCompletionListener(this);

    }

//    private void isStop() {
//        if (!player.isPlaying()) {
//
//            player.stop();
//        }
//
//    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentIndex = position;
        dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
        dots.get(position).setBackgroundResource(R.drawable.dot_focused);
        oldPosition = position;
        if (position == maxIndex - 1) {
            handler.postDelayed(runnable, 2000);

        } else {
            handler.removeCallbacks(runnable);
        }
        splashViewPager.setOnTouchListener(this);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handler.removeCallbacks(runnable);
                downX = (int) event.getX();
                break;
            case MotionEvent.ACTION_UP:
                if ((downX - event.getX()) > 100 && (currentIndex == maxIndex - 1)) {
                    Intent intent = new Intent();
                    intent.setClass(GuideActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                    overridePendingTransition(R.anim.right_into, R.anim.left_out);
                    //player.stop();
                }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (!mp.isPlaying()) {
            Intent intent = new Intent();
            intent.setClass(GuideActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.alpha_outof, R.anim.alpha_into);
        }

    }

    /**
     * 程序是否是第一次运行
     */
    private void isFirstIntoApp() {
        if (readInfo()) {
            createShortcut();   //第一次运行创建快捷方式
            saveInfo();
        } else {
            //如果程序不是第一次打开就直接跳到闪屏页
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }
    }

    /**
     * 保存信息
     */
    private void saveInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("isFirstInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirst", false);
        editor.commit();
    }

    /**
     * 读取信息
     */
    private boolean readInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("isFirstInfo", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isFirst", true);
    }

    /**
     * 创建快捷方式(需要添加权限)
     * <uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT"/>
     */
    private void createShortcut() {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        shortcut.putExtra("duplicate", false);//设置是否重复创建
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(this, GuideActivity.class);//设置第一个页面
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, R.drawable.weixin);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        sendBroadcast(shortcut);
    }

    /**
     * 适配器
     */
    public class GuideViewPagerAdapter extends PagerAdapter {

        private List<Map<String,Object>> list;

        public void addData(List<Map<String, Object>> list){
            this.list = list;
            notifyDataSetChanged();

        }
        @Override
        public int getCount() {
            return list != null && list.size() > 0 ? list.size() : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View view = inflater.inflate(R.layout.viewpager_guide, container, false);
            ImageView splashImage = (ImageView) view.findViewById(R.id.splashImage);
            TextView splashText = (TextView) view.findViewById(R.id.splashText);
            int id  = (int) list.get(position).get("image");
            String text = (String) list.get(position).get("text");
            splashText.setText(text);

            splashImage.setImageResource(id);
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
