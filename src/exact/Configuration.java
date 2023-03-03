package exact;

/**
 * 
 * Represents one configuration of the system.
 * 
 * @author robin
 *
 */
public class Configuration {
	
	/** Number of agents */
	public final int n;
	
	/** Number of distinct opinions */
	public final int m;
	
	/** repartition[i] is the number of agents with the ith-majority opinion */
	public final int[] repartition;
	
	public Configuration(int n, int[] repartition) {
		
		this.n = n;
		this.repartition = repartition;
		this.m = repartition.length;
		
	}
	
	public Configuration(Configuration c) {
		
		this.n = c.n;
		this.m = c.m;
		this.repartition = new int[m];
		for (int i=0 ; i<m ; i++) repartition[i] = c.repartition[i];
		
	}
	
	public String toString() {
		
		String s = "{ ";
		for (int i : repartition) {
			s += i+" ";
		}
		return s+"}";
	}
	
	public Configuration canonicalConfiguration() {
		
		//Configuration c = new Configuration(this);
		return null;
	}
	
	@Override
	public boolean equals(Object object) {
		
		if (!(object instanceof Configuration)) return false;
		
		Configuration other = (Configuration) object;
		
		if (other.repartition.length != repartition.length) return false;
		
		for (int i=0 ; i<repartition.length ; i++) {
			if (other.repartition[i] != repartition[i]) return false;
		}
		
		return true;
		
	}
	
	public int exactNumberOfOpinions() {
		
		int result = 0;
		while (result < repartition.length && repartition[result] != 0) result ++;
		return result;
		
	}

}


























