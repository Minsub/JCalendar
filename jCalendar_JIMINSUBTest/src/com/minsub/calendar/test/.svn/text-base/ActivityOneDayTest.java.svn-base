package com.minsub.calendar.test;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import com.minsub.calendar.ActivityOneDay;
import com.minsub.calendar.ActivityPlan;

public class ActivityOneDayTest extends ActivityInstrumentationTestCase2<ActivityOneDay> {	
	private Intent intent;
	private Context context;
	private ActivityOneDay activity;
	
	private Calendar c;
		
	public ActivityOneDayTest(String pkg, Class<ActivityOneDay> activityClass) {
		super(pkg, activityClass);
		
	}

	public ActivityOneDayTest() {
		super("com.minsub.calendar",ActivityOneDay.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getInstrumentation().getTargetContext();
		intent = new Intent(getInstrumentation().getTargetContext(), ActivityPlan.class);	
		activity = getActivity();
		
		
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
		assertNotNull(c);
	} // end of testPreConditions() method definition

	
	
	// 단위 테스트
	
	@UiThreadTest
	public final void test() {		
		// ADD 모드로 액티비티실행
	}
	



}
