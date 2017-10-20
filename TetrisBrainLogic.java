package edu.stanford.cs108.tetris;

/**
 * Created by ziyili on 10/18/17.
 */

public class TetrisBrainLogic extends TetrisLogic {
    private boolean ifBrain;

    private DefaultBrain defaultBrain;
    private boolean brainMode = false;
    private Brain.Move move;

    public TetrisBrainLogic(TetrisUIInterface tetrisUIInterface) {
        super(tetrisUIInterface);
        this.defaultBrain = new DefaultBrain();
    }

    public void setBrainMode(boolean brain) {
        brainMode = brain;
    }

    @Override
    public void tick (int verb) {
        if (verb == DOWN && brainMode && currentY < HEIGHT && currentPiece != null) {
            board.undo();
            move = defaultBrain.bestMove(board, currentPiece, HEIGHT, move);
//            board.undo();
            if (move != null) {
                if (!currentPiece.equals(move.piece)) {
                    super.tick(ROTATE);
                }
                if (move.x > currentX) {
                    super.tick(RIGHT);
                } else if (move.x < currentX) {
                    super.tick(LEFT);
                }
            }
        }
        super.tick(verb);
    }


}
