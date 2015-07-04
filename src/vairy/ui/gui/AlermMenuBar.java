package vairy.ui.gui;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vairy.ui.mediator.AlermMediator;
import vairy.ui.mediator.AlermMediator.MediatorType;

/**
 *
 * @author vairy
 */
public class AlermMenuBar extends JMenuBar{
	private static final long serialVersionUID = 8626617250102405892L;

	private enum MenuText{
		MENU_ALERM("アラーム"),
		MENU_UTIL("ユーティル"),
		ITEM_ADD("追加"),
		ITEM_MOUSE("マウス座標表示"),
		ITEM_ONTOP("常に前面表示"),
		;
		private final String menuname;
		private MenuText(final String menuname){
			this.menuname = menuname;
		}
		private String Menuname(){
			return menuname;
		}
	}

	private JMenu alermmenu = new JMenu(MenuText.MENU_ALERM.Menuname());
	private JMenu utilmenu = new JMenu(MenuText.MENU_UTIL.Menuname());
	private JMenuItem additem = new JMenuItem(MenuText.ITEM_ADD.Menuname());
	private JMenuItem mousecapitem = new JMenuItem(MenuText.ITEM_MOUSE.Menuname());
	private JCheckBoxMenuItem ontopitem = new JCheckBoxMenuItem(MenuText.ITEM_ONTOP.Menuname());

	public AlermMenuBar() {
		this.add(alermmenu);
		alermmenu.add(additem);
		additem.addActionListener(createAddItemListener());

		this.add(utilmenu);
		utilmenu.add(mousecapitem);
		mousecapitem.addActionListener(createMouseCapListener());

		utilmenu.add(ontopitem);
		ontopitem.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				boolean selected = ontopitem.isSelected();

				Component comp = AlermMenuBar.this.getParent();
				while( !(comp instanceof Frame) ){
					comp = comp.getParent();
				}

				Frame frame = (Frame) comp;
				frame.setAlwaysOnTop(selected);
			}
		});
	}

	private ActionListener createAddItemListener(){
		ActionListener rtn = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AlermMediator instance = AlermMediator.getInstance();
				instance.notifyMediator(MediatorType.ADD);
			}
		};
		return rtn;
	}

	private ActionListener createMouseCapListener(){
		ActionListener rtn = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AlermMediator instance = AlermMediator.getInstance();
				instance.notifyMediator(MediatorType.MOUSECAP);
			}
		};
		return rtn;
	}
}
