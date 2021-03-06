import java.util.ArrayList;


public class OurPlayerDKLL extends Player{
	
	//n = name of the player
	//t = the number corresponding to your turn
	//l = timelimit for your move in seconds
    
	private long moveStartTime;
	
	public OurPlayerDKLL(String n, int t, int l)
	{
		super(n, t, l);
	}

	public Move getMove(StateTree state)
	{
		moveStartTime = System.nanoTime();
		
		int depthLimit;
		depthLimit = (int) Math.floor(Math.log(Math.pow(9, 6))/Math.log(state.columns));// Limit the size of the iterative deepening tree (found through testing).
		
	    // Decide a move.
	    long elapsedTime = System.nanoTime() - moveStartTime;
	    double secondsElapsed = (double)elapsedTime / 1000000000.0;
	    Move bestMove = null; // Default if we can't run a search.
	    for(int i = 1; i < depthLimit; i++){ // STOP AT DEPTH 7 DUE TO RUNNING OUT OF MEMORY.
	        OurStateTreeDKLL stateTree = new OurStateTreeDKLL(state, null);
		    initializeStateTree(stateTree, i);
		    if(bestMove == null){
		        bestMove = stateTree.children.get(0).getPrevMove(); // Default move.
		    }
		    Move m = miniMax(stateTree);
		    if(m != null){
		        bestMove = m;
		    }
		    elapsedTime = System.nanoTime() - moveStartTime;
	        secondsElapsed = (double)elapsedTime / 1000000000.0;
	        if(this.timeLimit - secondsElapsed <= 2){return bestMove;}
	    }
		return bestMove;
	}
	
	public void initializeStateTree(OurStateTreeDKLL root, int d){ 
		if(d == 0){
			return;
		}
		else{
			root.genChild();
			for(int i = 0; i < root.children.size(); i++){
				if(Referee.checkForWinner(root.children.get(i).getStateTree()) == 0){
					initializeStateTree(root.children.get(i), d - 1);
				}
			}
		}
	}
	
	public Integer getUtilityScore(StateTree state){
		Integer utilityScore = 0;
		int opponentTurn = 1; // Assume opponent is turn 1
		ArrayList<PieceConnectionDKLL> connectionsFriendly = new ArrayList<PieceConnectionDKLL>();
		ArrayList<PieceConnectionDKLL> connectionsOpponent = new ArrayList<PieceConnectionDKLL>();
		ArrayList<BoardPointDKLL> poppableFriendly = new ArrayList<BoardPointDKLL>();
		ArrayList<BoardPointDKLL> poppableOpponent = new ArrayList<BoardPointDKLL>();
		
		int win = Referee.checkForWinner(state);
		if(win>0){
			if(turn==win){
				return Integer.MAX_VALUE;
			}
			else if(win==3){
				return 0;
			}
			else{
				return Integer.MIN_VALUE;
			}
		}
		
		if(this.turn==1){opponentTurn=2;} // Opponent is turn 2 if we are turn 1
		
		//  Populate piece connections and poppable piece lists.
		if(!state.pop1 && this.turn==1 || !state.pop2 && this.turn==2){ // Our player hasn't used pop move
    		for(int i = 0; i < state.columns; i++){
    		    if(state.boardMatrix[0][i] == this.turn) {poppableFriendly.add(new BoardPointDKLL(0,i));}
    		}
		}
		if(!state.pop1 && opponentTurn==1 || !state.pop2 && opponentTurn==2){ // Opponent player hasn't used pop move
            for(int i = 0; i < state.columns; i++){
                if(state.boardMatrix[0][i] == this.turn) {poppableOpponent.add(new BoardPointDKLL(0,i));}
            }
        }
		populateCons(state, connectionsFriendly, connectionsOpponent);
		
		//  Calculate the utility score for allied pieces.
		Integer allyUtility = 0;
		for(int i = 0; i < connectionsFriendly.size(); i++){
		    allyUtility += (int) Math.pow(connectionsFriendly.get(i).getPieces().size(), 2) * connectionsFriendly.get(i).numAdjacentEmpty;
		}
		
		//  Calculate the utility score for opponent pieces.
		Integer opponentUtility = 0;
		for(int i = 0; i < connectionsOpponent.size(); i++){
            opponentUtility += (int) Math.pow(connectionsOpponent.get(i).getPieces().size(), 2) * connectionsOpponent.get(i).numAdjacentEmpty;
        }
		if(state.turn == this.turn){ // Favor aggressive positions when you have the next move.
		    allyUtility = (int) (Math.round(allyUtility * 3));
		}
		else{ // Favor defensive positions when your opponent has the next move.
            opponentUtility = (int) (Math.round(allyUtility * 2));
        }
		utilityScore = allyUtility-opponentUtility;
        for(int c = 0; c < state.columns; c++){
            if(this.turn == state.boardMatrix[0][c]){
                if(this.turn==1){
                    if(!state.pop1){
                        utilityScore += 3;
                    }
                    else if(!state.pop2){
                        utilityScore += 3;
                    }
                }
            }
            else if(opponentTurn == state.boardMatrix[0][c]){
                if(opponentTurn==1){
                    if(!state.pop1){
                        utilityScore -= 3;
                    }
                    else if(!state.pop2){
                        utilityScore -= 3;
                    }
                }
            }
        }
		return utilityScore;
	}
	
	public void populateCons(StateTree board, ArrayList<PieceConnectionDKLL> ally, ArrayList<PieceConnectionDKLL> enemy){
		int lastPiece = -1;
		int opponentTurn = 1; // Assume opponent is turn 1
		PieceConnectionDKLL currentConnection = new PieceConnectionDKLL(0);
        if(this.turn==1){opponentTurn=2;} // Opponent is turn 2 if we are turn 1
		
	    for(int r = 0; r < board.rows; r++){ // Loop rows
		    for(int c = 0; c < board.columns; c++){
		        
		        if(lastPiece < 1 && board.boardMatrix[r][c] > 0){ // Starting new connection
		            currentConnection = new PieceConnectionDKLL(lastPiece+1);
		            currentConnection.AddPiece(new BoardPointDKLL(r,c));
		        }
		        else if(lastPiece == board.boardMatrix[r][c]){ // Add to connection
		            currentConnection.AddPiece(new BoardPointDKLL(r,c));
		        }
		        else{ // Ending a connection
		            if(lastPiece == this.turn){
		                if(board.boardMatrix[r][c] == 0){ // No new connection
		                    currentConnection.AddEmpty(1);
		                    ally.add(currentConnection);
		                }
		                else{ // New opponent connection
		                    ally.add(currentConnection);
		                    currentConnection = new PieceConnectionDKLL(0);
		                    currentConnection.AddPiece(new BoardPointDKLL(r,c));
		                }
		            }
		            if(lastPiece == opponentTurn){
		                if(board.boardMatrix[r][c] == 0){ // No new connection
                            currentConnection.AddEmpty(1);
                            enemy.add(currentConnection);
                        }
                        else{ // New ally connection
                            enemy.add(currentConnection);
                            currentConnection = new PieceConnectionDKLL(0);
                            currentConnection.AddPiece(new BoardPointDKLL(r,c));
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
                    currentConnection = new PieceConnectionDKLL(lastPiece+1);
                    currentConnection.AddPiece(new BoardPointDKLL(r,c));
                }
                else if(lastPiece == board.boardMatrix[r][c]){ // Add to connection
                    currentConnection.AddPiece(new BoardPointDKLL(r,c));
                }
                else{ // Ending a connection
                    if(lastPiece == this.turn){
                        if(board.boardMatrix[r][c] == 0){ // No new connection
                            currentConnection.AddEmpty(1);
                            ally.add(currentConnection);
                        }
                        else{ // New opponent connection
                            ally.add(currentConnection);
                            currentConnection = new PieceConnectionDKLL(0);
                            currentConnection.AddPiece(new BoardPointDKLL(r,c));
                        }
                    }
                    if(lastPiece == opponentTurn){
                        if(board.boardMatrix[r][c] == 0){ // No new connection
                            currentConnection.AddEmpty(1);
                            enemy.add(currentConnection);
                        }
                        else{ // New ally connection
                            enemy.add(currentConnection);
                            currentConnection = new PieceConnectionDKLL(0);
                            currentConnection.AddPiece(new BoardPointDKLL(r,c));
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
                    currentConnection = new PieceConnectionDKLL(lastPiece+1);
                    currentConnection.AddPiece(new BoardPointDKLL(r,c));
                }
                else if(lastPiece == board.boardMatrix[r][c]){ // Add to connection
                    currentConnection.AddPiece(new BoardPointDKLL(r,c));
                }
                else{ // Ending a connection
                    if(lastPiece == this.turn){
                        if(board.boardMatrix[r][c] == 0){ // No new connection
                            currentConnection.AddEmpty(1);
                            ally.add(currentConnection);
                        }
                        else{ // New opponent connection
                            ally.add(currentConnection);
                            currentConnection = new PieceConnectionDKLL(0);
                            currentConnection.AddPiece(new BoardPointDKLL(r,c));
                        }
                    }
                    if(lastPiece == opponentTurn){
                        if(board.boardMatrix[r][c] == 0){ // No new connection
                            currentConnection.AddEmpty(1);
                            enemy.add(currentConnection);
                        }
                        else{ // New ally connection
                            enemy.add(currentConnection);
                            currentConnection = new PieceConnectionDKLL(0);
                            currentConnection.AddPiece(new BoardPointDKLL(r,c));
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
                    currentConnection = new PieceConnectionDKLL(lastPiece+1);
                    currentConnection.AddPiece(new BoardPointDKLL(r,c));
                }
                else if(lastPiece == board.boardMatrix[r][c]){ // Add to connection
                    currentConnection.AddPiece(new BoardPointDKLL(r,c));
                }
                else{ // Ending a connection
                    if(lastPiece == this.turn){
                        if(board.boardMatrix[r][c] == 0){ // No new connection
                            currentConnection.AddEmpty(1);
                            ally.add(currentConnection);
                        }
                        else{ // New opponent connection
                            ally.add(currentConnection);
                            currentConnection = new PieceConnectionDKLL(0);
                            currentConnection.AddPiece(new BoardPointDKLL(r,c));
                        }
                    }
                    if(lastPiece == opponentTurn){
                        if(board.boardMatrix[r][c] == 0){ // No new connection
                            currentConnection.AddEmpty(1);
                            enemy.add(currentConnection);
                        }
                        else{ // New ally connection
                            enemy.add(currentConnection);
                            currentConnection = new PieceConnectionDKLL(0);
                            currentConnection.AddPiece(new BoardPointDKLL(r,c));
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
	
	public Move miniMax(OurStateTreeDKLL boardTree){
		Move best = null;

		int bestUtility = Integer.MIN_VALUE;
		if(boardTree.children.size() == 0){
			return best;
		}
		//best = boardTree.children.get(0).prevMove;
		for(int i = 0; i < boardTree.children.size(); i++){
			int var = min(boardTree.children.get(i), Integer.MIN_VALUE, Integer.MAX_VALUE);
			if(var > bestUtility){
				best = boardTree.moveList().get(i);
				bestUtility = var;
			}
			
		}
		return best;
	}
	
	public Integer max(OurStateTreeDKLL boardTree, Integer alpha, Integer beta){
		Integer bestMax = Integer.MIN_VALUE;
		if(boardTree.children.size() == 0){
			return getUtilityScore(boardTree.getStateTree());
		}
		else{
			for(int i = 0; i < boardTree.children.size(); i++){
			    long elapsedTime = System.nanoTime() - moveStartTime;
		        double secondsElapsed = (double)elapsedTime / 1000000000.0;
			    if(this.timeLimit - secondsElapsed <= 2){return bestMax;}
				int tmpUtilityScore = min(boardTree.children.get(i), alpha, bestMax);
                if(bestMax < tmpUtilityScore){
                    bestMax = tmpUtilityScore;
                }
                if(bestMax >= beta){
                    return bestMax;
                }
				if(alpha < tmpUtilityScore){
                    alpha = tmpUtilityScore;
                }
			}
		}
		return bestMax;
	}
	
	public Integer min(OurStateTreeDKLL boardTree, Integer alpha, Integer beta){
		Integer bestMin = Integer.MAX_VALUE;
		if(boardTree.children.size() == 0){
			return getUtilityScore(boardTree.getStateTree());
		}
		
		else{
			for(int i = 0; i < boardTree.children.size(); i++){
			    long elapsedTime = System.nanoTime() - moveStartTime;
                double secondsElapsed = (double)elapsedTime / 1000000000.0;
                if(this.timeLimit - secondsElapsed <= 2){return bestMin;}
				int tmpUtilityScore = max(boardTree.children.get(i), alpha, bestMin);	
				if(bestMin > tmpUtilityScore){
					bestMin = tmpUtilityScore;
				}
				if(bestMin <= alpha){
					return bestMin;
				}
				if(beta > tmpUtilityScore){
				    beta = tmpUtilityScore;
				}
			}
		}
		return bestMin;
	}
}
