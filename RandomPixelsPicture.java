package antClustering;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class RandomPixelsPicture 

{
	private final int WIDTH = 200;
	private final int HEIGHT = 200;
	
	private BufferedImage image;

	private File file;
	
	private String fileLocation = "/home/michal/workspace/AntClustering/Pictures/pic_1";
	
	public RandomPixelsPicture() 
	{

	}
	
	public BufferedImage getImage() 
	{
		return image;
	}
	
	public void generatePicture()
	{
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		final int GREEN = (0<<16) | (255<<8) | 0;
		final int BLUE = (0<<16) | (0<<8) | 255;
		final int RED = (255<<16) | (0<<8) | 0;
		final int WHITE = (255<<16) | (255<<8) | 255;
		
		final int[] COLOURS = new int[30];
		
		COLOURS[0] = GREEN;
		COLOURS[1] = BLUE;
		COLOURS[2] = RED;
		
		for(int i = 3; i < COLOURS.length; i++)
			COLOURS[i] = WHITE;
		
		
	    for(int y = 0; y < HEIGHT; y++){
	         for(int x = 0; x < WIDTH; x++){
	           //int r = (int)(Math.random()*256); //red
	          // int g = (int)(Math.random()*256); //green
	           //int b = (int)(Math.random()*256); //blue
	        	 
	           int p = COLOURS[(int)(Math.random()*COLOURS.length)];
	   
	           image.setRGB(x, y, p);
	         }
	       }
	}
	
	public void savePicture()
	{   
	    try 
	    {
	    	file = new File(fileLocation);
			ImageIO.write(image, "png", file);
		} 
	    catch (IOException e) 
	    {
			e.printStackTrace();
		}
	}
	
}
