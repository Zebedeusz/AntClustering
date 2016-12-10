package antClustering;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Clusterer 
{
	private BufferedImage imageIn;
	private BufferedImage imageOut;
	private File file;
	
	private int width;
	private int height;
	
	private String fileLocation = "/home/michal/workspace/AntClustering/Pictures/pic_1_clustered";

	public void setImage(BufferedImage img)
	{
		this.imageIn = img;
		this.width = img.getWidth();
		this.height = img.getHeight();
	}
	
	public void saveClusteredImage()
	{
	    try 
	    {
	    	file = new File(fileLocation);
			ImageIO.write(imageOut, "png", file);
		} 
	    catch (IOException e) 
	    {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	

	public void clusterPixelsOnImage()
	{
		/*ALGORITHM
		 * 
		 * randomly scatter o_i object on the grid file - done during image generation
		 * 
		 * for each agent a_j (10 agents)
		 * 	randomly select object o_i (different than white) -> separate method
		 * 	pick up the object (change pixel value to white, remember pixel carried by agent)
		 * 	place agent a_j at randomly selected empty place on grid (empty == white)
		 * 
		 * for t=1 to t_max (how many iterations -> N - number of items on the grid (currently 4k), t_start = 0.45N, t_end = 0.55N)
		 * 	randomly select agent a_j
		 * 	move the agent to new location
		 * 	i = object carried by the agent
		 * 	calculate f*(o_i) and p*_drop(o_i)
		 * 	if (drop == true)
		 * 		while (pick == false)
		 * 			i = randomly selected object o_i
		 * 			calculate f*(o_i) and p*pick(o_i)
		 * 			pick up object i
		 */
		
		/*IMPLEMENTATION TIPS
		 * 
		 * iterations: sqrt(20k*N) > 1M
		 * free space: 90% - done
		 * 1/sigma^2 changed to 1/N_occ, N_occ - number of occupied cells in local neighbourhood
		 * alpha - percentage of objects on the grid classified as similar
		 * d(i,j) - dissimilarity - if object are identical: 0 , if not: 1
		 * 
		 * AGENT
		 * 	quantity of agents: 10
		 * 	size of short-memory: 10
		 * 	
		 * NEIGHBOURHOOD
		 * 	try with closest surrounding pixels - 1 
		 * 
		 * CALCULATING f and p
		 * 	calculate only p(i) drop and pick (in one method with flag) as check if drop/pick is true or false is solely performed
		 * 
		 * 
		 */
		
		
	}



}
