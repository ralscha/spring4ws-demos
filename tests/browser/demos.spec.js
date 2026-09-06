const { test, expect } = require('@playwright/test');

test.beforeEach(async ({ page }) => {
  // Test application assets independently of the external map tile service.
  await page.route('https://tile.openstreetmap.org/**', route => route.fulfill({
    contentType: 'image/png',
    body: Buffer.from('iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+/l9sAAAAASUVORK5CYII=', 'base64')
  }));
});

test('all public demos load local assets without JavaScript errors', async ({ page }) => {
  const errors = [];
  page.on('pageerror', error => errors.push(error.message));
  page.on('response', response => {
    if (/\.(js|css)(\?|$)/.test(response.url()) && response.status() >= 400) errors.push(response.url());
  });
  const demos = {
    '': '#demo-links', 'chat/': '#editor input', 'snake/': '#playground',
    'tail/': '#map svg', 'smoothie/': '#host1Cpu', 'map/': '#map_canvas',
    'bandwidth/': '#rec_graph', 'drawboard/': '#drawContainer canvas', 'tennis/': '#m1-comments'
  };
  for (const [demo, content] of Object.entries(demos)) {
    const response = await page.goto(demo + 'index.html');
    expect(response.status()).toBe(200);
    await expect(page.locator(content).first()).toBeVisible();
  }
  expect(errors).toEqual([]);
});

test('chat broadcasts between browsers and renders messages as text', async ({ browser }) => {
  const alice = await browser.newPage();
  const bob = await browser.newPage();
  try {
    for (const [page, name] of [[alice, 'Alice'], [bob, 'Bob']]) {
      await page.goto('chat/index.html');
      await page.locator('#editor input').fill(name);
      await page.locator('#editor input').press('Enter');
      await expect(page.locator('#content')).toContainText('The connection has been opened');
    }
    const message = '<b>Hello from Alice</b>';
    await alice.locator('#editor input').fill(message);
    await alice.locator('#editor input').press('Enter');
    await expect(bob.locator('#content')).toContainText(message);
    await expect(bob.locator('#content b')).toHaveCount(0);
  } finally {
    await alice.close();
    await bob.close();
  }
});

test('portfolio login, quotes, trade modal, reconnect and logout', async ({ page }) => {
  let connection;
  let connections = 0;
  await page.routeWebSocket('**/sockjs/**/websocket', route => {
    connection = route;
    connections++;
    route.connectToServer();
  });
  await page.goto('portfolio/index.html');
  await expect(page).toHaveURL(/login.html$/);
  await page.getByLabel('User name').fill('fabrice');
  await page.getByLabel('Password').fill('fab123');
  await page.getByRole('button', { name: 'Sign in', exact: true }).click();
  await expect(page).toHaveURL(/portfolio\/index.html$/);
  const rows = page.locator('tbody[data-bind] tr');
  await expect(rows).toHaveCount(4);
  const first = rows.first();
  const shares = Number(await first.locator('td').nth(5).textContent());
  await first.getByRole('button', { name: 'Buy', exact: true }).click();
  await expect(page.locator('#trade-dialog')).toBeVisible();
  await page.getByLabel('Shares', { exact: true }).fill('2');
  await page.locator('#trade-dialog').getByRole('button', { name: 'Buy', exact: true }).click();
  await expect(first.locator('td').nth(5)).toHaveText(String(shares + 2));
  await expect(page.locator('.alert')).toContainText('Position update');
  await expect(first.locator('td').nth(4)).toHaveText(/[↑↓]/);
  // Reconnect must replace the initial positions, not append duplicate rows.
  await connection.close();
  await expect.poll(() => connections).toBe(2);
  await expect(page.locator('.text-info')).toHaveText('fabrice');
  await expect(rows).toHaveCount(4);
  await page.getByRole('button', { name: 'Sign out' }).click();
  await expect(page).toHaveURL(/login.html\?logout$/);
  await page.goto('portfolio/index.html');
  await expect(page).toHaveURL(/login.html$/);
});

test('live tennis, bandwidth, map, smoothie and raw WebSockets receive data', async ({ page }) => {
  await page.goto('tennis/index.html');
  await expect(page.locator('#m1-status')).toHaveText('LIVE');
  await expect(page.locator('#m1-p1')).toHaveText('Ferrer D.');
  await page.getByText('Match Winner ?').first().click();
  await expect(page.locator('#m1-betmatchwinner')).toHaveText('Ferrer D.');

  await page.goto('bandwidth/index.html');
  await expect(page.locator('#rec_result')).toContainText('KBps');
  await page.goto('map/index.html');
  await expect(page.locator('.leaflet-marker-icon')).toHaveCount(2);
  await page.goto('smoothie/index.html');
  await expect.poll(() => page.evaluate(() => cpuDataSets.host1[0].data.length)).toBeGreaterThan(0);
  await page.goto('snake/index.html');
  await expect(page.locator('#console')).toContainText('WebSocket connection opened');
  await page.keyboard.press('ArrowRight');
  await expect.poll(() => page.evaluate(() => Object.values(Game.entities).some(snake => snake.snakeBody.length > 0))).toBe(true);
  await page.goto('drawboard/index.html');
  await expect(page.locator('#console')).toContainText('WebSocket connection opened');
});

test('SockJS HTTP fallback supports STOMP with CSRF protection', async ({ page }) => {
  await page.goto('tennis/index.html');
  const received = await page.evaluate(() => new Promise((resolve, reject) => {
    const client = DemoMessaging.createClient();
    client.webSocketFactory = () => new SockJS(DemoMessaging.url('sockjs'), null, { transports: ['xhr-streaming'] });
    client.onConnect = () => client.subscribe('/topic/networkinfo', async message => {
      const data = JSON.parse(message.body);
      await client.deactivate();
      resolve(data);
    });
    client.onStompError = reject;
    client.activate();
  }));
  expect(received).toEqual({ rec: expect.any(Number), snd: expect.any(Number) });
});

test('anonymous clients cannot subscribe to portfolio positions or forge quotes', async ({ page }) => {
  await page.goto('tennis/index.html');
  for (const action of ['positions', 'quote']) {
    const denied = await page.evaluate(action => new Promise(resolve => {
      const client = DemoMessaging.createClient();
      client.reconnectDelay = 0;
      client.onConnect = () => {
        if (action === 'positions') client.subscribe('/app/positions', () => resolve(false));
        else client.publish({ destination: '/topic/price.stock.AAPL', body: '{}' });
      };
      client.onStompError = async () => { await client.deactivate(); resolve(true); };
      client.activate();
    }), action);
    expect(denied).toBe(true);
  }
});
