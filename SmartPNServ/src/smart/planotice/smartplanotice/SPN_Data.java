package smart.planotice.smartplanotice;

import java.lang.reflect.Array;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class SPN_Data {
	private Connection con;
	private Statement stmt;
	private ResultSet rs = null;
	private SPN_PhoneInfoObject PhoneInfoMy;
	private SPN_PhoneInfoObject PhoneInfoYou;
	private Timer DTimer;
	private Timer ReqGpsState01Timer;
	private SPN_UserInfo UserInfo;

	public void Dataopen() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager
					.getConnection("jdbc:mysql://localhost/smartplanotice?user=root&password=0000");
			stmt = con.createStatement();
		} catch (Exception e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}
	}

	public void DataClose(Vector UserVector, SocketChannel UserSC) {
		for (int i = 0; i < UserVector.size(); i++) {
			SPN_UserInfo User = (SPN_UserInfo) UserVector.get(i);
			if ((UserSC.socket().getInetAddress().getHostAddress().toString()
					+ "&" + User.PhoneNumber).equals(User.IpAndPhone)) {
				System.out.println(User.ChannelName
						+ "의 벡터제거 : "
						+ UserInfo.IMEI
						+ " / "
						+ UserSC.socket().getInetAddress().getHostAddress()
								.toString() + "&"
						+ User.PhoneNumber.replace("+82", "0"));
				UserVector.remove(User);
			}
		}
	}

	public SPN_Data() {
		Dataopen();
	}

	public SPN_UserInfo AddVector(String ChannelName,
			SPN_PhoneInfoObject PhoneMy, Vector UserVector, SocketChannel UserSC) {
		boolean isAlieVector = false;
		for (int i = 0; i < UserVector.size(); i++) {
			SPN_UserInfo User = (SPN_UserInfo) UserVector.get(i);
			if (User.PhoneNumber
					.equals(PhoneMy.PhoneNumber.replace("+82", "0"))
					&& User.ChannelName.equals(ChannelName)) {
				isAlieVector = true;
				break;
			}
		}

		if (!isAlieVector) {
			UserInfo = new SPN_UserInfo(ChannelName, UserSC.socket()
					.getInetAddress().getHostAddress().toString()
					+ "&" + PhoneMy.PhoneNumber.replace("+82", "0"),
					PhoneMy.PhoneID, PhoneMy.PhoneNumber.replace("+82", "0"),
					UserSC);
			System.out.println(ChannelName
					+ "의 벡터추가 : "
					+ PhoneMy.PhoneID
					+ " / "
					+ UserSC.socket().getInetAddress().getHostAddress()
							.toString() + "&"
					+ PhoneMy.PhoneNumber.replace("+82", "0"));
			UserVector.add(UserInfo);
		}
		return UserInfo;
	}

	public String CheckAddress(SPN_PhoneInfoObject PhoneMy) {
		String str = "SELECT * FROM user_info WHERE PhoneID ='"
				+ PhoneMy.PhoneID + "' and MacAddr ='" + PhoneMy.MacAddr + "';";
		String str2 = "";
		try {
			rs = stmt.executeQuery(str);
			rs.first();
			if (rs.getRow() == 0) {
				str2 = SPN_Preferense.EmptyState;
				return str2;
			} else {
				switch (rs.getInt("State")) {
				case 0:
					// 가입만 하고 관계는 없는거
					str2 = SPN_Preferense.NothingState;
					break;
				case 1:
					// 아이
					str2 = SPN_Preferense.ChildState;
					break;
				case 2:
					// 부모
					str2 = SPN_Preferense.ParentState;
					break;
				}

				return str2;
			}
		} catch (SQLException e) {
			// TODO 자동 생성된 catch 블록
			str2 = SPN_Preferense.EmptyState;
			return str2;
		}

	}

	public String RegisterPhone(SPN_PhoneInfoObject PhoneMy) {
		String PhoneNumber;

		PhoneNumber = PhoneMy.PhoneNumber.replace("+82", "0");

		String str = "INSERT INTO user_info(PhoneID,MacAddr,PhoneNumber) values('"
				+ PhoneMy.PhoneID
				+ "', '"
				+ PhoneMy.MacAddr
				+ "', '"
				+ PhoneNumber + "');";
		String str2 = "";
		try {
			if (PhoneNumber.equals("null")) {
				str2 = SPN_Preferense.ErrorState01;
				return str2;
			}
			stmt.execute(str);
			str2 = SPN_Preferense.OkState;
			return str2;

		} catch (SQLException e) {
			// TODO 자동 생성된 catch 블록
			str2 = SPN_Preferense.ErrorState01;
			return str2;
		}
	}

	public String CheckChild(SPN_PhoneInfoObject PhoneMy,
			SPN_PhoneInfoObject PhoneYou) {
		String str = "SELECT PhoneNumber FROM user_info where PhoneNumber = '"
				+ PhoneYou.PhoneNumber.replace("+82", "0") + "';";

		String str2 = "";
		if (PhoneMy.PhoneNumber.replace("+82", "0").equals(
				PhoneYou.PhoneNumber.replace("+82", "0"))) {
			str2 = SPN_Preferense.ErrorState02;
			return str2;
		}

		try {
			rs = stmt.executeQuery(str);
			rs.first();
			if (rs.getRow() == 0) {
				str2 = SPN_Preferense.FailState;
				return str2;
			}

			str = "SELECT PhoneParrentID FROM user_relation where PhoneParrentID ='"
					+ PhoneMy.PhoneNumber.replace("+82", "0")
					+ "' and PhoneChildID ='"
					+ PhoneYou.PhoneNumber.replace("+82", "0") + "';";
			rs = stmt.executeQuery(str);
			rs.first();
			if (rs.getRow() == 0) {
				str2 = SPN_Preferense.SuccessState;
				return str2;
			}
			str2 = SPN_Preferense.ErrorState01;
			return str2;
		} catch (SQLException e) {
			// TODO 자동 생성된 catch 블록
			str2 = SPN_Preferense.ErrorState01;
			return str2;
		}
	}

	public String AddChild(SPN_PhoneInfoObject PhoneMy,
			SPN_PhoneInfoObject PhoneYou) {
		String str = "SELECT * FROM serv_info where My ='"
				+ PhoneMy.PhoneNumber + "' and You='" + PhoneYou.PhoneNumber
				+ "' and mRandom=" + Integer.parseInt(PhoneYou.PhoneID) + ";";

		String str2 = "";

		int RelationCount = 0;
		try {
			rs = stmt.executeQuery(str);
			rs.first();
			if (rs.getRow() == 0) {
				return SPN_Preferense.ErrorState01;
			}

			str = "INSERT INTO user_relation(PhoneParrentID,PhoneChildID) values('"
					+ PhoneMy.PhoneNumber
					+ "', '"
					+ PhoneYou.PhoneNumber
					+ "');";
			stmt.execute(str);

			str = "SELECT RelationCount FROM user_info where PhoneNumber ='"
					+ PhoneMy.PhoneNumber + "';";
			rs = stmt.executeQuery(str);
			rs.first();
			RelationCount = rs.getInt("RelationCount");
			RelationCount++;

			str = "UPDATE user_info SET RelationCount=" + RelationCount
					+ ", State = 2 where PhoneNumber ='" + PhoneMy.PhoneNumber
					+ "';";
			stmt.executeUpdate(str);
			str = "UPDATE user_info SET State = 1 where PhoneNumber ='"
					+ PhoneYou.PhoneNumber + "';";
			stmt.executeUpdate(str);

			str = "delete from serv_info where My ='" + PhoneMy.PhoneNumber
					+ "' and You ='" + PhoneYou.PhoneNumber + "';";
			stmt.execute(str);

			str2 = SPN_Preferense.SuccessState;
			return str2;
		} catch (SQLException e) {
			// TODO 자동 생성된 catch 블록
			str2 = SPN_Preferense.ErrorState02;
			return str2;
		}
	}

	public String CountDownRandom(String PhoneMyNum, String PhoneYouNum,
			int nRandom) {
		String str = "select * from serv_info where My ='"
				+ PhoneMyNum.replace("+82", "0") + "' and You ='"
				+ PhoneYouNum.replace("+82", "0") + "';";

		String str2 = "";
		try {
			rs = stmt.executeQuery(str);
			rs.first();
			if (rs.getRow() == 0) {
				str = "INSERT INTO serv_info(My,You,mRandom) VALUES('"
						+ PhoneMyNum.replace("+82", "0") + "', '"
						+ PhoneYouNum.replace("+82", "0") + "', " + nRandom
						+ ");";
				stmt.execute(str);
				DTimer = new Timer();
				RandomTimer RT = new RandomTimer(
						PhoneMyNum.replace("+82", "0"), PhoneYouNum.replace(
								"+82", "0"));
				DTimer.schedule(RT, 180000);
				str2 = SPN_Preferense.SuccessState;
				return str2;
			} else {
				str = "UPDATE serv_info SET My='"
						+ PhoneMyNum.replace("+82", "0") + "', You ='"
						+ PhoneYouNum.replace("+82", "0") + "', mRandom ="
						+ nRandom + " where my ='"
						+ PhoneMyNum.replace("+82", "0") + "'and You ='"
						+ PhoneYouNum.replace("+82", "0") + "';";
				stmt.executeUpdate(str);
				str2 = SPN_Preferense.SuccessState;
				return str2;
			}
		} catch (SQLException e) {
			// TODO 자동 생성된 catch 블록
			str2 = SPN_Preferense.ErrorState01;
			return str2;
		}
	}

	public String ReqMyGpsSetting(SPN_PhoneInfoObject PhoneMy, String Parrent,
			String Child) {
		String str = "SELECT * FROM user_relation where " + Parrent + " ='"
				+ PhoneMy.PhoneNumber.replace("+82", "0") + "' and State=0;";

		String str2 = "";
		try {
			rs = stmt.executeQuery(str);
			rs.first();
			if (rs.getRow() == 0) {
				str2 = SPN_Preferense.EmptyState;
				return str2;
			}
			for (int i = 0; i < rs.getRow(); i++) {
				str2 += rs.getString(Child) + "/";
				rs.next();
			}
			return str2;
		} catch (SQLException e) {
			// TODO 자동 생성된 catch 블록
			str2 = SPN_Preferense.ErrorState01;
			return str2;
		}
	}

	public String AddChildGpsState(SPN_PhoneInfoObject PhoneMy,
			SPN_PhoneInfoObject PhoneYou) {
		String str = "SELECT Id FROM gps_info where Parrent ='"
				+ PhoneMy.PhoneNumber.replace("+82", "0") + "' and Child='"
				+ PhoneYou.PhoneNumber.replace("+82", "0") + "';";

		String str2 = "";
		
		try {
			rs = stmt.executeQuery(str);
			rs.first();
			if (rs.getRow() == 0) {

				for (int i = 0; i < PhoneYou.GPSInfo.SPN_Vertex.size(); i++) {
					String[] Vertex = PhoneYou.GPSInfo.SPN_Vertex.get(i).split(
							"/");

					System.out.println(Vertex[0] + " / " + Vertex[1]);
					str = "INSERT INTO gps_info(Parrent,Child,longtitude,latitude) values('"
							+ PhoneMy.PhoneNumber.replace("+82", "0")
							+ "', '"
							+ PhoneYou.PhoneNumber.replace("+82", "0")
							+ "', '"
							+ Vertex[0] + "', '" + Vertex[1] + "');";
					stmt.execute(str);
				}

				str2 = SPN_Preferense.SuccessState;
				return str2;
			} else {
				str = "delete from gps_info where Parrent ='"
						+ PhoneMy.PhoneNumber.replace("+82", "0")
						+ "' and Child ='"
						+ PhoneYou.PhoneNumber.replace("+82", "0") + "';";
				stmt.execute(str);
				for (int i = 0; i < PhoneYou.GPSInfo.SPN_Vertex.size(); i++) {
					String[] Vertex = PhoneYou.GPSInfo.SPN_Vertex.get(i).split(
							"/");

					str = "INSERT INTO gps_info(Parrent,Child,longtitude,latitude) values('"
							+ PhoneMy.PhoneNumber.replace("+82", "0")
							+ "', '"
							+ PhoneYou.PhoneNumber.replace("+82", "0")
							+ "', '"
							+ Vertex[0] + "', '" + Vertex[1] + "');";
					stmt.execute(str);

				}
				str2 = SPN_Preferense.SuccessState;
				return str2;
			}
			
			
			
			
			
			
			
			
			
			
			
//			if (rs.getRow() == 0) {
//				String Vertex[][] = new String[PhoneYou.GPSInfo.SPN_Vertex.size()][];
//				for (int i = 0; i < PhoneYou.GPSInfo.SPN_Vertex.size()-1; i++) {
//					Vertex[i] = PhoneYou.GPSInfo.SPN_Vertex.get(i).split(
//							"/");
//					Vertex[i+1] = PhoneYou.GPSInfo.SPN_Vertex.get(i+1).split(
//							"/");
//					System.out.println("i =" + i + "//" +Vertex[i][0] + " / " + Vertex[i][1]);
//					double longGap = Double.parseDouble(Vertex[i][0]) - Double.parseDouble(Vertex[i+1][0]);
//					double laGap = Double.parseDouble(Vertex[i][1]) - Double.parseDouble(Vertex[i+1][1]);
//						
//					
//					
//					if((longGap > 0.0005000 || longGap < -0.0005000)
//							|| (laGap > 0.0005000 || laGap < -0.0005000))
//					{
//						String strLong = String.format("%.8f", longGap / 10.0);
//						double dbLong = Double.parseDouble(strLong);
//						double imitationLong = Double.parseDouble(Vertex[i][0]);
//						
//						String strLa = String.format("%.8f", laGap / 10.0);
//						double dbLa = Double.parseDouble(strLa);
//						double imitaionLa = Double.parseDouble(Vertex[i][1]);
//						
//						for(int j=0;j<10;j++)
//						{
//							imitationLong += dbLong;
//							imitaionLa += dbLa;
//							
//							String imiLong = String.format("%.7f", imitationLong);
//							String imiLa = String.format("%.7f", imitaionLa);
//							str = "INSERT INTO gps_info(Parrent,Child,longtitude,latitude) values('"
//									+ PhoneMy.PhoneNumber.replace("+82", "0")
//									+ "', '"
//									+ PhoneYou.PhoneNumber.replace("+82", "0")
//									+ "', '"
//									+ imiLong + "', '" + imiLa + "');";
//							stmt.execute(str);
//						}
//					}
//					else
//					{
//						str = "INSERT INTO gps_info(Parrent,Child,longtitude,latitude) values('"
//								+ PhoneMy.PhoneNumber.replace("+82", "0")
//								+ "', '"
//								+ PhoneYou.PhoneNumber.replace("+82", "0")
//								+ "', '"
//								+ Vertex[i][0] + "', '" + Vertex[i][1] + "');";
//						stmt.execute(str);
//					}
//					
//				}
//
//				str2 = SPN_Preferense.SuccessState;
//				return str2;
//			} else {
//				str = "delete from gps_info where Parrent ='"
//						+ PhoneMy.PhoneNumber.replace("+82", "0")
//						+ "' and Child ='"
//						+ PhoneYou.PhoneNumber.replace("+82", "0") + "';";
//				stmt.execute(str);
//				String Vertex[][] = new String[PhoneYou.GPSInfo.SPN_Vertex.size()][];
//				for (int i = 0; i < PhoneYou.GPSInfo.SPN_Vertex.size()-1; i++) {
//					Vertex[i] = PhoneYou.GPSInfo.SPN_Vertex.get(i).split(
//							"/");
//					Vertex[i+1] = PhoneYou.GPSInfo.SPN_Vertex.get(i+1).split(
//							"/");
//					double longGap = Double.parseDouble(Vertex[i][0]) - Double.parseDouble(Vertex[i+1][0]);
//					double laGap = Double.parseDouble(Vertex[i][1]) - Double.parseDouble(Vertex[i+1][1]);
//						
//					System.out.println(Vertex[i][0] + " / " + Vertex[i][1]);
//					
//					if((longGap > 0.0005000 || longGap < -0.0005000)
//							|| (laGap > 0.0005000 || laGap < -0.0005000))
//					{
//						String strLong = String.format("%.8f", longGap / 10.0);
//						double dbLong = Double.parseDouble(strLong);
//						double imitationLong = Double.parseDouble(Vertex[i][0]);
//						
//						String strLa = String.format("%.8f", laGap / 10.0);
//						double dbLa = Double.parseDouble(strLa);
//						double imitaionLa = Double.parseDouble(Vertex[i][1]);
//						
//						for(int j=0;j<10;j++)
//						{
//							imitationLong += dbLong;
//							imitaionLa += dbLa;
//							
//							String imiLong = String.format("%.7f", imitationLong);
//							String imiLa = String.format("%.7f", imitaionLa);
//							str = "INSERT INTO gps_info(Parrent,Child,longtitude,latitude) values('"
//									+ PhoneMy.PhoneNumber.replace("+82", "0")
//									+ "', '"
//									+ PhoneYou.PhoneNumber.replace("+82", "0")
//									+ "', '"
//									+ imiLong + "', '" + imiLa + "');";
//							stmt.execute(str);
//						}
//					}
//					else
//					{
//						str = "INSERT INTO gps_info(Parrent,Child,longtitude,latitude) values('"
//								+ PhoneMy.PhoneNumber.replace("+82", "0")
//								+ "', '"
//								+ PhoneYou.PhoneNumber.replace("+82", "0")
//								+ "', '"
//								+ Vertex[i][0] + "', '" + Vertex[i][1] + "');";
//						stmt.execute(str);
//					}
//					
//					
//				}
//
//				str2 = SPN_Preferense.SuccessState;
//				return str2;
//			}
		} catch (SQLException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
			str2 = SPN_Preferense.ErrorState01;
			return str2;
		}
	}

	public ArrayList<String> TransChildGpsState(SPN_PhoneInfoObject PhoneMy,
			String ChildPhone) {
		ArrayList<String> GpsArraylist = new ArrayList<>();
		String str = "SELECT * FROM gps_info where Parrent ='"
				+ PhoneMy.PhoneNumber + "' and Child ='" + ChildPhone
				+ "';";
		try {
			rs = stmt.executeQuery(str);
			rs.first();
			if (rs.getRow() == 0) {
				return null;
			}
			
			for (int i = 0; i < rs.getRow(); i++) {
				GpsArraylist.add(rs.getString("longtitude") + "/" + rs.getString("latitude"));
				rs.next();
			}
			return GpsArraylist;
		} catch (SQLException e) {
			// TODO 자동 생성된 catch 블록
			return null;
		}
	}

	class RandomTimer extends TimerTask {
		String PhoneMyNum;
		String PhoneYouNum;

		public RandomTimer(String PhoneMyNum, String PhoneYouNum) {
			this.PhoneMyNum = PhoneMyNum;
			this.PhoneYouNum = PhoneYouNum;
		}

		@Override
		public void run() {
			// TODO 자동 생성된 메소드 스텁
			try {
				String str = "delete from serv_info where My ='"
						+ PhoneMyNum.replace("+82", "0") + "' and You ='"
						+ PhoneYouNum.replace("+82", "0") + "';";
				stmt.execute(str);
			} catch (SQLException e) {
				// TODO 자동 생성된 catch 블록
				e.printStackTrace();
			}
		}

	}
}
