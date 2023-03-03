package exact;

import java.util.Random;

import org.apache.commons.math3.fraction.BigFraction;

public class Utils {
	
	public static Random random = new Random();
	
	public static BigFraction multinomialCoefficient(int[] list, int n, int argmax) {
				
		BigFraction result = BigFraction.ONE;
		int tmp = n;
		
		for (int i=0 ; i<list.length ; i++) {
			
			if (i != argmax) {
				for (int j=1 ; j<=list[i] ; j++) {
					result = result.multiply(new BigFraction(tmp,j));
					tmp --;
				}
			}
		}
		
		return result;
		
	}
	
	public static BigFraction multinomialCoefficient(int[] list, int n) {
		return multinomialCoefficient(list,n,firstArgmax(list));
	}
	
	public static int firstArgmax(int[] list) {
		int imax = 0;
		int max = list[0];
		for (int i=1 ; i<list.length ; i++) {
			if (list[i] > max) {
				max = list[i];
				imax = i;
			}
		}
		return imax;
	}

}
