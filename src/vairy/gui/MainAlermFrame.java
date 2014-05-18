package vairy.gui;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import vairy.alerm.Alerm;
import vairy.alerm.AlermManager;
import vairy.app.AppMain;
import variy.timer.TimerRunnable;

public class MainAlermFrame extends JFrame implements TimerRunnable, Runnable{
	private Map<Alerm, MainAlermPanel> panelmap = new HashMap<Alerm, MainAlermPanel>();
	private final AppMain app;

	public MainAlermFrame(final AppMain app) {
		this.app = app;

		AlermManager manager = app.getManager();
		Integer alermsize = manager.alermsize();

		Container contentPane = this.getContentPane();
		contentPane.setLayout(new GridLayout(alermsize, 0));

		panelmap.clear();
		for (int i = 0; i < alermsize; i++) {
			Alerm alerm = manager.getAlerm(i);
			MainAlermPanel mainTimerPanel = new MainAlermPanel(alerm);
			contentPane.add(mainTimerPanel);
			panelmap.put(alerm, mainTimerPanel);
		}
		this.addWindowListener(		new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				app.getManager().saveAlerm();
			}
		});
	}

	@Override
	public void timerrun() {
		new Thread(this).run();;
	}

	@Override
	public void run() {
		Collection<MainAlermPanel> values = panelmap.values();

		for (MainAlermPanel mainTimerPanel : values) {
			mainTimerPanel.updateDisplay();
		}
	}
}
