package symboldiff;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Simplifier {
//	protected static enum ExpType {constant, interval, operation};
/*	protected boolean wasExpChanged;
	protected Expression exp;
	
	public Simplifier(Expression exp) {
		this.exp = exp;
		wasExpChanged = false;
	}
*/	
	public static void simplify(Expression exp) {
		Expression simplified = exp;
		boolean wasExpChanged = false;
		
		do {
			wasExpChanged = false;
//			wasExpChanged |= transformToCanonical(simplified);
			wasExpChanged |= reduceConstants(simplified);
			wasExpChanged |= removeZeros(simplified);
			wasExpChanged |= removeOnes(simplified);
	
			wasExpChanged |= makeTreeDipperNotWide(simplified);
		} while (wasExpChanged);
		
//		return simplified;
	}
	
	/*
	 * sort expression tree alphabetically (when possible)
	 *  otherwise (x+1)+2 will always be (something + 1)
	 *  Also this is important for finding sub-trees  
	 */
	private static boolean transformToCanonical(Expression exp) {
		boolean wasExpChanged = makeTreeDipperNotWide(exp);
		wasExpChanged |= sortTree(exp);
		return wasExpChanged;
	}

	private static boolean sortTree(Expression exp) {
		String opStr = exp.getMethodName();
		Expression l = exp.getLeftExpression();
		Expression r = exp.getRightExpression();
		//TODO
		((Class)null).getClass();
		return false;
	}
	
	/*
	 *                         +
	 *        +       =>     +   3 
	 *     +     +         +   4
	 *    1 2   4 3       1 2
	 */
	protected static boolean makeTreeDipperNotWide(Expression exp) {
		if (exp == null)
			return false;
		Expression l = exp.getLeftExpression();
		Expression r = exp.getRightExpression();
		boolean success = false;
		success |= makeTreeDipperNotWide(l);
		success |= makeTreeDipperNotWide(r);
		
		// process this node
		boolean rehanged;
		do {
			rehanged = moveSubTreeToLeftSide(exp);
			if (rehanged)
				success = true;
		} while (rehanged);
		// now right side of this node is either 
		// not a tree or not commutative. go further.
		
		//this.wasExpChanged |= success;
		return success;
	}

	private static boolean moveSubTreeToLeftSide(Expression node) {
		Expression l = node.getLeftExpression();
		Expression r = node.getRightExpression();
		boolean changed = false;

		if (r != null) {
			/* 
			 * node-> (+)        node -> (+)
			 *       /   \   ==>        /   \
			 *    (...)  (+) <-r----> (+)    a
			 *           / \         /   \
			 *          a   b     (...)   b
			 *          
			 * ===  absolutely equivalent if (+) & (-)  ===         
			 *          
			 * node-> (+)        node -> (+)
			 *       /   \   ==>        /   \
			 *    (...)  (-) <-r----> (-)    a
			 *           / \         /   \
			 *          a   b     (...)   b          
			 *          
			 * === but! if (-) followed by (-) or (\) after (\) ===
			 * 
			 *        (-) <----- node -> (-)
			 *       /   \   ==>        /   \
			 *    (...)  (-) <-r----> (+)    a
			 *           / \         /   \
			 *          a   b     (...)   b          
			 *          
			 *        (/) <----- node -> (/)
			 *       /   \   ==>        /   \
			 *    (...)  (/) <-r----> (*)    a
			 *           / \          / \
			 *          a   b     (...)  b          
			 *          
			 */
			if (node.isBinaryOperation() && r.isBinaryOperation()) {
				String thisOperation = node.getOperation();
				String rOperation = r.getOperation();

				if (r.getMethod() != null) { 
					// _sometimes_ we will change the operation
					// but if corresponding method already was generated
					throw new IllegalStateException("Expression is already " +
							"prepeared for reflection. Immposible to change methods");
				}
				
				// see the diagrame above
				if(isDirectlyCommutative(thisOperation, rOperation) ||
				   isReversableCommutative(thisOperation, rOperation)) {
					changed = true;
					Expression a = r.getLeftExpression();
					//Expression b = r.getRightExpression();
					node.setLeftExpression(r); // r
					r.setLeftExpression(l); // (...)
					// do nothing // b is already on his position
					node.setRightExpression(a); // a
					
					if (thisOperation.equals(rOperation)){
						// (/) after (/) or (-) after (-)
						//switch(thisOperation) // will be available in jdk1.7
						if (thisOperation.equals("/"))
							r.setOperation("*"); // not "mul" :(
						else if (thisOperation.equals("-"))
							r.setOperation("+");
						else {
							// +, *  -- it's OK
						}
					}
					else if (isReversableCommutative(thisOperation, rOperation)) {
						changed = true;
						// a - (b + c)   or   a / (b * c)
						if (thisOperation.equals("/"))
							// r can be only "*", otherwise it either not commutative
							// or is "/" again, so already processed in prev. if
							r.setOperation("/");
						else if (thisOperation.equals("-"))
							// again - r can be only "+" here
							r.setOperation("-");
						else {
							// must be an error
							throw new IllegalStateException();
						}
					}
//					return true;
				}
			}
		}
		//return false; // nothing was changed
		return changed;
	}

	private static boolean foldConstants(Expression exp) {
		// TODO Auto-generated method stub
		((Class)null).getClass();
		return false;
	}
/*
	private static void sortTree(Expression exp) {
		if (exp == null)
			return;
		Expression l = exp.getLeftExpression();
		Expression r = exp.getRightExpression();
		transformToCanonical(l);
		transformToCanonical(r);
		
		if (l == null || r == null)
			return; 

		String opStr = exp.getMethodName();
		if(isOpCommutative(opStr)) {
			if (getType(l) != ExpType.operation && getType(r) != ExpType.operation) {
				if (l.getOperation().compareTo(r.getOperation()) > 0) {
					exp.setLeftExpression(r);
					exp.setRightExpression(l);
				}
			}
		}
	}
*/
	protected static boolean reduceConstants(Expression exp) {
		if (exp == null)
			return false;
		Expression l = exp.getLeftExpression();
		Expression r = exp.getRightExpression();
		boolean success = false;
		success |= reduceConstants(l);
		success |= reduceConstants(r);
		
		// works with arithmetic only, no functions like sin(x) are supported
		if (l == null || r == null)
			return false;

		if (l.isConstant() && r.isConstant()) {
			// two constants
			String opStr = exp.getMethodName();

			double newVal;
			double a = l.getConstantValue(), b = r.getConstantValue();
			if (opStr.equals("+")) {
				newVal = a + b;
			} else if (opStr.equals("-")) {
				newVal = a - b;
			} else if (opStr.equals("*")) {
				newVal = a * b;
			} else if (opStr.equals("/")) {
				newVal = a / b;
			} else if (opStr.equals("^")) {
				newVal = Math.pow(a, b);
			} else
				throw new IllegalArgumentException("Unknown operation");
			//return new Expression(newVal);
			exp.setTo(newVal);
			return true; 
			// else next block with setLeft/RightExpressions will be executed.
		}
		// exp can be left the same, while left and right expression can be simplified: 1+2+x
		exp.setLeftExpression(l);
		exp.setRightExpression(r);
		return success;//exp;	
	}
	public static boolean removeZeros(Expression exp) {
		if (exp == null)
			return false;
		Expression l = exp.getLeftExpression();
		Expression r = exp.getRightExpression();
		boolean success = false;
		success |= removeZeros(l);
		success |= removeZeros(r);
		
		if (l == null || r == null)
			return false; 

		String opStr = exp.getMethodName();
		
		// left is 0, right is anything
		if (l.isConstant() && l.getConstantValue() == 0) {
			if (opStr.equals("+")) {
				exp.setTo(r);				
				return true; //r
			}
			if (opStr.equals("-")) {
				Expression neg = new Expression();
				neg.setOperation("negate");
				neg.setRightExpression(r);
				exp.setTo(neg);
				return true;// exp;
			}
			if (opStr.equals("*") || opStr.equals("/")) {
				exp.setTo(0);
				return true; // new Expression(0);
			}
			if (opStr.equals("^")) {
				exp.setTo(0);
				return true;
			}
		}
		// RIGHT is 0, LEFT is anything
		if (r.isConstant() && r.getConstantValue() == 0) {
			if (opStr.equals("+")) {
				exp.setTo(l);
				return true;// l;
			}
			if (opStr.equals("-")) {
				exp.setTo(l);
				return true;// l; // diff with l=0 case!
			}
			if (opStr.equals("*")) {
				exp.setTo(0);
				return true; // new Expression(0);
			}
			if (opStr.equals("/"))
				throw new IllegalArgumentException("zero divizion");
			if (opStr.equals("^")) {
				exp.setTo(l);
				return true;
			}
			
		}
		// l and r can be simplified. Update exp.
		exp.setLeftExpression(l);
		exp.setRightExpression(r);
		//return exp;
		return success;
	}
	protected static boolean removeOnes(Expression exp) {
		if (exp == null)
			return false;
		Expression l = exp.getLeftExpression();
		Expression r = exp.getRightExpression();
		boolean success = false;
		success |= removeOnes(l);
		success |= removeOnes(r);
		
		if (l == null || r == null)
			return false; 

		String opStr = exp.getMethodName();
		
		// left is 1, right is anything
		if (l.isConstant() && l.getConstantValue() == 1) {
			if (opStr.equals("*")) {
				exp.setTo(r);
				return true;
			}
			if (opStr.equals("/")) {
				// 1_x goes here
				return false; //!				
			}
			if (opStr.equals("^")) {
				exp.setTo(1);
				return true;
			}
			
		}
		// right is 1, left is anything
		if (r.isConstant() && r.getConstantValue() == 1) {
			if (opStr.equals("*") || opStr.equals("/") || opStr.equals("^")) {
				exp.setTo(l);
				return true;
			}
		}
		// l and r can be simplified. Update exp.
		exp.setLeftExpression(l);
		exp.setRightExpression(r);
		//return exp;
		return success;
	}
	
	private static boolean isDirectlyCommutative(String opStr1, String opStr2) {
		//here we use +, -, ...,  not sUb, mUv, Div, adD 
		if (opStr1.contains("u") || opStr1.contains("d"))
			throw new IllegalArgumentException();
		// x * (y / z) is OK, 
		// BUT!!  
		// x / (y * z) != x * z / y
		if (opStr1.equals(opStr2)) {
			if (opStr1.equals("+") || opStr1.equals("-")
			 || opStr1.equals("*") || opStr1.equals("/"))
				return true;
		}
		if (opStr1.equals("+")) 
			if (opStr2.equals("-") )
				return true;
		if (opStr1.equals("*")) 
			if (opStr2.equals("/") )
				return true;
		return false;
	}
	private static boolean isReversableCommutative(String opStr1, String opStr2) {
		// x / (y * z) != x * z / y
		if (opStr1.equals("-")) 
			if (opStr2.equals("+") )
				return true;
		if (opStr1.equals("/")) 
			if (opStr2.equals("*") )
				return true;
		return false;
	}
}
