Emergency Landings
===========================================================

Created by Igor Boczkaja

Overwiev
-----------------------------------------------------------
This program calculates list of airports: departure and destination one, as well as emergency landings and flying times past points closest to those landings

Usage
-----------------------------------------------------------

	EmergencyLandings.jar [source] [dest] [range] [speed] [time] [algorithm]
		source - source IATA code
		dest - destination IATA code
		range - maximum distance in km form flight's path to emergency airport
		speed - average speed of flight in km/h
		time - departure time in HH:MM:SS format
		algorithm - OPTIONAL - computation algorithm:
			1 - optimal (DEFAULT)
			2 - safe (SLOW!)
			3 - minimal

Output
-----------------------------------------------------------
Each of output lines contains the following

	[IATA code] [city] [country] [longitude] [latitude] [hour of passing]
	
	