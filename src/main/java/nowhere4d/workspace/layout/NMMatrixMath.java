package nowhere4d.workspace.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class NMMatrixMath extends CAMatrixMath
{
	
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
	@SuppressWarnings("unused")
	public static CDMatrix dichotomize(CDMatrix matrix, double threshold, int option, boolean includeZero){
		int n = matrix.rows();
		int m = matrix.columns();
		CDMatrix dichotomize = CDMatrix.sparse(n, m);
		CDMatrix.Iter iter = matrix.getIter();
		CDMatrix.RowIter rowIter;
				
		if( includeZero == true ) 
			if( (option == DICHOTOMIZE_GT && threshold < 0)
					|| (option == DICHOTOMIZE_GE && threshold <= 0)
					|| (option == DICHOTOMIZE_EQ && threshold == 0)
					|| (option == DICHOTOMIZE_LE && threshold >= 0)
					|| (option == DICHOTOMIZE_LT && threshold > 0) 
					|| (option == DICHOTOMIZE_NEQ && threshold != 0)){
				
				return dichotomizeDense(matrix, threshold, option);
			}
			
		int __prog__count = 0;
		
		for( rowIter = iter.Begin(0); rowIter != null; rowIter = iter.Next() ){
			int i = rowIter.GetCurrentRowIndex();
			for( ; rowIter.hasMoreValue(); rowIter.Next() ){
				__prog__count++;
				int j = rowIter.GetCurrentColumnIndex();
				
				// Missing Value는 그대로 넘긴다.
				final double val = matrix.getQuick(i, j);
				if( val == NMPPConst.getGlobalMissing() )
				{
					dichotomize.setQuick(i, j, NMPPConst.getGlobalMissing());
					continue;
				}
				
				switch( option ){
				case DICHOTOMIZE_GT:
					if( val > threshold )
						dichotomize.setQuick(i, j, 1);
					break;
					
				case DICHOTOMIZE_GE:
					if( val >= threshold )
						dichotomize.setQuick(i, j, 1);
					break;
					
				case DICHOTOMIZE_EQ:
					if( val == threshold )
						dichotomize.setQuick(i, j, 1);
					break;
					
				case DICHOTOMIZE_LE:
					if( val <= threshold )
						dichotomize.setQuick(i, j, 1);
					break;
					
				case DICHOTOMIZE_LT:
					if( val < threshold )
						dichotomize.setQuick(i, j, 1);
					break;
				case DICHOTOMIZE_NEQ:
					if( val != threshold )
						dichotomize.setQuick(i, j, 1);
				}
			}			
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
	public static CDMatrix dichotomizeDense(CDMatrix matrix, double threshold, int option){
		int n = matrix.rows();
		int m = matrix.columns();
		double[][] dMatrix = matrix.toArray();
		double[][] dDichotomize = new double[matrix.rows()][matrix.columns()]; 
		double val;
		
		for( int i = 0; i < n; i++ ){
			for( int j = 0; j < m; j++ ){
				
				// Missing Value는 그대로 넘긴다.
				if( dMatrix[i][j]==NMPPConst.getGlobalMissing() )
				{
					dDichotomize[i][j] = NMPPConst.getGlobalMissing();
					continue;
				}
				
				switch( option ){
				case DICHOTOMIZE_GT:
					val = dMatrix[i][j];
					if( val > threshold )
						dDichotomize[i][j] = 1;
					break;
					
				case DICHOTOMIZE_GE:
					val = dMatrix[i][j];
					if( val >= threshold )
						dDichotomize[i][j] = 1;
					break;
					
				case DICHOTOMIZE_EQ:
					val = dMatrix[i][j];
					if( val == threshold )
						dDichotomize[i][j] = 1;
					break;
					
				case DICHOTOMIZE_LE:
					val = dMatrix[i][j];
					if( val <= threshold )
						dDichotomize[i][j] = 1;
					break;
					
				case DICHOTOMIZE_LT:
					val = dMatrix[i][j];
					if( val < threshold )
						dDichotomize[i][j] = 1;
					break;
				}
			}			
		}
		
		CDMatrix sDichotomize = CDMatrix.sparse(dDichotomize);
		
		return sDichotomize;	
	}
	
	public static CDPair<double[], int[][]> partitionVector(CDVector vector) {
		HashMap<Double, ArrayList<Integer>> partitions = new HashMap<Double, ArrayList<Integer>>();
		for (int i=0; i < vector.size(); i++) {
			double value = vector.get(i);
			ArrayList<Integer> list = partitions.get(value);
			if (list == null)
				list = new ArrayList<Integer>();
			list.add(i);
			partitions.put(value, list);
		}
		Double[] valuesDouble = partitions.keySet().toArray(new Double[0]);
		double[] values = new double[valuesDouble.length];
		for (int i=0; i < valuesDouble.length; i++) {
			values[i] = valuesDouble[i];
		}
		Arrays.sort(values);
		int[][] partitionIndicies = new int[values.length][];
		for (int i=0; i < values.length; i++) {
			ArrayList<Integer> indexList = partitions.get(values[i]);
			int[] indicies = new int[indexList.size()];
			for (int j=0; j < indicies.length; j++) {
				indicies[j] = indexList.get(j);
			}
			partitionIndicies[i] = indicies;
		}
		return new CDPair<double[], int[][]>(values, partitionIndicies);
	}
	
}