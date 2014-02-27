package com.minsub.calendar;

/*
 * Class : ActivityOneDay
 * 작성자 : 지민섭 
 * 2011. 01. 16
 * 설명 : 하루단위의 일정을 상세한 시간단위로 사용자에게 제공
 *       
 */

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.minsub.calendar.planer.PlanerManager;
import com.minsub.calendar.planer.PlanerManager.PlanNode;
import com.minsub.utils.JStringUtils;

public class ActivityOneDay extends Activity {
	public static final String EXT_TIME = "oneday_time";
	public static final String EXT_INDEX = "oneday_index";
	
	private final int DEFAULT_PLAN_CNT = 4;
	private final long MILLISECOND_ONEDAY = 60 * 60 * 24 * 1000; // 하루의 밀리세컨트 값
	private final int colors[] ={0x901E90FF, 0x90DB7A9D , 0x903CC8C8, 0x90FF9100, 0x90B750EA, 0x90834683};
	
	
	private PlanerManager planerMgr = null;
	private LinearLayout cover_plan;
	private RelativeLayout cover;
	private TextView TV_subject;
	
	private long time;
	private int selectedIndex;
	private int coverWidth, coverHeight;
	private int colorCnt = 0;
	private int oneMinOfHeight;
	
	private Animation aniLeft, aniRight, aniDown;
	
	
	// 슬라이드를 위한 필드
	private final float SLIDE_ACTION = 150.0f;
	private final float SLIDE_ACTION_MIN = 180.0f;
	private float m_nCurrentX;
	private float m_nCurrentY;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oneday);

		init();

	}

	private void init() {
		
		cover = (RelativeLayout) findViewById(R.id.cover);
		cover_plan = (LinearLayout) findViewById(R.id.cover_plans);
		TV_subject = (TextView) findViewById(R.id.tv_subject);
		planerMgr = PlanerManager.getInstance(this);
		
		aniLeft = AnimationUtils.loadAnimation(ActivityOneDay.this, R.anim.push_left_in);
		aniRight = AnimationUtils.loadAnimation(ActivityOneDay.this, R.anim.push_right_in);
		aniDown = AnimationUtils.loadAnimation(ActivityOneDay.this, R.anim.push_down_in);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			time = extras.getLong(EXT_TIME);
			selectedIndex = extras.getInt(EXT_INDEX);
		} else{
			finish();
		}
		
	}
	
	// 어제로 이동
	private void prevDay() {
		if (selectedIndex == 0 ){
			Toast.makeText(this, "캘린더 화면에서 이전달로 이동해야 어제로 넘어갈 수 있습니다.", Toast.LENGTH_LONG).show();
			return;
		}else{
			time -= MILLISECOND_ONEDAY;
			selectedIndex--;
			setPlans();
			cover.startAnimation(aniRight);
		}
		
	}

	// 내일로 이동
	private void nextDay() {
		if (selectedIndex == 41) {
			Toast.makeText(this, "캘린더 화면에서 다음달로 이동해야 내일로 넘어갈 수 있습니다.", Toast.LENGTH_LONG).show();
			return;
		} else {
			time += MILLISECOND_ONEDAY;
			selectedIndex++;
			setPlans();
			cover.startAnimation(aniLeft);
		}
	}
	
	// 날자 subject 변경
	private void setTextSubject() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		TV_subject.setText(c.get(Calendar.YEAR) + ". "
				+ JStringUtils.retainNumber10(c.get(Calendar.MONTH) + 1) + ". "
				+ JStringUtils.retainNumber10(c.get(Calendar.DATE)));
	}
	
	
//	// add plans
//	private void setPlans(){
//		setTextSubject();
//		cover_plan.removeAllViews();
//		Cursor cursor = planerMgr.selectPlan(time, time + MILLISECOND_ONEDAY);
//		int cursorCnt = 0;
//		if (cursor != null && cursor.getCount() > 0){
//			
//			if ( cursor.getCount() > DEFAULT_PLAN_CNT){
//				cursorCnt = cursor.getCount();
//			} else {
//				cursorCnt = DEFAULT_PLAN_CNT;
//			}
//			
//			cursor.moveToFirst();
//			
//			do {
//				
//				// data 세팅
//				PlanData data = new PlanData();
//				data.id = cursor.getLong(0);
//				data.subject = cursor.getString(1);
//				data.startDate = cursor.getLong(2);
//				data.endDate = cursor.getLong(3);
//				data.location = cursor.getString(4);
//				data.memo = cursor.getString(5);
//				data.alarm = cursor.getInt(6);
//				data.repeat = cursor.getInt(7);
//				data.vibrate = cursor.getInt(8);
//				
//				// 높이 계산
//				int paddingTop = 0, paddingBottom = 0;
//				Calendar c = Calendar.getInstance();
//				c.setTimeInMillis(data.startDate);
//				int startMinOfDay = (c.get(Calendar.HOUR_OF_DAY) * 60) + c.get(Calendar.MINUTE);
//				c.setTimeInMillis(data.endDate);
//				int endMinOfDay = (c.get(Calendar.HOUR_OF_DAY) * 60) + c.get(Calendar.MINUTE);
//				
//				if (data.startDate >= time && data.endDate <= time + MILLISECOND_ONEDAY) { // 시작시간이과 종료시간이 오늘안에 있을 때
//					paddingTop = oneMinOfHeight * startMinOfDay;
//					paddingBottom = ((60 * 24) - endMinOfDay) * oneMinOfHeight;
//				} else if ( data.startDate >= time ) { // 시작시간만 오늘안에 있음 
//					paddingTop = oneMinOfHeight * startMinOfDay;
//				} else if( data.endDate <= time + MILLISECOND_ONEDAY) { // 종료시간이 하루안에 걸릴때
//					paddingBottom = ((60 * 24) - endMinOfDay) * oneMinOfHeight;
//				}
//				
//				
//				// 레아이웃 세팅
//				LinearLayout layout = new LinearLayout(this);
//				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(coverWidth/cursorCnt, LayoutParams.FILL_PARENT);
//				layout.setLayoutParams(params);
//				layout.setPadding(0, paddingTop, 0, paddingBottom);
//				
//				
//				LinearLayout inner_layout = new LinearLayout(this);
//				LinearLayout.LayoutParams inner_params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
//				inner_layout.setLayoutParams(inner_params);
//				inner_layout.setBackgroundColor(getColor());
//				
//				// subject 레이아웃 설정
//				TextView tv = new TextView(this);
//				tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
//				tv.setGravity(Gravity.CENTER);
//				tv.setTextColor(Color.BLACK);
//				tv.setTextSize(20.0f);
//				tv.setText(JStringUtils.convertVertical(data.subject));
//				
//				inner_layout.addView(tv);
//				layout.addView(inner_layout);
//				cover_plan.addView(layout);
//				inner_layout.setTag(data);
//				
//				inner_layout.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						PlanData item = (PlanData)v.getTag();
//						
//						// 클릭시 일정 상세정보보기로 가야함
//						Intent intent = new Intent(ActivityOneDay.this, ActivityPlan.class);
//						intent.putExtra(ActivityPlan.EXT_TYPE, 1);
//						intent.putExtra(ActivityPlan.EXT_ID, item.id);
//						intent.putExtra(ActivityPlan.EXT_SUBJECT, item.subject);
//						intent.putExtra(ActivityPlan.EXT_LOCATION, item.location);
//						intent.putExtra(ActivityPlan.EXT_STARTDATE, item.startDate);
//						intent.putExtra(ActivityPlan.EXT_ENDDATE, item.endDate);
//						intent.putExtra(ActivityPlan.EXT_ALARM, item.alarm);
//						intent.putExtra(ActivityPlan.EXT_REPEAT, item.repeat);
//						intent.putExtra(ActivityPlan.EXT_VIBRATE, item.vibrate);
//						intent.putExtra(ActivityPlan.EXT_MEMO, item.memo);
//						startActivityForResult(intent, 0);
//					}
//				});
//				
//				// 애니메이션
//				layout.startAnimation(aniDown);
//				
//			} while (cursor.moveToNext());
//			cursor.close();
//			cursorCnt = 0;
//		}
//	}
	
	
	// add plans
	private void setPlans(){
		setTextSubject();
		cover_plan.removeAllViews();
		
		
		PlanNode node = planerMgr.getNode(selectedIndex);
		// 링크드 리스트에 갯수 알아내기
		PlanNode tmp = node;
		int cursorCnt = 0;
		while(tmp != null) {
			cursorCnt++;
			tmp = tmp.getNext();
		}
		
		if(cursorCnt < DEFAULT_PLAN_CNT){
			cursorCnt = DEFAULT_PLAN_CNT;
		}
		
		while(node != null){
			
			// data 세팅
			PlanData data = new PlanData();
			data.id = node.getId();
			data.subject = node.getSubject();
			data.startDate = node.getStartDate();
			data.endDate = node.getEndDate();
			data.location = node.getLocation();
			data.memo = node.getMemo();
			data.alarm = node.getAlarm();
			data.repeat = node.getRepeat();
			data.vibrate = node.getVibrate();
			
			// 높이 계산
			int paddingTop = 0, paddingBottom = 0;
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(data.startDate);
			int startMinOfDay = (c.get(Calendar.HOUR_OF_DAY) * 60) + c.get(Calendar.MINUTE);
			c.setTimeInMillis(data.endDate);
			int endMinOfDay = (c.get(Calendar.HOUR_OF_DAY) * 60) + c.get(Calendar.MINUTE);
			
			if (data.startDate >= time && data.endDate <= time + MILLISECOND_ONEDAY) { // 시작시간이과 종료시간이 오늘안에 있을 때
				paddingTop = oneMinOfHeight * startMinOfDay;
				paddingBottom = ((60 * 24) - endMinOfDay) * oneMinOfHeight;
				if (data.startDate == data.endDate) {	// 시작시간과 끝시간이 같으면 그림이 안그려짐 그래서 좀 벌림
					paddingBottom -= (oneMinOfHeight * 30);
				} 
			} else if ( data.startDate >= time ) { // 시작시간만 오늘안에 있음 
				paddingTop = oneMinOfHeight * startMinOfDay;
			} else if( data.endDate <= time + MILLISECOND_ONEDAY) { // 종료시간이 하루안에 걸릴때
				paddingBottom = ((60 * 24) - endMinOfDay) * oneMinOfHeight;
			}
				
			// 레아이웃 세팅
			LinearLayout layout = new LinearLayout(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(coverWidth/cursorCnt, LayoutParams.FILL_PARENT);
			layout.setLayoutParams(params);
			layout.setPadding(0, paddingTop, 0, paddingBottom);
			
			
			LinearLayout inner_layout = new LinearLayout(this);
			LinearLayout.LayoutParams inner_params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			inner_layout.setLayoutParams(inner_params);
			inner_layout.setBackgroundColor(getColor());
			
			// subject 레이아웃 설정
			TextView tv = new TextView(this);
			tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
			tv.setGravity(Gravity.CENTER);
			tv.setTextColor(Color.BLACK);
			tv.setTextSize(20.0f);
			tv.setText(JStringUtils.convertVertical(data.subject));
			
			inner_layout.addView(tv);
			layout.addView(inner_layout);
			cover_plan.addView(layout);
			inner_layout.setTag(data);
			
			inner_layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					PlanData item = (PlanData)v.getTag();
					
					// 클릭시 일정 상세정보보기로 가야함
					Intent intent = new Intent(ActivityOneDay.this, ActivityPlan.class);
					intent.putExtra(ActivityPlan.EXT_TYPE, 1);
					intent.putExtra(ActivityPlan.EXT_ID, item.id);
					intent.putExtra(ActivityPlan.EXT_SUBJECT, item.subject);
					intent.putExtra(ActivityPlan.EXT_LOCATION, item.location);
					intent.putExtra(ActivityPlan.EXT_STARTDATE, item.startDate);
					intent.putExtra(ActivityPlan.EXT_ENDDATE, item.endDate);
					intent.putExtra(ActivityPlan.EXT_ALARM, item.alarm);
					intent.putExtra(ActivityPlan.EXT_REPEAT, item.repeat);
					intent.putExtra(ActivityPlan.EXT_VIBRATE, item.vibrate);
					intent.putExtra(ActivityPlan.EXT_MEMO, item.memo);
					startActivityForResult(intent, 0);
				}
			});
			
			// 애니메이션
			layout.startAnimation(aniDown);
			
			node = node.getNext();
		}
	}
	
	private int getColor(){
		if ( colorCnt == colors.length) {
			colorCnt = 0;
		}
		return colors[colorCnt++];
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		coverWidth = cover_plan.getWidth();
		coverHeight = cover_plan.getHeight();
		oneMinOfHeight = coverHeight / (60 * 24);
		setPlans();
	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESULT_OK)
		{
			switch(requestCode)
			{
				case 0:					
					setPlans();
					break;
				default:
					break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	// 슬라이드 이벤트를 위한 오버라이드 함수
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				m_nCurrentX = event.getX();
				m_nCurrentY = event.getY();
				break;
	
			case MotionEvent.ACTION_UP:
				if (m_nCurrentY + SLIDE_ACTION_MIN < event.getY() || m_nCurrentY - SLIDE_ACTION_MIN > event.getY()) {
					return true;
				}
	
				if (m_nCurrentX + SLIDE_ACTION < event.getX()) { // LEFT
					prevDay();
				} else if (m_nCurrentX > SLIDE_ACTION + event.getX()) { // RIGHT
					nextDay();
				}
				break;
		}
		
		return super.dispatchTouchEvent(event);
	}

	// 데이터 저장 클래스
	private class PlanData{
		 long id;
		 String subject;
		 long startDate;
		 long endDate;
		 String location;
		 String memo;
		 int alarm;
		 int repeat;
		 int vibrate;
	}
}
