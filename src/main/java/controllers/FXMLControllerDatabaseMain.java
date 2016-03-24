package controllers;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.ConexionSQL;

public class FXMLControllerDatabaseMain{
    
    private ConexionSQL myConnSQL;
    private ObservableList<ObservableList> data;
    private ObservableList tablesList;
    private ObservableList dbList;
    
    @FXML private TableView sqlTable = new TableView();
    @FXML private ListView dbListView = new ListView();
    @FXML private ListView tblListView = new ListView();
    @FXML private TextField sqlField;
    @FXML private Label dbInUse;
    
    public FXMLControllerDatabaseMain(){
        myConnSQL = new ConexionSQL();
        myConnSQL.createConection();
    }
 
    public void initialize(){
        //------------SHOW DATABASES------------
        dbList = FXCollections.observableArrayList(myConnSQL.getDatabases());
        dbListView.setItems(dbList);
        //------------SHOW TABLES------------
        showTables();
    }
    
    @FXML
    private void handleButtonAction() {
        String sql = sqlField.getText();
        
        if (comparar(sql,"SELECT") || comparar(sql,"select")) {
            //------------SELECT------------
            construirTabla(sqlTable, myConnSQL.executeSelect(sql)); 
        }else if(comparar(sql,"use") || comparar(sql,"USE")){
            String db = sql;
            db = db.substring(4);
            ConexionSQL.setDb(db);
            myConnSQL.safeClose();
            try {
                myConnSQL.createConection().setCatalog(db);
            } catch (SQLException ex) {
                ex.getMessage();
            }
            showTables();
        }else{
            //------------INSERT-UPDATE-DELETE-CREATE-DROP------------
            myConnSQL.executeUpdate(sql);
        }
        sqlField.clear();
    }
    
    @FXML
    private void disconnectButtonEvent(ActionEvent evt){
        try {
            myConnSQL.safeClose();
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/FXMLLogin.fxml"));
            Scene scene = new Scene(root);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
            ((Node)(evt.getSource())).getScene().getWindow().hide();
        } catch (IOException ex) {
            System.out.println(ex+"\n");
        }
    }
    
    public void showTables(){
        if (ConexionSQL.getDb() != null) {
            tablesList = FXCollections.observableArrayList(myConnSQL.getTables());
            tblListView.setItems(tablesList);
            dbInUse.setText(ConexionSQL.getDb());
        }
    }
    
    public Boolean comparar(String cad,String expr){
        Pattern pattern = Pattern.compile(expr);
        Matcher match = pattern.matcher(cad);
        return match.find();
    }
    
    public void construirTabla(TableView tableView, ResultSet rs){
        tableView.getColumns().clear();
        data = FXCollections.observableArrayList();
        try {
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){  
                final int j = i;          
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));  
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){            
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                 
                        return new SimpleStringProperty(param.getValue().get(j).toString());              
                    }            
                });  
             tableView.getColumns().addAll(col);  
            }
            while(rs.next()){  
                ObservableList<String> row = FXCollections.observableArrayList();  
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){  
                    row.add(rs.getString(i));  
                }  
                data.add(row);  
            }
           tableView.setItems(data);  
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}