package ib.flightplan.comp;

import ib.flightplan.exception.NoAirportInRangeException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class Flight {
	
	private Airport start;
	private Airport destination;
	private ArrayList<Airport> emLandings;
	private double distance;
	private double avgSpeed;
	private double rangeOfEmergency;
	private Date depTime;

	
	public Flight(Airport start, Airport destination, double range, Date departure, double speed) {
		this.start = start;
		this.destination = destination;
		this.rangeOfEmergency = range;
		this.depTime =  departure;
		this.avgSpeed = speed;
		this.distance = Point.calculateDistance(start.getLocation(), destination.getLocation());
	}
	
	/**
	 * Main computation method. 
	 * @param algorithm - algorithm which should be used in computation
	 * @param database - database of all airports
	 * @throws NoAirportInRangeException if range is insufficient
	 */
	public void computateFlight(int algorithm, HashSet<Airport> database) throws NoAirportInRangeException
	{
		HashSet<Airport> tmp = getClosest(database);
		
		switch (algorithm) {
		case 1:
			emLandings = algorithmA(tmp);
			break;
		case 2:
			emLandings = algorithmB(tmp);
			break;
		case 3:
			emLandings = algorithmC(tmp);
			break;
		default:
			emLandings = algorithmA(tmp);
			break;
		}
		SimpleDateFormat fd = new SimpleDateFormat("HH:mm:ss");
		
		for (Airport airport : emLandings) {
			Date time = timeOfPassing(airport);
			System.out.println(airport.toString() + " " + fd.format(time));
		}
	}
	
	/**
	 * Calculates time passing a point to which an airport is closest (in perpendicular line)
	 * 
	 * @param airport
	 * @return
	 */
	public Date timeOfPassing(Airport airport)
	{
		Point loc = Point.getClosestPoint(start.getLocation(), destination.getLocation(), airport.getLocation());
		double locDistance = Point.calculateDistance(start.getLocation(), loc);
		double time = locDistance/this.avgSpeed;
		
		int hour = (int)Math.floor(time);
		time = (time % 1)*60;
		int minute = (int)Math.floor(time);
		time = (time % 1)*60;
		int seconds = (int)Math.floor(time);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(depTime);
		cal.add(Calendar.HOUR, hour);
		cal.add(Calendar.MINUTE, minute);
		cal.add(Calendar.SECOND, seconds);
		return cal.getTime();
		
	}
	
	/**
	 * This algorithm finds airports in general vicinity of flight for
	 * further calculations.
	 * <p>
	 * It divides flight's path in equal increments, based on the emergency
	 * range of aircraft, then finds all airports within emergency range from
	 * that points.
	 *  
	 * @param database - full airport database
	 * @return cropped database of airports
	 */
	private HashSet<Airport> getClosest(HashSet<Airport> database)
	{
		//define variables
		HashSet<Airport> closest = new HashSet<>();
		Point startP = start.getLocation();
		Point endP = destination.getLocation();		
		Point current = new Point(startP);

		//calculate steps and increments based on range of emergency
		double steps = this.distance/this.rangeOfEmergency + 1;
		steps = Math.floor(steps);
		double dx = endP.x - startP.x;
		double dy = endP.y - startP.y;
		double incX = dx/steps;
		double incY = dy/steps;
		
		//set current point		
		current.clone(startP);
		
		for (int i = 0; i <= steps ; i++ )
		{
			//increment current position
			current.x += incX;
			current.y += incY;
			
			//check the database for airports in range of emergency, add them to new database
			for (Airport airport : database) {
				if (airport.isInRange(current, rangeOfEmergency))
				{
					closest.add(airport);
					/* TODO: Delete following line if database should be reusable. */
					//database.remove(airport); //remove that airport from database for optimization 
				}
			}
		}
		return closest;		
	}
	
	/**
	 * Checks the database for closest airport to given point that is in range
	 * @param current - current point of flight
	 * @param database - cropped database
	 * @return closest airport to given point
	 */
	private Airport getClosestAirport(Point current, HashSet<Airport> database)
	{
		Airport type = null;
		double mindist = this.distance;
		double curDist = 0;
		
		for (Airport airport : database) {
			if (airport.isInRange(current, rangeOfEmergency) && (curDist = airport.calculateDistance(current)) < mindist)
			{
				type = airport;
				mindist = curDist;
			}
		}
		return type;
	}
	
	/**
	 * Checks the database for closest airport to given point that is in range
	 * @param current - current point of flight
	 * @param database - cropped database
	 * @return closest airport to given point
	 */
	private Airport getFarthestAirport(Point current, HashSet<Airport> database)
	{
		Airport type = null;
		double maxdist = 0;
		double curDist = 0;
		
		for (Airport airport : database) {
			if (airport.isInRange(current, rangeOfEmergency) && (curDist = airport.calculateDistance(current)) > maxdist)
			{
				type = airport;
				maxdist = curDist;
			}
		}
		return type;
	}	
	
	/**
	 * Finds minimal, most cost-effective emergency landing path for given flight.
	 * Algorithm simulates flight path, and in case of loss of range with the current
	 * emergency landing finds nearest on it's way.
	 * <p>
	 * It uses cropped database of airports in the vicinity of flight path generated
	 * by getClosest() method for optimized performance
	 * 
	 * @param database - cropped database of airports
	 * @return ArrayList of emergency landing
	 * @throws NoAirportInRangeException if insufficient airports found
	 */
	private ArrayList<Airport> algorithmA (HashSet<Airport> database) throws NoAirportInRangeException
	{
		final int STEP_KM = 1; //define step in kilometers, for optimization purposes
		
		ArrayList<Airport> nearby = new ArrayList<>();
		nearby.add(start); //add start location as a first emergency lanidng
		
		// define points
		Point startP = start.getLocation();
		Point endP = destination.getLocation();		
		Point current = new Point(startP);

		
		//calculate steps and increments
		double steps = this.distance/STEP_KM + 1;
		steps = Math.floor(steps);
		double dx = endP.x - startP.x;
		double dy = endP.y - startP.y;
		double incX = dx/steps;
		double incY = dy/steps;
		
		//set current point
		current.clone(startP);
		
		for (int i = 0; i <= steps ; i++ )
		{
			//increment location of current position
			current.x += incX;
			current.y += incY;
			
			//if lost range to the closest airport
			if (!nearby.get(nearby.size()-1).isInRange(current, rangeOfEmergency))
			{
				//get closest airport to current position and add it to the list of emergency landings
				Airport tmp = getClosestAirport(current, database);
				if (tmp == null)
				{
					throw new NoAirportInRangeException();
				} else 
				{
					nearby.add(tmp);
					database.remove(tmp);	//remove that airport from database, for optimization purposes
				}
			}

		}
		if (!nearby.contains(destination))
		{
			nearby.add(destination);
		}
		return nearby;
	}
	
	/**
	 * Finds safest emergency landing path for given flight.
	 * For every x kilometers defined in method, algorithm finds closest airport.
	 * To make algorithm safe x must be as low as possible. Increasing x 
	 * decreases performance of algorithm.
	 * <p>
	 * Very low performance!
	 * <p>
	 * It uses cropped database of airports in the vicinity of flight path generated
	 * by getClosest() method for optimized performance
	 * 
	 * @param database - cropped database of airports
	 * @return ArrayList of emergency landing
	 * @throws NoAirportInRangeException if insufficient airports found
	 */
	private ArrayList<Airport> algorithmB (HashSet<Airport> database) throws NoAirportInRangeException
	{
		final double STEP_KM = 10; //define step in kilometers, for optimization purposes
		ArrayList<Airport> nearby = new ArrayList<>();
		nearby.add(start); //add start location as a first emergency lanidng

		// define points
		Point startP = start.getLocation();
		Point endP = destination.getLocation();		
		Point current = new Point(startP);

		
		//calculate steps and increments
		double steps = this.distance/STEP_KM;
		double dx = endP.x - startP.x;
		double dy = endP.y - startP.y;
		double incX = dx/steps;
		double incY = dy/steps;
		
		//set current point
		current.clone(startP);
		
		for (int i = 0; i <= steps ; i++ )
		{
			//increment location of current position
			current.x += incX;
			current.y += incY;
			
			//get airports in range from current position
			HashSet<Airport> tmp = new HashSet<>();
			for (Airport airport : database) {
				if (airport.isInRange(current, rangeOfEmergency)) {
					tmp.add(airport);
				}
			}
			
			//get closest airport from those in range
			Airport tmpA = getClosestAirport(current, tmp);
			if (tmpA == null)
			{
				throw new NoAirportInRangeException();
			}
			else if (!nearby.contains(tmpA))
			{
				nearby.add(tmpA);
			}
		}
		return nearby;
	}

	/**
	 * Finds minimal number of emergency airports for given flight.
	 * Algorithm simulates flight path, and in case of loss of range with the current
	 * emergency landing, calculates farthest point (B) on flight in range of emergency
	 * and finds airports closest do midpoint that are in range of both current location
	 * and B point.
	 * <p>
	 * It uses cropped database of airports in the vicinity of flight path generated
	 * by getClosest() method for optimized performance
	 * 
	 * @param database - cropped database of airports
	 * @return ArrayList of emergency landing
	 * @throws NoAirportInRangeException 
	 */
	private ArrayList<Airport> algorithmC (HashSet<Airport> database) throws NoAirportInRangeException
	{
		/* TODO: Fix this shit */
		final int STEP_KM = 1; //define step in kilometers, for optimization purposes
		
		ArrayList<Airport> nearby = new ArrayList<>();
		nearby.add(start); //add start location as a first emergency lanidng
		
		// define points
		Point startP = start.getLocation();
		Point endP = destination.getLocation();		
		Point current = new Point(startP);

		
		//calculate steps and increments
		double steps = this.distance/STEP_KM;
		double dx = endP.x - startP.x;
		double dy = endP.y - startP.y;
		double incX = dx/steps;
		double incY = dy/steps;
		
		//set current point
		current.clone(startP);
		double nextstep = 1;
		for (double i = 0; i <= steps ; i++)
		{
			//increment location of current position
			current.x += incX;
			current.y += incY;
			nextstep = 1;
			//if lost range to the closest airport
			if (!nearby.get(nearby.size()-1).isInRange(current, rangeOfEmergency))
			{
				
				//calculate end of line on which to check for airports
				nextstep = (rangeOfEmergency)/STEP_KM;
				if ((i + nextstep) > steps)
				{
					nextstep = steps - i;
				}
				Point next = new Point(current);
				next.add(incX*nextstep, incY*nextstep);
				
				//calculate midpoint
				Point mid = new Point((next.x+current.x)/2,(next.y+current.y)/2);
				
				HashSet<Airport> tmp = new HashSet<>();
				//get list of airports that are in range of next and current points
				for (Airport airport : database) {
					if (airport.isInRange(current, rangeOfEmergency) && airport.isInRange(next, rangeOfEmergency)) {
						tmp.add(airport);
					}
				}
				
				//get closest airport to midpoin position and add it to the list of emergency landings
				Airport tmpA = getClosestAirport(mid, tmp);
				if (tmpA == null)
				{
					throw new NoAirportInRangeException();
				} else 
				{
				nearby.add(tmpA);
				tmp.remove(tmpA);	//remove that airport from database, for optimization purposes
				}
			}
			

		}
		if (!nearby.contains(destination))
		{
			nearby.add(destination);
		}
		return nearby;
	}
}
