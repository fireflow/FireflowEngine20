package org.fireflow.demo.common.bean;

import org.nutz.dao.entity.annotation.*;

@Table("T_sys_dictionary")
public class Dictionary {
	
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
	private int sort;
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
	private int status;
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
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
