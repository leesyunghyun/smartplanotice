package smart.planotice.smartplanotice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Vector;

public class SPN_ServTh {

	private String[] ChildPhone;

	public void readhpobject(SPN_Data spndata, SocketChannel sc,
			Vector UserVector, SelectionKey key) {
		ByteBuffer data = ByteBuffer.allocate(65536);
		try {
			ObjectInputStream ois;
			ObjectOutputStream out;
			ByteArrayInputStream bais = null;
			ByteArrayOutputStream baos = null;

			System.out.println("읽기 대기중");
			String str = "";
			
			sc.read(data);
			
			str = data.toString();
			System.out.println("수신 : " + str);
//			if(data.position() == 0)
//			{
//				System.out.println("data가 0이여서 리턴함");
//				sc.close();
//				return ;
//			}
			bais = new ByteArrayInputStream(data.array());

			ois = new ObjectInputStream(bais);
			
			SPN_Object spn = (SPN_Object) ois.readObject();

			bais.close();
			ois.close();
			switch (spn.Message) {
			case SPN_Preferense.AddVector:
				spndata.AddVector("backchannel", spn.PhoneObjectMy, UserVector,
						sc);
				break;
			case SPN_Preferense.CheckAddress:
				baos = new ByteArrayOutputStream();
				out = new ObjectOutputStream(baos);

				SPN_Object CheckAddress = new SPN_Object(null, null, null,
						spndata.CheckAddress(spn.PhoneObjectMy));

				Object CheckAddressObject = CheckAddress;

				out.writeObject(CheckAddressObject);
				out.flush();
				if (sc.isConnected()) {
					sc.write(ByteBuffer.wrap(baos.toByteArray()));
					System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
				}
				spndata.AddVector("mainchannel", spn.PhoneObjectMy, UserVector,
						sc);

				System.out.println("로그인 상태 결과 : " + CheckAddress.Message);
				break;
			case SPN_Preferense.RegisterPhone:
				baos = new ByteArrayOutputStream();
				out = new ObjectOutputStream(baos);

				SPN_Object RegisterPhone = new SPN_Object(null, null, null,
						spndata.RegisterPhone(spn.PhoneObjectMy));

				Object RegisterPhoneObject = RegisterPhone;

				out.writeObject(RegisterPhoneObject);
				out.flush();
				if (sc.isConnected()) {
					sc.write(ByteBuffer.wrap(baos.toByteArray()));
					System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
				}
				spndata.AddVector("mainchannel", spn.PhoneObjectMy, UserVector,
						sc);

				System.out.println("휴대폰 등록 결과 : " + RegisterPhone.Message);
				break;
			case SPN_Preferense.CheckChildState:
				baos = new ByteArrayOutputStream();
				out = new ObjectOutputStream(baos);

				SPN_Object CheckChild = new SPN_Object(null, null, null,
						spndata.CheckChild(spn.PhoneObjectMy,
								spn.PhoneObjectYou));

				System.out.println("자녀등록 첫 번째 검사결과 : " + CheckChild.Message);

				if (CheckChild.Message.equals(SPN_Preferense.SuccessState)) {
					boolean isAliveVector = false;
					int nRandom = MakeRandomNumber();
					for (int i = 0; i < UserVector.size(); i++) {
						SPN_UserInfo User = (SPN_UserInfo) UserVector.get(i);
						if (User.PhoneNumber
								.equals(spn.PhoneObjectYou.PhoneNumber)
								&& User.ChannelName.equals("backchannel")) {
							isAliveVector = true;
							baos = new ByteArrayOutputStream();
							out = new ObjectOutputStream(baos);

							SPN_PhoneInfoObject CheckRandomPhone = new SPN_PhoneInfoObject(
									Integer.toString(nRandom), null, null,
									null, null, null);

							SPN_Object CheckRandom = new SPN_Object(
									CheckRandomPhone, null, null,
									SPN_Preferense.BackAccreditState);

							Object CheckRandomObject = CheckRandom;
							out.writeObject(CheckRandomObject);
							out.flush();
							if (User.UserSC.isConnected()) {
								User.UserSC.write(ByteBuffer.wrap(baos
										.toByteArray()));
								System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
							}
							System.out.println("자녀에게 랜덤메시지 발송 결과 : "
									+ CheckRandom.Message);
							spndata.CountDownRandom(
									spn.PhoneObjectMy.PhoneNumber,
									spn.PhoneObjectYou.PhoneNumber, nRandom);
						}
					}

					if (!isAliveVector) {
						baos = new ByteArrayOutputStream();
						out = new ObjectOutputStream(baos);

						SPN_Object UserOffline = new SPN_Object(null, null,
								null, SPN_Preferense.ErrorState03);

						Object UserOfflineObject = UserOffline;

						out.writeObject(UserOfflineObject);
						out.flush();
						if (sc.isConnected()) {
							sc.write(ByteBuffer.wrap(baos.toByteArray()));
							System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
						}

						System.out.println("자녀등록 두 번째 검사결과 : "
								+ UserOffline.Message);
					} else {
						baos = new ByteArrayOutputStream();
						out = new ObjectOutputStream(baos);

						SPN_Object UserOffline = new SPN_Object(null, null,
								null, SPN_Preferense.SuccessState);

						Object UserOfflineObject = UserOffline;

						out.writeObject(UserOfflineObject);
						out.flush();
						if (sc.isConnected()) {
							sc.write(ByteBuffer.wrap(baos.toByteArray()));
							System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
						}

						System.out.println("자녀등록 두 번째 검사결과 : "
								+ UserOffline.Message);
					}
				} else {
					Object CheckChildObject = CheckChild;

					out.writeObject(CheckChildObject);
					if (sc.isConnected()) {
						sc.write(ByteBuffer.wrap(baos.toByteArray()));
						System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
					}
				}
				break;
			case SPN_Preferense.AddChildState:
				baos = new ByteArrayOutputStream();
				out = new ObjectOutputStream(baos);

				SPN_Object AddChildState = new SPN_Object(null, null, null,
						spndata.AddChild(spn.PhoneObjectMy, spn.PhoneObjectYou));

				Object AddChildStateObject = AddChildState;

				out.writeObject(AddChildStateObject);
				out.flush();
				if (sc.isConnected()) {
					sc.write(ByteBuffer.wrap(baos.toByteArray()));
					System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
				}

				System.out.println("자녀 등록 결과 : " + AddChildState.Message);
				break;
			case SPN_Preferense.ReqOneMoreState:

				SPN_Object ReqSpn = spn.ReObject;

				baos = new ByteArrayOutputStream();
				out = new ObjectOutputStream(baos);

				SPN_Object ReqOneMoreState = ReqSpn;

				Object ReqOneMoreStateObject = ReqOneMoreState;

				out.writeObject(ReqOneMoreStateObject);
				out.flush();
				if (sc.isConnected()) {
					sc.write(ByteBuffer.wrap(baos.toByteArray()));
					System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
				}

				System.out.println(ReqOneMoreState.Message);
				break;
			case SPN_Preferense.ReqMyGpsState:
				baos = new ByteArrayOutputStream();
				out = new ObjectOutputStream(baos);

				SPN_Object ReqMyGps = new SPN_Object(null, null, null,
						spndata.ReqMyGpsSetting(spn.PhoneObjectMy,
								"PhoneParrentID", "PhoneChildID"));

				Object ReqMyGpsObject = ReqMyGps;

				out.writeObject(ReqMyGpsObject);
				out.flush();
				if (sc.isConnected()) {
					sc.write(ByteBuffer.wrap(baos.toByteArray()));
					System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
					
				}

				System.out.println(ReqMyGps.Message);
				break;
			case SPN_Preferense.AddChildGpsState:
				baos = new ByteArrayOutputStream();
				out = new ObjectOutputStream(baos);

				SPN_Object AddChildGpsState = new SPN_Object(null, null, null,
						spndata.AddChildGpsState(spn.PhoneObjectMy,
								spn.PhoneObjectYou));

				Object AddChildGpsStateObject = AddChildGpsState;

				out.writeObject(AddChildGpsStateObject);
				out.flush();
				if (sc.isConnected()) {
					sc.write(ByteBuffer.wrap(baos.toByteArray()));
					System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
				}

				System.out.println(AddChildGpsState.Message);
				break;
			case SPN_Preferense.ReqGpsState01:
				ChildPhone = spndata.ReqMyGpsSetting(spn.PhoneObjectMy,
						"PhoneParrentID", "PhoneChildID").split("/");

				for (int i = 0; i < UserVector.size(); i++) {
					final SPN_UserInfo User = (SPN_UserInfo) UserVector.get(i);
					for (int j = 0; j < ChildPhone.length; j++) {
						if (ChildPhone[j].equals(User.PhoneNumber)
								&& User.ChannelName.equals("backchannel")) {
							baos = new ByteArrayOutputStream();
							out = new ObjectOutputStream(baos);
							SPN_Object ReqGpsState01 = new SPN_Object(null,
									null, null, SPN_Preferense.ReqGpsState01);

							Object ReqGpsState01Object = ReqGpsState01;

							out.writeObject(ReqGpsState01Object);
							out.flush();
							if (User.UserSC.isConnected()) {
								User.UserSC.write(ByteBuffer.wrap(baos
										.toByteArray()));
								System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
								System.out.println(ChildPhone[j] + "에게 "
										+ ReqGpsState01.Message);
							}
						}
					}
				}

				baos = new ByteArrayOutputStream();
				out = new ObjectOutputStream(baos);

				SPN_Object ReqGpsState01 = new SPN_Object(null, null, null,
						SPN_Preferense.SuccessState);

				Object ReqGpsState01Object = ReqGpsState01;

				out.writeObject(ReqGpsState01Object);
				out.flush();
				if (sc.isConnected()) {
					sc.write(ByteBuffer.wrap(baos.toByteArray()));
					System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
				}

				System.out.println(ReqGpsState01.Message);
				break;
			case SPN_Preferense.NoReqGpsState01:
				for (int i = 0; i < UserVector.size(); i++) {
					final SPN_UserInfo User = (SPN_UserInfo) UserVector.get(i);
					for (int j = 0; j < ChildPhone.length; j++) {
						if (ChildPhone[j].equals(User.PhoneNumber)
								&& User.ChannelName.equals("backchannel")) {
							baos = new ByteArrayOutputStream();
							out = new ObjectOutputStream(baos);
							SPN_Object NoReqGpsState01 = new SPN_Object(null,
									null, null, SPN_Preferense.NoReqGpsState01);

							Object NoReqGpsState01Object = NoReqGpsState01;

							out.writeObject(NoReqGpsState01Object);
							out.flush();
							if (User.UserSC.isConnected()) {
								User.UserSC.write(ByteBuffer.wrap(baos
										.toByteArray()));
								System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
								System.out.println(ChildPhone[j] + "에게 "
										+ NoReqGpsState01.Message);
							}
						}
					}
				}

				baos = new ByteArrayOutputStream();
				out = new ObjectOutputStream(baos);

				SPN_Object NoReqGpsState01 = new SPN_Object(null, null, null,
						SPN_Preferense.SuccessState);

				Object NoReqGpsState01Object = NoReqGpsState01;

				out.writeObject(NoReqGpsState01Object);
				out.flush();
				if (sc.isConnected()) {
					sc.write(ByteBuffer.wrap(baos.toByteArray()));
					System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
				}

				System.out.println(NoReqGpsState01.Message);
				break;
			case SPN_Preferense.ReqGpsChild01:
				final String[] ParrentPhone = spndata.ReqMyGpsSetting(
						spn.PhoneObjectMy, "PhoneChildID", "PhoneParrentID")
						.split("/");
				for (int i = 0; i < UserVector.size(); i++) {
					final SPN_UserInfo User = (SPN_UserInfo) UserVector.get(i);
					for (int j = 0; j < ParrentPhone.length; j++) {
						if (ParrentPhone[j].equals(User.PhoneNumber)
								&& User.ChannelName.equals("backchannel")) {
							baos = new ByteArrayOutputStream();
							out = new ObjectOutputStream(baos);

							SPN_Object ReqGpsChild01 = new SPN_Object(
									spn.PhoneObjectMy, spn.PhoneObjectYou,
									null, SPN_Preferense.ReqGpsChild01);

							Object ReqGpsChild01Object = ReqGpsChild01;

							out.writeObject(ReqGpsChild01Object);
							out.flush();
							if (User.UserSC.isConnected()) {
								User.UserSC.write(ByteBuffer.wrap(baos
										.toByteArray()));
								System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
							}

							System.out.println(ParrentPhone[j] + "에게 "
									+ ReqGpsChild01.Message);
						}
					}
				}
				break;
			case SPN_Preferense.TransChildGpsState:
				final String[] ChildPhone = spndata.ReqMyGpsSetting(
						spn.PhoneObjectMy, "PhoneParrentID", "PhoneChildID")
						.split("/");
				for (int i = 0; i < UserVector.size(); i++) {
					final SPN_UserInfo User = (SPN_UserInfo) UserVector.get(i);
					for (int j = 0; j < ChildPhone.length; j++) {
						if (ChildPhone[j].equals(User.PhoneNumber)
								&& User.ChannelName.equals("backchannel")) {
							baos = new ByteArrayOutputStream();
							out = new ObjectOutputStream(baos);

							SPN_GPSInfoObject GpsTransChildGpsState = new SPN_GPSInfoObject(
									spndata.TransChildGpsState(
											spn.PhoneObjectMy,
											ChildPhone[j]), 0);

							SPN_PhoneInfoObject PhoneTransChildGpsState = new SPN_PhoneInfoObject(
									null, null, null, null, null,
									GpsTransChildGpsState);

							SPN_Object TransChildGpsState = new SPN_Object(
									PhoneTransChildGpsState, null,
									null, SPN_Preferense.TransChildGpsState);

							Object TransChildGpsStateObject = TransChildGpsState;

							out.writeObject(TransChildGpsStateObject);
							out.flush();
							if (User.UserSC.isConnected()) {
								User.UserSC.write(ByteBuffer.wrap(baos
										.toByteArray()));
								System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
							}

							System.out.println(ChildPhone[j] + "에게 "
									+ TransChildGpsState.Message);
						}
					}
				}
				baos = new ByteArrayOutputStream();
				out = new ObjectOutputStream(baos);

				SPN_Object TransChildGpsState = new SPN_Object(null, null,
						null, SPN_Preferense.SuccessState);

				Object TransChildGpsStateObject = TransChildGpsState;

				out.writeObject(TransChildGpsStateObject);
				out.flush();
				if (sc.isConnected()) {
					sc.write(ByteBuffer.wrap(baos.toByteArray()));
				}
				System.out.println("송신 : " + ByteBuffer.wrap(baos.toByteArray()).toString());
				System.out.println(TransChildGpsState.Message);
				break;
			case SPN_Preferense.ReqGpsState02:
				final String[] ParrentPhone02 = spndata.ReqMyGpsSetting(
						spn.PhoneObjectMy, "PhoneChildID", "PhoneParrentID")
						.split("/");
				for (int i = 0; i < UserVector.size(); i++) {
					final SPN_UserInfo User = (SPN_UserInfo) UserVector.get(i);
					for (int j = 0; j < ParrentPhone02.length; j++) {
						if (ParrentPhone02[j].equals(User.PhoneNumber)
								&& User.ChannelName.equals("backchannel")) {
							baos = new ByteArrayOutputStream();
							out = new ObjectOutputStream(baos);

							SPN_Object ReqGpsChild01 = new SPN_Object(
									spn.PhoneObjectMy, null,
									null, SPN_Preferense.ReqGpsState02);

							Object ReqGpsChild01Object = ReqGpsChild01;

							out.writeObject(ReqGpsChild01Object);
							out.flush();
							if (User.UserSC.isConnected()) {
								User.UserSC.write(ByteBuffer.wrap(baos
										.toByteArray()));
								System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
							}

							System.out.println(ParrentPhone02[j] + "에게 "
									+ ReqGpsChild01.Message);
						}
					}
				}
				break;
			case SPN_Preferense.ReqGpsState03:
				final String[] ParrentPhone03 = spndata.ReqMyGpsSetting(
						spn.PhoneObjectMy, "PhoneChildID", "PhoneParrentID")
						.split("/");
				for (int i = 0; i < UserVector.size(); i++) {
					final SPN_UserInfo User = (SPN_UserInfo) UserVector.get(i);
					for (int j = 0; j < ParrentPhone03.length; j++) {
						if (ParrentPhone03[j].equals(User.PhoneNumber)
								&& User.ChannelName.equals("backchannel")) {
							baos = new ByteArrayOutputStream();
							out = new ObjectOutputStream(baos);

							SPN_Object ReqGpsChild01 = new SPN_Object(
									spn.PhoneObjectMy, null,
									null, SPN_Preferense.ReqGpsState03);

							Object ReqGpsChild01Object = ReqGpsChild01;

							out.writeObject(ReqGpsChild01Object);
							out.flush();
							if (User.UserSC.isConnected()) {
								User.UserSC.write(ByteBuffer.wrap(baos
										.toByteArray()));
								System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
							}

							System.out.println(ParrentPhone03[j] + "에게 "
									+ ReqGpsChild01.Message);
						}
					}
				}
				break;
			case SPN_Preferense.NoReqGpsState02:
				final String[] ChildPhone02 = spndata.ReqMyGpsSetting(
						spn.PhoneObjectMy, "PhoneParrentID", "PhoneChildID")
						.split("/");
				for (int i = 0; i < UserVector.size(); i++) {
					final SPN_UserInfo User = (SPN_UserInfo) UserVector.get(i);
					for (int j = 0; j < ChildPhone02.length; j++) {
						if (ChildPhone02[j].equals(User.PhoneNumber)
								&& User.ChannelName.equals("backchannel")) {
							baos = new ByteArrayOutputStream();
							out = new ObjectOutputStream(baos);

							SPN_Object ReqGpsChild01 = new SPN_Object(
									spn.PhoneObjectMy, null,
									null, SPN_Preferense.NoReqGpsState02);

							Object ReqGpsChild01Object = ReqGpsChild01;

							out.writeObject(ReqGpsChild01Object);
							out.flush();
							if (User.UserSC.isConnected()) {
								User.UserSC.write(ByteBuffer.wrap(baos
										.toByteArray()));
								System.out.println("송신 : " + (ByteBuffer.wrap(baos.toByteArray()).toString()));
							}

							System.out.println(ChildPhone02[j] + "에게 "
									+ ReqGpsChild01.Message);
						}
					}
				}
				
				baos = new ByteArrayOutputStream();
				out = new ObjectOutputStream(baos);

				SPN_Object NoReqGpsState02 = new SPN_Object(null, null,
						null, SPN_Preferense.SuccessState);

				Object NoReqGpsState02Object = NoReqGpsState02;

				out.writeObject(NoReqGpsState02Object);
				out.flush();
				if (sc.isConnected()) {
					sc.write(ByteBuffer.wrap(baos.toByteArray()));
				}
				System.out.println("송신 : " + ByteBuffer.wrap(baos.toByteArray()).toString());
				System.out.println(NoReqGpsState02.Message);
				
				break;	
			case SPN_Preferense.LogOut:
				System.out.println(sc.socket().getInetAddress()
						.getHostAddress().toString()
						+ " 로그아웃 요청중");
				spndata.DataClose(UserVector, sc);

				sc.close();

				break;
			}
			// SPN_ServMain.initbuffer(data);
		} catch (ClassNotFoundException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
			System.out.println(sc.socket().getInetAddress().getHostAddress()
					.toString()
					+ " 접속이 끊킴");
			spndata.DataClose(UserVector, sc);
			try {
				SPN_ServMain.initbuffer(data);
				sc.close();
			} catch (Exception e1) {
				// TODO 자동 생성된 catch 블록
				e1.printStackTrace();
			}
		}
	}

	public int MakeRandomNumber() {
		int random = 0;
		random = (int) (Math.random() * 100000) + 100000;
		return random;
	}

}
