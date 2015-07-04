package vairy.main.resorce;

public interface SystemConst {
	public final Integer TIMERCYCLE = 100;
	public final Long WATCHDOG = (long) (TIMERCYCLE * 100);
	public final String ALERMSETFILE = "./userdata/ALERMSET.json";
}
