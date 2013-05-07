/**
 * 
 */
package cz.fim.project.data;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Roman
 * 
 */

@DatabaseTable
public class Clients {

	@DatabaseField(generatedId = true)
	int id;

	@DatabaseField
	String firstname;
	
	@DatabaseField
	String lastname;

	@DatabaseField
	String city, address;

	@DatabaseField
	String postalcode;
	
	@SerializedName("image_path")
	@DatabaseField (defaultValue = "null")
	String obrSign;

	@SerializedName("phone")
	@DatabaseField
	String myPhonenumber;

	@DatabaseField
	Date date;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
	private MyService myService;

	@DatabaseField (defaultValue = "0")
	private double gpsLatitude;

	@DatabaseField (defaultValue = "0")
	private double gpsLongitude;

	public Clients() {
	};


	public Clients(String firstname, String lastname, String city,
			String address, String postalcode, String myPhonenumber, Date date,
			MyService myService) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
		this.city = city;
		this.address = address;
		this.postalcode = postalcode;
		this.myPhonenumber = myPhonenumber;
		this.date = date;
		this.myService = myService;
	}
	
	public Clients(String firstname, String lastname, String address,
			String city, String postalcode, String obrSign,
			String myPhonenumber) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.city = city;
		this.address = address;
		this.postalcode = postalcode;
		this.myPhonenumber = myPhonenumber;
	}


	public int getId() {
		return id;
	}


	public String getFirstname() {
		return firstname;
	}


	public String getLastname() {
		return lastname;
	}


	public String getCity() {
		return city;
	}


	public String getAddress() {
		return address;
	}


	public String getPostalcode() {
		return postalcode;
	}


	public String getObrSign() {
		return obrSign;
	}


	public String getMyPhonenumber() {
		return myPhonenumber;
	}


	public Date getDate() {
		return date;
	}


	public MyService getMyService() {
		return myService;
	}


	public double getGpsLatitude() {
		return gpsLatitude;
	}


	public double getGpsLongitude() {
		return gpsLongitude;
	}


	public void setId(int id) {
		this.id = id;
	}


	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}


	public void setLastname(String lastname) {
		this.lastname = lastname;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}


	public void setObrSign(String obrSign) {
		this.obrSign = obrSign;
	}


	public void setMyPhonenumber(String myPhonenumber) {
		this.myPhonenumber = myPhonenumber;
	}


	public void setDate(Date date) {
		this.date = date;
	}


	public void setMyService(MyService myService) {
		this.myService = myService;
	}


	public void setGpsLatitude(double gpsLatitude) {
		this.gpsLatitude = gpsLatitude;
	}


	public void setGpsLongitude(double gpsLongitude) {
		this.gpsLongitude = gpsLongitude;
	}

}
