package org.fireflow.samples.pojo;

import java.util.List;

public class Company {
	String id = null;
	String name = null;
	String address = null;
	List<Employee> allEmployees = null;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public List<Employee> getAllEmployees() {
		return allEmployees;
	}
	public void setAllEmployees(List<Employee> allEmployees) {
		this.allEmployees = allEmployees;
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append("<company ")
			.append("id=\"").append(id).append("\"")
			.append(" name=\"").append(name).append("\"")
			.append(" address=\"").append(address).append("\">");
		if (allEmployees!=null){
			for (Employee employee: allEmployees){
				buf.append("\n\t<employee")
					.append(" id=\"").append(employee.getId()).append("\"")
					.append(" name=\"").append(employee.getName()).append("\"")
					.append(" age=\"").append(employee.getAge()).append("\"")
					.append(" salary=").append(employee.getSalary()).append("\"")
					.append(" deptId=\"").append(employee.getDeptId()).append("\"/>");
			}
		}
		
		buf.append("\n</company>");
		
		return buf.toString();
	}
}
