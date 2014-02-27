package com.minsub.jcalendar.calculator;

/*
 * Class : JCalendarData
 * 작성자 : 지민섭 
 * 2011. 01. 05
 * 설명 : JCalendarCalculator를 이용해 사용됨
 * 		 UI를 설정하기 위한 Calendar의 계산을 수행하는 클래스
 *       캘린더를 위한 UI에서는 날짜에 관련한 데이터를 저장할 필요 없이 해당 객체가 모두 관리
 * 		 각 셀에 어떤 날짜가 들어가는지를 확인 저장, 현재 선택된 날짜를 포함.
 * 		 음력 계산 포함 (LunarCalendar:오픈소스 사용)
 * 
 * 구현내용 : 7 * 6 개의 칸을 기준으로 달력을 설정을 기준 
 * 	        Calendar객체의 연산을 이용하여 양력을 계산
 *          테이블에서 선택된 셀의 인덱스를 저장하여 날짜를 계산함
 */

import java.util.Calendar;

import com.minsub.utils.JStringUtils;

class JCalendarData extends JCalendarCalculator{
	public static JCalendarData instance = new JCalendarData();
	private final int LENGTH_TABLE = 7 * 6;
	private final int LENGTH_LUNAR_TABLE = 6;
	
	private LunarCalendar lunar;
	private Calendar calendar;
	private int[] tableDayText;					// 7 * 6 의 테이블에 각각의 날짜가 삽입
	private String[] tableLunarDayText;			// 6개의 테이블( 7 * 6 테이블의 일요일만)에 각각의 음력 날짜 삽입 
	private int startIndex;						// 이 달이 시작되는 7 * 6 테이블의 인덱스 
	private int endIndex;						// 이 달이 끝나는 7 * 6 테이블의 인덱스
	private int selectedIndex;					// 7 * 6 테이블에서 선택된 인덱스
	
	// 싱글톤 생성자
	public static JCalendarData getInstance(){
		if (instance == null){
			instance = new JCalendarData();
		}
		return instance;
	}
	
	// 생성자
	private JCalendarData() {
		calendar = Calendar.getInstance();	
		initCalendar(calendar);
		lunar = new LunarCalendar();
		tableDayText = new int[LENGTH_TABLE];
		tableLunarDayText = new String[LENGTH_LUNAR_TABLE];
		setTableText(calendar);
		setLunar(calendar);
		
		selectedIndex = startIndex + calendar.get(Calendar.DATE) - 1;
		calendar.set(Calendar.DATE, 1);
	};
	
	// yyyy,mm,dd 형식으로 날짜를 저장
	@Override
	public int[] setDate(int year, int month, int day) {
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DATE, 1);
		setTableText(calendar);
		setLunar(calendar);
		selectedIndex = startIndex + day - 1;
		return tableDayText;
	}
	
	// 7 * 6 테이블에서 선택된 인덱스로 현재 날짜를 설정
	@Override
	public void setSelectIndex(int index) {
		this.selectedIndex = index;
	}
	
	
	
	// 현재 선택된 인덱스 반환
	@Override
	public int getSelectedIndex(){
		return selectedIndex;
	}
	
	// 현재의 날짜 반환
	@Override
	public Calendar getCalendar(){
		Calendar c = (Calendar)calendar.clone();
		
		if (selectedIndex < startIndex) {
			c.add(Calendar.MONTH, -1);
		} else if (selectedIndex > endIndex) {
			c.add(Calendar.MONTH, 1);
		}
		c.set(Calendar.DATE, tableDayText[selectedIndex]);
		return c;
	}
	
	// 현재의 음력 날짜 반환( LUNAR_YEAR, LUNAR_MONTH, LUNAR_DATE)
	@Override
	public Calendar getCalendar(int selectedIndex){
		Calendar c = (Calendar)calendar.clone();
		
		if (selectedIndex < startIndex) {
			c.add(Calendar.MONTH, -1);
		} else if (selectedIndex > endIndex) {
			c.add(Calendar.MONTH, 1);
		}
		c.set(Calendar.DATE, tableDayText[selectedIndex]);
		return c;
	}
	
	// 현재의 달의 1일기준의 Calendar객체 반환
	@Override
	public Calendar getCalendarThisMonth(){
		Calendar c = (Calendar)calendar.clone();
		return c;
	}
	
	// 현재 날짜의 음력을 배열로 반환 (0-year, 1-month, 2-date)
	@Override
	public int[] getLunar() {
		try{
			lunar.setSolarCalendar(getCalendar());
			int[] result = { lunar.get(LunarCalendar.LUNAR_YEAR),
					lunar.get(LunarCalendar.LUNAR_MONTH),
					lunar.get(LunarCalendar.LUNAR_DATE) };
			return result;
		}catch (Exception e) {
			return null;
		}
	}

	// 오늘이 현재 7 * 6에 있을떄 인덱스를 반환하고 없을 시 -1를 반환
	@Override
	public int getTodayIndex() {
		int currentYear = calendar.get(Calendar.YEAR);
		int currentMonth = calendar.get(Calendar.MONTH);

		Calendar todayC = Calendar.getInstance();
		int todayYear = todayC.get(Calendar.YEAR);
		int todayMonth = todayC.get(Calendar.MONTH);

		if (currentYear == todayYear && currentMonth == todayMonth) {
			return startIndex + todayC.get(Calendar.DATE) - 1;
		} else {
			return -1;
		}
	}

	// 내년
	@Override
	public int[] nextYear() {
		calendar.add(Calendar.YEAR, 1);
		setTableText(calendar);
		return tableDayText;
	}

	// 이전 년
	@Override
	public int[] prevYear() {
		calendar.add(Calendar.YEAR, -1);
		setTableText(calendar);
		return tableDayText;
	}

	// 다음달
	@Override
	public int[] nextMonth() {
		calendar.add(Calendar.MONTH, 1);
		setTableText(calendar);
		return tableDayText;
	}

	// 이전달
	@Override
	public int[] prevMonth() {
		calendar.add(Calendar.MONTH, -1);
		setTableText(calendar);
		return tableDayText;
	}

	// 현재 날짜
	@Override
	public int[] getTable() {
		return tableDayText;
	}

	// 이번달이 시작하는 인덱스
	@Override
	public int getStartIndex() {
		return startIndex;
	}

	// 이번달이 끝나는 인덱스
	@Override
	public int getEndIndex() {
		return endIndex;
	}

	// 현재 음력
	@Override
	public String[] getLunarTable() {
		return tableLunarDayText;
	}

	
	// /////////////////
	// PRIVATE METHOD //
	// /////////////////
	
	// 입력받은 Calendar 객체에 시간,분,초 를 0으로 초기화
	private void initCalendar(Calendar c){
		c.set(Calendar.HOUR_OF_DAY,0);
		c.set(Calendar.MINUTE,0);
		c.set(Calendar.SECOND,0);
		c.set(Calendar.MILLISECOND,0);
	}
	
	// 음력 날짜 설정
	private void setLunar(Calendar calendar) {
		try{
			lunar.setSolarCalendar(calendar);
		}catch (Exception e) {
		}
	}

	// 입력받은 Calendar를 세팅
	private void setTableText(Calendar calendar) {
		// 음력 설정
		setLunar(calendar);

		// 양력 테이블 계산
		Calendar c = (Calendar) calendar.clone();
		c.set(Calendar.DATE, 1);
		startIndex = c.get(Calendar.DAY_OF_WEEK) - 1;
		endIndex = c.getActualMaximum(Calendar.DATE) + startIndex - 1;

		// 이번달 채우기
		int index = 1;
		for (int i = startIndex; i <= endIndex; i++) {
			tableDayText[i] = index++;
		}

		// 다음달 채우기
		index = 1;
		for (int i = endIndex + 1; i < LENGTH_TABLE; i++) {
			tableDayText[i] = index++;
		}

		// 이전달 채우기
		c.add(Calendar.DATE, -1);
		index = c.get(Calendar.DATE);
		for (int i = startIndex - 1; i >= 0; i--) {
			tableDayText[i] = index--;
		}

		// 음력 테이블 계산
		try{
			LunarCalendar lunarCal = (LunarCalendar) lunar.clone();
			c = (Calendar)calendar.clone();
			
			if(startIndex == 0){
				c.set(Calendar.DATE, 1);
			}else{
				c.add(Calendar.MONTH, -1);
				c.set(Calendar.DATE, tableDayText[0]);
			}
			lunarCal.setSolarCalendar(c);
			for(int i = 0; i < LENGTH_LUNAR_TABLE; i++){
				tableLunarDayText[i] = JStringUtils.retainNumber10(lunarCal.get(LunarCalendar.LUNAR_MONTH))+"."+
								JStringUtils.retainNumber10(lunarCal.get(LunarCalendar.LUNAR_DATE));
				c.add(Calendar.DATE, 7);
				lunarCal.setSolarCalendar(c);
			}
		}catch (Exception e) {
			for(int i = 0; i < LENGTH_LUNAR_TABLE; i++){
				tableLunarDayText[i] = "";
			}
		}
	}
}
