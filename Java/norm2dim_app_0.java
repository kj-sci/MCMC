// norm2dim_app.java
// applet for small size norm2dim model

import java.applet.Applet;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

/*
 * Using (traditional) Monte Carlo method,
 * sample from {y>=0.5*x^2+3}
 * pdf = A * exp(-(x^2-2*beta*x*y+y^2)/2)
 *
 */
public class norm2dim_app_0 extends Applet implements Runnable{
	// beta
	double beta;
	// step size
	double sigma;
		
	// panel data
	int[] size_panel;
	int[] panel_offset;
	int min_size_panel;
	int[] orig_pt;
	
	int[] pt1;
	int[] pt2;

	int radius_pt;

	double param_scale;
	double[] center;
	
	int num_siml;
	int loop_siml;
	norm2dim_montecarlo mcmc_hd;
	double[] init_state;
	double[] prev_state;
	double[] state;
	boolean ret;
	
	Thread th = null;
	
	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *                                                                                           *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	public void init() {
		size_panel = new int[2];
		size_panel[0] = getSize().width;
		size_panel[1] = (int)(getSize().height);

		panel_offset = new int[2];
		panel_offset[0] = 0;
		panel_offset[1] = 0;

		// origin (pixel pt)
		orig_pt = new int[2];
		for (int loop=0; loop<2; loop++) {
			orig_pt[loop] = panel_offset[loop] + (size_panel[loop]-2*panel_offset[loop])/2;
		}
	
		// 1 unit (range in xy plane)  = 100 pixels
		param_scale = 50;

		// center of the xy plain
		center = new double[2];
		center[0] = 0.0;
		center[1] = 0.0;
		
		// for drawing arrows
		pt1 = new int[2];
		pt2 = new int[2];

		// radius of the pts
		radius_pt = 2;
		
		// setting for simulation
		num_siml = 1000;
		loop_siml = 0;
		beta = 0.8;
		sigma = 0.5;
		
		mcmc_hd = new norm2dim_montecarlo(beta, sigma);

		init_state = new double[2];
		init_state[0] = 3.0;
		init_state[1] = 9.0;
		mcmc_hd.set_state(init_state);
		System.err.println("Init: ("+init_state[0]+","+init_state[1]+")");

		prev_state = new double[2];
		state = mcmc_hd.get_state();
		
		th = new Thread(this);
		th.start();
	}

	public void update(Graphics g) {
		paint(g);
	}
	
	public void paint(Graphics g) {
		if (loop_siml == 0) {
			paint_frame(g);
			draw_def_area(g);
		}
		
		//draw_arrow(g, prev_state, state);
		draw_pt(g, state);
	}

	public void run() {
		for (loop_siml=0; loop_siml<num_siml; loop_siml++) {
			for (int loop=0; loop<2; loop++) {
				prev_state[loop] = state[loop];
			}

			ret = mcmc_hd.iteration();
			state = mcmc_hd.get_state();
			System.err.println(loop_siml+": ("+state[0]+","+state[1]+"): "+ret);
			
			repaint();
			sleep(100);
		}
	}

	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *                  Draw Functions                                                           *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	public void paint_frame(Graphics gg) {
		gg.setColor(Color.black);
		
		gg.drawLine(orig_pt[0], panel_offset[1], orig_pt[0], size_panel[1]-panel_offset[1]);
		gg.drawLine(panel_offset[0], orig_pt[1], size_panel[0]-panel_offset[0], orig_pt[1]);
	}

	public void draw_pt (Graphics gg, double[] pt) {
		transf_xy2pt(pt, pt1);
		
		if (ret) {
			gg.setColor(Color.red);
		} else {
			gg.setColor(Color.green);
		}
			
		gg.fillOval(pt1[0]-radius_pt, pt1[1]-radius_pt, 2*radius_pt, 2*radius_pt);
	}
	
	public void draw_arrow(Graphics gg, double[] from, double[] to) {
		transf_xy2pt(from, pt1);
		transf_xy2pt(to, pt2);

		double theta = 2*Math.PI*30.0/180.0;
		double[] dir = new double[2];
		double[] arrow_dir = new double[2];
		for (int loop=0; loop<2; loop++) {
			dir[loop] = (pt1[loop] - pt2[loop])/10.0;
		}
		
		gg.setColor(Color.black);
		//System.err.println("from=>to: "+pt1[0]+","+pt1[1]+"=>"+pt2[0]+","+pt2[1]);
		gg.drawLine(pt1[0], pt1[1], pt2[0], pt2[1]);
		rotate(dir, arrow_dir, theta);
		gg.drawLine(pt2[0], pt2[1], pt2[0]+(int)arrow_dir[0], pt2[1]+(int)arrow_dir[1]);
		rotate(dir, arrow_dir, -theta);
		gg.drawLine(pt2[0], pt2[1], pt2[0]+(int)arrow_dir[0], pt2[1]+(int)arrow_dir[1]);
	}

	public void draw_def_area(Graphics gg){
		int[] i_pt = new int[2];
		i_pt[0] = i_pt[1] = 0;
		int[] prev_i_pt = new int[2];
		
		double[] d_pt = new double[2];
		
		gg.setColor(Color.blue);
		for (int loop = panel_offset[0]; loop<size_panel[0]-panel_offset[0]; loop++) {
			for(int idx=0; idx<2; idx++) {
				prev_i_pt[idx] = i_pt[idx];
			}
			
			i_pt[0] = loop;
			transf_pt2xy(i_pt, d_pt);
			d_pt[1] = 0.5*d_pt[0]*d_pt[0] + 3.0;
			transf_xy2pt(d_pt, i_pt);
			if (loop == panel_offset[0]) {
				for(int idx=0; idx<2; idx++) {
					prev_i_pt[idx] = i_pt[idx];
				}
			}
			gg.drawLine(prev_i_pt[0], prev_i_pt[1], i_pt[0], i_pt[1]);
		}
	}
	
	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *                        Help Functions                                                     *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/
	
	// transformation from xy pt (d_pt) to pixel pt (i_pt)
	public void transf_xy2pt(double[] d_pt, int[] i_pt) {
		i_pt[0] = (int)(orig_pt[0] + (d_pt[0]-center[0])*param_scale);
		i_pt[1] = (int)(orig_pt[1] - (d_pt[1]-center[1])*param_scale);
	}

	public void transf_pt2xy(int[] i_pt, double[] d_pt) {
		d_pt[0] = (i_pt[0] - orig_pt[0])/param_scale + center[0];
		d_pt[1] = (orig_pt[1] - i_pt[1])/param_scale + center[1];
	}
	
	public void rotate(double[] vec_org, double[] vec_new, double theta) {
		vec_new[0] = Math.cos(theta)*vec_org[0] + Math.sin(theta)*vec_org[1];
		vec_new[1] = -Math.sin(theta)*vec_org[0] + Math.cos(theta)*vec_org[1];
	}
	
	public void sleep(int t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
		}
	}
	
}


