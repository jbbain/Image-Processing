/**
 * @author JBain
 * Lab Assignment 4
 */


import java.awt.image.ImagingOpException;
import java.util.Random;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;


public class Lab4Bain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ImageJ();
		Lab4Bain lab = new Lab4Bain();
		lab.openImageHTTP();
		lab.colorToGray();
		lab.invertGrayImage();
		lab.computeHistogram8Bits();
		lab.adjustContrast(1.5);
		lab.part1();
		lab.binaryImage(1);
	//	lab.randomBackgroundGenerator(ip, colorRange);
		lab.randomCircle().show();
		lab.part2(lab.randomCircle());
		lab.randomKCircles().show();
		

	}
	
	public void openImageHTTP(){
		//ImagePlus image2 = IJ.openImage("http://imagej.net/images/FluorescentCells.jpg");
		ImagePlus image = IJ.openImage("Audi.jpg");
		
		image.show();
		//image2.show();
	}
	
	public void colorToGray(){
		ImagePlus original = IJ.openImage("Audi.jpg");
		ImagePlus gray = IJ.createImage("Gray","8-bit", original.getWidth(), original.getHeight() , 1);
		int [] pixels = (int []) original.getStack().getPixels(1);
		byte [] pixDst = (byte []) gray.getStack().getPixels(1);
		for (int y = 0; y< original.getHeight(); y++){
			for (int x = 0; x < original.getWidth(); x++){
				int r = pixels [x + y * original.getWidth()] >> 16 & 0xFF;
				int g = pixels [x + y * original.getWidth()] >> 8 & 0xFF;
				int b = pixels [x + y * original.getWidth()] & 0xFF;
				
				pixDst [x +y * original.getWidth()] = (byte) ((r+g+b)/3);
			}
		}
		
		original.show();
		gray.show();
		
	}
	
	
	public void invertGrayImage(){
		ImagePlus image = IJ.openImage("AudiGray.jpg");
		if(image.getType() != ImagePlus.GRAY8){
			//TODO: Show error message			
			return;
		}
		byte[] pixels = (byte[]) image.getStack().getProcessor(1).getPixels();
		ImageProcessor ip = image.getStack().getProcessor(1);
		for (int y = 0; y < image.getHeight(); y++){
			for (int x = 0; x < image.getWidth(); x++){
				pixels [x + y * image.getWidth()] = (byte) (255 - pixels [ x + y * image.getWidth()]);
				//ip.getPixels();
			}
		}
		image.show();
	}
	
	
	
	public void computeHistogram8Bits(){
		int [] histogram = new int [256];
		ImagePlus image = IJ.openImage("AudiGray.jpg");
		//TODO: Check that image is grayscale. HW
		
		ImageProcessor ip = image.getStack().getProcessor(1);
		for ( int y = 0; y < image.getHeight(); y ++){
			for (int x = 0; x < image.getWidth(); x++){
				histogram [ip.getPixel(x, y)]++;
				
			}
		}
		//histogram can now be used
		
	}
	
	
	public void adjustContrast (double factor){
		//e.g. factor = 1.5 -> 50% increase of contrast
		ImagePlus image = IJ.openImage("Audi.jpg");
		//TODO: Check that image is grayscale.
		ImageProcessor ip = image.getStack().getProcessor(1);
		for ( int y = 0; y < image.getHeight(); y ++){
			for (int x = 0; x < image.getWidth(); x++){
				int a = (int) (ip.get(x, y) * 1.5 + 0.5);
				if (a > 255){
					a = 255;
				}
				
				ip.set(x, y, a);
			}
		}
		image.show();
			
	}
	
	
	
	public void part1(){
		ImagePlus image = IJ.openImage("AudiGray.jpg");
		ImageProcessor ip = image.getStack().getProcessor(1);
		ImageProcessor histIP = new ByteProcessor(256, 100);
		histIP.setValue(255); //Sets default fill or draw value;
		histIP.fill();
		int [] hist = ip.getHistogram();
		int max = 0;
		for (int i = 1; i < histIP.getWidth(); i++){
			if ( max < hist [i]){
				max = hist[i];
			}
		}
		for (int i = 0; i < histIP.getWidth(); i++){
			hist[i] = (int) (((double) hist[i]/max)*100);
			
		}
		for (int x = 0; x < 256; x++){
			for (int y = 0; y < 100; y++){
				if ( y<= (100 - hist[x])){
					histIP.set(x, y, 255);
					
				}
				else histIP.set(x, y, 0);
			}
		}
		
		//Creating image with existing Image Processor
		ImagePlus histImg = new ImagePlus ("Histogram", histIP);
		histImg.show();
		image.show();
	}
	
	
	private void binaryImage(int threshold){
		ImagePlus image = IJ.openImage("AudiGray.jpg");
		ImagePlus binary = IJ.createImage("AudiBinary.jpg", "8-bit", image.getWidth(),image.getHeight(),1);
		ImageProcessor ip = image.getStack().getProcessor(1);
		ImageProcessor ipB = binary.getStack().getProcessor(1);
		for (int y = 0; y < ip.getHeight(); y++){
			//v
			for (int x = 0; x < ip.getWidth(); x ++){
				//u
				if(ip.get(x, y)< threshold){
					ipB.set(x, y, 0);
				}
				else{
					ipB.set(x, y, 255);
				}
			}
		}
		binary.show();
	}


//PART TWO OF LAB - DETECTING BLOBS
	
public double getDistance(int x1, int y1, int x2, int y2){
	return Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
}

private void randomBackgroundGenerator (ColorProcessor ip, int colorRange){
	//Random Background
	Random rValue = new Random();
	for (int y = 0; y < ip.getHeight(); y ++){//v
		for (int x = 0; x < ip.getWidth(); x++){//x
			int r = (rValue.nextInt(colorRange)+ 256 - colorRange) << 16;
			int g = (rValue.nextInt(colorRange)+ 256 - colorRange) << 8;
			int b = rValue.nextInt(colorRange)+ 256 - colorRange;
			ip.set(x, y, r + g + b);
		}
	}
}


private void randomCircleGenerator(ColorProcessor ip, int colorRange){
	//Random Circle
	Random rValue = new Random();
	int radius = rValue.nextInt(30) + 10; //TODO: Remove magic number. 
	int xC = rValue.nextInt(ip.getWidth());
	int yC = rValue.nextInt(ip.getHeight());
	yC = ((yC - radius) >= 0)? yC: radius;
	xC = ((xC - radius) >= 0)? xC: radius;
	yC = ((yC + radius) < ip.getHeight())? yC: yC - radius;
	xC = ((xC + radius) < ip.getWidth())? xC: xC - radius;
	
	for (int y = yC - radius; y <= yC + radius; y++){
		for(int x = xC - radius; x <= xC + radius; x++){
			int r = (rValue.nextInt(colorRange) + 256 - colorRange) << 16;
			int g = (rValue.nextInt(colorRange)) << 8;
			int b = rValue.nextInt(colorRange);
			if (getDistance(x, y, xC, yC) < radius){
				ip.set(x, y, r + g + b);
			}
		}
	}
	
}

private ImagePlus randomCircle(){
	ImageProcessor ip = new ColorProcessor(600, 400);
	randomBackgroundGenerator((ColorProcessor) ip, 80);
	randomCircleGenerator((ColorProcessor) ip, 80);
	return new ImagePlus("randomCircle", ip);
	}



//ASSIGNMENT 2- DETECTING A CIRCLE

private void part2(ImagePlus image){ //Detecting a circle
	//CODE GOES HERE
	int minX = image.getWidth();
	int minY = image.getHeight();
	int maxX = 0;
	int maxY = 0;
	
	
	ImageProcessor ip = image.getStack().getProcessor(1);
	for (int y = 0; y < ip.getHeight(); y++){
		//v
		for (int x = 0; x < ip.getWidth(); x ++){
			if(isObject (ip.get(x, y))){
				if (x < minX){
					minX = x;
				}
				if (y < minY){
					minY = y;
				}
				if (x > maxX){
					maxX = x;
				}
				if (y> maxY){
					maxY = y;
				}
				
			}
			
		}
	}
	ip.drawRect(minX, minY, maxX - minX, maxY - minY);
	ip.drawString ("Object Found", minX, minY);
	image.show();
	
	
}

public boolean isObject(int c){
	int r = c >> 16 & 0xFF;
	int g = c >> 8 & 0xFF;
	int b = c & 0xFF;
	
	if (r > (256-80 ) && r < 256 && g < 79 & b < 79){
		return true;
	}
	return false;
}

private ImagePlus randomKCircles(){
	ImageProcessor ip = new ColorProcessor(600,400);
	randomBackgroundGenerator((ColorProcessor) ip, 80);
	
	Random rValue = new Random();
	int kCircles = rValue.nextInt(7) +3;
	for (int k = 0; k < kCircles; k ++){
		randomCircleGenerator((ColorProcessor) ip, 80);
		
	}
	return new ImagePlus("randomCircles", ip);
}


}
