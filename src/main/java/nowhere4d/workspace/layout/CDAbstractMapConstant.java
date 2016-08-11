package nowhere4d.workspace.layout;

public interface CDAbstractMapConstant
{
	static final int 	DEFAULT_CAPACITY 			= 277;
	static final int	INITIAL_CAPACITY_LIMIT 		= 1000000;
	
	static final double DEFAULT_MIN_LOAD_FACTOR 	= 0.2f;
	static final double DEFAULT_MAX_LOAD_FACTOR		= 0.5f;
	
	static final byte	STATE_FREE 					= 0;
	static final byte 	STATE_FULL 					= 1;
	static final byte 	STATE_REMOVED 				= 2;
}