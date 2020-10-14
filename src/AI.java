import java.util.Random;

public class AI {
	private Random generator = new Random();
	private Board board;
	private MainFrame mainFrame;
	
	// private static double maxPly;

	public AI(Board board, MainFrame mainFrame) {
		this.board = board;
		this.mainFrame = mainFrame;

	}

	public int randomInt(int n) {
		return generator.nextInt(n);
	}

	public void move() {
		// test thử cho AI random, sau này edit ráp thuật toán vào
		if (Board.nSteps == 0) {
			int boardX = randomInt(board.getN());
			int boardY = randomInt(board.getN());
			board.addMove(boardY, boardX);
			in();
			return;
		}
		
		// Random 100 lần, nếu 100 lần đều ra ô đã đi rồi thì chạy cái for dưới
		for (int ntimes = 1; ntimes <= 100; ntimes++) {
			int i = randomInt(board.getN());
			int j = randomInt(board.getN());
			if (board.table[i][j] == 0) {
				board.addMove(i, j);
				in();
				return;
			}
		}
		// vét cạn tất cả trường hợp để kiếm ô trống
		for (int i = 0; i < board.getN(); i++)
			for (int j = 0; j < board.getN(); j++)
				if (board.table[i][j] == 0) {
					board.addMove(i, j);
					in();
					return;
				}
	}
	
	public void in() {
			
			int xUserOld = Board.x[board.nSteps-2];
			int yUserOld = Board.y[board.nSteps-2];
			
			
			
	}
	
	public int minimax() {
		
		return 0;
	}
	
	public int getValueUser(int valueUser) {
		int user = valueUser;
		for (int i = 0; i < Board.table.length; i++) {
			for (int j = 0; j < Board.table.length; j++) {
				if(Board.table[i][j] == user) {
					
				}
			}
			System.out.println();
		}
		
		return 0;
	}
	
	public int getPointRow( int xRow, int yRow, int value ) {
		int point = 0;
		int getXRow = xRow;
		int getYRow = yRow;
		
		while (getXRow >= 0 && Board.table[getXRow][getYRow] == value) {
			point+=10;
			getXRow--;
		}
		
		getXRow = xRow + 1;
		while (getXRow < Board.table.length && Board.table[getXRow][getYRow] == value) {
			point+=10;
			getXRow++;
		}
		
		return point ;
	}
	
//	public int getPointCol( int xRow, int yRow, int value ) {
//		int point = 0;
//		int getXRow = xRow;
//		int getYRow = yRow;
//		
//		while (xRow >= 0 && Board.table[getXRow][getYRow] == value) {
//			point+=10;
//			getYRow--;
//		}
//		return point ;
//	}
	
}
