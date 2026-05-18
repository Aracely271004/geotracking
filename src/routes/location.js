const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const { saveLocation, getLocations, getDevices } = require('../controllers/locationController');

router.post('/', auth, saveLocation);
router.get('/devices', auth, getDevices);
router.get('/:device_id', auth, getLocations);

module.exports = router;
