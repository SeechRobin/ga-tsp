import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
/**
 *Robin Mukaganise MKNROB003
 *Chromosome Class
 */
class Chromosome
{
  /**
   * The list of cities, which are the genes of this
   * chromosome.
   */
  protected int [] cityList;

  /**
   * The cost of following the cityList order of this
   * chromosome.
   */
  protected double cost;
  /**
   * The Chromosome's fitness
   */
  protected double fitness = 0;
   /**
    *  rate of which mutation takes place
    */
  protected double muatationRate = 0.20;
    
  public static int numberOfCities = 0;
    
  public static boolean status = false;
    
  Chromosome(int size)
  {
      cityList = new int[size];
      for(int i=0; i<size;i++){
          cityList[i] = 999;
      }
  }
  /**
   * @param cities The order that this chromosome would 
   * visit the cities.
   */

  Chromosome(City [] cities) 
  {
      
      numberOfCities = cities.length;
      cityList = new int [numberOfCities];
      List genetic = new ArrayList();
      for (int i=0; i<numberOfCities; i++)
      {
          cityList[i] = i;
      }
      
      cityList = shuffleArray(cityList);
      
      
      
  }

  /**
   * Calculate the cost of the specified list of cities.
   * 
   * @param cities A list of cities.
   */
  void calculateCost(City [] cities) {
    cost=0;
    for ( int i=0;i<cityList.length-1;i++ ) {
      double dist = cities[cityList[i]].proximity(cities[cityList[i+1]]);
      cost += dist;
    }
  }

  /**
   * Get the cost for this chromosome. This is the
   * amount of distance that must be traveled.
   */
  double getCost() {
    return cost;
  }

  /**
   * @param i The city you want.
   * @return The ith city.
   */
  int getCity(int i) {
    return cityList[i];
  }

  /**
   * Set the order of cities that this chromosome
   * would visit.
   * 
   * @param list A list of cities.
   */
  void setCities(int [] list) {
    for ( int i=0;i<cityList.length;i++ ) {
      cityList[i] = list[i];
    }
  }

  /**
   * Set the index'th city in the city list.
   * 
   * @param index The city index to change
   * @param value The city number to place into the index.
   */
  void setCity(int index, int value) {
    cityList[index] = value;
  }

  public double getFitness()
  {
      if(fitness == 0){
          fitness = 1/(double)getCost();
      }
      return fitness;
        
  }
    
  public int [] getSequence()
  {
      return cityList;
  }
    
  public int size()
  {
      return cityList.length;
  }
    
 /**
  * Implementing Fisher–Yates shuffle
  * To generate unique genetic material
  * @param ar list of the genetic sequence
  */
  public static int [] shuffleArray(int[] ar)
  {
     Random rnd = new Random();
     for (int i = ar.length - 1; i > 0; i--)
     {
        int index = rnd.nextInt(i + 1);
        // Simple swap
        int a = ar[index];
        ar[index] = ar[i];
        ar[i] = a;
     }
      return ar;
  }

  /**
   * Sort the chromosomes by their cost.
   * 
   * @param chromosomes An array of chromosomes to sort.
   * @param num How much of the chromosome list to sort.
   */

  public static void sortChromosomes(Chromosome chromosomes[],int num) {
    Chromosome ctemp;
    boolean swapped = true;
    while ( swapped ) {
      swapped = false;
      for ( int i=0;i<num-1;i++ ) {
        if ( chromosomes[i].getCost() > chromosomes[i+1].getCost() ) {
          ctemp = chromosomes[i];
          chromosomes[i] = chromosomes[i+1];
          chromosomes[i+1] = ctemp;
          swapped = true;
        }
      }
    }
  }
  /**
   * toString
   */
  public String toString(){
     String gene = "|";
     for(int i=0; i<cityList.length; i++)
     {
        gene += getCity(i) + "|";
     }
      return gene;
    }
  
}
