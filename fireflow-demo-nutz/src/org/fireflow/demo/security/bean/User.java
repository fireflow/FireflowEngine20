package org.fireflow.demo.security.bean;

import java.io.Serializable;
import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Readonly;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.View;

/**
* 
*/
@Table("T_security_user")
@View("V_security_user")
public class User implements Serializable{
	public static final int STATUS_OK = 3;
	public static final int STATUS_LOCKED = 0;

	/**
	 * 
	 */
	@Id
	@Column("id")
	private Long id;
	/**
	 * 
	 */
	@Column("login_name")
	@Name
	private String loginName;
	/**
	 * 
	 */
	@Column("pwd")
	private String pwd;
	/**
	 * 
	 */
	@Column("salt")
	private String salt;
	/**
	 * 
	 */
	@Column("status")
	private Integer status;
	/**
	 * 
	 */
	@Column("name")
	private String name;
	/**
	 * 
	 */
	@Column("org_code")
	private String orgCode;
	/**
	 * 
	 */
	@Column("org_name")
	private String orgName;
	/**
	 * 
	 */
	@Column("last_update_person")
	private String lastUpdatePerson;
	/**
	 * 
	 */
	@Readonly
	@Column("last_update_time")
	private java.util.Date lastUpdateTime;
	/**
	 * 
	 */
	@Column("security_key")
	private String securityKey;
	/**
	 * 
	 */
	@Column("is_admin")
	private Integer isAdmin;
	/**
	 * 
	 */
	@Column("tel")
	private String tel;
	
	@Column("group_code")
	private String groupCode;
	
	@Column("group_name")
	private String groupName;
	/**
	 * 账户过期时间
	 */
	@Column("expire_date")
	private Date expireDate;
	
	@Column("must_change_pwd")
	private boolean mustChangePwd;
	
	@Column("employee_id")
	private String employeeId;
	
	@Readonly
	@Column("roleNames")
	private String roleNames;
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
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
	public String getSecurityKey() {
		return securityKey;
	}
	public void setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
	}
	public Integer getIsAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(Integer isAdmin) {
		this.isAdmin = isAdmin;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public boolean isMustChangePwd() {
		return mustChangePwd;
	}
	
	public void setMustChangePwd(boolean mustChangePwd) {
		this.mustChangePwd = mustChangePwd;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(String roleNames) {
		this.roleNames = roleNames;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	
}