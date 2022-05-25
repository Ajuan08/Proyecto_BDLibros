/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ajuan.libreriaderby.entities;

import es.ajuan.libreriaderby.entities.Autor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Antonio Juan
 */
@Entity
@Table(name = "LIBROS")
@NamedQueries({
    @NamedQuery(name = "Libros.findAll", query = "SELECT l FROM Libros l"),
    @NamedQuery(name = "Libros.findById", query = "SELECT l FROM Libros l WHERE l.id = :id"),
    @NamedQuery(name = "Libros.findByNombre", query = "SELECT l FROM Libros l WHERE l.nombre = :nombre"),
    @NamedQuery(name = "Libros.findByPrecio", query = "SELECT l FROM Libros l WHERE l.precio = :precio"),
    @NamedQuery(name = "Libros.findByFoto", query = "SELECT l FROM Libros l WHERE l.foto = :foto"),
    @NamedQuery(name = "Libros.findByFechaEdicion", query = "SELECT l FROM Libros l WHERE l.fechaEdicion = :fechaEdicion"),
    @NamedQuery(name = "Libros.findByIsbn", query = "SELECT l FROM Libros l WHERE l.isbn = :isbn"),
    @NamedQuery(name = "Libros.findByFavoritos", query = "SELECT l FROM Libros l WHERE l.favoritos = :favoritos"),
    @NamedQuery(name = "Libros.findByGenero", query = "SELECT l FROM Libros l WHERE l.genero = :genero")})
public class Libros implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "NOMBRE")
    private String nombre;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PRECIO")
    private BigDecimal precio;
    @Column(name = "FOTO")
    private String foto;
    @Column(name = "FECHA_EDICION")
    @Temporal(TemporalType.DATE)
    private Date fechaEdicion;
    @Column(name = "ISBN")
    private String isbn;
    @Column(name = "FAVORITOS")
    private Boolean favoritos;
    @Basic(optional = false)
    @Column(name = "GENERO")
    private String genero;
    @JoinColumn(name = "AUTOR", referencedColumnName = "ID")
    @ManyToOne
    private Autor autor;

    public Libros() {
    }

    public Libros(Integer id) {
        this.id = id;
    }

    public Libros(Integer id, String nombre, String genero) {
        this.id = id;
        this.nombre = nombre;
        this.genero = genero;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Date getFechaEdicion() {
        return fechaEdicion;
    }

    public void setFechaEdicion(Date fechaEdicion) {
        this.fechaEdicion = fechaEdicion;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Boolean getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(Boolean favoritos) {
        this.favoritos = favoritos;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Libros)) {
            return false;
        }
        Libros other = (Libros) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.ajuan.libreriaderby.entities.Libros[ id=" + id + " ]";
    }
    
}
