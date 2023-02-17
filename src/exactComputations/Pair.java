package exactComputations;

public class Pair {
	
	public final int x, y;
	public Pair(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Pair)) return false;
		
		Pair other = (Pair) object;
		return x == other.x && y == other.y;
	}

	@Override
	public int hashCode() {
		return 100*x+y;
	}

}
