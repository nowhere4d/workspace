package nowhere4d.workspace.controller;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import nowhere4d.workspace.layout.CDMatrix;
import nowhere4d.workspace.layout.KK;
import nowhere4d.workspace.model.Graph;
import nowhere4d.workspace.model.LayoutResult;

@RestController
public class LayoutController {

	private static Logger logger = LoggerFactory.getLogger(LayoutController.class);

	@RequestMapping(value = "/layout/", method = RequestMethod.POST)
	public ResponseEntity<LayoutResult> post(@RequestBody Graph graph) {
		try {
			logger.info("layout : " + graph);
			
			LinkedList<String> nodes = new LinkedList<String>();
			for (String source : graph.getSources()) if (nodes.contains(source) == false) nodes.add(source);
			for (String target : graph.getTargets()) if (nodes.contains(target) == false) nodes.add(target);
			
			CDMatrix matrix = CDMatrix.sparse(nodes.size(), nodes.size());
			double[][] points = new double[2][nodes.size()];
			for (int i = 0; i < graph.getSources().size(); i++) {
				String src = graph.getSources().get(i);
				int srcIdx = nodes.indexOf(src);
				int tgtIdx = nodes.indexOf(graph.getTargets().get(i));
				matrix.set(srcIdx, tgtIdx, 1.0);
				matrix.set(tgtIdx, srcIdx, 1.0);
				
				int idx = graph.getSources().indexOf(src);
				points[0][srcIdx] = graph.getXs().get(idx);
				points[1][srcIdx] = graph.getYs().get(idx);
			}
			
			KK kk = new KK();
			double[][] result = kk.Compute(matrix, points, graph.getWidth()-100, graph.getHeight()-100);
			
			LayoutResult res = new LayoutResult();
			res.nodes = nodes;
			res.xs = new LinkedList<Double>();
			res.ys = new LinkedList<Double>();
			for (int i = 0; i < result[0].length; i++) res.xs.add(result[0][i]);
			for (int i = 0; i < result[1].length; i++) res.ys.add(result[1][i]);
			
			logger.info("layout : " + res);
			return new ResponseEntity<LayoutResult>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<LayoutResult>(new LayoutResult(), HttpStatus.NO_CONTENT);
	}
	
}
