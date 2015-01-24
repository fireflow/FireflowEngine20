package org.fireflow.demo.system.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;


/**
* 
*/

@Table("T_sys_config")
public class SysConfig {

	/**
	 * 
	 */
	@Id
	@Column("id")
	private Long id;
	/**
	 * 
	 */
	@Column("config_key")
	private String configKey;
	/**
	 * 
	 */
	@Column("config_value")
	private String configValue;
	/**
	 * 
	 */
	@Column("description")
	private String description;
	/**
	 * 
	 */
	@Column("parent_key")
	private String parentKey;
	/**
	 * 
	 */
	@Column("media_code")
	private String mediaCode;
	/**
	 * 
	 */
	@Column("media_name")
	private String mediaName;
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getConfigKey() {
		return configKey;
	}
	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}
	public String getConfigValue() {
		return configValue;
	}
	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getParentKey() {
		return parentKey;
	}
	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
	}
	public String getMediaCode() {
		return mediaCode;
	}
	public void setMediaCode(String mediaCode) {
		this.mediaCode = mediaCode;
	}
	public String getMediaName() {
		return mediaName;
	}
	public void setMediaName(String mediaName) {
		this.mediaName = mediaName;
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
	
	
	
}