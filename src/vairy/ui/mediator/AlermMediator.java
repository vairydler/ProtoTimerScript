package vairy.ui.mediator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vairy.core.app.AppMain;

public class AlermMediator {
	public enum MediatorType{
		ADD,
		REMOVE,
		EDIT,
		MOUSECAP,
	}

	public class MediatorEvent{
		private MediatorType type;

		public MediatorEvent(MediatorType type) {
			this.type = type;
		}

		public MediatorType getType() {
			return type;
		}
		public void setType(MediatorType type) {
			this.type = type;
		}
	}

	public interface MediatorListener {
		public void notifyMediator(MediatorEvent event);
	}

	private static AlermMediator instance = new AlermMediator();
	private Map<MediatorType, List<MediatorListener>> listenermap = new HashMap<>();

	public static AlermMediator getInstance(){
		return instance;
	}
	private AlermMediator(){}

	public void addMediator(MediatorType type, MediatorListener listener){
		List<MediatorListener> list = this.listenermap.get(type);
		if( null == list ){
			list = new ArrayList<MediatorListener>();
			this.listenermap.put(type, list);
		}
		list.add(listener);
	}

	public void removeMediator(MediatorType type, MediatorListener listener){
		List<MediatorListener> list = this.listenermap.get(type);
		if( null != list ){
			list.remove(listener);
		}
	}

	public void notifyMediator(MediatorType type){
		List<MediatorListener> list = this.listenermap.get(type);
		if ( null != list ){
			MediatorEvent mediatorEvent = new MediatorEvent(type);
			for (MediatorListener mediatorListener : list) {
				mediatorListener.notifyMediator(mediatorEvent);
			}
		}
	}
}
