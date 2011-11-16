/**
 * 
 */
package algorithms;

/**
 * @author nvpanov
 * 
 * This enum shows why an alghorithm.solve() ended:
 * due to any stop criterion or because there is no more boxes
 * in the workList after screening.
 *
 */
public enum OptimizationStatus {
	RUNNING, EXTERNAL_INTERRUPTED, STOP_CRITERION_SATISFIED, EMPTY_WORKLIST
}
