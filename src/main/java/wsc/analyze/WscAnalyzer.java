package wsc.analyze;

import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;
import wsc.analyze.pojo.FrontData;
import wsc.analyze.pojo.FrontFile;
import wsc.analyze.pojo.OutData;
import wsc.analyze.pojo.OutFile;
import wsc.ecj.nsga2.SequenceVectorIndividual;
import wsc.ecj.nsga2.WSCMultiObjectiveStatistics;
import wsc.problem.WSCInitializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WscAnalyzer {
    // Define the number of parameters
    static final int NUM_PARAMETERS = 6;

    //填充每代的paretoFront
    public void fillOutFile(List<OutFile> outFiles){
        //多个outdata文件
        for(OutFile outFile : outFiles){
            List<List<OutData>> outDataList = outFile.getOutDataList();
            List<FrontFile> frontFiles = outFile.getFrontFiles();
            //文件的所有遍历generation
            for(List<OutData> outDataGroup : outDataList){
                FrontFile frontFile = new FrontFile();
                List<FrontData> frontList = frontFile.getFrontDataList();
               for(OutData outData : outDataGroup){
                   boolean isDominated = false;
                   for(OutData otherOut : outDataGroup){
                       if(!otherOut.equals(outData) && domain(otherOut.getNormalizedQos(),outData.getNormalizedQos())){
                           isDominated = true;
                           break;
                       }
                   }
                   if(!isDominated){
                       FrontData frontData = new FrontData();
                       frontData.setNormalizedQos(outData.getNormalizedQos());
                       frontList.add(frontData);
                   }
               }
                frontFiles.add(frontFile);
            }
        }
    }
    public boolean domain(SequenceVectorIndividual id1,SequenceVectorIndividual id2){
        return id1.getAvailability() <= id2.getAvailability()
                && id1.getCost() <= id2.getCost()
                && id1.getTime() <= id2.getTime()
                && id1.getReliability() <= id2.getCost()
                && id1.getMatchingType() <= id2.getMatchingType()
                && id1.getSemanticDistance() <= id2.getSemanticDistance()
                &&(id1.getAvailability() < id2.getAvailability()
                || id1.getCost() < id2.getCost()
                || id1.getTime() < id2.getTime()
                || id1.getReliability() < id2.getCost()
                || id1.getMatchingType() < id2.getMatchingType()
                || id1.getSemanticDistance() < id2.getSemanticDistance());
    }
    //拿到多个文件的帕累托最优
    public FrontFile getBestAnswer(List<FrontFile> frontFiles){
        List<FrontData> list = new ArrayList<>();
        for(FrontFile frontFile : frontFiles){
            list.addAll(frontFile.getFrontDataList());
        }
        FrontFile pfFile = new FrontFile();
        for(FrontData frontData: list){
            boolean isDominated = false;
            for(FrontData otherFrontData : list){
                if(!frontData.equals(otherFrontData) && domain(otherFrontData.getNormalizedQos(),frontData.getNormalizedQos())){
                    isDominated = true;
                    break;
                }
            }
            if(!isDominated){
                pfFile.getFrontDataList().add(frontData);
            }
        }
        return pfFile;
    }

    //计算IGD
    public void fillIGD(List<FrontFile> frontFiles){
        List<FrontData> pfList = getBestAnswer(frontFiles).getFrontDataList();
        for(FrontFile frontFile : frontFiles){
            frontFile.setIGD(calculateIGD(frontFile.getFrontDataList(),pfList));
        }

    }

    // Euclidean distance calculation
    public double euclideanDistance(double[] point1, double[] point2) {
        double sumSquaredDifferences = 0.0;
        for (int i = 0; i < NUM_PARAMETERS; i++) {
            double diff = point1[i] - point2[i];
            sumSquaredDifferences += diff * diff;
        }
        return Math.sqrt(sumSquaredDifferences);
    }

    // Calculate IGD given the true Pareto front and the solutions obtained by the algorithm
    public double calculateIGD(List<FrontData> algorithmSolutions, List<FrontData> trueParetoFront) {
        double sumDistances = 0.0;
        for (FrontData algorithmSolution : algorithmSolutions) {
            double[] point1 = algorithmSolution.getNormalizedQos().getDoubleListQos();
            double minDistance = Double.MAX_VALUE;
            for (FrontData trueParetoSolution : trueParetoFront) {
                double[] point2 = trueParetoSolution.getNormalizedQos().getDoubleListQos();
                double distance = euclideanDistance(point1, point2);
                minDistance = Math.min(minDistance, distance);
            }
            sumDistances += minDistance;
        }
        return sumDistances / algorithmSolutions.size();
    }

    //计算HV
//    public void caculateHV(List<FrontFile> frontFiles){
//        double[] referencePoint = {1.0,1.0,1.0,1.0,1.0,1.0};
//        PISAHypervolume hypervolume = new PISAHypervolume(referencePoint);
//        for(FrontFile frontFile : frontFiles){
//            List<double[]> pfList = new ArrayList<>();
//            for(FrontData frontData : frontFile.getFrontDataList()){
//                pfList.add(frontData.getNormalizedQos().getDoubleListQos());
//            }
//            double[][] front = new double[pfList.size()][];
//            for(int i = 0;i < pfList.size();i++){
//                front[i] = pfList.get(i);
//            }
//            frontFile.setHv(hypervolume.compute(front));
//
//        }
//    }

}
