import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.io.*;

/**
 * This class implements the Traveling Salesman problem
 * as a Java applet.
 * Robin Mukanganise MKNROB003
 */

public class TravelingSalesman extends
	Applet
  implements Runnable {
      
    protected Population pop = null;
  /**
   * How many cities to use.
   */
	protected int cityCount;

  /**
   * How many chromosomes to use.
   */
  protected int populationSize;

  /**
   * The part of the population eligable for mateing.
   */
  protected int matingPopulationSize;

  /**
   * The part of the population selected for mating.
   */
  protected int selectedParents;

  /**
   * The current generation
   */
  protected int generation;

  /**
   * The background worker thread.
   */
  protected Thread worker = null;

  /**
   * Is the thread started.
   */
  protected boolean started = false;

  /**
   * The list of cities.
   */
  protected City [] cities;

  /**
   * The list of chromosomes.
   */
  protected Chromosome [] chromosomes;

  /**
   * The Start button.
   */
  private Button ctrlStart;

  /**
   * The TextField that holds the number of cities.
   */
  private TextField ctrlCities;

  /**
   * The TextField for the population size.
   */
  private TextField ctrlPopulationSize;

  /**
   * Holds the buttons and other controls, forms a strip across
   * the bottom of the applet.
   */
  private Panel ctrlButtons;

  /**
   * The current status, which is displayed just above the controls.
   */
  private String status = "";

  

  public void init()
  {
    setLayout(new BorderLayout());
    
    // setup the controls
    ctrlButtons = new Panel();
    ctrlStart = new Button("Start");
    ctrlButtons.add(ctrlStart);
    ctrlButtons.add(new Label("# Cities:"));
    ctrlButtons.add(ctrlCities = new TextField(5));
    ctrlButtons.add(new Label("Population Size:"));
    ctrlButtons.add(ctrlPopulationSize = new TextField(5));
    this.add(ctrlButtons, BorderLayout.SOUTH);

    // set the default values
    ctrlPopulationSize.setText("1000");
    ctrlCities.setText("200");

    // add an action listener for the button
    ctrlStart.addActionListener(new ActionListener()
    {
      
      public void actionPerformed(ActionEvent arg0)
      {
          System.out.println("Press");
        startThread();
      }
    });

    started = false;
    update();
  }

  /**
   * Start the background thread.
   */
  public void startThread() {

	  try
	  {
		  cityCount = Integer.parseInt(ctrlCities.getText());
	  }
	  catch(NumberFormatException e)
	  {
		  cityCount = 50;
	  }

	  try
	  {
		  populationSize = Integer.parseInt(ctrlPopulationSize.getText());
	  }
	  catch(NumberFormatException e)
	  {
		  populationSize = 1000;
	  }

	  FontMetrics fm = getGraphics().getFontMetrics();
	  int bottom = ctrlButtons.getBounds().y - fm.getHeight()-2;

      // create a random list of cities
      int x = 0;
      int y = 0;

	    cities = new City[cityCount];
	    for ( int i=0;i<cityCount;i++ )
        {
            x = (int)(Math.random()*(getBounds().width-10));
            y = (int)(Math.random()*(bottom-10));
	        cities[i] = new City(x,y);
            
            //System.out.println(x+","+y);
       
	    }
      // create the initial population of chromosomes
      pop  = new Population(populationSize, cities);
      
     // start up the background thread

    started = true;

    generation = 0;

    if ( worker != null )
        worker = null;
    worker = new Thread(this);
    worker.setPriority(Thread.MIN_PRIORITY);
    worker.start();
  }

  /**
   * Update the display
   */

  public void update()
  {
    Image img = createImage(getBounds().width, getBounds().height);
    Graphics g = img.getGraphics();
    FontMetrics fm = g.getFontMetrics();

    int width = getBounds().width;
    int bottom = ctrlButtons.getBounds().y - fm.getHeight()-2;

    g.setColor(Color.black);
    g.fillRect(0, 0, width, bottom);

    if( started && (cities != null) )
    {
    	    g.setColor(Color.green);
    	    for ( int i=0;i<cityCount;i++ ) {
    	      int xpos = cities[i].getx();
    	      int ypos = cities[i].gety();
    	      g.fillOval(xpos-5,ypos-5,10,10);
    	    }

    	    g.setColor(Color.white);
    	    for ( int i=0;i<cityCount;i++ ) {
    	      //int icity = chromosomes[0].getCity(i);
                int icity = pop.getPopulation()[0].getCity(i);
    	      if ( i!=0 ) {
    	        //int last = chromosomes[0].getCity(i-1);
                  int last = pop.getPopulation()[0].getCity(i-1);
    	        g.drawLine(
    	                  cities[icity].getx(),
    	                  cities[icity].gety(),
    	                  cities[last].getx(),
    	                  cities[last].gety());
    	      }
    	    }

    }


    g.drawString(status, 0, bottom);

    getGraphics().drawImage(img, 0, 0, this);
  }

  /**
   * Update the status.
   *
   * @param status The status.
   */
  public void setStatus(String status)
  {
    this.status = status;
  }

  /**
   * The main loop for the background thread.
   */

  public void run() {

    double thisCost = 500.0;
    double currentCost = 0.0;

    update();
      

    while(generation<1000)
    {

      generation++;
      pop = evolve(pop);
    
      for (int i=0; i<pop.getPopulationSize(); i++)
      {
          pop.getPopulation()[i].calculateCost(cities);
      }
        
      matingPopulationSize = populationSize;
      Chromosome.sortChromosomes(pop.getPopulation(),matingPopulationSize);
        
      double cost = pop.getPopulation()[0].getCost();
      thisCost = cost;

        
      NumberFormat nf = NumberFormat.getInstance();
      nf.setMinimumFractionDigits(2);
      nf.setMinimumFractionDigits(2);

      setStatus("Generation "+generation+" Cost "+(int)thisCost);
      update();
      
      System.out.println(generation + "," + cost);
      
    }
    setStatus("Solution found after "+generation+" generations.");
  }
  /**
   * Tournament Selection
   *
   */
      
  public static Chromosome tournamentSelection(Population pop)
  {
      int tSize = 10; // size of the tournament
      //create a tournament population
      Population tournament = new Population(tSize);
      for(int i=0; i< tSize; i++)
      {
          int random = (int) (Math.random() * pop.getPopulationSize());
          tournament.add(i,pop.getChromosome(random));
                              
      }
      Chromosome chromosome = tournament.getFittest(); //return fittest chromosome
      return chromosome;
      
  }
  /**
   *Crossover: creating a new offspring by ordered crossover
   *
   */
  public static Chromosome crossover(Chromosome parent0, Chromosome parent1)
  {
      //new chromosome offspring
      Chromosome offspring = new  Chromosome(parent0.size());
      List<Integer> tempOffspring = new ArrayList<Integer>();
      tempOffspring =  arrayToList(offspring.getSequence());
      
      int startPos = (int) (Math.random() * parent0.size());
      int endPos = (int) (Math.random() * parent1.size());
    
      
      for (int i = 0; i < offspring.size(); i++)
      {
          if (startPos < endPos && i > startPos && i < endPos)
          {
              tempOffspring.set(i,parent0.getCity(i));
          } // if start position is larger
          else if (startPos > endPos) {
              if (!(i < startPos && i > endPos)) {
                  tempOffspring.set(i, parent0.getCity(i));
              }
          }
      }
     
      // Loop through parent1
      for (int i = 0; i < parent1.size(); i++) {
          // if not in offspring add
          if ((tempOffspring.contains(parent1.getCity(i))) == false)
          {
              // Find spare position
              for (int ii = 0; ii < offspring.size(); ii++) {
                  if (tempOffspring.get(ii) == 999)
                  {
                      tempOffspring.set(ii, parent1.getCity(i));
                      break;
                  }
              }
          }
      }
      
      int [] temp = new int[tempOffspring.size()];
      for(int i=0; i<tempOffspring.size(); i++)
      {
          temp[i] = tempOffspring.get(i);
      }
      offspring.setCities(temp);
      return offspring;
  }
  /**
   * Evolve the population for each generation
   *
   */
  public static Population evolve(Population pop)
  {
      
      Population newPopulation = new Population(pop.getPopulationSize());
      
      for(int i=0; i<newPopulation.getPopulationSize(); i++)
      {
          Chromosome [] parent = new Chromosome[2];
          parent[0] = tournamentSelection(pop);
          parent[1] = tournamentSelection(pop);
          Chromosome offspring = crossover(parent[0],parent[1]);
          newPopulation.add(i, offspring);
      }
      return newPopulation;
  }
  /**
   * Helper method to convert array to a List
   *
   */
  static List arrayToList(final int[] array)
   {
       final List l = new ArrayList(array.length);
       for (int i = 0; i < array.length; i++)
        {
          l.add(array[i]);
        }
       return (l);
  }

  public void paint(Graphics g)
  {
	  update();
  }
}