package vairy.fileaccess.user;

import vairy.alerm.param.AlermBeans;
import vairy.fileacces.JSonicAccecor;

public class AlermAccecor extends JSonicAccecor<AlermBeans> {
	public AlermAccecor(String filename, Class<AlermBeans> c) {
		super(filename, c);
	}

	@Override
	protected AlermBeans validate(AlermBeans src) {
		return src;
	}
}
