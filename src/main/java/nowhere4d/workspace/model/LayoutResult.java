package nowhere4d.workspace.model;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class LayoutResult {
	
	public List<String> nodes;
	public List<Double> xs;
	public List<Double> ys;
	
	public List<String> getNodes() {
		return nodes;
	}

	public void setNodes(List<String> nodes) {
		this.nodes = nodes;
	}

	public List<Double> getXs() {
		return xs;
	}

	public void setXs(List<Double> xs) {
		this.xs = xs;
	}

	public List<Double> getYs() {
		return ys;
	}

	public void setYs(List<Double> ys) {
		this.ys = ys;
	}

	@Override
	public String toString() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return super.toString();
	}
}
