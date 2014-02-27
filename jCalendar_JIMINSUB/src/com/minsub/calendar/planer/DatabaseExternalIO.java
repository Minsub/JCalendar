package com.minsub.calendar.planer;

/*
 * Class : DatabaseExport
 * 작성자 : 지민섭 
 * 2011. 01. 13
 * 설명 : 디비의 Plan 테이블의 전체를 외부로 Export
 *       현재 XML의 Import 와 Export를 지원
 *       저장 경로는 절대경로로 설정
 */

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.minsub.calendar.Main;

class DatabaseExternalIO {
	private static DatabaseExternalIO instance = null;
	private static final String START 			= "<";
	private static final String START_CLOSE 	= "</";
	private static final String END			    = ">";
	private static final String EQUAL			= "='";
	private static final String EQUAL_CLOSE 	= "' ";
	
	private static final String XML_PLAN 		= "plan";
	private static final String KEY_SUBJECT 	= "subject";
	private static final String KEY_LOCATION 	= "location";
	private static final String KEY_STARTDATE 	= "startdate";
	private static final String KEY_ENDDATE 	= "enddate";
	private static final String KEY_MEMO 		= "memo";
	private static final String KEY_ALARM 		= "alarm";
	private static final String KEY_REPEAT 		= "repeat";
	private static final String KEY_VIBRATE 	= "vibrate";
	
	

	private BufferedOutputStream _bos;
	private PlanerManager planerMgr = null;
	private File file;

	public static DatabaseExternalIO getInstance(Context context){
		if (instance == null) {
			instance = new DatabaseExternalIO(context);
		}
		return instance;
	}
	
	private DatabaseExternalIO(Context context){
		planerMgr = PlanerManager.getInstance(context);
	}

	// 디비의 Plan 테이블을 XML파일로 Export시킨다
	public boolean backupXML() {
		try{
			File dr = new File(PlanerManager.BACKUP_FILE_PATH);
			if (!dr.exists()) {
				dr.mkdir();
			}
	
			file = new File(PlanerManager.BACKUP_FILE_PATH, PlanerManager.BACKUP_FILE_NAME);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			
			FileOutputStream fOut =  new FileOutputStream(file);
	        _bos = new BufferedOutputStream( fOut );
	
			return writeXML();
		}catch (Exception e) {
			Log.v(Main.TAG,"DatabaseExport : backup Exception - "+e.toString());
			return false;
		}
	}

	// XML파일을 불러들여 디비에 저장한
	public boolean restoreXML() {
		try{
			file = new File(PlanerManager.BACKUP_FILE_PATH, PlanerManager.BACKUP_FILE_NAME);
			if (file.exists()) {
				return readXML();
			} else {
				return false;
			}
		}catch (Exception e) {
			Log.v(Main.TAG,"DatabaseExport : restore Exception - "+e.toString());
			return false;
		}
	}
	
	
	private boolean writeXML() {
		try {
			Cursor cursor = planerMgr.selectAllPlan();
			if (cursor != null && cursor.getCount() > 0) {
				writeStartNoAttr("root");
				cursor.moveToFirst();
				do {
					writeStart(XML_PLAN);
					writeAttr(KEY_SUBJECT, cursor.getString(1));
					writeAttr(KEY_STARTDATE, cursor.getString(2));
					writeAttr(KEY_ENDDATE, cursor.getString(3));
					writeAttr(KEY_LOCATION, cursor.getString(4));
					writeAttr(KEY_MEMO, cursor.getString(5));
					writeAttr(KEY_ALARM, cursor.getString(6));
					writeAttr(KEY_REPEAT, cursor.getString(7));
					writeAttr(KEY_VIBRATE, cursor.getString(8));
					writeEnd();
					writeClose(XML_PLAN);
				} while (cursor.moveToNext());
				close();
				cursor.close();
				Log.d(Main.TAG, "DatabaseExport : success writeXML ");
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			Log.d(Main.TAG,"DatabaseExport : writeXML Exception - "+e.toString());
			return false;
		}
	}
	

	private boolean readXML() {
		try {
			if (file.exists()) {
				planerMgr.deleteAllPlan();
				
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(file);
	
				Element planElemnt = doc.getDocumentElement();
				NodeList planItems = planElemnt.getElementsByTagName(XML_PLAN);
	
				// get plans
				for (int planCnt = 0; planCnt < planItems.getLength(); planCnt++) {
					Node item = planItems.item(planCnt);
	
					//getAttr
					long startDate=0, endDate=0;
					String subject="", location="", memo="";
					int alarm=0, repeat=0, vibrate=0;

					NamedNodeMap planAttrs = item.getAttributes();
					for (int planAttrCnt = 0; planAttrCnt < planAttrs.getLength(); planAttrCnt++) {
						Node attr_user = planAttrs.item(planAttrCnt);
						if (attr_user.getNodeName().equals(KEY_SUBJECT)) {
							subject = attr_user.getNodeValue();
						} else if (attr_user.getNodeName().equals(KEY_STARTDATE)) {
							startDate = Long.valueOf(attr_user.getNodeValue());
						} else if (attr_user.getNodeName().equals(KEY_ENDDATE)) {
							endDate = Long.valueOf(attr_user.getNodeValue());
						} else if (attr_user.getNodeName().equals(KEY_LOCATION)) {
							location = attr_user.getNodeValue();
						} else if (attr_user.getNodeName().equals(KEY_MEMO)) {
							memo = attr_user.getNodeValue();
						} else if (attr_user.getNodeName().equals(KEY_ALARM)) {
							alarm = Integer.valueOf(attr_user.getNodeValue());
						} else if (attr_user.getNodeName().equals(KEY_REPEAT)) {
							repeat = Integer.valueOf(attr_user.getNodeValue());
						} else if (attr_user.getNodeName().equals(KEY_VIBRATE)) {
							vibrate = Integer.valueOf(attr_user.getNodeValue());
						} 
					}
					planerMgr.insertPlan(subject, startDate, endDate, location, memo, alarm, repeat, vibrate);
				}
				Log.v(Main.TAG,"DatabaseExport : success readXML ");
				return true;
			}
		} catch (Exception e) {
			Log.v(Main.TAG,"DatabaseExport : readXML Exception - "+e.toString());
			return false;
		}
		return false;
	}
	
	private void close() throws IOException {
		if (_bos != null) {
			writeClose("root");
			_bos.close();
		}
	}

	private void writeStartNoAttr(String element) throws IOException {
		String str = START + element + END;
		_bos.write(str.getBytes());
	}

	private void writeStart(String element) throws IOException {
		String str = START + element + " ";
		_bos.write(str.getBytes());
	}

	private void writeClose(String element) throws IOException {
		String str = START_CLOSE + element + END;
		_bos.write(str.getBytes());
	}

	private void writeEnd() throws IOException {
		String str = END;
		_bos.write(str.getBytes());
	}

	private void writeAttr(String attr, String value) throws IOException {
		String str = attr + EQUAL + value + EQUAL_CLOSE;
		_bos.write(str.getBytes());
	}
}