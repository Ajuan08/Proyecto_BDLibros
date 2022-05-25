package es.ajuan.libreriaderby;

import es.ajuan.libreriaderby.entities.Libros;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.persistence.Query;

public class PrimaryController implements Initializable{
    
    private Libros librosSeleccionados;
    @FXML
    private TableView<Libros> tableViewLibros;
    @FXML
    private TableColumn<Libros, String> columnNombre;
    @FXML
    private TableColumn<Libros, String> columnApellidos;
    @FXML
    private TableColumn<Libros, String> columnISBN;
    @FXML
    private TableColumn<Libros, String> columnGenero;
    @FXML
    private TableColumn<Libros, String> columnPrecio;
    @FXML
    private TextArea textFieldNombre;
    @FXML
    private TextArea textFieldApellidos;
    @FXML
    private TextField textFieldBuscar;
    @FXML
    private CheckBox checkCoincide;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        columnGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        columnApellidos.setCellValueFactory(
                cellData -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    if (cellData.getValue().getAutor() != null) {
                       String apellidosAutor = cellData.getValue().getAutor().getApellidos();
                        property.setValue(apellidosAutor);
                    }
                    return property;
                });
        tableViewLibros.getSelectionModel().selectedItemProperty().addListener(
            (observable,oldValue, newValue) -> {
                librosSeleccionados = newValue;
                if (librosSeleccionados != null){
                    textFieldNombre.setText(librosSeleccionados.getNombre());
                    textFieldApellidos.setText(librosSeleccionados.getAutor().getApellidos());
                } else {
                    textFieldNombre.setText("");
                    textFieldApellidos.setText("");
                }
            });
        cargarTodosLibros();
    }
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
    
    private void cargarTodosLibros() {
    Query queryLibrosFindAll = App.em.createNamedQuery("Libros.findAll");
    List<Libros> listLibros = queryLibrosFindAll.getResultList();
    tableViewLibros.setItems(FXCollections.observableArrayList(listLibros));
    }

    @FXML
    private void onActionNuevo(ActionEvent event) {
        try {
        App.setRoot("secondary");
            SecondaryController secondaryController = (SecondaryController)App.fxmlLoader.getController();
            librosSeleccionados = new Libros();
            secondaryController.setLibros(librosSeleccionados, true);
        } catch (IOException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @FXML
    private void onActionEditar(ActionEvent event) {
        if (librosSeleccionados != null){
          try {
        App.setRoot("secondary");
            SecondaryController secondaryController = (SecondaryController)App.fxmlLoader.getController();
            secondaryController.setLibros(librosSeleccionados, false);
        } catch (IOException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }  
        }else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ALERTA");
            alert.setHeaderText("Debes seleccionar un registro");
            alert.showAndWait();
        }
        
    }

    @FXML
    private void onActionBorrar(ActionEvent event) {
        if (librosSeleccionados != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar");
            alert.setHeaderText("¿Estas seguro que quieres borrarlo?");
            alert.setContentText(librosSeleccionados.getNombre() + " "
                        + librosSeleccionados.getAutor());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                App.em.getTransaction().begin();
                App.em.remove(librosSeleccionados);
                App.em.getTransaction().commit();
                tableViewLibros.getItems().remove(librosSeleccionados);
                tableViewLibros.getFocusModel().focus(null);
                tableViewLibros.requestFocus();
            } else {
                int numFilaSeleccionada = tableViewLibros.getSelectionModel().getSelectedIndex();
                tableViewLibros.getItems().set(numFilaSeleccionada, librosSeleccionados);
                TablePosition pos = new TablePosition(tableViewLibros, numFilaSeleccionada, null);
                tableViewLibros.getFocusModel().focus(pos);
                tableViewLibros.requestFocus();
            }
            
        } 
            
    }

    @FXML
    private void onActionGuardar(ActionEvent event) {
        if (librosSeleccionados != null) {
            librosSeleccionados.setNombre(textFieldNombre.getText());
            librosSeleccionados.getAutor().setApellidos(textFieldApellidos.getText());
            App.em.getTransaction().begin();
            App.em.merge(librosSeleccionados);
            App.em.getTransaction().commit();
            
            int numFilaSeleccionada = tableViewLibros.getSelectionModel().getSelectedIndex();
            tableViewLibros.getItems().set(numFilaSeleccionada, librosSeleccionados);
            TablePosition pos = new TablePosition(tableViewLibros, numFilaSeleccionada, null);
            tableViewLibros.getFocusModel().focus(pos);
            tableViewLibros.requestFocus();
        }
    }

    @FXML
    private void OnActionButtonBuscar(ActionEvent event) {
        if(!textFieldBuscar.getText().isEmpty()) {
            if(checkCoincide.isSelected()) {
                Query queryLibrosFindAll = App.em.createNamedQuery("Libros.findByNombre");
                queryLibrosFindAll.setParameter("nombre", textFieldBuscar.getText());
                List<Libros> listLibros = queryLibrosFindAll.getResultList();
                tableViewLibros.setItems(FXCollections.observableArrayList(listLibros));
            } else {
                String strQuery = "SELECT * FROM Libros WHERE LOWER(nombre) LIKE ";
                strQuery += "\'%" + textFieldBuscar.getText().toLowerCase() + "%\'";
                Query queryLibrosFindLikeNombre = App.em.createNativeQuery(strQuery, Libros.class);
                
                List<Libros> listLibros = queryLibrosFindLikeNombre.getResultList();
                tableViewLibros.setItems(FXCollections.observableArrayList(listLibros));
                
            }
        } else {
            cargarTodosLibros();
        }
    }
}

   
