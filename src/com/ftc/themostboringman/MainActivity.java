package com.ftc.themostboringman;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

import com.ftc.themosrboringman.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {
    private TextView tvTime;
    private Button btnTouch;
    private AdView adView;
    private Timer touchTimer;
    private TimerTask touchTask;
    private final int MSG_SET_TEXT = 0;
    private final int MSG_TOUCH_END = 1;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	tvTime = (TextView)findViewById(R.id.tv_touch_time);
	initAd();
	initTouchButton();
    }

    private void initAd() {
	adView = (AdView) findViewById(R.id.ad_view);
	AdRequest adRequest = new AdRequest.Builder().build();
	adView.loadAd(adRequest);
    }

    private Handler handler = new Handler() {

	@Override
	public void handleMessage(Message msg) {
	    switch (msg.what) {
	    case MSG_SET_TEXT:
		Long time= (Long) msg.obj;
		String timeStr = getTimeFormat(startTime,time.longValue());
		tvTime.setText(timeStr);
		break;
	    case MSG_TOUCH_END:
		break;
	    }

	}
    };
    public String getTimeFormat(long startTime,long endTime){
	return endTime-startTime+"";
    }

    private void initTimer() {
	touchTimer = new Timer();
	touchTask = new TimerTask() {

	    @Override
	    public void run() {
		Message msg = new Message();
		msg.what = MSG_SET_TEXT;
		msg.obj = System.currentTimeMillis();
		handler.sendMessage(msg);
	    }

	};
    }

    private void initTouchButton() {

	btnTouch = (Button) findViewById(R.id.btn_touch);
	btnTouch.setOnTouchListener(new OnTouchListener() {

	    @Override
	    public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		    initTimer();
		    startTime = System.currentTimeMillis();
		    touchTimer.schedule(touchTask, 0, 100);
		    break;
		case MotionEvent.ACTION_OUTSIDE:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
		    touchTimer.cancel();
		    Message msg = new Message();
		    msg.what = MSG_SET_TEXT;
		    msg.obj = System.currentTimeMillis();
		    handler.sendMessage(msg);
		    break;
		}
		return false;
	    }

	});
    }

    @Override
    public void onPause() {
	adView.pause();
	super.onPause();
    }

    @Override
    public void onResume() {
	super.onResume();
	adView.resume();
    }

    @Override
    public void onDestroy() {
	adView.destroy();
	super.onDestroy();
    }
}
