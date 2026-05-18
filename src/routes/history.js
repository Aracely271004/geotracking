const express = require('express');
const router = express.Router();
const pool = require('../config/database');

router.get('/:device_id', async (req, res) => {
  const { device_id } = req.params;
  const limit = req.query.limit || 500;
  const from = req.query.from || null;
  const to = req.query.to || null;

  try {
    let query, params;
    if (from && to) {
      query = `SELECT latitude, longitude, timestamp, battery_level, accuracy, speed
               FROM locations WHERE device_id = $1
               AND timestamp >= $2 AND timestamp <= $3
               ORDER BY timestamp ASC LIMIT $4`;
      params = [device_id, from, to, limit];
    } else {
      query = `SELECT latitude, longitude, timestamp, battery_level, accuracy, speed
               FROM locations WHERE device_id = $1
               ORDER BY timestamp DESC LIMIT $2`;
      params = [device_id, limit];
    }
    const result = await pool.query(query, params);
    const data = from && to ? result.rows : result.rows.reverse();
    res.json({ success: true, data, total: data.length });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
