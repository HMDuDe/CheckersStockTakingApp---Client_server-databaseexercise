package csta_server;

import java.io.*;
import java.net.*;
import java.sql.*;

public class CSTAServer {
	Connection conn = this.connectDb();
	ServerSocket cstaServerSoc;
	Socket cstaSoc;
	
	public CSTAServer() throws IOException, ClassNotFoundException, SQLException {
		System.out.println("Server is starting...");
		
		this.cstaServerSoc = new ServerSocket(8000);
		this.cstaSoc = cstaServerSoc.accept();
		
		DataOutputStream writeWelcomeMsg = new DataOutputStream(this.cstaSoc.getOutputStream());
		writeWelcomeMsg.writeUTF("Welcome to Checkers Stock Taking App");
		
		String prodName = "";
		
		do {
			DataInputStream readProdName = new DataInputStream(this.cstaSoc.getInputStream());
			
			try {
				prodName = readProdName.readUTF();
			}catch(SocketException | EOFException e) {
				this.cstaServerSoc.close();
				System.out.println("Server stopping...");
				System.exit(0);
			}
			
			if(prodName != "stop") {
				DataInputStream readProdType = new DataInputStream(this.cstaSoc.getInputStream());
				String prodType = readProdType.readUTF();
				
				DataInputStream readProdPrice = new DataInputStream(this.cstaSoc.getInputStream());
				double prodPrice = readProdPrice.readDouble();
				
				System.out.println("Server received Product: " + prodName + " " + prodType + " R" + prodPrice);
				this.insertProduct(prodName, prodType, prodPrice);
				
			}
		}while(prodName != "stop");
	}
	
	public Connection connectDb() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection hughysConn = DriverManager.getConnection("jdbc:mysql://localhost/checkersproducts", "root", "Hm@dude21");
		
		return hughysConn;
	}
	
	public void insertProduct(String prodName, String prodType, double price) throws ClassNotFoundException, SQLException, IOException {
		System.out.println("Connected to database " + this.conn.getCatalog());
		PreparedStatement ps = conn.prepareStatement("INSERT INTO `products` (`prodName`, `prodType`, `prodPrice`) VALUES (?,?,?)");
		
		ps.setString(1, prodName);
		ps.setString(2, prodType);
		ps.setDouble(3, price);
		
		ps.execute();
		
		System.out.println("Product has been added to the database");
		DataOutputStream writeConfirmationMsg = new DataOutputStream(this.cstaSoc.getOutputStream());
		writeConfirmationMsg.writeUTF("Product has been added");
		
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		new CSTAServer();
	}
}
