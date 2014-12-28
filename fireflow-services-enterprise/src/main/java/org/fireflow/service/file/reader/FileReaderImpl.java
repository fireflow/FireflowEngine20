package org.fireflow.service.file.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.Message;
import org.fireflow.engine.invocation.impl.MessageImpl;
import org.fireflow.service.file.FileObject;
import org.fireflow.service.file.FileObjectImpl;

public class FileReaderImpl implements FileReader{
	private static final Log log = LogFactory.getLog(FileReaderImpl.class);
	FileReadServiceDef service = null;
	
	public void setFileReadService(FileReadServiceDef svc){
		this.service = svc;
	}
	
	public FileReadServiceDef getFileReadService(){
		return service;
	}
	
	public Message<List<FileObject>> readFile(Long startTime)throws ServiceInvocationException{
		//首先进行配置校验
		if (service==null){
			throw new ServiceInvocationException("FileReadService configuration object is null.");
		}
		if (StringUtils.isEmpty(service.getDirectory())){
			throw new ServiceInvocationException("FileReadService[id="+service.getId()+";name="+service.getName()+"] is invalid: the directory can NOT be empty.");
		}
		
		
		if (FileReadServiceDef.REMOVE_AFTER_READING.equals(service.getStrategyAfterReading()) && 
				StringUtils.isEmpty(service.getBakupDirectory())){
			throw new ServiceInvocationException("FileReadService[id="+service.getId()+";name="+service.getName()+"] is invalid: the bakup directory can NOT be empty when the value of StrategyAfterReading is REMOVE_AFTER_READING");
		}
		File directory = new File(service.getDirectory());
		if (!directory.exists() || !directory.isDirectory()){
			throw new ServiceInvocationException("FileReadService[id="+service.getId()+";name="+service.getName()+"] is invalid: the directory '"+service.getDirectory()+"' is empty or it is not a directory. ");

		}
		File bakupDirectory = null;
		if (FileReadServiceDef.REMOVE_AFTER_READING.equals(service.getStrategyAfterReading())){
			bakupDirectory = new File(service.getBakupDirectory());
			if (!bakupDirectory.exists() ||  !bakupDirectory.isDirectory()){
				throw new ServiceInvocationException("FileReadService[id="+service.getId()+";name="+service.getName()+"] is invalid: the bakup directory '"+service.getBakupDirectory()+"' is  empty or it is not a directory. ");

			}
		}
		
		//读取文件
		File[] fileList = directory.listFiles(new FilenameFilter(){

			public boolean accept(File dir, String name) {
				String suffixList = service.getFileNameSuffix();
				if (StringUtils.isEmpty(suffixList)){
					return true;
				}
				StringTokenizer tokenizer = new StringTokenizer(suffixList,",");
				while(tokenizer.hasMoreTokens()){
					String suffix = tokenizer.nextToken();
					if (name.endsWith(suffix)){
						return true;
					}
				}
				return false;
			}
			
		});
		
		MessageImpl<List<FileObject>> result = new  MessageImpl<List<FileObject>>();
		List<FileObject> readFileList = new ArrayList<FileObject>();
		result.setPayload(readFileList);
		if (fileList==null || fileList.length==0)return result;
		
		//先把fileList按照lastModified顺序排序		
		List<File> fileList2 = new ArrayList<File>();
		Collections.addAll(fileList2, fileList);
		Collections.sort(fileList2, new Comparator<File>(){
			public int compare(File o1, File o2) {
				if (o1.lastModified()<o2.lastModified()){
					return -1;
				}else if (o1.lastModified()>o2.lastModified()){
					return 1;
				}else{
					return 0;
				}
			}
			
		});
		
		int batchSize = service.getBatchSize()<=0?1:service.getBatchSize();
		for (File f : fileList){
			if (startTime==null || startTime<=0 || (startTime>0 && f.lastModified()>startTime)){
				FileObjectImpl fObj = new FileObjectImpl();
				fObj.setFileName(f.getName());
				fObj.setLastestEditTime(f.lastModified());
				fObj.setContent(readFile(f));
				fObj.setAbsolutePath(f.getAbsolutePath());
				fObj.setCharset(service.getCharset());
				fObj.setFileType(service.getFileType());
				readFileList.add(fObj);
				
				//将文件改名或者转移
				if (FileReadServiceDef.RENAME_AFTER_READING.equals(service.getStrategyAfterReading())){
					File newFile = new File(f.getAbsolutePath()+FileReadServiceDef.READ_FILE_SUFFIX);
					if (newFile.exists()){
						newFile.delete();
					}
					boolean b = f.renameTo(newFile);
					
				}else if (FileReadServiceDef.REMOVE_AFTER_READING.equals(service.getStrategyAfterReading())){
					File newFile = new File(bakupDirectory.getAbsolutePath()+File.separator+f.getName());
					if (newFile.exists()){
						newFile.delete();
					}
					boolean b = f.renameTo(newFile);
				}
			}
			
			if (readFileList.size()>=batchSize){
				break;
			}
		}
		
		if (readFileList.size()>0){
			Long lastModified = readFileList.get(readFileList.size()-1).getLastModified();
			result.getHeaders().put("NEXT_START_TIME", lastModified.toString());
		}

		return result;
	}
	
	private byte[] readFile(File f){
		try{
	        int byteread = 0;
	        int totalRead = 0;
	        FileInputStream fIn = new FileInputStream(f);
	        byte[] content = new byte[fIn.available()];
	        int offset = 0;
	        int numRead = 0;
	        while (offset < content.length
	               && (numRead=fIn.read(content, offset, content.length-offset)) >= 0) {
	            offset += numRead;
	        }

	        // Ensure all the bytes have been read in
	        if (offset < content.length) {
	            throw new IOException("Could not completely read file "+f.getName());
	        }

	        fIn.close();
			return content;
		}catch(IOException e){
			log.error(e);
			return null;
		}

	}
}
