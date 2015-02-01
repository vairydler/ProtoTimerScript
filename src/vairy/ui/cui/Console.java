package vairy.ui.cui;

import java.awt.Container;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.apache.commons.lang.math.NumberUtils;
import org.omg.PortableInterceptor.NON_EXISTENT;

import vairy.Debug.Debug;
import vairy.core.alerm.Alerm;
import vairy.core.alerm.AlermManager;
import vairy.core.alerm.Alerm_ForJS;
import vairy.core.alerm.param.AlermBean;
import vairy.core.alerm.param.EAlermMode;
import vairy.core.alerm.param.EAlermScriptType;
import vairy.core.alerm.param.TimerTime;
import vairy.core.app.AppMain;
import vairy.debug.user.DebugKey;
import vairy.gui.swing.CalendarBar;
import vairy.gui.swing.CalendarBar.FIELD;
import vairy.gui.swing.CalendarBar.TEXT_DIRECT;
import vairy.ui.gui.MainAlermFrame;
import vairy.ui.gui.MainAlermPanel;
import variy.timer.CycleTimer;

public class Console {
	private enum Command{
		EXIT("終了"),
		EDIT("編集"),
		SAVE("保存"),
		LOADSCRIPT("スクリプト読み直し"),
		STARTALERM("アラーム開始"),
		STOPALERM("アラーム停止"),
		DEBUG("デバッグ"),

		;
		public final String disptext;
		private Command(final String text) {
			this.disptext = text;
		}
	}
	private enum ControlMode{
		CONTROL("コマンドを入力してください。"),
		EDITTYPE("編集パターンを入力してください。"),
		EDITNUM("編集するタイマを選択してください。"),
		EDITDATA("編集データを選択してください。"),
		EDITDATESUB("編集データを選択してください。"),
		EDITDATE_YEAR("年を設定してください。"),
		EDITDATE_MONTH("月を設定してください。"),
		EDITDATE_DAY("日を設定してください。"),
		EDITDATE_HOUR("時を設定してください。"),
		EDITDATE_MIN("分を設定してください。"),
		EDITDATE_SEC("秒を設定してください。"),
		EDITSCRIPTSUB("設定するスクリプトのタイプを選択してください。"),
		EDITSCRIPT("スクリプトのパスを入力してください。"),
		EDITMODE("動作モードを入力してください。"),
		EDITNAME("名前を入力してください。"),
		STARTNUM("開始するタイマを選択してください。"),
		STOPNUM("停止するタイマを選択してください。"),

		;
		public final String disptext;
		private ControlMode(final String text) {
			this.disptext = text;
		}
	}
	private enum EditType{
		NONE("終了"),
		NEW("新規作成"),
		EDIT("編集"),
		DELETE("削除"),

		;
		public final String disptext;
		private EditType(final String text) {
			this.disptext = text;
		}
	}
	private enum EditData{
		NONE("キャンセル"),
		OK("決定"),
		NAME("名前を設定"),
		ALERMDATE("目標時間を設定"),
		MODE("動作モードを設定"),
		SCRIPTFILE("スクリプトファイルのパスを設定"),

		;
		public final String disptext;
		private EditData(final String text){
			this.disptext = text;
		}
	}
	private enum ScriptSub{
		NONE("終了",null),
		PROGRAMSTART("プログラム開始時",EAlermScriptType.PROGRAMSTART),
		TIMERSTART("タイマー動作開始時",EAlermScriptType.TIMERSTART),
		TIMEREXEC("タイマー満了時",EAlermScriptType.TIMEREXEC),
		TIMERSTOP("タイマー動作停止時",EAlermScriptType.TIMERSTOP),
		PROGRAMEND("プログラム終了時",EAlermScriptType.PROGRAMEND),

		;
		public final String disptext;
		public final EAlermScriptType type;
		private ScriptSub(final String text,final EAlermScriptType type){
			this.disptext = text;
			this.type = type;
		}
	}

	private enum DateSub{
		NONE("終了",0),
		YEAR("年",Calendar.YEAR),
		MONTH("月",Calendar.MONTH),
		DAY("日",Calendar.DAY_OF_MONTH),
		HOUR("時",Calendar.HOUR_OF_DAY),
		MIN("分",Calendar.MINUTE),
		SEC("秒",Calendar.SECOND),

		;
		public final String disptext;
		public final int field;
		private DateSub(final String text,final int field){
			this.disptext = text;
			this.field = field;
		}
	}

	private enum DriveMode{
		ALERM("アラーム",EAlermMode.ALERM),
		TIMER("タイマー",EAlermMode.TIMER),

		;
		public final String disptext;
		public final EAlermMode mode;
		private DriveMode(final String text,EAlermMode mode){
			this.disptext = text;
			this.mode = mode;
		}
	}
	private ControlMode ctrlmode;
	private EditType edittype;
	private ScriptSub scripttype;
	private DateSub datesub;
	private Alerm_ForJS editalerm;
	private AlermBean editbean;
	private AppMain app;
	public Console(AppMain app) {
		this.app = app;
	}

	public void main(){
		ctrlmode = ControlMode.CONTROL;

		Integer exitsts = 0;
		String buff;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while(0 == exitsts){
			disptext();

			try {
				buff = br.readLine();
			} catch (IOException e) {
				System.out.println("入力がよくない。");
				continue;
			}

			if (buff.length() > 0) {
				exitsts = doControl(buff);
			}
		}

		System.exit(exitsts);
	}

	private Integer doControl(String buff) {
		Integer rtn = -1;

		switch (ctrlmode) {
		case CONTROL:
			rtn = doCommand(buff);
			break;
		case EDITTYPE:
			rtn = doEdittype(buff);
			break;
		case EDITNUM:
			rtn = doEditnum(buff);
			break;
		case EDITDATA:
			rtn = doEditdata(buff);
			break;
		case EDITSCRIPTSUB:
			rtn = doEditScriptSub(buff);
			break;
		case EDITSCRIPT:
			rtn = doEditScript(buff);
			break;
		case EDITDATESUB:
			rtn = doEditDateSub(buff);
			break;
		case EDITDATE_YEAR:
		case EDITDATE_MONTH:
		case EDITDATE_DAY:
		case EDITDATE_HOUR:
		case EDITDATE_MIN:
		case EDITDATE_SEC:
			rtn = doEditDate(buff, datesub);
			break;
		case EDITMODE:
			rtn = doEditMode(buff);
			break;
		case EDITNAME:
			rtn = doEditName(buff);
			break;
		case STARTNUM:
			rtn = doAlermDrive(buff,true);
			break;
		case STOPNUM:
			rtn = doAlermDrive(buff,false);
			break;
		default:
			break;
		}

		return rtn;
	}

	/**
	 * タイマの動作開始/停止を設定
	 * @param buff 入力値
	 * @param b true：タイマ開始　false:タイマ停止
	 * @return 0：正常継続 1：正常終了 -1：異常終了
	 */
	private Integer doAlermDrive(String buff, boolean b) {
		Integer rtn = 0;

		if (NumberUtils.isNumber(buff)) {
			int parseInt = Integer.parseInt(buff);
			Alerm_ForJS alerm = app.getManager().getAlerm(parseInt);
			if(null != alerm){
				if (true == b) {
					alerm.notifyTimerStart();
				}else {
					alerm.notifyTimerStop();
				}
			}

			ctrlmode = ControlMode.CONTROL;
		}

		return rtn;
	}

	private Integer doEditName(String buff) {
		Integer rtn = 0;

		editbean.setName(buff);

		ctrlmode = ControlMode.EDITDATA;
		return rtn;
	}

	private Integer doEditMode(String buff) {
		Integer rtn = 0;

		if (NumberUtils.isNumber(buff)) {
			int parseInt = Integer.parseInt(buff);
			DriveMode[] values = DriveMode.values();

			if (values.length > parseInt) {
				DriveMode value = values[parseInt];
				editbean.setMode(value.mode);
			}
			ctrlmode = ControlMode.EDITDATA;
		}

		return rtn;
	}

	private Integer doEditDate(String buff, DateSub datesub2) {
		Integer rtn = 0;
		Calendar alerm = null;
		EAlermMode mode = editbean.getMode();

		if(EAlermMode.ALERM == mode){
			alerm = editbean.getAlerm();
		}else if (EAlermMode.TIMER == mode) {
			alerm = editbean.getTimer();
		}

		if (null != alerm) {
			alerm.setLenient(true);
			alerm.set(datesub2.field, Integer.parseInt(buff));
		}else{
			rtn = -1;
		}

		Debug.println(DebugKey.EDITTIMER, editbean.getTimer().getTime());
		Debug.println(DebugKey.EDITTIMER, alerm.getTime());
		ctrlmode = ControlMode.EDITDATESUB;
		return rtn;
	}

	private Integer doEditDateSub(String buff) {
		Integer rtn = 0;
		if (NumberUtils.isNumber(buff)) {
			int parseInt = Integer.parseInt(buff);
			DateSub[] values = DateSub.values();

			if (values.length > parseInt) {
				DateSub data = values[parseInt];

				datesub = data;
				switch (data) {
				case NONE:
					ctrlmode = ControlMode.EDITDATA;
					break;
				case YEAR:
					ctrlmode = ControlMode.EDITDATE_YEAR;
					break;
				case MONTH:
					ctrlmode = ControlMode.EDITDATE_MONTH;
					break;
				case DAY:
					ctrlmode = ControlMode.EDITDATE_DAY;
					break;
				case HOUR:
					ctrlmode = ControlMode.EDITDATE_HOUR;
					break;
				case MIN:
					ctrlmode = ControlMode.EDITDATE_MIN;
					break;
				case SEC:
					ctrlmode = ControlMode.EDITDATE_SEC;
					break;
				default:
					break;
				}
			}
		}

		return rtn;
	}

	private Integer doEditScript(String buff) {
		Integer rtn = 0;

		if (ScriptSub.NONE != scripttype) {
			editbean.getScriptmap().put(scripttype.type, buff);
		}
		ctrlmode = ControlMode.EDITSCRIPTSUB;
		return rtn;
	}

	private Integer doEditScriptSub(String buff) {
		Integer rtn = 0;
		int parseInt = Integer.parseInt(buff);
		ScriptSub[] values = ScriptSub.values();

		if (values.length > parseInt) {
			ScriptSub data = values[parseInt];

			scripttype = data;
			switch (data) {
			case NONE:
				ctrlmode = ControlMode.EDITDATA;
				break;
			case PROGRAMSTART:
			case TIMERSTART:
			case TIMEREXEC:
			case TIMERSTOP:
			case PROGRAMEND:

				ctrlmode = ControlMode.EDITSCRIPT;
				break;
			default:
				break;
			}
		}

		return rtn;
	}

	private Integer doEditdata(String buff) {
		Integer rtn = 0;
		EditData[] values = EditData.values();

		int parseInt = 0;
		if(NumberUtils.isNumber(buff)){
			parseInt = Integer.parseInt(buff);

			if (values.length > parseInt) {
				EditData data = values[parseInt];

				switch (data) {
				case NONE:
					ctrlmode = ControlMode.CONTROL;
					break;
				case OK:
					if(EditType.NEW == edittype){
						app.getManager().addAlermFromBean(editbean);
					}else{
						editalerm.getUnit().setBean(editbean);

						Debug.println(DebugKey.EDITTIMER, editalerm.getUnit().getBean().getTimer().getTime());
					}

					ctrlmode = ControlMode.CONTROL;
					break;
				case SCRIPTFILE:
					ctrlmode = ControlMode.EDITSCRIPTSUB;
					break;
				case ALERMDATE:
//					ctrlmode = ControlMode.EDITDATESUB;
					new CalendarBar();
					CalendarBar cBar = createCalendarBar(editbean);
					JDialog jDialog = new JDialog();
					jDialog.add(cBar);
					jDialog.validate();
					jDialog.setVisible(true);
					jDialog.pack();

					break;
				case MODE:
					ctrlmode = ControlMode.EDITMODE;
					break;
				case NAME:
					ctrlmode = ControlMode.EDITNAME;
					break;
				default:
					break;
				}
			}
		}

		return rtn;
	}

	private CalendarBar createCalendarBar(AlermBean target) {
		CalendarBar rtn = null;
		ArrayList<FIELD> arrayList = new ArrayList<FIELD>();

		if (EAlermMode.TIMER == target.getMode()) {
			arrayList.add(FIELD.DAY_OF_YEAR);
			arrayList.add(FIELD.HOUR);
			arrayList.add(FIELD.MIN);
			arrayList.add(FIELD.SEC);

			rtn = new CalendarBar(target.getTimer(), new TimerTime(),arrayList);
		}else if (EAlermMode.ALERM == target.getMode()) {
			rtn = new CalendarBar(target.getAlerm());
		}

		return rtn;
	}

	private Integer doEditnum(String buff) {
		Integer rtn = 0;

		int parseInt = 0;
		if (NumberUtils.isNumber(buff)) {
			parseInt = Integer.parseInt(buff);

			Alerm alerm = app.getManager().getAlerm(parseInt);
			if(null != alerm){
				if(EditType.DELETE == edittype){
					app.getManager().removeAlerm(alerm);
					ctrlmode = ControlMode.EDITTYPE;
				}else{
					editbean = alerm.getUnit().getBean().createClone();
					editalerm = alerm;
					ctrlmode = ControlMode.EDITDATA;
				}
			}
		}

		return rtn;
	}

	private Integer doEdittype(String buff) {
		Integer rtn = 0;
		EditType[] values = EditType.values();

		if (NumberUtils.isNumber(buff)) {
			int parseInt = Integer.parseInt(buff);

			if (values.length > parseInt) {
				EditType value = EditType.values()[parseInt];

				edittype = value;
				switch (value) {
				case NEW:
					editbean = new AlermBean();
					ctrlmode = ControlMode.EDITDATA;
					break;
				case EDIT:
					ctrlmode = ControlMode.EDITNUM;
					break;
				case DELETE:
					ctrlmode = ControlMode.EDITNUM;
					break;
				case NONE:
					ctrlmode = ControlMode.CONTROL;
					break;
				default:
					break;
				}
			}
		}

		return rtn;
	}

	private Integer doCommand(String buff) {
		Integer rtn = 0;

		if (NumberUtils.isNumber(buff)) {
			int parseInt = Integer.parseInt(buff);
			Command[] values = Command.values();

			if (values.length > parseInt) {
				Command value = values[parseInt];

				switch (value) {
				case EXIT:
					app.programEnd();
					rtn = 1;
					break;
				case EDIT:
					ctrlmode = ControlMode.EDITTYPE;
					break;
				case SAVE:
					app.getManager().saveAlerm();
					break;
				case LOADSCRIPT:
					app.getManager().loadScript();
					break;
				case STARTALERM:
					ctrlmode = ControlMode.STARTNUM;
					break;
				case STOPALERM:
					ctrlmode = ControlMode.STOPNUM;
					break;
				case DEBUG:
					{

					}


					break;
				default:
					break;
				}
			}
		}

		return rtn;
	}

	private void disptext() {
		System.out.println(ctrlmode.disptext);
		switch (ctrlmode) {
		case CONTROL:
			for (Command value : Command.values()) {
				System.out.println(value.ordinal() + ":" + value.disptext);
			}
			break;
		case EDITTYPE:
			for (EditType editType : EditType.values()) {
				System.out.println(editType.ordinal() + ":" + editType.disptext);
			}
			break;
		case EDITNUM:
			{
				AlermManager manager = app.getManager();
				Integer size = manager.alermsize();
				String name;

				for (int i = 0; i < size; i++) {
					 name = manager.getAlerm(i).getUnit().getBean().getName();
					 System.out.println(i + ":" + name);
				}
			}
			break;
		case EDITDATA:
			for (EditData value : EditData.values()) {
				System.out.println(value.ordinal() + ":" + value.disptext);
			}
			break;
		case EDITSCRIPTSUB:
			for (ScriptSub value : ScriptSub.values()) {
				System.out.println(value.ordinal() + ":" + value.disptext);
			}
			break;
		case EDITDATESUB:
			for (DateSub value : DateSub.values()) {
				System.out.println(value.ordinal() + ":" + value.disptext);
			}
			break;
		case EDITMODE:
			for (DriveMode value : DriveMode.values()) {
				System.out.println(value.ordinal() + ":" + value.disptext);
			}
			break;
		case EDITDATE_YEAR:
		case EDITDATE_MONTH:
		case EDITDATE_DAY:
		case EDITDATE_HOUR:
		case EDITDATE_MIN:
		case EDITDATE_SEC:
		case EDITNAME:
			break;
		case STARTNUM:
		case STOPNUM:
			{
				AlermManager manager = app.getManager();
				Integer size = manager.alermsize();
				String name;

				for (int i = 0; i < size; i++) {
					 name = manager.getAlerm(i).getUnit().getBean().getName();
					 System.out.println(i + ":" + name);
				}
			}
			break;

		default:
			break;
		}
	}
}
