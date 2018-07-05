package ru.proteiTestTask.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class RequestHandler {

	DataBase dataBase;
	ResultCode resultCode;

	public RequestHandler(DataBase dictionary) {
		dataBase = dictionary;
		resultCode = ResultCode.UNDEFINED;
	}

	public String take(String request) {
		String response = new String();

		HashMap<String, String> requestParsed;
		try {
			requestParsed = RequestParser.parseToHashMap(request);
		} catch (ParseException e) {
			return wrongCommand();
		}

		if (!requestParsed.containsKey("command")) {
			return wrongCommand();
		}
		switch (requestParsed.get("command")) {
		case "getDefinition":
			response = getDefinition(requestParsed.get("word"));
			break;
		case "add":
			response = addWord(requestParsed.get("word"), requestParsed.get("definition"));
			break;
		case "remove":
			response = removeWord(requestParsed.get("word"));
			break;
		case "change":
			response = changeWord(requestParsed.get("word"), requestParsed.get("newWord"));
			break;
		case "find":
			response = findWords(requestParsed.get("mask"));
			break;
		default:
			response = wrongCommand();
		}
		return response;
	}

	private String getDefinition(String word) {
		String definition = new String();
		try {
			definition = dataBase.getDefinition(word, resultCode);
		} catch (SQLException e) {
			System.out.println("Can't get definition from data base");
		}
		return RequestParser.toString(resultCode, definition);
	}

	private String addWord(String word, String definition) {
		try {
			dataBase.addWord(word, definition, resultCode);
		} catch (SQLException e) {
			System.out.println("Can't add word to data base");
		}
		return RequestParser.toString(resultCode);
	}

	private String removeWord(String word) {
		try {
			dataBase.removeWord(word, resultCode);
		} catch (SQLException e) {
			System.out.println("Can't remove word from data base");
		}
		return RequestParser.toString(resultCode);
	}

	private String changeWord(String word, String newWord) {
		try {
			dataBase.changeWord(word, newWord, resultCode);
		} catch (SQLException e) {
			System.out.println("Can't change word in data base");
		}
		return RequestParser.toString(resultCode);
	}

	private String findWords(String mask) {
		ArrayList<String> words = new ArrayList<String>();
		try {
			words = dataBase.findWords(mask, resultCode);
		} catch (SQLException e) {
			System.out.println("Can't find word in data base");
		}
		return RequestParser.toString(resultCode, words);
	}

	private String wrongCommand() {
		resultCode.setCode(ResultCode.INVALID_COMMAND);
		return RequestParser.toString(resultCode);
	}

}
