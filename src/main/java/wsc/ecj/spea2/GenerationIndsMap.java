package wsc.ecj.spea2;

import ec.Individual;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerationIndsMap {
    public static Map<Integer, List<Individual>> nonFrontMap = new HashMap<>();
    public static Map<Integer,List<Individual>> nonFrontMap2 = new HashMap<>();
    public static Map<Integer, List<Individual>> frontMap = new HashMap<>();
    public static Integer objIndex1;
    public static Integer objIndex2;
}
