package TCPSocket;
import java.net.*;
import java.util.Scanner;

import java.io.*;

/**
 * This program demonstrates a SMTP client.
 *
 * @author Petros Andreou
 */

public class TCPClient {
		private Socket tcpSocket;
	    private InetAddress serverAddress;
	    private int serverPort;
	    private Scanner scanner;
	    private String sender = "mehdi@reading.ac.uk"; //Sender's email
	    private String recipient = "loan@gov.co.uk";//Recipient's email
	    private String message = "";//Message content
	    private String hostname;//Hostname of the client
	   
	
	    /**TCPClient constructer
	     * @param serverAddress
	     * @param serverPort
	     * @throws Exception
	     */
	    private TCPClient(InetAddress serverAddress, int serverPort) throws Exception {     
	    	this.serverAddress = serverAddress;
	    	this.serverPort = serverPort;
	        //Initiate the connection with the server using Socket. 
	        //For this, creates a stream socket and connects it to the specified port number at the specified IP address. 
	    	this.tcpSocket = new Socket(this.serverAddress, this.serverPort);
	        this.scanner = new Scanner(System.in);
	        this.hostname = "reading.ac.uk";

	    }
	    
	    /** Send a command to the server
	     * @throws Exception
	     */
	    private void send() throws Exception{
	    	String input;
	    	
	    	//Create a PrintWriter which outputs to serber
	    	PrintWriter output = new PrintWriter(this.tcpSocket.getOutputStream(), true);
	    	
	    	//Send the hello command with the hostname of client
	    	System.out.println("Sending HELLO to server...");
	    	output.println("HELLO:" + this.hostname);
	    	listen();
	    	
	    	//Send the MAIL FROM command with the sender's email
	    	System.out.println("Sending MAIL FROM to server...");
	    	output.println("MAIL FROM:" + this.sender); 
	    	listen();
	    	
	    	//Send the RCPT TO command with the recipient's email
	    	System.out.println("Sending RCPT TO to server...");
	    	output.println("RCPT TO:" + this.recipient); //send recipient's email to the server
	    	listen();
	    	
	    	//Ask permission to enter the DATA command
	    	System.out.println("Asking to send DATA to server...");
	    	output.println("DATA:");
	        listen();
	    	 
	        //Get the message from the user until the user inputs "."
	    	input = scanner.nextLine(); 
	    	while(!input.equals(".")) { //if it does not contain a '.' character then go to the next line
	    		this.message += input + "\n"; //keep adding every line to the message
	    	    input = scanner.nextLine();
	        }
	    	
	    	//Send the message to the server
	    	System.out.println("Sending DATA to server...");
	    	output.println(this.message);
	    	output.println(".");
	    	listen();
	    	
	    	//Send the QUIT command to terminate the connection
	    	System.out.println("Quitting...");
	    	output.println("QUIT:");
	    	listen();
	    	
	       output.flush(); //flush the writer 
   		   output.close();
	    }
	    
	    /** Listens and prints the server's replies to the terminal
	     * @throws Exception
	     */
	    private void listen() throws Exception{
	    	String data = null;
	    	//initialise buffer input stream from the server
	    	BufferedReader in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
	    	if((data = in.readLine()) != null){
		    	System.out.println("Server: " + data);
	    	}
	    }
	    
	    /**
	     * The start method connect to the server and datagrams 
	     * @throws Exception 
	     */
	    private void start() throws Exception {
	    	listen();
	    	send();
	    	//listen();
	    	this.tcpSocket.close();
	    }
	    
	  public static void main(String[] args) throws Exception {
		 // set the server address (IP) and port number
		 InetAddress serverIP = InetAddress.getByName("192.168.56.1"); //local IP address
		 int port = 25;
				
		 if (args.length > 0) {
				serverIP = InetAddress.getByName(args[0]);
				port = Integer.parseInt(args[1]);
		 }
			// call the constructor and pass the IP and port
			//add your code here		
			TCPClient client = new TCPClient(serverIP, port);
			System.out.println("\r\nConnected to Server: " + client.tcpSocket.getInetAddress()+ "\n");
			
			client.start();
	    }
}


