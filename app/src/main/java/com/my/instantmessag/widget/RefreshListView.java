package com.my.instantmessag.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.my.instantmessag.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RefreshListView extends ListView implements OnScrollListener{

	private View headView;
	private int height;
	private int downY;

	private final int PULL_REFRESH=0;//下拉刷新
	private final int REALEASE_REFRESH=1;//松开刷新
	private final int REFRESHING=2;//正在刷新
	private int currentState=PULL_REFRESH;//当前状态
	//初始化head布局的变量
	private ImageView iv_arrow;
	private ProgressBar bar_rotate;
	private TextView tv_time,tv_state;

	//定义旋转动画
	private RotateAnimation up,down;

	private boolean isLoading=false;//当前是否在加载

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RefreshListView(Context context) {
		super(context);
		init();
	}
	private void init() {
		setOnScrollListener(this);//设置滚动监听器
		initHeadView();
		initRotateAnimation();
		initFootView();
	}


	/**
	 * 初始化旋转动画
	 */
	private void initRotateAnimation() {
		//向上旋转
		up=new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		//动画持续的时间
		up.setDuration(300);
		up.setFillAfter(true);
		//向下旋转
		down=new RotateAnimation(-180, -360,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		//动画持续的时间
		down.setDuration(300);
		down.setFillAfter(true);
	}

	private void initHeadView() {
		headView = View.inflate(getContext(), R.layout.refreshlist_head,null);

		iv_arrow = (ImageView) headView.findViewById(R.id.iv_arrow);
		bar_rotate =(ProgressBar) headView.findViewById(R.id.bar_rotate);
		tv_time = (TextView) headView.findViewById(R.id.tv_time);
		tv_state = (TextView) headView.findViewById(R.id.tv_state);

		headView.measure(0, 0);
		height = headView.getMeasuredHeight();
		headView.setPadding(0, -height,0,0);
		addHeaderView(headView);
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downY=(int) ev.getY();
				break;

			case MotionEvent.ACTION_MOVE:
				int deltaY=(int) (ev.getY()-downY);
				int paddingTop=-height+deltaY;
				//当课件条目是0的时候才可以下拉刷新
				if(paddingTop>-height&&getFirstVisiblePosition()==0){
					headView.setPadding(0,paddingTop,0,0);
					if(paddingTop>=0&&currentState==PULL_REFRESH){
						//从下拉刷新进入松开刷新状态
						currentState=REALEASE_REFRESH;
						refreshHeadView();
					}else if(paddingTop<=0&&currentState==REALEASE_REFRESH){
						//进入下拉刷新状态
						currentState=PULL_REFRESH;
						refreshHeadView();
					}

					return true;//拦截TouchMove，不然ListView处理该事件，会造成ListView无法滑动
				}
				break;
			case MotionEvent.ACTION_UP:
				if(currentState==PULL_REFRESH){
					headView.setPadding(0, -height,0,0);
				}else if(currentState==REALEASE_REFRESH){
					//headView完全显示
					headView.setPadding(0,0,0,0);
					currentState=REFRESHING;
					refreshHeadView();

					if(listener!=null){
						listener.onPullRefresh();
					}
				}
				break;
		}
		return super.onTouchEvent(ev);
	}
	/**
	 * 根据currentState来更新headView
	 */
	private void refreshHeadView(){
		switch (currentState) {
			case PULL_REFRESH:
				tv_state.setText("下拉刷新");
				iv_arrow.startAnimation(down);
				break;
			case REALEASE_REFRESH:
				tv_state.setText("松开刷新");
				iv_arrow.startAnimation(up);
				break;
			case REFRESHING:
				//停止动画
				iv_arrow.clearAnimation();
				//隐藏箭头
				iv_arrow.setVisibility(View.INVISIBLE);
				//显示bar_rotate
				bar_rotate.setVisibility(View.VISIBLE);
				tv_state.setText("正在刷新,请稍后");
				break;
		}
	}
	/**
	 * 完成刷新操作，重置状态
	 */
	public void completeRefresh(){
		if(isLoading){
			//重置footView
			footView.setPadding(0,0,0,-footViewHeight);
			isLoading=false;
		}else{
			//重置headView
			headView.setPadding(0, -height,0,0);
			currentState=PULL_REFRESH;
			bar_rotate.setVisibility(View.INVISIBLE);
			iv_arrow.setVisibility(View.VISIBLE);
			tv_state.setText("下拉刷新");
			tv_time.setText("最后刷新："+getCurrentTime());
		}

	}
	/**
	 * 获取当前时间
	 * @return
	 */
	private String getCurrentTime(){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}

	private OnRefreshListener listener;
	private View footView;
	private int footViewHeight;

	public void setOnRefreshListener(OnRefreshListener listener) {
		this.listener = listener;
	}

	public interface OnRefreshListener{
		void onPullRefresh();
		void onLoadingMore();
	}
	/**
	 * 初始化FootView
	 */
	private void initFootView() {
		footView = View.inflate(getContext(), R.layout.refreshlist_bottom,null);
		footView.measure(0, 0);
		footViewHeight = footView.getMeasuredHeight();
//		Log.e("footViewHeight","++++"+footViewHeight);
		footView.setPadding(0, 0, 0, -footViewHeight);
		addFooterView(footView);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}
	/**
	 * SCROLL_STATE_IDLE:闲置状态，就是手指松开
	 * SCROLL_STATE_TOUCH_SCROLL:手指触摸滚动
	 * SCROLL_STATE_FLING:快速手指松开
	 *
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(scrollState==OnScrollListener.SCROLL_STATE_IDLE&&getLastVisiblePosition()==(getCount()-1)){
//			Log.e("onScrollStateChanged","此时需要显示footView");
			isLoading=true;
			footView.setPadding(0, 0, 0, 0);
			setSelection(getCount());//显示ListView最后一条
			if(listener!=null){
				listener.onLoadingMore();
			}
		}

	}
}











