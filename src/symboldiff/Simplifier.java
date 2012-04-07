package symboldiff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import sun.org.mozilla.javascript.internal.ast.NewExpression;

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
		final int maxTurns = 100;
		int turns = 0;
		
		do {
			String prevToString = simplified.toString();
			/*wasExpChanged = false;*/
								 removeDoubleNegates(simplified);
								 //while (makeTreeDipperNotWide(simplified)) {};
//			/*wasExpChanged |= */removeNegateForConsts(simplified); // also helps fold() be more effective
								 
                                 removeNegateInAddOrSub(simplified); // helps fold() and also cleans after fold()
                                 removeNegateInMulOrDiv(simplified);
								 removeNegativeInPower(simplified);
								 removeNegateForConsts(simplified);
								 promoteNegateFromVariablesToExpressions(simplified);
								 calcFunctionsFromConsts(simplified);
								 
			/*wasExpChanged |= */fold(simplified);
								 //foldMul_AddSub(); // 2x+x => 3x it was moved inside fold
			
			/*wasExpChanged |= */reduceConstants(simplified); // TODO: probably now fold() do all the job 
			/*wasExpChanged |= */removeZeros(simplified); // remove meaningless operation and also cleans after fold()
			/*wasExpChanged |= */removeOnes(simplified);  // ---//---
                                 removeNegateInAddOrSub(simplified); // helps fold() and also cleans after fold()
                                 removeNegateInMulOrDiv(simplified); // helps fold() and also cleans after fold()
								 removeNegativeInPower(simplified); // x*y^-z => x/y^z, x/y^-z => x*y^z. cleans after fold()
								 removeNegateForConsts(simplified); // and now cleans after fold
								 //while (makeTreeDipperNotWide(simplified)) {};
								 
								 

	
			/*wasExpChanged |=*/ sort(simplified); // sort does not affect other optimizations. so for them it isn't important
													// if it changed something. but fold() changes the order so if sort() affect
													// wasExpChanged this two functions can run endless just resorting the expression
			wasExpChanged = !prevToString.equals(simplified.toString());
		} while (wasExpChanged && ++turns < maxTurns);
		simplified.setVariablesList(); // some part of expression were changed, created new sub-expressions and so on.
										// we have to re-set the variables list.
		assert(turns < maxTurns); // just to inform us about such case -- it could be a problem
//		if (turns => maxTurns)
//			throw new SimplificationStoped("Simplification was stoped after " + turns + " iterations.");
	}

	/*
	 * -2+x = > x-2; x-(-2) = > x+2
	 */
	protected static boolean removeNegateInAddOrSub(Expression exp) {
		if (exp == null)
			return false;

		Expression l = exp.getLeftExpression();
		Expression r = exp.getRightExpression();

		boolean changed = removeNegateInAddOrSub(l);
		changed |= removeNegateInAddOrSub(r);

		if (!(exp.isAdd() || exp.isSub()))
			return false;
		
		// first case
		// part a
		if (l.isConstant() && l.getConstantValue() < 0) {
			Expression newVal = Expression.newConstant(-l.getConstantValue());
			Expression newLeft = Expression.newExpression(null, newVal, "negate"); // -c => (-c)
			exp.setLeftExpression(newLeft); // this will continued in second case
		} 
		// first case part b
		if (r.isConstant() && r.getConstantValue() < 0) {
			Expression newVal = Expression.newConstant(-r.getConstantValue());
			Expression newRight = Expression.newExpression(null, newVal, "negate");
			exp.setRightExpression(newRight); // this will continued in second case
		}
		
		// second case (actually it holds the first cases as well)
		l = exp.getLeftExpression(); // left or right children could be changed in case 1
		r = exp.getRightExpression(); // so we need to update them!
		if (l.isNegate() || r.isNegate()) {
			if (exp.isAdd()) { 
				if (l.isNegate() && r.isNegate()) { // (-a) + (-b) => -(a+b)
					exp.setOperation("negate"); exp.setLeftExpression(null);
					exp.setRightExpression(Expression.newExpression(l.getRightExpression(), r.getRightExpression(), "+"));
					return true; // otherwise exp.setOperation("+") will mess up everything
				} 
				if (l.isNegate()) { // if _left_ is negate we will swap them
					exp.setLeftExpression(r);
					exp.setRightExpression(l);
				} 
				// this part works for both cases: 
				//	2) if r is negate we just get rid of it, 
				//	1) if l was negate we have already swapped l with r and now negate is in "r"
				// 	in both cases we need to get rid from negate and keep its value only.
				exp.setRightExpression(exp.getRightExpression().getRightExpression());
				exp.setOperation("-");
			} else { // "-"
				if (l.isNegate() && r.isNegate()) { // (-a) - (-b) => b - a (swap and remove both negates)
					Expression positiveR = r.getRightExpression();
					Expression positiveL = l.getRightExpression();
					exp.setLeftExpression(positiveR);
					exp.setRightExpression(positiveL);
				} else if (l.isNegate()) { // (-a) - b => -(a+b) 
					exp.setOperation("negate"); exp.setLeftExpression(null);
					exp.setRightExpression(Expression.newExpression(l.getRightExpression(), r, "+"));
				} else { // a - (-b) => a + b
					exp.setRightExpression(r.getRightExpression());
					exp.setOperation("+");
				}
			}
			return true;
		}
		return changed;
	}
	/*
	 * 2*(-x)=>-1*2*x; -x/x = > -1*x/x
	 */
	protected static boolean removeNegateInMulOrDiv(Expression exp) {
		if (exp == null)
			return false;

		Expression l = exp.getLeftExpression();
		Expression r = exp.getRightExpression();

		boolean changed = removeNegateInMulOrDiv(l);
		changed |= removeNegateInMulOrDiv(r);

		if (!(exp.isMul() || exp.isDiv()))
			return false;

		l = exp.getLeftExpression(); // left or right children could be changed during recursion
		r = exp.getRightExpression(); // so we need to update them!
		
		boolean negR = r.isNegate();
		boolean negL = l.isNegate();
		if (negR && negL) {
			exp.setLeftExpression (l.getRightExpression());
			exp.setRightExpression(r.getRightExpression());
			return true;
		}
		Expression negate = null, other = null;
		if (negL) {
			negate = l;
			other  = r;
		} else if ( negR ) { 
			negate = r;
			other  = l;
		} else
			return changed;

		Expression newL, newR;
		Expression newOther, newNeg;
		newNeg = negate.getRightExpression(); // in all cases we will get rig from negate.
		if (other.isConstant()) {
			newOther = Expression.newConstant(-other.getConstantValue());
		} else { // 'other' is not a constant
			Expression newExp = Expression.newExpression(null, null, exp.getOperation());
			exp.setTo(Expression.newExpression(null, newExp, "negate"));
			newOther = other;
			// now we need to attach new values to the right positions
			// the following code is uniform for both cases and to use it in this case we just
			// reassign a pointer from exp to newExp which actually needs the arguments. 
			// previous clause of 'if' works with exp directly. 
			exp = newExp;
		}
		// determining where to attach this new values. it is important in 'div' case
		if (other == r) { 
			newR = newOther;
			newL = newNeg;
		} else {
			newL = newOther;
			newR = newNeg;
		}
		exp.setLeftExpression(newL);
		exp.setRightExpression(newR); 
		return true;
	}

	
	/*
	 *  x*y^-z => x/y^z; x/y^-z => x*y^z.
	 *  Looks for mul/div followed by power with negative value.
	 *  Don't do anything with + or -
	 */
	protected static boolean removeNegativeInPower(Expression exp) {
		if (exp == null)
			return false;

		boolean changed = removeNegativeInPower(exp.getLeftExpression());
		changed |= removeNegativeInPower(exp.getRightExpression());

		Expression l = exp.getLeftExpression();
		Expression r = exp.getRightExpression();
		
		// case 1 -- negative in mantissa
		if (exp.isPow() && l.isNegate()) {
			exp.setLeftExpression( l.getRightExpression() );
			if (r.isConstant() && r.getConstantValue()%2 == 0)
				return true;
			Expression newExp = Expression.newExpression(exp.getLeftExpression(), 
														exp.getRightExpression(), 
														exp.getOperation());
			exp.setTo(Expression.newExpression(null, newExp, "negate"));
			return true;
		}
		// case 2 -- negative in EXPONENT
		if(!(exp.isMul() || exp.isDiv()))
			return changed;
		Expression pow = null;
		if(l.isPow()) {
			pow = l;
		} else if(r.isPow()) {
			pow = r;
		}
		if (pow == null)
			return changed;
		
		Expression otherOperand = l.isPow() ? r : l;

		// get rid from "-" in exponent
		Expression newPowerRight = null;
		if (pow.getRightExpression().isConstant() ) { //x^double
			double val = pow.getRightExpression().getConstantValue();
			if(val > 0) 	// we are interested only in x^-double
				return changed;
			assert(pow.getRightExpression().getConstantValue() == val);
			newPowerRight = Expression.newConstant(-val);
		} else if ( pow.getRightExpression().isNegate() ) {
			newPowerRight = pow.getRightExpression().getRightExpression();
			//                            ^^^ negate           
		} else
			return changed;
		
		if (newPowerRight.isConstant() && newPowerRight.getConstantValue() == 1)
			pow.setTo(pow.getLeftExpression()); // get rid of power in case of ^1
		else
			pow.setRightExpression(newPowerRight);

		// set the expression
		exp.setLeftExpression(otherOperand);
		exp.setRightExpression(pow);
		if (exp.isMul()) {
			exp.setOperation("/");
		} else { // div
			assert(exp.isDiv());
			if (otherOperand == l) // can't check if pow == r here because pow can be changed if exponent is eq to 1.
				exp.setOperation("*");
			else {
				// pow^(-V) / other = 1 / other / pow^(+V) 
				//                                pow^(+V) is already set as right subexpression. change the left part
				Expression one  = Expression.newConstant(1);
				Expression newExp = Expression.newExpression(one, otherOperand, "/");
				exp.setLeftExpression(newExp);				
			}
		}
		return true;
	}
	protected static boolean removeDoubleNegates(Expression exp) {
		boolean optimized = false;
		if (exp == null)
			return false;
		optimized |= removeDoubleNegates(exp.getLeftExpression());
		optimized |= removeDoubleNegates(exp.getRightExpression());
		if ( exp.isNegate() && exp.getRightExpression().isNegate() ) {
			exp.setTo(exp.getRightExpression().getRightExpression()); // negate(negate(x))
			//        ^ first negate;   ^ second negate;     ^ x
			optimized = true;
		}        
		return optimized;
	}
	/*
	 * negate(2) => -2.0
	 * negate(2*x) => -2*x
	 * this opt. helps fold() because fold looks for chains of the same operation
	 * and negate breaks them.
	 */
	protected static boolean removeNegateForConsts(Expression exp) {
		boolean optimized = false;
		if (exp == null)
			return false;
		optimized |= removeNegateForConsts(exp.getLeftExpression());
		optimized |= removeNegateForConsts(exp.getRightExpression());
		if ( exp.isNegate() ) {
			Expression op = exp.getRightExpression();
			if (op.isConstant()) { // -2
				exp.setTo(- op.getConstantValue() );
				optimized = true;
			}
			// for + and - we have to change sign for both operands
			if ( (op.isAdd() || op.isSub())
					&& canChangeSign(op) ) {
						changeSign(op);
						exp.setTo(op); // exp is negate. get rid of it
						optimized = true;
			}
			// for * and / it is enough to change sign only for one operand
			if ( op.isMul() || op.isDiv() ) { 
				if( canChangeSign(op.getLeftExpression()) ) {
					changeSign(op.getLeftExpression());
					exp.setTo(op); // remove negate
					optimized = true;
				} else if( canChangeSign(op.getRightExpression()) ) {
					changeSign(op.getRightExpression());
					exp.setTo(op); // remove negate
					optimized = true;
				}
			}
		}
		return optimized;
	}
	/*
	 * negate(x)/x => negate(x/x)
	 * this opt. helps fold() because fold looks for chains of the same operation
	 * and negate breaks them.
	 */
	protected static boolean promoteNegateFromVariablesToExpressions(Expression exp) {
		boolean optimized = false;
		if (exp == null)
			return false;
		optimized |= promoteNegateFromVariablesToExpressions(exp.getLeftExpression());
		optimized |= promoteNegateFromVariablesToExpressions(exp.getRightExpression());
		
		Expression l = exp.getLeftExpression();
		Expression r = exp.getRightExpression();
		
		if ( l == null || r == null) 
			return optimized;
		
		if ( !(l.isNegate() || r.isNegate() )) 
			return optimized;
		
		if (exp.isMul() || exp.isDiv()) {
			Expression newExp = Expression.newExpression(l, r, exp.getOperation());
			if (l.isNegate())
				newExp.setLeftExpression(l.getRightExpression());
			else
				newExp.setRightExpression(r.getRightExpression());
			exp.setTo(Expression.newExpression(null, newExp, "negate"));
			return true;
		}
		if (exp.isAdd() || exp.isSub()) {
			return true;
		}
		if (exp.isPow() || l.isNegate()) {
			return true;
		}

		return false;
	}
	
	/*
	 * Auxiliary function; is used in @removeNegateForConsts@. Checks if all
	 * tree consists only from consts. If so returns true because in this case
	 * negate could be removed and substituted with negated constants. 
	 */
	protected static boolean canChangeSign(Expression exp) {
		if (exp == null)
			return true;
		if (exp.isVariable())
			return false;
		return (canChangeSign(exp.getLeftExpression()) && canChangeSign(exp.getRightExpression()));		
	}
	/*
	 * auxiliary function; is used by @removeNegateForConsts@.
	 * change signs for all constants @exp@
	 */
	private static void changeSign(Expression exp) {
		if (exp == null)
			return;
		assert(!exp.isVariable());
		if (exp.isConstant())
			exp.setTo(- exp.getConstantValue() );
		else {
			changeSign(exp.getLeftExpression());
			changeSign(exp.getRightExpression());
		}
	}

	/*
	 * sort expression tree alphabetically (when possible)
	 * Also this is important for finding sub-trees  
	 */

	protected static boolean sort(Expression exp) {
		if (exp == null)
			return false;
		Expression l = exp.getLeftExpression();
		Expression r = exp.getRightExpression();
		boolean changed = sort(l);
		changed |= sort(r);

		if ( (exp.isAdd() || exp.isMul()) || 
				(exp.isSub() && (l.isNegate() || (l.isConstant() && l.getConstantValue() < 0)) )) { // (-x) - y == (-y) - x
			String rStr = r.toString(); 
			String lStr = l.toString();
			if (exp.isSub() && l.isNegate())
				lStr=lStr.substring(1); // no "-" for comparison in this case. because in this case both of operands looks like having "-": -x-y
			
			rStr = rStr.replace("(", "").replace(")", ""); // remove all brackets because they are smaller than anything else
			lStr = lStr.replace("(", "").replace(")", "");
			
			if (rStr.compareTo(lStr) < 0) { // l is lexicographically bigger than r
															// like l=z and l=2. swap them.
				Expression newLeft = r;  // in usual + or * case just swap them
				Expression newRight = l;
				if (exp.isSub()) { // in minus sub someting (-x - y) case change the sign as well
									// 	(-x) - y == (-y) - x
					newLeft = Expression.newExpression(null, r, "negate"); // (-y)
					if ( l.isNegate() ) {
						newRight= l.getRightExpression(); //x
					} else { // (l.isConstant() && l.getConstantValue() < 0)
						assert(l.isConstant() && l.getConstantValue() < 0);
						newRight= Expression.newConstant(-l.getConstantValue()); // x
					}
				}
				exp.setLeftExpression(newLeft);
				exp.setRightExpression(newRight);
				changed = true;
			}
		}
		return changed;
	}
	
	/* 
	 *                         +
	 *        +       =>     +   3 
	 *     +     +         +   4
	 *    1 2   4 3       1 2
	 *    
	 *    fold() depends on this optimization
	 */
	protected static boolean makeTreeDipperNotWide(Expression exp) {
		if (exp == null)
			return false;
		Expression l = exp.getLeftExpression();
		Expression r = exp.getRightExpression();
		boolean success = false;
		success |= makeTreeDipperNotWide(l);
		success |= makeTreeDipperNotWide(r);
		
//		l = exp.getLeftExpression();
//		r = exp.getRightExpression();
		
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
				
				// see the diagram above
				if(isDirectlyCommutative(thisOperation, rOperation) ||
				   isReversableCommutative(thisOperation, rOperation)) {
					changed = true;
					Expression a = r.getLeftExpression();
					//Expression b = r.getRightExpression();
					node.setLeftExpression(r); // r
					r.setLeftExpression(l); // (...)
					// do nothing // b is already on its position
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
			//String opStr = exp.getMethodName();

			double newVal;
			double a = l.getConstantValue(), b = r.getConstantValue();
			if (exp.isAdd()) {
				newVal = a + b;
			} else if (exp.isSub()) {
				newVal = a - b;
			} else if (exp.isMul()) {
				newVal = a * b;
			} else if (exp.isDiv()) {
				newVal = a / b;
			} else if (exp.isPow()) {
				newVal = Math.pow(a, b);
			} else
				throw new IllegalArgumentException("Unknown operation");
			//return new Expression(newVal);
			exp.setTo(newVal);
			return true; 
			// else next block with setLeft/RightExpressions will be executed.
		}
		// exp can be the same, while left and right expression can be simplified: 1+2+x
		exp.setLeftExpression(l);
		exp.setRightExpression(r);
		return success;//exp;	
	}
	
	/*
	 * this simplification allows to get rid from meaningless zeroes:
	 * 0 + x = x; 0 * x = 0; ...
	 */
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

		//String opStr = exp.getMethodName();
		
		// left is 0, right is anything
		if (l.isConstant() && l.getConstantValue() == 0) {
			if (exp.isAdd()) {
				exp.setTo(r);				
				return true; //r
			}
			if (exp.isSub()) {
				Expression neg = new Expression();
				neg.setOperation("negate");
				neg.setRightExpression(r);
				exp.setTo(neg);
				return true;// exp;
			}
			if (exp.isMul() || exp.isDiv()) {
				exp.setTo(0);
				return true; // new Expression(0);
			}
			if (exp.isPow()) {
				exp.setTo(0);
				return true;
			}
		}
		// RIGHT is 0, LEFT is anything
		if (r.isConstant() && r.getConstantValue() == 0) {
			if (exp.isAdd()) {
				exp.setTo(l);
				return true;// l;
			}
			if (exp.isSub()) {
				exp.setTo(l);
				return true;// l; // diff with l=0 case!
			}
			if (exp.isMul()) {
				exp.setTo(0);
				return true; // new Expression(0);
			}
			if (exp.isDiv())
				throw new IllegalArgumentException("zero divizion");
			if (exp.isPow()) {
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
	/*
	 * this simplification allows getting rid from meaningless ones:
	 * 1 * x = x; x / 1 = x; ...
	 */
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
		
		if (!exp.isOperation())
			return false;

		// -1 case
		if(exp.isMul() || exp.isDiv()) {
			if (l.isConstant() && l.getConstantValue() == -1) {
				exp.setTo(Expression.newExpression(null, r, "negate"));
				return true;
			}
			if (r.isConstant() && r.getConstantValue() == -1) {
				exp.setTo(Expression.newExpression(null, l, "negate"));
				return true;
			}
		}		
		
		// left is 1, right is anything
		if (l.isConstant() && l.getConstantValue() == 1) {
			if (exp.isMul()) {
				exp.setTo(r);
				return true;
			}
			if (exp.isDiv()) {
				// TODO: FIXME: 1_x goes here
				return false; //!				
			}
			if (exp.isPow()) {
				exp.setTo(1);
				return true;
			}
		}
		// right is 1, left is anything
		if (r.isConstant() && r.getConstantValue() == 1) {
			if (exp.isMul() || exp.isDiv() || exp.isPow()) {
				exp.setTo(l);
				return true;
			}
		}
		// l and r can be simplified as well. Update exp.
		exp.setLeftExpression(l);
		exp.setRightExpression(r);
		return success;
	}
	/*
	 * this optimization folds arithmetic expressions:
	 * 2*x*2 => 4x; x*y*x = y*x^2; x+x+x=3x; ...
	 */
	protected static boolean fold(Expression exp) {
		if (exp == null)
			return false;
		boolean optimized = false;
		
		optimized |= fold( exp.getLeftExpression() );
		optimized |= fold( exp.getRightExpression());
		
		String thisNodeOp = exp.getOperation();
		if (exp.isAdd() || exp.isMul() || exp.isDiv() || exp.isSub()) { 
			// not BinaryOp because of "^"
			
			LinkedList<ExpAndOp> operands = new LinkedList<>(); 
			getOperandsFromAllChildrenWithThisOrComplimentOp(exp, thisNodeOp, thisNodeOp, 0, operands);
			int size = operands.size();
			if (size > 1) {
				operands = foldSimilarOperations(operands, optimized); // try to collapse operands of the same types
				operands = foldMul_AddSub(operands, optimized); // 2x+x=3x
				//if (!optimized) 
				size = operands.size(); // size could change during calculations
				assert(size > 0);
				if (size == 1) {
					ExpAndOp e_o = operands.getFirst();
					Expression operand = e_o.e;
					// calculate() returned only one value. this could be a variable or constant OR expression like x^2
					// and it substitutes the original operation
					exp.setTo(operand);
				} else {
					// we are going to add all operands found by @getOperandsFromAllChildrenWithThisOp@
					// to the expression tree instead of this operation arguments.
					// before adding sort operands in a way that the order always will be the same
					Collections.sort(operands, alphabeticallySorter);
					
					// here we are going to attach all the arguments to the expression tree
					// if there are more arguments than for one node we will create extra nodes.
					assert(size >= 2);
					Expression lastNode = Expression.newExpression(null, null, null); // create new empty node

					// to begin with we are trying to find an operand that is suitable as a left one
					// it means it has "positive" (commutative) operation: "*", not "\".
					String operation = operands.getFirst().op;
					Expression suitableForLeft = getLeftOperandForThisOp(operation, operands); // finds appropriate operands or creates a dummy constant
					lastNode.setLeftExpression(suitableForLeft);

					ExpAndOp e_o = operands.poll();
					lastNode.setOperation(e_o.op);      // set right operation -- not "operation"! correct op is from this expression
					lastNode.setRightExpression(e_o.e); // attach next argument

					 // if there are more operands we will add them in this loop
					while(operands.size() > 0) {
						e_o = operands.poll();
						// create next node, set right operation and attache previous node
						Expression nextNode = Expression.newExpression(lastNode, null, e_o.op); 
						nextNode.setRightExpression(e_o.e); // attach the operand
						lastNode = nextNode;
					}
					
					exp.setTo(lastNode);					
				}
//				optimized = true IF CALCULATE CALCULATED SOMETHING OR IF size CHANGED;
			}
		}
		return optimized;
	}
//// { foldMul_AddSub : 2x+x=3x, 2x-x=x; x/2-x/2 = 0	
	static LinkedList<ExpAndOp> foldMul_AddSub(LinkedList<ExpAndOp> operands, boolean optimized) {
		if (operands.size() == 0)
			return operands;
		String rootOperationInThisExpressionChain = operands.getFirst().op;
		if (!("+".equals(rootOperationInThisExpressionChain) || "-".equals(rootOperationInThisExpressionChain) ))
			return operands;

		LinkedList<ExpAndOp> folded = new LinkedList<>();

		for (ListIterator<ExpAndOp> it = operands.listIterator(); it.hasNext(); ) {
			ExpAndOp e_o = it.next();
			ExpAndOp foldedExp = foldEqualsAccurateWithinMultipier(e_o, it, operands); 
						// ^^ finds expression in the list that is equal to *iterator accurate within const., f.e: 2xy & xy
						// folds them and remove them from the list
			if (foldedExp != null) {
				folded.add(foldedExp);
				it = operands.listIterator();
			}
		}
		if (folded.size() != 0) {
			folded.addAll(operands);
			return folded;
		} else 
			return operands; // nothing has been changed		
	}
	/*
	 * Searches in @allOperands@ starting from @iteratorToNext@ for anything 
	 * similar to @thisE_O@. If such expression or variable is founds, it will
	 * fold them and remove that similar expression or variable from the 
	 * @allOperands@ list. Returns folded expression or null if nothing can be folded     
	 */
	static ExpAndOp foldEqualsAccurateWithinMultipier(ExpAndOp thisE_O,
			ListIterator<ExpAndOp> iterator, LinkedList<ExpAndOp> allOperands) {
		
//		LinkedList<ExpAndOp> toRemove = new LinkedList<>();

		// 1. can thisExp be folded in theory?
		Expression thisExp = thisE_O.e;
		if ( thisExp.isVariable() || thisExp.isMul() /*|| thisExp.isDiv()*/ ) {
			Map<Expression, Expression> partsCandidate = new HashMap<>();
			Map<Expression, Expression> partsThis = new HashMap<>();
			splitByMuls(thisExp, partsThis, null);
			assert(partsThis.size() > 0);
			// 10. search from iteratorToNext till the end.
			// the following could be optimized!
			ListIterator<ExpAndOp> tailIterator = allOperands.listIterator(iterator.nextIndex());
			// ^^
			while (tailIterator.hasNext()) {
				ExpAndOp candidateE_O = tailIterator.next();
				partsCandidate.clear();
				splitByMuls(candidateE_O.e, partsCandidate, null);
				Map<String, Expression> commonMultipliers = commonMultipliers(partsThis, partsCandidate);
				if (commonMultipliers == null)
					continue;
				
//				toRemove.add(candidateE_O,)
				boolean removed = allOperands.remove(thisE_O);
				assert(removed);
				removed = allOperands.remove(candidateE_O);
				assert(removed);
				
				Expression common = commonMultipliers.get("common");
				assert (common != null);
				Expression p1 = commonMultipliers.get("part_1");
				assert (p1 != null);
				Expression p2 = commonMultipliers.get("part_2");
				assert (p2 != null);
				
				Expression twoParts;
				String resultOperation = "+";
				if (thisE_O.op.equals("+")) {
					if (candidateE_O.op.equals("+"))
						twoParts = Expression.newExpression(p1, p2, "+");
					else {
						assert (candidateE_O.op.equals("-"));
						twoParts = Expression.newExpression(p1, p2, "-");
					}
				} else {
					assert (thisE_O.op.equals("-"));
					if (candidateE_O.op.equals("+"))
						twoParts = Expression.newExpression(p2, p1, "-");
					else {
						assert (candidateE_O.op.equals("-"));
						resultOperation = "-";
						twoParts = Expression.newExpression(p1, p2, "+");
					}					
				}
				Expression simplified = Expression.newExpression(common, twoParts, "*");
				return new ExpAndOp(simplified, resultOperation);				
			}
			
		} // thisExp can't be folded
		// or it can but wasn't during that loop
		return null;
	}
/*	
	private static boolean isDigit(String s, int pos) {
		char c = s.charAt(pos); 
		assert(c < 128); // test that there are only ascii chars.
		if ( (c >= '0' && c <= '9') || c == '.' || c == '-')
			return true;
		return false;
	}
	private static boolean isEqualsAccurateWithinConstants(ExpAndOp eo1, ExpAndOp eo2) {
		String s1 = eo1.e.toString();
		String s2 = eo2.e.toString();
		
		// the variables can has only ASCII symbols. This is checked in Expression.init()
		int i1, i2;
		for (i1 = 0, i2 = 0; i1 < s1.length() && i2 < s2.length(); i1++, i2++) {
			if ( s1.charAt(i1) != s2.charAt(i2) ) {
				if (isDigit(s1, i1)) {
					i1++;
					continue;
				}
				if ( isDigit(s2, i2)) {
					i2++;					
					continue;
				} else
					return false;				
			}
		}
		while ( i1 < s1.length() ) {
			if (isDigit(s1, i1))
				i1++;
			else
				return false;
		}
		while ( i2 < s2.length() ) {
			if (isDigit(s2, i2))
				i2++;
			else
				return false;
		}
		return true;
	}
	private static boolean isEqualsAccurateWithinMultiplier(ExpAndOp eo1, ExpAndOp eo2) {
		Expression e1 = eo1.e, e2 = eo2.e;//, smallerExp = e2;
		Expression linkingOp = findSubexpression(e1, e2);
		if (linkingOp == null) {
			linkingOp = findSubexpression(e2, e1);
			if (linkingOp == null)
				return false;
			else {
//				smallerExp = e1;
			}
		}
		if (linkingOp.getOperation().equals("expressionsAreEqual")) // magic
			return true;
		if (linkingOp.isMul())
			return true;
		return false;		
	}
*/	
	static void splitByMuls(Expression e, Map<Expression, Expression> subexpressions, Expression previousMultiplier) {
		if (e == null)
			return;
		Expression leftMulByPrev, rightMulByPrev;
		if (previousMultiplier == null) {
			previousMultiplier = Expression.newConstant(1);
			leftMulByPrev  = e.getLeftExpression();
			rightMulByPrev = e.getRightExpression();
		} else {
			leftMulByPrev  = Expression.newExpression(e.getLeftExpression(), previousMultiplier, "*");
			rightMulByPrev = Expression.newExpression(e.getRightExpression(), previousMultiplier, "*");
		}
		
		subexpressions.put(e, previousMultiplier);

		if (e.isMul()) {
			subexpressions.put(leftMulByPrev,  e.getRightExpression());
			subexpressions.put(rightMulByPrev, e.getLeftExpression ());
			// we need both parts of the expression as keys
			subexpressions.put(e.getRightExpression(), leftMulByPrev );
			subexpressions.put(e.getLeftExpression (), rightMulByPrev);			
				
			splitByMuls(e.getLeftExpression(),  subexpressions, rightMulByPrev);
			splitByMuls(e.getRightExpression(), subexpressions, leftMulByPrev );
		}
	}
	private static Map<String, Expression> commonMultipliers(Map<Expression, Expression> one, Map<Expression, Expression> two) {
		Map<String, Expression> result = null;
		if (two.size() == 0 || one.size() == 0)
			return null;
		List<Expression> keys=new ArrayList<>(one.keySet());
		Collections.sort(keys, new Comparator<Expression>() {
			@Override
			public int compare(Expression e1, Expression e2) {
				// actually this comparator is really bad one. It returns 0 even if e1 and e2 are not equal!
				// but it is OK for our local purposes.
				int l1 = e1.length(), l2 = e2.length();
				if (l1 < l2)
					return -1;
				else if (l2 < l1)
					return 1;
				return 0;
			}
		});
		for(Expression e : keys)
			if (two.containsKey(e)) {
				result = new HashMap<>();
				result.put("common", e);
				result.put("part_1", one.get(e));
				result.put("part_2", two.get(e));
			}
		return result;				
	}
/*
	private static Expression findSubexpression(Expression big, Expression small) {
		Expression prevNode = Expression.newExpression(null, null, "expressionsAreEqual"); 
		// ^^ magic means that they are equal (no prev.node in this case)
		return findSubexpression(big, small, prevNode);
	}
	private static Expression findSubexpression(Expression big, Expression small, Expression prevNode) {
		if (areTreesEqual(big, small))
			return prevNode;
		prevNode = big;
		if (areTreesEqual(big.getLeftExpression(), small))
			return prevNode;
// tiny optimization
//		if (big.getRightExpression() == null)
//			return null;
		if (areTreesEqual(big.getRightExpression(), small))
			return prevNode;	
		return null;
	}
	private static boolean areTreesEqual(Expression t1, Expression t2) {
		if (t1 == null) {
			if (t2 == null)
				return true;
			return false;
		}
		if (t2 == null)
			return false; // t1 is not null here
		
		if ( t1.getOperation().equals(t2.getOperation()) )
			return areTreesEqual(t1.getLeftExpression(),  t2.getLeftExpression()) && 
				   areTreesEqual(t1.getRightExpression(), t2.getRightExpression() );
		return false;
	}
*/	
//// }	foldMul_AddSub : 2x+x=3x, 2x-x=x; x/2-x/2 = 0


	/*
	 * from list of operands finds one that has a "positive" operation (+, *), so it can be
	 * used as left-side operand in any operation: f.e. * or /.
	 * creates a dummy constant if there is no such entry. 
	 */
	private static Expression getLeftOperandForThisOp(String op, LinkedList<ExpAndOp> operands) {
		String lookingFor = getComutativeOperationThisType(op);
		for (Iterator<ExpAndOp> it = operands.iterator(); it.hasNext(); ) {
			ExpAndOp e_o = it.next();
			if (lookingFor.equals(e_o.op)) {
				it.remove();
				return e_o.e;
			}
		}
		return Expression.newConstant(getNeutralElemant(op));
	}
	// x+x=2x, x*x=x^2... Can't optimize 2x+x
	private static LinkedList<ExpAndOp> foldSimilarOperations(LinkedList<ExpAndOp> operands, boolean optimized) {
		LinkedList<ExpAndOp> folded = new LinkedList<>();
		if (operands.size() == 0)
			return folded;
		String typeOfChainOp = getComutativeOperationThisType(operands.getFirst().op);	// operands can have different operations: '+' and '-' OR '*' and '/'
		double constant = getNeutralElemant(typeOfChainOp);	// neutral element is the same for them
		for (Iterator<ExpAndOp> it = operands.iterator(); it.hasNext(); ) {
			ExpAndOp e_o = it.next();
			Expression e = e_o.e;
			if (e.isConstant()) { // constants from "our" operation
				constant = foldConsts(constant, e.getConstantValue(), e_o.op);
				it.remove(); // it is already processed. remove from further processing 
			} else if (e.isOperation()) { // some other operations as arguments to "our" operation
				folded.add(e_o); // copy them as is
				it.remove(); // and remove from the list of not processed operands
			}
		}
		if (operands.size() == 0 || constant != getNeutralElemant(typeOfChainOp) ) { 
			//    |                 ^^^ Add neutral element ONLY if there were some constants. otherwise x*x=>1*x^2 
			//    \___ BUT if there were _only_ constants that give Neutral in result (f.e. 0+0)
			//	           we have to return at least this neutral
			folded.add(new ExpAndOp(Expression.newConstant(constant), typeOfChainOp)); // add folded constant
			optimized = true;
		}
		
		if (operands.size() == 0) // in initial expression were no variables, only constants and expressions
			return folded;

		// now only variables are in the list
		// we will sort them alphabetically and then fold
		Collections.sort(operands, alphabeticallySorter);
		int cnt = 0;  
		boolean processedButNotAdded = false;
		String prevVar = operands.getFirst().e.getOperation();
		for (ExpAndOp e_o : operands) {
			assert(e_o.e.isVariable());
			if ( e_o.e.getOperation().equals(prevVar) ) { // the same variable occurs several times
				processedButNotAdded = true;
				if (e_o.op.equals(typeOfChainOp) )
					cnt++;  
				else {
					assert(e_o.op.equals("-") || e_o.op.equals("/"));
					cnt--;
				}
			} else { 
				// 1. finish with prevVar
				ExpAndOp newEO = new ExpAndOp(null, typeOfChainOp);
				if (cnt < 0 && typeOfChainOp.equals("*")) {
					cnt = -cnt;
					newEO.op = "/";
				}
				Expression newExp = foldExpression(prevVar, cnt, newEO.op, optimized);
				newEO.e = newExp;
				folded.add(newEO);
				
				// 2. process new variable (sequence)
				processedButNotAdded = false; // actual for sequences only. this is not a sequence yet.
				cnt = 1; // set counter to ONE because new "e" was already encountered
				if (!e_o.op.equals(typeOfChainOp)){
					// nvp 3/11/12: *x, /y, /z: y's cnt is 1 (due to "cnt = 1; // set counter to ONE because new "e" was already encountered")
					//              so it will be x*y.
					cnt = -1; // so if it is not "positive" op set cnt to -1.
				}

			} /*else { // cnt == 1 but prevVar != e.getOperation() :  do not forget to add such single variables
				folded.add(e);
			}*/
			prevVar = e_o.e.getOperation();
		}
		if (processedButNotAdded) { // do not forget to use computed counter 
			Expression newExp = foldExpression(prevVar, cnt, typeOfChainOp, optimized);
			folded.add(new ExpAndOp(newExp, typeOfChainOp));		
		} else { // cnt == 0 at the end of this cycle only if (e.getOperation() != prevVar) => prevVar was 
					// added to folded but we have to add this new "e" too
			folded.add(operands.getLast());			
		}
		return folded;
	}
	
	/*
	 * fold sequence of variables with the same operation. 
	 * f.e.: {x0, 3, "*"} means x0 is multiplied 3 times and becomes "x0^3";   
	 * {x1, 4, "+"} => x1*4, ...  
	 */
	private static Expression foldExpression(String var, int cnt, String opType, boolean optimized) {
		if (cnt == 0)
			return Expression.newConstant(getNeutralElemant(opType));
		//optimized = false; // DO NOT change initial value!
		Expression variable = Expression.newExpression(null, null, var);
		if (cnt == 1) { // actually nothing to do with this particular variable
			return variable; // otherwise x => x^1, or 1*x depending on "op". 
		}
		// convert int to Expressions
		Expression constant = Expression.newConstant(cnt);
		// disregard of what the "op" is left is our var, right is our const 
		Expression folded = Expression.newExpression(variable, constant, opType);
		switch (opType) {
		case "+":
			folded.setOperation("*");
			optimized = true;
			break;
		case "*":
			folded.setOperation("^");
			optimized = true;
			break;
		default:
			throw new IllegalArgumentException("Unsupported operation: " + opType);
		}
		assert(folded.getLeftExpression() == variable); // what if setOperation will be rewritten
		assert(folded.getRightExpression() == constant); // to drop the children...
		return folded;
	}

	/*
	 * computes a result from applying arithmetic operation @op@ for @var1@ and @var2@
	 * in other words returns val1 "operation" val2.
	 */
	private static double foldConsts(double val1, double val2, String op) {
		switch (op) {
		case "+":
			return val1+val2;
		case "-":
			return val1-val2;
		case "*":
			return val1*val2;
		case "/":
			return val1/val2;
		case "^":
			return Math.pow(val1,val2);
		default:
			throw new IllegalArgumentException("Unsupported operation: " + op);
		}
	}

	private static double getNeutralElemant(String op) {
		if (op.equals("+") || op.equals("-")) 
			return 0;
		if (op.equals("*") || op.equals("/"))
			return 1;
		else throw new RuntimeException(); // FIXME: Do something like "Turn off the simplification"!
	}

	/*
	 * rootOp - initial operation from which we've started the chain.
	 * curOp  - operation, effective for this expression.
	 * rootOp could be '+' and curOp '-'
	 */
	/*private*/ static void getOperandsFromAllChildrenWithThisOrComplimentOp(Expression exp, 
															String rootOp, String curOp,
															int subOrDivCnt, 
															List<ExpAndOp> mulDivOperands) {
		if(exp == null)
			return;

		// each node could be an operation or a constant or variable
		// if this is an operation and it is "our" operation
		// then process its children
		String thisOp = exp.getOperation();
		if(exp.isBinaryOperation() &&  // binary because of sin(sin(x)) 
				( isTheSameOp(rootOp, thisOp) || isComplimentary(rootOp, thisOp) )) { // here we check if it's "our" operation

			// if this is "our" type we process its children  
			Expression l = exp.getLeftExpression();
			Expression r = exp.getRightExpression();
			
			String leftOp, rightOp = thisOp; // operations effective for children. 
														// with this op they will be added to the operands list
			String positiveOperation = getComutativeOperationThisType(rightOp);

			if(subOrDivCnt % 2 == 0)
				leftOp = positiveOperation;
			else
				leftOp = getOppositeOperation(positiveOperation);
			getOperandsFromAllChildrenWithThisOrComplimentOp(l, rootOp, leftOp, subOrDivCnt, mulDivOperands);

			if (!thisOp.equals(positiveOperation)) { // if the operation is a "negative"...
				subOrDivCnt++; // we count num or chained subs or divs to handle the operation for the children:
								            // a - (+b - (+c)) = a-b+c; a - (+b - (+c - (+d))) = a-b+c-d
//				if(subOrDivCnt % 2 == 0) {  //  (1) ^ (2) ^       ^ ^    (1) ^ (2) ^ (3) ^        ^ ^ ^
											// a/(b/(c/(d/e))) = a/b*c/d*e
											//  ^     ^           ^   ^
//					rightOp = getOppositeOperation(thisOp); 
//				} else { 		
					//leftOp = getOppositeOperation(thisOp);
//				}
			}
			if(subOrDivCnt % 2 != 0) // a-b
				rightOp = getOppositeOperation(positiveOperation);
			else                     // a-(b-c)
				rightOp = positiveOperation;
			getOperandsFromAllChildrenWithThisOrComplimentOp(r, rootOp, rightOp, subOrDivCnt, mulDivOperands);
			if (!thisOp.equals(positiveOperation)) {
				subOrDivCnt--; // leaved this branch. restore the value. 
			}			
		} else { 	// it is different kind of operation OR it's an argument
					// we just add it to the operands list.
			ExpAndOp op = new ExpAndOp(exp, curOp);
			mulDivOperands.add(op);
		}
		return;
	}
	private static String getOppositeOperation(String op) {
		switch (op) {
		case "+":
			return "-";
		case "-":
			return "+";
		case "*":
			return "/";
		case "/":
			return "*";
		default:
			throw new IllegalArgumentException("Unsupported operation: " + op);
		}
	}

	/*
	 * returns "positive" analog of an operation
	 */
	static String getComutativeOperationThisType(String op) {
		if (op.equals("-"))
			return "+";
		if (op.equals("/"))
			return "*";
		assert("+".equals(op) || "*".equals(op));
		return op;
	}
	private static boolean isTheSameOp(String op1, String op2) {
		return op1.equals(op2);
	}
	private static boolean isComplimentary(String op1, String op2) {
		if(op1.equals("+") && op2.equals("-"))
			return true;
		if(op2.equals("+") && op1.equals("-"))
			return true;
		if(op1.equals("*") && op2.equals("/"))
			return true;
		if(op2.equals("*") && op1.equals("/"))
			return true;
		return false;
	}
	
	// support class for fold(). contains some expression and operation -- what to do with this expression: '+' it or '-'
	protected static class ExpAndOp {
		public ExpAndOp(Expression exp, String curOp) {
			e = exp;
			op = curOp;
		}
		Expression e;
		String op;
		public String toString() { // for debug
			return op+e;
		}
	}
	
	
	
	private static boolean isDirectlyCommutative(String opStr1, String opStr2) {
		//here we use +, -, ...,  not sUb, mUv, Div, adD 
		assert (!opStr1.contains("u") && !opStr1.contains("d"));
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
	
	//////// sorter -- it is used in fold() and compute()
	private static class AlphabeticallySorter implements Comparator<ExpAndOp> {
		@Override
		public int compare(ExpAndOp eo1, ExpAndOp eo2) {
			String rStr = eo1.e.toString().replace("(", "").replace(")", ""); // remove all brackets because they are smaller than anything else
			String lStr = eo2.e.toString().replace("(", "").replace(")", "");

			return rStr.compareTo(lStr);
		}
	}
	/*private*/ static AlphabeticallySorter alphabeticallySorter = new AlphabeticallySorter();
	
	private static boolean calcFunctionsFromConsts(Expression exp) {
		if (exp == null)
			return false;
		if (exp.isUnaryOperation() && exp.getRightExpression().isConstant()) {
			double dVal = Expression.evaluate(exp.getOperation(), Double.NaN, exp.getRightExpression().getConstantValue());
			assert(!Double.isNaN(dVal));
			exp.setTo(dVal);
			return true;
		} 
		return false;		
	}	
}
