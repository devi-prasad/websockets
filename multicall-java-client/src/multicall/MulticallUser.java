package multicall;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

public class MulticallUser {
	final static String destUri = "ws://127.0.0.1:12345";

	public static void main(String[] args)
    {
		connect();
    }

	private static void connect() {
        WebSocketClient client = new WebSocketClient();
        SocketListener socket = new SocketListener();
        try
        {
            client.start();

            URI uri = new URI(destUri);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            request.setSubProtocols("multicall-user");
            System.out.printf("Connecting to : %s%n", uri);
            client.connect(socket, uri, request);

            // wait for closed socket connection.
            socket.awaitClose(10, TimeUnit.SECONDS);
        }
        catch (Exception e)
        {
            System.out.println("Exception: " + e.toString());
        }
        finally
        {
            try
            {
                client.stop();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
	}
}
