package multicall;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * Basic Echo Client Socket
 */
public class SocketListener implements WebSocketListener {
	private final CountDownLatch closeLatch;
	private Session session;

	public SocketListener() {
		this.closeLatch = new CountDownLatch(1);
	}

	public boolean awaitClose(int duration, TimeUnit unit)
			throws InterruptedException {
		return this.closeLatch.await(duration, unit);
	}

	public void onClose(int statusCode, String reason) {
		System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
		this.session = null;
		this.closeLatch.countDown(); // trigger latch
	}

	public void onMessage(String msg) {
		System.out.printf("Got msg: %s%n", msg);
	}

	@Override
	public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		System.out.println("onWebSocketBinary response");
		SocketResponseParser.parseByte(arg0);
	}

	@Override
	public void onWebSocketClose(int arg0, String arg1) {
		System.out.println("onWebSocketClose " + arg1);
	}

	@Override
	public void onWebSocketConnect(Session session) {
		// TODO Auto-generated method stub
		System.out.println("onWebSocketConnect");
		this.session = session;
		sendRegistrationRequest();
	}

	@Override
	public void onWebSocketError(Throwable t) {
	    System.out.println("onWebSocketError: " + t.toString());
	}

	@Override
	public void onWebSocketText(String text) {
		System.out.println("onWebSocketText: " + text);
	}
	
	private void sendRegistrationRequest() {
		if (session != null) {
			RemoteEndpoint remote = session.getRemote();
			ByteBuffer buf  = ByteBuffer.wrap(MulticallRequest.getRequestBytes());
			try {
				remote.sendBytes(buf);
				System.out.println("Sent the request!");
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
		}
	}
}
