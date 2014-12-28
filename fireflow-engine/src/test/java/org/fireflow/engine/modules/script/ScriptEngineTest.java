package org.fireflow.engine.modules.script;

import java.util.List;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

public class ScriptEngineTest {
	public static void main(String[] args) {
		listAllScriptEngines();
	}

	public static void listAllScriptEngines() {
		System.out.println("==搜索到的所有的脚本引擎如下……");
		
		ScriptEngineManager manager = new ScriptEngineManager();
		
		List<ScriptEngineFactory> factoryList = manager.getEngineFactories();
		int i=0;
		for (ScriptEngineFactory factory : factoryList) {
			System.out.println("");
			System.out.print("No"+(i++)+"、enginename=");
			System.out.print(factory.getEngineName());

			System.out.print(";languagename=");
			System.out.print(factory.getLanguageName());
			System.out.print(";extensions=" + factory.getExtensions());
			System.out.print("; names="+factory.getNames());
			System.out.print("; MimeTypes="+factory.getMimeTypes());
			
		}
	}
}
