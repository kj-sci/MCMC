import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.File;

class ising {
	int L;
	int[][] ising_state;
	int num_ones;
	
	double normed_temperture;
	double theta;
	
	ising_gibbs mcmc_hd;
		
	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *            Init Functions                                                                 *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	ising() {
		mcmc_hd = new ising_gibbs();
	}

	ising(int this_L, double t, String flg){
		mcmc_hd = new ising_gibbs(this_L, t, flg);
		init_ising(this_L, t, flg);
	}
	
	ising(int this_L, double t, String flg, int w, int h){
		mcmc_hd = new ising_gibbs(this_L, t, flg);
		init_ising(this_L, t, flg);
	}
	
	public void init_ising(int this_L, double t, String flg) {
		set_size(this_L);

		if (flg.equals("temperture")) {
			set_temperture(t);
		} else if(flg.equals("theta")) {
			set_theta(t);
		} else {
			System.err.println("Error in ising.init_ising: flg="+flg);
		}
	}
	
	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *            Set Functions                                                                  *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	public void set_size(int s) {
		L = s;
		
		mcmc_hd.set_size(s);

		// init ising model
		int[][] dummy = new int[L+2][L+2];
		ising_state = dummy;
		init_state();

	}
	 public void init_state() {
		num_ones = 0;

		// init ising state
		for (int loop_x = 0; loop_x < L+2; loop_x++) {
			for (int loop_y = 0; loop_y < L+2; loop_y++) {
				if (loop_x == 0 || loop_y == 0 || loop_x == L+1 || loop_y == L+1) {
					ising_state[loop_x][loop_y] = 0;
				} else {
					ising_state[loop_x][loop_y] = 1;
					if (ising_state[loop_x][loop_y] == 1) {
						num_ones++;
					}
				}
			}
		}
	 }
	 
	// for pararell tempering method
	 public void set_state(int[][] s) {
		num_ones = 0;
		for (int loop_x = 0; loop_x < L+2; loop_x++) {
			for (int loop_y = 0; loop_y < L+2; loop_y++) {
				ising_state[loop_x][loop_y] = s[loop_x][loop_y];
				if (ising_state[loop_x][loop_y] == 1) {
					num_ones++;
				}
			}
		}
	 }
	 
	 public void set_temperture(double temp) {
		normed_temperture = temp;
		if (normed_temperture > 0) {
			theta = 1/normed_temperture;
		}
		
		mcmc_hd.set_temperture(temp);
	}
	
	 public void set_theta(double tt) {
		theta = tt;
		if (theta > 0) {
			normed_temperture = 1/theta;
		}

		mcmc_hd.set_theta(tt);
	}
	
	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *            Get Functions                                                                  *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/

	public void copy_state(int[][] s) {
		for (int loop_x = 0; loop_x < L+2; loop_x++) {
			for (int loop_y = 0; loop_y < L+2; loop_y++) {
				s[loop_x][loop_y] = ising_state[loop_x][loop_y];
			}
		}
	}

	public int[][] get_state() {
		return ising_state;
	}
	
	public int get_num_ones() {
		return num_ones;
	}
	
	public int get_energy() {
		int energy = 0;
		// horizontal links
		for (int loop_y=1; loop_y<L+1; loop_y++) {
			for (int loop_x=1; loop_x<L; loop_x++) {
				energy += -ising_state[loop_x][loop_y]*ising_state[loop_x+1][loop_y];
			}
		}
		// vertical links
		for (int loop_x=1; loop_x<L+1; loop_x++) {
			for (int loop_y=1; loop_y<L; loop_y++) {
				energy += -ising_state[loop_x][loop_y]*ising_state[loop_x][loop_y+1];
			}
		}

		return energy;
	}
	
	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *            Functions for Simulation                                                       *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	 public void simulation(int num_siml) {
		for (int loop = 0; loop<num_siml; loop++) {
			num_ones += mcmc_hd.iteration(ising_state);
			
		}
	}

	public int iteration() {
		num_ones += mcmc_hd.iteration(ising_state);
		
		return get_num_ones();
	}	

	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *            Print Functions                                                                *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	public void print_state() {
		for (int loop_y=1; loop_y<L+1; loop_y++) {
			for (int loop_x=1; loop_x<L+1; loop_x++) {
				if (loop_x > 1){
					System.out.print(" ");
				}
				if (ising_state[loop_x][loop_y] == 1) {
					System.out.print("+");
				} else {
					System.out.print("-");
				}
			}
			System.out.println("");
		}
	}
}


