import java.util.ArrayList;


public class OurStateTree{
	
	StateTree state;
	Move prevMove;
	ArrayList<OurStateTree> children;
	OurStateTree parent;
	
	public OurStateTree(StateTree state, Move prevMove) {
		this.state = state;
		this.prevMove = prevMove;
		this.parent = null;
		this.children = new ArrayList<OurStateTree>();
	}
	
	public StateTree getStateTree(){
		return state;
	}
	
	public Move getPrevMove(){
		return prevMove;
	}
	
	public ArrayList<Move> moveList(){
		ArrayList<Move> LoM = new ArrayList<Move>();
		
		for(int j = 0; j < this.state.columns; j++){
			Move temp = new Move(false, j);
			Move temp2 = new Move(true, j);
			if (validMove(temp)){
				LoM.add(temp);
			}
			if (validMove(temp2)){
				LoM.add(temp2);
			}
		}
		return LoM;
	}
	
	public boolean validMove(Move move){
		if(!move.pop && this.state.boardMatrix[this.state.rows-1][move.column] != 0)
		{
			return false;
		}
		if(move.pop)
		{
			if(this.state.boardMatrix[0][move.column] != this.state.turn)
			{
				return false;
			}
			if((this.state.turn == 1 && this.state.pop1) || (this.state.turn == 2 && this.state.pop2))
			{
				return false;
			}
		}
		return true;
	}
	
	public void genChild(){
		ArrayList<Move> move = moveList();
		StateTree childState = null;
		for(int i = 0; i < move.size(); i++){
			childState = new RefereeBoard(this.state.rows, this.state.columns, this.state.winNumber, this.state.turn, this.state.pop1, this.state.pop2, this.state);
			for(int row = 0; row < this.state.rows; row++){
				for(int col = 0; col < this.state.columns; col++){
					childState.boardMatrix[row][col] = this.state.boardMatrix[row][col];
				}
			}
			childState.makeMove(move.get(i));
			OurStateTree ourChild = new OurStateTree(childState, move.get(i));
			ourChild.parent = this;
			this.children.add(ourChild);
		}
	}
}
