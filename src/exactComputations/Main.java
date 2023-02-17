package exactComputations;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.commons.math3.fraction.*;

public class Main {
	
	public static long lastStep;
	
	public static void init() {
		lastStep = System.currentTimeMillis();
	}
	
	public static void log(String s) {
		
		long newStep = System.currentTimeMillis();
		long difference = newStep - lastStep;
		
		System.out.println(s);
		System.out.println( (((double) difference)/1000) +"s");
		System.out.println();
		
		lastStep = newStep;
	}
	
	public static void testSinkTime() {
		
		SinkMC mc = new SinkMC(new BigFraction[][] {
			{new BigFraction(1), new BigFraction(0),new BigFraction(0)},
			{new BigFraction(1,7), new BigFraction(3,7), new BigFraction(3,7)},
			{new BigFraction(2,8), new BigFraction(0), new BigFraction(6,8)},
		});
		
		for (BigFraction f : mc.expectedSinkTime()) {
			System.out.println(f.doubleValue());
		}
		
		System.out.println();
		
		for (double d : mc.averageSinkTime(1000000)) {
			System.out.println(d);
		}
	}
	
	public static void testCombinatorics() {
		
		for (Configuration c : Configuration.allConfigurations(6,3)) {
			System.out.println(c);
			System.out.println("Multinomial coefficient: "+c.getMultinomialCoefficient());
			System.out.println("Similar configurations: ");
			for (Configuration c2 : c.getSimilarConfigurations()) {
				System.out.println("\t"+c2);
			}
			System.out.println();
		}
		
		System.out.println();
		
		for (Permutation p : Permutation.allPermutations(3)) {
			System.out.println(p);
		}
		
		System.out.println();
		
		for (Sample s : Sample.allSamples(3,6)) {
			System.out.println(s+" Mult. coef.:"+s.getMultinomialCoefficient());
		}
	}
	
	
	public static boolean compareMajority(int nAgents, int nOpinions, int h1, int h2) {
		
		boolean collapse = false;
		
		GossipExecution execution1 = new GossipExecution(nAgents,nOpinions,new MajorityTransitionFunction(h1));
		GossipExecution execution2 = new GossipExecution(nAgents,nOpinions,new MajorityTransitionFunction(h2));
		
		Configuration[] configurations = Configuration.allConfigurations(nAgents,nOpinions);
		BigFraction[] times1 = execution1.getConvergenceTimes();
		BigFraction[] times2 = execution2.getConvergenceTimes();
		
		for (int i=0 ; i<configurations.length ; i++) {
			
			System.out.println(configurations[i]);
			
			if (i == 0) {
				System.out.println(0);
				System.out.println(0);
			} else {
				System.out.println(times1[i-1].doubleValue());
				System.out.println(times2[i-1].doubleValue());
				if (times1[i-1].compareTo(times2[i-1]) < 0) {
					System.out.println("Here, the hierarchy collapses...");
					collapse = true;
				}
			}
			
			System.out.println();
			
		}
		
		return collapse;
		
	}
	
	public static void lookingForCollapse(int iStart, int iEnd) {
		
		for (int i=iStart ; i<=iEnd ; i++) {
			if (compareMajority(i,3,3,4)) {
				System.out.println("The hierarchy collapsed for n = "+i);
				break;
			}
		}
		
	}
	
	public static void lookingForCollapse(int i) {
		
		lookingForCollapse(i,i);
		
	}
	
	public static void saveToFile(int nAgents, int nOpinions, int h) {
		
		String filename = ""+h+"-majority_n="+nAgents+"_k="+nOpinions+"_analysis.txt";
		
		FileWriter writer;
		BufferedWriter bufferedWriter;
		
		try {
			writer = new FileWriter(filename, false);
	        bufferedWriter = new BufferedWriter(writer);
	        
	        GossipExecution execution = new GossipExecution(nAgents,nOpinions,new MajorityTransitionFunction(h));
	        
	        bufferedWriter.write("Gossip model, "+h+"-majority, "+nAgents+" agents, "+nOpinions+" opinions\n\n");
	        
	        for (int i=0 ; i<execution.nConfs ; i++) {
	        	
	        	bufferedWriter.write("Configuration "+execution.configurations[i].toString()+"\n");
	        	for (int j=0 ; j<nOpinions ; j++) {
	        		bufferedWriter.write("Probability to adopt opinion "+j+": "
	        				+execution.probabilities[i][j].toString()+" = "
	        				+execution.probabilities[i][j].doubleValue()+"\n");
	        	}
	        	bufferedWriter.write("\n");
	        	
	        }
	        
	        bufferedWriter.write("Probability to go from one configuration to another:\n\n");
	        for (int i=0 ; i<execution.nConfs ; i++) {
	        	for (int j=0 ; j<execution.nConfs ; j++) {
	        		bufferedWriter.write(execution.configurations[i].toString(
	        				)+" -> "+execution.configurations[j].toString()+"\n"
	        				+execution.transitions[i][j].toString()+"\n"
	        				+execution.transitions[i][j].doubleValue()+"\n\n");
	        	}
	        }
	        
	        BigFraction[] times = execution.getConvergenceTimes();
	        bufferedWriter.write("Exact expected convergence times from each configuration:\n\n");
	        for (int i=0 ; i<execution.nConfs ; i++) {
	        	
	        	BigFraction time = (i == 0) ? BigFraction.ZERO : times[i-1];
	        	bufferedWriter.write("Configuration "+execution.configurations[i].toString()+"\n"
	        			+time.toString()+"\n"
	        			+time.doubleValue()+"\n\n");
	        	
	        	
	        }
	        
	        Main.log("Results stored in '"+filename+"'\nEnd of computations.");
	        bufferedWriter.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}


	public static void main(String[] args) {
		
		init();
		
		//testSinkTime();
		//testCombinatorics();
		
		//GossipExecution execution = new GossipExecution(10,3,new MajorityTransitionFunction(3));
		//execution.printTransitions();
		//execution.printSinkTimes();
		
		//lookingForCollapse(56);
		
		if (args.length != 3) {
			System.out.println("Usage: java Main <number of agents> <number of opinions> <sample size>");
			return;
		}
		
		int nAgents = Integer.valueOf(args[0]);
		int nOpinions = Integer.valueOf(args[1]);
		int h = Integer.valueOf(args[2]);

		saveToFile(nAgents,nOpinions,h);
		
	}

}



















