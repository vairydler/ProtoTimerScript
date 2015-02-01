package vairy.core.alerm;

public interface Alerm_ForJS {

	public void setUnit(AlermUnit unit);

	public AlermUnit getUnit();

	public void Delete();

	/**
	 * タイマー起動時の通知を受け取る。
	 */
	public void notifyTimerStart();

	/**
	 * タイマー停止時の通知を受け取る。
	 */
	public void notifyTimerStop();

}