package smart.planotice.smartplanotice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;

import smart.planotice.smartplanotice.R;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapLocationManager.OnLocationChangeListener;
import com.nhn.android.maps.maplib.NGeoPoint;

public class SPN_Connect_BackServ extends Service implements Serializable {

	private Thread SPNthread;
	private Thread SPNthread2;
	ByteArrayOutputStream baos;
	ObjectOutputStream out;
	ByteArrayInputStream bais;
	ObjectInputStream ois;

	Toast mtoast;
	NotificationManager NotiManager;
	Vibrator Vib;
	Timer tm;

	LocationManager LocationM;
	NMapLocationManager mMapLocationMy;

	public static Context Backcontext;

	public static SPN_ConnectThread spnCH;

	public static Hashtable<String, String> ChildLocation;
	public static ArrayList<String> ParrentSetMyLocation;
	public static NGeoPoint BackNGeoPoint;

	int ParrentORChildFlag = 0;
	SPN_Object inspn;


	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		ChildLocation = new Hashtable<String, String>();
		Backcontext = this;
		BackNGeoPoint = new NGeoPoint();
		spnCH = new SPN_ConnectThread(SPN_Connect_BackServ.this);
		// data = ByteBuffer.allocate(4096);
		NotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		LocationM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		mMapLocationMy = new NMapLocationManager(this);
		mMapLocationMy.enableMyLocation(true);
		mMapLocationMy
				.setOnLocationChangeListener(new OnLocationChangeListener() {

					@Override
					public void onLocationUpdateTimeout(NMapLocationManager arg0) {
						// TODO 자동 생성된 메소드 스텁

					}

					@Override
					public void onLocationUnavailableArea(
							NMapLocationManager arg0, NGeoPoint arg1) {
						// TODO 자동 생성된 메소드 스텁

					}

					@Override
					public boolean onLocationChanged(NMapLocationManager arg0,
							NGeoPoint arg1) {
						// TODO 자동 생성된 메소드 스텁
						// if (isParrentReqChildGps) {
						// if (mMapLocationMy.isMyLocationFixed()) {
						//
						// mMapLocationMy.disableMyLocation();
						// return true;
						// }
						// } else {
						if (mMapLocationMy.isMyLocationFixed()) {

							mMapLocationMy.disableMyLocation();
						}
						return false;
						// }
						// return false;
					}
				});

		if (SPNthread != null && SPNthread.isAlive()) {
			SPNthread.interrupt();
		}

		SPNthread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO 자동 생성된 메소드 스텁
				while (true) {
					if (!spnCH.channel.isConnected()) {
						ServConnect(SPN_Connect_BackServ.this);
					} else {
						int num;
						try {
							ByteBuffer data = ByteBuffer.allocateDirect(65536);
							String str = "";
							AddVector(SPN_Connect_BackServ.this);
							Log.e("해당없음4", "해당없음4");

							spnCH.channel.read(data);

							str = data.toString();
							Log.e("바이트버퍼용량flip전", str);
							data.rewind();
							Log.e("바이트버퍼용량flip후", str);

							bais = new ByteArrayInputStream(data.array());
							ois = new ObjectInputStream(bais);

							inspn = (SPN_Object) ois.readObject();

							if (inspn.Message
									.equals(SPN_Preferense.BackAccreditState)) {
								// 알람
								Log.e("알람울림", "알람울림");
								String acnum = inspn.PhoneObjectMy.PhoneID;
								Notification Noti = new Notification(
										R.drawable.main,
										"인증번호를 입력해주세요 !", System
												.currentTimeMillis());
								Noti.defaults = Notification.DEFAULT_SOUND;
								Noti.flags = Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_SHOW_LIGHTS;
								
								Intent putAccredit = new Intent(
										SPN_Connect_BackServ.this,
										SPN_ChkAccredit.class);
								putAccredit
										.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								putAccredit.putExtra("ChkAccreditNumber",
										acnum);

								PendingIntent pendAccredit = PendingIntent
										.getActivity(SPN_Connect_BackServ.this,
												0, putAccredit, 0);
								
								Noti.setLatestEventInfo(
										SPN_Connect_BackServ.this,
										"SmartPlaNotice", "인증번호 : "
												+ inspn.PhoneObjectMy.PhoneID,
										pendAccredit);
								NotiManager.notify(0, Noti);
								Vib.vibrate(1000);
							} else if (inspn.Message
									.equals(SPN_Preferense.ReqGpsState01)) {
								Log.e("ReqGpsState01", "여기옴");
								handler.sendEmptyMessage(3);
								if (!LocationM
										.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
									// gps꺼져있으니 서버에 꺼져있다고 보내야댐;
									baos = new ByteArrayOutputStream();
									out = new ObjectOutputStream(baos);

									SPN_PhoneInfoObject PhoneMyGps = new SPN_PhoneInfoObject(
											"GpsOff", null, null, null, null,
											null);

									SPN_Object spn = new SPN_Object(
											SPN_MainActivity.MainPhoneInfoMy,
											PhoneMyGps, null,
											SPN_Preferense.ReqGpsChild01);

									Object obc = spn;

									out.writeObject(obc);

									spnCH.channel.write(ByteBuffer.wrap(baos
											.toByteArray()));
								} else {
									baos = new ByteArrayOutputStream();
									out = new ObjectOutputStream(baos);

									String GpsState;
									String lo = null;
									String la = null;
									if (mMapLocationMy.isMyLocationFixed()) {
										lo = Double.toString(mMapLocationMy
												.getLastLocationFix()
												.getLongitude());
										la = Double.toString(mMapLocationMy
												.getLastLocationFix()
												.getLatitude());
										Log.e(lo, la);
										GpsState = "GpsOn";

									} else {
										GpsState = "GpsOff";
									}

									SPN_PhoneInfoObject PhoneMyGps = new SPN_PhoneInfoObject(
											GpsState, null, null, lo, la, null);

									SPN_Object spn = new SPN_Object(
											SPN_MainActivity.MainPhoneInfoMy,
											PhoneMyGps, null,
											SPN_Preferense.ReqGpsChild01);

									Object obc = spn;

									out.writeObject(obc);

									spnCH.channel.write(ByteBuffer.wrap(baos
											.toByteArray()));
								}

							} else if (inspn.Message
									.equals(SPN_Preferense.ReqGpsChild01)) {
								// 자식이 나한테 보내는 메시지
								if (inspn.PhoneObjectYou.PhoneID
										.equals("GpsOff")) {
									ChildLocation.put(
											inspn.PhoneObjectMy.PhoneNumber
													.replace("+82", "0"),
											"GpsOff");
								} else {
									ChildLocation.put(
											inspn.PhoneObjectMy.PhoneNumber
													.replace("+82", "0"),
											inspn.PhoneObjectYou.Longtitude
													+ "/"
													+ inspn.PhoneObjectYou.Latitude);
								}

								Log.e("ChildLocation", ChildLocation
										.get(inspn.PhoneObjectMy.PhoneNumber
												.replace("+82", "0")));
							} else if (inspn.Message
									.equals(SPN_Preferense.NoReqGpsState01)) {
								handler.sendEmptyMessage(2);
							} else if (inspn.Message
									.equals(SPN_Preferense.TransChildGpsState)) {
								ParrentSetMyLocation = inspn.PhoneObjectMy.GPSInfo.SPN_Vertex;
								ParrentORChildFlag = 1;
								if (ParrentSetMyLocation == null) {
									handler.sendEmptyMessage(4);
								} else {
									handler.sendEmptyMessage(3);
									if (!LocationM
											.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
										// gps꺼져있으니 서버에 꺼져있다고 보내야댐;
										baos = new ByteArrayOutputStream();
										out = new ObjectOutputStream(baos);

										SPN_PhoneInfoObject PhoneMyGps = new SPN_PhoneInfoObject(
												"GpsOff", null, null, null,
												null, null);

										SPN_Object spn = new SPN_Object(
												SPN_MainActivity.MainPhoneInfoMy,
												PhoneMyGps, null,
												SPN_Preferense.ReqGpsState02);

										Object obc = spn;

										out.writeObject(obc);

										spnCH.channel.write(ByteBuffer
												.wrap(baos.toByteArray()));
									} else {
										handler.sendEmptyMessage(5);
									}
								}
							} else if (inspn.Message
									.equals(SPN_Preferense.ReqGpsState02)) {
								handler.sendEmptyMessage(1);

							} else if (inspn.Message
									.equals(SPN_Preferense.ReqGpsState03)) {
								handler.sendEmptyMessage(6);
								ParrentORChildFlag = 2;
							} else if (inspn.Message
									.equals(SPN_Preferense.NoReqGpsState02)) {
								handler.sendEmptyMessage(7);
							} else {
								baos = new ByteArrayOutputStream();
								out = new ObjectOutputStream(baos);

								SPN_Object spn = new SPN_Object(null, null,
										inspn, SPN_Preferense.ReqOneMoreState);

								Object obc = spn;

								out.writeObject(obc);

								spnCH.channel.write(ByteBuffer.wrap(baos
										.toByteArray()));

								Log.e("해당없음2", "해당없음2");
							}
							// initbuffer(data);
						} catch (Exception e) {
							// TODO 자동 생성된 catch 블록
							e.printStackTrace();
							try {
								spnCH.channel.close();
								ServConnect(SPN_Connect_BackServ.this);
							} catch (IOException e1) {
								// TODO 자동 생성된 catch 블록
								e1.printStackTrace();
							}
						}
					}

					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		SPNthread.start();
		mMapLocationMy.enableMyLocation(true);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SPNthread.interrupt();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void ServConnect(Context context) {
		if (!spnCH.channel.isConnected()) {
			if (spnCH.isInterrupted()) {
				spnCH = new SPN_ConnectThread(Backcontext);
				spnCH.start();
			} else {
				spnCH.interrupt();
				spnCH = new SPN_ConnectThread(Backcontext);
				spnCH.start();
			}
		}

	}

	public void AddVector(Context context) {
		try {
			ByteArrayOutputStream baos;
			ObjectOutputStream out;
			TelephonyManager mgr;
			String phoneNumber;
			WifiManager wifi;

			String imei;
			String MacAddr;
			WifiInfo info;

			baos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(baos);

			mgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			phoneNumber = mgr.getLine1Number();

			wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

			imei = mgr.getDeviceId();
			info = wifi.getConnectionInfo();
			MacAddr = info.getMacAddress();

			SPN_PhoneInfoObject PhoneInfoMy = new SPN_PhoneInfoObject(imei,
					MacAddr, phoneNumber, null, null, null);
			SPN_MainActivity.MainPhoneInfoMy = PhoneInfoMy;
			SPN_Object spn = new SPN_Object(PhoneInfoMy, null, null,
					SPN_Preferense.AddVector);

			Object obc = spn;

			out.writeObject(obc);

			spnCH.channel.write(ByteBuffer.wrap(baos.toByteArray()));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void initbuffer(ByteBuffer bf) {
		bf.clear();
		bf.put(new byte[4096]);
		bf.flip();
		bf.clear();
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO 자동 생성된 메소드 스텁
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				// mtoast = Toast.makeText(SPN_Connect_BackServ.this,
				// "네트워크 상태가 원활하지 않습니다.", 0);
				// mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				// mtoast.show();
				break;
			case 1:
				mtoast = Toast
						.makeText(SPN_Connect_BackServ.this, "자녀 : "
								+ inspn.PhoneObjectMy.PhoneNumber
								+ "의 gps가 꺼져있어요!!", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			case 2:
				mMapLocationMy.disableMyLocation();
				break;
			case 3:
				mMapLocationMy.enableMyLocation(true);
				mMapLocationMy
						.setOnLocationChangeListener(new OnLocationChangeListener() {

							@Override
							public void onLocationUpdateTimeout(
									NMapLocationManager arg0) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onLocationUnavailableArea(
									NMapLocationManager arg0, NGeoPoint arg1) {
								// TODO Auto-generated method stub

							}

							@Override
							public boolean onLocationChanged(
									NMapLocationManager arg0, NGeoPoint arg1) {
								// TODO Auto-generated method stub
								return true;
							}
						});
				break;
			case 4:
				mtoast = Toast.makeText(SPN_Connect_BackServ.this, "실패", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			case 5:
				String str[];
				for (int i = 0; i < ParrentSetMyLocation.size(); i++) {
					str = ParrentSetMyLocation.get(i).split("/");
					double longtitude, latitude;
					longtitude = Double.parseDouble(str[0]);
					latitude = Double.parseDouble(str[1]);

					if (!mMapLocationMy.isMyLocationFixed()) {
						handler.sendEmptyMessage(5);
						break;
					}

					if (BackNGeoPoint.getDistance(new NGeoPoint(longtitude,
							latitude), new NGeoPoint(mMapLocationMy
							.getLastLocationFix().getLongitude(),
							mMapLocationMy.getLastLocationFix().getLatitude())) > 250) {
						if(i == ParrentSetMyLocation.size() - 1)
						{
							handler.sendEmptyMessage(8);
							handler.sendEmptyMessage(6);
							break;
						}
						continue;
					} else {
						if (i == ParrentSetMyLocation.size() - 1) {
							handler.sendEmptyMessage(7);
						}
						else
						{
							handler.sendEmptyMessageDelayed(5, 60000);
							break;
						}
					}
				}
				break;
			case 6:
				if(ParrentORChildFlag == 0)
				{
					
				}
				else if(ParrentORChildFlag == 1)
				{
					Notification childmad = new Notification(R.drawable.main,
							"아이가 방황하고 있어요", System.currentTimeMillis());
					childmad.defaults = Notification.DEFAULT_SOUND;
					childmad.flags = Notification.FLAG_ONGOING_EVENT;
					Intent putAccredit = new Intent(SPN_Connect_BackServ.this,
							SPN_ChildMain.class);
					putAccredit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					putAccredit.putExtra("ParrentORChildFlag", ParrentORChildFlag);
					PendingIntent pendAccredit = PendingIntent.getActivity(
							SPN_Connect_BackServ.this, 0, putAccredit, 0);
					childmad.setLatestEventInfo(SPN_Connect_BackServ.this,
							"SmartPlaNotice", "아이가 방황하고 있어요!", pendAccredit);
					NotiManager.notify(3, childmad);

					Vib.vibrate(1000);
				}
				else if(ParrentORChildFlag == 2)
				{
					Notification childmad = new Notification(R.drawable.main,
							"아이가 방황하고 있어요", System.currentTimeMillis());
					childmad.defaults = Notification.DEFAULT_SOUND;
					childmad.flags = Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_SHOW_LIGHTS;
					
					Intent putAccredit = new Intent();
					putAccredit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent pendAccredit = PendingIntent.getActivity(
							SPN_Connect_BackServ.this, 0, putAccredit, 0);
					childmad.setLatestEventInfo(SPN_Connect_BackServ.this,
							"SmartPlaNotice", "아이가 방황하고 있어요!", pendAccredit);
					NotiManager.notify(2, childmad);

					Vib.vibrate(1000);
				}
				break;
			case 7:
				handler.removeMessages(8);
				handler.removeMessages(5);
				NotiManager.cancelAll();
				handler.sendEmptyMessage(2);
				break;
			case 8:
				SPNthread2 = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							baos = new ByteArrayOutputStream();
							out = new ObjectOutputStream(baos);
							SPN_Object spn = new SPN_Object(
									SPN_MainActivity.MainPhoneInfoMy, null,
									null, SPN_Preferense.ReqGpsState03);

							Object obc = spn;

							out.writeObject(obc);

							spnCH.channel.write(ByteBuffer.wrap(baos
									.toByteArray()));

							SPNthread2.interrupt();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				SPNthread2.start();
				handler.sendEmptyMessageDelayed(5, 60000);
				break;
			}
		}
	};
}
