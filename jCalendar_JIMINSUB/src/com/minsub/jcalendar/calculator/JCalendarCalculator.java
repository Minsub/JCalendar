package com.minsub.jcalendar.calculator;

/*
 * Class : JCalendarData
 * 작성자 : 지민섭 
 * 2011. 01. 13
 * 설명 : JCalendarCalculator를 이용하기 위한 추상화 클래스
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

public abstract class JCalendarCalculator {
	
	// 싱글톤으로 JCalendarData 객체 리턴
	public static JCalendarCalculator getInstance(){
		return JCalendarData.getInstance();
	}
	
	public abstract int[] setDate(int year, int month, int day);
	
	// 7 * 6 테이블에서 선택된 인덱스로 현재 날짜를 설정
	public abstract void setSelectIndex(int index);
	
	// 현재 선택된 인덱스 반환
	public abstract int getSelectedIndex();
	
	// 현재의 음력 날짜 반환( LUNAR_YEAR, LUNAR_MONTH, LUNAR_DATE)
	public abstract Calendar getCalendar();
	
	// 파라미터로 넘어온 선택된 인덱스의 날짜를 반환
	public abstract Calendar getCalendar(int selectedIndex);
	
	// 현재의 음력 날짜 반환( LUNAR_YEAR, LUNAR_MONTH, LUNAR_DATE)
	public abstract Calendar getCalendarThisMonth();
	
	// 현재 날짜의 음력을 배열로 반환 (0-year, 1-month, 2-date)
	public abstract int[] getLunar();
	
	// 오늘이 현재 7 * 6에 있을떄 인덱스를 반환하고 없을 시 -1를 반환
	public abstract int getTodayIndex();

	// 내년
	public abstract int[] nextYear();

	// 이전 년
	public abstract int[] prevYear();

	// 다음달
	public abstract int[] nextMonth();
	
	// 이전달
	public abstract int[] prevMonth();

	// 현재 날짜
	public abstract int[] getTable();

	// 이번달이 시작하는 인덱스
	public abstract int getStartIndex();

	// 이번달이 끝나는 인덱스
	public abstract int getEndIndex();
	
	// 현재 음력 
	public abstract String[] getLunarTable();
}
