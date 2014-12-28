package org.fireflow.samples.biz;

public class Calculator implements ICalculator {

	public float divide(float a, float b) throws CalculatorException {
		if (b==0){
			
			throw new CalculatorException();
		}
		return a/b;
	}

	public float add(float a, float b) {
		return a+b;
	}

	public float multiply(float a, float b) {
		return a*b;
	}

	public float substract(float a, float b) {
		return a-b;
	}

}
