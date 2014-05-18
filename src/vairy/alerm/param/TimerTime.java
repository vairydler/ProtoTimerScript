package vairy.alerm.param;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimerTime extends GregorianCalendar {

	/**
	 *
	 */
	private static final long serialVersionUID = -5907907471704363064L;

	public TimerTime() {
		super(TimeZone.getTimeZone("Etc/GMT0"));
		this.setTimeInMillis(0);
	}
	/**
	 * 月の値を1オリジンにしただけのset
	 * @param field
	 * @param value
	 */
	public void setRealValue(int field, int value) {
		if(Calendar.MONTH == field){
			value = value-1;
		}
		super.set(field, value);
	}
	/**
	 * 月の値を1オリジンにしただけのget
	 * @param field
	 * @param value
	 */
	public int getRealValue(int field) {
		int rtn = super.get(field);
		if(Calendar.MONTH == field){
			rtn = rtn+1;
		}
		return rtn;
	}
}
