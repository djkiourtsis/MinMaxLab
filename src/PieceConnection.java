import java.util.ArrayList;

public class PieceConnection {
    ArrayList<BoardPoint> pieces;
    int numAdjacentEmpty;
    
    public PieceConnection(int empty){
        this.numAdjacentEmpty = empty;
        pieces = new ArrayList<BoardPoint>();
    }
    
    public void AddPiece(BoardPoint piece){
        this.pieces.add(piece);
    }
    
    public void AddEmpty(int e){
        this.numAdjacentEmpty += e;
    }

    public ArrayList<BoardPoint> getPieces() {
        return pieces;
    }

    public int getNumAdjacentEmpty() {
        return numAdjacentEmpty;
    }
    
}
