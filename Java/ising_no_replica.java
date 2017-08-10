// ising_no_replica.java
// 3 ising models simlate independently

public class ising_no_replica{
	int L;
	int size_ising; // L x L

	// # of layers
	int num_parms;

	ising[] ising_hd;
	double[] theta;
	double[] normed_temperture;
	
	// for exchange
	int[][] temp_state;
	
	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *                      Init Functions                                                       *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	ising_no_replica() {
	}
	
	ising_no_replica(int this_L, int this_num, double[] t, String flg) {
		init_parm(this_L, this_num, t, flg);
		init_ising(flg);
	}
	
	public void init_parm(int this_L, int this_num, double[] t, String flg) {
		num_parms = this_num;

		L = this_L;
		size_ising = L*L;
	
		theta = new double[num_parms];
		normed_temperture = new double[num_parms];

		if (flg.equals("temperture")) {
			set_temperture(t);
		} else if(flg.equals("theta")) {
			set_theta(t);
		} else {
			System.err.println("Error in ising_no_replica.init_param: flg="+flg);
		}

				// for exchanging states
		int[][] dummy = new int[L+2][L+2];
		temp_state = dummy;
	}

	public void init_ising(String flg) {
		// init each ising model
		ising_hd = new ising[num_parms];
		for (int loop=0; loop<num_parms; loop++) {
			if (flg.equals("temperture")) {
				ising_hd[loop] = new ising(L, normed_temperture[loop], flg);
			} else if(flg.equals("theta")) {
				ising_hd[loop] = new ising(L, theta[loop], flg);
			} else {
				System.err.println("Error in ising_no_replica.init_ising: flg="+flg);
			}
		}
	}

	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *                      Set Functions                                                        *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	public void set_theta(double[] t) {
		for (int loop=0; loop<num_parms; loop++) {
			theta[loop] = t[loop];
			if (theta[loop] > 0) {
				normed_temperture[loop] = 1.0/theta[loop];
			} else {
				normed_temperture[loop] = 9999999;
			}
		}
	}
	
	public void set_temperture(double[] t) {
		for (int loop=0; loop<num_parms; loop++) {
			theta[loop] = t[loop];
			if (theta[loop] > 0) {
				normed_temperture[loop] = 1.0/theta[loop];
			} else {
				normed_temperture[loop] = 9999999;
			}
		}
	}
	
	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *                      Get Functions                                                        *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	
	public int[][] get_state(int idx) {
		return ising_hd[idx].get_state();
	}

	public int get_num_ones(int idx) {
		return ising_hd[idx].get_num_ones();
	}

	public int get_energy(int idx) {
		return ising_hd[idx].get_energy();
	}
	
	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *                      Simulation Functions                                                 *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
		
	public void iteration() {
		for (int loop=0; loop<num_parms; loop++) {
			ising_hd[loop].iteration();
			exchange(0);
		}
	}
	
	public void exchange(int num) {
		for (int loop=0; loop<num; loop++) {
			int idx = (int)((num_parms-1) * Math.random());
			//int energy_1 = ising_hd[idx].get_energy();
			//int energy_2 = ising_hd[idx+1].get_energy();
			int diff_energy = ising_hd[idx+1].get_energy() - ising_hd[idx].get_energy();
			double diff_theta = theta[idx+1]-theta[idx];
			double r = Math.exp(diff_theta*diff_energy);
			
			/*
			System.out.println("================================");
			ising_hd[idx].print_state();
			System.out.println("------------------------------");
			ising_hd[idx+1].print_state();
			System.out.println("------------------------------");
			System.out.println("idx: "+idx);
			System.out.println("energy: "+energy_1+", "+energy_2);
			System.out.println("theta: "+theta[idx]+", "+theta[idx+1]);
			System.out.println("r: "+r);
			*/
			
			if (Math.random() < r) {
				//System.out.println("Exchange!!");
				ising_hd[idx].copy_state(temp_state);
				ising_hd[idx].set_state(ising_hd[idx+1].get_state());
				ising_hd[idx+1].set_state(temp_state);
				/*
				System.out.println("||||||||||||||||||||||||||||||");
				ising_hd[idx].print_state();
				System.out.println("------------------------------");
				ising_hd[idx+1].print_state();
				System.out.println("------------------------------");
				*/
			}
		}
	}
}


