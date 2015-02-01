package vairy.core.alerm.param;

public enum EAlermScriptType {
	PROGRAMSTART("プログラム起動時"),
	TIMERSTART("タイマー動作開始時"),
	TIMEREXEC("タイマー満了時"),
	TIMERSTOP("タイマー動作停止時"),
	PROGRAMEND("プログラム終了時"),

	;
	public final String dispText;
	private EAlermScriptType(final String text) {
		dispText = text;
	}
}
