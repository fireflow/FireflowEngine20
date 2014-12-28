package org.firesoa.common.schema.pojo_1;

import org.firesoa.common.schema.pojo_1.inner.Street;

public class City {
	String name;
	String countryName;
	Street street;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public Street getStreet() {
		return street;
	}
	public void setStreet(Street street) {
		this.street = street;
	}
	
	
}
