package com.minsub.calendar.test;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.Button;
import android.widget.EditText;

import com.minsub.calendar.ActivityPlan;

public class ActivityPlanTest extends ActivityInstrumentationTestCase2<ActivityPlan> {	
	private Intent intent;
	private Context context;
	private ActivityPlan activity;
	
	private Calendar c;
	private Button BTN_commit;
	private EditText ED_subject;

	
	public ActivityPlanTest(String pkg, Class<ActivityPlan> activityClass) {
		super(pkg, activityClass);
		
	}

	public ActivityPlanTest() {
		super("com.minsub.calendar",ActivityPlan.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getInstrumentation().getTargetContext();
		intent = new Intent(getInstrumentation().getTargetContext(), ActivityPlan.class);	
		activity = getActivity();
		
		BTN_commit = (Button)activity.findViewById(com.minsub.calendar.R.id.btn_commit);
		ED_subject = (EditText)activity.findViewById(com.minsub.calendar.R.id.ed_subject);
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
		assertNotNull(BTN_commit);
		assertNotNull(ED_subject);
		assertNotNull(c);
	} // end of testPreConditions() method definition

	
	
	// 단위 테스트
	
	@UiThreadTest
	public final void testAddMode() {		
		// ADD 모드로 액티비티실행
	}
	



}
