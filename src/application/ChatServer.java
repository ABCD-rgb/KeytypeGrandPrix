package application;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


/* This Class runs the server (to allow multiplayer connection) */


public class ChatServer {
	private static DatagramSocket socket;	
	private static final int PORT = 8000;	// port number for the server socket
	private static final InetAddress address;
	private static ArrayList<Integer> players = new ArrayList<>();	// list to store the players' port numbers
	private static byte[] incoming = new byte[265];	// byte array to store incoming data
	private static List<String> previousChats = new ArrayList<>();	// list to store previous chats
	private static int readyClients = 0;	// number of clients that are ready
	
	// connect the server socket to a specific port
	static {
		try {
			socket = new DatagramSocket(PORT);
		} catch (SocketException e) {
			// if the port is already in use
			throw new RuntimeException(e);			
		}
	}
	// set address of the server
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
    			socket.receive(packet);	// receive packet
    		} catch (IOException e) {
    			throw new RuntimeException(e);
    		}
    		
    		String message = new String(packet.getData(), 0, packet.getLength());	// string receive from packet
    		System.out.println("Server received: " + message);
    		
    		// when player just joined the server
    		if (message.contains("init;")) {
    		    int userPort = packet.getPort(); // get the port number of the player
    		    players.add(userPort);	// add the player to the list
    		    String enterMessage = message.split(";")[1] + " has entered the waiting room.";
    		    byte[] enterBytes = enterMessage.getBytes();
    		    
    		    // forward the enter message to all other players
    		    for (int forward_port : players) {
    		        if (forward_port != userPort) {
    		            DatagramPacket enterPacket = new DatagramPacket(enterBytes, enterBytes.length, packet.getAddress(), forward_port);
    		            try {
    		                socket.send(enterPacket);
    		            } catch (IOException e) {
    		                throw new RuntimeException(e);
    		            }
    		        }
    		    }
    		    previousChats.add(enterMessage); // store the enter message in the list
    		}
    		
    		// when player sent a message to the server
    		else {
    			int userPort = packet.getPort();
    		    byte[] byteMessage = message.getBytes();
    			
    		    // when player wants to fetch previous chats
    		    if (message.startsWith("fetch:")) {
    		        String identifier = message.substring(6);
    		        String fetchResponse = "fetchResponse:";
    		        
    		        // concatenate previous chats and messages
    		        for (String chat : previousChats) {
    		            fetchResponse += chat + "|";
    		        }
    		        
    		        byte[] fetchBytes = fetchResponse.getBytes();
    		        DatagramPacket fetchPacket = new DatagramPacket(fetchBytes, fetchBytes.length, packet.getAddress(), userPort);
    		        try {
    		            socket.send(fetchPacket);
    		        } catch (IOException e) {
    		            throw new RuntimeException(e);
    		        }
    		    } else if (message.endsWith(" has entered the waiting room.")) {
    		        if (!previousChats.contains(message)) {
    		            byte[] enterBytes = message.getBytes();
    		            
    		            // forward the enter message to all other players
    		            for (int forward_port : players) {
    		                if (forward_port != userPort) {
    		                    DatagramPacket enterPacket = new DatagramPacket(enterBytes, enterBytes.length, packet.getAddress(), forward_port);
    		                    try {
    		                        socket.send(enterPacket);
    		                    } catch (IOException e) {
    		                        throw new RuntimeException(e);
    		                    }
    		                }
    		            }
    		            previousChats.add(message); // store the enter message in the list
    		        }
    		    } else if (message.endsWith(" is ready")) {
    		        readyClients++;
    		        byte[] readyBytes = message.getBytes();
    		        
    		        // forward the ready message to all other players
    		        for (int forward_port : players) {
    		            if (forward_port != userPort) {
    		                DatagramPacket readyPacket = new DatagramPacket(readyBytes, readyBytes.length, packet.getAddress(), forward_port);
    		                try {
    		                    socket.send(readyPacket);
    		                } catch (IOException e) {
    		                    throw new RuntimeException(e);
    		                }
    		            }
    		        }
    		        
    		        if (readyClients == players.size()) {
    		            // all clients are ready, send "startGame" message to all clients
    		            byte[] startMessage = "startGame".getBytes();
    		            for (int forward_port : players) {
    		                DatagramPacket startPacket = new DatagramPacket(startMessage, startMessage.length, packet.getAddress(), forward_port);
    		                try {
    		                    socket.send(startPacket);
    		                } catch (IOException e) {
    		                    throw new RuntimeException(e);
    		                }
    		            }
    		        }
    		        
    		        previousChats.add(message); // store the ready message in the list
    		    } else {
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
        		    
        		    previousChats.add(message); // store the regular chat message in the list
    		    }    		    
    		}
    	}
    }
}
