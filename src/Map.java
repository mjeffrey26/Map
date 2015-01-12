import java.io.*;
import java.util.*;

public class Map
{

// Initiates map, then sorts through to find a path
	public static void main(String[] args)
	{
		Map map = new Map("cityFile", "flightFile");
		Scanner requests = null;
		try
		{
			requests = new Scanner(new File("requestFile"));
		}
		catch(FileNotFoundException e)
		{
			System.out.println(e);
			System.exit(0);
		}
		while(requests.hasNext())
		{
			map.resetCities();
			String requestInput = requests.nextLine();
			StringTokenizer token = new StringTokenizer(requestInput, ",", false);
			String cityName = token.nextToken().trim();
			City from = map.findCity(cityName);
			String toCityName = token.nextToken().trim();
			City to = map.findCity(toCityName);
			
			System.out.println("Request is to fly from " + cityName + " to " + toCityName + ".");
			if (from == null) 
				System.out.println("Sorry. We do not serve " + cityName + ".");
			else if (to == null)
				System.out.println("Sorry. We do not serve " + toCityName + ".");
			else
			{
				Stack<City> flightPattern = getPath(map, from, to);
				if (!flightPattern.isEmpty()){
					printPath(flightPattern);
				}
				else System.out.println("HPAir does not fly from " + from.name + " to " + to.name + ".");
			}
			System.out.println();
		}
	}
	
// Like "isPath" in book, but returns the stack at end of loop (if it is empty, then there is no path)
	private static Stack<City> getPath(Map map, City from, City to)
	{
		Stack<City> locations = new Stack<City>();
		City current = from;
		current.flag(true);
		locations.push(current);
		while(!current.name.equals(to.name) && !locations.isEmpty())
		{
			City flyTo = (current.hasMoreFlights())? map.findCity(current.nextFlight().name):null;
			if (flyTo == null)
			{
				locations.pop();
				if(!locations.isEmpty()) current = locations.peek();
			}
			else if(!flyTo.visited())
			{
				flyTo.flag(true);
				locations.push(flyTo);
				current = flyTo;
			}
		}
		return locations;
	}
	
	
// Goes through given stack and prints path starting from bottom of stack
	private static void printPath(Stack<City> locations)
	{
		Stack<City> temp = new Stack<City>();
		while(!locations.isEmpty())
		{
			temp.push(locations.pop());
		}
		while(!temp.isEmpty())
		{
			City from = temp.pop();
			City to = (temp.isEmpty())? null:temp.peek();
			if(to != null)
				System.out.println("Fly from " + from.name + " to " + to.name + ".");
			
		}
	}
	
	
/**	
 	* Constructor for Map
	* @ param1, param2: Files to read from
*/
	public Map(String cityFile, String flightFile)
	{
		cityList = new ArrayList<City>();
		numCities = 0;
		Scanner cityFileScanner = null, flightFileScanner = null;
		try
		{
			cityFileScanner = new Scanner(new File(cityFile));
			flightFileScanner = new Scanner(new File(flightFile));
			
		}
		catch(FileNotFoundException e)
		{
			System.out.println(e);
			System.exit(0);
		}
		while(cityFileScanner.hasNext())
		{
			cityList.add(new City(cityFileScanner.nextLine()));
			numCities++;
		}
		while(flightFileScanner.hasNext())
		{
			String input = flightFileScanner.nextLine();
			StringTokenizer token = new StringTokenizer(input, ",", false);
			String cityName = token.nextToken();
			City from = findCity(cityName.trim());
			City to = (token.hasMoreTokens())? new City(token.nextToken().trim()) : null;
			from.setFlyTo(to);
		}
		resetCities();
	}
	
	
// **********Create more efficient sorting Method***********
	private City findCity(String name)
	{
		for(int i = 0; i<numCities; i++)
		{
			if (cityList.get(i).name.equalsIgnoreCase(name)) return cityList.get(i);
		}
		return null;
	}
	
// Resets flag and iterator on all cities
	public void resetCities(){
		for(int i = 0; i<numCities; i++){
			cityList.get(i).reset();
		}
	}
	
// Instance variables for Map
	private ArrayList<City> cityList;
	private int numCities;
	
	
class City
{
//Instance variables for City
	private String name;
	private boolean flag;
	private LinkedList<City> flyTo;
	private ListIterator<City> itr;
	
	
// Constructor for City
	public City(String name)
	{
		this.name = name;
		flag = false;
		flyTo = new LinkedList<City>();
		itr = flyTo.listIterator();
	}
	
// Resets flag and iterator on city
	public void reset(){
		itr = flyTo.listIterator(0);
		flag = false;
	}
	
// Sets a City as a possible fly-to of the current instance of City
	public void setFlyTo(City city)
	{
		flyTo.add(city);
	}
	
// Boolean to say if the City has been visited
	public void flag(boolean visited)
	{
		flag = visited;
	}
	
// Returns if the city has been visited
	public boolean visited()
	{
		return flag;
	}
	
// Returns the "next" possible City that the current instance can fly to
	public City nextFlight()
	{
		if (itr.hasNext()) return itr.next();
		return null;
	}
	
// Returns boolean if there are more cities that the current instance can fly to
	public boolean hasMoreFlights(){
		return itr.hasNext();
	}
}

}
