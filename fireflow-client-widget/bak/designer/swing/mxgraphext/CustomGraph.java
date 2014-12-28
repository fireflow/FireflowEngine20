package org.fireflow.designer.swing.mxgraphext;

import java.util.Iterator;
import java.util.List;

import org.fireflow.designer.swing.proxy.Wrapper;
import org.fireflow.pdl.fpdl.io.FPDLNames;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

/**
 * A graph that creates new edges from a given template edge.
 */
public class CustomGraph extends mxGraph
{
	/**
	 * Holds the edge to be used as a template for inserting new edges.
	 */
	protected Object edgeTemplate;

	/**
	 * Custom graph that defines the alternate edge style to be used when
	 * the middle control point of edges is double clicked (flipped).
	 */
	public CustomGraph()
	{
		setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.ElbowConnector;elbow=vertical");
	}

	/**
	 * Sets the edge template to be used to inserting edges.
	 */
	public void setEdgeTemplate(Object template)
	{
		edgeTemplate = template;
	}

	/**
	 * Prints out some useful information about the cell in the tooltip.
	 */
	public String getToolTipForCell(Object cell)
	{
		String tip = "<html>";
		mxGeometry geo = getModel().getGeometry(cell);
		mxCellState state = getView().getState(cell);

		if (getModel().isEdge(cell))
		{
			tip += "points={";

			if (geo != null)
			{
				List<mxPoint> points = geo.getPoints();

				if (points != null)
				{
					Iterator<mxPoint> it = points.iterator();

					while (it.hasNext())
					{
						mxPoint point = it.next();
//						tip += "[x=" + FireflowDesigner.numberFormat.format(point.getX())
//								+ ",y=" + FireflowDesigner.numberFormat.format(point.getY())
//								+ "],";
					}

					tip = tip.substring(0, tip.length() - 1);
				}
			}

			tip += "}<br>";
			tip += "absPoints={";

			if (state != null)
			{

				for (int i = 0; i < state.getAbsolutePointCount(); i++)
				{
					mxPoint point = state.getAbsolutePoint(i);
//					tip += "[x=" + FireflowDesigner.numberFormat.format(point.getX())
//							+ ",y=" + FireflowDesigner.numberFormat.format(point.getY())
//							+ "],";
				}

				tip = tip.substring(0, tip.length() - 1);
			}

			tip += "}";
		}
		else
		{
			tip += "geo=[";

			if (geo != null)
			{
//				tip += "x=" + FireflowDesigner.numberFormat.format(geo.getX()) + ",y="
//						+ FireflowDesigner.numberFormat.format(geo.getY()) + ",width="
//						+ FireflowDesigner.numberFormat.format(geo.getWidth()) + ",height="
//						+ FireflowDesigner.numberFormat.format(geo.getHeight());
			}

			tip += "]<br>";
			tip += "state=[";

			if (state != null)
			{
//				tip += "x=" + FireflowDesigner.numberFormat.format(state.getX()) + ",y="
//						+ FireflowDesigner.numberFormat.format(state.getY()) + ",width="
//						+ FireflowDesigner.numberFormat.format(state.getWidth())
//						+ ",height="
//						+ FireflowDesigner.numberFormat.format(state.getHeight());
			}

			tip += "]";
		}

		mxPoint trans = getView().getTranslate();

//		tip += "<br>scale=" + FireflowDesigner.numberFormat.format(getView().getScale())
//				+ ", translate=[x=" + FireflowDesigner.numberFormat.format(trans.getX())
//				+ ",y=" + FireflowDesigner.numberFormat.format(trans.getY()) + "]";
//		tip += "</html>";

		return tip;
	}

	/**
	 * Overrides the method to use the currently selected edge template for
	 * new edges.
	 * 
	 * @param graph
	 * @param parent
	 * @param id
	 * @param value
	 * @param source
	 * @param target
	 * @param style
	 * @return
	 */
	public Object createEdge(Object parent, String id, Object value,
			Object source, Object target, String style)
	{
		if (edgeTemplate != null)
		{
			mxCell edge = (mxCell) cloneCells(new Object[] { edgeTemplate })[0];
			edge.setId(id);

			return edge;
		}
		return super.createEdge(parent, id, value, source, target, style);
	}

	public String convertValueToString(Object cell)
	{
		Object result = model.getValue(cell);

		if (result==null)return "";
		if (result instanceof Wrapper){
			Wrapper nodeWrapper = (Wrapper)result;
			String type = nodeWrapper.getElementType();
			
			String displayName = (String)nodeWrapper.getAttribute(FPDLNames.DISPLAY_NAME);
			
			if(FPDLNames.COMMENT.equals(type)){
				displayName = (String)nodeWrapper.getAttribute(FPDLNames.DESCRIPTION);
			}
			if(FPDLNames.ACTIVITY.equals(type)){
				if (displayName==null || displayName.trim().equals("")){
					displayName = (String)nodeWrapper.getAttribute(FPDLNames.NAME);
				}
			}

			
			return displayName;
		}else{
			return result.toString();
		}
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.view.mxGraph#isCellBendable(java.lang.Object)
	 */
	@Override
	public boolean isCellBendable(Object cell) {
		// TODO Auto-generated method stub
		return super.isCellBendable(cell);
	}

	
}