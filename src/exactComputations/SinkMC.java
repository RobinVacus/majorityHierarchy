package exactComputations;

import java.util.LinkedList;

import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.linear.FieldDecompositionSolver;
import org.apache.commons.math3.linear.FieldLUDecomposition;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.FieldVector;
import org.apache.commons.math3.linear.MatrixUtils;

public class SinkMC {
	
	int n;
	BigFraction[][] transition;
	
	public SinkMC(BigFraction[][] transition) {
		
		this.n = transition.length;
		this.transition = transition;
		checkValidity();
		
	}
	
	private void checkValidity() {
		
		for (int i=1; i<n ; i++) {
			if (transition[i][0].compareTo(BigFraction.ZERO) <= 0) {
				throw new RuntimeException("The MC may never reach the sink.");
			}
		}
		
		for (int i=1; i<n ; i++) {
			BigFraction sum = BigFraction.ZERO;
			for (int j=0 ; j<n ; j++) {
				sum = sum.add(transition[i][j]);
			}
			if (sum.compareTo(BigFraction.ONE) != 0) {
				throw new RuntimeException("The probabilities out of node "+i+" add to "+sum+".");
			}
		}
		
	}
	
	public BigFraction[] expectedSinkTime() {
		
		System.out.println("Starting computation of expected convergence times.");
		System.out.println("Size of the matrix: "+n+"x"+transition[0].length+"\n");
		
		BigFraction[][] coeffs = new BigFraction[n-1][n-1];
		
		for (int i=1 ; i<n ; i++) {
			for (int j=1 ; j<n ; j++) {
				if (i == j) coeffs[i-1][j-1] = BigFraction.ONE.add(transition[i][j].negate());
				else coeffs[i-1][j-1] = transition[i][j].negate();
			}
		}
		
		BigFraction[] pi = new BigFraction[n-1];
		for (int i=1 ; i<n ; i++) {
			pi[i-1] = transition[i][0];
			for (int j=1 ; j<n ; j++) {
				pi[i-1] = pi[i-1].add(transition[i][j]);
			}
		}
		
		FieldMatrix<BigFraction> matrix = MatrixUtils.createFieldMatrix(coeffs);
		FieldVector<BigFraction> constants = MatrixUtils.createFieldVector(pi);
		
		FieldLUDecomposition<BigFraction> luDecomposition = new FieldLUDecomposition<BigFraction>(matrix);
		
		Main.log("LU Decomposition computed");
		
		FieldDecompositionSolver<BigFraction> solver = luDecomposition.getSolver();
		
		FieldVector<BigFraction> solution = solver.solve(constants);
		
		Main.log("Convergence times computed");
		
		return solution.toArray();
	}
	
	private int randomJump(int i) {
		
		double r = Utils.random.nextDouble();
		double s = transition[i][0].doubleValue();
		int index = 0;
		
		while (s < r) {
			index++;
			s += transition[i][index].doubleValue();
		}
		
		return index;
	}
	
	public double[] averageSinkTime(int iterations) {
		
		double[] result = new double[n-1];
		Aggregator[] data = new Aggregator[n-1];
		for (int i=0 ; i<n-1 ; i++) {
			data[i] = new Aggregator();
		}
		
		for (int k=0 ; k<iterations ; k++) {
			
			LinkedList<Integer> history = new LinkedList<Integer>();
			int tmp = Utils.random.nextInt(n-1)+1;
			
			while (tmp != 0) {
				history.add(tmp);
				tmp = randomJump(tmp);
			}
			
			int sinkTime = history.size();
			for (int x : history) {
				data[x-1].add(sinkTime);
				sinkTime--;
			}
			
		}
		
		for (int i=0 ; i<n-1 ; i++) {
			result[i] = data[i].get();
		}
		
		return result;
		
		
	}
	

}












































