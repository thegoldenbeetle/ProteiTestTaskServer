package ru.proteiTestTask.server;

public enum ResultCode {
	
	OK, WORD_IN_DB_ALREADY, UNDEFINED, NO_SUCH_WORD, EMPTY_INPUT_DATA, INVALID_COMMAND;

	private ResultCode code;

	public void setCode(ResultCode code) {
		this.code = code;
	}

	public ResultCode getCode() {
		return this.code;
	}

}