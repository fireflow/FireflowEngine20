package org.fireflow.demo.holiday.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Readonly;
import org.nutz.dao.entity.annotation.Table;

/**
* 
*/

@Table("T_hr_leave_request")
public class LeaveRequest {
	
	/**
	 * 
	 */
	@Id
	@Column("id")
	private Long id;
	/**
	 * 
	 */
	@Column("bill_code")
	@Name
	private String billCode;
	/**
	 * 
	 */
	@Column("employee_id")
	private String employeeId;
	
	@Column("employee_name")
	private String employeeName;
	
	@Column("creator_id")
	private String creatorId;
	
	@Column("create_time")
	private Date createTime;
	
	/**
	 * 
	 */
	@Column("leave_type")
	private String leaveType;
	
	@Column("leave_type_name")
	private String leaveTypeName;
	/**
	 * 
	 */
	@Column("from_date")
	private java.util.Date fromDate;
	/**
	 * 
	 */
	@Column("to_date")
	private java.util.Date toDate;
	/**
	 * 
	 */
	@Column("total_days")
	private Float totalDays = 0f;
	/**
	 * 
	 */
	@Column("dept_mgr_id")
	private String deptMgrId;
	/**
	 * 
	 */
	@Column("dept_mgr_name")
	private String deptMgrName;
	/**
	 * 
	 */
	@Column("dept_approve_flag")
	private Integer deptApproveFlag;
	/**
	 * 
	 */
	@Column("dept_approve_time")
	private java.util.Date deptApproveTime;
	/**
	 * 
	 */
	@Column("dept_approve_info")
	private String deptApproveInfo;
	/**
	 * 
	 */
	@Column("director_id")
	private String directorId;
	/**
	 * 
	 */
	@Column("director_name")
	private String directorName;
	/**
	 * 
	 */
	@Column("director_approve_flag")
	private Integer directorApproveFlag;
	/**
	 * 
	 */
	@Column("director_approve_time")
	private java.util.Date directorApproveTime;
	/**
	 * 
	 */
	@Column("director_approve_info")
	private String directorApproveInfo;
	/**
	 * 
	 */
	@Column("ceo_id")
	private String ceoId;
	/**
	 * 
	 */
	@Column("ceo_name")
	private String ceoName;
	/**
	 * 
	 */
	@Column("ceo_approve_flag")
	private Integer ceoApproveFlag;
	/**
	 * 
	 */
	@Column("ceo_approve_time")
	private java.util.Date ceoApproveTime;
	/**
	 * 
	 */
	@Column("ceo_approve_info")
	private String ceoApproveInfo;
	/**
	 * 
	 */
	@Column("financial_record_uid")
	private String financialRecordUid;
	/**
	 * 
	 */
	@Column("financial_record_uname")
	private String financialRecordUname;
	/**
	 * 
	 */
	@Column("financial_record_time")
	private java.util.Date financialRecordTime;
	/**
	 * 
	 */
	@Column("financial_record_info")
	private String financialRecordInfo;
	/**
	 * 
	 */
	@Column("last_update_person")
	private String lastUpdatePerson;
	/**
	 * 
	 */
	@Column("last_update_time")
	@Readonly
	private java.util.Date lastUpdateTime;
	
	List<LeaveRequestDetail> leaveRequestDetailList = new ArrayList<LeaveRequestDetail>();
	
	public List<LeaveRequestDetail> getLeaveRequestDetailList(){
		return leaveRequestDetailList;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBillCode() {
		return billCode;
	}
	public void setBillCode(String billCode) {
		this.billCode = billCode;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public String getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}
	public java.util.Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(java.util.Date fromDate) {
		this.fromDate = fromDate;
	}
	public java.util.Date getToDate() {
		return toDate;
	}
	public void setToDate(java.util.Date toDate) {
		this.toDate = toDate;
	}
	public Float getTotalDays() {
		return totalDays;
	}
	public void setTotalDays(Float totalDays) {
		this.totalDays = totalDays;
	}
	public String getDeptMgrId() {
		return deptMgrId;
	}
	public void setDeptMgrId(String deptMgrId) {
		this.deptMgrId = deptMgrId;
	}
	public String getDeptMgrName() {
		return deptMgrName;
	}
	public void setDeptMgrName(String deptMgrName) {
		this.deptMgrName = deptMgrName;
	}
	public Integer getDeptApproveFlag() {
		return deptApproveFlag;
	}
	public void setDeptApproveFlag(Integer deptApproveFlag) {
		this.deptApproveFlag = deptApproveFlag;
	}
	public java.util.Date getDeptApproveTime() {
		return deptApproveTime;
	}
	public void setDeptApproveTime(java.util.Date deptApproveTime) {
		this.deptApproveTime = deptApproveTime;
	}
	public String getDeptApproveInfo() {
		return deptApproveInfo;
	}
	public void setDeptApproveInfo(String deptApproveInfo) {
		this.deptApproveInfo = deptApproveInfo;
	}
	public String getDirectorId() {
		return directorId;
	}
	public void setDirectorId(String directorId) {
		this.directorId = directorId;
	}
	public String getDirectorName() {
		return directorName;
	}
	public void setDirectorName(String directorName) {
		this.directorName = directorName;
	}
	public Integer getDirectorApproveFlag() {
		return directorApproveFlag;
	}
	public void setDirectorApproveFlag(Integer directorApproveFlag) {
		this.directorApproveFlag = directorApproveFlag;
	}
	public java.util.Date getDirectorApproveTime() {
		return directorApproveTime;
	}
	public void setDirectorApproveTime(java.util.Date directorApproveTime) {
		this.directorApproveTime = directorApproveTime;
	}
	public String getDirectorApproveInfo() {
		return directorApproveInfo;
	}
	public void setDirectorApproveInfo(String directorApproveInfo) {
		this.directorApproveInfo = directorApproveInfo;
	}
	public String getCeoId() {
		return ceoId;
	}
	public void setCeoId(String ceoId) {
		this.ceoId = ceoId;
	}
	public String getCeoName() {
		return ceoName;
	}
	public void setCeoName(String ceoName) {
		this.ceoName = ceoName;
	}
	public Integer getCeoApproveFlag() {
		return ceoApproveFlag;
	}
	public void setCeoApproveFlag(Integer ceoApproveFlag) {
		this.ceoApproveFlag = ceoApproveFlag;
	}
	public java.util.Date getCeoApproveTime() {
		return ceoApproveTime;
	}
	public void setCeoApproveTime(java.util.Date ceoApproveTime) {
		this.ceoApproveTime = ceoApproveTime;
	}
	public String getCeoApproveInfo() {
		return ceoApproveInfo;
	}
	public void setCeoApproveInfo(String ceoApproveInfo) {
		this.ceoApproveInfo = ceoApproveInfo;
	}
	public String getFinancialRecordUid() {
		return financialRecordUid;
	}
	public void setFinancialRecordUid(String financialRecordUid) {
		this.financialRecordUid = financialRecordUid;
	}
	public String getFinancialRecordUname() {
		return financialRecordUname;
	}
	public void setFinancialRecordUname(String financialRecordUname) {
		this.financialRecordUname = financialRecordUname;
	}
	public java.util.Date getFinancialRecordTime() {
		return financialRecordTime;
	}
	public void setFinancialRecordTime(java.util.Date financialRecordTime) {
		this.financialRecordTime = financialRecordTime;
	}
	public String getFinancialRecordInfo() {
		return financialRecordInfo;
	}
	public void setFinancialRecordInfo(String financialRecordInfo) {
		this.financialRecordInfo = financialRecordInfo;
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

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getLeaveTypeName() {
		return leaveTypeName;
	}

	public void setLeaveTypeName(String leaveTypeName) {
		this.leaveTypeName = leaveTypeName;
	}
	
	
}