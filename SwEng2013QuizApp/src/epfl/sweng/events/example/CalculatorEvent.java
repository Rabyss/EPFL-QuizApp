package epfl.sweng.events.example;

import epfl.sweng.events.Event;

public abstract class CalculatorEvent extends Event {
	private static final long serialVersionUID = -4248844530909515245L;

	public static class AddedEvent extends CalculatorEvent {
		private static final long serialVersionUID = -7343344567097493724L;
		private int result;
		
		public AddedEvent(int result) {
			this.result = result;
		}
		
		public int getRestult() {
			return result;
		}
	}
	
	public static class DividedEvent extends CalculatorEvent {
		private static final long serialVersionUID = 5668490883849082077L;
		private double result;
		
		public DividedEvent(double result) {
			this.result = result;
		}
		
		public double getRestult() {
			return result;
		}
	}
	
	public static class InvalidFormulaEvent extends CalculatorEvent {
		private static final long serialVersionUID = 2456283018671223679L;
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
