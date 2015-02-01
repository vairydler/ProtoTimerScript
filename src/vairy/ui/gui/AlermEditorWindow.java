package vairy.ui.gui;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import vairy.core.alerm.Alerm_ForJS;
import vairy.core.alerm.param.AlermBean;
import vairy.core.alerm.param.EAlermMode;
import vairy.core.alerm.param.EAlermScriptType;
import vairy.gui.swing.ButtonBar;
import vairy.gui.swing.LabelComp;
import vairy.gui.swing.ButtonBar.BUTTON_DIRECT;
import vairy.ui.mediator.AlermMediator;
import vairy.ui.mediator.AlermMediator.MediatorType;

/**
 * 時間編集ウィンドウ
 * @author vairy
 */
public class AlermEditorWindow extends JDialog {
	private enum BTN_NAME{
		OK,
		適用,
		キャンセル,
		削除
	};
	private final Alerm_ForJS target;
	private final LabelComp<JTextField> name;
	private final Map<EAlermScriptType, LabelComp<JTextField>> scriptmap = new HashMap<EAlermScriptType, LabelComp<JTextField>>();
	private final JPanel drivemode;
	private final Map<EAlermMode,JRadioButton> drivemodemap = new HashMap<EAlermMode, JRadioButton>();
	private final ButtonBar btnbar;

	public AlermEditorWindow(final Alerm_ForJS target) {
		this.target = target;

		this.setLayout(new GridLayout());

		name = createName();
		this.getContentPane().add(name);

		drivemode = createDriveMode();
		this.getContentPane().add(drivemode);

		EAlermScriptType[] values = EAlermScriptType.values();
		for (EAlermScriptType alermScriptType : values) {
			LabelComp<JTextField> labelComp = new LabelComp<JTextField>(new JTextField());
			scriptmap.put(alermScriptType ,labelComp);

			labelComp.setLblvalue(alermScriptType.dispText);
			labelComp.getComponent().setPreferredSize(new Dimension(200, 20));

			this.getContentPane().add(labelComp);
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
		this.getContentPane().add(btnbar);

		updateDisplay(target.getUnit().getBean());
	}

	/**
	 * 動作モード編集パネル
	 * @return
	 */
	private JPanel createDriveMode() {
		JPanel rtn = new JPanel();

		ButtonGroup buttonGroup = new ButtonGroup();
		EAlermMode[] values2 = EAlermMode.values();
		rtn.setLayout(new GridLayout(values2.length, 0));
		for (EAlermMode alermMode : values2) {
			JRadioButton jRadioButton = new JRadioButton(alermMode.dispText);
			drivemodemap.put(alermMode, jRadioButton);
			buttonGroup.add(jRadioButton);

			rtn.add(jRadioButton);
		}

		return rtn;
	}

	/**
	 * 名前編集パネル
	 * @return
	 */
	private LabelComp<JTextField> createName() {
		return new LabelComp<JTextField>(new JTextField());
	}

	/**
	 * データ→エディタ
	 * @param bean
	 */
	private void updateDisplay(AlermBean bean){
		name.getComponent().setText(bean.getName());

		Set<EAlermScriptType> keySet = scriptmap.keySet();
		Map<EAlermScriptType, String> scriptmap2 = bean.getScriptmap();
		for (EAlermScriptType alermScriptType : keySet) {
			String string = scriptmap2.get(alermScriptType);
			scriptmap.get(alermScriptType).getComponent().setText(string);
		}

		Set<EAlermMode> keySet2 = drivemodemap.keySet();
		EAlermMode mode = bean.getMode();
		for (EAlermMode alermMode : keySet2) {
			if(mode == alermMode){
				drivemodemap.get(alermMode).setSelected(true);;
			}
		}
	}

	/**
	 * エディタ→データ
	 * @return
	 */
	private AlermBean updateBean(){
		AlermBean rtn = getTarget().getUnit().getBean();

		rtn.setName(this.name.getComponent().getText());

		EAlermMode[] values2 = EAlermMode.values();
		for (EAlermMode alermMode : values2) {
			if( drivemodemap.get(alermMode).isSelected() ){
				rtn.setMode(alermMode);
				break;
			}
		}

		Set<EAlermScriptType> keySet = scriptmap.keySet();
		Map<EAlermScriptType, String> scriptmap2 = rtn.getScriptmap();
		for (EAlermScriptType alermScriptType : keySet) {
			String text = scriptmap.get(alermScriptType).getComponent().getText();
			scriptmap2.put(alermScriptType, text);
		}

		return rtn;
	}

	public final Alerm_ForJS getTarget(){
		return this.target;
	}

	private class pressOK implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			AlermBean updateBean = updateBean();
			AlermEditorWindow.this.target.getUnit().setBean(updateBean);
			AlermEditorWindow.this.dispose();
		}
	}
	private class pressSAVE implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			AlermBean updateBean = updateBean();
			AlermEditorWindow.this.target.getUnit().setBean(updateBean);
		}
	}
	private class pressCancel implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			AlermEditorWindow.this.dispose();
		}
	}
	private class pressDel implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			target.Delete();
			AlermMediator instance = AlermMediator.getInstance();
			instance.notifyMediator(MediatorType.REMOVE);
			AlermEditorWindow.this.dispose();
		}
	}
}
