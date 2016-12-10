package antClustering;

public class Initialiser {

	public static void main(String[] args) 
	{
		RandomPixelsPicture picture  = new RandomPixelsPicture();
		
		picture.generatePicture();
		picture.savePicture();
	}

}
