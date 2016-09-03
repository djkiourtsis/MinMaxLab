import java.util.ArrayList;


public class OurPlayer extends Player{
	
	//n = name of the player
	//t = the number corresponding to your turn
	//l = timelimit for your move in seconds
	
	public OurPlayer(String n, int t, int l)
	{
		super(n, t, l);
	}

	public Move getMove(StateTree state)
	{
		return new Move(false,0);
	}
	
	public OurStateTree initializeStateTree(OurStateTree root, int d){
		return null;
	}
	
	public Integer getUtilityScore(StateTree state){
		int utilityScore = 0;
		int opponentTurn = 1; // Assume opponent is turn 1
		ArrayList<PieceConnection> connectionsFriendly = new ArrayList<PieceConnection>();
		ArrayList<PieceConnection> connectionsOpponent = new ArrayList<PieceConnection>();
		ArrayList<BoardPoint> poppableFriendly = new ArrayList<BoardPoint>();
		ArrayList<BoardPoint> poppableOpponent = new ArrayList<BoardPoint>();
		
		if(this.turn==1){opponentTurn=2;} // Opponent is turn 2 if we are turn 1
		
		//  Populate piece connections and poppable piece lists.
		if(!state.pop1 && this.turn==1 || !state.pop2 && this.turn==2){ // Our player hasn't used pop move
    		for(int i = 0; i < state.columns; i++){
    		    if(state.boardMatrix[0][i] == this.turn) {poppableFriendly.add(new BoardPoint(0,i));}
    		}
		}
		if(!state.pop1 && opponentTurn==1 || !state.pop2 && opponentTurn==2){ // Opponent player hasn't used pop move
            for(int i = 0; i < state.columns; i++){
                if(state.boardMatrix[0][i] == this.turn) {poppableOpponent.add(new BoardPoint(0,i));}
            }
        }
		populateCons(state, connectionsFriendly, connectionsOpponent);
		
		//  Calculate the utility score for allied pieces.
		for(int i = 0; i < connectionsFriendly.size(); i++){
		    utilityScore += Math.pow(connectionsFriendly.get(i).getPieces().size(), 2) * connectionsFriendly.get(i).numAdjacentEmpty;
		}
		
		//  Calculate the utility score for opponent pieces.
		for(int i = 0; i < connectionsFriendly.size(); i++){
            utilityScore -= Math.pow(connectionsOpponent.get(i).getPieces().size(), 2) * connectionsOpponent.get(i).numAdjacentEmpty;
        }
		
		return utilityScore;
	}
	
	public void populateCons(StateTree board, ArrayList<PieceConnection> ally, ArrayList<PieceConnection> enemy){
		return;
	}
	
	public Move miniMax(OurStateTree boardTree){
		return new Move(false,1);
	}
}
