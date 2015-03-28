/**
 	*Visualizes coordinates
 	*Intended to represent something at the state[0] or county[1] level
 */
package mapping;

import java.io.File;
import java.util.Random;
import java.util.Scanner;

public class MapData {
	private File mapFile;
	private float maxLatitude, minLatitude;
	private float maxLongitude, minLongitude;
	private float width, height; 
	private float xScale, yScale;
	private int uniqueKeys;
	//whether state [0] or county level[1]
	private int level;
	
	private Random random = new Random();
	
	public MapData(File mapFile) {
		this.mapFile = mapFile;
		try {
			setup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//get Scale
	public MapData(File mapFile, int width, int height, int level) {
		this.mapFile = mapFile;
		this.level = level;
		try {
			setup();
		} catch (Exception e) {
			e.printStackTrace();
		}
		xScale = width / (maxLongitude - minLongitude);
		yScale = height / (maxLatitude - minLatitude);
		this.width = width;
		this.height = height;
	}
	
	public float getMaxLongitude() {
		return maxLongitude;
	}
	
	public float getMinLongitude() {
		return minLongitude;
	}
	
	public float getMaxLatitude() {
		return maxLatitude;
	}
	
	public float getMinLatitude() {
		return minLatitude;
	}
	
	public boolean fileExists() {
		return mapFile.exists();
	}
	
	private void setup() throws Exception{
		Scanner fileScan = new Scanner(mapFile);
		
		//initialize
		String[] fileLine = fileScan.nextLine().split(",");
		String key = "";
		float fileLong = Float.parseFloat(fileLine[1 + level]);
		float fileLat = Float.parseFloat(fileLine[2 + level]);
		maxLongitude = fileLong;
		minLongitude = fileLong;
		maxLatitude = fileLat;
		minLatitude = fileLat;
		while (fileScan.hasNext()) {
			//read token
			fileLine = fileScan.nextLine().split(",");
			
			if (!key.equals(fileLine[0 + level])) {
				key = fileLine[0 + level];
				uniqueKeys++;
			}
			
			fileLong = Float.parseFloat(fileLine[1 + level]);
			fileLat = Float.parseFloat(fileLine[2 + level]);
			//check and update longitude bounds
			if (fileLong > maxLongitude) maxLongitude = fileLong;
			else if (fileLong < minLongitude) minLongitude = fileLong;
			
			//check and update latitude bounds
			if (fileLat > maxLatitude) maxLatitude = fileLat;
			else if (fileLat < minLatitude) minLatitude = fileLat;
		}
		fileScan.close();
	}
	
	//Creates shapes from outlines
	public Shape[] getOutlines() throws Exception{
		Shape[] outlines = new Shape[uniqueKeys];
		//tracks indices in outlines
		int keyCount = 0;
		
		Scanner fileScan = new Scanner(mapFile);

		//initialize 1st outline
		String[] fileLine = fileScan.nextLine().split(",");
		//whole key, distinguishes index values for keys
		String key = fileLine[0 + level];
		//lumps keys with same starting substring
		String keyIndex = key.substring(0, 2);
		float fileLong = Float.parseFloat(fileLine[1 + level]);
		float fileLat = Float.parseFloat(fileLine[2 + level]);
		outlines[keyCount] = new Shape(key);
		//offset by minimum longitude and latitude
		float scaledX = xScale * (fileLong - minLongitude);
		float scaledY = height - (yScale * (fileLat - minLatitude));
		outlines[keyCount].addPoint(new Point(scaledX, scaledY));
		
		//rgb values
		int r = random.nextInt(256), g = random.nextInt(256), b = random.nextInt(256);
		outlines[keyCount].setColor(r, g, b);
		
		while (fileScan.hasNext()) {
			fileLine = fileScan.nextLine().split(",");
			key = fileLine[0 + level];
			
			//if new key, change array position in outlines
			if (!key.equals(outlines[keyCount].getName())) {
				keyCount++;
				outlines[keyCount] = new Shape(key);
				//if different starting substring as well, generate new color
				if (!keyIndex.equals(key.substring(0, 2))) {
					r = random.nextInt(256); g = random.nextInt(256); b = random.nextInt(256);
					keyIndex = key.substring(0, 2);
				}
				outlines[keyCount].setColor(r, g, b);
			}
			
			fileLong = Float.parseFloat(fileLine[1 + level]);
			fileLat = Float.parseFloat(fileLine[2 + level]);
			scaledX = xScale * (fileLong - minLongitude);
			scaledY = height - (yScale * (fileLat - minLatitude));
			outlines[keyCount].addPoint(new Point(scaledX, scaledY));
		}
		fileScan.close();
		
		return outlines;
	}
	
}
