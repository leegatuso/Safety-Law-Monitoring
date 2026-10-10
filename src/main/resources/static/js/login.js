const form = document.getElementById('loginForm');
form?.addEventListener('submit', () => {
  const button = form.querySelector('[type="submit"]');
  button.disabled = true;
  button.setAttribute('aria-busy', 'true');
  button.textContent = '로그인 중…';
});
window.addEventListener('pageshow', () => {
  const button = form?.querySelector('[type="submit"]');
  if (!button) return;
  button.disabled = false;
  button.removeAttribute('aria-busy');
  button.textContent = '로그인';
});
