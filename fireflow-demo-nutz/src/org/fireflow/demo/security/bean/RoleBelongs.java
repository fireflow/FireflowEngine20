package org.fireflow.demo.security.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Readonly;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.View;

/**
 * 用户组（部门）下关联的岗位实体。
 * @author 
 *
 */
@Table("T_security_role_belongs")
@View("V_security_role_belongs")
@PK({"orgGroupCode","roleCode"})
public class RoleBelongs {
	/**
	 * 角色所属的部门或者组织机构代号
	 */
	@Column("org_group_code")
	private String orgGroupCode = null;
	
	/**
	 * 角色代号
	 */
	@Column("role_code")
	private String roleCode = null;
	
	@Column("role_name")
	@Readonly
	private String roleName = null;
	
	/**
	 * 最后修改人
	 */
	@Column("last_update_person")
	private String lastUpdatePerson;
	/**
	 * 最后修改时间
	 */
	@Column("last_update_time")
	@Readonly
	private java.util.Date lastUpdateTime;
	public String getOrgGroupCode() {
		return orgGroupCode;
	}
	public void setOrgGroupCode(String orgGroupCode) {
		this.orgGroupCode = orgGroupCode;
	}
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	public String getLastUpdatePerson() {
		return lastUpdatePerson;
	}
	public void setLastUpdatePerson(String lastUpdatePerson) {
		this.lastUpdatePerson = lastUpdatePerson;
	}
	public java.util.Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(java.util.Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	
}
