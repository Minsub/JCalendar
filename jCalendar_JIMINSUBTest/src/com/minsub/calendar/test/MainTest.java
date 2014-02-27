package com.minsub.calendar.test;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.Button;
import android.widget.LinearLayout;

import com.minsub.calendar.ActivityPlan;
import com.minsub.calendar.Main;
import com.minsub.calendar.planer.PlanerManager;
import com.minsub.jcalendar.JCalendar;

public class MainTest extends ActivityInstrumentationTestCase2<Main> {	
	private Intent intent;
	private Context context;
	private Main activity;
	
	private Button BTN_add;
	private Button BTN_search;
	private LinearLayout LL_cover;
	private LinearLayout LL_bottom;
	private JCalendar jcalendar;
	private PlanerManager pm;
	private Calendar c;
	
	public MainTest(String pkg, Class<Main> activityClass) {
		super(pkg, activityClass);
		
	}

	public MainTest() {
		super("com.minsub.calendar",Main.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getInstrumentation().getTargetContext();
		intent = new Intent(getInstrumentation().getTargetContext(), ActivityPlan.class);	
		activity = getActivity();
		
		BTN_add = (Button)activity.findViewById(com.minsub.calendar.R.id.btn_add);
		BTN_search = (Button)activity.findViewById(com.minsub.calendar.R.id.btn_search);
		LL_cover = (LinearLayout)activity.findViewById(com.minsub.calendar.R.id.cover_calendar);
		LL_bottom = (LinearLayout)activity.findViewById(com.minsub.calendar.R.id.cover_plans);
		jcalendar = (JCalendar)LL_cover.getChildAt(0);
		pm = PlanerManager.getInstance(context);
		c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
	}

	
	public void testPreConditions() {
		assertNotNull(intent);
		assertNotNull(context);
		assertNotNull(activity);
		assertNotNull(BTN_add);
		assertNotNull(BTN_search);
		assertNotNull(jcalendar);
		assertNotNull(LL_bottom);
		assertNotNull(pm);
		assertNotNull(c);
	} // end of testPreConditions() method definition

	
	
	// ���� �׽�Ʈ
	@UiThreadTest
	public final void testCalendar_SelectedDate() {
		// �信 �ִ� Ķ������ ���� �׽�Ʈ
		Calendar cc = jcalendar.getSelectedDate();		
		assertEquals(cc.get(Calendar.DATE), 16);	// ���� ���õȼ��� ��¥ Ȯ��
		
		jcalendar.nextMonth();
		cc = jcalendar.getSelectedDate();	
		assertEquals(cc.get(Calendar.DATE), 13);	// ������ ���õȼ��� ��¥ Ȯ��
		
	}
	
	@UiThreadTest
	public final void testCalendar_Move() {
		// �信 �ִ� Ķ������ ���� �׽�Ʈ
		jcalendar.nextMonth();
		jcalendar.nextMonth();
		jcalendar.prevMonth();
		jcalendar.prevMonth();
	}
	
	@UiThreadTest
	public final void testBottomPlans() {
		long start = c.getTimeInMillis();
		c.add(Calendar.DATE, 1);
		long end = c.getTimeInMillis();
		Cursor cursor =  pm.selectPlan(start, end);
		if( cursor != null){
			assertEquals(cursor.getCount(), LL_bottom.getChildCount());
		}
		
	}
	
	@UiThreadTest
	public final void testAddButton() {
//		BTN_add.performClick();
	}
	
	@UiThreadTest
	public final void testSearchButton() {
//		BTN_search.performClick();
	}
}
