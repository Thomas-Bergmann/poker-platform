package de.hatoka.poker;

public class StateException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5988683745389056500L;
	String message = null;
	public StateException(String string) {
		message = string;
	}
	
	public String toString () {
		return message.concat("\n").concat(super.toString());
	}
}
