package tfy_lab3;

import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.RenderContext;

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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.apache.commons.collections15.Transformer;


public class BinaryTreeGUI extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final double VERTEX_SIZE = 70;
	private static final double EMPTY_VERTEX_SIZE = 20;

    private DelegateForest<TreeNode, TreeEdge> g;
    private DynamicTreeLayout<TreeNode, TreeEdge> layout;
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
        RenderContext<TreeNode, TreeEdge> context = vv.getRenderContext();

        context.setEdgeLabelTransformer(new ToStringLabeller());
        context.setEdgeFontTransformer(new Transformer<TreeEdge, Font>() {
            public Font transform(TreeEdge i) {
                Font font = new Font("Times", Font.BOLD, 16);
                return font;
            }
        });
        context.setLabelOffset(-5);
        final Color edgeLabelColor = Color.black;
        context.setEdgeLabelRenderer(new DefaultEdgeLabelRenderer(edgeLabelColor, true) {
			@Override
			public <E> Component getEdgeLabelRendererComponent(JComponent vv, Object value, Font font,
					boolean isSelected, E edge) {
				super.getEdgeLabelRendererComponent(vv, value, font, isSelected, edge);
				setForeground(edgeLabelColor);
				return this;
			}
        });
        
        context.setEdgeShapeTransformer(new EdgeShape.Line<>());
        context.setVertexLabelTransformer(new ToStringLabeller<TreeNode>() {
            @Override
            public String transform(TreeNode n) {
            	if (n.data == null) return "no data";
            	
            	if (n.data.type == NodeType.typePlaceholder || n.data.type == NodeType.typeNone) {
            		return "";
            	}
            	
            	if (n.data.id != null) {
            		String str = new String(n.data.id).trim();
            		
            		if (n.data.type == NodeType.typeVar) {
            			str += " = " + n.data.refValue.value + " [" + n.data.refValue.getTypeName() + "]";
            		} else if (n.data.type == NodeType.typeMain) {
            			str += " [" + n.data.type + "]";
            		}
            		
            		return str;
            	} else {
            		return "";
            	}
            }
        });
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        
        Transformer<TreeNode, Paint> painter = new Transformer<TreeNode, Paint>() {
            public Paint transform(TreeNode n) {
            	if (n.data != null) {
                	if (n.data.type == NodeType.typeNone) {
                        return Color.BLACK;
                	} else if (n.data.type == NodeType.typePlaceholder) {
                        return new Color(0,0,0,0);                		
                	}
            	}
            	
                return Color.WHITE;
            }
        };
        
        context.setVertexFillPaintTransformer(painter);
        context.setVertexFontTransformer(new Transformer<TreeNode, Font>() {
            public Font transform(TreeNode i) {
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
        context.setVertexLabelRenderer(vertexLabelRenderer);

        Transformer<TreeNode, Shape> vertexSize;
        vertexSize = new Transformer<TreeNode, Shape>() {
            @Override
            public Shape transform(TreeNode n) {
            	Rectangle2D circle = new Rectangle2D.Double(-60, -15, 120, 30);
                
            	if (n.data != null) {
            		if (n.data.type == NodeType.typeNone || n.data.type == NodeType.typePlaceholder) {
                        return new Ellipse2D.Double(-EMPTY_VERTEX_SIZE/2, -EMPTY_VERTEX_SIZE/2, EMPTY_VERTEX_SIZE, EMPTY_VERTEX_SIZE);
            		}
            	}
            	
                return circle;
            }
        };
        context.setVertexShapeTransformer(vertexSize);

        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());

        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);        
        scrollPane = new JScrollPane(panel);
        add(scrollPane);   
    }

    private void buildTree(TreeNode node) {
        if (node != null) {
        	if (node.leftChild != null || node.rightChild != null) {
            	TreeNode child = node.leftChild != null ? node.leftChild : new TreeNode(new NodeData(NodeType.typePlaceholder));
                g.addEdge(new TreeEdge(node, child), node, child, EdgeType.DIRECTED);
                
                if (node.leftChild != null) {
                    buildTree(child);
                }
        	
            	child = node.rightChild != null ? node.rightChild : new TreeNode(new NodeData(NodeType.typePlaceholder));
                g.addEdge(new TreeEdge(node, child), node, child, EdgeType.DIRECTED);
                
                if (node.rightChild != null) {
                    buildTree(child);
                }
        	}
        }
    }
    
    public void buildTree() {
        g.addVertex(st);
        buildTree(st);
    }
}
