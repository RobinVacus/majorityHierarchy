package exactComputations;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.math3.fraction.*;

public class Main {
	
	public static long lastStep;
	public static boolean firstLog = true;
	
	public static void log(String s) {
		
		System.out.println(s);
		
		if (firstLog) {
			firstLog = false;
			System.out.println();
			lastStep = System.currentTimeMillis();
			return;
		}
		
		long newStep = System.currentTimeMillis();
		long difference = newStep - lastStep;
		
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

	
	public static void saveToFile(int nAgents, int nOpinions, int h) {
		
		String filename = ""+h+"-majority_n="+nAgents+"_k="+nOpinions+"_analysis.txt";
		
		FileWriter writer;
		BufferedWriter bufferedWriter;
		
		try {
			writer = new FileWriter(filename, false);
	        bufferedWriter = new BufferedWriter(writer);
	        
	        GossipExecution execution = new GossipExecution(nAgents,nOpinions,new MajorityTransitionFunction(h));
	        
	        bufferedWriter.write("Gossip model, "+h+"-majority, "+nAgents+" agents, "+nOpinions+" opinions\n\n");
	        /*
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
	        
	        
	        bufferedWriter.write("Exact expected convergence times from each configuration:\n\n");
	        */
	        
	        BigFraction[] times = execution.getConvergenceTimes();
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

	
	public static String filename(int nAgents, int nOpinions, int h) {
		return "n="+nAgents+"_m="+nOpinions+"_h="+h+".txt";
	}

	public static Result serialize(int nAgents, int nOpinions, int h) throws IOException {
		
		GossipExecution execution = new GossipExecution(nAgents,nOpinions,new MajorityTransitionFunction(h));
		
		Result result = execution.compute();
		
		FileOutputStream fileOutputStream
	      = new FileOutputStream(filename(nAgents,nOpinions,h));
	    ObjectOutputStream objectOutputStream 
	      = new ObjectOutputStream(fileOutputStream);
	    objectOutputStream.writeObject(result);
	    objectOutputStream.flush();
	    objectOutputStream.close();
	    
	    return result;
		
	}
	
	public static Result deserialize(int nAgents, int nOpinions, int h) throws IOException, ClassNotFoundException {
		
		FileInputStream fileInputStream 
	      = new FileInputStream(filename(nAgents,nOpinions,h));
	    ObjectInputStream objectInputStream 
	      = new ObjectInputStream(fileInputStream);
	    Result result = (Result) objectInputStream.readObject();
	    objectInputStream.close();
	    
	    return result;
		
	}
	
	public static Result get(int nAgents, int nOpinions, int h) {
		
		try {
			return deserialize(nAgents,nOpinions,h);
		}
		catch (IOException e) {
			try {
				return serialize(nAgents,nOpinions,h);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	public static void compare(Result r1, Result r2) {
		
		for (int i=0 ; i<r1.convergenceTimes.length; i++) {
			
			if (r1.convergenceTimes[i].compareTo(r2.convergenceTimes[i]) < 0) {
				System.out.println("Collapse for nAgents = "+r1.nAgents+" on configuration "+r1.configurationString(i));
				System.out.println("h = 3: "+r1.convergenceTimes[i].doubleValue());
				System.out.println("h = 4: "+r2.convergenceTimes[i].doubleValue());
				System.out.println();
			}
			
		}
		
		
	}
	
	public static void lookForCollapse(int nAgents) {
		
		Result result3 = get(nAgents,3,3);
		Result result4 = get(nAgents,3,4);
		
		compare(result3,result4);
		
		System.out.println("No collapse for nAgents = "+nAgents);
		
	}
	
	public static void main(String[] args) throws IOException {
				
		//testSinkTime();
		//testCombinatorics();
		
		//GossipExecution execution = new GossipExecution(10,3,new MajorityTransitionFunction(3));
		//execution.printTransitions();
		//execution.printSinkTimes();
		
		//lookingForCollapse(35);
		
		if (args[0].equals("--search")) {
			
			if (args.length != 3) {
				System.out.println("Usage: java Main --search <min number of agents> <max number of agents>");
				return;
			}
			
			int nAgentsMin = Integer.valueOf(args[1]);
			int nAgentsMax = Integer.valueOf(args[2]);
			
			for (int nAgents = nAgentsMin ; nAgents <= nAgentsMax ; nAgents++) {
				lookForCollapse(nAgents);
			}
		}
		
		if (args[0].equals("--get")) {
			
			if (args.length != 4) {
				System.out.println("Usage: java Main --get <number of agents> <number of opinions> <sample size>");
				return;
			}
			
			int nAgents = Integer.valueOf(args[1]);
			int nOpinions = Integer.valueOf(args[2]);
			int h = Integer.valueOf(args[3]);
			
			System.out.println(serialize(nAgents,nOpinions,h));
			
		}
		
		

		//saveToFile(nAgents,nOpinions,h);
		
	}

}



















