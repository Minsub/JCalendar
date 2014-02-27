package com.minsub.calendar.planer;

import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;

/*
 * Class : PlanerData
 * 작성자 : 지민섭 
 * 2011. 01. 07
 * 설명 : 캘린더의 삽입될 스케줄들의 데이터들을 관리.
 *       UI클래스에서 해당 클래스의 데이터를 사용.
 *       DatabaseAdapter 클래스를 이용하여 데이터를 저장.
 *       (디비를 사용하지 않고 파일에 저장을 한다면 DatabaseAdapter에서 제공하는
 *       각각의 메소드만을 재정의 해서 사용될 수 있음)
 *       
 * 구현내용 : 한달과 이전달, 다음달의 날짜의 약간(7 * 6)의 데이터를 배열로 유지
 *          각각의 날에는 PlanNode의 링크드리스트로 각각의 스케줄을 연결하여 유지
 *          스케줄의 전체시간을 각각의 날짜에 맞춰 배치
 *          각각의 날마다 PlanNode를 생성하여 하나의 PlanData를 참조하여 사용
 *          달이 바뀔때 새로 데이터를 불러옴
 */

class PlanerData extends PlanerManager{
	private static PlanerData instance = null;
	
	private final int LENGTH_NODETABLE = 7 * 6;	// 노드테이블의 크기
	private final long MILLISECOND_ONEDAY = 60 * 60 * 24 * 1000; // 하루의 밀리세컨트 값
	
	private PlanData[] repeatDatas;
	private PlanNode[] nodeTable;						// 7 * 6 의 노드 테이블
	private DatabaseAdapter DBAdapter;
	private DatabaseExternalIO externalIO = null;
	private long startIndexTime;
	private long endIndexTime;
	private Context ctx;

	// 싱글톤 생성자
	public static PlanerData getInstance(Context context){
		if (instance == null) {
			instance = new PlanerData(context);
		}
		return instance;
	}
	
	// 생성자
	private PlanerData(Context context){
		ctx = context;
		nodeTable = new PlanNode[LENGTH_NODETABLE];
		DBAdapter = new DatabaseAdapter(context);
		DBAdapter.open();
	}
	
	// 현재 노드 테이블에서 해당 인덱스의 노드 반
	@Override
	public PlanNode getNode(int index) {
		if (index > 0 && index < LENGTH_NODETABLE) {
			return nodeTable[index];
		}
		return null;
	}
	
	// 현재 노트테이블을 반환
	@Override
	public PlanNode[] getPlanNodeTable() {
		return nodeTable;
	}

	// 입력받은 Start와 End 사이의 값들로 노드테이블을 채우고 그 테이블을 반환
	@Override
	public PlanNode[] getPlanNodeTable(long start, long end) {
		setNodeTabe(start, end);
		return nodeTable;
	}
	
	// 입력받은 Start와 End 사이의 값들로 노드테이블을 채움
	@Override
	public void setPlanNodeTable(long start, long end) {
		setNodeTabe(start, end);
	}

	// 입력받은 일정을 디비와 현재 노드테이블에 삽입
	@Override
	public boolean insertPlan(String subject, long startdate, long enddate,
			String location, String memo, int alarm, int repeat, int vibrate) {

		if (DBAdapter.insertPlan(subject, startdate, enddate, location, memo,
				alarm, repeat, vibrate) > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	// 입력받은 일정을 디비와 현재 노드테이블에서 수정
	@Override
	public boolean updatePlan(long id, String subject, long startdate,
			long enddate, String location, String memo, int alarm, int repeat,
			int vibrate) {

		if (DBAdapter.updatePlan(id, subject, startdate, enddate, location,
				memo, alarm, repeat, vibrate)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Cursor selectAllPlan() {
		return DBAdapter.selectAllPlan();
	}

	@Override
	public Cursor selectPlan(long start, long end) {
		return DBAdapter.selectPlan(start, end);
	}

	@Override
	public Cursor selectPlan(String keyword) {
		return DBAdapter.selectPlan(keyword);
	}

	@Override
	public Cursor selectPlanForAlarm(long start, long end) {
		return DBAdapter.selectPlanForAlarm(start, end);
	}

	@Override
	public Cursor selectRepeatPlan() {
		return DBAdapter.selectRepeatPlan();
	}

	@Override
	public Cursor selectRepeatPlanForAlarm() {
		return DBAdapter.selectRepeatPlanForAlarm();
	}
	
	@Override
	public boolean deleteAllPlan() {
		return DBAdapter.deleteAllPlan();
	}

	@Override
	public boolean deletePlan(long id) {
		if (DBAdapter.deletePlan(id)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean backupXML() {
		if (externalIO == null) {
			externalIO = DatabaseExternalIO.getInstance(ctx);
		}
		return externalIO.backupXML();
	}

	@Override
	public boolean restoreXML() {
		if (externalIO == null) {
			externalIO = DatabaseExternalIO.getInstance(ctx);
		}
		return externalIO.restoreXML();
	}

	// /////////////////
	// private Method //
	// /////////////////
	
	// 반복설정을 노드테이블에 반복적 속성을 대입해 삽입
	private void setNodeTableAtRepeatPlan() {
		setRepeatDatas();
		if (repeatDatas != null) {
			
			PlanData data;
			PlanNode node;
			Calendar startCal = Calendar.getInstance();
			Calendar endCal = Calendar.getInstance();
			
			for (int i = 0; i < repeatDatas.length; i++){
				data = repeatDatas[i];
				switch (data.repeat) {
					case 1: 	// 매일
						for (int j = 0; j < LENGTH_NODETABLE; j++) {
							node = new PlanNode();
							node.data = data;
							node.time = data.startDate;
							addNode(j, node);
						}
						
						break;
						
					case 2: 	// 매주
						startCal.setTimeInMillis(data.startDate);
						endCal.setTimeInMillis(data.endDate);
						int startWeekIndex = startCal.get(Calendar.DAY_OF_WEEK) - 1;
						int endWeekIndex = endCal.get(Calendar.DAY_OF_WEEK) - 1;
						int tmpWeek = endWeekIndex - startWeekIndex;
						int index;
						for (int j = 0; j < LENGTH_NODETABLE; j++) {
							index = j % 7;
							if (index == startWeekIndex) { // 시작시간
								node = new PlanNode();
								node.data = data;
								node.time = data.startDate;
								addNode(j, node);
							} else if (index == endWeekIndex) { // 종료시간
								node = new PlanNode();
								node.data = data;
								node.time = data.endDate;
								addNode(j, node);
							} else if ( tmpWeek > 0 && (index > startWeekIndex && j < endWeekIndex) ) { // 하루종일
								node = new PlanNode();
								node.data = data;
								node.time = 0;
								addNode(j, node);
							} else if ( (tmpWeek < 0) && ( (index > startWeekIndex) || (index < endWeekIndex) )){ // 하루종일
								node = new PlanNode();
								node.data = data;
								node.time = 0;
								addNode(j, node);
							} 
						}
						
						break;
					case 3:	 	// 매월
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(startIndexTime);
						startCal.setTimeInMillis(data.startDate);
						endCal.setTimeInMillis(data.endDate);
						int firstCellDate = c.get(Calendar.DATE);		// 테이블 첫번째 셀의 날짜
						int prevCellCnt;								// 7*6테이블에서 1일전에 있는 셀의 갯수
						if (firstCellDate != 1) {
							prevCellCnt = c.getActualMaximum(Calendar.DATE) - firstCellDate + 1;
							c.add(Calendar.DATE, prevCellCnt+1);
						}else{
							prevCellCnt = 0;
						}
						int realStartDay = startCal.get(Calendar.DATE);		//진짜 시작날짜
						int realEndDay = endCal.get(Calendar.DATE);			//진짜 끝날짜
						int maxDateThisMonth = c.getActualMaximum(Calendar.DATE); //이번달 의 최대 날짜
						int startDay = realStartDay, endDay = realEndDay; // 변경된 시작,끝 날짜
						c.add(Calendar.MONTH, -1);
						int maxDatePrevMonth = c.getActualMaximum(Calendar.DATE);
						
						// 설정된 날짜가 이번달에 없을 때 ex) 종료날짜 31일인데 이번달은 2월달 
						if (realStartDay > maxDateThisMonth) {
							startDay = maxDateThisMonth;
						} else if (realEndDay > maxDateThisMonth) {
							endDay = maxDateThisMonth;
						}
						

						if (realEndDay >= realStartDay) {	// 시작날짜가 종료날짜보다 더 작을떄 ex)1일부터 10일까지..
							
							for ( int j = startDay -1 + prevCellCnt; j <= endDay - 1 + prevCellCnt; j++) {
								node = new PlanNode();
								node.data = data;
								if ( j == startDay -1 + prevCellCnt) {
									node.time = data.startDate;
								} else if ( j == endDay -1 + prevCellCnt ) {
									node.time = data.endDate;
								} else {
									node.time = 0;
								}
								addNode(j, node);
							}
							
							// 다음달의 셀에도 포함될때..
							if (maxDateThisMonth + prevCellCnt + startDay -1 < LENGTH_NODETABLE) {
								
								int tmp = maxDateThisMonth + prevCellCnt + endDay -1;
								for ( int j = maxDateThisMonth + prevCellCnt + startDay -1; j < LENGTH_NODETABLE; j++) {
									if (j <= tmp) {
										node = new PlanNode();
										node.data = data;
										if ( j == maxDateThisMonth + prevCellCnt + startDay -1) {
											node.time = data.startDate;
										} else if ( j == tmp) {
											node.time = data.endDate;
										} else {
											node.time = 0;
										}
										addNode(j, node);
									}
								}
							}
							
							// 이전달의 셀에도 포함될때...
							if (prevCellCnt != 0 && firstCellDate <= realStartDay) {
								for (int j = realStartDay; j <= realEndDay && j <= maxDatePrevMonth; j++) {
									node = new PlanNode();
									node.data = data;
									if (j == realStartDay) {
										node.time = data.startDate;
									} else if (j == realEndDay || j == maxDatePrevMonth) {
										node.time = data.endDate;
									} else{
										node.time = 0;
									}
									addNode(j - firstCellDate, node);
								}
							} else if (prevCellCnt != 0 && firstCellDate <= endDay ) {
								for (int j = firstCellDate; j <= realEndDay && j <= maxDatePrevMonth; j++) {
									node = new PlanNode();
									node.data = data;
									if (j == realEndDay || j ==maxDatePrevMonth) {
										node.time = data.endDate;
									} else{
										node.time = 0;
									}
									addNode(j - firstCellDate, node);
								}
							} 
							
							
						} else { // 시작날짜가 종료날짜보다 더 클때 ex)28일부터 10일까지..
							// 이번달 후반부부터 끝날까지 삽입
							for (int j = startDay - 1; j < maxDateThisMonth; j++) {
								node = new PlanNode();
								node.data = data;
								if (j == startDay -1) {
									node.time = data.startDate;
								}else {
									node.time = 0;
								}
								addNode(j + prevCellCnt  , node);
							}
							// 1일 부터 종료시간까지 삽입
							for (int j = 0; j < realEndDay; j++) {
								node = new PlanNode();
								node.data = data;
								if (j == realEndDay - 1) {
									node.time = data.endDate;
								}else {
									node.time = 0;
								}
								addNode(j + prevCellCnt  , node);
							}
							// 다음달의 1일부터 삽입
							for (int j = maxDateThisMonth + prevCellCnt; j < LENGTH_NODETABLE; j++) {
								if(j < maxDateThisMonth + prevCellCnt + realEndDay) {
									node = new PlanNode();
									node.data = data;
									if (j == realEndDay + maxDateThisMonth + prevCellCnt - 1) {
										node.time = data.endDate;
									}else {
										node.time = 0;
									}
									addNode(j  , node);
								}
							}
							// 이전달 삽입
							if(prevCellCnt != 0){
								for (int j = firstCellDate; j <= maxDatePrevMonth; j++) {
									if( j >= realStartDay) {
										node = new PlanNode();
										node.data = data;
										if (j == realStartDay) {
											node.time = data.startDate;
										}else {
											node.time = 0;
										}
										addNode(j - firstCellDate  , node);
									}
								}
							}
						}
						break;
					default:
						break;
				}
			}
		}
	}

	// 반복설정이 된 일정을 DB로 부터 불러온다.
	private void setRepeatDatas() {
		Cursor cursor = DBAdapter.selectRepeatPlan();
		if (cursor == null || cursor.getCount() < 1) {
			repeatDatas = null;
			return;
		}

		cursor.moveToFirst();
		int index = 0;
		repeatDatas = new PlanData[cursor.getCount()];

		do {
			PlanData data;
			data = new PlanData();
			data.id = cursor.getLong(0);
			data.subject = cursor.getString(1);
			data.startDate = cursor.getLong(2);
			data.endDate = cursor.getLong(3);
			data.location = cursor.getString(4);
			data.memo = cursor.getString(5);
			data.alarm = cursor.getInt(6);
			data.repeat = cursor.getInt(7);
			data.vibrate = cursor.getInt(8);
			repeatDatas[index++] = data;
		} while (cursor.moveToNext());
		cursor.close();
	}

	// 노드 테이블에 데이터를 채운다
	private void setNodeTabe(long start, long end) {
		resetNodeTable();
		startIndexTime = start;
		endIndexTime = end + MILLISECOND_ONEDAY;
		
		Cursor cursor = DBAdapter.selectPlan(startIndexTime, endIndexTime);
		if (cursor != null) {
			cursor.moveToFirst();
			do {
				PlanData data;
				data = new PlanData();
				data.id = cursor.getLong(0);
				data.subject = cursor.getString(1);
				data.startDate = cursor.getLong(2);
				data.endDate = cursor.getLong(3);
				data.location = cursor.getString(4);
				data.memo = cursor.getString(5);
				data.alarm = cursor.getInt(6);
				data.repeat = cursor.getInt(7);
				data.vibrate = cursor.getInt(8);
	
				addTableNode(data); // 불러들인 노드를 테이블에 저장
	
			} while (cursor.moveToNext());
			cursor.close();
		}
		
		// repeat일정을 추가적으로 불러들이고, 노드 테이블에 삽입
		setNodeTableAtRepeatPlan();

	}
	
	// 노드테이블에 노드를 삽입
	private void addTableNode(PlanData nodeData){
		// 시작날짜부터 종료날짜까지 검사하며 포함되는 모든 날짜의 테이블에 삽입
		PlanNode node;
		long start = (nodeData.startDate - startIndexTime) / MILLISECOND_ONEDAY;
		long end = endIndexTime < nodeData.endDate ? ((endIndexTime - startIndexTime) / MILLISECOND_ONEDAY) -1
				: (nodeData.endDate - startIndexTime) / MILLISECOND_ONEDAY;
		int index = (int)start;
		while (index <= end) {
			if (index >= 0) {
				// NodePlan 생성(시간 설정)
				node = new PlanNode();
				node.data = nodeData;
				if (index == start) { // 시작시간
					node.time = node.data.startDate;
				} else if (index == end) { // 끝시간
					if(node.data.endDate > endIndexTime){
						node.time = 0;
					}else{
						node.time = node.data.endDate;
					}
				} else { // 하루종일
					node.time = 0;
				}
				
				addNode(index, node);
			}
			index++;
		}
	}
	
	// 지정한 인덱스에 해당 노드를 순서에 맞춰 삽입
	private void addNode(int index, PlanNode node){
		// 생성된 NodePlan을 테이블에 삽입
		if (nodeTable[index] == null) {
			nodeTable[index] = node;
		} else {
			// TEST CODE
			if( index == 11) {
				int aa= 0;
				aa++;
			}
			
			PlanNode parentNode = nodeTable[index];
			PlanNode tmpNode = parentNode;
			while (tmpNode != null) {
				if (node.time <= tmpNode.time) { // 삽입된 노드가 현재 노드의 시간보다 작을때..
					if (tmpNode == nodeTable[index]) { // 부모가 루트일때..
						nodeTable[index] = node;
						node.next = tmpNode;
					} else {
						parentNode.next = node;
						node.next = tmpNode;
					}
					break;
				} else if (tmpNode.getNext() == null) {
					tmpNode.next = node;
					break;
				}
				parentNode = tmpNode;
				tmpNode = parentNode.getNext();
			}
		}
	}
	
	// NodeTable을 리셋
	private void resetNodeTable(){
		for (int i = 0; i < nodeTable.length; i++) {
			nodeTable[i] = null;
		}
	}
}
