package vairy.app;

import java.util.ArrayList;
import java.util.Map;

import vairy.alerm.Alerm;
import vairy.alerm.AlermManager;
import vairy.alerm.AlermUnit;
import vairy.alerm.param.AlermBean;
import vairy.alerm.param.AlermBeans;
import vairy.alerm.param.AlermScriptType;
import vairy.fileaccess.user.AlermAccecor;
import vairy.resorce.SystemConst;
import variy.timer.CycleTimer;

public class AppMain implements SystemConst{
	private AlermManager manager = new AlermManager();
	public void startinit(){
		manager = new AlermManager();
		manager.loadAlerm();
		manager.notifyProgStart();
	}
	public void programEnd(){
		manager.notifyProgEnd();
		manager.saveAlerm();
	}
	public final AlermManager getManager() {
		return manager;
	}
}
