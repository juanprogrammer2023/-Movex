const express = require('express');
const router = express.Router();
const db = require('../rutas/db'); // Conexión a la base de datos

// Crear un post
router.post('/crear', (req, res) => {
    const { titulo, contenido, usuario_id } = req.body;
    console.log(titulo,contenido,usuario_id)

    const sqlInsert = "INSERT INTO posts (titulo, contenido, usuario_id) VALUES (?, ?, ?)";
    db.query(sqlInsert, [titulo, contenido, usuario_id], (err, result) => {
        if (err) {
            return res.status(500).json({ error: err.message });
        }
        res.status(201).json({ message: 'Post creado exitosamente' });
    });
});

// Obtener todos los posts
router.get('/listar', (req, res) => {
    const sqlSelect = `
        SELECT 
            posts.id, 
            posts.titulo, 
            posts.contenido, 
            posts.fecha_publicacion,  -- Incluye la fecha de publicación
            users.name, 
            users.last_name,
            users.profile_image  -- Incluye la imagen de perfil
        FROM posts 
        JOIN users ON posts.usuario_id = users.id;
    `;
    
    db.query(sqlSelect, (err, result) => {
        if (err) {
            return res.status(500).json({ error: err.message });
        }
        res.status(200).json(result);
    });
});


module.exports = router;
