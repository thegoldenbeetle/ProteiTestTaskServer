package ru.proteiTestTask.server;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {

	private Connection connection;
	public static Statement statmt;

	public DataBase(String dataBasePath) {

		connection = null;

		try {
			String url = "jdbc:sqlite:" + dataBasePath;

			connection = DriverManager.getConnection(url);
			statmt = connection.createStatement();
			PreparedStatement st = connection.prepareStatement("SELECT * FROM dictionary");
			st.execute();

			System.out.println("Connection to SQLite has been established.");

		} catch (SQLException e) {
			System.out.println("No such dictionary " + dataBasePath + "!");
			System.exit(1);
		}

	}


	public String getDefinition(String word, ResultCode resultCode) throws SQLException {
		String definition = new String();
		if (word.isEmpty()) {
			resultCode.setCode(ResultCode.EMPTY_INPUT_DATA);
			return definition;
		}
		if (!inDataBase(word)) {
			resultCode.setCode(ResultCode.NO_SUCH_WORD);
			return definition;
		}
		PreparedStatement st = connection.prepareStatement("SELECT * FROM dictionary WHERE word = (?)");
		st.setString(1, word);
		ResultSet resultSet = st.executeQuery();
		definition = resultSet.getString("definition");
		resultCode.setCode(ResultCode.OK);
		return definition;
	}

	public void addWord(String word, String definition, ResultCode resultCode) throws SQLException {
		if (word.isEmpty() || definition.isEmpty()) {
			resultCode.setCode(ResultCode.EMPTY_INPUT_DATA);
			return;
		}
		if (inDataBase(word)) {
			resultCode.setCode(ResultCode.WORD_IN_DB_ALREADY);
			return;
		}
		PreparedStatement st = connection.prepareStatement("INSERT INTO dictionary (word, definition) VALUES (?, ?)");
		st.setString(1, word);
		st.setString(2, definition);
		st.execute();
		resultCode.setCode(ResultCode.OK);
	}

	public void removeWord(String word, ResultCode resultCode) throws SQLException {
		if (word.isEmpty()) {
			resultCode.setCode(ResultCode.EMPTY_INPUT_DATA);
			return;
		}
		if (!inDataBase(word)) {
			resultCode.setCode(ResultCode.NO_SUCH_WORD);
			return;
		}
		PreparedStatement st = connection.prepareStatement("DELETE FROM dictionary WHERE word = (?)");
		st.setString(1, word);
		st.execute();
		resultCode.setCode(ResultCode.OK);
	}

	private String correctMask(String mask) {
		String correctedMask = new String();
		correctedMask = mask.replace('*', '%');
		correctedMask = correctedMask.replace('?', '_');
		return correctedMask;
	}

	private ArrayList<String> getFindedWords(ResultSet resultSet) throws SQLException {
		ArrayList<String> resultWords = new ArrayList<String>();
		String word;
		while (resultSet.next()) {
			word = resultSet.getString("word");
			resultWords.add(word);
		}
		return resultWords;
	}

	public ArrayList<String> findWords(String mask, ResultCode resultCode) throws SQLException {
		if (mask.isEmpty()) {
			resultCode.setCode(ResultCode.EMPTY_INPUT_DATA);
			return null;
		}
		mask = correctMask(mask);
		PreparedStatement st = connection.prepareStatement("SELECT * FROM dictionary WHERE word LIKE (?)");
		st.setString(1, mask);
		ResultSet resultSet = st.executeQuery();
		ArrayList<String> resultWords = getFindedWords(resultSet);
		if (resultWords.isEmpty()) {
			resultCode.setCode(ResultCode.NO_SUCH_WORD);
		} else
			resultCode.setCode(ResultCode.OK);
		return resultWords;
	}

	public void changeWord(String word, String newWord, ResultCode resultCode) throws SQLException {
		if (word.isEmpty() || newWord.isEmpty()) {
			resultCode.setCode(ResultCode.EMPTY_INPUT_DATA);
			return;
		}
		if (!inDataBase(word)) {
			resultCode.setCode(ResultCode.NO_SUCH_WORD);
			return;
		}
		if (inDataBase(newWord)) {
			resultCode.setCode(ResultCode.WORD_IN_DB_ALREADY);
			return;
		}
		PreparedStatement st = connection.prepareStatement("UPDATE dictionary SET word = (?) WHERE word = (?)");
		st.setString(1, newWord);
		st.setString(2, word);
		st.executeUpdate();
		resultCode.setCode(ResultCode.OK);
	}

	private boolean inDataBase(String word) throws SQLException {
		PreparedStatement st = connection.prepareStatement("SELECT * FROM dictionary WHERE word = (?)");
		st.setString(1, word);
		ResultSet resultSet = st.executeQuery();
		return resultSet.next();
	}

}
