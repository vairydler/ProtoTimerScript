package vairy.alerm.param;

public enum AlermScriptType {
	PROGRAMSTART("プログラム起動時"),
	TIMERSTART("タイマー動作開始時"),
	TIMEREXEC("タイマー満了時"),
	TIMERSTOP("タイマー動作停止時"),
	PROGRAMEND("プログラム終了時"),

	;
	public final String dispText;
	private AlermScriptType(final String text) {
		dispText = text;
	}
}
