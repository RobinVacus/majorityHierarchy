package exact;

import java.io.IOException;

public class Main {
	
	public static void main(String [] args) throws IOException {
		
		int n = Integer.valueOf(args[0]);
		
		ConfigurationTree tree2 = new ConfigurationTree(n,2);
		
		TransitionFunction maj34 = new MajorityTransitionFunction(3,2,tree2);
		
		ConvergenceTime time1 = new ConvergenceTime(n,1,maj34,new ConfigurationTree(n,1));
		ConvergenceTime time2 = new ConvergenceTime(n,2,maj34,tree2);
		time2.computeTimes(time1);
		time2.printToFile("times_n="+n+"_m=2.txt");
		
		ConfigurationTree tree3 = new ConfigurationTree(n,3);
		TransitionFunction maj3 = new MajorityTransitionFunction(3,3,tree3);
		TransitionFunction maj4 = new MajorityTransitionFunction(4,3,tree3);
		
		ConvergenceTime time3 = new ConvergenceTime(n,3,maj3,tree3);
		time3.computeTimes(time2);
		time3.printToFile("times_n="+n+"_m=3_h=3.txt");
		
		ConvergenceTime time4 = new ConvergenceTime(n,3,maj4,tree3);
		time4.computeTimes(time2);
		time4.printToFile("times_n="+n+"_m=3_h=4.txt");
		
	}

}
