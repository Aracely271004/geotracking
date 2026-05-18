const express = require('express');
const router = express.Router();
const pool = require('../config/database');

router.get('/', async (req, res) => {
  try {
    const result = await pool.query(
      `SELECT DISTINCT ON (device_id) device_id, latitude, longitude, 
       battery_level, accuracy, speed, timestamp
       FROM locations ORDER BY device_id, timestamp DESC`
    );
    res.json({ success: true, data: result.rows });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
