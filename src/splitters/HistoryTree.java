package splitters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import core.Box;

// we will store splitting history here
public class HistoryTree {
	protected static class Node implements Comparable<Node> {
		Node parent;
		Node[] children;
		Box value;
		int coordinate; // this is an n-dimension tree, so each node 
						// has to care on what dimension it was split
						// !! parent !! contains this info:
						//  	      Node(coordinate = 0)
						//				/				\
						//		Node(coordinate=2)		Node(coordinate=-1)
						//		/				\
						// Node(coordinate=-1) Node(coordinate=-1)
		
		public Node(Node parent/*, int sideNum*/, Box value) {
/*			if (parent!=null) {
				parent.value = null;
				parent.coordinate = sideNum;
			}
*/ // speed optimization...			
			this.parent = parent;
			this.value = value;
		}
		@Override
		public int compareTo(Node node) {
			if (this == node)
				return 0;
			return toString().compareTo(node.toString());
		}
	}
	protected static enum Direction{left, right}; 
	
	protected TreeSet<Node> activeNodes; // current children only
	Deque<Node> roots = new LinkedList<Node>();
	ArrayList<Box> neighbors = new ArrayList<Box>();
	
	
	// API {
	public HistoryTree(Box initialArea) {
		activeNodes = new TreeSet<Node>();
		activeNodes.add(new Node(null, initialArea));
	}
	public void boxWasSplited(Box originalBox, int sideNum, Box[] children) {
		Node parent = findActiveNode(originalBox);
		addNodes(parent, sideNum, children);
	}
	public ArrayList<Box> getLeftNeighbors(Box b, int coordinateNum) {
		return getNeighbors(b, coordinateNum, Direction.left);
	}
	public ArrayList<Box> getRightNeighbors(Box b, int coordinateNum) {
		return getNeighbors(b, coordinateNum, Direction.right);		
	}
	/// }

	// add new history records {
	private Node findActiveNode(Box valueToFind) {
		// looking only through the lowest level of children -- only they are "active" nodes, the rest are already split 
		for (Node n : activeNodes) {
			if (n.value == valueToFind) // comparing pointers! 
										//	(actually, "equals" could be used here as well, pointers are just more effective)
				return n;
		}
		return null;
	}
	private void addNodes(Node parent, int sideNum, Box[] children) {
		Node[] newNodes = createNodes(parent, children);
		addChildren(parent, sideNum, newNodes);
	}
	private Node[] createNodes(Node parent, Box[] children) {
		final int length = children.length;
		Node[] newNodes = new Node[length];
		for (int i = 0; i < length; i++) {
			newNodes[i] = new Node(parent/*, sideNum*/, children[i]);
		}
		return newNodes;
	}
	private void addChildren(Node parent, int sideNum, Node newNodes[]) {
		parent.coordinate = sideNum;
		parent.children = newNodes;
		parent.value = null;
		activeNodes.remove(parent);
		activeNodes.addAll(Arrays.asList(newNodes));
	}
	// }
	
	// get info from the history {
	private ArrayList<Box> getNeighbors(Box b, int coordinateNum, Direction dir) {
		Node parent = findParentSplitOnThisSide(b, coordinateNum);
		if (parent == null)
			return null; // no neighbors
		ArrayList<Box> neighbours = getAllChildren(coordinateNum, parent, dir);
		return neighbours;
	}
	private Node findParentSplitOnThisSide(Box b, int coordinateNum) {
		Node node = findActiveNode(b);
		if (node == null)
			System.out.println("For box " + b + " findActiveNode returned null!");
		assert(node != null); // the box should be known by the history
		while (node.parent != null) {
			if (node.parent.coordinate == coordinateNum)
				return node.parent; 
		}
		return null;
	}
	private ArrayList<Box> getAllChildren(int coordinateNum, Node parent, Direction dir) {
		// parent is a parent:) we need children only
		if (parent.children.length != 2)
			throw new NotImplementedException();
/*
 * probably, 				
 * 		int idx = (dir == Direction.left ? 0 : n.children.length-1);
 * 		_roots.add(n.children[idx]);
 * 	in @processNextNode@ 
 * handles this case...		
 */
		
		int idx;
		if (dir == Direction.left)
			idx = 0;
		else if (dir == Direction.right)
			idx = 1;
		else
			throw new IllegalStateException("strange direction");
		
		roots.add( parent.children[idx] );
		do {
			// logging could be here
		} while (processNextNode(coordinateNum, dir));
		return neighbors;
	}
	
	private boolean processNextNode(int coordinateNum, Direction dir) {
		Node n = null;//roots.offerFirst(); //roots.pop();
		if (n == null)
			return false;
		if (n.children != null) {
			if (n.coordinate != coordinateNum) 
				roots.addAll(Arrays.asList(n.children));
			else {
				int idx = (dir == Direction.left ? 0 : n.children.length-1);
				roots.add(n.children[idx]);
			}
		} else {
			assert(n.value != null);
			neighbors.add(n.value);
		}
		return true;
	}
	
	/// }

	
}
