import { WebPlugin } from '@capacitor/core';

import type { ShareInstagramPlugin } from './definitions';

export class ShareInstagramWeb extends WebPlugin implements ShareInstagramPlugin {
  async shareToStory(options: { imageUrl: string, appID: string }): Promise<void> {
    console.log('shareToStory', options);
  }
}
