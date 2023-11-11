package wsc.ecj.nsga2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Parameter;
import ec.vector.VectorIndividual;
import wsc.data.pool.InitialWSCPool;
import wsc.data.pool.Service;
import wsc.graph.ServiceGraph;
import wsc.problem.WSCInitializer;

public class SequenceVectorIndividual extends VectorIndividual {

	private static final long serialVersionUID = 1L;

	private double availability;
	private double reliability;
	private double time;
	private double cost;
	private double matchingType;
	private double semanticDistance;
	private double TchebycheffScore;

	private int splitPosition;

	public Service[] genome; // before encoding
	public List<Integer> serQueue = new ArrayList<Integer>(); // after encoding

	private String strRepresentation; // a string of graph-based representation


	@Override
	public Parameter defaultBase() {
		return new Parameter("sequencevectorindividual");
	}

	@Override
	/**
	 * Initializes the individual.
	 */
	public void reset(EvolutionState state, int thread) {
		WSCInitializer init = (WSCInitializer) state.initializer;
		List<Service> relevantList = WSCInitializer.initialWSCPool.getServiceSequence();
		Collections.shuffle(relevantList, WSCInitializer.random);

		genome = new Service[relevantList.size()];
		relevantList.toArray(genome);
		this.evaluated = false;
	}

	@Override
	public boolean equals(Object ind) {
		boolean result = false;

		if (ind != null && ind instanceof SequenceVectorIndividual) {
			result = true;
			SequenceVectorIndividual other = (SequenceVectorIndividual) ind;

			for (int i = 0; i < genome.length; i++) {
				if (!genome[i].equals(other.genome[i])) {
					result = false;
					break;
				}

			}
		}
		return result;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(genome);
	}

	@Override
	public String toString() {
		return Arrays.toString(genome);
	}

	@Override
	public SequenceVectorIndividual clone() {
		SequenceVectorIndividual g = new SequenceVectorIndividual();
		g.species = this.species;
		if (this.fitness == null)
			g.fitness = (Fitness) g.species.f_prototype.clone();
		else
			g.fitness = (Fitness) this.fitness.clone();
		if (genome != null) {
			// Shallow cloning is fine in this approach
			g.genome = genome.clone();
		}
		return g;
	}

	public void calculateSequenceFitness(SequenceVectorIndividual ind2, WSCInitializer init, EvolutionState state) {
		List<Integer> fullSerQueue = new ArrayList<Integer>();
		List<Integer> usedSerQueue = new ArrayList<Integer>();		
		
		// use ind2 to generate graph
		InitialWSCPool.getServiceCandidates().clear();
		List<Service> serviceCandidates = new ArrayList<Service>(Arrays.asList(ind2.genome));
		InitialWSCPool.setServiceCandidates(serviceCandidates);

		ServiceGraph graph = init.graGenerator.generateGraph(fullSerQueue);
		// create a queue of services according to breath first search
		List<Integer> usedQueue = init.graGenerator.usedQueueofLayers("startNode", graph, usedSerQueue);
		// set the position of the split position of the queue
		ind2.setSplitPosition(usedQueue.size()); // index from 0 to (splitposition-1)

		// add unused queue to form a complete a vector-based individual
		List<Integer> serQueue = init.graGenerator.completeSerQueueIndi(usedQueue, fullSerQueue);
		// Set serQueue to individual(do I need deep clone ?)
		if (ind2.serQueue != null) {
			ind2.serQueue.clear();
		}
		ind2.serQueue.addAll(serQueue);

		ind2.setStrRepresentation(graph.toString());
		// evaluate updated updated_graph
		init.eval.aggregationAttribute(ind2, graph);

		((MultiObjectiveFitness) ind2.fitness).setObjectives(state, init.eval.calculateFitness(ind2));
		ind2.evaluated = true;
	}
	public double[] getDoubleListQos(){
		double[] list = new double[6];
		list[0] = availability;
		list[1] = reliability;
		list[2] = time;
		list[3] = cost;
		list[4] = matchingType;
		list[5] = semanticDistance;
		return list;
	}
	public void setAvailability(double availability) {
		this.availability = availability;
	}

	public void setReliability(double reliability) {
		this.reliability = reliability;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getAvailability() {
		return availability;
	}

	public double getReliability() {
		return reliability;
	}

	public double getTime() {
		return time;
	}

	public double getCost() {
		return cost;
	}

	public double getMatchingType() {
		return matchingType;
	}

	public void setMatchingType(double matchingType) {
		this.matchingType = matchingType;
	}

	public double getSemanticDistance() {
		return semanticDistance;
	}

	public void setSemanticDistance(double semanticDistance) {
		this.semanticDistance = semanticDistance;
	}

	public String getStrRepresentation() {
		return strRepresentation;
	}

	public void setStrRepresentation(String strRepresentation) {
		this.strRepresentation = strRepresentation;
	}

	public int getSplitPosition() {
		return splitPosition;
	}

	public void setSplitPosition(int splitPosition) {
		this.splitPosition = splitPosition;
	}

	public double getTchebycheffScore() {
		return TchebycheffScore;
	}

	public void setTchebycheffScore(double tchebycheffScore) {
		TchebycheffScore = tchebycheffScore;
	}

}
