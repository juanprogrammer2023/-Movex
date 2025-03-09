package com.example.myapplicationhector.network;

import com.example.myapplicationhector.Comment;

import java.util.List;

public class Post {
    private int id;  // Identificador único del post
    private String titulo;
    private String contenido;
    private int usuario_id;
    private String name;  // Nombre del usuario
    private String last_name;  // Apellido del usuario
    private String fecha_publicacion;  // Fecha de publicación
    private String profile_image;  // URL de la imagen de perfil
    private List<Comment> comments;  // Lista de comentarios

    // Constructor para crear un nuevo post con 3 parámetros (para la creación del post)
    public Post(String titulo, String contenido, int usuario_id) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.usuario_id = usuario_id;
        this.name = "";  // Puedes inicializar con un valor vacío o predeterminado
        this.last_name = "";
        this.fecha_publicacion = "";
        this.profile_image = "";  // Inicializa con un valor vacío
    }

    // Constructor para crear un post con todos los datos (para obtener posts de la API)
    public Post(int id, String titulo, String contenido, int usuario_id, String name, String last_name, String fecha_publicacion, String profile_image, List<Comment> comments) {
        this.id = id;  // Inicializa el ID del post
        this.titulo = titulo;
        this.contenido = contenido;
        this.usuario_id = usuario_id;
        this.name = name;
        this.last_name = last_name;
        this.fecha_publicacion = fecha_publicacion;
        this.profile_image = profile_image;  // Inicializa con la URL de la imagen
        this.comments = comments;  // Inicializa la lista de comentarios
    }

    // Getters y setters para todos los campos, incluyendo comentarios
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public int getUsuarioId() {
        return usuario_id;
    }

    public void setUsuarioId(int usuario_id) {
        this.usuario_id = usuario_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getFechaPublicacion() {
        return fecha_publicacion;
    }

    public void setFechaPublicacion(String fecha_publicacion) {
        this.fecha_publicacion = fecha_publicacion;
    }

    public String getProfileImage() {
        return profile_image;
    }

    public void setProfileImage(String profile_image) {
        this.profile_image = profile_image;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
