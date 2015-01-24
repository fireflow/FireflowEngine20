package org.fireflow.demo.misc;

import java.util.HashMap;
import java.util.Map;

import org.fireflow.demo.MainModule;

public class Message {
	boolean ok = true;
	
	String message = null;
	String stack = null;
	Map<String,String> fieldMsg = new HashMap<String,String>();
	
	public Message(){
		
	}
	
	public Message(boolean isOK,String message){
		this.ok = isOK;
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStack() {
		return stack;
	}
	public void setStack(String stack) {
		this.stack = stack;
	}
	public Map<String, String> getFieldMsg() {
		return fieldMsg;
	}
	public void setFieldMsg(Map<String, String> fieldMsg) {
		this.fieldMsg = fieldMsg;
	}
	public boolean isOk() {
		return ok;
	}
	public void setOk(boolean isOK) {
		this.ok = isOK;
	}
	
	public static Message fromJTableMessage(Map<String,Object> jTableMsg){
		Message msg = new Message();
		String result = (String)jTableMsg.get(MainModule.JTABLE_RESULT_KEY);
		if (result!=null && result.equals(MainModule.JTABLE_RESULT_VALUE_OK)){
			msg.setOk(true);
		}else{
			msg.setOk(false);
		}
		msg.setMessage((String)jTableMsg.get(MainModule.JTABLE_MESSAGE_KEY));
		return msg;
	}
	
	public static Message fromThrowable(Throwable t){
		Message msg = new Message();
		msg.setOk(false);
		msg.setMessage(t.getMessage());
		msg.setStack(Utils.exceptionStackToString(t));
		return msg;
	}
}
