package org.fireflow.service.file.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.exception.ServiceInvocationException;

public class FileWriterImpl implements FileWriter {
	private static final Log log = LogFactory.getLog(FileWriterImpl.class);
	
	FileWriteServiceDef fileWriteService = null;	

	public FileWriteServiceDef getFileWriteService() {
		return fileWriteService;
	}

	public void setFileWriteService(FileWriteServiceDef fileWriteService) {
		this.fileWriteService = fileWriteService;
	}

	public void writeBytesToFile(String fileName, byte[] content) throws ServiceInvocationException{
		if (StringUtils.isEmpty(fileWriteService.getDirectory())){
			throw new ServiceInvocationException("FileWriteService[id="+fileWriteService.getId()+";name="+fileWriteService.getName()+"] is invalid: the directory can NOT be empty.");
		}

		File directory = new File(fileWriteService.getDirectory());
		if (!directory.exists()){
			try{
				boolean b = directory.createNewFile();
				if (!b){
					throw new ServiceInvocationException("FileWriteService[id="+fileWriteService.getId()+";name="+fileWriteService.getName()+"] is invalid: the directory["+fileWriteService.getDirectory()+"] can NOT be created.");
				}
			}catch(Exception e){
				log.error(e);
				throw new ServiceInvocationException("FileWriteService[id="+fileWriteService.getId()+";name="+fileWriteService.getName()+"] is invalid: the directory["+fileWriteService.getDirectory()+"] can NOT be created.");
			}
		}
		
		File f = new File(fileWriteService.getDirectory()+File.separator+fileName);
		if (f.exists()){
			//TODO 缺省覆盖模式
			boolean b = f.delete();
			if (!b){
				throw new ServiceInvocationException("Invoking FileWriteService[id="+fileWriteService.getId()+";name="+fileWriteService.getName()+"] failed: the file["+fileName+"] is exist and can NOT be deleted.");
			}
		}
		
		try{
			FileOutputStream fOut = new FileOutputStream(f);			
			fOut.write(content);
		}catch(IOException e){
			throw new ServiceInvocationException(e);
		}

	}

	public void writeStringToFile(String fileName, String content)throws ServiceInvocationException {
		String s = content;
		if (content==null) s = "";
		try{
			byte[] bytes = s.getBytes(fileWriteService.getCharset());
			this.writeBytesToFile(fileName, bytes);
		}catch(Exception e){
			log.error(e);
		}
	}

}
