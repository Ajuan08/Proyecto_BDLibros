package es.ajuan.libreriaderby;

import es.ajuan.libreriaderby.entities.Autor;
import es.ajuan.libreriaderby.entities.Libros;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javax.persistence.Query;
import javax.persistence.RollbackException;

public class SecondaryController {
    private Libros libros;
    private static final String CARPETA_FOTOS = "Fotos";
    
    private boolean nuevoLibro;
    @FXML
    private TextField textNombre;
    private TextField textApellidos;
    @FXML
    private TextField textPrecio;
    @FXML
    private TextField textISBN;
    @FXML
    private CheckBox checkBoxFavorito;
    @FXML
    private DatePicker datePickerFechaEdicion;
    @FXML
    private ComboBox<Autor> ComboBoxAutor;
    @FXML
    private TextField textFieldGenero;
    private TextField textFieldAutor_Nombre;
    @FXML
    private BorderPane rootSecondary;
    @FXML
    private ImageView imageViewFoto;
   
    public void initialize(URL url, ResourceBundle rb){
    }
    
    public void setLibros(Libros libros, boolean nuevoLibro) {
        App.em.getTransaction().begin();
        if (!nuevoLibro){
            this.libros = App.em.find(Libros.class, libros.getId());
        } else {
            this.libros = libros;
        }
        this.nuevoLibro = nuevoLibro;
        
        mostrarDatos();
    }
    
     private void mostrarDatos(){
        textNombre.setText(libros.getNombre());
        textFieldGenero.setText(libros.getGenero());
        
        if (libros.getPrecio() != null) {
            textPrecio.setText(String.valueOf(libros.getPrecio()));
        }
        if (libros.getIsbn() != null) {
            textISBN.setText(String.valueOf(libros.getIsbn()));
        }
        
        if (libros.getFechaEdicion() != null) {
            Date date = libros.getFechaEdicion();
            Instant instant = date.toInstant();
            ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
            LocalDate localDate = zdt.toLocalDate();
            datePickerFechaEdicion.setValue(localDate);
        }
        
        if (libros.getFavoritos() != null) {
            checkBoxFavorito.setSelected(libros.getFavoritos());
        }
        
        Query queryAutorFindAll = App.em.createNamedQuery("Autor.findAll");
        List<Autor> listAutor = queryAutorFindAll.getResultList();  
        
        ComboBoxAutor.setItems(FXCollections.observableList(listAutor));
       System.out.println(libros.getAutor());
        if (libros.getAutor()!= null){
            ComboBoxAutor.setValue(libros.getAutor());       
        }
        
        ComboBoxAutor.setCellFactory((ListView<Autor> a) -> new ListCell<Autor>() {
        @Override
            protected void updateItem(Autor autor, boolean empty){
                super.updateItem(autor, empty);
                if(autor == null || empty){
                    setText("");
                }else {
                    setText(autor.getNombre());
                }
            }
        });
        ComboBoxAutor.setConverter(new StringConverter<Autor>(){
         @Override
            public String toString (Autor autor){
                if(autor == null){
                    return null;
                }else {
                    return autor.getNombre();
                }
            }
            @Override
            public Autor fromString(String userId) {
                return null;
            }
        });
        
        if(libros.getFoto() != null){
            String imageFileName = libros.getFoto();
            File file = new File(CARPETA_FOTOS + "/" + imageFileName);
            if (file.exists()){
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No se encuentra la imágen");
                alert.showAndWait();
            }
        }
    }
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void onActionButtonExaminar(ActionEvent event) {
        File carpertaFotos = new File(CARPETA_FOTOS);
        if(!carpertaFotos.exists()){
            carpertaFotos.mkdir();
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes (jpg, JPG, png)", "*.png"),
                new FileChooser.ExtensionFilter("Todos los archivos", ".")
        );
        
        File file = fileChooser.showOpenDialog(rootSecondary.getScene().getWindow());
        if (file != null){
            try{
                Files.copy(file.toPath(), new File(CARPETA_FOTOS + "/" + file.getName()).toPath());
                libros.setFoto(file.getName());
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            } catch (FileAlreadyExistsException ex){
                Alert alert = new Alert(Alert.AlertType.WARNING,"Nombre de archivo duplicado");
                alert.showAndWait();
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING,"No se ha podido guardar la imagen");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void onActionButtonBorrarFoto(ActionEvent event) {
         Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar supresion ed imagen");
        alert.setHeaderText("¿Desea SUPRIMIR el archivo asociado a la imagen, \n"
                + "quitar la foto pero MANTENER el archivo, \no CANCELAR la operación?");
        alert.setContentText("Elija a opción deseada");
        
        ButtonType buttonTypeEiminar = new ButtonType("Suprimir");
        ButtonType buttonTypeMantener = new ButtonType("Mantener");
        ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(buttonTypeEiminar, buttonTypeMantener, buttonTypeCancel);
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if(result.get() == buttonTypeEiminar) {
            String imageFileName = libros.getFoto();
            File file = new File(CARPETA_FOTOS + "/" + imageFileName);
            if(file.exists()){
                file.delete();
            }
            libros.setFoto(null);
            imageViewFoto.setImage(null);
        } else if (result.get() == buttonTypeMantener){
            libros.setFoto(null);
            imageViewFoto.setImage(null);
        }
    }

    
    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        boolean errorFormato= false;
        
        libros.setNombre(textNombre.getText());
        libros.setAutor(ComboBoxAutor.getValue());
        libros.setIsbn(textISBN.getText());
        libros.setGenero(textFieldGenero.getText());
        
        if (!textPrecio.getText().isEmpty()) {
            try {
                libros.setPrecio(BigDecimal.valueOf(Double.valueOf(textPrecio.getText()).doubleValue()));
            } catch (NumberFormatException ex) {
                errorFormato= true;
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Precio no válido");
                alert.showAndWait();
                textPrecio.requestFocus();
                
            }}
         if (datePickerFechaEdicion.getValue() != null){
            LocalDate localDate = datePickerFechaEdicion.getValue();
            ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            Date date = Date.from(instant);
            libros.setFechaEdicion(date);
        } else {
            libros.setFechaEdicion(null);
        }
        
        
        if (!errorFormato) {
            try {
                if (libros.getId() == null){
                    System.out.println("Guardando nueva persona en BD");
                    App.em.persist(libros);
                } else {
                    System.out.println("Actualizando persona en BD");
                    App.em.merge(libros);
                }
                App.em.getTransaction().commit();
                
                App.setRoot("primary");
            } catch (RollbackException ex){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("No se han podido guardar los cambios."
                        + "Compruebe que los datos cumplen los requisitos");
                alert.setContentText(ex.getLocalizedMessage());
                alert.showAndWait();
            } catch (IOException ex) {
                Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    @FXML
    private void onActionButtonCancelar(ActionEvent event) {
        App.em.getTransaction().rollback();
        try {
            App.setRoot("primary");
            
        } catch (IOException ex) {
            Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}