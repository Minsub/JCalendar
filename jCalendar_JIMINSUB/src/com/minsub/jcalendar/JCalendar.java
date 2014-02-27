package com.minsub.jcalendar;

/*
 * Class : JCalendarData
 * 작성자 : 지민섭 
 * 2011. 01. 05
 * 설명 : UI를 설정하기 위한 Calendar의 계산을 수행하는 클래스
 * 		 7 * 6 개의 칸을 기준으로 달력을 설정을 기준
 * 		 각 셀에 어떤 날짜가 들어가는지를 확인 저장, 현재 선택된 날짜를 포함.
 */

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.minsub.jcalendar.calculator.JCalendarCalculator;

public class JCalendar extends LinearLayout {
	// 커스텀 리스너 (클릭한 셀의 인덱스, 날짜가 반환)
	public interface OnClickDayListener {		
		public void onClickDay(int index, int year, int month, int day); 
	}
	// 커스텀 리스너 (롱 클릭한 셀의 인덱스, 날짜가 반환)
	public interface OnLongClickDayListener {		
		public void onLongClickDay(int index, int year, int month, int day); 
	}
	// 커스텀 리스너 (달력이 변경될때 호출)
	public interface OnChangeCalendarListener {		
		public void onChangeCalendar(int index, int year, int month, int day); 
	}
	
	public static final int SIZE_MAX_DISPLAY    = 0;	//생정자에 width,height의 최대치로 설정할때 필요한 파라미터
	// 달력의 들어가는 Cell의 갯수
	private final int LENGTH_DAYLAYOUT 			= 7 * 6;
	private final int LENGTH_WEEKLAYOUT 		= 7;
	private final int LENGTH_HORI_LINE 			= 6 * 7;
	private final int LENGTH_VERT_LINE 			= 8;
	// 달력의 기본 색 지정
	private final int DEFAULT_COLOR_SAT				= Color.BLUE;
	private final int DEFAULT_COLOR_SUN 			= Color.RED;
	private final int DEFAULT_COLOR_BORDER 			= Color.GRAY;
	private final int DEFAULT_COLOR_DAY_FONT 		= Color.BLACK;
	private final int DEFAULT_COLOR_DATE		 	= Color.BLACK;
	private final int DEFAULT_COLOR_LUNAR		 	= Color.BLACK;
	
	
	private final int DEFAULT_COLOR_WEEK_BG	 		= 0xFF696969;
	private final int DEFAULT_COLOR_DAY_BG	 		= Color.WHITE;
	private final int DEFAULT_COLOR_DAY_BG_OTHER	= 0xFFDCDCDC;
	private final int DEFAULT_COLOR_DAY_CLICKBG 	= 0xFF50C2FF;
	private final int DEFAULT_COLOR_TODAY		 	= 0xFFF06E6E;
	
	// 달력의 기본 폰트 사이즈
	private final float DEFAULT_FONTSIZE_LUNAR		= 8.0f;
	private final float DEFAULT_FONTSIZE_DAY		= 15.0f;
	private final float DEFAULT_FONTSIZE_DATE		= 20.0f;
	private final float DEFAULT_FONTSIZE_WEEK		= 15.0f;
	
	private final String[] weekText = { "일", "월", "화", "수", "목", "금", "토" };
	
	// 기본 사이즈
	private final int DEFAULT_LINE_SIZE 	= 1;
	private final int DEFAULT_HEIGHT_DATE 	= 60;
	private final int DEFAULT_HEIGHT_WEEK 	= 30;
	// UI 관련 프로퍼티
	private LinearLayout containerDate;			//상단 Date표시의 리니어레이아웃
	private LinearLayout containerWeeks;		//요일 표시 레이아웃
	private LinearLayout containerDays;			//날짜 레이아웃
	
	private TextView TV_Date;
	private TextView BTN_prevMonth, BTN_nextMonth;
	
	private CellWeek[] weeks;					// 7
	private CellDay[] days;						// 42
	private LinearLayout[] horiLine;			// 42
	private LinearLayout[] vertLine;			// 8
	private LinearLayout[] bottomBar;			// 42
	
	private JCalendarCalculator calendarCalculator;
	private Animation aniLeft = null;
	private Animation aniRight = null;
	private OnClickDayListener clickDayListener = null;
	private OnLongClickDayListener longClickDayListener = null;
	private OnChangeCalendarListener changeCalendarListener = null;
	private DatePickerDialog.OnDateSetListener dateSetListener;
	
	private Context mCtx;
	private int width;							
	private int height;							
	
	// UI 세팅 
	private int widthCell;									//Day Cell, Week Cell의 넓이
	private int heightDayCell;								//Day Cell의 높이
	private int lineSize 			= DEFAULT_LINE_SIZE;	//라인 두께
	private int heightDateCell		= DEFAULT_HEIGHT_DATE;	//Date 셀의 높이
	private int heightWeekCell		= DEFAULT_HEIGHT_WEEK;	//Week Cell의 높이
	
	
	private float fontsizeWeek		= DEFAULT_FONTSIZE_WEEK;
	private float fontsizeDay		= DEFAULT_FONTSIZE_DAY;
	private float fontsizeLunarDay	= DEFAULT_FONTSIZE_LUNAR;
	private float fontsizeDate		= DEFAULT_FONTSIZE_DATE;
	
	private int colorLine 			= DEFAULT_COLOR_BORDER;
	private int colorFontSat		= DEFAULT_COLOR_SAT;
	private int colorFontSun		= DEFAULT_COLOR_SUN;
	private int colorFontDefault	= DEFAULT_COLOR_DAY_FONT;
	private int colorFontDate		= DEFAULT_COLOR_DATE;
	private int colorFontLunar      = DEFAULT_COLOR_LUNAR;
	
	private int colorBgWeek 		= DEFAULT_COLOR_WEEK_BG;
	private int colorBgSat			= DEFAULT_COLOR_DAY_BG;
	private int colorBgSun			= DEFAULT_COLOR_DAY_BG;
	private int colorBgDefault		= DEFAULT_COLOR_DAY_BG;
	private int colorBgOtherMonth	= DEFAULT_COLOR_DAY_BG_OTHER;
	private int colorBgClick		= DEFAULT_COLOR_DAY_CLICKBG;
	private int colorBgToday		= DEFAULT_COLOR_TODAY;
	

	public JCalendar(Context context, int width, int height) {
		super(context);
		Display display = ((WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay();
		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();
		
		if (width == SIZE_MAX_DISPLAY || width >= displayWidth ) {
			this.width = displayWidth;
		} else {
			this.width = width;
		}
		
		if (height == SIZE_MAX_DISPLAY || height >= displayHeight ) {
			this.height = displayHeight - 70;
		} else {
			this.height = height;
		}
		init(context);
	}
	
	// 현재 설정된 값에 대응하는 달력을 현재 날짜에 맞춰 그리기
	public void drawCalendar() {
		int[] table = calendarCalculator.getTable();
		String[] tableLunar = calendarCalculator.getLunarTable();
		for (int i = 0; i < LENGTH_DAYLAYOUT; i++) {
			days[i].setText(String.valueOf(table[i]));
			if (i % 7 == 0) {
				days[i].setLunarText(tableLunar[i / 7]);
			}
		}
		setDateUI();
		setSelectedDay(calendarCalculator.getSelectedIndex());
	}

	// 각 Day 하단에 달려있는 레이아웃의 모음인 배열을 반환
	public LinearLayout[] getBottomBarTable() {
		return bottomBar;
	}
	
	// 날짜 테이블의 0번째 인덱스의 날짜
	public Calendar getDateStartIndex(){
		Calendar c = calendarCalculator.getCalendar();
		c.add(Calendar.DATE, -(calendarCalculator.getSelectedIndex()));
		return c;
	}
	
	// 날짜 테이블의 0번째 인덱스의 날짜
	public Calendar getDateEndIndex(){
		Calendar c = calendarCalculator.getCalendar();
		c.add(Calendar.DATE, LENGTH_DAYLAYOUT - calendarCalculator.getSelectedIndex() -1);
		return c;
	}
	
	// 현재 선택된 인덱스의 날짜를 반환
	public Calendar getSelectedDate(){
		return calendarCalculator.getCalendar();
	}
	
	// 현재 선택된 셀의 Index 반환
	public int getSelectedIndex(){
		return calendarCalculator.getSelectedIndex();
	}
	
	// 다음달로 이동
	public void nextMonth() {
		calendarCalculator.nextMonth();
		drawCalendar();
		callChangeCalendarListener();
		
		if (aniLeft != null) {
			containerDays.startAnimation(aniLeft);
		}
	}
	
	// 이전달로 이동
	public void prevMonth() {
		calendarCalculator.prevMonth();
		drawCalendar();
		callChangeCalendarListener();
		
		if (aniRight != null) {
			containerDays.startAnimation(aniRight);
		}
	}

	// Day를 클릭했을때 반응하는 리스너 등록
	public void setOnClickDayListener(OnClickDayListener onClickDayListener) {
		this.clickDayListener = onClickDayListener;
	}
	
	// Day를 롱 클릭했을때 반응하는 리스너 등록
	public void setOnLongClickDayListener(OnLongClickDayListener onLongClickDayListener) {
		this.longClickDayListener = onLongClickDayListener;
	}
	
	// 캘린더가 바뀔때 호출하는 리스너 등록
	public void setOnChangeCalendarListener(OnChangeCalendarListener onChangeCalendarListener) {
		this.changeCalendarListener = onChangeCalendarListener;
	}

	// UI 세팅 값을 변경시키는 인터페이스
	// BG 색 설정
	// Week 폰트 크기 변경
	public void setWeekFont(float size) {
		this.fontsizeWeek = size;
		for (int i = 0; i < LENGTH_WEEKLAYOUT; i++) {
			weeks[i].setTextSize(fontsizeWeek);
		}
	}

	// Day 폰트 크기 변경
	public void setDayFont(float size) {
		this.fontsizeDay = size;
		for (int i = 0; i < LENGTH_DAYLAYOUT; i++) {
			days[i].setTextSize(fontsizeDay);
		}
	}

	// 음력 Day 폰트 크기 변경
	public void setLunarDayFont(float size) {
		this.fontsizeLunarDay = size;
		for (int i = 0; i < LENGTH_DAYLAYOUT; i++) {
			days[i].setLunarTextSize(fontsizeLunarDay);
		}
	}

	// Date 폰트 크기 변경
	public void setDateFont(float size) {
		this.fontsizeDate = size;
		TV_Date.setTextSize(fontsizeDate);
		BTN_nextMonth.setTextSize(fontsizeDate);
		BTN_prevMonth.setTextSize(fontsizeDate);
	}

	// Day Cell 색 설정
	public void setDayColorThisMonth(int color) {
		this.colorBgDefault = color;
		setSelectedDay(calendarCalculator.getSelectedIndex());
	}

	// Day Cell 색 설정(다른달)
	public void setDayColorOtherMonth(int color) {
		this.colorBgOtherMonth = color;
		setSelectedDay(calendarCalculator.getSelectedIndex());
	}

	// Font 색 설정 //
	// 기본 Day Font 색 설정
	public void setDefaultDayFontColor(int color) {
		this.colorFontDefault = color;
		for (int i = 0; i < LENGTH_DAYLAYOUT; i++) {
			days[i].setColor();
		}
	}

	// 음력 Font 색 설정
	public void setLunarDayFontColor(int color) {
		this.colorFontLunar = color;
		for (int i = 0; i < LENGTH_DAYLAYOUT; i++) {
			if (i % 7 == 0) {
				days[i].setLunarTextColor(colorFontLunar);
			}
		}
	}

	// Date Font 색 설정
	public void setDateColor(int color) {
		this.colorFontDate = color;
		TV_Date.setTextColor(colorFontDate);
		BTN_nextMonth.setTextColor(colorFontDate);
		BTN_prevMonth.setTextColor(colorFontDate);
	}

	// Today Cell 색 설정
	public void setTodayColor(int color) {
		this.colorBgToday = color;
		setSelectedDay(calendarCalculator.getSelectedIndex());
	}
	
	// 선택된 Cell 색 설정 
	public void setSelectCellColor(int color) {
		this.colorBgClick = color;
		setSelectedDay(calendarCalculator.getSelectedIndex());
	}

	// 선색 변경
	public void setLineColor(int color) {
		this.colorLine = color;
		for (int i = 0; i < LENGTH_HORI_LINE; i++) {
			horiLine[i].setBackgroundColor(colorLine);
		}
		for (int i = 0; i < LENGTH_VERT_LINE; i++) {
			vertLine[i].setBackgroundColor(colorLine);
		}
	}
	
	// 다음달로 가는 애니메이션 등록
	public void setNextMonthAni(Animation ani){
		this.aniLeft = ani;
	}
	
	// 이전달로 가는 애니메이션 등록
	public void setPrevMonthAni(Animation ani){
		this.aniRight = ani;
	}
	
	
	// /////////////////
	// private Method //
	// /////////////////

	private void init(Context context) {
		mCtx = context;
		calendarCalculator = JCalendarCalculator.getInstance();
		setOrientation(LinearLayout.VERTICAL);
		initViewSize(); // 각각의 셀들의 사이즈 초기화
		initCell();
		setListener();

		// UI
		setDateUI();
		drawCalendar();
	}

	// 각각의 View들의 크기를 초기화
	private void initViewSize() {
		widthCell = (width - (lineSize * 6)) / 7;
		heightDayCell = (height - heightWeekCell - heightDateCell - (lineSize * 8)) / 6;
	}

	// 각종 리스너 등록
	private void setListener() {

		// Date 클릭시
		dateSetListener = new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				calendarCalculator.setDate(year, monthOfYear + 1, dayOfMonth);
				drawCalendar();
				callChangeCalendarListener();
			}
		};
		TV_Date.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createDatePickerDig();
			}
		});

		// PrevMonth 버튼 리스너
		BTN_prevMonth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				prevMonth();
			}
		});

		// NextMonth 버튼 리스너
		BTN_nextMonth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nextMonth();
			}
		});
	}

	// 각각의 셀들을 추가
	private void initCell() {
		horiLine = new LinearLayout[LENGTH_HORI_LINE];
		vertLine = new LinearLayout[LENGTH_VERT_LINE];
		weeks = new CellWeek[LENGTH_WEEKLAYOUT];
		days = new CellDay[LENGTH_DAYLAYOUT];
		bottomBar = new LinearLayout[LENGTH_DAYLAYOUT];

		// Date, Week, Day들을 넣을 리니어레이아웃 생성
		containerDate = new LinearLayout(mCtx);
		containerDate.setOrientation(LinearLayout.HORIZONTAL);
		containerDate.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		containerWeeks = new LinearLayout(mCtx);
		containerWeeks.setOrientation(LinearLayout.HORIZONTAL);
		containerWeeks.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		containerDays = new LinearLayout(mCtx);
		containerDays.setOrientation(LinearLayout.VERTICAL);
		containerDays.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		// Data에 기타 위젯 추가...
		RelativeLayout rlayout = new RelativeLayout(mCtx);
		rlayout.setLayoutParams(new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, heightDateCell));
		rlayout.setPadding(25, 0, 25, 0);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		TV_Date = new TextView(mCtx);
		TV_Date.setTextSize(fontsizeDate);
		TV_Date.setTextColor(colorFontDate);
		TV_Date.setLayoutParams(params);

		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		BTN_prevMonth = new TextView(mCtx);
		BTN_prevMonth.setLayoutParams(params);
		BTN_prevMonth.setTextSize(fontsizeDate);
		BTN_prevMonth.setTextColor(colorFontDate);
		BTN_prevMonth.setText("<<<   ");

		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		BTN_nextMonth = new TextView(mCtx);
		BTN_nextMonth.setLayoutParams(params);
		BTN_nextMonth.setTextSize(fontsizeDate);
		BTN_nextMonth.setTextColor(colorFontDate);
		BTN_nextMonth.setText("   >>>");

		rlayout.addView(TV_Date);
		rlayout.addView(BTN_prevMonth);
		rlayout.addView(BTN_nextMonth);
		containerDate.addView(rlayout);

		addView(containerDate);

		// Week에 View들 추가
		addView(vertLine[0] = getVertLine());
		addView(containerWeeks);
		for (int i = 0; i < LENGTH_WEEKLAYOUT; i++) {
			weeks[i] = new CellWeek(mCtx, i);
			containerWeeks.addView(weeks[i]);
			if (i != LENGTH_WEEKLAYOUT - 1) {
				horiLine[i] = getHoriLine();
				containerWeeks.addView(horiLine[i]);
			}
		}
		addView(vertLine[1] = getVertLine());

		// Day에 View들 추가
		addView(containerDays);
		LinearLayout lineLayout = null;
		for (int i = 0; i < LENGTH_DAYLAYOUT; i++) {
			if (i % 7 == 0) {
				lineLayout = new LinearLayout(mCtx);
				lineLayout.setOrientation(LinearLayout.HORIZONTAL);
				lineLayout.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				containerDays.addView(lineLayout);
				containerDays.addView(vertLine[(i / 7) + 2] = getVertLine());
			}

			lineLayout.addView(days[i] = new CellDay(mCtx, i));
			if (i == 0 || (i + 1) % 7 != 0) {
				lineLayout.addView(horiLine[i + 6 - (i / 7)] = getHoriLine());
			}
		}

	}

	// 세로 라인 만들기 (초기화시 사용)
	private LinearLayout getHoriLine() {
		LinearLayout line = new LinearLayout(mCtx);
		line.setLayoutParams(new LayoutParams(lineSize,
				LayoutParams.FILL_PARENT));
		line.setBackgroundColor(colorLine);
		return line;
	}

	// 가로 라인 만들기 (초기화시 사용)
	private LinearLayout getVertLine() {
		LinearLayout line = new LinearLayout(mCtx);
		line.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				lineSize));
		line.setBackgroundColor(colorLine);
		return line;
	}
	
	// onChangeCalendarListener호출하는 메소드
	private void callChangeCalendarListener(){
		Calendar c = calendarCalculator.getCalendar();
		if (changeCalendarListener != null) {
			changeCalendarListener.onChangeCalendar(calendarCalculator
					.getSelectedIndex(), c.get(Calendar.YEAR), (c
					.get(Calendar.MONTH) + 1), c.get(Calendar.DATE));
		}

		if (clickDayListener != null) {
			clickDayListener.onClickDay(calendarCalculator.getSelectedIndex(), c
					.get(Calendar.YEAR), (c.get(Calendar.MONTH) + 1), c
					.get(Calendar.DATE));
		}
	}
	
	// 오늘 UI설정
	private void setTodayUI() {
		int todayIndex = calendarCalculator.getTodayIndex();
		if (todayIndex > 0 && calendarCalculator.getSelectedIndex() != todayIndex) {
			days[todayIndex].setBackgroundColor(colorBgToday);
		}
	}

	// Day를 선택했을때 UI 및 선택된 YY/MM/DD 설정
	private void setSelectedDay(int select) {
		int start = calendarCalculator.getStartIndex();
		int end = calendarCalculator.getEndIndex();

		for (int i = 0; i < LENGTH_DAYLAYOUT; i++) {
			if (i == select) {
				days[i].setBackgroundColor(colorBgClick);
			} else if (i < start || i > end) {
				days[i].setBackgroundColor(colorBgOtherMonth);
			} else {
				days[i].setBackgroundColor(colorBgDefault);
			}
		}

		setTodayUI();
	}

	// Date TextView에 날짜 설정
	private void setDateUI() {
		Calendar c = calendarCalculator.getCalendarThisMonth();
		TV_Date.setText(c.get(Calendar.YEAR) + ". "
				+ (c.get(Calendar.MONTH) + 1));
	}
	
	// Date 클릭시 나타나는 다이얼로그 생성
	private void createDatePickerDig() {
		Calendar c = calendarCalculator.getCalendar();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int date = c.get(Calendar.DATE);

		if (year < 1900 ){
			year = 1900;
		}
		
		DatePickerDialog dateDialog = new DatePickerDialog(mCtx,
				dateSetListener, year, month, date);
		dateDialog.show();
	}

	// Week의 각 항목
	private class CellWeek extends LinearLayout {
		private TextView textview;
		private int week;

		public CellWeek(Context context, int week) {
			super(context);

			setLayoutParams(new LinearLayout.LayoutParams(widthCell,
					heightWeekCell));
			setGravity(Gravity.CENTER);

			this.week = week;

			textview = new TextView(context);
			textview.setText(weekText[week]);
			setColor();
			addView(textview);
		}

		public void setTextColor(int color) {
			textview.setTextColor(color);
		}
		
		public void setTextSize(float size){
			textview.setTextSize(size);
		}

		public void setColor() {
			switch (week) {
			case 0: // Sun
				setTextColor(colorFontSun);
				setBackgroundColor(colorBgWeek);
				break;
			case 1: // Mon
			case 2: // Tue
			case 3: // Wed
			case 4: // Thu
			case 5: // Fri
				setTextColor(colorFontDefault);
				setBackgroundColor(colorBgWeek);
				break;
			case 6: // Sat
				setTextColor(colorFontSat);
				setBackgroundColor(colorBgWeek);
				break;
			default:
				break;
			}
		}
	}

	// Day의 각 항목
	private class CellDay extends LinearLayout implements OnClickListener, OnLongClickListener {
		private TextView textview;
		private TextView textviewlunar = null;
		private LinearLayout layout;
		private int week;
		private int index;

		public CellDay(Context context, int index) {
			super(context);
			this.index = index;
			this.week = index % 7;

			setLayoutParams(new LinearLayout.LayoutParams(widthCell,
					heightDayCell));
			setOrientation(LinearLayout.VERTICAL);
			setGravity(Gravity.CENTER);

			/*
			 * Cell의 반을 고정으로 지정하여 크기 설정
			 */
//			// TextView 2개( 양력, 음력)을 담을 레이아웃
//			LinearLayout llayout = new LinearLayout(mCtx);
//			llayout.setOrientation(LinearLayout.HORIZONTAL);
//			llayout.setLayoutParams(new LayoutParams(widthCell,
//					heightDayCell / 2));
//
//			textview = new TextView(context);
//			textview.setGravity(Gravity.CENTER_VERTICAL);
//			textview.setPadding(5, 0, 0, 0);
//			textview.setLayoutParams(new LayoutParams(widthCell / 2,
//					heightDayCell / 2));
//			setColor();
//			llayout.addView(textview);
//
//			if (week == 0) {
//				textviewlunar = new TextView(context);
//				textviewlunar.setGravity(Gravity.CENTER);
//				textviewlunar.setTextSize(DEFAULT_FONTSIZE_LUNAR);
//				textviewlunar.setTextColor(DEFAULT_COLOR_LUNAR);
//				textviewlunar.setLayoutParams(new LayoutParams(widthCell / 2,
//						heightDayCell / 2));
//				llayout.addView(textviewlunar);
//			}
			
			/*
			 * Cell의 Date텍스트의 높이에 맞춰서 동적으로 크기설정
			 */
			// TextView 2개( 양력, 음력)을 담을 레이아웃
			LinearLayout llayout = new LinearLayout(mCtx);
			llayout.setOrientation(LinearLayout.HORIZONTAL);
			llayout.setLayoutParams(new LayoutParams(widthCell,
					LayoutParams.WRAP_CONTENT));

			textview = new TextView(context);
			textview.setGravity(Gravity.CENTER_VERTICAL);
			textview.setPadding(5, 0, 0, 0);
			textview.setLayoutParams(new LayoutParams(widthCell / 2,
					LayoutParams.WRAP_CONTENT));
			setColor();
			llayout.addView(textview);

			if (week == 0) {
				textviewlunar = new TextView(context);
				textviewlunar.setGravity(Gravity.CENTER);
				textviewlunar.setTextSize(DEFAULT_FONTSIZE_LUNAR);
				textviewlunar.setTextColor(DEFAULT_COLOR_LUNAR);
				textviewlunar.setLayoutParams(new LayoutParams(widthCell / 2,
						LayoutParams.WRAP_CONTENT));
				llayout.addView(textviewlunar);
			}
			
			addView(llayout);

			layout = new LinearLayout(mCtx);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setLayoutParams(new LayoutParams(widthCell,
					LayoutParams.FILL_PARENT));
			addView(layout);
			bottomBar[index] = layout;

			setOnClickListener(this);
			setOnLongClickListener(this);
		}

		// 양력 텍스트 컬러 설정
		public void setTextColor(int color) {
			textview.setTextColor(color);
		}

		// 양력 텍스트 설정
		public void setText(String text) {
			textview.setText(text);
		}
		
		//양력 텍스트 사이즈 설정
		public void setTextSize(float size){
			textview.setTextSize(size);
		}

		// 음력 텍스트 설정
		public void setLunarText(String text) {
			if (textviewlunar != null) {
				textviewlunar.setText(text);
			}
		}
		
		//음력 텍스트 사이즈 설정
		public void setLunarTextSize(float size){
			if (textviewlunar != null) {
				textviewlunar.setTextSize(size);
			}
		}
		
		//음력 텍스트 색 설정
		public void setLunarTextColor(int color){
			if (textviewlunar != null) {
				textviewlunar.setTextColor(color);
			}
		}

		// 양력 텍스트 색 자동 설정
		public void setColor() {
			switch (week) {
			case 0: // Sun
				setTextColor(colorFontSun);
				setBackgroundColor(colorBgSun);
				break;
			case 1: // Mon
			case 2: // Tue
			case 3: // Wed
			case 4: // Thu
			case 5: // Fri
				setTextColor(colorFontDefault);
				setBackgroundColor(colorBgDefault);
				break;
			case 6: // Sat
				setTextColor(colorFontSat);
				setBackgroundColor(colorBgSat);
				break;
			default:
				break;
			}
		}

		@Override
		public void onClick(View v) {
			calendarCalculator.setSelectIndex(index);
			setSelectedDay(index);
			if (clickDayListener != null) {
				Calendar c = calendarCalculator.getCalendar();
				clickDayListener.onClickDay(index, 
						c.get(Calendar.YEAR), 
						c.get(Calendar.MONTH) + 1, 
						c.get(Calendar.DATE));
			}
		}

		@Override
		public boolean onLongClick(View v) {
			if (longClickDayListener != null) {
				Calendar c = calendarCalculator.getCalendar(index);
				longClickDayListener.onLongClickDay(index, 
						c.get(Calendar.YEAR), 
						c.get(Calendar.MONTH) + 1, 
						c.get(Calendar.DATE));
			}
			return true;
		}
		
	}
}
