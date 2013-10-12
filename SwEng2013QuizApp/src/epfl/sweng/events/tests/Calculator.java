package epfl.sweng.events.tests;

import epfl.sweng.events.EventEmitter;

public class Calculator extends EventEmitter {
	public void add(int a, int b) {
		int result = a+b;
		
		emit(new CalculatorEvent.AddedEvent(result));
	}
	
	public void divide(double a, double b) {
		double result = a/b;
		
		emit(new CalculatorEvent.DividedEvent(result));
	}
}
