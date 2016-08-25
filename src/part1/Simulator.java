package part1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Simulator {
	//all kinds of things are here, then I can use the things for it.
	//I can do a set of instructions.
	static int PC;
	static int cycle;
	static int jumpAddress;
	static boolean jumped;
	static boolean dependency;
	static Vector<String> instructions1=new Vector<String>();
	static Vector<Instruction> instructions2=new Vector<Instruction>();
	static Vector<Integer> memoryStorage=new Vector<Integer>();
	static Vector<RegisterStruct> registersArray=new Vector<RegisterStruct>();
	static Vector<Vector<Integer>> printTable=new Vector<Vector<Integer>>();
	//after decode, the things get into the storedSequence, then other kinds of the things 
	static Map<String,Instruction> pipeLine=new HashMap<String,Instruction>();

	void Initialize(String inputFile,String outputFile)
	{
		//initialize is also a method to do the work, then just do it here.
		//该粘贴的时候可以粘贴,don't spend too much time in the useless things..
		FileReader fr;
		FileWriter fw;
		BufferedReader br;
		BufferedWriter bw;
		
		try {
			fr=new FileReader(inputFile);
			fw=new FileWriter(outputFile);
			br=new BufferedReader(fr);
			bw=new BufferedWriter(fw);
			
			String temp;
			while((temp=br.readLine())!=null)//different kinds of the code do different kind of things,
				//know what kind of java code it do for a single piece of code then use it to do the work..
			{
				instructions1.add(temp);	//one piece
			}
		} catch (IOException e) {//just use IO exception for the things that I know.
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i=0;i<10000;i++)
		{
			memoryStorage.add(0);
		}
		
		pipeLine.put("FET", null);
		pipeLine.put("D/RF", null);
		pipeLine.put("EX", null);
		pipeLine.put("MEM", null);
		pipeLine.put("WB", null);
		
		//initialize the register .Every data structure and the other kinds of the things need to 
		//be inialize properly..get the every part's codes work properly.Then I can do more work right..
		for(int i=0;i<9;i++)
		{
			registersArray.add(new RegisterStruct(0,true));
		}
		
		//from instructions1 to instructions2.
		for(int i=0;i<instructions1.size();i++)
		{
			String [] strs=instructions1.get(i).split("\\s+");
			if(strs.length==2)
			{
				//bz 2
				instructions2.add(new Instruction(strs[0],strs[1],i));
				//map use put, vector use set....
			}
			else if(strs.length==3)
			{
				//move r3 4
				instructions2.add(new Instruction(strs[0],strs[1],strs[2],i));
			}
			else if(strs.length==4)
			{
				//add 1 2 3
				instructions2.add(new Instruction(strs[0],strs[1],strs[2],strs[3],i));
			}
				
		}
		PC=0;
		cycle=0;
		jumped=false;
		dependency=false;
		for(int i=0;i<5;i++)
		{
			printTable.add(new Vector<Integer>());
		}
	}
	
	public static void main(String args[])  //forget how many programming have 
	{	
	
		if(args.length!=2)
		{
			System.out.println("Usage: java Driver <intput_file_name> <output_file_name>");
			System.exit(0);
			//JUST COPY THE THINGS HERE...Simple and Easy..
		}
		
		String inputFile=new String();
		String outputFile=new String();
		inputFile=args[0];
		outputFile=args[1];
		
		Simulator simulator=new Simulator();
		//after new a object of the class,Then I can use it in this place...
		simulator.Initialize(inputFile,outputFile);
		simulator.Simulate();
		simulator.Display();//display the things in different stages..Then display every round things here..
		
	}

	//new a function here, and put together things, then put it in the main function.
	void Simulate()
	{
		Instruction inst=instructions2.get(PC);
		
		while(cycle<=30)
		//while(true)
		 {
			 jumped=false;
			 if(cycle>0 && pipeLineEmpty())
			 {
				 break;
			 }
			 if(PC <= instructions2.size()-1)
			 {
				 inst=instructions2.get(PC);
			 }
			 retire();
			 writeback();
			 memory();
			 execute();
			 decode();
			 fetch(inst);
			 //display all the things after the simulation
			 //store the things into a 2-D array, after that display them out..
			 pushTable();
			 cycle++;
		 }
		 
	}
	
	void Display()
	{
		System.out.println("Display the contexts in the stages....");
		//display all the things in the table, 
		//every for loop is everytime to repeat the same kinds of pattern, then do the same to others...
		
		for(int m=0; m<printTable.get(0).size();m+=80)
		{
			
			for(int i=0;i<5;i++)  //no matter write out the column first or print out the rows firstly, just print out all kinds of things.			
			{
				for(int j=m;j<m+80&& j<printTable.get(0).size();j++)
				{
					
					if(printTable.get(i).get(j)!=null)
					{
						if(  printTable.get(i).get(j)>=0 && printTable.get(i).get(j)<=9 )
							System.out.print("I"+(printTable.get(i).get(j))+"   ");//use get to do the vector, get and set.
						else if( printTable.get(i).get(j)>=10  && printTable.get(i).get(j)<=99)
							System.out.print("I"+(printTable.get(i).get(j))+"  ");
						else if( printTable.get(i).get(j)>=100 && printTable.get(i).get(j)<=999)
							System.out.print("I"+(printTable.get(i).get(j))+" ");
					}
					else if(printTable.get(i).get(j)==null)
					{
						System.out.print("     ");
					}
				}
				
				System.out.println();
			}
			System.out.println();
			System.out.println();
		}
		
		
		//display register value
		for(int i=0;i<8;i++)
		{
			System.out.println("[register"+i+": "+registersArray.get(i).rvalue+"]");
		}
		System.out.println();
		
		//display memory value
		for(int i=0;i<10000;i++)
		{
			if(memoryStorage.get(i)!=0)
				System.out.println("[memory"+i+": "+memoryStorage.get(i)+"]");
			
		}
	}
	
	void pushTable()
	{
		if(pipeLine.get("FET")!=null)
		{
			printTable.get(0).add(new Integer(0));
			printTable.get(0).set(cycle, pipeLine.get("FET").idx+1);
		}
		else
		{
			printTable.get(0).add(new Integer(0));
			printTable.get(0).set(cycle, null);//when the there is nothing int the slot, then I will push null in it.
		}
		if(pipeLine.get("D/RF")!=null)
		{
			printTable.get(1).add(new Integer(0));
			printTable.get(1).set(cycle, pipeLine.get("D/RF").idx+1);
		}
		else
		{
			printTable.get(1).add(new Integer(0));
			printTable.get(1).set(cycle,null);
		}
		if(pipeLine.get("EX")!=null)
		{
			printTable.get(2).add(new Integer(0));
			printTable.get(2).set(cycle, pipeLine.get("EX").idx+1);
		}
		else
		{
			printTable.get(2).add(new Integer(0));
			printTable.get(2).set(cycle,null);
		}
		if(pipeLine.get("MEM")!=null)
		{
			printTable.get(3).add(new  Integer(0));
			printTable.get(3).set(cycle, pipeLine.get("MEM").idx+1);
		}
		else
		{
			printTable.get(3).add(new Integer(0));
			printTable.get(3).set(cycle, null);
		}
		if(pipeLine.get("WB")!=null)
		{
			printTable.get(4).add(new Integer(0));
			printTable.get(4).set(cycle, pipeLine.get("WB").idx+1);
		}
		else
		{
			printTable.get(4).add(new Integer(0));
			printTable.get(4).set(cycle, null);
		}
	}
	
	void retire()
	{
		if(pipeLine.get("WB")!=null)
		{
			pipeLine.put("WB", null);
		}
	}
	
	void writeback()
	{
		//registersArray.get(Character.getNumericValue(inst.dest.charAt(1))).rvalid=false;
		Instruction inst; //every symbol need to be remembered and to be used correctly..
		if((inst=pipeLine.get("MEM"))!=null && (pipeLine.get("WB"))==null)
		{
			
			//use the inst's information to do the work.
			pipeLine.put("WB",inst);
			pipeLine.put("MEM",null);
			//if it is move or other kinds of the information include the load, not the store, then I will write the things
			//back to the registers. Nearly all except for load.I think both load and store, the instruction need to have the result as..
			if( !inst.operation.equals("BZ") && !inst.operation.equals("BNZ") && !inst.operation.equals("BAL") && !inst.operation.equals("JUMP")&& !inst.operation.equals("STORE"))				
			{
				//set store the result into the corresponding register, and set the registers as valid.
				//there is no valid or invalid to the thign,so it has something wrong with them.
				registersArray.get(Character.getNumericValue(inst.dest.charAt(1))).rvalue=inst.result;
				//until the writeback the load can be set as true.
				registersArray.get(Character.getNumericValue(inst.dest.charAt(1))).rvalid=true;
				//in java, use set get to get the right valu e at the specific place...	
			}
			
		}
	}
	
	void memory()
	{
		Instruction inst;
		if((inst=pipeLine.get("EX"))!=null && (pipeLine.get("MEM"))==null)
		{
			//In memory stage, use the previous calculated value as the address,LOAD then put the things int the memory location.
			//get the things from the memory location into the result place,
			pipeLine.put("MEM",inst);
			pipeLine.put("EX",null);
			if(inst.operation.equals("STORE"))
			{
				if(registersArray.get(Character.getNumericValue(inst.dest.charAt(1))).rvalid)
				{
				//Get the things from the store, then get from the other places.use PC to get the inst,each one, don't know copy or something like that
				//do copy or do reference.
					
					//before that this one cannot be used.
					memoryStorage.set(inst.result, registersArray.get(Character.getNumericValue(inst.dest.charAt(1))).rvalue);
				}
				else
				{
					dependency=true;
				}
				//-->LOAD
			}
			else if(inst.operation.equals("LOAD"))
			{
				
				inst.result=memoryStorage.get(inst.result);
				
				//then I can use the registersArray to get the 
			}
			
		}
	}
	
	void execute()
	{
		Instruction inst;
		if((inst=pipeLine.get("D/RF"))!=null && (pipeLine.get("EX"))==null)
		{
			//sperately check if all kinds of the operation's sources have been come here.
			if(inst.operation.equals("ADD")||inst.operation.equals("SUB")||inst.operation.equals("MUL")||inst.operation.equals("AND")||inst.operation.equals("OR")||inst.operation.equals("EX-OR"))
			{
				
				//registersArray.get(Character.getNumericValue(inst.dest.charAt(1))).rvalid=true;
				if(registersArray.get(Character.getNumericValue(inst.src1.charAt(1))).rvalid && registersArray.get(Character.getNumericValue(inst.src2.charAt(1))).rvalid)
				{
					//only If the thigns get done, then I can do the things here.
					if(dependency==true) { dependency=false; return; }
					pipeLine.put("EX",inst);
					pipeLine.put("D/RF",null);
					
					//directly put the results into the physical registers,
					if(inst.operation.equals("ADD"))
					{
						inst.result=registersArray.get(Character.getNumericValue(inst.src1.charAt(1))).rvalue+registersArray.get(Character.getNumericValue(inst.src2.charAt(1))).rvalue;
						//do the writeback simultaneously,then I can do everything I can do.
					}
					else if(inst.operation.equals("SUB"))
					{
						
						inst.result=registersArray.get(Character.getNumericValue(inst.src1.charAt(1))).rvalue-registersArray.get(Character.getNumericValue(inst.src2.charAt(1))).rvalue;
					}
					else if(inst.operation.equals("MUL"))
						inst.result=registersArray.get(Character.getNumericValue(inst.src1.charAt(1))).rvalue*registersArray.get(Character.getNumericValue(inst.src2.charAt(1))).rvalue;
					else if(inst.operation.equals("AND"))
						inst.result=registersArray.get(Character.getNumericValue(inst.src1.charAt(1))).rvalue & registersArray.get(Character.getNumericValue(inst.src2.charAt(1))).rvalue;
					else if(inst.operation.equals("OR"))
						inst.result=registersArray.get(Character.getNumericValue(inst.src1.charAt(1))).rvalue | registersArray.get(Character.getNumericValue(inst.src2.charAt(1))).rvalue;
					else if(inst.operation.equals("EX-OR"))
						inst.result=registersArray.get(Character.getNumericValue(inst.src1.charAt(1))).rvalue ^ registersArray.get(Character.getNumericValue(inst.src2.charAt(1))).rvalue;
					
					registersArray.get(Character.getNumericValue(inst.dest.charAt(1))).rvalid=false;
					
				}
				else
				{
					dependency=true;
				}
			}
			else if(inst.operation.equals("LOAD"))
			{
				
				if(registersArray.get(Character.getNumericValue(inst.src1.charAt(1))).rvalid==true)
				{
					if(dependency==true) { dependency=false; return; }
					pipeLine.put("EX",inst);
					pipeLine.put("D/RF",null);
					inst.result=registersArray.get(Character.getNumericValue(inst.src1.charAt(1))).rvalue+Integer.parseInt(inst.src2);		
					
					registersArray.get(Character.getNumericValue(inst.dest.charAt(1))).rvalid=false;
				}
				else
				{
					dependency=true;
				}
			}
			else if(inst.operation.equals("STORE"))
			{
				if(isInteger(inst.src2))
				{
					
					if(registersArray.get(Character.getNumericValue(inst.src1.charAt(1))).rvalid==true)
					{
						if(dependency==true) { dependency=false; return; }
						
						pipeLine.put("EX",inst);
						pipeLine.put("D/RF",null);
						inst.result=registersArray.get(Character.getNumericValue(inst.src1.charAt(1))).rvalue+Integer.parseInt(inst.src2);		
						
						
					}
					else
					{
						dependency=true;
					}
				}
				else 
				{
					if(registersArray.get(Character.getNumericValue(inst.src1.charAt(1))).rvalid==true && registersArray.get(Character.getNumericValue(inst.src2.charAt(1))).rvalid==true)
					{
						if(dependency==true) { dependency=false; return; }
						pipeLine.put("EX",inst);
						pipeLine.put("D/RF",null);
						inst.result=registersArray.get(Character.getNumericValue(inst.src1.charAt(1))).rvalue+registersArray.get(Character.getNumericValue(inst.src2.charAt(1))).rvalue;		
						
					
					}
					else
					{
						dependency=true;
					}
				}
			}
			else if(inst.operation.equals("MOVC"))
			{
				
				pipeLine.put("EX",inst);
				pipeLine.put("D/RF",null);
				inst.result=Integer.parseInt(inst.src1);
				registersArray.get(Character.getNumericValue(inst.dest.charAt(1))).rvalid=false;
			}
			else if (inst.operation.equals("BZ"))
			{
				pipeLine.put("EX",inst);
				pipeLine.put("D/RF",null);
				
				
				
				System.out.println("BZ");
				Instruction prev=instructions2.get(inst.idx-1);
				if(prev.result==0)
				{
					jumped=true;
					
					
					
					System.out.println("result is 0!");
					squash();
					PC=PC+Integer.parseInt(inst.src1);
				}
			}
			else if (inst.operation.equals("BNZ")) //if I forgot how to do it, just keep looking it, 好好编程吧，为啥我要活着，活在地球上面呢，其实就是这样的一个过程吧。
			{
				pipeLine.put("EX",inst);
				pipeLine.put("D/RF",null);
				System.out.println("BNZ");
				
				Instruction prev=instructions2.get(inst.idx-1);
				System.out.println("BNZ");
				if(prev.result!=0)
				{
					jumped=true;
					System.out.println("result is not 0!");
					squash();
					PC=PC+Integer.parseInt(inst.src1);
				}
			}
			else if(inst.operation.equals("JUMP"))
			{
				pipeLine.put("EX",inst);
				pipeLine.put("D/RF",null);
				if(inst.src1.equals("X"))
				{
					if(registersArray.get(Character.getNumericValue(inst.dest.charAt(1))).rvalid==true)
					{
						if(dependency==true) { dependency=false; return; }
						squash();
						int temp=registersArray.get(8).rvalue+0;
						PC=temp;
						// no matter it is jump or bal or bz or bnz, just need to
						//use squash to do the work, then get a new one.
						jumped=true;
						System.out.println("JUMP X");
					}
					else
					{
						dependency=true;
					}
					//so if I use the JUMP X, I will go to this place, then I can do more to ..
				}
				else
				{
					if(registersArray.get(Character.getNumericValue(inst.dest.charAt(1))).rvalid==true)
					{
						if(dependency==true) { dependency=false; return; }
						squash();
						int temp=registersArray.get(Character.getNumericValue(inst.dest.charAt(1))).rvalue+Integer.parseInt(inst.src1)-20000;
						PC=temp;
						jumped=true;
						System.out.println("JUMP...");
					}
					else
					{
						dependency=true;
					}
				}
			}
			else if(inst.operation.equals("BAL"))
			{
				if(registersArray.get(Character.getNumericValue(inst.dest.charAt(1))).rvalid==true)
				{	
					if(dependency==true) { dependency=false; return; }
					System.out.println("BAL");
				
					pipeLine.put("EX",inst);
					pipeLine.put("D/RF",null);
					squash();
					int temp=registersArray.get(Character.getNumericValue(inst.dest.charAt(1))).rvalue+Integer.parseInt(inst.src1);
					//so I can do the dest add the src1, then I can get the address.
					PC=temp;
					registersArray.get(8).rvalue=1+inst.idx;
					jumped=true;
				}
				else 
				{
					dependency=true;
				}
				//this one registersArray.get(8) will store the index of the instructions.
			}
				//first write by myself, if I encounter something wrong,then I can refer to others' work	
		}
	}
	
	void decode()
	{
		Instruction inst;
		//put the things from instructions2 to instruction1.Then I can do the things from there.
		if( (pipeLine.get("D/RF"))==null && (inst=pipeLine.get("FET"))!=null )  //这些就是不够仔细的表现是吧。。
		{
			pipeLine.put("D/RF",inst);
			pipeLine.put("FET",null);
			//if(!inst.operation.equals("AND")&&!inst.operation.equals("OR")&&!inst.operation.equals("EX-OR"))
		}
		
	}
	
	void fetch(Instruction inst)//give an instruction from the main function, then put it into the function that I want to use, 
	//this is not an easy one with regard to the actual information in it.Then I want to do something related to the 
	{
		if(jumped==true||PC > instructions2.size()-1)	
		{
			return;
		}
		if(pipeLine.get("FET")==null)
		{
			pipeLine.put("FET",inst);
			PC++;//this place is very good, after do the things,then PC++;
		}
	}
	
	
	boolean pipeLineEmpty()
	{
		if(pipeLine.get("FET")==null && pipeLine.get("D/RF")==null && pipeLine.get("EX")==null
				&& pipeLine.get("MEM")==null && pipeLine.get("WB")==null)
		{  return true; }
		else
		{  return false; }
	}
	
	static boolean isInteger(String s)
	{
		try{
			Integer.parseInt(s);
		}
		catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
		return true;
	}
	
	void squash()
	{
		pipeLine.put("FET", null);
	}
}
