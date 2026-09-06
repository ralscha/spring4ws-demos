/* Shared STOMP configuration. Paths also work below a servlet context path. */
window.DemoMessaging = (() => {
  const base = new URL('./', document.currentScript.src);
  const url = path => new URL(path, base).href;

  async function csrf() {
    const response = await fetch(url('csrf'), { cache: 'no-store' });
    if (!response.ok) throw new Error('Unable to get CSRF token');
    return response.json();
  }

  function createClient() {
    const client = new StompJs.Client({
      webSocketFactory: () => new SockJS(url('sockjs')),
      reconnectDelay: 5000,
      heartbeatIncoming: 0,
      heartbeatOutgoing: 10000,
      onStompError: frame => console.error('Messaging error:', frame.headers.message)
    });
    client.beforeConnect = async () => {
      const token = await csrf();
      client.connectHeaders = { [token.headerName]: token.token };
    };
    window.addEventListener('pagehide', () => { void client.deactivate(); });
    return client;
  }

  async function submitForm(form) {
    const token = await csrf();
    const input = document.createElement('input');
    input.type = 'hidden';
    input.name = token.parameterName;
    input.value = token.token;
    form.append(input);
    form.submit();
  }

  return { createClient, csrf, submitForm, url };
})();
