package nowhere4d.workspace.layout;

import java.io.Serializable;
import java.util.Arrays;

public class CD64BDoubleMap extends CDAbstractMap implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2576846387949620402L;

	//=========================================================================
	// 생성자
	//-------------------------------------------------------------------------
	
	public CD64BDoubleMap()
	{
		this(DEFAULT_CAPACITY);
	}

	public CD64BDoubleMap(int initialCapacity)
	{
		this(initialCapacity, DEFAULT_MIN_LOAD_FACTOR, DEFAULT_MAX_LOAD_FACTOR);
	}

	public CD64BDoubleMap(int initialCapacity, double minLoadFactor, double maxLoadFactor)
	{
		setUp(initialCapacity, minLoadFactor, maxLoadFactor);
	}

	//=========================================================================
	// PUBLIC 메소드
	//-------------------------------------------------------------------------
	
	public double get(long key)
	{
		int index = indexOfKey(key);
	
		// 인덱스가 없다면 0을 반환한다.
		if (index < 0)
			return 0;

		return m_values[index];
	}

	public long[] getKeys()
	{
		long[] keyArray = new long[m_distinct];

		int count = 0;
		
		for (int i = 0; i < m_keys.length; i++)
		{
			// 상태가 FULL 일 경우에만 키를 반환한다.
			if (m_states[i] == STATE_FULL)
				keyArray[count++] = m_keys[i];
		}

		return keyArray;
	}

	public double[] getValues()
	{
		double[] valueArray = new double[m_distinct];

		int count = 0;
		
		for (int i = 0; i < m_values.length; i++)
		{
			// 상태가 FULL 일 경우에만 값을 반환한다.
			if (m_states[i] == STATE_FULL)
				valueArray[count++] = m_values[i];
		}

		return valueArray;
	}
	
	public boolean put(long key, double value)
	{
		// 값을 넣기 위해 키에 맞는 인덱스를 구한다.
		int index = indexOfInsertion(key);
		
		if (index < 0) { //already contained
			index = -index -1;
			//if (debug) if (this.state[i] != FULL) throw new InternalError();
			//if (debug) if (this.table[i] != key) throw new InternalError();
			this.m_values[index]=value;
			return false;
		}
		
		if (m_distinct > m_hiWaterMark)
		{
			// 새로운 용량을 계산한다.
			int newCapacity = chooseGrowCapacity(m_distinct + 1, m_minLoadFactor, m_maxLoadFactor);
			
			// 새로운 용량으로 다시 해쉬 한다.
			rehash(newCapacity);
			
			return put(key, value);
		}

		/*
		// 이미 키가 존재하는 경우에 처리
		if (index < 0)
		{
			// 실제 인덱스 [ -(index+1) ] 에 값을 넣는다.  
			m_values[ -(index+1) ] = value;
			
			// 실제로 키가 추가되지 않았으므로 FALSE를 반환한다. 
			return false;
		}
		*/

		// 해당 인덱스에 키와 값을 설정한다.
		m_keys  [index] = key;
		m_values[index] = value;
		
		// FREE 상태를 채웠다면 총 FREE 상태 개수에서 하나를 뺀다.
		if (m_states[index] == STATE_FREE)
			m_freeEntries--;
		
		// 상태 플래그를 FULL 상태로 전환한다.
		m_states[index] = STATE_FULL;
		
		// 키의 개수를 하나 증가시킨다.
		m_distinct++;

		// FREE 상태가 하나도 없다면 새로운 용량으로 다시 해쉬한다.
		if (m_freeEntries < 1)
		{
			// 새로운 용량을 계산한다.
			int newCapacity = chooseGrowCapacity(m_distinct + 1, m_minLoadFactor, m_maxLoadFactor);
			
			// 새로운 용량으로 다시 해쉬 한다.
			rehash(newCapacity);
		}

		// 키가 하나 추가되었으므로 TRUE를 반환한다.
		return true;
	}

	public boolean removeKey(long key)
	{
		int index = indexOfKey(key);
		
		// 인덱스가 없다면 FALSE를 반환한다.
		if (index < 0)
			return false;

		// 상태 플래그를 REMOVED 상태로 전환한다.
		m_states[index] = STATE_REMOVED;
		
		// 키의 개수를 하나 감소시킨다.
		m_distinct--;

		// 키의 개수가 특정 값보다 작아진다면 다시 해쉬한다.
		if (m_distinct < m_loWaterMark)
		{
			// 새로운 용량을 계산한다.
			int newCapacity = chooseShrinkCapacity(m_distinct, m_minLoadFactor, m_maxLoadFactor);
			
			// 새로운 용량으로 다시 해쉬 한다.
			rehash(newCapacity);
		}

		// 키가 하나 제거되었으므로 TRUE를 반환한다.
		return true;
	}

	@Override
	public CD64BDoubleMap clone()
	{
		CD64BDoubleMap copy = new CD64BDoubleMap();
		
		// 배열을 메모리 복사한다.
		copy.m_keys   = m_keys.clone();
		copy.m_states = m_states.clone();
		copy.m_values = m_values.clone();
		copy.m_freeEntries = m_freeEntries;
		
		// 기타 맴버 변수의 값을 복사한다.
		copy.m_distinct 	 = m_distinct;
		copy.m_loWaterMark   = m_loWaterMark;
		copy.m_hiWaterMark   = m_hiWaterMark;
		copy.m_minLoadFactor = m_minLoadFactor;
		copy.m_maxLoadFactor = m_maxLoadFactor;

		return copy;
	}

	@Override
	public void clear()
	{
		// 독립적인 키의 개수는 0이 된다.
		m_distinct = 0;
	
		// 모든 엔트리를 FREE 상태로 만든다.
		Arrays.fill(m_states, STATE_FREE);

		// FREE 상태의 개수는 할당된 상태의 개수이다. 
		m_freeEntries = m_states.length;
	
		// 새로운 용량을 계산한다.
		int newCapacity = nextPrime((int)(1 + 1.2 * m_distinct));
		
		// 이미 할당된 상태의 개수와 새로 계산한 용량을 비교하여 다시 해쉬한다.
		if (m_freeEntries > newCapacity)
		{
			rehash(newCapacity);
		}
	}
	
	//=========================================================================
	// PROTECTED 메소드
	//-------------------------------------------------------------------------
	@Override
	protected void setUp(int initialCapacity, double minLoadFactor, double maxLoadFactor)
	{
		super.setUp(initialCapacity, minLoadFactor, maxLoadFactor);
	
		// 소수 값을 이용하여 적당한 용량을 계산한다. (용량은 적어도 1보다 크거나 같아야한다.)
		int capacity = Math.max(nextPrime(initialCapacity), 1);
		
		// 배열을 생성한다.
		m_keys 	 = new long  [capacity];
		m_states = new byte  [capacity];
		m_values = new double[capacity];

		// 최소 로드 팩터를 설정한다.
		m_minLoadFactor = minLoadFactor;
		
		// 용량이 구할 수 있는 가장 큰 소수일 경우에 최대 로드 팩터는 1.0으로 설정한다.
		if (capacity == CDPrimeFinder.largestPrime)
			m_maxLoadFactor = 1.0;
		else
			m_maxLoadFactor = maxLoadFactor;

		// 키가 하나도 없으므로 키의 개수를 0으로 설정한다. 
		m_distinct = 0;
		
		// 사용한 용량이 하나도 없으므로 FREE 상태 개수를 용량으로 설정한다.
		m_freeEntries = capacity;

		// 워터마크의 수위를 설정한다.
		m_loWaterMark = 0;
		m_hiWaterMark = chooseHiWaterMark(capacity, m_maxLoadFactor);
	}

	protected int indexOfKey(long key)
	{
		
		final int length = m_keys.length;
		
		final long hash = key & 0x7FFFFFFFFFFFFFFFL;
		int index = (int)(hash % length);
		int decrement = (int)(hash % (length - 2));

		// 감소 상수가 0일 경우는 1로 설정한다.
		if (decrement == 0)
			decrement = 1;

		// FREE 상태인 슬롯을 찾거나 (REMOVED 상태가 아닌 슬롯에서) 해당 키를 찾게 되면 루프를 빠져나온다.
		while (m_states[index] != STATE_FREE && (m_states[index] == STATE_REMOVED || m_keys[index] != key))
		{
			index -= decrement;
			
			if (index < 0)
				index += length;
		}
		
		// 키를 찾지 못했을 경우에는 -1을 반환한다.
		if (m_states[index] == STATE_FREE) {
			return -1;
		}
		
		// 키를 담고 있는 인덱스를 반환한다.
		return index;
	}

	protected int indexOfInsertion(long key)
	{
		final int length = m_keys.length;
		
		final long hash = key & 0x7FFFFFFFFFFFFFFFL;;
		int index = (int)(hash % length);
		int decrement = (int)(hash % (length - 2));
		
		// 감소 상수가 0일 경우는 1로 설정한다.
		if (decrement == 0)
			decrement = 1;

		// REMOVED 또는 FREE 상태를 찾거나 해당 키를 찾게되면 루프를 빠져나온다.
		while (m_states[index] == STATE_FULL && m_keys[index] != key)
		{
			index -= decrement;
			
			if (index < 0)
				index += length;
		}
		
		// 해당 인덱스의 상태가 REMOVED 이라면 처리한다. 
		if (m_states[index] == STATE_REMOVED)
		{
			int oldIndex = index;
			
			// FREE 상태인 슬롯을 찾거나 (REMOVED 상태가 아닌 슬롯에서) 해당 키를 찾게 되면 루프를 빠져나온다.
			while (m_states[index] != STATE_FREE && (m_states[index] == STATE_REMOVED || m_keys[index] != key))
			{
				index -= decrement;
				
				if (index < 0)
					index += length;
			}
			
			if (m_states[index] == STATE_FREE)
				index = oldIndex;
		}

		// 슬롯에 데이터가 이미 존재한다면 가상 인덱스 [ -(index+1) ]를 반환한다.
		if (m_states[index] == STATE_FULL) {
			return -(index+1);
		}
		
		
		// 새로운 키가 담길 인덱스를 반환한다.
		return index;
	}

	protected void rehash(int newCapacity)
	{
		// 새로운 용량이 키의 개수보다 작거나 같다면 에러를 내고 종료한다.
		if (newCapacity <= m_distinct)
			throw new InternalError();

		// 이전 정보를 백업해둔다.
		final long[] oldKeys = m_keys;
		final byte[] oldStates = m_states;
		final double[] oldValues = m_values;

		// 새로운 정보를 생성한다.
		long[] newKeys = new long[newCapacity];
		byte[] newStates = new byte[newCapacity];
		double[] newValues = new double[newCapacity];

		// 새로운 용량에 적당한 워터마크를 설정한다.
		m_loWaterMark = chooseLoWaterMark(newCapacity, m_minLoadFactor);	
		m_hiWaterMark = chooseHiWaterMark(newCapacity, m_maxLoadFactor);

		// 사용할 데이터를 새로운 정보로 대체한다.
		m_keys = newKeys;
		m_states = newStates;
		m_values = newValues;
		m_freeEntries = newCapacity - m_distinct;

		// 이전 정보를 새로운 정보에 덮어쓴다. 
		for (int index = oldKeys.length; index-- > 0;)
		{
			// FULL 상태만 처리한다.
			if (oldStates[index] == STATE_FULL)
			{
				// 이전 값을 가져온다.
				final long oldKey = oldKeys[index];
				final double oldValue = oldValues[index];
				
				// 추가를 위한 새로운 인덱스를 구한다.
				int newIndex = indexOfInsertion(oldKey);
				
				// 새로운 값을 집어 넣는다.
				newKeys  [newIndex] = oldKey;
				newValues[newIndex] = oldValue;
				newStates[newIndex] = STATE_FULL;
			}
		}
	}
	
	//=========================================================================
	// 맴버 변수
	//-------------------------------------------------------------------------

	// 해시 테이블의 키를 가지는 배열
	protected long[] m_keys = null;
	
	// 해시 테이블의 값을 가지는 배열
	protected double[] m_values = null;
	
	// 해시 테이블의 상태 값을 가지는 배열 (FREE, FULL, REMOVED 의 세가지 상태)
	protected byte[] m_states = null;
	
	// FREE 상태인 엔트리의 개수
	protected int m_freeEntries = 0;
}
