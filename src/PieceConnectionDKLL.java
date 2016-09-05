import java.util.ArrayList;

public class PieceConnectionDKLL {
    ArrayList<BoardPointDKLL> pieces;
    int numAdjacentEmpty;
    
    public PieceConnectionDKLL(int empty){
        this.numAdjacentEmpty = empty;
        pieces = new ArrayList<BoardPointDKLL>();
    }
    
    public void AddPiece(BoardPointDKLL piece){
        this.pieces.add(piece);
    }
    
    public void AddEmpty(int e){
        this.numAdjacentEmpty += e;
    }

    public ArrayList<BoardPointDKLL> getPieces() {
        return pieces;
    }

    public int getNumAdjacentEmpty() {
        return numAdjacentEmpty;
    }
    
}
