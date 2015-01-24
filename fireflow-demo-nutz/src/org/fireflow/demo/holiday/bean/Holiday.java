package org.fireflow.demo.holiday.bean;

import org.nutz.dao.entity.annotation.*;


/**
* 
*/

@Table("T_hr_holidays")
public class Holiday {
	public static final String HOLIDAY_PAID = "paid";
	public static final String HOLIDAY_SICK = "sick";
	public static final String HOLIDAY_ABSENCE="absence";
	public static final String HOLIDAY_MARITAL = "marital";
	public static final String HOLIDAY_MATERNITY = "maternity";
	public static final String HOLIDAY_FUNERAL ="funeral";
	;
	/**
	 * 
	 */
	@Id
	@Column("id")
	private Long id;
	/**
	 * 
	 */
	@Column("employee_id")
	private String employeeId;
	/**
	 * 
	 */
	@Column("year_start")
	private java.util.Date yearStart;
	/**
	 * 
	 */
	@Column("year_end")
	private java.util.Date yearEnd;
	/**
	 * 
	 */
	@Column("paid_vacation_days")
	private Float paidVacationDays=0f;
	/**
	 * 
	 */
	@Column("used_paid_vacation_days")
	private Float usedPaidVacationDays=0f;
	/**
	 * 
	 */
	@Column("used_sick_leave_days")
	private Float usedSickLeaveDays=0f;
	/**
	 * 
	 */
	@Column("used_absence_leave_days")
	private Float usedAbsenceLeaveDays=0f;
	/**
	 * 
	 */
	@Column("used_marital_leave_days")
	private Float usedMaritalLeaveDays=0f;
	/**
	 * 
	 */
	@Column("used_maternity_leave_days")
	private Float usedMaternityLeaveDays=0f;
	/**
	 * 
	 */
	@Column("used_funeral_leave_days")
	private Float usedFuneralLeaveDays=0f;
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
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public java.util.Date getYearStart() {
		return yearStart;
	}
	public void setYearStart(java.util.Date yearStart) {
		this.yearStart = yearStart;
	}
	public java.util.Date getYearEnd() {
		return yearEnd;
	}
	public void setYearEnd(java.util.Date yearEnd) {
		this.yearEnd = yearEnd;
	}
	public Float getPaidVacationDays() {
		return paidVacationDays;
	}
	public void setPaidVacationDays(Float paidVacationDays) {
		this.paidVacationDays = paidVacationDays;
	}
	public Float getUsedPaidVacationDays() {
		return usedPaidVacationDays;
	}
	public void setUsedPaidVacationDays(Float usedPaidVacationDays) {
		this.usedPaidVacationDays = usedPaidVacationDays;
	}
	public Float getUsedSickLeaveDays() {
		return usedSickLeaveDays;
	}
	public void setUsedSickLeaveDays(Float usedSickLeaveDays) {
		this.usedSickLeaveDays = usedSickLeaveDays;
	}
	public Float getUsedAbsenceLeaveDays() {
		return usedAbsenceLeaveDays;
	}
	public void setUsedAbsenceLeaveDays(Float usedAbsenceLeaveDays) {
		this.usedAbsenceLeaveDays = usedAbsenceLeaveDays;
	}
	public Float getUsedMaritalLeaveDays() {
		return usedMaritalLeaveDays;
	}
	public void setUsedMaritalLeaveDays(Float usedMaritalLeaveDays) {
		this.usedMaritalLeaveDays = usedMaritalLeaveDays;
	}
	public Float getUsedMaternityLeaveDays() {
		return usedMaternityLeaveDays;
	}
	public void setUsedMaternityLeaveDays(Float usedMaternityLeaveDays) {
		this.usedMaternityLeaveDays = usedMaternityLeaveDays;
	}
	public Float getUsedFuneralLeaveDays() {
		return usedFuneralLeaveDays;
	}
	public void setUsedFuneralLeaveDays(Float usedFuneralLeaveDays) {
		this.usedFuneralLeaveDays = usedFuneralLeaveDays;
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