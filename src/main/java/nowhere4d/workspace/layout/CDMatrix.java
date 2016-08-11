package nowhere4d.workspace.layout;

import java.io.PrintWriter;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

public class CDMatrix implements CDAbstractMapConstant, Serializable, Iterable<CDMatrixEntry>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3245853915073588832L;

	//=========================================================================
	// 생성자
	//-------------------------------------------------------------------------

	public static final double THRESHOLD_DENSITY = 0.1;
	
	

	protected CDMatrix(){
		
	}

	public static CDMatrix sparse(int rows, int columns)
	{
		return new CDMatrixSparse(rows, columns);
	}
	
	public static CDMatrix sparse(int rows, int columns, double initialValue)
	{
		return new CDMatrixSparse(rows, columns, initialValue);	
	}

	public static CDMatrix sparse(int rows, int columns, int initialCapacity, double minLoadFactor, double maxLoadFactor)
	{
		return new CDMatrixSparse(rows, columns, initialCapacity, minLoadFactor, maxLoadFactor);
	}

	public static CDMatrix sparse(double[][] data)
	{
		return new CDMatrixSparse(data);
	}
	
	public static CDMatrix dense(int rows, int columns)
	{
		return new CDMatrixDense(rows, columns);
	}
	
	public static CDMatrix dense(int rows, int columns, double initialValue)
	{
		return new CDMatrixDense(rows, columns, initialValue);	
	}

	public static CDMatrix dense(double[][] data)
	{
		return dense(data, true);
	}

	public static CDMatrix dense(double[][] data, boolean copy)
	{
		return new CDMatrixDense(data, copy);
	}
	
	public static CDMatrix matrixFromLongVector(CDVector longVector, int numberOfColumns){
		if(longVector.isDense())
			return new CDMatrixDense(longVector, numberOfColumns);
		else
			return new CDMatrixSparse(longVector, numberOfColumns);
	}
	
	public static CDMatrix matrixFromRowVector(CDVector rowVector, int numberOfRows){
		if(rowVector.isDense())
			return new CDMatrixDense(numberOfRows, rowVector);
		else
			return new CDMatrixSparse(numberOfRows, rowVector);
	}
	
	public static CDMatrix identity(int size, boolean dense){
		if(dense)
			return CDMatrixDense.identity(size);
		else
			return CDMatrixSparse.identity(size);
	}
	
	public static CDMatrix random(int rows, int cols, double min, double max, boolean dense, int randomSeed){
		if(dense)
			return CDMatrixDense.random(rows, cols, min, max, randomSeed);
		else
			return CDMatrixSparse.random(rows, cols, min, max, randomSeed);
	}
	
	//=========================================================================
	// PUBLIC 메소드
	//-------------------------------------------------------------------------
	
	/**************************************************************************
	 * 입력 행렬과 동일하지만, CDMatrixDense로 구현된 행렬를 반환한다.
	 * 이미 CDMatrixDense로 구현되어 있으면 자기자신을 반환
	 ***************************************************************************/
	
	public CDMatrix toDense(){
		return new CDMatrixDense(toArray());
	}
	/**************************************************************************
	 * 입력 행렬과 동일하지만, CDMatrixSparse로 구현된 행렬를 반환한다.
	 * 이미 CDMatrixSparse로 구현되어 있으면 자기자신을 반환
	 ***************************************************************************/
	
	public CDMatrix toSparse(){
		return this;
	}
	
	/**************************************************************************
	 * CDMatrixDense의 인스턴스인지 여부를 반환한다.
	 ***************************************************************************/
	
	public boolean isDense() {
		return false;
	}

	/**************************************************************************
	 * 행렬의 특정 위치의 값을 반환한다. 바운드 처리를 해준다.
	 **************************************************************************/
	
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
	

	
	public double[][] getArray() {
		return toArray();
	}

	
	public void setArray(double[][] array) {
		assign(array);
		
	}
	
	/**************************************************************************
	 * 백터를 문자열로 반환한다.
	 **************************************************************************/
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%d,%d", rows(), columns()));
		
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
				
				// 행렬 데이터를 구성한다.
				builder.append(String.format("\r\n%d,%d,%.3f", row, col, getQuick(row, col)));
			}
		}

		return builder.toString();
	}
	
	public String toStringMatlabStyle()
	{
		StringBuffer buf = new StringBuffer();
		
		for (int row = 0; row < rows(); row++)
		{
			for (int col = 0; col < columns(); col++) {
				buf.append(String.format("%3.5g", getQuick(row, col)));
				if (col < columns() - 1)
					buf.append("\t");
			}
		
			if (row < rows()-1)
				buf.append("\n");
		}
		
		return buf.toString();
	}
	
	public String toStringJavaArrayStyle()
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append('{');
		for (int row = 0; row < rows(); row++)
		{
			buf.append('{');
			for (int col = 0; col < columns(); col++) {
				buf.append(String.format("%3.5g", getQuick(row, col)));
				if (col < columns() - 1)
					buf.append(",");
			}
		
			buf.append("},\n");
		}
		buf.append("};");
		
		return buf.toString();
	}
	
	
	/**************************************************************************
	 * 백터를 문자열로 반환한다. 구분자는 공백을 사용한다.
	 **************************************************************************/
	public String toStringPretty()
	{
		return toStringPrettyHelper(" ");
	}

	/**************************************************************************
	 * 백터를 문자열로 반환한다. 구분자는 콤마를 사용한다.
	 **************************************************************************/
	public String toCSVStringPretty()
	{
		return toStringPrettyHelper(",");
	}
	
	/**************************************************************************
	 * 백터를 문자열로 반환한다. 구분자는 탭을 사용한다.
	 **************************************************************************/
	public String toTSVStringPretty()
	{
		return toStringPrettyHelper("\t");
	}
	
	/**************************************************************************
	 * 백터를 문자열로 반환한다. 구분자는 선택할 수 있다.
	 **************************************************************************/
	public String toStringPrettyHelper(String token)
	{
		StringBuilder buffer = new StringBuilder();

		for (int row = 0; row < rows(); row++)
		{
			for (int col = 0; col < columns(); col++)
				buffer.append(getQuick(row, col) + token);
			
			buffer.append("\n");
		}

		return buffer.toString();
	}
	
	/**************************************************************************
	 * 행렬의 행 크기를 반환한다.
	 **************************************************************************/
	
	public int rows()
	{
		return m_rowSize;
	}

	/**************************************************************************
	 * 행렬의 열 크기를 반환한다.
	 **************************************************************************/
	
	public int columns()
	{
		return m_colSize;
	}

	/**************************************************************************
	 * 행렬의 행*열 크기를 반환한다.
	 **************************************************************************/	
	
	public int size()
	{
		return m_rowSize * m_colSize;
	}

	/**************************************************************************
	 * 행렬에 0.0f 가 아닌 값들의 개수를 반환한다.
	 **************************************************************************/	
	
	public int cardinality()
	{
		return m_map.getSize();
	}

	/**************************************************************************
	 * 행 이터레이터를 반환한다.
	 **************************************************************************/	
	
	public Iter getIter()
	{
		return new Iter();
	}

	

	/**************************************************************************
	 * 행렬이 이진 행렬인지 검사한다. (이진 행렬: 0과 1로 이루어진 행렬)
	 **************************************************************************/
	
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
	public class Iter
	{
		/***********************************************************************
		 * 인덱스 정보를 초기화한다.
		 **********************************************************************/
		
		public Iter(){
			init();
		}
		
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

		
		public RowIter getRow(int rowIndex)
		{
			// 행 인덱스가 행렬의 행 크기보다 크거나 같다면 null을 반환한다. 
			if (rowIndex >= m_rowSize)
				return null;

			currentRowIndex = rowIndex;

			// 열 이터레이터를 반환한다.
			return new RowIter(m_rowPointer[rowIndex], currentRowIndex);
		}

		/***********************************************************************
		 * 다음 행을 가리키는 이터레이터를 반환한다.<BR>
		 * 비어있는 행의 경우도 반환되며, 이 경우 hasMoreValue()가 바로 FALSE를 반환한다.
		 **********************************************************************/
		
		public RowIter Next()
		{
			currentRowIndex++;
			
			// 현재 행 인덱스가 행렬의 행 크기보다 크거나 같다면 null을 반환한다. 
			if (currentRowIndex >= m_rowSize)
				return null;

			// 열 이터레이터를 반환한다.
			return new RowIter(m_rowPointer[currentRowIndex], currentRowIndex);
		}

		// 현재 행 인덱스
		private int currentRowIndex = 0;
	}

	/******************************************************************************
	 * 열 이터레이터
	 * 
	 * @author Torius
	 ******************************************************************************/
	public class RowIter
	{
		/******************************************************************************
		 * 열 이터레이터를 얻어오기 위해서는 전체 저장된 인덱스 중에 몇번째로 작은 것인가에 
		 * 대한 정보가 필요한데 이는 내부 구현에 해당하므로 밖에서 알 수 없다.
		 * 그래서 Public 선언이 되있던 것을 protected로 변경하였다.
		 ******************************************************************************/
		
		protected RowIter(int index, int rowIndex)
		{
			this.initIndex = index;
			this.currentIndex = index;
			this.currentRowIndex = rowIndex;
		}

		/**************************************************************************
		 * 초기화
		 **************************************************************************/
		public void init(){
			currentIndex = initIndex;
		}
		
		/**************************************************************************
		 * 다음 위치로 이동한다.
		 **************************************************************************/
		
		public void Next()
		{
			currentIndex++;
		}

		/**************************************************************************
		 * 현재 위치의 행 인덱스를 반환하다.
		 **************************************************************************/
		
		public int GetCurrentRowIndex()
		{
			return currentRowIndex;
		}

		/**************************************************************************
		 * 현재 위치의 열 인덱스를 반환하다.
		 **************************************************************************/
		
		public int GetCurrentColumnIndex()
		{
			return (int) (m_indices[currentIndex] % m_colSize);
		}

		/**************************************************************************
		 * 현재 위치의 값을 반환하다.
		 **************************************************************************/
		
		public double GetCurrentValue()
		{
			return m_map.get(m_indices[currentIndex]);
		}

		/**************************************************************************
		 * 뒤에 값이 더 있는지 알아본다.(현재 값을 포함해서)
		 **************************************************************************/
		
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

		// 생성될 당시의 인덱스를 기억
		protected int initIndex = 0;
		
		// 전체 저장된 인덱스 중에 몇번째로 작은 것인가.
		protected int currentIndex = 0;

		// 현재 행 인덱스
		protected int currentRowIndex = 0;
	}
	
	//=========================================================================
	// 맴버 변수
	//-------------------------------------------------------------------------
	
	// 행렬의 행 크기
	private int m_rowSize;
	
	// 행렬의 열 크기
	private int m_colSize;
	
	// 행렬의 내용을 담고있는 맵
	private CD64BDoubleMap m_map = null;

	// 정렬된 맵의 키 배열
	private long[] m_indices = null;
	
	// 맵의 키 정보가 변경되었는지 여부
	private boolean m_keyChanged = false;

	// 행의 시작 위치가 담긴 배열
	private int[] m_rowPointer;
	
	//
	private int m_lastRowIndex;

	
	public Iterator<CDMatrixEntry> iterator() {
		return new CDMatrixIter();
	}
	
	class CDMatrixIter implements Iterator<CDMatrixEntry>{
		
		CDMatrix.Iter iter;
		CDMatrix.RowIter rowIter;
		
		public CDMatrixIter() {
			iter = CDMatrix.this.getIter();
			rowIter = iter.Begin(0);
		}
		
		
		public boolean hasNext() {
			if (rowIter == null)
				return false;
			return (rowIter.hasMoreValue());
		}

		
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

		
		public void remove() {
			throw new UnsupportedOperationException("remove not supported");
		}
	}


	
	public void print(){
		
		DecimalFormat format = new DecimalFormat();
	    format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
	    format.setMinimumIntegerDigits(1);
	    format.setMaximumFractionDigits(5);
	    format.setMinimumFractionDigits(5);
	    format.setGroupingUsed(false);
	    PrintWriter output = new PrintWriter(System.out,true);
	    output.println();  // start on new line.
	      for (int i = 0; i < rows(); i++) {
	         for (int j = 0; j < columns(); j++) {
	            String s = format.format(getQuick(i,j)); // format the number
	            int padding = Math.max(1,10-s.length()); // At _least_ 1 space
	            for (int k = 0; k < padding; k++)
	               output.print(' ');
	            output.print(s);
	         }
	         output.println();
	      }
	      output.println();
		
	}

}

