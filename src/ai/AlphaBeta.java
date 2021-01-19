package ai;

import java.util.List;

public class AlphaBeta implements ISearchAlgo {

	@Override
	public int[] execute(Node node, int depth) {
		maxValue(node, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
		Node re = node.getBestNextMove();
		return new int[] { re.getRowMove(), re.getColMove() };
	}

	public int maxValue(Node node, int alpha, int beta, int depth) {
		if (depth == 0)
			return node.getValue();

		int v = Integer.MIN_VALUE;
		int newV = Integer.MIN_VALUE;
		// 1 là AI => generate các nước đi của AI
		List<Node> childrens = node.getChildren(1);

		for (Node child : childrens) {
			if (child.checkWin(1)) {
				node.setBestNextMove(child);
				return child.getValue();
			}
			newV = minValue(child, alpha, beta, depth - 1);
			if (newV > v) {
				node.setBestNextMove(child);
				v = newV;
			}
			if (v >= beta) {
				return v;
			}
			alpha = Math.max(alpha, v);
		}
		return v;
	}

	public int minValue(Node node, int alpha, int beta, int depth) {
		if (depth == 0)
			return node.getValue();
		int v = Integer.MAX_VALUE;
		int newV = Integer.MAX_VALUE;
		// -1 là Người => generate các nước đi của Người
		List<Node> childrens = node.getChildren(-1);

		for (Node child : childrens) {
			if (child.checkWin(-1)) {
				node.setBestNextMove(child);
				return child.getValue();
			}
			
			newV = maxValue(child, alpha, beta, depth - 1);
			if (newV < v) {
				node.setBestNextMove(child);
				v = newV;
			}
			if (v <= alpha) {
				return v;
			}
			beta = Math.min(beta, v);
		}
		return v;
	}

}
