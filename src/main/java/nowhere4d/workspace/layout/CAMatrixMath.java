package nowhere4d.workspace.layout;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import nowhere4d.workspace.layout.CDMatrix.Iter;
import nowhere4d.workspace.layout.CDMatrix.RowIter;

import java.util.TreeSet;

public class CAMatrixMath implements CAMatrixMathOptions
{
	
	/** 
	 * 실수 연산 오차 때문에 발생하는 문제를 제거하기 위하여 
	 * 소수점 아래 7자리에서 반올림하여, 6자리의 소수로 나타낸다.(넷마이너에서 제공하는 최고 정확도) 
	*/
	public static CDMatrix roundHalfUp(CDMatrix A){
		
		int n = A.rows();
		int m = A.columns();
			
		CDMatrix result;
		if(A.isDense()){
			result = CDMatrix.dense(n, m);
			result.setArray(CAMatrixMathDense.roundHalfUp(A.getArray()));
			return result;
		}
		else{
			result = CDMatrix.sparse(n, m);
			CDMatrix.Iter iter = A.getIter();
			CDMatrix.RowIter rowIter;
			
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){ 
				int i = rowIter.GetCurrentRowIndex();
				for( ; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					BigDecimal bd = new BigDecimal(A.getQuick(i,j));
					result.setQuick(i, j, bd.setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue());
				}
			}
			return result;
		}
	}
	
	/** 입력된 CDvector 의 모든 요소들을 더한 결과를 리턴한다
	 * @param vector - Sum 을 할 CDVector 
	 * @return double 로서 리턴한다
	 * @author swRyu@20080821
	 */
	public static double getSumOfAllEntries(CDVector vector){
		
		if(vector.isDense())
			return CAMatrixMathDense.sumOfAllElements(vector.getArray());
		
		else{
			
			double sum = 0;
			
			CDVector.Iter iter = vector.getIter(); 
			//불가피하게 패키지명까지 다 specify.. CDBigVector의 Iter 와 타입명 중복되기때문
			for(iter.Begin(); iter.hasMoreValue(); iter.Next()){
				sum += iter.GetCurrentValue();
			}
			return sum;
		}
	}

	/** 입력된 matrix 의 모든 entry들을 더한 결과를 리턴한다 
	 * @param matrix - Sum 을 수행할 CDMatrix
	 * @return double 로서 리턴한다
	 * @author swRyu@20080821
	 */
	public static double getSumOfAllEntries(CDMatrix matrix){
		
		if(matrix.isDense())
			return CAMatrixMathDense.sumOfAllElements(matrix.getArray());
		
		else{
			CDMatrix.Iter iter = matrix.getIter();
			CDMatrix.RowIter rowIter;		
			
			double sum = 0;
	 		for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next()){
				
				for (; rowIter.hasMoreValue(); rowIter.Next())
				{
					sum += rowIter.GetCurrentValue();
				}
			}
	 		return sum;
		}
	}
	
	/** 입력된 matrix 의 지정된 column 에 속한 모든 entry들을 더한 결과를 리턴한다 
	 * @param matrix - Sum 을 수행할 CDMatrix
	 * @param col - Sum을 수행할 column의 index
	 * @return double 로서 리턴한다
	 * @author swRyu@20080821
	 */
	public static double getSumOfAllEntries_column(CDMatrix matrix, int col){
		return getSumOfAllEntries( matrix.getCol(col) );
	}
	
	/** 입력된 matrix 의 지정된 row 에 속한 모든 entry들을 더한 결과를 리턴한다 
	 * @param matrix - Sum 을 수행할 CDMatrix
	 * @param row - Sum을 수행할 row의 index
	 * @return double 로서 리턴한다
	 * @author swRyu@20080821
	 */
	public static double getSumOfAllEntries_row(CDMatrix matrix, int row){
		return getSumOfAllEntries( matrix.getRow(row) );
	}
	
	/** 입력된 CDvector 의 모든 요소 중 가장 큰 값을 리턴한다.
	 */
	public static double getMaxOfAllEntries(CDVector vector){
		
		return vector.getMax();
	}

	/** 입력된 matrix 의 모든 요소 중 가장 큰 값을 리턴한다 
	 */
	public static double getMaxOfAllEntries(CDMatrix matrix){
		
		if(matrix.isDense()){
			return CAMatrixMathDense.maxOfAllElements(matrix.getArray());
		}
		else{
			CDMatrix.Iter iter = matrix.getIter();
			CDMatrix.RowIter rowIter;		
			
			double max;
			if(matrix.size()==matrix.cardinality())
				max = - Double.MAX_VALUE;
			else
				max = 0;
	 		for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next()){
				
				for (; rowIter.hasMoreValue(); rowIter.Next())
				{
					double currentVal = rowIter.GetCurrentValue();
					if(currentVal > max)
						max = currentVal;
				}
			}
	 		return max;
		}
	}
	
	/** 입력된 matrix 의 지정된 column 에 모든 요소 중 가장 큰 값을 리턴한다. 
	 */
	public static double getMaxOfAllEntries_column(CDMatrix matrix, int col){
		return getMaxOfAllEntries( matrix.getCol(col) );
	}
	
	/** 입력된 matrix 의 지정된 row 에 모든 요소 중 가장 큰 값을 리턴한다. 
	 */
	public static double getMaxOfAllEntries_row(CDMatrix matrix, int row){
		return getMaxOfAllEntries( matrix.getRow(row) );
	}
	
	/** 입력된 CDvector 의 모든 요소 들의 평균을 리턴한다.
	 */
	public static double average(CDVector vector){
		return vector.getSum()/vector.size();
	}
	
	/** 입력된 CDvector 의 모든 요소 들의 분산을 리턴한다.
	 */
	public static double variance(CDVector vector){
		
		return elementWisePow(vector,2).getSum()/vector.size() - Math.pow(average(vector),2);
		
	}
	
	
	public static CDMatrix identity(int size) {
		return CDMatrix.identity(size, false);

	}
	
	public static double norm(CDVector vector) {
		if(vector.isDense())
			return CAMatrixMathDense.norm(vector.getArray());
		return Math.sqrt(innerProduct(vector, vector));
	}
	
	/**********************************************************************************************
	 * 입력으로 받은 벡터들을 행으로 하는 벡터를 생성한다.
	 * 
	 * 
	 **********************************************************************************************/
	public static CDMatrix matrixFromRowVectors(CDVector[] rowVectors){
		
		
		int numberOfVectors = rowVectors.length;
		int lengthOfVector = rowVectors[0].size();
		
		int denseMat = 0;
		for(int i=0; i<numberOfVectors; i++){
			if(rowVectors[i].isDense()){
				denseMat++;
				break;
			}
		}
		
		CDMatrix ret;
		
		if(denseMat==numberOfVectors){
			double[][] dvectors = new double[numberOfVectors][];
			for(int i=0; i<numberOfVectors; i++){
				dvectors[i] = rowVectors[i].getArray();
			}
			ret = CDMatrix.dense(dvectors);
			return ret;
		}
		
		else{
			if(denseMat > numberOfVectors*0.1)
				ret = CDMatrix.dense(numberOfVectors, lengthOfVector);
			else
				ret = CDMatrix.sparse(numberOfVectors, lengthOfVector);
					
			for(int i =0; i<numberOfVectors; i++){
				CDVector.Iter iter = rowVectors[i].getIter(); 
				for(iter.Begin(); iter.hasMoreValue(); iter.Next()){
					ret.setQuick(i, iter.GetCurrentIndex(), iter.GetCurrentValue());
				}
			}
			
			return ret;
			
		}
	}
	
	
	/**********************************************************************************************
	 * 입력된 Matrix의 행과 열을 동일하게 확장한다.<br>
	 * 새롭게 생성된 행,열들은 모두 0으로 차있다. <br>
	 * 
	 * @param matrix
	 * @param nIndex - 삽입될 행,열의 위치
	 * @param nCount - 삽입될 행,열의 개수
	 * @return NMDoubleMatrix
	 **********************************************************************************************/
	public static CDMatrix expand(CDMatrix matrix, int nIndex, int nCount){
		
		CDMatrix expand;
		if(matrix.isDense()){
			expand = CDMatrix.dense(matrix.rows() + nCount, matrix.columns()+ nCount);
			expand.setArray(CAMatrixMathDense.expand(matrix.getArray(), nIndex, nCount));
			return expand;
		}
		else{	
			expand = CDMatrix.sparse(matrix.rows() + nCount, matrix.columns() + nCount);
			CDMatrix.Iter iter = matrix.getIter();
			CDMatrix.RowIter rowIter;
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					expand.setQuick(( i < nIndex ) ? i : i + nCount, ( j < nIndex ) ? j : j + nCount, matrix.getQuick(i, j));
				}
			}
			
			return expand;
		}
	}
	
	/**********************************************************************************************
	 * 입력된 Matrix의 행을 확장한다.<br>
	 * 새롭게 생성된 행들은 모두 0으로 차있다. <br>
	 * 
	 * @param matrix
	 * @param nIndex - 삽입될 행의 위치
	 * @param nCount - 삽입될 행의 개수
	 * @return NMDoubleMatrix
	 **********************************************************************************************/
	public static CDMatrix expandRow(CDMatrix matrix, int nIndex, int nCount){
		
		CDMatrix expand;
		if(matrix.isDense()){
			expand = CDMatrix.dense(matrix.rows() + nCount, matrix.columns());
			expand.setArray(CAMatrixMathDense.expandRow(matrix.getArray(), nIndex, nCount));
			return expand;
		}
		else{
			expand = CDMatrix.sparse(matrix.rows() + nCount, matrix.columns());
	
			CDMatrix.Iter iter = matrix.getIter();
			CDMatrix.RowIter rowIter;
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					expand.setQuick(( i < nIndex ) ? i : i + nCount, j, matrix.getQuick(i, j));
				}
			}
			
			return expand;
		}
	}
	
	/**********************************************************************************************
	 * 입력된 Matrix의 열을 확장한다.<br>
	 * 새롭게 생성된 열들은 모두 0으로 차있다. <br>
	 * 
	 * @param matrix
	 * @param nIndex - 삽입될 열의 위치
	 * @param nCount - 삽입될 열의 개수
	 * @return NMDoubleMatrix
	 **********************************************************************************************/
	public static CDMatrix expandColumn(CDMatrix matrix, int nIndex, int nCount){
		
		CDMatrix expand;
		if(matrix.isDense()){
			expand = CDMatrix.dense(matrix.rows(), matrix.columns() + nCount);
			expand.setArray(CAMatrixMathDense.expandColumn(matrix.getArray(), nIndex, nCount));
			return expand;
		}
		else{
			expand = CDMatrix.sparse(matrix.rows(), matrix.columns() + nCount);
			
			CDMatrix.Iter iter = matrix.getIter();
			CDMatrix.RowIter rowIter;
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					expand.setQuick(i, ( j < nIndex ) ? j : j + nCount, matrix.getQuick(i, j));
				}
			}
			
			return expand;
		}
	}
	
	public static CDVector expandVector(CDVector vector, int nIndex, int nCount)
	{
		
		CDVector extract;
		if(vector.isDense()){
			extract = CDVector.dense(vector.size()+nCount);
			extract.setArray(CAMatrixMathDense.expandVector(vector.getArray(), nIndex, nCount));
			return extract;
		}
		else{
			extract = CDVector.sparse(vector.size()+nCount);
			
			CDVector.Iter iter = vector.getIter(); 
			//불가피하게 패키지명까지 다 specify.. CDBigVector의 Iter 와 타입명 중복되기때문
			for(iter.Begin(); iter.hasMoreValue(); iter.Next()){
				int i = iter.GetCurrentIndex();
				extract.setQuick(( i < nIndex ) ? i : i + nCount ,iter.GetCurrentValue());
			}			
			return extract;
		}
	}
	
	public static String[] expandVector(String[] vector, int nIndex, int nCount)
	{
		String[] extract = new String[vector.length+nCount];
		
		for( int i = 0; i < nIndex; i++ )
    		extract[i] = vector[i];
		
		for( int i = nIndex; i < nIndex + nCount; i++ )
			extract[i] = "";
		
		for( int i = nIndex + nCount; i < vector.length + nCount; i++ )
    		extract[i] = vector[i-nCount];
		
		return extract;
	}
	
	/**
	 * 입력된 Matrix로부터 지정된 노드들로 구성된 subMatirx를 추출한다.<br>
	 * 
	 * @param CDMatrix matrix
	 * @param int[] nodes
	 * @return NMDoubleMatrix
	 */	
	public static CDMatrix extract(CDMatrix matrix, int[] nodes){
		
		final int n = nodes.length;
		
		CDMatrix extract;
		if(matrix.isDense()){
			extract = CDMatrix.dense(n,n);
			extract.setArray(CAMatrixMathDense.extract(matrix.getArray(), nodes));
			return extract;
		}
		else{
			extract = CDMatrix.sparse(n,n);
			// dense하게 하는 것이 빠를지, sparse하게 하는 것이 빠를지 판단한다.
			if ((long)n * n > matrix.cardinality()) {
	
				// 각 node 별로 새로운 index로의 맵을 만든다.
				HashMap<Integer, Integer> indexMap = new HashMap<Integer, Integer>(nodes.length, 1.0f);
				
				for (int i=0; i < nodes.length; i++) {
					indexMap.put(nodes[i], i);
				}
				
				CDMatrix.Iter iter = matrix.getIter();
				CDMatrix.RowIter rowIter;
				for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
					int i = rowIter.GetCurrentRowIndex();
					Integer newI = indexMap.get(i);
					if (newI == null)
						continue;
					for(; rowIter.hasMoreValue(); rowIter.Next() ){
						int j = rowIter.GetCurrentColumnIndex();
						Integer newJ = indexMap.get(j);
						if (newJ == null)
							continue;
						extract.set(newI, newJ, matrix.getQuick(i, j));
					}
				}
			}
			else {
				for( int i = 0; i < n; i++ )
		    		for( int j = 0; j < n; j++ )
		    			extract.setQuick(i, j, matrix.getQuick(nodes[i], nodes[j]));
			}
    	
    	return extract;
		}
	}
	
	/**
	 * 입력된 Matrix로부터 지정된 노드들로 구성된 subMatirx를 추출한다.<br>
	 * 단 focal과의 연결에 관계없는 link는 제외<br>
	 * 
	 * @param CDMatrix matrix
	 * @param int[] nodes
	 * @param int[] focals
	 * @return NMDoubleMatrix
	 */	
	public static CDMatrix extract( CDMatrix matrix, int[] nodes, int[] focals, CDVector distance ){
		int n = nodes.length;
		CDMatrix extract;
		if(matrix.isDense())
			extract = CDMatrix.dense(n, n);
		else
			extract = CDMatrix.sparse(n, n);
		
		for( int i = 0; i < n; i++ ){
			double distI = distance.getQuick(i);
    		for( int j = 0; j < n; j++ )
    			if( Math.abs(distI - distance.getQuick(j)) == 1 )
    				extract.setQuick(i, j, matrix.getQuick(nodes[i], nodes[j]));
		}
    	
    	return extract;
	}
	
	/**
	 * 입력된 Vector로부터 지정된 노드들로 구성된 subVector를 추출한다.<br>
	 * 
	 * @param CDVector vector
	 * @param int[] nodes
	 * @return NMDoubleVector
	 */
	public static CDVector extract( CDVector vector, int[] nodes ){
		int n = nodes.length;
		CDVector extract;
		if(vector.isDense()){
			extract = CDVector.dense(n);
			extract.setArray(CAMatrixMathDense.extract(vector.getArray(), nodes));
			return extract;
		}
		else{
			extract = CDVector.sparse(n);
			
			for( int i = 0; i < n; i++ )
				extract.set(i, vector.getQuick(nodes[i]));
			
			return extract;
		}
	}
	
	/**********************************************************************************************
	 * 입력된 Matrix로부터 지정된 index에 해당하는 행만을 추출한 subMatrix를 구한다.<br>
	 * extract()는 정사각 행렬에서 행과 열을 모두 추출하는 것이라면<br>
	 * extractColumn()는 일반 행렬에서 행만 추출한다.<br>
	 * 2-mode 데이터에서 지정된 category의 정보만 추출할때 사용한다.<br>	 
	 * 
	 * @param matrix
	 * @param index - 추출할 행의 index
	 * @return NMDoubleMatrix
	 **********************************************************************************************/
	public static CDMatrix extractRow(CDMatrix matrix, int[] index){
		int n = index.length;
		int m = matrix.columns();
		
		CDMatrix extract;
		if(matrix.isDense()){
			extract = CDMatrix.dense(n, m);
			extract.setArray(CAMatrixMathDense.extractRow(matrix.getArray(), index));
			return extract;
		}
		else{
			extract = CDMatrix.sparse(n, m);
			CDMatrix.Iter iter = matrix.getIter();
			CDMatrix.RowIter rowIter;
	
			for( int i = 0; i < index.length; i++ )
				for( rowIter = iter.Begin(index[i]); rowIter.hasMoreValue(); rowIter.Next() )
					extract.setQuick(i, rowIter.GetCurrentColumnIndex(), rowIter.GetCurrentValue());
			
			return extract;
		}
	}
	
	/**********************************************************************************************
	 * 입력된 Matrix로부터 지정된 index에 해당하는 열만을 추출한 subMatrix를 구한다.<br>
	 * extract()는 정사각 행렬에서 행과 열을 모두 추출하는 것이라면<br>
	 * extractColumn()는 일반 행렬에서 열만 추출한다.<br>
	 * 2-mode 데이터에서 지정된 노드의 정보만 추출할때 사용한다.<br>	 
	 * 
	 * @param matrix
	 * @param index - 추출할 열의 index
	 * @return NMDoubleMatrix
	 **********************************************************************************************/	
	public static CDMatrix extractColumn(CDMatrix matrix, int[] index){
		int n = matrix.rows();
		int m = index.length;
		
		CDMatrix extract;
		if(matrix.isDense()){
			extract = CDMatrix.dense(n, m);
			extract.setArray(CAMatrixMathDense.extractColumn(matrix.getArray(), index));
			return extract;
		}	
		else{
			extract = CDMatrix.sparse(n, m);
			for( int i = 0; i < n; i++ )
				for( int j = 0; j < m; j++ ){
					double value = matrix.getQuick(i, index[j]);
					if( value != 0 )
						extract.setQuick(i, j, value);
				}
				
			return extract;
		}
	}
	
	/**********************************************************************************************
	 * 입력된 Matrix로부터 지정된 index에 해당하는 열만을 추출한  vector를 구한다.<br>
	 * extract()는 정사각 행렬에서 행과 열을 모두 추출하는 것이라면<br>
	 * extractColumn()는 일반 행렬에서 열만 추출한다.<br>
	 * 2-mode 데이터에서 지정된 노드의 정보만 추출할때 사용한다.<br>	 
	 * 
	 * @param matrix
	 * @param index - 추출할 열의 index
	 * @return 추출된 vector
	 **********************************************************************************************/	
	public static CDVector extractColumn(CDMatrix matrix, int index){
		int n = matrix.rows();
		CDVector extract;
		if(matrix.isDense()){
			extract = CDVector.dense(n);
			extract.setArray(CAMatrixMathDense.extractColumn(matrix.getArray(), index));
			return extract;
		}		
		extract = CDVector.sparse(n);
		
		for( int i = 0; i < n; i++ )
		{
			double value = matrix.getQuick(i, index);
			if( value != 0 )
				extract.setQuick(i, value);
		}
    	return extract;
	}
	
	
	/**
	 * 입력된 vector로부터 지정된 filter에서 값이 0 아닌 값만을 추출한 subVector를 구한다.<br>
	 * 
	 * @param CDVector vector
	 * @param CDVector filter
	 * @return NMDoubleVector
	 */
	public static CDVector extract(CDVector vector, CDVector filter){
		int n = filter.cardinality();
		CDVector result;
		if(vector.isDense()&&filter.isDense()){
			result = CDVector.dense(n);
			result.setArray(CAMatrixMathDense.extract(vector.getArray(), filter.getArray()));
			return result;
		}
		else if(vector.isDense())
			result = CDVector.dense(n);
		else
			result = CDVector.sparse(n);
		CDVector.Iter iter = filter.getIter();
		
		int cnt = 0;
		for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
			int i = iter.GetCurrentIndex();
			result.setQuick(cnt++, vector.getQuick(i));
		}
		
		return result;
	}
	
	/**
	 * 입력된 String 배열로부터 지정된 filter에서 값이 0이 아닌 값만을 추출한 subVectro를 구한다.<br>
	 * 
	 * @param String[] strings
	 * @param NMDoubleVectorfilter
	 * @return String[]
	 */
	public static String[] extract(String[] strings, CDVector filter){
		int n = filter.cardinality();
		String[] result = new String[n];
		CDVector.Iter iter = filter.getIter();
		
		int cnt = 0;
		for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
			int i = iter.GetCurrentIndex();
			result[cnt++] = strings[i];
		}
		
		return result;
	}
	
	/**
	 * 입력된 Matrix의 row들을 구한다.
	 * 
	 * @param CDMatrix matrix
	 * @param int startrow, int endrow
	 * @return CDMatrix
	 */
	public static CDMatrix getRow(CDMatrix matrix, int startrow, int endrow) {
		if(startrow > endrow)
				return CDMatrix.sparse(endrow-startrow+1, matrix.columns());
		CDMatrix result;
		if(matrix.isDense()){
			result = CDMatrix.dense(endrow-startrow+1, matrix.columns());
			result.setArray(CAMatrixMathDense.extractRow(matrix.getArray(), startrow, endrow));
			return result;
		}
		else{
			result = CDMatrix.sparse(endrow-startrow+1, matrix.columns());
			CDMatrix.Iter iter = matrix.getIter();
			CDMatrix.RowIter rowIter;
		
			for( rowIter = iter.Begin(startrow); rowIter != null; rowIter = iter.Next() ){
				int i = rowIter.GetCurrentRowIndex();
				if (i > endrow) break;
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					result.setQuick(i-startrow, j, matrix.getQuick(i, j));
				}
			}
			return result;
		}	
	}

	/**
	 * 입력된 Matrix의 column들을 구한다.<br>
	 * 
	 * @param CDMatrix matrix
	 * @param int startrow, int endrow
	 * @return CDDoubleMatrix
	 */
	public static CDMatrix getColumn(CDMatrix matrix, int startrow, int endrow) {
		if(startrow > endrow)
			return CDMatrix.sparse(matrix.rows(), endrow-startrow+1);
		CDMatrix result;
		if(matrix.isDense())
			result = CDMatrix.dense(matrix.rows(), endrow-startrow+1);
		else
			result = CDMatrix.sparse(matrix.rows(), endrow-startrow+1);
		for (int i=0; i < result.rows(); i++)
			for (int j=0; j < result.columns(); j++)
				result.setQuick(i, j, matrix.getQuick(i, j+startrow));
		return result;
	}
	
	
	/**
	 * 입력된 Matrix의 scalar product를 구한다.<br>
	 * v[i] = s * v[i];
	 * 
	 * @param CDMatrix matrix
	 * @param double s
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix scalarProduct(CDMatrix matrix, double s) {
		int n = matrix.rows();
		int m = matrix.columns();
		
		if( s == 0 )
			return CDMatrix.sparse(n, m);
		
		CDMatrix result;
		if(matrix.isDense()){
			result = CDMatrix.dense(n,m);
			result.setArray(CAMatrixMathDense.scalarProduct(matrix.getArray(), s));
			return result;
		}
		else{
			result = matrix.copy();
			CDMatrix.Iter iter = matrix.getIter();
			CDMatrix.RowIter rowIter;
		
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					result.setQuick(i, j, matrix.getQuick(i, j) * s);
				}
			}	
			return result;
		}
	}

	/**
	 * 입력된 Vector의 scalar product를 구한다.<br>
	 * v[i] = s * v[i];
	 * 
	 * @param CDVector vector
	 * @param double s
	 * @return NMDoubleVector
	 */
	public static CDVector scalarProduct(CDVector vector, double s) {
		
		
		int n = vector.size();
		
		if( s == 0 )
			return CDVector.sparse(n);
		
		CDVector result;
		if(vector.isDense()){
			result = CDVector.dense(n);
			result.setArray(CAMatrixMathDense.scalarProduct(vector.getArray(), s));
			return result;
		}
		else{
			result = vector.copy();
			CDVector.Iter iter = vector.getIter();
		
			for(iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int i = iter.GetCurrentIndex();
				result.setQuick(i, result.getQuick(i) * s );
			}			
						
			return result;
		}
	}
	
	/**
	 * 입력된 Matrix를 scalar로 나눈다.<br>
	 * v[i] = s * v[i];
	 * 
	 * @param CDMatrix matrix
	 * @param double s
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix scalarDivide(CDMatrix matrix, double s) {
		
//		if (s == 0) return null;
		
		int n = matrix.rows();
		int m = matrix.columns();
		CDMatrix result;
		if(matrix.isDense()){
			result = CDMatrix.dense(n,m);
			result.setArray(CAMatrixMathDense.scalarDivide(matrix.getArray(), s));
			return result;
		}
		else{
			result = matrix.copy();
			CDMatrix.Iter iter = matrix.getIter();
			CDMatrix.RowIter rowIter;
			
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					result.setQuick(i, j, matrix.getQuick(i, j) / s);
				}
			}
			return result;
		}				
	}		
	
	/**
	 * 입력된 Vector를 scalar로 나눈다.<br>
	 * v[i] = s * v[i];
	 * 
	 * @param CDVector vector
	 * @param double s
	 * @return NMDoubleVector
	 */
	public static CDVector scalarDivide(CDVector vector, double s) {
		int n = vector.size();
		CDVector result;
		if(vector.isDense()){
			result = CDVector.dense(n);
			result.setArray(CAMatrixMathDense.scalarDivide(vector.getArray(), s));
			return result;
		}
		else{
			result = vector.copy();
			CDVector.Iter iter = vector.getIter();
		
			for(iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int i = iter.GetCurrentIndex();
				result.setQuick(i, result.getQuick(i) / s );
			}			
						
			return result;
		}
	}	
	
	/**
	 * 입력된 Matrix의 transpose를 구한다.<br>
	 * <b>Example:</b> 
	 * <table border="0">
	 * <tr nowrap> 
	 * 	<td valign="top">2 x 3 matrix: <br>
	 *	  1, 2, 3<br>
	 * 	  4, 5, 6 </td>
	 * 	<td>transpose ==></td>
	 * 	<td valign="top">3 x 2 matrix:<br>
	 * 	  1, 4 <br>
	 * 	  2, 5 <br>
	 * 	  3, 6</td>
	 * </tr>
	 * </table>
	 * 
	 * @param matrix
	 * @return
	 */
	public static CDMatrix transpose(CDMatrix matrix) {
		CDMatrix result;
		if(matrix.isDense()){
			result= CDMatrix.dense(matrix.columns(), matrix.rows());
			result.setArray(CAMatrixMathDense.transpose(matrix.getArray()));
			return result;
		}
		else{
			result= CDMatrix.sparse(matrix.columns(), matrix.rows(),(int)(matrix.cardinality()*2.5), 0.2, 0.5);
			CDMatrix.Iter iter = matrix.getIter();
			CDMatrix.RowIter rowIter;
			
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					result.setQuick(j, i, rowIter.GetCurrentValue());
				}
			}			
			
			return result;
		}
	}
	
	/**
	 * 입력된 Matrix와 Matrix간의 곱을 구한다.<br>
	 * 
	 * @param CDMatrix A
	 * @param CDMatrix B
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix multiply(CDMatrix A, CDMatrix B ){
		int m = A.rows();
		int n = A.columns();
		int p = B.columns();

		boolean BSparse=false;
		
		if( B.rows() != n )
			return null;
		CDMatrix C;
		if(A.isDense() && B.isDense()){
			C = CDMatrix.dense(m,p);
			C.setArray( CAMatrixMathDense.multiply(A.getArray(), B.getArray()));
			return C;
		}
		else if (A.isDense()) {
			BSparse=true;
			C = CDMatrix.dense(m,p);
		}
		else if(B.isDense()){
			C = CDMatrix.dense(m,p);
		}
		else
			C = CDMatrix.sparse(m, p);
		
		if(BSparse){
			B=transpose(B);
			CDMatrix.Iter iter = B.getIter();
			CDMatrix.RowIter rowIter;
			for (int j = m; --j >= 0; ) {
				for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
					double s = 0;
					int i = rowIter.GetCurrentRowIndex();
					for(; rowIter.hasMoreValue(); rowIter.Next() ){
						int k = rowIter.GetCurrentColumnIndex();
						s += A.getQuick(j,k) * B.getQuick(i,k);
					}
					
					C.setQuick(j, i, s);
				}
			}
			return C;
		}
		
		else{
			CDMatrix.Iter iter = A.getIter();
			CDMatrix.RowIter rowIter;
			
			for (int j = p; --j >= 0; ) {
				for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
					double s = 0;
					int i = rowIter.GetCurrentRowIndex();
					for(; rowIter.hasMoreValue(); rowIter.Next() ){
						int k = rowIter.GetCurrentColumnIndex();
						s += A.getQuick(i,k) * B.getQuick(k,j);
					}
					
					C.setQuick(i, j, s);
				}
			}
			return C;
		}
	}
	
	/**
	 * 입력된 Matirx와 Vector의 곱을 구한다.<br>
	 * 
	 * @param CDMatrix A
	 * @param CDVector vec
	 * @return NMDoubleVector
	 */
	public static CDVector multiply(CDMatrix A, CDVector vec){
		int m = A.rows();
		int n = A.columns();
		int p = vec.size();
		boolean BSparse=false;
		if( n != p )
			return null;
		
		CDVector C;
		if(A.isDense() && vec.isDense()){
			C= CDVector.dense(m);
			C.setArray(CAMatrixMathDense.multiply(A.getArray(), vec.getArray()));
			return C;
		}
		if(A.isDense()){
			BSparse=true;
			C= CDVector.dense(m);
		}
		else if(vec.isDense())
			C= CDVector.dense(m);
		
		else
			C= CDVector.sparse(m);
		
		if(BSparse){
			CDVector.Iter iter = vec.getIter();
			for (int i = m; --i >= 0; ) {
				double s = 0;
				for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
					int j = iter.GetCurrentIndex();
					s += A.getQuick(i,j) * vec.getQuick(j);
				}
				C.setQuick(i, s);
			}
			return C;
		}
		else{
			CDMatrix.Iter iter = A.getIter();
			CDMatrix.RowIter rowIter;
			
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				double s = 0;
				int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					s += A.getQuick(i,j) * vec.getQuick(j);
				}
				
				C.setQuick(i, s);
			}
				
			return C;
		}
	}

	/**
	 * 입력된 Vector와 Matrix의 곱을 구한다.<br>
	 * 
	 * @param CDMatrix A
	 * @param CDVector vec
	 * @return NMDoubleVector
	 */
	public static CDVector multiply(CDVector vec, CDMatrix B ){
		int m = B.rows();
		int n = B.columns();
		int p = vec.size();
		
		boolean BSparse=false;
		
		if( m != p )
			return null;
		CDVector C;
		if(B.isDense() && vec.isDense()){
			C= CDVector.dense(n);
			C.setArray(CAMatrixMathDense.multiply(vec.getArray(), B.getArray()));
			return C;
		}
		else if(B.isDense())
			C= CDVector.dense(n);
		else if(vec.isDense()){
			BSparse=true;
			C= CDVector.dense(n);
		}
		else{
			C= CDVector.sparse(n);
		}
		
		if(BSparse){
			B=transpose(B);
			CDMatrix.Iter iter = B.getIter();
			CDMatrix.RowIter rowIter;
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				double s = 0;
				int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int k = rowIter.GetCurrentColumnIndex();
					s += vec.getQuick(k) * B.getQuick(i,k);
				}
				C.setQuick(i, s);
			}
			return C;		
		}	
		else{
			CDVector.Iter iter = vec.getIter();
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ) {
				int i = iter.GetCurrentIndex();
				double vecnum = vec.getQuick(i);
				for(int j=0; j < n; j++)
					C.setQuick(j, C.getQuick(j)+vecnum*B.getQuick(i,j));
			}
			return C;
		}
		
	}
	
	/**
	 * 입력된 Vector와 Vector를 곱해서 Matrix를 구한다.<br>
	 * Matrix[i][j] = A[i] * B[j]<br>
	 * 
	 * @param CDVector A
	 * @param CDVector B
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix multiply(CDVector A, CDVector B ){
		int n = A.size();
		int m = B.size();
				
		CDMatrix C;
		if(A.isDense() && B.isDense()){
			C= CDMatrix.dense(n, m);
			C.setArray(CAMatrixMathDense.multiply(A.getArray(), B.getArray()));
			return C;
		}
		else
			C= CDMatrix.sparse(n, m);
		
		CDVector.Iter iter1 = A.getIter();
		CDVector.Iter iter2 = B.getIter();
		
		for( iter1.Begin(); iter1.hasMoreValue(); iter1.Next() ){
			int i = iter1.GetCurrentIndex();
			for( iter2.Begin(); iter2.hasMoreValue(); iter2.Next() ){
				int j = iter2.GetCurrentIndex();
				C.setQuick(i, j, A.getQuick(i) * B.getQuick(j));
			}
		}
				
		return C;
	}
	
	
	/**
	 * 입력된 Vector의 각 원소에 s를 더한다.
	 * 
	 * @param CDVector vector
	 * @param int s
	 * @return NMDoubleVector
	 */	
	public static CDVector scalarAdd(CDVector vector, double s)	{
		int n = vector.size();
		
		if (n == 0)
			return CDVector.sparse(0);
		
		if(s==0)
			return vector.copy();
		
		CDVector result=CDVector.dense(n);
		if(vector.isDense()){
			result.setArray(CAMatrixMathDense.scalarAdd(vector.getArray(), s));
			return result;
		}
		else{
			result.assign(s);
			CDVector.Iter iter = vector.getIter();
			
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int i = iter.GetCurrentIndex();
				result.setQuick(i, s + vector.getQuick(i));
			}
	
			return result;				
		}
	}
	
	/**
	 * 입력된 Matrix의 각 원소에 s를 더한다.
	 * 
	 * @param CDVector vector
	 * @param int s
	 * @return NMDoubleVector
	 */
	public static CDMatrix scalarAdd(CDMatrix matrix, double s) {
		
		int n = matrix.rows();
		int m = matrix.columns();
		
		if(s==0)
			return matrix.copy();
		
		CDMatrix result = CDMatrix.dense(n, m);
		
		if(matrix.isDense()){
			result.setArray(CAMatrixMathDense.scalarAdd(matrix.getArray(), s));
			return result;
		}
		else{
			
			result.assign(s);
			
			CDMatrix.Iter iter = matrix.getIter();
			CDMatrix.RowIter rowIter;
			
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					result.setQuick(i, j, matrix.getQuick(i, j) + s);
				}
			}
			
						
			return result;
		}
	}	
	
	/**
	 * 입력된 Vector의 각 원소와 s를 and 연산한다.
	 * 
	 * @param CDVector vector
	 * @param int s
	 * @return NMDoubleVector
	 */	
	public static CDVector scalarAnd(CDVector vector, double s)	{
		int n = vector.size();
		
		if(s==0)
			return CDVector.sparse(n);
		
		CDVector result;
		if(vector.isDense())
			result = CDVector.dense(n);
		
		else
			result = CDVector.sparse(n);
			
		CDVector.Iter iter = vector.getIter();
		
		for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
			int i = iter.GetCurrentIndex();
			result.setQuick(i, 1);
		}
		
		return result;
			
	}
	

	/**
	 * 입력된 Matrix의 각 원소에 s를 and 연산한다.
	 * 
	 * @param CDVector vector
	 * @param int s
	 * @return NMDoubleVector
	 */
	public static CDMatrix scalarAnd(CDMatrix matrix, double s) {
		
		int n = matrix.rows();
		int m = matrix.columns();
		
		if(s==0)
			return CDMatrix.sparse(n, m);

		CDMatrix result;
		if(matrix.isDense())
			result = CDMatrix.dense(n,m);
		
		else
			result = CDMatrix.sparse(n,m);
			
		CDMatrix.Iter iter = matrix.getIter();
		CDMatrix.RowIter rowIter;
		
		for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
			int i = rowIter.GetCurrentRowIndex();
			for(; rowIter.hasMoreValue(); rowIter.Next() ){
				int j = rowIter.GetCurrentColumnIndex();
				result.setQuick(i, j, 1);
			}
		}
		return result;
	}
	
	/**
	 * 입력된 Vector의 각 원소와 s를 or 연산한다.
	 * 
	 * @param CDVector vector
	 * @param int s
	 * @return NMDoubleVector
	 */	
	public static CDVector scalarOr(CDVector vector, double s)	{
		int n = vector.size();
		
		if(s==0){
			CDVector result;
			if(vector.isDense())
				result = CDVector.dense(n);
			
			else
				result = CDVector.sparse(n);
				
			CDVector.Iter iter = vector.getIter();
			
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int i = iter.GetCurrentIndex();
				result.setQuick(i, 1);
			}
			
			return result;
		}
		else{
			CDVector C = CDVector.dense(n);
			C.assign(1);
			return C;
		}
	}
	
	
	/**
	 * 입력된 Matrix의 각 원소에 s를 or 연산한다.
	 * 
	 * @param CDVector vector
	 * @param int s
	 * @return NMDoubleVector
	 */
	public static CDMatrix scalarOr(CDMatrix matrix, double s) {
		
		int n = matrix.rows();
		int m = matrix.columns();
		
		if(s==0){

			CDMatrix result;
			if(matrix.isDense())
				result = CDMatrix.dense(n,m);
			
			else
				result = CDMatrix.sparse(n,m);
				
			CDMatrix.Iter iter = matrix.getIter();
			CDMatrix.RowIter rowIter;
			
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					result.setQuick(i, j, 1);
				}
			}
			return result;
		}
		else
			return CDMatrix.dense(n, m, 1);
			
	}
	
	
	/**
	 * 입력된 두개 Vector의 합을 구한다.
	 * 
	 * @param CDVector A
	 * @param CDVector B
	 * @return NMDoubleVector
	 */	
	public static CDVector add(CDVector A, CDVector B)	{
		int n = A.size();
		int m = B.size();
		
		if(n!=m)
			return null;
		
		if (n == 0)
			return CDVector.sparse(0);
	
		CDVector C;
		if(A.isDense() && B.isDense()){
			C = CDVector.dense(n);
			C.setArray(CAMatrixMathDense.add(A.getArray(), B.getArray()));
			return C;
		}

		else if(B.isDense()){
			C= B.copy();
			CDVector.Iter iter = A.getIter();
			
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int i = iter.GetCurrentIndex();
				C.setQuick(i, C.getQuick(i) + A.getQuick(i));
			}
			return C;
		}
		else{
			C= A.copy();
			CDVector.Iter iter = B.getIter();
				
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int i = iter.GetCurrentIndex();
				C.setQuick(i, C.getQuick(i) + B.getQuick(i));
			}
					
			return C;
		}
	}
	
	
	/**
	 * 입력된 두개 Matrix의 합을 구한다.
	 * 
	 * @param CDMatrix A
	 * @param CDMatrix B
	 * @return NMDoubleMatrix
	 */	
	public static CDMatrix add(CDMatrix A, CDMatrix B)	{
		int n1 = A.rows();
		int n2 = B.rows();
		int m1 = A.columns();
		int m2 = B.columns();

		if(n1!=n2 || m1!=m2)
			return null;
		
		if (n1 == 0)
			return CDMatrix.sparse(0, 0);

		CDMatrix C;
		if(A.isDense() && B.isDense()){
			C = CDMatrix.dense(n1, m1);
			C.setArray(CAMatrixMathDense.add(A.getArray(), B.getArray()));
			return C;
		}
		else if(B.isDense()){
			C= B.copy();
			CDMatrix.Iter iter = A.getIter();
			CDMatrix.RowIter rowIter;
			
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					C.setQuick(i, j, C.getQuick(i, j) + A.getQuick(i, j));
				}
			}
			return C;
		}
		else{
			C = A.copy();
			CDMatrix.Iter iter = B.getIter();
			CDMatrix.RowIter rowIter;
		
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					C.setQuick(i, j, C.getQuick(i, j) + B.getQuick(i, j));
				}
			}	
			return C;
		}
	}
	
	
	/**
	 * 입력된 두개 Vector의 차를 구한다.
	 * 
	 * @param CDVector A
	 * @param CDVector B
	 * @return NMDoubleVector
	 */	
	public static CDVector difference(CDVector A, CDVector B)	{
		int n = A.size();
		int m = B.size();
		
		if (n != m)
			return null;
		
		if (n == 0)
			return CDVector.sparse(0);
		
		CDVector C;
		if(A.isDense() && B.isDense()){
			C = CDVector.dense(n);
			C.setArray(CAMatrixMathDense.difference(A.getArray(), B.getArray()));
			return C;
		}
		else if(A.isDense())
			C= A.copy();
		else if(B.isDense())
			C= A.toDense();
		else
			C= A.copy();
		CDVector.Iter iter = B.getIter();
				
		for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
			int i = iter.GetCurrentIndex();
			C.setQuick(i, C.getQuick(i) - B.getQuick(i));
		}
					
		return C;
	}

	/**
	 * 입력된 두개 Matrix의 차를 구한다.
	 * 
	 * @param CDMatrix A
	 * @param CDMatrix B
	 * @return NMDoubleMatrix
	 */	
	public static CDMatrix difference(CDMatrix A, CDMatrix B)	{
		
		int n = A.rows();
		int m = A.columns();
		if (n == 0)
			return CDMatrix.sparse(0, 0);
		CDMatrix C;
		if(B.isDense()){
			C= CDMatrix.dense(n, m);
			C.setArray(CAMatrixMathDense.difference(A.getArray(), B.getArray()));
			return C;
		}
		else if(A.isDense())
			C= A.copy();
		else
			C= A.copy();

		CDMatrix.Iter iter = B.getIter();
		CDMatrix.RowIter rowIter;
		
		for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
			int i = rowIter.GetCurrentRowIndex();
			for(; rowIter.hasMoreValue(); rowIter.Next() ){
				int j = rowIter.GetCurrentColumnIndex();
				C.setQuick(i, j, C.getQuick(i, j) - B.getQuick(i, j));
			}
		}
				
		return C;
	}
	

	public static CDVector subtract(CDVector A, CDVector B)	{
		return difference(A, B);
	}
	
	
	public static CDMatrix subtract(CDMatrix A, CDMatrix B)	{
		return difference(A, B);
	}
	
	
	/**
	 * 입력된 matrix의 trace를 구한다.<br>
	 * ( diagnol의 합을 구한다 )<br>
	 * 
	 * @param CDMatrix matrix
	 * @return double
	 */
	public static double trace(CDMatrix matrix){
		int n = matrix.rows();
		int m = matrix.columns();
		
		if( n != m )
			return -1;
		
		if(matrix.isDense()){
			return CAMatrixMathDense.trace(matrix.getArray());
		}
		
		double trace = 0;
		for( int i = 0; i < n; i++ )
			trace += matrix.getQuick(i, i);
		
		return trace;		
	}
	
	/**
	 * 입력된 Matrix의 diagonal을 주어진 값으로 치환한다.<br>
	 * 입력 Matrix가 square가 아닐 경우 아무것도 안하고 리턴한다.<br>
	 * 
	 * @param CDMatrix matrix
	 * @param double value
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix recodeDiagonal(CDMatrix matrix, double value){
		
		int n = matrix.rows();
		int m = matrix.columns();
		
		if( n != m )
			return matrix;
		
		CDMatrix result = matrix.copy();
		
		for( int i = 0 ; i < n; i++ )
			result.setQuick(i, i, value);
		
		return result;
	}
	
	/**
	 * 입력된 Matrix의 diagonal을 주어진 Vector로 치환한다.<br>
	 * 입력 Matrix가 square가 아닐 경우 아무것도 안하고 리턴한다.<br>
	 * 
	 * @param CDMatrix matrix
	 * @param CDVector vector
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix recodeDiagonal(CDMatrix matrix, CDVector vector){
		if( matrix.rows() != matrix.columns() || matrix.rows() != vector.size() )
			return matrix;
		
		CDMatrix result = matrix.copy();
		for( int i = 0 ; i < result.rows(); i++ )
			result.setQuick(i, i, vector.getQuick(i));
		
		return result;
	}	
	
	/**
	 * 입력된 Matrix의 diagnol을 구한다.<br>
	 * 
	 * @param CDMatrix matrix
	 * @return NMDoubleVector
	 */
	public static CDVector diag(CDMatrix matrix) {
		int n = matrix.rows();
		int m = matrix.columns();
		
		if( n!= m )
			return null;
		
		CDVector diagonal;
		if(matrix.isDense()){
			diagonal = CDVector.dense(n);
			diagonal.setArray(CAMatrixMathDense.diag(matrix.getArray()));
			return diagonal;
		}
		else{
			diagonal = CDVector.sparse(n);
			for( int i = 0; i < n; i++ )
			diagonal.setQuick(i, matrix.getQuick(i, i));			
			return diagonal;
		}
	}
	
	public static CDMatrix mergeMatrix(CDMatrix[] matrix, int option, double[] weights){
		CDMatrix compose = null;
		
		switch (option){
		case MERGE_AND:
			for( int layerIndex = 0; layerIndex < matrix.length; layerIndex++ ){
				if( layerIndex == 0 ){
					compose = matrix[layerIndex];
				} else
					compose = and(compose, matrix[layerIndex]);					
			}
			
			break;
			
		case MERGE_OR:
			for( int layerIndex = 0; layerIndex < matrix.length; layerIndex++ ){
				if( layerIndex == 0 ){
					compose = matrix[layerIndex];
				} else
					compose = or(compose, matrix[layerIndex]);					
			}
			
			break;
			
		case MERGE_SUM:
			for( int layerIndex = 0; layerIndex < matrix.length; layerIndex++ ){
				if( layerIndex == 0 ){
					compose = matrix[layerIndex];
				} else
					compose = add(compose, matrix[layerIndex]);
			}
			
			break;
		
		case MERGE_MAX:
			for( int layerIndex = 0; layerIndex < matrix.length; layerIndex++ ){
				if( layerIndex == 0 ){
					compose = matrix[layerIndex];
				} else
					compose = elementWiseMax(compose, matrix[layerIndex]);
			}
			
			break;
			
		case MERGE_MIN:
			for( int layerIndex = 0; layerIndex < matrix.length; layerIndex++ ){
				if( layerIndex == 0 ){
					compose = matrix[layerIndex];
				} else
					compose = elementWiseMin(compose, matrix[layerIndex]);						
			}
			
			break;
			
		case MERGE_AVERAGE:
			int cnt = 0;
			for( int layerIndex = 0; layerIndex < matrix.length; layerIndex++ ){
				if( layerIndex == 0 ){
					compose = matrix[layerIndex];
				} else
					compose = add(compose, matrix[layerIndex]);
				
				cnt++;
			}
			compose = scalarProduct(compose, 1/(double)cnt);
			
			break;
			
		case MERGE_LINEAR:
			for( int layerIndex = 0; layerIndex < matrix.length; layerIndex++ ){
				if( layerIndex == 0 ){
					compose = scalarProduct(matrix[layerIndex], weights[layerIndex]);
				} else
					compose = add(compose, scalarProduct(matrix[layerIndex], weights[layerIndex]));
			}
			
			break;
			
		default:
			break;
		}
		
		return compose;
	}
	
	public static CDMatrix mergeMatrix(CDMatrix[] matrix, CDVector filter, int option, CDVector vector)
	{
		int n = matrix[0].rows();
		boolean flag = false;
		CDMatrix compose = CDMatrix.sparse(n, n);
		CDVector.Iter iter = filter.getIter();
		
		switch (option){
		case MERGE_AND:
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int layerIndex = iter.GetCurrentIndex();
				if( flag == false ){
					compose = matrix[layerIndex];
					flag = true;
				} else
					compose = and(compose, matrix[layerIndex]);					
			}
			
			break;
			
		case MERGE_OR:
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int layerIndex = iter.GetCurrentIndex();
				if( flag == false ){
					compose = matrix[layerIndex];
					flag = true;
				} else
					compose = or(compose, matrix[layerIndex]);					
			}
			
			break;
			
		case MERGE_SUM:
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int layerIndex = iter.GetCurrentIndex();
				if( flag == false ){
					compose = matrix[layerIndex];
					flag = true;
				} else
					compose = add(compose, matrix[layerIndex]);
			}
			
			break;
		
		case MERGE_MAX:
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int layerIndex = iter.GetCurrentIndex();
				if( flag == false ){
					compose = matrix[layerIndex];
					flag = true;
				} else
					compose = elementWiseMax(compose, matrix[layerIndex]);
			}
			
			break;
			
		case MERGE_MIN:
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int layerIndex = iter.GetCurrentIndex();
				if( flag == false ){
					compose = matrix[layerIndex];
					flag = true;
				} else
					compose = elementWiseMin(compose, matrix[layerIndex]);						
			}
			
			break;
			
		case MERGE_AVERAGE:
			int cnt = 0;
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int layerIndex = iter.GetCurrentIndex();
				if( flag == false ){
					compose = matrix[layerIndex];
					flag = true;
				} else
					compose = add(compose, matrix[layerIndex]);
				
				cnt++;
			}
			compose = scalarProduct(compose, 1/cnt);
			
			break;
			
		case MERGE_LINEAR:
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int layerIndex = iter.GetCurrentIndex();
				if( flag == false ){
					compose = scalarProduct(matrix[layerIndex], vector.getQuick(layerIndex));
					flag = true;
				} else
					compose = add(compose, scalarProduct(matrix[layerIndex], vector.getQuick(layerIndex)));
			}
			
			break;
			
		default:
			break;
		}
		
		return compose;
	}
	
	
	/**
	 * 입력된 두개 Vector의 and를 구한다.<br>
	 * 둘다 0이 아니면 1로 세팅<br>
	 * 
	 * @param CDVector A
	 * @param CDVector B
	 * @return NMDoubleVector
	 */
	public static CDVector and(CDVector A, CDVector B){
		int n = A.size();
		int m = B.size();
		
		if( n!= m )
			return null;
		CDVector result;
		if(A.isDense() && B.isDense()){
			result = CDVector.dense(n);
			result.setArray(CAMatrixMathDense.and(A.getArray(), B.getArray()));
			if(result.cardinality()> result.size()* CDMatrix.THRESHOLD_DENSITY)
				return result;
			else
				return result.toSparse();
		}
		else if(A.isDense()){
			result = CDVector.sparse(n);
			CDVector.Iter iter = B.getIter();
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){ 
				int i = iter.GetCurrentIndex();
				if( A.getQuick(i) != 0 )
					result.setQuick(i, 1);
			}
			return result;
		}
		else{
			result = CDVector.sparse(n);
			CDVector.Iter iter = A.getIter();
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){ 
				int i = iter.GetCurrentIndex();
				if( B.getQuick(i) != 0 )
					result.setQuick(i, 1);
			}
			
			return result;
		}
	}
	
	/**
	 * 입력된 두개 Matrix의 and를 구한다.<br>
	 * 둘다 0이 아니면 1로 세팅<br>
	 * 
	 * @param CDMatrix A
	 * @param CDMatrix B
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix and(CDMatrix A, CDMatrix B){
		int n1 = A.rows();
		int n2 = B.rows();
		int m1 = A.columns();
		int m2 = B.columns();
		
		if( n1!= n2 || m1 != m2 )
			return null;
		
		CDMatrix result;
		if(A.isDense() && B.isDense()){
			result = CDMatrix.dense(n1,m1);
			result.setArray(CAMatrixMathDense.and(A.getArray(), B.getArray()));
			if(result.cardinality()> result.size()* CDMatrix.THRESHOLD_DENSITY)
				return result;
			else
				return result.toSparse();
		}
		else if(A.isDense()){
			result = CDMatrix.sparse(n1, m1);
			CDMatrix.Iter iter = B.getIter();
			CDMatrix.RowIter rowIter;
			
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){ 
				int i = rowIter.GetCurrentRowIndex();
				for( ; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					if( A.getQuick(i, j) != 0 )
						result.setQuick(i, j, 1);
				}
			}
			return result;
		}
		else{
			result = CDMatrix.sparse(n1, m1);
			CDMatrix.Iter iter = A.getIter();
			CDMatrix.RowIter rowIter;
		
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){ 
				int i = rowIter.GetCurrentRowIndex();
				for( ; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					if( B.getQuick(i, j) != 0 )
					result.setQuick(i, j, 1);
				}
			}
					
			return result;
		}
	}
	
	/**
	 * 입력된 두개 Vector의 or를 구한다.<br>
	 * 둘중 적어도 하나가 0이 아니면 1로 세팅<br>
	 * 
	 * @param CDVector A
	 * @param CDVector B
	 * @return NMDoubleVector
	 */
	public static CDVector or(CDVector A, CDVector B){
		int n = A.size();
		int m = B.size();
		
		if( n!= m )
			return null;
		
		CDVector result;
		if(A.isDense() && B.isDense()){
			result = CDVector.dense(n);
			result.setArray(CAMatrixMathDense.or(A.getArray(), B.getArray()));
			return result;
		}
		else if(A.isDense() || B.isDense())
			result = CDVector.dense(n);
		else
			result = CDVector.sparse(n, A.cardinality()*3, 0.2, 0.5);
			
		CDVector.Iter iter = A.getIter();
		for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){ 
			int i = iter.GetCurrentIndex();
			result.setQuick(i, 1);
		}
		
		iter = B.getIter();
		for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){ 
			int i = iter.GetCurrentIndex();
			result.setQuick(i, 1);
		}
					
		return result;
	}
	
	/**
	 * 입력된 두개 Matrix의 or를 구한다.<br>
	 * 둘중 적어도 하나가 0이 아니면 1로 세팅<br>
	 * 
	 * @param CDMatrix A
	 * @param CDMatrix B
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix or(CDMatrix A, CDMatrix B){
		int n1 = A.rows();
		int n2 = B.rows();
		int m1 = A.columns();
		int m2 = B.columns();
		
		if( n1!= n2 || m1 != m2 )
			return null;
		
		
		CDMatrix result;
		if(A.isDense() && B.isDense()){
			result = CDMatrix.dense(n1,m1);
			result.setArray(CAMatrixMathDense.or(A.getArray(), B.getArray()));
			return result;
		}
		
		else if(A.isDense() || B.isDense())
			result = CDMatrix.dense(n1,m1);
		
		else
			result = CDMatrix.sparse(n1,m1, A.cardinality()*3, 0.2, 0.5);
		
		CDMatrix.Iter iter = A.getIter();
		CDMatrix.RowIter rowIter;
		
		for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){ 
			int i = rowIter.GetCurrentRowIndex();
			for( ; rowIter.hasMoreValue(); rowIter.Next() ){
				int j = rowIter.GetCurrentColumnIndex();
				result.setQuick(i, j, 1);
			}
		}
		
		iter = B.getIter();
		for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){ 
			int i = rowIter.GetCurrentRowIndex();
			for( ; rowIter.hasMoreValue(); rowIter.Next() ){
				int j = rowIter.GetCurrentColumnIndex();
				result.setQuick(i, j, 1);
			}
		}
					
		return result;
	}
	

	public static boolean equals(CDMatrix A, CDMatrix B){
		int n1 = A.rows();
		int m1 = A.columns();
		int n2 = B.rows();
		int m2 = B.columns();
		
		if( n1!= n2 || m1!=m2 )
			return false;
		
		if(A.isDense()&&B.isDense()){
			return CAMatrixMathDense.equals(A.getArray(), B.getArray());
		}
		else{
			CDMatrix.Iter iter = A.getIter();
			CDMatrix.RowIter rowIter;
			
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){ 
				int i = rowIter.GetCurrentRowIndex();
				for( ; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					if (B.getQuick(i, j) != A.getQuick(i, j))
						return false;
				}
			}
			
			iter = B.getIter();
			
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){ 
				int i = rowIter.GetCurrentRowIndex();
				for( ; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					if (B.getQuick(i, j) != A.getQuick(i, j))
						return false;
				}
			}
						
			return true;
		}
	}
	
	/**
	 * 입력된 두개 Matrix간의 elementWiseMax을 구한다.
	 * C[i][j] = max(A[i][j], B[i][j]);
	 * 
	 * @param CDMatrix A
	 * @param CDMatrix B
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix elementWiseMax(CDMatrix A, CDMatrix B){
		int n1 = A.rows();
		int m1 = A.columns();
		int n2 = B.rows();
		int m2 = B.columns();
		
		if( n1!= n2 || m1!=m2 )
			return null;
		
		CDMatrix C; 
		if(A.isDense()&&B.isDense()){
			C = CDMatrix.dense(n1,m1);
			C.setArray(CAMatrixMathDense.elementWiseMax(A.getArray(), B.getArray()));
			return C;
		}
		else if(B.isDense() || A.isDense())
			C = CDMatrix.dense(n1,m1);
		else 
			C= CDMatrix.sparse(n1,m1);
		
		CDMatrix.Iter iter = A.getIter();
		CDMatrix.RowIter rowIter;
			
		for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
			int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					double value = rowIter.GetCurrentValue();
					if( B.getQuick(i, j) <= value )
						C.setQuick(i, j, value );
				}								
			}
		
		iter = B.getIter();
		
		for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
			int i = rowIter.GetCurrentRowIndex();
			for(; rowIter.hasMoreValue(); rowIter.Next() ){
				int j = rowIter.GetCurrentColumnIndex();
				double value = rowIter.GetCurrentValue();
				if( A.getQuick(i, j) <= value )
					C.setQuick(i, j, value );
			}								
		}
		
		return C;
	}
		
	/**
	 * 입력된 두개 Matrix간의 elementWiseMin을 구한다.
	 * C[i][j] = min(A[i][j], B[i][j]);
	 * 
	 * @param CDMatrix A
	 * @param CDMatrix B
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix elementWiseMin(CDMatrix A, CDMatrix B){
		int n1 = A.rows();
		int m1 = A.columns();
		int n2 = B.rows();
		int m2 = B.columns();
		
		if( n1!= n2 || m1!=m2 )
			return null;
		
		CDMatrix C; 
		if(A.isDense()&&B.isDense()){
			C = CDMatrix.dense(n1,m1);
			C.setArray(CAMatrixMathDense.elementWiseMin(A.getArray(), B.getArray()));
			return C;
		}
		else if(B.isDense() || A.isDense())
			C = CDMatrix.dense(n1,m1);
		else 
			C= CDMatrix.sparse(n1,m1);
		
		CDMatrix.Iter iter = A.getIter();
		CDMatrix.RowIter rowIter;
			
		for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
			int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					double value = rowIter.GetCurrentValue();
					if( B.getQuick(i, j) >= value )
						C.setQuick(i, j, value );
				}								
			}
		
		iter = B.getIter();
		
		for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
			int i = rowIter.GetCurrentRowIndex();
			for(; rowIter.hasMoreValue(); rowIter.Next() ){
				int j = rowIter.GetCurrentColumnIndex();
				double value = rowIter.GetCurrentValue();
				if( A.getQuick(i, j) >= value )
					C.setQuick(i, j, value );
			}								
		}
		
		return C;
	}
	
	/**
	 * 입력된 두개 Vector를 elementwise multiplication한다.<br>
	 * 둘다 0이 아니면 1로 세팅<br>
	 * 
	 * @param CDVector A
	 * @param CDVector B
	 * @return NMDoubleVector
	 */
	public static CDVector elementWiseMul(CDVector A, CDVector B){
		int n = A.size();
		int m = B.size();
		
		if( n!= m )
			return null;
		
		CDVector result;
		//둘다 Dense인 경우에만 Dense
		if(A.isDense() && B.isDense()){
			result = CDVector.dense(n);
			result.setArray(CAMatrixMathDense.elementWiseMul(A.getArray(), B.getArray()));
			return result;
		}
		
		else
			result = CDVector.sparse(n);
		if(A.isDense()){
			CDVector.Iter iter = B.getIter();
			
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){ 
				int i = iter.GetCurrentIndex();
				double val = A.getQuick(i);
				if( val != 0 )
				result.setQuick(i, val * B.getQuick(i));
			}

			return result;
		}
		else{
			CDVector.Iter iter = A.getIter();
		
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){ 
				int i = iter.GetCurrentIndex();
				double val = B.getQuick(i);
				if( val != 0 )
				result.setQuick(i, A.getQuick(i) * val);
			}

			return result;
		}			
	}	

	/**
	 * 입력된 두개 Matrix를 elementwise multiplication한다.<br>
	 * 둘다 0이 아니면 1로 세팅<br>
	 * 
	 * @param CDVector A
	 * @param CDVector B
	 * @return NMDoubleVector
	 */
	public static CDMatrix elementWiseMul(CDMatrix A, CDMatrix B){
		int n1 = A.rows();
		int m1 = A.columns();
		int n2 = B.rows();
		int m2 = B.columns();
		
		if( n1!= n2 || m1!=m2 )
			return null;
		
		CDMatrix result;
		//둘다 Dense인 경우에만 Dense
		if(A.isDense() && B.isDense()){
			result = CDMatrix.dense(n1, m1);
			result.setArray(CAMatrixMathDense.elementWiseMul(A.getArray(), B.getArray()));
			return result;
		}
		else
			result = CDMatrix.sparse(n1, m1);
		
		if(A.isDense()){
			CDMatrix.Iter iter = B.getIter();
			CDMatrix.RowIter rowIter;
			
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){ 
				int i = rowIter.GetCurrentRowIndex();
				for( ; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					double val = A.getQuick(i, j);
					if( val != 0 )
						result.setQuick(i, j, val*B.getQuick(i,j));
				}
			}
					
			return result;

		}
		
		else{
			CDMatrix.Iter iter = A.getIter();
			CDMatrix.RowIter rowIter;
		
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){ 
				int i = rowIter.GetCurrentRowIndex();
				for( ; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					double val = B.getQuick(i, j);
					if( val != 0 )
						result.setQuick(i, j, A.getQuick(i,j)*val);
				}
			}
					
			return result;
		}
	}
	
	/**
	 * 입력된 두개 Vector를 elementwise division한다.<br>
	 * 둘다 0이 아니면 1로 세팅<br>
	 * 
	 * @param CDVector A
	 * @param CDVector B
	 * @return NMDoubleVector
	 */
	public static CDVector elementWiseDiv(CDVector A, CDVector B){
		int n = A.size();
		int m = B.size();
		if( n!= m )
			return null;
		
		CDVector result;
		//A가 Sparse B가 Dense인 경우에만 sparse
		if(A.isDense() && B.isDense()){
			result = CDVector.dense(n);
			result.setArray(CAMatrixMathDense.elementWiseDiv(A.getArray(), B.getArray()));
			return result;
		}
		
		result = CDVector.dense(n);
		
		for (int i=0; i < n; i++) {
			result.setQuick(i, A.getQuick(i) / B.getQuick(i));
		}
		
		if((!(A.isDense()))&& (B.isDense())){
			if(result.cardinality() < CDMatrix.THRESHOLD_DENSITY * n)
			result = result.toSparse();
		}
		
		return result;
	}	

	/**
	 * 입력된 두개 Matrix를 elementwise division한다.<br>
	 * 둘다 0이 아니면 1로 세팅<br>
	 * 
	 * @param CDVector A
	 * @param CDVector B
	 * @return NMDoubleVector
	 */
	public static CDMatrix elementWiseDiv(CDMatrix A, CDMatrix B){
		int n1 = A.rows();
		int m1 = A.columns();
		int n2 = B.rows();
		int m2 = B.columns();
		
		if( n1!= n2 || m1!=m2 )
			return null;
		
		CDMatrix result;
		
		//A가 Sparse B가 Dense인 경우에만 sparse
		if(A.isDense()&& B.isDense()){
			result = CDMatrix.dense(n1, m1);
			result.setArray(CAMatrixMathDense.elementWiseDiv(A.getArray(), B.getArray()));
			return result;
		}
		else
			result = CDMatrix.dense(n1, m1);
			
		for (int i=0; i < n1; i++) {
			for (int j=0; j < m1; j++){
				result.setQuick(i, j, A.getQuick(i,j) / B.getQuick(i,j));
			}
		}
		
		if((!(A.isDense()))&& (B.isDense())){
			if(result.cardinality() < CDMatrix.THRESHOLD_DENSITY * n1*m1)
			result = result.toSparse();
		}
		
		return result;
	}	
	/**
	 * 입력된 Matrix를 elementwise power한다.<br>
	 * 
	 * @param CDVector A
	 * @param double pownum
	 * @return NMDoubleVector
	 */
	public static CDMatrix elementWisePow(CDMatrix A, double pownum){
		int n = A.rows();
		int m = A.columns();
		
		if (pownum == 0) return getFill(n, m, 1);
		
		CDMatrix result;
		if(A.isDense()){
			result = CDMatrix.dense(n, m);
			result.setArray(CAMatrixMathDense.elementWisePow(A.getArray(), pownum));
			return result;
		}
		else
			result = CDMatrix.sparse(n, m);
			CDMatrix.Iter iter = A.getIter();
			CDMatrix.RowIter rowIter;
		
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){ 
				int i = rowIter.GetCurrentRowIndex();
				for( ; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					result.setQuick(i, j, Math.pow(A.getQuick(i,j), pownum));
				}
		}
		return result;
	}
	
	/**
	 * 입력된 Vector를 elementwise power한다.<br>
	 * 
	 * @param CDVector A
	 * @param CDVector B
	 * @return NMDoubleVector
	 */
	public static CDVector elementWisePow(CDVector A, double pownum){
		int n = A.size();
		if (pownum == 0) return getFill(n, 1);
		
		
		CDVector result;
		if(A.isDense()){
			result = CDVector.dense(n);
			result.setArray(CAMatrixMathDense.elementWisePow(A.getArray(), pownum));
			return result;
		}
		else{
			result = CDVector.sparse(n);
			CDVector.Iter iter = A.getIter();
			
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){ 
				int i = iter.GetCurrentIndex();
					result.setQuick(i, Math.pow(A.getQuick(i),pownum));
			}
						
			return result;
		}
	}	
	
	/**
	 * 입력된 Matrix를 elementwise abs한다.<br>
	 * A[i][j]=|A[i][j]|
	 */
	public static CDMatrix elementWiseAbs(CDMatrix A){
		int n = A.rows();
		int m = A.columns();
		
		CDMatrix result;
		if(A.isDense()){
			result = CDMatrix.dense(n, m);
			result.setArray(CAMatrixMathDense.elementWiseAbs(A.getArray()));
			return result;
		}
		
		else{
			result = CDMatrix.sparse(n, m);
		
			CDMatrix.Iter iter = A.getIter();
			CDMatrix.RowIter rowIter;
		
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){ 
				int i = rowIter.GetCurrentRowIndex();
				for( ; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					result.setQuick(i, j, Math.abs(A.getQuick(i,j)));
				}
			}
			return result;
		}
	}
	
	/**
	 * 입력된 Matrix를 elementwise abs한다.<br>
	 * A[i]=|A[i]|
	 */
	public static CDVector elementWiseAbs(CDVector A){
		int n = A.size();
		CDVector result;
		if(A.isDense()){
			result = CDVector.dense(n);
			result.setArray(CAMatrixMathDense.elementWiseAbs(A.getArray()));
			return result;
		}
		else{
			result = CDVector.sparse(n);
			CDVector.Iter iter = A.getIter();
			
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){ 
				int i = iter.GetCurrentIndex();
				result.setQuick(i, Math.abs(A.getQuick(i)));
			}
						
			return result;
		}
	}	
	
	/**
	 * 
	 * 1로 가득찬 Matrix1D를 리턴한다
	 * 
	 * @param n
	 * @param arg
	 * @return
	 */
	public static CDVector getFill(int n, double arg) {
		CDVector result = CDVector.dense(n, arg);
		return result;
	}
	
	/**
	 * 
	 * 1로 가득찬 Matrix2D를 리턴한다.<br>
	 *
	 * @param n
	 * @param m
	 * @param arg
	 * @return
	 */
	public static CDMatrix getFill(int n, int m, double arg) {
		
		CDMatrix result = CDMatrix.dense(n, m, arg);
		return result;
	}
	
	/**
	 * 입력된 크기의 Identity Matrix를 구한다.
	 * 
	 * @param int size
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix getI(int size){
        CDMatrix identity = CDMatrix.identity(size,false);
        return identity;
    }
	
	/**
	 * 입력된 Vector를 diagnol로 갖는 Matrix를 만든다.<br>
	 * 
	 * @param CDVector vec
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix makeDiagonalMatrix(CDVector vec) {
		int n = vec.size();
		
		CDMatrix result = CDMatrix.sparse(n, n);
		CDVector.Iter iter = vec.getIter();
	
		for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
			int i = iter.GetCurrentIndex();
			result.setQuick(i, i, vec.getQuick(i));
		}
		
		return result;
	}

	
	/**
	 * 입력된 similarity Matrix를 dissimilarity로 바꾼다.<br>
	 * dissimilarity[i][j] = max + min - similarity[i][j];<br>
	 * ( similarity[i][j] != 0 일때 ) 
	 * 
	 * @param CDMatrix similarity
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix toDissimilarity(CDMatrix similarity){
		int n = similarity.rows();
		int m = similarity.columns();
		
		CDMatrix dissimilarity;
		if(similarity.isDense()){
			dissimilarity = CDMatrix.dense(n, m);
			dissimilarity.setArray(CAMatrixMathDense.toDissimilarity(similarity.getArray()));
			return dissimilarity;
		}
		else
			dissimilarity = CDMatrix.sparse(n, m);
		
		CDMatrix.Iter iter = similarity.getIter();
		CDMatrix.RowIter rowIter;
		
		double min = Double.MAX_VALUE;
		double max = 0;
		
		for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
			int i = rowIter.GetCurrentRowIndex();
			for( ; rowIter.hasMoreValue(); rowIter.Next() ){
				int j = rowIter.GetCurrentColumnIndex();
				if( i != j ){
					double value = similarity.getQuick(i, j);
					min = Math.min(min, value);
					max = Math.max(max, value);
				}
			}
		}
		
		
		if( min == Double.MAX_VALUE )
			min = 0;
		
		for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
			int i = rowIter.GetCurrentRowIndex();
			for( ; rowIter.hasMoreValue(); rowIter.Next() ){
				int j = rowIter.GetCurrentColumnIndex();
				if( i != j )
					dissimilarity.setQuick(i, j, max + min - similarity.getQuick(i, j));					
			}
		}
		
		return dissimilarity;
	}
	
	/**
	 * 입력된 dissimilarity Matrix를 similarity로 바꾼다.<br>
	 * similarity[i][j] = max + min - dissimilarity[i][j];<br>
	 * 
	 * @param CDMatrix dissimilarity
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix toSimilarity(CDMatrix dissimilarity){
		
		return toDissimilarity(dissimilarity);
	}
	
	/**
	 * 입력된 similarity Vector를 dissimilarity로 바꾼다.<br>
	 * dissimilarity[i] = 1 - similarity[i];<br>
	 * 
	 * @param CDVector similarity
	 * @return NMDoubleVector
	 */
	public static CDVector toDissimilarity(CDVector similarity){
		int n = similarity.size();
		
		CDVector dissimilarity;
		if(similarity.isDense()){
			dissimilarity = CDVector.dense(n);
			dissimilarity.setArray(CAMatrixMathDense.toDissimilarity(similarity.getArray()));
			if(dissimilarity.cardinality() < dissimilarity.size() * CDMatrix.THRESHOLD_DENSITY)
				return dissimilarity.toSparse();
			else
				return dissimilarity;
		}
		else{
			dissimilarity = CDVector.dense(n);
			for( int i = 0; i < n; i++ ){
				double value = similarity.getQuick(i);
				if( value != 1 )
					dissimilarity.setQuick(i, 1 - value);
			}
			
			return dissimilarity;
		}
	}
	
	/**
	 * 입력된 dissimilarity Vector를 similarity로 바꾼다.<br>
	 * similarity[i] = 1 - dissimilarity[i];<br>
	 * 
	 * @param CDVector dissimilarity
	 * @return NMDoubleVector
	 */
	public static CDVector toSimilarity(CDVector dissimilarity){
		
		return toDissimilarity(dissimilarity);
	}
	
	

	
	/**
	 * 입력된 vector를 이용해 difference Matrix를 생성한다.<br>
	 * matrix[i][j] = abs( vector[i] - vector[j] );<br>
	 * 
	 * @param CDVector vector
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix absoluteDifference(CDVector vector){
		int n = vector.size();
		CDMatrix matrix;
		
		if(vector.isDense()){
			matrix = CDMatrix.dense(n,n);
			matrix.setArray(CAMatrixMathDense.absoluteDifference(vector.getArray()));
			if(matrix.cardinality() < matrix.size() *CDMatrix.THRESHOLD_DENSITY)
				return matrix.toSparse();
			else
				return matrix;
		}
		
		else{
			matrix = CDMatrix.sparse(n, n);
			CDVector.Iter iter = vector.getIter();
			
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){ 
				int i = iter.GetCurrentIndex();
				for(int j=0; j<i; j++ ){ 
					double value = Math.abs(vector.getQuick(i) - vector.getQuick(j));
						if( value != 0 ){
							matrix.setQuick(i, j, value);
							matrix.setQuick(j, i, value);
						}
				}
			}

			return matrix;
		}
				
	}
	
	/**
	 * 입력된 vector를 이용해 exactMatch Matrix를 생성한다.<br>
	 * matrix[i][j] = ( vector[i] == vector[j] )? 1 : 0;<br>
	 * 
	 * @param CDVector vector
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix exactMatch(CDVector vector){
		int n = vector.size();
		CDMatrix matrix;
		
		if(vector.isDense()){
			matrix = CDMatrix.dense(n,n);
			matrix.setArray(CAMatrixMathDense.exactMatch(vector.getArray()));
			if(matrix.cardinality() < matrix.size() *CDMatrix.THRESHOLD_DENSITY)
				return matrix.toSparse();
			else
				return matrix;
		}
		
		else{
			matrix = CDMatrix.dense(n, n);
			for(int i=0; i<n; i++ ){ 
				for(int j=0; j<=i; j++ ){ 
					double value = (vector.getQuick(i) == vector.getQuick(j)) ? 1 : 0;
						if( value != 0 ){
							matrix.setQuick(i, j, value);
							matrix.setQuick(j, i, value);
						}
				}
			}
			return matrix;
		}
	}
		
	/**
	 * 입력된 두 Vector의 내적을 구한다.<br>
	 * 
	 * @param CDVector A
	 * @param CDVector B
	 * @return double
	 */
	public static double innerProduct(CDVector A, CDVector B){
		int n = A.size();
		int m = B.size();
		
		if( n!= m )
			return -1;
		
		else if(A.isDense() && B.isDense())
			return CAMatrixMathDense.innerProduct(A.getArray(), B.getArray());
		
		else if(A.isDense()){
			CDVector.Iter iter = B.getIter();
			double s = 0;

			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int i = iter.GetCurrentIndex();
				s += A.getQuick(i) * B.getQuick(i); 
			}
			
			return s;
		}
		else{
			CDVector.Iter iter = A.getIter();
			double s = 0;

			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int i = iter.GetCurrentIndex();
				s += A.getQuick(i) * B.getQuick(i); 
			}
			
			return s;
		}
	}
	
	/**
	 * Affiliation Matrix를 이용하여 Comembership // Overlap // Bipartite 을 만든다.<br>
	 * 
	 * @param CDMatrix matrix
	 * @param int option
	 * @return NMDoubleMatrix
	 */	
	public static CDMatrix affiliate(CDMatrix matrix, int option){
		CDMatrix affiliate;
		CDMatrix.Iter iter, iterB;
		CDMatrix.RowIter rowIter, rowIterB;
		
		int n = matrix.rows();
		int m = matrix.columns();
		
		switch (option)
		{
		case AFFILIATE_COMEMBERSHIP:
			if(matrix.isDense()){
				affiliate = CDMatrix.dense(n, n);
				affiliate.setArray(CAMatrixMathDense.comembership(matrix.getArray()));
				return affiliate;
			}
			else{
				affiliate = CDMatrix.sparse(n, n);
				iter = matrix.getIter();
				iterB = matrix.getIter();
				
				for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
					int i = rowIter.GetCurrentRowIndex();
					for( rowIterB = iterB.Begin(0); rowIterB != null; rowIterB = iterB.Next() ){
						int j = rowIterB.GetCurrentRowIndex();
						double inProduct = 0;
						for(; rowIterB.hasMoreValue(); rowIterB.Next() ){
							int k = rowIterB.GetCurrentColumnIndex();
							inProduct += matrix.getQuick(i, k) * matrix.getQuick(j, k);
						}
						
						if( inProduct != 0 )
							affiliate.setQuick(i, j, inProduct);
					}
				}
			}
			break;
			
		case AFFILIATE_OVERLAP:
			if(matrix.isDense()){
				affiliate = CDMatrix.dense(m, m);
				affiliate.setArray(CAMatrixMathDense.overLap(matrix.getArray()));
				return affiliate;
			}
			else{
				affiliate = CDMatrix.sparse(m, m);
				CDMatrix transposeMatrix = transpose(matrix); 
				iter = transposeMatrix.getIter();
				iterB = transposeMatrix.getIter();
				
				for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
					int i = rowIter.GetCurrentRowIndex();
					for( rowIterB = iterB.Begin(0); rowIterB != null; rowIterB = iterB.Next() ){
						int j = rowIterB.GetCurrentRowIndex();
						double inProduct = 0;
						for(; rowIterB.hasMoreValue(); rowIterB.Next() ){
							int k = rowIterB.GetCurrentColumnIndex();
							inProduct += transposeMatrix.getQuick(i, k) * transposeMatrix.getQuick(j, k);
						}
						
						if( inProduct != 0 )
							affiliate.setQuick(i, j, inProduct);
					}
				}
			}
			break;
			
		case AFFILIATE_BIPARTITE:
			affiliate = CDMatrix.sparse(n + m, n + m);
			iter = matrix.getIter();
			
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					double value = matrix.getQuick(i, j);
					affiliate.setQuick(i, j + n, value);
					affiliate.setQuick(j + n, i, value);
				}
			}			
			break;
		
		case AFFILIATE_BIPARTITE_SINGLE:
			affiliate = CDMatrix.sparse(n + m, n + m);
			iter = matrix.getIter();
			
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					double value = matrix.getQuick(i, j);
					affiliate.setQuick(j + n, i, value);
				}
			}			
			break;
			
		default:
			affiliate = null;
			
		}
		if(affiliate.cardinality() > affiliate.size()*CDMatrix.THRESHOLD_DENSITY)
			affiliate = affiliate.toDense();

		
		return affiliate;
	}
	
	
	
	/**********************************************************************************************
	 * 입력된 Matrix를 주어진 option에 따라 0과 1로 리코딩한다.<br>
	 * 일반적인 경우 이 메쏘드를 사용하여 dichotomize 한다.<br>  
	 *  
	 * @param CDMatrix matrix
	 * @param double threshold 
	 * @param int option
	 * @param boolean includeZero
	 * @return NMDoubleMatrix
	 **********************************************************************************************/
	public static CDMatrix dichotomize(CDMatrix matrix, double threshold, int option, boolean includeZero){
		int n = matrix.rows();
		int m = matrix.columns();
		CDMatrix dichotomize = CDMatrix.sparse(n, m);
		CDMatrix.Iter iter = matrix.getIter();
		CDMatrix.RowIter rowIter;
				
		if( includeZero == true ) //0도 전부 고려해야하는 경우.
			if( (option == DICHOTOMIZE_GT && threshold < 0)
					|| (option == DICHOTOMIZE_GE && threshold <= 0)
					|| (option == DICHOTOMIZE_EQ && threshold == 0)
					|| (option == DICHOTOMIZE_LE && threshold >= 0)
					|| (option == DICHOTOMIZE_LT && threshold > 0) ){
				
				return dichotomizeDense(matrix, threshold, option, includeZero);
			}
		
		if(matrix.isDense()){
			return dichotomizeDense(matrix, threshold, option, includeZero);
		}
		
	
		switch( option ){
			case DICHOTOMIZE_GT:
				for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
					int i = rowIter.GetCurrentRowIndex();
					for( ; rowIter.hasMoreValue(); rowIter.Next() ){
						int j = rowIter.GetCurrentColumnIndex();
						if( matrix.getQuick(i, j) > threshold )
							dichotomize.setQuick(i, j, 1);
					}
				}
				break;
				
			case DICHOTOMIZE_GE:
				for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
					int i = rowIter.GetCurrentRowIndex();
					for( ; rowIter.hasMoreValue(); rowIter.Next() ){
						int j = rowIter.GetCurrentColumnIndex();
						if( matrix.getQuick(i, j) >= threshold )
							dichotomize.setQuick(i, j, 1);
					}
				}
				break;
				
			case DICHOTOMIZE_EQ:
				for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
					int i = rowIter.GetCurrentRowIndex();
					for( ; rowIter.hasMoreValue(); rowIter.Next() ){
						int j = rowIter.GetCurrentColumnIndex();
						if( matrix.getQuick(i, j) == threshold )
							dichotomize.setQuick(i, j, 1);
					}
				}
				break;
				
			case DICHOTOMIZE_LE:
				for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
					int i = rowIter.GetCurrentRowIndex();
					for( ; rowIter.hasMoreValue(); rowIter.Next() ){
						int j = rowIter.GetCurrentColumnIndex();
						if( matrix.getQuick(i, j) <= threshold )
							dichotomize.setQuick(i, j, 1);
					}
				}
				break;
				
			case DICHOTOMIZE_LT:
				for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
					int i = rowIter.GetCurrentRowIndex();
					for( ; rowIter.hasMoreValue(); rowIter.Next() ){
						int j = rowIter.GetCurrentColumnIndex();
						if( matrix.getQuick(i, j) < threshold )
							dichotomize.setQuick(i, j, 1);
					}
				}
				break;
			}
		
		return dichotomize;
	}

	/**********************************************************************************************
	 * 입력된 Matrix를 주어진 option에 따라 0과 1로 리코딩한다.<br>
	 * 0도 recode의 대상이 되는 경우에 호출된다.<br>
	 * 일반적으로 dichotomize()를 호출하면 필요한 경우 알아서 dichotomizeDense()를 호출하기 때문에 <br>
	 * 사용자가 직접 이 메쏘드를 호출할 일은 적을 것이다.<br>  
	 *  
	 * @param CDMatrix matrix
	 * @param double threshold 
	 * @param int option
	 * @return NMDoubleMatrix
	 **********************************************************************************************/
	public static CDMatrix dichotomizeDense(CDMatrix matrix, double threshold, int option, boolean includeZero){
		int n = matrix.rows();
		int m = matrix.columns();
		double[][] dMatrix = matrix.toArray();
		double[][] dDichotomize = new double[n][m]; 
		double val;
		
		switch( option ){
			case DICHOTOMIZE_GT:
				for( int i = 0; i < n; i++ ){
					for( int j = 0; j < m; j++ ){
						val = dMatrix[i][j];
						if( (includeZero || val!=0) && val > threshold )
							dDichotomize[i][j] = 1;
					}			
				}
				break;
				
			case DICHOTOMIZE_GE:
				for( int i = 0; i < n; i++ ){
					for( int j = 0; j < m; j++ ){
						val = dMatrix[i][j];
						if( (includeZero || val!=0) && val >= threshold )
							dDichotomize[i][j] = 1;
					}			
				}
				break;
				
			case DICHOTOMIZE_EQ:
				for( int i = 0; i < n; i++ ){
					for( int j = 0; j < m; j++ ){
						val = dMatrix[i][j];
						if( (includeZero || val!=0) && val == threshold )
							dDichotomize[i][j] = 1;
					}			
				}
				break;
				
			case DICHOTOMIZE_LE:
				for( int i = 0; i < n; i++ ){
					for( int j = 0; j < m; j++ ){
						val = dMatrix[i][j];
						if( (includeZero || val!=0) && val <= threshold )
							dDichotomize[i][j] = 1;
					}			
				}
				break;
				
			case DICHOTOMIZE_LT:
				for( int i = 0; i < n; i++ ){
					for( int j = 0; j < m; j++ ){
						val = dMatrix[i][j];
						if( (includeZero || val!=0) && val < threshold )
							dDichotomize[i][j] = 1;
					}			
				}
				break;
			}
		
		CDMatrix sDichotomize = CDMatrix.dense(n,m);
		sDichotomize.setArray(dDichotomize);
		
		if(sDichotomize.cardinality() < sDichotomize.size()*CDMatrix.THRESHOLD_DENSITY){
			sDichotomize = sDichotomize.toSparse();
		}
	
		return sDichotomize;
		
	}
	
	/**********************************************************************************************
	 * 입력된 Vector 주어진 option에 따라 0과 1로 리코딩한다.<br>
	 * 일반적인 경우 이 메쏘드를 사용하여 dichotomize 한다.<br>  
	 *  
	 * @param CDVector vector
	 * @param double threshold 
	 * @param int option
	 * @param boolean includeZero
	 * @return NMDoubleVector
	 **********************************************************************************************/
	public static CDVector dichotomize(CDVector vector, double threshold, int option, boolean includeZero)
	{
		int n = vector.size();
		CDVector dichotomize = CDVector.sparse(n);
		CDVector.Iter iter = vector.getIter();
		
		if( includeZero == true ) 
			if( (option == DICHOTOMIZE_GT && threshold < 0)
					|| (option == DICHOTOMIZE_GE && threshold <= 0)
					|| (option == DICHOTOMIZE_EQ && threshold == 0)
					|| (option == DICHOTOMIZE_LE && threshold >= 0)
					|| (option == DICHOTOMIZE_LT && threshold > 0) ){
				
				return dichotomizeDense(vector, threshold, option, includeZero);
			}
		
		if(vector.isDense()){
			return dichotomizeDense(vector, threshold, option, includeZero);
		}
		
		switch( option ){
		case DICHOTOMIZE_GT:
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() )
			{
				// missing이 들어온 경우
				if( iter.GetCurrentValue() == CA.NM_GLOBAL_MISSING )
				{
					dichotomize.setQuick(iter.GetCurrentIndex(), CA.NM_GLOBAL_MISSING);
					continue;
				}
				if( iter.GetCurrentValue() > threshold )
					dichotomize.setQuick(iter.GetCurrentIndex(), 1);
				
			}
			
			break;
			
		case DICHOTOMIZE_GE:
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() )
			{
				// missing이 들어온 경우
				if( iter.GetCurrentValue() == CA.NM_GLOBAL_MISSING )
				{
					dichotomize.setQuick(iter.GetCurrentIndex(), CA.NM_GLOBAL_MISSING);
					continue;
				}

				if( iter.GetCurrentValue() >= threshold )
					dichotomize.setQuick(iter.GetCurrentIndex(), 1);
				
			}
			break;
			
		case DICHOTOMIZE_EQ:
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() )
			{
				// missing이 들어온 경우
				if( iter.GetCurrentValue() == CA.NM_GLOBAL_MISSING )
				{
					dichotomize.setQuick(iter.GetCurrentIndex(), CA.NM_GLOBAL_MISSING);
					continue;
				}

				if( iter.GetCurrentValue() == threshold )
					dichotomize.setQuick(iter.GetCurrentIndex(), 1);
				
			}
			break;
		case DICHOTOMIZE_LE:
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() )
			{
				// missing이 들어온 경우
				if( iter.GetCurrentValue() == CA.NM_GLOBAL_MISSING )
				{
					dichotomize.setQuick(iter.GetCurrentIndex(), CA.NM_GLOBAL_MISSING);
					continue;
				}

				if( iter.GetCurrentValue() <= threshold )
					dichotomize.setQuick(iter.GetCurrentIndex(), 1);
				
			}
			break;
			
		case DICHOTOMIZE_LT:
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() )
			{
				// missing이 들어온 경우
				if( iter.GetCurrentValue() == CA.NM_GLOBAL_MISSING )
				{
					dichotomize.setQuick(iter.GetCurrentIndex(), CA.NM_GLOBAL_MISSING);
					continue;
				}

				if( iter.GetCurrentValue() < threshold )
					dichotomize.setQuick(iter.GetCurrentIndex(), 1);
				
			}
			break;
		}
		
		
		
		return dichotomize;
	}
	
	
	
	/**********************************************************************************************
	 * 입력된 Vector 주어진 option에 따라 0과 1로 리코딩한다.<br>
	 * 0도 recode의 대상이 되는 경우에 호출된다.<br>
	 * 일반적으로 dichotomize()를 호출하면 필요한 경우 알아서 dichotomizeDense()를 호출하기 때문에 <br>
	 * 사용자가 직접 이 메쏘드를 호출할 일은 적을 것이다.<br>  
	 *  
	 * @param CDVector vector
	 * @param double threshold 
	 * @param int option
	 * @return NMDoubleVector
	 **********************************************************************************************/
	public static CDVector dichotomizeDense(CDVector vector, double threshold, int option, boolean includeZero){
		int n = vector.size();
		double[] dMatrix = vector.toArray();
		double[] dDichotomize = new double[n];
		double val;
		switch( option ){
		case DICHOTOMIZE_GT:
			for( int i = 0; i < n; i++ ){
				// missing이 들어온 경우 넘어간다.
				if( dMatrix[i]==CA.NM_GLOBAL_MISSING ){
					dDichotomize[i] = CA.NM_GLOBAL_MISSING;
					continue;
				}

				val = dMatrix[i];
				if((includeZero || val!=0) &&  val > threshold )
					dDichotomize[i] = 1;	
			}
			break;
			
		case DICHOTOMIZE_GE:
			for( int i = 0; i < n; i++ ){
				// missing이 들어온 경우 넘어간다.
				if( dMatrix[i]==CA.NM_GLOBAL_MISSING ){
					dDichotomize[i] = CA.NM_GLOBAL_MISSING;
					continue;
				}

				val = dMatrix[i];
				if((includeZero || val!=0) &&  val >= threshold )
					dDichotomize[i] = 1;	
			}	
			break;
			
		case DICHOTOMIZE_EQ:
			for( int i = 0; i < n; i++ ){
				// missing이 들어온 경우 넘어간다.
				if( dMatrix[i]==CA.NM_GLOBAL_MISSING ){
					dDichotomize[i] = CA.NM_GLOBAL_MISSING;
					continue;
				}

				val = dMatrix[i];
				if((includeZero || val!=0) &&  val == threshold )
					dDichotomize[i] = 1;	
			}	
			break;
			
		case DICHOTOMIZE_LE:
			for( int i = 0; i < n; i++ ){
				// missing이 들어온 경우 넘어간다.
				if( dMatrix[i]==CA.NM_GLOBAL_MISSING ){
					dDichotomize[i] = CA.NM_GLOBAL_MISSING;
					continue;
				}

				val = dMatrix[i];
				if((includeZero || val!=0) &&  val <= threshold )
					dDichotomize[i] = 1;	
			}	
			break;
			
		case DICHOTOMIZE_LT:
			for( int i = 0; i < n; i++ ){
				// missing이 들어온 경우 넘어간다.
				if( dMatrix[i]==CA.NM_GLOBAL_MISSING ){
					dDichotomize[i] = CA.NM_GLOBAL_MISSING;
					continue;
				}

				val = dMatrix[i];
				if((includeZero || val!=0) &&  val < threshold )
					dDichotomize[i] = 1;	
			}	
			break;
		}			
	
		CDVector sDichotomize = CDVector.dense(n);
		sDichotomize.setArray(dDichotomize);
		
		if(sDichotomize.cardinality() < sDichotomize.size()*CDMatrix.THRESHOLD_DENSITY)
			sDichotomize = sDichotomize.toSparse();
		
		return sDichotomize;
		
	}
	
	/**********************************************************************************************
	 * 입력된 Matrix에 주어진 filter를 적용시킨다.<br>
	 * null로 입력된 필터는 없는 것으로 보고 적용시키지 않는다.<br>
	 *  
	 * @param int[] nodeFilter
	 * @param double[][] edgeFilter
	 * @param CDMatrix matrix
	 * @return NMDoubleMatrix
	 **********************************************************************************************/
	public static CDMatrix filterMatrix( int[] nodeFilter, double[][] edgeFilter, CDMatrix matrix ){
		CDMatrix filteredM;
		
		if( edgeFilter == null ){
			filteredM = matrix.copy();
			
		}
		else{
			
			if(matrix.isDense()){
				filteredM = CDMatrix.dense(matrix.rows(), matrix.columns());
				filteredM.setArray(CAMatrixMathDense.filterMatrix(nodeFilter, edgeFilter, matrix.getArray()));
				if(filteredM.cardinality() < filteredM.size()*CDMatrix.THRESHOLD_DENSITY)
					filteredM = filteredM.toSparse();
			}
			
			else{
				filteredM = CDMatrix.sparse(matrix.rows(), matrix.columns());
				CDMatrix.Iter iter = matrix.getIter();
				CDMatrix.RowIter rowIter;
				
				for (rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next()) {
					int row = rowIter.GetCurrentRowIndex();
					for (; rowIter.hasMoreValue(); rowIter.Next()) {
						int column = rowIter.GetCurrentColumnIndex();
						double value = rowIter.GetCurrentValue();
						boolean include = false;
						for (int i = 0; i < edgeFilter.length; i++) {
							if (value >= edgeFilter[i][0] && value <= edgeFilter[i][1]){
								include = true;
								break;
							}
						}
					
					if( include == true )
						filteredM.setQuick(row, column, value);
					}
				}
			}
		}
		
		if( nodeFilter == null )
			return filteredM;
		else
			return extract(filteredM, nodeFilter);		
	}
	


	
	/**
	 * 입력된 Matrix의 diagnol을 모두 0으로 바꾼다.<br>
	 * 정사각행렬이 아니면 아무것도 수행하지 않는다.<br>
	 * 
	 * @param CDMatrix matrix
	 */
	public static void removeDiagonal(CDMatrix matrix){
		int n = matrix.rows();
		int m = matrix.columns();
		
			
		if( n!= m )
			return;
		

		if(matrix.isDense()){
			CAMatrixMathDense.removeDiagonal(matrix.getArray());
		}
		
		else{
			for(int i=0; i < matrix.rows(); i++)
				matrix.setQuick(i, i, 0);
		}
			
		
		return;
	}
	
	/**
	 * 입력된 Matrix를 주어진 option에 따라 symmetrize 한다.<br>
	 * 
	 * @param CDMatrix matrix
	 * @param int option
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix symmetrize(CDMatrix matrix, int option){
		int n = matrix.rows();
		int m = matrix.columns();
		
		if( n!= m )
			return matrix;
		CDMatrix symmetrized;
		
		if(matrix.isDense()){
			symmetrized = CDMatrix.dense(n, n);
			symmetrized.setArray(CAMatrixMathDense.symmetrize(matrix.getArray(), option));
			return symmetrized;
		}
		
		symmetrized = CDMatrix.sparse(n, n); 
		CDMatrix.Iter iter = matrix.getIter();
		CDMatrix.RowIter rowIter;
		
		for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
			int i = rowIter.GetCurrentRowIndex();
			for(; rowIter.hasMoreValue(); rowIter.Next() ){
				int j = rowIter.GetCurrentColumnIndex();				
				if( i == j )
					symmetrized.setQuick(i, i, matrix.getQuick(i, i));
				
				else{
					double value;
					double value1 = matrix.getQuick(i, j);
					double value2 = matrix.getQuick(j, i);
					
					// 두 값 중 missing이 있을 경우 처리.
					if( (value1==CA.NM_GLOBAL_MISSING || value2==CA.NM_GLOBAL_MISSING) 
						&& (option!=SYMMETRIZE_LOWER || option!=SYMMETRIZE_UPPER) )
					{
						symmetrized.setQuick(i, j, CA.NM_GLOBAL_MISSING);
						symmetrized.setQuick(j, i, CA.NM_GLOBAL_MISSING);
						continue;
					}
					
					switch( option ){
					case SYMMETRIZE_MAX:
						value = Math.max(value1, value2);
						symmetrized.setQuick(i, j, value);
						symmetrized.setQuick(j, i, value);
						break;
						
					case SYMMETRIZE_MIN:
						value = Math.min(value1, value2);
						symmetrized.setQuick(i, j, value);
						symmetrized.setQuick(j, i, value);
						break;
						
					case SYMMETRIZE_AVERAGE:
						value = (value1 + value2) / 2.0;
						symmetrized.setQuick(i, j, value);
						symmetrized.setQuick(j, i, value);
						break;
						
					case SYMMETRIZE_SUM:
						value = value1 + value2;
						symmetrized.setQuick(i, j, value);
						symmetrized.setQuick(j, i, value);
						break;
						
					case SYMMETRIZE_PRODUCT:
						value = value1 * value2;
						symmetrized.setQuick(i, j, value);
						symmetrized.setQuick(j, i, value);
						break;
												
					case SYMMETRIZE_UPPER:
						symmetrized.setQuick(i, j, i < j ? value1 : value2 );
						symmetrized.setQuick(j, i, i < j ? value1 : value2 );
						break;
					case SYMMETRIZE_LOWER:
						symmetrized.setQuick(i, j, i > j ? value1 : value2 );
						symmetrized.setQuick(j, i, i > j ? value1 : value2 );
					}
				}
			}
		}
				
		return symmetrized;
	}
	
	/**
	 * 입력된 Matrix를 기본 옵션으로 symmetrize, dichotomize 한다.<br>
	 * 
	 * @param CDMatrix matrix
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix symmDichMatrix(CDMatrix matrix){
		
		return dichotomize(symmetrize(matrix, SYMMETRIZE_MAX), 0, DICHOTOMIZE_GT, true);
	}
	
	
	/**
	 * 입력된 Matrix에서 값이 oldStart[i]~oldEnd[i]인 element를 찾아 newVal[i]로 recoding 한다.<br>
	 * diagnolLock이 true이거나 square matrix가 아니면 diagnol 값은 건드리지 않는다.<br>
	 * 
	 * @param CDMatrix matrix
	 * @param double[] oldVal
	 * @param double[] newVal
	 * @param boolean diagonalLock
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix recode(CDMatrix matrix, double[] oldStart, double[] oldEnd, double[] newVal, boolean diagonalLock){
		int n = matrix.rows();
		int m = matrix.columns();		
		
		if( m != n )
			diagonalLock = false;
		
		CDMatrix recode = matrix.copy();
		CDMatrix.Iter iter = matrix.getIter();
		CDMatrix.RowIter rowIter;
		
		for( int k = 0; k < oldStart.length; k++ ){
			if( oldStart[k] > 0 || oldEnd[k] < 0 )
				for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
					int i = rowIter.GetCurrentRowIndex();
					for(; rowIter.hasMoreValue(); rowIter.Next() ){
						int j = rowIter.GetCurrentColumnIndex();
						if( i!= j || diagonalLock == false ) {
							double value = matrix.getQuick(i, j);
							if( value >= oldStart[k] && value <= oldEnd[k] )
								recode.setQuick(i, j, newVal[k]);
						}
						else{
							;// do nothing
						}
					
					}
				}
			
			else
				for( int i = 0; i < n; i++ ){
					for( int j = 0; j < m; j++ ){
						if( i!= j || diagonalLock == false ) {
							double value = matrix.getQuick(i, j);
							if( value >= oldStart[k] && value <= oldEnd[k] )
								recode.setQuick(i, j, newVal[k]);
						}
						else{
							;// do nothing							
						}
					}
				}
			
		}
				
		return recode;
	}
	
	/**
	 * 입력된 Vector에서 값이 oldStart[i]~oldEnd[i]인 element를 찾아 newVal[i]로 recoding 한다.<br>
	 * 
	 * @param CDVector vector
	 * @param double[] oldVal
	 * @param double[] newVal
	 * @return NMDoubleVector
	 */
	public static CDVector recode(CDVector vector, double[] oldStart, double[] oldEnd, double[] newVal){
		int n = vector.size();
		
		CDVector recode = vector.copy();
		CDVector.Iter iter = vector.getIter();
		
		for( int k = 0; k < oldStart.length; k++ ){
			if( oldStart[k] > 0 || oldEnd[k] < 0 )
				for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
					int i = iter.GetCurrentIndex();
					double value = vector.getQuick(i);
					if( value >= oldStart[k] && value <= oldEnd[k] )
						recode.setQuick(i, newVal[k]);
							
				}
			
			else
				for( int i = 0; i < n; i++ ){
					double value = vector.getQuick(i);
					if( value >= oldStart[k] && value <= oldEnd[k] )
						recode.setQuick(i, newVal[k]);
				}
		}
				
		return recode;
	}
	
	/**
	 * 입력된 Matrix에서 값이 oldVal[i]인 element를 찾아 newVal[i]로 recoding 한다.<br>
	 * diagnolLock이 true이거나 square matrix가 아니면 diagnol 값은 건드리지 않는다.<br>
	 * 
	 * @param CDMatrix matrix
	 * @param double[] oldVal
	 * @param double[] newVal
	 * @param boolean diagonalLock
	 * @return NMDoubleMatrix
	 */
	public static CDMatrix recode(CDMatrix matrix, double[] oldVal, double[] newVal, boolean diagonalLock){
		int n = matrix.rows();
		int m = matrix.columns();
		
		CDMatrix recode;
		if(matrix.isDense()){
			recode = CDMatrix.dense(CAMatrixMathDense.recode(matrix.getArray(), oldVal, newVal, diagonalLock), false); 
			return recode;
		}
		else{
			
			recode = matrix.copy();
			
			for(int i=0 ; i<oldVal.length; i++){
				if(oldVal[i]==0 && newVal[i]!=0){
					recode = matrix.toDense();
					break;
				}
			}
			
			CDMatrix.Iter iter = matrix.getIter();
			CDMatrix.RowIter rowIter;
			
			if (m!=n) diagonalLock = false;
	
			for( int k = 0; k < oldVal.length; k++ ){
				if( oldVal[k] != 0 )
					for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
						int i = rowIter.GetCurrentRowIndex();
						for(; rowIter.hasMoreValue(); rowIter.Next() ){
							int j = rowIter.GetCurrentColumnIndex();
							if( i!= j || diagonalLock == false ) {
								if( matrix.getQuick(i, j) == oldVal[k] )
									recode.setQuick(i, j, newVal[k]);
							}
							else{
								;// do nothing
							}
						
						}
					}
				
				else
					for( int i = 0; i < n; i++ ){
						for( int j = 0; j < m; j++ ){
							if( i!= j || diagonalLock == false ) {
								if( matrix.getQuick(i, j) == oldVal[k] )
									recode.setQuick(i, j, newVal[k]);
							}
							else{
								;// do nothing
							}
						}
					}
				
			}		
			return recode;
		}
	}
	
	/**
	 * 입력된 Vector에서 값이 oldVal[i]인 element를 찾아 newVal[i]로 recoding 한다.<br>
	 * 
	 * @param CDVector vector
	 * @param double[] oldVal
	 * @param double[] newVal
	 * @return NMDoubleVector
	 */
	public static CDVector recode(CDVector vector, double[] oldVal, double[] newVal){
		int n = vector.size();
		
		CDVector recode;
		if(vector.isDense()){
			recode = CDVector.dense(n);
			recode.setArray(CAMatrixMathDense.recode(vector.getArray(), oldVal, newVal)); 
			return recode;
		}
		
		recode = vector.copy();
		
		for(int i=0 ; i<oldVal.length; i++){
			if(oldVal[i]==0 && newVal[i]!=0){
				recode = vector.toDense();
				break;
			}
		}
		
		CDVector.Iter iter = vector.getIter();
		
		for( int k = 0; k < oldVal.length; k++ ){
			if( oldVal[k] != 0 )
				for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
					int i = iter.GetCurrentIndex();
					if( vector.getQuick(i) == oldVal[k] )
						recode.setQuick(i, newVal[k]);
							
				}
			
			else
				for( int i = 0; i < n; i++ ){
					if( vector.getQuick(i) == oldVal[k] )
						recode.setQuick(i, newVal[k]);
				}
		}
				
		return recode;
	}

	
	/**
	 * 입력된 Matrix 값을 뒤집어 준다.<br>
	 * 
	 * @param CDMatrix cost
	 * @param boolean includeZero
	 * @param boolean includeDiagonal
	 * @return NMDoubleVector
	 */
	public static CDMatrix toReverse(CDMatrix matrix)
	{
		return toReverse(matrix,false);
	}
 	public static CDMatrix toReverse(CDMatrix matrix, boolean includeZero)
	{
		return toReverse(matrix,includeZero,true);
	}
	public static CDMatrix toReverse(CDMatrix matrix, boolean includeZero, boolean processDiagonal)
	{
		return toReverse(matrix,includeZero,processDiagonal,REVERSE_INTERVAL,0.0);
	}
	
	public static CDMatrix toReverse(CDMatrix matrix, boolean includeZero, boolean processDiagonal, int reverseOption, double reverseValue )
	{
		int n = matrix.rows();
		int m = matrix.columns();
		

		CDMatrix resultMatrix;
		if(matrix.isDense()){
			resultMatrix = CDMatrix.dense(n,m);
			resultMatrix.setArray(CAMatrixMathDense.toReverse(matrix.getArray(), includeZero, processDiagonal, reverseOption, reverseValue)); 
			return resultMatrix;
		}
		
		else if(includeZero){
			resultMatrix = CDMatrix.dense(n,m);
			resultMatrix.setArray(CAMatrixMathDense.toReverse(matrix.getArray(), includeZero, processDiagonal, reverseOption, reverseValue)); 
			return resultMatrix;
		}
		
		else{
	    	double min = Double.MAX_VALUE;
			double max = -Double.MAX_VALUE;
	
			if ( reverseOption == REVERSE_INTERVAL )
			{
				CDMatrix.Iter iter = matrix.getIter();
		    	CDMatrix.RowIter rowIter;
				
				for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() )
				{
					int i = rowIter.GetCurrentRowIndex();
					for( ; rowIter.hasMoreValue(); rowIter.Next() )
					{
						int j = rowIter.GetCurrentColumnIndex();
						
						if( i == j && n == m && processDiagonal == false )
							continue;
	
						double value = matrix.getQuick(i, j);
						min = Math.min(min, value);
						max = Math.max(max, value);
					}
				}
	
				// 비어있는 경우에는 min과 max를 0으로 한다.
				if( matrix.cardinality() == 0 )
				{
					min = 0;
					max = 0;
				}
	
			}
    		// 0을 포함시키지 않는 경우에는 Sparse하게 돈다.
			resultMatrix = CDMatrix.sparse(n, m);
			CDMatrix.Iter iter = matrix.getIter();
	    	CDMatrix.RowIter rowIter;
			
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() )
			{
				int i = rowIter.GetCurrentRowIndex();
				for( ; rowIter.hasMoreValue(); rowIter.Next() )
				{
					int j = rowIter.GetCurrentColumnIndex();
					
					if( i == j && n == m && processDiagonal == false )
					{
						resultMatrix.setQuick(i, j, matrix.getQuick(i, j));
						continue;
					}
					
					switch ( reverseOption )
					{
					case REVERSE_INTERVAL:
						resultMatrix.setQuick(i, j, max + min - matrix.getQuick(i, j));
						break;
					case REVERSE_RATIO:
						resultMatrix.setQuick(i, j, 1.0 / matrix.getQuick(i, j));
						break;
					case REVERSE_FIXED_DECAY:
						resultMatrix.setQuick(i, j, Math.pow(reverseValue,matrix.getQuick(i, j)));
						break;
					default:
						System.out.println("Oh No");
					}
				}
			}
			return resultMatrix;
		}
	}
	
	public static CDVector toReverse(CDVector vector, boolean includeZero, int reverseOption, double reverseValue )
	{
    	double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		
		int n = vector.size();
		
		CDVector resultVector;
		if(vector.isDense()){
			resultVector = CDVector.dense(n);
			resultVector.setArray(CAMatrixMathDense.toReverse(vector.getArray(), includeZero, reverseOption, reverseValue)); 
			return resultVector;
		}
		
		else if(includeZero){
			resultVector = CDVector.dense(n);
			resultVector.setArray(CAMatrixMathDense.toReverse(vector.getArray(), includeZero, reverseOption, reverseValue)); 
			return resultVector;
		}
		else{
			if ( reverseOption == REVERSE_INTERVAL )
			{
				CDVector.Iter iter = vector.getIter();
				
				for( iter.Begin(); iter.hasMoreValue(); iter.Next() )
				{
					double value = iter.GetCurrentValue();
					min = Math.min(min, value);
					max = Math.max(max, value);
				}
	
				// 비어있는 경우에는 min과 max를 0으로 한다.
				if( vector.cardinality() == 0 )
				{
					min = 0;
					max = 0;
				}
				
				// Zero를 포함하여 계산할 경우에는 max와 min의 결과에 0을 고려해 준다.
				if( includeZero == true && vector.cardinality() != vector.size() )
				{
					min = Math.min(min, 0.0);
					max = Math.max(max, 0.0);
				}
	
			}
			resultVector =  CDVector.sparse(n);
		
			CDVector.Iter iter = vector.getIter();
			
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int i = iter.GetCurrentIndex();
				double value = vector.getQuick(i);
				switch ( reverseOption ){
				
				case REVERSE_INTERVAL:
					resultVector.setQuick(i, max + min - value);
					break;
				case REVERSE_RATIO:
					resultVector.setQuick(i, 1.0 / value);
					break;
				case REVERSE_FIXED_DECAY:
					resultVector.setQuick(i, Math.pow(reverseValue,value));
					break;
				default:
					System.out.println("Oh No");
				}
								
			}
			return resultVector;
		}
	}
	
	/**
	 * capacity 를 cost 로 변환한다. <i>toCapacity</i> 와 연산이 같다.
	 *
	 * 2003년 7월 2일
	 * 김세권 수정 - Include/Exclude '0' Option 추가
	 *
	 */
	
	public static CDMatrix toCost(CDMatrix capacity, boolean includableZero)
	{
		
		int n = capacity.rows();
		int m = capacity.columns();
		
		CDMatrix cost; 
		if(capacity.isDense()){
			cost = CDMatrix.dense(n,m);
			cost.setArray(CAMatrixMathDense.toCost(capacity.getArray(), includableZero));
			return cost;
		}
		else if(includableZero){
			cost = CDMatrix.dense(n,m);
			cost.setArray(CAMatrixMathDense.toCost(capacity.getArray(), includableZero));
			return cost;
		}
		else{
			cost = CDMatrix.sparse(n,m);
			double min = Double.MAX_VALUE;
			double max = 0;
			
			CDMatrix.Iter iter = capacity.getIter();
	    	CDMatrix.RowIter rowIter;
			
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() )
			{
				int i = rowIter.GetCurrentRowIndex();
				for( ; rowIter.hasMoreValue(); rowIter.Next() )
				{
					int j = rowIter.GetCurrentColumnIndex();
					
					if(i!=j){
						double value = rowIter.GetCurrentValue();
						min = Math.min(min, value);
						max = Math.max(max, value);
					}
				}
			}
		
			if (min==Double.MAX_VALUE) min = 0;
			
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() )
			{
				int i = rowIter.GetCurrentRowIndex();
				for( ; rowIter.hasMoreValue(); rowIter.Next() )
				{
					int j = rowIter.GetCurrentColumnIndex();
					if(i!=j){
						cost.setQuick(i,j, max+min-rowIter.GetCurrentValue());
					}
				}
			}
			
			return cost;
		}
	}

	/**
	 * 입력된 Matrix를 1행, 2행, 3행, ... , n행의 순서로 붙여서 Vector로 만든다.<br> 
	 * 
	 * @param CDMatrix matrix
	 * @param boolean diagonal
	 * @return NMDoubleVector
	 */
	public static CDVector toVector(CDMatrix matrix, boolean diagonal){
		int n = matrix.rows();
		int m = matrix.columns();
		
		if( n != m )
			diagonal = true;
		
		CDVector vector;
		if(matrix.isDense()){
			if( diagonal == false )
				vector = CDVector.dense( n * m - n );
			else
				vector = CDVector.dense( n * m );
			vector.setArray(CAMatrixMathDense.toVector(matrix.getArray(), !diagonal));
			return vector;
		}
		
		else{
	
			if( diagonal == false )
				vector = CDVector.sparse( n * m - n );
			else
				vector = CDVector.sparse( n * m );
		
			CDMatrix.Iter iter = matrix.getIter();
			CDMatrix.RowIter rowIter;
		
			if( diagonal == false ){
				for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
					int i = rowIter.GetCurrentRowIndex();
					for(; rowIter.hasMoreValue(); rowIter.Next() ){
						int j = rowIter.GetCurrentColumnIndex();
						if( i != j ){
							int index = i * (m - 1) + ((i > j) ? j : j - 1);
							vector.setQuick( index, matrix.getQuick(i, j));
						}
					}
				}
			}
			else{ 
				for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
					int i = rowIter.GetCurrentRowIndex();
					for(; rowIter.hasMoreValue(); rowIter.Next() ){
						int j = rowIter.GetCurrentColumnIndex();
							vector.setQuick( i * m + j, matrix.getQuick(i, j));
					}
				}
			}
			return vector;
		}
	}
	
	public static CDVector toVector(CDMatrix matrix, boolean diagonal, boolean useEntire){
		
		if(useEntire){
			return toVector(matrix, diagonal);
		}
		
		int n = matrix.rows();
		int m = matrix.columns();
		
		if( n != m )
			diagonal = true;
		
		CDVector vector;
		
		if(matrix.isDense()){
			if( diagonal == false )
				vector = CDVector.dense( n * m - n );
			else
				vector = CDVector.dense( n * m );
			vector.setArray(CAMatrixMathDense.toVector(matrix.getArray(), !diagonal, useEntire));
			return vector;
		}
		
		if( diagonal == false )
			vector = CDVector.sparse( n * m - n );
		else
			vector = CDVector.sparse( n * m );
		
		CDMatrix.Iter iter = matrix.getIter();
		CDMatrix.RowIter rowIter;
		if( diagonal == false ) {
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					if(i>=j)
						continue;
					
					vector.setQuick( i * (m - 1) + (j - 1), matrix.getQuick(i, j));
					vector.set(j * (m - 1) + i, matrix.getQuick(i, j));
				}
			}
		}
		else{
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					if(i>j)
						continue;
					
					vector.setQuick( i * m + j, matrix.getQuick(i, j));
					vector.setQuick(j * m + i, matrix.getQuick(i, j));
				}
			}
		}
		
		return vector;
	}

	
	/**
	 * Square Matrix를 toVector로 만든 결과 Vector를
	 * 다시 Matrix로 복구한다.
	 * @param vector
	 * @param diagonal
	 * @return
	 */
	public static CDMatrix toMatrix(CDVector vector, boolean diagonal){
		int n;
		if (diagonal){
			n = (int)Math.sqrt(vector.size());
		} else {
			n = (int)(0.5*(1+ Math.sqrt(1 + 4*vector.size())));
		}
		
		CDMatrix matrix;
		if(vector.isDense()){
			matrix = CDMatrix.dense(n,n);
			matrix.setArray(CAMatrixMathDense.toMatrix(vector.getArray(), diagonal));
			return matrix;
		}
		else{
			matrix = CDMatrix.sparse(n,n);
			CDVector.Iter iter = vector.getIter();		
			
			int columnSize = diagonal ? n : n - 1;
				
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int i = iter.GetCurrentIndex();
				int row =  i / columnSize;
				int col = i % columnSize;
				if (!diagonal && col >= row) col++;
				matrix.setQuick(row, col, vector.getQuick(i));
			}
			return matrix;
		}
	}
	
	public static CDMatrix toWideMatrix(CDVector vector){
		int n = vector.size();
		CDMatrix matrix;
		if(vector.isDense()){
		 matrix= CDMatrix.dense(1, n);
		 matrix.setArray(CAMatrixMathDense.toWideMatrix(vector.getArray()));
		 return matrix;
		}
		else{
			matrix = CDMatrix.sparse(1,n);
			CDVector.Iter iter = vector.getIter();
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int i = iter.GetCurrentIndex();
				matrix.setQuick(0, i, vector.getQuick(i));
			}	
			return matrix;
		}
	}

	
	public static CDMatrix rank(CDMatrix matrix, boolean asc, boolean ignoreSameValue, int[] numOfDifferectValue, int offset){
		// 오름차순으로 정렬할 것인지 내림차순으로 정렬할 것인지 결정한다.
		
		if(matrix.isDense()){
			return CDMatrix.dense(CAMatrixMathDense.rank(matrix.getArray(), asc, ignoreSameValue, numOfDifferectValue, offset));
		}
		else{
			ValueIndexPair.ascending = asc;
			
			int n = matrix.rows();
			int m = matrix.columns();
			int cardinality = matrix.cardinality();
			
			// Iteration 을 준비하자.
			CDMatrix.Iter iter = matrix.getIter(); iter.init();
			CDMatrix.RowIter rowIter;
			
			int nIndex = 0;
	    	ValueIndexPair[] pairs = new ValueIndexPair[cardinality];
	    	for ( rowIter = iter.getRow(0); rowIter != null; rowIter = iter.Next() )
	    	{
	    		for ( ; rowIter.hasMoreValue(); rowIter.Next() )
	    		{
	    			// 2차원 배열의 index를 1차원으로 변경시켜서 집어넣는다.
	    			pairs[nIndex] = new ValueIndexPair(rowIter.GetCurrentValue(),rowIter.GetCurrentRowIndex()*n + rowIter.GetCurrentColumnIndex());
	    			nIndex++;
	    		}
	    	}
	    	
	    	// Sort 한다.
	    	java.util.Arrays.sort(pairs);
	    	
	    	// 결과를 기록한다.
	    	CDMatrix result = CDMatrix.sparse(n,m,cardinality*2,0.2,0.5);
	    	
	    	if ( ignoreSameValue ){
	    		numOfDifferectValue[0] = 1;	// value 갯수 (1.0 과  2.0으로 이루어진 matrix 경우에는 2)
	    		int sameValueCount = -1;	// 첫 값을 pairs[0] 으로 잡았기 때문에 sameValueCount 를 0 이 아니라 -1 로 설정해 놓는다.
	    		double oldValue = pairs[0].value;
	    		for ( int i = 0; i < pairs.length; i++ )
	    		{
	    			if ( pairs[i].value == oldValue )
	    				sameValueCount ++;
	    			else
	    				numOfDifferectValue[0]++;
		   			
	    			result.set(pairs[i].index/n, pairs[i].index%n,offset + i - sameValueCount);
	    			oldValue = pairs[i].value;
	    		}
	    	}
	    	
	    	else{
	    		for ( int i = 0; i < pairs.length; i++ )
	    			result.setQuick(pairs[i].index/n, pairs[i].index%matrix.rows(),offset + i);
	    	}
	    	
	    	return result;
		}
    }
    
	
	public static int[] rank(double[] vector, boolean asc)
    {
		ValueIndexPair.ascending = asc;
    	
		ValueIndexPair[] pairs = new ValueIndexPair[vector.length];
    	for ( int i = 0; i < vector.length; i++ )
    	{
    		if ( asc )
    			pairs[i] = new ValueIndexPair(vector[i],i);
    		else
    			pairs[vector.length - i - 1] = new ValueIndexPair(vector[i],i);
    	}
    	java.util.Arrays.sort(pairs);
    	
    	int[] rank = new int[vector.length];
    	for ( int i = 0; i < vector.length; i++ )
    		rank[i] = pairs[i].index;
    	
    	return rank;
    }
	
	public static int[] rank(int length, boolean[] asc, double[][] vectors )
	{
		ValueIndexMultiPair.ascending = asc;
		
		if( vectors==null )
			return null;
		
		ValueIndexMultiPair[] pairs = new ValueIndexMultiPair[length];
		
		for( int i=0; i<length; i++ )
		{
			final double[] values = new double[vectors.length];
			for( int j=0; j<vectors.length; j++ )
			{
				if(vectors[j].length-1 < i)
					values[j] = 0;
				else
					values[j] = vectors[j][i];
			}
			
			pairs[i] = new ValueIndexMultiPair(i, values);
		}
		
		Arrays.sort(pairs);
		int[] rank = new int[length];
    	for ( int i = 0; i < length; i++ )
    		rank[i] = pairs[i].getIndex();
    	
    	return rank;
	}
	
	public static int[] rank(int length, boolean[] asc, CDVector[] vectors)
	{
		// Vector to Array
		double[][] vectorArray = new double[vectors.length][];
		for(int i=0; i<vectors.length; i++)
			vectorArray[i] = vectors[i].toArray();
		
		return rank(length, asc, vectorArray);
	}
	
	public static int[] rank(float[] vector, boolean asc)
    {
		ValueIndexPair.ascending = asc;
    	
		ValueIndexPair[] pairs = new ValueIndexPair[vector.length];
    	for ( int i = 0; i < vector.length; i++ )
    	{
    		if ( asc )
    			pairs[i] = new ValueIndexPair(vector[i],i);
    		else
    			pairs[vector.length - i - 1] = new ValueIndexPair(vector[i],i);
    	}
    	java.util.Arrays.sort(pairs);
    	
    	int[] rank = new int[vector.length];
    	for ( int i = 0; i < vector.length; i++ )
    		rank[i] = pairs[i].index;
    	
    	return rank;
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int[] rank(String[] vector, boolean asc)
    {
		ValueIndexPair.ascending = asc;
    	
		ObjectValueIndexPair<String>[] pairs = new ObjectValueIndexPair[vector.length];
    	for ( int i = 0; i < vector.length; i++ )
    	{
    		if ( asc )
    			pairs[i] = new ObjectValueIndexPair<String>(vector[i],i);
    		else
    			pairs[vector.length - i - 1] = new ObjectValueIndexPair(vector[i],i);
    	}
    	java.util.Arrays.sort(pairs);
    	
    	int[] rank = new int[vector.length];
    	for ( int i = 0; i < vector.length; i++ )
    		rank[i] = pairs[i].index;
    	
    	return rank;
    }
	
	/**
	 * 입력된 vector의 reverse를 구한다.
	 * { 1, 2, 3, 4 } => { 4, 3, 2, 1 }
	 * @param CDVector vector
	 * @return NMDoubleVector
	 */
	public static CDVector reverse(CDVector vector){
		
		int n = vector.size();
		CDVector result;
		if(vector.isDense()){
			result = CDVector.dense(vector.size());
			result.setArray(CAMatrixMathDense.reverse(vector.getArray()));
			return result;
		}
		else{
			result = CDVector.sparse(vector.size(),vector.cardinality() * 2, 0.2, 0.5);
			CDVector.Iter iter = vector.getIter();
			for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
				int i = iter.GetCurrentIndex();
				result.setQuick( n - i -1, vector.getQuick(i));
			}
			return result;	
		}
	}
	
	public static int[] sortedIndex( CDVector vector, int criterion )
	{
		int n = vector.size();
		double[] dVector = vector.toArray();
		double[] dVectorSorted = dVector.clone();
		Arrays.sort(dVectorSorted);
		
		int[] order = new int[n];
		for( int i = 0; i < n; i++ )
		{
			int index = -1;
			for( int j = 0; j < n; j++ )
				if( dVectorSorted[i] == dVector[j] )
				{
					index = j;
					break;
				}
			
			if( index == -1 )
				return null;
			
			order[i] = index;
			dVector[index] = Double.NaN;
		}
		
		if( criterion == CAMatrixMathOptions.DECREASING_ORDER )
			order = CAMatrixMathDense.reverse(order);
		
		return order;
	}

	public static int[] sortedIndex( String[] vector, int criterion ){
		int n = vector.length;
		String[] dVectorSorted = vector.clone();
		String[] dVector = vector.clone();
		Arrays.sort(dVectorSorted);
		
		int[] order = new int[n];
		for( int i = 0; i < n; i++ ){
			int index = -1;
			for( int j = 0; j < n; j++ )
				if( dVectorSorted[i] == dVector[j] ){
					index = j;
					break;
				}
			
			if( index == -1 )
				return null;
			
			order[i] = index;
			dVector[index] = "";
		}
		
		if( criterion == CAMatrixMathOptions.DECREASING_ORDER )
			order = CAMatrixMathDense.reverse(order);
		
		return order;
	}

	
	
	public static int[] sortedIndex( double[] vector, int criterion ){
		int n = vector.length;
		double[] dVectorSorted = vector.clone();
		
		//입력으로 들어온 Vector는 Undefined Element를 포함할 수 있다.
		//이 Undefined 값들은 맨 뒤에 오도록 정렬한다.
		double nanValue = (criterion==CAMatrixMathOptions.INCREASING_ORDER) ? Double.MAX_VALUE : - Double.MAX_VALUE;
		for(int i =0; i<dVectorSorted.length; i++){
			if(Double.valueOf(dVectorSorted[i]).isNaN())
				dVectorSorted[i]=nanValue;
		}
			
		double[] dVector = dVectorSorted.clone();
		Arrays.sort(dVectorSorted);
		
		int[] order = new int[n];
		for( int i = 0; i < n; i++ ){
			int index = -1;
			for( int j = 0; j < n; j++ )
				if( dVectorSorted[i] == dVector[j] ){
					index = j;
					break;
				}
			
			if( index == -1 )
				return null; //에러에 해당
			
			order[i] = index;
			dVector[index] = Double.NaN;
		}
		
		if( criterion == CAMatrixMathOptions.DECREASING_ORDER )
			order = CAMatrixMathDense.reverse(order);
		
		return order;
	}
	
	
	public static int[] sortedIndex( int[] vector, int criterion ){
		int n = vector.length;
		int[] dVectorSorted = vector.clone();
		int[] dVector = vector.clone();
		Arrays.sort(dVectorSorted);
		
		int[] order = new int[n];
		for( int i = 0; i < n; i++ ){
			int index = -1;
			for( int j = 0; j < n; j++ )
				if( dVectorSorted[i] == dVector[j] ){
					index = j;
					break;
				}
			
			if( index == -1 )
				return null;
			
			order[i] = index;
			dVector[index] = Integer.MIN_VALUE;
		}
		
		if( criterion == CAMatrixMathOptions.DECREASING_ORDER )
			order = CAMatrixMathDense.reverse(order);
		
		return order;
	}
	
	public static void main (String[] args) {
		
		CDMatrix train = CDMatrix.dense(3, 3); // matrix data
		train.setQuick(0, 0, 38.6666666666);
		train.setQuick(0, 1, 38.6666633333);
		train.setQuick(0, 2, 38.6666655555);
		
		roundHalfUp(train).print();
		
//		CDVector train2 = CDVector.dense(5);
//		
//		CDVector train3 = CDVector.dense(5);
//		train3.set(0, 1);
//		train3.set(1, 5);
//		train3.set(2, 4);
//		train3.set(3, 3);
//		train3.set(4, 2);
//
//		matrixFromRowVectors(new CDVector[]{train, train2, train3}).print();
//		System.out.println(matrixFromRowVectors(new CDVector[]{train, train2, train3}).isDense());
		
//		toDissimilarity(train).print();
//		System.out.println(toDissimilarity(train).isDense());
//		CDMatrix train2 = CDMatrix.sparse(3, 3); // matrix data
//		for(int i=0; i<3; i++){
//			for(int j=0; j< 3; j++){
//				if((i+j)%3!=1)
//					train2.set(i, j, i*(-10)+j);
//			}
//		}
//		train.print();
//		train2.print();
//		elementWiseMin(train, train2).print();
//		System.out.println(elementWiseMin(train, train2).isDense());
//		CDVector A = CDVector.sparse(5);
//		A.set(0, 0);
//		A.set(1, -2);
//		A.set(2, CA.NM_GLOBAL_MISSING);
//		A.set(3, 1);
//		A.set(4, 0);
//		dichotomize(A, 0, DICHOTOMIZE_EQ, false).print();
//		System.out.println(dichotomize(A, 0, DICHOTOMIZE_EQ, false).isDense());
//		CDVector B = CDVector.sparse(5);
//		B.set(0, 0);
//		B.set(1, 3);
//		B.set(2, -1);
//		B.set(3, 1);
//		B.set(4, 100);
//		System.out.println(innerProduct(A,B));
//		System.out.println(exactMatch(A).isDense());
//		CDVector B = CDVector.dense(5);
//		B.set(0, 1.3);
//		B.set(1, 0);
//		B.set(2, 1.3);
//		B.set(3, -1.3);
//		B.set(4, 2);
//		elementWiseAbs(A).print();
//		System.out.println(elementWiseAbs(A).isDense());
		
		return;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////

	
	public static <T extends Comparable<? super T>> int[] sortedIndex(final List<T> list){
		
		LinkedList<CDPair<Integer, T>> copiedList = new LinkedList<CDPair<Integer,T>>();
		int count = 0;
		for (T value : list) {
			copiedList.add(new CDPair<Integer, T>(count, value));
			count++;
		}
		Collections.sort(copiedList, new Comparator<CDPair<Integer, T>>() {
			public int compare(CDPair<Integer, T> o1, CDPair<Integer, T> o2) {
				return o1.getRight().compareTo(o2.getRight());
			}
		});
		int[] ret = new int[count];
		count = 0;
		for (CDPair<Integer, T> pair : copiedList) {
			ret[count] = pair.getLeft();
			count++;
		}
		
		return ret;
	}
	
	public static <T> int[] sortedIndex(final T[] list, final Comparator<T> comparator){
		
		LinkedList<CDPair<Integer, T>> copiedList = new LinkedList<CDPair<Integer,T>>();
		int count = 0;
		for (T value : list) {
			copiedList.add(new CDPair<Integer, T>(count, value));
			count++;
		}
		Collections.sort(copiedList, new Comparator<CDPair<Integer, T>>() {
			public int compare(CDPair<Integer, T> o1, CDPair<Integer, T> o2) {
				return comparator.compare(o1.getRight(), o2.getRight());
			}
		});
		int[] ret = new int[count];
		count = 0;
		for (CDPair<Integer, T> pair : copiedList) {
			ret[count] = pair.getLeft();
			count++;
		}
		
		return ret;
	}
	
	public static int[] sortedIndex(final float[] list, final Comparator<Float> comparator){
		
		LinkedList<CDPair<Integer, Float>> copiedList = new LinkedList<CDPair<Integer,Float>>();
		int count = 0;
		for (Float value : list) {
			copiedList.add(new CDPair<Integer, Float>(count, value));
			count++;
		}
		Collections.sort(copiedList, new Comparator<CDPair<Integer, Float>>() {
			public int compare(CDPair<Integer, Float> o1, CDPair<Integer, Float> o2) {
				return comparator.compare(o1.getRight(), o2.getRight());
			}
		});
		int[] ret = new int[count];
		count = 0;
		for (CDPair<Integer, Float> pair : copiedList) {
			ret[count] = pair.getLeft();
			count++;
		}
		
		return ret;
	}
	
	public static <T> int[] sortedIndex(final List<T> list, final Comparator<T> comparator){
		
		LinkedList<CDPair<Integer, T>> copiedList = new LinkedList<CDPair<Integer,T>>();
		int count = 0;
		for (T value : list) {
			copiedList.add(new CDPair<Integer, T>(count, value));
			count++;
		}
		Collections.sort(copiedList, new Comparator<CDPair<Integer, T>>() {
			public int compare(CDPair<Integer, T> o1, CDPair<Integer, T> o2) {
				return comparator.compare(o1.getRight(), o2.getRight());
			}
		});
		int[] ret = new int[count];
		count = 0;
		for (CDPair<Integer, T> pair : copiedList) {
			ret[count] = pair.getLeft();
			count++;
		}
		
		return ret;
	}
	
	
	public static int[] rank(int[] vector, boolean asc)
    {
		ValueIndexPair.ascending = asc;
    	
		ValueIndexPair[] pairs = new ValueIndexPair[vector.length];
    	for ( int i = 0; i < vector.length; i++ )
    	{
    		if ( asc )
    			pairs[i] = new ValueIndexPair(vector[i],i);
    		else
    			pairs[vector.length - i - 1] = new ValueIndexPair(vector[i],i);
    	}
    	java.util.Arrays.sort(pairs);
    	
    	int[] rank = new int[vector.length];
    	for ( int i = 0; i < vector.length; i++ )
    		rank[i] = pairs[i].index;
    	
    	return rank;
    }
	

	public static int[] rank(CDVector vector, boolean asc)
	{
		
		if(vector.isDense()){
			return rank(vector.getArray(), asc);
		}
		else{
			ValueIndexPair.ascending = asc;
	    	
			ValueIndexPair[] pairs = new ValueIndexPair[vector.size()];
	    	for ( int i = 0; i < vector.size(); i++ )
	    	{
	    		if ( asc )
	    			pairs[i] = new ValueIndexPair(vector.getQuick(i),i);
	    		else
	    			pairs[vector.size()-i-1] = new ValueIndexPair(vector.getQuick(i),i);
	    	}
	    	
	    	java.util.Arrays.sort(pairs);
	    	
	    	int[] rank = new int[vector.size()];
	    	for ( int i = 0; i < vector.size(); i++ )
	    		rank[i] = pairs[i].index;
	    	
	    	return rank;
		}
	}
	

	
    // 각각의 원소의 역수값을 취해서 넘겨준다.
    // Input array 에는 0.0 이 있어서는 안 된다.
	public static CDVector Reciprocal1D(CDVector sdm1dArray){
		int n = sdm1dArray.size();
		CDVector sdm1dArrayResult;
		if(sdm1dArray.isDense()){
			sdm1dArrayResult = CDVector.dense(n);
			sdm1dArrayResult.setArray(CAMatrixMathDense.Reciprocal1D(sdm1dArray.getArray()));
			return sdm1dArrayResult;
		}
		else{
			sdm1dArrayResult = CDVector.dense(n); // 무조건 Dense
			for (int nIndex=0; nIndex<sdm1dArray.size(); nIndex++)
				sdm1dArrayResult.setQuick( nIndex, 1.0/sdm1dArray.getQuick( nIndex ) );
			return sdm1dArrayResult;	
		}
	}
	
	/**
	 * 노드의 Attribute Matrix 중 Missing value를 포함하고 있는 row(노드에 해당)를 삭제한다. 
	 * 분석을 수행하기 위한 전처리 과정으로 사용된다.
	 * 
	 */
	public static CDVector[] extractMissingManyVector(CDVector[] vectors, double[][] missingData) {
		HashSet<Integer> set = new HashSet<Integer>();
		int i, j, k;
		double value;
		boolean missing;
		int numberOfVectors = vectors.length;
		int lengthOfVector = vectors[0].size();
		
		boolean denseAll=true;
		for(i=0; i<numberOfVectors; i++){
			if(!vectors[i].isDense()){
				denseAll=false;
				break;
			}
		}
		
		CDVector[] ret = new CDVector[numberOfVectors];
		
		if(denseAll){
			double[][] dvectors = new double[numberOfVectors][];
			for(i=0; i<numberOfVectors; i++){
				dvectors[i] = vectors[i].getArray();
			}
			double[][]dresult = CAMatrixMathDense.extractMissingManyVector(dvectors, missingData);
			int length = dresult[0].length;
			for(i=0; i<numberOfVectors; i++){
				ret[i] = CDVector.dense(length);
				ret[i].setArray(dresult[i]);
			}
			return ret;
			
		}
		
		for (i = 0; i < numberOfVectors; i++) {
			for (j = 0; j < lengthOfVector; j++) {
				value = vectors[i].getQuick(j);
				missing = false;
				if ( missingData[i] == null ) continue;
				
				for (k = 0; k < missingData[i].length; k++) {
					if ( value == missingData[i][k] ) {
						missing = true;
						break;
					}
				}
				if (missing) {
					set.add(j);
				}
			}
		}
		
		for (i = 0; i < numberOfVectors; i++) {
			if(vectors[i].isDense())
				ret[i] = CDVector.dense(lengthOfVector - set.size());
			else
				ret[i] = CDVector.sparse(lengthOfVector - set.size());
		}
		
		int cnt = 0;
		for (i = 0; i < lengthOfVector; i++) {
			if ( set.contains(i) ) 
				continue;		
			for (j = 0; j < numberOfVectors; j++) {
				ret[j].setQuick(cnt, vectors[j].getQuick(i) );
			}
			cnt++;
		}
		
		return ret;
	}
	
	/**********************************************************************************************
	 * Dichotomize, Symmetrize, toCost, Diagonal 처리<br>
	 * 
	 * @param CDMatrix matrix
	 * @param HashMap<Integer, Object> var
	 * @return NMDoubleMatrix
	 **********************************************************************************************/
	public static CDMatrix PreProcessWeight(CDMatrix matrix, HashMap<Integer, Object> var){
		CDMatrix result = matrix;
		
		//// DICHOTOMIZE //////////////////////////////////////////////////////////////////////////
		if( var.containsKey(CAMatrixMathOptions.DICHOTOMIZE_OPERATOR) == true ){
			int dichotomize_op = (Integer)var.get(CAMatrixMathOptions.DICHOTOMIZE_OPERATOR);
			double dichotomize_val = (Double)var.get(CAMatrixMathOptions.DICHOTOMIZE_VALUE);
			result = CAMatrixMath.dichotomize(result,  dichotomize_val, dichotomize_op, true);
		}
		
		//// SYMMETRIZE ///////////////////////////////////////////////////////////////////////////
		if( var.containsKey(CAMatrixMathOptions.SYMMETRIZE_OPERATOR) == true ){
			int symmetrize_op = (Integer)var.get(CAMatrixMathOptions.SYMMETRIZE_OPERATOR);
			result = CAMatrixMath.symmetrize(result, symmetrize_op);
		}
		
		//// COST /////////////////////////////////////////////////////////////////////////////////
		if( var.containsKey(CAMatrixMathOptions.COST_TO_STRENGTH) == true ){
			result = CAMatrixMath.toReverse(result);
		}

		//// Similarity ///////////////////////////////////////////////////////////////////////////
		if( var.containsKey(CAMatrixMathOptions.TO_DISSIMILARITY) == true ){
			result = CAMatrixMath.toDissimilarity(result);
		}
		
		//// DIAGONAL /////////////////////////////////////////////////////////////////////////////
		if( var.containsKey(CAMatrixMathOptions.DIAGONAL) == true ){
			result = CAMatrixMath.recodeDiagonal(result, 0);
		}
		
		// Weight 처리과정이 생략된 경우 result를 새로운 객체로 만들어준다.
		if( result == matrix )
			result = matrix.copy();
		
		return result;
	}

	// 옵션 다이얼로그 처리시 인덱스를 구한다.
	public static int selectedIndex(CDVector list)
	{
        CDVector.Iter iter = list.getIter();

        iter.Begin();

        if(iter.hasMoreValue())
        {
        	return iter.GetCurrentIndex();
        }
        
		return 0;
	}
	
	/** 
	 * Autocorrelation - Binary에서 binary 체크하는 메소드
	 * 2종류 이하의 값이 있는 경우에는 거짓을 반환
	 * 3종류 이상의 값이 있는 경우에는 참을 반환
	 */
	public static boolean checkAutocorrelationBinary(CDVector list){
		
		if(list.isDense()){
			return CAMatrixMathDense.checkAutocorrelationBinary(list.getArray());
		}
		else{
			TreeSet<Double> set = new TreeSet<Double>();
			CDVector.Iter iter = list.getIter();
			
			if (list.cardinality() != list.size())
			{
				set.add(0.0);
			}
			
	        for(iter.Begin(); iter.hasMoreValue(); iter.Next() )
	        {
	        	set.add( iter.GetCurrentValue() );
	        	if (set.size() > 2) return true; 
	        }
	
	        return false;
		}
	}
	
	/**
	 * nIndex번째 row로 부터 nCount 개의 row를 삭제한 행렬을 반환한다.
	 */
	public static CDMatrix deleteRow(CDMatrix matrix, int nIndex, int nCount){
		if (matrix.rows() <= nCount) {
			return null;
		}
		
		
		CDMatrix expand; 

		if(matrix.isDense()){
			expand = CDMatrix.dense(matrix.rows() - nCount, matrix.columns());
			expand.setArray(CAMatrixMathDense.deleteRow(matrix.getArray(), nIndex, nCount));
			return expand;
		}
		else{
			expand = CDMatrix.sparse(matrix.rows() - nCount, matrix.columns());
			CDMatrix.Iter iter = matrix.getIter();
			CDMatrix.RowIter rowIter;
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					if (i >= nIndex && i < nIndex + nCount)
						continue;
					expand.set((i<nIndex)?i:i-nCount, j, matrix.getQuick(i, j));
				}
			}
			return expand;
		}
	}
	
	/**
	 * nIndex번째 column으로 부터 nCount 개의 column을 삭제한 행렬을 반환한다.
	 */
	public static CDMatrix deleteColumn(CDMatrix matrix, int nIndex, int nCount){
		if (matrix.columns() <= nCount) {
			return null;
		}
		CDMatrix expand;
		if(matrix.isDense()){
			expand = CDMatrix.dense(matrix.rows(), matrix.columns() - nCount);
			expand.setArray(CAMatrixMathDense.deleteColumn(matrix.getArray(), nIndex, nCount));
			return expand;
		}
		else{
			expand = CDMatrix.sparse(matrix.rows(), matrix.columns() - nCount);
	
			CDMatrix.Iter iter = matrix.getIter();
			CDMatrix.RowIter rowIter;
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				int i = rowIter.GetCurrentRowIndex();
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					if (j >= nIndex && j < nIndex + nCount)
						continue;
					expand.set(i, (j<nIndex)?j:j-nCount, matrix.getQuick(i, j));
				}
			}
			return expand;
		}
	}
	
	/**
	 * nIndex번째 row와 column으로 부터
	 * 각각 nCount 개의 row와 nCount개의 column을 삭제한 행렬을 반환한다.
	 */
	public static CDMatrix deleteRowAndColumn(CDMatrix matrix, int row, int col, int nCount){
		if (matrix.rows() <= nCount || matrix.columns() <= nCount) {
			return null;
		}
		CDMatrix expand;
		if(matrix.isDense()){
			expand = CDMatrix.dense(matrix.rows()- nCount, matrix.columns()- nCount);
			expand.setArray(CAMatrixMathDense.deleteRowAndColumn(matrix.getArray(), row, col, nCount));
			return expand;
		}
		else{
		expand = CDMatrix.sparse(matrix.rows() - nCount, matrix.columns()-nCount);

			CDMatrix.Iter iter = matrix.getIter();
			CDMatrix.RowIter rowIter;
			for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
				int i = rowIter.GetCurrentRowIndex();
				if (i >= row && i < row + nCount)
					continue;
				int iIndex = (i<row)?i:i-nCount;
				for(; rowIter.hasMoreValue(); rowIter.Next() ){
					int j = rowIter.GetCurrentColumnIndex();
					if (j >= col && j < col + nCount)
						continue;
					expand.set(iIndex, (j<col)?j:j-nCount, matrix.getQuick(i, j));
				}
			}
			return expand;
		}
	}
	
	/**
	 * onemode Matrix 의 Upper Cell 값을 없애고 반환한다.
	 * onemode Matrix 가 아닐 경우 원본 Matrix 가 그대로 반환된다.<br />
	 * <p>
	 * 1 2 3       1 			<br />
	 * 4 5 6  -->  4 5			<br />
	 * 7 8 9       7 8 9		<br />
	 * </p>
	 * @param matrix 입력 Matrix
	 * @return Upper Cell 이 지워진 Matrix.
	 */
	public static CDMatrix deleteUpper(final CDMatrix matrix) {
		// onemode network 가 아니면 원본 matrix 를 반환한다.
		if(matrix.rows()!=matrix.columns())
			return matrix;
		
		CDMatrix _toRet;
		if(matrix.isDense()){
			_toRet = CDMatrix.dense(matrix.rows(), matrix.columns());
			_toRet.setArray(CAMatrixMathDense.deleteUpper(matrix.getArray()));
			return _toRet;
		}
		else{
			_toRet = CDMatrix.sparse(matrix.rows(), matrix.columns());
			CDMatrix.Iter iter = matrix.getIter();
			for( CDMatrix.RowIter rowIter = iter.Begin(0); rowIter!=null; rowIter = iter.Next() )
			{
				final int rowIndex = rowIter.GetCurrentRowIndex();
				for( ; rowIter.hasMoreValue(); rowIter.Next() )
				{
					final int colIndex = rowIter.GetCurrentColumnIndex();
					if(rowIndex < colIndex )
						continue;
					final double value = rowIter.GetCurrentValue();
					_toRet.setQuick(rowIndex, colIndex, value);
				}
			}
			return _toRet;
		}
	}
	
	/**
	 * onemode Matrix 의 Lower Cell 값을 없애고 반환한다.
	 * onemode Matrix 가 아닐 경우 원본 Matrix 가 그대로 반환된다.<br />
	 * <p>
	 * 1 2 3       1 2 3<br />
	 * 4 5 6  -->    5 6<br />
	 * 7 8 9           9<br />z
	 * </p>
	 * @param matrix 입력 Matrix
	 * @return Upper Cell 이 지워진 Matrix.
	 */
	public static CDMatrix deleteLower(final CDMatrix matrix) {
		// onemode network 가 아니면 원본 matrix 를 반환한다.
		if(matrix.rows()!=matrix.columns())
			return matrix;
		
		CDMatrix _toRet;
		if(matrix.isDense()){
			_toRet = CDMatrix.dense(matrix.rows(), matrix.columns());
			_toRet.setArray(CAMatrixMathDense.deleteLower(matrix.getArray()));
			return _toRet;
		}
		
		_toRet = CDMatrix.sparse(matrix.rows(), matrix.columns());
		CDMatrix.Iter iter = matrix.getIter();
		for( CDMatrix.RowIter rowIter = iter.Begin(0); rowIter!=null; rowIter = iter.Next() )
		{
			final int rowIndex = rowIter.GetCurrentRowIndex();
			for( ; rowIter.hasMoreValue(); rowIter.Next() )
			{
				final int colIndex = rowIter.GetCurrentColumnIndex();
				if(rowIndex > colIndex )
					continue;
				final double value = rowIter.GetCurrentValue();
				_toRet.setQuick(rowIndex, colIndex, value);
			}
		}
		
		return _toRet;
	}
	
	
	/**
	 * 주어진 Partition Vector 값을 이용해 네트워크 분할
	 * @param adjacency
	 * @param partitionVector
	 * @return partitionIndex -> ( NewIndex -> OriginalIndex ) / partitionIndex -> Adjacency
	 */
	public static CDPair<int[][], CDMatrix[]> partition(final CDMatrix adjacency, double[] partitionVector){
		
		HashMap<Double, HashMap<Integer, Integer>> partitionVectorToOriginalIndexToNewIndex = new HashMap<Double, HashMap<Integer, Integer>>();
		for(int i=0; i<partitionVector.length; i++){
			if(!partitionVectorToOriginalIndexToNewIndex.containsKey(partitionVector[i])){
				partitionVectorToOriginalIndexToNewIndex.put(partitionVector[i], new HashMap<Integer, Integer>(1));
			}
			HashMap<Integer, Integer> originalIndexToNewIndex = partitionVectorToOriginalIndexToNewIndex.get(partitionVector[i]);
			if(!originalIndexToNewIndex.containsKey(i)){
				originalIndexToNewIndex.put(i, originalIndexToNewIndex.size());
			}
		}
		
		HashMap<Double, CDMatrix> partitionVectorToNewMatrix = new HashMap<Double, CDMatrix>();
		
		Iter iter = adjacency.getIter();
		for( CDMatrix.RowIter rowIter = iter.Begin(0); rowIter!=null; rowIter = iter.Next() )
		{
			int i = rowIter.GetCurrentRowIndex();
			double i_partition = partitionVector[i];
			HashMap<Integer, Integer> originalIndexToNewIndex = partitionVectorToOriginalIndexToNewIndex.get(i_partition);
			if(!partitionVectorToNewMatrix.containsKey(i_partition)){
				int size = originalIndexToNewIndex.size();
				partitionVectorToNewMatrix.put(i_partition, CDMatrix.sparse(size, size));
			}
			CDMatrix partitionMatrix = partitionVectorToNewMatrix.get(i_partition);
			
			for( ; rowIter.hasMoreValue(); rowIter.Next() )
			{
				int j = rowIter.GetCurrentColumnIndex();
				double j_partition = partitionVector[j];
				if(i_partition==j_partition){
					partitionMatrix.set(originalIndexToNewIndex.get(i), originalIndexToNewIndex.get(j), rowIter.GetCurrentValue());
				}
			}
		}
		
		int partitionNum = partitionVectorToOriginalIndexToNewIndex.size();
		CDMatrix[] partitionIndexToMatrix = new CDMatrix[partitionNum];
		int[][] partitionIndexToNewIndexToOriginalIndex = new int[partitionNum][];
		
		int index = 0;
		for(double pv : partitionVectorToOriginalIndexToNewIndex.keySet()){
			partitionIndexToMatrix[index] = partitionVectorToNewMatrix.get(pv);
			HashMap<Integer, Integer> originalIndexToNewIndex =  partitionVectorToOriginalIndexToNewIndex.get(pv);
			partitionIndexToNewIndexToOriginalIndex[index] = new int[originalIndexToNewIndex.size()];
			for(Entry<Integer, Integer> entry : originalIndexToNewIndex.entrySet()){
				partitionIndexToNewIndexToOriginalIndex[index][entry.getValue()] = entry.getKey();
			}
			index++;
		}
		
		return new CDPair<int[][], CDMatrix[]>(partitionIndexToNewIndexToOriginalIndex, partitionIndexToMatrix);
	}
	
	
	/**
	 * 주어진 affiliation을 이용해 네트워크 분할
	 * @param adjacency
	 * @param affiliation
	 * @return partitionIndex -> ( NewIndex -> OriginalIndex ) / partitionIndex -> Adjacency
	 */
	public static CDPair<int[][], CDMatrix[]> partition(final CDMatrix adjacency, CDMatrix affiliation){

		int n = affiliation.rows();
		CDMatrix[] partitionIndexToMatrix = new CDMatrix[n];
		
		@SuppressWarnings("rawtypes")
		HashMap[] partitionIndexToOldIndexToNewIndex = new HashMap[n];
		Iter iter = affiliation.getIter();
		iter.init();
		
		for(int pi=0; pi<n; pi++){
			HashMap<Integer, Integer> oldIndexToNewIndex = new HashMap<Integer, Integer>();
			partitionIndexToOldIndexToNewIndex[pi] = oldIndexToNewIndex;
			for(RowIter rowIter = iter.getRow(pi) ; rowIter.hasMoreValue(); rowIter.Next() )
			{
				int j = rowIter.GetCurrentColumnIndex();
				if(!oldIndexToNewIndex.containsKey(j)){
					oldIndexToNewIndex.put(j, oldIndexToNewIndex.size());
				}
			}
			int size = oldIndexToNewIndex.size();
			partitionIndexToMatrix[pi] = CDMatrix.sparse(size, size);
		}
			
		iter = adjacency.getIter();
		iter.init();
		for( CDMatrix.RowIter rowIter = iter.Begin(0); rowIter!=null; rowIter = iter.Next() )
		{
			int i = rowIter.GetCurrentRowIndex();
			for( ; rowIter.hasMoreValue(); rowIter.Next() )
			{
				int j = rowIter.GetCurrentColumnIndex();
				for(int pi = 0; pi<n; pi++){
					@SuppressWarnings("unchecked")
					HashMap<Integer, Integer> originalIndexToNewIndex = (HashMap<Integer, Integer>)partitionIndexToOldIndexToNewIndex[pi];
					if(originalIndexToNewIndex.containsKey(i) && originalIndexToNewIndex.containsKey(j)){
						partitionIndexToMatrix[pi].set(originalIndexToNewIndex.get(i), originalIndexToNewIndex.get(j), rowIter.GetCurrentValue());
					}
				}
			}
		}
		
		int[][] partitionIndexNewIndexToOriginalIndex = new int[n][];
		for(int i=0; i<n; i++){
			partitionIndexNewIndexToOriginalIndex[i] = new int[partitionIndexToOldIndexToNewIndex[i].size()];
			@SuppressWarnings("unchecked")
			HashMap<Integer, Integer> originalIndexToNewIndex = (HashMap<Integer, Integer>)partitionIndexToOldIndexToNewIndex[i];
			for(Entry<Integer, Integer> entry : originalIndexToNewIndex.entrySet()){
				partitionIndexNewIndexToOriginalIndex[i][entry.getValue()] = entry.getKey();
			}
		}
		
		return new CDPair<int[][], CDMatrix[]>(partitionIndexNewIndexToOriginalIndex, partitionIndexToMatrix);
		
	}
}


class ObjectValueIndexPair<T extends Comparable<T>> implements Comparable<T>
{
	
	protected static boolean ascending;
	protected T value;
	protected int index;
	public ObjectValueIndexPair(T value, int index)
	{
		this.value = value;
		this.index = index;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int compareTo(T o)
	{
		if (o instanceof Comparable) {
			return ((Comparable)value).compareTo(((ObjectValueIndexPair)o).value);	
		}
		else {
			System.out.println("CAMatrixMath : 3203 : " + o.toString());
			return -1;
		}
	}
	
}


class ValueIndexPair implements Comparable<Object>
{
	protected static boolean ascending;
	protected double value;
	protected int index;
	public ValueIndexPair(double value, int index)
	{
		this.value = value;
		this.index = index;
	}
	public ValueIndexPair(int value, int index)
	{
		this.value = value;
		this.index = index;
	}
	public ValueIndexPair(long value, int index)
	{
		this.value = value;
		this.index = index;
	}
	
	public int compareTo(Object o)
	{
		if ( o instanceof ValueIndexPair )
		{
			ValueIndexPair pair = (ValueIndexPair)o;
			
			// NaN 일 경우에는 무조건 크게 한다.
			if ( Double.isNaN(value) && Double.isNaN(pair.value) )
				return (ascending ? index-pair.index : pair.index-index);
			else if ( Double.isNaN(value) )
				return (ascending ? 1 : -1);
			else if ( Double.isNaN(pair.value) )
				return (ascending ? -1 : 1);
			else if ( value > pair.value )
				return (ascending ? 1 : -1);
			else if ( value < pair.value )
				return (ascending ? -1 : 1);
			else
				return 0;
		}
		else
			return 0;
	}
}

// Referenced From ValueIndexPair
class ValueIndexMultiPair implements Comparable<ValueIndexMultiPair>
{
	public ValueIndexMultiPair(int index, double[] value)
	{
		m_index = index;
		m_values = value;
	}
	
	public int getIndex() {
		return m_index;
	}
	
	public double getValue(int index){
		return m_values[index];
	}
	
	public int getValueCount() {
		return m_values.length;
	}
	
	public int compareTo(ValueIndexMultiPair o) {		
		for( int i=0; i<m_values.length; i++ )
		{
			if( o==null )
				return (ascending[i] ? 1 : -1);
			
			try
			{
				final double anotherVal = o.getValue(i); 
				// NaN 일 경우에는 무조건 크게 한다.
				if ( Double.isNaN(m_values[i]) && Double.isNaN(anotherVal) )
					return (ascending[i] ? m_index-o.getIndex() : o.getIndex()-m_index);
				else if ( Double.isNaN(m_values[i]) )
					return (ascending[i] ? 1 : -1);
				else if ( Double.isNaN(anotherVal) )
					return (ascending[i] ? -1 : 1);
				else if ( m_values[i] > anotherVal )
					return (ascending[i] ? 1 : -1);
				else if ( m_values[i] < anotherVal )
					return (ascending[i] ? -1 : 1);
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				// 나는 값이 남았고 쟤는 값이 없다.
				return (ascending[i] ? 1 : -1);
			}
			
			// 나는 값이 다 떨어졌고 쟤는 남았다.
			if(m_values.length < o.getValueCount())
				return (ascending[i] ? -1 : 1);
		}		
		return 0;
	}
		
	private int m_index;
	private double[] m_values;
	protected static boolean ascending[];
}