# Augmented WSC08 and WSC09 Dataset
This new benchmark inherits the functionalities provided by services in benchmark dataset WSC-08 and WSC-09 and QoS attributes of web services in the benchmark dataset QWS. The number of web services in the service repository is doubled (with much bigger searching space).


# Introduction
Both WSC08 and WSC09 dataset includes a set of composition tasks. Each composition task consists of three XML files, which serve as input files of web service composition algorithm. These files includes:

1. services-output.xml (i.e., a set of services with instances of inputs and outputs, and values of QoS), 
2. problem.xml (i.e., a service request that consists of required inputs and required outputs), and 
3. taxonomy.owl (i.e, a subclass hierarchy of the OWL-S Profile concept).

Based on the semantics described in the taxonomy.owl file, it is feasible to perform Taxonomy-based matchmaking based on the concepts that are associated with input and output instances. 

# Dataset creatation
We clone each services in WSC08 and WSC09 with the same inputs and outputs, but with different QoS values. This augmented datasets provide more services that can be selected for each vertex of the DAG-based solutions. We also make this datasets available to the public. Particularly, WSC08 contains 8 composition tasks with increasing size of service repository, i.e., 316, 1116, 1216, 2082, 2180, 4396, 8226, and 16238, and WSC09 contains 5 composition tasks with increasing size of service repository, i.e., 1144, 8258, 16276, 16602, and 30422 $\mathcal{SR}$s respectively

# Please cite this paper
C. Wang, H. Ma, G. Chen and S. Hartmann, "Memetic EDA-Based Approaches to QoS-Aware Fully-Automated Semantic Web Service Composition," in IEEE Transactions on Evolutionary Computation, doi: 10.1109/TEVC.2021.3127633.
