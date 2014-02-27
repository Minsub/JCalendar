package com.minsub.calendar;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.minsub.calendar.planer.PlanerManager;

/*
 * Class : PlanerAlarm
 * 작성자 : 지민섭 
 * 2011. 01. 07
 * 설명 : 저장된 스케줄들에 대해 알람을 설정하거나 해제하는 클래스
 *       getInstance를 통해 싱클톤으로만 객체를 생성할 수 있다.
 *   
 */

public class PlanerAlarm {
	private static PlanerAlarm instance = null;

	private PlanerManager planerMgr = null;
	private AlarmManager alarmMgr = null;
	private Context mCtx;

	// 싱글톤 객체 생성자
	public static PlanerAlarm getInstance(Context context){
		if (instance == null){
			instance = new PlanerAlarm(context);
		}
		return instance;
	}
	
	// 생성자
	private PlanerAlarm(Context context) {
		planerMgr = PlanerManager.getInstance(context);
		this.mCtx = context;
		init();
	}

	private void init() {
		alarmMgr = (AlarmManager) mCtx.getSystemService(Context.ALARM_SERVICE);
	}

	public void setTodayAlarm() {
		Calendar c = Calendar.getInstance();
		long start = c.getTimeInMillis();
		c.add(Calendar.DATE, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		long end = c.getTimeInMillis();

		// 기존 알람 다 삭제
		removeAllAllarm();

		long id;
		String subject;
		long startDate;
		long endDate;
		String location;
		String memo;
		int alarm;
		int repeat;
		int vibrate;
		PendingIntent pi = null;
		Intent intent = null;

		int alarmCnt = 0;
		Cursor cursor = planerMgr.selectPlanForAlarm(start, end);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				id = cursor.getLong(0);
				subject = cursor.getString(1);
				startDate = cursor.getLong(2);
				endDate = cursor.getLong(3);
				location = cursor.getString(4);
				memo = cursor.getString(5);
				alarm = cursor.getInt(6);
				repeat = cursor.getInt(7);
				vibrate = cursor.getInt(8);

				

				c.setTimeInMillis(startDate);
				if (alarm == 2) { // 10분전
					c.add(Calendar.MINUTE, -10);
				} else if (alarm == 3) { // 30분전
					c.add(Calendar.MINUTE, -30);
				} else if (alarm == 4) { // 1시간전
					c.add(Calendar.HOUR_OF_DAY, -1);
				}
				
				// 계산된 시간이 지금시간보다 높은지 확인
				if(c.getTimeInMillis() > start) {
					
					intent = new Intent(mCtx, ReceiverAlarm.class);
					intent.putExtra(ActivityPlan.EXT_ID, id);
					intent.putExtra(ActivityPlan.EXT_SUBJECT, subject);
					intent.putExtra(ActivityPlan.EXT_LOCATION, location);
					intent.putExtra(ActivityPlan.EXT_STARTDATE, startDate);
					intent.putExtra(ActivityPlan.EXT_ENDDATE, endDate);
					intent.putExtra(ActivityPlan.EXT_ALARM, alarm);
					intent.putExtra(ActivityPlan.EXT_REPEAT, repeat);
					intent.putExtra(ActivityPlan.EXT_VIBRATE, vibrate);
					intent.putExtra(ActivityPlan.EXT_MEMO, memo);
					
					pi = PendingIntent.getBroadcast(mCtx, alarmCnt++, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					alarmMgr.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
	
					Log.d(Main.TAG, "Register subject: " + subject + ", TIME:"
							+ c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
				}
			} while (cursor.moveToNext());
			cursor.close();
		}

		// 반복 일정 추가
		Calendar todayCal = Calendar.getInstance();
		todayCal.set(Calendar.SECOND, 0);
		todayCal.set(Calendar.MILLISECOND, 0);
		Calendar startCal = Calendar.getInstance();

		cursor = planerMgr.selectRepeatPlanForAlarm();
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				intent = null;
				id = cursor.getLong(0);
				subject = cursor.getString(1);
				startDate = cursor.getLong(2);
				endDate = cursor.getLong(3);
				location = cursor.getString(4);
				memo = cursor.getString(5);
				alarm = cursor.getInt(6);
				repeat = cursor.getInt(7);
				vibrate = cursor.getInt(8);

				switch (repeat) {
				case 1: // 매일
					intent = new Intent(mCtx, ReceiverAlarm.class);
					startCal.setTimeInMillis(startDate);
					todayCal.set(Calendar.HOUR_OF_DAY, startCal
							.get(Calendar.HOUR_OF_DAY));
					todayCal
							.set(Calendar.MINUTE, startCal.get(Calendar.MINUTE));
					break;

				case 2: // 매주
					startCal.setTimeInMillis(startDate);
					if (startCal.get(Calendar.DAY_OF_WEEK) == todayCal
							.get(Calendar.DAY_OF_WEEK)) {
						intent = new Intent(mCtx, ReceiverAlarm.class);
						todayCal.set(Calendar.HOUR_OF_DAY, startCal
								.get(Calendar.HOUR_OF_DAY));
						todayCal.set(Calendar.MINUTE, startCal
								.get(Calendar.MINUTE));
					}
					break;

				case 3: // 매달
					startCal.setTimeInMillis(startDate);
					int maxDate = todayCal.getActualMaximum(Calendar.DATE);
					int planDate = startCal.get(Calendar.DATE);
					int todayDate = todayCal.get(Calendar.DATE);
					if (todayDate == planDate) {
						intent = new Intent(mCtx, ReceiverAlarm.class);
						todayCal.set(Calendar.HOUR_OF_DAY, startCal
								.get(Calendar.HOUR_OF_DAY));
						todayCal.set(Calendar.MINUTE, startCal
								.get(Calendar.MINUTE));
					} else if (planDate > maxDate && maxDate == todayDate) { // ex)31일이 시작인데 이번달이 28일때.. MaxDate를 시작날짜로 지정
						intent = new Intent(mCtx, ReceiverAlarm.class);
						todayCal.set(Calendar.HOUR_OF_DAY, startCal
								.get(Calendar.HOUR_OF_DAY));
						todayCal.set(Calendar.MINUTE, startCal
								.get(Calendar.MINUTE));
					}
					break;

				default:
					break;
				}

				if (intent != null) {
					// alarm타입에 따라 시간 조절
					if (alarm == 2) { // 10분전
						todayCal.add(Calendar.MINUTE, -10);
					} else if (alarm == 3) { // 30분전
						todayCal.add(Calendar.MINUTE, -30);
					} else if (alarm == 4) { // 1시간전
						todayCal.add(Calendar.HOUR_OF_DAY, -1);
					}

					if (start < todayCal.getTimeInMillis()) {
						intent.putExtra(ActivityPlan.EXT_ID, id);
						intent.putExtra(ActivityPlan.EXT_SUBJECT, subject);
						intent.putExtra(ActivityPlan.EXT_LOCATION, location);
						intent.putExtra(ActivityPlan.EXT_STARTDATE, startDate);
						intent.putExtra(ActivityPlan.EXT_ENDDATE, endDate);
						intent.putExtra(ActivityPlan.EXT_ALARM, alarm);
						intent.putExtra(ActivityPlan.EXT_REPEAT, repeat);
						intent.putExtra(ActivityPlan.EXT_VIBRATE, vibrate);
						intent.putExtra(ActivityPlan.EXT_MEMO, memo);

						pi = PendingIntent.getBroadcast(mCtx, alarmCnt++,
								intent, PendingIntent.FLAG_UPDATE_CURRENT);
						alarmMgr.set(AlarmManager.RTC_WAKEUP, todayCal
								.getTimeInMillis(), pi);

						Log.d(Main.TAG, "Register subject: " + subject
								+ ", TIME:" + todayCal.get(Calendar.HOUR_OF_DAY) + ":" + todayCal.get(Calendar.MINUTE));
					}
				}

			} while (cursor.moveToNext());
			cursor.close();
		}

		// 0시 0분에 알람을 등록하여 계속적으로 알람 자동 추가
		intent = new Intent(mCtx, ReceiverBoot.class);
		pi = PendingIntent.getBroadcast(mCtx, alarmCnt++, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		alarmMgr.set(AlarmManager.RTC_WAKEUP, end, pi);
		Log.d(Main.TAG, "Register ReceiverBoot for Tomorrow Alarm ");
	}

	// 기존 알람 삭제
	private void removeAllAllarm() {
		// 일단 수동적인 cancel 안하고 해보고 중복되면 코드 삽압하자!!

	}

}
