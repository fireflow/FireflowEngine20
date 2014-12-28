package org.fireflow.samples.biz;

public interface ICalculator {
	public float divide(float a,float b) throws CalculatorException;
	
	public float add(float a,float b);
	
	public float multiply(float a,float b);
	
	public float substract(float a,float b);
}
