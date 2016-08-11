package nowhere4d.workspace.layout;

public interface CAMatrixMathOptions
{
	public static final int MERGE_AND 		= 0;
	public static final int MERGE_OR 		= 1;
	public static final int MERGE_SUM 		= 2;
	public static final int MERGE_AVERAGE 	= 3;
	public static final int MERGE_MAX 		= 4;
	public static final int MERGE_MIN 		= 5;	
	public static final int MERGE_LINEAR 	= 6;
	
	// Pre-process variable's Hash Key
	public static final int MISSING						= 0;
	public static final int DIAGONAL					= 1;
	public static final int LAYER_FILETER				= 2;
	public static final int NODE_FILTER					= 3;
	public static final int EDGE_FILTER					= 4;
	public static final int MERGE_OPTION				= 5;
	public static final int MERGE_VECTOR				= 6;
	
	public static final int DICHOTOMIZE_OPERATOR		= 7;
	public static final int DICHOTOMIZE_VALUE			= 8;
	public static final int SYMMETRIZE_OPERATOR			= 9;
	public static final int COST_TO_STRENGTH			= 10;
	public static final int TO_DISSIMILARITY			= 11;
	
	/**
	* 두 matrix 요소를 곱하는 것으로 matrix 를 compose.
	*/
	public static final int COMPOSE_PRODUCT	= 0;
	/**
	* 두 matrix 요소를 더하는 것으로 matrix 를 compose.
	*/
	public static final int COMPOSE_SUM		= 1;
	
	/**
	* affiliation matrix 를 comembership matrix 로 변환한다.
	*/
	public static final int AFFILIATE_COMEMBERSHIP	= 0;
	/**
	* affiliation matrix 를 overlap matrix 로 변환한다.
	*/
	public static final int AFFILIATE_OVERLAP		= 1;
	/**
	* affiliation matrix 를 bipartite matrix 로 변환한다.
	*/
	public static final int AFFILIATE_BIPARTITE		= 2;
	
	
	/**
	 * affiliation matrix를 bipartite matrix로 만들되, main -> sub로의 링크만 만든다.
	 */
	public static final int AFFILIATE_BIPARTITE_SINGLE = 3;
	
	/**
	* extract match 로 attribute matrix 를 생성한다.
	*/
	public static final int CONVERT_EXACT		= 0;
	/**
	* absolute difference 로 attribute matrix 를 생성한다.
	*/
	public static final int CONVERT_ABSOLUTE	= 1;
	
	// Dichotomize Operator들 목록
	public static final String[] DICHOTOMIZE_OPERATORS_TEXT = {
		">", ">=", "=", "<", "<=", "!="
	};
	/**
	* dichotomize 연산시 threshold 값을 초과하는 것을 1로 만든다.
	*/
	public static final int DICHOTOMIZE_GT	= 0;
	/**
	* dichotomize 연산시 threshold 값이상을 1로 만든다.
	*/
	public static final int DICHOTOMIZE_GE	= 1;
	/**
	* dichotomize 연산시 threshold 값과 같은 값을 1로 만든다.
	*/
	public static final int DICHOTOMIZE_EQ	= 2;
	/**
	* dichotomize 연산시 threshold 값미만을 1로 만든다.
	*/
	public static final int DICHOTOMIZE_LT	= 3;
	/**
	* dichotomize 연산시 threshold 값이하를 1로 만든다.
	*/
	public static final int DICHOTOMIZE_LE	= 4;
	/**
	* dichotomize 연산시 threshold 값과 다른 값을 1로 만든다.
	*/
	public static final int DICHOTOMIZE_NEQ	= 5;
	
//	// Symmetrize Operator 목록
//	public static final String[] SYMMETRIZE_OPERATORS_TEXT = {
//		"MAX", "MIN", "AVG", "SUM", "PRODUCT", "LOWER", "UPPER"
//	};
	/**
	* symmetrize 연산시 upper 값과 lower 값중 큰값으로 두값을 셋팅한다.
	*/
	public static final int SYMMETRIZE_MAX		= 0;
	/**
	* symmetrize 연산시 upper 값과 lower 값중 작은값으로 두값을 셋팅한다.
	*/
	public static final int SYMMETRIZE_MIN		= 1;
	/**
	* symmetrize 연산시 upper 값과 lower 값의 평균값으로 두값을 셋팅한다.
	*/
	public static final int SYMMETRIZE_AVERAGE	= 2;
	/**
	* symmetrize 연산시 upper 값과 lower 값의 합으로 두값을 셋팅한다.
	*/
	public static final int SYMMETRIZE_SUM		= 3;
	/**
	* symmetrize 연산시 upper 값과 lower 값의 곱으로 두값을 셋팅한다.
	*/
	public static final int SYMMETRIZE_PRODUCT	= 4;
	/**
	* symmetrize 연산시 lower 값으로 upper 값과 lower 값을 셋팅한다.
	*/
	public static final int SYMMETRIZE_LOWER	= 5;
	/**
	* symmetrize 연산시 upper 값으로 upper 값과 lower 값을 셋팅한다.
	*/
	public static final int SYMMETRIZE_UPPER	= 6;
	
	public static final String[] SYMMETRIZE_OPERATORS_TEXT = {"MAX","MIN","AVG","SUM","PRODUCT","LOWER","UPPER"};
	
	/**
	 * Reverse 연산
	 */
	public static final int REVERSE_INTERVAL		= 0;
	public static final int REVERSE_RATIO			= 1;
	public static final int REVERSE_FIXED_DECAY		= 2;
	
	public static final int DECREASING_ORDER = 1;
	public static final int INCREASING_ORDER = 0;
}