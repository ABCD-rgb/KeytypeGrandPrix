package application;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ChatServer {
	private static final int PORT = 8000;
	
	private static byte[] incoming = new byte[265];
	
	// connect the server socket to a specific port
	private static DatagramSocket socket;	
	static {
		try {
			socket = new DatagramSocket(PORT);
		} catch (SocketException e) {
			
		}
	}
	
	private static ArrayList<Integer> players = new ArrayList<>();
	
	// set address of the server
    private static final InetAddress address;
    static {
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void main(String[] args) {
    	System.out.println("Server started on port "+PORT);
    	
    	while (true) {
    		DatagramPacket packet = new DatagramPacket(incoming, incoming.length); // prepare packet
    		try {
    			socket.receive(packet);
    		} catch (IOException e) {
    			throw new RuntimeException(e);
    		}
    		
    		String message = new String(packet.getData(), 0, packet.getLength());	// string receive from packet
    		System.out.println("Server received: " + message);
    		
    		// when player just joined the server
    		if (message.contains("init;")) {
    			players.add(packet.getPort());
    		}
    		// when player sent a message to the server
    		else {
    			int userPort = packet.getPort();
    			byte[] byteMessage = message.getBytes();
    			
    			// forward to all other players (except the one who sent the message)
    			for (int forward_port : players) {
                    if (forward_port != userPort) {
                        DatagramPacket forward = new DatagramPacket(byteMessage, byteMessage.length, address, forward_port);
                        try {
                            socket.send(forward);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
    			}
    		}
    	}
    }
}
