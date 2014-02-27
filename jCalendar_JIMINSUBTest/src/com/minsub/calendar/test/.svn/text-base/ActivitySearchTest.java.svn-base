package com.minsub.calendar.test;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.minsub.calendar.ActivityPlan;
import com.minsub.calendar.ActivitySearch;
import com.minsub.calendar.planer.PlanerManager;

public class ActivitySearchTest extends ActivityInstrumentationTestCase2<ActivitySearch> {	
	private Intent intent;
	private Context context;
	private ActivitySearch activity;
	
	private Calendar c;
	private ListView LV_search;
	private Button BTN_search;
	private EditText ED_keyword;
	private PlanerManager pm;

	
	public ActivitySearchTest(String pkg, Class<ActivitySearch> activityClass) {
		super(pkg, activityClass);
		
	}

	public ActivitySearchTest() {
		super("com.minsub.calendar",ActivitySearch.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getInstrumentation().getTargetContext();
		intent = new Intent(getInstrumentation().getTargetContext(), ActivityPlan.class);	
		activity = getActivity();
		
		LV_search = (ListView)activity.findViewById(com.minsub.calendar.R.id.lv_search);
		BTN_search = (Button)activity.findViewById(com.minsub.calendar.R.id.btn_search);
		ED_keyword = (EditText)activity.findViewById(com.minsub.calendar.R.id.ed_subject);
		
		c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		pm = PlanerManager.getInstance(context); 
	}

	
	public void testPreConditions() {
		assertNotNull(intent);
		assertNotNull(context);
		assertNotNull(activity);
		assertNotNull(LV_search);
		assertNotNull(BTN_search);
		assertNotNull(ED_keyword);
		assertNotNull(pm);
		assertNotNull(c);
	} // end of testPreConditions() method definition

	
	
	// 단위 테스트
	
	@UiThreadTest
	public final void testAllPlan() {		
		int lvCnt = LV_search.getCount();
		
		// 모든 스케줄의 갯수와 리스트뷰에 나타난 스케줄의 갯수 비교
		assertEquals(pm.selectAllPlan().getCount(),lvCnt);
		
	}
	
	
	@UiThreadTest
	public final void testSearch() {		
		int lvCnt = LV_search.getCount();
		// 공백으로 검색시 동작하지 않음
		BTN_search.performClick();
		assertEquals(pm.selectAllPlan().getCount(),lvCnt);
		
		
		
		// test 란 키워드로 검색시 검색된 결과와 리스트뷰에 나온 결과의 갯수가 같은지 확인
		ED_keyword.setText("test");
		BTN_search.performClick();
		
		lvCnt = LV_search.getCount();
		assertEquals(pm.selectPlan("test").getCount(),lvCnt);
		
	}
	
	@UiThreadTest
	public final void testItemClick() {	
		// 아이템 클릭시 액티비티 전환 가능
		View v = LV_search.getChildAt(0);
//		assertTrue(v.performClick());
	}



}
