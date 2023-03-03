package exact;

import java.util.ArrayList;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * Contains useful information about each possible canonical configuration for a given
 * number of agents and opinions.
 * This is to ensure that these quantities are computed only once.
 * 
 * 
 * @author robin
 *
 */
public class ConfigurationTree {
	
	/** Total number of configurations */
	private int leafCount = 0;
	
	/** Number of agents */
	public final int n;
	
	/** Number of distinct opinions */
	public final int m;
	
	/** The root of the tree used to compute the IDs of the configurations */
	private Node root;
	
	/** All configurations, accessible via ID */
	Configuration[] configurations;
	
	/** The multinomial coefficient of each configuration */
	BigFraction[] multinomialCoefficient;
	
	/** The probability to sample opinion i in each configuration */
	BigFraction[][] probabilityToSample;
	
	/** All configurations that, once sorted by decreasing order, are equal to a canonical configuration */
	ArrayList<ArrayList<Configuration>> similarConfigurations;
	
	public ConfigurationTree(int n, int m) {
		
		this.n = n;
		this.m = m;
		root = new Node(n,m,n);
		
		configurations = new Configuration[leafCount];
		root.computeConfigurations(new int[m]);
		
		multinomialCoefficient = new BigFraction[leafCount];
		for (int i=0 ; i<leafCount ; i++) {
			multinomialCoefficient[i] = Utils.multinomialCoefficient(configurations[i].repartition,n,0);
		}
		
		probabilityToSample = new BigFraction[leafCount][m];
		for (int i=0 ; i<leafCount ; i++) {
			for (int j=0 ; j<m ; j++) {
				probabilityToSample[i][j] = new BigFraction(configurations[i].repartition[j],n);
			}
		}
		
		similarConfigurations = new ArrayList<ArrayList<Configuration>>(leafCount);
		for (int i=0 ; i<leafCount ; i++) {
			
			ArrayList<Configuration> tmp = new ArrayList<Configuration>();
			
			for (Permutation permutation : Permutation.allPermutations(m)) {
				
				int[] newRepartition = new int[m];
				for (int j=0 ; j<m ; j++) {
					newRepartition[j] = configurations[i].repartition[permutation.get(j)];
				}
				Configuration candidate = new Configuration(n,newRepartition);
				if (!tmp.contains(candidate)) tmp.add(candidate);
				
			}
			
			similarConfigurations.add(tmp);
		}
		
	}
	
	private class Node {
		
		int id;
		int nMin,nMax,mLeft;
		Node[] children;
		
		public Node(int nLeft, int mLeft, int max) {
			
			this.mLeft = mLeft;
			
			if (mLeft == 0) {
				
				children = null;
				id = leafCount;
				leafCount++;
				
			} else {
				
				id = -1;
				nMax = Math.min(nLeft,max);
				nMin = (int) (Math.ceil( ((double) nLeft)/mLeft ));
				if (nMin > nMax) System.out.println("Argh");
				children = new Node[nMax - nMin + 1];
				for (int i=nMax ; i>=nMin ; i--) {
					children[i-nMin] = new Node(nLeft-i,mLeft-1,i);
				}
				
			}
			
		}
		
		private void computeConfigurations(int[] repartition) {
			
			if (mLeft == 0) {
				configurations[id] = new Configuration(n,repartition);
			} else {
				
				for (int i=nMin ; i<=nMax ; i++) {
					
					int[] newRepartition = new int[m];
					for (int j=0 ; j<m-mLeft ; j++) newRepartition[j] = repartition[j];
					newRepartition[m-mLeft] = i;
					children[i-nMin].computeConfigurations(newRepartition);
					
				}
				
			}
			
		}
		
		private int getID(Configuration c) {
			
			if (mLeft == 0) return id;
			
			if (m-mLeft >= c.repartition.length) return children[0].getID(c);
			
			return children[c.repartition[m-mLeft]-nMin].getID(c);
			
		}
		
	}
	
	/**
	 * Returns the ID of the given {@link Configuration configuration} in O(log m) time.
	 * @param c A {@link Configuration configuration}
	 * @return The ID of the configuration
	 */
	public int getID(Configuration c) {
		
		return root.getID(c);
		
	}
	
	public BigFraction getMultinomialCoefficient(int ID) {
		return multinomialCoefficient[ID];
	}
	
	public BigFraction getMultinomialCoefficient(Configuration c) {
		return getMultinomialCoefficient(getID(c));
	}
	
	public BigFraction[] getProbabilityToSample(int ID) {
		return probabilityToSample[ID];
	}
	
	public BigFraction[] getProbabilityToSample(Configuration c) {
		return getProbabilityToSample(getID(c));
	}
	
	public ArrayList<Configuration> getSimilarConfigurations(int ID) {
		return similarConfigurations.get(ID);
	}
	
	public ArrayList<Configuration> getSimilarConfigurations(Configuration c) {
		return getSimilarConfigurations(getID(c));
	}
	
	public static void main(String[] args) {
		
		ConfigurationTree tree = new ConfigurationTree(6,3);
		for (Configuration c : tree.configurations) {
			System.out.println("Configuration: "+c);
			int id = tree.getID(c);
			System.out.println("ID: "+id);
			System.out.println("Multinomial coefficient: "+tree.getMultinomialCoefficient(id));
			System.out.println("Similar configurations: ");
			for (Configuration c2 : tree.getSimilarConfigurations(id)) {
				System.out.println("\t"+c2);
			}
			System.out.println();
		}
		
	}
	
	
	
	

}



























 
