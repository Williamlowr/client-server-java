import java.net.*;
import java.security.MessageDigest;
import java.util.Base64;
import java.io.*;

public class TCPServer 
{
	
	public static void main(String[] args) 
	{
				
		ServerSocket serverSocket;
		
		try 
		{
		
			   serverSocket = new ServerSocket(5050); //creates a socket and binds it to port 5050
			   //serverSocket = new ServerSocket(0); //creates a socket and binds it to next available port 

			   System.out.println("Waiting for client on port 5050...");
			   
			   while (true)
			   {
			   
				   System.out.println("TCP Server waiting for client on port " + serverSocket.getLocalPort() + "...");
				   
				   Socket connectionSocket = serverSocket.accept();  //listens for connection and 
				   										// creates a connection socket for communication
				   
				   System.out.println("Just connected server port # " + connectionSocket.getLocalSocketAddress() + " to client port # " + connectionSocket.getRemoteSocketAddress());
				   
				   DataInputStream in = new DataInputStream(connectionSocket.getInputStream()); //get incoming data in bytes from connection socket
				   
				   int fileSize = in.readInt();
				   byte[] file = new byte[fileSize];
				   in.readFully(file);

				   String hash = compHash(file);
				   
				   DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream()); //setup a stream for outgoing bytes of data	   
				   String responseJson = "{"
						+ "\"fileSizeBytes\": " + fileSize + ","
						+ "\"sha256\": \"" + hash + "\""
						+ "}";

					System.out.println("Sending JSON: " + responseJson);
					out.writeUTF(responseJson);
				   
				   connectionSocket.close();  //close connection socket after this exchange
				   
				   System.out.println("Received file size in bits = " + (fileSize));
				   System.out.println("Received fileSHA256 hash: " + hash);
			   }
	
		} 
		catch (IOException e)
		{
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
