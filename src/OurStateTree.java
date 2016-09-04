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
	
	public ArrayList<Move> moveList(StateTree node){
		ArrayList<Move> LoM = new ArrayList<Move>();
		
		for(int j = 0; j < node.columns; j++){
			Move temp = new Move(false, j);
			Move temp2 = new Move(true, j);
			if (validMove(temp, node)){
				LoM.add(temp);
			}
			if (validMove(temp2, node)){
				LoM.add(temp2);
			}
		}
		return LoM;
	}
	
	public boolean validMove(Move move, StateTree node){
		if(!move.pop && node.boardMatrix[node.rows-1][move.column] != 0)
		{
			return false;
		}
		if(move.pop)
		{
			if(node.boardMatrix[0][move.column] != node.turn)
			{
				return false;
			}
			if((node.turn == 1 && node.pop1) || (node.turn == 2 && node.pop2))
			{
				return false;
			}
		}
		return true;
	}
	
	public void genChild(StateTree node){
		ArrayList<Move> move = moveList(node);
		StateTree childState = null;
		for(int i = 0; i < move.size(); i++){
			childState = new RefereeBoard(node.rows, node.columns, node.winNumber, node.turn, node.pop1, node.pop2, node);
			for(int row = 0; row < node.rows; row++){
				for(int col = 0; col < node.columns; col++){
					childState.boardMatrix[row][col] = node.boardMatrix[row][col];
				}
			}
			childState.makeMove(move.get(i));
			OurStateTree ourChild = new OurStateTree(childState, move.get(i));
			ourChild.parent = this;
			this.children.add(ourChild);
		}
	}
}
