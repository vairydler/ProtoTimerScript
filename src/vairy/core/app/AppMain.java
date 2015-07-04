package vairy.core.app;

import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JLabel;

import vairy.debug.Debug;
import vairy.core.alerm.Alerm;
import vairy.core.alerm.AlermManager;
import vairy.core.alerm.AlermUnit;
import vairy.core.alerm.param.AlermBean;
import vairy.core.alerm.param.AlermBeans;
import vairy.core.alerm.param.EAlermScriptType;
import vairy.debug.user.DebugKey;
import vairy.lib.fileaccess.user.AlermAccecor;
import vairy.main.resorce.SystemConst;
import vairy.timer.CycleTimer;
import vairy.timer.TimerRunnable;
import vairy.ui.mediator.AlermMediator;
import vairy.ui.mediator.AlermMediator.MediatorEvent;
import vairy.ui.mediator.AlermMediator.MediatorListener;
import vairy.ui.mediator.AlermMediator.MediatorType;

public class AppMain implements SystemConst,TimerRunnable,Runnable{
	private static final AppMain instance = new AppMain();
	private AlermManager manager = new AlermManager();
	private TextDialog mousedialog = null;

	public static AppMain getInstance(){
		return instance;
	}

	private AppMain(){

	}

	public void startinit(){
		manager = new AlermManager();
		manager.loadAlerm();
		manager.notifyProgStart();

		AlermMediator instance2 = AlermMediator.getInstance();
		notifyUI notifyUI = new notifyUI();
		instance2.addMediator(MediatorType.ADD, notifyUI);
		instance2.addMediator(MediatorType.MOUSECAP, notifyUI);


	}

	public void programEnd(){
		manager.notifyProgEnd();
		manager.saveAlerm();
	}
	public final AlermManager getManager() {
		return manager;
	}

	private class notifyUI implements MediatorListener{

		@Override
		public void notifyMediator(MediatorEvent event) {
			switch(event.getType()){
			case ADD:
				manager.addAlermFromBean(new AlermBean());
				break;
			case EDIT:
				break;
			case MOUSECAP:
				mousedialog = new TextDialog();
				break;
			case REMOVE:
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void run() {
		while(true){
			Debug.println(DebugKey.CYCLE, "APPMAIN_start");
			manager.notifyCycle();

			if(null != mousedialog){
				Point p = MouseInfo.getPointerInfo().getLocation();
				mousedialog.setText(p.toString());
				mousedialog.pack();
			}

			Debug.println(DebugKey.CYCLE, "APPMAIN_end");
			try {
				dummywait(WATCHDOG);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void timerrun() {
		dummynotify();
	}

	private synchronized void dummywait(long time) throws InterruptedException{
		wait(time);
	}

	private synchronized void dummynotify(){
		notify();
	}

	private class TextDialog extends JDialog{
		/**
		 *
		 */
		private static final long serialVersionUID = -5903295865645283213L;
		private JLabel label = new JLabel();
		private JDialog dialog = this;
		public TextDialog(){
			dialog.add(label);
			dialog.setUndecorated(false);
			dialog.setFocusable(false);
			dialog.setEnabled(true);
			dialog.setVisible(true);
			dialog.setAlwaysOnTop(true);
			dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			dialog.setLocation(new Point(0,0));

			label.setFont(new Font(null, Font.PLAIN, 18));
		}

		public void setText(String txt){
			this.label.setText(txt);
		}

		@Override
		public void dispose() {
			super.dispose();
			mousedialog = null;
		}
	}
}
