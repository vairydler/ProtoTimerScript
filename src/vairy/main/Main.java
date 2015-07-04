package vairy.main;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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

import vairy.core.app.AppMain;
import vairy.debug.Debug;
import vairy.debug.user.DebugKey;
import vairy.debug.user.GuiDebug;
import vairy.main.resorce.SystemConst;
import vairy.script.variable.JScriptVarFactory;
import vairy.timer.CycleTimer;
import vairy.ui.cui.Console;
import vairy.ui.gui.MainAlermFrame;
import vairy.watchdog.DogOwner;
import vairy.watchdog.WatchDog;
import vairy.watchdog.WatchDog.WatchDogDebug;
import vairy.watchdog.WatchDogException;

public class Main implements SystemConst{
	private final static WatchDog watchDog = new WatchDog(WATCHDOG);
	public static void main(String[] args) {
		Debug.setDebug(DebugKey.ALERM, false);
		Debug.setDebug(DebugKey.EDITTIMER, false);
		Debug.setDebug(DebugKey.LISTENER, false);
		Debug.setDebug(DebugKey.CYCLE, false);
		Debug.setDebug(GuiDebug.CALENDARBAR, false);
		Debug.setDebug(WatchDogDebug.MONITOR, false);
		Debug.setDebug(WatchDogDebug.DEAD, false);

		Thread thread = new Thread(watchDog);
		watchDog.addOwner(new WatchDogAssert());
		thread.setName("WatchDog");
		thread.start();

		AppMain appMain = AppMain.getInstance();
		appMain.startinit();
		createThread(appMain, "AppMain");
		CycleTimer.addListener(appMain);

		/* ユーザーインターフェイス */
		MainAlermFrame jframe = new MainAlermFrame(appMain);
		createThread(jframe,"Frame");
		CycleTimer.addListener(jframe);
		jframe.setVisible(true);


		/* タイマー回すのはイニットの読み込み終わってからネ！ */
		CycleTimer.setCycleunit(TIMERCYCLE);
		CycleTimer.startTimer();

		Console console = new Console(appMain);
		console.main();

		System.exit(0);
		return;
	}

	private static void createThread(Runnable run, String name){
		Thread thread = new Thread(run);
		thread.setName(name);
		thread.start();
		watchDog.addMonitor(thread);
	}

	private static class WatchDogAssert implements DogOwner{
		@Override
		public void notifyIllegal(WatchDogException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private static void test_gd(){
		GraphicsDevice[] gds = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		for(GraphicsDevice gd : gds ){
			gd.getDefaultConfiguration().getBounds();
		}
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
