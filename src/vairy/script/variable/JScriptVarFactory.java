package vairy.script.variable;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

public class JScriptVarFactory {
	private ScriptEngine se;

	public JScriptVarFactory(ScriptEngine se) {
		this.se = se;
	}
	/**
	 * 変数を宣言。JavaScriptエンジンに登録する。
	 * @param jarpath JARファイルのパス。
	 * @param classname インスタンス生成するクラス名
	 * @param varname JavaScript側で認識する変数名。
	 * @return 処理成功/処理失敗
	 */
	public Boolean declare(final String jarpath, final String classname, final String varname){
		Boolean rtn = true;
		try {
			URL[] urls = { new File(jarpath).toURI().toURL()};
			ClassLoader loader = URLClassLoader.newInstance(urls);
			Class<?> c = loader.loadClass(classname);

			Object o = c.newInstance();
			se.put(varname, o);
		} catch (Exception e) {
			rtn = false;
		}

		return rtn;
	}
	/**
	 * 変数を削除。
	 * @param varname 削除する変数名。
	 * @return 処理成功/処理失敗
	 */
	public Boolean remove(final String varname){
		se.getBindings(ScriptContext.ENGINE_SCOPE).remove(varname);
		return true;
	}
}
