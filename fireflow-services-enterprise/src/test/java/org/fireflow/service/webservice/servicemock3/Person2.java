package org.fireflow.service.webservice.servicemock3;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(namespace="http://model.webservice.fireflow.org/")
//@XmlRootElement
public class Person2 {
	private String name ;
	private SEX sex ;
	private Address2 address ;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SEX getSex() {
		return sex;
	}
	public void setSex(SEX sex) {
		this.sex = sex;
	}
	public Address2 getAddress() {
		return address;
	}
	public void setAddress(Address2 address) {
		this.address = address;
	}
	
	
}
