package smart.planotice.smartplanotice;

import java.io.Serializable;
import java.util.ArrayList;

public class SPN_GPSInfoObject implements Serializable{

	public ArrayList<String> SPN_Vertex;
	public int ChildIndex;
	
	public SPN_GPSInfoObject(ArrayList<String> SPN_Vertex, int ChildIndex)
	{
		this.SPN_Vertex = SPN_Vertex;
		this.ChildIndex = ChildIndex;
	}
}
