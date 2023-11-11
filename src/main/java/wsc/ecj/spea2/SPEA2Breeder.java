/*
  Portions copyright 2010 by Sean Luke, Robert Hubley, and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package wsc.ecj.spea2;

import ec.*;
import ec.util.*;
import ec.multiobjective.*;
import ec.simple.*;
import wsc.problem.WSCInitializer;

import java.util.*;

/*
 * SPEA2Breeder.java
 *
 * Created: Sat Oct 16 11:24:43 EDT 2010
 * By: Faisal Abidi and Sean Luke
 * Replaces earlier class by: Robert Hubley, with revisions by Gabriel Balan and Keith Sullivan
 */

/**
 * This subclass of SimpleBreeder overrides the loadElites method to build an archive in the top elites[subpopnum]
 * of each subpopulation.  It computes the sparsity metric, then constructs the archive.
 */

public class SPEA2Breeder extends SimpleBreeder {
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        for (int i = 0; i < state.population.subpops.length; i++)
            if (reduceBy[i] != 0)
                state.output.fatal("SPEA2Breeder does not support population reduction.", base.push(P_REDUCE_BY).push("" + i), null);

        if (sequentialBreeding) // uh oh, haven't tested with this
            state.output.fatal("SPEA2Breeder does not support sequential evaluation.",
                    base.push(P_SEQUENTIAL_BREEDING));

        if (!clonePipelineAndPopulation)
            state.output.fatal("clonePipelineAndPopulation must be true for SPEA2Breeder.");
    }


    protected void loadElites(EvolutionState state, Population newpop) {
        // are our elites small enough?
        for (int x = 0; x < state.population.subpops.length; x++)
            if (numElites(state, x) > state.population.subpops[x].individuals.length)
                state.output.error("The number of elites for subpopulation " + x + " exceeds the actual size of the subpopulation");
        state.output.exitIfErrors();

        // do it
        for (int sub = 0; sub < state.population.subpops.length; sub++) {
            Individual[] newInds = newpop.subpops[sub].individuals;  // The new population after we are done picking the elites                 
            Individual[] oldInds = state.population.subpops[sub].individuals;   // The old population from which to pick elites

            buildArchive(state, oldInds, newInds, numElites(state, sub));
        }

        // optionally force reevaluation
        unmarkElitesEvaluated(state, newpop);
    }

    public double[] calculateDistancesFromIndividual(Individual ind, Individual[] inds) {
        double[] d = new double[inds.length];
        for (int i = 0; i < inds.length; i++)
            d[i] = ((SPEA2MultiObjectiveFitness) ind.fitness).sumSquaredObjectiveDistance((SPEA2MultiObjectiveFitness) inds[i].fitness);
        // now sort
        Arrays.sort(d);
        return d;
    }


    public void buildArchive(EvolutionState state, Individual[] oldInds, Individual[] newInds, int archiveSize) {
        //Individual[] dummy = new Individual[0];

        // step 1: load the archive with the pareto-nondominated front
        ArrayList archive = new ArrayList();
        ArrayList nonFront = new ArrayList();
        MultiObjectiveFitness.partitionIntoParetoFront(oldInds, archive, nonFront);
        GenerationIndsMap.objIndex1 = WSCInitializer.random.nextInt(6);
        GenerationIndsMap.objIndex2 = WSCInitializer.random.nextInt(6);
        // 选择的纬度要求不同
        while (GenerationIndsMap.objIndex2 == GenerationIndsMap.objIndex1) {
            GenerationIndsMap.objIndex2 = WSCInitializer.random.nextInt(6);
        }
        getSortInds(state.generation,archive,GenerationIndsMap.objIndex1,GenerationIndsMap.frontMap,archive.size());
        getSortInds(state.generation,nonFront,GenerationIndsMap.objIndex1,GenerationIndsMap.nonFrontMap,20);
        getSortInds(state.generation,nonFront,GenerationIndsMap.objIndex2,GenerationIndsMap.nonFrontMap2,20);
        int currentArchiveSize = archive.size();

        // step 2: if the archive isn't full, load the remainder with the fittest individuals (using customFitnessMetric) that aren't in the archive yet
        if (currentArchiveSize < archiveSize) {
            Collections.sort(nonFront);  // the fitter individuals will be earlier
            int len = (archiveSize - currentArchiveSize);
            for (int i = 0; i < len; i++) {
                archive.add(nonFront.get(i));
                currentArchiveSize++;
            }
        }


        // step 3: if the archive is OVERFULL, iterate as follows:
        //              step 3a: remove the k-closest individual in the archive

        //SPEA2Evaluator evaluator = ((SPEA2Evaluator)(state.evaluator));
        // Individual[] inds = (Individual[])(archive.toArray(dummy));

        while (currentArchiveSize > archiveSize) {
            Individual closest = (Individual) (archive.get(0));
            int closestIndex = 0;
            double[] closestD = calculateDistancesFromIndividual(closest, oldInds);

            for (int i = 1; i < currentArchiveSize; i++) {
                Individual competitor = (Individual) (archive.get(i));
                double[] competitorD = calculateDistancesFromIndividual(competitor, oldInds);
                for (int k = 0; k < oldInds.length; k++) {
                    if (closestD[k] > competitorD[k]) {
                        closest = competitor;
                        closestD = competitorD;
                        closestIndex = i;
                        break;
                    } else if (closestD[i] < competitorD[i]) {
                        break;
                    }
                }
            }

            // remove him destructively -- put the top guy in his place and remove the top guy.  This is O(1)
            archive.set(closestIndex, archive.get(archive.size() - 1));
            archive.remove(archive.size() - 1);

            currentArchiveSize--;
        }

        // step 4: put clones of the archive in the new individuals
        Object[] obj = archive.toArray();
        for (int i = 0; i < archiveSize; i++)
            newInds[newInds.length - archiveSize + i] = (Individual) (((Individual) obj[i]).clone());
    }

    public void getSortInds(int generation, List<Individual> inds,int sortIndex,Map<Integer,List<Individual>> map,int size) {
        //清除无用的上一代
        if (map.containsKey(generation - 1)) {
            map.remove(generation - 1);
        }
        Collections.sort(inds, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return (int) (((MultiObjectiveFitness) (((Individual) o1).fitness)).getObjective(
                       sortIndex) - (((MultiObjectiveFitness) ((Individual) o2).fitness)).getObjective(sortIndex));
            }
        });
        List<Individual> list = new ArrayList<>();
        for(int i = 0;i < size;i++){
            list.add((Individual) inds.get(i));
        }
        map.put(generation, list);
    }
}
