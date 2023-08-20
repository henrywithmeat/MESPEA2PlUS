package wsc.analyze;

import wsc.analyze.pojo.FrontData;
import wsc.analyze.pojo.FrontFile;
import wsc.analyze.pojo.OutFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**public class Main {
    private static final String[] filePathPreifx = {
            "/Users/wanghanting/Desktop/code_paper/MENSGA24SWSC-master/spea2res/spea",
            "/Users/wanghanting/Desktop/code_paper/MENSGA24SWSC-master/nsga2res/nsga"
    };
    private static final String[] filePathSuffix = {
            "front.stat",
            "out.stat"
    };
    private static final String currentPath = "0801";
    private static final int objectNum = 6;
    public static void main(String[] args){
        WscFileReader fileReader = new WscFileReader();
        //WscAnalyzer analyzer = new WscAnalyzer();
        List<FrontFile> frontFileList = new ArrayList<>();
        List<OutFile> outFileList = new ArrayList<>();
        for(String prefix : filePathPreifx){
            FrontFile frontFile= fileReader.readFrontFile(prefix+currentPath+filePathSuffix[0]);
            frontFileList.add(frontFile);
            OutFile outFile = fileReader.readOutFile(prefix + currentPath + filePathSuffix[1]);
            outFileList.add(outFile);
        }
        System.out.println("File read Over");
    }

}*/
