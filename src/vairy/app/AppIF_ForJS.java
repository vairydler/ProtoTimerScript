package vairy.app;

import vairy.alerm.Alerm;
import vairy.alerm.AlermManager;
import vairy.alerm.AlermUnit;
import vairy.alerm.param.AlermBean;

public class AppIF_ForJS {
	private AlermManager manager = null;
	public AppIF_ForJS(final AlermManager manager) {
		if(null != manager){
			this.manager = manager;
		}
	}

	/**
	 * 名前基準でアラームを検索して取得する。おんなじ名前がいっぱいあった場合は最初のやつ。
	 * @param alermname 検索対象のアラーム名
	 * @return 見つけたアラーム名。なかったらnull
	 */
	public AlermUnit getAlermUnit(final String alermname){
		Alerm temp = manager.getAlerm(alermname);
		return (null != temp) ? temp.getUnit():null;
	}
	/**
	 * インデックス基準でアラームを検索して取得する。
	 * @param alermnum 検索対象のインデックス
	 * @return 見つけたアラーム名。なかったらnull
	 */
	public AlermUnit getAlermUnit(final Integer alermnum){
		Alerm temp = manager.getAlerm(alermnum);
		return (null != temp) ? temp.getUnit():null;
	}

	/**
	 * アラームを削除。
	 * @param alermname 削除対象のアラーム名。
	 * @return 削除の成否。
	 */
	public boolean removeAlerm(final String alermname){
		Alerm temp = manager.getAlerm(alermname);
		return manager.removeAlerm(temp);
	}
	/**
	 * アラームを削除。
	 * @param alermnum 削除対象のインデックス。
	 * @return 削除の成否。
	 */
	public boolean removeAlerm(final Integer alermnum){
		Alerm temp = manager.getAlerm(alermnum);
		return manager.removeAlerm(temp);
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
}
