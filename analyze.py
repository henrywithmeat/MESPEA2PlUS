import sys
import ctypes
import queue
import operator
import math
obj_num = 6
def readfront(frontfile: str) :
    global obj_num
    obj_list = []
    frontdata = open(frontfile)
    #dis = open(outfile, mode='w')
    input = frontdata.readline()
    while(input):
        frontlist = input.split()
        obj = []
        for num in range(0,obj_num):
            obj.append(frontlist[num])
        obj_list.append(obj)
        input = frontdata.readline()
    #print(objlist)
    return obj_list

def readout(outfile: str,time_list: list,speaFlag: bool) :
    global obj_num
    population = []
    outdata = open(outfile)
    input = outdata.readline()
    generation = -1
    while(input):
        outlist = input.split()
        obj = []
        cnt = 0
        for num in range(4,10):
            obj.append(outlist[num])
        if generation != int(outlist[0]):
            cnt = 1
            time = []
            time.append(outlist[2])
            time.append(outlist[3])
            time_list.append(time)
            new_generation = []
            new_generation.append(obj)
            generation = generation +1
            population.append(new_generation)
        else:
            if cnt <= 500 :
                population[generation].append(obj)
            cnt = cnt + 1           
        input = outdata.readline()
    return population       
            

#从list中找到pareto front
def getParetoFront(frontlist: list):
    pareto_list = []
    flag = True
    for solution in frontlist:
        for compare_solution in frontlist:
            if(domain(compare_solution,solution) == True):
                flag = False
        if(flag == True):
            pareto_list.append(solution)
        flag = True
    return pareto_list

def domain(list1: list,list2: list):
    global obj_num
    flag = 0
    for num in range(0,obj_num):
        if float(list1[num]) == float(list2[num]):
            flag = flag + 1
    if flag == obj_num:
        return False
    flag = 0
    for num in range(0,obj_num):
        if float(list1[num]) <= float(list2[num]):
            flag = flag+1
    if flag == obj_num:
        return True
    else:
        return False
#计算IGD
def caculateIGD(solutions: list,pareto_front: list):
    global obj_num
    sum_distance = 0.0
    for solution in solutions:
        min_distance = 10000.0
        for pareto in pareto_front:
            distance = 0.0
            for num in range(0,obj_num):
                snum = float(solution[num])
                pnum = float(pareto[num])
                distance = distance + (snum - pnum) * (snum - pnum)
            if(min_distance > distance):
                min_distance = distance
        sum_distance = sum_distance + min_distance
            
    return math.sqrt(sum_distance) / len(pareto_front)
            
#计算hv
def calculateHV(solutions: list):
    global obj_num
    max_hv = 0
    newSolutions = []
    for solution in solutions:
        if(solution not in newSolutions):
            newSolutions.append(solution)
    for solution in newSolutions:
        hv = 0
        for num in  range(0,obj_num):
            hv = hv + (1-float(solution[num])) * (1-float(solution[num]))
        hv = math.sqrt(hv)
        if(max_hv < hv): 
            max_hv = hv
    return max_hv

def caculateout(population1: list,population2: list,data_list1: list,data_list2: list,pf: list):
    for num in range(0,101):
        p1 = population1[num]
        p2 = population2[num]
        IGD1 = caculateIGD(p1,pf)
        hv1 = calculateHV(p1)
        data1 = [IGD1,hv1]
        data_list1.append(data1)
        IGD2 = caculateIGD(p2,pf)
        hv2 = calculateHV(p2)
        data2 = [IGD2,hv2]
        data_list2.append(data2)


if __name__ == '__main__':
    obj_list = []
    all_list = []
    obj_list1 = readfront('spea2res/spea0801front.stat')
    obj_list2 = readfront('nsga2res/nsga0801front.stat')
    pareto_list = obj_list1 + obj_list2
    pareto_front = getParetoFront(pareto_list)
    spea_IGD = caculateIGD(obj_list1,pareto_front)
    nsga_IGD = caculateIGD(obj_list2,pareto_front)
    spea_hv = calculateHV(obj_list1)
    nsga_hv = calculateHV(obj_list2)
    time_list1 = []
    population1 = readout('nsga2res/nsga0801out.stat',time_list1,False)
    time_list2 = []
    population2 = readout('spea2res/spea0801out.stat',time_list2,True)
    spea_data_list = []
    nsga_data_list = []
    caculateout(population1,population2,nsga_data_list,spea_data_list,pareto_front)
    dis = open("outData.txt",mode = 'w')
    dis.write('IGD\n')
    for data in spea_data_list:
        dis.write('%f\n' % data[0])
    dis.write('hv\n')
    for data in spea_data_list:
        dis.write('%f\n' % data[1])
    dis.write('time\n')
    curtime = int(time_list1[0][0])
    dis.write('%d\n' % int(curtime))
    for time in time_list1:
        curtime = curtime + int(time[1]) - int(time[0])
        dis.write('%d\n'% curtime)

    #print(spea_data_list)
    #print(nsga_data_list)
    #print('end')
    #analyze(sys.argv[1],sys.argv[2])
