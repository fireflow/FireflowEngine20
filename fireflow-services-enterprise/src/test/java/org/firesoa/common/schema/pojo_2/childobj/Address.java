package org.firesoa.common.schema.pojo_2.childobj;

import javax.xml.bind.annotation.XmlType;

//@XmlType(name="Address",namespace="http://abc.com")
public class Address {
	private String country;
	private String state;
	private String city;
	private String street;
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	
	
}
