package exactComputations;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.fraction.BigFraction;

public class Configuration {
	
	public final int n;
	
	public int[] repartition;
	
	private Box<BigFraction> multinomialCoefficient;
	private Box<BigFraction[]> probabilities;
	private Box<Configuration[]> similarConfigurations;
	
	
	public Configuration(int n, int[] repartition) {
		
		this.n = n;
		this.repartition = repartition;
		
		multinomialCoefficient = new Box<BigFraction>();
		probabilities = new Box<BigFraction[]>();
		similarConfigurations = new Box<Configuration[]>();
		
	}
	
	public String toString() {
		
		String s = "{ ";
		for (int i : repartition) {
			s += i+" ";
		}
		return s+"}";//+" "+multinomialCoefficient;
	}
	
	public int numberOfOpinions() {
		return repartition.length;
	}
	
	public int get(int i) {
		return repartition[i];
	}
	
	public BigFraction getMultinomialCoefficient() {
		
		if (!multinomialCoefficient.isEmpty()) return multinomialCoefficient.get();
		
		BigFraction result = Utils.multinomialCoefficient(repartition,n,0);
		multinomialCoefficient.put(result);
		return result;
		
	}
	
	public BigFraction[] getSampleProbabilities() {
		
		if (!probabilities.isEmpty()) return probabilities.get();
		
		BigFraction[] result = new BigFraction[repartition.length];
		for (int j=0 ; j<repartition.length ; j++) result[j] = new BigFraction(repartition[j],n);
		probabilities.put(result);
		return result;
	}
	
	public Configuration[] getSimilarConfigurations() {
		
		if (!similarConfigurations.isEmpty()) return similarConfigurations.get();
		
		ArrayList<Configuration> tmp = new ArrayList<Configuration>();
		
		for (Permutation permutation : Permutation.allPermutations(repartition.length)) {
			
			int[] newRepartition = new int[repartition.length];
			for (int i=0 ; i<repartition.length ; i++) {
				newRepartition[i] = repartition[permutation.get(i)];
			}
			Configuration candidate = new Configuration(n,newRepartition);
			if (!tmp.contains(candidate)) tmp.add(candidate);
			
		}
		
		Configuration[] result = new Configuration[tmp.size()];
		result = tmp.toArray(result);
		similarConfigurations.put(result);
		return result;
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
	
	public int randomPull() {
		
		int r = Utils.random.nextInt(n);
		int s = repartition[0];
		int index = 0;
		while (s <= r) {
			index ++;
			s += repartition[index];
		}
		return index;
		
	}
	
	/* Static methods */
	
	private static void aux(ArrayList<Configuration> result, int[] acc, int index, int max, int left, int n) {
		
		if (index == acc.length) {
			
			if (left > 0) return;
			result.add(new Configuration(n,acc));
			return;
			
		}
		
		for (int i=Math.min(max,left) ; i>=0 ; i--) {
			
			int[] newAcc = new int[acc.length];
			for (int j=0 ; j<index ; j++) newAcc[j] = acc[j];
			newAcc[index] = i;
			aux(result,newAcc,index+1,Math.min(max,i),left-i,n);
			
			
		}
		
	}
	
	private static HashMap<Pair,Configuration[]> memory = new HashMap<Pair,Configuration[]>();
	
	public static Configuration[] allConfigurations(int n, int length) {
		
		Pair param = new Pair(n,length);
		if (memory.containsKey(param)) return memory.get(param);
		
		ArrayList<Configuration> tmp = new ArrayList<Configuration>();
		aux(tmp,new int[length],0,n,n,n);
		
		Configuration[] result = new Configuration[tmp.size()];
		result = tmp.toArray(result);
		
		memory.put(param,result);
		Main.log("All configurations computed for nAgents = "+n+", nOpinions = "+length
				+"\n("+result.length+" configurations found)");
		return result;
	}



}



























