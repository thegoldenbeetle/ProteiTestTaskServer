package ru.proteiTestTask.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TCPServer {

	ServerSocket socket;
	RequestHandler requestHandler;
	DataBase dictionary;

	public TCPServer(int port, String pathToDataBase) {

		try {
			socket = new ServerSocket(port);
			dictionary = new DataBase(pathToDataBase);
			requestHandler = new RequestHandler(dictionary);
		} catch (IOException e) {
			System.out.println("Wrong port! " + port);
		}

	}

	public void run() throws IOException {

		System.out.println("Server is running");

		while (true) {
			Socket connectionSocket = socket.accept();
			try {
				while (!connectionSocket.isClosed()) {
					BufferedReader inFromClient = new BufferedReader(
							new InputStreamReader(connectionSocket.getInputStream()));
					DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
					String request = inFromClient.readLine();
					String response = requestHandler.take(request);

					outToClient.writeBytes(response + "\n");
				}
			} catch (Exception e) {
				connectionSocket.close();
				e.getStackTrace();
			}
		}
	}
}
