package org.fireflow.service.webservice.servicemock3;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace="http://model.webservice.fireflow.org/")
public class Address2 {
	private int streetNo;
	private String city;
	public int getStreetNo() {
		return streetNo;
	}
	public void setStreetNo(int streetNo) {
		this.streetNo = streetNo;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	
}
