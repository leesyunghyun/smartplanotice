package smart.planotice.smartplanotice;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class SPN_Object implements Serializable{
	
	public SPN_PhoneInfoObject PhoneObjectMy;
	public SPN_PhoneInfoObject PhoneObjectYou;
	public SPN_Object ReObject;
	public String Message;
	public SPN_Object(SPN_PhoneInfoObject PhoneObjectMy,SPN_PhoneInfoObject PhoneObjectYou,SPN_Object ReObject,String Message)
	{
		this.PhoneObjectMy = PhoneObjectMy; 
		this.PhoneObjectYou = PhoneObjectYou;
		this.ReObject = ReObject;
		this.Message = Message;
	}
}
