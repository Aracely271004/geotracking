'use strict';
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const path = require('path');

require('dotenv').config({ path: '/opt/geotracking/.env' });

const app = express();
const PORT = process.env.PORT || 3000;

app.use(helmet({ contentSecurityPolicy: false }));
app.use(cors());
app.use(express.json());
app.use(express.static(path.join(__dirname, '../public')));

app.get('/health', (req, res) => {
  res.json({ status: 'OK', timestamp: new Date() });
});

app.get('/panel', (req, res) => {
  res.sendFile(path.join(__dirname, '../public/index.html'));
});

app.use('/api/location', require('./routes/location'));
app.use('/api/panel', require('./routes/panel'));
app.use('/api/history', require('./routes/history'));

const server = app.listen(PORT, '0.0.0.0', () => {
  console.log('🚀 Servidor corriendo en puerto ' + PORT);
});

process.on('SIGTERM', () => { server.close(() => console.log('Servidor detenido')); });
process.on('uncaughtException', (err) => { console.error('Error:', err); });
