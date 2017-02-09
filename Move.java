
public class Move {
		public int row1;
		public int column1;
		public int row2;
		public int column2;
		public Move(int i, int j,int k, int l)
		{
			row1=i;
			column1=j;
			row2=k;
			column2=l;
		}
		public int getRow1()
		{
			return row1;
		}
		public int getColumn1()
		{
			return column1;
		}
		public int getRow2()
		{
			return row2;
		}
		public int getColumn2()
		{
			return column2;
		}
}
