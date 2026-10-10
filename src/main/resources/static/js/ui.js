// Common workspace interactions; page modules keep ownership of business actions.
export function initWorkspaceUI() {
  const menu = document.getElementById('menuToggle');
  const sidebar = document.getElementById('appSidebar');
  const closeMenu = () => {
    document.body.classList.remove('nav-open');
    menu.setAttribute('aria-expanded', 'false');
  };
  menu.addEventListener('click', () => {
    const open = document.body.classList.toggle('nav-open');
    menu.setAttribute('aria-expanded', String(open));
    if (open) sidebar.querySelector('[aria-current="page"]')?.focus();
  });
  document.getElementById('menuClose').addEventListener('click', () => { closeMenu(); menu.focus(); });
  sidebar.addEventListener('keydown', event => {
    if (event.key === 'Escape') { closeMenu(); menu.focus(); }
  });
  window.matchMedia('(min-width: 1024px)').addEventListener('change', closeMenu);

  const title = document.querySelector('.page-heading h1')?.textContent;
  if (title) document.title = `${title} · 안전보건 정보 모니터링`;

  // Existing dialogs toggle `hidden`. Observe that one state instead of duplicating
  // the save/cancel behavior of each business form.
  const overlays = [...document.querySelectorAll('.modal-overlay')];
  // Page templates place dialogs inside main; move them out before making the
  // background inert so the dialog itself remains interactive.
  overlays.forEach(overlay => document.body.append(overlay));
  const returnFocus = new Map();
  const openDialogs = [];
  const focusable = box => [...box.querySelectorAll('a[href], button, input, select, textarea, summary, [tabindex="0"]')]
    .filter(el => !el.disabled && el.getClientRects().length);
  const syncDialog = overlay => {
    overlays.forEach(item => { item.inert = false; });
    const box = overlay.querySelector('.modal-box');
    if (!overlay.hidden && !returnFocus.has(overlay)) {
      returnFocus.set(overlay, document.activeElement);
      openDialogs.push(overlay);
      (focusable(box)[0] || box).focus();
    } else if (overlay.hidden && returnFocus.has(overlay)) {
      const target = returnFocus.get(overlay);
      returnFocus.delete(overlay);
      openDialogs.splice(openDialogs.indexOf(overlay), 1);
      document.getElementById('appBody').inert = false;
      sidebar.inert = false;
      if (target?.isConnected) target.focus();
    }
    const active = openDialogs.at(-1);
    document.body.classList.toggle('has-modal', !!active);
    document.getElementById('appBody').inert = !!active;
    sidebar.inert = !!active;
    overlays.forEach(item => { item.inert = !!active && item !== active; });
  };
  overlays.forEach((overlay, index) => {
    const box = overlay.querySelector('.modal-box');
    const heading = box.querySelector('h2');
    box.setAttribute('role', 'dialog');
    box.setAttribute('aria-modal', 'true');
    box.tabIndex = -1;
    if (heading) {
      heading.id ||= `dialogTitle${index}`;
      box.setAttribute('aria-labelledby', heading.id);
    }
    new MutationObserver(() => syncDialog(overlay)).observe(overlay, { attributes: true, attributeFilter: ['hidden'] });
  });
  document.addEventListener('keydown', event => {
    const active = openDialogs.at(-1);
    if (!active) return;
    if (event.key === 'Escape') {
      event.preventDefault();
      event.stopImmediatePropagation();
      active.querySelector('.modal-close')?.click();
    } else if (event.key === 'Tab') {
      const targets = focusable(active);
      const first = targets[0], last = targets.at(-1);
      if (!first) { event.preventDefault(); return; }
      if (event.shiftKey && (document.activeElement === first || !active.contains(document.activeElement))) {
        event.preventDefault(); last.focus();
      } else if (!event.shiftKey && (document.activeElement === last || !active.contains(document.activeElement))) {
        event.preventDefault(); first.focus();
      }
    }
  }, true);
}
