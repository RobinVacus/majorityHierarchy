package exact;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.linear.FieldDecompositionSolver;
import org.apache.commons.math3.linear.FieldLUDecomposition;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.FieldVector;
import org.apache.commons.math3.linear.MatrixUtils;



/**
 * 
 * Stores the convergence time of certain configurations under a given protocol.
 * 
 * @author robin
 *
 */
public class ConvergenceTime {
	
	/** Number of agents */
	public final int n;
	
	/** Number of distinct opinions */
	public final int m;
	
	/** Transition function */
	public final TransitionFunction function;
	
	ConfigurationTree tree;
	
	/** Expected convergence time of each configuration */
	BigFraction[] times;
	
	
	public ConvergenceTime(int n, int m, TransitionFunction function, ConfigurationTree tree) {
		
		if (tree.n != n || tree.m != m) {
			throw new RuntimeException("Invalid convergence tree");
		}
		
		this.n = n;
		this.m = m;
		this.function = function;
		this.tree = tree;
		
		times = new BigFraction[tree.configurations.length];
		
		if (times.length == 1) {
			times[0] = BigFraction.ZERO;
		}
		
	}
	
	public ConvergenceTime(String filename) throws FileNotFoundException {
		

		File file = new File(filename);
	    Scanner scanner = new Scanner(file);
	    
	    n = Integer.valueOf(scanner.nextLine());
	    m = Integer.valueOf(scanner.nextLine());
	    function = null;
	    tree = new ConfigurationTree(n,m);
	    times = new BigFraction[tree.configurations.length];
	    
	    int index = 0;
	    
	    while (index < times.length) {
	    	
	    	scanner.nextLine(); scanner.nextLine(); scanner.nextLine();
	    	BigInteger num = new BigInteger(scanner.nextLine());
	    	BigInteger den = new BigInteger(scanner.nextLine());
	    	times[index] = new BigFraction(num,den);
	    	index++;
	    	
	    }
	    scanner.close();
		
	}
		
	private BigFraction computeTransitionProbability(int ID1, int ID2) {
		
		BigFraction[] probabilities = function.getProbabilities(tree.configurations[ID1]);		
		BigFraction result = tree.getMultinomialCoefficient(ID2);
				
		BigFraction sum = BigFraction.ZERO;
		for (Configuration possibleConfig : tree.getSimilarConfigurations(ID2)) {
						
			BigFraction product = BigFraction.ONE;
			for (int k=0 ; k<m ; k++) {
				
				for (int l=0 ; l<possibleConfig.repartition[k] ; l++) {
					product = product.multiply(probabilities[k]);
				}
				
			}
			
			sum = sum.add(product);
			
		}
				
		result = result.multiply(sum);
						
		return result;
	}
	
	public void computeTimes(ConvergenceTime aux) {
		
		System.out.println("Computing times for n = "+n+", m = "+m+", function: "+function);
		
		if (aux.m != m-1) throw new RuntimeException("Invalid induction");
		
		// Number of configurations that are not already computed
		int k = times.length - aux.times.length;
		
		// Map from the subset of non-computed configurations to the set of all configurations
		int[] mapping = new int[k];
		
		// Putting away the non-computed configurations
		int index = 0;
		for (int i=0 ; i<tree.configurations.length ; i++) {
			Configuration c = tree.configurations[i];
			if (c.exactNumberOfOpinions() == m) {
				mapping[index] = i;
				index++;
			} else {
				times[i] = aux.times[aux.tree.getID(c)];
			}
		}
		
		System.out.println("Computing internal transition probabilities");
		// Transition probabilities among non-computed configurations
		BigFraction[][] internalTransitionProbabilities = new BigFraction[k][k];
		
		for (int i=0 ; i<k ; i++) {
			for (int j=0 ; j<k ; j++) {
				internalTransitionProbabilities[i][j] = computeTransitionProbability(mapping[i],mapping[j]);
			}
		}
		
		System.out.println("Computing constant coefficients");
		// Constant coefficient for each non-computed configurations
		BigFraction[] constantCoeffs = new BigFraction[k];
		
		for (int i=0 ; i<k ; i++) {
			constantCoeffs[i] = BigFraction.ZERO;
			
			for (int j=0 ; j<aux.times.length ; j++) {
				
				Configuration c = aux.tree.configurations[j];
				BigFraction tmp = computeTransitionProbability(mapping[i],tree.getID(c));
				tmp = tmp.multiply(aux.times[j].add(BigFraction.ONE));
				
				constantCoeffs[i] = constantCoeffs[i].add(tmp);
			}
			
			for (int j=0 ; j<k ; j++) {
				constantCoeffs[i] = constantCoeffs[i].add(internalTransitionProbabilities[i][j]);
			}
			
		}
		
		System.out.println("Computing linear system");
		// Linear system
		BigFraction[][] matrixCoeffs = new BigFraction[k][k];
		for (int i=0 ; i<k ; i++) {
			for (int j=0 ; j<k ; j++) {
				if (i == j) matrixCoeffs[i][j] = BigFraction.ONE.add(internalTransitionProbabilities[i][j].negate());
				else matrixCoeffs[i][j] = internalTransitionProbabilities[i][j].negate();
			}
		}
		
		FieldMatrix<BigFraction> matrix = MatrixUtils.createFieldMatrix(matrixCoeffs);
		FieldVector<BigFraction> constant = MatrixUtils.createFieldVector(constantCoeffs);
		
		System.out.println("Computing expected convergence times");
		
		FieldLUDecomposition<BigFraction> luDecomposition = new FieldLUDecomposition<BigFraction>(matrix,true);
		
		System.out.println("LU Decomposition computed");
		
		FieldDecompositionSolver<BigFraction> solver = luDecomposition.getSolver();
				
		FieldVector<BigFraction> solution = solver.solve(constant);
		
		System.out.println("Convergence times computed");
		
		for (int i=0 ; i<k ; i++) times[mapping[i]] = solution.getEntry(i);
		
		
	}
	
	public void printToFile(String filename) throws IOException {
		
		FileWriter writer = new FileWriter(filename, false);
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		
		bufferedWriter.write(n+"\n");
		bufferedWriter.write(m+"\n");
		bufferedWriter.write("Transition function = "+function+"\n");
		bufferedWriter.write("\n");
		
		for (int i=0 ; i<times.length ; i++) {
			bufferedWriter.write(tree.configurations[i]+"\n");
			bufferedWriter.write(times[i].getNumerator()+"\n");
			bufferedWriter.write(times[i].getDenominator()+"\n");
			bufferedWriter.write(times[i].doubleValue()+"\n\n");
		}
		
		
		bufferedWriter.close();
		
	}
	
	public static void main(String[] args) throws IOException {
		
		int n = 6;
		
		ConfigurationTree tree = new ConfigurationTree(n,2);
		TransitionFunction function = new MajorityTransitionFunction(3,2,tree);
		
		ConvergenceTime time1 = new ConvergenceTime(n,1,function,new ConfigurationTree(n,1));
		ConvergenceTime time2 = new ConvergenceTime(n,2,function,tree);
		
		time2.computeTimes(time1);
		
		for (int i=0 ; i<time2.times.length ; i++) {
			System.out.println(time2.tree.configurations[i]+" "+time2.times[i].doubleValue());
		}
		
		ConfigurationTree tree3 = new ConfigurationTree(n,3);
		TransitionFunction function3 = new MajorityTransitionFunction(3,3,tree3);
		
		ConvergenceTime time3 = new ConvergenceTime(n,3,function3,tree3);
		
		time3.computeTimes(time2);
		
		for (int i=0 ; i<time3.times.length ; i++) {
			System.out.println(time3.tree.configurations[i]+" "+time3.times[i].doubleValue());
		}
		
		time3.printToFile("test.txt");
		ConvergenceTime time4 = new ConvergenceTime("test.txt");
		
		for (int i=0 ; i<time4.times.length ; i++) {
			System.out.println(time4.tree.configurations[i]+" "+time4.times[i].doubleValue());
		}
		
	}
	
	/*
	{ 6 0 0 } 0.0
	{ 5 1 0 } 1.767053685888968
	{ 4 2 0 } 3.3704690112589857
	{ 4 1 1 } 3.120221567125276
	{ 3 3 0 } 4.234516240191755
	{ 3 2 1 } 4.504929298193759
	{ 2 2 2 } 5.090174658071745
	*/

}































