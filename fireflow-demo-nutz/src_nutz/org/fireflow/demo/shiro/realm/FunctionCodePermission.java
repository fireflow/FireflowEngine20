package org.fireflow.demo.shiro.realm;

import org.apache.shiro.authz.Permission;

/**
 * 基于T_sys_function的code字段的permission
 * @author SX-7F-01-006
 *
 */
public class FunctionCodePermission implements Permission {
	String functionCode = null;
	
	public FunctionCodePermission(String fCode){
		this.functionCode = fCode;
	}

	public String getFunctionCode() {
		return functionCode;
	}



	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}



	@Override
	public boolean implies(Permission arg0) {
		if (arg0==null) return false;
		FunctionCodePermission fPermission = (FunctionCodePermission) arg0;
		if (fPermission.getFunctionCode()==null) return false;
		if (fPermission.getFunctionCode().equals(this.getFunctionCode())){
			return true;
		}
		return false;
	}

}
