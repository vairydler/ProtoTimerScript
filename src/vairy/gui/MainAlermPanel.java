package vairy.gui;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import vairy.alerm.Alerm;
import vairy.alerm.param.AlermBean;
import vairy.alerm.param.AlermMode;
import vairy.alerm.param.AlermTime;
import vairy.alerm.param.TimerTime;
import vairy.gui.swing.CalendarBar;
import vairy.gui.swing.CalendarBar.FIELD;

public class MainAlermPanel extends JPanel{
	private final Alerm target;
	private SubTimerPanel content;
	public MainAlermPanel(Alerm target) {
		this.target = target;

		content = new SubTimerPanel();
		this.add(content);

		this.validate();
	}

	public void updateDisplay(){
		content.updateDisplay();
	}
	private class SubTimerPanel extends JPanel{
		private JLabel name;
		private CalendarPanel calpanel;
		private JToggleButton driveButton;
		private JButton editButton;

		public SubTimerPanel() {
			name = new JLabel();
			calpanel = new CalendarPanel();
			driveButton = new JToggleButton("動作");
			editButton = new JButton("編集");

			setLayout(new GridLayout(0, 4));
			this.add(name);
			this.add(calpanel);
			this.add(driveButton);
			this.add(editButton);

			driveButton.addActionListener(createDriveButtonListener());
			editButton.addActionListener(createEditButtonListener());

			updateDisplay();
		}

		public void updateDisplay(){
			name.setText(target.getUnit().getBean().getName());
			driveButton.setSelected(target.getUnit().getBean().isDrive());

			calpanel.cardUpdate();
			calpanel.updateDisplay();
		}

		public final ActionListener createDriveButtonListener(){
			return new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(target.getUnit().getBean().isDrive()){
						target.notifyTimerStop();
					}else{
						target.notifyTimerStart();
						calpanel.updateDisplay();
					}

					calpanel.cardUpdate();
				}
			};
		}
		public final ActionListener createEditButtonListener(){
			return new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					AlermEditorWindow alermEditorWindow = new AlermEditorWindow(target);
					alermEditorWindow.setVisible(true);
					alermEditorWindow.pack();
				}
			};
		}
	}

	public enum CardIndex{
		TIMEREDIT,
		TIMERDISP,
		ALERMEDIT,
		ALERMDISP,
	}

	private class CalendarPanel extends JPanel{
		private CardLayout layout;
		private CalendarBar timerEditBar;
		private CalendarBar alermEditBar;
		private CalendarBar timerDisplayBar;
		private CalendarBar alermDisplayBar;

		public CalendarPanel() {
			AlermBean bean = target.getUnit().getBean();
			ArrayList<FIELD> arrayList = new ArrayList<FIELD>();
			arrayList.add(FIELD.DAY_OF_YEAR);
			arrayList.add(FIELD.HOUR);
			arrayList.add(FIELD.MIN);
			arrayList.add(FIELD.SEC);

			timerEditBar = new CalendarBar(bean.getTimer(), new TimerTime(),arrayList);
			timerDisplayBar = new CalendarBar(new TimerTime(), new TimerTime(),arrayList);
			alermEditBar = new CalendarBar(bean.getAlerm());
			alermDisplayBar = new CalendarBar(new AlermTime());

			layout = new CardLayout();
			this.setLayout(layout);

			this.add(timerEditBar, CardIndex.TIMEREDIT.name());
			this.add(timerDisplayBar, CardIndex.TIMERDISP.name());
			this.add(alermEditBar, CardIndex.ALERMEDIT.name());
			this.add(alermDisplayBar, CardIndex.ALERMDISP.name());
		}

		public void chgDisp(CardIndex index){
			layout.show(this, index.name());
		}

		public void updateDisplay(){
			timerUpdate();
			cardUpdate();
		}

		public void timerUpdate(){
			long diff = target.getUnit().getDiff().getTimeInMillis();
			timerDisplayBar.getTarget().setTimeInMillis(diff);
			timerDisplayBar.updateText();
		}
		/**
		 * 動作モードと動作状態に応じて、表示を切り替える。
		 */
		public void cardUpdate(){
			CardIndex index;
			AlermBean bean = target.getUnit().getBean();
			if(bean.isDrive()){
				if(AlermMode.ALERM == bean.getMode()){
					index = CardIndex.ALERMDISP;
				}else{
					index = CardIndex.TIMERDISP;
				}
			}else{
				if(AlermMode.ALERM == bean.getMode()){
					index = CardIndex.ALERMEDIT;
				}else{
					index = CardIndex.TIMEREDIT;
				}
			}

			chgDisp(index);
		}
	}
}
