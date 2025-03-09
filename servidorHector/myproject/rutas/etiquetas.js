const express=require('express')
const router = express.Router();
const db = require('../rutas/db'); 

// Obtener todas las etiquetas
router.get('/etiquetas', (req, res) => {
    const sqlSelect = "SELECT * FROM etiquetas";
    db.query(sqlSelect, (err, result) => {
        if (err) {
            return res.status(500).json({ error: err.message });
        }
        res.status(200).json(result);
    });
});

// Crear una nueva etiqueta
router.post('/etiquetas', (req, res) => {
    const { nombre_etiqueta } = req.body;

    if (!nombre_etiqueta) {
        return res.status(400).json({ message: 'El nombre de la etiqueta es obligatorio' });
    }

    const sqlInsert = "INSERT INTO etiquetas (nombre_etiqueta) VALUES (?)";
    db.query(sqlInsert, [nombre_etiqueta], (err, result) => {
        if (err) {
            return res.status(500).json({ error: err.message });
        }
        console.log(`Etiqueta ${nombre_etiqueta} creada con exito`)
        res.status(201).json({ message: 'Etiqueta creada exitosamente', id: result.insertId });
    });
});

// Actualizar una etiqueta
router.put('/etiquetas/:id', (req, res) => {
    const { id } = req.params;
    const { nombre_etiqueta } = req.body;

    if (!nombre_etiqueta) {
        return res.status(400).json({ message: 'El nombre de la etiqueta es obligatorio' });
    }

    const sqlUpdate = "UPDATE etiquetas SET nombre_etiqueta = ? WHERE id = ?";
    db.query(sqlUpdate, [nombre_etiqueta, id], (err, result) => {
        if (err) {
            return res.status(500).json({ error: err.message });
        }
        res.status(200).json({ message: 'Etiqueta actualizada exitosamente' });
    });
});

// Eliminar una etiqueta
router.delete('/etiquetas/:id', (req, res) => {
    const { id } = req.params;

    const sqlDelete = "DELETE FROM etiquetas WHERE id = ?";
    db.query(sqlDelete, [id], (err, result) => {
        if (err) {
            return res.status(500).json({ error: err.message });
        }
        res.status(200).json({ message: 'Etiqueta eliminada exitosamente' });
    });
});

router.get('/posts/:postId/etiquetas', (req, res) => {
    const postId = req.params.postId;
    const sql = `
        SELECT e.id, e.nombre_etiqueta
        FROM etiquetas e
        JOIN posts_etiquetas pe ON e.id = pe.etiqueta_id
        WHERE pe.post_id = ?;
    `;

    db.query(sql, [postId], (err, result) => {
        if (err) {
            return res.status(500).json({ error: err.message });
        }
        res.status(200).json(result);
    });
});





module.exports = router;
