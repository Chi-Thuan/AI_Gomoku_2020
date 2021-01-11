package ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Board;

@SuppressWarnings("unused")
public class Node {
	private int[][] matrix;
	private int rowMove;
	private int colMove;
	private int value;
	private List<Node> children = new ArrayList<Node>();
	private Node bestNextMove;

	public Node() { // deep copy table
		rowMove = -1;
		colMove = -1;
		matrix = cloneMatrix(Board.table);
	}

	public Node(Node parent, int rowMove, int colMove, int val) {
		this.rowMove = rowMove;
		this.colMove = colMove;
		this.matrix = cloneMatrix(parent.matrix);
		this.matrix[rowMove][colMove] = val;
	}

	public int[][] cloneMatrix(int[][] src) { // square matrix
		int len = src.length;
		int[][] re = new int[len][];
		for (int i = 0; i < len; i++)
			re[i] = Arrays.copyOf(src[i], len);
		return re;
	}

	public List<Node> getChildren(int val) {
		// val -1 là Người, 1 là AI
		int len = Board.n;
		for (int row = 0; row < len; row++)
			for (int col = 0; col < len; col++)
				if (matrix[row][col] != 0) {
					// trên
					addChild(row - 1, col, val);
					// dưới
					addChild(row + 1, col, val);
					// trái
					addChild(row, col - 1, val);
					// phải
					addChild(row, col + 1, val);
					// trái trên
					addChild(row - 1, col - 1, val);
					// phải trên
					addChild(row - 1, col + 1, val);
					// trái dưới
					addChild(row + 1, col - 1, val);
					// phải dưới
					addChild(row + 1, col + 1, val);
				}

		return children;
	}

	public void addChild(int row, int col, int val) {
		try {
			if (matrix[row][col] == 0) {
				Node child = new Node(this, row, col, val);
				if (!children.contains(child))
					children.add(child);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	public int getEval(int val) {
		int enemy = val * -1, fiveInRow, live4, dead4, dead4b, live3, dead3, dead3b, live2, dead2, dead2b;
		fiveInRow = live4 = dead4 = dead4b = live3 = dead3 = dead3b = live2 = dead2 = dead2b = 0;
		for (int row = 0; row < matrix.length; row++)
			for (int col = 0; col < matrix.length; col++) {
				if (matrix[row][col] == val) {
					fiveInRow += getFiveInRow(row, col, val);
					live4 += getLiveFour(row, col, val);
					dead4 += getDeadFour1(row, col, val);
					dead4 += getDeadFour2(row, col, val);
					live3 += getLiveThree(row, col, val);
					dead3 += getDeadThree1(row, col, val);
					dead3 += getDeadThree2(row, col, val);
					dead3 += getDeadThree3(row, col, val);
					live2 += getLiveTwo(row, col, val);
					dead2 += getDeadTwo1(row, col, val);
					dead2 += getDeadTwo2(row, col, val);
					dead2 += getDeadTwo3(row, col, val);
				}

				if (matrix[row][col] == enemy) {
					dead4b += getDeadFourBlock(row, col, val, enemy);
					dead3b += getDeadThreeBlock1(row, col, val, enemy);
					dead3b += getDeadThreeBlock2(row, col, val, enemy);
					dead3b += getDeadThreeBlock3(row, col, val, enemy);
					dead3b += getDeadThreeBlock4(row, col, val, enemy);
					dead2b += getDeadTwoBlock1(row, col, val, enemy);
					dead2b += getDeadTwoBlock2(row, col, val, enemy);
					dead2b += getDeadTwoBlock3(row, col, val, enemy);
				}

			}

		if (fiveInRow >= 1)
			return Integer.MAX_VALUE;

		if (live4 >= 1)
			return 1000000;

		if (dead4 >= 1 || dead4b >= 1 || live3 >= 1 || dead3 >= 2 || (dead4 >= 1 && dead4b >= 1)
				|| (dead4 >= 1 && live3 >= 1) || (dead4 >= 1 && dead3 >= 1) || (dead4b >= 1 && live3 >= 1)
				|| (dead4b >= 1 && dead3 >= 1))
			return 400000;

		int eval = live3 * 500 + dead3 * 200 + dead3b * 90 + live2 * 40 + dead2 * 10 + dead2b;

		return eval;
	}

	public int getValue() {
		int a = getEval(1);
		int b = getEval(-1);
//		if (Board.humanFirst)
//			b++;
//		else
//			a++;
		return value = a - b;
	}

	public int getFiveInRow(int row, int col, int val) {
		// check 4 hướng
		int re = 0;
		// dòng
		try {
			if (matrix[row][col + 1] == val && matrix[row][col + 2] == val && matrix[row][col - 1] == val
					&& matrix[row][col - 2] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// cột
		try {
			if (matrix[row + 1][col] == val && matrix[row + 2][col] == val && matrix[row - 1][col] == val
					&& matrix[row - 2][col] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo chính
		try {
			if (matrix[row - 1][col - 1] == val && matrix[row - 2][col - 2] == val && matrix[row + 1][col + 1] == val
					&& matrix[row + 2][col + 2] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phụ
		try {
			if (matrix[row - 1][col + 1] == val && matrix[row - 2][col + 2] == val && matrix[row + 1][col - 1] == val
					&& matrix[row + 2][col - 2] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	public int getLiveFour(int row, int col, int val) {
		// check 4 hướng
		int re = 0;
		// dòng
		try {
			if (matrix[row][col - 1] == 0 && matrix[row][col + 1] == val && matrix[row][col + 2] == val
					&& matrix[row][col + 3] == val && matrix[row][col + 4] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// cột
		try {
			if (matrix[row + 1][col] == 0 && matrix[row - 1][col] == val && matrix[row - 2][col] == val
					&& matrix[row - 3][col] == val && matrix[row - 4][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo chính
		try {
			if (matrix[row + 1][col + 1] == 0 && matrix[row - 1][col - 1] == val && matrix[row - 2][col - 2] == val
					&& matrix[row - 3][col - 3] == val && matrix[row - 4][col - 4] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phụ
		try {
			if (matrix[row + 1][col - 1] == 0 && matrix[row - 1][col + 1] == val && matrix[row - 2][col + 2] == val
					&& matrix[row - 3][col + 3] == val && matrix[row - 4][col + 4] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	public int getDeadFour1(int row, int col, int val) {
		// check 8 hướng
		int re = 0;
		// sang phải
		try {
			if (matrix[row][col + 1] == 0 && matrix[row][col + 2] == val && matrix[row][col + 3] == val
					&& matrix[row][col + 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// sang trái
		try {
			if (matrix[row][col - 1] == 0 && matrix[row][col - 2] == val && matrix[row][col - 3] == val
					&& matrix[row][col - 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// xuống
		try {
			if (matrix[row + 1][col] == 0 && matrix[row + 2][col] == val && matrix[row + 3][col] == val
					&& matrix[row + 4][col] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// lên
		try {
			if (matrix[row - 1][col] == 0 && matrix[row - 2][col] == val && matrix[row - 3][col] == val
					&& matrix[row - 4][col] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải trên
		try {
			if (matrix[row - 1][col + 1] == 0 && matrix[row - 2][col + 2] == val && matrix[row - 3][col + 3] == val
					&& matrix[row - 4][col + 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái dưới
		try {
			if (matrix[row + 1][col - 1] == 0 && matrix[row + 2][col - 2] == val && matrix[row + 3][col - 3] == val
					&& matrix[row + 4][col - 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái trên
		try {
			if (matrix[row - 1][col - 1] == 0 && matrix[row - 2][col - 2] == val && matrix[row - 3][col - 3] == val
					&& matrix[row - 4][col - 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải dưới
		try {
			if (matrix[row + 1][col + 1] == 0 && matrix[row + 2][col + 2] == val && matrix[row + 3][col + 3] == val
					&& matrix[row + 4][col + 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	public int getDeadFour2(int row, int col, int val) {
		// check 4 hướng
		int re = 0;
		// dòng
		try {
			if (matrix[row][col + 1] == val && matrix[row][col + 2] == 0 && matrix[row][col + 3] == val
					&& matrix[row][col + 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// cột
		try {
			if (matrix[row - 1][col] == val && matrix[row - 2][col] == 0 && matrix[row - 3][col] == val
					&& matrix[row - 4][col] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo chính
		try {
			if (matrix[row - 1][col - 1] == val && matrix[row - 2][col - 2] == 0 && matrix[row - 3][col - 3] == val
					&& matrix[row - 4][col - 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phụ
		try {
			if (matrix[row - 1][col + 1] == val && matrix[row - 2][col + 2] == 0 && matrix[row - 3][col + 3] == val
					&& matrix[row - 4][col + 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	public int getLiveThree(int row, int col, int val) {
		// check 4 hướng
		int re = 0;
		// dòng
		try {
			if (matrix[row][col - 1] == 0 && matrix[row][col + 1] == val && matrix[row][col + 2] == val
					&& matrix[row][col + 3] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// cột
		try {
			if (matrix[row + 1][col] == 0 && matrix[row - 1][col] == val && matrix[row - 2][col] == val
					&& matrix[row - 3][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo chính
		try {
			if (matrix[row + 1][col + 1] == 0 && matrix[row - 1][col - 1] == val && matrix[row - 2][col - 2] == val
					&& matrix[row - 3][col - 3] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phụ
		try {
			if (matrix[row + 1][col - 1] == 0 && matrix[row - 1][col + 1] == val && matrix[row - 2][col + 2] == val
					&& matrix[row - 3][col + 3] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	public int getDeadThree1(int row, int col, int val) {
		// check 8 hướng
		int re = 0;
		// sang phải
		try {
			if (matrix[row][col + 1] == 0 && matrix[row][col + 2] == val && matrix[row][col + 3] == val
					&& matrix[row][col + 4] == 0 && matrix[row][col - 1] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// sang trái
		try {
			if (matrix[row][col - 1] == 0 && matrix[row][col - 2] == val && matrix[row][col - 3] == val
					&& matrix[row][col - 4] == 0 && matrix[row][col + 1] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// xuống
		try {
			if (matrix[row + 1][col] == 0 && matrix[row + 2][col] == val && matrix[row + 3][col] == val
					&& matrix[row + 4][col] == 0 && matrix[row - 1][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// lên
		try {
			if (matrix[row - 1][col] == 0 && matrix[row - 2][col] == val && matrix[row - 3][col] == val
					&& matrix[row - 4][col] == 0 && matrix[row + 1][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải trên
		try {
			if (matrix[row - 1][col + 1] == 0 && matrix[row - 2][col + 2] == val && matrix[row - 3][col + 3] == val
					&& matrix[row - 4][col + 4] == 0 && matrix[row + 1][col - 1] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái dưới
		try {
			if (matrix[row + 1][col - 1] == 0 && matrix[row + 2][col - 2] == val && matrix[row + 3][col - 3] == val
					&& matrix[row + 4][col - 4] == 0 && matrix[row - 1][col + 1] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái trên
		try {
			if (matrix[row - 1][col - 1] == 0 && matrix[row - 2][col - 2] == val && matrix[row - 3][col - 3] == val
					&& matrix[row - 4][col - 4] == 0 && matrix[row + 1][col + 1] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải dưới
		try {
			if (matrix[row + 1][col + 1] == 0 && matrix[row + 2][col + 2] == val && matrix[row + 3][col + 3] == val
					&& matrix[row + 4][col + 4] == 0 && matrix[row - 1][col - 1] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	public int getDeadThree2(int row, int col, int val) {
		// check 8 hướng
		int re = 0;
		// sang phải
		try {
			if (matrix[row][col + 1] == val && matrix[row][col + 2] == 0 && matrix[row][col + 3] == 0
					&& matrix[row][col + 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// sang trái
		try {
			if (matrix[row][col - 1] == val && matrix[row][col - 2] == 0 && matrix[row][col - 3] == 0
					&& matrix[row][col - 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// xuống
		try {
			if (matrix[row + 1][col] == val && matrix[row + 2][col] == 0 && matrix[row + 3][col] == 0
					&& matrix[row + 4][col] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// lên
		try {
			if (matrix[row - 1][col] == val && matrix[row - 2][col] == 0 && matrix[row - 3][col] == 0
					&& matrix[row - 4][col] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải trên
		try {
			if (matrix[row - 1][col + 1] == val && matrix[row - 2][col + 2] == 0 && matrix[row - 3][col + 3] == 0
					&& matrix[row - 4][col + 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái dưới
		try {
			if (matrix[row + 1][col - 1] == val && matrix[row + 2][col - 2] == 0 && matrix[row + 3][col - 3] == 0
					&& matrix[row + 4][col - 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái trên
		try {
			if (matrix[row - 1][col - 1] == val && matrix[row - 2][col - 2] == 0 && matrix[row - 3][col - 3] == 0
					&& matrix[row - 4][col - 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải dưới
		try {
			if (matrix[row + 1][col + 1] == val && matrix[row + 2][col + 2] == 0 && matrix[row + 3][col + 3] == 0
					&& matrix[row + 4][col + 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	public int getDeadThree3(int row, int col, int val) {
		// check 4 hướng
		int re = 0;
		// dòng
		try {
			if (matrix[row][col + 1] == 0 && matrix[row][col + 2] == val && matrix[row][col + 3] == 0
					&& matrix[row][col + 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// cột
		try {
			if (matrix[row - 1][col] == 0 && matrix[row - 2][col] == val && matrix[row - 3][col] == 0
					&& matrix[row - 4][col] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		// chéo chính
		try {
			if (matrix[row - 1][col - 1] == 0 && matrix[row - 2][col - 2] == val && matrix[row - 3][col - 3] == 0
					&& matrix[row - 4][col - 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		// chéo phụ
		try {
			if (matrix[row + 1][col + 1] == 0 && matrix[row + 2][col + 2] == val && matrix[row + 3][col + 3] == 0
					&& matrix[row + 4][col + 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		return re;
	}

	public int getLiveTwo(int row, int col, int val) {
		// check 8 hướng
		int re = 0;
		// sang phải
		try {
			if (matrix[row][col - 1] == 0 && matrix[row][col + 1] == val && matrix[row][col + 2] == 0
					&& matrix[row][col + 3] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// sang trái
		try {
			if (matrix[row][col + 1] == 0 && matrix[row][col - 1] == val && matrix[row][col - 2] == 0
					&& matrix[row][col - 3] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// xuống
		try {
			if (matrix[row - 1][col] == 0 && matrix[row + 1][col] == val && matrix[row + 2][col] == 0
					&& matrix[row + 3][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// lên
		try {
			if (matrix[row + 1][col] == 0 && matrix[row - 1][col] == val && matrix[row - 2][col] == 0
					&& matrix[row - 3][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải trên
		try {
			if (matrix[row + 1][col - 1] == 0 && matrix[row - 1][col + 1] == val && matrix[row - 2][col + 2] == 0
					&& matrix[row - 3][col + 3] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái dưới
		try {
			if (matrix[row - 1][col + 1] == 0 && matrix[row + 1][col - 1] == val && matrix[row + 2][col - 2] == 0
					&& matrix[row + 3][col - 3] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái trên
		try {
			if (matrix[row + 1][col + 1] == 0 && matrix[row - 1][col - 1] == val && matrix[row - 2][col - 2] == 0
					&& matrix[row - 3][col - 3] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải dưới
		try {
			if (matrix[row - 1][col - 1] == 0 && matrix[row + 1][col + 1] == val && matrix[row + 2][col + 2] == 0
					&& matrix[row + 3][col + 3] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	public int getDeadTwo1(int row, int col, int val) {
		// check 4 hướng
		int re = 0;
		// dòng
		try {
			if (matrix[row][col + 1] == 0 && matrix[row][col + 2] == 0 && matrix[row][col + 3] == 0
					&& matrix[row][col + 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// cột
		try {
			if (matrix[row - 1][col] == 0 && matrix[row - 2][col] == 0 && matrix[row - 3][col] == 0
					&& matrix[row - 4][col] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo chính
		try {
			if (matrix[row - 1][col - 1] == 0 && matrix[row - 2][col - 2] == 0 && matrix[row - 3][col - 3] == 0
					&& matrix[row - 4][col - 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phụ
		try {
			if (matrix[row - 1][col + 1] == 0 && matrix[row - 2][col + 2] == 0 && matrix[row - 3][col + 3] == 0
					&& matrix[row - 4][col + 4] == val)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	public int getDeadTwo2(int row, int col, int val) {
		// check 4 hướng
		int re = 0;
		// dòng
		try {
			if (matrix[row][col - 1] == 0 && matrix[row][col + 1] == 0 && matrix[row][col + 2] == val
					&& matrix[row][col + 3] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// cột
		try {
			if (matrix[row + 1][col] == 0 && matrix[row - 1][col] == 0 && matrix[row - 2][col] == val
					&& matrix[row - 3][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo chính
		try {
			if (matrix[row + 1][col + 1] == 0 && matrix[row - 1][col - 1] == 0 && matrix[row - 2][col - 2] == val
					&& matrix[row - 3][col - 3] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phụ
		try {
			if (matrix[row + 1][col - 1] == 0 && matrix[row - 1][col + 1] == 0 && matrix[row - 2][col + 2] == val
					&& matrix[row - 3][col + 3] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	public int getDeadTwo3(int row, int col, int val) {
		// check 8 hướng
		int re = 0;
		// sang phải
		try {
			if (matrix[row][col + 1] == 0 && matrix[row][col + 2] == 0 && matrix[row][col + 3] == val
					&& matrix[row][col + 4] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// sang trái
		try {
			if (matrix[row][col - 1] == 0 && matrix[row][col - 2] == 0 && matrix[row][col - 3] == val
					&& matrix[row][col - 4] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// xuống
		try {
			if (matrix[row + 1][col] == 0 && matrix[row + 2][col] == 0 && matrix[row + 3][col] == val
					&& matrix[row + 4][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// lên
		try {
			if (matrix[row - 1][col] == 0 && matrix[row - 2][col] == 0 && matrix[row - 3][col] == val
					&& matrix[row - 4][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải trên
		try {
			if (matrix[row - 1][col + 1] == 0 && matrix[row - 2][col + 2] == 0 && matrix[row - 3][col + 3] == val
					&& matrix[row - 4][col + 4] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái dưới
		try {
			if (matrix[row + 1][col - 1] == 0 && matrix[row + 2][col - 2] == 0 && matrix[row + 3][col - 3] == val
					&& matrix[row + 4][col - 4] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái trên
		try {
			if (matrix[row - 1][col - 1] == 0 && matrix[row - 2][col - 2] == 0 && matrix[row - 3][col - 3] == val
					&& matrix[row - 4][col - 4] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải dưới
		try {
			if (matrix[row + 1][col + 1] == 0 && matrix[row + 2][col + 2] == 0 && matrix[row + 3][col + 3] == val
					&& matrix[row + 4][col + 4] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	public int getDeadFourBlock(int row, int col, int val, int enemy) {
		// check 8 hướng
		int re = 0;
		// sang phải
		try {
			if (matrix[row][col + 1] == val && matrix[row][col + 2] == val && matrix[row][col + 3] == val
					&& matrix[row][col + 4] == val && matrix[row][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// sang trái
		try {
			if (matrix[row][col - 1] == val && matrix[row][col - 2] == val && matrix[row][col - 3] == val
					&& matrix[row][col - 4] == val && matrix[row][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// xuống
		try {
			if (matrix[row + 1][col] == val && matrix[row + 2][col] == val && matrix[row + 3][col] == val
					&& matrix[row + 4][col] == val && matrix[row + 5][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// lên
		try {
			if (matrix[row - 1][col] == val && matrix[row - 2][col] == val && matrix[row - 3][col] == val
					&& matrix[row - 4][col] == val && matrix[row - 5][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải trên
		try {
			if (matrix[row - 1][col + 1] == val && matrix[row - 2][col + 2] == val && matrix[row - 3][col + 3] == val
					&& matrix[row - 4][col + 4] == val && matrix[row - 5][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái dưới
		try {
			if (matrix[row + 1][col - 1] == val && matrix[row + 2][col - 2] == val && matrix[row + 3][col - 3] == val
					&& matrix[row + 4][col - 4] == val && matrix[row + 5][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái trên
		try {
			if (matrix[row - 1][col - 1] == val && matrix[row - 2][col - 2] == val && matrix[row - 3][col - 3] == val
					&& matrix[row - 4][col - 4] == val && matrix[row - 5][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải dưới
		try {
			if (matrix[row + 1][col + 1] == val && matrix[row + 2][col + 2] == val && matrix[row + 3][col + 3] == val
					&& matrix[row + 4][col + 4] == val && matrix[row + 5][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	public int getDeadThreeBlock1(int row, int col, int val, int enemy) {
		// check 8 hướng
		int re = 0;
		// sang phải
		try {
			if (matrix[row][col + 1] == val && matrix[row][col + 2] == val && matrix[row][col + 3] == val
					&& matrix[row][col + 4] == 0 && matrix[row][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// sang trái
		try {
			if (matrix[row][col - 1] == val && matrix[row][col - 2] == val && matrix[row][col - 3] == val
					&& matrix[row][col - 4] == 0 && matrix[row][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// xuống
		try {
			if (matrix[row + 1][col] == val && matrix[row + 2][col] == val && matrix[row + 3][col] == val
					&& matrix[row + 4][col] == 0 && matrix[row + 5][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// lên
		try {
			if (matrix[row - 1][col] == val && matrix[row - 2][col] == val && matrix[row - 3][col] == val
					&& matrix[row - 4][col] == 0 && matrix[row - 5][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải trên
		try {
			if (matrix[row - 1][col + 1] == val && matrix[row - 2][col + 2] == val && matrix[row - 3][col + 3] == val
					&& matrix[row - 4][col + 4] == 0 && matrix[row - 5][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái dưới
		try {
			if (matrix[row + 1][col - 1] == val && matrix[row + 2][col - 2] == val && matrix[row + 3][col - 3] == val
					&& matrix[row + 4][col - 4] == 0 && matrix[row + 5][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái trên
		try {
			if (matrix[row - 1][col - 1] == val && matrix[row - 2][col - 2] == val && matrix[row - 3][col - 3] == val
					&& matrix[row - 4][col - 4] == 0 && matrix[row - 5][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải dưới
		try {
			if (matrix[row + 1][col + 1] == val && matrix[row + 2][col + 2] == val && matrix[row + 3][col + 3] == val
					&& matrix[row + 4][col + 4] == 0 && matrix[row + 5][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	public int getDeadThreeBlock2(int row, int col, int val, int enemy) {
		// check 8 hướng
		int re = 0;
		// sang phải
		try {
			if (matrix[row][col + 1] == val && matrix[row][col + 2] == val && matrix[row][col + 3] == 0
					&& matrix[row][col + 4] == val && matrix[row][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// sang trái
		try {
			if (matrix[row][col - 1] == val && matrix[row][col - 2] == val && matrix[row][col - 3] == 0
					&& matrix[row][col - 4] == val && matrix[row][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// xuống
		try {
			if (matrix[row + 1][col] == val && matrix[row + 2][col] == val && matrix[row + 3][col] == 0
					&& matrix[row + 4][col] == val && matrix[row + 5][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// lên
		try {
			if (matrix[row - 1][col] == val && matrix[row - 2][col] == val && matrix[row - 3][col] == 0
					&& matrix[row - 4][col] == val && matrix[row - 5][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải trên
		try {
			if (matrix[row - 1][col + 1] == val && matrix[row - 2][col + 2] == val && matrix[row - 3][col + 3] == 0
					&& matrix[row - 4][col + 4] == val && matrix[row - 5][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái dưới
		try {
			if (matrix[row + 1][col - 1] == val && matrix[row + 2][col - 2] == val && matrix[row + 3][col - 3] == 0
					&& matrix[row + 4][col - 4] == val && matrix[row + 5][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái trên
		try {
			if (matrix[row - 1][col - 1] == val && matrix[row - 2][col - 2] == val && matrix[row - 3][col - 3] == 0
					&& matrix[row - 4][col - 4] == val && matrix[row - 5][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải dưới
		try {
			if (matrix[row + 1][col + 1] == val && matrix[row + 2][col + 2] == val && matrix[row + 3][col + 3] == 0
					&& matrix[row + 4][col + 4] == val && matrix[row + 5][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	public int getDeadThreeBlock3(int row, int col, int val, int enemy) {
		// check 8 hướng
		int re = 0;
		// sang phải
		try {
			if (matrix[row][col + 1] == val && matrix[row][col + 2] == 0 && matrix[row][col + 3] == val
					&& matrix[row][col + 4] == val && matrix[row][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// sang trái
		try {
			if (matrix[row][col - 1] == val && matrix[row][col - 2] == 0 && matrix[row][col - 3] == val
					&& matrix[row][col - 4] == val && matrix[row][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// xuống
		try {
			if (matrix[row + 1][col] == val && matrix[row + 2][col] == 0 && matrix[row + 3][col] == val
					&& matrix[row + 4][col] == val && matrix[row + 5][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// lên
		try {
			if (matrix[row - 1][col] == val && matrix[row - 2][col] == 0 && matrix[row - 3][col] == val
					&& matrix[row - 4][col] == val && matrix[row - 5][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải trên
		try {
			if (matrix[row - 1][col + 1] == val && matrix[row - 2][col + 2] == 0 && matrix[row - 3][col + 3] == val
					&& matrix[row - 4][col + 4] == val && matrix[row - 5][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái dưới
		try {
			if (matrix[row + 1][col - 1] == val && matrix[row + 2][col - 2] == 0 && matrix[row + 3][col - 3] == val
					&& matrix[row + 4][col - 4] == val && matrix[row + 5][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái trên
		try {
			if (matrix[row - 1][col - 1] == val && matrix[row - 2][col - 2] == 0 && matrix[row - 3][col - 3] == val
					&& matrix[row - 4][col - 4] == val && matrix[row - 5][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải dưới
		try {
			if (matrix[row + 1][col + 1] == val && matrix[row + 2][col + 2] == 0 && matrix[row + 3][col + 3] == val
					&& matrix[row + 4][col + 4] == val && matrix[row + 5][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	public int getDeadThreeBlock4(int row, int col, int val, int enemy) {
		// check 4 hướng
		int re = 0;
		// dòng
		try {
			if (matrix[row][col + 1] == 0 && matrix[row][col + 2] == val && matrix[row][col + 3] == val
					&& matrix[row][col + 4] == val && matrix[row][col + 5] == 0 && matrix[row][col + 6] == enemy)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// cột
		try {
			if (matrix[row + 1][col] == 0 && matrix[row + 2][col] == val && matrix[row + 3][col] == val
					&& matrix[row + 4][col] == val && matrix[row + 5][col] == 0 && matrix[row + 6][col] == enemy)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo chính
		try {
			if (matrix[row + 1][col + 1] == 0 && matrix[row + 2][col + 2] == val && matrix[row + 3][col + 3] == val
					&& matrix[row + 4][col + 4] == val && matrix[row + 5][col + 5] == 0
					&& matrix[row + 6][col + 6] == enemy)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phụ
		try {
			if (matrix[row + 1][col - 1] == 0 && matrix[row + 2][col - 2] == val && matrix[row + 3][col - 3] == val
					&& matrix[row + 4][col - 4] == val && matrix[row + 5][col - 5] == 0
					&& matrix[row + 6][col - 6] == enemy)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	public int getDeadTwoBlock1(int row, int col, int val, int enemy) {
		// check 8 hướng
		int re = 0;
		// sang phải
		try {
			if (matrix[row][col + 1] == val && matrix[row][col + 2] == val && matrix[row][col + 3] == 0
					&& matrix[row][col + 4] == 0 && matrix[row][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// sang trái
		try {
			if (matrix[row][col - 1] == val && matrix[row][col - 2] == val && matrix[row][col - 3] == 0
					&& matrix[row][col - 4] == 0 && matrix[row][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// xuống
		try {
			if (matrix[row + 1][col] == val && matrix[row + 2][col] == val && matrix[row + 3][col] == 0
					&& matrix[row + 4][col] == 0 && matrix[row + 5][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// lên
		try {
			if (matrix[row - 1][col] == val && matrix[row - 2][col] == val && matrix[row - 3][col] == 0
					&& matrix[row - 4][col] == 0 && matrix[row - 5][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải trên
		try {
			if (matrix[row - 1][col + 1] == val && matrix[row - 2][col + 2] == val && matrix[row - 3][col + 3] == 0
					&& matrix[row - 4][col + 4] == 0 && matrix[row - 5][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái dưới
		try {
			if (matrix[row + 1][col - 1] == val && matrix[row + 2][col - 2] == val && matrix[row + 3][col - 3] == 0
					&& matrix[row + 4][col - 4] == 0 && matrix[row + 5][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái trên
		try {
			if (matrix[row - 1][col - 1] == val && matrix[row - 2][col - 2] == val && matrix[row - 3][col - 3] == 0
					&& matrix[row - 4][col - 4] == 0 && matrix[row - 5][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải dưới
		try {
			if (matrix[row + 1][col + 1] == val && matrix[row + 2][col + 2] == val && matrix[row + 3][col + 3] == 0
					&& matrix[row + 4][col + 4] == 0 && matrix[row + 5][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	public int getDeadTwoBlock2(int row, int col, int val, int enemy) {
		// check 8 hướng
		int re = 0;
		// sang phải
		try {
			if (matrix[row][col + 1] == val && matrix[row][col + 2] == 0 && matrix[row][col + 3] == val
					&& matrix[row][col + 4] == 0 && matrix[row][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// sang trái
		try {
			if (matrix[row][col - 1] == val && matrix[row][col - 2] == 0 && matrix[row][col - 3] == val
					&& matrix[row][col - 4] == 0 && matrix[row][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// xuống
		try {
			if (matrix[row + 1][col] == val && matrix[row + 2][col] == 0 && matrix[row + 3][col] == val
					&& matrix[row + 4][col] == 0 && matrix[row + 5][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// lên
		try {
			if (matrix[row - 1][col] == val && matrix[row - 2][col] == 0 && matrix[row - 3][col] == val
					&& matrix[row - 4][col] == 0 && matrix[row - 5][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải trên
		try {
			if (matrix[row - 1][col + 1] == val && matrix[row - 2][col + 2] == 0 && matrix[row - 3][col + 3] == val
					&& matrix[row - 4][col + 4] == 0 && matrix[row - 5][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái dưới
		try {
			if (matrix[row + 1][col - 1] == val && matrix[row + 2][col - 2] == 0 && matrix[row + 3][col - 3] == val
					&& matrix[row + 4][col - 4] == 0 && matrix[row + 5][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái trên
		try {
			if (matrix[row - 1][col - 1] == val && matrix[row - 2][col - 2] == 0 && matrix[row - 3][col - 3] == val
					&& matrix[row - 4][col - 4] == 0 && matrix[row - 5][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải dưới
		try {
			if (matrix[row + 1][col + 1] == val && matrix[row + 2][col + 2] == 0 && matrix[row + 3][col + 3] == val
					&& matrix[row + 4][col + 4] == 0 && matrix[row + 5][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	public int getDeadTwoBlock3(int row, int col, int val, int enemy) {
		// check 8 hướng
		int re = 0;
		// sang phải
		try {
			if (matrix[row][col + 1] == val && matrix[row][col + 2] == 0 && matrix[row][col + 3] == 0
					&& matrix[row][col + 4] == val && matrix[row][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// sang trái
		try {
			if (matrix[row][col - 1] == val && matrix[row][col - 2] == 0 && matrix[row][col - 3] == 0
					&& matrix[row][col - 4] == val && matrix[row][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// xuống
		try {
			if (matrix[row + 1][col] == val && matrix[row + 2][col] == 0 && matrix[row + 3][col] == 0
					&& matrix[row + 4][col] == val && matrix[row + 5][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// lên
		try {
			if (matrix[row - 1][col] == val && matrix[row - 2][col] == 0 && matrix[row - 3][col] == 0
					&& matrix[row - 4][col] == val && matrix[row - 5][col] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải trên
		try {
			if (matrix[row - 1][col + 1] == val && matrix[row - 2][col + 2] == 0 && matrix[row - 3][col + 3] == 0
					&& matrix[row - 4][col + 4] == val && matrix[row - 5][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái dưới
		try {
			if (matrix[row + 1][col - 1] == val && matrix[row + 2][col - 2] == 0 && matrix[row + 3][col - 3] == 0
					&& matrix[row + 4][col - 4] == val && matrix[row + 5][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo trái trên
		try {
			if (matrix[row - 1][col - 1] == val && matrix[row - 2][col - 2] == 0 && matrix[row - 3][col - 3] == 0
					&& matrix[row - 4][col - 4] == val && matrix[row - 5][col - 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// chéo phải dưới
		try {
			if (matrix[row + 1][col + 1] == val && matrix[row + 2][col + 2] == 0 && matrix[row + 3][col + 3] == 0
					&& matrix[row + 4][col + 4] == val && matrix[row + 5][col + 5] == 0)
				re++;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return re;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Node))
			return false;
		Node that = (Node) obj;
		return Arrays.deepEquals(this.matrix, that.matrix);
	}

	// getters & setters

	public int[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(int[][] matrix) {
		this.matrix = matrix;
	}

	public int getRowMove() {
		return rowMove;
	}

	public void setRowMove(int rowMove) {
		this.rowMove = rowMove;
	}

	public int getColMove() {
		return colMove;
	}

	public void setColMove(int colMove) {
		this.colMove = colMove;
	}

//	public int getValue() {
//		return value;
//	}

	public void setValue(int value) {
		this.value = value;
	}

//	public List<Node> getChildren() {
//		return children;
//	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public Node getBestNextMove() {
		return bestNextMove;
	}

	public void setBestNextMove(Node bestNextMove) {
		this.bestNextMove = bestNextMove;
	}

}
