import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

public class Client {
	private final int selfId;
	Socket socket;
	Thread readThread, writeThread;

	public Client(InetAddress server, int port, int clientId)
			throws IOException {
		this.selfId = clientId;
		socket = new Socket(server, port);
		OutputStream out = socket.getOutputStream();
		InputStream in = socket.getInputStream();
		out.write(clientId);
		out.flush();
		readThread = new Thread(new ReadRunnable(in));
		writeThread = new Thread(new WriteRunnable(out));
	}

	public void start() {
		readThread.start();
		writeThread.start();
		try {
			readThread.join();
			writeThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private class ReadRunnable implements Runnable {

		final InputStream in;

		ReadRunnable(InputStream in) {
			this.in = in;
		}

		@Override
		public void run() {
			byte[] buffer = new byte[Const.BUFFER_SIZE + Const.HEADER_SIZE];
			int len;
			try {
				while ((len = in.read(buffer)) != -1) {
					// Check if self is target
					if (buffer[0] != selfId) {
						System.out.println(String.format(
								"Client %d: Received packet for %d", selfId,
								buffer[0]));
						continue;
					} else {
						System.out.println(String.format(
								"Client %d received packet from %d", selfId,
								buffer[1]));
					}
					String fileName = String.format("file_from_%d_to_%d",
							buffer[1], buffer[0]);
					FileOutputStream fout = new FileOutputStream(new File(
							fileName), true);
					fout.write(buffer, Const.HEADER_SIZE, len
							- Const.HEADER_SIZE);
					fout.flush();
					fout.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class WriteRunnable implements Runnable {
		final OutputStream out;

		WriteRunnable(OutputStream out) {
			this.out = out;
		}

		@Override
		public void run() {
			byte[] buffer = new byte[Const.BUFFER_SIZE + Const.HEADER_SIZE];

			try {
				Random random = new Random();
				Thread.sleep(random.nextInt(5000) + 1000);
				int target = random.nextInt(Const.MAX_CLIENT);
				String fileName;
				if (random.nextBoolean())
					fileName = Const.FILE_A;
				else
					fileName = Const.FILE_B;
				FileInputStream fin = new FileInputStream(new File(fileName));
				int len;
				int seq = 0;
				System.out.println(String.format(
						"Client %d sent %s to client %d", selfId, fileName,
						target));
				while ((len = fin.read(buffer, Const.HEADER_SIZE,
						Const.BUFFER_SIZE)) != -1) {
					buffer[0] = (byte) target;
					buffer[1] = (byte) selfId;
					buffer[2] = (byte) seq++;
					out.write(buffer, 0, len + Const.HEADER_SIZE);
				}
				out.flush();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 3) // Test for correct # of args
			throw new IllegalArgumentException(
					"Parameter(s): <Server> <Port> <ClientId>");

		InetAddress serverAddr = InetAddress.getByName(args[0]);
		int servPort = Integer.parseInt(args[1]);
		int clientId = Integer.parseInt(args[2]);

		if (clientId >= Const.MAX_CLIENT || clientId < 0) {
			throw new IllegalArgumentException(String.format(
					"Clientd must between 0 and %d", Const.MAX_CLIENT - 1));
		}
		Client cln = new Client(serverAddr, servPort, clientId);
		cln.start();

	}

}
