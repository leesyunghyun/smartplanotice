package smart.planotice.smartplanotice;

import java.io.Serializable;
import java.nio.channels.SocketChannel;

public class SPN_UserInfo implements Serializable{

	String ChannelName;
	String IMEI;
	String PhoneNumber;
	SocketChannel UserSC;
	String IpAndPhone;
	public SPN_UserInfo(String ChannelName,String IpAndPhone,String IMEI, String PhoneNumber, SocketChannel UserSC)
	{
		this.IpAndPhone = IpAndPhone;
		this.ChannelName = ChannelName;
		this.IMEI = IMEI;
		this.PhoneNumber = PhoneNumber;
		this.UserSC = UserSC;
	}
}
