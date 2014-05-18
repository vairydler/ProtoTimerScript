package vairy.alerm.param;

public enum AlermMode {
	TIMER("タイマー"),
	ALERM("アラーム")

	;
	public final String dispText;
	private AlermMode(final String text) {
		dispText = text;
	}

}
