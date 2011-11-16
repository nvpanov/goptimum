package symboldiff;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import symboldiff.exceptions.IncorrectExpression;

import core.Box;
import functions.Function;
import functions.Function_DeJong_nD;
import net.sourceforge.interval.ia_math.RealInterval;

public class Main {
	Random rnd = new Random();

	@Test
	public void t2() throws IncorrectExpression {
		Expression e = new Expression("x");
		assertEquals("x", e.toString());
	}
	
	@Test
	public void t1() throws IncorrectExpression {
	  RPN rpn;
	  Expression expr;
	  Gradient grad;
	  MethodRunner executor;
	  RealInterval result;
	  Box box;
	  final int dim = 4;
	  RealInterval interval = new RealInterval(-1, 1);
	    box = new Box(dim, interval);
    	for (int i = 0; i < dim; i++)
    		box.setInterval(rnd.nextInt(dim), new RealInterval(rnd.nextInt(6) - rnd.nextInt(3)));
	    
	    //rpn = new RPN("arcctg(x_1+1)*sqrt(x_3)-asdasdasdasdasd + x_4");
	    rpn = new RPN("x1*x1+x2*x2+x3*x3*x2");
	    expr = new Expression(rpn);
	    grad = new Gradient(expr);
	    executor = new MethodRunner();
	    System.out.println("rpn: " + rpn.toString());	
	    System.out.println("expr: " + expr.toString());	    
	    System.out.println("gradient: " + grad.getGradient().toString());
	    try {
	      executor.generateMethods(grad.getGradient());
	      System.out.println(grad.getPartialDerivative(0));
	      result = executor.invokeMethods(box, grad.getPartialDerivative(0));
	      System.out.println("result: " + result);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@Test
	public void testDeJongdiff() throws IncorrectExpression {
		Function deJong = new Function_DeJong_nD(2);
		String df_dx1 = deJong.getGradient().toString();
		//System.out.println(df_dx1);
		assertEquals("(2*x0)+(2*x1)", df_dx1);
		
		Box b = new Box(2, new RealInterval(1.2640918036031814) );
		RealInterval df_dx1_val = deJong.calc1Derivative(b, 1);
		assertTrue(df_dx1_val.wid() < 1e-4);
		assertTrue(Math.abs(df_dx1_val.hi() - b.getInterval(1).hi()*2) < 1e-4);
		
		b.setInterval(0, new RealInterval(2.374262669340239E-6));
		df_dx1_val = deJong.calc1Derivative(b, 1);
		assertTrue(df_dx1_val.wid() < 1e-4);
		assertTrue(Math.abs(df_dx1_val.hi() - b.getInterval(1).hi()*2) < 1e-4);
	}	


}
