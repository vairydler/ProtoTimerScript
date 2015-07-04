package vairy.core.app;

import vairy.core.alerm.Alerm;
import vairy.core.alerm.AlermManager;
import vairy.core.alerm.AlermUnit_ForJS;
import vairy.core.alerm.Alerm_ForJS;
import vairy.core.alerm.param.AlermBean;

public class AppIF_ForJS {
	private AlermManager manager = null;
	public AppIF_ForJS(final AlermManager manager) {
		if(null != manager){
			this.manager = manager;
		}
	}

	/**
	 * 名前基準でアラームを検索してインデックスを取得する。おんなじ名前がいっぱいあった場合は最初に見つけたやつ。
	 * @param name 検索対象のアラーム名
	 * @return 見つけたアラームインデックス。なかったらnull
	 */
	public Integer getAlermIndex(final String alermname){
		return this.manager.getAlermIndex(alermname);
	}

	/**
	 * インデックス基準でアラームを検索して取得する。
	 * @param alermnum 検索対象のインデックス
	 * @return 見つけたアラーム名。なかったらnull
	 */
	public AlermUnit_ForJS getAlermUnit(final Integer alermnum){
		Alerm_ForJS temp = manager.getAlerm(alermnum);
		return (null != temp) ? temp.getUnit():null;
	}

	/**
	 * アラームを削除。
	 * @param alermnum 削除対象のインデックス。
	 * @return 削除の成否。
	 */
	public boolean removeAlerm(final Integer alermnum){
		Alerm_ForJS temp = manager.getAlerm(alermnum);
		Boolean rtn = (null != temp);
		if(rtn){
			temp.Delete();
		}
		return rtn;
	}

	/**
	 * パラメータを取得。
	 * @return パラメータ。
	 */
	public AlermBean createBean(){
		return new AlermBean();
	}
	/**
	 * パラメータを元にアラームを追加。
	 * @param bean パラメータ
	 */
	public void addAlerm(AlermBean bean){
		manager.addAlermFromBean(bean);
	}

	/**
	 * アラームの運転停止を設定する。
	 * @param alermnum
	 * @param drive
	 */
	public void setDriveAlerm(final Integer alermnum, Boolean drive){
		Alerm alerm = manager.getAlerm(alermnum);
		if (drive) {
			alerm.notifyTimerStart();
		}else{
			alerm.notifyTimerStop();;
		}
	}
}
