package nowhere4d.workspace.layout;

public class CDMatrixEntry {
	int mSource, mTarget;
	double mWeight;
	CDMatrixEntry(int source, int target, double weight) {
		mSource = source;
		mTarget = target;
		mWeight = weight;
	}
	
	public int getSource() {
		return mSource;
	}
	
	public int getTarget() {
		return mTarget;
	}
	
	public double getWeight() {
		return mWeight;
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %d) : %f", mSource, mTarget, mWeight);
	}
}
