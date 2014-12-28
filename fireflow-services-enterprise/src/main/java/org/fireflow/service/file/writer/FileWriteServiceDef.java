package org.fireflow.service.file.writer;

import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.model.servicedef.impl.AbstractServiceDef;
import org.fireflow.model.servicedef.impl.JavaInterfaceDef;

public class FileWriteServiceDef extends AbstractServiceDef implements ServiceDef {
	
	/**
	 * 文件目录
	 */
	private String directory = null;
	
	private String charset = "UTF-8";

	protected void init(){
	
		JavaInterfaceDef javaInterface = new JavaInterfaceDef();
		javaInterface.setInterfaceClassName("org.fireflow.service.file.writer.FileWriter");
		this.setInterface(javaInterface);
		
		this.invokerClassName = "org.fireflow.service.file.writer.FileWriteServiceInvoker";

	}
	
	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	
}
