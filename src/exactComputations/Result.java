package exactComputations;

import java.io.Serializable;

import org.apache.commons.math3.fraction.BigFraction;

public class Result implements Serializable {
	
	private static final long serialVersionUID = 2587318361601621111L;
	
	int nAgents, nOpinions;
	TransitionFunction transitionFunction;
	int[][] configurations;
	BigFraction[] convergenceTimes;
	
	public Result(int nAgents, int nOpinions,
			TransitionFunction transitionFunction,
			Configuration[] configurations, BigFraction[] convergenceTimes) {
		
		this.nAgents = nAgents;
		this.nOpinions = nOpinions;
		this.transitionFunction = transitionFunction;
		
		this.configurations = new int[configurations.length][nOpinions];
		for (int i=0 ; i<configurations.length ; i++) {
			for (int j=0 ; j<nOpinions ; j++) {
				this.configurations[i][j] = configurations[i].get(j);
			}
		}
		
		this.convergenceTimes = convergenceTimes;
		
	}
	
	public String configurationString(int i) {
		String s = "";
		s += "{ ";
		for (int j=0 ; j<nOpinions ; j++) {
			s += configurations[i][j]+" ";
		}
		s += "}";
		return s;
	}
	
	public String toString() {
		
		String s = "";
		s += "number of agents: "+nAgents+"\n";
		s += "number of opinions: "+nOpinions+"\n";
		s += "transition function: "+transitionFunction+"\n\n";
		for (int i=0 ; i<configurations.length ; i++) {
			s += configurationString(i)+" "+convergenceTimes[i].doubleValue()+"\n";
		}
		
		return s;
	}
	
}
