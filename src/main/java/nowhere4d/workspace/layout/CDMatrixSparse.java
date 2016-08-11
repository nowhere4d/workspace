package nowhere4d.workspace.layout;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

public class CDMatrixSparse extends CDMatrix
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 373717791720903911L;

	//=========================================================================
	// 생성자
	//-------------------------------------------------------------------------

	protected CDMatrixSparse(int rows, int columns)
	{
		// -1을 넘겨주면 적당한 초기 용량을 계산하여 생성한다.
		this(rows, columns, -1, DEFAULT_MIN_LOAD_FACTOR, DEFAULT_MAX_LOAD_FACTOR);
	}
	
	protected CDMatrixSparse(int rows, int columns, double initialValue)
	{
		this(rows, columns, 2*rows*columns, 0.2, 0.5);
		
		assign(initialValue);
		
	}

	protected CDMatrixSparse(int rows, int columns, int initialCapacity, double minLoadFactor, double maxLoadFactor)
	{
		// 행렬의 행과 열의 크기는 음수일 수 없다.
		if (rows < 0 || columns < 0)
			throw new IllegalArgumentException("Negative size");

		// 행렬의 크기를 설정한다.
		m_rowSize = rows;
		m_colSize = columns;

		// 행의 시작 위치가 담길 배열을 생성한다.
		m_rowPointer = new int[rows];		

		// 초기 용량의 값이 음수이면 적당한 초기 용량을 계산한다.
		if (initialCapacity < 0)
			initialCapacity = Math.min(rows * (columns / 1000), INITIAL_CAPACITY_LIMIT);
		
		// 내부적으로 사용할 맵을 생성한다.
		m_map = new CD64BDoubleMap(initialCapacity, minLoadFactor, maxLoadFactor);

		// 키를 새로 생성하였으므로 키 변경 플래그를 켠다.
		m_keyChanged = true;
	}

	protected CDMatrixSparse(double[][] data)
	{
		this(data.length, (data.length == 0) ? 0 : data[0].length);
		
		// 배열로 이루어진 행렬의 값을 대입한다.
		assign(data);
	}

	/*
	 * 벡터를 Matrix로, 한 행의 크기를 지정해준다.
	 */
	protected CDMatrixSparse(CDVector longVector, int numberOfColumns){
		
		this(longVector.size()/numberOfColumns, numberOfColumns, longVector.cardinality()*3, 0.2, 0.5);
				
		int length = longVector.size();
		if (length%numberOfColumns!=0)
			throw new IllegalArgumentException("numberOfColumns should be a divisor of size of vector.");
		
		CDVector.Iter iter = longVector.getIter();
		
		for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){ 
			int i = iter.GetCurrentIndex();
			double val = iter.GetCurrentValue();
			if(!(val==0.0))
				setQuick(i/numberOfColumns,i%numberOfColumns, val);
			
		}
	}
	
	/* 
	 * Input Vector를 한 Row로 해서, 총 numberOfRows개의 Row를 가지는 Matrix를 반환한다. 
	 */
	protected CDMatrixSparse(int numberOfRows, CDVector rowVector){
		
		this(numberOfRows, rowVector.size(), rowVector.cardinality()*3, 0.2, 0.5);

		CDVector.Iter iter = rowVector.getIter();
		
		for(int i=0; i<numberOfRows; i++){
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){ 
				int j = iter.GetCurrentIndex();
				double val = iter.GetCurrentValue();
				setQuick(i,j, val);
				
			}
		}
	}
	
	//=========================================================================
	// 팩토리
	//-------------------------------------------------------------------------

	protected static CDMatrix identity(int size) {
		CDMatrix ret = new CDMatrixSparse(size, size, size*2, 0.2, 0.5);
		for (int i=0; i < size; i++) {
			ret.set(i, i, 1);
		}
		return ret;
	}
	
	
	protected static CDMatrix random(int rows, int cols, double min, double max, int randomSeed){
		// 행렬의 행과 열의 크기는 음수일 수 없다.
		if (max <= min)
			throw new IllegalArgumentException("Max should be larger than Min");
		
		Random rand = new Random(randomSeed);
		
		CDMatrix ret = new CDMatrixSparse(rows, cols, rows*cols*2, 0.2, 0.5);
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
		return new CDMatrixDense(toArray());
	}
	/**************************************************************************
	 * 입력 행렬과 동일하지만, CDMatrixSparse로 구현된 행렬를 반환한다.
	 * 이미 CDMatrixSparse로 구현되어 있으면 자기자신을 반환
	 ***************************************************************************/
	@Override
	public CDMatrix toSparse(){
		return this;
	}
	
	/**************************************************************************
	 * CDMatrixDense의 인스턴스인지 여부를 반환한다.
	 ***************************************************************************/
	@Override
	public boolean isDense() {
		return false;
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
		// 행 위치와 열 위치를 이용해서 인덱스를 계산한다.
		long index = (long)row * (long)m_colSize + (long)column;

		// 해당 인덱스의 값을 반환한다.
		return m_map.get(index);
	}
	
	/***************************************************************************
	 * 지정된 행의 값을 백터로 반환한다.<BR>
	 * 초기화가 되어있지 않는 경우 O(E*log(E)) 의 복잡도를 가진다.
	 **************************************************************************/
	@Override
	public CDVector getRow(int row)
	{
		/*
		 * 바운드 처리 해줌
		 * swRyu@20080821
		 */
		if (row < 0 || row >= m_rowSize)
			throw new IndexOutOfBoundsException("row:" + row + ", m_rowSize:" + m_rowSize);
		
		CDVector vector = CDVector.sparse(m_colSize);

		// 행 이터레이터를 생성한다.
		Iter iter = getIter();

		// 열 이터레이터를 순회하면서 백터에 값을 대입한다.
		RowIter rowIter = iter.Begin(row);
		// 현재 Row가 비어있으면 아무 데이터도 없는 Vector를 반환한다.
		if(rowIter==null)
		{
			return vector;
		}
		
		for ( ; rowIter.hasMoreValue(); rowIter.Next())
		{
			// 열 인덱스를 가져온다.
			int col = rowIter.GetCurrentColumnIndex();
			
			// 백터에 값을 대입한다.
			vector.setQuick(col, getQuick(row, col));
		}

		return vector;
	}
	
	@Override
	public CDVector getCol(int col)
	{
		/*
		 * 바운드 처리 해줌
		 * swRyu@20080821
		 */
		if (col < 0 || col >= m_colSize)
			throw new IndexOutOfBoundsException("col:" + col + ", m_colSize:" + m_colSize);
		
		CDVector vector = CDVector.sparse(m_rowSize);
		
		// Column을 받아온다.
		for(int i=0; i<m_rowSize; i++)
		{
			final double val = getQuick(i, col);
			vector.setQuick(i, val);
		}
		
		return vector;
	}
	/***************************************************************************
	 * 각 행의 합을 원소로 갖는 백터를 반환한다. 벡터의 크기는 행의 갯수와 같다.
	 **************************************************************************/
	@Override
	public CDVector getRowSumVector()
	{
		CDVector vector = CDVector.sparse(m_rowSize, m_rowSize*2, 0.2, 0.5);	
		for(int i=0; i<m_rowSize; i++){
			vector.set(i, getRow(i).getSum());	
		}
		return vector;
	}
	
	/***************************************************************************
	 * 각 열의 합을 원소로 갖는 백터를 반환한다. 벡터의 크기는 열의 갯수와 같다.
	 **************************************************************************/
	@Override
	public CDVector getColSumVector()
	{	
		CDVector vector = CDVector.sparse(m_colSize, m_colSize*2, 0.2, 0.5);	
		for(int i=0; i<m_colSize; i++){
			vector.set(i, getCol(i).getSum());
			
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
		long index = (long)row * (long)m_colSize + (long)column;

		if (value == 0.0f)
			m_keyChanged |= m_map.removeKey(index);
		else
			m_keyChanged |= m_map.put(index, value);		
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
	          this.setQuick(i, j, X.getQuick(i-initRow,j-initCol));
	       }
	    }
	}
	
	/**************************************************************************
	* 행과 열 값으로 설정된 범위의 Sub Matrix를 새로운 Matrix로 반환한다.
	**************************************************************************/
	@Override
	public CDMatrix getSubMatrix (int initRow, int lastRow, int initCol, int lastCol) {	   
	  
		if(lastRow < initRow ||  lastCol < initCol )
			throw new IllegalArgumentException("lastRow must be no smaller than initRow, and lastCol must be no smaller than initCol");
	     
		if(lastRow >= rows() ||  lastCol >= columns())
		   throw new IllegalArgumentException("lastRow must be smaller than "+ rows() +", lastCol must be smaller than "+columns());
			   
		int subRowSize =  lastRow-initRow+1;
		int subColSize =  lastCol-initCol+1;
			   
		CDMatrix subMatrix = new CDMatrixSparse(subRowSize, subColSize, subRowSize*subColSize*cardinality()/(m_colSize*m_rowSize)*3, 0.2, 0.5);
		   
		// 이터레이터를 얻는다.
		Iter iter = getIter();

				// 순회하면서 값을 배열에 배정한다.
		for (RowIter rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next())
		{
			// 행 인덱스를 구한다.
			int row = rowIter.GetCurrentRowIndex();
			if(row<initRow)
				continue;
			else if(row>lastRow)
				break;
			for (; rowIter.hasMoreValue(); rowIter.Next())
			{
				// 열 인덱스를 구한다.
				int col = rowIter.GetCurrentColumnIndex();
					
				if(col<initCol)
					continue;
				else if(col>lastCol)
					break;
				// 배열에 값을 배정한다.
				subMatrix.setQuick(row-initRow, col-initCol, getQuick(row, col));
			}
		}
				
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

		// 맵의 키와 값을 모두 제거한다.
		clear();
		
		// 배열을 순회하면서 행렬에 값을 배정한다. 
		for (int row = 0; row < values.length; row++)
		{
			if (values[row].length != m_colSize)
				throw new IllegalArgumentException("Must have same number of columns: values[row].length=" +
													values[row].length + "getColSize()=" + columns());

			// 열을 순회하면서 행렬에 값을 배정한다.
			for (int col = 0; col < values[row].length; col++)
			{
				// 값이 0인 경우는 값을 배정하지 않아도 된다.
				if (values[row][col] != 0.0f)
					setQuick(row, col, values[row][col]);
			}
		}
	}

	
	/**************************************************************************
	 * 행렬을 지정한 한가지 값으로 모두 채운다.
	 **************************************************************************/
	@Override
	public void assign(double value)
	{
		
		// 맵의 키와 값을 모두 제거한다.
		clear();
		
		// 값이 0.0f 인 경우에 처리한다.
		if (value == 0.0f){
			// value 값 배정을 하지 않고 함수를 종료한다.
			return;
		}

		// 행렬의 크기만큼 value 값을 배정한다.
		for (int row = 0; row < m_rowSize; row++)
		{
			for (int col = 0; col < m_colSize; col++)
				setQuick(row, col, value);
		}
	}

	
	/**************************************************************************
	 * 백터를 초기화한다.
	 **************************************************************************/
	@Override
	public void clear()
	{
		// 맵의 키와 값을 모두 제거한다.
		m_map.clear();
		
		// 키가 변경되었으므로 플래그를 설정한다.
		m_keyChanged = true;
	}

	
	/**************************************************************************
	 * 백터를 복사하여 반환한다.
	 **************************************************************************/
	@Override
	public CDMatrix copy()
	{
		CDMatrixSparse newMatrix = new CDMatrixSparse(m_rowSize, m_colSize);

		// 맵을 복사한다.
		newMatrix.m_map = m_map.clone();
		
		// 키가 변경되었으므로 플래그를 설정한다.
		newMatrix.m_keyChanged = true;

		// 새로 생성된 행렬을 반환한다.
		return newMatrix;		
	}

	
	/**************************************************************************
	 * 백터를 배열로 반환한다.
	 **************************************************************************/
	@Override
	public double[][] toArray()
	{
		double[][] array = new double[m_rowSize][m_colSize];

		// 이터레이터를 얻는다.
		Iter iter = getIter();

		// 순회하면서 값을 배열에 배정한다.
		for (RowIter rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next())
		{
			// 행 인덱스를 구한다.
			int row = rowIter.GetCurrentRowIndex();
			
			for (; rowIter.hasMoreValue(); rowIter.Next())
			{
				// 열 인덱스를 구한다.
				int col = rowIter.GetCurrentColumnIndex();
				
				// 배열에 값을 배정한다.
				array[row][col] = getQuick(row, col);
			}
		}

		// 생성된 배열을 반환한다.
		return array;
	}
	

	@Override
	public double[][] getArray() {
		return toArray();
	}

	@Override
	public void setArray(double[][] array) {
		assign(array);
		
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
		return m_map.getSize();
	}

	/**************************************************************************
	 * 행 이터레이터를 반환한다.
	 **************************************************************************/	
	@Override
	public Iter getIter()
	{
		return new IterSparse();
	}

	

	/**************************************************************************
	 * 행렬이 이진 행렬인지 검사한다. (이진 행렬: 0과 1로 이루어진 행렬)
	 **************************************************************************/
	@Override
	public boolean isBinary()
	{
		// 행 이터레이터를 얻는다.
		Iter iter = getIter();
		
		// 행 이터레이터를 순회한다.
		for (RowIter rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next())
		{
			// 열 이터레이터를 순회하면서 값을 확인한다.
			for (; rowIter.hasMoreValue(); rowIter.Next())
			{
				// 값이 1.0f 이 아니면 이진 행렬이 아니다.
				if (rowIter.GetCurrentValue() != 1.0f)
					return false;
			}
		}

		return true;
	}

	/**************************************************************************
	 * 행렬이 음의 값을 갖지 않는 행렬인지 검사한다.
	 **************************************************************************/
	@Override
	public boolean isNonNegative()
	{
		// 행 이터레이터를 얻는다.
		Iter iter = getIter();
		
		// 행 이터레이터를 순회한다.
		for (RowIter rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next())
		{
			// 열 이터레이터를 순회하면서 값을 확인한다.
			for (; rowIter.hasMoreValue(); rowIter.Next())
			{
				// 값이 음수면 NonNegative가 아니다.
				if (rowIter.GetCurrentValue() < 0)
					return false;
			}
		}

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
		Iter iter = getIter();
		
		// 행 이터레이터를 순회한다.
		for (RowIter rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next())
		{
			// 행 인덱스를 얻는다.
			int row = rowIter.GetCurrentRowIndex();
			
			// 열 이터레이터를 순회하면서 값을 확인한다.
			for (; rowIter.hasMoreValue(); rowIter.Next())
			{
				// 열 인덱스를 얻는다.
				int col = rowIter.GetCurrentColumnIndex();
				
				// 값이 대칭 관계인지 확인한다.
				if (Math.abs(rowIter.GetCurrentValue() - getQuick(col, row)) >= 0.0000000001) //실수 연산 오차 를 고려하여 symmetric 여부를 판단한다. 
					return false;
			}
		}

		return true;
	}
	
	/**************************************************************************
	 * 행렬이 원소를 0을 갖는지를 검사한다.
	 **************************************************************************/
	@Override
	public boolean hasZeroElement()
	{
		return m_map.getSize()!=m_rowSize*m_colSize;
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
	public class IterSparse extends Iter
	{
		
		public IterSparse(){
			super();
			init();
		}
		/***********************************************************************
		 * 인덱스 정보를 초기화한다.
		 **********************************************************************/		
		@Override
		public void init()
		{
			// 행이나 열의 크기가 하나라도 0이라면 처리한다. 
			if (m_rowSize == 0 || m_colSize == 0)
			{
				m_lastRowIndex = -1;
				
				// 데이터가 있을 수 없으므로 키도 변경되지 않았다.
				m_keyChanged = false;
			}

			// 초기화된 상태 그대로가 아니라면 처리한다.
			if (m_keyChanged != false)
			{
				// 인덱스를 가져온다.				
				m_indices = m_map.getKeys();
				
				// 인덱스를 정렬한다.				
				Arrays.sort(m_indices);
	
				// 행의 시작 위치 배열을 초기화한다.
				int rowIndex = -1;
				
				for (int i = 0; i < m_indices.length; i++)
				{
					int row = (int) (m_indices[i] / m_colSize);
					
					while (row > rowIndex)
					{
						m_rowPointer[++rowIndex] = i;
					}
				}
	
				// 마지막 행 인덱스를 저장한다.
				m_lastRowIndex = rowIndex;
	
				// 키 정보를 초기화했으므로 키 변경 플래그를 끈다.
				m_keyChanged = false;
			}
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
			return new RowIterSparse(m_rowPointer[rowIndex], currentRowIndex);
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
			return new RowIterSparse(m_rowPointer[currentRowIndex], currentRowIndex);
		}

		// 현재 행 인덱스
		private int currentRowIndex = 0;
	}

	/******************************************************************************
	 * 열 이터레이터
	 * 
	 * @author Torius
	 ******************************************************************************/
	public class RowIterSparse extends RowIter
	{
		/******************************************************************************
		 * 열 이터레이터를 얻어오기 위해서는 전체 저장된 인덱스 중에 몇번째로 작은 것인가에 
		 * 대한 정보가 필요한데 이는 내부 구현에 해당하므로 밖에서 알 수 없다.
		 * 그래서 Public 선언이 되있던 것을 protected로 변경하였다.
		 ******************************************************************************/
		
		protected RowIterSparse(int index, int rowIndex)
		{
			super(index, rowIndex);
		}

		/**************************************************************************
		 * 다음 위치로 이동한다.
		 **************************************************************************/
		@Override
		public void Next()
		{
			currentIndex++;
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
			return (int) (m_indices[currentIndex] % m_colSize);
		}

		/**************************************************************************
		 * 현재 위치의 값을 반환하다.
		 **************************************************************************/
		@Override
		public double GetCurrentValue()
		{
			return m_map.get(m_indices[currentIndex]);
		}

		/**************************************************************************
		 * 뒤에 값이 더 있는지 알아본다.(현재 값을 포함해서)
		 **************************************************************************/
		@Override
		public boolean hasMoreValue()
		{
			// if next row exists
			if (currentRowIndex < m_lastRowIndex)
			{
				return currentIndex < m_rowPointer[currentRowIndex + 1];
			}
			// for the last row
			else if (currentRowIndex == m_lastRowIndex) 
			{
				return currentIndex < m_indices.length;
			}

			// row out of index
			return false;
		}
	}
	
	//=========================================================================
	// 맴버 변수
	//-------------------------------------------------------------------------
	
	// 행렬의 행 크기
	protected int m_rowSize;
	
	// 행렬의 열 크기
	protected int m_colSize;
	
	// 행렬의 내용을 담고있는 맵
	protected CD64BDoubleMap m_map = null;

	// 정렬된 맵의 키 배열
	protected long[] m_indices = null;
	
	// 맵의 키 정보가 변경되었는지 여부
	protected boolean m_keyChanged = false;

	// 행의 시작 위치가 담긴 배열
	protected int[] m_rowPointer;
	
	//
	protected int m_lastRowIndex;

	@Override
	public Iterator<CDMatrixEntry> iterator() {
		return new CDMatrixSparseIter();
	}
	
	class CDMatrixSparseIter extends CDMatrixIter {
		
		CDMatrix.Iter iter;
		CDMatrix.RowIter rowIter;
		
		public CDMatrixSparseIter() {
			iter = CDMatrixSparse.this.getIter();
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

