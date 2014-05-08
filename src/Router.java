import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Router {

	private ServerSocket servSock;
	private Socket[] clientPool;

	public Router(int port, int maxClient) throws IOException {
		servSock = new ServerSocket(port);
		clientPool = new Socket[maxClient];
	}

	public void start() throws IOException {
		while (true) { // Run forever, accepting and servicing connections
			Socket clntSock = servSock.accept(); // Get client connection
			new Thread(new RouterRunnable(clntSock)).start();
		}
	}

	private class RouterRunnable implements Runnable {

		Socket clientSocket;
		byte[] buffer;

		RouterRunnable(Socket clntSock) {
			this.clientSocket = clntSock;
			buffer = new byte[Const.BUFFER_SIZE + Const.HEADER_SIZE];
		}

		@Override
		public void run() {
			int len;
			int targetClient;
			try {
				InputStream in = this.clientSocket.getInputStream();
				// First byte is client number
				int clientIndex = in.read();
				System.out.println("Received " + clientIndex);
				if (clientIndex > clientPool.length - 1) {
					System.out.println("Invalid client index:" + clientIndex);
					return;
				}
				clientPool[clientIndex] = this.clientSocket;

				while ((len = in.read(buffer)) != -1) {
					if (len <= 2) {
						// Invalid packet
						continue;
					}
					targetClient = buffer[0];
					if (clientPool[targetClient] == null) {
						System.out.println("Unknown target client: "
								+ targetClient);
					} else {
						OutputStream out = clientPool[targetClient].getOutputStream();
						out.write(buffer);
						out.flush();
					}
					Arrays.fill(buffer, (byte) 0);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (true) {

			}
		}
	}

	private class RouterWriteRunnable implements Runnable {

		Socket clientSocket;
		byte[] dataToSend;

		public RouterWriteRunnable(Socket client, byte[] data, int len) {
			clientSocket = client;
			dataToSend = new byte[len];
			System.arraycopy(data, 0, dataToSend, 0, len);
		}

		@Override
		public void run() {
			try {
				OutputStream out = clientSocket.getOutputStream();
				out.write(dataToSend);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 1) // Test for correct # of args
			throw new IllegalArgumentException("Parameter(s): <Port>");

		int servPort = Integer.parseInt(args[0]);

		Router router = new Router(servPort, Const.MAX_CLIENT);
		router.start();
	}

}
