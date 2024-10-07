import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Base64;

public class TCPClient {

	public static void main(String[] args) {
		String serverName = args[0];
		String filePath = args[1];
		int port = 9999;

		try {
			System.out.println("Connecting to " + serverName + " on port " + port);

			Socket clientSocket = new Socket(serverName, port); // create socket for connecting to server

			System.out.println("Just connected to " + clientSocket.getRemoteSocketAddress());

			byte[] file = Files.readAllBytes(Paths.get(filePath));
			// compute hash
			String hash = compHash(file);

			OutputStream outToServer = clientSocket.getOutputStream(); // stream of bytes

			DataOutputStream out = new DataOutputStream(outToServer);

			out.writeInt(file.length);
			out.write(file);

			double startTime = System.currentTimeMillis(); // start time

			InputStream inFromServer = clientSocket.getInputStream(); // stream of bytes

			DataInputStream in = new DataInputStream(inFromServer);

			String serverHash = in.readUTF();

			double endTime = System.currentTimeMillis(); // end time
			double totalTimeMS = endTime - startTime;

			// check hash
			if (hash.equals(serverHash)) {
				System.out.println("Successfully sent!");
			} else {
				System.out.println("Error!");
			}

			long fileSizeByte = file.length;
			long fileSizeBit = fileSizeByte * 8;
			double totalTime = totalTimeMS/1000;

			double bPS = (fileSizeBit / (totalTime / 2));
			double throughput = bPS / 1_000_000; // Mbps

			System.out.println("File name: " + Paths.get(filePath).getFileName());
			System.out.println("SHA256 hash: " + hash);
			System.out.println("File size in bits = " + (fileSizeBit));
			System.out.println("Time taken (approx. one way) = " + String.format("%.2f", totalTimeMS / 2) + " ms");
			System.out.println("Throughput = " + String.format("%.2f", throughput) + " Mbps");

			clientSocket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String compHash(byte[] file) {
		try {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			byte[] hash = sha256.digest(file);
			return Base64.getEncoder().encodeToString(hash);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}