package me.image.assembler;




import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import me.image.domain.EPAAirBean;
import me.image.domain.EPAAirDetailBean;
import me.image.domain.ServiceCheckBean;
import me.image.util.BeanUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;





/**
 * 組出回傳至手機的訊息
 * 
 * @author Ken
 * 
 * **/
public class AndroidFormAssembler {
	
	private static final String LOGGER = AndroidFormAssembler.class.getName();
	/**
	 * 
	 *  將return XML放入EPAAirBean
	 *  依XML內checkBean判斷
	 *  checkResult=true則代表結果正常
	 *  checkResult=fale則代表結果不正常
	 *   若@type CameraMEConstants.QUERY_TYPE_EPA_AIR_RE 出現手動輸入視窗
	 * 
	 * */
    public EPAAirBean assemblerXMLToEPAAirBean(String responseXML){
    	Log.i(LOGGER," Enter Method assemblerToEPAAirBean =====:");
    	EPAAirBean epaAirBean=new EPAAirBean();
    	try{
    	  DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();  
    	  InputSource inputSource=new InputSource(new ByteArrayInputStream(responseXML.getBytes("UTF-8")));
    	  Document dom = db.parse(inputSource);      
    	  dom.getDocumentElement().normalize();  
          Element root = dom.getDocumentElement();
          NodeList checkBeans = root.getElementsByTagName("checkBean");
          assemblerToServiceCheckBean(checkBeans, epaAirBean.getCheckBean());
          Log.i(LOGGER," checkBean size ["+checkBeans.getLength()+"]");
          //若checkBean為真的
          if(epaAirBean.getCheckBean().getCheckResult()){
            NodeList lists = root.getElementsByTagName("list");
            assemblerToEPAAirDetailBean(lists, epaAirBean);
            Log.i(LOGGER," checkBean size ["+lists.getLength()+"]");
            assemblerToEPAAirBean(root, epaAirBean);
          }
          
        }catch(Exception e){
    		e.printStackTrace();
    		epaAirBean.getCheckBean().setCheckResult(false);
    		epaAirBean.getCheckBean().setErrorMessage("解析回傳錯誤");
    	}
    	return epaAirBean;
    }
    /**
     * checkBean之結果
     * 
     * **/
    private void assemblerToServiceCheckBean( NodeList checkBeans,ServiceCheckBean serviceCheckBean){
    	Log.i(LOGGER," Enter Method assemblerToServiceCheckBean :");
    	if(checkBeans!=null&&checkBeans.getLength()>0){
    		Node node=checkBeans.item(0);
    	    NodeList nChilds = node.getChildNodes();  

    	    for (int j = nChilds.getLength() - 1; j >= 0; --j){  
    	      Node nodeChild = nChilds.item(j);  
    	      String nodeVluae = null;
    	      //需再取text node，若僅用nodeChild.getValue()無值
    	      NodeList nodes = nodeChild.getChildNodes();
    	      if(nodes!=null&&nodes.getLength()>0){
    	    	nodeVluae = nodes.item(0).getNodeValue(); 
    	      }
    	      BeanUtils.invokeSet(serviceCheckBean, nodeChild.getNodeName(), nodeVluae);
    	      Log.i(LOGGER,"== NodeName ["+nodeChild.getNodeName()+"] NodeValue ["+BeanUtils.invokeGet(serviceCheckBean, nodeChild.getNodeName())+"]");
  
    	    }
    	}
    	else {
    		
    		
    		
    	}
    	
    }
    
    /**
     * checkBean之結果
     * 
     * **/
    private void assemblerToEPAAirDetailBean( NodeList checkBeans,EPAAirBean epaAirBean){
    	Log.i(LOGGER," Enter Method assemblerToEPAAirDetailBean :");
    	if(checkBeans!=null&&checkBeans.getLength()>0){
    	
    	  for(int i=0;i<checkBeans.getLength();i++){	
    		Node node=checkBeans.item(i);
    	    NodeList nChilds = node.getChildNodes();  
  	        EPAAirDetailBean epaAirDetailBean=new EPAAirDetailBean();
    	    for (int j = nChilds.getLength() - 1; j >= 0; --j){  

    	      Node nodeChild = nChilds.item(j);  
    	      String nodeVluae = null;
    	      //需再取text node，若僅用nodeChild.getValue()無值
    	      NodeList nodes = nodeChild.getChildNodes();
    	      if(nodes!=null&&nodes.getLength()>0){
    	    	nodeVluae = nodes.item(0).getNodeValue(); 
    	      }
    	      //Log.i(LOGGER,"==["+i+"] NodeName ["+nodeChild.getNodeName()+"]NodeVluae ["+nodeVluae+"]");
    	      BeanUtils.invokeSet(epaAirDetailBean, nodeChild.getNodeName(), nodeVluae);
    	      Log.i(LOGGER,"== NodeName ["+nodeChild.getNodeName()+"] NodeValue ["+BeanUtils.invokeGet(epaAirDetailBean, nodeChild.getNodeName())+"]");
  
    	    }
    	    epaAirBean.getList().add(epaAirDetailBean);
    	    
    	  } 
    	}
     	
    }
    /**
     * checkBean之結果
     * 
     * **/
    private void assemblerToEPAAirBean(  Element root,EPAAirBean epaAirBean){
    	Log.i(LOGGER," Enter Method assemblerToEPAAirBean :");
    	 NodeList checkBeans=null;
    	 String nodeVluae = null;
    	 checkBeans= root.getElementsByTagName("license");
    	 if(checkBeans!=null&&checkBeans.getLength()>0){
    		  Node nodeChild=checkBeans.item(0);
    	      NodeList nodes = nodeChild.getChildNodes();
    	      if(nodes!=null&&nodes.getLength()>0){
    	    	nodeVluae = nodes.item(0).getNodeValue(); 
    	      }
    	      BeanUtils.invokeSet(epaAirBean, nodeChild.getNodeName(), nodeVluae);
    	      Log.i(LOGGER,"== NodeName ["+nodeChild.getNodeName()+"] NodeValue ["+BeanUtils.invokeGet(epaAirBean, nodeChild.getNodeName())+"]");
    	 }
    	 checkBeans= root.getElementsByTagName("brandType");
    	 nodeVluae=null;
    	 if(checkBeans!=null&&checkBeans.getLength()>0){
    		  Node nodeChild=checkBeans.item(0);
    	      NodeList nodes = nodeChild.getChildNodes();
    	      if(nodes!=null&&nodes.getLength()>0){
    	    	nodeVluae = nodes.item(0).getNodeValue(); 
    	      }
    	      BeanUtils.invokeSet(epaAirBean, nodeChild.getNodeName(), nodeVluae);
    	      Log.i(LOGGER,"== NodeName ["+nodeChild.getNodeName()+"] NodeValue ["+BeanUtils.invokeGet(epaAirBean, nodeChild.getNodeName())+"]");
    	 }
    	 checkBeans= root.getElementsByTagName("engineCapacity");
    	 nodeVluae=null;
    	 if(checkBeans!=null&&checkBeans.getLength()>0){
    		  Node nodeChild=checkBeans.item(0);
    	      NodeList nodes = nodeChild.getChildNodes();
    	      if(nodes!=null&&nodes.getLength()>0){
    	    	nodeVluae = nodes.item(0).getNodeValue(); 
    	      }
    	      BeanUtils.invokeSet(epaAirBean, nodeChild.getNodeName(), nodeVluae);
    	      Log.i(LOGGER,"== NodeName ["+nodeChild.getNodeName()+"] NodeValue ["+BeanUtils.invokeGet(epaAirBean, nodeChild.getNodeName())+"]");
    	 }
    	 checkBeans= root.getElementsByTagName("strokecycle");
    	 nodeVluae=null;
    	 if(checkBeans!=null&&checkBeans.getLength()>0){
    		  Node nodeChild=checkBeans.item(0);
    	      NodeList nodes = nodeChild.getChildNodes();
    	      if(nodes!=null&&nodes.getLength()>0){
    	    	nodeVluae = nodes.item(0).getNodeValue(); 
    	      }
    	      BeanUtils.invokeSet(epaAirBean, nodeChild.getNodeName(), nodeVluae);
    	      Log.i(LOGGER,"== NodeName ["+nodeChild.getNodeName()+"] NodeValue ["+BeanUtils.invokeGet(epaAirBean, nodeChild.getNodeName())+"]");
    	 }
    	 checkBeans= root.getElementsByTagName("birthDate");
    	 nodeVluae=null;
    	 if(checkBeans!=null&&checkBeans.getLength()>0){
    		  Node nodeChild=checkBeans.item(0);
    	      NodeList nodes = nodeChild.getChildNodes();
    	      if(nodes!=null&&nodes.getLength()>0){
    	    	nodeVluae = nodes.item(0).getNodeValue(); 
    	      }
    	      BeanUtils.invokeSet(epaAirBean, nodeChild.getNodeName(), nodeVluae);
    	      Log.i(LOGGER,"== NodeName ["+nodeChild.getNodeName()+"] NodeValue ["+BeanUtils.invokeGet(epaAirBean, nodeChild.getNodeName())+"]");
    	 }
    	 checkBeans= root.getElementsByTagName("useDate");
    	 nodeVluae=null;
    	 if(checkBeans!=null&&checkBeans.getLength()>0){
    		  Node nodeChild=checkBeans.item(0);
    	      NodeList nodes = nodeChild.getChildNodes();
    	      if(nodes!=null&&nodes.getLength()>0){
    	    	nodeVluae = nodes.item(0).getNodeValue(); 
    	      }
    	      BeanUtils.invokeSet(epaAirBean, nodeChild.getNodeName(), nodeVluae);
    	      Log.i(LOGGER,"== NodeName ["+nodeChild.getNodeName()+"] NodeValue ["+BeanUtils.invokeGet(epaAirBean, nodeChild.getNodeName())+"]");
    	 }
    	 checkBeans= root.getElementsByTagName("message");
    	 nodeVluae=null;
    	 if(checkBeans!=null&&checkBeans.getLength()>0){
    		  Node nodeChild=checkBeans.item(0);
    	      NodeList nodes = nodeChild.getChildNodes();
    	      if(nodes!=null&&nodes.getLength()>0){
    	    	nodeVluae = nodes.item(0).getNodeValue(); 
    	      }
    	      BeanUtils.invokeSet(epaAirBean, nodeChild.getNodeName(), nodeVluae);
    	      Log.i(LOGGER,"== NodeName ["+nodeChild.getNodeName()+"] NodeValue ["+BeanUtils.invokeGet(epaAirBean, nodeChild.getNodeName())+"]");
    	 }
    }
}
