package org.fireflow.demo.holiday.bean;

import org.nutz.dao.entity.annotation.*;
/**
* 
*/
@Table("T_hr_leave_request_detail")
public class LeaveRequestDetail {
	private int sn  =0;//序号，用于jtable主键

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
	private String billCode;
	/**
	 * 
	 */
	@Column("leave_date")
	private java.util.Date leaveDate;
	/**
	 * 
	 */
	@Column("time_section")
	private Integer timeSection;
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
	public java.util.Date getLeaveDate() {
		return leaveDate;
	}
	public void setLeaveDate(java.util.Date leaveDate) {
		this.leaveDate = leaveDate;
	}
	public Integer getTimeSection() {
		return timeSection;
	}
	public void setTimeSection(Integer timeSection) {
		this.timeSection = timeSection;
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
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
}