
public class ising_siml {
	public static void main(String[] args) {
		int L = 3;
		int size_ising = L*L;
		int num_siml = 100000;
		double normed_temperture_center = 2.27;
		
		ising ising_hd = new ising(L, normed_temperture_center, 300, 300);
		ising_hd.set_theta(0.2);
		
		// statistics
		int[] stats_ones = new int[size_ising+1];
		for (int loop=0; loop<size_ising+1; loop++) {
			stats_ones[loop] = 0;
		}
		
		// simulation
		for (int loop=0; loop<num_siml; loop++) {
			int cnt_ones = ising_hd.iteration();
			stats_ones[cnt_ones]++;
		}

		// print stats_ones
		System.out.println("Stats:");
		for (int loop=0; loop<size_ising+1; loop++) {
			System.out.println(loop+"\t"+stats_ones[loop]);
		}
		
	}

}

