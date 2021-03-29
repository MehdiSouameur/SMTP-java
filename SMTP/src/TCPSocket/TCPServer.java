/**
 * 
 */
package TCPSocket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author user
 *
 */
public class TCPServer{
    private ServerSocket server;
    private String hostname; //hostname of the server
    private Socket client; 
    private String sender; // Sender's email
    private String[] mail; //String array containing the message content
    private int mailCounter = 0; //Mail counter to handle DATA comamand
    private String recipient; // Recipient's email.
    private boolean inData = false; //InData boolean to handle DATA command
   
    
    /**
     * The TCPServer constructor 
     * @param ipAddress
     * @param port
     * @throws Exception
     */
    public TCPServer(String ipAddress, int port) throws Exception {
		//add your code here
    	if(ipAddress != null && !ipAddress.isEmpty())
    		this.server = new ServerSocket(port, 1, InetAddress.getByName(ipAddress));
    	else
    		this.server = new ServerSocket(0, 1, InetAddress.getLocalHost());
    	this.mail = new String[50];
    	this.hostname = "gov.uk";
    }
    
    /** method that replies to the client based on the client's message
     * @param String message - message from the client
     * @throws Exception
     */
    private void send(String message) throws Exception {
        
        PrintWriter output = new PrintWriter(this.client.getOutputStream(), true); 
		
        //Split the received message to get the command
		String[] tokens = message.split(":");
		
		// This handles all the different commands
		switch(tokens[0]) {
		   
		//Command for the sender's email
		   case("MAIL FROM"):
			  System.out.print(tokens[0] + ": ");
			  this.sender = tokens[1];
		      System.out.println(this.sender);
		      output.println("250 ok");
		      break;
		
		//Command for the recipient's email
		   case("RCPT TO"):
			   System.out.print(tokens[0] + ": ");
			   this.recipient = tokens[1];
		       System.out.println(this.recipient);
		       output.println("250 ok");
		       break;
		
		//The message content of the email
		   case("DATA"):
			   //if this is the first time, send a reply.
			   if(this.mailCounter == 0) {
				   output.println("354 End data with \".\"");
				   this.mailCounter = this.mailCounter + 1;
				   this.inData = true;
				   System.out.println("<Receiving Data>");
			       break;
			   }
		       System.out.print(tokens[0] + ":");
		       //if message is ".", stop the command and send a reply
		       if(tokens[1].equals(" .")) {
			      output.println("250 ok Message accepted for delivery");
			      System.out.println(".");
			      this.inData = false;
			      break;
		       }
			   this.mail[mailCounter-1] = tokens[1];
			   System.out.println(this.mail[this.mailCounter-1]);
			   this.mailCounter = this.mailCounter + 1;
		       break;
		
		 //Client establishing connection with a hello       
		   case("HELLO"):
			   System.out.print(tokens[0] + " ");
			   System.out.println(this.hostname);
			   output.println("250 HELLO " + tokens[1] + ", pleased to meet you");
		       break;
		 
		 //Terminating the connection with quit
		   case("QUIT"):
			   System.out.print(tokens[0] + ": ");
			   output.println("221 " + this.hostname + " closing connection");
		       break;
		       
		 //First connection message
		   case("CONNECTION"):
			   output.println("220 " + this.hostname);
		       break;
		       
		   default:
			   System.out.println("error");
		}
    }
    
    /**
     * Listens to incoming messages from clients
     * @throws Exception
     */
    private void listen() throws Exception {
    	String data = null;
    	String clientAddress = client.getInetAddress().getHostAddress();
    	System.out.println("\r\nNew client connection from " + clientAddress);

        // handle received datagrams and pass them to the send() method
    	BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    	while ((data = in.readLine()) != null) {
    		//If we're currently inside a data command, handle data command
    		if(inData == true) {
    			//stop data command if the message is "."
    			if(data.equals(".")) {
    				send("DATA: " + ".");
    				this.inData = false;
    				continue;
    			}
    			send("DATA: " + data);
    			continue;
    		}
    		
    		send(data);
    		
    		
    		
        }
    	in.close();
    }
    
    /** Starts up the server, calls the listen command and 
     * writes text to the file.
     * 
     * @throws exception
     */
    private void start() throws Exception {
    	//A
    	this.client = this.server.accept();
    	send("CONNECTION:");
    	listen();
    	server.close();
    	System.out.println("Connection terminated");
    	System.out.println("\nWriting text to mail.txt...");
    	File email = new File("C:\\Users\\souam\\eclipse-workspace\\TCP-Protocol\\mail.txt");
    	WriteToFile();
    	System.out.println("Mail written to file!");
    	
    }
    
    public InetAddress getSocketAddress() {
        return this.server.getInetAddress();
    }
    
    public int getPort() {
        return this.server.getLocalPort();
    }
    
    /*Method which writes to a file
     * 
     */
    public void WriteToFile() {
    	try {
		      FileWriter myWriter = new FileWriter("C:\\\\Users\\\\souam\\\\eclipse-workspace\\\\TCP-Protocol\\\\email.txt");
		      myWriter.write("Sender: " + this.sender + "\n");
		      myWriter.write("Recipient: " + this.recipient+ "\n");
		      int i = 1;
		      myWriter.write("Message:\n");
		      while(this.mail[i] != null) {
		          myWriter.write(this.mail[i-1] + "\n");
		    	  i++;
		      }
		      myWriter.close();
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
    }
    
      
    public static void main(String[] args) throws Exception {
    	// set the server address (IP) and port number
    	String serverIP = "192.168.56.1"; // local IP address
    	int port = 25;
    	
		if (args.length > 0) {
			serverIP = args[0];
			port = Integer.parseInt(args[1]);
		}
		// call the constructor and pass the IP and port
		TCPServer server = new TCPServer(serverIP, port);
		System.out.println("\r\nRunning Server: " + "Host=" + server.getSocketAddress().getHostAddress() + " Port=" + server.getPort());
		server.start();
		
    }
}

