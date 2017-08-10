import java.util.*;

// pdf = A * exp(-(x^2-2*beta*x*y+y^2)/2)
public class norm2dim_montecarlo {
	double beta;
	double sigma;
	double[] state;
	double[] step;
	double[] candidate;
	Random r;
	
	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *            Init Functions                                                                 *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/

	public norm2dim_montecarlo() {
	}
	
	public norm2dim_montecarlo(double this_beta, double this_sigma) {
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
		r = new Random();
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
	/* Simulation (Gibbs Sampler) */
	public boolean iteration() {
		int idx = (int)(2*Math.random());
		set_state(idx, state);
		return accept(state);
	}

	private void set_state(int idx, double[] s) {
		int idx2 = (idx+1) % 2;
		double mean = beta * state[idx2];
		s[idx] = r.nextGaussian()+mean;
	}

	private boolean accept(double[] s) {
		if (s[1] < 0.5*s[0]*s[0] + 3) {
			return false;
		} else {
			return true;
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
