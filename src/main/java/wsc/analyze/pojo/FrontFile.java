package wsc.analyze.pojo;

import java.util.ArrayList;
import java.util.List;

public class FrontFile {
    private List<FrontData> frontDataList;
    private int frontSize;

    public List<FrontData> getFrontDataList() {
        return frontDataList;
    }

    public void setFrontDataList(List<FrontData> frontDataList) {
        this.frontDataList = frontDataList;
    }

    public int getFrontSize() {
        return frontSize;
    }

    public void setFrontSize(int frontSize) {
        this.frontSize = frontSize;
    }

    public FrontFile() {
        this.frontDataList = new ArrayList<>();
        frontSize = 0;
    }

    public void add(FrontData frontData) {
        frontDataList.add(frontData);
        frontSize++;
    }
}
