package exactComputations;

import java.io.Serializable;

import org.apache.commons.math3.fraction.BigFraction;

public interface TransitionFunction extends Serializable {
	
	public BigFraction[] getProbabilities(Configuration configuration);
	
	public int randomExecution(Configuration configuration);

}
