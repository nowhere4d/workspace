package nowhere4d.workspace.model;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Graph {
	
	private List<String> sources;
	private List<String> targets;
	
	private List<Double> xs;
	private List<Double> ys;
	
	private Double width;
	private Double height;
	
	public List<String> getSources() {
		return sources;
	}

	public void setSources(List<String> sources) {
		this.sources = sources;
	}

	public List<String> getTargets() {
		return targets;
	}

	public void setTargets(List<String> targets) {
		this.targets = targets;
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
	
	public Double getWidth() {
		return width;
	}

	public void setWidth(Double width) {
		this.width = width;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
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
