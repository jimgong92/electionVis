package mapping;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class MapSetup {
	//File containing the to-be-mapped coordinates
	private File coordinatesFile;
	//Minimum and maximum longitude (x) and latitude (y) from coordinatesFile
	private float maxLongitude, minLongitude, maxLatitude, minLatitude;
	//width and height of map in pixels
	private float width, height;
	//Scale values to adjust coordinate space to map space
	private float xScale, yScale;
	//Number of unique keys (e.g. number of states, number of counties)
	private int numUniqueKeys;
	//Collects all keys in file
	private ArrayList<String> keys = new ArrayList<String>();
	//If two separate level keys (e.g. county when there is also state), collect high keys to allow for zooming
	private ArrayList<String> highKeys = new ArrayList<String>();
	/*
	 * Adjusts what level of key is read from file 
	 * e.g. if a file has state and county keys, 0 reads state keys, 1 offsets the scanner to read county keys
	 */
	private int level;
	//random
	private Random random = new Random();
	
	public MapSetup(File coordinatesFile, float width, float height, int level) {
		this.coordinatesFile = coordinatesFile;
		this.width = width;
		this.height = height;
		this.level = level;
		try {
			setup();
		} catch (Exception e) {
			e.printStackTrace();
		}
		xScale = width / (maxLongitude - minLongitude);
		yScale = height / (maxLatitude - minLatitude);
	}
	
	//Reads coordinateFile to get max/min lat/long values 
	private void setup() throws Exception {
		//indicate whether need for two levels of keys
		boolean multi = level > 0;
		
		Scanner fileScan = new Scanner(this.coordinatesFile);
		
		//Read first line to initialize variables
		String[] fileLine = fileScan.nextLine().split(",");
		if (multi) highKeys.add(fileLine[0]);
		String currentKey = fileLine[level];
		numUniqueKeys++;
		//initialize key(s)
		keys.add(currentKey);
		
		float fileLong = Float.parseFloat(fileLine[level + 1]);
		float fileLat = Float.parseFloat(fileLine[level + 2]);
		maxLongitude = fileLong; minLongitude = fileLong;
		maxLatitude = fileLat; minLatitude = fileLat;
		
		while (fileScan.hasNext()) {
			//read next line
			fileLine = fileScan.nextLine().split(",");
			
			//if multi-level, check if new key to add
			if (multi) {
				if (!highKeys.get(highKeys.size() - 1).equals(fileLine[0])) highKeys.add(fileLine[0]);
			}
			
			currentKey = fileLine[level];
			//check key, if new, add to keys array
			//assumes file is sorted
			if (!keys.get(numUniqueKeys - 1).equals(currentKey)) {
				numUniqueKeys++;
				keys.add(currentKey);
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
		
	}
	
	public Shape[] getOutlines() throws Exception{
		Shape[] outlines = new Shape[numUniqueKeys];
		//tracks indices in outlines
		int keyCount = 0;
		
		Scanner fileScan = new Scanner(coordinatesFile);

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
	
	//returns the coordinates to make regional map upon click
	public HashMap<String, File> getRegionCoordinates() throws Exception{
		HashMap<String, File> regionCoordinates= new HashMap<String, File>();
		
		//TODO : Writes county-level coordinates into new text file in order to read upon retrieval
		//TODO : Need to use PrintWriter
		
		for (int i = 0; i < highKeys.size(); i++) {
			//TODO Must create outlines for individual state and then add into the hashmap attached to key name
			MapSetup stateMap = new MapSetup(this.coordinatesFile, this.width, this.height, this.level);
			//TODO Afix coordinate file 
			regionCoordinates.put(highKeys.get(i), arg1);
		}
		
		return regionCoordinates;
	}
	
}
