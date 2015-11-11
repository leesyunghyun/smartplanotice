package smart.planotice.smartplanotice;

import java.io.Serializable;

public class SPN_PhoneInfoObject implements Serializable{
	
	public String PhoneID;
	public String MacAddr;
	public String PhoneNumber;
	public String Longtitude;
	public String Latitude;
	SPN_GPSInfoObject GPSInfo;
	
	public SPN_PhoneInfoObject(String PhoneID, String MacAddr, String PhoneNumber,String Longtitude, String Latitude, SPN_GPSInfoObject GPSInfo)
	{
		this.PhoneID = PhoneID; 
		this.MacAddr = MacAddr;
		this.PhoneNumber = PhoneNumber;
		this.Longtitude = Longtitude;
		this.Latitude = Latitude;
		this.GPSInfo = GPSInfo;
	}
}
