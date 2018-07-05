package ru.proteiTestTask.server;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.out.println("Enter data base path!");
			System.exit(1);
		}
		
		TCPServer server = new TCPServer(6895, args[0]);
		try {
			server.run();
		} catch (IOException e) {
			System.out.println("Problem with closing socket");
		}
	}

}
