package wsc.analyze.pojo;

import wsc.ecj.nsga2.SequenceVectorIndividual;

public class FrontData {
    //归一化的qos
    private SequenceVectorIndividual normalizedQos;
    //未归一化的qos
    private SequenceVectorIndividual Qos;
    //第几代的paretofront
    private int generation;

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


}
