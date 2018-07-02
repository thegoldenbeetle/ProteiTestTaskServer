package ru.proteiTestTask.server;
public class Main {

	public static void main(String[] args) {
		TCPServer server = new TCPServer(6895, args[0]);
		server.run();
	}

}
