package nowhere4d.workspace.layout;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CDVectorSparse extends CDVector 
{

	private static final long serialVersionUID = 454168863395625738L;
	
	//=========================================================================
	// 생성자
	//-------------------------------------------------------------------------
	protected CDVectorSparse(int size)
	{
		// 초기 용량을 백터 크기의 1000 분의 1로 한다.
		this(size, size / 1000, DEFAULT_MIN_LOAD_FACTOR, DEFAULT_MAX_LOAD_FACTOR);
	}

	protected CDVectorSparse(int size, int initialCapacity, double minLoadFactor, double maxLoadFactor)
	{
		// 백터의 크기는 음수일 수 없다.
		if (size < 0)
			throw new IllegalArgumentException("Negative size");

		// 백터의 크기를 설정한다.
		m_size = size;

		// 내부적으로 사용할 맵을 생성한다.
		m_map = new CD32BDoubleMap(initialCapacity, minLoadFactor, maxLoadFactor);

		// 키를 새로 생성하였으므로 키 변경 플래그를 켠다.
		m_keyChanged = true;
	}

	protected CDVectorSparse(double[] values)
	{
		this(values.length);
		
		// 배열로 이루어진 백터의 값을 대입한다.
		assign(values);
	}

	//=========================================================================
	// PUBLIC 메소드
	//-------------------------------------------------------------------------

	@Override
	public boolean isDense(){
		return false;
	}
	
	/**************************************************************************
	 * 입력 벡터와 동일하지만, CDVectorDense로 구현된 행렬를 반환한다.
	 * 이미 CDVectorDense로 구현되어 있으면 자기자신을 반환
	 ***************************************************************************/
	@Override
	public CDVector toDense(){
		return new CDVectorDense(toArray());
	}
	
	/**************************************************************************
	 * 입력 벡터와 동일하지만, CDVectorSparse로 구현된 행렬를 반환한다.
	 * 이미 CDVectorSparse로 구현되어 있으면 자기자신을 반환
	 ***************************************************************************/
	@Override
	public CDVector toSparse(){
		return this;
		
	}
	
	/**************************************************************************
	 * 백터의 특정 인덱스 값을 반환한다. 바운드 처리를 해준다.
	 **************************************************************************/
	@Override
	public double get(int index)
	{
		if (index < 0 || index >= m_size)
			throw new IndexOutOfBoundsException("index:" + index);

		return getQuick(index);
	}

	/**************************************************************************
	 * 백터의 특정 인덱스 값을 반환한다.
	 **************************************************************************/
	@Override
	public double getQuick(int index)
	{
		return m_map.get(index);
	}
	
	/**************************************************************************
	 * 백터의 특정 인덱스에 값을 대입한다. 바운드 처리를 해준다.
	 **************************************************************************/
	@Override
	public void set(int index, double value)
	{
		if (index < 0 || index >= m_size)
			throw new IndexOutOfBoundsException("index:" + index);

		setQuick(index, value);
	}

	/**************************************************************************
	 * 백터의 특정 인덱스에 값을 대입한다.
	 **************************************************************************/
	@Override
	public void setQuick(int index, double value)
	{
		if (value == 0.0f)
			m_keyChanged |= m_map.removeKey(index);
		else
			m_keyChanged |= m_map.put(index, value);
	}
	
	/**************************************************************************
	 * 백터를 지정한 배열의 값으로 모두 채운다.
	 **************************************************************************/
	@Override
	public void assign(double[] values)
	{
		if (values.length != m_size)
			throw new IllegalArgumentException("Must have same size: values.length=" + values.length + "getSize()=" + size());

		// 맵의 키와 값을 모두 제거한다.
		clear();
		
		// 배열을 순회하면서 백터에 값을 배정한다.
		for (int index = 0; index < values.length; index++)
		{
			// 값이 0인 경우는 값을 배정하지 않아도 된다.
			if (values[index] != 0.0f)
				setQuick(index, values[index]);
		}
	}

	/**************************************************************************
	 * 백터를 지정한 한가지 값으로 모두 채운다.
	 **************************************************************************/
	@Override
	public void assign(double value)
	{
		// 값이 0.0f 인 경우에 처리한다.
		if (value == 0.0f)
		{
			// 맵의 키와 값을 모두 제거한다.
			clear();
			
			// value 값 배정을 하지 않고 함수를 종료한다.
			return;
		}

		// 백터의 크기만큼 value 값을 배정한다.
		for (int index = 0; index < m_size; index++)
			setQuick(index, value);
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
	public CDVector copy()
	{
		CDVectorSparse newVector = new CDVectorSparse(m_size);

		// 맵을 복사한다.
		newVector.m_map = m_map.clone();
		
		// 키가 변경되었으므로 플래그를 설정한다.
		newVector.m_keyChanged = true;

		// 새로 생성된 백터를 반환한다.
		return newVector;
	}

	/**************************************************************************
	 * 백터를 배열로 반환한다.
	 **************************************************************************/
	@Override
	public double[] toArray()
	{
		double[] array = new double[m_size];

		// 이터레이터를 얻는다.
		Iter iter = getIter();
		
		// 순회하면서 값을 배열에 배정한다. 
		for (iter.Begin(); iter.hasMoreValue(); iter.Next())
		{
			// 인덱스를 구한다.
			int index = iter.GetCurrentIndex();
			
			// 배열에 값을 배정한다.
			array[index] = getQuick(index);
		}

		// 생성된 배열을 반환한다.
		return array;
	}
	
	@Override
	public double[] getArray() {
		return toArray();
	}

	@Override
	public void setArray(double[] newArray) {
		assign(newArray);
	}
	
	/**************************************************************************
	 * 벡터를  <Integer, Double> 형태의 맵으로 반환한다.
	 **************************************************************************/
	@Override
	public Map<Integer, Double> toMap()
	{
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		// 이터레이터를 얻는다.
		Iter iter = getIter();
		
		// 순회하면서 값을 맵에 배정한다. 
		for (iter.Begin(); iter.hasMoreValue(); iter.Next())
		{
			// 인덱스를 구한다.
			int index = iter.GetCurrentIndex();
			
			// 맵에 값을 배정한다.
			map.put(index, getQuick(index));
		}
		
		// 생성된 맵을 반환한다.
		return map;
	}
	
	/**************************************************************************
	 * 지정한 범위대로 잘라낸 벡터를 반환한다
	 **************************************************************************/
	@Override
	public CDVector trim(int start, int length)
	{
		// 효율적인 구현은 아니다..
		
		CDVector ret = new CDVectorSparse(length);
		
		for (int i=0; i<length; i++){
			ret.setQuick(i, get(start+i));
		}
		
		return ret;
	}

	/**************************************************************************
	 * 백터의 크기를 반환한다.
	 **************************************************************************/
	@Override
	public int size()
	{
		return m_size;
	}

	/**************************************************************************
	 * 백터에 0.0f 가 아닌 값들의 개수를 반환한다.
	 **************************************************************************/
	@Override
	public int cardinality()
	{
		return m_map.getSize();
	}
	
	/**************************************************************************
	 * @return 벡터의 최소값을 리턴한다.
	 **************************************************************************/
	@Override
	public double getMin()
	{
		double min;
		if(m_size==cardinality())
			min = Double.MAX_VALUE;
		else
			min = 0;
		
		Iter iter = getIter();
		
		for (iter.Begin(); iter.hasMoreValue(); iter.Next())
		{
			double value = iter.GetCurrentValue();
			if( value < min)
				min = value;
		}
		
		// 생성된 맵을 반환한다.
		return min;
	}
	
	/**************************************************************************
	 * @return 벡터의 최대값을 리턴한다.
	 **************************************************************************/
	@Override
	public double getMax()
	{
		double max;
		if(m_size==cardinality())
			max = Double.MIN_VALUE;
		else
			max = 0;
		
		Iter iter = getIter();
		
		for (iter.Begin(); iter.hasMoreValue(); iter.Next())
		{
			double value = iter.GetCurrentValue();
			if( value > max)
				max = value;
		}
		
		// 생성된 맵을 반환한다.
		return max;
	}


	/**************************************************************************
	 * @return 벡터의 원소 합을 리턴한다.
	 **************************************************************************/
	@Override
	public double getSum()
	{
		double sum=0;
		CDVector.Iter iter = getIter();
		
		for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
			sum += iter.GetCurrentValue();
		}
		
		return sum;
	}
	
	/*********************************************************************************************************
	 * @return CDPair 벡터내의 0이 아닌 원소들 중 최소값과 해당되는 entry의 index( 복수개일 수도 있음)의 Linked List 를 리턴한다
	 * @author swRyu 20080826
	 *********************************************************************************************************/
	@Override
	public CDPair< LinkedList<Integer>, Double > getMin_valueAndIndex(){
		
		// min value 를 갖는 벡터 내의 entry 들의 index 를 Linked List 형태로 저장한다
		LinkedList<Integer> min_index = new LinkedList<Integer>();
		
		// 벡터 내에서 iter 돌면서  0 아닌 원소중 제일 작은 값을 저장하고 있는다
		Double min_value;
				
		Iter iter = getIter(); // 이터레이터를 얻는다.
		
		// 아래와 같이 Iter 돌고나면, max_value 와 max_index 가 얻어진다 
		
		iter.Begin();
		min_value = iter.GetCurrentValue();
		min_index.add(iter.GetCurrentIndex());
		iter.Next();

		//for (iter.Begin(); iter.hasMoreValue(); iter.Next())
		//for 문에 들어가는 코드를 위에서 아래와 같이 수정함 - 오류가 있었다.
		//Begin 을 두번 불러서, 0번째 가 최소값에 해당하는 경우 두번 리스트에 적히는 문제가 있었다 
		
		for ( ; iter.hasMoreValue(); iter.Next())
		{	
			if (min_value == iter.GetCurrentValue()){
				
				min_index.add(iter.GetCurrentIndex());
				
			}else if(min_value > iter.GetCurrentValue()){
				
				min_value = iter.GetCurrentValue();
				min_index.clear();
				min_index.add(iter.GetCurrentIndex());
			
			}
		}
		
		// CDPair 형태로, min 값을 갖는 entry 들의 index 값을 linked list, 그리고 그 값을 Double 로 묶어서 리턴한다
		CDPair< LinkedList<Integer>, Double > ret = new CDPair< LinkedList<Integer>, Double >( min_index, min_value);
		return ret;
	}
	
	/******************************************************************************************************
	 * @return CDPair 벡터내의 0이 아닌 원소들중 최대값과 해당되는 entry의 index( 복수개일 수도 있음)의 Linked List 를 리턴한다
	 * @author swRyu 20080826
	 ******************************************************************************************************/
	@Override
	public CDPair< LinkedList<Integer>, Double > getMax_valueAndIndex(){
		
		// max value 를 갖는 벡터 내의 entry 들의 index 를 Linked List 형태로 저장한다
		LinkedList<Integer> max_index = new LinkedList<Integer>(); 
		
		// 벡터 내에서 제일 큰 값을 저장한다
		Double max_value;
		
		
		Iter iter = getIter(); // 이터레이터를 얻는다.
		// 아래와 같이 Iter 돌고나면, max_value 와 max_index 가 얻어진다 
		iter.Begin();
		max_value = iter.GetCurrentValue();
		max_index.add(iter.GetCurrentIndex());
		iter.Next();
		
		for ( ; iter.hasMoreValue(); iter.Next())
		{	
			if (max_value == iter.GetCurrentValue()){
				
				max_index.add(iter.GetCurrentIndex());
				
			}else if(max_value < iter.GetCurrentValue()){
				
				max_value = iter.GetCurrentValue();
				max_index.clear();
				max_index.add(iter.GetCurrentIndex());
			
			}
		}
		
		// CDPair 형태로, max 값을 갖는 entry 들의 index 값을 linked list, 그리고 그 값을 Double 로 묶어서 리턴한다
		CDPair< LinkedList<Integer>, Double > ret = new CDPair< LinkedList<Integer>, Double >( max_index, max_value);
		return ret;
	}
	
	/**************************************************************************
	 * 이터레이터를 반환한다.
	 **************************************************************************/
	@Override
	public Iter getIter()
	{
		return new IterSparse();
	}
	
	//=========================================================================
	// 내부 클래스
	//-------------------------------------------------------------------------
	
	/******************************************************************************
	 * 이터레이터 클래스
	 ******************************************************************************/
	
	public class IterSparse extends Iter
	{
		/**************************************************************************
		 * 인덱스 정보를 초기화한다.
		 **************************************************************************/
		@Override
		public void init()
		{
			// 초기화된 상태 그대로가 아니라면 처리한다.
			if (m_keyChanged != false)
			{
				// 인덱스를 가져온다.
				m_indices = m_map.getKeys();
				
				// 인덱스를 정렬한다.
				Arrays.sort(m_indices);
	
				// 키 정보를 초기화했으므로 키 변경 플래그를 끈다.
				m_keyChanged = false;
			}
		}
		
		/**************************************************************************
		 * 위치를 초기화한다.
		 **************************************************************************/
		@Override
		public void Begin()
		{
			init();
			currentPosition = 0;
		}
		
		@Override
		public void BeginQuick()
		{
			currentPosition = 0;
		}

		/**************************************************************************
		 * 다음 위치로 이동한다.
		 **************************************************************************/
		@Override
		public void Next()
		{
			currentPosition++;
		}
		
		/**************************************************************************
		 * 현재 위치의 인덱스를 반환한다.
		 **************************************************************************/
		@Override
		public int GetCurrentIndex()
		{
			return m_indices[currentPosition];
		}

		/**************************************************************************
		 * 현재 위치의 값을 반환한다.
		 **************************************************************************/
		@Override
		public double GetCurrentValue()
		{
			return m_map.get(m_indices[currentPosition]);
		}

		/**************************************************************************
		 * 뒤에 값이 더 있는지 알아본다.
		 **************************************************************************/
		@Override
		public boolean hasMoreValue()
		{
			return currentPosition != m_indices.length;
		}
		
		// 현재 인덱스
		protected int currentPosition = 0;
	}
	
	/** NaN to zero */
	@Override public CDVector recodeNaNToZero () {
		CDVector result = copy();
		CDVector.Iter iter = getIter();
		
		for( iter.Begin(); iter.hasMoreValue(); iter.Next() ){
			int i = iter.GetCurrentIndex();
			if( new Double(getQuick(i)).isNaN() )
				result.setQuick(i, 0);
		}
		
		return result;
	}
	
	//=========================================================================
	// 맴버 변수
	//-------------------------------------------------------------------------
	
	// 백터의 크기
	protected int m_size = 0;

	// 백터의 내용을 담고있는 맵
	protected CD32BDoubleMap m_map = null;

	// 정렬된 맵의 키 배열
	protected int[] m_indices = null;

	// 맵의 키 정보가 변경되었는지 여부
	protected boolean m_keyChanged = false;
}
