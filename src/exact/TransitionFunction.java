package exact;

import org.apache.commons.math3.fraction.BigFraction;


public interface TransitionFunction {
	
	public BigFraction[] getProbabilities(Configuration configuration);
	
	//public int randomExecution(Configuration configuration);

}
