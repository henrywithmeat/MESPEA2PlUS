package wsc.ecj.spea2;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleBreeder;
import ec.util.Parameter;
import wsc.data.pool.Service;
import wsc.ecj.nsga2.SequenceVectorIndividual;
import wsc.problem.WSCInitializer;

public class Spea2MutationPipeline extends BreedingPipeline {

	private static final long serialVersionUID = 1L;

	@Override
	public Parameter defaultBase() {
		return new Parameter("wscmutationpipeline");
	}

	@Override
	public int numSources() {
		return 1;
	}

	@Override
	public int produce(int min, int max, int start, int subpopulation,
			Individual[] inds, EvolutionState state, int thread) {

		int n = sources[0].produce(min, max, start, subpopulation, inds, state, thread);

        if (!(sources[0] instanceof BreedingPipeline)) {
            for(int q=start;q<n+start;q++)
                inds[q] = (Individual)(inds[q].clone());
        }

        if (!(inds[start] instanceof wsc.ecj.nsga2.SequenceVectorIndividual))
            // uh oh, wrong kind of individual
            state.output.fatal("WSCMutationPipeline didn't get a SequenceVectorIndividual. The offending individual is in subpopulation "
            + subpopulation + " and it's:" + inds[start]);

        WSCInitializer init = (WSCInitializer) state.initializer;
		int archiveSize = ((SimpleBreeder)(state.breeder)).numElites(state, subpopulation);
        // Perform mutation
        for(int q=start;q<n+start;q++) {
        	wsc.ecj.nsga2.SequenceVectorIndividual tree = (SequenceVectorIndividual)inds[q];
			wsc.ecj.nsga2.SequenceVectorIndividual temp = tree.clone();
        	int indexA = WSCInitializer.random.nextInt(tree.genome.length);
        	int indexB = WSCInitializer.random.nextInt(tree.genome.length);
        	swapServices(temp.genome, indexA, indexB);
			inds[q-archiveSize] = temp;
            inds[q-archiveSize].evaluated=false;
        }
        return n;
	}

	private void swapServices(Service[] genome, int indexA, int indexB) {
		Service temp = genome[indexA];
		genome[indexA] = genome[indexB];
		genome[indexB] = temp;
	}

}
