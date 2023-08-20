package wsc.analyze.pojo;

import java.util.ArrayList;
import java.util.List;

public class OutFile {
    private List<List<OutData>> outDataList;
    private List<FrontFile> frontFiles;

    public List<List<OutData>> getOutDataList() {
        return outDataList;
    }
    public void setOutDataList(List<List<OutData>> outDataList) {
        this.outDataList = outDataList;
    }

    public List<FrontFile> getFrontFiles() {
        return frontFiles;
    }

    public void setFrontFiles(List<FrontFile> frontFiles) {
        this.frontFiles = frontFiles;
    }

    public void add(OutData outData){
        int generation = outData.getGeneration();
        while(generation >= outDataList.size()){
            outDataList.add(new ArrayList<>());
        }
        outDataList.get(generation).add(outData);
    }
    public OutFile() {
        this.outDataList = new ArrayList<>();
    }
}
