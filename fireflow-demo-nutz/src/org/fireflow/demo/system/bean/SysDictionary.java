package org.fireflow.demo.system.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;


/**
* 
*/

@Table("T_sys_dictionary")
public class SysDictionary {

	/**
	 * 
	 */
	@Id
	@Column("id")
	private Long id;
	/**
	 * 
	 */
	@Column("dic_type")
	private String dicType;
	/**
	 * 
	 */
	@Column("dic_key")
	private String dicKey;
	/**
	 * 
	 */
	@Column("dic_value")
	private String dicValue;
	/**
	 * 
	 */
	@Column("dic_description")
	private String dicDescription;
	/**
	 * 
	 */
	@Column("dic_mnemonic")
	private String dicMnemonic;
	/**
	 * 
	 */
	@Column("sort")
	private Integer sort;
	/**
	 * 
	 */
	@Column("parent_key")
	private String parentKey;
	/**
	 * 
	 */
	@Column("last_update_person")
	private String lastUpdatePerson;
	/**
	 * 
	 */
	@Column("last_update_time")
	private java.util.Date lastUpdateTime;
	/**
	 * 
	 */
	@Column("status")
	private Integer status;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDicType() {
		return dicType;
	}
	public void setDicType(String dicType) {
		this.dicType = dicType;
	}
	public String getDicKey() {
		return dicKey;
	}
	public void setDicKey(String dicKey) {
		this.dicKey = dicKey;
	}
	public String getDicValue() {
		return dicValue;
	}
	public void setDicValue(String dicValue) {
		this.dicValue = dicValue;
	}
	public String getDicDescription() {
		return dicDescription;
	}
	public void setDicDescription(String dicDescription) {
		this.dicDescription = dicDescription;
	}
	public String getDicMnemonic() {
		return dicMnemonic;
	}
	public void setDicMnemonic(String dicMnemonic) {
		this.dicMnemonic = dicMnemonic;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public String getParentKey() {
		return parentKey;
	}
	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
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
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	
}