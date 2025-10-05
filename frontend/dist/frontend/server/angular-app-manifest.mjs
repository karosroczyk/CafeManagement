
export default {
  bootstrap: () => import('./main.server.mjs').then(m => m.default),
  inlineCriticalCss: true,
  baseHref: '/',
  locale: undefined,
  routes: [
  {
    "renderMode": 2,
    "route": "/login"
  },
  {
    "renderMode": 2,
    "route": "/register"
  },
  {
    "renderMode": 2,
    "route": "/activate-account"
  },
  {
    "renderMode": 2,
    "route": "/cafe-client"
  },
  {
    "renderMode": 2,
    "route": "/cafe-client/menucafe"
  },
  {
    "renderMode": 2,
    "route": "/cafe-client/order"
  },
  {
    "renderMode": 2,
    "route": "/cafe-client/orderHistory"
  },
  {
    "renderMode": 2,
    "route": "/cafe-client/profile"
  },
  {
    "renderMode": 2,
    "route": "/cafe-admin"
  },
  {
    "renderMode": 2,
    "route": "/cafe-admin/profiles"
  },
  {
    "renderMode": 2,
    "route": "/cafe-admin/profile/*"
  },
  {
    "renderMode": 2,
    "route": "/cafe-employee"
  },
  {
    "renderMode": 2,
    "route": "/cafe-employee/menucafe"
  },
  {
    "renderMode": 2,
    "route": "/cafe-employee/orders"
  },
  {
    "renderMode": 2,
    "route": "/cafe-employee/profile"
  }
],
  assets: {
    'index.csr.html': {size: 29030, hash: 'b09db2429cdcbd3d0daf96c88b82140308eafd31b401c14669e80c7f9caffe6d', text: () => import('./assets-chunks/index_csr_html.mjs').then(m => m.default)},
    'index.server.html': {size: 17330, hash: '0c0a872f1c8c295d2ce2295c62c6d943458a4dbcf1e4493fed7d04d82e2897e2', text: () => import('./assets-chunks/index_server_html.mjs').then(m => m.default)},
    'register/index.html': {size: 38609, hash: '0cf7247f66a99f6094e435105dbdcc7495edca50186f6c2ae9f8e0776ae30c1e', text: () => import('./assets-chunks/register_index_html.mjs').then(m => m.default)},
    'cafe-client/menucafe/index.html': {size: 29249, hash: '734c0370ed0f465af56fefcc51060fc512822f1807a9fbc1151e936dc5e40774', text: () => import('./assets-chunks/cafe-client_menucafe_index_html.mjs').then(m => m.default)},
    'cafe-client/order/index.html': {size: 29249, hash: '734c0370ed0f465af56fefcc51060fc512822f1807a9fbc1151e936dc5e40774', text: () => import('./assets-chunks/cafe-client_order_index_html.mjs').then(m => m.default)},
    'cafe-client/index.html': {size: 29249, hash: '734c0370ed0f465af56fefcc51060fc512822f1807a9fbc1151e936dc5e40774', text: () => import('./assets-chunks/cafe-client_index_html.mjs').then(m => m.default)},
    'activate-account/index.html': {size: 36780, hash: '031acd1d29e1849dc6493ad169f31db3ce79da72df7d71b91bc8ad71490edd24', text: () => import('./assets-chunks/activate-account_index_html.mjs').then(m => m.default)},
    'login/index.html': {size: 38009, hash: '9c9acaefb2ffbd502c0a011c68d71690e1b08cbe733bf19f8a554ff6713a4f66', text: () => import('./assets-chunks/login_index_html.mjs').then(m => m.default)},
    'cafe-client/orderHistory/index.html': {size: 29249, hash: '734c0370ed0f465af56fefcc51060fc512822f1807a9fbc1151e936dc5e40774', text: () => import('./assets-chunks/cafe-client_orderHistory_index_html.mjs').then(m => m.default)},
    'cafe-admin/profiles/index.html': {size: 29249, hash: '734c0370ed0f465af56fefcc51060fc512822f1807a9fbc1151e936dc5e40774', text: () => import('./assets-chunks/cafe-admin_profiles_index_html.mjs').then(m => m.default)},
    'cafe-client/profile/index.html': {size: 29249, hash: '734c0370ed0f465af56fefcc51060fc512822f1807a9fbc1151e936dc5e40774', text: () => import('./assets-chunks/cafe-client_profile_index_html.mjs').then(m => m.default)},
    'cafe-admin/index.html': {size: 29249, hash: '734c0370ed0f465af56fefcc51060fc512822f1807a9fbc1151e936dc5e40774', text: () => import('./assets-chunks/cafe-admin_index_html.mjs').then(m => m.default)},
    'cafe-employee/index.html': {size: 29249, hash: '734c0370ed0f465af56fefcc51060fc512822f1807a9fbc1151e936dc5e40774', text: () => import('./assets-chunks/cafe-employee_index_html.mjs').then(m => m.default)},
    'cafe-employee/menucafe/index.html': {size: 29249, hash: '734c0370ed0f465af56fefcc51060fc512822f1807a9fbc1151e936dc5e40774', text: () => import('./assets-chunks/cafe-employee_menucafe_index_html.mjs').then(m => m.default)},
    'cafe-employee/profile/index.html': {size: 29249, hash: '734c0370ed0f465af56fefcc51060fc512822f1807a9fbc1151e936dc5e40774', text: () => import('./assets-chunks/cafe-employee_profile_index_html.mjs').then(m => m.default)},
    'cafe-employee/orders/index.html': {size: 29249, hash: '734c0370ed0f465af56fefcc51060fc512822f1807a9fbc1151e936dc5e40774', text: () => import('./assets-chunks/cafe-employee_orders_index_html.mjs').then(m => m.default)},
    'styles-UKEM4DS6.css': {size: 342829, hash: 'BDjbw66B5s0', text: () => import('./assets-chunks/styles-UKEM4DS6_css.mjs').then(m => m.default)}
  },
};
