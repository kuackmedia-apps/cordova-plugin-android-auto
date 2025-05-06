#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const xml2js = require('xml2js');

module.exports = function(context) {
  const manifestPath = path.join(context.opts.projectRoot, 'platforms/android/app/src/main/AndroidManifest.xml');

  fs.readFile(manifestPath, 'utf8', (err, data) => {
    if (err) {
      console.error('Error leyendo AndroidManifest.xml', err);
      return;
    }

    xml2js.parseString(data, (err, result) => {
      if (err) {
        console.error('Error parseando AndroidManifest.xml', err);
        return;
      }

      const manifest = result['manifest'];
      const application = manifest['application'][0];

      if (!application['service']) application['service'] = [];

      const existingServices = application['service'].filter(service =>
        service['$']['android:name'] === 'com.kuackmedia.androidauto.AndroidAutoService'
      );

      if (existingServices.length === 0) {
        application['service'].push({
          '$': {
            'android:name': 'com.kuackmedia.androidauto.AndroidAutoService',
            'android:exported': 'true'
          },
          'intent-filter': [{
            'action': [{
              '$': { 'android:name': 'android.media.browse.MediaBrowserService' }
            }]
          }]
        });
        console.log('✅ AndroidAutoService agregado correctamente.');
      } else {
        console.log('🔍 AndroidAutoService ya registrado.');
      }

      // Declarar metadata Android Auto claramente
      if (!application['meta-data']) application['meta-data'] = [];

      const existingMetaData = application['meta-data'].filter(meta =>
        meta['$']['android:name'] === 'com.google.android.gms.car.application'
      );

      if (existingMetaData.length === 0) {
        application['meta-data'].push({
          '$': {
            'android:name': 'com.google.android.gms.car.application',
            'android:resource': '@xml/automotive_app_desc'
          }
        });
        console.log('✅ Metadata Android Auto agregada correctamente.');
      } else {
        console.log('🔍 Metadata Android Auto ya existente.');
      }

      const builder = new xml2js.Builder();
      const xml = builder.buildObject(result);

      fs.writeFile(manifestPath, xml, (err) => {
        if (err) {
          console.error('Error escribiendo AndroidManifest.xml', err);
        } else {
          console.log('🎉 AndroidManifest.xml actualizado correctamente.');
        }
      });
    });
  });
};
