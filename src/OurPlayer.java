import java.util.ArrayList;


public class OurPlayer extends Player{
	
	//n = name of the player
	//t = the number corresponding to your turn
	//l = timelimit for your move in seconds
    
	private long moveStartTime;
	
	public OurPlayer(String n, int t, int l)
	{
		super(n, t, l);
	}

	public Move getMove(StateTree state)
	{
	    moveStartTime = System.nanoTime();
	    // Decide a move.
	    long elapsedTime = System.nanoTime() - moveStartTime;
	    double secondsElapsed = (double)elapsedTime / 1000000000.0;
		return new Move(false,0);
	}
	
	public OurStateTree initializeStateTree(OurStateTree root, int d){
		return null;
	}
	
	public Integer getUtilityScore(StateTree state){
		Integer utilityScore = 0;
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
		Integer allyUtility = 0;
		for(int i = 0; i < connectionsFriendly.size(); i++){
		    if(connectionsFriendly.get(i).getPieces().size() >= state.winNumber){ // Friendly win or tie
		        return Integer.MAX_VALUE;
		    }
		    allyUtility += (int) Math.pow(connectionsFriendly.get(i).getPieces().size(), 2) * connectionsFriendly.get(i).numAdjacentEmpty;
		}
		
		//  Calculate the utility score for opponent pieces.
		Integer opponentUtility = 0;
		for(int i = 0; i < connectionsFriendly.size(); i++){
		    if(connectionsFriendly.get(i).getPieces().size() >= state.winNumber){ // Enemy win
                return Integer.MIN_VALUE;
            }
            opponentUtility += (int) Math.pow(connectionsOpponent.get(i).getPieces().size(), 2) * connectionsOpponent.get(i).numAdjacentEmpty;
        }
		if(state.turn == this.turn){ // Favor aggressive positions when you have the next move.
		    allyUtility = (int) (Math.round(allyUtility * 1.5));
		}
		else{ // Favor defensive positions when your opponent has the next move.
            opponentUtility = (int) (Math.round(allyUtility * 1.5));
        }
		utilityScore = allyUtility-opponentUtility;
		return utilityScore;
	}
	
	public void populateCons(StateTree board, ArrayList<PieceConnection> ally, ArrayList<PieceConnection> enemy){
		int lastPiece = -1;
		int opponentTurn = 1; // Assume opponent is turn 1
		PieceConnection currentConnection = new PieceConnection(0);
        if(this.turn==1){opponentTurn=2;} // Opponent is turn 2 if we are turn 1
		
	    for(int r = 0; r < board.rows; r++){ // Loop rows
		    for(int c = 0; c < board.columns; c++){
		        
		        if(lastPiece < 1 && board.boardMatrix[r][c] > 0){ // Starting new connection
		            currentConnection = new PieceConnection(lastPiece+1);
		            currentConnection.AddPiece(new BoardPoint(r,c));
		        }
		        else if(lastPiece == board.boardMatrix[r][c]){ // Add to connection
		            currentConnection.AddPiece(new BoardPoint(r,c));
		        }
		        else{ // Ending a connection
		            if(lastPiece == this.turn){
		                if(board.boardMatrix[r][c] == 0){ // No new connection
		                    currentConnection.AddEmpty(1);
		                    ally.add(currentConnection);
		                }
		                else{ // New opponent connection
		                    ally.add(currentConnection);
		                    currentConnection = new PieceConnection(0);
		                    currentConnection.AddPiece(new BoardPoint(r,c));
		                }
		            }
		            if(lastPiece == opponentTurn){
		                if(board.boardMatrix[r][c] == 0){ // No new connection
                            currentConnection.AddEmpty(1);
                            enemy.add(currentConnection);
                        }
                        else{ // New ally connection
                            enemy.add(currentConnection);
                            currentConnection = new PieceConnection(0);
                            currentConnection.AddPiece(new BoardPoint(r,c));
                        }
		            }
		        }
		        lastPiece = board.boardMatrix[r][c];
		    }
		    if(lastPiece == this.turn){ // Ally connection at end of row
		        ally.add(currentConnection);
		    }
		    else if(lastPiece == opponentTurn){ // Opponent connection at end of row
		        enemy.add(currentConnection);
            }
		    lastPiece = -1; // Prepare for new row
		}
	    
	    for(int c = 0; c < board.columns; c++){ // Loop columns
            for(int r = 0; r < board.rows; r++){
                
                if(lastPiece < 1 && board.boardMatrix[r][c] > 0){ // Starting new connection
                    currentConnection = new PieceConnection(lastPiece+1);
                    currentConnection.AddPiece(new BoardPoint(r,c));
                }
                else if(lastPiece == board.boardMatrix[r][c]){ // Add to connection
                    currentConnection.AddPiece(new BoardPoint(r,c));
                }
                else{ // Ending a connection
                    if(lastPiece == this.turn){
                        if(board.boardMatrix[r][c] == 0){ // No new connection
                            currentConnection.AddEmpty(1);
                            ally.add(currentConnection);
                        }
                        else{ // New opponent connection
                            ally.add(currentConnection);
                            currentConnection = new PieceConnection(0);
                            currentConnection.AddPiece(new BoardPoint(r,c));
                        }
                    }
                    if(lastPiece == opponentTurn){
                        if(board.boardMatrix[r][c] == 0){ // No new connection
                            currentConnection.AddEmpty(1);
                            enemy.add(currentConnection);
                        }
                        else{ // New ally connection
                            enemy.add(currentConnection);
                            currentConnection = new PieceConnection(0);
                            currentConnection.AddPiece(new BoardPoint(r,c));
                        }
                    }
                }
                lastPiece = board.boardMatrix[r][c];
            }
            if(lastPiece == this.turn){ // Ally connection at end of row
                ally.add(currentConnection);
            }
            else if(lastPiece == opponentTurn){ // Opponent connection at end of row
                enemy.add(currentConnection);
            }
            lastPiece = -1; // Prepare for new row
        }
	    
	    for(int n = board.rows-board.winNumber; n > -1 * (board.columns-(board.winNumber-1)); n--){ // Loop diagonal up-right.  Ignore lines with < winNumber spots.
	        int r = 0;
            int c = 0;
            if(n > -1){ // Left edge of board
                r = n;
            }
            else{ // Bottom edge of board
                c = -1*n;
            }
	        while(c < board.columns && r < board.rows){
                
                if(lastPiece < 1 && board.boardMatrix[r][c] > 0){ // Starting new connection
                    currentConnection = new PieceConnection(lastPiece+1);
                    currentConnection.AddPiece(new BoardPoint(r,c));
                }
                else if(lastPiece == board.boardMatrix[r][c]){ // Add to connection
                    currentConnection.AddPiece(new BoardPoint(r,c));
                }
                else{ // Ending a connection
                    if(lastPiece == this.turn){
                        if(board.boardMatrix[r][c] == 0){ // No new connection
                            currentConnection.AddEmpty(1);
                            ally.add(currentConnection);
                        }
                        else{ // New opponent connection
                            ally.add(currentConnection);
                            currentConnection = new PieceConnection(0);
                            currentConnection.AddPiece(new BoardPoint(r,c));
                        }
                    }
                    if(lastPiece == opponentTurn){
                        if(board.boardMatrix[r][c] == 0){ // No new connection
                            currentConnection.AddEmpty(1);
                            enemy.add(currentConnection);
                        }
                        else{ // New ally connection
                            enemy.add(currentConnection);
                            currentConnection = new PieceConnection(0);
                            currentConnection.AddPiece(new BoardPoint(r,c));
                        }
                    }
                }
                lastPiece = board.boardMatrix[r][c];
                r++; // Move up-right
                c++;
            }
            if(lastPiece == this.turn){ // Ally connection at end of row
                ally.add(currentConnection);
            }
            else if(lastPiece == opponentTurn){ // Opponent connection at end of row
                enemy.add(currentConnection);
            }
            lastPiece = -1; // Prepare for new row
        }
	    
	    for(int n = board.columns-board.winNumber; n > -1 * (board.rows-(board.winNumber-1)); n--){ // Loop diagonal down-right.  Ignore lines with < winNumber spots.
            int r = board.rows-1;
            int c = 0;
            if(n > -1){
                c = n;
            }
            else{
                r += n;
            }
	        while(c < board.columns && r > 0){
                
                if(lastPiece < 1 && board.boardMatrix[r][c] > 0){ // Starting new connection
                    currentConnection = new PieceConnection(lastPiece+1);
                    currentConnection.AddPiece(new BoardPoint(r,c));
                }
                else if(lastPiece == board.boardMatrix[r][c]){ // Add to connection
                    currentConnection.AddPiece(new BoardPoint(r,c));
                }
                else{ // Ending a connection
                    if(lastPiece == this.turn){
                        if(board.boardMatrix[r][c] == 0){ // No new connection
                            currentConnection.AddEmpty(1);
                            ally.add(currentConnection);
                        }
                        else{ // New opponent connection
                            ally.add(currentConnection);
                            currentConnection = new PieceConnection(0);
                            currentConnection.AddPiece(new BoardPoint(r,c));
                        }
                    }
                    if(lastPiece == opponentTurn){
                        if(board.boardMatrix[r][c] == 0){ // No new connection
                            currentConnection.AddEmpty(1);
                            enemy.add(currentConnection);
                        }
                        else{ // New ally connection
                            enemy.add(currentConnection);
                            currentConnection = new PieceConnection(0);
                            currentConnection.AddPiece(new BoardPoint(r,c));
                        }
                    }
                }
                lastPiece = board.boardMatrix[r][c];
                r--; // Move down-right
                c++;
            }
            if(lastPiece == this.turn){ // Ally connection at end of row
                ally.add(currentConnection);
            }
            else if(lastPiece == opponentTurn){ // Opponent connection at end of row
                enemy.add(currentConnection);
            }
            lastPiece = -1; // Prepare for new row
        }
	    
	    return;
	}
	
	public Move miniMax(OurStateTree boardTree){
		return new Move(false,1);
	}
}
