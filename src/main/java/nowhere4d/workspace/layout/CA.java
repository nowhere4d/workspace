package nowhere4d.workspace.layout;

public class CA
{
	public final static String	VERSION				= "1.1.0.070202";
	
	public final static int		MEMORY_MODE			= 0;
	public final static int		CONSOL_MODE			= 1;
	
	public final static int		BROADCAST_HANDLE	= 0;
	
	//WM_USER = 1024 
	public final static int		CAM_START			= 1024 + 1000;
	public final static int		CAM_FINISH			= 1024 + 1010;
	public final static int		CAM_PROGRESS		= 1024 + 1020;
	public final static int		CAM_STATUS			= 1024 + 1030;
	public final static int		CAM_ERROR			= 1024 + 1040;
	public final static int		CAM_RETURN_MATRIX	= 1024 + 1050;
	public final static int		CAM_RETURN_VECTOR	= 1024 + 1060;
	public final static int		CAM_RETURN_REAL		= 1024 + 1070;
	public final static int		CAM_RETURN_INTEGER	= 1024 + 1080;
	public final static int		CAM_RETURN_BOOLEAN	= 1024 + 1090;
	
	// Missing - NMPPConst에서 바뀌면 같이 바꿀 것
	public final static double	NM_GLOBAL_MISSING	= -999999.0;
}