package nowhere4d.workspace.layout;

public abstract class Layout
{
	
	// 화면의 왼쪽과 위의 여백
	public static final int LEFT_TOP_MARGIN = 50;
	
	// 목표 영역의 폭, 목표 영역의 높이
	protected double DesiredWidth, DesiredHeight;

	// 좌표
	protected double[][] Points;
	
	// Node State
	public int[] NodeState;

	public static final int STATE_NORMAL	= 0x01;
	public static final int STATE_FIXED		= 0x02;
	public static final int STATE_ISOLATED	= 0x04;

	// Isolate인 점들을 찾는다.
	public int[] setNodeState(CDMatrix matrix, CDVector fixedVector,boolean ignoreIsolated)
	{
		int[] nodeState = new int[matrix.rows()];
		
		for ( int i = 0; i < nodeState.length; i++ )
		{
			if ( fixedVector != null && fixedVector.getQuick(i) == 1.0 )
				nodeState[i] = STATE_FIXED;
			else
				nodeState[i] = STATE_NORMAL;
		}
		
		if ( ignoreIsolated == false )
			return nodeState;
		
		CDMatrix.Iter iter = matrix.getIter(); iter.init();
		CDMatrix.Iter transposeIter = NMMatrixMath.transpose(matrix).getIter(); transposeIter.init();
		
		for ( int i = 0; i < matrix.rows(); i++ )
		{
			if ( iter.getRow(i).hasMoreValue() == false && transposeIter.getRow(i).hasMoreValue() == false )
				nodeState[i] = nodeState[i] | STATE_ISOLATED;
		}
		
		return nodeState;
	}
	
	// Isolate인 점들을 minx 와 miny로 배치한다.
	protected void arrangeIsolated()
	{
		double xmin = Double.MAX_VALUE;
		double ymin = Double.MAX_VALUE;
		
		for ( int i = 0; i < Points[0].length; i++ ) 
		{
			if ( (NodeState[i] & STATE_ISOLATED) != 0 )
				continue;
			
			xmin = Math.min(xmin, Points[0][i]);
			ymin = Math.min(ymin, Points[1][i]);
		}
		
		for ( int i = 0; i < Points[0].length; i++ ) 
		{
			if ( (NodeState[i] & STATE_ISOLATED) == 0 )
				continue;
			
			Points[0][i] = xmin;
			Points[1][i] = ymin;
		}
	}
	
//	public static final double MapRatio = 0.99;
	
	public void resizeToScreen(boolean isIsolate) {
		
		double minx	= Double.MAX_VALUE;
		double maxx	= -Double.MAX_VALUE;
		double miny	= Double.MAX_VALUE;
		double maxy	= -Double.MAX_VALUE;

		final int nPoints = Points[0].length;
		for (int i=0; i<nPoints; i++)
		{
			minx	= Math.min(minx, Points[0][i]);
			maxx	= Math.max(maxx, Points[0][i]);
			miny	= Math.min(miny, Points[1][i]);
			maxy	= Math.max(maxy, Points[1][i]);
		}
		
		// 화면의 중점과 Layout 결과의 중점을 맞춘다.
		for ( int i = 0; i < nPoints; i++ )
		{
			Points[0][i] = Points[0][i] - minx + LEFT_TOP_MARGIN;//((Points[0][i]+tx) - width/2.0)*sx + width/2.0;
			Points[1][i] = Points[1][i] - miny + LEFT_TOP_MARGIN;//((Points[1][i]+ty) - height/2.0)*sy + height/2.0;
		}
		
		if (isIsolate)
			positionIsolated((int)(maxx-minx), (int)(maxy-miny));
	}
	
	public void resizeToScreen(boolean isIsolate, int width, int height, boolean flipVertical) {
		double minx	= Double.MAX_VALUE;
		double maxx	= -Double.MAX_VALUE;
		double miny	= Double.MAX_VALUE;
		double maxy	= -Double.MAX_VALUE;

		final int nPoints = Points[0].length;
		for (int i=0; i<nPoints; i++)
		{
			minx	= Math.min(minx, Points[0][i]);
			maxx	= Math.max(maxx, Points[0][i]);
			miny	= Math.min(miny, Points[1][i]);
			maxy	= Math.max(maxy, Points[1][i]);
		}
		
		double widthRatio = width / (maxx-minx);
		double heightRatio = height / (maxy-miny);
		
		double ratio = Math.min(widthRatio, heightRatio);
		
		// 화면의 중점과 Layout 결과의 중점을 맞춘다.
		for ( int i = 0; i < nPoints; i++ )
		{
			Points[0][i] = (Points[0][i] - minx) * ratio + LEFT_TOP_MARGIN;//((Points[0][i]+tx) - width/2.0)*sx + width/2.0;
			if (flipVertical) {
				Points[1][i] = (maxy - (Points[1][i] - miny)) * ratio + LEFT_TOP_MARGIN;//((Points[1][i]+ty) - height/2.0)*sy + height/2.0;
			}
			else {
				Points[1][i] = (Points[1][i] - miny) * ratio + LEFT_TOP_MARGIN;//((Points[1][i]+ty) - height/2.0)*sy + height/2.0;			
			}
		}
		
		computedMinX = minx;
		computedMinY = miny;
		computedRatio = ratio;
		
		if (isIsolate)
			positionIsolated(width, height);
	}
	
	public void resizeToScreen(boolean isIsolate, int width, int height) {
		
		resizeToScreen(isIsolate, width, height, false);
	}
	
	protected double computedMinX = 0;
	protected double computedMinY = 0;
	protected double computedRatio = 1;
	
	public void resizeIntPointsToScreen(int[][] Points, boolean isIsolate) {
		
		int minx	= Integer.MAX_VALUE;
		int maxx	= Integer.MIN_VALUE;
		int miny	= Integer.MAX_VALUE;
		int maxy	= -Integer.MAX_VALUE;

		final int nPoints = Points[0].length;
		for (int i=0; i<nPoints; i++)
		{
			minx	= Math.min(minx, Points[0][i]);
			maxx	= Math.max(maxx, Points[0][i]);
			miny	= Math.min(miny, Points[1][i]);
			maxy	= Math.max(maxy, Points[1][i]);
		}
		this.Points = new double[2][Points[0].length];
		
		// 화면의 중점과 Layout 결과의 중점을 맞춘다.
		for ( int i = 0; i < nPoints; i++ )
		{
			this.Points[0][i] = Points[0][i] - minx + 50;//((Points[0][i]+tx) - width/2.0)*sx + width/2.0;
			this.Points[1][i] = Points[1][i] - miny + 50;//((Points[1][i]+ty) - height/2.0)*sy + height/2.0;
		}
		
		if (isIsolate)
			positionIsolated((int)(maxx-minx), (int)(maxy-miny));
	}
	
	protected void positionIsolated(int width, int height)
	{
		int nNodeOfOneSide = 10;
		
		double ratio = 1;
		
		double xmin = width*(1-ratio)/4.0;
		double ymin = height*(1-ratio)/4.0;
		double xmax = width-width*(1-ratio)/4.0;
		double ymax = height-height*(1-ratio)/4.0;
		
		xmin = 30;
		ymin = 30;
		xmax = width + 80;
		ymax = height + 80;
		
		int nCount = 0;
		for ( int i = 0; i < NodeState.length; i++ )
		{
			if ( (NodeState[i] & Layout.STATE_ISOLATED) != 0 )
				nCount++;
		}
		nNodeOfOneSide = Math.max(nNodeOfOneSide,(int) Math.ceil( nCount / 4.0 ));

		double xInterval = (xmax-xmin)/nNodeOfOneSide;
		double yInterval = (ymax-ymin)/nNodeOfOneSide;
		
		nCount = 0;
		for ( int i = 0; i < NodeState.length; i++ )
		{
			if ( (NodeState[i] & Layout.STATE_ISOLATED) == 0 )
				continue;
			
			switch (nCount/nNodeOfOneSide) 
			{
			case 0:
				Points[0][i] = xmax;
				Points[1][i] = ymin+yInterval*nCount;
				break;
			case 1:
				Points[0][i] = xmax-xInterval*(nCount-nNodeOfOneSide);
				Points[1][i] = ymax;
				break;
			case 2:
				Points[0][i] = xmin;
				Points[1][i] = ymax-yInterval*(nCount-nNodeOfOneSide*2);
				break;
			case 3:
				Points[0][i] = xmin+xInterval*(nCount-nNodeOfOneSide*3);
				Points[1][i] = ymin;
				break;
			}
			
			nCount++;
		}
		
	}
}
