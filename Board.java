// Board.java
package edu.stanford.cs108.tetris;

import java.util.Arrays;

/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
*/
public class Board	{
	// Some ivars are stubbed out for you:
	private int width;
	private int height;
	private boolean[][] grid;
	private boolean DEBUG = true;
	boolean committed;

	private int[] widths, xWidths;
	private int[] heights, xHeights;
	private int maxHeight, xmaxHeight;
	private boolean[][] xGrid;
	
	
	// Here a few trivial methods are provided:
	
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	*/
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new boolean[width][height];
		committed = true;
		
		// YOUR CODE HERE
		widths = new int[height];
		heights = new int[width];
		xWidths = new int[height];
		xHeights = new int[width];
		xGrid = new boolean[width][height];
		maxHeight = 0;
		xmaxHeight = 0;
	}
	
	
	/**
	 Returns the width of the board in blocks.
	*/
	public int getWidth() {
		return width;
	}
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() {
		return height;
	}
	
	
	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	*/
	public int getMaxHeight() {	 
		return maxHeight; // YOUR CODE HERE
	}
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	*/
	public void sanityCheck() {
		if (DEBUG) {
			// YOUR CODE HERE
			int[] widthsCheck = new int[height];
			int[] heightsCheck = new int[width];
			int maxHeightCheck = 0;

			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					if (grid[i][j]) {
						widthsCheck[j]++;
						heightsCheck[i] = j + 1;
						if (j + 1 > maxHeightCheck) {
							maxHeightCheck = j + 1;
						}
					}
				}
			}

//			System.out.print("max height check: " + Integer.toString(maxHeightCheck));
//			System.out.print("max height actual: " + Integer.toString(maxHeight));

//			System.out.println("widths check:" + Arrays.toString(widthsCheck));
//			System.out.println("widths actual:" + Arrays.toString(widths));

//			System.out.println("heights check:" + Arrays.toString(heightsCheck));
//			System.out.println("heights actual:" + Arrays.toString(heights));

			if (maxHeightCheck != maxHeight) {
				throw new RuntimeException("Sanity Check: max height is wrong");
			}
			if (!Arrays.equals(widths, widthsCheck)) {
				throw new RuntimeException("Sanity Check: widths are wrong");
			}
			if (!Arrays.equals(heights, heightsCheck)) {
				throw new RuntimeException("Sanity Check: heights are wrong");
			}

		}
	}
	
	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		int[] skirt = piece.getSkirt();
		int result = 0;
		if (x < 0 || x + piece.getWidth() > width) {
			throw new RuntimeException("Dropping a piece out of bound");
		}

		for (int i = 0; i < piece.getWidth(); i++) {
			if (result < heights[x + i] - skirt[i]) {
				result = heights[x + i] - skirt[i];
			}
		}

		return result; // YOUR CODE HERE
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		if (x < 0 || x >= width) {
			throw new RuntimeException("column index is not a valid");
		}
		return heights[x]; // YOUR CODE HERE
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		if (y < 0 || y >= height) {
			throw new RuntimeException("row index is not a valid");
		}
		return widths[y]; // YOUR CODE HERE
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public boolean getGrid(int x, int y) {
		// YOUR CODE HERE
		if (x >= width || x < 0 || y >= height || y < 0) {
			return false;
		}
		return grid[x][y];
	}
	
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.
	 
	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	*/
	private void backup() {
		xmaxHeight = maxHeight;
		System.arraycopy(widths, 0, xWidths, 0, height);
		System.arraycopy(heights, 0, xHeights, 0, width);
		for (int i = 0; i < width; i++) {
			System.arraycopy(grid[i], 0, xGrid[i], 0, height);
		}

	}


	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) throw new RuntimeException("place commit problem");
			
		int result = PLACE_OK;

		// YOUR CODE HERE
		backup();
		TPoint[] body = piece.getBody();
		int xPoint, yPoint;
		committed = false;

		for (int i = 0; i < body.length; i++) {
			xPoint = x + body[i].x;
			yPoint = y + body[i].y;
			if (xPoint < 0 || yPoint < 0 || xPoint >= width || yPoint >= height) {
				System.out.println("Place out bounds error");
				return PLACE_OUT_BOUNDS;
			}
			if (grid[xPoint][yPoint]) {
				System.out.println("Place bad error");
				return PLACE_BAD;
			}
			// update grid
			grid[xPoint][yPoint] = true;
			// update max height
			if (yPoint + 1 > maxHeight) {
				maxHeight = yPoint + 1;
			}
			// update widths and heights
			widths[yPoint]++;
			if (heights[xPoint] < yPoint + 1) {
				heights[xPoint] = yPoint + 1;
			}
			if (widths[yPoint] == width) {
				result = PLACE_ROW_FILLED;
			}

		}
		sanityCheck();
		return result;
	}
	
	
	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	*/
	public int clearRows() {
		int rowsCleared = 0;
		// YOUR CODE HERE
		if (committed) {
			committed = false;
			backup();
		}
		int from = 0;
		int to = 0;

		while (from < maxHeight) {
			if (widths[from] == width) {
				rowsCleared++;
				from++;
				continue;
			}
			if (from != to){
				// copy
				widths[to] = widths[from];
				for (int i = 0; i < width; i++) {
					grid[i][to] = grid[i][from];
				}

			}
			from++;
			to++;
		}
		if (rowsCleared != 0) {
			// update variables
			for (int i = to; i < maxHeight; i++) {
				widths[i] = 0;
			}
			Arrays.fill(heights, 0);
			for (int i = 0; i < width; i++) {
				for (int j = to - 1; j >= 0; j--) {
					if (grid[i][j]) {
						heights[i] = j + 1;
						break;
					}
				}
				for (int j = to; j < maxHeight; j++) {
					grid[i][j] = false;
				}
			}
			maxHeight = maxHeight - rowsCleared;
		}
		sanityCheck();
		return rowsCleared;
	}



	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	public void undo() {
		// YOUR CODE HERE
		maxHeight = xmaxHeight;
		System.arraycopy(xWidths, 0, widths, 0, height);
		System.arraycopy(xHeights, 0, heights, 0, width);
		for (int i = 0; i < width; i++) {
			System.arraycopy(xGrid[i], 0, grid[i], 0, height);
		}
		committed = true;
		sanityCheck();
	}
	
	
	/**
	 Puts the board in the committed state.
	*/
	public void commit() {
		committed = true;
	}


	
	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility) 
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
}


