import java.util.HashSet;

@SuppressWarnings("unused")
public class Board {

	private byte n; // Kích thước hiện tại của bàn cờ
	private byte maxN; // Kích thước tối đa của bàn cờ
	private byte defaultN; // Kích thước mặc định của bàn cờ
	private byte lengthWin; // Kích thước win : 5 hoặc bé hơn 5, tùy vào kích thước bàn cờ
	private short nUserWin; // Người chơi win
	private short nComputerWin; // Máy win
	public static boolean humanFirst; // Người đi trước
	public static boolean userX; // Người chơi là quân X

	public static short nSteps; // số nước đi
	private boolean[][] used; // Đánh dấu đã đánh hay chưa
	public static int[] x; // Lưu phần row của các nước đi
	public static int[] y; // Lưu phần column của các nước đi
	public static int[][] table; // Lưu các nước đi
	public static boolean isGameOver;

	static MainFrame mainFrame;

	public Board(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		n = defaultN = 20;
		maxN = 30;
		lengthWin = 5;
		nUserWin = 0;
		nComputerWin = 0;
		humanFirst = true;
		userX = true;

		nSteps = 0;
		used = new boolean[maxN][maxN];
		x = new int[maxN * maxN];
		y = new int[maxN * maxN];
		table = new int[maxN][maxN];
		isGameOver = false;
	}

	public void clearData() {
		nSteps = 0;
		for (boolean[] arr : used)
			for (int i = 0; i < n; i++)
				arr[i] = false;

		for (int[] arr : table)
			for (int i = 0; i < n; i++)
				arr[i] = 0;
		isGameOver = false;
		getLengthWin();

	}

	public boolean isCanMove(int row, int col) {
		return !used[row][col];
	}

	public void addMove(int row, int col) {
		used[row][col] = true;
		x[nSteps] = row;
		y[nSteps] = col;
		nSteps++;
		if (Board.humanFirst) {
			if (nSteps % 2 != 0) {
				table[row][col] = -1; // -1 là người
				mainFrame.updateMove(true, col, row);
			} else {
				table[row][col] = 1;
				mainFrame.updateMove(false, col, row);
			}
		} else if (nSteps % 2 != 0) {
			table[row][col] = 1;
			mainFrame.updateMove(false, col, row);
		} else {
			table[row][col] = -1;
			mainFrame.updateMove(true, col, row);
		}

		checkFinalState(row, col, table[row][col]);
		if (isGameOver) {
			mainFrame.getScore(getTurn());
			mainFrame.showDialogEndGame(getTurn());

		} else {
			if (nSteps == getN() * getN())
				mainFrame.showDialogEndGame(0);
		}
	}

	public void deleteMove() {
		used[x[nSteps - 1]][y[nSteps - 1]] = false;
		mainFrame.clearCell(y[nSteps - 1], x[nSteps - 1]);
		table[x[nSteps - 1]][y[nSteps - 1]] = 0;
		nSteps--;

		used[x[nSteps - 1]][y[nSteps - 1]] = false;
		mainFrame.clearCell(y[nSteps - 1], x[nSteps - 1]);
		table[x[nSteps - 1]][y[nSteps - 1]] = 0;
		nSteps--;
	}

	// Hàm kiểm tra thắng thua
	public boolean checkFinalState(int row, int col, int value) {

		// check row
		int checkRow = 0;
		int col1 = col;
		while (col - 1 >= 0 && table[row][col - 1] == value) {
			checkRow++;
			col--;
		}
		col = col1;
		while (col + 1 <= getN() - 1 && table[row][col + 1] == value) {
			checkRow++;
			col++;
		}

		if (checkRow >= getLengthWin() - 1)
			return isGameOver = true;

		// check column
		int checkCol = 0;
		int row1 = row;
		while (row - 1 >= 0 && table[row - 1][col] == value) {
			checkCol++;
			row--;
		}
		row = row1;
		while (row + 1 <= getN() - 1 && table[row + 1][col] == value) {
			checkCol++;
			row++;
		}

		if (checkCol >= getLengthWin() - 1)
			return isGameOver = true;

		// checkDiagonalFromTopLeft
		int checkDiagonalFromTopLeft = 0;
		row = row1;
		col = col1;
		while (row - 1 >= 0 && col - 1 >= 0 && table[row - 1][col - 1] == value) {
			checkDiagonalFromTopLeft++;
			row--;
			col--;
		}
		row = row1;
		col = col1;
		while (row + 1 <= getN() - 1 && col + 1 <= getN() - 1 && table[row + 1][col + 1] == value) {
			checkDiagonalFromTopLeft++;
			row++;
			col++;
		}

		if (checkDiagonalFromTopLeft >= getLengthWin() - 1)
			return isGameOver = true;

		// checkDiagonalFromTopRight
		int checkDiagonalFromTopRight = 0;
		row = row1;
		col = col1;
		while (row - 1 >= 0 && col + 1 <= getN() - 1 && table[row - 1][col + 1] == value) {
			checkDiagonalFromTopRight++;
			row--;
			col++;
		}
		row = row1;
		col = col1;
		while (row + 1 <= getN() - 1 && col - 1 >= 0 && table[row + 1][col - 1] == value) {
			checkDiagonalFromTopRight++;
			row++;
			col--;
		}

		if (checkDiagonalFromTopRight >= getLengthWin() - 1)
			return isGameOver = true;

		return false;
	}
	// getters & setters

	public byte getN() {
		return n;
	}

	public void setN(byte n) {
		this.n = n;
		getLengthWin();
	}

	public byte getDefaultN() {
		return defaultN;
	}

	public void setDefaultN(byte defaultN) {
		this.defaultN = defaultN;
	}

	public byte getLengthWin() {
		return (byte) Math.min(5, n);
	}

	public void setLengthWin(byte lengthWin) {
		this.lengthWin = lengthWin;
	}

	public short getnUserWin() {
		return nUserWin;
	}

	public void setnUserWin(short nUserWin) {
		this.nUserWin = nUserWin;
	}

	public short getnComputerWin() {
		return nComputerWin;
	}

	public void setnComputerWin(short nComputerWin) {
		this.nComputerWin = nComputerWin;
	}

	public boolean isHumanFirst() {
		return humanFirst;
	}

	public void setHumanFirst(boolean humanFirst) {
		Board.humanFirst = humanFirst;
	}

	public boolean isUserX() {
		return userX;
	}

	public void setUserX(boolean userX) {
		Board.userX = userX;
	}

	public short getnSteps() {
		return nSteps;
	}

	public void setnSteps(short nSteps) {
		Board.nSteps = nSteps;
	}

	public boolean[][] getUsed() {
		return used;
	}

	public void setUsed(boolean[][] used) {
		this.used = used;
	}

	public int[] getX() {
		return x;
	}

	public void setX(int[] x) {
		Board.x = x;
	}

	public int[] getY() {
		return y;
	}

	public void setY(int[] y) {
		Board.y = y;
	}

	public int getTurn() {
		return table[x[nSteps - 1]][y[nSteps - 1]];
	}

	public Board getDeepCopy() {
		Board board = new Board(null);

		for (int i = 0; i < board.getN(); i++) {
			board.table[i] = this.table[i].clone();
		}

		board.nSteps = this.nSteps;
		board.isGameOver = this.isGameOver;
		board.lengthWin = this.lengthWin;
		return board;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int x = 0; x < getN(); x++) {
			for (int y = 0; y < getN(); y++) {

				if (table[x][y] == 0) {
					sb.append("-");
				} else {
					sb.append(table[x][y]);
				}
				sb.append(" ");

			}
			if (x != getN() - 1) {
				sb.append("\n");
			}
		}

		return new String(sb);
	}
}
