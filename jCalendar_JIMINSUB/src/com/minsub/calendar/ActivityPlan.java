package com.minsub.calendar;

/*
 * Class : ActivityPlan
 * 작성자 : 지민섭 
 * 2011. 01. 10
 * 설명 : 스케줄의 입력, 삭제, 수정등에서 사용될 화면 클래스
 *       
 */

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.minsub.calendar.planer.PlanerManager;
import com.minsub.utils.JStringUtils;

public class ActivityPlan extends Activity {
	public static final String EXT_TYPE = "type";			// 0: 입력, 1: 보기, 2:편집
	public static final String EXT_SELECTED_DATE_MILLI = "selecteddate";
	
	public static final String EXT_ID = "id";
	public static final String EXT_SUBJECT = "subject";
	public static final String EXT_LOCATION = "location";
	public static final String EXT_STARTDATE = "startdate";
	public static final String EXT_ENDDATE = "enddate";
	public static final String EXT_ALARM = "alarm";
	public static final String EXT_REPEAT = "repeat";
	public static final String EXT_VIBRATE = "vibrate";
	public static final String EXT_MEMO = "memo";
	
	private final String[] SP_STRING_ALARM = {"none","That time","10 minutes ago","30 minutes ago", "1 hour ago"};
	private final String[] SP_STRING_REPEAT = {"none","daily","each week","monthly"};
	private final String[] SP_STRING_VIBRATE = {"none","set"};
	private final int[] SP_VALUE = {0,1,2,3,4};

	// UI 필드
	private TextView TV_title;
	private EditText ED_subject;
	private EditText ED_location;
	private Button BTN_startdateyear;
	private Button BTN_startdatehour;
	private Button BTN_enddateyear;
	private Button BTN_enddatehour;
	private Spinner SP_alarm;
	private Spinner SP_repeat;
	private Spinner SP_vibrate;
	private EditText ED_memo;
	private Button BTN_commit;
	private Button BTN_edit;

	private DatePickerDialog.OnDateSetListener startDateListener;
	private DatePickerDialog.OnDateSetListener endDateListener;
	private TimePickerDialog.OnTimeSetListener startTimeListener;
	private TimePickerDialog.OnTimeSetListener endTimeListener;
	
	private int type = 0;
	private long selecteddate;
	
	private long id;
	private String subject;
	private String location;
	private long startdate;
	private long enddate;
	private int alarm = 0;
	private int repeat = 0;
	private int vibrate = 0;
	private String memo;
	
	private int startYear, startMonth, startDay, startHour, startMin;
	private int endYear, endMonth, endDay, endHour, endMin;
	
	private PlanerManager planerMgr = null;
	private PlanerAlarm planerAlarm = null;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plan);
		
		init();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	

	private void setAllViewVisibility(boolean bool){
		ED_subject.setEnabled(bool);
		ED_location.setEnabled(bool);
		BTN_startdateyear.setEnabled(bool);
		BTN_startdatehour.setEnabled(bool);
		BTN_enddateyear.setEnabled(bool);
		BTN_enddatehour.setEnabled(bool);
		SP_alarm.setEnabled(bool);
		SP_repeat.setEnabled(bool);
		SP_vibrate.setEnabled(bool);
		ED_memo.setEnabled(bool);
	}
	
	private void init(){
		// UI 세팅
		TV_title = (TextView)findViewById(R.id.tv_title);
		ED_subject = (EditText)findViewById(R.id.ed_subject);
		ED_location = (EditText)findViewById(R.id.ed_location);
		BTN_startdateyear = (Button)findViewById(R.id.btn_startdate_year);
		BTN_startdatehour = (Button)findViewById(R.id.btn_startdate_hour);
		BTN_enddateyear = (Button)findViewById(R.id.btn_enddate_year);
		BTN_enddatehour = (Button)findViewById(R.id.btn_enddate_hour);
		SP_alarm = (Spinner)findViewById(R.id.sp_alarm);
		SP_repeat = (Spinner)findViewById(R.id.sp_repeat);
		SP_vibrate = (Spinner)findViewById(R.id.sp_vibrate);
		ED_memo = (EditText)findViewById(R.id.ed_memo);
		BTN_commit = (Button)findViewById(R.id.btn_commit);
		BTN_edit = (Button)findViewById(R.id.btn_edit);
		
		setExtraProperty();
		planerMgr = PlanerManager.getInstance(this);
		
		planerAlarm = PlanerAlarm.getInstance(ActivityPlan.this);
		
		setUIProperty();
		setListener();
	}
	
	// 넘어온 속성값으로 UI 변경
	private void setUIProperty() {
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();
		if (type == 0) { // 스케줄 입력일때..
			startCal.setTimeInMillis(selecteddate);
			startCal.set(Calendar.HOUR_OF_DAY, 13);
			endCal.setTimeInMillis(selecteddate);
			endCal.set(Calendar.HOUR_OF_DAY, 14);
			TV_title.setText(getString(R.string.plan_title_add));
			BTN_edit.setVisibility(View.GONE);
			BTN_commit.setText(getString(R.string.btn_plan_add));
		} else {
			startCal.setTimeInMillis(startdate);
			endCal.setTimeInMillis(enddate);
			ED_location.setText(location);
			ED_subject.setText(subject);
			ED_memo.setText(memo);
			TV_title.setText(getString(R.string.plan_title_view));
			BTN_commit.setText(getString(R.string.btn_plan_delete));
		}
	
		startYear = startCal.get(Calendar.YEAR);
		startMonth = startCal.get(Calendar.MONTH) + 1;
		startDay = startCal.get(Calendar.DATE);
		startHour = startCal.get(Calendar.HOUR_OF_DAY);
		startMin = startCal.get(Calendar.MINUTE);
		
		endYear = endCal.get(Calendar.YEAR);
		endMonth = endCal.get(Calendar.MONTH) + 1;
		endDay = endCal.get(Calendar.DATE);
		endHour = endCal.get(Calendar.HOUR_OF_DAY);
		endMin = endCal.get(Calendar.MINUTE);

		// 날짜 UI 세팅
		BTN_startdateyear.setText(startCal.get(Calendar.YEAR) + "/"
				+ JStringUtils.retainNumber10(startCal.get(Calendar.MONTH) + 1) + "/"
				+ JStringUtils.retainNumber10(startCal.get(Calendar.DATE)));

		BTN_enddateyear.setText(endCal.get(Calendar.YEAR) + "/"
				+ JStringUtils.retainNumber10(endCal.get(Calendar.MONTH) + 1) + "/"
				+ JStringUtils.retainNumber10(endCal.get(Calendar.DATE)));
		
		BTN_startdatehour.setText(JStringUtils.retainNumber10(startCal
				.get(Calendar.HOUR_OF_DAY))
				+ ":" + JStringUtils.retainNumber10(startCal.get(Calendar.MINUTE)));
		
		BTN_enddatehour.setText(JStringUtils.retainNumber10(endCal
				.get(Calendar.HOUR_OF_DAY))
				+ ":" + JStringUtils.retainNumber10(endCal.get(Calendar.MINUTE)));
		
		
		ArrayAdapter<String> asAlarm = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,SP_STRING_ALARM);
		asAlarm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		SP_alarm.setAdapter(asAlarm);
		SP_alarm.setSelection(alarm);
		
		asAlarm = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,SP_STRING_REPEAT);
		asAlarm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		SP_repeat.setAdapter(asAlarm);
		SP_repeat.setSelection(repeat);
		
		asAlarm = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,SP_STRING_VIBRATE);
		asAlarm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		SP_vibrate.setAdapter(asAlarm);
		SP_vibrate.setSelection(vibrate);

		
		if (type == 0) {
			setAllViewVisibility(true);
		} else if (type == 1) {
			setAllViewVisibility(false);
		} else if (type == 2) {
			setAllViewVisibility(true);
		}
	}
	
	// Activity에 넘어온 파라미터들로 속성값을 설정
	private void setExtraProperty() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			type = extras.getInt(EXT_TYPE);
			if (type == 0) {
				selecteddate = extras.getLong(EXT_SELECTED_DATE_MILLI);
			}else{
				id = extras.getLong(EXT_ID);
				subject = extras.getString(EXT_SUBJECT);
				location = extras.getString(EXT_LOCATION);
				startdate = extras.getLong(EXT_STARTDATE);
				enddate = extras.getLong(EXT_ENDDATE);
				alarm = extras.getInt(EXT_ALARM);
				repeat = extras.getInt(EXT_REPEAT);
				vibrate = extras.getInt(EXT_VIBRATE);
				memo = extras.getString(EXT_MEMO);
			}
		}
	}
	
	private void setListener(){
		// Commit 버튼 클릭시...(핵심)
		BTN_commit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String tstMsg = "";
				subject = ED_subject.getText().toString();
				if (type == 1) { // DELETE MODE
					planerMgr.deletePlan(id);
					tstMsg = getString(R.string.tst_success_delete);
					
					// 삭제된 일정에 대한 알람을 재설정
					planerAlarm.setTodayAlarm();
				} else {

					if(subject.equalsIgnoreCase("")){		// SUBJECT 공백시 리턴
						Toast.makeText(ActivityPlan.this,getString(R.string.tst_none_subject), Toast.LENGTH_LONG).show();
						return;
					}
					
					long startTime = getMilliTimeDate(startYear, startMonth, startDay, startHour, startMin);
					long endTime = getMilliTimeDate(endYear, endMonth, endDay, endHour, endMin);
					
					if (startTime > endTime) {
						Toast.makeText(ActivityPlan.this,getString(R.string.tst_error_time), Toast.LENGTH_LONG).show();
						return;
					}
					
					location = ED_location.getText().toString();
					memo = ED_memo.getText().toString();
					
					// 반복설정시 예외처리
					if (repeat == 1) { // 매일반복에 대한 예외처리
						if (startYear != endYear || startMonth != endMonth || startDay != endDay) {
							Toast.makeText(ActivityPlan.this,"매일반복설정시 시작시간과 종료시간이 동일한 날짜로 설정해야 합니다.", Toast.LENGTH_LONG).show();
							return;
						}
					} else if (repeat == 2) { // 매주
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(startTime);
						initCalendar(c);
						c.add(Calendar.DATE, 7);
						if (endTime > c.getTimeInMillis()) {
							Toast.makeText(ActivityPlan.this,"주간 반복시 일주일안으로 일정을 설정해야 합니다.", Toast.LENGTH_LONG).show();
							return;
						}
					} else if (repeat == 3) {
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(startTime);
						initCalendar(c);
						c.add(Calendar.MONTH, 1);
						if (endTime > c.getTimeInMillis()) {
							Toast.makeText(ActivityPlan.this,"매월 반복시 한달안으로 일정을 설정해야 합니다.", Toast.LENGTH_LONG).show();
							return;
						}
					}
					
					// 업데이트 or 추가 작업 수행
					if (type == 0) { // ADD MODE
						planerMgr.insertPlan(subject, startTime, endTime, location, memo, alarm, repeat, vibrate);
						tstMsg = getString(R.string.tst_success_add);
					}else if (type == 2) { // UPDATE MODE
						planerMgr.updatePlan(id,subject, startTime, endTime, location, memo, alarm, repeat, vibrate);
						tstMsg = getString(R.string.tst_success_update);
					}
					
					// Alarm or Vibrate 설정시 알람 재 등록
					if (alarm > 0 || vibrate > 0){
						planerAlarm.setTodayAlarm();
					}
				}
				
				Toast.makeText(ActivityPlan.this,subject+" "+  tstMsg, Toast.LENGTH_LONG).show();
	            setResult(Activity.RESULT_OK, null);
	            finish();
			}
		});
		
		// 스피너 클릭시..
		SP_alarm.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				alarm = SP_VALUE[arg2];
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});

		SP_repeat.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				repeat = SP_VALUE[arg2];
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		SP_vibrate.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				vibrate = SP_VALUE[arg2];
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		// Date 버튼
		BTN_startdateyear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DatePickerDialog dateDialog = new DatePickerDialog(
						ActivityPlan.this, startDateListener, startYear,
						startMonth - 1, startDay);
				dateDialog.show();
			}
		});
		
		BTN_enddateyear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DatePickerDialog dateDialog = new DatePickerDialog(
						ActivityPlan.this, endDateListener, endYear,
						endMonth - 1, endDay);
				dateDialog.show();
			}
		});
		
		BTN_startdatehour.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TimePickerDialog timeDialog = new TimePickerDialog(ActivityPlan.this,
						startTimeListener, startHour, startMin, false);
				timeDialog.show();
			}
		});
		
		BTN_enddatehour.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TimePickerDialog timeDialog = new TimePickerDialog(ActivityPlan.this,
						endTimeListener, endHour, endMin, false);
				timeDialog.show();
			}
		});
		
		// 시간 버튼 클릭시..
		startDateListener = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				startYear = year;
				startMonth = monthOfYear+1;
				startDay = dayOfMonth;
				BTN_startdateyear.setText(startYear+"/"+JStringUtils.retainNumber10(startMonth)+"/"+JStringUtils.retainNumber10(startDay));
			}
		};
		endDateListener = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				endYear = year;
				endMonth = monthOfYear+1;
				endDay = dayOfMonth;
				BTN_enddateyear.setText(endYear+"/"+JStringUtils.retainNumber10(endMonth)+"/"+JStringUtils.retainNumber10(endDay));
			}
		};
		startTimeListener = new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				startHour = hourOfDay;
				startMin = minute;
				BTN_startdatehour.setText(JStringUtils.retainNumber10(hourOfDay)+":"+JStringUtils.retainNumber10(minute));
			}
		};
		endTimeListener = new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				endHour = hourOfDay;
				endMin = minute;
				BTN_enddatehour.setText(JStringUtils.retainNumber10(hourOfDay)+":"+JStringUtils.retainNumber10(minute));
			}
		};
		
		BTN_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (type == 1) { //현재 보기모드에서 버튼 클릭
					TV_title.setText(getString(R.string.plan_title_update));
					type = 2;
					setAllViewVisibility(true);
					BTN_edit.setText(getString(R.string.btn_edit_edit));
					BTN_commit.setText(getString(R.string.btn_plan_update));
				} else { // 편집모드에서 버튼클릭
					TV_title.setText(getString(R.string.plan_title_view));
					type = 1;
					setAllViewVisibility(false);
					BTN_edit.setText(getString(R.string.btn_edit_view));
					BTN_commit.setText(getString(R.string.btn_plan_delete));
				}
			}
		});
		
	}
	
	// 입력받은 날짜의 각각의 속성값을 대입하여 MilliSecond단위로 반환
	private long getMilliTimeDate(int year, int month, int day, int hour, int min){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.SECOND,0);
		c.set(Calendar.MILLISECOND,0);
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month-1);
		c.set(Calendar.DATE, day);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, min);
		
		return c.getTimeInMillis();
	}
	
	// 캘린더의 시간 이하값을 0으로 초기화
	private void initCalendar(Calendar c){
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
	}
}

