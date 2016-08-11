package nowhere4d.workspace.layout;

import java.io.Serializable;

public class CDPair<L, R> implements Comparable<CDPair<L,R>>, Serializable
{
	
	private static final long serialVersionUID = 6515523522072542159L;
	
	private static final int PRIME = 1000003;
	
	//=========================================================================
	// 생성자
	//-------------------------------------------------------------------------
	
	public CDPair(L left, R right)
	{
		m_left = left;
		m_right = right;
	}
	
	//=========================================================================
	// PUBLIC 메소드
	//-------------------------------------------------------------------------
	
	public L getLeft()
	{
		return m_left;
	}
	
	public R getRight()
	{
		return m_right;
	}
	
	private boolean compare(Object o, Object comp) {
		if(o==null && comp==null)
			return true;
		else if(o!=null && comp!=null)
			return o.equals(comp);
		else 
			return false;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CDPair)
		{
			CDPair<?, ?> pair = (CDPair<?, ?>)o;
			final boolean sameLeft = compare(pair.getLeft(), m_left);
			final boolean sameRight = compare(pair.getRight(), m_right);
			return sameLeft && sameRight;
//			if((pair.getLeft()==null && m_left!=null)
//			|| (pair.getRight()==null && m_right!=null)
//			|| (pair.getLeft()!=null && m_left==null)
//			|| (pair.getRight()!=null && m_right==null)) {
//				return false;
//			} else if(pair.getLeft()==null && m_left==null)
//			return pair.getLeft().equals(m_left) && pair.getRight().equals(m_right);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		if(m_left==null)
			return m_right.hashCode();
		else if(m_right==null)
			return m_left.hashCode() * PRIME;
		else 
			return m_left.hashCode() * PRIME + m_right.hashCode();
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append('(');
		buf.append(m_left.toString());
		buf.append(',');
		buf.append(m_right.toString());
		buf.append(')');
		return buf.toString();
	}
	
	//=========================================================================
	// PROTECTED 메소드
	//-------------------------------------------------------------------------
	
	protected void setLeft(L left)
	{
		m_left = left;
	}
	
	protected void setRight(R right)
	{
		m_right = right;
	}

	//=========================================================================
	// 변수
	//-------------------------------------------------------------------------
	
	private L m_left;
	private R m_right;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int compareTo(CDPair<L, R> o) {
		int left = (((Comparable)this.getLeft()).compareTo(o.getLeft()) );
		if (left != 0) {
			return left;
		}
		int right = (((Comparable)this.getRight()).compareTo(o.getRight()) );
		return right;
	}
}
