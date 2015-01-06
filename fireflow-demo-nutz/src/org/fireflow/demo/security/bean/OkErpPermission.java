package org.fireflow.demo.security.bean;

import org.nutz.dao.entity.annotation.*;

/**
* 
*/
@Table("T_security_permissions")
public class OkErpPermission {
	public static final String GRANTEE_TYPE_USER = "U";
	public static final String GRANTEE_TYPE_GROUP = "G";
	public static final String GRANTEE_TYPE_ROLE = "R";

	/**
	 * 
	 */
	@Id
	@Column("id")
	private Long id;
	/**
	 * 
	 */
	@Column("grantee_code")
	private String granteeCode;
	/**
	 * 
	 */
	@Column("grantee_name")
	private String granteeName;
	/**
	 * 
	 */
	@Column("grantee_type")
	private String granteeType;
	/**
	 * 
	 */
	@Column("function_code")
	private String functionCode;
	/**
	 * 
	 */
	@Column("function_name")
	private String functionName;
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
	public String getGranteeCode() {
		return granteeCode;
	}
	public void setGranteeCode(String granteeCode) {
		this.granteeCode = granteeCode;
	}
	public String getGranteeName() {
		return granteeName;
	}
	public void setGranteeName(String granteeName) {
		this.granteeName = granteeName;
	}
	public String getGranteeType() {
		return granteeType;
	}
	public void setGranteeType(String granteeType) {
		this.granteeType = granteeType;
	}
	public String getFunctionCode() {
		return functionCode;
	}
	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((functionCode == null) ? 0 : functionCode.hashCode());
		result = prime * result
				+ ((granteeCode == null) ? 0 : granteeCode.hashCode());
		result = prime * result
				+ ((granteeType == null) ? 0 : granteeType.hashCode());
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
		OkErpPermission other = (OkErpPermission) obj;
		if (functionCode == null) {
			if (other.functionCode != null)
				return false;
		} else if (!functionCode.equals(other.functionCode))
			return false;
		if (granteeCode == null) {
			if (other.granteeCode != null)
				return false;
		} else if (!granteeCode.equals(other.granteeCode))
			return false;
		if (granteeType == null) {
			if (other.granteeType != null)
				return false;
		} else if (!granteeType.equals(other.granteeType))
			return false;
		return true;
	}

	
	
	
}