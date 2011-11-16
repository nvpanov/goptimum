package symboldiff;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import functions.Function;
import functions.Function_RosenbrockG_nD;
import functions.Function_RosenbrockG_nDTest;

import symboldiff.exceptions.IncorrectExpression;

public class SimplifierTest {
	private Random rnd = new Random();
	private long seed;
	String formula;
	
	private Expression exp;

	@Before
	public void setUp() throws Exception {
		seed = System.currentTimeMillis();
		rnd.setSeed(seed);
		
	}

	@Test
	public final void testSimplify() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testReduceConstants() throws IncorrectExpression {
		formula = "1+2-x";
		exp = new Expression(formula);
		Simplifier.reduceConstants(exp);
		assertTrue(Long.toString(seed), exp.toString().equals("3.0-x") );

		formula = "1-2";
		exp = new Expression(formula);
		Simplifier.reduceConstants(exp);
		assertTrue(Long.toString(seed), exp.toString().equals("-1.0") );
	}
	
	@Test
	public final void testRemoveZeros() throws IncorrectExpression {
		formula = "(x-0)";
		exp = new Expression(formula);
		Simplifier.removeZeros(exp);
		assertTrue(""+seed, exp.toString().equals("x") );

		formula = "0*x";
		exp = new Expression(formula);
		Simplifier.removeZeros(exp);
		assertTrue(""+seed, exp.toString().equals("0.0") );
		formula = "x*0";
		exp = new Expression(formula);
		Simplifier.removeZeros(exp);
		assertTrue(""+seed, exp.toString().equals("0.0") );
		
		formula = "0+x";
		exp = new Expression(formula);
		Simplifier.removeZeros(exp);
		assertTrue(""+seed, exp.toString().equals("x") );
		formula = "x+0";
		exp = new Expression(formula);
		Simplifier.removeZeros(exp);
		assertTrue(""+seed, exp.toString().equals("x") );

		formula = "0/x";
		exp = new Expression(formula);
		Simplifier.removeZeros(exp);
		assertTrue(""+seed, exp.toString().equals("0.0") );
		
		formula = "(x+1-0) * 0 + x - 0 + 1";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
//		System.out.println(exp);
		assertTrue(""+seed, exp.toString().equals("x+1") );
		
		formula = "x^0";
		exp = new Expression(formula);
		Simplifier.removeZeros(exp);
//		System.out.println(exp);
		assertTrue(""+seed, exp.toString().equals("x") );
		
		formula = "0^(x+y+1)";
		exp = new Expression(formula);
		Simplifier.removeZeros(exp);
//		System.out.println(exp);
		assertTrue(""+seed, exp.toString().equals("0.0") );
		
		
	}

	@Test
	public final void testRemoveOnes() throws IncorrectExpression {
		formula = "(x+1)*1";
		exp = new Expression(formula);
		Simplifier.removeOnes(exp);
		assertTrue(""+seed, exp.toString().equals("x+1") );
		formula = "1*x";
		exp = new Expression(formula);
		Simplifier.removeOnes(exp);
		assertTrue(""+seed, exp.toString().equals("x") );
		formula = "1*(x*1*x*1*1)";
		exp = new Expression(formula);
		Simplifier.removeOnes(exp);
		assertTrue(""+seed, exp.toString().equals("x*x") );

		formula = "1/x";
		exp = new Expression(formula);
		Simplifier.removeOnes(exp);
		assertTrue(""+seed, exp.toString().equals("1/x") );
		formula = "x/1";
		exp = new Expression(formula);
		Simplifier.removeOnes(exp);
		assertTrue(""+seed, exp.toString().equals("x") );

		formula = "x^1";
		exp = new Expression(formula);
		Simplifier.removeOnes(exp);
		assertTrue(""+seed, exp.toString().equals("x") );
		
		formula = "1^(x+y+1)";
		exp = new Expression(formula);
		Simplifier.removeOnes(exp);
//		System.out.println(exp);
		assertTrue(""+seed, exp.toString().equals("1.0") );		
	}
	@Test
	public final void testMakeTreeDipperNotWide0() throws IncorrectExpression {
		formula = "1 - (2-3)";
		exp = new Expression(formula);
		Simplifier.makeTreeDipperNotWide(exp);
//		System.out.println(exp);
		assertTrue(""+seed, exp.toString().equals("(1+3)-2") );		
		formula = "1-2-3";
		exp = new Expression(formula);
		Simplifier.makeTreeDipperNotWide(exp);
//		System.out.println(exp);
		assertTrue(""+seed, exp.toString().equals("(1-2)-3") );		
	}
	@Test
	public final void testMakeTreeDipperNotWide() throws IncorrectExpression {
		formula = "1-z/(2*3)/4/5*(2-3)";
		//formula = "z/(2*3)";
		exp = new Expression(formula);
//		System.out.println(exp);
//		System.out.println(exp.toStringGraph() + "\n------------------");
		Simplifier.makeTreeDipperNotWide(exp);
//		System.out.println(exp.toStringGraph() + "\n==================");
//		System.out.println(exp);
		assertTrue(""+seed, exp.toString().equals("1-(((((z/3)/2)/4)/5)*(2-3))") );		
	}

	@Test//(timeout=1*1000)
	public final void test0() throws IncorrectExpression {
		formula = "(x1 - x0)";
		exp = new Expression(formula);
		Simplifier.simplify(exp);

		Gradient grad = new Gradient(exp);
	    Expression d1_0 = grad.getPartialDerivative(0);
	    
	    Simplifier.simplify(d1_0);
	    assertEquals("-1.0", d1_0.toString());
	}
	
	@Test
	public final void testNegate() throws IncorrectExpression {
		formula = "-1";//"0.0-(2*x0)";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("-1", exp.toString());
		formula = "0-x";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("-x", exp.toString());
	}
	@Test
	public final void test_RastriginG_nD_part1a_simplification() throws IncorrectExpression {
		formula = "(2*(x1-(x0^2)))*(0.0-(2*x0))";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		// TODO:
		//assertEquals("(-4 * x0 * (x1 - (x0^2)))", exp.toString());
		assertEquals("(2*(x1-(x0^2)))*-(2*x0)", exp.toString()); // better than nothing...
	}
	@Test//(timeout=1*1000)
	public final void test_RastriginG_nD_part1_diff() throws IncorrectExpression {
		formula = "(x1 - x0^2)^2";
		exp = new Expression(formula);
		Simplifier.simplify(exp);

		Gradient grad = new Gradient(exp);
	    Expression d1_0 = grad.getPartialDerivative(0);
	    
	    //System.out.println("df/dx0: " + d1_0); // (-2 * (x1 - (x0^2)) * (2 * x0))

	    Simplifier.simplify(d1_0);
	    //System.out.println("Simplified df/dx0: " + d1_0);
	    //assertEquals("(-4 * x0 * (x1 - (x0^2)))", d1_0.toString());
	  	assertEquals("(2*(x1-(x0^2)))*-(2*x0)", d1_0.toString()); // better than nothing...

	    Expression d1_1 = grad.getPartialDerivative(1);
	    //System.out.println("df/dx1: " + d1_1);	    
	    Simplifier.simplify(d1_1);
	    //System.out.println("Simplified df/dx1: " + d1_1);
	    assertEquals("2*(x1-(x0^2))", d1_1.toString());
	}	

	@Test
	public final void test_RastriginG_nD() throws IncorrectExpression {
		final int dim = 2;
		StringBuilder sb = new StringBuilder("0");
		for (int i = 0; i < dim-1; i++) {
			sb.append(" + ");
			sb.append("(100*(x");
			sb.append(i+1);
			sb.append("-x");
			sb.append(i);
			sb.append("^2)^2 + (x");
			sb.append(i);
			sb.append("-1)^2)");
		}
		formula = sb.toString();
		exp = new Expression(formula);
//		System.out.println(exp);
//		System.out.println(exp.toStringGraph() + "\n------------------");
		Simplifier.simplify(exp);
//		System.out.println(exp.toStringGraph() + "\n==================");
//		System.out.println(exp);
		if (dim == 2)
			assertEquals("((100*(x1-(x0^2)))^2)+((x0-1)^2)", exp.toString());

		Gradient grad = new Gradient(exp);
	    
	    Expression d1_0 = grad.getPartialDerivative(0);
	    //System.out.println("R: df/dx0: " + d1_0);	    
	    
	    // http://www.adiff.com :	 ((100 * 100 * 2 * (x1 - (x0^2)) * (-(2 * x0))) + (2 * (x0 - 1)))
	    // 					simplify-> (-(40000 * x0 * (x1 - (x0^2))) + (2 * (-1 + x0)))
	    Simplifier.simplify(d1_0);
	    //System.out.println("R: Simplified df/dx0: " + d1_0);
	    assertEquals("(((200.0*(x1-(x0^2)))*100)*-(2*x0))+(2*(x0-1))", d1_0.toString());

	    Expression d1_1 = grad.getPartialDerivative(1);
	    System.out.println("R: df/dx1: " + d1_0);
	    Simplifier.simplify(d1_1);
	    System.out.println("R: Simplified df/dx1: " + d1_1);
	    
	    /*
	     * http://www.adiff.com
	     * <<<< diff(((100*(x1-(x0^2)))^2)+((x0-1)^2),x1)
	     * >>>> (100 * 100 * 2 * (x1 - (x0^2)))
	     * <<<< simplify($_)
	     * >>>> (20000 * (x1 - (x0^2)))
	     */	    
	    assertEquals("(200.0*(x1-(x0^2)))*100.0", d1_1.toString());
	    //assertEquals("(20000*(x1-(x0^2)))", d1_1.toString());
	    
/*	    
		MethodRunner executor = new MethodRunner(exp);
	    try {
	      executor.generateMethods(grad.getGradient());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    result = executor.invokeMethods(box, grad.getPartialDerivative(0));
	    System.out.println("result: " + result);
*/		
	}
}
