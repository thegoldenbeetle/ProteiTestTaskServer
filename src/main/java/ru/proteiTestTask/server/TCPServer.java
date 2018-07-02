package ru.proteiTestTask.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TCPServer {

	ServerSocket socket;
	DataBase dictionary;

	public TCPServer(int port, String pathToDataBase) {

		try {
			socket = new ServerSocket(port);
			dictionary = new DataBase(pathToDataBase);
		} catch (IOException e) {
			System.out.println("Wrong port! " + port);
		}

	}

	private JSONObject getDefinition(String word) {
		JSONObject response = new JSONObject();
		ResultCode resultCode = ResultCode.UNDEFINED;
		String definition;
		if (!word.isEmpty()) {
			definition = dictionary.getDefinition(word, resultCode);
			response.put("ResultCode", (resultCode.getCode()).toString());
			if (resultCode.getCode().equals(ResultCode.OK)) {
				response.put("definition", definition);
			}
		} else {
			response.put("ResultCode", (ResultCode.EMPTY_INPUT_DATA).toString());
		}
		return response;
	}

	private JSONObject addWord(String word, String definition) {
		JSONObject response = new JSONObject();
		ResultCode resultCode = ResultCode.UNDEFINED;
		if (!word.isEmpty() && !definition.isEmpty()) {
			dictionary.addWord(word, definition, resultCode);
			response.put("ResultCode", (resultCode.getCode()).toString());
		} else {
			response.put("ResultCode", (ResultCode.EMPTY_INPUT_DATA).toString());
		}
		return response;
	}

	private JSONObject removeWord(String word) {
		JSONObject response = new JSONObject();
		ResultCode resultCode = ResultCode.UNDEFINED;
		if (!word.isEmpty()) {
			dictionary.removeWord(word, resultCode);
			response.put("ResultCode", (resultCode.getCode()).toString());
		} else {
			response.put("ResultCode", (ResultCode.EMPTY_INPUT_DATA).toString());
		}
		return response;
	}

	private JSONObject changeWord(String word, String newWord) {
		JSONObject response = new JSONObject();
		ResultCode resultCode = ResultCode.UNDEFINED;
		if (!word.isEmpty() && !newWord.isEmpty()) {
			dictionary.changeWord(word, newWord, resultCode);
			response.put("ResultCode", (resultCode.getCode()).toString());
		} else {
			response.put("ResultCode", (ResultCode.EMPTY_INPUT_DATA).toString());
		}
		return response;
	}

	private JSONObject findWords(String mask) {
		JSONObject response = new JSONObject();
		ResultCode resultCode = ResultCode.UNDEFINED;
		if (!mask.isEmpty()) {
			ArrayList<String> words = dictionary.findWords(mask, resultCode);
			response.put("ResultCode", (resultCode.getCode()).toString());
			if (resultCode.getCode().equals(ResultCode.OK)) {
				response.put("words", words.toString());
			}
		} else {
			response.put("ResultCode", (ResultCode.EMPTY_INPUT_DATA).toString());
		}
		return response;
	}

	public void run() {

		System.out.println("Server is running");

		while (true) {

			Socket connectionSocket;

			try {

				connectionSocket = socket.accept();
				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(connectionSocket.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

				JSONParser parser = new JSONParser();
				JSONObject request = (JSONObject) parser.parse(inFromClient.readLine());
				String command = (String) request.get("command");

				JSONObject response = new JSONObject();

				switch (command) {
				case "getDefinition":
					response = getDefinition((String) request.get("word"));
					break;
				case "add":
					response = addWord((String) request.get("word"), (String) request.get("definition"));
					break;
				case "remove":
					response = removeWord((String) request.get("word"));
					break;
				case "change":
					response = changeWord((String) request.get("word"), (String) request.get("newWord"));
					break;
				case "find":
					response = findWords((String) request.get("mask"));
					break;
				case "stop":
					return;
				default:
					response = new JSONObject();
					response.put("ResultCode", (ResultCode.INVALID_COMMAND).toString());
				}

				outToClient.writeBytes(response.toString() + "\n");

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
}
