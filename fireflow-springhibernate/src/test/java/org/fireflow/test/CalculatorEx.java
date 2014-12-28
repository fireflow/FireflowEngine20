package org.fireflow.test;

public class CalculatorEx extends Calculator implements ICalculatorEx {


	public int minus(int a, int b) {
		System.out.println("====调用到此处,a="+a+";b="+b);
		return a-b;
	}

	public float divide(float a,float b)throws MyException{
		if (b==0) throw new RuntimeException("除数不能为0");
		if (b<0) throw new MyException("当除数小于0时，抛出该异常");
		return a/b;
	}
}
