package vairy.ui.gui;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import vairy.core.alerm.Alerm;
import vairy.core.alerm.AlermManager;
import vairy.core.app.AppMain;
import vairy.main.resorce.SystemConst;
import vairy.timer.TimerRunnable;
import vairy.ui.mediator.AlermMediator;
import vairy.ui.mediator.AlermMediator.MediatorEvent;
import vairy.ui.mediator.AlermMediator.MediatorListener;
import vairy.ui.mediator.AlermMediator.MediatorType;

public class MainAlermFrame extends JFrame implements SystemConst,TimerRunnable, Runnable{
	private class ReloadListener implements MediatorListener{
		@Override
		public void notifyMediator(MediatorEvent event) {
			reload();
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 5519082622851112042L;
	private Map<Alerm, MainAlermPanel> panelmap = new HashMap<Alerm, MainAlermPanel>();
	private final AppMain app;

	public MainAlermFrame(final AppMain app) {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.app = app;

		this.addWindowListener(		new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				app.getManager().saveAlerm();
			}
		});

		AlermMediator instance = AlermMediator.getInstance();
		ReloadListener reloadListener = new ReloadListener();
		instance.addMediator(MediatorType.ADD, reloadListener);
		instance.addMediator(MediatorType.REMOVE, reloadListener);

		reload();
	}

	public void reload(){
		this.getContentPane().removeAll();
		this.setJMenuBar(new AlermMenuBar());

		synchronized (panelmap) {
			for (MainAlermPanel panel : panelmap.values()) {
				panel.dispose();
			}
			panelmap.clear();

			AlermManager manager = app.getManager();
			Integer alermsize = manager.alermsize();

			if( 0 != alermsize ){

				Container contentPane = new JPanel();
				JScrollPane jScrollPane = new JScrollPane(contentPane);

				contentPane.setLayout(new GridLayout(alermsize, 0));

				for (int i = 0; i < alermsize; i++) {
					Alerm alerm = manager.getAlerm(i);
					MainAlermPanel mainTimerPanel = new MainAlermPanel(alerm);
					contentPane.add(mainTimerPanel);
					panelmap.put(alerm, mainTimerPanel);
				}

				this.getContentPane().add(jScrollPane);
			}
		}

		this.pack();
	}

	@Override
	public void timerrun() {
		dummynotify();
	}

	@Override
	public void run() {
		while(true){
			synchronized (panelmap) {
				Collection<MainAlermPanel> values = panelmap.values();

				for (MainAlermPanel mainTimerPanel : values) {
					mainTimerPanel.updateDisplay();
				}
			}

			try {
				dummywait(WATCHDOG);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	private synchronized void dummywait(long time) throws InterruptedException{
		wait(time);
	}

	private synchronized void dummynotify(){
		notify();
	}
}
