package ai;

import model.Board;

public class AI {
	private Board board;
	private ISearchAlgo miniMax;
	private ISearchAlgo alphaBeta;

	public AI(Board board) {
		this.board = board;
		miniMax = new MiniMax();
		alphaBeta = new AlphaBeta();
	}

	public void moveFirst() {
		int n = board.getN();
		int index = (n % 2 == 0) ? n / 2 - 1 : n / 2;
		board.addMove(index, index);
		// nước đi đầu tiên của AI luôn luôn vào chính giữa
		// 3x3 thì đánh vào [1, 1]
		// 10x10 thì đánh vào [4, 4]
	}

	public void move(int algo) {
		// index của cbb = 0 là MiniMax, 1 là AlpheBeta
		int[] arr = null;
		switch (algo) {
		case 0:
			arr = miniMax.execute(new Node(), 3);
			break;
		case 1:
			arr = alphaBeta.execute(new Node(), 3);
			break;
		}

		board.addMove(arr[0], arr[1]);
	}
}
