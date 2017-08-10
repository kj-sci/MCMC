// ising_replica_siml.java
// applet for small size ising model

public class ising_replica_siml{
	public static void main(String[] args){
		/**********************************************
		 *                                            *
		 *        init parameters                     *
		 *                                            *
		 **********************************************/
		int L = 3;
		int size_ising = L*L; //L x L
		int num_siml = 100000;

		int num_parms = 10;
		double[] theta = new double[num_parms];
		double[] normed_temperture = new double[num_parms];
		theta[0] = 0.0;
		theta[1] = 0.1;
		theta[2] = 0.2;
		theta[3] = 0.4;
		theta[4] = 0.6;
		theta[5] = 0.8;
		theta[6] = 1.0;
		theta[7] = 1.25;
		theta[8] = 1.6;
		theta[9] = 2.0;
		
		for (int loop=0; loop<num_parms; loop++) {
			normed_temperture[loop] = 1/theta[loop];
		}

		System.err.println("size_ising: "+size_ising);
		
		ising_replica ising_hd = new ising_replica(L, num_parms, theta, "theta");

		/**********************************************
		 *                                            *
		 *        init output variables               *
		 *                                            *
		 **********************************************/

		 // used for calculate distribution of spin="up"s
		int[][] stats_ones = new int[num_parms][size_ising+1];
		for (int loop=0; loop<num_parms; loop++) {
			for (int idx=0; idx<size_ising+1; idx++) {
				stats_ones[loop][idx] = 0;
			}
			stats_ones[loop][ising_hd.get_num_ones(loop)]++;
		}

		// used for calculating partition functions
		double[] avg_energy = new double[num_parms];
		for (int loop=0; loop<num_parms; loop++) {
			avg_energy[loop] = -ising_hd.get_energy(loop);
		}

		/**********************************************
		 *                                            *
		 *      simulation                            *
		 *                                            *
		 *                                            *
		 **********************************************/
		for (int loop_siml=1; loop_siml<num_siml; loop_siml++) {
			ising_hd.iteration();

			// record # of "up"s
			for (int loop=0; loop<num_parms; loop++) {
				int cnt = ising_hd.get_num_ones(loop);
				stats_ones[loop][cnt]++;
			}

			// record average energy
			//System.out.println("-------------------------");
			for (int loop=0; loop<num_parms; loop++) {
				double this_energy = -ising_hd.get_energy(loop);
				//double tmp1 = ((float)loop_siml) / ((float)(loop_siml+1)) * avg_energy[loop];
				//double tmp2 = 1.0 / ((float)(loop_siml+1)) * this_energy;
				//double new_energy = tmp1+tmp2;
				//System.out.print(loop_siml+","+loop+": "+avg_energy[loop]+"  "+this_energy+ "=>"+tmp1+" "+tmp2+"=>"+new_energy);
				
				avg_energy[loop] = ((float)loop_siml) / ((float)(loop_siml+1)) * avg_energy[loop] + 1.0 / ((float)(loop_siml+1)) * this_energy;
				//System.out.println("=>"+avg_energy[loop]);
			}
		}
		
		/**********************************************
		 *                                            *
		 *        output                              *
		 *                                            *
		 *                                            *
		 **********************************************/
		print_stats(num_parms, size_ising, theta, stats_ones);
		
		System.out.println("average -energy:");
		for (int loop=0; loop<num_parms; loop++) {
			System.out.println(loop+"\t"+theta[loop]+"\t"+avg_energy[loop]);
		}
	}
	
	public static void print_stats(int num_parms, int size_ising, double[] theta, int[][] stats_ones) {
		System.out.println("Distribution of 'up's:");
		for (int loop=0; loop<num_parms; loop++) {
			System.out.println("===== theta["+loop+"]: "+theta[loop]+" ========");
			for (int idx = 0; idx<size_ising+1; idx++) {
				System.out.println(idx+"\t"+stats_ones[loop][idx]);
			}
		}
	}
}


