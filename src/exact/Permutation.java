package exact;

import java.util.ArrayList;
import java.util.HashMap;

public class Permutation {
	
	private int n;
	private int[] map;
	public final int ID;
	
	public Permutation(int n, int[] map, int ID) {
		
		this.n = n;
		this.map = map;
		this.ID = ID;
	}
	
	public int get(int i) {
		return map[i];
	}
	
	public String toString() {
		
		String s = "( ";
		for (int i : map) {
			s += i+" ";
		}
		return s+") "+ID;
	}
	
	/* Static methods */
	
	private static void aux(ArrayList<Permutation> result, int[] acc, int index, ArrayList<Integer> left) {
		
		if (index == acc.length) {
			result.add(new Permutation(acc.length,acc,result.size()));
			return;
		}
		
		for (int i=0 ; i<left.size() ; i++) {
			
			int[] newAcc = new int[acc.length];
			for (int j=0 ; j<index ; j++) newAcc[j] = acc[j];
			newAcc[index] = left.get(i);
			
			@SuppressWarnings("unchecked")
			ArrayList<Integer> newLeft = (ArrayList<Integer>) left.clone();
			newLeft.remove(i);
			aux(result,newAcc,index+1,newLeft);
			
		}
		
	}
	
	private static HashMap<Integer,Permutation[]> memory = new HashMap<Integer,Permutation[]>();
	
	public static Permutation[] allPermutations(int n) {
		
		if (memory.containsKey(n)) return memory.get(n);
		
		ArrayList<Permutation> tmp = new ArrayList<Permutation>();
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i=0 ; i<n ; i++) list.add(i);
		aux(tmp,new int[n],0,list);
		
		Permutation[] result = new Permutation[tmp.size()];
		result = tmp.toArray(result);
		
		memory.put(n,result);
		
		return result;
	}

}











