# Simulator-for-APEX
Implement a out-of-order simulator. 
Fulfilled IQ, LSQ, ROB and other technologies like forwarding and branching.
************************************************
1.To deal with input file which include instructions like BAL, JUMP, BZ, BNZ. If there is a right prediction, squash the previous instructions in the pipeline and fetch right address instruction. 
2.Implement ROB to let the instructions retire in order. Implement MEM FU, Integer FU, MUL FU.
