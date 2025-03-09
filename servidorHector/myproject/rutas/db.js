const mysql = require('mysql2');

// Crear una conexión o un pool de conexiones a MySQL
const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'Pineda1345',
    database: 'Android'
});

// Conectar a la base de datos MySQL
db.connect(err => {
    if (err) {
        console.error('Error al conectarse a la base de datos:', err.message);
        return;
    }
    console.log('Conectado a la base de datos MySQL');
});

module.exports = db;
