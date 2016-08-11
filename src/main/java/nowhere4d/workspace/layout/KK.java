package nowhere4d.workspace.layout;

/****************************************************************************************
* 클래스 설명		: Sparse Matrix(Colt Library)를 이용한 Kamada and Kawai's Spring Embedding 알고리즘 <br>
****************************************************************************************/

public class KK extends Layout
{
	private double[][] m_distance;
	
	public double[][] Compute(CDMatrix matrix, double DesiredWidth, double DesiredHeight)
	{
		return Compute(matrix, null, null, DesiredWidth, DesiredHeight, DefaultC, DefaultK, DefaultEPS, DefaultMaxIteration, DefaultBetweenComponent, false);
	}
	public double[][] Compute(CDMatrix matrix, int nPoint, double DesiredWidth, double DesiredHeight, int MaxIteration)
	{
		return Compute(matrix, null, null, DesiredWidth, DesiredHeight, DefaultC, DefaultK, DefaultEPS, MaxIteration, DefaultBetweenComponent, true);
	}
	public double[][] Compute(CDMatrix matrix, double[][] points, double DesiredWidth, double DesiredHeight)
	{
		return Compute(matrix, points, null, DesiredWidth, DesiredHeight, DefaultC, DefaultK, DefaultEPS, DefaultMaxIteration, DefaultBetweenComponent, true);
	}
	public double[][] Compute(CDMatrix matrix, double[][] points, double DesiredWidth, double DesiredHeight, int MaxIteration)
	{
		return Compute(matrix, points, null, DesiredWidth, DesiredHeight, DefaultC, DefaultK, DefaultEPS, MaxIteration, DefaultBetweenComponent, true);
	}
	
	
	// Run
	public double[][] Compute(double[][] distanceMatrix, CDMatrix matrix, 
			double[][] Points, CDVector fixed_node_vector, double DesiredWidth, double DesiredHeight,
			double C, double K, double EPS, int MaxIteration, int BetweenComponent, boolean ignoreIsolated)
	{
		this.DesiredWidth	= DesiredWidth;
		this.DesiredHeight = DesiredHeight;
		
		this.C = C;
		this.K = K;
		this.EPS = EPS;
		this.MaxIteration	= MaxIteration;
		this.MaxPointIteration = DefaultMaxPointIteration;
		this.BetweenComponent = BetweenComponent;
		this.IgnoreIsolated = ignoreIsolated;
		
		final int nPoint = matrix.rows();
		
		if ( Points == null )
		{
			this.Points = new double[2][nPoint];
			for (int i=0; i<nPoint; i++)
			{
				this.Points[0][i]	= DesiredWidth*Math.random();
				this.Points[1][i]	= DesiredHeight*Math.random();
			}
		}
		else
		{
			// 원본을 훼손시키지 않기 위해서 복사를 해준다.
			this.Points = new double[2][nPoint];
			System.arraycopy(Points[0],0,this.Points[0],0,nPoint);
			System.arraycopy(Points[1],0,this.Points[1],0,nPoint);
		}
		
		
		System.out.println("기존 이용  디스턴스 계산 시작"); //$NON-NLS-1$
		// 계산 전처리 과정
		long nowTime = System.currentTimeMillis();
		this.Prepare(distanceMatrix,nPoint);
		
		System.out.println("기존 이용 디스턴스 계산 끝 : " + (System.currentTimeMillis() - nowTime)); //$NON-NLS-1$
		
		mainProcess(matrix, fixed_node_vector);
		return this.Points;
		
	}
	// Run
	public double[][] Compute(CDMatrix matrix, 
			double[][] Points, CDVector fixed_node_vector, double DesiredWidth, double DesiredHeight,
			double C, double K, double EPS, int MaxIteration, int BetweenComponent, boolean ignoreIsolated)
	{
		this.DesiredWidth	= DesiredWidth;
		this.DesiredHeight = DesiredHeight;
		
		this.C = C;
		this.K = K;
		this.EPS = EPS;
		this.MaxIteration	= MaxIteration;
		this.MaxPointIteration = DefaultMaxPointIteration;
		this.BetweenComponent = BetweenComponent;
		this.IgnoreIsolated = ignoreIsolated;
		
		final int nPoint = matrix.rows();
		
		if ( Points == null )
		{
			this.Points = new double[2][nPoint];
			for (int i=0; i<nPoint; i++)
			{
				this.Points[0][i]	= DesiredWidth*Math.random();
				this.Points[1][i]	= DesiredHeight*Math.random();
			}
		}
		else
		{
			// 원본을 훼손시키지 않기 위해서 복사를 해준다.
			this.Points = new double[2][nPoint];
			System.arraycopy(Points[0],0,this.Points[0],0,nPoint);
			System.arraycopy(Points[1],0,this.Points[1],0,nPoint);
		}
		
		
		System.out.println("디스턴스 계산 시작"); //$NON-NLS-1$
		// 계산 전처리 과정
		long nowTime = System.currentTimeMillis();
		this.Prepare(matrix,nPoint);
		
		System.out.println("디스턴스 계산 끝 : " + (System.currentTimeMillis() - nowTime)); //$NON-NLS-1$
		
		mainProcess(matrix, fixed_node_vector);
		return this.Points;
	}
	
	private void mainProcess(CDMatrix matrix, CDVector fixed_node_vector) {
		NodeState = setNodeState(matrix,fixed_node_vector,IgnoreIsolated);
		
		System.out.println("KK 계산 시작"); //$NON-NLS-1$
		long nowTime = System.currentTimeMillis();
		
		final int nPoint = matrix.rows();
		
		// 실제적인 계산 과정
		this.Compute(matrix,nPoint);
		System.out.println("KK 계산 끝 : " + (System.currentTimeMillis() - nowTime)); //$NON-NLS-1$
		
		
		if ( this.IgnoreIsolated == true ) {
			arrangeIsolated();
		}
		resizeToScreen(IgnoreIsolated, (int)DesiredWidth, (int)DesiredHeight);
		
	}
	
	@SuppressWarnings("unused")
	private void Compute(CDMatrix matrix,	int nPoint)
	{
		double Dm, PDm, D, Dy, Dx;
		int DmIndex, i;
		EnergyX = new double[nPoint];
		EnergyY = new double[nPoint];
		
		// 미리 에너지를 계산해 놓자.
		for (i=0; i<nPoint; i++)
			computeSxSy(i, nPoint);
		
		float __step__prog = 1 / (float)MaxIteration;
		float __totl__prog = 0;
		
		for (int t=0; t<MaxIteration; t++)
		{
			__totl__prog += __step__prog;
//			proc.setProgress(__totl__prog);
			
			// 에너지가 가장 큰 노드를 찾는다.
			Dm = 0.0;
			DmIndex = -1;
			
			for (i=0; i<nPoint; i++)
			{
				if ( (NodeState[i] & STATE_FIXED) != 0 )
					continue;
				if ( IgnoreIsolated == true && (NodeState[i] & STATE_ISOLATED) != 0 )
					continue;
				
				D = Math.sqrt(EnergyX[i]*EnergyX[i]+EnergyY[i]*EnergyY[i]);
				if (Dm<D)
				{
					Dm = D;
					DmIndex = i;
				}
			}
			if (Dm<=EPS) {
				break;
			}

			PDm = Dm;
			
			// 모든 노드와 DmIndex 번째의 노드와의 에너지를 뺀다.
			computeSdxSdy(DmIndex, nPoint, false);
			for (int Pt=0; Pt<MaxPointIteration; Pt++)
			{
				if (PDm<=EPS) break;
				computeSxxSxySyy(DmIndex, nPoint);
				Dy = (EnergyY[DmIndex]*sxx-EnergyX[DmIndex]*sxy)/(sxy*sxy-sxx*syy);
				Dx = (-EnergyX[DmIndex]-sxy*Dy)/sxx;
				Points[0][DmIndex] += Dx;	// y 좌표 보정
				Points[1][DmIndex] += Dy;	// x 좌표 보정
				// 에너지 재 계산
				computeSxSy(DmIndex, nPoint);
				PDm = Math.sqrt(EnergyX[DmIndex]*EnergyX[DmIndex]+EnergyY[DmIndex]*EnergyY[DmIndex]);
			}
			// 모든 노드와 DmIndex 번째의 노드와의 에너지를 더한다.
			computeSdxSdy(DmIndex, nPoint, true);
		}
	}
	
	public double[][] getDistanceMatrix() {
		return m_distance;
	}
	
	// KK 알고리즘에서 사용하는 변수들 생성
	private void Prepare(CDMatrix matrix,int nPoint)
	{
		// 두 개의 노드간의 거리를 계산한다. 노드가 도달이 불가능할 경우에는 Return 값이 0.0 이 된다.
		m_distance = new DistanceDense().Compute(matrix);
		
		// 노드들이 그래프에 그려졌을 경우에, 대략적인 지름을 계산해 본다.
		double Diameter = DistanceDense.ComputeDiameter(m_distance);
		
		// 목표 영역의 크기를 정한다.
		double areaSize = DesiredWidth*DesiredHeight;
		// 길의 상수를 임의로 정한다.
		double lengthConstant = C * Math.sqrt(areaSize/4.0/Diameter);	

		LengthMatrix = new double[nPoint][nPoint];
		KMatrix = new double[nPoint][nPoint];;

		MaxDesiredLength = lengthConstant*(Diameter+1);
		MinSpringConstant = K/((Diameter+1)*(Diameter+1));
		
		for(int i=0; i < m_distance.length; i++) {
			for(int j=0; j < m_distance.length; j++) {
				if ( m_distance[i][j] == 0 )
				{
					LengthMatrix[i][j] = MaxDesiredLength;
					KMatrix[i][j] = MinSpringConstant;
				}
				else
				{
					LengthMatrix[i][j] = lengthConstant * m_distance[i][j];
					KMatrix[i][j] =K / (m_distance[i][j] * m_distance[i][j]);
				}
			}
		}
		
		Diameter = Diameter * Math.max(1,BetweenComponent);
		
	}
	
	// 기존에 존재하는 distance matrix를 이용해 기본 정보 생성
	private void Prepare(double[][] distanceMatrix, int nPoint)
	{
		// 두 개의 노드간의 거리를 계산한다. 노드가 도달이 불가능할 경우에는 Return 값이 0.0 이 된다.
		m_distance = distanceMatrix;
		
		// 노드들이 그래프에 그려졌을 경우에, 대략적인 지름을 계산해 본다.
		double Diameter = DistanceDense.ComputeDiameter(m_distance);
		
		// 목표 영역의 크기를 정한다.
		double areaSize = DesiredWidth*DesiredHeight;
		// 길의 상수를 임의로 정한다.
		double lengthConstant = C * Math.sqrt(areaSize/4.0/Diameter);	

		LengthMatrix = new double[nPoint][nPoint];
		KMatrix = new double[nPoint][nPoint];;

		MaxDesiredLength = lengthConstant*(Diameter+1);
		MinSpringConstant = K/((Diameter+1)*(Diameter+1));
		
		for(int i=0; i < m_distance.length; i++) {
			for(int j=0; j < m_distance.length; j++) {
//				NMRuntime.get().setProgress((i+1)/(double)distance.length);
				if ( m_distance[i][j] == 0 )
				{
					LengthMatrix[i][j] = MaxDesiredLength;
					KMatrix[i][j] = MinSpringConstant;
				}
				else
				{
					LengthMatrix[i][j] = lengthConstant * m_distance[i][j];
					KMatrix[i][j] =K / (m_distance[i][j] * m_distance[i][j]);
				}
			}
		}
		
		Diameter = Diameter * Math.max(1,BetweenComponent);
		
	}
	
	private void computeSdxSdy(int j, int nPoint, boolean plus)
	{
		double sdx, sdy;
		for ( int i = 0; i < nPoint; i++ )
		{
			if ( IgnoreIsolated == true && (NodeState[i] & STATE_ISOLATED) != 0 )
				continue;
			
			if ( j != i )
			{
				double dx = Points[0][i]-Points[0][j];
				double dy = Points[1][i]-Points[1][j];
				double dd = Math.sqrt(dx*dx+dy*dy);
				
				double kValue = KMatrix[i][j];				                           
				if ( kValue == 0.0 )
				{
					sdx = MinSpringConstant*(dx-MaxDesiredLength*dx/dd);
					sdy = MinSpringConstant*(dy-MaxDesiredLength*dy/dd);
				}
				else
				{
					double lValue = LengthMatrix[i][j];
					sdx = kValue*(dx-lValue*dx/dd);
					sdy = kValue*(dy-lValue*dy/dd);
				}
				
				if ( plus )
				{
					EnergyX[i] += sdx;
					EnergyY[i] += sdy;	
				}
				else
				{
					EnergyX[i] -= sdx;
					EnergyY[i] -= sdy;	
				}
			}
		}
	}
	
	// 이중 계산을 막기 위해 global 로 정의
	// 성능 문제로 동일한 일을 하는 2개의 함수를 따로 제작하였다.
	private void computeSxSy(int i, int nPoint)
	{
		EnergyX[i] = EnergyY[i] = 0.0;

		for ( int j = 0; j < nPoint; j++ )
		{
			if ( IgnoreIsolated == true && (NodeState[j] & STATE_ISOLATED) != 0 )
				continue;
			
			if ( j !=i )
			{
				double dx = Points[0][i]-Points[0][j];
				double dy = Points[1][i]-Points[1][j];
				double dd = Math.sqrt(dx*dx+dy*dy);
				
				double kValue = KMatrix[i][j];
				if ( kValue == 0.0 )
				{
					EnergyX[i] += MinSpringConstant*(dx-MaxDesiredLength*dx/dd);
					EnergyY[i] += MinSpringConstant*(dy-MaxDesiredLength*dy/dd);
				}
				else
				{
					double lValue = LengthMatrix[i][j];
					EnergyX[i] += kValue*(dx-lValue*dx/dd);
					EnergyY[i] += kValue*(dy-lValue*dy/dd);
				}
			}
		}
	}
	
	private void computeSxxSxySyy(int i, int nPoint)
	{
		sxx = sxy = syy = 0.0;
		for ( int j = 0; j < nPoint; j++ )
		{
			if ( IgnoreIsolated == true && (NodeState[j] & STATE_ISOLATED) != 0 )
				continue;
			
			if ( i!=j )
			{
				double dx = Points[0][i]-Points[0][j];
				double dy = Points[1][i]-Points[1][j];
				double dd = Math.sqrt(dx*dx+dy*dy);
				double d3 = dd*dd*dd;
				
				double kValue = KMatrix[i][j];

				if ( kValue == 0.0 )
				{
					sxx += (MinSpringConstant*(1.0-MaxDesiredLength*dy*dy/(d3)));
					sxy += (MinSpringConstant*(MaxDesiredLength*dx*dy/(d3)));
					syy += (MinSpringConstant*(1.0-MaxDesiredLength*dx*dx/(d3)));
				}
				else
				{
					double lValue = LengthMatrix[i][j];
					sxx += (kValue*(1.0-lValue*dy*dy/(d3)));
					sxy += (kValue*(lValue*dx*dy/(d3)));
					syy += (kValue*(1.0-lValue*dx*dx/(d3)));				
				}
			}
		}
	}
	
	private double EnergyX[], EnergyY[];
	private double sxx, sxy, syy;	// 이중 계산을 막기 위해 global 로 정의
	// 알고리즘 옵션 변수들
	protected double C;
	protected double K;
	protected double EPS;
	protected int MaxIteration;
	protected int MaxPointIteration;
	protected int BetweenComponent;
	protected boolean IgnoreIsolated;
	
	// desired length
	protected double[][] LengthMatrix;
	protected double MaxDesiredLength;
	protected double lengthValue;
	// spring constant
	protected double[][] KMatrix;
	protected double MinSpringConstant;
	protected double springValue;
	
	// Natural Length Coefficient
	public static final double DefaultC = 1.0;
	// Stiffness Coefficient
	public static final double DefaultK = 1.0;
	// EPS
	public static final double DefaultEPS = 10e-5;
	// Max # Iterations
	public static final int DefaultMaxIteration = 500;
	// 점 하나당 반복횟수
	public static final int DefaultMaxPointIteration = 10;
	// Component 간 거리
	public static final int DefaultBetweenComponent = 2;
}