package smart.planotice.smartplanotice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import smart.planotice.smartplanotice.R;




import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SPN_MainActivity extends Activity {

	Button btn1, btn2, btn3;
	// public static SPN_ConnectThread spnCH;
	public static Activity main_activity;
	public static ProgressDialog progressDialog = null;
	public ProgressDialog progressDialog2 = null;
	public static SPN_PhoneInfoObject MainPhoneInfoMy;
	private Thread SPNthread;
	ByteArrayOutputStream baos;
	ObjectOutputStream out;
	ByteArrayInputStream bais;
	ObjectInputStream ois;
	Toast mtoast;
	Intent result;
	public String InMessage;
	
	
	public static SPN_ConnectThreadMain MainThread;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btn1 = (Button) findViewById(R.id.btnMain01);
		btn2 = (Button) findViewById(R.id.btnMain02);
		btn3 = (Button) findViewById(R.id.btnMain03);

		main_activity = SPN_MainActivity.this;

		if (!isMyServiceRunning(main_activity,
				"smart.planotice.smartplanotice.SPN_Connect_BackServ")) {
			startService(new Intent(SPN_MainActivity.this,
					SPN_Connect_BackServ.class));
		}
		MainThread = new SPN_ConnectThreadMain(SPN_MainActivity.this);
		
		startActivityForResult(new Intent(SPN_MainActivity.this,
				SPN_LoadingSplash.class), 0);

		progressDialog2 = new ProgressDialog(SPN_MainActivity.this);
		progressDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog2.setMessage(Html.fromHtml("<FONT Color='Black'>"
				+ "로딩중입니다...잠시만 기다려주세요" + "</FONT>"));
		progressDialog2.setIcon(0);
		progressDialog2.setCancelable(true);

		btn1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 자동 생성된 메소드 스텁
				// 부모접속
				progressDialog2.show();

			}
		});

		btn2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 자동 생성된 메소드 스텁
				// 자녀관리
				Intent intent = new Intent(SPN_MainActivity.this,
						SPN_ChildSetting.class);
				startActivity(intent);
			}
		});

		btn3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 자동 생성된 메소드 스텁
				// 도움말
				Intent intent = new Intent(SPN_MainActivity.this,
						SPN_HELP.class);
				startActivity(intent);
			}
		});

		progressDialog2.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(1);
			}
		});

		progressDialog2.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

	}

	public static void ProgressSet(Activity activity, String meesage) {
		progressDialog = new ProgressDialog(activity);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(Html.fromHtml("<FONT Color='Black'>"
				+ meesage + "</FONT>"));
		progressDialog.setIcon(0);
		progressDialog.setCancelable(true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO 자동 생성된 메소드 스텁
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (data != null) {
				if (data.getStringExtra("loading_flag").equals(
						SPN_Preferense.ParentState)) {

				} else if (data.getStringExtra("loading_flag").equals(
						SPN_Preferense.ChildState)) {
					startActivityForResult(new Intent(SPN_MainActivity.this, SPN_ChildMain.class),1);
				} else if (data.getStringExtra("loading_flag").equals(
						SPN_Preferense.NothingState)) {

				} else if (data.getStringExtra("loading_flag").equals(
						SPN_Preferense.EmptyState)) {
					// System.exit(0);
				}
			}
		}
		else if(requestCode == 1)
		{
			SPN_MainActivity.this.onBackPressed();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO 자동 생성된 메소드 스텁
		super.onBackPressed();
		// UnConnect();
	}

	public static void MainServConnect(Context context) {
		if (!MainThread.channel.isConnected()) {
			if (MainThread.isInterrupted()) {
				MainThread = new SPN_ConnectThreadMain(main_activity);
				MainThread.start();
			} else {
				MainThread.interrupt();
				MainThread = new SPN_ConnectThreadMain(main_activity);
				MainThread.start();
			}
			
		}

	}
	
	public void UnConnect() {
		if (SPN_Connect_BackServ.spnCH.channel.isConnected()) {
			if (SPNthread != null && SPNthread.isAlive()) {
				SPNthread.interrupt();
			}

			SPNthread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO 자동 생성된 메소드 스텁
					try {
						baos = new ByteArrayOutputStream();
						out = new ObjectOutputStream(baos);
						SPN_Object hp = new SPN_Object(null, null, null,
								SPN_Preferense.LogOut);

						Object obc = hp;

						out.writeObject(obc);
						SPN_MainActivity.MainThread.channel.write(ByteBuffer
								.wrap(baos.toByteArray()));
						SPN_MainActivity.MainThread.channel.close();
						SPN_MainActivity.MainThread.selector.close();
						SPN_MainActivity.MainThread.interrupt();
					} catch (IOException e) {
						// TODO 자동 생성된 catch 블록
						e.printStackTrace();
					}
				}
			});
			SPNthread.start();
		}
	}

	// 서비스가 돌아가고 있는지 확인
	private boolean isMyServiceRunning(Context ctx, String s_service_name) {

		ActivityManager manager = (ActivityManager) ctx
				.getSystemService(Activity.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (s_service_name.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public void SendServWrite01() {
		try {
			baos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(baos);

			SPN_Object spn = new SPN_Object(MainPhoneInfoMy, null, null,
					SPN_Preferense.ReqMyGpsState);

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
			if (InMessage.equals(SPN_Preferense.ErrorState01)) {
				handler.sendEmptyMessage(3);
			} else {
				handler.sendEmptyMessage(2);
			}
			progressDialog2.dismiss();
		} catch (Exception e) {
			// TODO 자동 생성된 catch 블록
			handler.sendEmptyMessageDelayed(1, 3000);
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO 자동 생성된 메소드 스텁
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mtoast = Toast.makeText(SPN_MainActivity.this,
						"네트워크 상태가 원활하지 않습니다.", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			case 1:
				if (progressDialog2.isShowing()) {
					if (!SPN_MainActivity.MainThread.channel.isConnected()) {
						handler.sendEmptyMessage(0);
						SPN_MainActivity.MainServConnect(SPN_MainActivity.this);
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
			case 2:
				mtoast = Toast.makeText(SPN_MainActivity.this,
						"서버로부터 데이터 전송이 완료되었습니다.", 0);
				mtoast.show();

				// 여기서 받아온 값으로 다음 인텐트에 값을 넘김 예를들어 자녀의 수 이런 정보
				Intent intent = new Intent(SPN_MainActivity.this,
						SPN_ParentMain.class);
				String[] ChildInfo = InMessage.split("/");
					
				intent.putExtra("ChildInfo", ChildInfo);
				
				startActivity(intent);
				break;
			case 3:
				mtoast = Toast.makeText(SPN_MainActivity.this,
						"오류발생, 다시시도해주세요.", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			}
		}
	};

}
