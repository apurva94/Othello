import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class OthelloReversi {
	static int size=8; //size of the board
	static int rr;
	static int rc;
	static int count;
	static List<Move> listOfMoves= new ArrayList<Move>(); //list of available moves for agent
	public static void main(String args[]){
		List<Move> listOfUserMoves= new ArrayList<Move>(); //list of available moves for the user
		
		int[][] currState=new int[size][size]; //current state of the board
		for (int i=0;i<size;i++)
			for (int j=0;j<size;j++)
				currState[i][j]=-1;
		currState[3][3]=currState[4][4]=1;
		currState[3][4]=currState[4][3]=0;	
		while(true)
		{
			count=0;
			System.out.println("Current state is:");
			display(currState);
			listOfUserMoves=new ArrayList<Move>();
			movesU(currState,listOfUserMoves); //computing permissible moves for user
			if(listOfUserMoves.isEmpty()){
				System.out.println("No more moves left");
				System.exit(0);
			}
			System.out.println("Viable moves are:");
			for(Move move : listOfUserMoves){
				System.out.println(move.getRow2()+" "+move.getColumn2());
			}
			boolean found=false;
			int movex=-1;
			int movey=-1;
			int pointx=-1;
			int pointy=-1;
			while(found==false){
		System.out.println("You play Black 'B'. Enter coordinates of next move(i.e i,j)...... or Q to quit");
		Scanner src= new Scanner(System.in);
		String input=src.nextLine();
		if(input.equalsIgnoreCase("Q")) System.exit(0);
		else if(input.length()==3){
			movex=Integer.parseInt(input.substring(0, 1)); //row of user's move
			movey=Integer.parseInt(input.substring(2,3)); //column of user's move
			//movex=Integer.parseInt(input.substring(4, 5));
			//movey=Integer.parseInt(input.substring(6));
			for(Move move : listOfUserMoves){
				if(movex==move.getRow2()&&movey==move.getColumn2()){
					pointx=move.getRow1();
					pointy=move.getColumn1();
					found=true;
				}
					
			}
			if(found==false){
				System.out.println("That move is not allowed");
			}
		}
		else System.out.println("Invalid input");
		}
		coverSpaces(pointx, pointy,movex,movey,currState,false); //capture White pieces on the board
 	
		display(currState);
		listOfMoves= new ArrayList<Move>();
		listOfMoves=movesA(currState); //computing permissible moves for agent
		if(listOfMoves.isEmpty()){
			System.out.println("No more moves left");
			System.exit(0);
		}
		MoveOrdering(currState,true);

		//int[][]chosenState=decideBestMove(currState,listOfMoves,true); <---depth==1
		int[][]chosenState=AlphaBetaBestMove(currState,listOfMoves,4,-999,999,true); //best move made after considering a depth of 4
		for (int i=0;i<size;i++)
			for (int j=0;j<size;j++)
				currState[i][j]=chosenState[i][j];
		System.out.println("Agent has moved"+"  nodes traversed:"+count);
		}
	}

	static void coverSpaces(int Px, int Py,int Mx,int My,int[][] State,boolean agent)
	{
		int stepi1=0;
		int stepj1=0;
		if(Px<Mx) {
			stepi1=+1;
			if(Py<My) {
				stepj1=+1;
				for(int i=Px, j=Py;i<=Mx&&j<=My;i=i+stepi1,j=j+stepj1)
			 		if(agent)State[i][j]=0;else State[i][j]=1;
			}
			else if(Py>My) {
				stepj1=-1;
				for(int i=Px, j=Py;i<=Mx&&j>=My;i=i+stepi1,j=j+stepj1)
					if(agent)State[i][j]=0;else State[i][j]=1;
			}
			else {
				for(int i=Px, j=Py;i<=Mx;i=i+stepi1)
					if(agent)State[i][j]=0;else State[i][j]=1;
			}
		}
		else if(Px>Mx) {
			stepi1=-1;
			if(Py<My) {
				stepj1=+1;
				for(int i=Px, j=Py;i>=Mx&&j<=My;i=i+stepi1,j=j+stepj1)
					if(agent)State[i][j]=0;else State[i][j]=1;
			}
			else if(Py>My) {
				stepj1=-1;
				for(int i=Px, j=Py;i>=Mx&&j>=My;i=i+stepi1,j=j+stepj1)
					if(agent)State[i][j]=0;else State[i][j]=1;
			}
			else {
				for(int i=Px, j=Py;i>=Mx;i=i+stepi1)
					if(agent)State[i][j]=0;else State[i][j]=1;
			}
		}
		else {
			stepi1=0;
			if(Py<My) {
				stepj1=+1;
				for(int i=Px, j=Py;i<=Mx&&j<=My;j=j+stepj1)
					if(agent)State[i][j]=0; else State[i][j]=1;
			}
			else if(Py>My) {
				stepj1=-1;
				for(int i=Px, j=Py;i<=Mx&&j>=My;j=j+stepj1)
					if(agent)State[i][j]=0; else State[i][j]=1;
			}
			else {
			}
		}
	}
	
	static void MoveOrdering(int[][]State,boolean Agent){
		for(int m=0; m<listOfMoves.size();m++)
			for(int n=0; n<listOfMoves.size()-m-1;n++){
			int[][] nextState1=new int[size][size];
			int[][] nextState2=new int[size][size];
			for (int i=0;i<size;i++)
				for (int j=0;j<size;j++){
					nextState1[i][j]=State[i][j];
					nextState2[i][j]=State[i][j];
				}
			coverSpaces(listOfMoves.get(n).getRow1(),listOfMoves.get(n).getColumn1(),listOfMoves.get(n).getRow2(),listOfMoves.get(n).getColumn2(),nextState1,Agent);
			coverSpaces(listOfMoves.get(n+1).getRow1(),listOfMoves.get(n+1).getColumn1(),listOfMoves.get(n+1).getRow2(),listOfMoves.get(n+1).getColumn2(),nextState2,Agent);
			if(Agent)
				if(utility(nextState1)>utility(nextState2)){
				Move temp=listOfMoves.get(n);
			//	System.out.println(utility(nextState1)+" "+utility(nextState2));
			//	System.out.println(temp.row1+" "+temp.column1+" "+temp.row2+" "+temp.column2);
				listOfMoves.set(n,listOfMoves.get(n+1));
			//	System.out.println(list1.get(n).row1+" "+list1.get(n).column1+" "+list1.get(n).row2+" "+list1.get(n).column2);
				listOfMoves.set(n+1,temp);
			//	System.out.println(list1.get(n+1).row1+" "+list1.get(n+1).column1+" "+list1.get(n+1).row2+" "+list1.get(n+1).column2);
				}
			else
				if(utility(nextState1)<utility(nextState2)){
					Move temp=listOfMoves.get(n);
					listOfMoves.set(n,listOfMoves.get(n+1));
					listOfMoves.set(n+1,temp);
					
				}	
			}
	}
	static int[][] AlphaBetaBestMove(int[][]State,List<Move> list1,int depth,int alpha, int beta, boolean Agent){
		//System.out.println(depth);
		MoveOrdering(State,Agent);
		count++;
		if(depth==0 || list1.size()==0){
			return State;
		}
		int[][] cstate=new int[size][size];
		if(Agent){
			int v=999;
			for(Move move : list1){
				int[][] nextState=new int[size][size];
				for (int i=0;i<size;i++)
					for (int j=0;j<size;j++)
						nextState[i][j]=State[i][j];
				coverSpaces(move.getRow1(),move.getColumn1(),move.getRow2(),move.getColumn2(),nextState,Agent);
				int a1=utility(nextState);
				int[][] b=new int[size][size];
				b=AlphaBetaBestMove(nextState,movesA(nextState),depth-1,alpha,beta,false);
				int b1=utility(b);
				if(a1<b1)
				{
					v=a1;
					cstate=nextState;
				}
				else{
					v=b1;
					cstate=b;
				}
				
				beta=Math.min(v, beta);
				if(beta<=alpha){
					break;
				}
			}
			return cstate;
		}
		else{
			int v=-999;
			for(Move move : list1){
				int[][] nextState=new int[size][size];
				for (int i=0;i<size;i++)
					for (int j=0;j<size;j++)
						nextState[i][j]=State[i][j];
				coverSpaces(move.getRow1(),move.getColumn1(),move.getRow2(),move.getColumn2(),nextState,Agent);
				int a1=utility(nextState);
				int[][] b=new int[size][size];
				b=AlphaBetaBestMove(nextState,movesA(nextState),depth-1,alpha,beta,true);
				int b1=utility(b);
				if(a1>b1)
				{
					v=a1;
					cstate=nextState;
				}
				else{
					v=b1;
					cstate=b;
				}
				
				beta=Math.max(v, beta);
				if(beta<=alpha){
					break;
				}
			}
			return cstate;
		}			
	}		
	
	static int utility(int state[][]){
		int eval=0;
		int countW,countB;
		countW=countB=0;
		for (int i=0;i<size;i++)
			for (int j=0;j<size;j++)
				if(state[i][j]==1)
					countB++;
				else if(state[i][j]==0)
					countW++;
		eval=countB+(64-countW);
		return eval;
	}
	static void display(int[][]State)
	{
		for (int i=0;i<size;i++){
			for (int j=0;j<size;j++)
				if(State[i][j]==1)
					System.out.print("B ");
				else if (State[i][j]==0)
					System.out.print ("W ");
				else System.out.print("- ");
			System.out.println();
		}
	}
	static List<Move> movesA(int state[][]){
		List<Move> list1=new ArrayList<Move>();
		list1.clear();
		for (int i=0;i<size;i++)
			for (int j=0;j<size;j++)
			{
				if(state[i][j]==0){
				if(i>0&&j>0&&state[i-1][j-1]==1)
					l1:for(int k=1;k<=6;k++){
						if((i-1-k)>-1&&(j-1-k)>-1)
							if(state[i-1-k][j-1-k]==-1)
							{
								list1.add(new Move(i,j,(i-1-k),(j-1-k)));
								break l1;
							}
					}
				if(i>0&&state[i-1][j]==1)
					l1:for(int k=1;k<=6;k++){
						if((i-1-k)>-1)
							if(state[i-1-k][j]==-1)
								{
								list1.add(new Move(i,j,(i-1-k),(j)));
								break l1;
								}
					}
				if(i>0&& j<7&&state[i-1][j+1]==1) 
					l1:for(int k=1;k<=6;k++){
						if((i-1-k)>-1&&(j+1+k)<size)
							if(state[i-1-k][j+1+k]==-1)
								{
								list1.add(new Move(i,j,(i-1-k),(j+1+k)));
								break l1;
								}
					}
				if(j>0&&state[i][j-1]==1) 
					l1:for(int k=1;k<=6;k++){
					if((j-1-k)>-1)
						if(state[i][j-1-k]==-1)
							{
							list1.add(new Move(i,j,(i),(j-1-k)));
							break l1;
							}
				}
				if(j<7&&state[i][j+1]==1) 
					l1:for(int k=1;k<=6;k++){
						if((j+1+k)<size)
							if(state[i][j+1+k]==-1)
								{
								list1.add(new Move(i,j,(i),(j+1+k)));
								break l1;
								}
					}

				if(i<7&&j>0&&state[i+1][j-1]==1)
					l1:for(int k=1;k<=6;k++){
						if((i+1+k)<size&&(j-1-k)>-1)
							if(state[i+1+k][j-1-k]==-1)
								{
								list1.add(new Move(i,j,(i+1+k),(j-1-k)));
								break l1;
								}
					}
				if(i<7&&state[i+1][j]==1)
					l1:for(int k=1;k<=6;k++){
						if((i+1+k)<size)
							if(state[i+1+k][j]==-1)
								{
								list1.add(new Move(i,j,(i+1+k),(j)));
								break l1;
								}
					}
				if(i<7&&j<7&&state[i+1][j+1]==1) 
					l1:for(int k=1;k<=6;k++){
						if((i+1+k)<size&&(j+1+k)<size)
							if(state[i+1+k][j+1+k]==-1)
								{
								list1.add(new Move(i,j,(i+1+k),(j+1+k)));
								break l1;
								}
					}
				}
			}
		return list1;
	}
	static void movesU(int state[][],List<Move> list1){
		list1.clear();
		for (int i=0;i<size;i++)
			for (int j=0;j<size;j++)
			{
				if(state[i][j]==1){
				if(i>0&&j>0&&state[i-1][j-1]==0)
					l1:for(int k=1;k<=6;k++){
						if((i-1-k)>-1&&(j-1-k)>-1)
							if(state[i-1-k][j-1-k]==-1)
							{
								list1.add(new Move(i,j,(i-1-k),(j-1-k)));
								break l1;
							}
					}
				if(i>0&&state[i-1][j]==0)
					l1:for(int k=1;k<=6;k++){
						if((i-1-k)>-1)
							if(state[i-1-k][j]==-1)
								{
								list1.add(new Move(i,j,(i-1-k),(j)));
								break l1;
								}
					}
				if(i>0&& j<7&&state[i-1][j+1]==0) 
					l1:for(int k=1;k<=6;k++){
						if((i-1-k)>-1&&(j+1+k)<size)
							if(state[i-1-k][j+1+k]==-1)
								{
								list1.add(new Move(i,j,(i-1-k),(j+1+k)));
								break l1;
								}
					}
				if(j>0&&state[i][j-1]==0) 
					l1:for(int k=1;k<=6;k++){
					if((j-1-k)>-1)
						if(state[i][j-1-k]==-1)
							{
							list1.add(new Move(i,j,(i),(j-1-k)));
							break l1;
							}
				}
				if(j<7&&state[i][j+1]==0) 
					l1:for(int k=1;k<=6;k++){
						if((j+1+k)<size)
							if(state[i][j+1+k]==-1)
								{
								list1.add(new Move(i,j,(i),(j+1+k)));
								break l1;
								}
					}

				if(i<7&&j>0&&state[i+1][j-1]==0)
					l1:for(int k=1;k<=6;k++){
						if((i+1+k)<size&&(j-1-k)>-1)
							if(state[i+1+k][j-1-k]==-1)
								{
								list1.add(new Move(i,j,(i+1+k),(j-1-k)));
								break l1;
								}
					}
				if(i<7&&state[i+1][j]==0)
					l1:for(int k=1;k<=6;k++){
						if((i+1+k)<size)
							if(state[i+1+k][j]==-1)
								{
								list1.add(new Move(i,j,(i+1+k),(j)));
								break l1;
								}
					}
				if(i<7&&j<7&&state[i+1][j+1]==0) 
					l1:for(int k=1;k<=6;k++){
						if((i+1+k)<size&&(j+1+k)<size)
							if(state[i+1+k][j+1+k]==-1)
								{
								list1.add(new Move(i,j,(i+1+k),(j+1+k)));
								break l1;
								}
					}
				}
			}
	}
}
