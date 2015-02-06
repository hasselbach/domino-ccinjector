package ch.hasselba.xpages.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.jscript.InterpretException;
import com.ibm.jscript.std.ObjectObject;
import com.ibm.xsp.component.FacesComponent;
import com.ibm.xsp.component.UIIncludeComposite;
import com.ibm.xsp.model.DataContextImpl;
import com.ibm.xsp.page.FacesComponentBuilder;

/**
 * Component to dynamically insert Custom Controls and values are added as an
 * own data context variable
 * 
 * The custom controls are defined in the value binding "fielddata". The name of
 * the dataContext variable is defined in "contextVar".
 * 
 * 
 * 
 * @author Sven Hasselbach
 * @version 1.2
 */
public class UICCInjector extends UIComponentBase implements FacesComponent,
		Serializable {

	private static final long serialVersionUID = 1L;
	private final static String UITYPE = "ch.hasselba.ccInjector";
	private final static String PROPERTY_FIELDTYPE = "fieldTypes";
	private final static String PROPERTY_FIELDDATA = "fieldData";
	private final static String PROPERTY_CONTEXTVAR = "contextVar";
	private final static String CC_PREFIX = "/";
	private final static String CC_SUFFIX = ".xsp";
	private final static String CC_ID_PREFIX = "new_";

	// private properties
	private String _variableName;
	private List<ObjectObject> _fieldData;
	private boolean _fieldDataSet;

	private String _contextVar;

	private boolean _generated = false;
	private FacesComponentBuilder _builder;

	/**
	 * the constructor
	 */
	public UICCInjector() {
		setRendererType(UITYPE);
	}

	/**
	 * returns the family of the component
	 * 
	 * @return String the family
	 */
	@Override
	public String getFamily() {
		return UITYPE;
	}

	/**
	 * sets the property "generated"
	 * 
	 * @param generated
	 *            boolean if already generated
	 */
	public void setGenerated(boolean generated) {
		this._generated = generated;
	}

	/**
	 * returns if already generated
	 * 
	 * @return boolean
	 */
	public boolean isGenerated() {
		return _generated;
	}

	/**
	 * access to the FacesComponentBuilder of this component required for the
	 * Custom Controls
	 * 
	 * @return FacesComponentBuilder instance
	 */
	public FacesComponentBuilder getBuilder() {
		return _builder;
	}

	/**
	 * sets the FacesComponentBuilder
	 * 
	 * @param builder
	 *            FacesComponentBuilder instance
	 */
	public void setBuilder(FacesComponentBuilder builder) {
		this._builder = builder;
	}

	/**
	 * @see com.ibm.xsp.component.com.ibm.xsp.component#buildContents(FacesContext
	 *      fc, FacesComponentBuilder builder)
	 */
	public void buildContents(FacesContext fc, FacesComponentBuilder builder)
			throws FacesException {
		this._builder = builder;
		this.injectCustomControls(fc);

	}

	/**
	 * @see com.ibm.xsp.component.com.ibm.xsp.component#initAfterContents(FacesContext
	 *      fc)
	 */
	public void initAfterContents(FacesContext fc) throws FacesException {
	}

	/**
	 * @see com.ibm.xsp.component.com.ibm.xsp.component#initBeforeContents(FacesContext
	 *      fc)
	 */
	public void initBeforeContents(FacesContext fc) throws FacesException {
	}

	/**
	 * gets the name of the variable containing the CC name
	 * 
	 * @return Stringcontaining the field types
	 */
	public String getVariableName() {
		if (this._variableName != null) {
			return this._variableName;
		}

		ValueBinding vb = getValueBinding(PROPERTY_FIELDTYPE);
		if (vb != null) {
			this._variableName = (String) vb.getValue(getFacesContext());
			return this._variableName;
		}

		return null;
	}

	/**
	 * sets the name of the variable containing the CC name
	 * 
	 * @param variableName
	 *            String containing the name of the variable
	 */

	public void setVariableName(final String variableName) {
		this._variableName = variableName;
	}

	/**
	 * gets the field data binding
	 * 
	 * @return List containing the field data
	 */
	@SuppressWarnings("unchecked")
	public List<ObjectObject> getFieldData() {

		if (this._fieldData != null) {
			return this._fieldData;
		}

		if (this._fieldDataSet)
			return this._fieldData;

		ValueBinding vb = getValueBinding(PROPERTY_FIELDDATA);
		if (vb != null) {

			Object value = vb.getValue(getFacesContext());

			if (value instanceof String || value instanceof ObjectObject) {
				List<ObjectObject> hlp = new ArrayList<ObjectObject>();
				hlp.add((ObjectObject) value);
				this._fieldData = hlp;
			} else {
				this._fieldData = (List<ObjectObject>) value;
			}
			return this._fieldData;

		}

		return null;
	}

	/**
	 * sets the field data binding
	 * 
	 * @param fieldType
	 *            Object containing the field data
	 */

	public void setFieldData(final List<ObjectObject> fieldData) {
		this._fieldData = fieldData;
		this._fieldDataSet = true;
	}

	/**
	 * sets the field data binding
	 * 
	 * @param fieldType
	 *            Object containing the field data
	 */

	@SuppressWarnings("unchecked")
	public void setFieldData(final Object fieldData) {

		this._fieldData = (List<ObjectObject>) fieldData;

		this._fieldDataSet = true;
	}

	/**
	 * sets the context variable name
	 * 
	 * @param fieldType
	 *            the name of the context variable
	 */

	public void setContextVar(final String contextVarName) {
		this._contextVar = contextVarName;
	}

	/**
	 * gets the context variable name
	 * 
	 * @return String the name of the context variable
	 */
	public String getContextVar() {

		if (this._contextVar != null) {
			return this._contextVar;
		}

		ValueBinding vb = getValueBinding(PROPERTY_CONTEXTVAR);
		if (vb != null) {
			this._contextVar = (String) vb.getValue(getFacesContext());
			return this._contextVar;
		}

		return null;
	}

	/**
	 * @see javax.faces.component#restoreState(FacesContext fc, Object obj)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void restoreState(FacesContext fc, Object obj) {

		Object[] arrayOfObject = (Object[]) obj;
		super.restoreState(fc, arrayOfObject[0]);
		this._variableName = (String) arrayOfObject[1];
		this._fieldData = (List<ObjectObject>) arrayOfObject[2];
		this._generated = (Boolean) arrayOfObject[3];
		this._fieldDataSet = (Boolean) arrayOfObject[4];
	}

	/**
	 * @see javax.faces.component#saveState(FacesContext fc)
	 */
	@Override
	public Object saveState(FacesContext fc) {

		Object[] arrayOfObject = new Object[5];
		arrayOfObject[0] = super.saveState(fc);
		arrayOfObject[1] = this._variableName;
		arrayOfObject[2] = this._fieldData;
		arrayOfObject[3] = this._generated;
		arrayOfObject[4] = this._fieldDataSet;
		return arrayOfObject;
	}

	/**
	 * injects the defined Custom Controls
	 * 
	 * @param fc
	 *            FacesContext
	 * @throws InterpretException
	 */
	@SuppressWarnings("unchecked")
	private void injectCustomControls(FacesContext fc) {

		List<ObjectObject> dataList = getFieldData();
		if (dataList == null)
			return;

		String varName = getVariableName();
		String contextVar = getContextVar();

		try {

			for (ObjectObject listElem : dataList) {
				try {

					// get the name of the CC
					String ccName = listElem.get(varName).stringValue();

					// create new CC
					UIIncludeComposite cmpCC = new UIIncludeComposite();
					cmpCC.setPageName(CC_PREFIX + ccName + CC_SUFFIX);
					cmpCC.setId(CC_ID_PREFIX + UUID.randomUUID());

					// add it as new children
					List<UIComponent> children = this.getChildren();
					children.add(cmpCC);

					// copy values to CC
					DataContextImpl dc = new DataContextImpl();
					dc.setVar(contextVar);
					dc.setValue(listElem);
					cmpCC.addDataContext(dc);

					// build the CC
					FacesComponent fCmp = cmpCC;
					fCmp.initBeforeContents(fc);
					fCmp.buildContents(fc, this.getBuilder());
					fCmp.initAfterContents(fc);

				} catch (InterpretException e) {
					e.printStackTrace();
				}
			}
		} catch (NullPointerException ne) { // catch if Custom Control does
			ne.printStackTrace();
		}

	}

	/**
	 * rebuilds the attached components
	 * 
	 * @param fc
	 *            FacesContext instance
	 */
	public void rebuild(FacesContext fc) {
		this.setGenerated(false);

		if (this.getChildCount() > 0)
			this.getChildren().clear();

		// this.clearFieldData();
		this.injectCustomControls(fc);
	}

}
