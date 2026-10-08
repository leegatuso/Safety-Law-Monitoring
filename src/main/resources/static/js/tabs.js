// 상단 탭 전환 + 탭별로 어떤 로드 함수를 불러야 하는지 연결하는 라우터.
// 화면 전환 자체를 담당하므로 거의 모든 화면 모듈의 load*()를 가져온다.

import { state } from "./core.js";
import { loadDashboard } from "./dashboard.js";
import { loadNewsPage } from "./news-page.js";
import { loadLaws } from "./laws.js";
import { loadRevisions } from "./revisions.js";
import { loadKoshaGuides } from "./kosha-guide.js";
import { loadDocuments } from "./documents.js";
import { loadDocRevisions } from "./doc-revisions.js";
import { loadSettings } from "./settings.js";


const routes = { dashboard:'/dashboard', news:'/news', laws:'/laws', revisions:'/revisions', 'kosha-guides':'/kosha-guides', 'keyword-search':'/search', documents:'/documents', 'doc-revisions':'/document-impacts', 'document-revisions':'/document-revisions', settings:'/settings' };
export function activateTab(tab) { if (document.body.dataset.page !== tab) location.assign(routes[tab] || '/dashboard'); }
export function initTabs() {
  const legacy = location.hash.slice(1);
  if (routes[legacy] && legacy !== document.body.dataset.page) { location.replace(routes[legacy]); return; }
  const query = new URLSearchParams(location.search);
  if (document.body.dataset.page === 'revisions' && query.has('status')) document.getElementById('revisionStatusFilter').value = query.get('status');
  if (document.body.dataset.page === 'revisions' && query.has('law_id')) state.revisionsLawFilter = { id:Number(query.get('law_id')), name:query.get('name') || '선택 법령' };
}
export function viewRevisionsForLaw(lawId, lawName) { location.assign(`/revisions?law_id=${encodeURIComponent(lawId)}&name=${encodeURIComponent(lawName)}`); }
export function loadTab(tab) {
  if (tab !== document.body.dataset.page) return;
  if (tab === 'dashboard') return loadDashboard();
  if (tab === 'news') return loadNewsPage();
  if (tab === 'laws') return loadLaws();
  if (tab === 'revisions') return loadRevisions();
  if (tab === 'kosha-guides') return loadKoshaGuides();
  if (tab === 'documents') return loadDocuments();
  if (tab === 'doc-revisions') return loadDocRevisions();
  if (tab === 'settings') return loadSettings();
}
