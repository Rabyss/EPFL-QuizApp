package epfl.sweng.events.tests;

import epfl.sweng.events.Event;

@SuppressWarnings("serial")
public abstract class CalculatorEvent extends Event {
	
	public static class AddedEvent extends CalculatorEvent {
		private int result;
		
		public AddedEvent(int result) {
			this.result = result;
		}
		
		public int getRestult() {
			return result;
		}
	}
	
	public static class DividedEvent extends CalculatorEvent {
		private double result;
		
		public DividedEvent(double result) {
			this.result = result;
		}
		
		public double getRestult() {
			return result;
		}
	}
	
	public static class InvalidFormulaEvent extends CalculatorEvent {
		private String message;
		private Throwable exception;
		
		public InvalidFormulaEvent(String message, Throwable exception) {
			this.message = message;
			this.exception = exception;
		}
		
		public String getMessage() {
			return this.message;
		}
		
		public Throwable getException() {
			return this.exception;
		}
	}
}
