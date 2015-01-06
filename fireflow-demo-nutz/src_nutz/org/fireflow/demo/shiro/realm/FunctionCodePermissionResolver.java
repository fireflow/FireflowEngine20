package org.fireflow.demo.shiro.realm;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;

public class FunctionCodePermissionResolver implements PermissionResolver {

	@Override
	public Permission resolvePermission(String arg0) {
		return new FunctionCodePermission(arg0);
	}

}
