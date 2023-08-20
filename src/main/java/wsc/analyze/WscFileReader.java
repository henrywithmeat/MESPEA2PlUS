package wsc.analyze;

import wsc.analyze.pojo.FrontData;
import wsc.analyze.pojo.FrontFile;
import wsc.analyze.pojo.OutData;
import wsc.analyze.pojo.OutFile;
import wsc.ecj.nsga2.SequenceVectorIndividual;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WscFileReader {

    public FrontFile readFrontFile(String filePath){
        FrontFile frontFile = new FrontFile();
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while ((line = reader.readLine()) != null) {
                FrontData frontData = generateFront(line);
                frontFile.add(frontData);
            }
        } catch (IOException e) {
            System.out.println("System read file error,reading:" + filePath);
            e.printStackTrace();
        }
        return frontFile;
    }

    public OutFile readOutFile(String filePath){
        OutFile outFile = new OutFile();
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while ((line = reader.readLine()) != null) {
                OutData outData = generateOut(line);
                outFile.add(outData);
            }
        } catch (IOException e) {
            System.out.println("System read file error,reading:" + filePath);
            e.printStackTrace();
        }
       return outFile;
    }

    private FrontData generateFront(String line) {
        FrontData frontData = new FrontData();
        List<SequenceVectorIndividual> individuals = generate(line);
        frontData.setNormalizedQos(individuals.get(0));
        frontData.setQos(individuals.get(1));
        return frontData;
    }

    private OutData generateOut(String line) {
        OutData outData = new OutData();
        String[] value = line.split("\\s+");
        outData.setGeneration(Integer.valueOf(value[0]));
        outData.setPopulationId(Integer.valueOf(value[1]));
        outData.setStartTime(value[2]);
        outData.setEndTime(value[3]);
        int lastIndex = value[0].length() + value[1].length()+value[2].length()+value[3].length() + 4;
        List<SequenceVectorIndividual> individuals = generate(line.substring(lastIndex));
        outData.setNormalizedQos(individuals.get(0));
        outData.setQos(individuals.get(1));
        return outData;
    }

    public List<SequenceVectorIndividual> generate(String line){
        String[] value = line.split("\\s+");
        List<SequenceVectorIndividual> individuals = new ArrayList<>();
        for(int i = 0;i < 12;i += 6){
            SequenceVectorIndividual individual = new SequenceVectorIndividual();
            if(value[i].isEmpty()){
                System.out.println(1);
            }
            individual.setMatchingType(Double.valueOf(value[i]));
            individual.setSemanticDistance(Double.valueOf(value[i+1]));
            individual.setAvailability(Double.valueOf(value[i+2]));
            individual.setReliability(Double.valueOf(value[i+3]));
            individual.setTime(Double.valueOf(value[i+4]));
            individual.setCost(Double.valueOf(value[i+5]));
            individuals.add(individual);
        }
        return individuals;
    }

}
