package exactComputations;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.fraction.BigFraction;

public class Sample {
	
	private Box<BigFraction> multinomialCoefficient;
	ArrayList<Integer> argmax;
	int[] content;
	int h, nOpinions;
	
	public Sample(int[] content, int h) {
		
		this.content = content;
		this.h = h;
		nOpinions = content.length;
		
		argmax = new ArrayList<Integer>();
		computeArgmax();
		
		multinomialCoefficient = new Box<BigFraction>();
		
	}
	
	public String toString() {
		
		String s = "[ ";
		for (int i : content) {
			s += i+" ";
		}
		return s+"]";
		
	}
	
	public BigFraction getMultinomialCoefficient() {
		
		if (!multinomialCoefficient.isEmpty()) return multinomialCoefficient.get();
		BigFraction result = Utils.multinomialCoefficient(content,h,argmax.get(0));
		multinomialCoefficient.put(result);
		return result;
		
	}
	
	private void computeArgmax() {
		
		int max = 0;
		
		for (int i=0 ; i<nOpinions ; i++) {
			
			if (content[i] > max) {
				max = content[i];
				argmax.clear();
				argmax.add(i);
			} else if (content[i] == max) {
				argmax.add(i);
			}
			
		}
		
	}
	
	/**
	 * Computes the probability that this specific sample occurs in a given configuration.
	 * @param config Configuration of the system.
	 * @return Probability that this sample occurs when the system is in Configuration {@link config}.
	 */
	public BigFraction probabilityToOccur(Configuration config) {
		
		BigFraction[] probas = config.getSampleProbabilities();
		
		BigFraction result = BigFraction.ONE;
		for (int i=0 ; i<nOpinions ; i++) {
			for (int j=0 ; j<content[i] ; j++) {
				result = result.multiply(probas[i]);
			}
			
		}
		
		return result.multiply(getMultinomialCoefficient());
		
	}
	
	/**
	 * Computes the probability that a given opinion is adopted conditioning on this sample occuring.
	 * @param i An opinion
	 * @return Probability that {@link i} is adopted given that this sample occured.
	 */
	public BigFraction probabilityToWin(int i) {
		
		if (!argmax.contains(i)) return BigFraction.ZERO;
		return new BigFraction(1,argmax.size());
		
	}
	
	/* Static methods */
	
	public static void aux(ArrayList<Sample> result, int[] acc, int index, int left, int h) {
		
		if (index == acc.length-1) {
			
			acc[index] = left;
			result.add(new Sample(acc,h));
			return;
			
		}
		
		for (int i=0 ; i<=left ; i++) {
			
			int[] newAcc = new int[acc.length];
			for (int j=0 ; j<index ; j++) newAcc[j] = acc[j];
			newAcc[index] = i;
			aux(result,newAcc,index+1,left-i,h);
			
		}
		
	}
	
	private static HashMap<Pair,Sample[]> memory = new HashMap<Pair,Sample[]>();
	
	public static Sample[] allSamples(int nOpinions, int h) {
		
		
		Pair param = new Pair(h,nOpinions);
		if (memory.containsKey(param)) {
			return memory.get(param);
		}
				
		ArrayList<Sample> tmp = new ArrayList<Sample>();
		aux(tmp,new int[nOpinions],0,h,h);
		
		Sample[] result = new Sample[tmp.size()];
		result = tmp.toArray(result);
		
		memory.put(param,result);
		
		Main.log("All samples computed for nOpinions = "+nOpinions+", h = "+h);
		
		return result;
		
	}
	

}










































