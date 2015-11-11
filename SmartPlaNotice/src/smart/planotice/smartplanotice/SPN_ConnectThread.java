package smart.planotice.smartplanotice;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import android.content.Context;
import android.telephony.TelephonyManager;

public class SPN_ConnectThread extends Thread {
	Context context;
	public static SocketChannel channel;
	public static Selector selector;
	int message = 0;
	

	public SPN_ConnectThread(Context context) {
		this.context = context;
		try {
			selector = Selector.open();
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_READ);
		} catch (Exception e) {

		}
	}

	@Override
	public void run() {
		try {
			String str = "192.168.0.8";
			if (!channel.isConnected()) {
				channel = SocketChannel.open(new InetSocketAddress(InetAddress
						.getByName(str), 1021));	
			}
		} catch (Exception e) {

		}
		super.run();
	}
}
