package nf.fr.ephys.playerproxies.helpers;

import java.awt.Color;

public class Debug {
	public static void main(String[] args) {
		Color color = new Color(9474208);
		
		System.out.println(color.getRed());
		System.out.println(color.getGreen());
		System.out.println(color.getBlue());
		
		System.out.println("==============");
		
		System.out.println((byte) color.getRed());
		System.out.println((byte) color.getGreen());
		System.out.println((byte) color.getBlue());
	}
}