package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
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

import ai.AI;
import model.Board;

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
	private int algo = 0;
	private JLabel lblColorX;
	private JLabel lblColorO;
	private JLabel lblState;
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
		ai = new AI(board);
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

		panel.add(lblState = new JLabel("TURN OF USER"));
		lblState.setFont(new Font(lblState.getFont().getName(), Font.BOLD, 16));
		lblState.setHorizontalAlignment(JLabel.CENTER);
		lblState.setBounds(textPositionX, marginButton - 40, scoreTextWidth, heightButton);

		panel.add(txtScoreText = new JTextField("User " + 0 + " : " + 0 + " Computer"));
		txtScoreText.setEditable(false);
		txtScoreText.setFocusable(false);
		txtScoreText.setHorizontalAlignment(JTextField.CENTER);
		txtScoreText.setBounds(textPositionX, marginButton, scoreTextWidth, heightButton);

		int buttonPositionX = widthCanvas + (widthPanel - widthButton) / 2;
		panel.add(btnNewGame = new JButton("New Game"));
		btnNewGame.setFocusPainted(false);
		btnNewGame.setBounds(buttonPositionX, 2 * marginButton, widthButton, heightButton);

		panel.add(btnUndo = new JButton("Undo"));
		btnUndo.setFocusPainted(false);
		btnUndo.setBounds(buttonPositionX, 3 * marginButton, widthButton, heightButton);

		panel.add(btnSurrender = new JButton("Surrender"));
		btnSurrender.setFocusPainted(false);
		btnSurrender.setBounds(buttonPositionX, 4 * marginButton, widthButton, heightButton);

		int labelPositionX = widthCanvas + marginButton;
		JLabel lblBoardSize = new JLabel("Board size:");
		panel.add(lblBoardSize);
		lblBoardSize.setBounds(labelPositionX, 5 * marginButton, widthButton, heightButton);

		short widthCombobox = 160;
		panel.add(cbbBoardSize = new JComboBox<>(boardSizeData));
		cbbBoardSize.setBounds(labelPositionX + widthButton, 5 * marginButton, widthCombobox, heightButton);

		JLabel lblLevel = new JLabel("Algorithms:");
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

	public void updateScore() {
		txtScoreText.setText("User " + board.getnUserWin() + " : " + board.getnComputerWin() + " Computer");
	}

	public void setStateText(String turn) {
		switch (turn) {
		case "User":
			lblState.setText("TURN OF USER");
			break;
		case "Computer":
			lblState.setText("TURN OF COMPUTER");
			break;
		case "Over":
			lblState.setText("GAME OVER");
			break;
		}
	}

	public void play() {
		clearBoard();
		drawBoard();
		board.clearData();

		if (!board.isHumanFirst()) { // nếu là máy chơi trước
			setStateText("Computer");
			ai.moveFirst();
			setStateText("User");
			// TODO code xử lý trong method move() cho AI
			// có thể thêm nhiều tham số cho method này, tạm thời để vậy
		} else
			setStateText("User");
		EventObject anEvent;
		while (true) {

			anEvent = events.waitEvent();
			if (events.isMouseEvent(anEvent))
				if (events.isMousePressed(anEvent) && !Board.isGameOver) {
					int mouseX = events.getMouseX(anEvent);
					int mouseY = events.getMouseY(anEvent);
					if ((mouseX > marginBoard) && (mouseX < marginBoard + boardSize))
						if ((mouseY > marginBoard) && (mouseY < marginBoard + boardSize)) {
							int boardX = (mouseX - marginBoard) / lengthCell;
							int boardY = (mouseY - marginBoard) / lengthCell;
							if (Board.isCanMove(boardY, boardX)) {
								board.addMove(boardY, boardX);

								if (!Board.isGameOver) {
									setStateText("Computer");
									ai.move(algo);
									setStateText((Board.isGameOver) ? "Over" : "User");

								} else
									setStateText("Over");
							}
						}
				}

			String name = events.getName(anEvent);

			if (name.equals("NewGame")) {
//				clearScore();
				play();
				return;
			}

			if (name.equals("Surrender"))
				if (!Board.isGameOver && !board.isEmpty()) {
					JOptionPane.showMessageDialog(this, SURRENDER, "Surrender", JOptionPane.INFORMATION_MESSAGE);
					board.setnComputerWin((short) (board.getnComputerWin() + 1));
					updateScore();
					play();
					return;
				}

			if (name.equals("Undo"))
				if (!Board.isGameOver) {
					undoMove();
					continue;
				}

			if (name.equals("About")) {
				JOptionPane.showMessageDialog(this, ABOUT, "About", JOptionPane.INFORMATION_MESSAGE);
				continue;
			}

			if (name.equals("Level")) {
				algo = cbbLevel.getSelectedIndex();
			}

			if (name.equals("ColorX")) {
				changeColor("X", cbbColorX.getSelectedIndex());
				continue;
			}

			if (name.equals("ColorO")) {
				changeColor("O", cbbColorO.getSelectedIndex());
				continue;
			}

			if (name.equals("whoFirst"))
				if (cbbWhoFirst.getSelectedIndex() == 0) {
					if (!Board.humanFirst) {
						board.setHumanFirst(true);
						play();
						return;
					}
				} else if (Board.humanFirst) {
					board.setHumanFirst(false);
					play();
					return;
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
					return;
				}
				continue;
			}
			unfocusAll();
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

	public void unfocusAll() {
		btnNewGame.setFocusable(false);
		btnUndo.setFocusable(false);
		btnSurrender.setFocusable(false);
		btnAbout.setFocusable(false);
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

	public static final String USER_WIN = "User Win\n DO YOU WANT TO PLAY NEWGAME ?";
	public static final String BOT_WIN = "Computer Win\n DO YOU WANT TO PLAY NEWGAME ?";
	public static final String DRAW = "DRAW\n DO YOU WANT TO PLAY NEWGAME ?";
	public static final String SURRENDER = "SURRENDER!!!";
	private int option;

	public void showDialogEndGame(int winner) {
		if (winner == -1)
			option = JOptionPane.showConfirmDialog(null, USER_WIN, "GAME OVER!!!", JOptionPane.YES_NO_OPTION);
		else if (winner == 1)
			option = JOptionPane.showConfirmDialog(null, BOT_WIN, "GAME OVER!!!", JOptionPane.YES_NO_OPTION);
		else
			option = JOptionPane.showConfirmDialog(null, DRAW, "GAME OVER!!!", JOptionPane.YES_NO_OPTION);

		if (option == JOptionPane.YES_OPTION)
			play();
	}
}
