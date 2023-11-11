package wsc.analyze.common;

import ec.multiobjective.MultiObjectiveFitness;
import wsc.analyze.pojo.FrontData;
import wsc.analyze.pojo.FrontFile;
import wsc.analyze.pojo.OutData;
import wsc.analyze.pojo.OutFile;
import wsc.ecj.nsga2.SequenceVectorIndividual;

import java.util.ArrayList;
import java.util.List;

//解析数据工具
public class WSCTools {

    //填充每代的paretoFront
    public void fillOutFile(List<OutFile> outFiles){
        //多个outdata文件
        for(OutFile outFile : outFiles){
            List<List<OutData>> outDataList = outFile.getOutDataList();
            List<FrontData> frontList = new ArrayList<>();
            //文件的所有遍历generation
            for(List<OutData> outDataGroup : outDataList){
                //拿出当前的generation对应的population
                //转换归一化数据
                SequenceVectorIndividual[] noramlIndividuals = new SequenceVectorIndividual[outDataGroup.size()];
                SequenceVectorIndividual[] individuals = new SequenceVectorIndividual[outDataGroup.size()];
                for (int i = 0; i < noramlIndividuals.length; i++) {
                    noramlIndividuals[i] = outDataGroup.get(i).getNormalizedQos();
                    individuals[i] = outDataGroup.get(i).getQos();
                }
                ArrayList normalParetoFront = MultiObjectiveFitness.partitionIntoParetoFront(noramlIndividuals, null, null);
                ArrayList paretoFront = MultiObjectiveFitness.partitionIntoParetoFront(individuals,null,null);
                FrontFile frontFile = new FrontFile();
                for(int i = 0 ;i < noramlIndividuals.length;i++){
                    FrontData frontData = new FrontData();
                    frontData.setNormalizedQos((SequenceVectorIndividual) normalParetoFront.get(i));
                    frontData.setQos((SequenceVectorIndividual) paretoFront.get(i));
                    frontFile.add(frontData);
                }
                List<FrontFile> frontFileList = outFile.getFrontFiles();
                frontFileList.add(frontFile);
                outFile.setFrontFiles(frontFileList);
            }
        }
    }

}
