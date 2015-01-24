package org.fireflow.demo.system.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Readonly;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.View;


/**
* 
*/
@Table("T_sys_function")
@View("V_sys_menu")
public class SysMenu {
	public static final String MENUGROUP_TYPE = "GROUP";
	public static final String MENUITEM_TYPE = "MENU";

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
	@Readonly
	private String code;
	/**
	 * 
	 */
	@Column("name")
	private String name;
	/**
	 * 
	 */
	@Column("type")
	@Readonly
	private String type;
	/**
	 * 
	 */
	@Column("sort")
	private Integer sort;
	/**
	 * 
	 */
	@Column("func_code")
	@Readonly
	private String funcCode;
	/**
	 * 
	 */
	@Column("url")
	private String url;
	/**
	 * 
	 */
	@Column("parent_code")
	@Readonly
	private String parentCode;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public String getFuncCode() {
		return funcCode;
	}
	public void setFuncCode(String funcCode) {
		this.funcCode = funcCode;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	
	
}