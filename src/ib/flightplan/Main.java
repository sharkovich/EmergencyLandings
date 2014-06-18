package ib.flightplan;

import ib.flightplan.comp.Airport;
import ib.flightplan.comp.Flight;
import ib.flightplan.comp.Point;
import ib.flightplan.exception.NoAirportInRangeException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.StringTokenizer;

public class Main {

	public static final double DISTANCE_FAC = 111;
	public static final String REGION = "E";
	public static final String DATABASE_FILENAME = "airports.dat";
	public static final int DEF_ALOGIRHM = 1;
	private static Airport start;
	private static Airport destination;
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		int algorithm = DEF_ALOGIRHM;
		
		if (args.length == 6 || args.length == 5)
		{
			if (args.length == 6 && args[5] != null)
			{
				algorithm = Integer.parseInt(args[5]);
			}
			String src = args[0] , dst = args[1];
			double range = 0;
			double speed = 0;
			try {
				range = Double.parseDouble(args[2]);
				speed = Double.parseDouble(args[3]);				
			} catch (NullPointerException | NumberFormatException e)
			{
				System.err.println("\nERROR: Please check your range and speed arguments!\n");
				e.printStackTrace();
			}

			
			//check if start time is proper, convert string to time
			SimpleDateFormat fd = new SimpleDateFormat("HH:mm:ss");
			String startTimeS = args[4];
			Date startTime = null;
			try {
				startTime = fd.parse(startTimeS);
			} catch (ParseException e) {
				System.err.println("\nERROR: Given start time is not valid\n");
				e.printStackTrace();
			}
			
			
			HashSet<Airport> database = new HashSet<>();
			
			if (!executeDbCreation(database, src, dst))
			{
				System.err.println("\nERROR: Given airports do not exist in database.\n");
				throw new Exception();
			} else
			{
				Flight flight = new Flight(start, destination, range, startTime, speed);
				try {
					flight.computateFlight(algorithm, database);
				} catch (NoAirportInRangeException e) {
					// TODO: handle exception
					System.err.println("\nERROR: Could not find airports in range!\n"
							+ "Flight path is not safe!\n"
							+ "Emergency range must be increased for safe flight.\n");
				}
					
			}
				
		} else
		{
			printHelp();
		}

	}
	/**
	 * Loads database of aircraft from CSV file. 
	 * <p>
	 * Ignores airports without IATA code.
	 * <p>
	 * Checks if start and destination airports exist in database.
	 * Returns true if database is created successfully and two airports
	 * are found.
	 * 
	 * @param db - HashSet variable for containing database
	 * @param src - IATA code of start airport
	 * @param dst - IATA code of destination airport
	 * @return true if database is created successfully and two airports
	 * are found.
	 */
	public static boolean executeDbCreation (HashSet<Airport> db, String src, String dst)
	{
		boolean found1 = false;
		boolean found2 = false;
		InputStream in = Main.class.getResourceAsStream("/" + DATABASE_FILENAME);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) 
		{
			String line = br.readLine();
			while(line != null)
			{
			
				StringTokenizer tokenizer = new StringTokenizer(line, ",\"");
				if (tokenizer.countTokens() == 11)
				{
					tokenizer.nextToken();
					tokenizer.nextToken();
					String city = tokenizer.nextToken();
					String country = tokenizer.nextToken();
					String iata = tokenizer.nextToken();
					tokenizer.nextToken();
					double locy = Double.parseDouble(tokenizer.nextToken());
					double locx = Double.parseDouble(tokenizer.nextToken());	
					tokenizer.nextToken();
					tokenizer.nextToken();
					String reg = tokenizer.nextToken();
					
					if (REGION.equalsIgnoreCase(reg))
					{
						Airport a = new Airport(iata, city, country, new Point(locx, locy));
						db.add(a);
						if (iata.equalsIgnoreCase(src))
						{
							start = a;
							found1 = true;
						}
						else if (iata.equalsIgnoreCase(dst))
						{
							destination = a;
							found2 = true;
						}
					}
				}
				line = br.readLine();
			}
			
		} catch (IOException | NullPointerException e) {
			System.err.println("\nERROR: Database file does not exist or corrupted.\n"
					+ "Could not create database.\n");
		}
		return (found1 && found2);
	}
	public static void printHelp()
	{
		System.out.printf("This program finds suitable emergency landings for a flight\n"
				+ "from given source to destination. \n"
				+ "\t Usage: EmergencyLandings.jar [source] [dest] [range] [speed] [time] [algorithm]\n"
				+ "\t\tsource - source IATA code\n"
				+ "\t\tdest - destination IATA code\n"
				+ "\t\trange - maximum distance in km form flight's path to emergency airport\n"
				+ "\t\tspeed - average speed of flight in km/h\n"
				+ "\t\ttime - departure time in HH:MM:SS format\n"
				+ "\t\talgorithm - OPTIONAL - computation algorithm:\n"
				+ "\t\t\t 1 - optimal (DEFAULT)\n"
				+ "\t\t\t 2 - safe (SLOW!)\n"
				+ "\t\t\t 3 - minimal\n"
				+ "\n Program outputs data in following format\n"
				+ "[IATA code] [city] [country] [longitude] [latitude] [hour of passing]\n\n"
				+ "Created by Igor Boczkaja\n");
	}

}

