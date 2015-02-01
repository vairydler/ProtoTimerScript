package vairy.core.alerm.param;

public enum EAlermMode {
	TIMER("タイマー"),
	ALERM("アラーム")

	;
	public final String dispText;
	private EAlermMode(final String text) {
		dispText = text;
	}

}
