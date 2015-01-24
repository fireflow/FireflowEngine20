package org.fireflow.demo.security.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Readonly;
import org.nutz.dao.entity.annotation.Table;

/**
* 
*/
@Table("T_security_role")
public class Role {

	/**
	 * 
	 */
	@Id
	@Column("id")
	private Long id;
	/**
	 * 角色表代码
	 */
	@Name
	@Column("code")
	private String code;
	/**
	 * 角色名称
	 */
	@Column("name")
	private String name;
	/**
	 * 角色描述
	 */
	@Column("description")
	private String description;
	
	@Column("is_built_in")
	private boolean isBuiltIn = false;
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
	
	@Column("is_position")
	private boolean isPosition = false;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	

	public boolean isBuiltIn() {
		return isBuiltIn;
	}
	public void setBuiltIn(boolean isBuiltIn) {
		this.isBuiltIn = isBuiltIn;
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
	public boolean isPosition() {
		return isPosition;
	}
	public void setPosition(boolean isPosition) {
		this.isPosition = isPosition;
	}
	
}