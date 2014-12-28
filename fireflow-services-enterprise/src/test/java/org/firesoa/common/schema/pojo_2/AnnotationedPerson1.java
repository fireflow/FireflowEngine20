package org.firesoa.common.schema.pojo_2;

import javax.xml.bind.annotation.XmlType;

import org.firesoa.common.schema.JAXBUtil;
import org.firesoa.common.schema.pojo_2.childobj.Address;


//@XmlRootElement(name="person",namespace="http://test")
@XmlType(name="AnnotationedPerson1")
public class AnnotationedPerson1 {
	private String name;
	private SEX sex;
	private int age;
	
	private Address adress ;
	
	
	
	public Address getAdress() {
		return adress;
	}
	public void setAdress(Address adress) {
		this.adress = adress;
	}
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
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
	
	public static void main(String[] args)throws Exception{

		JAXBUtil.generatePojoSchema(AnnotationedPerson1.class);
	}
}
