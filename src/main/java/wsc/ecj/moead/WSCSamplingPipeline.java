package wsc.ecj.moead;

import java.util.List;

import com.google.common.collect.Iterators;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import wsc.data.pool.Service;
import wsc.ecj.nsga2.SequenceVectorIndividual;
import wsc.nhbsa.NHBSA;
import wsc.problem.WSCInitializer;

import ec.Subpopulation;
import ec.multiobjective.MultiObjectiveFitness;

public class WSCSamplingPipeline extends BreedingPipeline {

	private static final long serialVersionUID = 1L;

	@Override
	public Parameter defaultBase() {
		return new Parameter("wscsamplingpipeline");
	}

	@Override
	public int numSources() {
		return 1;
	}

	public int produce(int min, int max, int start, int subpopulation, Individual[] inds, EvolutionState state,
			int thread) {

		int n = sources[0].produce(min, max, start, subpopulation, inds, state, thread);

		int subproblem  = -1;

		if (!(sources[0] instanceof BreedingPipeline)) {
			for (int q = start; q < n + start; q++) {

				subproblem = Iterators.indexOf(Iterators.forArray(state.population.subpops[0].individuals),
						x -> x.equals(inds[start]));

				inds[q] = (Individual) (inds[q].clone());
			}
		}

		if (!(inds[start] instanceof SequenceVectorIndividual))
			// uh oh, wrong kind of individual
			state.output.fatal(
					"WSCSamplingPipeline didn't get a SequenceVectorIndividual. The offending individual is in subpopulation "
							+ subpopulation + " and it's:" + inds[start]);

		SequenceVectorIndividual bestNeighbour = null;
		for (int q = start; q < n + start; q++) {
			bestNeighbour = (SequenceVectorIndividual) inds[q];
		}

		WSCInitializer init = (WSCInitializer) state.initializer;

		// SequenceVectorIndividual bestNeighbour = (SequenceVectorIndividual)
		// inds[start].clone();

		double bestScore = init.calculateTchebycheffScore(bestNeighbour, subproblem);
		bestNeighbour.setTchebycheffScore(bestScore);

		if (WSCInitializer.pop_updated != null) {
			WSCInitializer.pop_updated.clear();
		}

		// learn NHM from subproblem, but it is penalized on based the cosine similarity
		WSCInitializer.pop_updated = sampleNeighbors(init, subproblem, state);

		for (int i = 0; i < WSCInitializer.pop_updated.size(); i++) {
			SequenceVectorIndividual neighbour = bestNeighbour.clone();
			updatedIndi(neighbour.genome, WSCInitializer.pop_updated.get(i));

			// Calculate fitness of neighbor
			neighbour.calculateSequenceFitness(neighbour, init, state);

			// Calculate tchebycheffScore
			double score = init.calculateTchebycheffScore(neighbour, subproblem);
			bestNeighbour.setTchebycheffScore(score);
			if (score < bestScore) {
				bestScore = score;
				bestNeighbour = neighbour;
			}

		}

		inds[start] = bestNeighbour;
		inds[start].evaluated = false;

		return n;
	}

	private List<int[]> sampleNeighbors(WSCInitializer init, int subproblem, EvolutionState state) {
		// Get population
		Subpopulation pop = state.population.subpops[0];
		// System.out.println("learn a NHM from a pop size: " + pop.individuals.length);
		NHBSA nhbsa = new NHBSA(WSCInitializer.numNeighbours, WSCInitializer.dimension_size);

		int[][] m_generation = new int[WSCInitializer.numNeighbours][WSCInitializer.dimension_size];

		double consinSimilarity[] = new double[WSCInitializer.numNeighbours];

		for (int m = 0; m < WSCInitializer.numNeighbours; m++) {

			MultiObjectiveFitness fit = (MultiObjectiveFitness) pop.individuals[m].fitness;
			//m=目标放入
			// //
			//
			//
			//
			//
			//
			//
			double[] array = { fit.getObjective(0), fit.getObjective(1),fit.getObjective(2) };
			consinSimilarity[m] = cosineSimilarity(array, init.weights[subproblem]);

			for (int n = 0; n < WSCInitializer.dimension_size; n++) {
				m_generation[m][n] = ((SequenceVectorIndividual) (pop.individuals[m])).serQueue.get(n);
			}
		}

		nhbsa.setM_pop(m_generation);
		nhbsa.setM_L(WSCInitializer.dimension_size);
		nhbsa.setM_N(WSCInitializer.numNeighbours);
		nhbsa.setNormalizedConsineSIM(consinSimilarity);

		// Sample numLocalSearchTries number of neighbors
		return nhbsa.sampling4NHBSA(WSCInitializer.numLocalSearchTries, WSCInitializer.random);
	}

	public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
		double dotProduct = 0.0;
		double normA = 0.0;
		double normB = 0.0;
		for (int i = 0; i < vectorA.length; i++) {
			dotProduct += vectorA[i] * vectorB[i];
			normA += Math.pow(vectorA[i], 2);
			normB += Math.pow(vectorB[i], 2);
		}
		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}

	private void updatedIndi(Service[] genome, int[] updateIndi) {
		for (int n = 0; n < updateIndi.length; n++) {
			genome[n] = WSCInitializer.Index2ServiceMap.get(updateIndi[n]);
		}
	}

}
