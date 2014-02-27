package com.minsub.calendar.planer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 * Class : DatabaseAdapter
 * 작성자 : 지민섭 
 * 2011. 01. 07
 * 설명 : SQLite를 사용하기 위한 클래스
 *       INSERT, DELETE, UPDATE, SELECT등의 쿼리를 함수화하여 등록
 *       외부 클래스에서 해당 메소드만 사용하여 쿼리를 사용할 수 있도록 제공
 *       디비나 테이블은 정적으로 미리 정의하여 사용
 */
class DatabaseAdapter {
	public static final String KEY_ID = "_id";					//0 ID
	public static final String KEY_SUBJECT = "subject";			//1 제목
	public static final String KEY_STARTDATE = "startdate";		//2 일정 시작날짜
	public static final String KEY_ENDDATE = "enddate";			//3 일정 종료날짜
	public static final String KEY_LOCATION = "location";		//4 지역
	public static final String KEY_MEMO = "memo";				//5 메모
	public static final String KEY_ALARM = "alarm";				//6 알림 ( 0: 없음, 1: 해당시간 2: 10분전, 3: 30분전, 4: 1시간전)
	public static final String KEY_REPEAT = "repeat";			//7 반복설정 (0: 없음, 1: 매일, 2: 매주, 3: 매달)
	public static final String KEY_VIBRATE = "vibrate";			//8 해당 일정 시작시 진동모드( 0: 없음, 1: 사용);
	
	private static final String DATABASE_NAME = "db_jcalendar";
	private static final String DATABASE_TABLE_PLAN = "tbl_jcal_plan";
	private static final int DATABASE_VERSION = 3;

	public static final String DATABASE_CREATE_TABLE_PLAN = 
		   "create table " + DATABASE_TABLE_PLAN + 
		   "( "+ KEY_ID + " integer primary key autoincrement ,"
			+ KEY_SUBJECT + " text, "
			+ KEY_STARTDATE + " integer, "
			+ KEY_ENDDATE + " integer, "
			+ KEY_LOCATION + " text, "
			+ KEY_MEMO + " text, "
			+ KEY_ALARM + " integer, "
			+ KEY_REPEAT + " integer, "
			+ KEY_VIBRATE + " integer);";

	private DBHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mCtx;

	// SQLite를 사용하기 위한 클래스
	private static class DBHelper extends SQLiteOpenHelper {
		DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			createTableMemo(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("plife", "DB: Upgrading database from version "
							+ oldVersion + " to " + newVersion
							+ ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_PLAN);
			onCreate(db);
		}

		public void createTableMemo(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE_TABLE_PLAN);
		}
	}

	// 생성자
	public DatabaseAdapter(Context context) {
		this.mCtx = context;
	}

	// SQLite를 사용하기 위해 DBHelper클래스를 생성
	public DatabaseAdapter open() throws SQLException {
		mDbHelper = new DBHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	// DBHelper 종료
	public void close() {
		mDbHelper.close();
	}
	
	
	//////////////
	// querys.. //
	//////////////

	// Insert Query //
	// 스케줄 입력
	public long insertPlan(String subject, long startdate, long enddate,
			String location, String memo, int alarm, int repeat, int vibrate) {
		
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_SUBJECT, subject);
		initialValues.put(KEY_STARTDATE, startdate);
		initialValues.put(KEY_ENDDATE, enddate);
		initialValues.put(KEY_LOCATION, location);
		initialValues.put(KEY_MEMO, memo);
		initialValues.put(KEY_ALARM, alarm);
		initialValues.put(KEY_REPEAT, repeat);
		initialValues.put(KEY_VIBRATE, vibrate);

		return mDb.insert(DATABASE_TABLE_PLAN, null, initialValues);
	}

	// Update Query //
	// 스케줄의 ID에 따른 각 속성 업데이트 ( null이나 -1이 들어오면 데이터를 변경하지 않음)
	public boolean updatePlan(long id, String subject, long startdate,
			long enddate, String location, String memo, int alarm, int repeat,
			int vibrate) {
		try{
			String strWhere = KEY_ID + "=" + id;
			ContentValues initialValues = new ContentValues();
			if(subject != null){
				initialValues.put(KEY_SUBJECT, subject);
			}
			
			if(startdate != -1){
				initialValues.put(KEY_STARTDATE, startdate);
			}
			
			if(enddate != -1){
				initialValues.put(KEY_ENDDATE, enddate);
			}
			
			if(location != null){
				initialValues.put(KEY_LOCATION, location);
			}
		
			if(memo != null){
				initialValues.put(KEY_MEMO, memo);
			}
			
			if(alarm != -1){
				initialValues.put(KEY_ALARM, alarm);
			}
			
			if(repeat != -1){
				initialValues.put(KEY_REPEAT, repeat);
			}
			
			if(vibrate != -1){
				initialValues.put(KEY_VIBRATE, vibrate);
			}
			
			return mDb.update(DATABASE_TABLE_PLAN, initialValues, strWhere, null) > 0;
			
		} catch (Exception e) {
			return false;
		}
	}

	// Select Query //
	// 모든일정 검색.
	public Cursor selectAllPlan() {
		String[] strSelectField = { KEY_ID, KEY_SUBJECT, KEY_STARTDATE,
				KEY_ENDDATE, KEY_LOCATION, KEY_MEMO, KEY_ALARM, KEY_REPEAT,
				KEY_VIBRATE };
		String strSort = KEY_STARTDATE+" ASC";
		
		Cursor cursor = mDb.query(true, DATABASE_TABLE_PLAN, strSelectField,
				null, null, null, null, strSort, null);

		if (cursor == null || cursor.getCount() < 1) {
			return null;
		}
		return cursor;
	}
	
	// 기간검색
	public Cursor selectPlan(long start, long end){
		String[] strSelectField = { KEY_ID, KEY_SUBJECT, KEY_STARTDATE,
				KEY_ENDDATE, KEY_LOCATION, KEY_MEMO, KEY_ALARM, KEY_REPEAT,
				KEY_VIBRATE };
		
//		String strWhere = "(( "+ KEY_STARTDATE+" BETWEEN "+start + " AND " + end +
//		") OR (" + KEY_ENDDATE + " BETWEEN " + start + " AND " + end + " )) AND " + KEY_REPEAT + " = 0 " ;
		
		String strWhere = "(( "+ KEY_STARTDATE+" < "+start + " AND " + KEY_ENDDATE + " > " + end +
		" ) OR ( " + KEY_STARTDATE + " > " + start + " AND "+ KEY_ENDDATE + " < " + end +
		" ) OR ( " + KEY_STARTDATE + " > " + start + " AND "+ KEY_STARTDATE + " < " + end +
		" ) OR ( " + KEY_ENDDATE + " > " + start + " AND "+ KEY_ENDDATE + " < " + end +" )) AND "+ KEY_REPEAT + " = 0 ";
		
		String strSort = KEY_STARTDATE+" ASC";
		
		Cursor cursor = mDb.query(true, DATABASE_TABLE_PLAN, strSelectField,
				strWhere, null, null, null, strSort, null);

		if (cursor == null || cursor.getCount() < 1) {
			return null;
		}
		return cursor;
	}
	
	// 키워드검색
	public Cursor selectPlan(String keyword){
		String[] strSelectField = { KEY_ID, KEY_SUBJECT, KEY_STARTDATE,
				KEY_ENDDATE, KEY_LOCATION, KEY_MEMO, KEY_ALARM, KEY_REPEAT,
				KEY_VIBRATE };
		
		String strWhere = KEY_SUBJECT + " LIKE '%"+keyword+"%' OR "+
						  KEY_LOCATION + " LIKE '%"+keyword+"%' OR "+
						  KEY_MEMO + " LIKE '%"+keyword+"%' ";
		
		// MUST ADD ( START값과 END값중 큰값으로 정렬하는 쿼리 추가해야 함) 
		String strSort = KEY_STARTDATE+" ASC";
		
		Cursor cursor = mDb.query(true, DATABASE_TABLE_PLAN, strSelectField,
				strWhere, null, null, null, strSort, null);

		if (cursor == null || cursor.getCount() < 1) {
			return null;
		}
		return cursor;
	}
	
	// 반복일정 
	public Cursor selectRepeatPlan(){
		String[] strSelectField = { KEY_ID, KEY_SUBJECT, KEY_STARTDATE,
				KEY_ENDDATE, KEY_LOCATION, KEY_MEMO, KEY_ALARM, KEY_REPEAT,
				KEY_VIBRATE };
		
		String strWhere = KEY_REPEAT + " > 0";
		
		// MUST ADD ( START값과 END값중 큰값으로 정렬하는 쿼리 추가해야 함) 
		String strSort = KEY_STARTDATE+" ASC";
		
		Cursor cursor = mDb.query(true, DATABASE_TABLE_PLAN, strSelectField,
				strWhere, null, null, null, strSort, null);

		if (cursor == null || cursor.getCount() < 1) {
			return null;
		}
		return cursor;
	}
	
	// 알람을 위한 하루 일정 검색
	public Cursor selectPlanForAlarm(long start, long end){
		String[] strSelectField = { KEY_ID, KEY_SUBJECT, KEY_STARTDATE,
				KEY_ENDDATE, KEY_LOCATION, KEY_MEMO, KEY_ALARM, KEY_REPEAT,
				KEY_VIBRATE };
		
		String strWhere = KEY_STARTDATE + " > " + start +
				" AND " + KEY_STARTDATE + " < " + end + 
				" AND (" + KEY_ALARM + " > 0 OR " + KEY_VIBRATE + " = 1 )" +
				" AND " + KEY_REPEAT + " = 0";
		
		String strSort = KEY_STARTDATE+" ASC";
		
		Cursor cursor = mDb.query(true, DATABASE_TABLE_PLAN, strSelectField,
				strWhere, null, null, null, strSort, null);

		if (cursor == null || cursor.getCount() < 1) {
			return null;
		}
		return cursor;
	}
	
	// 반복일정 (alarm and vibrate)
	public Cursor selectRepeatPlanForAlarm(){
		String[] strSelectField = { KEY_ID, KEY_SUBJECT, KEY_STARTDATE,
				KEY_ENDDATE, KEY_LOCATION, KEY_MEMO, KEY_ALARM, KEY_REPEAT,
				KEY_VIBRATE };
		
		String strWhere = KEY_REPEAT + " > 0 AND ( " + KEY_ALARM + " > 0 OR " + KEY_VIBRATE + " > 0 )";
		
		// MUST ADD ( START값과 END값중 큰값으로 정렬하는 쿼리 추가해야 함) 
		String strSort = KEY_STARTDATE+" ASC";
		
		Cursor cursor = mDb.query(true, DATABASE_TABLE_PLAN, strSelectField,
				strWhere, null, null, null, strSort, null);

		if (cursor == null || cursor.getCount() < 1) {
			return null;
		}
		return cursor;
	}
	

	/* Delete Query */
	// 모든 스케줄 삭제
	public boolean deleteAllPlan() {
		return mDb.delete(DATABASE_TABLE_PLAN, null, null) > 0;
	}

	// 해당 스케줄의 ID에 따른 삭제
	public boolean deletePlan(long id) {
		String strWhere = KEY_ID+"="+id;
		return mDb.delete(DATABASE_TABLE_PLAN, strWhere, null) > 0;
		
	}
}
