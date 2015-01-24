package org.fireflow.demo.security.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Readonly;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.View;

/**
* 
*/
@Table("T_security_user_role")
@View("V_security_user_role")
@PK({"userCode","roleCode"})
public class UserRole {

	/**
	 * 
	 */
	@Id
	@Column("id")
	private Long id;
	/**
	 * 用户编码
	 */
	@Column("user_code")
	private String userCode;
	
	@Column("user_name")
	@Readonly
	private String userName;
	/**
	 * 角色编码
	 */
	@Column("role_code")
	private String roleCode;
	
	@Column("role_name")
	@Readonly
	private String roleName;
	
	@Column("group_code")
	private String groupCode ;
	
	/**
	 * 最后修改人
	 */
	@Column("last_update_person")
	private String lastUpdatePerson;
	/**
	 * 最后修改时间
	 */
	@Column("last_update_time")
	private java.util.Date lastUpdateTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
}