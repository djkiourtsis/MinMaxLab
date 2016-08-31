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
		return 0;
	}
	
	public void populateCons(StateTree board, ArrayList<PieceConnection> ally, ArrayList<PieceConnection> enemy){
		return;
	}
	
	public Move miniMax(OurStateTree boardTree){
		return new Move(false,1);
	}
}
