package com.minsub.calendar;

/*
 * Class : ActivitySearch
 * 작성자 : 지민섭 
 * 2011. 01. 10
 * 설명 : 스케줄의 검색
 *       
 */

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.minsub.calendar.planer.PlanerManager;
import com.minsub.utils.JStringUtils;

public class ActivitySearch extends Activity {
	private PlanerManager planerMgr = null;
	
	private Button BTN_search;
	private EditText ED_keyword;
	private ListView LV_search;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		init();
	}
	
	@Override
	protected void onResume() {
		
		fillListView();
		super.onResume();
	}
	
	private void init(){
		planerMgr = PlanerManager.getInstance(this);

		BTN_search = (Button) findViewById(R.id.btn_search);
		ED_keyword = (EditText) findViewById(R.id.ed_subject);
		LV_search = (ListView) findViewById(R.id.lv_search);
		LV_search.setDivider(new ColorDrawable(0xFFA9A9A9));
		LV_search.setDividerHeight(2);

		setListener();
	}

	// 모든 리스너 등록
	private void setListener() {
		// 검색버튼 리스너
		BTN_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				fillListView();
			}
		});
	}
	
	// 검색결과로 리스트뷰 채우기
	private void fillListView(){
		String keyword = ED_keyword.getText().toString();
		if(keyword.equalsIgnoreCase("")){
			Cursor cursor = planerMgr.selectAllPlan();
			if(cursor == null || cursor.getCount() < 1 ){
				LV_search.setAdapter(null);
			}else{
				SearchPlanAdapter adapter = new SearchPlanAdapter(this, cursor);
				LV_search.setAdapter(adapter);
			}
		} else {
			Cursor cursor = planerMgr.selectPlan(keyword);
			if(cursor == null || cursor.getCount() < 1 ){
				LV_search.setAdapter(null);
				Toast.makeText(this, "["+keyword+"]에 대한 검색결과는 없습니다.", Toast.LENGTH_LONG).show();
			}else{
				SearchPlanAdapter adapter = new SearchPlanAdapter(this, cursor);
				LV_search.setAdapter(adapter);
				Toast.makeText(this, "["+keyword+"]에 대한 검색결과는 "+cursor.getCount()+" 건입니다.", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	// 리스트뷰에 사용될 커스텀 아답터 클래스
	private class SearchPlanAdapter extends BaseAdapter {
    	private Context mCtx;
    	private Cursor cursor;
    	private Calendar calendar;
    	private OnClickListener clickListener;
		public SearchPlanAdapter(Context context,Cursor c){
			mCtx = context;
			cursor = c;
			calendar = Calendar.getInstance();
			clickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					ViewWrapper vw = (ViewWrapper)v.getTag();
					PlanItem item = (PlanItem)vw.subject.getTag();
					
					// 클릭시 일정 상세정보보기로 가야함
					Intent intent = new Intent(ActivitySearch.this, ActivityPlan.class);
					intent.putExtra(ActivityPlan.EXT_TYPE, 1);
					intent.putExtra(ActivityPlan.EXT_ID, item.getId());
					intent.putExtra(ActivityPlan.EXT_SUBJECT, item.getSubject());
					intent.putExtra(ActivityPlan.EXT_LOCATION, item.getLocation());
					intent.putExtra(ActivityPlan.EXT_STARTDATE, item.getStartDate());
					intent.putExtra(ActivityPlan.EXT_ENDDATE, item.getEndDate());
					intent.putExtra(ActivityPlan.EXT_ALARM, item.getAlarm());
					intent.putExtra(ActivityPlan.EXT_REPEAT, item.getRepeat());
					intent.putExtra(ActivityPlan.EXT_VIBRATE, item.getVibrate());
					intent.putExtra(ActivityPlan.EXT_MEMO, item.getMemo());
					startActivity(intent);
				}
			};
		}
		public int getCount() {
			return cursor.getCount();
		}
		
		public Object getItem(int position) {
			cursor.moveToPosition(position);
			return cursor;
		}


		public long getItemId(int position) {
			cursor.moveToPosition(position);
			return cursor.getInt(0);
		}


		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			TextView tv_time;
			TextView tv_subject;
			
			if(row == null){
				LayoutInflater inflater=((Activity) mCtx).getLayoutInflater();
				row=inflater.inflate(R.layout.search_list_item, null);
				ViewWrapper vw = new ViewWrapper();
				vw.subject = (TextView)row.findViewById(R.id.tv_subject);
				vw.time = (TextView)row.findViewById(R.id.tv_time);
				
				tv_time = vw.time;
				tv_subject = vw.subject;
				
				row.setTag(vw);
			}
			else{
				ViewWrapper vw = (ViewWrapper)row.getTag();
				tv_time = vw.time;
				tv_subject = vw.subject;
			}
			
			// 검색된 결과를 커서에서 받아서 PlanNode로 저장
			cursor.moveToPosition(position);
			
			PlanItem item = new PlanItem();
			item.setId(cursor.getLong(0));
			item.setSubject(cursor.getString(1));
			item.setStartDate(cursor.getLong(2));
			item.setEndDate(cursor.getLong(3));
			item.setLocation(cursor.getString(4));
			item.setMemo(cursor.getString(5));
			item.setAlarm(cursor.getInt(6));
			item.setRepeat(cursor.getInt(7));
			item.setVibrate(cursor.getInt(8));
			tv_subject.setTag(item);
			
			calendar.setTimeInMillis(item.getStartDate());
			tv_time.setText(calendar.get(Calendar.YEAR) + "/"
					+ JStringUtils.retainNumber10(calendar.get(Calendar.MONTH) + 1) + "/"
					+ JStringUtils.retainNumber10(calendar.get(Calendar.DATE)));
			
			tv_subject.setText(item.getSubject());
			
			row.setOnClickListener(clickListener);
			
			return row;
		}
		private class ViewWrapper{
			public TextView time;
			public TextView subject;
		}
		
		private class PlanItem{
			private long id;
			private String subject;
			private long startDate;
			private long endDate;
			private String location;
			private String memo;
			private int alarm;
			private int repeat;
			private int vibrate;
			public PlanItem() {}
			public long getId() {
				return id;
			}
			public void setId(long id) {
				this.id = id;
			}
			public String getSubject() {
				return subject;
			}
			public void setSubject(String subject) {
				this.subject = subject;
			}
			public long getStartDate() {
				return startDate;
			}
			public void setStartDate(long startDate) {
				this.startDate = startDate;
			}
			public long getEndDate() {
				return endDate;
			}
			public void setEndDate(long endDate) {
				this.endDate = endDate;
			}
			public String getLocation() {
				return location;
			}
			public void setLocation(String location) {
				this.location = location;
			}
			public String getMemo() {
				return memo;
			}
			public void setMemo(String memo) {
				this.memo = memo;
			}
			public int getAlarm() {
				return alarm;
			}
			public void setAlarm(int alarm) {
				this.alarm = alarm;
			}
			public int getRepeat() {
				return repeat;
			}
			public void setRepeat(int repeat) {
				this.repeat = repeat;
			}
			public int getVibrate() {
				return vibrate;
			}
			public void setVibrate(int vibrate) {
				this.vibrate = vibrate;
			}
		}
    }

}
