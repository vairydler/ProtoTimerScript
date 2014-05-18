package vairy.alerm;

import java.util.HashSet;
import java.util.Set;

import javax.naming.Binding;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import vairy.Debug.Debug;
import vairy.alerm.param.AlermMode;
import vairy.alerm.param.AlermScriptType;
import vairy.app.AppIF_ForJS;
import vairy.debug.user.DebugKey;
import vairy.resorce.SystemConst;
import vairy.script.variable.JScriptVarFactory;
import variy.timer.TimerRunnable;

/**
 *
 * @author vairydler
 *
 */
public class Alerm implements Runnable,TimerRunnable,SystemConst{
	private AlermUnit unit;
	private ScriptEngine se;
	private Set<DeleteListener> dellistener = new HashSet<DeleteListener>();

	public Alerm(final AlermUnit unit,final AppIF_ForJS api) {
		JScriptVarFactory varfactory;

		setUnit(unit);
		se = new ScriptEngineManager().getEngineByName("JavaScript");

		if (null != se ) {
			varfactory = new JScriptVarFactory(se);
			Bindings bindings = se.getBindings(ScriptContext.GLOBAL_SCOPE);
			bindings.put("vf", varfactory);
			bindings.put("me", this);
			bindings.put("api", api);

		}else{
			Debug.StackTraceprintln(DebugKey.ALERM, "JavaScript未対応のソフトでは動きません。");
			System.exit(-1);
		}
	}

	public void setUnit(final AlermUnit unit){
		this.unit = unit;
	}
	public final AlermUnit getUnit() {
		return unit;
	}
	public void addDeleteListener(DeleteListener listener){
		this.dellistener.add(listener);
	}
	public void removeDeleteListener(DeleteListener listener){
		this.dellistener.remove(listener);
	}

	public void Delete(){
		for (DeleteListener listener : dellistener) {
			listener.Delete(this);
		}
	}
	/**
	 * プログラム起動時の通知を受け取る。
	 */
	public void notifyProgramStart(){
		eval(AlermScriptType.PROGRAMSTART);
	}
	/**
	 * プログラム終了時の通知を受け取る。
	 */
	public void notifyProgramEnd(){
		eval(AlermScriptType.PROGRAMEND);
	}
	/**
	 * タイマー起動時の通知を受け取る。
	 */
	public void notifyTimerStart(){
		eval(AlermScriptType.TIMERSTART);
		unit.start();
	}
	/**
	 * タイマー停止時の通知を受け取る。
	 */
	public void notifyTimerStop(){
		unit.stop();
		eval(AlermScriptType.TIMERSTOP);
	}

	/**
	 * ローカル向けのeval。Exception対策がめんどくさいのでラップ。
	 * @param type スクリプトタイプ。
	 */
	private void eval(AlermScriptType type){
		try {
			se.eval(unit.getScript(type));
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void timerrun() {
		if(unit.getBean().isDrive()){
			Thread t = new Thread(this);
			t.run();
			t = null;
		}
	}
	@Override
	public synchronized void run() {
		/* 時間過ぎた */
		if (unit.getDiff().getTimeInMillis() <= 0) {
			eval(AlermScriptType.TIMEREXEC);
			unit.stop();

			chkRepeat();
		}
	}

	/**
	 * タイマーを繰り返し処理するか判定する。
	 * TODO 同期してないけど大丈夫やろか・・。
	 */
	private void chkRepeat(){
		/* 繰り返し処理の場合は再設定する。 */
		if (AlermMode.TIMER == unit.getBean().getMode()) {
			if (unit.getBean().isTimer_repeat()){
				unit.start();
			}
		}
	}
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		Delete();
	}
}
