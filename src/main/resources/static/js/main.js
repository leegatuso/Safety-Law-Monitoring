// 앱 진입점. 화면별 모듈들의 init*()/load*()를 불러와 DOMContentLoaded
// 시점에 한 번 배선하는 역할만 한다 - 실제 로직은 각 모듈 안에 있다.

import { initTabs, loadTab } from "./tabs.js";
import { initLawsTab } from "./laws.js";
import { initRevisionsTab } from "./revisions.js";
import { initKoshaGuideForm, initKoshaGuideSearch, initKoshaGuideSync, initKoshaGuideBulkImport, initKoshaGuideBulkDelete, initKoshaGuideContentCache } from "./kosha-guide.js";
import { initDocRevisionsTab } from "./doc-revisions.js";
import { initDocumentForm } from "./documents.js";
import { initSettingsForms } from "./settings.js";
import { initSyncButton } from "./sync.js";
import { initAlarmBell } from "./alarm.js";
import { initHelpModal } from "./help.js";
import { initKeywordSearch } from "./keyword-search.js";
import { initNewsBoard } from "./news.js";
import { initNewsPage } from "./news-page.js";
import { loadIntegrationStatus, initIntegrationStatusModal } from "./integration-status.js";


document.addEventListener("DOMContentLoaded", () => {
  initTabs();
  initSyncButton(); initAlarmBell(); initHelpModal(); initIntegrationStatusModal();
  const page = document.body.dataset.page;
  if (page === 'laws') initLawsTab();
  if (page === 'revisions') initRevisionsTab();
  if (page === 'kosha-guides') {
    initKoshaGuideForm(); initKoshaGuideSearch(); initKoshaGuideSync();
    initKoshaGuideBulkImport(); initKoshaGuideBulkDelete(); initKoshaGuideContentCache();
  }
  if (page === 'documents') initDocumentForm();
  if (page === 'doc-revisions') initDocRevisionsTab();
  if (page === 'settings') initSettingsForms();
  if (page === 'keyword-search' || page === 'dashboard') initKeywordSearch();
  if (page === 'dashboard') initNewsBoard();
  if (page === 'news') initNewsPage();
  loadTab(page); loadIntegrationStatus();
  document.addEventListener('visibilitychange', () => {
    if (document.visibilityState === 'visible') { loadTab(page); loadIntegrationStatus(); }
  });
});
