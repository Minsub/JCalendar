package com.minsub.calendar.planer;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;

public abstract class PlanerManager {
	public static final String BACKUP_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/jCalendar";
	public static final String BACKUP_FILE_NAME = "jCalendar_backup.xml";
	
	// 싱글톤 생성자
	public static PlanerManager getInstance(Context context){
		return PlanerData.getInstance(context); 
	}
	
	// 현재 노느테이블에서 해당 인덱스의 planNode 반환
	public abstract PlanNode getNode(int index);
	
	// 현재 노트테이블을 반환
	public abstract PlanNode[] getPlanNodeTable();

	// 입력받은 Start와 End 사이의 값들로 노드테이블을 채우고 그 테이블을 반환
	public abstract PlanNode[] getPlanNodeTable(long start, long end);
	
	// 입력받은 Start와 End 사이의 값들로 노드테이블을 채움
	public abstract void setPlanNodeTable(long start, long end);

	// 입력받은 일정을 디비와 현재 노드테이블에 삽입
	public abstract boolean insertPlan(String subject, long startdate, long enddate,
			String location, String memo, int alarm, int repeat, int vibrate);

	// 입력받은 일정을 디비와 현재 노드테이블에서 수정
	public abstract boolean updatePlan(long id, String subject, long startdate, long enddate, 
			String location, String memo, int alarm, int repeat, int vibrate);
	
	// 모든일정 검색.
	public abstract Cursor selectAllPlan();	
	// 기간검색
	public abstract Cursor selectPlan(long start, long end);
	
	// 키워드검색
	public abstract Cursor selectPlan(String keyword);
	
	// 반복일정 
	public abstract Cursor selectRepeatPlan();
	
	// 알람을 위한 하루 일정 검색
	public abstract Cursor selectPlanForAlarm(long start, long end);
	
	// 반복일정 (alarm and vibrate)
	public abstract Cursor selectRepeatPlanForAlarm();
	
	// 모든 스케줄 삭제
	public abstract boolean deleteAllPlan();
	
	// 해당 스케줄의 ID에 따른 삭제
	public abstract boolean deletePlan(long id);
	
	// 현재의 데이터베이스를 XML파일로 export
	public abstract boolean backupXML();

	// XML파일로 부터 데이터베이스로 import
	public abstract boolean restoreXML();
	
	// 스케줄의 데이터를 가지는 클래스, 동일한 날짜의 다음 스케줄을 가르킨다.
	public class PlanNode{
		protected PlanData data;
		protected long time;			// 0이면 하루종일
		protected PlanNode next = null;
		
		public PlanNode() {};
		public PlanNode getNext() {
			return this.next;
		}
		public long getTodayTime() {
			return this.time;
		}
		public long getId() {
			return data.id;
		}
		public String getSubject() {
			return data.subject;
		}
		public long getStartDate() {
			return data.startDate;
		}
		public long getEndDate() {
			return data.endDate;
		}
		public String getLocation() {
			return data.location;
		}
		public String getMemo() {
			return data.memo;
		}
		public int getAlarm() {
			return data.alarm;
		}
		public int getRepeat() {
			return data.repeat;
		}
		public int getVibrate() {
			return data.vibrate;
		}
	}
	
	protected class PlanData{
		protected long id;
		protected String subject;
		protected long startDate;
		protected long endDate;
		protected String location;
		protected String memo;
		protected int alarm;
		protected int repeat;
		protected int vibrate;
		protected PlanData() {}
	}
}
