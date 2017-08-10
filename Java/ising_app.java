// ising_app.java
// applet for small size ising model

import java.applet.Applet;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
//import java.util.*; // for Hashtable
//import java.net.*; // for URL

public class ising_app extends Applet implements Runnable{
	// panel data
	int[] size_panel;
	int min_size_panel;
	int[] panel_offset;

	// ising (display) data
	int ising_interval;
	int[] ising_offset;
	int rad_circle;
	int[] placement;
	int[][] ising_state;
	
	
	int L;
	int size_ising;
	int num_siml;
	int loop_siml;
	double normed_temperture_center;
	double normed_temperture;
	double theta;
	ising ising_hd;

	Thread th = null;
	
	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *                                                                                           *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	public void init() {
		L = 3;

		size_panel = new int[2];
		size_panel[0] = getSize().width;
		size_panel[1] = (int)(getSize().height);

		panel_offset = new int[2];
		panel_offset[0] = 0;
		panel_offset[1] = 0;
		
		int min_size_panel = size_panel[0];
		if (min_size_panel > size_panel[1]) {
			min_size_panel = size_panel[1];
		}

		// set radius
		rad_circle = min_size_panel / (L - 1) / 5;
		
		// set size of a ising cell
		ising_interval = (min_size_panel - 2*rad_circle)/(L - 1);

		ising_offset = new int[2];
		ising_offset[0] = panel_offset[0] + (int)(size_panel[0]/2 - ising_interval*((float)(L-1)/2.0));
		ising_offset[1] = panel_offset[1] + (int)(size_panel[1]/2 - ising_interval*((float)(L-1)/2.0));
		/*
		System.err.println("panel_offset: "+panel_offset[0]+","+panel_offset[1]);
		System.err.println("size_panel: "+size_panel[0]+","+size_panel[1]);
		System.err.println("min_size_panel: "+min_size_panel);
		System.err.println("rad_circle: "+rad_circle);
		System.err.println("ising_interval: "+ising_interval);
		*/
		
		placement = new int[2];
		
		// setting for simulation
		size_ising = L*L;
		num_siml = 1000;
		loop_siml = 0;
		normed_temperture_center = 2.27;
		theta = 0.2;
		normed_temperture = 1/theta;
		
		ising_hd = new ising(L, normed_temperture, "temperture", size_panel[0], size_panel[1]);
		// Temp
		//ising_hd.set_panel_offset(panel_offset[0], panel_offset[1]);
		
		th = new Thread(this);
		th.start();
	}

	public void update(Graphics g) {
		paint(g);
	}
	
	public void paint(Graphics g) {
		if (loop_siml == 0) {
			System.err.println("paint frame");
			paint_frame(g);
		}
		//g.setColor(Color.green);
		//int margin = 20;
		//g.drawRect(0, 0, 300, 300);
		//g.drawRect(margin, margin, size_panel[0]-margin, size_panel[1]-margin);
		//g.fillRect(0, 0, size_panel[0], size_panel[1]);
		//for (int loop=0; loop<1000; loop++) {
		//	ising_hd.iteration();
		//}
		
		paint_cell(g);
	}
	

	public void run() {
		for (loop_siml=0; loop_siml<num_siml; loop_siml++) {
			ising_hd.iteration();
			ising_state = ising_hd.get_state();
			repaint();
			System.err.println(loop_siml);
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}
	
	 public void paint_frame(Graphics gg) {
		gg.setColor(Color.white);
		gg.fillRect(panel_offset[0], panel_offset[1], panel_offset[0]+size_panel[0], panel_offset[1]+size_panel[1]);

		int[] from_pt = new int[2];
		int[] to_pt = new int[2];
		
		gg.setColor(Color.black);
		// vertical lines
		//System.err.println("ising_offset: "+ising_offset[0]+","+ising_offset[1]);
		//System.err.println("ising_interval: "+ising_interval);

		for (int loop_x = 0; loop_x < L; loop_x++) {
			from_pt[0] = ising_offset[0] + ising_interval * loop_x;
			from_pt[1] = ising_offset[1];
			to_pt[0] = ising_offset[0] + ising_interval * loop_x;
			to_pt[1] = ising_offset[1] + ising_interval * (L-1);
			//System.err.println("from, to: "+from_pt[0]+","+from_pt[1]+"=>"+to_pt[0]+","+to_pt[1]);
			gg.drawLine(from_pt[0], from_pt[1], to_pt[0], to_pt[1]);
		}
		
		// horizontal lines
		for (int loop_y = 0; loop_y < L; loop_y++) {
			from_pt[0] = ising_offset[0];
			from_pt[1] = ising_offset[1] + ising_interval * loop_y;
			to_pt[0] = ising_offset[0] + ising_interval * (L-1);
			to_pt[1] = ising_offset[1] + ising_interval * loop_y;
			gg.drawLine(from_pt[0], from_pt[1], to_pt[0], to_pt[1]);
		}
	}
	
	public void paint_cell(Graphics gg) {
		for (int loop_x = 0; loop_x < L; loop_x++) {
			for (int loop_y = 0; loop_y < L; loop_y++) {
				placement[0] = ising_offset[0] + ising_interval * loop_x - rad_circle;
				placement[1] = ising_offset[1] + ising_interval * loop_y - rad_circle;

				if (ising_state[loop_x+1][loop_y+1] == 1) {
					gg.setColor(Color.red);
				} else {
					gg.setColor(Color.blue);
				}
				gg.fillOval(placement[0], placement[1], rad_circle*2, rad_circle*2);
			}
		}
	}
	
}


