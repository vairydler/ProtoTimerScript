package vairy.alerm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import vairy.Debug.Debug;
import vairy.alerm.param.AlermBean;
import vairy.alerm.param.AlermMode;
import vairy.alerm.param.AlermScriptType;
import vairy.alerm.param.AlermTime;
import vairy.alerm.param.TimerTime;
import vairy.debug.user.DebugKey;
import vairy.fileacces.FileReader;

/**
 * アラームのデータを除いた本体部分の実装。
 * データは別インスタンスで持つことで、差し替え可能とする。
 * @see AlermBean
 * @author vairydler
 *
 */
public class AlermUnit {
	private Map<AlermScriptType, String> scriptmap = new HashMap<AlermScriptType, String>();
	private AlermBean bean;
	public AlermUnit() {
		AlermTime alerm;
		TimerTime timer;
		GregorianCalendar goal;

		alerm = new AlermTime();
		alerm.setTimeInMillis(0);
		timer = new TimerTime();
		timer.setTimeInMillis(0);
		goal = new GregorianCalendar();
		goal.setTimeInMillis(0);

		bean = new AlermBean("", alerm, timer, goal, AlermMode.TIMER, false, false, new HashMap<AlermScriptType, String>());
	}
	public final AlermBean getBean() {
		return bean;
	}
	public void setBean(AlermBean alermbean) {
		if (null != alermbean) {
			bean = alermbean;
			loadScript();
		}
	}
	/**
	 * 現在時刻と設定時間の差を取得。
	 * @return
	 */
	public final Calendar getDiff() {
		Calendar rtn = new TimerTime();
		long TimeInMillis = bean.getGoal().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

		rtn.setTimeInMillis(TimeInMillis);
		return rtn;
	}
	/**
	 * タイマー開始。
	 */
	public final void start() {
		AlermMode mode;
		mode = bean.getMode();

		switch (mode) {
		/* 目標時間＝設定時間 */
		case ALERM:
			bean.getGoal().setTime(bean.getAlerm().getTime());
			break;
		/* 目標時間＝現在時間＋設定時間 */
		case TIMER:
			Calendar now = new GregorianCalendar();
			bean.getGoal().setTimeInMillis(now.getTimeInMillis() + bean.getTimer().getTimeInMillis());

			Debug.println(DebugKey.EDITTIMER, bean.getTimer().getClass());
			Debug.println(DebugKey.EDITTIMER, now.getTimeInMillis());
			Debug.println(DebugKey.EDITTIMER, bean.getTimer().getTimeInMillis());
			Debug.println(DebugKey.EDITTIMER, now.getTimeInMillis() + bean.getTimer().getTimeInMillis());
			Debug.println(DebugKey.EDITTIMER, bean.getGoal().getTime());
			break;
		default:
			break;
		}
		bean.setDrive(true);
	}
	/**
	 * タイマー停止。
	 */
	public final void stop() {
		bean.setDrive(false);
	}
	/**
	 * タイミングに応じたスクリプトの取得。
	 * @param type 対象のスクリプトタイミング
	 * @return スクリプト
	 */
	public final String getScript(AlermScriptType type){
		String buff = scriptmap.get(type);
		return (null == buff) ? "" : buff;
	}

	/**
	 * スクリプトファイルからスクリプトを読み込んで、マップに追加する。
	 */
	public final void loadScript(){
		String filepath;
		for (AlermScriptType type : AlermScriptType.values()) {
			filepath = bean.getScriptmap().get(type);

			scriptmap.put(type, readScript(filepath));
		}
	}
	/**
	 * スクリプトファイルからスクリプトを読み込む。
	 * @param filepath スクリプトファイルパス
	 * @return 読み込んだスクリプト。
	 */
	private String readScript(String filepath) {
		StringBuilder rtn = new StringBuilder();
		String readbuff;
		FileReader fr;

		if (null != filepath) {
			if(new File(filepath).exists()){
				try {
					fr = new FileReader(filepath);
					while( null != (readbuff = fr.ReadLine())){
						rtn.append(readbuff);
					}
				} catch (FileNotFoundException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				} catch (NullPointerException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
		}
		return rtn.toString();
	}
}
