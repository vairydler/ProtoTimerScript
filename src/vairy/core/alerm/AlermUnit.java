package vairy.core.alerm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import vairy.Debug.Debug;
import vairy.core.alerm.param.AlermBean;
import vairy.core.alerm.param.EAlermMode;
import vairy.core.alerm.param.EAlermScriptType;
import vairy.core.alerm.param.AlermTime;
import vairy.core.alerm.param.TimerTime;
import vairy.debug.user.DebugKey;
import vairy.io.FileReader;

/**
 * アラームのデータを除いた本体部分の実装。
 * データは別インスタンスで持つことで、差し替え可能とする。
 * @see AlermBean
 * @author vairydler
 *
 */
public class AlermUnit implements AlermUnit_ForJS {
	private Map<EAlermScriptType, String> scriptmap = new HashMap<EAlermScriptType, String>();
	private AlermBean bean;
	private ArrayList<PropertyChangeListener> propchalistener = new ArrayList<>();

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

		setBean(new AlermBean("", alerm, timer, goal, EAlermMode.TIMER, false, false, new HashMap<EAlermScriptType, String>()));
	}
	public final AlermBean getBean() {
		return bean;
	}
	public void setBean(AlermBean alermbean) {
		if (null != alermbean) {
			AlermBean old = bean;
			bean = alermbean;
			loadScript();
			bean.addPropertyChangeListener(new UpdateListener());
			notifyPropertyChangeListener(new PropertyChangeEvent(this, "Bean", old, bean));
		}
	}
	/* (非 Javadoc)
	 * @see vairy.core.alerm.AlermUnit_ForJS#getDiff()
	 */
	@Override
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
		EAlermMode mode = bean.getMode();
		Calendar now = new GregorianCalendar();
		Boolean drive = true;

		switch (mode) {
		/* 目標時間＝設定時間 */
		case ALERM:
			if(now.before(bean.getAlerm())){
				bean.getGoal().setTime(bean.getAlerm().getTime());

				Debug.println(DebugKey.EDITTIMER, bean.getAlerm().getClass());
				Debug.println(DebugKey.EDITTIMER, bean.getAlerm().getTimeInMillis());
				Debug.println(DebugKey.EDITTIMER, bean.getGoal().getTime());
			}else{
				drive = false;
			}

			break;
		/* 目標時間＝現在時間＋設定時間 */
		case TIMER:
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
		bean.setDrive(drive);
	}
	/**
	 * タイマー停止。
	 */
	public final void stop() {
		bean.setDrive(false);
	}
	/* (非 Javadoc)
	 * @see vairy.core.alerm.AlermUnit_ForJS#getScript(vairy.core.alerm.param.EAlermScriptType)
	 */
	@Override
	public final String getScript(EAlermScriptType type){
		String buff = scriptmap.get(type);
		return (null == buff) ? "" : buff;
	}

	/* (非 Javadoc)
	 * @see vairy.core.alerm.AlermUnit_ForJS#loadScript()
	 */
	@Override
	public final void loadScript(){
		String filepath;
		for (EAlermScriptType type : EAlermScriptType.values()) {
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

	public void addPropertyChangeListener(PropertyChangeListener listener){
		propchalistener.add(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener){
		propchalistener.remove(listener);
	}

	private void notifyPropertyChangeListener(PropertyChangeEvent evt){
		for (PropertyChangeListener listener : propchalistener) {
			listener.propertyChange(evt);
		}
	}

	private class UpdateListener implements PropertyChangeListener{
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			AlermUnit.this.notifyPropertyChangeListener(evt);
		}
	}
}
