package exact;

import java.util.ArrayList;

import org.apache.commons.math3.fraction.BigFraction;


public class MajorityTransitionFunction implements TransitionFunction {
	
	
	ConfigurationTree configurationTree, sampleTree;
	int h,m;
	
	public MajorityTransitionFunction(int h, int m, ConfigurationTree configurationTree) {
		
		this.h = h;
		this.m = m;
		this.configurationTree = configurationTree;
		
		sampleTree = new ConfigurationTree(h,m);
	}
	
	public String toString() {
		return ""+h+"-majority";
	}
	
	public BigFraction probabilityToOccur(Configuration configuration, Configuration sample) {
		
		BigFraction[] probas = configurationTree.getProbabilityToSample(configuration);
		
		BigFraction result = BigFraction.ONE;
		for (int i=0 ; i<m ; i++) {
			for (int j=0 ; j<sample.repartition[i] ; j++) {
				result = result.multiply(probas[i]);
			}
			
		}
		
		return result.multiply(Utils.multinomialCoefficient(sample.repartition,h));
		
	}
	
	public BigFraction probabilityToWin(Configuration sample, int j) {
		
		ArrayList<Integer> argmax = new ArrayList<Integer>();
		
		int max = 0;
		
		for (int i=0 ; i<m ; i++) {
			
			if (sample.repartition[i] > max) {
				max = sample.repartition[i];
				argmax.clear();
				argmax.add(i);
			} else if (sample.repartition[i] == max) {
				argmax.add(i);
			}
			
		}
		
		if (!argmax.contains(j)) return BigFraction.ZERO;
		return new BigFraction(1,argmax.size());
		
	}
	

	@Override
	public BigFraction[] getProbabilities(Configuration configuration) {
				
		if (h <= 2) {
			return configurationTree.getProbabilityToSample(configuration);
		}
		
		BigFraction[] result = new BigFraction[m];
		for (int i=0 ; i<m ; i++) {
			
			BigFraction proba = BigFraction.ZERO;
			for (Configuration aux : sampleTree.configurations) {
								
				for (Configuration sample : sampleTree.getSimilarConfigurations(aux)) {
					
					BigFraction tmp = probabilityToWin(sample,i);
					if (tmp.compareTo(BigFraction.ZERO) > 0) {
						
						tmp = tmp.multiply(probabilityToOccur(configuration,sample));
						proba = proba.add(tmp);
						
					}
				}
				
			}
			
			result[i] = proba;
			
		}
				
		return result;
	}
	
	public static void main(String [] args) {
		
		int n = 6;
		int m = 3;
		
		ConfigurationTree tree = new ConfigurationTree(n,m);
		MajorityTransitionFunction f = new MajorityTransitionFunction(3,m,tree);
		
		for (Configuration c : tree.configurations) {
			
			System.out.println(c);
			for (BigFraction bf : f.getProbabilities(c)) {
				System.out.print(bf+" ");
			}
			System.out.println("\n");
			
		}
		
	}

	/*
	@Override
	public int randomExecution(Configuration configuration) {
		
		int nOpinions = configuration.numberOfOpinions();
		
		int[] samples = new int[nOpinions];
		for (int i=0 ; i<h ; i++) {
			samples[configuration.randomPull()]++;
		}
		
		ArrayList<Integer> candidates = new ArrayList<Integer>();
		int max = 0;
		
		for (int i=0 ; i<nOpinions ; i++) {
			if (samples[i] > max) {
				candidates.clear();
				candidates.add(i);
				max = samples[i];
			} else if (samples[i] == max) {
				candidates.add(i);
			}
		}
		
		return candidates.get(Utils.random.nextInt(candidates.size()));
	}
	*/

}





































