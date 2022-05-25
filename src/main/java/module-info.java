module es.ajuan.libreriaderby {
    requires javafx.controls;
    requires javafx.fxml;
    
    requires java.instrument;
    requires java.persistence;
    requires java.sql;
    requires java.base;
    
    opens es.ajuan.libreriaderby.entities;
    opens es.ajuan.libreriaderby to javafx.fxml;
    exports es.ajuan.libreriaderby;
}

