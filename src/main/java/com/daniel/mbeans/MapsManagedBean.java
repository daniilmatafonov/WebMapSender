/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.daniel.mbeans;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.primefaces.component.api.UIData;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;

import com.daniel.db.DBManager;
import com.daniel.processimage.ImageManager;
import com.vano.clientserver.NavigationData;
import com.vano.clientserver.Table;




/**
 *
 * @author Admin
 */
@ManagedBean(name = "mapBean")
@RequestScoped
public class MapsManagedBean implements Serializable {

	
	public MapsManagedBean() throws SQLException
	{
		db = new DBManager();
		naviData = new NavigationData();
		//fillListOfTables();
	}

    
    private NavigationData naviData;
    DBManager db;
    DataTable tb;
    private Table tableSelected;
    private Part file;
    ImageManager im = new ImageManager();
    private String errorString;
    private boolean objectsent=false;
    public static final String WiFiNavigatorUri = "http://wifinavigatorapp-municipalpayment.rhcloud.com/WiFiNavigatorSite/downloadPlan";
    int NetworkConnectionTimeout_ms = 500000;
    private ArrayList<Table> tables = new ArrayList<Table>();
    
    
    public void fillListOfTables() throws SQLException
    {
    	setTables(db.fillListOfTables());
    }
   
    public String nameOfTable()
    {
    	String name="";
    	name = String.valueOf(tableSelected.getTable());
    	return name;
    }
    
    

    public void serializeObjectNavigationData() throws IOException, SQLException
    {
          
            try
            {
            	//naviData.setTableName(nameOfTable());
            	NavigationData nd = db.selectDataFromDB(naviData, getFile());
            	sendNavigationData(nd);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                //Добавить логи
            }
        
       
        
    }
    
    private void sendNavigationData(NavigationData nd) throws IOException{
	    HttpParams params = new BasicHttpParams();    
	    HttpConnectionParams.setStaleCheckingEnabled(params, false);
	    HttpConnectionParams.setConnectionTimeout(params, NetworkConnectionTimeout_ms);
	    HttpConnectionParams.setSoTimeout(params, NetworkConnectionTimeout_ms);
	    DefaultHttpClient httpClient = new DefaultHttpClient(params);

    // create post method
	    HttpPost postMethod = new HttpPost(WiFiNavigatorUri);

    // create request entity
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(baos);
	    oos.writeObject(nd);
	    ByteArrayEntity req_entity = new ByteArrayEntity(baos.toByteArray());
	    req_entity.setContentType("application/octet-stream");

    // associating entity with method
	    postMethod.setEntity(req_entity);
	    httpClient.execute(postMethod);
    }
    

    
    public String uploadData() throws IOException, SQLException, SocketException, SocketTimeoutException
    {
           try
           {
               serializeObjectNavigationData();
               objectsent=true;
           }
            catch(Exception e)
            {
                errorString=e.getMessage();
                
            }
       if(objectsent)
           return "result.xhtml";
       else
    	   return "errorPage.xhtml";
    }
    

    
    

    /**
     * @return the naviData
     */
    public NavigationData getNaviData() {
        return naviData;
        
    }

    /**
     * @param naviData the naviData to set
     * @throws SQLException 
     */
    public void setNaviData(NavigationData naviData) throws SQLException {
        this.naviData = naviData;
    }

    /**
     * @return the file
     */
    public Part getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(Part file) {
        this.file = file;
    }

    /**
     * @return the errorString
     */
    public String getErrorString() {
        return errorString;
    }

    /**
     * @param errorString the errorString to set
     */
    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }




	public Table getTableSelected() {
		return tableSelected;
	}


	public void setTableSelected(Table tableSelected) {
		this.tableSelected = tableSelected;
	}

	public ArrayList<Table> getTables() {
		return tables;
	}

	public void setTables(ArrayList<Table> tables) {
		this.tables = tables;
	}



	

      
    
}
