import { registerPlugin } from '@capacitor/core';

import type { ShareInstagramPlugin } from './definitions';

const ShareInstagram = registerPlugin<ShareInstagramPlugin>('ShareInstagram', {
  web: () => import('./web').then((m) => new m.ShareInstagramWeb()),
});

export * from './definitions';
export { ShareInstagram };
