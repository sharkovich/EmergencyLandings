package ib.flightplan.comp;

import ib.flightplan.Main;

public class Point {
	public double x;
	public double y;
	
	public Point ()
	{
		this.x = 0;
		this.y = 0;
	}
	
	public Point(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	public Point(Point p)
	{
		this.x += p.x;
		this.y += p.y;		
	}
	
	public static double calculateDistance (Point a, Point b)
	{
		double dx = b.x - a.x;
		double dy = b.y - a.y;
		return Main.DISTANCE_FAC*Math.sqrt(dx*dx + dy*dy);
	}
	public boolean isInRange(Point point, double range)
	{
		double square_distance = (Math.pow(Main.DISTANCE_FAC, 2))*(Math.pow((this.x - point.x),2) + Math.pow((this.y - point.y),2));
		return (square_distance <= range*range);
	}
	
	public void add(Point p)
	{
		this.x += p.x;
		this.y += p.y;
	}
	public void add(double x, double y)
	{
		this.x += x;
		this.y += y;
	}

	@Override
	public boolean equals(Object v)
	{
		boolean value = false;
		
		if (v instanceof Point)
		{
			Point ptr = (Point) v;
			value = ptr.x == this.x && ptr.y == this.y;
		}
		return value;
	}
	public void copyTo(Point p)
	{
		p.x = this.x;
		p.y = this.y;
	}
	public void clone(Point p)
	{
		this.x = p.x;
		this.y = p.y;
	}
	
	public static Point getClosestPoint (Point A, Point B, Point P)
	{
		double[] AP = {P.x - A.x, P.y - A.y};
		double[] AB = {B.x - A.x, B.y - A.y};
		
		double magnitudeAB = Math.pow(AB[0], 2) + Math.pow(AB[1], 2);
		double ABAPproduct = AP[0]*AB[0] + AP[1]*AB[1];
		
		double distance = ABAPproduct/magnitudeAB;
		
		Point returnPoint;
		if (distance < 0)
		{
			returnPoint = new Point(A);
		} else if (distance > magnitudeAB)
		{
			returnPoint = new Point(B);
		} else
		{
			returnPoint = new Point(A.x + AB[0]*distance, A.y + AB[1]*distance);
		}
		return returnPoint;
	}


	
}
