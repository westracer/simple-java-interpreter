package tfy_lab3;

import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import java.awt.Dimension;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.apache.commons.collections15.Transformer;

public class BinaryTreeGUI extends JPanel {
	private static final long serialVersionUID = 1L;

    private DelegateForest<TreeNode, TreeNode> g;
    private DynamicTreeLayout layout;
    private int edgeCounter;
	private VisualizationViewer vv;
    private TreeNode st;
    private JScrollPane scrollPane;
   
	@SuppressWarnings("unchecked")
	public BinaryTreeGUI(TreeNode st) {
        this.st = st;
        g = new DelegateForest(new DirectedOrderedSparseMultigraph());
        buildTree();
        layout = new DynamicTreeLayout(g);
        vv = new VisualizationViewer(layout, new Dimension(1024, 600));

        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeFontTransformer(new Transformer<Object, Font>() {
            public Font transform(Object i) {
                Font font = new Font("Times", Font.BOLD, 16);
                return font;
            }
        });
        vv.getRenderContext().setLabelOffset(-5);
        final Color edgeLabelColor = Color.red;
        vv.getRenderContext().setEdgeLabelRenderer(new DefaultEdgeLabelRenderer(edgeLabelColor, true) {
			@Override
			public <E> Component getEdgeLabelRendererComponent(JComponent vv, Object value, Font font,
					boolean isSelected, E edge) {
				super.getEdgeLabelRendererComponent(vv, value, font, isSelected, edge);
				setForeground(edgeLabelColor);
				return this;
			}
        });
        
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.QuadCurve());
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>() {
            @Override
            public String transform(Integer v) {
            	if (v < 1000)
            		return "";
            	else
            		// лучше создать класс для вершины
            		return String.valueOf(v - 1000);
            }
        });
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        
        Transformer<Integer, Paint> redBlack = new Transformer<Integer, Paint>() {
            public Paint transform(Integer i) {
                return Color.WHITE;
            }
        };
        
        vv.getRenderContext().setVertexFillPaintTransformer(redBlack);
        vv.getRenderContext().setVertexFontTransformer(new Transformer<Object, Font>() {
            public Font transform(Object i) {
                Font font = new Font("Times", Font.BOLD, 12);
                return font;
            }
        });
        
        final Color vertexLabelColor = Color.BLACK;
		@SuppressWarnings("serial")
		DefaultVertexLabelRenderer vertexLabelRenderer = new DefaultVertexLabelRenderer(vertexLabelColor) {
			@Override
			public <V> Component getVertexLabelRendererComponent(JComponent vv, Object value, Font font,
					boolean isSelected, V vertex) {
				super.getVertexLabelRendererComponent(vv, value, font, isSelected, vertex);
				setForeground(vertexLabelColor);
				return this;
			}
		};
        vv.getRenderContext().setVertexLabelRenderer(vertexLabelRenderer);

        Transformer<Object, Shape> vertexSize;
        vertexSize = new Transformer<Object, Shape>() {
            @Override
            public Shape transform(Object i) {
                Ellipse2D circle = new Ellipse2D.Double(-15, -15, 30, 30);
                if (i != null) {
                    return AffineTransform.getScaleInstance(1.5, 1.5).createTransformedShape(circle);
                } else {
                    return circle;
                }
            }
        };
        vv.getRenderContext().setVertexShapeTransformer(vertexSize);

        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());

        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);        
        scrollPane = new JScrollPane(panel);
        add(scrollPane);   
    }

    private void buildTree(TreeNode node) {
        if (node != null) {
        	if (node.leftChild != null) {
                g.addEdge(node.leftChild, node, node.leftChild, EdgeType.DIRECTED);
                buildTree(node.leftChild);
        	}
        	
        	if (node.rightChild != null) {
                g.addEdge(node.rightChild, node, node.rightChild, EdgeType.DIRECTED);
                buildTree(node.rightChild);
        	}
        }
    }
    
    public void buildTree() {
    	System.out.println(st);
        g.addVertex(st);
        buildTree(st);
    }
}
