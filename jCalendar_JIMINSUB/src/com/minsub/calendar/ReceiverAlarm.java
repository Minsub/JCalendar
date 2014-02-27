package com.minsub.calendar;

/*
 * Class : ReceiverAlarm
 * 작성자 : 지민섭 
 * 2011. 01. 12
 * 설명 : 해당 일정의 알람시간이 되면 호출되는 클래스
 *       팝업이나 진동모드를 설정한다.
 */

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

public class ReceiverAlarm extends BroadcastReceiver{
	private static int notifyCnt = 0;
	private final String[] arrAlarm = {"none","now","10 minutes ago","30 minutes ago", "1 hour ago"};
	
	@Override
	public void onReceive(Context context, Intent intent) {
		long id;
		String subject;
		long startDate;
		long endDate;
		String location;
		String memo;
		int alarm;
		int repeat;
		int vibrate;
		
		Bundle extras = intent.getExtras();
		if (extras != null) {
			id = extras.getLong(ActivityPlan.EXT_ID);
			subject = extras.getString(ActivityPlan.EXT_SUBJECT);
			location = extras.getString(ActivityPlan.EXT_LOCATION);
			startDate = extras.getLong(ActivityPlan.EXT_STARTDATE);
			endDate = extras.getLong(ActivityPlan.EXT_ENDDATE);
			alarm = extras.getInt(ActivityPlan.EXT_ALARM);
			repeat = extras.getInt(ActivityPlan.EXT_REPEAT);
			vibrate = extras.getInt(ActivityPlan.EXT_VIBRATE);
			memo = extras.getString(ActivityPlan.EXT_MEMO);
		
		
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(startDate);
			
			Log.d(Main.TAG,"[excute alarm] subject: " + subject + ", TIME:"
					+ c.get(Calendar.HOUR_OF_DAY) + ":"
					+ c.get(Calendar.MINUTE) );
			
			NotificationManager notifiyMgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notify = new Notification();
			notify.icon = R.drawable.icon;
			notify.tickerText = subject;
			notify.when = System.currentTimeMillis();
			notify.defaults = Notification.DEFAULT_ALL;
			notify.flags = Notification.FLAG_AUTO_CANCEL;
			
			// 알림시간 세팅
			
			String strBody = arrAlarm[alarm];
			if (alarm == 0 && vibrate == 1) {
				strBody = arrAlarm[1];
			}
			
			Intent startIntent = new Intent(context,Main.class);
			PendingIntent intentBack = PendingIntent.getActivity(context, 0,startIntent, 0);
			notify.setLatestEventInfo(context, subject, strBody, intentBack);
			
			if(notifyCnt == Integer.MAX_VALUE){
				notifyCnt = 0;
			}
			
			// 진동모드 바꾸기
			if(vibrate == 1){
				AudioManager aManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				aManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				Log.d(Main.TAG,"[excute vibrate] subject: " + subject + ", TIME:"
						+ c.get(Calendar.HOUR_OF_DAY) + ":"
						+ c.get(Calendar.MINUTE) );
			}
			
			notifiyMgr.notify(notifyCnt++,notify);
			
			
		} else {
			Log.d(Main.TAG, "ERROR: register alarm");
		}
	}

}
