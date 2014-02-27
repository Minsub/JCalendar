package com.minsub.calendar.test;

import java.io.File;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.test.ActivityUnitTestCase;

import com.minsub.calendar.Main;
import com.minsub.calendar.planer.PlanerManager;
import com.minsub.calendar.planer.PlanerManager.PlanNode;
import com.minsub.jcalendar.calculator.JCalendarCalculator;

public class PlanerDataTest extends ActivityUnitTestCase<Main> {	
	long start;
	long end;
	long nextStart;
	long nextEnd;
	
	private Intent intent;
	private Context context;
	private PlanerManager pm;
	private Cursor cursor_selectPlan_thisMonth;
	private Cursor cursor_selectPlan_nextMonth;
	

	private JCalendarCalculator jc;
	
	public PlanerDataTest() {
		super(Main.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getInstrumentation().getTargetContext();
		intent = new Intent(getInstrumentation().getTargetContext(), Main.class);
		pm = PlanerManager.getInstance(context);
		jc = JCalendarCalculator.getInstance();
		
		Calendar c = jc.getCalendar();
		c.add(Calendar.DATE, -(jc.getSelectedIndex()));
		start = c.getTimeInMillis();
		c.add(Calendar.DATE, 42);
		nextStart = c.getTimeInMillis();
		
		Calendar c2 = jc.getCalendar();
		c2.add(Calendar.DATE, 42 - jc.getSelectedIndex() -1);
		end = c2.getTimeInMillis();
		c2.add(Calendar.DATE, 42);
		nextEnd = c2.getTimeInMillis();
		
		cursor_selectPlan_thisMonth = pm.selectPlan(start, end);
		cursor_selectPlan_nextMonth = pm.selectPlan(nextStart, nextEnd);
	}

	public void testPreConditions() {
		assertNotNull(intent);
		assertNotNull(context);
		assertNotNull(pm);
		assertNotNull(jc);
		assertNotNull(cursor_selectPlan_thisMonth);
		assertNotNull(cursor_selectPlan_nextMonth);
	} // end of testPreConditions() method definition

	// ���� �׽�Ʈ

	public final void testGetInstanceContext() {
		// ���� ��ü�� ���������� �̹� ������� ��ü�� �������� ��
		assertEquals(pm, PlanerManager.getInstance(context));
	}
	
	public final void testGetPlanNodeTable() {
		pm.setPlanNodeTable(start, end);
		PlanNode[] nodes = pm.getPlanNodeTable();
		assertNotNull(nodes);
		PlanNode item;
		int cnt = 0;
		for (int i = 0; i < 42; i++) {
			item = nodes[i];
			
			while (item != null) {
				if (item.getNext() != null) {
					// ��尡 �ð������� ����������� �迭�Ǿ� �ִ��� Ȯ��
					assertTrue(item.getTodayTime() <= item.getNext().getTodayTime());
				}
				cnt++;
				item = item.getNext();
			}
		}

		//��񿡼� ���������� �������� ������ PlanNode�� ������ ������ ��(�ݺ� X, �Ϸ�Ѿ�� ������ X)
//		assertEquals(cnt, cursor_selectPlan_thisMonth.getCount()+pm.selectRepeatPlan().getCount()); 
		
	}

	public final void testGetPlanNodeTableLongLong() {
		PlanNode[] nodes = pm.getPlanNodeTable(nextStart, nextEnd);
		assertNotNull(nodes);
		PlanNode item;
		int cnt = 0;
		for (int i = 0; i < 42; i++) {
			item = nodes[i];
			
			while (item != null) {
				if (item.getNext() != null) {
					// ��尡 �ð������� ����������� �迭�Ǿ� �ִ��� Ȯ��
					assertTrue(item.getTodayTime() <= item.getNext().getTodayTime());
				}
				cnt++;
				item = item.getNext();
			}
		}

		//��񿡼� ���������� �������� ������ PlanNode�� ������ ������ ��(�ݺ� X, �Ϸ�Ѿ�� ������ X)
//		assertEquals(cnt, cursor_selectPlan_nextMonth.getCount()+pm.selectRepeatPlan().getCount());
	}
	
	public final void testSetPlanNodeTableLongLong() {
		pm.setPlanNodeTable(start, end);
		PlanNode[] nodes = pm.getPlanNodeTable();
		assertNotNull(nodes);
		PlanNode item;
		int cnt = 0;
		for (int i = 0; i < 42; i++) {
			item = nodes[i];
			
			while (item != null) {
				if (item.getNext() != null) {
					// ��尡 �ð������� ����������� �迭�Ǿ� �ִ��� Ȯ��
					assertTrue(item.getTodayTime() <= item.getNext().getTodayTime());
				}
				cnt++;
				item = item.getNext();
			}
		}

		//��񿡼� ���������� �������� ������ PlanNode�� ������ ������ ��(�ݺ� X, �Ϸ�Ѿ�� ������ X)
//		assertEquals(cnt, cursor_selectPlan_thisMonth.getCount()+pm.selectRepeatPlan().getCount()); 
	}
	
	public final void testBackupXML(){
		assertTrue(pm.backupXML());
		File file = new File(PlanerManager.BACKUP_FILE_PATH,PlanerManager.BACKUP_FILE_NAME);
		assertTrue(file.exists());
	}
	
	public final void testRestoreXML(){
		File file = new File(PlanerManager.BACKUP_FILE_PATH,PlanerManager.BACKUP_FILE_NAME);
		assertTrue(file.exists());
		assertTrue(pm.restoreXML());
		assertTrue(pm.selectAllPlan().getCount() > 0);
	}

	// DB ������ ��κ� ����
	
	public final void testInsertPlan() {
		// ���ڵ� 100�� ����
		String subject = "TEST3";
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, 5);
		c.set(Calendar.DATE, 1);
		
//		for(int i = 0 ; i < 100; i++) {
//			pm.insertPlan(subject, c.getTimeInMillis(), c.getTimeInMillis(), "","", 0, 0, 0);
//			c.add(Calendar.DATE, 1);
//		}
		
	}

	public final void testSelectAllPlan() {
		// ��� ���ڵ� �˻�
		Cursor cursor = pm.selectAllPlan();
		if (cursor != null) {
			cursor.moveToFirst();
			do {
				assertTrue(cursor.getLong(0) > -1);
			} while (cursor.moveToNext());
		}
	}
	
	public final void testSelectRepeatPlanForAlarm() {
		Cursor cursor = pm.selectRepeatPlanForAlarm();
		if (cursor != null) {
			cursor.moveToFirst();
			do {
				assertTrue(cursor.getInt(7) > 0);
				assertTrue(cursor.getInt(6) > 0 || cursor.getInt(8) > 0);
			} while (cursor.moveToNext());
		}
	}


}
