package wsc.analyze.pojo;

import wsc.ecj.nsga2.SequenceVectorIndividual;

public class OutData {
    //归一化的qos
    private SequenceVectorIndividual normalizedQos;
    //未处理的qos
    private SequenceVectorIndividual Qos;
    //第几代out
    private int generation;
    //种群id
    private int populationId;
    //计算开始时间
    private String startTime;
    //计算结束时间
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
