package nowhere4d.workspace.layout;

/****************************************************************************************
* 파일명			: Distance.java <br>
* 만든날짜		: 2006.01.09 <br>
* 작성자	 		: 고승필<br>
* 클래스 설명		: Sparse Matrix(Colt Library)를 이용하여 Distance를 계산하는 알고리즘 <br>
****************************************************************************************/

/****************************************************************************************
* Test Result
* makeData: Time=0.047 secs
* sparse: Time=0.031 secs
* 5.0 1.0 4.0 3.0 2.0 3.0 4.0 2.0 6.0 5.0 7.0
* 5.0 2.0 4.0 3.0 4.0 2.0 3.0 1.0 5.0 4.0 6.0
* 5.0 4.0 5.0 3.0 2.0 7.0 8.0 6.0 10.0 9.0 11.0
* 2.0 5.0 6.0 1.0 3.0 8.0 9.0 7.0 11.0 10.0 12.0 
* 7.0 2.0 3.0 6.0 5.0 5.0 6.0 4.0 8.0 7.0 9.0
* 5.0 1.0 2.0 2.0 3.0 3.0 5.0 3.0 7.0 6.0 8.0
* 8.0 3.0 4.0 6.0 6.0 5.0 4.0 3.0 2.0 1.0 3.0
* 6.0 1.0 2.0 5.0 4.0 3.0 3.0 2.0 4.0 3.0 5.0
* 6.0 1.0 2.0 4.0 4.0 3.0 2.0 5.0 3.0 6.0 8.0
* 8.0 3.0 4.0 6.0 6.0 5.0 4.0 1.0 3.0 2.0 2.0
* 7.0 2.0 3.0 6.0 5.0 4.0 4.0 3.0 1.0 3.0 2.0
* 8.0 3.0 4.0 7.0 6.0 5.0 5.0 1.0 2.0 3.0 2.0 1.0 2.0
* 9.0 4.0 5.0 7.0 7.0 6.0 5.0 1.0 3.0 3.0 2.0 2.0 2.0
* 8.0 3.0 4.0 7.0 6.0 1.0 6.0 3.0 4.0 5.0 4.0 3.0 2.0 4.0
* 8.0 3.0 4.0 6.0 6.0 3.0 4.0 5.0 3.0 2.0 6.0 5.0 4.0 6.0 2.0 1.0 1.0
* 7.0 2.0 3.0 5.0 5.0 2.0 3.0 4.0 4.0 1.0 5.0 4.0 3.0 5.0 1.0 2.0 3.0
* 8.0 3.0 4.0 6.0 6.0 3.0 4.0 5.0 5.0 2.0 6.0 5.0 4.0 6.0 2.0 3.0 1.0 
****************************************************************************************/

public class DistanceDense
{
	public DistanceDense()
	{
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	// Calculate Method
	/////////////////////////////////////////////////////////////////////////////////////////
	
	@SuppressWarnings("unused")
	public double[][] Compute(CDMatrix sdm2dMatrix)
	{
		double[][] matrix = sdm2dMatrix.toArray();
		int nPoint	= matrix.length;
		
		double[][] distanceMatrix	= new double[nPoint][nPoint];
		piMatrix = new double[nPoint][nPoint];

	    // initialize distance matrix.
    	for (int i=0; i<nPoint; i++)
		{
			for (int j=0; j<nPoint; j++)
	   		{
				if (i != j && matrix[i][j] == 0) distanceMatrix[i][j] = Double.POSITIVE_INFINITY;
				else distanceMatrix[i][j] = matrix[i][j];
			}
		}
		
		// initialize pi matrix.
    	for (int i=0; i<nPoint; i++)
		{
			for(int j=0; j<nPoint; j++)
	   		{
				if (i == j || distanceMatrix[i][j] == Double.POSITIVE_INFINITY) piMatrix[i][j] = EOP;
				else piMatrix[i][j] = i+1;
			}
		}

    	float __prog__step = 1 / (float)nPoint;
    	float __prog__now = 0;
    	for (int k=0; k<nPoint; k++)
    	{
    		__prog__now += __prog__step;
			for(int i=0; i<nPoint; i++)
			{
				for(int j=0; j<nPoint; j++)
		    	{
					if (distanceMatrix[i][k]==Double.POSITIVE_INFINITY||distanceMatrix[k][j]==Double.POSITIVE_INFINITY) continue;
			    	if (distanceMatrix[i][j] > distanceMatrix[i][k]+distanceMatrix[k][j])
			    	{
				    	piMatrix[i][j] = piMatrix[k][j];
					 	distanceMatrix[i][j] = distanceMatrix[i][k]+distanceMatrix[k][j];
					}
				}
			}
    	}

    	// 도달 불가능한 노드는 0.0으로 세팅을 한다.
    	for (int i=0; i<nPoint; i++)
		{
			for(int j=0; j<nPoint; j++)
	   		{
				if (distanceMatrix[i][j] == Double.POSITIVE_INFINITY)
					distanceMatrix[i][j] = 0.0;
			}
		}
		return distanceMatrix;
	}
	/////////////////////////////////////////////////////////////////////////////////////////
	// Static Method
	/////////////////////////////////////////////////////////////////////////////////////////
	/**
	* reachability matrix 를 계산한다.
	* @param distanceMatrix distance matrix
	*/
	public static int[][] computeReachability(double[][] distanceMatrix)
	{
		return CAMatrixMathDense.dichotomize(distanceMatrix, 0, NMMatrixMath.DICHOTOMIZE_GT);
	}
	
	/** 
	* compute Reachability Matrix
	* @param distanceMatrix distance matrix
	* @param INFINITY_VALUE infinity value
	* lee woo shick 2003-08-25
	*/
	public static int[][] computeReachability(double[][] distanceMatrix, double INFINITY_VALUE)
	{
		int n = distanceMatrix.length;
		int[][] reachabilityMatrix = new int[n][n];
		for(int i=0; i<n; i++)
		{
			for(int j=0; j<n; j++)
			{
				if (i!=j)
				{
					if(distanceMatrix[i][j] == INFINITY_VALUE || distanceMatrix[i][j] == 0) 
						reachabilityMatrix[i][j] = 0;
					else
						reachabilityMatrix[i][j] = 1;	
				}
				else
					reachabilityMatrix[i][j] = 0;
			}
		}
		return reachabilityMatrix;
	}

	public static double ComputeDiameter(double[][] matrix)
	{
		double diameter	= 0.0;
		for ( int i = 0; i < matrix.length; i++ )
			for ( int j = 0; j < matrix[i].length; j++ )
				if ( matrix[i][j] != Double.POSITIVE_INFINITY )
					diameter = Math.max(diameter, matrix[i][j]);
		return diameter;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Getter
	/////////////////////////////////////////////////////////////////////////////////////////
	public double[][] GetPiMatrix() { return this.piMatrix; }
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Variables
	/////////////////////////////////////////////////////////////////////////////////////////
	
	// Distance.compute(...) 가 끝난 상태에서 pi matrix 를 저장하고 있는 변수
	private double[][] piMatrix;
	
	// end of path
	private static final int EOP = -1;
	
}
