package smart.planotice.smartplanotice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import smart.planotice.smartplanotice.R;




import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnShowListener;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SPN_ChildAdd extends Activity {

	TextView tv1, tv2, tv3;
	EditText et1, et2;
	Button btn1, btn2, btn3;

	private Thread SPNthread;
	ByteArrayOutputStream baos;
	ObjectOutputStream out;
	ByteArrayInputStream bais;
	ObjectInputStream ois;
	Toast mtoast;
	private ProgressDialog progressDialog = null;
	String InMessage;
	Activity ChildAdd;
	CountDownTimer TCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.childadd);
		tv1 = (TextView) findViewById(R.id.tvchildadd02);
		tv2 = (TextView) findViewById(R.id.tvchildadd03);
		tv3 = (TextView) findViewById(R.id.tvchildadd04);
		et1 = (EditText) findViewById(R.id.etchildadd01);
		et2 = (EditText) findViewById(R.id.etchildadd02);
		btn1 = (Button) findViewById(R.id.btnchildadd01);
		btn2 = (Button) findViewById(R.id.btnchildadd02);
		btn3 = (Button) findViewById(R.id.btnchildadd03);
		
		ChildAdd = SPN_ChildAdd.this;
		SPN_MainActivity.ProgressSet(ChildAdd, "�ε���...��ø� ��ٷ� �ּ���!");

		btn1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO �ڵ� ������ �޼ҵ� ����
				Intent i = new Intent(Intent.ACTION_PICK);
				i.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
				startActivityForResult(i, 0);
			}
		});

		btn2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!et1.getText().toString().isEmpty()
						&& (et1.getText().toString().length() > 9)) {
					SPN_MainActivity.progressDialog.show();
					handler.sendEmptyMessage(4);
				} else {
					handler.sendEmptyMessage(6);
				}
			}
		});

		btn3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO �ڵ� ������ �޼ҵ� ����
				if (!et2.getText().toString().isEmpty()) {
					SPN_MainActivity.progressDialog.show();
					handler.sendEmptyMessage(9);
				} else {
					handler.sendEmptyMessage(10);
				}
			}
		});

		SPN_MainActivity.progressDialog
				.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
	}

	public void SendServWrite01(String PhoneNumber) {
		try {
			if (SPN_MainActivity.MainPhoneInfoMy.PhoneNumber
					.equals(PhoneNumber)) {
				handler.sendEmptyMessage(3);
				SPN_MainActivity.progressDialog.dismiss();
				return;
			}

			baos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(baos);

			SPN_PhoneInfoObject PhoneInfoYou = new SPN_PhoneInfoObject(null,
					null, PhoneNumber, null, null, null);

			SPN_Object spn = new SPN_Object(SPN_MainActivity.MainPhoneInfoMy,
					PhoneInfoYou, null, SPN_Preferense.CheckChildState);

			Object obc = spn;

			out.writeObject(obc);

			SPN_MainActivity.MainThread.channel.write(ByteBuffer.wrap(baos
					.toByteArray()));
			ByteBuffer data = ByteBuffer.allocate(4096);
			SPN_MainActivity.MainThread.selector.select();
			SPN_MainActivity.MainThread.channel.read(data);

			bais = new ByteArrayInputStream(data.array());

			ois = new ObjectInputStream(bais);

			SPN_Object inspn = (SPN_Object) ois.readObject();

			InMessage = inspn.Message;
			if (InMessage.equals(SPN_Preferense.SuccessState)) {
				handler.sendEmptyMessage(1);
			} else if (InMessage.equals(SPN_Preferense.FailState)) {
				handler.sendEmptyMessage(2);
			} else if (InMessage.equals(SPN_Preferense.ErrorState02)) {
				handler.sendEmptyMessage(3);
			} else if (InMessage.equals(SPN_Preferense.ErrorState03)) {
				handler.sendEmptyMessage(7);
			} else {
				handler.sendEmptyMessage(5);
			}
			SPN_MainActivity.progressDialog.dismiss();
		} catch (Exception e) {
			// TODO �ڵ� ������ catch ���
			handler.sendEmptyMessageDelayed(4, 3000);
		}

	}

	public void SendServWrite02() {
		try {
			baos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(baos);

			SPN_PhoneInfoObject PhoneInfoYou = new SPN_PhoneInfoObject(et2
					.getText().toString(), null, et1.getText().toString(),
					null, null, null);

			SPN_Object spn = new SPN_Object(SPN_MainActivity.MainPhoneInfoMy,
					PhoneInfoYou, null, SPN_Preferense.AddChildState);

			Object obc = spn;

			out.writeObject(obc);

			SPN_MainActivity.MainThread.channel.write(ByteBuffer.wrap(baos
					.toByteArray()));
			ByteBuffer data = ByteBuffer.allocate(4096);
			SPN_MainActivity.MainThread.selector.select();
			SPN_MainActivity.MainThread.channel.read(data);

			bais = new ByteArrayInputStream(data.array());

			ois = new ObjectInputStream(bais);

			SPN_Object inspn = (SPN_Object) ois.readObject();

			InMessage = inspn.Message;
			if (InMessage.equals(SPN_Preferense.SuccessState)) {
				handler.sendEmptyMessage(8);
			} else if (InMessage.equals(SPN_Preferense.ErrorState01)) {
				handler.sendEmptyMessage(10);
			} else {
				handler.sendEmptyMessage(5);
			}
			SPN_MainActivity.progressDialog.dismiss();
		} catch (Exception e) {
			// TODO �ڵ� ������ catch ���
			handler.sendEmptyMessageDelayed(9, 3000);
		}

	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO �ڵ� ������ �޼ҵ� ����
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mtoast = Toast.makeText(SPN_ChildAdd.this,
						"��Ʈ��ũ ���°� ��Ȱ���� �ʽ��ϴ�.", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			case 1:
				mtoast = Toast.makeText(SPN_ChildAdd.this,
						"��û�Ϸ�, �ϴ� ������ȣ�� �Է����ּ���.", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				tv1.setVisibility(View.VISIBLE);
				tv2.setVisibility(View.VISIBLE);
				tv3.setVisibility(View.VISIBLE);
				et2.setVisibility(View.VISIBLE);
				btn2.setEnabled(false);
				btn3.setVisibility(View.VISIBLE);
				TCount = new CountDownTimer(180000, 1000) {

					@Override
					public void onTick(long millisUntilFinished) {
						// TODO �ڵ� ������ �޼ҵ� ����
						tv3.setText((int) millisUntilFinished / 1000 + " ��");
					}

					@Override
					public void onFinish() {
						// TODO �ڵ� ������ �޼ҵ� ����
						tv1.setText("������ȣ �Է� �ð��� �ʰ��Ǿ����ϴ�.");
						tv2.setVisibility(View.INVISIBLE);
						tv3.setVisibility(View.INVISIBLE);
						et2.setVisibility(View.INVISIBLE);
						btn3.setVisibility(View.INVISIBLE);
						btn2.setEnabled(true);
					}
				}.start();
				
				break;
			case 2:
				mtoast = Toast.makeText(SPN_ChildAdd.this,
						"�������� �ʴ� ������Դϴ�. ������� ���� ���ּ���.", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			case 3:
				mtoast = Toast.makeText(SPN_ChildAdd.this,
						"�ڱ� �ڽ��� ����� �� �����ϴ�.", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			case 4:
				if (SPN_MainActivity.progressDialog.isShowing()) {
					if (!SPN_Connect_BackServ.spnCH.channel.isConnected()) {
						handler.sendEmptyMessage(0);
						SPN_MainActivity.MainServConnect(SPN_ChildAdd.this);
					}

					if (SPNthread != null && SPNthread.isAlive()) {
						SPNthread.interrupt();
					}

					SPNthread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO �ڵ� ������ �޼ҵ� ����
							SendServWrite01(et1.getText().toString());
							SPNthread.interrupt();
						}
					});
					SPNthread.start();
				}
				break;
			case 5:
				mtoast = Toast.makeText(SPN_ChildAdd.this,
						"�����߻�, �̹� ����� �ڳ��� �� �ֽ��ϴ�. \nȮ�� �� �ٽ� �õ��� �ּ���.", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			case 6:
				mtoast = Toast.makeText(SPN_ChildAdd.this,
						"����ó�� �ʹ� ª�ų� �ùٸ��� �ʽ��ϴ�.\nEx)01012345678", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			case 7:
				mtoast = Toast.makeText(SPN_ChildAdd.this,
						"���� �޴����� �����ְų� �������� �����Դϴ�.", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			case 8:
				mtoast = Toast.makeText(SPN_ChildAdd.this, "�ڳ��� ����", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				TCount.cancel();
				finish();
				break;
			case 9:
				if (SPN_MainActivity.progressDialog.isShowing()) {
					if (!SPN_Connect_BackServ.spnCH.channel.isConnected()) {
						handler.sendEmptyMessage(0);
						SPN_MainActivity.MainServConnect(SPN_ChildAdd.this);
					}

					if (SPNthread != null && SPNthread.isAlive()) {
						SPNthread.interrupt();
					}

					SPNthread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO �ڵ� ������ �޼ҵ� ����
							SendServWrite02();
							SPNthread.interrupt();
						}
					});

					SPNthread.start();
				}
				break;
			case 10:
				mtoast = Toast.makeText(SPN_ChildAdd.this,
						"������ȣ�� �ʹ� ª�ų� �ùٸ��� �ʽ��ϴ�.", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO �ڵ� ������ �޼ҵ� ����
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			Cursor cursor = getContentResolver()
					.query(data.getData(),
							new String[] {
									ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
									ContactsContract.CommonDataKinds.Phone.NUMBER },
							null, null, null);
			cursor.moveToFirst();
			String name = cursor.getString(0);
			String number = cursor.getString(1);
			String number2 = number.replace("+82", "0");
			String number3[] = number2.split("-");
			String number4 = "";
			for (int i = 0; i < number3.length; i++) {
				number4 += number3[i];
			}
			et1.setText(number4);
			cursor.close();
		}
	}

}
