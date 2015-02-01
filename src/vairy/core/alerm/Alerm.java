package vairy.core.alerm;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.naming.Binding;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import vairy.Debug.Debug;
import vairy.core.alerm.param.EAlermMode;
import vairy.core.alerm.param.EAlermScriptType;
import vairy.core.app.AppIF_ForJS;
import vairy.debug.user.DebugKey;
import vairy.main.resorce.SystemConst;
import vairy.script.engine.VScriptEngineFactory;
import vairy.script.engine.VScriptEngineFactory.CreateEngineType;
import vairy.script.variable.JScriptVarFactory;
import variy.timer.TimerRunnable;

/**
 *
 * @author vairydler
 *
 */
public class Alerm implements Runnable,TimerRunnable,SystemConst, Alerm_ForJS{
	private AlermUnit unit;
	private ScriptEngine se;
	private Set<DeleteListener> dellistener = new HashSet<DeleteListener>();

	public Alerm(final AlermUnit unit,final AppIF_ForJS api) {
		setUnit(unit);
		se = VScriptEngineFactory.getEngineByName("JavaScript", CreateEngineType.ROBOT);

		if (null != se ) {
			Bindings bindings = se.getBindings(ScriptContext.GLOBAL_SCOPE);

			bindings.put("me", this);
			bindings.put("api", api);

		}else{
			Debug.StackTraceprintln(DebugKey.ALERM, "JavaScript未対応のソフトでは動きません。");
			System.exit(-1);
		}
	}

	/* (非 Javadoc)
	 * @see vairy.core.alerm.Alerm_ForJS#setUnit(vairy.core.alerm.AlermUnit)
	 */
	@Override
	public void setUnit(final AlermUnit unit){
		this.unit = unit;
		/** TODO PropertyChangeをアラームマネージャまで伝える。 */
	}
	/* (非 Javadoc)
	 * @see vairy.core.alerm.Alerm_ForJS#getUnit()
	 */
	@Override
	public final AlermUnit getUnit() {
		return unit;
	}
	public void addDeleteListener(DeleteListener listener){
		this.dellistener.add(listener);
	}
	public void removeDeleteListener(DeleteListener listener){
		this.dellistener.remove(listener);
	}
	private void notifyDeleteListener(){
		for (DeleteListener listener : this.dellistener) {
			listener.Delete(this);
		}
	}

	/* (非 Javadoc)
	 * @see vairy.core.alerm.Alerm_ForJS#Delete()
	 */
	@Override
	public void Delete(){
		notifyDeleteListener();
	}
	/**
	 * プログラム起動時の通知を受け取る。
	 */
	public void notifyProgramStart(){
		eval(EAlermScriptType.PROGRAMSTART);
	}
	/**
	 * プログラム終了時の通知を受け取る。
	 */
	public void notifyProgramEnd(){
		eval(EAlermScriptType.PROGRAMEND);
	}
	/* (非 Javadoc)
	 * @see vairy.core.alerm.Alerm_ForJS#notifyTimerStart()
	 */
	@Override
	public void notifyTimerStart(){
		eval(EAlermScriptType.TIMERSTART);
		unit.start();
	}
	/* (非 Javadoc)
	 * @see vairy.core.alerm.Alerm_ForJS#notifyTimerStop()
	 */
	@Override
	public void notifyTimerStop(){
		unit.stop();
		eval(EAlermScriptType.TIMERSTOP);
	}

	/**
	 * ローカル向けのeval。Exception対策がめんどくさいのでラップ。
	 * @param type スクリプトタイプ。
	 */
	private void eval(EAlermScriptType type){
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
			eval(EAlermScriptType.TIMEREXEC);
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
		if (EAlermMode.TIMER == unit.getBean().getMode()) {
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
