package com.minsub.calendar;

/*
 * Class : BootReceiver
 * 작성자 : 지민섭 
 * 2011. 01. 07
 * 설명 : 부팅시 호출되는 BroadcastReceiver
 *       알람이나 진동모드 변환에 관련된 동작을 실행한다.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiverBoot extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
//		PlanerAlarm planerAlarm = new PlanerAlarm(context);
		PlanerAlarm planerAlarm = PlanerAlarm.getInstance(context);
		planerAlarm.setTodayAlarm();
		Log.d(Main.TAG,"ReceiverBoot excute");
	}

}
