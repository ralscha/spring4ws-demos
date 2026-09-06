import { copyFile, cp, mkdir, readFile, writeFile } from 'node:fs/promises';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const destination = resolve(root, 'src/main/resources/static/vendor');
const assets = {
  '@stomp/stompjs/bundles/stomp.umd.min.js': 'stomp.umd.min.js',
  'sockjs-client/dist/sockjs.min.js': 'sockjs.min.js',
  'sockjs-client/dist/sockjs.min.js.map': 'sockjs.min.js.map',
  'jquery/dist/jquery.min.js': 'jquery.min.js',
  'jquery/dist/jquery.min.map': 'jquery.min.map',
  'bootstrap/dist/css/bootstrap.min.css': 'bootstrap.min.css',
  'bootstrap/dist/css/bootstrap.min.css.map': 'bootstrap.min.css.map',
  'bootstrap/dist/js/bootstrap.bundle.min.js': 'bootstrap.bundle.min.js',
  'bootstrap/dist/js/bootstrap.bundle.min.js.map': 'bootstrap.bundle.min.js.map',
  'knockout/build/output/knockout-latest.js': 'knockout.js',
  'smoothie/smoothie.js': 'smoothie.js',
  'raphael/raphael.min.js': 'raphael.min.js',
  'leaflet/dist/leaflet.js': 'leaflet/leaflet.js',
  'leaflet/dist/leaflet.js.map': 'leaflet/leaflet.js.map',
  'leaflet/dist/leaflet.css': 'leaflet/leaflet.css'
};

for (const [source, target] of Object.entries(assets)) {
  const output = resolve(destination, target);
  await mkdir(dirname(output), { recursive: true });
  await copyFile(resolve(root, 'node_modules', source), output);
}
await cp(resolve(root, 'node_modules/leaflet/dist/images'), resolve(destination, 'leaflet/images'), { recursive: true });

const { dependencies } = JSON.parse(await readFile(resolve(root, 'package.json'), 'utf8'));
const licenses = {
  '@stomp/stompjs': 'LICENSE', bootstrap: 'LICENSE', jquery: 'LICENSE.txt',
  knockout: 'LICENSE', leaflet: 'LICENSE', raphael: 'license.txt',
  smoothie: 'LICENSE.txt', 'sockjs-client': 'LICENSE'
};
await mkdir(resolve(destination, 'licenses'), { recursive: true });
for (const [name, version] of Object.entries(dependencies)) {
  const installed = JSON.parse(await readFile(resolve(root, 'node_modules', name, 'package.json'), 'utf8'));
  if (installed.version !== version) throw new Error(`Run npm ci: ${name} is not ${version}`);
  await copyFile(resolve(root, 'node_modules', name, licenses[name]),
    resolve(destination, 'licenses', name.replaceAll('/', '-').replace('@', '') + '.txt'));
}
await writeFile(resolve(destination, 'versions.json'), JSON.stringify(dependencies, null, 2) + '\n');
console.log('Browser assets copied to src/main/resources/static/vendor');
