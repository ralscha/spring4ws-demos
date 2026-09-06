const { defineConfig } = require('@playwright/test');
const baseURL = (process.env.DEMO_BASE_URL || 'http://localhost:18080').replace(/\/?$/, '/');

module.exports = defineConfig({
  testDir: './tests/browser',
  fullyParallel: false,
  workers: 1,
  timeout: 30000,
  expect: { timeout: 10000 },
  use: {
    baseURL,
    browserName: 'chromium',
    channel: 'chromium',
    trace: 'retain-on-failure'
  },
  webServer: process.env.DEMO_BASE_URL ? undefined : {
    command: 'java -jar target/spring4ws-demos.jar --server.port=18080',
    url: 'http://localhost:18080',
    reuseExistingServer: !process.env.CI,
    timeout: 60000
  }
});
