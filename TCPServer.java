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
                
                try {
                    // Set socket timeout to prevent hanging
                    connectionSocket.setSoTimeout(5000);
                    
                    DataInputStream in = new DataInputStream(connectionSocket.getInputStream());
                    
                    int fileSize = in.readInt();
                    if (fileSize <= 0 || fileSize > 50_000_000) {
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
                    OutputStream out = connectionSocket.getOutputStream();
                    String responseJson = "{"
                            + "\"fileSizeBytes\": " + fileSize + ","
                            + "\"sha256\": \"" + hash + "\""
                            + "}";
                    
                    System.out.println("Sending JSON: " + responseJson);
                    
                    // Write response and flush
                    out.write((responseJson + "\n").getBytes("UTF-8"));
                    out.flush();
                    
                    System.out.println("Response sent, waiting for client to close...");
                    
                    // DON'T close immediately - wait for client to close or read EOF
                    try {
                        // Try to read one more byte - this will block until client closes
                        int eof = in.read();
                        if (eof == -1) {
                            System.out.println("Client closed connection gracefully");
                        }
                    } catch (SocketTimeoutException e) {
                        System.out.println("Client didn't close within timeout, closing anyway");
                    } catch (IOException e) {
                        System.out.println("Connection closed by client: " + e.getMessage());
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error handling client: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    try {
                        connectionSocket.close();
                        System.out.println("Socket closed\n");
                    } catch (IOException e) {
                        System.err.println("Error closing socket: " + e.getMessage());
                    }
                }
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