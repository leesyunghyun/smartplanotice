package smart.planotice.smartplanotice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import android.content.Context;
import android.widget.Toast;

public class SPN_GPSThread extends Thread {

	Context context;
	ByteArrayOutputStream baos;
	ObjectOutputStream out;
	ByteArrayInputStream bais;
	ObjectInputStream ois;
	Toast mtoast;
	public String InMessage;
	public int State = 0;

	public SPN_GPSThread(Context context, int State) {
		this.context = context;
		this.State = State;
	}

	@Override
	public void run() {
		super.run();
		// State에 따라서 위치만 전송하는지 패턴그려서 위치전송하는지 구분
		while (true) {
			switch (State) {
			case 0: // 0은 위치만 전송
				try {
					baos = new ByteArrayOutputStream();
					out = new ObjectOutputStream(baos);

					SPN_Object spn = new SPN_Object(
							SPN_MainActivity.MainPhoneInfoMy, null,null,
							SPN_Preferense.ReqGpsState01);

					Object obc = spn;

					out.writeObject(obc);

					SPN_Connect_BackServ.spnCH.channel.write(ByteBuffer
							.wrap(baos.toByteArray()));

					ByteBuffer data = ByteBuffer.allocate(1024);
					SPN_Connect_BackServ.spnCH.selector.select();
					SPN_Connect_BackServ.spnCH.channel.read(data);

					bais = new ByteArrayInputStream(data.array());

					ois = new ObjectInputStream(bais);

					SPN_Object inspn = (SPN_Object) ois.readObject();

					InMessage = inspn.Message;
					if (InMessage.equals(SPN_Preferense.OkState)) {
						// handler.sendEmptyMessage(3);
					} else if (InMessage.equals(SPN_Preferense.ErrorState01)) {
						// handler.sendEmptyMessage(4);
					}

				} catch (Exception e) {
					// TODO 자동 생성된 catch 블록
					// handler.sendEmptyMessageDelayed(6, 3000);
				}
				break;
			case 1:
				break;
			}

		}
	}
}
