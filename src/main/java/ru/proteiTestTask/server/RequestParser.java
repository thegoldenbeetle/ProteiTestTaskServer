package ru.proteiTestTask.server;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RequestParser {

	public static HashMap<String, String> parseToHashMap(String request) throws ParseException {
		HashMap<String, String> result = new HashMap<String, String>();

		JSONParser parser = new JSONParser();
		JSONObject requestJSON = (JSONObject) parser.parse(request);

		String command = (String) requestJSON.get("command");
		result.put("command", command);

		if (!command.equals("stop") && !command.equals("find")) {

			result.put("word", (String) requestJSON.get("word"));

			switch (command) {
			case "add":
				result.put("definition", (String) requestJSON.get("definition"));
				break;
			case "change":
				result.put("newWord", (String) requestJSON.get("newWord"));
				break;
			}
		}
		if (command.equals("find")) {
			result.put("mask", (String) requestJSON.get("mask"));
		}
		return result;
	}

	public static String toString(ResultCode code) {
		return putResultCode(code).toString();
	}

	public static String toString(ResultCode code, ArrayList<String> words) {
		JSONObject result = putResultCode(code);
		if (code.getCode().equals(ResultCode.OK)) {
			result.put("words", words);
		}
		return result.toString();
	}

	public static String toString(ResultCode code, String definition) {
		JSONObject result = putResultCode(code);
		if (code.getCode().equals(ResultCode.OK)) {
			result.put("definition", definition);
		}
		return result.toString();
	}

	private static JSONObject putResultCode(ResultCode code) {
		JSONObject json = new JSONObject();
		json.put("ResultCode", code.getCode().toString());
		json.put("answer", caseCode(code));
		return json;
	}

	private static String caseCode(ResultCode code) {
		String result = new String();
		switch (code.getCode()) {
		case OK:
			result = "Success";
			break;
		case WORD_IN_DB_ALREADY:
			result = "Such word is in dictionary already";
			break;
		case NO_SUCH_WORD:
			result = "There is no such word in the dictionary";
			break;
		case EMPTY_INPUT_DATA:
			result = "Some parameters don't contain any symbols";
			break;
		case INVALID_COMMAND:
			result = "There is no such command";
			break;
		case UNDEFINED:
			result = "Undefined error";
			break;
		}
		return result;
	}
}
