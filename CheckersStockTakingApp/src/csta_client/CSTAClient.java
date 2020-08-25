package csta_client;

import java.io.*;
import java.net.*;
import java.util.*;

public class CSTAClient {
	Socket cstaClientSoc;
	
	String prodName, prodType;
	double prodPrice;
	
	public CSTAClient() throws UnknownHostException, IOException {
		this.cstaClientSoc = new Socket("localhost", 8000);
		
		DataInputStream readWelcomeMsg = new DataInputStream(this.cstaClientSoc.getInputStream());
		System.out.println(readWelcomeMsg.readUTF());

		enterDetails();
	}
	
	public void enterDetails() throws IOException {
		Scanner input = new Scanner(System.in);
		
		do {
			try {
				System.out.println("Enter Product name: ");
				this.prodName = input.nextLine();
				
				if(!this.prodName.equals("stop")) {
					System.out.println("Enter Product Type: ");
					this.prodType = input.nextLine();
					
					System.out.println("Enter product Price: ");
					this.prodPrice = Double.parseDouble(input.nextLine());
					
					sendProductToServer();
				}else {
					this.cstaClientSoc.close();
					System.out.println("Exiting Checkers Stock Taking App, Good Bye");
					System.exit(0);
				}
			}catch(InputMismatchException e) {
				System.out.println("ERROR: " + e.getMessage());
			}
		}while(!this.prodName.equals("stop"));
	}
	
	public void sendProductToServer() throws IOException {
		DataOutputStream writeProdName = new DataOutputStream(this.cstaClientSoc.getOutputStream());
		writeProdName.writeUTF(this.prodName);
		
		DataOutputStream writeProdType = new DataOutputStream(this.cstaClientSoc.getOutputStream());
		writeProdType.writeUTF(this.prodType);
		
		DataOutputStream writeProdPrice = new DataOutputStream(this.cstaClientSoc.getOutputStream());
		writeProdPrice.writeDouble(this.prodPrice);
		
		DataInputStream readConfirmationMsg = new DataInputStream(this.cstaClientSoc.getInputStream());
		System.out.println("Server says: " + readConfirmationMsg.readUTF());
	}
	public static void main(String[] args) throws UnknownHostException, IOException {
		new CSTAClient();
	}
}
