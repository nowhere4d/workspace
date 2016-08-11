package nowhere4d.workspace.layout;

import java.util.Iterator;
import java.util.Random;

public class CDMatrixDense extends CDMatrix
{

	private static final long serialVersionUID = 4399409064935607170L;

	private int m_rowSize;
	private int m_colSize;
	
	//=========================================================================
	// 생성자
	//-------------------------------------------------------------------------

	protected CDMatrixDense(int rows, int columns)
	{
		super();
		m_rowSize = rows;
		m_colSize = columns;
		array = new double[rows][columns];
	}
	
	protected CDMatrixDense(int rows, int columns, double initialValue)
	{
		this(rows, columns);
		assign(initialValue);
	}

	protected CDMatrixDense(double[][] data)
	{
		this(data, true);
	}
	
	protected CDMatrixDense(double[][] data, boolean copy)
	{
		if(copy){ //행렬을 복사해서 할당
			m_rowSize = data.length;
			m_colSize = data[0].length;
			assign(data);
		}
		else{ //복사하지 않고 그대로 사용. 여러 side effect가 있을 수 있으므로 주의해서 사용
			m_rowSize = data.length;
			m_colSize = data[0].length;
			array = data; 
		}
	}

	/*
	 * 벡터를 Matrix로, 한 행의 크기를 지정해준다.
	 */
	protected CDMatrixDense(CDVector longVector, int numberOfColumns){
		
		this(longVector.size()/numberOfColumns, numberOfColumns);
				
		int length = longVector.size();
		if (length%numberOfColumns!=0)
			throw new IllegalArgumentException("numberOfColumns should be a divisor of size of vector.");
		
		int rowDimension = length / numberOfColumns;
		
		double[][] data = new double[rowDimension][numberOfColumns];
		for(int i =0; i<length; i++){
			double val = longVector.get(i);
			data[i/numberOfColumns][i%numberOfColumns] = val;
		}
		assign(data);
	}
	
	/* 
	 * Input Vector를 한 Row로 해서, 총 numberOfRows개의 Row를 가지는 Matrix를 반환한다. 
	 */
	protected CDMatrixDense(int numberOfRows, CDVector rowVector){
		
		this(numberOfRows, rowVector.size());

		double[] vec = rowVector.getArray();
		double[][] data = new double[numberOfRows][rowVector.size()];
		
		for(int i =0; i<numberOfRows; i++){
			data[i] = vec.clone();
		}
		assign(data);
	}
	
	//=========================================================================
	// 팩토리
	//-------------------------------------------------------------------------

	protected static CDMatrixDense identity(int size) {
		CDMatrixDense ret = new CDMatrixDense(size, size);
		for (int i=0; i < size; i++) {
			ret.set(i, i, 1);
		}
		return ret;
	}
	
	
	protected static CDMatrixDense random(int rows, int cols, double min, double max, int randomSeed){
		
		Random rand = new Random(randomSeed);
		
		// 행렬의 행과 열의 크기는 음수일 수 없다.
		if (max <= min)
			throw new IllegalArgumentException("Max should be larger than Min");
		
		CDMatrixDense ret = new CDMatrixDense(rows, cols);
		for(int i =0; i<rows; i++){
			for(int j =0; j<cols;j++){
				ret.setQuick(i, j, min+(rand.nextDouble()*(max-min)));
			}
		}
		
		return ret;
	}
	
	
	
	//=========================================================================
	// PUBLIC 메소드
	//-------------------------------------------------------------------------
	
	/**************************************************************************
	 * 입력 행렬과 동일하지만, CDMatrixDense로 구현된 행렬를 반환한다.
	 * 이미 CDMatrixDense로 구현되어 있으면 자기자신을 반환
	 ***************************************************************************/
	@Override
	public CDMatrix toDense(){
		return this;
	}
	/**************************************************************************
	 * 입력 행렬과 동일하지만, CDMatrixSparse로 구현된 행렬를 반환한다.
	 * 이미 CDMatrixSparse로 구현되어 있으면 자기자신을 반환
	 ***************************************************************************/
	@Override
	public CDMatrix toSparse(){
		return new CDMatrixSparse(array);
	}
	
	/**************************************************************************
	 * CDMatrixDense의 인스턴스인지 여부를 반환한다.
	 ***************************************************************************/
	@Override
	public boolean isDense() {
		return true;
	}
	
	/**************************************************************************
	 * 행렬의 특정 위치의 값을 반환한다. 바운드 처리를 해준다.
	 **************************************************************************/
	@Override
	public double get(int row, int column)
	{
		
		if (row < 0 || row >= m_rowSize)
			throw new IndexOutOfBoundsException("row:" + row + ", m_rowSize:" + m_rowSize);
		
		if (column < 0 || column >= m_colSize)
			throw new IndexOutOfBoundsException("column:" + column + ", m_columnSize:" + m_colSize);

		return getQuick(row, column);
	}

	/**************************************************************************
	 * 행렬의 특정 위치의 값을 반환한다.
	 **************************************************************************/
	@Override
	public double getQuick(int row, int column)
	{
		return array[row][column];
	}
	
	/***************************************************************************
	 * 지정된 행의 값을 백터로 반환한다.<BR>
	 * 초기화가 되어있지 않는 경우 O(E*log(E)) 의 복잡도를 가진다.
	 **************************************************************************/
	@Override
	public CDVector getRow(int row)
	{
		if (row < 0 || row >= m_rowSize)
			throw new IndexOutOfBoundsException("row:" + row + ", m_rowSize:" + m_rowSize);
		
		CDVector vector = CDVector.dense(m_colSize);
		
		vector.assign(array[row]);

		return vector;
	}
	
	@Override
	public CDVector getCol(int col)
	{
		if (col < 0 || col >= m_colSize)
			throw new IndexOutOfBoundsException("col:" + col + ", m_colSize:" + m_colSize);
		
		CDVector vector = CDVector.dense(m_rowSize);
		
		// Column을 받아온다.
		for(int i=0; i<m_rowSize; i++){
			vector.setQuick(i, getQuick(i, col));
		}
		
		return vector;
	}
	
	@Override
	public CDVector getRowSumVector()
	{
		CDVector vector = CDVector.dense(m_rowSize);	
		for(int i=0; i<m_rowSize; i++){
			double sum = 0;
			for(int j=0; j<m_colSize; j++)
				sum+=array[i][j];
			vector.setQuick(i, sum);	
		}
		return vector;
	}
	
	@Override
	public CDVector getColSumVector()
	{	
		CDVector vector = CDVector.dense(m_colSize);	
		for(int i=0; i<m_rowSize; i++){
			for(int j=0; j<m_colSize; j++)
				vector.setQuick(j, vector.getQuick(j)+array[i][j]);	
		}
		return vector;
	}
	
	
	/**************************************************************************
	 * 행렬의 특정 위치에 값을 대입한다. 바운드 처리를 해준다.
	 **************************************************************************/
	
	@Override
	public void set(int row, int column, double value)
	{
		
		if (row < 0 || row >= m_rowSize)
			throw new IndexOutOfBoundsException("row:" + row + ", m_rowSize:" + m_rowSize);
		
		if (column < 0 || column >= m_colSize)
			throw new IndexOutOfBoundsException("column:" + column + ", m_columnSize:" + m_colSize);

		setQuick(row, column, value);
	}

	/**************************************************************************
	 * 행렬의 특정 위치에 값을 대입한다.
	 **************************************************************************/
	@Override
	public void setQuick(int row, int column, double value)
	{
		// 행 위치와 열 위치를 이용해서 인덱스를 계산한다.
		array[row][column]=value;		
	}

	
	/**************************************************************************
	 * 행과 열 값으로 설정된 범위에 주어진 Matrix를 대입한다.
	 **************************************************************************/
	@Override
	public void setSubMatrix (int initRow, int lastRow, int initCol, int lastCol, CDMatrix X) {
	      
	   if(lastRow - initRow + 1 != X.rows() ||  lastCol - initCol + 1 != X.columns())
		   throw new IllegalArgumentException("Must satisfy following equation : lastRow - initRow = "+ (X.rows()- 1) +", lastCol - initCol" + (X.columns()-1));
	      
	   if(lastRow >= rows() ||  lastCol >= columns())
		   throw new IllegalArgumentException("lastRow must be smaller than "+ rows() +", lastCol must be smaller than "+columns());
		   
	   for (int i = initRow; i <= lastRow; i++) {
	         for (int j = initCol; j <= lastCol; j++) {
	            array[i][j] = X.getQuick(i-initRow,j-initCol);
	         }
	      }
	  }
	
	 /**************************************************************************
	 * 행과 열 값으로 설정된 범위의 Sub Matrix를 새로운 Matrix로 반환한다.
	 **************************************************************************/
	@Override
	public CDMatrixDense getSubMatrix (int initRow, int lastRow, int initCol, int lastCol) {	   
			   
		if(lastRow < initRow ||  lastCol < initCol )
		   throw new IllegalArgumentException("lastRow must be no smaller than initRow, and lastCol must be no smaller than initCol");
	       
		if(lastRow >= rows() ||  lastCol >= columns())
		   throw new IllegalArgumentException("lastRow must be smaller than "+ rows() +", lastCol must be smaller than "+columns());
			   
		int subRowSize =  lastRow-initRow+1;
		int subColSize =  lastCol-initCol+1;
		   
		CDMatrixDense subMatrix = new CDMatrixDense(subRowSize, subColSize);
		for(int i = initRow; i<=lastRow; i++)
		  for(int j=initCol; j<=lastCol; j++)
			   subMatrix.setQuick(i-initRow, j-initCol, array[i][j]);
				
		return subMatrix;
			   
	}      
	   
	/**************************************************************************
	 * 행렬을 지정한 배열의 값으로 모두 채운다.
	 **************************************************************************/	
	@Override
	public void assign(double[][] values)
	{
		if (values.length != m_rowSize)
			throw new IllegalArgumentException("Must have same number of rows: values.length=" +
												values.length + "getRowSize()=" + rows());

		double[][] newArray = new double[m_rowSize][m_colSize];
		// 배열을 순회하면서 행렬에 값을 배정한다. 
		for (int row = 0; row < values.length; row++)
		{
			if (values[row].length != m_colSize)
				throw new IllegalArgumentException("Must have same number of columns: values[row].length=" +
													values[row].length + "getColSize()=" + columns());

			// 값이 0인 경우는 값을 배정하지 않아도 된다.
			newArray[row] = (values[row].clone());
		}
		array = newArray;
	}

	/**************************************************************************
	 * 행렬을 지정한 한가지 값으로 모두 채운다.
	 **************************************************************************/
	@Override
	public void assign(double value)
	{
		// 값이 0.0f 인 경우에 처리한다.
		if (value == 0.0f)
		{
			// 맵의 키와 값을 모두 제거한다.
			clear();
			
			// value 값 배정을 하지 않고 함수를 종료한다.
			return;
		}

		double[] oneRow = new double[m_colSize];
		for (int col = 0; col < m_colSize; col++)
			oneRow[col] = value;
		// 행렬의 크기만큼 value 값을 배정한다.
		for (int row = 0; row < m_rowSize; row++)
		{
			array[row] = (oneRow.clone());
		}
	}

	/**************************************************************************
	 * 백터를 초기화한다.
	 **************************************************************************/
	@Override
	public void clear()
	{
		array = new double[m_rowSize][m_colSize];
	}

	/**************************************************************************
	 * 백터를 복사하여 반환한다.
	 **************************************************************************/
	@Override
	public CDMatrixDense copy()
	{
		CDMatrixDense newMatrix = new CDMatrixDense(m_rowSize, m_colSize);

		
		// 맵을 복사한다.
		for(int i =0; i<m_rowSize; i++)
				newMatrix.array[i] = array[i].clone();

		// 새로 생성된 행렬을 반환한다.
		return newMatrix;		
	}

	/**************************************************************************
	 * 백터를 배열로 반환한다.
	 **************************************************************************/
	@Override
	public double[][] toArray()
	{
		// 생성된 배열을 반환한다.
		// 맵을 복사한다.
		
		double[][] newArray = new double[m_rowSize][m_colSize];
		
		for(int i =0; i<m_rowSize; i++)
				newArray[i] = array[i].clone();

		// 새로 생성된 행렬을 반환한다.
		return newArray;
	}

	/**************************************************************************
	 * 행렬의 행 크기를 반환한다.
	 **************************************************************************/
	@Override
	public int rows()
	{
		return m_rowSize;
	}

	/**************************************************************************
	 * 행렬의 열 크기를 반환한다.
	 **************************************************************************/
	@Override
	public int columns()
	{
		return m_colSize;
	}

	/**************************************************************************
	 * 행렬의 행*열 크기를 반환한다.
	 **************************************************************************/	
	@Override
	public int size()
	{
		return m_rowSize * m_colSize;
	}

	/**************************************************************************
	 * 행렬에 0.0f 가 아닌 값들의 개수를 반환한다.
	 **************************************************************************/	
	@Override
	public int cardinality()
	{
		int count=0;
		for(int i =0 ; i<m_rowSize; i++)
			for(int j =0; j< m_colSize; j++)
				if(array[i][j]!=0.0f)
					count++;
		return count;
	}

	/**************************************************************************
	 * 행 이터레이터를 반환한다.
	 **************************************************************************/	
	@Override
	public Iter getIter()
	{
		return new IterDense();
	}


	/**************************************************************************
	 * 행렬이 이진 행렬인지 검사한다. (이진 행렬: 0과 1로 이루어진 행렬)
	 **************************************************************************/
	@Override
	public boolean isBinary()
	{
		for(int i =0 ; i<m_rowSize; i++)
			for(int j =0; j< m_colSize; j++)
				if(array[i][j]!=0.0f&&array[i][j]!=1.0f)
					return false;
		return true;
	}

	/**************************************************************************
	 * 행렬이 음의 값을 갖지 않는 행렬인지 검사한다.
	 **************************************************************************/
	@Override
	public boolean isNonNegative()
	{
		// 행 이터레이터를 얻는다.
		for(int i =0 ; i<m_rowSize; i++)
			for(int j =0; j< m_colSize; j++)
				if(array[i][j]<0.0f)
					return false;

		return true;
	}
	
	/**************************************************************************
	 * 행렬이 대칭 행렬인지 검사한다.
	 **************************************************************************/
	@Override
	public boolean isSymmetric()
	{
		// 행과 열의 크기가 다른 경우는 무조건 대칭일 수 없다. (대칭 조건이 성립 안됨)
		if (m_rowSize != m_colSize)
			return false;

		// 행 이터레이터를 얻는다.
		for(int i =0 ; i<m_rowSize; i++)
			for(int j =0; j< i; j++)
				if(Math.abs(array[i][j] - array[j][i]) >= 0.0000000001) //실수 연산 오차 를 고려하여 symmetric 여부를 판단한다.
					return false;

		return true;
	}
	
	/**************************************************************************
	 * 행렬이 원소를 0을 갖는지를 검사한다.
	 **************************************************************************/
	@Override
	public boolean hasZeroElement()
	{
	
		// 행 이터레이터를 얻는다.
		for(int i =0 ; i<m_rowSize; i++)
			for(int j =0; j< m_colSize; j++)
				if(array[i][j]==0.0f)
					return true;

		return false;
	}
	
	//=========================================================================
	// Dense Matrix 만의 메소드 - 성능 향상을 위해 작성.
	//-------------------------------------------------------------------------
	
	/**************************************************************************
	 * 행렬을 반환한다.(복사하지 말고 그대로) (의도치 않는 문제를 일으킬 수 있으니 주의해서 사용)
	 **************************************************************************/
	@Override
	public double[][] getArray(){
		return array;
	}
	
	/**************************************************************************
	 * 행렬을 설정한다.(복사하지 말고 그대로) (의도치 않는 문제를 일으킬 수 있으니 주의해서 사용)
	 **************************************************************************/
	@Override
	public void setArray(double[][] newArray){
		if (newArray.length != m_rowSize)
			throw new IllegalArgumentException("Must have same number of rows: values.length=" +
					newArray.length + "getRowSize()=" + rows());

		
		// 배열을 순회하면서 행렬에 값을 배정한다. 
		if (newArray[0].length != m_colSize)
				throw new IllegalArgumentException("Must have same number of columns: values[row].length=" +
						newArray[0].length + "getColSize()=" + columns());
		
		// 성능 상의 이유로 첫행만 검사한다.
		array = newArray;
	}
	
	//=========================================================================
	// 내부 클래스
	//-------------------------------------------------------------------------
	
	/***************************************************************************
	 * Forward만 되고 Backward는 안되는 iterator<br>
	 * 
	 * <code>
	 *  NMDoubleMatrix sparseMatrix = 임의 데이터;
	 *  NMDoubleMatrix.Iter iter = sparseMatrix.getIter();
	 *  NMDoubleMatrix.RowIter rowIter;
	 *  for ( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
	 *  	int row = rowIter.GetCurrentRowIndex();
	 *  	for ( ; rowIter.hasMoreValue(); rowIter.Next())
	 *  		System.out.print( rowIter.GetCurrentValue() + &quot; &quot;);
	 *  	System.out.println(&quot; /&quot;+row);
	 * 
	 *  }
	 * </code>
	 **************************************************************************/
	public class IterDense extends CDMatrix.Iter
	{
		/***********************************************************************
		 * 인덱스 정보를 초기화한다.
		 **********************************************************************/
		@Override
		public void init(){
			
		}
		
		/**************************************************************************
		 * 열 이터레이터를 반환한다.
		 **************************************************************************/
		@Override
		public RowIter Begin(int rowIndex)
		{
			// 행 인덱스가 행렬의 행 크기보다 크거나 같다면 null을 반환한다. 
			if (rowIndex >= m_rowSize)
				return null;

			// 위치 정보를 초기화한다.
			init();
			
			// 열 이터레이터를 반환한다.
			return getRow(rowIndex);
		}

		@Override
		public RowIter getRow(int rowIndex)
		{
			// 행 인덱스가 행렬의 행 크기보다 크거나 같다면 null을 반환한다. 
			if (rowIndex >= m_rowSize)
				return null;

			currentRowIndex = rowIndex;

			// 열 이터레이터를 반환한다.
			return new RowIterDense(currentRowIndex);
		}

		/***********************************************************************
		 * 다음 행을 가리키는 이터레이터를 반환한다.<BR>
		 * 비어있는 행의 경우도 반환되며, 이 경우 hasMoreValue()가 바로 FALSE를 반환한다.
		 **********************************************************************/
		@Override
		public RowIter Next()
		{
			currentRowIndex++;
			
			// 현재 행 인덱스가 행렬의 행 크기보다 크거나 같다면 null을 반환한다. 
			if (currentRowIndex >= m_rowSize)
				return null;

			// 열 이터레이터를 반환한다.
			return new RowIterDense(currentRowIndex);
		}

		// 현재 행 인덱스
		private int currentRowIndex = 0;
	}

	/******************************************************************************
	 * 행 이터레이터
	 ******************************************************************************/
	public class RowIterDense extends CDMatrix.RowIter
	{
		
		protected RowIterDense(int rowIndex){
			super(0,0);
			this.currentRowIndex = rowIndex;
			init();
		}
		
		/**************************************************************************
		 * 초기화
		 **************************************************************************/
		public void init(){
			this.currentColIndex = 0;
			while(true){
				if(currentColIndex>=m_colSize){
					currentColIndex=-1;
					break;
				}
				else if(array[currentRowIndex][currentColIndex]!=0.0f){
					break;
				}
				currentColIndex++;
			}
		}

		/**************************************************************************
		 * 다음 위치로 이동한다.
		 **************************************************************************/
		@Override
		public void Next(){
			currentColIndex++;
			while(true){
				if(currentColIndex>=m_colSize){
					currentColIndex=-1;
					break;
				}
				else if(array[currentRowIndex][currentColIndex]!=0.0f){
					break;
				}
				currentColIndex++;
			}
		}

		/**************************************************************************
		 * 현재 위치의 행 인덱스를 반환하다.
		 **************************************************************************/
		@Override
		public int GetCurrentRowIndex()
		{
			return currentRowIndex;
		}

		/**************************************************************************
		 * 현재 위치의 열 인덱스를 반환하다.
		 **************************************************************************/
		@Override
		public int GetCurrentColumnIndex()
		{
			return currentColIndex;
		}

		/**************************************************************************
		 * 현재 위치의 값을 반환하다.
		 **************************************************************************/
		@Override
		public double GetCurrentValue()
		{
			return array[currentRowIndex][currentColIndex];
		}

		/**************************************************************************
		 * 뒤에 값이 더 있는지 알아본다.
		 **************************************************************************/
		@Override
		public boolean hasMoreValue()
		{
			if(currentColIndex==-1)
				return false;
			else
				return true;
		}

		// 현재 열 인덱스
		protected int currentColIndex = 0;
		
		// 현재 행 인덱스
		protected int currentRowIndex = 0;
	}
	
	
	//=========================================================================
	// 맴버 변수
	//-------------------------------------------------------------------------
	
	//값을 저장하는 배열
	private double array[][];

	@Override
	public Iterator<CDMatrixEntry> iterator() {
		return new CDMatrixDenseIter();
	}
	
	class CDMatrixDenseIter extends CDMatrixIter{
		
		CDMatrixDense.Iter iter;
		CDMatrixDense.RowIter rowIter;
		
		public CDMatrixDenseIter() {
			iter = CDMatrixDense.this.getIter();
			rowIter = iter.Begin(0);
		}
		
		@Override
		public boolean hasNext() {
			if (rowIter == null)
				return false;
			return (rowIter.hasMoreValue());
		}

		@Override
		public CDMatrixEntry next() {
			CDMatrixEntry ret = new CDMatrixEntry(
					rowIter.GetCurrentRowIndex(), 
					rowIter.GetCurrentColumnIndex(),
					rowIter.GetCurrentValue());
			
			rowIter.Next();
			if (rowIter.hasMoreValue() == false) {
				rowIter = iter.Next();
			}
			
			return ret;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("remove not supported");
		}
	}
	
	
	public static void main(String[] ar){
		
	}

	
}

