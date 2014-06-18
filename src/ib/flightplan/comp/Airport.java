package ib.flightplan.comp;

import java.util.ArrayList;

public class Airport {
	private Point location;
	private String city;
	private String country;
	private String iata;
	
	public Airport (String iata, String city, String country, Point loc)
	{
		this.iata = iata;
		this.city = city;
		this.country = country;
		this.location = loc;
	}
	public double calculateDistance (Point dest)
	{
		return Point.calculateDistance(this.location, dest);
	}
	public boolean isInRange (Point p, double range)
	{
		return this.location.isInRange(p, range);
	}
	
	public String getCity()
	{
		return this.city;
	}
	public String getCountry()
	{
		return this.country;
	}
	public String getIATA()
	{
		return this.iata;
	}
	public Point getLocation()
	{
		return this.location;
	}
	@Override
	public String toString()
	{
		String desc = this.iata + " " + this.city + " " + this.country + " " + String.format("%.4f %.4f", location.y, location.x);
		return desc;
	}
	
	@Override
	public boolean equals(Object v)
	{
		boolean value = false;
		
		if (v instanceof Airport)
		{
			Airport ptr = (Airport) v;
			value = ptr.iata.equals(this.iata);
		}
		return value;
	}
}
