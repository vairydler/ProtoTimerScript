﻿package vairy.alerm;

import java.util.ArrayList;

import vairy.alerm.param.AlermBean;
import vairy.alerm.param.AlermBeans;
import vairy.app.AppIF_ForJS;
import vairy.fileaccess.user.AlermAccecor;
import vairy.resorce.SystemConst;
import variy.timer.CycleTimer;

public class AlermManager implements SystemConst,DeleteListener{
	private ArrayList<Alerm> alermlist = new ArrayList<Alerm>();
	private AppIF_ForJS api = new AppIF_ForJS(this);
	public Integer alermsize(){
		return alermlist.size();
	}
	/**
	 * アラームを設定する。
	 * @param alerm 設定対象のアラーム。
	 */
	public void setAlerm(Alerm alerm){
		alermlist.add(alerm);
		alerm.addDeleteListener(this);
		CycleTimer.addListener(alerm);
	}
	/**
	 * インデックス基準でアラームを検索して取得する。
	 * @param index 検索対象のインデックス。
	 * @return 見つけたアラーム名。なかったらnull
	 */
	public Alerm getAlerm(Integer index) {
		Alerm rtn = null;

		if (alermlist.size() > index) {
			rtn = alermlist.get(index);
		}

		return rtn;
	}
	/**
	 * 名前基準でアラームを検索して取得する。おんなじ名前がいっぱいあった場合は最初に見つけたやつ。
	 * @param name 検索対象のアラーム名
	 * @return 見つけたアラーム名。なかったらnull
	 */
	public Alerm getAlerm(final String name) {
		Alerm rtn = null;

		for (Alerm element : alermlist) {
			if (name.equals(element.getUnit().getBean().getName())) {
				rtn = element;
				break;
			}
		}

		return rtn;
	}
	/**
	 * アラームを削除。
	 * @param index 削除対象のインデックス。
	 * @return 削除の成否。
	 */
	public Boolean removeAlerm(Integer index) {
		return removeAlerm(alermlist.get(index));
	}
	/**
	 * アラームを削除。
	 * @param obj 削除対象のアラーム。
	 * @return 削除の成否。
	 */
	public Boolean removeAlerm(Alerm obj) {
		Boolean rtn = false;
		if(null != obj){
			CycleTimer.removeListener(obj);
			rtn = alermlist.remove(obj);
		}
		return rtn;
	}
	/**
	 * リストのクリア。
	 */
	public void clear() {
		while(true != alermlist.isEmpty())
		{
			removeAlerm(alermlist.get(0));
		}
	}

	/**
	 * パラメータを元に、アラームを生成・追加する。
	 * クリアせずに追加。
	 * @param bean パラメータ
	 */
	public void addAlermFromBean(AlermBean bean){
		AlermUnit unit;
		unit = new AlermUnit();
		unit.setBean(bean);
		setAlerm(new Alerm(unit,api));
	}

	/**
	 * パラメータを元に、アラームを生成・追加する。
	 * クリアせずに追加。
	 * @param beans パラメータ
	 */
	public void addAlermFromBeans(AlermBeans beans){
		for (AlermBean alermbean : beans.getBeans()) {
			addAlermFromBean(alermbean);
		}
	}
	/**
	 * パラメータを元に、アラームを生成・設定する。
	 * クリアしてからの設定。
	 * @param beans パラメータ
	 */
	public void setAlermFromBeans(AlermBeans beans){
		clear();
		addAlermFromBeans(beans);
	}
	/**
	 * パラメータを取得する。ぶっちゃけセーブ用。
	 * @return パラメータ。
	 */
	public AlermBeans getAlermBeans(){
		AlermBeans rtn = new AlermBeans();

		for (Alerm alerm : alermlist) {
			rtn.getBeans().add(alerm.getUnit().getBean());
		}

		return rtn;
	}
	/**
	 * アラームを書き込む。
	 */
	public void saveAlerm() {
		AlermAccecor alermreader;

		alermreader = new AlermAccecor(ALERMSETFILE, AlermBeans.class );

		alermreader.writeFile(getAlermBeans());
	}
	/**
	 * アラームを読み込む。既存のアラームは全て破棄して登録する。
	 */
	public void loadAlerm() {
		AlermBeans beans;
		AlermAccecor alermreader;

		alermreader = new AlermAccecor(ALERMSETFILE, AlermBeans.class );
		beans = alermreader.readFile();

		if(null != beans){
			setAlermFromBeans(beans);
		}
	}

	/**
	 * 管理中のアラームにプログラムの開始を通知する。
	 */
	public void notifyProgStart() {
		for (Alerm element : alermlist) {
			element.notifyProgramStart();
		}
	}
	/**
	 * 管理中のアラームにプログラムの終了を通知する。
	 */
	public void notifyProgEnd() {
		for (Alerm element : alermlist) {
			element.notifyProgramEnd();
		}
	}

	/**
	 * 管理中のアラームのスクリプトを読み直す。
	 */
	public void loadScript() {
		for (Alerm element : alermlist) {
			element.getUnit().loadScript();
		}
	}
	@Override
	public void Delete(Object sorce) {
		if(sorce.getClass() == Alerm.class){
			removeAlerm((Alerm)sorce);
		}
	}
}
