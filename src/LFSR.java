
public class LFSR {

	private String m_seed;
	private int m_tap;
	
	public LFSR(String seed, int tap)
	{
		m_seed = seed;
		m_tap = tap;
	}
	
	public int length()
	{
		return m_seed.length();
	}
	
	public int bitAt(int i)
	{
		return Integer.parseInt(""+m_seed.charAt(i));
	}
	
	public String toString()
	{
		return m_seed;
	}
	
	public int step()
	{
		int tapVal = bitAt(m_seed.length() - m_tap);
		int othVal = bitAt(0);
		
		int newVal = (tapVal != othVal) ? 1 : 0;
		m_seed = m_seed.substring(1) + newVal;
		return newVal;
	}
	
	public int generate(int k)
	{
		String s = "";
		for (int i = 0; i < k; i++)
		{
			s += "" + step();
		}
		return Integer.parseInt(s, 2);
	}
	
	public String generateS(int k)
	{
		String s = "";
		for (int i = 0; i < k; i++)
		{
			s += "" + step();
		}
		return s;
	}
	
	public static String encrypt(String plain, LFSR l, int k)
	{
		String res = "";
		for (int i = 0; i < plain.length(); i++)
			res += (char)(plain.charAt(i) + l.generate(k));
		return res;
	}
	
	public static String crack(String s, LFSR l, int k)
	{
		String res = "";
		for (int i = 0; i < s.length(); i++)
			res += (char)(s.charAt(i) - l.generate(k));
		return res;
	}
	
	public static void main(String[] args)
	{
		LFSR g = new LFSR("01101000010100010000", 17);
		
		//for (int i = 0; i < 10; i++)
		//{
		//	System.out.println(g.generateS(8));
		//}
		
		//System.out.println(encrypt("blades of sand", g, 4));
		System.out.println(crack("dvnmfz,pg*|mwp", g, 4));
	}
	
}
