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
		this.name = name;
	}

	public AlermTime getAlerm() {
		return (AlermTime) alerm;
	}

	public void setAlerm(AlermTime alerm) {
		this.alerm = alerm;
	}

	public TimerTime getTimer() {
		return (TimerTime) timer;
	}

	public void setTimer(TimerTime timer) {
		this.timer = timer;
	}

	public Calendar getGoal() {
		return goal;
	}

	public void setGoal(Calendar goal) {
		this.goal = goal;
	}

	public EAlermMode getMode() {
		return mode;
	}

	public void setMode(EAlermMode mode) {
		this.mode = mode;
	}

	public Boolean isDrive() {
		return drive;
	}

	public void setDrive(Boolean drive) {
		this.drive = drive;
	}

	public Boolean isTimer_repeat() {
		return timer_repeat;
	}

	public void setTimer_repeat(Boolean timer_repeat) {
		this.timer_repeat = timer_repeat;
	}

	public Map<EAlermScriptType, String> getScriptmap() {
		return scriptfilemap;
	}

	public void setScriptmap(Map<EAlermScriptType, String> scriptmap) {
		this.scriptfilemap = scriptmap;

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