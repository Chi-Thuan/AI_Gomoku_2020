package ai;

import java.util.List;

public class MiniMax implements ISearchAlgo {

	@Override
	public int[] execute(Node node, int depth) {
		maxValue(node, depth);
		Node re = node.getBestNextMove();
		return new int[] { re.getRowMove(), re.getColMove() };
	}

	public int maxValue(Node node, int depth) {
		if (depth == 0)
			return node.getValue();
		int v = Integer.MIN_VALUE;
		int newV = Integer.MIN_VALUE;
		// 1 là AI => generate các nước đi của AI
		List<Node> childrens = node.getChildren(1);

		for (Node child : childrens) {
			newV = minValue(child, depth - 1);
			if (newV > v) {
				node.setBestNextMove(child);
				v = newV;
			}
		}
		return v;
	}

	public int minValue(Node node, int depth) {
		if (depth == 0)
			return node.getValue();
		int v = Integer.MAX_VALUE;
		int newV = Integer.MAX_VALUE;
		// -1 là Người => generate các nước đi của Người
		List<Node> childrens = node.getChildren(-1);
		for (Node child : childrens) {
			newV = maxValue(child, depth - 1);
			if (newV < v) {
				node.setBestNextMove(child);
				v = newV;
			}
		}
		return v;
	}

}
