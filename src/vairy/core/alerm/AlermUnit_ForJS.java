package vairy.core.alerm;

import java.util.Calendar;

import vairy.core.alerm.param.AlermBean;
import vairy.core.alerm.param.EAlermScriptType;

public interface AlermUnit_ForJS {

	/**
	 * 現在時刻と設定時間の差を取得。
	 * @return
	 */
	public Calendar getDiff();

	/**
	 * タイミングに応じたスクリプトの取得。
	 * @param type 対象のスクリプトタイミング
	 * @return スクリプト
	 */
	public String getScript(EAlermScriptType type);

	/**
	 * スクリプトファイルからスクリプトを読み込んで、マップに追加する。
	 */
	public void loadScript();

}