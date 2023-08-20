package wsc.problem;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import wsc.ecj.nsga2.SequenceVectorIndividual;

public class WSCProblem extends Problem implements SimpleProblemForm {
	private static final long serialVersionUID = 1L;

	@Override
	public void evaluate(final EvolutionState state, final Individual ind, final int subpopulation,
			final int threadnum) {
		if (ind.evaluated)
			return;

		if (!(ind instanceof SequenceVectorIndividual))
			state.output.fatal("Whoa!  It's not a SequenceVectorIndividual!!!", null);

		WSCInitializer init = (WSCInitializer) state.initializer;

		SequenceVectorIndividual individual = (SequenceVectorIndividual) ind;

		individual.calculateSequenceFitness(individual, init, state);
	}

}