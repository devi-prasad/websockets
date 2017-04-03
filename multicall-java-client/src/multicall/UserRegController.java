package multicall;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.net.URI;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;

public class UserRegController implements WebSocketListener {
    private CountDownLatch closeLatch;
    private String mgwurl;
    private String subprotocol;
    private Session session;
    private MulticallUser user;
    private MulticallRequest request;
    private MulticallResponse response;
    private ResponseObserver responseObserver;
    private WebSocketClient endpoint;

    public UserRegController(String mgwurl, String subprotocol) {
        this.mgwurl = mgwurl;
        this.subprotocol = subprotocol;
        this.session = null;
        this.user = null;
        this.responseObserver = null;
        this.endpoint = null;
//        this.closeLatch = new CountDownLatch(1);
    }

    public void setMulticallUser(MulticallUser user) {
        this.user = user;
    }

    public void setResponseObserver(ResponseObserver ro) {
        assert(ro != null);
        if (ro != null) {
            this.responseObserver = ro;
        }
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return this.closeLatch.await(duration, unit);
    }

	public void onClose(int statusCode, String reason) {
		System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
/*		try {
		    if (this.session.isOpen()) {
			    this.session.close( going away 1001, "waited too long!");
		    }
		} catch (IOException ioex) {}
*/
		this.session = null;
		this.closeLatch.countDown(); // trigger latch
	}

	@Override
	public void onWebSocketBinary(byte[] payload, int offset, int len) {
		System.out.println("onWebSocketBinary response");
		this.response = MulticallResponse.parse(payload, offset, len);
		responseObserver.onResponse(this.response);
	}

	@Override
	public void onWebSocketClose(int statuscode, String reason) {
		System.out.println("onWebSocketClose - status: " + statuscode + 
		                                             ", reason: " + reason);
	}

	@Override
	public void onWebSocketConnect(Session session) {
		System.out.println("onWebSocketConnect");
		this.session = session;
		sendRegistrationRequest();
	}

	@Override
	public void onWebSocketError(Throwable t) {
	    System.out.println("onWebSocketError: " + t.toString());
        disconnectWithGateway();
	}

    @Override
    public void onWebSocketText(String text) {
        System.out.println("onWebSocketText: " + text);
	}
	
	private void sendRegistrationRequest()
    {
        assert(this.user != null || this.session != null);
        if (this.user == null || this.session == null) return;

        RemoteEndpoint remote = session.getRemote();
        this.request = MulticallRequest.createUserRegRequest(this.user.getName(),
                                                             this.user.getEmail(),
                                                             this.user.getTelnum());

        ByteBuffer buf  = ByteBuffer.wrap(this.request.getBytes());
        try {
            remote.sendBytes(buf);
            System.out.println("Sent the request!");
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public void disconnectWithGateway() {
        if (this.endpoint != null ) {
            this.closeLatch.countDown();
        }
    }
    
    public void connectWithGateway() {
        assert(this.user != null);
        if (this.user == null) return;

        this.endpoint = new WebSocketClient();
        this.closeLatch = new CountDownLatch(1);
        try
        {
            endpoint.start();

            URI uri = new URI(this.mgwurl);
            ClientUpgradeRequest ur = new ClientUpgradeRequest();
            ur.setSubProtocols(this.subprotocol);
            System.out.printf("Connecting to : %s%n", uri);
            endpoint.connect(this, uri, ur);

            // wait for closed socket connection.
            this.awaitClose(10, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
        finally {
            disconnectWithGateway();
            try {
                if (this.endpoint!= null) {
                    this.endpoint.stop();
                    this.endpoint = null;
                    this.closeLatch = null;
                }
            } catch (Exception e) {
            }
        }
	}
}
