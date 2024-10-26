import { WebPlugin } from '@capacitor/core';

import type { ShareInstagramPlugin } from './definitions';

export class ShareInstagramWeb extends WebPlugin implements ShareInstagramPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
