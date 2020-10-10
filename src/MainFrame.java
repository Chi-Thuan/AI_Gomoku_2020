import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.EventObject;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings(value = { "serial", "unused", "rawtypes", "unchecked" })
public class MainFrame extends JFrame {

	private Board board;
	private AI ai;
	private MyCanvas canvas;
	private JPanel panel;
	private JButton btnNewGame;
	private JButton btnUndo;
	private JButton btnAbout;
	private JButton btnSurrender;
	private JComboBox<String> cbbLevel;
	private JLabel lblColorX;
	private JLabel lblColorO;
	private JComboBox<String> cbbColorX;
	private JComboBox<String> cbbColorO;
	private JComboBox<String> cbbWhoFirst;
	private JComboBox<String> cbbRepresent;
	private JComboBox<String> cbbBoardSize;
	private JTextField txtScoreText;

	private MyEventQueue events;

	private byte thicknessX; // độ dày nét vẽ X
	private byte thicknessO;// độ dày nét vẽ O
	private byte marginBoardCell; /*
									 * cái này khá giống padding bên CSS, phần vẽ sẽ được căn lề vào, nếu số này quá
									 * lớn => chương trình sẽ chạy sai
									 */
	private byte marginBoard;
	private short boardSize;
	private short lengthCell;
	private int widthCanvas;
	private short widthPanel;
	private int height;
	private short widthButton;
	private short heightButton;
	private int userScore;
	private int aiScore;

	private Color defaultColorX;
	private Color defaultColorO;
	private Color defaultColorBoard;
	private Color colorX;
	private Color colorO;
	private Color colorBoard;

	private String[] levelData = { "Minimax", "Alpha—Beta Pruning" };
	private String[] colorSelectionData = { "Default", "Black", "Blue", "Cyan", "Dark Gray", "Gray", "Green",
			"Light Gray", "Magenta", "Orange", "Pink", "Red", "White", "Yellow" };
	private String[] whoFirstData = { "User plays first", "Computer plays first" };
	private String[] representData = { "Plays as X", "Plays as O" };
	private String[] boardSizeData = { "Default", "3 x 3", "5 x 5", "10 x 10", "15 x 15", "20 x 20", "25 x 25",
			"30 x 30" };

	public MainFrame() {
		super("Gomoku");
		thicknessX = 4;
		thicknessO = 4;
		marginBoardCell = 2;
		colorX = defaultColorX = Color.ORANGE;
		colorO = defaultColorO = Color.GREEN;
		colorBoard = defaultColorBoard = Color.GRAY;
		board = new Board(this);
		ai = new AI(board, this);
		initGUI();
		initEventListener();
		getLengthCell();
		init();
	}

	public void initGUI() {
		boardSize = 600;
		marginBoard = 10;
		widthCanvas = boardSize + 2 * marginBoard;
		height = widthCanvas;
		canvas = new MyCanvas();
		canvas.setBounds(0, 0, widthCanvas, height);
		add(canvas);

		widthPanel = 400;
		panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(widthCanvas, 0, widthPanel, height);
		add(panel);

		widthButton = 120;
		heightButton = 30;
		int marginButton = 50;
		short scoreTextWidth = 250;
		int textPositionX = widthCanvas + (widthPanel - scoreTextWidth) / 2;
		panel.add(txtScoreText = new JTextField("User " + userScore + " : " + aiScore + " Computer"));
		txtScoreText.setEditable(false);
		txtScoreText.setFocusable(false);
		txtScoreText.setHorizontalAlignment(JTextField.CENTER);
		txtScoreText.setBounds(textPositionX, marginButton, scoreTextWidth, heightButton);

		int buttonPositionX = widthCanvas + (widthPanel - widthButton) / 2;
		panel.add(btnNewGame = new JButton("New Game"));
		btnNewGame.setFocusPainted(false);
		btnNewGame.setBounds(buttonPositionX, 2 * marginButton, widthButton, heightButton);

		panel.add(btnSurrender = new JButton("Surrender"));
		btnSurrender.setFocusPainted(false);
		btnSurrender.setBounds(buttonPositionX, 3 * marginButton, widthButton, heightButton);

		panel.add(btnUndo = new JButton("Undo"));
		btnUndo.setFocusPainted(false);
		btnUndo.setBounds(buttonPositionX, 4 * marginButton, widthButton, heightButton);

		int labelPositionX = widthCanvas + marginButton;
		JLabel lblBoardSize = new JLabel("Board size:");
		panel.add(lblBoardSize);
		lblBoardSize.setBounds(labelPositionX, 5 * marginButton, widthButton, heightButton);

		short widthCombobox = 160;
		panel.add(cbbBoardSize = new JComboBox<>(boardSizeData));
		cbbBoardSize.setBounds(labelPositionX + widthButton, 5 * marginButton, widthCombobox, heightButton);

		JLabel lblLevel = new JLabel("Level:");
		panel.add(lblLevel);
		lblLevel.setBounds(labelPositionX, 6 * marginButton, widthButton, heightButton);

		panel.add(cbbLevel = new JComboBox<>(levelData));
		cbbLevel.setBounds(labelPositionX + widthButton, 6 * marginButton, widthCombobox, heightButton);

		JLabel lblWhoFirst = new JLabel("Plays first:");
		panel.add(lblWhoFirst);
		lblWhoFirst.setBounds(labelPositionX, 7 * marginButton, widthButton, heightButton);

		panel.add(cbbWhoFirst = new JComboBox<>(whoFirstData));
		cbbWhoFirst.setBounds(labelPositionX + widthButton, 7 * marginButton, widthCombobox, heightButton);

		JLabel lblRepresent = new JLabel("User:");
		panel.add(lblRepresent);
		lblRepresent.setBounds(labelPositionX, 8 * marginButton, widthButton, heightButton);

		panel.add(cbbRepresent = new JComboBox<>(representData));
		cbbRepresent.setBounds(labelPositionX + widthButton, 8 * marginButton, widthCombobox, heightButton);

		lblColorX = new JLabel("Color of X:");
		panel.add(lblColorX);
		lblColorX.setBounds(labelPositionX, 9 * marginButton, widthButton, heightButton);
		lblColorX.setOpaque(true);
		lblColorX.setBackground(colorX);

		panel.add(cbbColorX = new JComboBox<>(colorSelectionData));
		cbbColorX.setBounds(labelPositionX + widthButton, 9 * marginButton, widthCombobox, heightButton);
		cbbColorX.setRenderer(new MyRendererCombobox(cbbColorX.getRenderer()));

		lblColorO = new JLabel("Color of O:");
		panel.add(lblColorO);
		lblColorO.setBounds(labelPositionX, 10 * marginButton, widthButton, heightButton);
		lblColorO.setOpaque(true);
		lblColorO.setBackground(colorO);

		panel.add(cbbColorO = new JComboBox<>(colorSelectionData));
		cbbColorO.setBounds(labelPositionX + widthButton, 10 * marginButton, widthCombobox, heightButton);
		cbbColorO.setRenderer(new MyRendererCombobox(cbbColorO.getRenderer()));

		panel.add(btnAbout = new JButton("About"));
		btnAbout.setFocusPainted(false);
		btnAbout.setBounds(buttonPositionX, 11 * marginButton, widthButton, heightButton);
	}

	public void initEventListener() {
		events = new MyEventQueue();
		events.listenTo(canvas, "canvas");
		events.listenTo(btnNewGame, "NewGame");
		events.listenTo(btnUndo, "Undo");
		events.listenTo(btnAbout, "About");
		events.listenTo(btnSurrender, "Surrender");
		events.listenTo(cbbLevel, "Level");
		events.listenTo(cbbBoardSize, "BoardSize");
		events.listenTo(cbbWhoFirst, "whoFirst");
		events.listenTo(cbbRepresent, "Represent");
		events.listenTo(cbbColorX, "ColorX");
		events.listenTo(cbbColorO, "ColorO");
	}

	public void init() {
		setMinimumSize(new Dimension(widthCanvas + widthPanel, height + 50));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			System.out.println("setLookAndFeel => Failed");
			e.printStackTrace();
		}
		setVisible(true);
	}

	public void clearBoard() {
		canvas.setBackground(panel.getBackground());
	}

	public void clearScore() {
		userScore = 0;
		aiScore = 0;
		txtScoreText.setText("User " + userScore + " : " + aiScore + " Computer");

	}

	public short getLengthCell() {
		return lengthCell = (short) (boardSize / board.getN());
	}

	public void drawBoard() {
		getLengthCell();
		byte x1 = marginBoard;
		int x2 = marginBoard + boardSize;
		byte y1 = marginBoard;
		int y2 = marginBoard + boardSize;
		canvas.setColor(colorBoard);
		for (byte i = 0; i <= board.getN(); i++) {
			canvas.drawLine(x1, y1 + i * lengthCell, x2, y1 + i * lengthCell);
			canvas.drawLine(x1 + i * lengthCell, y1, x1 + i * lengthCell, y2);
		}
	}

	public void drawX(int boardX, int boardY) {
		int x1 = marginBoard + boardX * lengthCell;
		int y1 = marginBoard + boardY * lengthCell;
		int x2 = x1 + lengthCell;
		int y2 = y1 + lengthCell;
		x1 += marginBoardCell;
		y1 += marginBoardCell;
		x2 -= marginBoardCell;
		y2 -= marginBoardCell;
		canvas.setColor(colorX);
		for (int i = 0; i <= thicknessX; i++) {
			canvas.drawLine(x1, y1 + i, x2 - i, y2);
			canvas.drawLine(x1 + i, y1, x2, y2 - i);
			canvas.drawLine(x1, y2 - i, x2 - i, y1);
			canvas.drawLine(x1 + i, y2, x2, y1 + i);
		}
	}

	public void drawO(int boardX, int boardY) {
		int x = marginBoard + boardX * lengthCell + marginBoardCell;
		int y = marginBoard + boardY * lengthCell + marginBoardCell;
		int diameter = lengthCell - 2 * marginBoardCell;
		canvas.setColor(colorO);
		for (byte i = 0; i <= thicknessO; i++)
			canvas.drawOval(x + i, y + i, diameter - 2 * i, diameter - 2 * i);
	}

	public void updateMove(boolean userMove, int boardX, int boardY) {
		if (userMove) {
			if (Board.userX)
				drawX(boardX, boardY);
			else
				drawO(boardX, boardY);
		} else {
			if (Board.userX)
				drawO(boardX, boardY);
			else
				drawX(boardX, boardY);
		}
	}

	public void play() {
		clearBoard();
		drawBoard();

		board.clearData();

		if (!board.isHumanFirst()) { // nếu là máy chơi trước
			Board.nSteps = 1;
			ai.move();
			// TODO code xử lý trong method move() cho AI
			// có thể thêm nhiều tham số cho method này, tạm thời để vậy
		}
		EventObject anEvent;
		while (true) {

			anEvent = events.waitEvent();
			if (events.isMouseEvent(anEvent))
				if (events.isMousePressed(anEvent)) {
					int mouseX = events.getMouseX(anEvent);
					int mouseY = events.getMouseY(anEvent);
					if ((mouseX > marginBoard) && (mouseX < marginBoard + boardSize))
						if ((mouseY > marginBoard) && (mouseY < marginBoard + boardSize)) {
							int boardX = (mouseX - marginBoard) / lengthCell;
							int boardY = (mouseY - marginBoard) / lengthCell;

							if (board.isCanMove(boardY, boardX)) {
								board.addMove(boardY, boardX);

								updateMove(true, boardX, boardY);
								//
								// if (ai.checkFinalState()) {
								// // TODO xử lý end game
								// return;
								// }

								ai.move();
								// if (ai.checkFinalState()) {
								// // TODO xử lý end game
								// return;
								// }

							}
						}
				}

			String name = events.getName(anEvent);

			if (name.equals("NewGame")) {
				clearScore();
				play();
				return;
			}

			if (name.equals("Surrender")) {
				aiScore++;
				txtScoreText.setText("User " + userScore + " : " + aiScore + " Computer");
				JOptionPane.showMessageDialog(this, SURRENDER, "Surrender", JOptionPane.INFORMATION_MESSAGE);
				play();
				return;
			}

			if (name.equals("Undo")) {
				undoMove();
				continue;
			}
			if (name.equals("About")) {
				JOptionPane.showMessageDialog(this, ABOUT, "About", JOptionPane.INFORMATION_MESSAGE);
				continue;
			}

			if (name.equals("Level")) {
				// TODO chá»�n thuáº­t toĂ¡n cho AI sá»­ dá»¥ng, ...
			}

			if (name.equals("ColorX")) {
				changeColor("X", cbbColorX.getSelectedIndex());
				continue;
			}

			if (name.equals("ColorO")) {
				changeColor("O", cbbColorO.getSelectedIndex());
				continue;
			}

			if (name.equals("whoFirst")) {
				if (cbbWhoFirst.getSelectedIndex() == 0) {
					if (!Board.humanFirst) {
						board.setHumanFirst(true);

						play();
						// TODO xử lý khi đang là máy chơi trước,
						// bây giờ user chọn thành người chơi trước
						return;
					}
				} else {
					board.setHumanFirst(false);
					play();
					// TODO xử lý khi đang là người chơi trước,
					// bây giờ user chọn thành máy chơi trước
					return;

				}
			}

			if (name.equals("Represent")) {
				if (cbbRepresent.getSelectedIndex() == 0) {
					if (!Board.userX) {
						board.setUserX(true);
						reDrawXO();
					}
				} else if (Board.userX) {
					board.setUserX(false);
					reDrawXO();
				}
				continue;
			}

			if (name.equals("BoardSize")) {
				if (getBoardSize(cbbBoardSize.getSelectedIndex()) != board.getN()) {
					board.setN(getBoardSize(cbbBoardSize.getSelectedIndex()));
					play();
					// TODO khi Ä‘á»•i kĂ­ch thÆ°á»›c bĂ n cá»�
					return;
				}
				continue;
			}
			unfocusAllCombobox();

		}

	}

	public byte getBoardSize(int index) {
		if (index == 0)
			return board.getDefaultN();
		if (index == 1)
			return 3;
		if (index == 2)
			return 5;
		if (index == 3)
			return 10;
		if (index == 4)
			return 15;
		if (index == 5)
			return 20;
		if (index == 6)
			return 25;
		return 30;
	}

	public boolean isDarkGroup(Color color) {
		if (color == Color.BLACK || color == Color.BLUE || color == Color.DARK_GRAY)
			return true;
		return false;
	}

	public void changeColor(String type, int index) {
		switch (type) {
		case "X":
			if (colorSelectionData[index].equals("Default"))
				colorX = defaultColorX;
			else
				colorX = getColor(colorSelectionData[index]);
			lblColorX.setBackground(colorX);
			if (isDarkGroup(colorX))
				lblColorX.setForeground(Color.WHITE);
			else
				lblColorX.setForeground(Color.BLACK);
			reDrawX();
			break;

		case "O":
			if (colorSelectionData[index].equals("Default"))
				colorO = defaultColorO;
			else
				colorO = getColor(colorSelectionData[index]);
			lblColorO.setBackground(colorO);
			if (isDarkGroup(colorO))
				lblColorO.setForeground(Color.WHITE);
			else
				lblColorO.setForeground(Color.BLACK);
			reDrawO();
			break;
		}

	}

	public void reDrawX() {
		for (short i = 0; i < Board.nSteps; i++)
			if (!Board.humanFirst) {
				if ((i % 2 == 0) && (!Board.userX))
					drawX(Board.y[i], Board.x[i]);
				if ((i % 2 == 1) && (Board.userX))
					drawX(Board.y[i], Board.x[i]);
			} else {
				if ((i % 2 == 0) && (Board.userX))
					drawX(Board.y[i], Board.x[i]);
				if ((i % 2 == 1) && (!Board.userX))
					drawX(Board.y[i], Board.x[i]);
			}
	}

	public void reDrawO() {
		boolean userO = false;
		if (!Board.userX)
			userO = true;

		for (short i = 0; i < Board.nSteps; i++)
			if (!Board.humanFirst) {
				if ((i % 2 == 0) && (!userO))
					drawO(Board.y[i], Board.x[i]);
				if ((i % 2 == 1) && (userO))
					drawO(Board.y[i], Board.x[i]);
			} else {
				if ((i % 2 == 0) && (userO))
					drawO(Board.y[i], Board.x[i]);
				if ((i % 2 == 1) && (!userO))
					drawO(Board.y[i], Board.x[i]);
			}
	}

	public void reDrawXO() {
		for (short i = 0; i < Board.nSteps; i++)
			clearCell(Board.y[i], Board.x[i]);
		reDrawX();
		reDrawO();
	}

	public Color getColor(String s) {
		if (s.equals("Black"))
			return Color.BLACK;
		if (s.equals("Blue"))
			return Color.BLUE;
		if (s.equals("Cyan"))
			return Color.CYAN;
		if (s.equals("Dark Gray"))
			return Color.DARK_GRAY;
		if (s.equals("Gray"))
			return Color.GRAY;
		if (s.equals("Green"))
			return Color.GREEN;
		if (s.equals("Light Gray"))
			return Color.LIGHT_GRAY;
		if (s.equals("Magenta"))
			return Color.MAGENTA;
		if (s.equals("Orange"))
			return Color.ORANGE;
		if (s.equals("Pink"))
			return Color.PINK;
		if (s.equals("Red"))
			return Color.RED;
		if (s.equals("White"))
			return Color.WHITE;
		if (s.equals("Yellow"))
			return Color.YELLOW;
		return Color.GRAY;
	}

	public void undoMove() {
		if (board.getnSteps() == 0)
			return;
		if (board.getnSteps() == 1) {
			JOptionPane.showMessageDialog(this, "You cannot undo!", "Notice", JOptionPane.ERROR_MESSAGE);
			return;
		}
		board.deleteMove();
	}

	public void clearCell(int boardX, int boardY) {
		int x1 = marginBoard + boardX * lengthCell;
		int y1 = marginBoard + boardY * lengthCell;
		int length = lengthCell - 2;
		canvas.setColor(panel.getBackground());
		canvas.fillRect(x1 + 1, y1 + 1, length, length);
	}

	public void unfocusAllCombobox() {
		cbbBoardSize.setFocusable(false);
		cbbLevel.setFocusable(false);
		cbbWhoFirst.setFocusable(false);
		cbbRepresent.setFocusable(false);
		cbbColorX.setFocusable(false);
		cbbColorO.setFocusable(false);
	}

	public static final String ABOUT = String.format("%s\n%s\n%s\n%s %s\n%s\n%s\n%s\n%s\n%s\n%s\n\n\n",
			"Software: Gomoku — Assessment project", "Course: Artificial Itelligence Fundamentals",
			"Lecturer: Van Du Nguyen, Ph.D", "Authors:", "18130089 — Vo Doan Minh Huan",
			"                 18130128 — Vo Duy Loc", "                 18130157 — Le Viet Nha",
			"                 18130232 — Huynh Chi Thuan", "Class: 2018 — 2022", "Institution: Nong Lam University",
			"Copyright © 2020, Faculty of Information Technology. All rights reserved.");

	class MyRendererCombobox extends DefaultListCellRenderer {
		private ListCellRenderer defaultRenderer;
		private Component c;

		public MyRendererCombobox(ListCellRenderer defaultRenderer) {
			this.defaultRenderer = defaultRenderer;
		}

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			c = defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (c instanceof JLabel)
				if (isSelected)
					list.setSelectionBackground(getColor((String) value));
			return c;

		}
	}

	public static final String X_WIN = "X Win";
	public static final String O_WIN = "O Win";
	public static final String DRAW = "DRAW";
	public static final String SURRENDER = "SURRENDER!!!";

	public void showDialogEndGame(int winner) {
		if (board.userX) {
			if (winner == -1) // white == true => Black wins
				JOptionPane.showMessageDialog(null, X_WIN);
			else if (winner == 1)
				JOptionPane.showMessageDialog(null, O_WIN);
			else
				JOptionPane.showMessageDialog(null, DRAW);
		} else {
			if (winner == 1) // white == true => Black wins
				JOptionPane.showMessageDialog(null, X_WIN);
			else if (winner == -1)
				JOptionPane.showMessageDialog(null, O_WIN);
			else
				JOptionPane.showMessageDialog(null, DRAW);
		}
		play();
	}

	public void getScore(int winner) {
		if (board.isHumanFirst() == true) {
			if (board.userX) {
				if (winner == -1) {
					userScore++;
					txtScoreText.setText("User " + userScore + " : " + aiScore + " Computer");
				} else if (winner == 1) {
					aiScore++;
					txtScoreText.setText("User " + userScore + " : " + aiScore + " Computer");
				}

			} else {

				if (winner == 1) {
					aiScore++;
					txtScoreText.setText("User " + userScore + " : " + aiScore + " Computer");

				} else if (winner == -1) {
					userScore++;
					txtScoreText.setText("User " + userScore + " : " + aiScore + " Computer");
				}
			}
		}else {
			if (board.userX) {
				if (winner == -1) {
					aiScore++;;
					txtScoreText.setText("User " + userScore + " : " + aiScore + " Computer");
				} else if (winner == 1) {
					userScore++;
					txtScoreText.setText("User " + userScore + " : " + aiScore + " Computer");
				}

			} else {

				if (winner == 1) {
					userScore++;
					txtScoreText.setText("User " + userScore + " : " + aiScore + " Computer");

				} else if (winner == -1) {
					aiScore++;
					txtScoreText.setText("User " + userScore + " : " + aiScore + " Computer");
				}
			}
		}

	}

}
