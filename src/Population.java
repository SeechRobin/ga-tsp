class Population
{
    Chromosome [] chromosomes;
    
    public Population(int populationSize)
    {
        chromosomes = new Chromosome[populationSize];
    }
    
    public Population(int populationSize, City [] cities)
    {
        chromosomes = new Chromosome[populationSize];
        
        for(int i=0; i<populationSize; i++)
        {
            addNewChromosome(i,cities);
        }
    }
    
    void addNewChromosome(int index, City [] cities)
    {
        chromosomes[index] = new Chromosome(cities);
    }
    
    void add(int index, Chromosome chromosome)
    {
        chromosomes[index] = chromosome;
    }
    
    public Chromosome getChromosome(int index)
    {
        return chromosomes[index];
    }
    
    Chromosome [] getPopulation()
    {
        return chromosomes;
    }
    
    public Chromosome getFittest()
    {
        Chromosome fittest = chromosomes[0];
        
        //looping through all individuals to find the fittest
        for (int i=1; i<getPopulationSize(); i++)
        {
            if(fittest.getFitness() <= chromosomes[i].getFitness()){
                fittest = chromosomes[i];
            }
        }
        return fittest;
        
    }
    
    int getPopulationSize()
    {
        return chromosomes.length;
    }
}