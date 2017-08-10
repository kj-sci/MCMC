// Usage: java ising2img size image_filename
class ising2img {
	public static void main(String[] args){
		if (args.length < 2) {
			System.err.println("Usage: java ising2img size outfname");
			System.exit(1);
		}
		
		int size = Integer.valueOf(args[0]).intValue();
		String outfname = args[1];
		
		System.out.println("size: "+size);
		System.out.println("fname: "+outfname);

		int panel_w = 300;
		int panel_h = 300;
		
		ising ising_hd = new ising(size);
		ising_hd.init(panel_w, panel_h);
		ising_hd.init_graphics();
		ising_hd.paint();
		ising_hd.write_image(outfname);

	}
}

		
		
		