const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const bcrypt = require('bcrypt');
const postsRoutes = require('./rutas/post');
const db=require('./rutas/db')
const app = express();
const os = require('os');
const port = 3001; // Puedes cambiar el puerto si es necesario
const etiquetasRoutes = require('./rutas/etiquetas'); 
const multer = require('multer');
const path = require('path');
const fs = require('fs');

// Crear la carpeta 'uploads' si no existe
const uploadDir = path.join(__dirname, 'uploads');
if (!fs.existsSync(uploadDir)) {
    fs.mkdirSync(uploadDir);
}

// Configuración de almacenamiento con multer
const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, uploadDir); // Carpeta donde se guardarán las imágenes
    },
    filename: (req, file, cb) => {
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
        cb(null, uniqueSuffix + path.extname(file.originalname)); // Nombre único para la imagen
    }
});

const upload = multer({ storage: storage });

// Endpoint para subir la imagen
// Endpoint para subir la imagen y actualizar el perfil del usuario
app.post('/upload', upload.single('profileImage'), (req, res) => {
    if (!req.file) {
        return res.status(400).json({ message: 'No se ha subido ningún archivo' });
    }

    const imageUrl = `${req.protocol}://${req.get('host')}/uploads/${req.file.filename}`;
    const userId = req.body.userId; // Asume que envías el ID del usuario desde la app

    // Actualizar la URL de la imagen en la base de datos del usuario
    const sql = 'UPDATE users SET profile_image = ? WHERE id = ?';
    db.query(sql, [imageUrl, userId], (err, result) => {
        if (err) {
            console.error('Error al actualizar la imagen del usuario:', err);
            return res.status(500).json({ error: 'Error al actualizar la imagen del usuario.' });
        }

        res.json({ imageUrl, message: 'Imagen subida y perfil actualizado correctamente' });
    });
});


// Servir imágenes estáticas desde el directorio 'uploads'
app.use('/uploads', express.static(uploadDir));
// Configuración de middleware
app.use(cors());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use('/posts', postsRoutes); // Ruta base para los post
app.use('/api', etiquetasRoutes);
// Ruta para manejar el registro de usuarios
app.post('/register', async (req, res) => {
    console.log('Datos recibidos:', req.body); // Agrega esta línea para ver los datos recibidos

    const { name, lastName, email, phone, password } = req.body;

    // Verificación de campos vacíos
    let missingFields = [];
    if (!name) missingFields.push('name');
    if (!lastName) missingFields.push('lastName');
    if (!email) missingFields.push('email');
    if (!phone) missingFields.push('phone');
    if (!password) missingFields.push('password');

    if (missingFields.length > 0) {
        return res.status(400).send(`Faltan los siguientes campos: ${missingFields.join(', ')}`);
    }

    try {
        // Encriptar la contraseña con bcrypt
        const saltRounds = 10;
        const hashedPassword = await bcrypt.hash(password, saltRounds);
        console.log('Contraseña encriptada:', hashedPassword); // Verifica la contraseña encriptada

        // Consulta SQL para insertar el usuario
        const sql = 'INSERT INTO users (name, last_name, email, phone, password) VALUES (?, ?, ?, ?, ?)';
        db.query(sql, [name, lastName, email, phone, hashedPassword], (err, result) => {
            if (err) {
                console.error('Error al registrar el usuario:', err); // Error detallado
                return res.status(500).send('Error al registrar el usuario');
            }
            console.log('Usuario registrado exitosamente:', result); // Registro exitoso
            res.send('Usuario registrado exitosamente');
        });
    } catch (err) {
        console.error('Error en el proceso de registro:', err); // Error en el proceso
        res.status(500).send('Error en el proceso de registro');
    }
});

app.post('/login', async (req, res) => {
    console.log('Datos recibidos:', req.body);  // Verifica que los datos lleguen correctamente
    const { email, password } = req.body;

    if (!email || !password) {
        return res.status(400).json({ error: 'Por favor, ingrese el email y la contraseña.' });
    }

    try {
        // Consulta SQL para encontrar al usuario por su email
        const sql = 'SELECT * FROM users WHERE email = ?';
        db.query(sql, [email], async (err, result) => {
            if (err) {
                console.error('Error al buscar el usuario:', err);
                return res.status(500).json({ error: 'Error del servidor.' });
            }

            // Verificar si el usuario existe
            if (result.length === 0) {
                return res.status(404).json({ error: 'El usuario no existe.' });
            }

            const user = result[0];

            // Comparar la contraseña ingresada con la almacenada (hash)
            console.log('Contraseña ingresada:', password);
            console.log('Hash almacenado:', user.password);

            bcrypt.compare(password, user.password, (err, isMatch) => {
                if (err) {
                    console.error('Error en la comparación de contraseñas:', err);
                    return res.status(500).json({ error: 'Error al comparar contraseñas.' });
                }

                if (!isMatch) {
                    console.log('Contraseña incorrecta.');
                    return res.status(401).json({ error: 'Contraseña incorrecta.' });
                }

                // Si la contraseña es correcta, generar un token o responder con éxito
                const token = 'some-generated-token';  // Aquí puedes generar un token JWT o similar
                console.log('Contraseña correcta. Inicio de sesión exitoso.');
                
                // Enviar respuesta JSON con el token
                return res.status(200).json({
                    message: `Inicio de sesión exitoso.${token} ${user.id}`,
                    token: token , // Este token debe coincidir con el campo en tu LoginResponse
                    userId: user.id // Asegúrate de incluir el ID del usuario aquí
                });
            });
        });
    } catch (err) {
        console.error('Error en el proceso de inicio de sesión:', err);
        return res.status(500).json({ error: 'Error en el servidor.' });
    }
});

app.get('/ping', (req, res) => {
    res.status(200).send('El servidor está funcionando correctamente.');
});

app.get('/', (req, res) => {
    res.status(200).send('Servidor Node.js funcionando correctamente.');
});

// Endpoint para obtener el perfil de un usuario por su ID
app.get('/user/:id', (req, res) => {
    const userId = req.params.id;

    // Consulta SQL para obtener el usuario por ID, incluyendo profile_image
    const sql = 'SELECT id, name, last_name AS lastName, email, phone, created_at AS createdAt, profile_image AS profileImage FROM users WHERE id = ?';
    db.query(sql, [userId], (err, result) => {
        if (err) {
            console.error('Error al obtener el perfil del usuario:', err);
            return res.status(500).json({ error: 'Error del servidor.' });
        }

        // Verificar si el usuario existe
        if (result.length === 0) {
            return res.status(404).json({ error: 'Usuario no encontrado.' });
        }

        const user = result[0];

        // Enviar la respuesta con los datos del usuario, incluyendo profileImage
        res.status(200).json({
            id: user.id,
            name: user.name,
            lastName: user.lastName,
            email: user.email,
            phone: user.phone,
            createdAt: user.createdAt,
            profileImage: user.profileImage // Añadir la URL de la imagen de perfil
        });
    });
});


// Ruta para manejar la actualización de información del usuario
app.put('/user/:id', async (req, res) => {
    const userId = req.params.id;
    const { name, lastName, email, phone } = req.body;

    // Verificación de campos vacíos
    if (!name || !lastName || !email) {
        return res.status(400).json({ error: 'Por favor, complete todos los campos requeridos.' });
    }

    try {
        // Consulta SQL para actualizar el usuario
        const sql = 'UPDATE users SET name = ?, last_name = ?, email = ?, phone = ? WHERE id = ?';
        db.query(sql, [name, lastName, email, phone, userId], (err, result) => {
            if (err) {
                console.error('Error al actualizar el usuario:', err);
                return res.status(500).json({ error: 'Error al actualizar el usuario.' });
            }

            // Verificar si se actualizó alguna fila
            if (result.affectedRows === 0) {
                return res.status(404).json({ error: 'Usuario no encontrado.' });
            }

            console.log('Usuario actualizado correctamente:', result);
            res.status(200).json({ message: 'Usuario actualizado correctamente.' });
        });
    } catch (err) {
        console.error('Error en el proceso de actualización:', err);
        res.status(500).json({ error: 'Error en el servidor.' });
    }
});

app.post('/posts/:postId/etiquetas', (req, res) => {
    const postId = req.params.postId;
    const { etiquetas } = req.body; // Obtener las etiquetas del cuerpo de la solicitud

    if (!postId || !etiquetas || !Array.isArray(etiquetas)) {
        return res.status(400).json({ error: 'Datos inválidos' });
    }

    // Suponiendo que etiquetas contiene una lista de IDs de etiquetas
    etiquetas.forEach((etiquetaId) => {
        // Inserta cada etiqueta en la tabla
        const query = 'INSERT INTO posts_etiquetas (post_id, etiqueta_id) VALUES (?, ?)';
        db.query(query, [postId, etiquetaId], (err, result) => {
            if (err) {
                console.error('Error al insertar etiqueta:', err);
                return res.status(500).json({ error: 'Error al insertar etiqueta' });
            }
        });
    });

    res.status(200).json({ message: 'Etiquetas asignadas exitosamente' });
});

app.get('/usuario/:id/imagen_perfil', (req, res) => {
    const userId = req.params.id;

    // Consulta SQL para obtener la URL de la imagen de perfil
    const sql = 'SELECT profile_image FROM users WHERE id = ?';
    db.query(sql, [userId], (err, result) => {
        if (err) {
            console.error('Error al obtener la imagen de perfil:', err);
            return res.status(500).json({ error: 'Error en el servidor' });
        }

        // Verificar si se encontró el usuario y tiene una imagen
        if (result.length > 0 && result[0].profile_image) {
            res.status(200).json({ profileImageUrl: result[0].profile_image }); // Devolver solo la URL de la imagen
        } else {
            res.status(404).json({ error: 'Imagen de perfil no encontrada' });
        }
    });
});

app.get('/comments/:postId', (req, res) => {
    const postId = req.params.postId;
    const sqlSelectComments = `
        SELECT comments.id AS commentId, users.name AS userName, comments.comment_text AS text, 
               users.profile_image AS userImageUrl, comments.created_at AS createdAt
        FROM comments 
        JOIN users ON comments.user_id = users.id
        WHERE comments.post_id = ?
    `;
    db.query(sqlSelectComments, [postId], (err, result) => {
        if (err) {
            return res.status(500).json({ error: err.message });
        }
        res.status(200).json(result);
    });
});

// Endpoint para agregar un comentario a un post específico
app.post('/posts/:postId/comments', (req, res) => {
    
    const { postId } = req.params; // ID del post
    const { user_id, comment_text } = req.body; // Datos del comentario
    console.log("Datos recibidos:", { postId, user_id, comment_text });

    // Validar los datos
    if (!user_id || !comment_text) {
        return res.status(400).json({ error: 'user_id y comment_text son requeridos' });
    }

    // Consulta SQL para insertar el comentario en la tabla
    const query = `
        INSERT INTO comments (post_id, user_id, comment_text) 
        VALUES (?, ?, ?)
    `;

    // Ejecutar la consulta
    db.query(query, [postId, user_id, comment_text], (err, result) => {
        if (err) {
            console.error('Error al insertar el comentario:', err);
            return res.status(500).json({ error: 'Error al insertar el comentario' });
        }
        res.status(201).json({
            message: 'Comentario agregado exitosamente',
            comment: {
                id: result.insertId,
                post_id: postId,
                user_id,
                comment_text,
                created_at: new Date() // Se generará automáticamente si la columna está configurada como `CURRENT_TIMESTAMP`
            }
        });
    });
});

app.get('/get-ip', (req, res) => {
    const networkInterfaces = os.networkInterfaces();
    let serverIp = 'localhost'; // Valor predeterminado

    for (const iface of Object.values(networkInterfaces)) {
        for (const details of iface) {
            if (details.family === 'IPv4' && !details.internal) {
                serverIp = details.address;
                break;
            }
        }
    }

    res.json({ ip: serverIp });
});

app.listen(port, '0.0.0.0', () => {
    console.log(`Servidor corriendo en http://0.0.0.0:${port}`);
});