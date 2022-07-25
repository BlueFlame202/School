import java.awt.Color;

public class Image2 {

    /**
     * Returns the monochrome luminance of the given color as an intensity
     * between 0.0 and 255.0 using the NTSC formula
     * Y = 0.299*r + 0.587*g + 0.114*b. If the given color is a shade of gray
     * (r = g = b), this method is guaranteed to return the exact grayscale
     * value (an integer with no floating-point roundoff error).
     */
    public static double intensity(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        if (r == g && r == b) return r;   // to avoid floating-point issues
        return 0.299*r + 0.587*g + 0.114*b;
    }

    /**
     * Returns a grayscale version of the given color as a {@code Color} object.
     */
    public static Color toGray(Color color) {
        int y = (int) (Math.round(intensity(color)));   // round to nearest int
        Color gray = new Color(y, y, y);
        return gray;
    }
    
    public static Picture LFSRTransform(Picture original, LFSR t)
    {
    	Picture f = original;
    	for (int col = 0; col < original.width(); col++)
        {
    		for (int row = 0; row < original.height(); row++)
        	{
        		int r = original.get(col, row).getRed();
        		int g = original.get(col, row).getGreen();
        		int b = original.get(col, row).getBlue();
        		
        		String sr = Integer.toBinaryString(r);
        		String sg = Integer.toBinaryString(g);
        		String sb = Integer.toBinaryString(b);
        		String sx = t.generateS(8);
        		String result = "";
        		
        		while (sr.length() < 8)
        			sr = "0" + sr;
        		for (int i = 0; i < sr.length(); i++)
        		{
        			result += "" + ((sr.charAt(i) != sx.charAt(i)) ? 1 : 0);
        		}
        		r = Integer.parseInt(result, 2);
        		
        		sx = t.generateS(8);
        		result = "";
        		while (sg.length() < 8)
        			sg = "0" + sg;
        		for (int i = 0; i < sg.length(); i++)
        		{
        			result += "" + ((sg.charAt(i) != sx.charAt(i)) ? 1 : 0);
        		}
        		g = Integer.parseInt(result, 2);
        		
        		sx = t.generateS(8);
        		result = "";
        		while (sb.length() < 8)
        			sb = "0" + sb;
        		for (int i = 0; i < sb.length(); i++)
        		{
        			result += "" + ((sb.charAt(i) != sx.charAt(i)) ? 1 : 0);
        		}
        		b = Integer.parseInt(result, 2);
        		
        		f.set(col, row, new Color(r, g, b));
        	}
        }
    	return f;
    }
    
    public static Picture crack(Picture original, LFSR t)
    {
    	Picture res = original;
    	
    	int basisWidth = original.width();
    	int basisHeight = original.height();
    	
    			
    	double dev = 0;
    	int l = 0;
    			
	    for (int col = 0; col < basisWidth; col++)
	    {
	    	for (int row = 0; row < basisHeight; row++)
	        {
	            int r = original.get(col, row).getRed();
	            int g = original.get(col, row).getGreen();
	            int b = original.get(col, row).getBlue();
	            		
	            String sr = Integer.toBinaryString(r);
	            String sg = Integer.toBinaryString(g);
	            String sb = Integer.toBinaryString(b);
	            String sx = t.generateS(8);
	            String result = "";
	            		
	            while (sr.length() < 8)
	            	sr = "0" + sr;
	            for (int k = 0; k < sr.length(); k++)
	            {
	           		result += "" + ((sr.charAt(k) != sx.charAt(k)) ? 1 : 0);
	            }
	            r = Integer.parseInt(result, 2);
	            dev = ((dev * l) + (Math.abs(r - 128.0)))/(l+1);
	            l++;
	            		
	            sx = t.generateS(8);
	            result = "";
	            while (sg.length() < 8)
	            	sg = "0" + sg;
	            for (int k = 0; k < sg.length(); k++)
	            {
	            	result += "" + ((sg.charAt(k) != sx.charAt(k)) ? 1 : 0);
	            }
	            g = Integer.parseInt(result, 2);
	            dev = ((dev * l) + (Math.abs(g - 128.0)))/(l+1);
	            l++;
	            		
	            sx = t.generateS(8);
	            result = "";
	            while (sb.length() < 8)
	            	sb = "0" + sb;
	            for (int k = 0; k < sb.length(); k++)
	            {
	            	result += "" + ((sb.charAt(k) != sx.charAt(k)) ? 1 : 0);
	            }
	            b = Integer.parseInt(result, 2);
	            dev = ((dev * l) + (Math.abs(b - 128.0)))/(l+1);
	            l++;
	            		
	            original.set(col, row, new Color(r, g, b));
	        }
        }
	        	
    	return res;
    }
    
    public static Picture crack(Picture original, int length)
    {
    	double maxDev = 0;
    	Picture best = original;
    	
    	int basisWidth = original.width();
    	int basisHeight = original.height();
    	
    	String bestPassword = "0";
    	int bestTap = 1;
    	
    	for (int i = 0; i < Math.pow(2, length); i++)
    	{
    		String s = Integer.toBinaryString(i);
    		while (s.length() < length)
    			s = "0" + s;
    		System.out.print(s + " ");
    		for (int j = 1; j < length; j++)
    		{
    			LFSR t = new LFSR(s, j);
    			
    			Picture f = new Picture(original.width(), original.height());
    			double dev = 0;
    			int l = 0;
    			
	        	for (int col = 0; col < basisWidth; col++)
	            {
	        		for (int row = 0; row < basisHeight; row++)
	            	{
	            		int r = original.get(col, row).getRed();
	            		int g = original.get(col, row).getGreen();
	            		int b = original.get(col, row).getBlue();
	            		
	            		String sr = Integer.toBinaryString(r);
	            		String sg = Integer.toBinaryString(g);
	            		String sb = Integer.toBinaryString(b);
	            		String sx = t.generateS(8);
	            		String result = "";
	            		
	            		while (sr.length() < 8)
	            			sr = "0" + sr;
	            		for (int k = 0; k < sr.length(); k++)
	            		{
	            			result += "" + ((sr.charAt(k) != sx.charAt(k)) ? 1 : 0);
	            		}
	            		r = Integer.parseInt(result, 2);
	            		dev = ((dev * l) + (Math.abs(r - 128.0)))/(l+1);
	            		l++;
	            		
	            		sx = t.generateS(8);
	            		result = "";
	            		while (sg.length() < 8)
	            			sg = "0" + sg;
	            		for (int k = 0; k < sg.length(); k++)
	            		{
	            			result += "" + ((sg.charAt(k) != sx.charAt(k)) ? 1 : 0);
	            		}
	            		g = Integer.parseInt(result, 2);
	            		dev = ((dev * l) + (Math.abs(g - 128.0)))/(l+1);
	            		l++;
	            		
	            		sx = t.generateS(8);
	            		result = "";
	            		while (sb.length() < 8)
	            			sb = "0" + sb;
	            		for (int k = 0; k < sb.length(); k++)
	            		{
	            			result += "" + ((sb.charAt(k) != sx.charAt(k)) ? 1 : 0);
	            		}
	            		b = Integer.parseInt(result, 2);
	            		dev = ((dev * l) + (Math.abs(b - 128.0)))/(l+1);
	            		l++;
	            		
	            		f.set(col, row, new Color(r, g, b));
	            	}
            	}
	        	System.out.print(dev + "\n");
	        	
	        	if (dev > maxDev)
	        	{
	        		maxDev = dev;
	        		best = f;
	        		bestPassword = s;
	        		bestTap = j;
	        	}
    		}
    	}
    	System.out.println(bestPassword);
    	System.out.println(bestTap);
    	return best;
    }
    
    public static String translate(String original)
    {
    	String base64 = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz0123456789+/";
    	String result = "";
    	for (int i = 0; i < original.length(); i++)
    	{
    		int val = 0;
    		for (int j = 0; j < 64; j++)
    			if (base64.charAt(j) == original.charAt(i))
    				val = j;
    		String sval = Integer.toBinaryString(val);
    		result += sval;
    	}
    	return result;
    }
    
    public static String reverse(String in)
    {
    	String result = "";
    	for (int i = in.length() - 1; i >= 0; i--)
    	{
    		result += in.charAt(i);
    	}
    	return result;
    }

    public static Picture dBlend(Picture s1, Picture s2, boolean mneg) // rn assumes square image
    {
    	Picture res = new Picture(Math.min(s1.width(), s2.width()), Math.min(s1.height(), s2.height()));
    	
    	double w = res.width();
    	double h = res.height();
    	
    	for (int i = 0; i < w; i++)
    	{
    		for (int j = 0; j < h; j++)
    		{
    			int j2 = (int) (h - j - 1);
    			double r = Math.abs(h*i - w*j2)/(w*h);
    			double u = ((h*i)/w); // doing h - this gives interesting results
    			if (mneg) // flop the ! around and replace j2 with j and you can get interesting results with diagonal blends
    			{
    				u = h - u;
    				r = Math.abs(h*i + w*j2-w*h)/(w*h);
    			}
    			
    			// double r = 1 - 0.5*(1+(Math.sqrt(Math.pow(i-j,2))/w)); // SQUARES
    			// double r = (Math.sqrt(Math.pow(i-j,2))/res.width()); // SQUARES
    			if (j2 < u) // Before the j in j < (h*(w-i))/w was accidentally an i and that gave some p neat results actually
    				res.set(i, j, new Color((int)(r*s1.get(i, j).getRed() + (1-r)*s2.get(i, j).getRed())
    						, (int)(r*s1.get(i, j).getGreen() + (1-r)*s2.get(i, j).getGreen())
    						, (int)(r*s1.get(i, j).getBlue() + (1-r)*s2.get(i, j).getBlue())));
    			else
    				res.set(i, j, new Color((int)(r*s2.get(i, j).getRed() + (1-r)*s1.get(i, j).getRed())
    						, (int)(r*s2.get(i, j).getGreen() + (1-r)*s1.get(i, j).getGreen())
    						, (int)(r*s2.get(i, j).getBlue() + (1-r)*s1.get(i, j).getBlue())));
    		}
    	}
    	
    	return res;
    }
    
    public static Picture dBlend2(Picture s1, Picture s2, double k, double rbalance, double gbalance, double bbalance)
    {
    	Picture res = new Picture(Math.min(s1.width(), s2.width()), Math.min(s1.height(), s2.height()));
    	
    	double w = res.width();
    	double h = res.height();
    	
    	for (int i = 0; i < w; i++)
    	{
    		for (int j = 0; j < h; j++)
    		{
    			double a = i*i + j*j;
    			double b = (w-i)*(w-i) + (h-j)*(h-j);
    			
    			Math.pow(a, k);
    			Math.pow(b, k);
    			
    			// Normalize so that a and b add to one:
    			a = a/(a+b);
    			b = b/(a+b);
    			
    			res.set(i, j, new Color((int)(Math.min((a*(double)s1.get(i, j).getRed() + b*(double)s2.get(i, j).getRed())/rbalance,255))
						, (int)(Math.min((a*(double)s1.get(i, j).getGreen() + b*(double)s2.get(i, j).getGreen())/gbalance,255))
						, (int)(Math.min((a*(double)s1.get(i, j).getBlue() + b*(double)s2.get(i, j).getBlue())/bbalance,255))));
    		}
    	}
    	
    	return res;
    }
    
    public static Picture cBlend(Picture s1, Picture s2, double k, double rbalance, double gbalance, double bbalance)
    {
    	Picture res = new Picture(Math.min(s1.width(), s2.width()), Math.min(s1.height(), s2.height()));
    	
    	double w = res.width();
    	double h = res.height();
    	
    	for (int i = 0; i < w; i++)
    	{
    		for (int j = 0; j < h; j++)
    		{
    			double a = 2 * Math.abs(i - w/2)/w; // a \in [0,1]
    			
    			a = Math.pow(a, k);
    			double b = 1 - a;
    			
    			res.set(i, j, new Color((int)(Math.min((a*(double)s1.get(i, j).getRed() + b*(double)s2.get(i, j).getRed())/rbalance,255))
						, (int)(Math.min((a*(double)s1.get(i, j).getGreen() + b*(double)s2.get(i, j).getGreen())/gbalance,255))
						, (int)(Math.min((a*(double)s1.get(i, j).getBlue() + b*(double)s2.get(i, j).getBlue())/bbalance,255))));
    		}
    	}
    	
    	return res;
    }
    
    public static Picture avg(Picture[] pics)
    {
    	Picture res = new Picture(pics[0].width(), pics[0].height());
    	
    	double w = res.width();
    	double h = res.height();
    	
    	int rsum = 0;
    	int bsum = 0;
    	int gsum = 0;
    	for (int i = 0; i < w; i++)
    	{
    		for (int j = 0; j < h; j++)
    		{
    			
    	    	for (Picture pic : pics)
    	    	{
    	    		rsum += pic.get(i, j).getRed();
    	    		bsum += pic.get(i, j).getBlue();
    	    		gsum += pic.get(i, j).getGreen();
    	    	}
    	    	res.set(i, j, new Color((int)(rsum/pics.length), (int)(gsum/pics.length), (int)(bsum/pics.length)));
    	    	rsum = 0; bsum = 0; gsum = 0;
    		}
    	}
    	
    	return res;
    }
    
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        Picture sample = new Picture(args[1]);
    	
        
        // cblend (blend along x = width/2) // in the future make a blend across any lambda function
    	double w = picture.width();
    	double h = picture.height();
    	
    	cBlend(sample, picture, 10, 1, 1, 1).show();
    	
    	
    	//Picture[] pics = new Picture[args.length];
    	//for (int i = 0; i < args.length; i++)
    	//	pics[i] = new Picture(args[i]);
    	
    	//avg(pics).show();
    	
        
        //for (int i = 0; i < picture.width(); i++)
        //{
        //	for (int j = 0; j < picture.height(); j++)
        //	{
        //		picture.set(i, j, new Color((int)(255*Math.random()), (int)(255 * Math.random()), Math.min(picture.get(i, j).getBlue()/*/4*/ + (int)(30*Math.random()), 255)));
        //		picture.set(i, j, new Color(picture.get(i, j).getRed(), (int)(0.8 * picture.get(i, j).getGreen() + 0.2 * picture.get(i, j).getBlue()/2), picture.get(i, j).getBlue()));
        //	}
        //}
        //picture.show();
        
        //Picture pic = new Picture(args[0]);
        //Picture res = crack(pic, new LFSR("01101000010100010000", 17));
        //res.show();
        //crack(pic, 8).show();
    }
}


/*Copyright 2000-2017, Robert Sedgewick and Kevin Wayne and now Aathreya Kadambi.*/