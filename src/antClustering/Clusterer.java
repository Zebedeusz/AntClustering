package antClustering;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Clusterer 
{
	private BufferedImage image;
	private File file;
	
	private int width;
	private int height;
	
	private String fileLocation = "C:/Users/Micha³/git/AntClustering/pic_1_clustered.png";

	private Ant[] ants = new Ant[10];
	
	public void setImage(BufferedImage img)
	{
		this.image = img;
		this.width = img.getWidth() - 1;
		this.height = img.getHeight() - 1;
	}
	
	public void saveClusteredImage()
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
	
	public void clusterPixelsOnImage()
	{
		/*IMPLEMENTATION TIPS
		 * 
		 * iterations: sqrt(20k*N) > 1M
		 * free space: 90% - done
		 * 1/sigma^2 changed to 1/N_occ, N_occ - number of occupied cells in local neighbourhood
		 * alpha - percentage of objects on the grid classified as similar (try with 0.03)
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
		
		for(int i = 0; i < 10; i++)
		{
			ants[i] = new Ant();
			ants[i].selectRandomObject();
			ants[i].setLocationAsRandomEmpty();
		}
		
		for(int j = 0; j < 100000000; j++)
		{
			for(int i = 0; i < 1; i++)
			{
			int agentIndex = (int) Math.random()*10;
			ants[agentIndex].setLocationAsRandomEmpty();
			
			if(ants[agentIndex].shouldDrop())
			{
				ants[agentIndex].dropObject();
				
				do
				{
					ants[agentIndex].selectRandomObject();
				}while(!ants[agentIndex].shouldPick());
			}
		}
			if(j%100000000 == 0)
				System.out.println(j/100000000);
		}
		
	}
	
	class Ant
	{
		//final int WHITE = (255<<16) | (255<<8) | 255;
		final int WHITE = -1;
		int positionX;
		int positionY;
		int carriedObject;
		
		void pickObject()
		{
			this.carriedObject = image.getRGB(positionX, positionY);
			image.setRGB(positionX, positionY, WHITE);
		}
		
		void dropObject()
		{
			image.setRGB(positionX, positionY, carriedObject);
			this.carriedObject = WHITE;
		}
		
		boolean shouldPick()
		{
			double objectNeighbourhoodGrade = calculateObjectFunction();
			
			if(objectNeighbourhoodGrade > 1)
				return true;
			else
			{
				 return 1/Math.pow(objectNeighbourhoodGrade, 2) >= 1 ? true : false;
			}
				 
		}
		
		boolean shouldDrop()
		{
			double objectNeighbourhoodGrade = calculateObjectFunction();
			//TODO
			//System.out.println(objectNeighbourhoodGrade);
			if(objectNeighbourhoodGrade >= 1)
				return true;
			else if	(objectNeighbourhoodGrade == 0)
				return false;
			else
			{
				 return 1/Math.pow(objectNeighbourhoodGrade, 4) >= 1 ? true : false;
			}
		}
		
		double calculateObjectFunction()
		{
			final double alpha = 0.003;
			int occupiedNeighbouringCells = 0;
			double tempSum = 0;
			int tempObject;
			
			int initialHeightIndex = positionY - 1;
			int finalHeightIndex = positionY + 1;
			int initialWidthIndex = positionX - 1;
			int finalWidthtIndex = positionX + 1;
			
			if(initialHeightIndex == -1)
				initialHeightIndex = 0;
			if(finalHeightIndex == height + 1)
				finalHeightIndex = height;
			if(initialWidthIndex == -1)
				initialWidthIndex = 0;
			if(finalWidthtIndex == width + 1)
				finalWidthtIndex = width;
			

			try
			{
				for(int heightIndex = initialHeightIndex; heightIndex <= finalHeightIndex; heightIndex++)
				{
					for(int  widthIndex = initialWidthIndex; widthIndex <= finalWidthtIndex; widthIndex++)
					{
						if(heightIndex == positionY && widthIndex == positionX)
							continue;
						tempObject = image.getRGB(widthIndex, heightIndex);
						
						if(tempObject != WHITE)
						{
							occupiedNeighbouringCells++;
							
							double tempVar = 1 - (dissimilarity(carriedObject, tempObject)/alpha);
							
							if(tempVar <= 0)
								return 0;
							
							tempSum += tempVar;
						}
					}
					
					tempSum /= occupiedNeighbouringCells;
				}
			}
			
			catch(IndexOutOfBoundsException e)
			{
				System.out.println("initialHeightIndex:" + initialHeightIndex);
				System.out.println("finalHeightIndex:" + finalHeightIndex);
				System.out.println("initialWidthIndex:" + initialWidthIndex);
				System.out.println("finalWidthtIndex:" + finalWidthtIndex);
				e.printStackTrace();
			}
			
			if(tempSum > 0)
				return tempSum;
			else 
				return 0;
		}
		
		int dissimilarity(int objectA, int objectB)
		{
			if(objectA == objectB)
				return 0;
			else 
				return 1;
		}
		
		void selectRandomObject()
		{
			do
			{
				this.positionX = (int) (Math.random()*width); 
				this.positionY = (int) (Math.random()*height); 
			}while (image.getRGB(positionX, positionY) == WHITE);
			
			pickObject();
		}
		
		void setLocationAsRandomEmpty()
		{
			do
			{
				this.positionX = (int) (Math.random()*width); 
				this.positionY = (int) (Math.random()*height); 

			}while (image.getRGB(positionX, positionY) != WHITE);
		}
	}
}
