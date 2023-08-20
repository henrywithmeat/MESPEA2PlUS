package wsc.analyze.pojo;

import wsc.ecj.nsga2.SequenceVectorIndividual;

public class OutData {
    private SequenceVectorIndividual normalizedQos;
    private SequenceVectorIndividual Qos;
    private int generation;
    private int populationId;
    private String startTime;
    private String endTime;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public SequenceVectorIndividual getNormalizedQos() {
        return normalizedQos;
    }

    public void setNormalizedQos(SequenceVectorIndividual normalizedQos) {
        this.normalizedQos = normalizedQos;
    }

    public SequenceVectorIndividual getQos() {
        return Qos;
    }

    public void setQos(SequenceVectorIndividual qos) {
        Qos = qos;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public int getPopulationId() {
        return populationId;
    }

    public void setPopulationId(int populationId) {
        this.populationId = populationId;
    }
}
