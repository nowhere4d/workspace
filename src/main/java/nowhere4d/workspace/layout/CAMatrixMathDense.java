package nowhere4d.workspace.layout;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

public class CAMatrixMathDense implements CAMatrixMathOptions 
{
	public static void main(String[] ar){
		double[] a = {1,2,3};
		double[][] b = {{1,0},{1,1}, {0,2}};
		double[] c = multiply(a,b);
		for(int i =0; i<c.length; i++)
			System.out.println(c[i]);
	}
	
	/** 
	 * 실수 연산 오차 때문에 발생하는 문제를 제거하기 위하여 
	 * 소수점 아래 7자리에서 반올림하여, 6자리의 소수로 나타낸다.(넷마이너에서 제공하는 최고 정확도) 
	*/
	public static double[][] roundHalfUp(double[][] a){
		int n = a.length;
		int m = a[0].length;
		double[][] c = new double[n][m];
		for(int i=0; i<n; i++)
			for(int j=0; j<m; j++){
				BigDecimal bd = new BigDecimal(a[i][j]);
				c[i][j]= bd.setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
		return c;
	}
	
	/**
	 * 두 매트릭스를 곱한다.
	 */
	public static double[][] multiply(double[][] a, double[][] b)
	{
		int m	= a.length;
		
		if (m == 0)
			return new double[0][0];
		
		int n = a[0].length;
		int p = b[0].length;
	
	    double[][] c = new double[m][p];
	      double[] Bcolj = new double[n];
	      for (int j = 0; j < p; j++) {
	         for (int k = 0; k < n; k++) {
	            Bcolj[k] = b[k][j];
	         }
	         for (int i = 0; i < m; i++) {
	            double[] Arowi = a[i];
	            double s = 0;
	            for (int k = 0; k < n; k++) {
	               s += Arowi[k]*Bcolj[k];
	            }
	            c[i][j] = s;
	         }
	      }
		
		return c;
	}
	
	/**
	 * 두 매트릭스를 곱한다.
	 */
	public static float[][] multiply(float[][] a, float[][] b)
	{
		int m	= a.length;
		
		if (m == 0)
			return new float[0][0];
		
		int n	= b[0].length;
		int l	= a[0].length;
		
		float[][] c	= new float[m][n];
		for (int i=0; i<m; i++)
		{
			for (int j=0; j<n; j++)
			{
				for (int k=0; k<l; k++)
				{
					c[i][j]	+= a[i][k]*b[k][j];
				}
			}
		}
		return c;
	}
	
	public static double[][] multiply(double[] a, double[] b){
		int n = a.length;
		int m = b.length;
		
		double[][] result = new double[n][m];
		for( int i = 0; i < n; i++ )
			for( int j = 0; j < m; j++ )
				result[i][j] = a[i] * b[j];
		
		return result;
	}
	
	/**
	 * 매트릭스와 벡터를 곱한다. 2003.01.06 추가
	 */
	public static double[] multiply(double[][] a, double[] b)
	{
		int m	= a.length;
		
		if (m == 0)
			return new double[0];
		
		int l	= a[0].length;
		
		double[] c	= new double[m];
		for (int i=0; i<m; i++)
		{
			for (int k=0; k<l; k++)
			{
				c[i]	+= a[i][k]*b[k];
			}
		}
		return c;
	}
	
	/**
	 * 매트릭스와 벡터를 곱한다. 2003.01.06 추가
	 */
	public static double[] multiply(double[] vec, double[][] a)
	{
		int m	= a[0].length;
		
		if (m == 0)
			return new double[0];
		
		int l	= a.length;
		
		double[] c	= new double[m];
		
		for (int i=0; i<m; i++)
		{
			for (int k=0; k<l; k++)
			{
				c[i] += vec[k]*a[k][i];
			}
		}
		return c;
	}
	
	/**
	 * 매트릭스와 벡터를 곱한다. 2003.01.06 추가
	 */
	public static float[] multiply(float[][] a, float[] b)
	{
		int m	= a.length;
		
		if (m == 0)
			return new float[0];
		
		int l	= a[0].length;
		
		float[] c	= new float[m];
		for (int i=0; i<m; i++)
		{
			for (int k=0; k<l; k++)
			{
				c[i]	+= a[i][k]*b[k];
			}
		}
		return c;
	}
	
	/**
	 * 매트릭스와 벡터를 곱한다. 2003.01.06 추가
	 */
	public static double[] multiplyRef(double[][] a, double[] b, double[] c)
	{
		int m	= a.length;
		
		if (m == 0)
			return new double[0];
		
		int l	= a[0].length;
		
		for (int i=0; i < c.length; i++) {
			c[i] = 0;
		}
		for (int i=0; i<m; i++)
		{
			for (int k=0; k<l; k++)
			{
				c[i]	+= a[i][k]*b[k];
			}
		}
		return c;
	}
	
	public static float[] multiplyRef(float[][] a, float[] b, float[] c)
	{
		int m	= a.length;
		
		if (m == 0)
			return new float[0];
		
		int l	= a[0].length;
		
		for (int i=0; i < c.length; i++) {
			c[i] = 0;
		}
		for (int i=0; i<m; i++)
		{
			for (int k=0; k<l; k++)
			{
				c[i]	+= a[i][k]*b[k];
			}
		}
		return c;
	}
	
	/**
	 * <i>a</i> 의 transpose 를 구한다.
	 */
	public static double[][] transpose(double[][] a)
	{
		int n = a.length;
		
		if (n == 0)
			return new double[0][0];
		
		int m = a[0].length;
		double[][] b = new double[m][n];
		
		for (int i=0; i<m; i++)
			for (int j=0; j<n; j++)
				b[i][j] = a[j][i];
		return b;
	}
	
	/**
	 * <i>a</i> 의 trace 를 구한다. 2002.12.27 추가
	 */
	public static double trace(double[][] a)
	{
		double tr	= 0.0;
		int n	= a.length;
		for (int i=0; i<n; i++)
		{
			tr += a[i][i];
		}
		return tr;
	}
	
	/**
	 * <i>a</i> 와 <i>b</i> 의 차이를 구한다. 2002.12.30 추가
	 */
	public static double[][] difference(double[][] a, double[][] b)
	{
		int n	= a.length;
		
		if (n == 0)
			return new double[0][0];
		
		int m	= a[0].length;
		
		double[][] c	= new double[n][m];
		for (int i=0; i<n; i++)
		{
			for (int j=0; j<m; j++)
			{
				c[i][j]	= a[i][j]-b[i][j];
			}
		}
		
		return c;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (2003-02-20 오후 7:55:39)
	 * @return double[][]
	 * @param m1 double[][]
	 * @param m2 double[][]
	 */
	public static double[][] add(double[][] a, double[][] b) {
		int n	= a.length;
		
		if (n == 0)
			return new double[0][0];
		
		int m	= a[0].length;
		
		double[][] c	= new double[n][m];
		for (int i=0; i<n; i++)
		{
			for (int j=0; j<m; j++)
			{
				c[i][j]	= a[i][j]+b[i][j];
			}
		}
		
		return c;
	}
	
	public static double[] add(double[] a, double[] b) {
		int n = a.length;
		
		if (n == 0)
			return new double[0];
		
		double[] c	= new double[n];
		for (int i=0; i<n; i++)
		{
			c[i] = a[i]+b[i];
		}
		
		return c;
	}
	
	public static float[] add(float[] a, float[] b) {
		int n = a.length;
		
		if (n == 0)
			return new float[0];
		
		float[] c	= new float[n];
		for (int i=0; i<n; i++)
		{
			c[i] = a[i]+b[i];
		}
		
		return c;
	}
	
	public static double norm(double[] vector) {
		return Math.sqrt(innerProduct(vector, vector));
	}
	
	public static float norm(float[] vector) {
		return (float)(Math.sqrt(innerProduct(vector, vector)));
	} 
	
	public static double[] subtract(double[] a, double[] b) {
		return add(a, scalarProduct(b, -1));
	}
	
	public static float[] subtract(float[] a, float[] b) {
		return add(a, scalarProduct(b, -1));
	}
	
	public static double[][] subtract(double[][] a, double[][] b) {
		return add(a, scalarProduct(b, -1));
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (2003-02-18 오후 12:14:08)
	 * @return double
	 * @param M double[][]
	 */
	public static double[] diag(double[][] M) {
		
		int i;
		int length;
		double[] result;
		
		length = M.length;
		
		result = new double[M.length];
		for (i = 0; i < length; i++)
			result[i] = M[i][i];
		
		return result;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (2003-02-18 오후 12:04:00)
	 * @return double[]
	 * @param a double[]
	 * @param b double[]
	 */
	public static double[] difference(double[] a, double[] b) {
		
		int i;
		int length;
		double[] result;
		
		if (a.length != b.length)
			return null;
		
		length = a.length;
		result = new double[length];
		
		for (i = 0; i < length; i++)
			result[i] = a[i] - b[i];
		
		return result;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (2003-02-18 오후 12:16:00)
	 * @return double[][]
	 * @param m double[][]
	 * @param s double
	 */
	public static double[][] scalarProduct(double[][] m, double s) {
		
		double[][] result;
		
		result = new double[m.length][];
		for (int i = 0; i < m.length; i++) {
			
			result[i] = new double[m[i].length];
			
			for (int j = 0; j < m[i].length; j++)
				result[i][j] = s * m[i][j];
		}
		
		return result;
	}
	
	public static double[][] scalarDivide(double[][] m, double s) {
		
		double[][] result;
		
		result = new double[m.length][];
		for (int i = 0; i < m.length; i++) {
			
			result[i] = new double[m[i].length];
			
			for (int j = 0; j < m[i].length; j++){
				if(m[i][j]!=0)
					result[i][j] = m[i][j] / s;
			}
		}
		
		return result;
	}
	

	public static double sumOfAllElements (double[]m) {
		
		double result = 0;
		for (int i = 0; i < m.length; i++)
				result += m[i];
		
		return result;
	}
	
	public static double sumOfAllElements (double[][] m) {
		
		double result = 0;
		int i, j;
		
		for (i = 0; i < m.length; i++)
			for (j = 0; j < m[i].length; j++)
				result += m[i][j];
		
		return result;
	}
	
	public static double maxOfAllElements (double[][] m) {
		
		double max = - Double.MAX_VALUE;
		int i, j;
		
		for (i = 0; i < m.length; i++)
			for (j = 0; j < m[i].length; j++)
				if(m[i][j]>max)
					max=m[i][j];
		
		return max;
	}
	
	/**
	 * 입력된 Vector를 diagnol로 갖는 Matrix를 만든다.
	 */
	public static double[][] makeDiagonalMatrix (double[] v) {
		
		int n = v.length;
		double[][] result = new double[n][n];
		
		for (int i = 0; i < n; i++)
			result[i][i] = v[i];
		
		return result;
	}
	
	/**
	 * <i>a</i> 의 transpose 를 구한다.
	 */
	public static int[][] transpose(int[][] a)
	{
		int n = a.length;
		
		if (n == 0)
			return new int[0][0];
		
		int m = a[0].length;
		int[][] b = new int[m][n];
		
		for (int i=0; i<m; i++)
			for (int j=0; j<n; j++)
				b[i][j] = a[j][i];
		return b;
	}
	
	/**
	 * 둘다 0 이상이면 1로 세팅해준다.
	 * @param matrix a
	 * @param matrix b
	 * @return
	 */
	public static double[][] and(double[][] a, double[][] b){
		int n = a.length;
		int m = a[0].length;
		
		double result[][] = new double[n][m];
		for(int i=0; i<n; i++)
			for(int j=0; j<m; j++){
				if( a[i][j] != 0 && b[i][j] != 0 )
					result[i][j] = 1;
			}
		
		return result;
	}
	
	public static double[] and(double[] a, double[] b){
		int n = a.length;
		double result[] = new double[n];
		for(int i=0; i<n; i++)
			if( a[i] != 0 && b[i] != 0 )
				result[i] =1;
		return result;
	}
	
	/**
	 * 둘중 적어도 하나가 0 이상이면 1로 세팅해준다.
	 * @param matrix a
	 * @param matrix b
	 * @return
	 */
	public static double[][] or(double[][] a, double[][] b){
		int n = a.length;
		int m = a[0].length;
		double result[][] = new double[n][m];
		for(int i=0; i<n; i++)
			for(int j=0; j<m; j++){
				if( a[i][j] != 0 || b[i][j] != 0 )
					result[i][j] = 1;
			}
		
		return result;
	}
	
	public static double[] or(double[] a, double[] b){
		int n = a.length;
		double result[] = new double[n];
		for(int i=0; i<n; i++)
			if( a[i] != 0 || b[i] != 0 )
				result[i] =1;
		return result;
	}
	
	
	public static boolean equals(double[][] a, double[][] b){
		int n = a.length;
		int m = a[0].length;
		for(int i=0; i<n; i++)
			for(int j=0; j<m; j++)
				if( a[i][j] != b[i][j])
					return false;
		
		return true;
	}
	
	
	/**
	 * 입력된 두개 Matrix간의 elementWiseMax을 구한다.
	 * C[i][j] = max(A[i][j], B[i][j]);
	 */
	public static double[][] elementWiseMax(double[][] a, double[][] b){
		int n = a.length;
		int m = a[0].length;
		double[][] c = new double[n][m];
		for(int i=0; i<n; i++)
			for(int j=0; j<m; j++)
				c[i][j] = (a[i][j] > b[i][j]) ? a[i][j] : b[i][j];
		
		return c;
	}
	
	/**
	 * 입력된 두개 Matrix간의 elementWiseMin을 구한다.
	 * C[i][j] = min(A[i][j], B[i][j]);
	 */
	public static double[][] elementWiseMin(double[][] a, double[][] b){
		int n = a.length;
		int m = a[0].length;
		double[][] c = new double[n][m];
		for(int i=0; i<n; i++)
			for(int j=0; j<m; j++)
				c[i][j] = (a[i][j] > b[i][j]) ? b[i][j] : a[i][j];
		
		return c;
	}
	
	/**
	 * 입력된 두개 Vector간의 elementWiseMul을 구한다.
	 * C[i] = A[i] * B[i];
	 */
	public static double[] elementWiseMul(double[] a, double[] b){
		int n = a.length;
		double[] c = new double[n];
		for(int i=0; i<n; i++)
				c[i] = b[i] * a[i];
		
		return c;
	}
	
	/**
	 * 입력된 두개 Matrix간의 elementWiseMul을 구한다.
	 * C[i][j] = A[i][j] * B[i][j];
	 */
	public static double[][] elementWiseMul(double[][] a, double[][] b){
		int n = a.length;
		int m = a[0].length;
		double[][] c = new double[n][m];
		for(int i=0; i<n; i++)
			for(int j=0; j<m; j++)
				c[i][j] = b[i][j] * a[i][j];
		
		return c;
	}
	
	/**
	 * 입력된 두개 Vector간의 elementWiseDiv을 구한다.
	 * C[i] = A[i] / B[i];
	 */
	public static double[] elementWiseDiv(double[] a, double[] b){
		int n = a.length;
		double[] c = new double[n];
		for(int i=0; i<n; i++)
				c[i] = a[i] / b[i];
		
		return c;
	}
	
	/**
	 * 입력된 두개 Matrix간의 elementWiseDiv을 구한다.
	 * C[i][j] = A[i][j] / B[i][j];
	 */
	public static double[][] elementWiseDiv(double[][] a, double[][] b){
		int n = a.length;
		int m = a[0].length;
		double[][] c = new double[n][m];
		for(int i=0; i<n; i++)
			for(int j=0; j<m; j++)
				c[i][j] = a[i][j] / b[i][j] ;
		
		return c;
	}

	/**
	 * 입력된 Matrix의 elementWisePow을 구한다.
	 * C[i][j] = A[i][j] ** pownum;
	 */
	public static double[][] elementWisePow(double[][] a, double pownum){
		int n = a.length;
		int m = a[0].length;
		double[][] c = new double[n][m];
		for(int i=0; i<n; i++)
			for(int j=0; j<m; j++)
				c[i][j] = Math.pow(a[i][j], pownum);
		
		return c;
	}
	
	/**
	 * 입력된 Vector의 elementWisePow을 구한다.
	 * C[i] = A[i] ** pownum;
	 */
	public static double[] elementWisePow(double[] a, double pownum){
		int n = a.length;
		double[] c = new double[n];
		for(int i=0; i<n; i++)
				c[i] = Math.pow(a[i], pownum);
		
		return c;
	}
	
	/**
	 * 입력된 Matrix의 elementWisePow을 구한다.
	 *  * C[i][j] = |A[i][j]|;
	 */
	public static double[][] elementWiseAbs(double[][] a){
		int n = a.length;
		int m = a[0].length;
		double[][] c = new double[n][m];
		for(int i=0; i<n; i++)
			for(int j=0; j<m; j++)
				c[i][j] = Math.abs(a[i][j]);
		
		return c;
	}
	
	/**
	 * 입력된 Vector의 elementWiseAbs을 구한다.
	 * C[i] = |A[i]|;
	 */
	public static double[] elementWiseAbs(double[] a){
		int n = a.length;
		double[] c = new double[n];
		for(int i=0; i<n; i++)
				c[i] = Math.abs(a[i]);
		
		return c;
	}
	
	public static double[][] getI (int size) {
		
		double[][] result = new double[size][size];
		fill(result, 0);
		
		for (int i = 0; i < size; i++)
			result[i][i] = 1;
		
		return result;
	}
	
	public static double[][] makeVectorToWideMatrix (double[] a) {
		
		double[][] result = new double[1][];
		result[0] = a;
		
		return result;
	}
	
	public static double[][] makeVectorToLongMatrix (double[] a) {
		
		return transpose(makeVectorToWideMatrix(a));
	}
	
	public static double[] rowSum(double[][] matrix){
		int n = matrix.length;
		double[] result = new double[n];
		
		for( int i = 0; i < n; i++ ){
			double sum = 0;
			for( int j = 0; j < matrix[i].length; j++ )
				sum += matrix[i][j];
			
			result[i] = sum; 
		}
		
		return result;
	}
	
	public static double[] columnSum(double[][] matrix){
		int n = matrix.length;
		int m = matrix[0].length;
		double[] result = new double[m];
		
		for( int j = 0; j < m; j++ ){
			double sum = 0;
			for( int i = 0; i < n; i++ )
				sum += matrix[i][j];
			
			result[j] = sum; 
		}
		
		return result;
	}
	
	public static double[] power (double[] v, double p) {
		
		double[] x = new double[v.length];
		
		for (int i= 0; i < x.length; i++)
			x[i] = Math.pow(v[i], p);
		
		return x;
	}
	
	public static double[][] unitProduct (double[][] m1, double[][] m2) {
		
		double[][] r = new double[m1.length][m1[0].length];
		
		for (int i = 0; i < r.length; i++)
			for (int j = 0; j < r[i].length; j++) 
				r[i][j] = m1[i][j] * m2[i][j];
		
		return r;
	}
	
	public static double[] unitProduct (double[] m1, double[] m2) {
		
		double[] r = new double[m1.length];
		
		for (int i = 0; i < r.length; i++)
			r[i] = m1[i] * m2[i];
		
		return r;
	}
	
	public static double sum (double[] m1) {
		double ret = 0;
		for (double value : m1) {
			ret += value;
		}
		return ret;
	}
	
	public static float sum (float[] m1) {
		float ret = 0;
		for (float value : m1) {
			ret += value;
		}
		return ret;
	}
	
	public static double[] sum (double[] m1, double[] m2) {
		
		double[] r = new double[m1.length];
		
		for (int i = 0; i < r.length; i++)
			r[i] = m1[i] + m2[i];
		
		return r;
	}
	
	public static double[][] symmDichMatrix(double[][] m)
	{
		double[][] tempAdj = CAMatrixMathDense.symmetrize(m, SYMMETRIZE_MAX);
		int[][] tempAdj2 = CAMatrixMathDense.dichotomize(tempAdj, 0, DICHOTOMIZE_GT);
		double[][] result = new double[tempAdj2.length][tempAdj2[0].length];
		for( int i = 0; i < tempAdj2.length; i ++ )
			for( int j = 0; j < tempAdj2[i].length; j++ )
				result[i][j] = tempAdj2[i][j];
		
		return result;
	}
	
	public static double innerProduct(double[] x, double[] y) {
		
		int n	= x.length;
		double s1 = 0.0;
		
		for (int i=0; i<n; i++)
			s1	+= x[i]*y[i];
		
		return s1;
	}
	
	public static float innerProduct(float[] x, float[] y) {
		
		int n	= x.length;
		float s1 = 0.0f;
		
		for (int i=0; i<n; i++)
			s1	+= x[i]*y[i];
		
		return s1;
	}
	
	public static double[] log (double[] x) {
		
		double[] r = new double[x.length];
		
		for (int i = 0; i < x.length; i++)
			r[i] = Math.log(x[i]);
		
		return r;
	}
	
	public static double[] exp (double[] x) {
		
		double[] r = new double[x.length];
		
		for (int i = 0; i < x.length; i++)
			r[i] = Math.exp(x[i]);
		
		return r;
	}
	
	public static double[] add (double[] x, double v) {
		
		double[] r = new double[x.length];
		
		for (int i = 0; i < x.length; i++)
			r[i] = x[i] + v;
		
		return r;
	}
	
	public static double[] scalarProduct (double[] x, double v) {
		
		double[] r = new double[x.length];
		
		for (int i = 0; i < x.length; i++)
			r[i] = x[i] * v;
		
		return r;
	}
	
	public static float[] scalarProduct (float[] x, float v) {
		
		float[] r = new float[x.length];
		
		for (int i = 0; i < x.length; i++)
			r[i] = x[i] * v;
		
		return r;
	}

	public static double[] scalarDivide (double[] x, double v) {
		
		double[] r = new double[x.length];
		
		for (int i = 0; i < x.length; i++){
			if(x[i]!=0)
				r[i] = x[i] / v;
		}
		
		return r;
	}
	
	public static double[] scalarAdd(double[] x, double v){
		double[] r = new double[x.length];
		
		for (int i = 0; i< x.length; i++)
			r[i] = x[i] + v;
		
		return r;
	}
	
	public static double[][] scalarAdd(double[][] x, double v){
		double[][] r = new double[x.length][];
		
		for (int i = 0; i< x.length; i++){
			r[i] = new double[x[i].length];
			for(int j=0; j<x[i].length; j++)
				r[i][j] = x[i][j]+v;
		}
				
		
		return r;
	}
	
	
	
	/**
	 * matrix 를 vector 로 변환
	 * @param matrix square matrix
	 * @param ignoreDiagonal true 면 diagonal 을 무시
	 */
	public static double[] toVector(double[][] matrix, boolean ignoreDiagonal)
	{
		
		if (matrix.length != matrix[0].length) {
			double[] result = new double[matrix.length * matrix[0].length];
			
			int count = 0;
			for (int i = 0; i < matrix.length; i++)
				for (int j = 0; j < matrix[i].length; j++)
					result[count++] = matrix[i][j];
			
			return result;
		}
		
		int mn = matrix.length;
		int n = mn*mn;
		if (ignoreDiagonal) n = n-mn;
		
		double[] vector = new double[n];
		if (ignoreDiagonal){
			int count = 0;
			for (int i=0; i<mn; i++){
				for (int j=0; j<mn; j++){	
					if (i!=j){
						vector[count] = matrix[i][j];
						count++;
					}
				}
			}
		}
		else{
			int count = 0;
			for (int i=0; i<mn; i++){
				for (int j=0; j<mn; j++){	
					vector[count] = matrix[i][j];
					count++;
				}
			}
		}
		
		return vector;
	}

	public static double[] toVector(double[][] matrix, boolean ignoreDiagonal, boolean useEntire)
	{
		
		if(useEntire){
			return toVector(matrix, ignoreDiagonal);
		}
		
		int n = matrix.length;
		int m = matrix[0].length;
		
		if( n != m )
			ignoreDiagonal = false;
		
		double[] vector;
		
		if( ignoreDiagonal )
			vector = new double[n*m - n];
		else
			vector = new double[n*m];
		
		if( ignoreDiagonal ) {
			for (int i=0; i<n; i++){
				for (int j=0; j<m; j++){	
					if(i>j)
						continue;
					if( i != j ){
						vector[i * (m - 1) + (j - 1)] = matrix[i][j];
						vector[j * (m - 1) + i] = matrix[i][j];
					}
				}
			}
		}
		else{
			for (int i=0; i<n; i++){
				for (int j=0; j<m; j++){	
					if(i>j)
						continue;
					if( i != j ){
						vector[i * m + j] = matrix[i][j];
						vector[j * m + i]  = matrix[i][j];
					}
				}
			}
		}
		
		return vector;
	}
	
	/**
	 * Square Matrix를 toVector로 만든 결과 Vector를
	 * 다시 Matrix로 복구한다.
	 */
	public static double[][] toMatrix(double[] vector, boolean diagonal){
		int n;
		if (diagonal)
			n = (int)Math.sqrt(vector.length);
		else
			n = (int)(0.5*(1+ Math.sqrt(1 + 4*vector.length)));
		double[][] matrix = new double[n][n];
		
		int columnSize = diagonal ? n : n - 1;
			
		for( int i =0 ; i<vector.length; i++){
			int row =  i / columnSize;
			int col = i % columnSize;
			if (!diagonal && col >= row) col++;
			matrix[row][col] = vector[i];
		}
		return matrix;
	}
	
	public static double[][] toWideMatrix(double[] vector){
		int n = vector.length;
		double[][] matrix = new double[1][n];
		
		for(int i =0; i<n; i++){
			matrix[0][i] = vector[i];
		}	
		return matrix;
	}
	
	public static double[][] rank(double[][] matrix, boolean asc, boolean ignoreSameValue, int[] numOfDifferectValue, int offset){
		// 오름차순으로 정렬할 것인지 내림차순으로 정렬할 것인지 결정한다.
		
		ValueIndexPair.ascending = asc;
		
		int n = matrix.length;
		int m = matrix[0].length;
		int cardinality = 0;
		for(int i =0; i<n; i++)
			for(int j=0; j<m; j++)
				if(matrix[i][j]!=0)
					cardinality++;
		
		int nIndex = 0;
	   	ValueIndexPair[] pairs = new ValueIndexPair[cardinality];
	   	for(int i=0; i<n; i++)
	   		for(int j=0; j<m; j++){
	   			double value = matrix[i][j];
	   			if(value!=0){
	   				// 2차원 배열의 index를 1차원으로 변경시켜서 집어넣는다.
	   				pairs[nIndex] = new ValueIndexPair(value, i*n+j);
	   				nIndex++;
	   			}
	   		}
	   	
	   	// Sort 한다.
	   	java.util.Arrays.sort(pairs);
	   	
	   	// 결과를 기록한다.
	   	double[][] result = new double[n][m];
	   	
	   	if ( ignoreSameValue ){
	   		numOfDifferectValue[0] = 1;	// value 갯수 (1.0 과  2.0으로 이루어진 matrix 경우에는 2)
	   		int sameValueCount = -1;	// 첫 값을 pairs[0] 으로 잡았기 때문에 sameValueCount 를 0 이 아니라 -1 로 설정해 놓는다.
	   		double oldValue = pairs[0].value;
	   		for ( int i = 0; i < pairs.length; i++ ){
	   			if ( pairs[i].value == oldValue )
	   				sameValueCount ++;
	   			else
	   				numOfDifferectValue[0]++;
	   			
	   			result[pairs[i].index/n][pairs[i].index%n] = offset + i - sameValueCount;
	   			oldValue = pairs[i].value;
	   		}
	   	}
	   	else
	   		for ( int i = 0; i < pairs.length; i++ )
	   			result[pairs[i].index/n][pairs[i].index%n] = offset + i;
	 
	   	return result;
	}
    
	
	public static int[] sortedIndex( double[] vector, int criterion ){
		int n = vector.length;
		
		HashMap<Double, Queue<Integer>> valueToIndicies = new HashMap<Double, Queue<Integer>>();
		for(int i=0; i<n; i++){
			if(!valueToIndicies.containsKey(vector[i])){
				valueToIndicies.put(vector[i], new LinkedList<Integer>());
			}
			valueToIndicies.get(vector[i]).add(i);
		}
		
		double[] dVectorSorted = vector.clone();
		Arrays.sort(dVectorSorted);
		
		int[] order = new int[n];
		for(int i=0; i<n; i++){
			double value = dVectorSorted[i];
			order[i] = valueToIndicies.get(value).poll();
		}
		
		if( criterion == DECREASING_ORDER )
			order = CAMatrixMathDense.reverse(order);
		
		return order;
	}
	
	public static int maxIndex(double[] vector){
		int maxIndex = 0;
		double maxValue = - Double.MAX_VALUE;
		for(int i=0; i<vector.length; i++){
			if(maxValue < vector[i] ){
				maxIndex = i;
				maxValue = vector[i];
			}
		}
		return maxIndex;
	}
	
	public static int[] reverse(int[] vector){
		int n = vector.length;
		int[] result = new int[n];
		
		for( int i = 0; i < n; i++ )
			result[i] = vector[n - i - 1];
		
		return result;
	}
	
	/**
	 * 입력된 vector의 reverse를 구한다.
	 * { 1, 2, 3, 4 } => { 4, 3, 2, 1 }
	 */
	public static double[] reverse(double[] vector){
		int n = vector.length;
		double[] result = new double[n];
		
		for( int i = 0; i < n; i++ )
			result[i] = vector[n - i - 1];
		
		return result;
	}
	
	/*
	 * 입력된 matrix 로부터 지정된 노드들로 구성된 sub-matrix 를 추출한다.
	 * @param nodes extract 될 노드들의 index.
	 */
	public static double[][] extract(double[][] a, int[] nodes)
	{
		int n = nodes.length;
		double[][] extract = new double[n][n];
		for (int i=0; i<n; i++)
		{
			for (int j=0; j<n; j++)
			{
				extract[i][j] = a[nodes[i]][nodes[j]];
			}
		}
		
		return extract;
	}
	
	/** Get a submatrix.
	  @param A	Original matrix
	  @param i0   Initial row index
	  @param i1   Final row index
	  @param c    Array of column indices.
	  @return     A(i0:i1,c(:))
	  @exception  ArrayIndexOutOfBoundsException Submatrix indices
	*/
	public static double[][] extract(double[][] A, int i0, int i1, int[] c) {
		double[][] B = new double[i1-i0+1][c.length];
		try {
	    	for (int i = i0; i <= i1; i++) {
	    		for (int j = 0; j < c.length; j++) {
	    			B[i-i0][j] = A[i][c[j]];
	            }
	    	}
	      } catch(ArrayIndexOutOfBoundsException e) {
	         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
	   }
	   return B;
	}

	/** Get a submatrix.
	  @param A	Original matrix
	  @param r    Array of row indices.
	  @param i0   Initial column index
	  @param i1   Final column index
	  @return     A(r(:),j0:j1)
	  @exception  ArrayIndexOutOfBoundsException Submatrix indices
	*/
	public static double[][] extract(double[][] A, int[] r, int j0, int j1) {
		double[][] B = new double[r.length][j1-j0+1];
	    try {
	    	for (int i = 0; i < r.length; i++) {
	    		for (int j = j0; j <= j1; j++) {
	    			B[i][j-j0] = A[r[i]][j];
	            }
	    	}
	    } catch(ArrayIndexOutOfBoundsException e) {
	    	throw new ArrayIndexOutOfBoundsException("Submatrix indices");
	    }
	    return B;
	}
	
	/**
	 * 입력된 Vector로부터 지정된 노드들로 구성된 subVector를 추출한다.<br>
	 * 
	 * @param double[]
	 * @param int[] nodes
	 * @return double[]
	 */
	public static double[] extract(double[] a, int[] nodes ){
		int n = nodes.length;
		double[] extract = new double[n];
		for (int i=0; i<n; i++)
		{
			extract[i] = a[nodes[i]];
		}
		return extract;
	}
	
	public static double[] extract(double[] a, double[] filter ){
		int n = filter.length;
		double[] extract = new double[n];
		int cnt =0;
		for (int i=0; i<n; i++)
		{
			if(filter[i]!=0)
				extract[cnt++] = a[i];
		}
		double[] result = new double[cnt];
		System.arraycopy(extract, 0, result, 0, cnt);
		return result;
	}

	/**********************************************************************************************
	 * 입력된 Matrix로부터 지정된 index에 해당하는 행만을 추출한 subMatrix를 구한다.<br>
	 * extract()는 정사각 행렬에서 행과 열을 모두 추출하는 것이라면<br>
	 * extractColumn()는 일반 행렬에서 행만 추출한다.<br>
	 * 2-mode 데이터에서 지정된 category의 정보만 추출할때 사용한다.<br>	 
	 * 
	 * @param duoble[][]
	 * @param index - 추출할 행의 index
	 * @return duoble[][]
	 **********************************************************************************************/
	public static double[][] extractRow(double[][] matrix, int[] index){
		int n = index.length;
		
		double[][] extract = new double[n][matrix[0].length];
		
		for( int i = 0; i < n; i++ )
			extract[i] = matrix[index[i]].clone();
		
		return extract;
	}
	
	/**********************************************************************************************
	 * 입력된 Matrix로부터 지정된 index에 해당하는 행만을 추출한 subMatrix를 구한다.<br>
	 * extract()는 정사각 행렬에서 행과 열을 모두 추출하는 것이라면<br>
	 * extractColumn()는 일반 행렬에서 행만 추출한다.<br>
	 * 2-mode 데이터에서 지정된 category의 정보만 추출할때 사용한다.<br>	 
	 * 
	 * @param duoble[][]
	 * @param index - 추출할 행의 index
	 * @return duoble[][]
	 **********************************************************************************************/
	public static double[][] extractRow(double[][] matrix, int startrow, int endrow){	

		double[][] extract = new double[endrow-startrow+1][matrix[0].length];
		
		for( int i = startrow; i <= endrow; i++ )
			extract[i-startrow] = matrix[i].clone();
		
		return extract;
	}
	/**********************************************************************************************
	 * 입력된 Matrix로부터 지정된 index에 해당하는 열만을 추출한 subMatrix를 구한다.<br>
	 * extract()는 정사각 행렬에서 행과 열을 모두 추출하는 것이라면<br>
	 * extractColumn()는 일반 행렬에서 열만 추출한다.<br>
	 * 2-mode 데이터에서 지정된 노드의 정보만 추출할때 사용한다.<br>	 
	 * 
	 * @param duoble[][]
	 * @param index - 추출할 열의 index
	 * @return duoble[][]
	 **********************************************************************************************/	
	public static double[][] extractColumn(double[][] matrix, int[] index){
		int n = index.length;
		
		double[][] extract = new double[matrix.length][n];
		
		for( int i = 0; i < n; i++ ){
			int currentIndex = index[i];
			for(int j=0; j<matrix.length; j++)
				extract[j][i] = matrix[j][currentIndex];
		}
		return extract;
	}
	
	public static double[] extractColumn(double[][] matrix, int index) {
		double[] tortn = new double[matrix.length];
		for (int i=0; i < tortn.length; i++)
			tortn[i] = matrix[i][index];
		return tortn;
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
	public static double[][] filterMatrix( int[] nodeFilter, double[][] edgeFilter, double[][] matrix ){
		
		int n =matrix.length;
		int m = matrix[0].length;
		
		double[][] filteredM;
		
		if( edgeFilter == null ){
			filteredM = copy(matrix);
			
		}
		else{
			filteredM = new double[n][m];
			
			for(int i=0; i<n; i++){
				for(int j=0; j<m; j++){
					boolean include = false;
					double value = matrix[i][j];
					for (int k = 0; k < edgeFilter.length; k++) {
						if (value >= edgeFilter[k][0] && value <= edgeFilter[k][1]){
							include = true;
							break;
						}
					}
					if( include == true )
						filteredM[i][j]= value;
				}
			}
		}
		
		if( nodeFilter == null )
			return filteredM;
		else
			return extract(filteredM, nodeFilter);		
	}
	
	
	/*
	* matrix 의 모든 요소를 0 과 1로 만드는 dichotomize 연산을 수행한다.
	* @param threshold 기준값
	*/
	public static int[][] dichotomize(double[][] a, double threshold, int option)
	{
		int n = a.length;
		
		int[][] dichotomize = new int[n][];
		
		for (int i=0; i<n; i++)
		{
			int m = a[i].length;
			dichotomize[i] = new int[m];
			for (int j=0; j<m; j++)
			{
				switch (option)
				{
				case DICHOTOMIZE_GT:
					if (a[i][j]>threshold) dichotomize[i][j] = 1;
					else dichotomize[i][j] = 0;
					break;
				case DICHOTOMIZE_GE:
					if (a[i][j]>=threshold) dichotomize[i][j] = 1;
					else dichotomize[i][j] = 0;
					break;
				case DICHOTOMIZE_EQ:
					if (a[i][j]==threshold) dichotomize[i][j] = 1;
					else dichotomize[i][j] = 0;
					break;
				case DICHOTOMIZE_LE:
					if (a[i][j]<=threshold) dichotomize[i][j] = 1;
					else dichotomize[i][j] = 0;
					break;
				case DICHOTOMIZE_LT:
					if (a[i][j]<threshold) dichotomize[i][j] = 1;
					else dichotomize[i][j] = 0;
					break;
				case DICHOTOMIZE_NEQ:
					if (a[i][j]!=threshold) dichotomize[i][j] = 1;
					else dichotomize[i][j] = 0;
					break;
				}
			}
		}
		
		return dichotomize;
	}

	/*
	* matrix 를 symmetrize 한다.
	*/
	public static double[][] symmetrize(double[][] a, int option)
	{
		int n = a.length;
		
		double[][] symmetrize = new double[n][n];
		
		for (int i=0; i<n; i++)
		{
			symmetrize[i][i] = a[i][i];
			
			for (int j=i+1; j<n; j++)
			{
				switch (option)
				{
				case SYMMETRIZE_MAX:
					if (a[i][j]>a[j][i]) symmetrize[i][j] = symmetrize[j][i] = a[i][j];
					else  symmetrize[i][j] = symmetrize[j][i] = a[j][i];
					break;
				case SYMMETRIZE_MIN:
					if (a[i][j]<a[j][i]) symmetrize[i][j] = symmetrize[j][i] = a[i][j];
					else  symmetrize[i][j] = symmetrize[j][i] = a[j][i];
					break;
				case SYMMETRIZE_AVERAGE:
					symmetrize[i][j] = symmetrize[j][i] = (a[i][j]+a[j][i])/2.0;
					break;
				case SYMMETRIZE_SUM:
					symmetrize[i][j] = symmetrize[j][i] = a[i][j]+a[j][i];
					break;
				case SYMMETRIZE_PRODUCT:
					symmetrize[i][j] = symmetrize[j][i] = a[i][j]*a[j][i];
					break;
				case SYMMETRIZE_LOWER:
					symmetrize[i][j] = symmetrize[j][i] = a[j][i];
					break;
				case SYMMETRIZE_UPPER:
					symmetrize[i][j] = symmetrize[j][i] = a[i][j];
					break;
				}
			}
		}
		
		return symmetrize;
	}
	
	public static boolean isSymmetric(double[][] matrix){
		if (matrix == null || matrix.length == 0 || matrix[0] == null) return false;
		if (matrix.length != matrix[0].length) return false;
		
		int n = matrix.length;
		for( int i=0; i< n; i++ )
		{
			for( int j=0; j< n; j++ )
			{
				if( Math.abs(matrix[i][j] - matrix[j][i]) >= 0.0000000001 )
				{
					return false;
				}
			}
		}
		
		return true;
	}

	/*
	* matrix 에서 이전 값을 새로운 값으로 치환하는 recode 연산을 수행한다.
	*/
	public static double[][] recode(double[][] a, double[] oldVal, double[] newVal, boolean diagonalLock)
	{
		int n = a.length;
		int m = a[0].length;
		
		if (m!=n) diagonalLock = false;
		
		double[][] recode = copy(a);
		
		for (int k=0; k<oldVal.length; k++)
		{
			for (int i=0; i<n; i++)
				for (int j=0; j<m; j++){
					if(i==j && diagonalLock)
						continue;
					else{		
						if (a[i][j]==oldVal[k]){
							recode[i][j] = newVal[k];
						}
					}
				}
		}
				
		return recode;
	}

	/*
	* vector 에서 이전 값을 새로운 값으로 치환하는 recode 연산을 수행한다.
	*/
	public static double[] recode(double[] a, double[] oldVal, double[] newVal)
	{
		int n = a.length;
		
		double[] recode = a.clone();
		
		for (int k=0; k<oldVal.length; k++)
		{
			for (int i=0; i<n; i++)	
				if (a[i]==oldVal[k]){
						recode[i] = newVal[k];
				}
		}
				
		return recode;
	}
	
	/**
	 * 입력된 Matrix 값을 뒤집어 준다.
	 */
	
	public static double[][] toReverse(double[][] matrix, boolean includeZero, boolean processDiagonal, int reverseOption, double reverseValue )
	{
		int n = matrix.length;
		int m = matrix[0].length;
		
    	double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;

		if ( reverseOption == REVERSE_INTERVAL )
		{
			
			int count=0;
			for(int i =0; i<n; i++)
				for(int j=0; j<m; j++){
					if( i == j && n == m && processDiagonal == false )
						continue;
					else{
						double value = matrix[i][j];
						if(value!=0){
							count++;
							min = Math.min(min, value);
							max = Math.max(max, value);
						}
					}
				}
			

			// 비어있는 경우에는 min과 max를 0으로 한다.
			if(count==0)
			{
				min = 0;
				max = 0;
			}
			
			// Zero를 포함하여 계산할 경우에는 max와 min의 결과에 0을 고려해 준다.
			if( includeZero == true && count != m*n )
			{
				min = Math.min(min, 0.0);
				max = Math.max(max, 0.0);
			}

		}
    			
		double resultMatrix[][] = new double[n][m];
		if (includeZero) {
			for( int i = 0; i < n; i++)
			{
				for( int j = 0; j < m; j++ )
				{
					if( i == j && n == m && processDiagonal == false )
					{
						resultMatrix[i][j] =  matrix[i][j];
						continue;
					}
					
					switch ( reverseOption )
					{
					case REVERSE_INTERVAL:
						resultMatrix[i][j] = max + min - matrix[i][j];
						break;
					case REVERSE_RATIO:
						resultMatrix[i][j] = 1.0 / matrix[i][j];
						break;
					case REVERSE_FIXED_DECAY:
						resultMatrix[i][j] = Math.pow(reverseValue,matrix[i][j]);
						break;
					default:
						System.out.println("Oh No");
					}
				}
			}
		}
		else { 
			
			for( int i = 0; i < n; i++)
			{
				for( int j = 0; j < m; j++ )
				{
					if( i == j && n == m && processDiagonal == false )
					{
						resultMatrix[i][j] =  matrix[i][j];
						continue;
					}
					
					if(matrix[i][j]!=0){
						switch ( reverseOption )
						{
						case REVERSE_INTERVAL:
							resultMatrix[i][j] = max + min - matrix[i][j];
							break;
						case REVERSE_RATIO:
							resultMatrix[i][j] = 1.0 / matrix[i][j];
							break;
						case REVERSE_FIXED_DECAY:
							resultMatrix[i][j] = Math.pow(reverseValue,matrix[i][j]);
							break;
						default:
							System.out.println("Oh No");
						}
					}
				}
			}
		}
		
		return resultMatrix;
	}
	
	public static double[] toReverse(double[] vector, boolean includeZero, int reverseOption, double reverseValue )
	{
		int n = vector.length;
		
    	double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;

		if ( reverseOption == REVERSE_INTERVAL )
		{
			int count =0;
			for(int i =0; i<n; i++)
			{
				double value = vector[i];
				if(value!=0){
					min = Math.min(min, value);
					max = Math.max(max, value);
					count++;
				}
			}

			// 비어있는 경우에는 min과 max를 0으로 한다.
			if( count == 0 )
			{
				min = 0;
				max = 0;
			}
			
			// Zero를 포함하여 계산할 경우에는 max와 min의 결과에 0을 고려해 준다.
			if( includeZero == true && count != n )
			{
				min = Math.min(min, 0.0);
				max = Math.max(max, 0.0);
			}

		}
    			
		double[] resultVector = new double[n];
		for( int index = 0; index < n; index++)
		{
			if ( includeZero == false && vector[index] == 0.0 )
				continue;
				
			switch ( reverseOption )
			{
			case REVERSE_INTERVAL:
				resultVector[index] = max + min - vector[index];
				break;
			case REVERSE_RATIO:
				resultVector[index] = 1.0 / vector[index];
				break;
			case REVERSE_FIXED_DECAY:
				resultVector[index] = Math.pow(reverseValue, vector[index]);
				break;
			default:
				System.out.println("Oh No");
			}
		}
		
		return resultVector;
	}
	

	/*
	 * capacity 를 cost 로 변환한다. <i>toCapacity</i> 와 연산이 같다.
	 *
	 * 2003년 7월 2일
	 * 김세권 수정 - Include/Exclude '0' Option 추가
	 *
	 */
	public static double[][] toCost(double[][] capacity, boolean includableZero)
	{
		int n	= capacity.length;
		double[][] cost = new double[n][n];
		
		double min = Double.MAX_VALUE;
		double max = 0;
		
		for (int i=0; i<n; i++){
			for (int j=0; j<n; j++){
				if (i!=j){
	               
					double value = capacity[i][j];
					
					if ((includableZero || (value != 0))) {
	                	min = Math.min(min, value);
						max = Math.max(max, value);
					}
				}
			}
		}
		
		if (min==Double.MAX_VALUE) min = 0;
		
		for (int i=0; i<n; i++){
			for (int j=0; j<n; j++){	
				if(i!=j){	
					double value = capacity[i][j];
					if ((includableZero || (value != 0))) 
						cost[i][j] = max+min-capacity[i][j];
				}
			}
		}
		
		return cost;
	}
	
	 // 각각의 원소의 역수값을 취해서 넘겨준다.
    // Input array 에는 0.0 이 있어서는 안 된다.
	public static double[] Reciprocal1D(double[] sdm1dArray)
	{
		int n = sdm1dArray.length;
		double[] sdm1dArrayResult = new double[n];
		for (int nIndex=0; nIndex<n; nIndex++){
				sdm1dArrayResult[nIndex] =  1.0/sdm1dArray[nIndex];
		}
		return sdm1dArrayResult;	
	}
	
	
	/**
	 * 노드의 Attribute Matrix 중 Missing value를 포함하고 있는 row(노드에 해당)를 삭제한다. 
	 * 분석을 수행하기 위한 전처리 과정으로 사용된다.
	 * 
	 */
	public static double[][] extractMissingManyVector(double[][] vectors, double[][] missingData) {
		HashSet<Integer> set = new HashSet<Integer>();
		int i, j, k;
		double value;
		boolean missing;
		int numberOfVectors = vectors.length;
		int lengthOfVector = vectors[0].length;
		
		for (i = 0; i < numberOfVectors; i++) {
			for (j = 0; j < lengthOfVector; j++) {
				value = vectors[i][j];
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
		
		double [][] ret = new double[numberOfVectors][lengthOfVector - set.size()];
		int cnt = 0;
		for (i = 0; i < lengthOfVector; i++) {
			if ( set.contains(i) ) 
				continue;		
			for (j = 0; j < numberOfVectors; j++)
				ret[j][cnt] =  vectors[j][i];
			cnt++;
		}
		
		return ret;
	}
	
	/* 
	 * Autocorrelation - Binary에서 binary 체크하는 메소드
	 * 2종류 이하의 값이 있는 경우에는 거짓을 반환
	 * 3종류 이상의 값이 있는 경우에는 참을 반환
	 */
	public static boolean checkAutocorrelationBinary(double[] list){
		TreeSet<Double> set = new TreeSet<Double>();
		int size = list.length;
        for(int i =0; i < size; i++ ){
        	set.add(list[i]);
        	if (set.size() > 2)
        		return true; 
        }
       return false;
	}
	
	/*
	* square matrix 의 diagonal 값들을 0 으로 셋팅한다.
	*/
	public static void removeDiagonal(double[][] matrix)
	{
		for (int i=0; i<matrix.length; i++)
				matrix[i][i] = 0;
	}

	/*
	* cost 를 capacity 로 변환한다. <i>toCost</i> 와 연산이 같다.
	     *
	     * 2003년 7월 2일
	     * 김세권 수정 - Include/Exclude '0' Option 추가
	     */
	public static double[][] toCapacity(double[][] cost, boolean includableZero)
	{
		return CAMatrixMathDense.toCost(cost, includableZero);
	}

	public static double[][] toCapacity (double[][] cost) {
	    
	    return toCapacity(cost, false);
	}

	public static double[][] toCost (double[][] capacity) {
	    
	    return CAMatrixMathDense.toCost(capacity, false);
	}


	/*
	* similarity matrix 를 dissimilarity matrix 로 변환한다.
	* Edited by 김세권 2003 04 05
	*/
	public static double[][] toDissimilarity(double[][] similarity)
	{
			
		int n	= similarity.length;
		double[][] result = new double[n][n];
		
		double min = Double.MAX_VALUE;
		double max = 0;
		
		for (int i=0; i<n; i++)
		{
			for (int j=0; j<n; j++)
			{
				if (i!=j && similarity[i][j]!=0)
				{
					min = Math.min(min, similarity[i][j]);
					max = Math.max(max, similarity[i][j]);
				}
			}
		}
		
		if (min==Double.MAX_VALUE) min = 0;
		
		for (int i=0; i<n; i++)
		{
			for (int j=0; j<n; j++)
			{
				if (i==j || similarity[i][j]==0)
					result[i][j] = 0;
				else
					result[i][j] = max+min-similarity[i][j];
			}
		}
		
		return result;
	}

	/*
	* dissimilarity matrix 를 similarity matrix 로 변환한다.
	*/
	public static double[][] toSimilarity(double[][] dissimilarity)
	{
		return toDissimilarity(dissimilarity);
	}

	/*
	* similarity Vector 를 dissimilarity Vector 로 변환한다. 
	* 2003.03.26 woo-shick
	*/
	public static double[] toDissimilarity(double[] similarity)
	{
		double[] dissim = new double[similarity.length];
		for(int i=0; i<similarity.length; i++)
		{
			dissim[i] = 1.0-similarity[i];
		}
		return dissim;
	}

	/*
	* dissimilarity Vector 를 similarity Vector 로 변환한다.
	*/
	public static double[] toSimilarity(double[] dissimilarity)
	{
		return toDissimilarity(dissimilarity);
	}

	public static String[] extract(String[] a, int[] nodes){
		int n = nodes.length;
		String[] extract = new String[n];
		for( int i = 0; i < n; i++ )
			extract[i] = a[nodes[i]];
		
		return extract;
	}

	/**
	 * 입력된 vector를 이용해 difference Matrix를 생성한다.<br>
	 * matrix[i][j] = abs( vector[i] - vector[j] );<br>
	 * 
	 * @param CDVector vector
	 * @return NMDoubleMatrix
	 */
	public static double[][] absoluteDifference(double[] vector){
		
		int n = vector.length;
		double[][] result = new double[n][n];
		for(int i=0; i<n; i++)
			for(int j=i+1; j<n; j++){
				double val = Math.abs(vector[i]-vector[j]);
				result[i][j] = val;
				result[j][i] = val;
			}
		return result;
	}
	
	/**
	 * 입력된 vector를 이용해 exactMatch Matrix를 생성한다.<br>
	 * matrix[i][j] = ( vector[i] == vector[j] )? 1 : 0;<br>
	 * 
	 * @param CDVector vector
	 * @return NMDoubleMatrix
	 */
	public static double[][] exactMatch(double[] vector){
		
		int n = vector.length;
		double[][] result = new double[n][n];
		for(int i=0; i<n; i++)
			for(int j=i; j<n; j++){
				double val = ( vector[i] == vector[j] )? 1 : 0;
				result[i][j] = val;
				result[j][i] = val;
			}
		return result;
	}
	
	/**
	 * Affiliation Matrix를 이용하여 Comembership을 만든다.<br>
	 */
	public static double[][] comembership(double[][] matrix){
		
		int n = matrix.length;
		int m = matrix[0].length;
		double[][] result = new double[n][n];
		
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++){
				double inProduct = 0;
				for(int k=0; k<m; k++)
					inProduct += matrix[i][k] * matrix[j][k];
				result[i][j] = inProduct;
			}
		return result;
	}
	
	/**
	 * Affiliation Matrix를 이용하여 overLap을 만든다.<br>
	 */
	public static double[][] overLap(double[][] matrix){
		
		int n = matrix.length;
		int m = matrix[0].length;
		
		double[][] result = new double[m][m];
		
		for(int i=0; i<m; i++)
			for(int j=0; j<m; j++){
				double inProduct = 0;
				for(int k=0; k<n; k++)
					inProduct += matrix[k][i] * matrix[k][j];
				result[i][j] = inProduct;
			}
		return result;
	}
	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (2003-02-20 오전 12:48:15)
	 * @param x double[][]
	 * @param v double
	 */
	public static void fill(double[][] x, double v) {
		
		int i;
		int length;

		length = x.length;
		for (i = 0; i < length; i++) 
			fill(x[i], v);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2003-02-18 오전 10:59:06)
	 * @param a double[]
	 * @param val double
	 */
	public static void fill(double[] a, double val) {

		int i;
		int length;

		length = a.length;
		
		for (i = 0; i < length; i++) {

			a[i] = val;
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2003-02-18 오전 10:59:20)
	 * @param a int[]
	 * @param val int
	 */
	public static void fill(int[] a, int val) {

		int i;
		int length;

		length = a.length;
		
		for (i = 0; i < length; i++) {

			a[i] = val;
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2003-02-18 오전 11:31:12)
	 * @param a boolean[]
	 * @param val boolean
	 */
	public static void fill(boolean[] a, boolean val) {

		int i;
		int length;

		length = a.length;
		
		for (i = 0; i < length; i++) {

			a[i] = val;
		}
	}
	
	public static double upperSum(final double[][] matrix) {
		double ret = 0;
		for (int i=0; i < matrix.length; i++) {
			for (int j=i+1; j < matrix[i].length; j++) {
				ret += matrix[i][j];
			}
		}
		return ret;
	}
	
	public static double lowerSum(final double[][] matrix) {
		double ret = 0;
		for (int i=0; i < matrix.length; i++) {
			for (int j=0; j < i; j++) {
				ret += matrix[i][j];
			}
		}
		return ret;
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
	public static double[][] expand(double[][] matrix, int nIndex, int nCount){
		
		int expandedRow = matrix.length + nCount;
		int expnadedCol = matrix[0].length + nCount;
		double [][] expand = new double[expandedRow][expnadedCol];
		for(int i =0; i< matrix.length; i++){
			for(int j=0; j<matrix[0].length; j++){
				if(matrix[i][j]!=0)
					expand[( i < nIndex ) ? i : i + nCount][( j < nIndex ) ? j : j + nCount] = matrix[i][j];
			}
		}
		return expand;
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
	public static double[][] expandRow(double[][] matrix, int nIndex, int nCount){
		
		int expandedRow = matrix.length + nCount;
		double [][] expand = new double[expandedRow][matrix[0].length];
		for(int i =0; i< matrix.length; i++){
			for(int j=0; j<matrix[0].length; j++){
				if(matrix[i][j]!=0)
					expand[( i < nIndex ) ? i : i + nCount][j] = matrix[i][j];
			}
		}
		
		return expand;
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
	public static double[][] expandColumn(double[][] matrix, int nIndex, int nCount){
		
		int expnadedCol = matrix[0].length + nCount;
		double [][] expand = new double[matrix.length][expnadedCol];
		for(int i =0; i< matrix.length; i++){
			for(int j=0; j<matrix[0].length; j++){
				if(matrix[i][j]!=0)
					expand[i][( j < nIndex ) ? j : j + nCount] = matrix[i][j];
			}
		}
		
		return expand;
	}
	
	public static double[] expandVector(double[] vector, int nIndex, int nCount)
	{
		
		double[] expand = new double[vector.length+nCount]; 
		
		for(int i=0; i< vector.length; i++)
			expand[( i < nIndex ) ? i : i + nCount] = vector[i];
		
		return expand;
	}
	
	/**
	 * nIndex번째 row로 부터 nCount 갯수의 row를 삭제한 행렬을 반환한다.
	 */
	public static double[][] deleteRow(double[][] matrix, int nIndex, int nCount){
		
		double[][] expand; 
		int n = matrix.length;
		int m = matrix[0].length;
		
		expand = new double[n - nCount][m];
		for(int i=0; i<n; i++){
				if (i >= nIndex && i < nIndex + nCount)
					continue;
				else{
					int index = (i<nIndex) ? i : i-nCount;
					expand[index] = matrix[i].clone();
				}
		}
		return expand;
	}
	
	/**
	 * nIndex번째 column으로 부터 nCount 갯수의 column을 삭제한 행렬을 반환한다.
	 */
	public static double[][] deleteColumn(double[][] matrix, int nIndex, int nCount){
		
		double[][] expand; 
		int n = matrix.length;
		int m = matrix[0].length;
		
		expand = new double[n][m - nCount];
		for(int i=0; i<n; i++){
			
			for(int j=0; j<m; j++){
				
				if (j >= nIndex && j < nIndex + nCount)
					continue;
				else
					expand[i][j<nIndex ? j : j-nCount] =  matrix[i][j];
			}
		}
		return expand;
	}
	
	/**
	 * nIndex번째 row와 column으로 부터
	 * 각각 nCount 개의 row와 nCount개의 column을 삭제한 행렬을 반환한다.
	 */
	public static double[][] deleteRowAndColumn(double[][] matrix, int row, int col, int nCount){
		
		int n = matrix.length;
		int m = matrix[0].length;
		
		if (n <= nCount || m <= nCount) {
			return null;
		}
		
		double[][] expand = new double[n-nCount][m-nCount];

		for(int i=0; i<n; i++){
			if (i >= row && i < row + nCount)
				continue;
			int iIndex = (i<row)?i:i-nCount;
			for(int j=0; j<m; j++){
					if (j >= col && j < col + nCount)
						continue;
					expand[iIndex][(j<col)?j:j-nCount] = matrix[i][j];
			}
		}
		return expand;

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
	public static double[][] deleteUpper(final double[][] matrix) {
		// onemode network 가 아니면 원본 matrix 를 반환한다.
		int n = matrix.length;
		int m = matrix[0].length;
		if(n!=m)
			return matrix;
		
		double[][] _toRet;
		
		_toRet = new double[n][m];
		for(int rowIndex=0; rowIndex<n; rowIndex++){
			for(int colIndex=0; colIndex<m; colIndex++){
				if(rowIndex >= colIndex ){
					_toRet[rowIndex][colIndex] = matrix[rowIndex][colIndex];
				}
				
			}
		}
		return _toRet;
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
	public static double[][] deleteLower(final double[][] matrix) {
		// onemode network 가 아니면 원본 matrix 를 반환한다.
		int n = matrix.length;
		int m = matrix[0].length;
		if(n!=m)
			return matrix;
		
		double[][] _toRet;
		
		_toRet = new double[n][m];
		for(int rowIndex=0; rowIndex<n; rowIndex++){
			for(int colIndex=0; colIndex<m; colIndex++){
				if(rowIndex <= colIndex ){
					_toRet[rowIndex][colIndex] = matrix[rowIndex][colIndex];
				}
				
			}
		}
		return _toRet;
	}
	
	public static double[][] copy(double[][] m) {

		int i, length;
		double[][] result;
		

		result = new double[m.length][];
		length = m.length;
		
		for (i = 0; i < length; i++) {

			result[i] = m[i].clone();
		}

		return result;
	}
	
	public static double[] normalize(int[] vector)
	{
		double result[] = new double[vector.length];
		double maxValue = - Double.MAX_VALUE;
		for (int i = 0; i < vector.length; i++) 
			if(vector[i] > maxValue)
				maxValue = vector[i];
		for (int i = 0; i < vector.length; i++) 
			result[i] = vector[i]/maxValue;		                  
		return result;
	}
	
	public static double[] normalize(double[] vector)
	{
		double result[] = new double[vector.length];
		double maxValue = - Double.MAX_VALUE;
		for (int i = 0; i < vector.length; i++) 
			if(vector[i] > maxValue)
				maxValue = vector[i];
		if(maxValue==0)
			return vector.clone();
		for (int i = 0; i < vector.length; i++) 
			result[i] = vector[i]/maxValue;		                  
		return result;
	}
	
}
