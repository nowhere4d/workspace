package nowhere4d.workspace.layout;

import java.io.Serializable;

public abstract class CDAbstractMap implements CDAbstractMapConstant, Serializable
{
	//=========================================================================
	// 생성자
	//-------------------------------------------------------------------------
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2506940311066852284L;

	protected CDAbstractMap()
	{
		// 아무것도 처리하지 않음
	}

	//=========================================================================
	// PUBLIC 메소드
	//-------------------------------------------------------------------------
	
	public boolean isEmpty()
	{
		return m_distinct == 0;
	}

	public int getSize()
	{
		return m_distinct;
	}

	//=========================================================================
	// PUBLIC ABSTRACT 메소드
	//-------------------------------------------------------------------------
	
	public abstract void clear();

	//=========================================================================
	// PROTECTED 메소드
	//-------------------------------------------------------------------------
	
	protected int chooseGrowCapacity(int size, double minLoad, double maxLoad)
	{
		return nextPrime(Math.max(size + 1, (int) ((4 * size / (3 * minLoad + maxLoad)))));
	}

	protected int chooseShrinkCapacity(int size, double minLoad, double maxLoad)
	{
		return nextPrime(Math.max(size + 1, (int) ((4 * size / (minLoad + 3 * maxLoad)))));
	}

	protected int chooseHiWaterMark(int capacity, double maxLoad)
	{
		return Math.min(capacity - 2, (int) (capacity * maxLoad));
	}

	protected int chooseLoWaterMark(int capacity, double minLoad)
	{
		return (int) (capacity * minLoad);
	}

	protected void setUp(int initialCapacity, double minLoadFactor, double maxLoadFactor)
	{
		if (initialCapacity < 0)
			throw new IllegalArgumentException
				("Initial Capacity must not be less than zero: " + initialCapacity);
		
		if (minLoadFactor < 0.0 || minLoadFactor >= 1.0)
			throw new IllegalArgumentException
				("Illegal minLoadFactor: " + minLoadFactor);
		
		if (maxLoadFactor <= 0.0 || maxLoadFactor >= 1.0)
			throw new IllegalArgumentException
				("Illegal maxLoadFactor: " + maxLoadFactor);
		
		if (minLoadFactor >= maxLoadFactor)
			throw new IllegalArgumentException
				("Illegal minLoadFactor: " + minLoadFactor + " and maxLoadFactor: " + maxLoadFactor);
	}

	protected int nextPrime(int desiredCapacity)
	{
		return CDPrimeFinder.nextPrime(desiredCapacity);
	}

	//=========================================================================
	// 맴버 변수
	//-------------------------------------------------------------------------
	
	// 중복되지 않는 키의 개수
	protected int m_distinct;
	
	// 낮은 워터마크 (수위선)
	protected int m_loWaterMark;
	
	// 높은 워터마크 (수위선)
	protected int m_hiWaterMark;
	
	// 최소 로드 팩터
	protected double m_minLoadFactor;
	
	// 최대 로드 팩터
	protected double m_maxLoadFactor;
}
