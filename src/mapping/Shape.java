package mapping;
import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;


public class Shape {
	protected String name;
	protected float red, green, blue;
	protected ArrayList<Point> points;
	private Random random = new Random();
	
	public Shape(String name) {
		this.name = name;
		this.points = new ArrayList<Point>();
	}
	
	public void setColor(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public void addPoint(Point p) {
		points.add(p);
	}
	
	public String getName() {
		return name;
	}
	
	public int getNumPoints() {
		return points.size();
	}
	
	public void draw (PApplet canvas) {
		canvas.fill(this.red, this.green, this.blue);
		canvas.noStroke();
		canvas.beginShape();
		
		//vertex method allows to lay down a point 
		for (int i = 0; i < points.size(); i++) {
			canvas.vertex(points.get(i).getX(), points.get(i).getY());
		}
		
		//end shape
		canvas.endShape(canvas.CLOSE);
	}
	
	
}
