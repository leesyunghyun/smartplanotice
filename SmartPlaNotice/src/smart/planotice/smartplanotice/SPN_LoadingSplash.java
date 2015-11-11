package smart.planotice.smartplanotice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import smart.planotice.smartplanotice.R;




import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SPN_LoadingSplash extends Activity {

	private Thread SPNthread;
	ByteArrayOutputStream baos;
	ObjectOutputStream out;
	ByteArrayInputStream bais;
	ObjectInputStream ois;
	Toast mtoast;
	Intent result;

	public String InMessage;
	Activity LoadingSplash;
	TelephonyManager mgr;
	String phoneNumber;
	WifiManager wifi;

	String imei;
	String MacAddr;
	WifiInfo info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 자동 생성된 메소드 스텁
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadingsplash);

		LoadingSplash = SPN_LoadingSplash.this;
		SPN_MainActivity
				.ProgressSet(LoadingSplash, "서버에 접속중입니다... 잠시만 기다려주세요!");
		Handler mainhandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO 자동 생성된 메소드 스텁
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					phoneNumber = mgr.getLine1Number();

					wifi = (WifiManager) SPN_LoadingSplash.this
							.getSystemService(Context.WIFI_SERVICE);

					imei = mgr.getDeviceId();
					info = wifi.getConnectionInfo();
					MacAddr = info.getMacAddress();
					SPN_MainActivity.progressDialog.show();
					break;
				}
			}
		};

		mainhandler.sendEmptyMessageDelayed(0, 1000);
		SPN_MainActivity.progressDialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				// TODO Auto-generated method stub
				SPN_MainActivity.MainServConnect(SPN_LoadingSplash.this);
				//SPN_Connect_BackServ.ServConnect(SPN_LoadingSplash.this);
				handler.sendEmptyMessage(5);
			}
		});
		SPN_MainActivity.progressDialog
				.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						if (!SPN_Connect_BackServ.spnCH.channel.isConnected()) {
							AlertDialog.Builder dlg = new AlertDialog.Builder(
									SPN_LoadingSplash.this);
							dlg.setTitle("안내");
							dlg.setMessage("종료하시겠습니까?");
							dlg.setCancelable(false);
							dlg.setPositiveButton("예", new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO 자동 생성된 메소드 스텁
									dialog.dismiss();
									finish();
									SPN_LoadingSplash.this.onDestroy();
									SPN_MainActivity.main_activity.finish();
									System.exit(0);
								}
							});

							dlg.setNegativeButton("아니요", new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO 자동 생성된 메소드 스텁
									dialog.dismiss();
									SPN_MainActivity.progressDialog.show();
								}
							});
							dlg.show();
						}
					}
				});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SPN_MainActivity.progressDialog.dismiss();
	}

	public void SendServWrite01() {
		try {
			baos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(baos);

			SPN_PhoneInfoObject PhoneInfoMy = new SPN_PhoneInfoObject(imei,
					MacAddr, phoneNumber, null,null,null);
			SPN_MainActivity.MainPhoneInfoMy = PhoneInfoMy;
			SPN_Object spn = new SPN_Object(PhoneInfoMy, null,null,
					SPN_Preferense.CheckAddress);

			Object obc = spn;

			out.writeObject(obc);

			SPN_MainActivity.MainThread.channel.write(ByteBuffer.wrap(baos
					.toByteArray()));

			ByteBuffer data = ByteBuffer.allocate(1024);
			SPN_MainActivity.MainThread.selector.select();

			SPN_MainActivity.MainThread.channel.read(data);

			bais = new ByteArrayInputStream(data.array());

			ois = new ObjectInputStream(bais);

			SPN_Object inspn = (SPN_Object) ois.readObject();

			InMessage = inspn.Message;
			if (InMessage.equals(SPN_Preferense.ParentState)) {
				// 부모로 등록됨
				result = new Intent();
				result.putExtra("loading_flag", SPN_Preferense.ParentState);
				setResult(0, result);
				finish();
				SPN_LoadingSplash.this.onDestroy();

			} else if (InMessage.equals(SPN_Preferense.ChildState)) {
				// 아이로 등록됨
				result = new Intent();
				result.putExtra("loading_flag", SPN_Preferense.ChildState);
				setResult(0, result);
				finish();
				SPN_LoadingSplash.this.onDestroy();
			} else if (InMessage.equals(SPN_Preferense.EmptyState)) {
				// 회원가입하라구 문구 띄움
				handler.sendEmptyMessage(2);
			} else if (InMessage.equals(SPN_Preferense.NothingState)) {
				// 회원가입만 하고 정해진 상태가 없음.
				result = new Intent();
				result.putExtra("loading_flag", SPN_Preferense.NothingState);
				setResult(0, result);
				finish();
				SPN_LoadingSplash.this.onDestroy();
			}
		} catch (Exception e) {
			// TODO 자동 생성된 catch 블록
			handler.sendEmptyMessageDelayed(5, 3000);
		}
	}

	public void SendServWrite02() {
		try {
			baos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(baos);

			SPN_PhoneInfoObject PhoneInfoMy = new SPN_PhoneInfoObject(imei,
					MacAddr, phoneNumber,null,null,null);

			SPN_Object spn = new SPN_Object(PhoneInfoMy, null,null,
					SPN_Preferense.RegisterPhone);

			Object obc = spn;

			out.writeObject(obc);

			SPN_MainActivity.MainThread.channel.write(ByteBuffer.wrap(baos
					.toByteArray()));

			ByteBuffer data = ByteBuffer.allocate(1024);
			SPN_MainActivity.MainThread.selector.select();
			SPN_MainActivity.MainThread.channel.read(data);

			bais = new ByteArrayInputStream(data.array());

			ois = new ObjectInputStream(bais);

			SPN_Object inspn = (SPN_Object) ois.readObject();

			InMessage = inspn.Message;
			if (InMessage.equals(SPN_Preferense.OkState)) {
				handler.sendEmptyMessage(3);
			} else if (InMessage.equals(SPN_Preferense.ErrorState01)) {
				handler.sendEmptyMessage(4);
			}

		} catch (Exception e) {
			// TODO 자동 생성된 catch 블록
			handler.sendEmptyMessageDelayed(6, 3000);
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO 자동 생성된 메소드 스텁
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mtoast = Toast.makeText(SPN_LoadingSplash.this,
						"네트워크 상태가 원활하지 않습니다.", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			case 1:
				mtoast = Toast.makeText(SPN_LoadingSplash.this,
						"서버연결에 필요한 정보 전송중", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			case 2: // 회원가입
				AlertDialog.Builder dlg = new AlertDialog.Builder(
						SPN_LoadingSplash.this);
				dlg.setTitle("알림");
				dlg.setMessage("서버에 기기 등록이 되어있지 않습니다. 기기등록이 되어야 서비스를 이용하실 수 있습니다. 기기 등록을 원하시면 '예'를 눌러주세요\n ※기기등록시 휴대폰의 연락처가 서버에 저장됩니다.");
				dlg.setPositiveButton("예", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 자동 생성된 메소드 스텁
						if (SPNthread != null && SPNthread.isAlive()) {
							SPNthread.interrupt();
						}
						SPNthread = new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO 자동 생성된 메소드 스텁
								SendServWrite02();
								SPNthread.interrupt();
							}
						});
						SPNthread.start();
					}
				});
				dlg.setNegativeButton("아니요", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 자동 생성된 메소드 스텁
						mtoast = Toast.makeText(SPN_LoadingSplash.this,
								"프로그램을 종료합니다.", 0);
						mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
						mtoast.show();
						dialog.dismiss();
						finish();
						SPN_LoadingSplash.this.onDestroy();
						SPN_MainActivity.main_activity.finish();
						System.exit(0);
					}
				});
				dlg.show();
				break;
			case 3:
				mtoast = Toast.makeText(SPN_LoadingSplash.this,
						"기기등록에 성공하였습니다.", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();

				result = new Intent();
				result.putExtra("loading_flag", SPN_Preferense.NothingState);
				setResult(0, result);
				finish();
				SPN_LoadingSplash.this.onDestroy();

				break;
			case 4:
				mtoast = Toast.makeText(SPN_LoadingSplash.this,
						"기기등록에 실패하였습니다. 기기 상태를 확인해주세요(휴대폰번호 및 기기상태)", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				finish();
				SPN_LoadingSplash.this.onDestroy();
				SPN_MainActivity.main_activity.finish();
				System.exit(0);
				break;
			case 5:
				if (SPN_MainActivity.progressDialog.isShowing()) {
					if (!SPN_Connect_BackServ.spnCH.channel.isConnected()) {
						handler.sendEmptyMessage(0);
						SPN_MainActivity.MainServConnect(SPN_LoadingSplash.this);
					}

					if (SPNthread != null && SPNthread.isAlive()) {
						SPNthread.interrupt();
					}

					SPNthread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO 자동 생성된 메소드 스텁
							SendServWrite01();
							SPNthread.interrupt();
						}
					});
					SPNthread.start();
				}
				break;
			case 6:
				if (!SPN_Connect_BackServ.spnCH.channel.isConnected()) {
					handler.sendEmptyMessage(0);
					SPN_MainActivity.MainServConnect(SPN_LoadingSplash.this);
				}
				if (SPNthread != null && SPNthread.isAlive()) {
					SPNthread.interrupt();
				}

				SPNthread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO 자동 생성된 메소드 스텁
						SendServWrite02();
						SPNthread.interrupt();
					}
				});
				SPNthread.start();
				break;
			}
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 0, "넘어가기");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
		{
		case 1:
			Intent jump = new Intent(SPN_LoadingSplash.this, SPN_ParentMain.class);
			
			String str = "0/1/2/3/4/5/6/7/8/9/";
			String[] ChildInfo = str.split("/");
			
			jump.putExtra("ChildInfo", ChildInfo);
			
			startActivity(jump);
			finish();
			SPN_LoadingSplash.this.onDestroy();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
