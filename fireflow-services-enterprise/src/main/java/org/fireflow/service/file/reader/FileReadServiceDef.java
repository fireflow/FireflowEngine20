package org.fireflow.service.file.reader;

import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.model.servicedef.impl.AbstractServiceDef;
import org.fireflow.model.servicedef.impl.JavaInterfaceDef;
import org.fireflow.service.file.FileObject;

public class FileReadServiceDef extends AbstractServiceDef implements ServiceDef {
	/**
	 * 读取之后删除（暂时不采用）
	 */
//	public static final String DELETE_AFTER_READING = "DELETE_AFTER_READING";
	/**
	 * 读取之后改名
	 */
	public static final String RENAME_AFTER_READING = "RENAME_AFTER_READING";
	
	/**
	 * 读取之后将源文件转移到另一个文件夹
	 */
	public static final String REMOVE_AFTER_READING = "REMOVE_AFTER_READING";
	
	
	public static final String READ_FILE_SUFFIX = ".read";
	
	/**
	 * 文件目录
	 */
	private String directory = null;
	
	/**
	 * 备份目录，当strategyAfterReading=REMOVE_AFTER_READING时，已经读取的文件被转移到该目录
	 */
	private String bakupDirectory = null;
	
	/**
	 * 读取之后的策略，null或者空字符串表示什么都不做。<br/>
	 * 当该值等于DELETE_AFTER_READING时，表示读取完毕后删除源文件；<br/>
	 * 当该值等于RENAME_AFTER_READING时，表示读取完毕后改源文件名字；尾缀变成“.read”；<br/>
	 * 当该值等于REMOVE_AFTER_READING时，表示读取完毕后源文件被转移到bakupDirectory；
	 */
	private String strategyAfterReading = "";
	
	private String fileNameSuffix = null;//文件名模式，用于匹配已经读取的文件
	
	/**
	 * 每次读取的文件数量，默认值1
	 */
	private Integer batchSize = 1;
	
	private String fileType = FileObject.FILE_TYPE_TEXT;
	
	private String charset = "UTF-8";


	public FileReadServiceDef(){
		super();
	}

	public void afterPropertiesSet(){
		//构造缺省的interface
//		_interface = new CommonInterface();
//		OperationImpl operation = new OperationImpl();
//		operation.setOperationName("readFiles");
//		OutputImpl outputImpl = new OutputImpl();
//		outputImpl.setName("files");
//		outputImpl.setDisplayName("The Files");
//		outputImpl.setDataType(new QName(NameSpaces.JAVA.getUrl(),"java.util.Map"));
//		_interface.getOperations().add(operation);
		
		JavaInterfaceDef javaInterface = new JavaInterfaceDef();
		javaInterface.setInterfaceClassName("org.fireflow.service.file.reader.FileReader");
		this.setInterface(javaInterface);
		
		this.invokerClassName = "org.fireflow.service.file.reader.FileReaderInvoker";

	}
	
	public String getDirectory() {
		return directory;
	}


	public void setDirectory(String directory) {
		this.directory = directory;
	}


	public String getStrategyAfterReading() {
		return strategyAfterReading;
	}


	public void setStrategyAfterReading(String strategyAfterReading) {
		this.strategyAfterReading = strategyAfterReading;
	}


	public String getFileNameSuffix() {
		return fileNameSuffix;
	}


	public void setFileNameSuffix(String fileNameSuffix) {
		this.fileNameSuffix = fileNameSuffix;
	}

	public String getBakupDirectory() {
		return bakupDirectory;
	}

	public void setBakupDirectory(String bakupDirectory) {
		this.bakupDirectory = bakupDirectory;
	}

	public Integer getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	
}
