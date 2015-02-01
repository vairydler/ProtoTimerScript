package vairy.lib.fileaccess.user;

import vairy.core.alerm.param.AlermBeans;
import vairy.json.JsonAccecor;

public class AlermAccecor extends JsonAccecor<AlermBeans> {
	public AlermAccecor(String filename) {
		super(filename, AlermBeans.class);
	}
}
