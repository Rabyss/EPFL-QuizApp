package epfl.sweng.events.tests;

import epfl.sweng.events.EventListener;

public class Listener implements EventListener {
	public static void main(String[] args) {
		new Listener().run();
	}
	
	public void run() {
		Calculator calc = new Calculator();
		calc.addListener(this);
		
		calc.add(4, 6);
		calc.divide(33, 22);
		calc.add(1, 2);
	}
	
	public void on(CalculatorEvent.AddedEvent e) {
		System.out.println("Successfully added: "+e.getRestult());
	}
	
	public void on(CalculatorEvent.DividedEvent e) {
		System.out.println("Successfully divided: "+e.getRestult());
	}
	
	public void on(CalculatorEvent.InvalidFormulaEvent e) {
		System.out.println("ERROR: "+e.getMessage());
	}
}
