package com.minsub.calendar;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minsub.calendar.planer.PlanerManager;
import com.minsub.calendar.planer.PlanerManager.PlanNode;
import com.minsub.jcalendar.JCalendar;
import com.minsub.jcalendar.JCalendar.OnChangeCalendarListener;
import com.minsub.jcalendar.JCalendar.OnClickDayListener;
import com.minsub.jcalendar.JCalendar.OnLongClickDayListener;
import com.minsub.utils.JStringUtils;

public class Main extends Activity {
	public final static String TAG = "jcalendar";
	
	private final int DEFAULT_SIZE_CALENDAR_WIDTH = 0;
	private final int DEFAULT_SIZE_CALENDAR_HEIGHT = 500;
	private final int LAYOUT_PLAN_LIST = R.layout.plan_list_item;
	private final int LAYOUT_PLAN_CALENDAR = R.layout.plan_calendar_item;
	private final int REQUEST_TYPE_ADD = 0;
	private final int REQUEST_TYPE_VIEW = 1;
	private final int REQUEST_TYPE_UPDATE = 2;
	
	private PlanerAlarm planerAlarm;
	private PlanerManager planerMgr;
	private PlanNode[] planNodeTable = null;
	private JCalendar jCalendar;
	private LinearLayout[] bottombarTable;
	
	private LinearLayout LL_plans; 
	private LinearLayout LL_Calendar;
	private View BTN_add;
	private View BTN_Search;
	
	// 슬라이드를 위한 필드
	private final float SLIDE_ACTION = 150.0f;
	private final float SLIDE_ACTION_MIN = 180.0f;
	private float m_nCurrentX;
	private float m_nCurrentY;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
        
    }

	@Override
	protected void onResume() {
		// 다시 PlanData세팅 후 그리기
		setCalendarPlan();
		addPlanLayout(LL_plans, jCalendar.getSelectedIndex());
		super.onResume();
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESULT_OK)
		{
			switch(requestCode)
			{
				case REQUEST_TYPE_ADD:					
				case REQUEST_TYPE_VIEW:
				case REQUEST_TYPE_UPDATE:
					// 수정된 PlanData를 세팅 후 그리기( onResume에서 처리됨에 따라 주석처리)
//					setCalendarPlan();
//					addPlanLayout(LL_plans, jCalendar.getSelectedIndex());
					break;
				default:
					break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void init() {
		Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		int height = display.getHeight();
		int width = display.getWidth();
		jCalendar = new JCalendar(this, DEFAULT_SIZE_CALENDAR_WIDTH, (int)(height * 0.63));
//		jCalendar = new JCalendar(this, DEFAULT_SIZE_CALENDAR_WIDTH, DEFAULT_SIZE_CALENDAR_HEIGHT);
		
		bottombarTable = jCalendar.getBottomBarTable();
		planerMgr = PlanerManager.getInstance(this);
		planerAlarm = PlanerAlarm.getInstance(this);
		planerAlarm.setTodayAlarm();
		
		LL_Calendar = (LinearLayout)findViewById(R.id.cover_calendar);
		LL_Calendar.addView(jCalendar);
		LL_plans = (LinearLayout)findViewById(R.id.cover_plans);
		BTN_add = findViewById(R.id.btn_add);
		BTN_Search = findViewById(R.id.btn_search);
		
		setListener();
		
		jCalendar.setNextMonthAni(AnimationUtils.loadAnimation(Main.this, R.anim.push_left_in));
		jCalendar.setPrevMonthAni(AnimationUtils.loadAnimation(Main.this, R.anim.push_right_in));
	}
	
	// 리스너 등록
	private void setListener() {
		// jCalendar의 데이를 클릭시 
		jCalendar.setOnClickDayListener(new OnClickDayListener() {
			@Override
			public void onClickDay(int index, int year, int month, int day) {
				addPlanLayout(LL_plans, index);
			}
		});
		
		jCalendar.setOnLongClickDayListener(new OnLongClickDayListener() {
			@Override
			public void onLongClickDay(int index, int year, int month, int day) {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, year);
				c.set(Calendar.MONTH, month-1);
				c.set(Calendar.DATE, day);
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				
				Intent intent = new Intent(Main.this, ActivityOneDay.class);
				intent.putExtra(ActivityOneDay.EXT_TIME, c.getTimeInMillis());
				intent.putExtra(ActivityOneDay.EXT_INDEX, index);
				startActivity(intent);
			}
		});

		// jCalendar 월 이동시..
		jCalendar.setOnChangeCalendarListener(new OnChangeCalendarListener() {
			@Override
			public void onChangeCalendar(int index, int year, int month, int day) {
				setCalendarPlan();
			}
		});
		
		// ADD 버튼 리스너
		BTN_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Main.this,ActivityPlan.class);
				intent.putExtra(ActivityPlan.EXT_TYPE, 0);
				intent.putExtra(ActivityPlan.EXT_SELECTED_DATE_MILLI, jCalendar.getSelectedDate().getTimeInMillis());
				startActivityForResult(intent,REQUEST_TYPE_ADD);
			}
		});
		
		// Search 버튼 리스너
		BTN_Search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Main.this, ActivitySearch.class));
			}
		});
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
					jCalendar.prevMonth();
				} else if (m_nCurrentX > SLIDE_ACTION + event.getX()) { // RIGHT
					jCalendar.nextMonth();
				}
				break;
		}
		
		return super.dispatchTouchEvent(event);
	}

	private void resetBottombarTable(){
		for (int i = 0; i < bottombarTable.length; i++){
			bottombarTable[i].removeAllViews();
		}
	}
	
	// 플랜노드 테이블을 이용해 일정을 jCalendar에 표시
	private void setCalendarPlan(){
		resetBottombarTable();
		planNodeTable = planerMgr.getPlanNodeTable(jCalendar.getDateStartIndex().getTimeInMillis(), jCalendar.getDateEndIndex().getTimeInMillis());
		LayoutInflater inflater = getLayoutInflater();
		View view;
		TextView tv_subject;
		PlanNode item;
	
		for (int i = 0; i < bottombarTable.length; i++){
			item = planNodeTable[i];
			while(item != null){
				view = inflater.inflate(LAYOUT_PLAN_CALENDAR, null);
				tv_subject  = (TextView)view.findViewById(R.id.tv_subject);
				tv_subject.setText(item.getSubject());
				bottombarTable[i].addView(view);
				item = item.getNext();
			}
		}
	}
	
	// 하루를 선택했을때 해당 일에 있는 모든 스케줄을 리스트로 표시
	private void addPlanLayout(LinearLayout parent, int index){
		parent.removeAllViews();
		View view = null;
		LayoutInflater inflater = getLayoutInflater();
		PlanNode node = planNodeTable[index];
		TextView tv_time, tv_subject;
		String strTime;
		
		while(node != null){
			view = inflater.inflate(LAYOUT_PLAN_LIST, null);
			tv_time = (TextView)view.findViewById(R.id.tv_time);
			tv_subject  = (TextView)view.findViewById(R.id.tv_subject);
			
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(node.getTodayTime());
			if (node.getTodayTime() == 0) {
				strTime = (getString(R.string.plan_item_alltime));
			} else {
				if (node.getTodayTime() == node.getStartDate()) {
					strTime = getString(R.string.plan_item_starttime)
							+ " " + JStringUtils.retainNumber10(c.get(Calendar.HOUR_OF_DAY))
							+ ":" + JStringUtils.retainNumber10(c.get(Calendar.MINUTE));
				} else {
					strTime = getString(R.string.plan_item_endtime)
							+ " " + JStringUtils.retainNumber10(c.get(Calendar.HOUR_OF_DAY))
							+ ":" + JStringUtils.retainNumber10(c.get(Calendar.MINUTE));
				}
			}
			tv_time.setText(strTime);
			tv_subject.setText(node.getSubject());

			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					PlanNode item = (PlanNode)v.getTag();
					
					// 클릭시 일정 상세정보보기로 가야함
					Intent intent = new Intent(Main.this, ActivityPlan.class);
					intent.putExtra(ActivityPlan.EXT_TYPE, 1);
					intent.putExtra(ActivityPlan.EXT_ID, item.getId());
					intent.putExtra(ActivityPlan.EXT_SUBJECT, item.getSubject());
					intent.putExtra(ActivityPlan.EXT_LOCATION, item.getLocation());
					intent.putExtra(ActivityPlan.EXT_STARTDATE, item.getStartDate());
					intent.putExtra(ActivityPlan.EXT_ENDDATE, item.getEndDate());
					intent.putExtra(ActivityPlan.EXT_ALARM, item.getAlarm());
					intent.putExtra(ActivityPlan.EXT_REPEAT, item.getRepeat());
					intent.putExtra(ActivityPlan.EXT_VIBRATE, item.getVibrate());
					intent.putExtra(ActivityPlan.EXT_MEMO, item.getMemo());
					startActivityForResult(intent, REQUEST_TYPE_VIEW);
				}
			});
		 
			view.setTag(node);		//뷰에 고유의 PlanNode를 연결 시킨다.
			parent.addView(view); // 완성된 레이아웃을 Add

			node = node.getNext();
		}
		
		// 애니메이션 부분
		Animation ani = AnimationUtils.loadAnimation(Main.this, R.anim.push_up_in);
		parent.startAnimation(ani);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,0,Menu.NONE,getString(R.string.menu_backup)).setIcon(R.drawable.xml_export);
		menu.add(0,1,Menu.NONE,getString(R.string.menu_restore)).setIcon(R.drawable.xml_import);
		menu.add(0,2,Menu.NONE,getString(R.string.menu_oneday)).setIcon(R.drawable.menu_oneday);

		return super.onCreateOptionsMenu(menu);
	}
	
	

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId())
		{
			case 0:
				if (planerMgr.backupXML()){
					Toast.makeText(Main.this, "XML Export 완료", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(Main.this, "XML Export 실패", Toast.LENGTH_LONG).show();
				}
				break;
				
			case 1:
				if (planerMgr.restoreXML()){
					Toast.makeText(Main.this, "XML Import 완료", Toast.LENGTH_LONG).show();
					setCalendarPlan();
					addPlanLayout(LL_plans, jCalendar.getSelectedIndex());
					planerAlarm.setTodayAlarm();
				} else {
					Toast.makeText(Main.this, "XML Import 실패", Toast.LENGTH_LONG).show();
				}
				break;
			case 2:
				Calendar c = jCalendar.getSelectedDate();
				Intent intent = new Intent(Main.this, ActivityOneDay.class);
				intent.putExtra(ActivityOneDay.EXT_TIME, c.getTimeInMillis());
				intent.putExtra(ActivityOneDay.EXT_INDEX, jCalendar.getSelectedIndex());
				startActivity(intent);
				break;
				
			default:
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}