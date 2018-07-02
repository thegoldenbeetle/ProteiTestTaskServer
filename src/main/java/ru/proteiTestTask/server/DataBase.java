package ru.proteiTestTask.server;

import java.sql.Connection;
import java.util.ArrayList;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class DataBase {

	private Connection connection;
	public static Statement statmt;

	public DataBase(String dataBasePath) {

		connection = null;

		try {
			String url = "jdbc:sqlite:" + dataBasePath;

			connection = DriverManager.getConnection(url);
			statmt = connection.createStatement();

			System.out.println("Connection to SQLite has been established.");

		} catch (SQLException e) {
			System.out.println("No such data base " + dataBasePath + "!");
			System.exit(1);
		}

	}

	public String getDefinition(String word, ResultCode resultCode) {
		String definition = new String();
		try {
			if (inDataBase(word)) {
				PreparedStatement st = connection.prepareStatement("SELECT * FROM dictionary WHERE word = (?)");
				st.setString(1, word);
				ResultSet resultSet = st.executeQuery();
				definition = resultSet.getString("definition");
				resultCode.setCode(ResultCode.OK);
			} else {
				resultCode.setCode(ResultCode.NO_SUCH_WORD);
			}
		} catch (SQLException e) {
			resultCode.setCode(ResultCode.UNDEFINED);
			e.printStackTrace();
		}
		return definition;
	}

	public void addWord(String word, String definition, ResultCode resultCode) {
		try {
			if (!inDataBase(word)) {
				PreparedStatement st = connection
						.prepareStatement("INSERT INTO dictionary (word, definition) VALUES (?, ?)");
				st.setString(1, word);
				st.setString(2, definition);
				st.execute();
				resultCode.setCode(ResultCode.OK);
			} else {
				resultCode.setCode(ResultCode.WORD_IN_DB_ALREADY);
			}
		} catch (SQLException e) {
			resultCode.setCode(ResultCode.UNDEFINED);
			e.printStackTrace();
		}
	}

	public void removeWord(String word, ResultCode resultCode) {
		try {
			if (inDataBase(word)) {
				PreparedStatement st = connection.prepareStatement("DELETE FROM dictionary WHERE word = (?)");
				st.setString(1, word);
				st.execute();
				resultCode.setCode(ResultCode.OK);
			} else {
				resultCode.setCode(ResultCode.NO_SUCH_WORD);
			}
		} catch (SQLException e) {
			resultCode.setCode(ResultCode.UNDEFINED);
			e.printStackTrace();
		}
	}

	public ArrayList<String> findWords(String mask, ResultCode resultCode) {
		ArrayList<String> resultWords = new ArrayList<String>();
		try {
			mask = mask.replace('*', '%');
			mask = mask.replace('?', '_');
			PreparedStatement st = connection.prepareStatement("SELECT * FROM dictionary WHERE word LIKE (?)");
			st.setString(1, mask);
			ResultSet resultSet = st.executeQuery();
			String word;
			while (resultSet.next()) {
				word = resultSet.getString("word");
				resultWords.add(word);
			}
			if (resultWords.isEmpty()) {
				resultCode.setCode(ResultCode.NO_SUCH_WORD);
			} else
				resultCode.setCode(ResultCode.OK);

		} catch (SQLException e) {
			resultCode.setCode(ResultCode.UNDEFINED);
			e.printStackTrace();
		}
		return resultWords;
	}

	public void changeWord(String word, String newWord, ResultCode resultCode) {
		try {
			if (inDataBase(word)) {
				if (!inDataBase(newWord)) {
					PreparedStatement st = connection
							.prepareStatement("UPDATE dictionary SET word = (?) WHERE word = (?)");
					st.setString(1, newWord);
					st.setString(2, word);
					st.executeUpdate();
					resultCode.setCode(ResultCode.OK);
				} else {
					resultCode.setCode(ResultCode.WORD_IN_DB_ALREADY);
				}
			} else {
				resultCode.setCode(ResultCode.NO_SUCH_WORD);
			}
		} catch (SQLException e) {
			resultCode.setCode(ResultCode.UNDEFINED);
			e.printStackTrace();
		}
	}

	private boolean inDataBase(String word) {
		try {
			PreparedStatement st = connection.prepareStatement("SELECT * FROM dictionary WHERE word = (?)");
			st.setString(1, word);
			ResultSet resultSet = st.executeQuery();
			if (resultSet.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
