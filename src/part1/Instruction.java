package part1;

public class Instruction {
	int idx;
	int result;
	String dest;  //a block of things can be changed to another kind of things, then change back, it 's up to me.
	//The computer has a lot of inner storage, just do it, don't be so..
	String src1;  //some instruction has one src1, some has two, not all of them are the same thing.
	String src2;
	String operation;
	

	public Instruction(String operation_, String dest_, String src1_,int idx_)//MOVC,jump
	{
		this.operation=operation_;
		this.dest=dest_;
		this.src1=src1_;
		idx=idx_;
	}
	//use a lot of kinds of data structure to do the work..
	public Instruction(String operation_, String dest_, String src1_,String src2_,int idx_)//ADD SUB
	{
		this.operation=operation_;
		this.dest=dest_;
		this.src1=src1_;
		
		this.src2=src2_;
		idx=idx_;
	}
	
	
	//this one is for branch.Then I can use get and set methods to do the work.
	public Instruction(String operation_,String src1_,int idx_)
	{
		this.operation=operation_;
		this.src1=src1_;
		idx=idx_;
	}
	
	
}
