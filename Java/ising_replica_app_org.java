// ising_replica_app.java
// applet for small size ising model

import java.applet.Applet;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
//import java.util.*; // for Hashtable
//import java.net.*; // for URL

public class ising_replica_app extends Applet implements Runnable{
//public class ising_app extends Applet {
	// panel data
	int[] size_panel;
	int min_size_panel;
	// window data
	int[] size_window;
	int[][] window_offset;
	int margin_window;

	// ising (display) data
	int ising_interval;
	int[] ising_offset;
	int rad_circle;
	int[] placement;
	
	
	int L;
	int size_ising;
	int num_siml;
	int loop_siml;
	double normed_temperture_center;
	int[][] temp_state;

	int num_parms;
	ising[] ising_hd;
	double[] theta;
	double[] normed_temperture;
	
	int[][] stats_ones;

	Thread th = null;
	
	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *                                                                                           *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	public void init() {
		num_parms = 3;

		size_panel = new int[2];
		size_panel[0] = getSize().width;
		size_panel[1] = getSize().height;
		//size_panel[1] = (int)(getSize().height/2);

		// window data
		margin_window = 10;
		size_window = new int[2];
		size_window[0] = size_panel[0];
		size_window[1] = (size_panel[1] - (num_parms-1)*margin_window)/num_parms;
		
		// Temp
		window_offset = new int[num_parms][2];
		for (int loop=0; loop<num_parms; loop++) {
			window_offset[loop] = new int[2];
			window_offset[loop][0] = 0;
			window_offset[loop][1] = loop*(size_window[1]+margin_window);
		}
			
		// setting for simulation
		L = 3;
		size_ising = L*L;
		num_siml = 10000;
		loop_siml = 0;
		normed_temperture_center = 2.27;

		theta = new double[num_parms];
		normed_temperture = new double[num_parms];
		theta[0] = 0.4;
		theta[1] = 1.0;
		theta[2] = 1.6;
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
		
		ising_hd = new ising[3];
		for (int loop=0; loop<num_parms; loop++) {
			ising_hd[loop] = new ising(L, normed_temperture[loop], "temperture", size_window[0], size_window[1]);
			ising_hd[loop].set_panel_offset(window_offset[loop][0], window_offset[loop][1]);
			stats_ones[loop][ising_hd[loop].get_num_ones()]++;
		}
		
		// for exchanging states
		int[][] dummy = new int[L+2][L+2];
		temp_state = dummy;

		th = new Thread(this);
		th.start();
	}

	public void update(Graphics g) {
		paint(g);
	}
	
	public void paint(Graphics g) {
		if (loop_siml == 0) {
			for (int loop=0; loop<num_parms; loop++) {
				ising_hd[loop].paint_frame(g);
				String label = "theta: " + Double.toString(theta[loop]);
				g.drawString(label, window_offset[loop][0]+10, window_offset[loop][1]+30);
				if (loop>0) {
					g.setColor(Color.black);
					g.drawLine(0, window_offset[loop][1]-margin_window/2, size_panel[0], window_offset[loop][1]-margin_window/2);
				}
			}
		}
		//g.setColor(Color.green);
		//int margin = 20;
		//g.drawRect(0, 0, 300, 300);
		//g.drawRect(margin, margin, size_panel[0]-margin, size_panel[1]-margin);
		//g.fillRect(0, 0, size_panel[0], size_panel[1]);
		//for (int loop=0; loop<1000; loop++) {
		//	ising_hd.iteration();
		//}
		
		for (int loop=0; loop<num_parms; loop++) {
			ising_hd[loop].paint(g);
		}
	}
	

	public void run() {
		for (loop_siml=0; loop_siml<num_siml; loop_siml++) {
			for (int loop=0; loop<num_parms; loop++) {
				ising_hd[loop].iteration();
				exchange(1);
				int cnt = ising_hd[loop].get_num_ones();
				//System.err.println("loop, cnt: "+loop+", "+cnt);
				stats_ones[loop][cnt]++;
			}
			if (loop_siml == num_siml-1) {
				print_stats();
			}
			
			repaint();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public void exchange(int num) {
		for (int loop=0; loop<num; loop++) {
			int idx = (int)((num_parms-1) * Math.random());
			int energy_1 = ising_hd[idx].get_energy();
			int energy_2 = ising_hd[idx+1].get_energy();
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
	
	public void print_stats() {
		System.out.println("Stats:");
		for (int loop=0; loop<num_parms; loop++) {
			System.out.println("===== theta: "+theta[loop]+" ========");
			for (int idx = 0; idx<size_ising+1; idx++) {
				System.out.println(idx+"\t"+stats_ones[loop][idx]);
			}
		}
	}
}


