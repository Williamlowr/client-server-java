import java.net.*;
import java.security.MessageDigest;
import java.util.Base64;
import java.io.*;

public class TCPServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5050);
            System.out.println("Waiting for client on port 5050...");
            
            while (true) {
                System.out.println("TCP Server waiting for client on port " + serverSocket.getLocalPort() + "...");
                Socket connectionSocket = serverSocket.accept();
                System.out.println("Just connected server port # "
                        + connectionSocket.getLocalSocketAddress()
                        + " to client port # "
                        + connectionSocket.getRemoteSocketAddress());
                
                DataInputStream in = new DataInputStream(connectionSocket.getInputStream());
                
                int fileSize = in.readInt();
                if (fileSize <= 0 || fileSize > 50_000_000) { // <50MB safety cap
                    System.out.println("Invalid file size: " + fileSize);
                    connectionSocket.close();
                    continue;
                }
                
                byte[] file = new byte[fileSize];
                in.readFully(file);
                String hash = compHash(file);
                
                System.out.println("Received file size in bytes = " + fileSize);
                System.out.println("Received file SHA256 hash: " + hash);
                
                // Respond to client
                DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
                String responseJson = "{"
                        + "\"fileSizeBytes\": " + fileSize + ","
                        + "\"sha256\": \"" + hash + "\""
                        + "}";
                
                System.out.println("Sending JSON: " + responseJson);
                out.write((responseJson + "\n").getBytes("UTF-8"));
                out.flush();
                
                // Give the client time to receive the data before closing
                Thread.sleep(100); // Increased from 10ms to 100ms
                
                // Gracefully close the connection
                try {
                    connectionSocket.shutdownOutput();
                } catch (Exception e) {
                    System.out.println("Error during shutdown: " + e.getMessage());
                }
                
                connectionSocket.close();
            }
        } catch (Exception e) {
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