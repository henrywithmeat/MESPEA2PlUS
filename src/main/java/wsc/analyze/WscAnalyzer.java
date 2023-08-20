package wsc.analyze;

import ec.multiobjective.MultiObjectiveFitness;
import wsc.analyze.pojo.FrontData;
import wsc.analyze.pojo.FrontFile;
import wsc.analyze.pojo.OutData;
import wsc.analyze.pojo.OutFile;
import wsc.ecj.nsga2.SequenceVectorIndividual;
import wsc.ecj.nsga2.WSCMultiObjectiveStatistics;
import wsc.problem.WSCInitializer;

import java.util.ArrayList;
import java.util.List;

public class WscAnalyzer {
    private List<FrontFile> frontFiles;
    private List<OutFile> outFiles;

    public WscAnalyzer(List<FrontFile> frontFiles, List<OutFile> outFiles) {
        this.frontFiles = frontFiles;
        this.outFiles = outFiles;
    }
    //填充每代的paretoFront
     public void fillOutFile(List<OutFile> outFiles){
        //多个outdata文件
        for(OutFile outFile : outFiles){
            List<List<OutData>> outDataList = outFile.getOutDataList();
            List<FrontData> frontList = new ArrayList<>();
            //每个文件有多个generation
            for(List<OutData> outDataGroup : outDataList){
                //拿出当前的generation
                //只转换归一化数据
                SequenceVectorIndividual[] individuals = new SequenceVectorIndividual[outDataGroup.size()];
                for (int i = 0; i < individuals.length; i++) {
                    individuals[i] = outDataGroup.get(i).getNormalizedQos();
                }
                ArrayList paretoFront = MultiObjectiveFitness.partitionIntoParetoFront(individuals, null, null);
                FrontFile frontFile = new FrontFile();
                for(Object individual: paretoFront){
                    FrontData frontData = new FrontData();
                    frontData.setNormalizedQos((SequenceVectorIndividual) individual);
                    frontFile.add(frontData);
                }
                List<FrontFile> frontFileList = outFile.getFrontFiles();
                frontFileList.add(frontFile);
                outFile.setFrontFiles(frontFileList);
            }
        }
    }
    //解析标准解 并计算各数据
    public void getBestAnswer(){
        
    }
    //计算IGD
    public void caculateIGD(){

    }
    //计算HV

}
