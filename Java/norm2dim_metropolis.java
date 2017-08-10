// pdf = A * exp(-(x^2-2*beta*x*y+y^2)/2)
public class norm2dim_metropolis {
	double beta;
	double sigma;
	double[] state;
	double[] step;
	double[] candidate;
	
	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *            Init Functions                                                                 *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/

	public norm2dim_metropolis() {
	}
	
	public norm2dim_metropolis(double this_beta, double this_sigma) {
		set_beta(this_beta);
		set_sigma(this_sigma);
		init_state();
	}

	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *            Set Functions                                                                  *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	public void set_beta(double this_beta) {
		beta = this_beta;
	}
	 
	public void set_sigma(double this_sigma) {
		sigma = this_sigma;
	}
	
	public void init_state() {
		state = new double[2];
		state[0] = 0;
		state[1] = 3;

		step = new double[2];
		candidate = new double[2];
	}
	
	public void set_state(double[] s) {
		for (int loop = 0; loop < 2; loop++) {
			state[loop] = s[loop];
		}
	}

	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *            Get Functions                                                                  *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/

	public void copy_state(double[] s) {
		for (int loop = 0; loop < 2; loop++) {
			s[loop] = state[loop];
		}
	}

	public double[] get_state() {
		return state;
	}
		
	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *            Functions for Simulation                                                       *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	/* Simulation (Metropolis Method) */
	public void iteration() {
		set_step(step);
		for (int loop=0; loop<2; loop++) {
			candidate[loop] = state[loop]+step[loop];
		}
		double r = set_r(state, candidate);
		if (accept(r)) {
			for (int loop=0; loop<2; loop++) {
				state[loop] = candidate[loop];
			}
		}
	}

	private void set_step(double[] s) {
		for (int loop=0; loop<2; loop++) {
			s[loop] = (2*Math.random()-1)*sigma;
		}
	}

	private double set_r(double[] from, double[] to) {
		if (to[1] < 0.5*to[0]*to[0] + 3) {
			return 0;
		} else {
			double arg = from[0]*from[0]-2*beta*from[0]*from[1]+from[1]*from[1];
			arg -= to[0]*to[0]-2*beta*to[0]*to[1]+to[1]*to[1];
			arg /= 2.0;
			return Math.exp(arg);
		}
	}

	private boolean accept(double r) {
		if (Math.random() < r) {
			return true;
		} else {
			return false;
		}
	}
	
	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *            Print Functions                                                                *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	public void print_state() {
		System.out.println("("+state[0]+", "+state[1]+")");
	}
}
