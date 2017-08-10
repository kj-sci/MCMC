public class ising_gibbs {
	int L;
	double normed_temperture;
	double theta;

	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *            Init Functions                                                                 *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/

	public ising_gibbs() {
	}
	
	public ising_gibbs(int this_L, double t, String flg) {
		set_size(this_L);

		if (flg.equals("temperture")) {
			set_temperture(t);
		} else if(flg.equals("theta")) {
			set_theta(t);
		} else {
			System.err.println("Error in ising_gibbs: flg="+flg);
		}
	}

	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *            Set Functions                                                                  *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	public void set_size(int this_L) {
		L = this_L;
	}
	 
	 public void set_temperture(double temp) {
		normed_temperture = temp;
		if (normed_temperture > 0) {
			theta = 1/normed_temperture;
		}
	}
	
	 public void set_theta(double tt) {
		theta = tt;
		if (theta > 0) {
			normed_temperture = 1/theta;
		}
	}
	
	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *            Functions for Simulation                                                       *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	/* Simulation (Gibbs Sampler) */
	public int iteration(int[][] s) {
		// incremental number of 1's
		int flg = 0;

		// choose a node
		int i = (int)(L * Math.random()) + 1;
		int j = (int)(L * Math.random()) + 1;

		// calc energy

		int e00 = s[i][j - 1] + s[i][j + 1] + s[i - 1][j] + s[i + 1][j];
		int e0 = -s[i][j]*(s[i][j - 1] + s[i][j + 1] + s[i - 1][j] + s[i + 1][j]);

		// calc conditional probability
		double p = (1.0 + Math.tanh(theta * e0))/2.0;
		
		if (Math.random() < p) {
			s[i][j] = -s[i][j];
			if (s[i][j] == 1) {
				flg = 1;
			} else {
				flg = -1;
			}
		}

		return flg;
	}

	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *            Print Functions                                                                *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	public void print_state(int[][] s) {
		for (int loop_y=1; loop_y<L+1; loop_y++) {
			for (int loop_x=1; loop_x<L+1; loop_x++) {
				if (loop_x > 1){
					System.out.print(" ");
				}
				if (s[loop_x][loop_y] == 1) {
					System.out.print("+");
				} else {
					System.out.print("-");
				}
			}
			System.out.println("");
		}
	}
}
