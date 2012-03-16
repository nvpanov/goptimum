package symboldiff;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import symboldiff.exceptions.ExpressionException;
import symboldiff.exceptions.IncorrectExpression;
import symboldiff.exceptions.MethodGenerationException;
import symboldiff.exceptions.MethodInvocationException;

import core.Box;

import net.sourceforge.interval.ia_math.RealInterval;

public class MethodRunner {
//	private List<String> coords;
	private final static String iamathClassName = "net.sourceforge.interval.ia_math.IAMath";
	
	public MethodRunner(/*Expression originalExpression*/) {
//		this(originalExpression.getCoords());
	}
/*
	public MethodRunner(ArrayList<String> coordinats) {
		coords = coordinats;
	}
*/


	@SuppressWarnings("unchecked")
	private static void genUnaryMethod(Expression expr)
			throws ClassNotFoundException, SecurityException,
			NoSuchMethodException {
		Class cl = Class.forName(iamathClassName);
		expr.setMethod(cl.getMethod(expr.getMethodName(),
				RealInterval.class));
	}
	
	@SuppressWarnings("unchecked")
	private static void genBinaryMethod(Expression expr)
			throws ClassNotFoundException, SecurityException,
			NoSuchMethodException {
		Class cl = Class.forName(iamathClassName);
		String methodName = expr.getMethodName();

		if (methodName.equals("^") && expr.getRightExpression().isConstant()) {
			methodName = "intPow";
		}
		
		methodName = getNameForReflection(methodName);
		
		expr.setMethod(cl.getMethod(methodName,
				new Class[] { RealInterval.class, RealInterval.class }));
	}

	private static String getNameForReflection(String methodName) {
		if (methodName.equals("^") )
			return "power";
		if (methodName.equals("+") )
			return "add";
		if (methodName.equals("-") )
			return "sub";
		if (methodName.equals("*") )
			return "mul";
		if (methodName.equals("/") )
			return "div";
		
		return methodName;
	}	

	// transform some unary operations to binary
	// because ia_math doesn't have method for this function
	// FE: arcctg => pi/2 - arctg
	private static boolean disclosureOperation(Expression expr) throws ExpressionException {
		RPN rpn = null;
		Expression arg = expr.getRightExpression();
		if (expr.getOperation().equalsIgnoreCase("arcctg")) {
			expr.setOperation("-");
			try {
				rpn = new RPN("pi/2");
			} catch (IncorrectExpression e) {/*impossible*/}
			expr.setRightExpression(arg);
			expr.setLeftExpression(new Expression(rpn));
			return true;
		}
		if (expr.getOperation().equalsIgnoreCase("ctg")) {
			expr.setOperation("/");
			expr.setRightExpression(arg);
			try {
				rpn = new RPN("1");
			} catch (IncorrectExpression e) {/*impossible*/}
			expr.setLeftExpression(new Expression(rpn));
			return true;
		}
		if (expr.getOperation().equalsIgnoreCase("sqrt")) {
			expr.setOperation("^");
			expr.setLeftExpression(arg);
			try {
				rpn = new RPN("1/2");
			} catch (IncorrectExpression e) {/*impossible*/}
			
			expr.setRightExpression(new Expression(rpn));
			return true;
		}
		return false;
	}
	
/*
 * added later...
 * actually it would be better to rewrite Expression
 * and completely get rid from MethodRunner
 */
/*	public static RealInterval evaluate(Expression expr) {
		RealInterval result;
		
		
		return result;
	}
*/
	
	public void generateMethods(Expression expr)
			throws MethodGenerationException {
		try {
			if (expr.isUnaryOperation()) {
				if (!disclosureOperation(expr)) {
					genUnaryMethod(expr);
					generateMethods(expr.getRightExpression());
				}
			}

			if (expr.isBinaryOperation()) {
				genBinaryMethod(expr);
				generateMethods(expr.getLeftExpression());
				generateMethods(expr.getRightExpression());
			}
		} catch (Exception e) {
			// ClassNotFoundException, SecurityException, NoSuchMethodException
			throw new MethodGenerationException(
					"Failed to generate methods for expr=" + expr, e);
		}
	}

	public RealInterval invokeMethods(Box box, Expression exp) 
		throws MethodInvocationException {

		RealInterval result = null;
		RealInterval op1, op2;
		try {
			if (exp.isBinaryOperation()) {
				if (exp.getMethod() == null) {
					throw new RuntimeException("Missed Method for \""
							+ exp.getOperation() + "\"");
				}
				op1 = invokeMethods/*Internal*/(box, exp.getLeftExpression());
				op2 = invokeMethods/*Internal*/(box, exp.getRightExpression());
				Object r = exp.getMethod().invoke(null, new Object[] { op1, op2 });
				//result = new Operand(r, op1, op2 );
				result = (RealInterval) r;
			} else if (exp.isUnaryOperation()) {
				if (exp.getMethod() == null) {
					throw new RuntimeException("Missed Methed for \""
							+ exp.getOperation() + "\"");
				}
				op2 = invokeMethods/*Internal*/(box, exp.getRightExpression());
				Object r = exp.getMethod().invoke(null, new Object[] { op2 });
				//result = new Operand(r, op2);
				result = (RealInterval) r;
			} else {
				if (exp.isVariable()) {
					int num = exp.getNumberOfThisVariable();//coords.indexOf(exp.getOperation());
					result = /*new Operand*/(box.getInterval(num));
				} else {
					double d = exp.getConstantValue();
					result = new RealInterval(d);
					//result = new Operand(d);
				}
			}
		} catch (Exception e) {
			// IllegalArgumentException, IllegalAccessException, InvocationTargetException 
			throw new MethodInvocationException(
					"Failed to generate methods for expr=" + exp + " on box = " + box, e);
		}
	
		return result;
	}
/*	
	private class Operand {
		private Double dVal = null;
		private RealInterval iVal = null;
		public Operand(double d) {
			dVal = Double.valueOf(d);
		}
		public Operand(RealInterval i) {
			iVal = i;
		}
		public Operand(Object r, Operand op1, Operand op2) {
			if (isTypeOfInterval(op1, op2))
				iVal = (RealInterval)r;
			else
				dVal = (Double)r;
		}
		public Operand(Object r, Operand op2) {
			if (op2.isTypeOfInterval())
				iVal = (RealInterval)r;
			else
				dVal = (Double)r;
		}
		public boolean isTypeOfInterval(Operand op1, Operand op2) {
			return op1.isTypeOfInterval() || op2.isTypeOfInterval();
		}
		public boolean isTypeOfInterval() {
			if (iVal != null)
				return true;
			return false;
		}

		public double getDValue() {
			return dVal;
		}
		public RealInterval getIValue() {
			return iVal;
		}
		
	}
*/
}
