package symboldiff;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import symboldiff.Gradient;
import symboldiff.Simplifier.ExpAndOp;
import symboldiff.exceptions.ExpressionException;

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
	
	@Test//(timeout=1*1000) 
	public final void testRemoveNegateForConsts() throws Exception {
		formula = "-(2*2)";
		exp = new Expression(formula);
		Simplifier.removeNegateForConsts(exp);
		assertEquals("-2*2", exp.toString() );
		Simplifier.simplify(exp);
		assertEquals("-4", exp.toString() );
		formula = "-(2*x)";
		exp = new Expression(formula);
		Simplifier.removeNegateForConsts(exp);
		assertEquals("-2*x", exp.toString() );
		Simplifier.simplify(exp);
		assertEquals("-2*x", exp.toString() );		
		
		formula = "-(x*x)";
		exp = new Expression(formula);
		Simplifier.removeNegateForConsts(exp);
		assertEquals("-x*x", exp.toString() );
		Simplifier.simplify(exp);
		assertEquals("-x^2", exp.toString() );
		/////////////////////////////////////////
		formula = "-(2+2)";
		exp = new Expression(formula);
		Simplifier.removeNegateForConsts(exp);
		assertEquals("-2+-2", exp.toString() );
		Simplifier.simplify(exp);
		assertEquals("-4", exp.toString() );
		
		formula = "-(2+x)";
		exp = new Expression(formula);
		Simplifier.removeNegateForConsts(exp);
		assertEquals("-(2+x)", exp.toString() );
		Simplifier.simplify(exp);
		assertEquals("-(2+x)", exp.toString() );		
		
		formula = "-(x+x)";
		exp = new Expression(formula);
		Simplifier.removeNegateForConsts(exp);
		assertEquals("-(x+x)", exp.toString() );
		Simplifier.simplify(exp);
		assertEquals("-2*x", exp.toString() );
		////////////////////////////////////////
		formula = "y*-3*y*(2+3)";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("-15*y^2", exp.toString() );
	}
	@Test 
	public final void test_MullAdd() throws Exception {
		formula = "2*x+x";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("3*x", exp.toString() );		

		formula = "2*x+2*x";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("4*x", exp.toString() );		

		formula = "3*x-x";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("2*x", exp.toString() );		

		formula = "3*x-4*x";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("-x", exp.toString() );		

		formula = "3*x-x-x-x";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("0", exp.toString() );		

		formula = "x+x+x+x";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("4*x", exp.toString() );		
	}

	@Test 
	public final void testFold_0() throws Exception {
		formula = "-(x/x)";
		exp = new Expression(formula);
		Simplifier.fold(exp);
		assertEquals("-1", exp.toString() );
		Simplifier.simplify(exp);
		assertEquals("-1", exp.toString() );
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("-1", exp.toString() );
	}
	@Test 
	public final void testFold_PowAndMul() throws Exception {
		formula = "y*(y^2)";
		exp = new Expression(formula);
//		Simplifier.fold(exp);
//		assertEquals("y^3", exp.toString() );
		Simplifier.simplify(exp);
		assertEquals("y^3", exp.toString() );
	}
	@Test//(timeout=1*1000) 
	public final void testFold_Mull() throws Exception {
		formula = "2*x*2";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("4*x", exp.toString() );
		
		formula = "2*2";
		exp = new Expression(formula);
		Simplifier.fold(exp);
		assertEquals("4", exp.toString() );
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("4", exp.toString() );
		
		formula = "x*x";
		exp = new Expression(formula);
		Simplifier.fold(exp);
		assertEquals("x^2", exp.toString() );
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("x^2", exp.toString() );
		
		formula = "(x1*2)*x2";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("2*x1*x2", exp.toString() );

		formula = "(x1+x1)*x2";
//		exp = new Expression(formula);
//		Simplifier.fold(exp);
//		assertEquals("(x1*2)*x2", exp.toString() );
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("2*x1*x2", exp.toString() );

		formula = "y*y*(2+3)";
		exp = new Expression(formula);
		Simplifier.fold(exp);
		assertEquals("5*y^2", exp.toString() );

		formula = "y*3*y*(2+3)";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("15*y^2", exp.toString() );

		formula = "2+x+2+x+6+y";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("10+2*x+y", exp.toString() );	

		formula = "z*(-2)*x*2*z";
		exp = new Expression(formula);
		assertEquals("z*-2*x*2*z", exp.toString() );
		Simplifier.simplify(exp);
		assertEquals("-4*x*z^2", exp.toString() );

/// fails...
		formula = "y*y*3*y";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("3*y^3", exp.toString() );
		
		formula = "x*x+y*y*3*y*(2+3)";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("15*y^2+x^2", exp.toString() );
	}	
	@Test
	public final void testSort() throws Exception {
		formula = "x+2";
		exp = new Expression(formula);
		Simplifier.sort(exp);
		assertEquals("2+x", exp.toString());

		formula = "z+(x*10)";
		exp = new Expression(formula);
		Simplifier.sort(exp);
		assertEquals("10*x+z", exp.toString());
		
		formula = "100*(((x0-x1)+1)*2)";
		exp = new Expression(formula);
		Simplifier.sort(exp);
		assertEquals("(1+x0-x1)*2*100", exp.toString());
	}
	@Test
	public final void testReduceConstants() throws Exception {
		formula = "1+2-x";
		exp = new Expression(formula);
		Simplifier.reduceConstants(exp);
		assertTrue(Long.toString(seed), exp.toString().equals("3-x") );

		formula = "1-2";
		exp = new Expression(formula);
		Simplifier.reduceConstants(exp);
		assertTrue(Long.toString(seed), exp.toString().equals("-1") );
	}
	
	@Test
	public final void testRemoveZeros() throws Exception {
		formula = "(x-0)";
		exp = new Expression(formula);
		Simplifier.removeZeros(exp);
		assertEquals("x", exp.toString());

		formula = "0*x";
		exp = new Expression(formula);
		Simplifier.removeZeros(exp);
		assertEquals("0", exp.toString());
		formula = "x*0";
		exp = new Expression(formula);
		Simplifier.removeZeros(exp);
		assertEquals("0", exp.toString());
		
		formula = "0+x";
		exp = new Expression(formula);
		Simplifier.removeZeros(exp);
		assertEquals("x", exp.toString());
		formula = "x+0";
		exp = new Expression(formula);
		Simplifier.removeZeros(exp);
		assertEquals("x", exp.toString());

		formula = "0/x";
		exp = new Expression(formula);
		Simplifier.removeZeros(exp);
		assertEquals("0", exp.toString());
		
		formula = "(x+1-0) * 0 + x - 0 + 1";
		exp = new Expression(formula);
		Simplifier.removeZeros(exp);
		assertEquals("x+1", exp.toString());
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("1+x", exp.toString());
		
		formula = "x^0";
		exp = new Expression(formula);
		Simplifier.removeZeros(exp);
//		System.out.println(exp);
		assertEquals("1", exp.toString());
		
		formula = "0^(x+y+1)";
		exp = new Expression(formula);
		Simplifier.removeZeros(exp);
//		System.out.println(exp);
		assertEquals("0", exp.toString());
		
		
	}

	@Test
	public final void testRemoveOnes() throws Exception {
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
		assertEquals("x*x", exp.toString() );

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
		assertTrue(""+seed, exp.toString().equals("1") );		
		
		formula = "1/(x*y)";
		exp = new Expression(formula);
		Simplifier.removeOnes(exp);
		assertEquals("1/(x*y)", exp.toString());		
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("1/(x*y)", exp.toString());		
	}
	@Test
	public final void testMakeTreeDipperNotWide0() throws Exception {
		formula = "1 - (2-3)";
		exp = new Expression(formula);
		Simplifier.makeTreeDipperNotWide(exp);
//		System.out.println(exp);
		assertEquals("1+3-2", exp.toString() );		
		formula = "1-2-3";
		exp = new Expression(formula);
		Simplifier.makeTreeDipperNotWide(exp);
//		System.out.println(exp);
		assertEquals("1-2-3", exp.toString() );		
	}
	@Test
	public final void testMakeTreeDipperNotWide() throws Exception {
		formula = "1-z/(2*3)/4/5*(2-3)";
		//formula = "z/(2*3)";
		exp = new Expression(formula);
//		System.out.println(exp);
//		System.out.println(exp.toStringGraph() + "\n------------------");
		Simplifier.makeTreeDipperNotWide(exp);
//		System.out.println(exp.toStringGraph() + "\n==================");
//		System.out.println(exp);
		assertEquals("1-z/3/2/4/5*(2-3)", exp.toString() );		
	}
	@Test
	public final void testMakeTreeDipperNotWide1() throws Exception {
		formula = "9-(8-(7-(6+55)))";
		exp = new Expression(formula);
		Simplifier.makeTreeDipperNotWide(exp);
//		System.out.println(exp.toStringGraph() + "\n==================");
		assertEquals("9+7-55-6-8", exp.toString());
		
		formula = "9-(8-(7-(6-(5-4))))"; // 9-8+7-6+5-4
		exp = new Expression(formula);
		Simplifier.makeTreeDipperNotWide(exp);
		//System.out.println(exp.toStringGraph() + "\n==================");
		assertEquals("9+7+5-4-6-8", exp.toString());		
	}
	

	@Test//(timeout=1*1000)
	public final void test0() throws Exception {
		formula = "(x1 - x0)";
		exp = new Expression(formula);
		Simplifier.simplify(exp);

		Gradient grad = new Gradient(exp);
	    Expression d1_0 = grad.getPartialDerivative(0);
	    
	    Simplifier.simplify(d1_0);
	    assertEquals("-1", d1_0.toString());
	}
	
	@Test
	public final void testNegate() throws Exception {
		formula = "-1";//"0-(2*x0)";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("-1", exp.toString());
		formula = "0-x";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("-x", exp.toString());
		formula = "-x";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("-x", exp.toString());
	}
	@Test
	public final void test_RastriginG_nD_part1a_simplification() throws Exception {
		formula = "(2*(x1-(x0^2)))*(0-(2*x0))";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("-4*x0*(x1-x0^2)", exp.toString());
	}
	@Test//(timeout=1*1000)
	public final void test_RastriginG_nD_part1_diff() throws Exception {
		formula = "(x1 - x0^2)^2";
		exp = new Expression(formula);
		Simplifier.simplify(exp);

		Gradient grad = new Gradient(exp);
	    Expression d1_0 = grad.getPartialDerivative(0);
	    //System.out.println("df/dx1: " + d1_0);	    
	    
	    Simplifier.simplify(d1_0);
	    assertEquals("-4*x0*(x1-x0^2)", d1_0.toString()); // adiff: ((-4) * x0 * (x1 - (x0^2)))
	    
	    Expression d1_1 = grad.getPartialDerivative(1);
	    //System.out.println("df/dx1: " + d1_1);	    
	    Simplifier.simplify(d1_1);
	    //System.out.println("Simplified df/dx1: " + d1_1);
	    assertEquals("2*(x1-x0^2)", d1_1.toString());
	}
	@Test
	public final void test_RastriginG_nD_1() throws Exception {
		formula = "(100*x)^2";
		exp = new Expression(formula);
		Gradient grad = new Gradient(exp);
		Simplifier.simplify(grad.getPartialDerivative(0));
		assertEquals("20000*x", grad.toString());

		formula = "100*(x)^2";
		exp = new Expression(formula);
		grad = new Gradient(exp);
		Simplifier.simplify(grad.getPartialDerivative(0));
		assertEquals("200*x", grad.toString());		
	}	
	@Test
	public final void test_RastriginG_nD_simplify_small() throws Exception {
		formula = "((x0-1)^2)";
		exp = new Expression(formula);
		Gradient grad = new Gradient(exp);
		Simplifier.simplify(grad.getPartialDerivative(0)); //nvp 01/19/12 -- Gradient doesn't call simplify() anymore
		assertEquals("2*(x0-1)", grad.toString());

		formula = "((x1-(x0^2))^2)";
		exp = new Expression(formula);
		grad = new Gradient(exp);
		Simplifier.simplify(grad.getPartialDerivative(0));
		Simplifier.simplify(grad.getPartialDerivative(1));
		assertEquals("-4*x0*(x1-x0^2)+2*(x1-x0^2)", grad.toString()); // adiff: ((-2*(x1-(x0^2))*(2*x0)) + 2*(x1-(x0^2))) 

		formula = "(100*((x1-(x0^2))^2))+((x0-1)^2)";
		exp = new Expression(formula);
		grad = new Gradient(exp); // adiff: (-(400 * x0 * (x1 - (x0^2))) + (2 * (-1 + x0)))     +     (200 * (x1 - (x0^2)))
		Simplifier.simplify(grad.getPartialDerivative(0));
		Simplifier.simplify(grad.getPartialDerivative(1));
		assertEquals("-400*x0*(x1-x0^2)+2*(x0-1)+200*(x1-x0^2)", grad.toString());
	}
		
	@Test
	public final void test_RastriginG_nD() throws Exception {
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
		//System.out.println(exp);
		if (dim == 2)
			assertEquals("100*(x1-x0^2)^2+(x0-1)^2", exp.toString());

		Gradient grad = new Gradient(exp);
	    
	    Expression d1_0 = grad.getPartialDerivative(0);
		Simplifier.simplify(d1_0);
	    
	    // http://www.adiff.com :	 (-(400 * x0 * (x1 - (x0^2))) + (2 * (-1 + x0)))
	    assertEquals("-400*x0*(x1-x0^2)+2*(x0-1)", d1_0.toString());

	    Expression d1_1 = grad.getPartialDerivative(1);
		Simplifier.simplify(d1_1);
	    assertEquals("200*(x1-x0^2)", d1_1.toString());
	}
	
	@Test
	public void t2() throws ExpressionException {
		String f = "((((x1+x1)+(x3*x3))+x2)+x2)+(x2*(x3+x3))"; // 2*x1+x3^2+2*x2+x2*2*x3 = x2(2+2x3)+2x1+x3^2 = ((1+x3)x2+x1)*2+x3^2 :: OK 
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("((1+x3)*x2+x1)*2+x3^2", exp.toString());
	}
	@Test
	public void t3() throws ExpressionException {
		String f = "2*(x2*x3)";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("2*x2*x3", exp.toString());
		
		exp = new Expression("(x3^2)+(2*(x2*x3))");
		Simplifier.simplify(exp);
		assertEquals("2*x2*x3+x3^2", exp.toString());
	}
	@Test
	public void t4() throws ExpressionException {
		String f;
		f = "xx-x";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("xx-x", exp.toString());
		f = "x-x";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("0", exp.toString());
		f = "x-1-x";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("-1", exp.toString());
		f = "-x+x";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("0", exp.toString());
		f = "-x/x";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("-1", exp.toString());
	}
	
	@Test
	public void t5() throws ExpressionException {
		formula = "y*-3*y*(2+3)";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("-15*y^2", exp.toString() );
	}
	
	@Test
	public void testE() throws ExpressionException {
		String f;
		f = "1e-1";
		exp = new Expression(f);
		//System.out.println(exp);
		Simplifier.simplify(exp);
		assertEquals("0.1", exp.toString());
		
		f = "1e-8-1e-8+2-ee-2";
		exp = new Expression(f);
		//System.out.println(exp);
		Simplifier.simplify(exp);
		assertEquals("-ee", exp.toString());
	}
	
	@Test
	public void test_removeNegateInAddOrSub() throws ExpressionException {
		//-2+x = > x-2; x-(-2) = > x+2
		String f = "-2+x";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("x-2", exp.toString());
		f = "x-(-2)";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("2+x", exp.toString());
		f = "-(-2)";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("2", exp.toString());

		/////////////////////////////////////////////
		
		f = "( a)-( b)";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("a-b", exp.toString());
		
		f = "( a)-(-b)";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("a+b", exp.toString());
		
		f = "(-a)-( b)";
		exp = new Expression(f);
		Simplifier.removeNegateInAddOrSub(exp);
		assertEquals("-(a+b)", exp.toString());
		
		f = "(-a)-(-b)";
		exp = new Expression(f);
		Simplifier.removeNegateInAddOrSub(exp);
		assertEquals("b-a", exp.toString());
		
		////
		f = "( a)+( b)";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("a+b", exp.toString());		
		
		f = "(-a)+( b)";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("b-a", exp.toString());		
		
		f = "( a)+(-b)";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("a-b", exp.toString());		

		f = "(-a)+(-b)";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("-(a+b)", exp.toString());		
	}
	@Test
	public void removeNegateInMulOrDiv() throws ExpressionException {
		//2*(-x)=>-1*2*x; -x/x = > -1*x/x
		String f;
		f = "2*(-x)";
		exp = new Expression(f);
		Simplifier.removeNegateInMulOrDiv(exp);
		assertEquals("-2*x", exp.toString());
		assertFalse(exp.toStringGraph().contains("negate"));
		Simplifier.simplify(exp);
		assertEquals("-2*x", exp.toString());		
		f = "-x/x";
		exp = new Expression(f);
		Simplifier.removeNegateInMulOrDiv(exp);
		assertEquals("-x/x", exp.toString());
		Simplifier.simplify(exp);
		assertEquals("-1", exp.toString());		
		f = "-1/1+1/-1-1/-1";
		exp = new Expression(f);
		Simplifier.removeNegateInMulOrDiv(exp);
//		assertEquals("(((-1*1)/1)+(1/(-1*1)))-(1/(-1*1))", exp.toString());
		Simplifier.simplify(exp);
		assertEquals("-1", exp.toString());		
		
		f = "a/b";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("a/b", exp.toString());		
		
		f = "a/-b";
		exp = new Expression(f);
		Simplifier.removeNegateInMulOrDiv(exp);
		assertEquals("-a/b", exp.toString());		
		
		f = "-a/b";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("-a/b", exp.toString());		
		
		f = "-a/-b";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("a/b", exp.toString());		
		
		////
		f = "a*b";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("a*b", exp.toString());		
		
		f = "a*-b";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("-a*b", exp.toString());		
		
		f = "-a*b";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("-a*b", exp.toString());		
		
		f = "-a*-b";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("a*b", exp.toString());		
		
	}
	
	@Test
	public void removeDoubleNegate() throws ExpressionException {
		String f;
		f = "-(x/-y)";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("x/y", exp.toString()); 

		f = "-(-x/2)";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("0.5*x", exp.toString());		
	}
	
	@Test
	public void removeNegativeInPower() throws ExpressionException {
		//x*y^-z => x/y^z; x/y^-z => x*y^z
		String f;
		// negative in mantissa
		f = "-x^y"; 
		exp = new Expression(f);
		Simplifier.removeNegativeInPower(exp);
		assertEquals("-x^y", exp.toString());
		assertTrue(exp.isNegate());
		
		f = "-x^2";
		exp = new Expression(f);
		Simplifier.removeNegativeInPower(exp);
		assertEquals("x^2", exp.toString());
		
		f = "-x^-2";
		exp = new Expression(f);
		Simplifier.removeNegativeInPower(exp);
		assertEquals("-x^-2", exp.toString());
		assertTrue(exp.isNegate());

		f = "-x^3";
		exp = new Expression(f);
		Simplifier.removeNegativeInPower(exp);
		assertEquals("-x^3", exp.toString());
		assertTrue(exp.isNegate());

		f = "-x^-y";
		exp = new Expression(f);
		Simplifier.removeNegativeInPower(exp);
		assertEquals("-x^-y", exp.toString());
		assertTrue(exp.isNegate());
		
		////
		f = "-x*y^-z";
		exp = new Expression(f);
		Simplifier.removeNegativeInPower(exp);
		assertEquals("-x/y^z", exp.toString());
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("-x/y^z", exp.toString());
		f = "x/y^-z";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("x*y^z", exp.toString());
		f = "x*y^-(4/2)";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("x/y^2", exp.toString());
		f = "x/y^-2";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("x*y^2", exp.toString());
		
		f = "y^-1/x^1";
		exp = new Expression(f);
		Simplifier.removeNegativeInPower(exp);
		assertEquals("1/x^1/y", exp.toString());
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("1/y/x", exp.toString());
	}
	@Test
	public void testFold2() throws ExpressionException {
		String f = "x/(y*z)";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("x/y/z", exp.toString());
		f = "x-(y+z)";
		exp = new Expression(f);
		Simplifier.simplify(exp);
//		assertEquals("x-(y+z)", exp.toString());
		f = "a/(b/(c/(d/e)))";
		// (((d/e)^-1*c)^-1*b)^-1*a  =  ( ((e/d)c )^-1*b)^-1*a  =  {[(ec/d)^-1*b]^-1*b}^-1*a   = 
		//      = (d/ec * b)^-1*a  =  (db/ec)^-1*a   =   aec/db    =  a/b*c/d*e  
		exp = new Expression(f);
		assertEquals("a/(b/(c/(d/e)))", exp.toString());
		Simplifier.simplify(exp);
		assertEquals("a/b*c/d*e", exp.toString());
		
		f = "(((d/e)^-1*c)^-1*b)^-1*a";
		exp = new Expression(f);
		Simplifier.simplify(exp);
		assertEquals("a/b*c/d*e", exp.toString());
	}
	@Test
	public void testFold3() throws ExpressionException {
		formula = "1/x*y";
		exp = new Expression(formula);
		Simplifier.simplify(exp);
		assertEquals("y/x", exp.toString());		
		formula = "1/(x*y)";
		exp = new Expression(formula);
		Simplifier.fold(exp);
		assertEquals("1/x/y", exp.toString());
		Simplifier.simplify(exp);
		assertEquals("1/(x*y)", exp.toString());
	}
	@Test
	public void testFold_getOperandsFromAllChildrenWithThisOrComplimentOp() throws ExpressionException {
		String f = "x/(y)";
		exp = new Expression(f);
		LinkedList<ExpAndOp> operands = new LinkedList<>();
		String thisNodeOp = exp.getOperation();
		Simplifier.getOperandsFromAllChildrenWithThisOrComplimentOp(exp, thisNodeOp, thisNodeOp, 0, operands);
		assertEquals(2, exp.getDimension());
		assertEquals(2, operands.size());
		assertEquals("*x", operands.get(0).toString());
		assertEquals("/y", operands.get(1).toString());
		
		f = "x/(y/z)";
		exp = new Expression(f);
		operands = new LinkedList<>();
		thisNodeOp = exp.getOperation();
		Simplifier.getOperandsFromAllChildrenWithThisOrComplimentOp(exp, thisNodeOp, thisNodeOp, 0, operands);
		assertEquals(3, operands.size());
		assertEquals(exp.getDimension(), operands.size());
		assertEquals("*x", operands.get(0).toString());
		assertEquals("/y", operands.get(1).toString());
		assertEquals("*z", operands.get(2).toString());
		
		f = "-a-b-(c-d-(-e-f-(g-k)))";
		//   -a-b -c+d  -e-f-g+k 
		exp = new Expression(f);
//		System.out.println(exp.toStringGraph());
		operands = new LinkedList<>();
		thisNodeOp = exp.getOperation();
		Simplifier.getOperandsFromAllChildrenWithThisOrComplimentOp(exp, thisNodeOp, thisNodeOp, 0, operands);
		assertEquals(exp.getDimension(), operands.size());
		assertEquals("+-a", operands.get(0).toString());
		assertEquals("-b", operands.get(1).toString());
		assertEquals("-c", operands.get(2).toString());		
		assertEquals("+d", operands.get(3).toString());		
		assertEquals("+-e", operands.get(4).toString());		
		assertEquals("-f", operands.get(5).toString());		
		assertEquals("-g", operands.get(6).toString());		
		assertEquals("+k", operands.get(7).toString());
		
		f = "a-b-c";
		exp = new Expression(f);
		operands = new LinkedList<>();
		thisNodeOp = exp.getOperation();
		Simplifier.getOperandsFromAllChildrenWithThisOrComplimentOp(exp, thisNodeOp, thisNodeOp, 0, operands);
		assertEquals(exp.getDimension(), operands.size());
		assertEquals("+a", operands.get(0).toString());
		assertEquals("-b", operands.get(1).toString());
		assertEquals("-c", operands.get(2).toString());
		f = "a-(b-c)";
		exp = new Expression(f);
		operands = new LinkedList<>();
		thisNodeOp = exp.getOperation();
		Simplifier.getOperandsFromAllChildrenWithThisOrComplimentOp(exp, thisNodeOp, thisNodeOp, 0, operands);
		assertEquals(exp.getDimension(), operands.size());
		assertEquals("+a", operands.get(0).toString());
		assertEquals("-b", operands.get(1).toString());
		assertEquals("+c", operands.get(2).toString());
		
		f = "a/(b/(c/((d*f)/(e*g))))"; // a/(b/( (ceg)/(df) )) => a/( (bdf)/(ceg) ) => aceg / bdf 
		exp = new Expression(f);
		operands = new LinkedList<>();
		thisNodeOp = exp.getOperation();
//	System.out.println(exp.toStringGraph());		
		Simplifier.getOperandsFromAllChildrenWithThisOrComplimentOp(exp, thisNodeOp, thisNodeOp, 0, operands);
		assertEquals(exp.getDimension(), operands.size());
		Collections.sort(operands, Simplifier.alphabeticallySorter); // getOperandsFromAllChildrenWithThisOrComplimentOp doesn't sort operands
		assertEquals("*a", operands.get(0).toString());
		assertEquals("/b", operands.get(1).toString());
		assertEquals("*c", operands.get(2).toString());
		assertEquals("/d", operands.get(3).toString());
		assertEquals("*e", operands.get(4).toString());
		assertEquals("/f", operands.get(5).toString());		
		assertEquals("*g", operands.get(6).toString());
		
		f = "x/(y*z)";
		exp = new Expression(f);
		operands = new LinkedList<>();
		thisNodeOp = exp.getOperation();
//	System.out.println(exp.toStringGraph());		
		Simplifier.getOperandsFromAllChildrenWithThisOrComplimentOp(exp, thisNodeOp, thisNodeOp, 0, operands);
		Collections.sort(operands, Simplifier.alphabeticallySorter); // getOperandsFromAllChildrenWithThisOrComplimentOp doesn't sort operands
		assertEquals("*x", operands.get(0).toString());
		assertEquals("/y", operands.get(1).toString());
		assertEquals("/z", operands.get(2).toString());
	}
	@Test
	public void test_Feature_cancellationOfLikeTerms() throws ExpressionException {
		exp = new Expression("(a+b)/(b+a)");
		Simplifier.simplify(exp);
		assertEquals("1", exp.toString());		
	}

	@Test
	public void internalTest_splitByMuls() throws ExpressionException {
		Map<Expression, Expression> parts = new HashMap<>();
		exp = new Expression("a*b");
		Simplifier.splitByMuls(exp, parts, null);
		assertTrue(parts.size() == 2+1);

		parts = new HashMap<>();
		exp = new Expression("a*b*c"); // abc, a*bc, ab*c
		Simplifier.splitByMuls(exp, parts, null);
		assertTrue(parts.size() == 6+1);

		parts = new HashMap<>();
		exp = new Expression("a*b*(c+d)");
		Simplifier.splitByMuls(exp, parts, null);
		assertTrue(parts.size() == 6+1);
		assertEquals(parts.get(new Expression("a*b")), new Expression("c+d"));
		assertEquals(parts.get(new Expression("c+d")), new Expression("a*b"));
		assertEquals(parts.get(new Expression("a")), new Expression("b*(c+d)"));
		assertEquals(parts.get(new Expression("a*b*(c+d)")), new Expression("1"));
	}
	@Test
	public void internalTest_foldEqualsAccurateWithinMultipier() throws ExpressionException {
		LinkedList<ExpAndOp> operands = new LinkedList<>();
		operands.add(new ExpAndOp(new Expression("a*b*c"), "+"));
		operands.add(new ExpAndOp(new Expression("a*z"), "+"));
		operands.add(new ExpAndOp(new Expression("a*k"), "-"));
		operands.add(new ExpAndOp(new Expression("a*w"), "-"));
		operands.add(new ExpAndOp(new Expression("q*w"), "+"));
		operands.add(new ExpAndOp(new Expression("w"), "-"));
		operands.add(new ExpAndOp(new Expression("none"), "-"));
		ListIterator<ExpAndOp> it = operands.listIterator();
		int size = operands.size();

		ExpAndOp e_o = it.next();
		ExpAndOp foldedExp = Simplifier.foldEqualsAccurateWithinMultipier(e_o, it, operands);
		assertFalse(foldedExp == null);
		assertEquals("+a*(b*c+z)", foldedExp.toString());
		assertEquals(size-2, operands.size());

		it = operands.listIterator();
		e_o = it.next();
		foldedExp = Simplifier.foldEqualsAccurateWithinMultipier(e_o, it, operands);
		assertEquals("-a*(k+w)", foldedExp.toString());

		it = operands.listIterator();
		e_o = it.next();
		foldedExp = Simplifier.foldEqualsAccurateWithinMultipier(e_o, it, operands);
		assertEquals("+w*(q-1)", foldedExp.toString());

		size = operands.size();
		it = operands.listIterator();
		e_o = it.next();
		foldedExp = Simplifier.foldEqualsAccurateWithinMultipier(e_o, it, operands);
		assertEquals(null, foldedExp);
		assertEquals(size, operands.size());
	}
	@Test
	public void internalTest_foldMul_AddSub() throws ExpressionException {
		LinkedList<ExpAndOp> operands = new LinkedList<>();
		operands.add(new ExpAndOp(new Expression("2*a"), "-"));
		operands.add(new ExpAndOp(new Expression("a"), "+"));
		LinkedList<ExpAndOp> folded = Simplifier.foldMul_AddSub(operands, false);
		assertEquals(0, operands.size());
		assertEquals(1, folded.size());
		assertEquals("+a*(1-2)", folded.getFirst().toString());
	}
	@Test
	public void test_foldMul_AddSub() throws ExpressionException {
		exp = new Expression("(x+y)*a+(x+y)*b");
		Simplifier.simplify(exp);
		assertEquals("(a+b)*(x+y)", exp.toString());
		
		exp = new Expression("ln(y)*y^sin(x^2)+ln(y)*y^sin(x^2)");
		Simplifier.simplify(exp);
		assertEquals("2*ln(y)*y^sin(x^2)", exp.toString());
	}
	@Test
	public void testPowers_feature() throws ExpressionException {
		exp = new Expression("a^(b-1)*a^b");
		Simplifier.simplify(exp);
		assertEquals("a^b", exp.toString());
		
		exp = new Expression("a^(b-1)*a^b^(c-1)*b*c");
		Simplifier.simplify(exp);
		assertEquals("a^b^c*b*c/a", exp.toString());
		
		exp = new Expression("a^b*a^b^(c-1)*c*ln(a)");
		Simplifier.simplify(exp);
		assertEquals("c * ln(a) * a^b^c", exp.toString());
	}		
	@Test
	public void testConstantsInFunctions() throws ExpressionException {
		exp = new Expression("sin(pi)");
		Simplifier.simplify(exp);
		assertEquals("0", exp.toString()); 
		// TODO: CONVERT TO INTERVAL!!!
	}		
	@Test
	public void test_minusFeature() throws ExpressionException {
		exp = new Expression("a--7*b");
		Simplifier.simplify(exp);
		assertEquals("a+7*b", exp.toString()); 
	}
	@Test
	public void test_Griewangk() throws ExpressionException {
		String test = "x^(2-1-1)";
		Expression exp = new Expression(test);
		Simplifier.simplify(exp);
		assertEquals("1", exp.toString());
		String df = "2.5e-4*(0+(2-1)*1*x1^(2-1-1)*2*1+0)+0";
		exp = new Expression(df);
		Simplifier.simplify(exp);
		assertEquals("5.0E-4", exp.toString());
	}	
}
