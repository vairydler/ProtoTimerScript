package vairy.main;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Calendar;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import vairy.Debug.Debug;
import vairy.core.app.AppMain;
import vairy.debug.user.DebugKey;
import vairy.debug.user.GuiDebug;
import vairy.main.resorce.SystemConst;
import vairy.script.variable.JScriptVarFactory;
import vairy.ui.cui.Console;
import vairy.ui.gui.MainAlermFrame;
import variy.timer.CycleTimer;

public class Main implements SystemConst{
	public static void main(String[] args) {
		Debug.setDebug(DebugKey.ALERM, true);
		Debug.setDebug(DebugKey.EDITTIMER, true);
		Debug.setDebug(GuiDebug.CALENDARBAR, false);

		AppMain appMain = AppMain.getInstance();
		appMain.startinit();

		/* タイマー回すのはイニットの読み込み終わってからネ！ */
		CycleTimer.setCycleunit(TIMERCYCLE);
		CycleTimer.startTimer();

		/* ユーザーインターフェイス */
		MainAlermFrame jframe = new MainAlermFrame(appMain);
		CycleTimer.addListener(jframe);

		jframe.setVisible(true);

		Console console = new Console(appMain);
		console.main();

		System.exit(0);
		return;
	}

	private static void test_script(){
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine se = sem.getEngineByName("js");
		se.put("vf", new JScriptVarFactory(se));

		FileInputStream fis;
		try {
			fis = new FileInputStream("C:\\TempDrive\\eclipse workspace\\TimerScript\\userscript\\teststart.js");
			InputStreamReader isr = new InputStreamReader(fis);
			se.eval(isr);
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ScriptException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}
