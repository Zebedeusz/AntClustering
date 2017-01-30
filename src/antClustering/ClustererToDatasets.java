package antClustering;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;

public class ClustererToDatasets 
{
	private List<String[]> dataToCluster;
	private List<ArrayList<String[]>> dataMatrix;
	
	private String[] blankExample;
	private String fileLocation = "C:/Users/Micha³/git/AntClustering/results/";
	
	public String fileName;
	public String[] classValues;
	
	public void setData(List<String[]> data)
	{
		this.dataToCluster = data;
		this.dataMatrix = new ArrayList<ArrayList<String[]>>();
		List<String[]> tempData = new ArrayList<String[]>(data);
		
		int rowsAndColumnsInDataMatrixQnt = (int) Math.ceil(Math.sqrt((double) (dataToCluster.size()+(dataToCluster.size() * 9))));
		
		for(int i = 0; i < rowsAndColumnsInDataMatrixQnt; i++)
		{
			dataMatrix.add(new ArrayList<String[]>());
		}
			
		//adding data from dataset to the matrix
		while(!tempData.isEmpty())
		{
			int randomExampleIndex = (int) (Math.random()*tempData.size());
			int randomRow;
			
			do
			{
				randomRow = (int) (Math.random()*dataMatrix.size());
			}
			while(dataMatrix.get(randomRow).size() == rowsAndColumnsInDataMatrixQnt);
				
			dataMatrix.get(randomRow).add(tempData.get(randomExampleIndex));
			tempData.remove(randomExampleIndex);
		}
		
		//adding empty string to the matrix
		blankExample = new String[dataToCluster.get(0).length];
		for(int i = 0; i < blankExample.length; i++)
			blankExample[i] = "0";
		
		for(int i = 0; i < rowsAndColumnsInDataMatrixQnt; i++)
			while(dataMatrix.get(i).size() != rowsAndColumnsInDataMatrixQnt)
				dataMatrix.get(i).add(blankExample);
		
		//shuffling data in every row
		for(int i = 0; i < rowsAndColumnsInDataMatrixQnt; i++)
			for(int j = 0; j < 100; j++)
			{
				int randomExampleIndex1 = (int) (Math.random()*dataMatrix.size());
				int randomExampleIndex2 = (int) (Math.random()*dataMatrix.size());
				
				String[] tempExample1 = dataMatrix.get(i).get(randomExampleIndex1);
				dataMatrix.get(i).set(randomExampleIndex1, dataMatrix.get(i).get(randomExampleIndex2));
				dataMatrix.get(i).set(randomExampleIndex2, tempExample1);
			}
	}
	
	public void saveClusteredImage()
	{
		final int GREEN = (0<<16) | (255<<8) | 0;
		final int BLUE = (0<<16) | (0<<8) | 255;
		final int RED = (255<<16) | (0<<8) | 0;
		final int WHITE = (255<<16) | (255<<8) | 255;
		
    	File file = new File(fileLocation + fileName);
    	BufferedImage image = new BufferedImage(dataMatrix.size(), dataMatrix.size(), BufferedImage.TYPE_INT_RGB);
    	
    	int[] colours = {GREEN, BLUE, RED};
    	HashMap<String, Integer> classesAndColors = new HashMap<>();
    	
    	int k = 0;
    	for(String classValue : classValues)
    	{
    		classesAndColors.put(classValue, colours[k]);
    		k++;
    	}
    	classesAndColors.put("0", WHITE);
    	
    	for(int i = 0; i < dataMatrix.size(); i++)
    		for(int j = 0; j < dataMatrix.size(); j++)
    		{
    			String clusteredClass = dataMatrix.get(i).get(j)[dataMatrix.get(i).get(j).length - 1];

    			image.setRGB(i, j, classesAndColors.get(clusteredClass));
    		}
    	
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void clusterData(final int NUMBER_OF_ANTS, final int NUMBER_OF_ITERATIONS, final int REGION_SIZE, final double ALPHA, final double SIGMOID_CONST)
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
		Ant[] ants = new Ant[NUMBER_OF_ANTS];
		
		for(int i = 0; i < NUMBER_OF_ANTS; i++)
		{
			ants[i] = new Ant(REGION_SIZE, ALPHA, SIGMOID_CONST);
			ants[i].selectRandomObject();
		}
		
		int tempProgress = 0;
		for(int j = 0; j < NUMBER_OF_ITERATIONS; j++)
		{
			for(int agentIndex = 0; agentIndex < NUMBER_OF_ANTS; agentIndex++)
			{
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

			int progress = (int) (((double) j / (double) NUMBER_OF_ITERATIONS)*100);
			if(progress != tempProgress)
			{
				System.out.println(progress + "%");
				saveClusteredImage();
				tempProgress = progress;
			}
			
		}
		
		for(Ant agent : ants)
		{
			while(!agent.carriedObject.equals(blankExample))
			{
				agent.setLocationAsRandomEmpty();
				
				if(agent.shouldDrop())
					agent.dropObject();
			}
		}
		
	}
	
	class Ant
	{
		int positionX;
		int positionY;
		String[] carriedObject;
		final int WIDTH = dataMatrix.size(), HEIGHT = dataMatrix.size();
		final int REGION_SIZE;
		final double ALPHA, SIGMOID_CONST; 
		
		Ant(int regionSize, double alpha, double sigmoidConst)
		{
			this.REGION_SIZE = regionSize;
			this.ALPHA = alpha;
			this.SIGMOID_CONST = sigmoidConst;
		}
		
		void pickObject()
		{
			this.carriedObject = dataMatrix.get(positionX).get(positionY);
			dataMatrix.get(positionX).set(positionY, blankExample);
		}
		
		void dropObject()
		{
			dataMatrix.get(positionX).set(positionY, carriedObject);
			this.carriedObject = blankExample;
		}
		
		boolean shouldPick()
		{
			boolean shouldPick = false;
			
			double objectNeighbourhoodGrade = calculateObjectFunction();
			
			//System.out.println("pick_P: " + objectNeighbourhoodGrade);
			double p = 1 - sigmoid(objectNeighbourhoodGrade);
			//if(p*(Math.random()*10+1) > p*(10-(10*p)))
			if(p > Math.random())
				shouldPick = true;
			else
				dropObject();

			return shouldPick;
		}
		
		boolean shouldDrop()
		{
			boolean shouldDrop = false;
			
			double objectNeighbourhoodGrade = calculateObjectFunction();

			//System.out.println("drop_P: " + objectNeighbourhoodGrade);
			double p = sigmoid(objectNeighbourhoodGrade);
			//if(p*(Math.random()*10+1) > p*(10-(10*p)))
			if(p > 0)
				shouldDrop = true;
			
			return shouldDrop;
		}
		
		double sigmoid(double x)
		{
			double result = (1 - Math.exp(-SIGMOID_CONST*x))/(1 + Math.exp(-SIGMOID_CONST*x));
			//System.out.println(result);
			return result;
		}
		
		double calculateObjectFunction()
		{
			double tempSum = 0;
			String[] tempObject;
			List<String> tempObjectClasses = new ArrayList<String>();
			
			int initialHeightIndex = positionY - REGION_SIZE;
			int finalHeightIndex = positionY + REGION_SIZE;
			int initialWidthIndex = positionX - REGION_SIZE;
			int finalWidthtIndex = positionX + REGION_SIZE;
			
			while (initialHeightIndex < 0)
				initialHeightIndex ++;
			while(finalHeightIndex >= HEIGHT)
				finalHeightIndex--;
			while(initialWidthIndex < 0)
				initialWidthIndex --;
			while(finalWidthtIndex >= WIDTH)
				finalWidthtIndex--;

			for(int heightIndex = initialHeightIndex; heightIndex <= finalHeightIndex; heightIndex++)
			{
				for(int  widthIndex = initialWidthIndex; widthIndex <= finalWidthtIndex; widthIndex++)
				{
					if(heightIndex == positionY && widthIndex == positionX)
						continue;
					tempObject = dataMatrix.get(widthIndex).get(heightIndex);
					
					if(!tempObject.equals(blankExample))
					{
						tempObjectClasses.add(tempObject[tempObject.length - 1]);
						
						double tempVar = 1 - (distanceCosine(carriedObject, tempObject)/ALPHA);
						
						tempSum += tempVar;
					}
				}
			}
			
			if(tempSum != 0)
				tempSum /= Math.pow((REGION_SIZE*2+1),2);
			
			String out = "V: " + tempSum + ", for: " + carriedObject[carriedObject.length - 1];
			for(String s : tempObjectClasses)
				out += ", " + s;
			
			//System.out.println(out);
			
			if(tempSum > 0)
				return tempSum;
			else 
				return 0;
			
		}
		
		double distanceEuclidean(String[] objectA, String[] objectB)
		{
			double distance = 0;
			
			for(int i = 0; i < objectA.length - 1; i++)
				distance += Math.pow((Double.parseDouble(objectA[i]) - Double.parseDouble(objectB[i])),2);
			
			return Math.sqrt(distance);
		}
		
		double distanceCosine(String[] objectA, String[] objectB)
		{
			double sumOfMultipliedComponents = 0;
			double sumOfPow2ComponentsInA = 0;
			double sumOfPow2ComponentsInB = 0;
			
			for(int i = 0; i < objectA.length - 1; i++)
			{
				sumOfMultipliedComponents += (Double.parseDouble(objectA[i]) * Double.parseDouble(objectB[i]));
				sumOfPow2ComponentsInA += Math.pow(Double.parseDouble(objectA[i]), 2);
				sumOfPow2ComponentsInB += Math.pow(Double.parseDouble(objectB[i]), 2);
			}
			
			sumOfPow2ComponentsInA = Math.sqrt(sumOfPow2ComponentsInA);
			sumOfPow2ComponentsInB = Math.sqrt(sumOfPow2ComponentsInB);
			
			double distance = Math.abs(((sumOfMultipliedComponents/(sumOfPow2ComponentsInA*sumOfPow2ComponentsInB)) - 1)/2);
					
			return distance;
		}
		
		void selectRandomObject()
		{
			do
			{
				this.positionX = (int) (Math.round(Math.random()*(WIDTH - 1))); 
				this.positionY = (int) (Math.round(Math.random()*(HEIGHT - 1))); 
				
			}while (dataMatrix.get(positionX).get(positionY).equals(blankExample));
			
			pickObject();
		}
		
		void setLocationAsRandomEmpty()
		{
			do
			{
				this.positionX = (int) (Math.round(Math.random()*(WIDTH - 1))); 
				this.positionY = (int) (Math.round(Math.random()*(HEIGHT - 1)));  
				
			}while (!dataMatrix.get(positionX).get(positionY).equals(blankExample));
		}
	}
}

