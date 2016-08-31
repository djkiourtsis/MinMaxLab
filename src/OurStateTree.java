
public class OurStateTree{
	
	StateTree state;
	Move prevMove;
	
	public OurStateTree(StateTree state, Move prevMove) {
		this.state = state;
		this.prevMove = prevMove;
	}
	
	public StateTree getStateTree(){
		return state;
	}
	
	public Move getPrevMove(){
		return prevMove;
	}
}
