package com.minsub.calendar.test;

import java.util.Calendar;

import junit.framework.TestCase;

import com.minsub.jcalendar.calculator.JCalendarCalculator;

public class JCalendarDataTest extends TestCase {

	JCalendarCalculator jc;
	public JCalendarDataTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		jc = JCalendarCalculator.getInstance();
		jc.setDate(2012, 2, 10);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
    public void testPreConditions() {
    	assertNotNull(jc);
    }
    
    
    // 단위 테스

    // 인스턴스 생성 테스트
	public void testGetInstance() {
		JCalendarCalculator jc = JCalendarCalculator.getInstance();
		assertEquals(jc, this.jc);
	}

	// 날짜 세팅 확인
	public void testSetDate() {
		jc.setDate(2012, 3, 25);
		Calendar c = jc.getCalendar();
		assertEquals(c.get(Calendar.YEAR), 2012);
		assertEquals(c.get(Calendar.MONTH)+1, 3);
		assertEquals(c.get(Calendar.DATE), 25);
	}

	// 세팅된 날짜 확
	public void testGetCalendar() {
		Calendar c =jc.getCalendar();
		assertNotNull(c);
		assertEquals(c.get(Calendar.YEAR), 2012);
		assertEquals(c.get(Calendar.MONTH)+1, 2);
		assertEquals(c.get(Calendar.DATE), 10);
	}
	
	// 셀을 선택 하고 확인.
	public void testSetSelectIndex() {
		assertTrue(jc.getSelectedIndex() != 0);
		jc.setSelectIndex(10);
		assertTrue(jc.getSelectedIndex() == 10);
		
	}

	// 선택된 셀 확인.
	public void testGetSelectedIndex() {
		jc.setSelectIndex(10);
		assertTrue(jc.getSelectedIndex() == 10);
	}

	// 이번 캘린더 확인.
	public void testGetCalendarThisMonth() {
		Calendar c =jc.getCalendarThisMonth();
		assertNotNull(c);
		assertEquals(c.get(Calendar.YEAR), 2012);
		assertEquals(c.get(Calendar.MONTH)+1, 2);
		assertEquals(c.get(Calendar.DATE), 1);
	}

	// 음력 날짜 화인
	public void testGetLunar() {
		int[] lunar =jc.getLunar();
		assertNotNull(lunar);
		assertEquals(lunar[0], 2012);
		assertEquals(lunar[1], 1);
		assertEquals(lunar[2], 19);
	}

	// 오늘의 인덱스 확인.
	public void testGetTodayIndex() {
		int todayIndex = jc.getTodayIndex();
		assertTrue(todayIndex == -1);
		jc.setDate(2012, 1, 20);
		todayIndex = jc.getTodayIndex();
		assertTrue(todayIndex == 14);
		
	}

	// 7 * 6 테이블 확인, 몇개의 인덱스에 데이터가 제대로 들어갔는지 확인
	public void testGetTable() {
		int[] table = jc.getTable();
		assertNotNull(table);
		assertTrue(table[3] == 1);
		assertTrue(table[10] == 8);
		assertTrue(table[32] == 1);
		assertTrue(table[41] == 10);
	}

	// 이번달이 시작되는 인덱스를 확인, 다음달로 이동시키고 다시한번 확
	public void testGetStartIndex() {
		assertTrue(jc.getStartIndex() == 3);
		jc.nextMonth();
		assertTrue(jc.getStartIndex() == 4);
	}
	// 이번달이 끝나는 인덱스를 확인, 다음달로 이동시키고 다시한번 확인
	public void testGetEndIndex() {
		assertTrue(jc.getEndIndex() == 31);
		jc.nextMonth();
		assertTrue(jc.getEndIndex() == 34);
	}

	// 테이블의 일요일의 음려 데이터 확인, 이전달로 이동하고 한번 더 확
	public void testGetLunarTable() {
		String[] table = jc.getLunarTable();
		assertNotNull(table);
		assertTrue(table[0].equalsIgnoreCase("01.07"));
		assertTrue(table[1].equalsIgnoreCase("01.14"));
		assertTrue(table[2].equalsIgnoreCase("01.21"));
		assertTrue(table[5].equalsIgnoreCase("02.12"));
		jc.prevMonth();
		assertTrue(table[0].equalsIgnoreCase("12.08"));
		assertTrue(table[1].equalsIgnoreCase("12.15"));
		assertTrue(table[2].equalsIgnoreCase("12.22"));
		assertTrue(table[5].equalsIgnoreCase("01.14"));
	}
	
	// 다음년으 이동후 날짜 확인
	public void testNextYear() {
		jc.nextYear();
		Calendar c = jc.getCalendarThisMonth();
		assertEquals(c.get(Calendar.YEAR), 2013);
		assertEquals(c.get(Calendar.MONTH)+1, 2);
	}

	// 이전년으 이동후 날짜 확인
	public void testPrevYear() {
		jc.prevYear();
		Calendar c = jc.getCalendarThisMonth();
		assertEquals(c.get(Calendar.YEAR), 2011);
		assertEquals(c.get(Calendar.MONTH)+1, 2);
	}

	// 다음달로 이동후 날짜 확인
	public void testNextMonth() {
		jc.nextMonth();
		Calendar c = jc.getCalendarThisMonth();
		assertEquals(c.get(Calendar.YEAR), 2012);
		assertEquals(c.get(Calendar.MONTH)+1, 3);
	}

	// 이전달로 이동후 날짜 확인
	public void testPrevMonth() {
		jc.prevMonth();
		Calendar c = jc.getCalendarThisMonth();
		assertEquals(c.get(Calendar.YEAR), 2012);
		assertEquals(c.get(Calendar.MONTH)+1, 1);
	}
}
