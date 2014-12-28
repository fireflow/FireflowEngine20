package org.fireflow.service.file;

import java.util.Date;

public class FileObjectImpl implements FileObject {
	String fileName;
	String absolutePath;
	String fileType;
	String charset;
	byte[] content;
	long lastestEditTime;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getAbsolutePath() {
		return absolutePath;
	}
	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
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
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public long getLastModified() {
		return lastestEditTime;
	}
	public void setLastestEditTime(long lastestEditTime) {
		this.lastestEditTime = lastestEditTime;
	}
	

}
