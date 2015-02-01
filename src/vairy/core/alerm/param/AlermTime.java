package vairy.core.alerm.param;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlermTime extends GregorianCalendar {

	/**
	 *
	 */
	private static final long serialVersionUID = 8257412798082452277L;

	public AlermTime() {
		super();
	}

	public AlermTime(long millis){
		super();
		this.setTimeInMillis(millis);
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
