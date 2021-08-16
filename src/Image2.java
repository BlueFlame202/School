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

    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        int width  = picture.width();
        int height = picture.height();
        System.out.println("width: " + width);
        System.out.println("height: " + height);
        
        for (int i = 0; i < picture.width(); i++)
        {
        	for (int j = 0; j < picture.height(); j++)
        	{
        		picture.set(i, j, new Color((int)Math.min(picture.get(i, j).getBlue(),255), 
        				(int)Math.min(picture.get(i, j).getGreen()/3,255), 
        				(int)Math.min(picture.get(i, j).getRed()*4, 255)));
        	}
        }
        
        picture.show(); 
    }
}


/*Copyright 2000-2017, Robert Sedgewick and Kevin Wayne and now Aathreya Kadambi.*/