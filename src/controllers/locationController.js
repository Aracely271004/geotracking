const pool = require('../config/database');

const saveLocation = async (req, res) => {
  const { device_id, latitude, longitude, accuracy, speed, altitude, battery_level } = req.body;
  if (!device_id || !latitude || !longitude)
    return res.status(400).json({ error: 'device_id, latitude y longitude son requeridos' });
  try {
    const result = await pool.query(
      `INSERT INTO locations (device_id, latitude, longitude, accuracy, speed, altitude, battery_level)
       VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING *`,
      [device_id, latitude, longitude, accuracy, speed, altitude, battery_level]
    );
    res.status(201).json({ success: true, data: result.rows[0] });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

const getLocations = async (req, res) => {
  const { device_id } = req.params;
  try {
    const result = await pool.query(
      `SELECT * FROM locations WHERE device_id = $1 ORDER BY timestamp DESC LIMIT 100`,
      [device_id]
    );
    res.json({ success: true, data: result.rows });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

const getDevices = async (req, res) => {
  try {
    const result = await pool.query(
      `SELECT DISTINCT ON (device_id) device_id, latitude, longitude, battery_level, timestamp
       FROM locations ORDER BY device_id, timestamp DESC`
    );
    res.json({ success: true, data: result.rows });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

module.exports = { saveLocation, getLocations, getDevices };
