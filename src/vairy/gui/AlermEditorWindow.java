package vairy.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import vairy.alerm.Alerm;
import vairy.alerm.param.AlermBean;
import vairy.alerm.param.AlermMode;
import vairy.alerm.param.AlermScriptType;
import vairy.gui.swing.ButtonBar;
import vairy.gui.swing.LabelComp;
import vairy.gui.swing.ButtonBar.BUTTON_DIRECT;

public class AlermEditorWindow extends JDialog {
	private enum BTN_NAME{
		OK,
		適用,
		キャンセル,
		削除
	};
	private final Alerm target;
	private final AlermBean editbean;
	private final LabelComp<JTextField> name;
	private final Map<AlermScriptType, LabelComp<JTextField>> scriptmap = new HashMap<AlermScriptType, LabelComp<JTextField>>();
	private final JPanel drivemode;
	private final Map<AlermMode,JRadioButton> drivemodemap = new HashMap<AlermMode, JRadioButton>();
	private final ButtonBar btnbar;

	public AlermEditorWindow(final Alerm target) {
		this.target = target;
		this.editbean= target.getUnit().getBean();

		this.setLayout(new GridLayout(0, 1));

		name = createName();
		this.add(name);


		drivemode = createDriveMode();
		this.add(drivemode);

		AlermScriptType[] values = AlermScriptType.values();
		for (AlermScriptType alermScriptType : values) {
			LabelComp<JTextField> labelComp = new LabelComp<JTextField>(new JTextField());
			scriptmap.put(alermScriptType ,labelComp);

			labelComp.setLblvalue(alermScriptType.dispText);
			labelComp.getComponent().setPreferredSize(new Dimension(200, 20));

			this.add(labelComp);
		}

		ArrayList<String> btnlist = new ArrayList<String>();
		btnlist.add(BTN_NAME.OK.name());
		btnlist.add(BTN_NAME.適用.name());
		btnlist.add(BTN_NAME.キャンセル.name());
		btnlist.add(BTN_NAME.削除.name());
		btnbar = new ButtonBar(this, btnlist, BUTTON_DIRECT.HORIZON);
		btnbar.getButton(BTN_NAME.OK.name()).addActionListener(new pressOK());
		btnbar.getButton(BTN_NAME.適用.name()).addActionListener(new pressSAVE());
		btnbar.getButton(BTN_NAME.キャンセル.name()).addActionListener(new pressCancel());
		btnbar.getButton(BTN_NAME.削除.name()).addActionListener(new pressDel());
		this.add(btnbar);

		updateDisplay();
	}

	private JPanel createDriveMode() {
		JPanel rtn = new JPanel();

		ButtonGroup buttonGroup = new ButtonGroup();
		AlermMode[] values2 = AlermMode.values();
		rtn.setLayout(new GridLayout(values2.length, 0));
		for (AlermMode alermMode : values2) {
			JRadioButton jRadioButton = new JRadioButton(alermMode.dispText);
			drivemodemap.put(alermMode, jRadioButton);
			buttonGroup.add(jRadioButton);

			rtn.add(jRadioButton);
		}

		return null;
	}

	private LabelComp<JTextField> createName() {
		return new LabelComp<JTextField>(new JTextField());
	}

	public void updateDisplay(){
		AlermBean bean = editbean;

		name.getComponent().setText(bean.getName());

		Set<AlermScriptType> keySet = scriptmap.keySet();
		Map<AlermScriptType, String> scriptmap2 = bean.getScriptmap();
		for (AlermScriptType alermScriptType : keySet) {
			String string = scriptmap2.get(alermScriptType);
			scriptmap.get(alermScriptType).getComponent().setText(string);
		}

		Set<AlermMode> keySet2 = drivemodemap.keySet();
		AlermMode mode = bean.getMode();
		for (AlermMode alermMode : keySet2) {
			if(mode == alermMode){
				drivemodemap.get(alermMode).setSelected(true);;
			}
		}
	}

	public final Alerm getTarget(){
		return this.target;
	}

	private class pressOK implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			AlermEditorWindow.this.target.getUnit().setBean(editbean);
			AlermEditorWindow.this.setVisible(false);
		}
	}
	private class pressSAVE implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			AlermEditorWindow.this.target.getUnit().setBean(editbean);
		}
	}
	private class pressCancel implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			AlermEditorWindow.this.setVisible(false);
		}
	}
	private class pressDel implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			target.Delete();
			AlermEditorWindow.this.setVisible(false);
		}
	}
}
