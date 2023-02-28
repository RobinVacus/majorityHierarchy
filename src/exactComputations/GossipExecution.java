package exactComputations;

import java.util.Arrays;

import org.apache.commons.math3.fraction.BigFraction;

public class GossipExecution {

	int nAgents,nOpinions,nConfs;
	
	/** All possible configurations */
	Configuration[] configurations;
	
	/** transitions[i][j] is the probability to from configuration i to configuration j */
	BigFraction[][] transitions;
	
	/** probabilities[i][j] is the probability that an agent adopts opinion j from configuration i */
	BigFraction[][] probabilities;
	
	TransitionFunction transitionFunction;
	
	/**
	 * @param nAgents The number of agents involved in the dynamics
	 * @param nOpinions The number of possible opinions
	 * @param transitionFunction The transition function of the protocol being used
	 */
	public GossipExecution(int nAgents, int nOpinions, TransitionFunction transitionFunction) {
		
		this.nAgents = nAgents;
		this.nOpinions = nOpinions;
		this.transitionFunction = transitionFunction;
		
		configurations = Configuration.allConfigurations(nAgents,nOpinions);
		nConfs = configurations.length;
		
		probabilities = new BigFraction[nConfs][nOpinions];
		for (int i=0 ; i<nConfs ; i++) {
			probabilities[i] = transitionFunction.getProbabilities(configurations[i]);
		}
		Main.log("Output probabilities computed for "+transitionFunction);
		
		transitions = new BigFraction[nConfs][nConfs];
		computeTransitionProbabilities();
		
	}
	
	public String toString() {
		String s = "GossipExecution {";
		s += "nAgent = "+nAgents+", ";
		s += "nOpinions = "+nOpinions+", ";
		s += "protocol = "+transitionFunction;
		return s+"}";
	}
	
	private BigFraction computeTransitionProbability(int i, int j) {
		
		Configuration c2 = configurations[j];
		
		//Main.log("Proceeding with "+c1+" -> "+c2);
		
		BigFraction result = c2.getMultinomialCoefficient();
		
		//Main.log("multinomial coefficient: "+result);
		
		BigFraction sum = BigFraction.ZERO;
		for (Configuration possibleConfig : c2.getSimilarConfigurations()) {
			
			BigFraction product = BigFraction.ONE;
			for (int k=0 ; k<nOpinions ; k++) {
				
				for (int l=0 ; l<possibleConfig.get(k) ; l++) {
					product = product.multiply(probabilities[i][k]);
				}
				
			}
			
			//Main.log("Product for "+possibleConfig+": "+product);
			
			sum = sum.add(product);
			
		}
		
		//Main.log("sum: "+sum);
		
		result = result.multiply(sum);
		
		//Main.log("result: "+result+"\n");
		
		return result;
	}
	
	public void computeTransitionProbabilities() {
		
		for (int i=0 ; i<nConfs ; i++) {
			for (int j=0 ; j<nConfs ; j++) {
				transitions[i][j] = computeTransitionProbability(i,j);
			}
		}
		
		Main.log("Transition probabilities computed for"+this.toString());
		
	}
	
	public void printTransitions() {
		
		for (int i=0 ; i<nConfs ; i++) {
			for (int j=0 ; j<nConfs ; j++) {
				System.out.println(configurations[i]+" -> "+configurations[j]);
				System.out.println("Theoretical probability: "+transitions[i][j]);
				System.out.println(transitions[i][j].doubleValue());
				System.out.println("Empirical check: "+empiricalTransitionProbability(i,j,100000));
				System.out.println();
			}
		}
		
	}
	
	public BigFraction[] getConvergenceTimes() {
		
		SinkMC mc = new SinkMC(transitions);
		return mc.expectedSinkTime();		
		
	}
	
	public double[] getAverageConvergenceTimes(int iterations) {
		
		SinkMC mc = new SinkMC(transitions);
		return mc.averageSinkTime(iterations);
		
	}
	
	public void printSinkTimes() {
		
		BigFraction[] times = getConvergenceTimes();
		for (int i=0 ; i<nConfs ; i++) {
			
			System.out.println(configurations[i]+" "+(i > 0 ? times[i-1].doubleValue() : 0.));
			
		}
		
	}
	
	public void printAverageSinkTimes(int iterations) {
		
		double[] times = getAverageConvergenceTimes(iterations);
		for (int i=0 ; i<nConfs ; i++) {
			
			System.out.println(configurations[i]+" "+(i > 0 ? times[i-1] : 0.));
			
		}
		
	}
	
	public double empiricalTransitionProbability(int i, int j, int iterations) {
		
		Configuration c1 = configurations[i];
		Configuration c2 = configurations[j];
				
		double hit = 0;
		for (int k=0 ; k<iterations ; k++) {
			
			int[] tmp = new int[nOpinions];
			for (int l=0 ; l<nAgents ; l++) {
				tmp[transitionFunction.randomExecution(c1)]++;
			}
			Arrays.sort(tmp);
			boolean equals = true;
			for (int l=0 ; l<tmp.length ; l++) {
				if (tmp[l] != c2.get(tmp.length-l-1)) {
					equals = false;
					break;
				}
			}
			
			if (equals) hit++;
			
		}
		
		return hit/iterations;
	}

	Result compute() {
		
		BigFraction[] tmp = getConvergenceTimes();
		BigFraction[] times = new BigFraction[configurations.length];
		for (int i=0 ; i<configurations.length ; i++) {
			times[i] = (i == 0) ? BigFraction.ZERO : tmp[i-1]; 
		}
		return new Result(nAgents,nOpinions,transitionFunction,configurations,times);
		
	}
	
}
































