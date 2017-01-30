package antClustering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Initialiser {

	public static void main(String[] args) 
	{
		/*RandomPixelsPicture picture  = new RandomPixelsPicture();
		
		picture.generatePicture();
		picture.savePicture();
		
		Clusterer clusterer = new Clusterer();
		clusterer.setImage(picture.getImage());
		clusterer.clusterPixelsOnImage();
		clusterer.saveClusteredImage();*/
		
		DataAcquisitor dataAcq = DataAcquisitor.getInstance();
		ClustererToDatasets cTD = new ClustererToDatasets();
		/*
		dataAcq.initialise("iris.data.txt");
		dataAcq.getDataFromFile();
		dataAcq.standarizeData();
		dataAcq.divideData(1);
		dataAcq.appendTestData(0, 1);
		
		cTD.setData(dataAcq.getTestData());
		
		cTD.ALPHA = 5;
		cTD.fileName = "ALPHA=" + cTD.ALPHA + ".png";
		cTD.clusterData();
		cTD.saveClusteredImage();
		*/
		
		String dataSetName = "seeds.data.txt";

		List<double[]> options = new ArrayList<double[]>();
		
		double[] opts1 = {5,1000,2,0.3,3};
		double[] opts2 = {10,5000,1,0.03,3};
		double[] opts3 = {10,10000,1,0.05,3};
		double[] opts4 = {10,30000,2,5,3};
		double[] opts5 = {5,60000,2,10,3};
		double[] opts6 = {20,15000,2,100,3};
		options.addAll(new ArrayList<double[]>(Arrays.asList(opts1)));
		
		/*final double[] alphas = {100};
		final int iterations = 15000;
		final int ants = 5;*/
		for(double[] opts : options)
		{
			dataAcq.initialise(dataSetName);
			dataAcq.getDataFromFile();
			dataAcq.standarizeData();
			dataAcq.divideData(1);
			dataAcq.appendTestData(0, 1);
			
			cTD.setData(dataAcq.getTestData());
			
			String info = dataSetName.split("\\.")[0] 
					+ "_ants=" + String.valueOf(opts[0]) 
					+ "_iters=" + String.valueOf(opts[1]) 
					+ "_size=" + String.valueOf(opts[2]) 
					+ "_alpha=" + String.valueOf(opts[3])
					+ "_c=" + String.valueOf(opts[4]) + "_cosine_distance";
			
			System.out.println(info + " started");
			cTD.fileName = info +  ".png";
			cTD.classValues = dataAcq.getClassValues();
			cTD.clusterData((int) opts[0], (int) opts[1], (int) opts[2], opts[3], opts[4]);
			cTD.saveClusteredImage();
			System.out.println(info + " finished");
		}

		System.out.println("Finished totally");
	}

}
