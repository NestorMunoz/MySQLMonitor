package model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.scene.control.Alert;

public class ConexionSQL {
    
    private static String url,user,pass,db;
    private Connection myConn = null;
    private Statement myStm = null;
    private PreparedStatement myPstm = null;
    private ResultSet myRs = null;
    
    public ConexionSQL(String url,String user,String pass){
        ConexionSQL.url=url;
        ConexionSQL.user=user;
        ConexionSQL.pass=pass;
        ConexionSQL.db=db;
    }

    public static String getDb() {return db;}
    public static void setDb(String db) {ConexionSQL.db = db;}
    
    public ConexionSQL(){
        super();
    }
    
    public Connection createConection(){
        try {
                myConn = DriverManager.getConnection(url,user,pass);
            } catch (SQLException ex) {
                alertsErrors(ex,"Error de Conexión.");
            }finally{
            return myConn;
        }
    }
    
    public ResultSet executeSelect(String sql){   
        try {
            myPstm = myConn.prepareStatement(sql);
            myRs = myPstm.executeQuery(sql);
        } catch (SQLException ex) {
            alertsErrors(ex,"Ingrese sentencia SQL valida.");
        }finally{
            return myRs;
        }
    }
    
    public int executeUpdate(String sql){
        try {
            myPstm = myConn.prepareStatement(sql);
            return myPstm.executeUpdate(sql);
        } catch (SQLException ex) {
            alertsErrors(ex,"Ingrese sentencia SQL valida.");
        }finally{
            return 0;
        }
    }
    
    public ArrayList getDatabases(){
        ArrayList list = new ArrayList();
        try {
            myRs = myConn.getMetaData().getCatalogs();
            while(myRs.next()){
                list.add(myRs.getString(1));
            }
        } catch (SQLException ex) {
            alertsErrors(ex,"Error al obtener bases de Datos");
        }finally{
            try {
                myRs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return list;
        }
    }
    
    public void getColumns(){
        try {
            DatabaseMetaData meta = myConn.getMetaData();
            myRs = meta.getColumns(null, null, "%", null);
            
        } catch (SQLException e) {
        }
    }
    
    public ArrayList getTables(){
        ArrayList list = new ArrayList();
        DatabaseMetaData dbMtDt;
        try {
            dbMtDt = myConn.getMetaData();
            myRs = dbMtDt.getTables(null, null, "%", null);
            while(myRs.next()){
                list.add(myRs.getString(3));
            }
        } catch (SQLException ex) {
            alertsErrors(ex,"Error al obtener tablas");
        }finally{
            return list;
        }
    }
    
    private void alertsErrors(Exception e, String error){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(error);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
    
    public void safeClose(){
        try { if (myRs != null) myRs.close(); } catch (Exception e) {}
        try { if (myStm != null) myStm.close(); } catch (Exception e) {}
        try { if (myConn != null) myConn.close(); } catch (Exception e) {}
    }
}
