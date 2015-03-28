package mapping;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import processing.core.PApplet;
import processing.core.PFont;

public class Map extends PApplet{
	//size of window
	private int w = 800, h = 500;
	//year of election - 2012 is default
	private String year = "2012"; 
	private String typing = ""; //collect user input
	private PFont f;
	private Shape[] counties;
	//Retrieve individual state coordinates from the MapData file
	//Allows for zooming in
	private File[] stateCoordinates;
	
	public void setup() {
		
		size(w, h); 
		//county level MapData
		MapData map = new MapData(new File("data/USA-county.csv"), w, h, 1);
		
		
		
		f = createFont("Arial",16,true);
		

		try {
			counties = map.getOutlines();
			
			File directory = new File("data");
			
			//Retrieve votes in each county
			ArrayList<File> stateFiles = new ArrayList<File>();
			File[] filler = directory.listFiles();
			for (int i = 0; i < filler.length; i++) {
				String fileName = filler[i].getName();
				//do not count AK or HI
				if (fileName.endsWith(this.year+".txt") && !fileName.startsWith("AK") && !fileName.startsWith("HI")) {
					stateFiles.add(filler[i]);
				}
			}
			//track counties index
			int countyCount = 0;
			
			for (int i = 0; i < stateFiles.size(); i++) {
				Scanner fileScan = new Scanner(stateFiles.get(i));
				//skip first line
				fileScan.nextLine();
				
				//read state file
				while (fileScan.hasNext()) {
					String[] fileLine = fileScan.nextLine().split(",");
					String countyKey = fileLine[0];
					long rVotes = Long.parseLong(fileLine[1]); //Romney
					long gVotes = Long.parseLong(fileLine[3]); //Other
					long bVotes = Long.parseLong(fileLine[2]); //Obama
					float total = rVotes + gVotes + bVotes;
					float r = (rVotes/total) * 255, g = (gVotes/total) * 255, b = (bVotes/total) * 255;
					
					for (Shape county : counties) {
						if (county.getName().equals(countyKey)) {
							county.setColor(r, g, b);
						}
					}
				}
			}
			
			for (int i = 0; i < counties.length; i++) {
				counties[i].draw(this);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void draw() {
		int indent = 25;
		  
		// Set the font and fill for text
		textFont(f);
		fill(0);
		  
		// Display everything
		text("Enter election year: " + typing, indent, 40);
		text("Current Selection: " + year,indent, 60);
		text("Hit enter twice to view map, otherwise hit enter once and retype to change selection.", indent, 100);
	}
	

	public void keyPressed() {
		// If the return key is pressed, save the String and clear it
		if (key == '\n' ) {
			//If nothing typed (i.e. enter pressed once) and there is a selection
			if (typing == "" && year != typing) {
				for (int i = 0; i < counties.length; i++) {
					background(128, 128, 128);
					counties[i].draw(this);
				}
			}
			else {
				year = typing;
				// A String can be cleared by setting it equal to ""
				typing = "";
			}

		} else {
			// Otherwise, concatenate the String
			// Each character typed by the user is added to the end of the String variable.
			typing = typing + key;
		}
	}
}
