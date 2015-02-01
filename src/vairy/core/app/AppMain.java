package vairy.core.app;

import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JLabel;

import vairy.core.alerm.Alerm;
import vairy.core.alerm.AlermManager;
import vairy.core.alerm.AlermUnit;
import vairy.core.alerm.param.AlermBean;
import vairy.core.alerm.param.AlermBeans;
import vairy.core.alerm.param.EAlermScriptType;
import vairy.lib.fileaccess.user.AlermAccecor;
import vairy.main.resorce.SystemConst;
import vairy.ui.mediator.AlermMediator;
import vairy.ui.mediator.AlermMediator.MediatorEvent;
import vairy.ui.mediator.AlermMediator.MediatorListener;
import vairy.ui.mediator.AlermMediator.MediatorType;
import variy.timer.CycleTimer;
import variy.timer.TimerRunnable;

public class AppMain implements SystemConst{
	private static final AppMain instance = new AppMain();
	private AlermManager manager = new AlermManager();
	private final TimerRunnable mousecap = new TimerRunnable() {

		@Override
		public void timerrun() {

		}
	};

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
		instance2.addMediator(MediatorType.ADD, new notifyUI());
		instance2.addMediator(MediatorType.MOUSECAP, new notifyUI());


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
				new TextDialog();
				break;
			case REMOVE:
				break;
			default:
				break;
			}
		}
	}

	private class TextDialog extends JDialog implements TimerRunnable{
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
			CycleTimer.addListener(this);
		}

		public void setText(String txt){
			this.label.setText(txt);
		}

		@Override
		public void timerrun() {
			Point p = MouseInfo.getPointerInfo().getLocation();
			setText(p.toString());
			dialog.pack();
		}

		@Override
		public void dispose() {
			super.dispose();
			CycleTimer.removeListener(this);
		}
	}
}
