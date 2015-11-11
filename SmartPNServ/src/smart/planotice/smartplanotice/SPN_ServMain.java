package smart.planotice.smartplanotice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Timer;
import java.util.Vector;

public class SPN_ServMain {
	// �������Ӱ��� ���� ���� ����
	private Selector spnSelector = null;
	private ServerSocketChannel spnserversocketchannel = null;
	private ServerSocket spnserversocket = null;
	private SocketAddress spnsocketaddress = null;
	private SPN_Data spndata;
	private int SocketOps = SelectionKey.OP_CONNECT | SelectionKey.OP_READ
			| SelectionKey.OP_WRITE;
	// �������Ӱ��� ���� ���� ��

	// ���� ���� ������ ���� ���� (����� �� ���� �� ���)
	private SPN_ServTh spnServTh = null;

	// ���� ���� ������ ���� �� (����� �� ���� �� ���)
	// ���� ��ſ� �ʿ��� ��Ʈ�� ���� ���� ����
	// ���� ��ſ� �ʿ��� ��Ʈ�� ���� ���� ��

	private Vector UserVector;

	public SPN_ServMain() {
		try {
			spnSelector = Selector.open();
			spnserversocketchannel = ServerSocketChannel.open();
			
			spnserversocket = spnserversocketchannel.socket();
			
			spnsocketaddress = new InetSocketAddress(1021);

			spnserversocket.bind(spnsocketaddress);

			spnserversocketchannel.configureBlocking(false);
			spnserversocketchannel
					.register(spnSelector, SelectionKey.OP_ACCEPT);
			
			spnserversocketchannel.setOption(StandardSocketOptions.SO_RCVBUF, 1024*128);
			spnserversocketchannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			spndata = new SPN_Data();
			spnServTh = new SPN_ServTh();
			UserVector = new Vector();
			System.out.println("��������");
			while (true) {
				int num;
				// Thread.sleep(2000);
				num = spnSelector.select();
				if (num > 0) {
					Iterator it = spnSelector.selectedKeys().iterator();
					// System.out.println("������ iterator");
					while (it.hasNext()) {
						SelectionKey key = (SelectionKey) it.next();
						it.remove();
						if (key.isAcceptable()) {
							System.out.println("������ ����������");
							ServerSocketChannel SpnAcceptsocketchannel = (ServerSocketChannel) key
									.channel();
							SocketChannel SpnSocketChannel = SpnAcceptsocketchannel
									.accept();
							System.out.println(SpnSocketChannel.socket()
									.getInetAddress().getHostAddress()
									.toString()
									+ " //accept����");
							SpnSocketChannel.configureBlocking(false);
							SpnSocketChannel.register(spnSelector, SocketOps);
							Thread.sleep(1000);
						} else if (key.isReadable()) {
							SocketChannel SpnSocketChannel = (SocketChannel) key
									.channel();
							SpnSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 128*1024);
							SpnSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 128*1024);
							SpnSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
							
							spnServTh.readhpobject(spndata, SpnSocketChannel,
									UserVector,key);
						}
					}
				}
			}

		} catch (Exception e) {
			// TODO �ڵ� ������ catch ���
			e.printStackTrace();
		}
	}

	public static void initbuffer(ByteBuffer bf) {
		bf.clear();
		bf.put(new byte[65536]);
		bf.flip();
		bf.clear();
	}

	public static void main(String args[]) {
		SPN_ServMain hpservmain = new SPN_ServMain();

	}
}
