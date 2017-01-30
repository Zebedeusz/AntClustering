package antClustering;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataAcquisitor 
{
	private final String dataFileLocation = "C:/Users/Micha³/git/AntClustering/data/";
	//info about data from user
	private int attributesQuantity;

	private int classesQuantity;
	private String dataFileName;
	private String[] classValues;
	
	public int getClassesQuantity() {
		return classesQuantity;
	}

	public String[] getClassValues() {
		return classValues;
	}
	public int getAttributesQuantity() {
		return attributesQuantity;
	}
	
	public String getDataFileLocation() {
		return dataFileLocation;
	}

	public String getDataFileName() {
		return dataFileName;
	}

	//variables used for data manipulation
    private String[] example;
    private List <List<String[]>> classes;
    private List <List<List<String[]>>> dividedData;
    private List<List<String[]>> trainingData;
    private List<String[]> testData;
    
    private DataAcquisitor(){};
    
    private static final DataAcquisitor instance = new DataAcquisitor();
    
    public static DataAcquisitor getInstance()
    {
    	return instance;
    }
    
    public void initialise(String dataFileName) 
    {
    	this.dataFileName = dataFileName;

        this.trainingData = new ArrayList<>();
        this.testData = new ArrayList<>();
    	this.example = new String[attributesQuantity+1];
    	this.dividedData = new ArrayList<>();
    	this.classes = new ArrayList<>();
	}
    
    private void inspectData() throws IOException
    {
    	BufferedReader dataFileReader = new BufferedReader(new FileReader(dataFileLocation + dataFileName));
    	
    	String[] tempClassValues = new String[30];
    	this.attributesQuantity = dataFileReader.readLine().split(",|\\t").length-1;
    	tempClassValues[0] = dataFileReader.readLine().split(",|\\t")[attributesQuantity];
    	this.classesQuantity = 1;
    	
    	
    	String classValue;
    	while (dataFileReader.ready())
    	{
    		classValue = dataFileReader.readLine().split(",|\\t")[attributesQuantity];
    		
    		boolean notFound = true;
    		for(int i = 0; i < classesQuantity; i++)
    		{
	    		if(tempClassValues[i].equals(classValue))
	    		{
	    			notFound = false;
	    			break;
	    		}		
    		}
    		
    		if(notFound)
    		{
    			tempClassValues[classesQuantity] = classValue;
    			this.classesQuantity++;
    		}
    	}
    	
    	int tempClassesSize = 0;
    	for(int i = 0; i < tempClassValues.length; i++)
    		if(tempClassValues[i] != null)
    			tempClassesSize++;
    	
    	this.classValues = new String[tempClassesSize];

    	for(int i = 0; i < tempClassesSize; i++)
    		this.classValues[i] = tempClassValues[i];
    		
    	tempClassValues = null;
    	dataFileReader.close();
    }
    
    public boolean getDataFromFile()
    {
    	
    	BufferedReader dataFileReader = null;
    	
    	try
    	{
    		inspectData();
    		
    		dataFileReader = new BufferedReader(new FileReader(dataFileLocation + dataFileName));
    		
        	if (!classes.isEmpty())
        		classes.clear();
        	
        	for (int i = 0; i < classesQuantity; i++)
        		classes.add(new ArrayList<String[]>());
        	
        	while (dataFileReader.ready())
        	{
        		example = dataFileReader.readLine().split(",|\\t");

        		for (int i = 0; i < classesQuantity; i++)
        		{
        			if (classValues[i].equals(example[attributesQuantity]))
        			{
        				classes.get(i).add(example);
        				break;
        			}
        		}
        	}
        	
        	dataFileReader.close();
        	System.out.println("Data aquisition finished successfuly.");
        	return true;
    	}
    	catch (IOException e)
    	{
    		System.out.println("The file does not exist in this path.");
    		return false;
    	}
    }
    
    public void divideData(int chunks)
    {
		int dividedClassSize;
		dividedData = new ArrayList<>();
		
    	for (int i = 0; i < chunks; i++)
    	{
			int k = 0;		
    		dividedData.add(new ArrayList<>());
    		
    		for (Iterator<List<String[]>> iterator = classes.iterator(); iterator.hasNext();) 
    		{
				List <String[]> iterClass = iterator.next();
				dividedClassSize = iterClass.size()/chunks;
				dividedData.get(i).add(new ArrayList<>());
								
				for(int j = dividedClassSize*i; j < dividedClassSize*(i+1); j++)
				{
					dividedData.get(i).get(k).add(iterClass.get(j));
				}
				
				k++;
			}
    	}
    	
    	int j = 0;
    	String[] tempExample = new String[classes.get(0).get(0).length];
    	
    	for (Iterator<List<String[]>> iterator = classes.iterator(); iterator.hasNext();)
    	{
    		List<String[]> tempClasses = iterator.next();	
    		int tempClassesSize = tempClasses.size();
    		
    		if(tempClassesSize%chunks!=0)
    		{
    			for(int i = 0; i < tempClassesSize%chunks; i++)
    			{
    				tempExample = tempClasses.get(tempClassesSize-1-i);
    				dividedData.get(i).get(j).add(tempExample);
    			}
    		}
    		j++;
    	}
    	
    	/*
    	System.out.println("chunks in divedData: " + dividedData.size());
    	j = 0;
    	for (Iterator<List<List<String[]>>> iterator = dividedData.iterator(); iterator.hasNext();)
    	{
    		List <List<String[]>> tempClasses = iterator.next();
    		int nc = tempClasses.size();
    		System.out.println("classes in chunk" + j + ": " + nc);
    		for(int i = 0; i < nc; i++)
    		{
    			System.out.println("examples in class" + i + ": " + tempClasses.get(i).size());
    		}
    		j++;
    		
    	} 
    	*/   	
    }
    
    
    public void discretizeAttributeByWidth(int attrNumber, int bins)
    {
		double max = Double.parseDouble(classes.get(0).get(0)[attrNumber]);
		double min = max;
		
    	for (Iterator<List<String[]>> iteratorClasses = classes.iterator(); iteratorClasses.hasNext();)
    	{	
    		for (Iterator<String[]> iteratorExamples = iteratorClasses.next().iterator(); iteratorExamples.hasNext();)
    		{
    			//finding maximum value of attribute
    			double tempValue = Double.parseDouble(iteratorExamples.next()[attrNumber]);
    			if(max < tempValue)
    				max = tempValue;
    			
    			//finding minimum value of attribute
    			else if(min > tempValue)
    				min = tempValue;
    		}
    	}
    	
    	int i = 0;
    	for (Iterator<List<String[]>> iteratorClasses = classes.iterator(); iteratorClasses.hasNext();)
    	{	
    		int j = 0;
    		for (Iterator<String[]> iteratorExamples = iteratorClasses.next().iterator(); iteratorExamples.hasNext();)
    		{
    			double attrValue = Double.parseDouble(iteratorExamples.next()[attrNumber]);
    			
    			for(int k = 0; k < bins; k++)
    			{
    				if(attrValue >= (min+((max-min)/bins)*k) && attrValue <= (min+(((max-min)/bins)*(k+1))))
        				classes.get(i).get(j)[attrNumber] = String.valueOf((min+((max-min)/bins)*k)) + "-" +  String.valueOf(min+(((max-min)/bins)*(k+1)));
    			}	
    			j++;
    		}
    		i++;
    	}
    }
    
    public void discretizeAttributeByFrequency (int attrNumber, int bins)
    {
    	
	List<String[]> examples = new ArrayList<String[]>();

		for (Iterator<List<String[]>> iteratorClasses = classes.iterator(); iteratorClasses.hasNext();)
    	{	
    		for (Iterator<String[]> iteratorExamples = iteratorClasses.next().iterator(); iteratorExamples.hasNext();)
    			examples.add(iteratorExamples.next());
    	}
    	
    	//sorting examples list
    	String[] tempExample;
    	for(int i = 1; i < examples.size()-1; i++)
    	{
    		int j = i;
    		while((j > 0) && (Double.parseDouble(examples.get(j-1)[attrNumber]) > Double.parseDouble(examples.get(j)[attrNumber])))
			{
    			tempExample = examples.get(j-1);
    			examples.set(j-1,examples.get(j));
    			examples.set(j,tempExample);
    			j--;
			}

    	}
    	
    	//discretisation 
    	int binSize = examples.size()/bins;
    	int leftExamples = examples.size()%bins;
    	int j = 0;
    	int compValue = 0;
    	String newAttrValue;
    	
		for(int i = 0; i < bins; i++)
		{
			if(leftExamples > 0)
				newAttrValue = examples.get(j)[attrNumber] + "-" + examples.get(binSize*(i+1)+compValue)[attrNumber];
			else
				newAttrValue = examples.get(j)[attrNumber] + "-" + examples.get(binSize*(i+1)+compValue-1)[attrNumber];
			
			for(; j < binSize*(i+1) + compValue; j++)
			{
				//examples.get(j)[attrNumber] = "BIN" + String.valueOf(i+1);
				examples.get(j)[attrNumber] = newAttrValue;
			}
			
			if(leftExamples > 0)
			{
				//examples.get(binSize*(i+1))[attrNumber] = "BIN" + String.valueOf(i+1);
				examples.get(binSize*(i+1)+compValue)[attrNumber] = newAttrValue;
				compValue++;
				j++;
				leftExamples--;
			}
		}
	
		//writing discretised data to classes variable
  		classes.clear();
  		
    	for (int i = 0; i < classesQuantity; i++)
    	{
    		classes.add(new ArrayList<String[]>());
    	}
	

		for (Iterator<String[]> iteratorExamples = examples.iterator(); iteratorExamples.hasNext();)
		{
			example = iteratorExamples.next();
    		for (int i = 0; i < classesQuantity; i++)
    		{
    			if (classValues[i].equals(example[attributesQuantity]))
    			{
    				classes.get(i).add(example);
    				break;
    			}
    		}
		}




	
    }
    
    public void writeDataToFile(String path)
    {
    	String writeFile = path;

		BufferedWriter dataFileWriter;
		try 
		{
			dataFileWriter = new BufferedWriter(new FileWriter(writeFile));
		for (Iterator<List<String[]>> iteratorClasses = classes.iterator(); iteratorClasses.hasNext();)
    	{	
    		for (Iterator<String[]> iteratorExamples = iteratorClasses.next().iterator(); iteratorExamples.hasNext();)
    		{
    			int i = 0;
    			for(String attr : iteratorExamples.next())
    			{
    				dataFileWriter.write(attr);
	    			if (i<attributesQuantity)
	    				dataFileWriter.write(",");
	    			
	    			i++;
    			}
    			dataFileWriter.write("\n");
    		}
    	}
		
		dataFileWriter.close();
		System.out.println("Writing data to file finished successfuly.");
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
    }
    
    public List<List<String[]>> getData()
    {
    	return this.classes;
    }
    
    public void appendTrainingData(int chunkFrom, int chunkTo)
    {
    	List<String[]> dataClass;
    	int j;
    	
    	if(trainingData.isEmpty())
    	{
	    	for (int i = 0; i < classesQuantity; i++)
	    	{
	    		trainingData.add(new ArrayList<>());
	    	}
    	}
    	
    	for (int i = chunkFrom; i < chunkTo; i++)
    	{
    		j = 0;
    		for(Iterator<List<String[]>> iterator = dividedData.get(i).iterator(); iterator.hasNext();)
    		{
    			dataClass = iterator.next();
    			
    			for(int k = 0; k < dataClass.size(); k++)
    			{
    				this.trainingData.get(j).add(dataClass.get(k));
    			}
    			j++;
    		}	
    	}		
    }
    
    public List<List<String[]>> getTrainingData()
    {
    	return this.trainingData;
    }
     
    public void clearTrainingData()
    {
    	trainingData = new ArrayList<>();
    }
    
    public void appendTestData(int chunkFrom, int chunkTo)
    {
    	List<String[]> dataClass;
    	String[] example;
    	
    	for (int i = chunkFrom; i < chunkTo; i++)
    	{
    		for(Iterator<List<String[]>> iterator = dividedData.get(i).iterator(); iterator.hasNext();)
    		{
    			dataClass = iterator.next();
    			
    			for(int k = 0; k < dataClass.size(); k++)
    			{
    				example = dataClass.get(k);
    				//example[attributesQuantity] = "";
    				testData.add(example);
    			}
    		}	
    	}	

    }
    
    public List<String[]> getTestData()
    {
    	return testData;
    }
    
    public void clearTestData()
    {
    	testData = new ArrayList<>();
    }
    
    public List <List<List<String[]>>> getDividedData()
    {
    	return dividedData;
    }
    
    public void standarizeData()
	{
		List<String[]> simplifiedClassesList = new ArrayList<>();
		List<String[]> tempClassesList = new ArrayList<>();

		//copying examples from classes list to temporary simplified list
		for (Iterator<List<String[]>> iteratorClasses = classes.iterator(); iteratorClasses.hasNext();)
		{
			List<String[]> examplesList = iteratorClasses.next();
			
			for (Iterator<String[]> iteratorExamples = examplesList.iterator(); iteratorExamples.hasNext();)
			{
				String[] example = iteratorExamples.next();
				String[] exampleToAdd = new String[example.length - 1];
				
				for(int i = 0; i < example.length - 1; i++)
					exampleToAdd[i] = example[i];
				
				simplifiedClassesList.add(exampleToAdd);
				tempClassesList.add(example);
			}
		}
		
		double[] attributeMeans = new double[simplifiedClassesList.get(0).length];

		for(int i = 0; i < attributeMeans.length; i++)
			attributeMeans[i] = 0;
		
		double[] attributeStds = new double[simplifiedClassesList.get(0).length];

		for(int i = 0; i < attributeStds.length; i++)
			attributeStds[i] = 0;
		
		//calculate mean
		for (Iterator<String[]> iterator = simplifiedClassesList.iterator(); iterator.hasNext();)
		{
			String[] example = iterator.next();
			
			for(int i = 0; i < example.length; i++)
			{
				double attrValue = Double.parseDouble(example[i]);
				
				attributeMeans[i] += attrValue; 
			}
		}
		
		for(int i = 0; i < attributeMeans.length; i++)
			attributeMeans[i] /= simplifiedClassesList.size();

		//calculate std
		for (Iterator<String[]> iterator = simplifiedClassesList.iterator(); iterator.hasNext();)
		{
			String[] example = iterator.next();
			
			for(int i = 0; i < example.length; i++)
			{
				double attrValue = Double.parseDouble(example[i]);
				
				attributeStds[i] += Math.pow(attrValue - attributeMeans[i], 2); 
			}
		}
		
		for(int i = 0; i < attributeStds.length; i++)
			attributeStds[i] = Math.sqrt(attributeStds[i] /(simplifiedClassesList.size() - 1));
		
		//calculate standarized value
		for (Iterator<String[]> iterator = simplifiedClassesList.iterator(); iterator.hasNext();)
		{
			String[] example = iterator.next();

			for(int i = 0; i < example.length; i++)
			{
				double attrValue = Double.parseDouble(example[i]);
				
				example[i] = String.valueOf((attrValue - attributeMeans[i]) / attributeStds[i]);
			}
		}
		
		for (int i = 0; i < tempClassesList.size(); i++)
		{
			String[] example = new String[tempClassesList.get(0).length];
			
			for(int j = 0; j < example.length - 1; j++)
				example[j] = simplifiedClassesList.get(i)[j];
			
			example[example.length - 1] = tempClassesList.get(i)[tempClassesList.get(i).length - 1];
			
			simplifiedClassesList.set(i, example);
		}
		
    	if (!classes.isEmpty())
    		classes.clear();
    	
    	for (int i = 0; i < classesQuantity; i++)
    		classes.add(new ArrayList<String[]>());
    	
		for (Iterator<String[]> iterator = simplifiedClassesList.iterator(); iterator.hasNext();)
    	{
    		String[] example = iterator.next();

    		for (int i = 0; i < classesQuantity; i++)
    		{
    			if (classValues[i].equals(example[attributesQuantity]))
    			{
    				classes.get(i).add(example);
    				break;
    			}
    		}
    	}
	}

}
