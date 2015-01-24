package org.fireflow.demo.system.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
* 
*/

@Table("T_sys_function")
public class SysFunction implements java.lang.Comparable{

	/**
	 * 
	 */
	@Id
	@Column("id")
	private Long id;
	/**
	 * 
	 */
	@Column("code")
	private String code;
	/**
	 * 
	 */
	@Column("name")
	private String name;
	/**
	 * 
	 */
	@Column("scope")
	private String scope;
	/**
	 * 
	 */
	@Column("parent_code")
	private String parentCode;
	/**
	 * 
	 */
	@Column("description")
	private String description;
	/**
	 * 
	 */
	@Column("url")
	private String url;

	/**
	 * 
	 */
	@Column("sort")
	private Integer sort;
	
	@Column("ftype")
	private String ftype;
	
	//不保存到数据库，只作赋予权限时是否已经赋予判断
	private boolean ischecked;

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
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	public boolean getIschecked() {
		return ischecked;
	}
	public void setIschecked(boolean ischecked) {
		this.ischecked = ischecked;
	}
	public String getFtype() {
		return ftype;
	}
	public void setFtype(String ftype) {
		this.ftype = ftype;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SysFunction other = (SysFunction) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}
	@Override
	public int compareTo(Object arg0) {
		if (!(arg0 instanceof SysFunction)){
			return -1;
		}
		SysFunction tmpFunc = (SysFunction)arg0;
		
		return this.getCode().compareTo(tmpFunc.getCode());
	}
	
}