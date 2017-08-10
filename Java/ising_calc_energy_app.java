// ising_calc_energy_app.java
// applet for small size ising model

import java.applet.Applet;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

public class ising_calc_energy_app extends Applet implements Runnable{
	// panel data
	int[] size_panel;
	//int min_size_panel;
	// window data
	int[] size_window;
	int[][] window_offset;
	int[][] ising_offset;
	int margin_window;

	// ising (display) data
	int ising_interval;
	int rad_circle;
	int[] placement;
	
	ising_replica ising_hd;
	
	int L;
	int size_ising; //L x L
	int num_siml;
	int loop_siml;

	int num_parms;
	double[] theta;
	double[] normed_temperture;
	
	int[][] stats_ones;
	double[] avg_energy;
	double[] ln_partition_function;
	int burn_in;
	int num_skip;

	Thread th = null;
	
	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *                                                                                           *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	public void init() {
		// setting for simulation
		num_parms = 10;
		L = 3;
		size_ising = L*L;

		size_panel = new int[2];
		size_panel[0] = getSize().width;
		size_panel[1] = getSize().height;

		// window data
		margin_window = 10;
		size_window = new int[2];
		size_window[0] = size_panel[0];
		size_window[1] = (size_panel[1] - (num_parms-1)*margin_window)/num_parms;

		// 
		int min_size_window = size_window[0];
		if (min_size_window > size_window[1]) {
			min_size_window = size_window[1];
		}
		
		window_offset = new int[num_parms][2];
		for (int loop=0; loop<num_parms; loop++) {
			window_offset[loop] = new int[2];
			window_offset[loop][0] = 0;
			window_offset[loop][1] = loop*(size_window[1]+margin_window);
		}

		// set radius
		rad_circle = min_size_window / (L - 1) / 5;
		
		// set size of the square
		ising_interval = (min_size_window - 2*rad_circle)/(L - 1);
		ising_offset = new int[num_parms][2];
		for (int loop=0; loop<num_parms; loop++) {
			ising_offset[loop][0] = window_offset[loop][0] + (int)(size_window[0]/2 - ising_interval*((float)(L-1)/2.0));
			ising_offset[loop][1] = window_offset[loop][1] + (int)(size_window[1]/2 - ising_interval*((float)(L-1)/2.0));
		}
		
		placement = new int[2];
			
		num_siml = 1000;
		burn_in = 1000;
		num_skip = 5;
		loop_siml = 0;

		theta = new double[num_parms];
		normed_temperture = new double[num_parms];
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
		
		stats_ones = new int[num_parms][size_ising+1];
		for (int loop=0; loop<num_parms; loop++) {
			for (int idx=0; idx<size_ising+1; idx++) {
				stats_ones[loop][idx] = 0;
			}
		}
		
		avg_energy = new double[num_parms];
		ln_partition_function = new double[num_parms];
		ln_partition_function[0] = Math.log(Math.pow(2, size_ising));
		
		ising_hd = new ising_replica(L, num_parms, theta, "theta");
		
		th = new Thread(this);
		th.start();
	}

	public void update(Graphics g) {
		paint(g);
	}
	
	public void paint(Graphics g) {
		if (loop_siml == 1) {
			for (int loop=0; loop<num_parms; loop++) {
				paint_frame(loop, g);
				String label = "theta: " + Double.toString(theta[loop]);
				g.drawString(label, window_offset[loop][0]+10, window_offset[loop][1]+30);
				if (loop>0) {
					g.setColor(Color.black);
					g.drawLine(0, window_offset[loop][1]-margin_window/2, size_panel[0], window_offset[loop][1]-margin_window/2);
				}
			}
		}
		
		for (int loop=0; loop<num_parms; loop++) {
			paint_state(loop, g);
		}
	}
	

	public void run() {
		for (int loop=0; loop<burn_in; loop++) {
			ising_hd.iteration();
		}

		for (int loop=0; loop<num_parms; loop++) {
			stats_ones[loop][ising_hd.get_num_ones(loop)]++;
		}

		for (int loop=0; loop<num_parms; loop++) {
			avg_energy[loop] = -ising_hd.get_energy(loop);
		}
		
		for (loop_siml=1; loop_siml<num_siml; loop_siml++) {
			for (int loop=0; loop<num_skip; loop++) {
				ising_hd.iteration();
			}
			
			for (int loop=0; loop<num_parms; loop++) {
				int cnt = ising_hd.get_num_ones(loop);
				//System.err.println("loop, cnt: "+loop+", "+cnt);
				stats_ones[loop][cnt]++;
			}

			for (int loop=0; loop<num_parms; loop++) {
				double this_energy = -ising_hd.get_energy(loop);
				avg_energy[loop] = ((float)loop_siml) / ((float)(loop_siml+1)) * avg_energy[loop] + 1.0 / ((float)(loop_siml+1)) * this_energy;
				if (loop>0) {
					ln_partition_function[loop] = ln_partition_function[loop-1] + (avg_energy[loop-1]+avg_energy[loop])*(theta[loop]-theta[loop-1])/2.0;
				}
			}
			
			print_stats();
			
			repaint();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}
	
	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *            Graphics Functions                                                             *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	
	public void paint_frame(int loop, Graphics gg) {
		gg.setColor(Color.white);
		gg.fillRect(window_offset[loop][0], window_offset[loop][1], window_offset[loop][0]+size_window[0], window_offset[loop][1]+size_window[1]);

		int[] from_pt = new int[2];
		int[] to_pt = new int[2];
		

		gg.setColor(Color.black);
		for (int loop_x = 0; loop_x < L; loop_x++) {
			from_pt[0] = ising_offset[loop][0] + ising_interval * loop_x;
			from_pt[1] = ising_offset[loop][1];
			to_pt[0] = ising_offset[loop][0] + ising_interval * loop_x;
			to_pt[1] = ising_offset[loop][1] + ising_interval * (L-1);
			gg.drawLine(from_pt[0], from_pt[1], to_pt[0], to_pt[1]);
		}
		
		for (int loop_y = 0; loop_y < L; loop_y++) {
			from_pt[0] = ising_offset[loop][0];
			from_pt[1] = ising_offset[loop][1] + ising_interval * loop_y;
			to_pt[0] = ising_offset[loop][0] + ising_interval * (L-1);
			to_pt[1] = ising_offset[loop][1] + ising_interval * loop_y;
			gg.drawLine(from_pt[0], from_pt[1], to_pt[0], to_pt[1]);
		}
	}

	public void paint_state(int loop, Graphics gg) {
		int[][] ising_state = ising_hd.get_state(loop);
		
		for (int loop_x = 0; loop_x < L; loop_x++) {
			for (int loop_y = 0; loop_y < L; loop_y++) {
				placement[0] = ising_offset[loop][0] + ising_interval * loop_x - rad_circle;
				placement[1] = ising_offset[loop][1] + ising_interval * loop_y - rad_circle;

				if (ising_state[loop_x+1][loop_y+1] == 1) {
					gg.setColor(Color.red);
				} else {
					gg.setColor(Color.blue);
				}
				gg.fillOval(placement[0], placement[1], rad_circle*2, rad_circle*2);
			}
		}
	}

	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *            Print Functions                                                                *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	public void print_stats() {
		System.out.println("*** "+loop_siml+" [idx|theta|dist #(up spins)|-energy|ln(partition_func)] ***");
		for (int loop=0; loop<num_parms; loop++) {
			System.out.print(loop+"| "+String.format("%1.2f", theta[loop])+"| ");
			for (int idx = 0; idx<size_ising+1; idx++) {
				System.out.print(" "+String.format("%3d", stats_ones[loop][idx]));
			}
			System.out.print("| "+String.format("%2.1f", avg_energy[loop]));
			System.out.println("| "+String.format("%3.2f", ln_partition_function[loop]));
		}
	}		
}


