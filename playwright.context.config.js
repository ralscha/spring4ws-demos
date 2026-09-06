const config = require('./playwright.config');

module.exports = {
  ...config,
  use: { ...config.use, baseURL: 'http://localhost:18081/demos/' },
  webServer: {
    command: 'java -jar target/spring4ws-demos.jar --server.port=18081 --server.servlet.context-path=/demos',
    url: 'http://localhost:18081/demos/',
    reuseExistingServer: !process.env.CI,
    timeout: 60000
  }
};
