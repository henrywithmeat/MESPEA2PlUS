package wsc.ecj.spea2;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import wsc.data.pool.Service;
import wsc.problem.WSCInitializer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import wsc.ecj.nsga2.SequenceVectorIndividual;
public class WSCSpea2Pipiline extends BreedingPipeline {
    public final double CROSSOVER_PROB = 0.8;
    public final double MUTATION_PROB = 0.1;

    @Override
    public int numSources() {
        return 500;
    }

    @Override
    public Parameter defaultBase() {
        return new Parameter("wscspea2pipeline");
    }

    @Override
    public int produce(int min, int max, int start, int subpopulation, Individual[] inds, EvolutionState state, int thread) {
        int n = sources[0].produce(min, max, start, subpopulation, inds, state, thread);

        if (!(sources[0] instanceof BreedingPipeline)) {
            for(int q=start;q<n+start;q++)
                inds[q] = (Individual)(inds[q].clone());
        }

        if (!(inds[start] instanceof SequenceVectorIndividual))
            // uh oh, wrong kind of individual
            state.output.fatal("WSCMutationPipeline didn't get a SequenceVectorIndividual. The offending individual is in subpopulation "
                    + subpopulation + " and it's:" + inds[start]);

        WSCInitializer init = (WSCInitializer) state.initializer;

        //Binary tournament selection
        LinkedList<SequenceVectorIndividual> MatePool = new LinkedList<>();
        for(int i = 0;i < n;i++) {
            int randomA = WSCInitializer.random.nextInt(n) + start;
            int randomB = WSCInitializer.random.nextInt(n) + start;
            while (randomA == randomB) {
                randomB = WSCInitializer.random.nextInt(n) + start;
            }
            SequenceVectorIndividual rA = (SequenceVectorIndividual) inds[randomA];
            SequenceVectorIndividual rB = (SequenceVectorIndividual) inds[randomB];
            if(((SPEA2MultiObjectiveFitness) rA.fitness).fitness < ((SPEA2MultiObjectiveFitness) rB.fitness).fitness) {
                MatePool.add(rA);
            }else{
                MatePool.add(rB);
            }
        }
        //Perform Crossover
        for(int i = 0;i < CROSSOVER_PROB*n;i++){
            int randomA = WSCInitializer.random.nextInt(n);
            int randomB = WSCInitializer.random.nextInt(n);
            while (randomA == randomB) {
                randomB = WSCInitializer.random.nextInt(n) + start;
            }
            SequenceVectorIndividual rA = MatePool.get(randomA);
            SequenceVectorIndividual rB = MatePool.get(randomB);
            // Select two random index numbers as the boundaries for the crossover section
            int indexA = WSCInitializer.random.nextInt(rA.genome.length);
            int indexB = WSCInitializer.random.nextInt(rA.genome.length);

            // Make sure they are different
            while (indexA == indexB)
                indexB = WSCInitializer.random.nextInt(rA.genome.length);

            // Determine which boundary they are
            int minBoundary = Math.min(indexA, indexB);
            int maxBoundary = Math.max(indexA, indexB);

            // Create new genomes
            Service[] newGenome1 = new Service[rA.genome.length];
            Service[] newGenome2 = new Service[rB.genome.length];

            // Swap crossover sections between candidates, keeping track of which services are in each section
            Set<Service> newSection1 = new HashSet<Service>();
            Set<Service> newSection2 = new HashSet<Service>();

            for (int index = minBoundary; index <= maxBoundary; index++) {
                // Copy section from parent 1 to genome 2
                newGenome2[index] = rA.genome[index];
                newSection2.add(rA.genome[index]);

                // Copy section from parent 2 to genome 1
                newGenome1[index] = rB.genome[index];
                newSection1.add(rB.genome[index]);
            }

            // Now fill the remainder of the new genomes, making sure not to duplicate any services
            fillNewGenome(rB, newGenome2, newSection2, minBoundary, maxBoundary);
            fillNewGenome(rA, newGenome1, newSection1, minBoundary, maxBoundary);

            // Replace the old genomes with the new ones
            rA.genome = newGenome1;
            rB.genome = newGenome2;


        }

        // Perform mutation
        for(int q=start;q<n+start;q++) {
            SequenceVectorIndividual tree = (SequenceVectorIndividual)inds[q];

            int indexA = WSCInitializer.random.nextInt(tree.genome.length);
            int indexB = WSCInitializer.random.nextInt(tree.genome.length);
            swapServices(tree.genome, indexA, indexB);
            tree.evaluated=false;
        }
        return n;
    }

    private void fillNewGenome(SequenceVectorIndividual parent, Service[] newGenome, Set<Service> newSection, int minBoundary, int maxBoundary) {
        int genomeIndex = getInitialIndex(minBoundary, maxBoundary);

        for (int i = 0; i < parent.genome.length; i++) {
            if (genomeIndex >= newGenome.length + 1)
                break;
            // Check that the service has not been already included, and add it
            if (!newSection.contains(parent.genome[i])) {
                newGenome[genomeIndex] = parent.genome[i];
                // Increment genome index
                genomeIndex = incrementIndex(genomeIndex, minBoundary, maxBoundary);
            }
        }
    }
    private int getInitialIndex(int minBoundary, int maxBoundary) {
        if (minBoundary == 0)
            return maxBoundary + 1;
        else
            return 0;
    }

    private int incrementIndex(int currentIndex, int minBoundary, int maxBoundary) {
        if (currentIndex + 1 >= minBoundary && currentIndex + 1 <= maxBoundary)
            return maxBoundary + 1;
        else
            return currentIndex + 1;
    }

    //运用于mutation交换两个service
    private void swapServices(Service[] genome, int indexA, int indexB) {
        Service temp = genome[indexA];
        genome[indexA] = genome[indexB];
        genome[indexB] = temp;
    }
}
