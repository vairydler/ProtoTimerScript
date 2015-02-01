package vairy.core.alerm.param;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AlermBean implements Cloneable{
	private String name = "";
	private Calendar alerm = new AlermTime();
	private Calendar timer = new TimerTime();
	private Calendar goal = Calendar.getInstance();
	private EAlermMode mode = EAlermMode.ALERM;
	private Boolean drive = false;
	private Boolean timer_repeat = false;
	private Map<EAlermScriptType, String> scriptfilemap = new HashMap<EAlermScriptType, String>();
	private ArrayList<PropertyChangeListener> propchalistener = new ArrayList<>();

	public AlermBean(){
		timer.setTimeInMillis(0);
		goal.setTimeInMillis(0);
	}
	public AlermBean(String name, GregorianCalendar alerm, GregorianCalendar timer,
			GregorianCalendar goal, EAlermMode mode, Boolean drive, Boolean timer_repeat,
			Map<EAlermScriptType, String> scriptmap) {
		this.name = name;
		this.alerm = alerm;
		this.timer = timer;
		this.goal = goal;
		this.mode = mode;
		this.drive = drive;
		this.timer_repeat = timer_repeat;
		this.scriptfilemap = scriptmap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String old = this.name;
		this.name = name;
		notifyPropertyChangeListener(new PropertyChangeEvent(this, "Name", old, name));
	}

	public AlermTime getAlerm() {
		return (AlermTime) alerm;
	}

	public void setAlerm(AlermTime alerm) {
		Calendar old = this.alerm;
		this.alerm = alerm;
		notifyPropertyChangeListener(new PropertyChangeEvent(this, "Alerm", old, alerm));
	}

	public TimerTime getTimer() {
		return (TimerTime) timer;
	}

	public void setTimer(TimerTime timer) {
		Calendar old = this.timer;
		this.timer = timer;
		notifyPropertyChangeListener(new PropertyChangeEvent(this, "Timer", old, timer));
	}

	public Calendar getGoal() {
		return goal;
	}

	public void setGoal(Calendar goal) {
		Calendar old = this.goal;
		this.goal = goal;
		notifyPropertyChangeListener(new PropertyChangeEvent(this, "Goal", old, goal));
	}

	public EAlermMode getMode() {
		return mode;
	}

	public void setMode(EAlermMode mode) {
		EAlermMode old = this.mode;
		this.mode = mode;
		notifyPropertyChangeListener(new PropertyChangeEvent(this, "Mode", old, mode));
	}

	public Boolean isDrive() {
		return drive;
	}

	public void setDrive(Boolean drive) {
		Boolean old = this.drive;
		this.drive = drive;
		notifyPropertyChangeListener(new PropertyChangeEvent(this, "Drive", old, drive));
	}

	public Boolean isTimer_repeat() {
		return timer_repeat;
	}

	public void setTimer_repeat(Boolean timer_repeat) {
		Boolean old = this.timer_repeat;
		this.timer_repeat = timer_repeat;
		notifyPropertyChangeListener(new PropertyChangeEvent(this, "Timer_repeat", old, timer_repeat));
	}

	public Map<EAlermScriptType, String> getScriptmap() {
		return scriptfilemap;
	}

	public void setScriptmap(Map<EAlermScriptType, String> scriptmap) {
		this.scriptfilemap = scriptmap;

	}

	public void addPropertyChangeListener(PropertyChangeListener listener){
		propchalistener.add(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener){
		propchalistener.remove(listener);
	}

	private void notifyPropertyChangeListener(PropertyChangeEvent e){
		for (PropertyChangeListener listener : propchalistener) {
			listener.propertyChange(e);
		}
	}

	public AlermBean createClone(){
		AlermBean rtn;
		try {
			rtn = (AlermBean)this.clone();
			rtn.postClone();
		} catch (CloneNotSupportedException e) {
			rtn = new AlermBean();
			e.printStackTrace();
		}

		return rtn;
	}
	private void postClone() {
		Calendar instance;

		instance = new TimerTime();
		instance.setTimeInMillis(this.timer.getTimeInMillis());
		this.timer = instance;

		instance = new AlermTime();
		instance.setTimeInMillis(this.alerm.getTimeInMillis());
		this.alerm = instance;

		instance = Calendar.getInstance();
		instance.setTimeInMillis(this.goal.getTimeInMillis());
		this.goal = instance;

		Map<EAlermScriptType, String> scriptfilemap2 = this.scriptfilemap;
		this.scriptfilemap = new HashMap<EAlermScriptType, String>();
		this.scriptfilemap.putAll(scriptfilemap2);
	}


}