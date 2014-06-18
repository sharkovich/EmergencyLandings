package ib.flightplan.exception;

public class NoAirportInRangeException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1130625296761770261L;
	public NoAirportInRangeException() { super("ERROR: Could not find airports in range!"); }
	public NoAirportInRangeException(String message) { super(message); }
	public NoAirportInRangeException(String message, Throwable cause) { super(message, cause); }
	public NoAirportInRangeException(Throwable cause) { super(cause); }
}
