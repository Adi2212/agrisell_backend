package com.agrisell.exception;

public class UserNotFound extends RuntimeException {
	public UserNotFound(String mesg) {
		super(mesg);
	}

}
