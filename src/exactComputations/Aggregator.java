package exactComputations;

public class Aggregator {

	private double total;
	private int occ;
	
	public Aggregator() {
		total = 0;
		occ = 0;
	}
	
	public void add(double value) {
		total += value;
		occ++;
	}
	
	public double get() {
		if (occ == 0) return 0;
		return total/occ;
	}
	
}
