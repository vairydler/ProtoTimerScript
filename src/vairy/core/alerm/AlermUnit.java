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
import java.util.List;
import java.util.Map;

import vairy.debug.Debug;
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
public class AlermUnit implements AlermUnit_ForJS,Cloneable {
	private AlermUnit prevalue = null;
	private Map<EAlermScriptType, String> scriptmap = new HashMap<EAlermScriptType, String>();
	private AlermBean bean;
	private List<PropertyChangeListener> propchalistener = new ArrayList<>();

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
			bean = alermbean;
			loadScript();
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
					fr = new FileReader(filepath,"UTF-8");
					while( null != (readbuff = fr.ReadLine())){
						rtn.append(readbuff + "\r\n");
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

	private AlermUnit createClone(){
		AlermUnit rtn;
		try {
			rtn = (AlermUnit) this.clone();
			rtn.postClone();
		} catch (CloneNotSupportedException e) {
			rtn = new AlermUnit();
			e.printStackTrace();
		}
		return rtn;
	}

	private void postClone(){
		Debug.println(DebugKey.CYCLE, "postClone");
		this.setBean(this.getBean().createClone());

		Map<EAlermScriptType, String> hashMap = new HashMap<>();
		hashMap.putAll(this.scriptmap);
		this.scriptmap = hashMap;

		List<PropertyChangeListener> propchalistener2 = new ArrayList<>();
		propchalistener2.addAll(this.propchalistener);
		this.propchalistener = propchalistener2;

		if(null != this.prevalue){
			this.prevalue = this.prevalue.createClone();
			this.prevalue.prevalue = null;
		}
	}

	public void chkPropChng(){
		if(null != this.prevalue){
			AlermBean prebean = prevalue.getBean();
			AlermBean nowbean = this.getBean();

			if(!prebean.getName().equals(nowbean.getName())){
				notifyPropertyChangeListener(new PropertyChangeEvent(this, "Name", prebean.getName(), nowbean.getName()));
			}

			if(!prebean.getMode().equals(nowbean.getMode())){
				notifyPropertyChangeListener(new PropertyChangeEvent(this, "Mode", prebean.getMode(), nowbean.getMode()));
			}

			if(!(0 == prebean.getAlerm().compareTo(nowbean.getAlerm()))){
				notifyPropertyChangeListener(new PropertyChangeEvent(this, "Alerm", prebean.getAlerm(), nowbean.getAlerm()));
			}

			if(!(0 == prebean.getTimer().compareTo(nowbean.getTimer()))){
				notifyPropertyChangeListener(new PropertyChangeEvent(this, "Timer", prebean.getTimer(), nowbean.getTimer()));
			}

			if(!(prebean.isDrive().equals(nowbean.isDrive()))){
				notifyPropertyChangeListener(new PropertyChangeEvent(this, "Drive", prebean.isDrive(), nowbean.isDrive()));
			}

			if(!(prebean.isTimer_repeat().equals(nowbean.isTimer_repeat()))){
				notifyPropertyChangeListener(new PropertyChangeEvent(this, "Repeat", prebean.isTimer_repeat(), nowbean.isTimer_repeat()));
			}

			if(!prebean.getScriptmap().equals(nowbean.getScriptmap())){
				notifyPropertyChangeListener(new PropertyChangeEvent(this, "ScriptPath", prebean.getScriptmap(), nowbean.getScriptmap()));
			}

			if(!prevalue.scriptmap.equals(this.scriptmap)){
				notifyPropertyChangeListener(new PropertyChangeEvent(this, "ScriptValue", prebean.getScriptmap(), nowbean.getScriptmap()));
			}
		}

		prevalue = createClone();
	}
}
