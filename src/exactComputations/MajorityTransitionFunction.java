package exactComputations;

import java.util.ArrayList;

import org.apache.commons.math3.fraction.BigFraction;

public class MajorityTransitionFunction implements TransitionFunction {
	
	int h;
	
	public MajorityTransitionFunction(int h) {
		this.h = h;
	}
	
	public String toString() {
		return ""+h+"-majority";
	}

	@Override
	public BigFraction[] getProbabilities(Configuration configuration) {
		
		if (h <= 2) {
			return configuration.getSampleProbabilities();
		}
		
		int nOpinions = configuration.numberOfOpinions();
		Sample[] samples = Sample.allSamples(nOpinions,h);
		
		BigFraction[] result = new BigFraction[nOpinions];
		for (int i=0 ; i<nOpinions ; i++) {
			
			BigFraction proba = BigFraction.ZERO;
			for (Sample sample : samples) {
				
				BigFraction tmp = sample.probabilityToWin(i);
				if (tmp.compareTo(BigFraction.ZERO) > 0) {
					
					tmp = tmp.multiply(sample.probabilityToOccur(configuration));
					proba = proba.add(tmp);
					
				}
				
			}
			
			result[i] = proba;
			
		}
				
		return result;
	}

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

}





































