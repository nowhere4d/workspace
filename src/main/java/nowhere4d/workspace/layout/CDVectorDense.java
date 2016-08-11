package nowhere4d.workspace.layout;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CDVectorDense extends CDVector 
{

	private static final long serialVersionUID = -7986725437897571343L;
	
	//=========================================================================
	// 생성자
	//-------------------------------------------------------------------------
	
	protected CDVectorDense(int size)
	{
		m_size = size;
		array = new double[m_size];
	}
	
	protected CDVectorDense(double[] values)
	{
		this(values.length);
		
		// 배열로 이루어진 백터의 값을 대입한다.
		assign(values);
	}
	
	protected CDVectorDense(int size, double initialValue)
	{
		this(size);
		assign(initialValue);
	}

	//=========================================================================
	// PUBLIC 메소드
	//-------------------------------------------------------------------------

	

	@Override
	public boolean isDense() {
		return true;
	}

	/**************************************************************************
	 * 입력 벡터와 동일하지만, CDVectorDense로 구현된 행렬를 반환한다.
	 * 이미 CDVectorDense로 구현되어 있으면 자기자신을 반환
	 ***************************************************************************/
	@Override
	public CDVector toDense(){
		return this;
	}
	
	/**************************************************************************
	 * 입력 벡터와 동일하지만, CDVectorSparse로 구현된 행렬를 반환한다.
	 * 이미 CDVectorSparse로 구현되어 있으면 자기자신을 반환
	 ***************************************************************************/
	@Override
	public CDVector toSparse(){
		return new CDVectorSparse(array);
		
	}
	/**************************************************************************
	 * 백터의 특정 인덱스 값을 반환한다. 바운드 처리를 해준다.
	 **************************************************************************/
	@Override
	public double get(int index)
	{
		if (index < 0 || index >= m_size)
			throw new IndexOutOfBoundsException("index:" + index);

		return array[index];
	}

	/**************************************************************************
	 * 백터의 특정 인덱스 값을 반환한다.
	 **************************************************************************/
	@Override
	public double getQuick(int index)
	{
		return array[index];
	}
	
	/**************************************************************************
	 * 백터의 특정 인덱스에 값을 대입한다. 바운드 처리를 해준다.
	 **************************************************************************/
	@Override
	public void set(int index, double value)
	{
		if (index < 0 || index >= m_size)
			throw new IndexOutOfBoundsException("index:" + index);

		array[index]=value;
	}

	/**************************************************************************
	 * 백터의 특정 인덱스에 값을 대입한다.
	 **************************************************************************/
	@Override
	public void setQuick(int index, double value)
	{
		array[index]=value;
	}
	
	/**************************************************************************
	 * 백터를 지정한 배열의 값으로 모두 채운다.
	 **************************************************************************/
	@Override
	public void assign(double[] values)
	{
		if (values.length != m_size)
			throw new IllegalArgumentException("Must have same size: values.length=" + values.length + "getSize()=" + size());

		array=values.clone();
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
			array[index]=value;
	}

	/**************************************************************************
	 * 백터를 초기화한다.
	 **************************************************************************/
	@Override
	public void clear()
	{
		array = new double[m_size];
	}
	
	/**************************************************************************
	 * 백터를 복사하여 반환한다.
	 **************************************************************************/
	@Override
	public CDVector copy()
	{
		CDVectorDense newVector = new CDVectorDense(m_size);

		// 맵을 복사한다.
		newVector.array = array.clone();
		

		// 새로 생성된 백터를 반환한다.
		return newVector;
	}

	/**************************************************************************
	 * 백터를 배열로 반환한다.
	 **************************************************************************/
	@Override
	public double[] toArray()
	{
		return array.clone();
	}
	

	@Override
	public double[] getArray() {
		return array;
	}

	@Override
	public void setArray(double[] newArray) {
		if (newArray.length != m_size)
			throw new IllegalArgumentException("Must have same size: values.length=" +
					newArray.length + "getSize()=" + size());

		// 성능 상의 이유로 첫행만 검사한다.
		array = newArray;
		
	}
	
	/**************************************************************************
	 * 벡터를  <Integer, Double> 형태의 맵으로 반환한다.
	 **************************************************************************/
	@Override
	public Map<Integer, Double> toMap()
	{
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		// 순회하면서 값을 맵에 배정한다. 
		for (int i=0; i<m_size;i++)
		{
			if(array[i]!=0.0f)
			// 맵에 값을 배정한다.
				map.put(i, array[i]);
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
		CDVectorDense ret = new CDVectorDense(length);
		double[] newArray= new double[length];
		for (int i=0; i<length; i++){
			newArray[i] = array[start+i];
		}
		ret.setArray(newArray);
		return ret;
	}

	/**************************************************************************
	 * 백터를 문자열로 반환한다.
	 **************************************************************************/
	@Override
	public String toString()
	{
		String str = String.valueOf(m_size);

		// 순회하면서 값을 맵에 배정한다. 
		for (int i=0; i<m_size;i++)
		{
			if(array[i]!=0.0f)
				str += String.format("\r\n%d,%.3f", i, array[i]);
		}
		return str;
	}
	
	/**************************************************************************
	 * CDVector의 값을 String[]로 변환한다.<br>
	 * NaN은 null로 내보낸다.
	 * @return
	 **************************************************************************/
	@Override
	public String[] toStringArray() {
		String[] tortn = new String[size()];
		for (int i=0; i < m_size; i++) {
			double value = array[i];
			// NaN은 null로 만든다.
			if (Double.isNaN(value))
				tortn[i] = null;
			else 
				tortn[i] = Double.toString(value);
		}
		return tortn;
	}

	@Override
	public String toStringPretty()
	{
		String str = new String();

		for (int index = 0; index < m_size; index++)
			str += array[index] + " ";

		return str;
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
		int count=0;
		for(int i =0; i<m_size; i++){
			if(array[i]!=0.0f)
				count++;
		}
		return count;
	}
	
	/**************************************************************************
	 * @return 벡터의 최소값을 리턴한다.
	 **************************************************************************/
	@Override
	public double getMin()
	{	
		double min = array[0];
		for(int i =0; i<m_size; i++){
			if(array[i]<min)
				min = array[i];
		}
		
		return min;
	}
	
	/**************************************************************************
	 * @return 벡터의 최대값을 리턴한다.
	 **************************************************************************/
	@Override
	public double getMax()
	{
		double max = array[0];
		for(int i =0; i<m_size; i++){
			if(array[i] > max)
				max = array[i];
		}
		
		return max;
	}


	/**************************************************************************
	 * @return 벡터의 원소 합을 리턴한다.
	 **************************************************************************/
	@Override
	public double getSum()
	{
		double sum=0;
		for(int i =0; i<m_size; i++){
			sum += array[i];
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
		return new IterDense();
	}
	
	//=========================================================================
	// 내부 클래스
	//-------------------------------------------------------------------------
	
	/******************************************************************************
	 * 이터레이터 클래스
	 ******************************************************************************/
	public class IterDense extends Iter 
	{
		
		/**************************************************************************
		 * 인덱스 정보를 초기화한다.
		 **************************************************************************/
		@Override
		public void init(){
		}
		/**************************************************************************
		 * 위치를 초기화한다.
		 **************************************************************************/
		@Override
		public void Begin(){
			BeginQuick();
		}
		
		@Override
		public void BeginQuick(){
			this.currentPosition = 0;
			//첫 시작 인덱스를 계산한다. 원소가 하나도 없는 경우에는-1.
			
			while(true){
				if(currentPosition>=m_size){
					currentPosition=-1;
					break;
				}
				else if(array[currentPosition]!=0.0f){
					break;
				}
				currentPosition++;
			}
		}

		/**************************************************************************
		 * 다음 위치로 이동한다.
		 **************************************************************************/
		@Override
		public void Next(){
			currentPosition++;
			while(true){
				if(currentPosition>=m_size){
					currentPosition=-1;
					break;
				}
				else if(array[currentPosition]!=0.0f){
					break;
				}
				currentPosition++;
			}
		}
		
		/**************************************************************************
		 * 현재 위치의 인덱스를 반환한다.
		 **************************************************************************/
		@Override
		public int GetCurrentIndex()
		{
			return currentPosition;
		}

		/**************************************************************************
		 * 현재 위치의 값을 반환한다.
		*************************************************************************/
		@Override
		public double GetCurrentValue()
		{
			return array[currentPosition];
		}

		/**************************************************************************
		 * 뒤에 값이 더 있는지 알아본다.
		 **************************************************************************/
		@Override
		public boolean hasMoreValue()
		{
			return currentPosition != -1;
		}
		
		// 현재 인덱스
		protected int currentPosition = 0;
	}
	
	/** NaN to zero */
	@Override
	public CDVector recodeNaNToZero () {
		CDVector result = new CDVectorDense(m_size);
		double[] newArray= new double [m_size]; 
		for(int i =0; i< m_size; i++){
			if(new Double(array[i]).isNaN())
				newArray[i]=0;
			else
				newArray[i]=array[i];
		}
		result.setArray(newArray);
		return result;
	}
	
	//=========================================================================
	// 맴버 변수
	//-------------------------------------------------------------------------
	
	//값을 저장하는 행렬
	private double array[];
	// 백터의 크기
	protected int m_size = 0;


}
