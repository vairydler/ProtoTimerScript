package vairy.core.alerm.param;

import java.util.ArrayList;
import java.util.List;

/**
 * ArrayListのままだとJSONICが使いにくいのでダミー実装。
 * @author vairydler
 *
 */
public class AlermBeans{
	private ArrayList<AlermBean> beans = new ArrayList<AlermBean>();

	public final ArrayList<AlermBean> getBeans() {
		return beans;
	}
	public final void setBeans(ArrayList<AlermBean> beans) {
		this.beans = beans;
	}
}
