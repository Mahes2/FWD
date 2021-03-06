import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

@SuppressWarnings("serial")
public class TicTacToe extends JFrame {
	
	public static final int DEFAULT_RC = 3;
	public static int ROWS = DEFAULT_RC;
	public static int COLS = DEFAULT_RC;

	public static final int CELL_SIZE = 100;
	public static int CANVAS_WIDTH = CELL_SIZE * COLS;
	public static int CANVAS_HEIGHT = CELL_SIZE * ROWS;
	public static final int GRID_WIDTH = 8;
	public static final int GRID_WIDHT_HALF = GRID_WIDTH / 2;

	public static final int CELL_PADDING = CELL_SIZE / 6;
	public static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2;
	public static final int SYMBOL_STROKE_WIDTH = 8;

	public enum GameState {
		PLAYING, DRAW, CROSS_WON, NOUGHT_WON
	}
	
	private GameState currentState;

	public enum Seed {
		EMPTY, CROSS, NOUGHT
	}
	
	private Seed currentPlayer;

	private Seed[][] board   ;
	private DrawCanvas canvas;
	private JLabel statusBar;

	private JButton button;
	private JTextField tfield;

	private void drawCanvas() {
		canvas = new DrawCanvas();
		canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int mouseX = e.getX();
				int mouseY = e.getY();

				int rowSelected = mouseY / CELL_SIZE;
				int colSelected = mouseX / CELL_SIZE;

				if (currentState == GameState.PLAYING) {
					if (rowSelected >= 0 && rowSelected < ROWS && colSelected >= 0
							&& colSelected < COLS && board[rowSelected][colSelected] == Seed.EMPTY) {
						board[rowSelected][colSelected] = currentPlayer;
						updateGame(currentPlayer, rowSelected, colSelected);

						currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
					}
				}

				repaint();
			}
		});
	}

	public TicTacToe() {
		drawCanvas();

		statusBar = new JLabel("  ");
		statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));

		button = new JButton();
		button.setText("Start");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ROWS = Integer.valueOf(tfield.getText());
				COLS = Integer.valueOf(tfield.getText());
				CANVAS_WIDTH = CELL_SIZE * COLS;
				CANVAS_HEIGHT = CELL_SIZE * ROWS;
				canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
				pack();
				board = new Seed[ROWS][COLS];
				repaint();
				initGame();
			}
		});

		NumberFormat format = NumberFormat.getInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(3);
		formatter.setMaximum(99);
		formatter.setAllowsInvalid(false);

		tfield = new JFormattedTextField(formatter){
			@Override
			public Dimension getPreferredSize() {
				Dimension superPref = super.getPreferredSize();
				FontMetrics fontMetrics = getFontMetrics(getFont());
				String text = getText();
				if (text.length() > 0) {
					int width = fontMetrics.charsWidth(text.toCharArray(), 0, text.length()) + 0;
					return new Dimension(width, superPref.height);
				}
				return superPref;
			}
		};

		tfield.setText(Integer.toString(DEFAULT_RC));
		tfield.setMaximumSize(new Dimension(150, 20));
		tfield.setHorizontalAlignment(JTextField.CENTER);

		Container cp = getContentPane();

		JPanel jp = new JPanel();
		jp.setPreferredSize(new Dimension(150, 0));
		jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
		tfield.setAlignmentX(Component.CENTER_ALIGNMENT);
		jp.add(tfield);
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		jp.add(button);

		cp.setLayout(new BorderLayout());
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(statusBar, BorderLayout.PAGE_END);
		cp.add(jp, BorderLayout.EAST);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setResizable(false);
		setTitle("Tic Tac Toe");
		setVisible(true);

		board = new Seed[ROWS][COLS];
	}

	public void initGame() {
		for (int row = 0; row < ROWS; ++row) {
			for (int col = 0; col < COLS; ++col) {
				board[row][col] = Seed.EMPTY;
			}
		}
		currentState = GameState.PLAYING;
		currentPlayer = Seed.CROSS;
	}

	public void updateGame(Seed theSeed, int rowSelected, int colSelected) {
		if (hasWon(theSeed, rowSelected, colSelected)) {
			currentState = (theSeed == Seed.CROSS) ? GameState.CROSS_WON : GameState.NOUGHT_WON;
		} else if (isDraw()) {
			currentState = GameState.DRAW;
		}
	}

	public boolean isDraw() {
		for (int row = 0; row < ROWS; ++row) {
			for (int col = 0; col < COLS; ++col) {
				if (board[row][col] == Seed.EMPTY) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean hasWon(Seed theSeed, int rowSelected, int colSelected) {
		return  checkRow(theSeed, rowSelected) || 
				checkCol(theSeed, colSelected) || 
				checkDiagLeft(theSeed, rowSelected, colSelected) ||
				checkDiagRight(theSeed, rowSelected, colSelected);
	}

	private boolean checkRow(Seed theSeed, int rowSelected) {
		for(int i=0;i<ROWS;i++) {
			if(board[rowSelected][i] != theSeed) {
				return false;
			}
		}
		return true;
	}

	private boolean checkCol(Seed theSeed, int colSelected) {
		for(int i=0;i<COLS;i++) {
			if(board[i][colSelected] != theSeed) {
				return false;
			}
		}
		return true;
	}

	private boolean checkDiagLeft(Seed theSeed, int rowSelected, int colSelected) {
		if(rowSelected != colSelected) {
			return  false;
		}
		for(int i=0;i<ROWS;i++) {
			for(int j=0;j<COLS;j++) {
				if(i == j && board[i][j] != theSeed) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean checkDiagRight(Seed theSeed, int rowSelected, int colSelected) {
		if(rowSelected + colSelected != ROWS - 1) {
			return  false;
		}
		for(int i=0;i<ROWS;i++) {
			for(int j=0;j<COLS;j++) {
				if((i + j == ROWS - 1) && board[i][j] != theSeed) {
					return false;
				}
			}
		}
		return true;
	}

	class DrawCanvas extends JPanel {
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			setBackground(Color.WHITE); 

			g.setColor(Color.LIGHT_GRAY);
			for (int row = 1; row < ROWS; ++row) {
				g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDHT_HALF,
						CANVAS_WIDTH-1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH);
			}
			for (int col = 1; col < COLS; ++col) {
				g.fillRoundRect(CELL_SIZE * col - GRID_WIDHT_HALF, 0,
						GRID_WIDTH, CANVAS_HEIGHT-1, GRID_WIDTH, GRID_WIDTH);
			}

			Graphics2D g2d = (Graphics2D)g;
			g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND));
			for (int row = 0; row < ROWS; ++row) {
				for (int col = 0; col < COLS; ++col) {
					int x1 = col * CELL_SIZE + CELL_PADDING;
					int y1 = row * CELL_SIZE + CELL_PADDING;
					if (board[row][col] == Seed.CROSS) {
						g2d.setColor(Color.RED);
						int x2 = (col + 1) * CELL_SIZE - CELL_PADDING;
						int y2 = (row + 1) * CELL_SIZE - CELL_PADDING;
						g2d.drawLine(x1, y1, x2, y2);
						g2d.drawLine(x2, y1, x1, y2);
					} else if (board[row][col] == Seed.NOUGHT) {
						g2d.setColor(Color.BLUE);
						g2d.drawOval(x1, y1, SYMBOL_SIZE, SYMBOL_SIZE);
					}
				}
			}

			if (currentState == GameState.PLAYING) {
				statusBar.setForeground(Color.BLACK);
				if (currentPlayer == Seed.CROSS) {
					statusBar.setText("X's Turn");
				} else {
					statusBar.setText("O's Turn");
				}
			} else if (currentState == GameState.DRAW) {
				statusBar.setForeground(Color.RED);
				statusBar.setText("It's a Draw! Click Start to play again.");
			} else if (currentState == GameState.CROSS_WON) {
				statusBar.setForeground(Color.RED);
				statusBar.setText("'X' Won! Click Start to play again.");
			} else if (currentState == GameState.NOUGHT_WON) {
				statusBar.setForeground(Color.RED);
				statusBar.setText("'O' Won! Click Start to play again.");
			}
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new TicTacToe();
			}
		});
	}
}