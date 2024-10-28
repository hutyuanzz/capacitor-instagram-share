export interface ShareInstagramPlugin {
  shareToStory(options: { imageUrl: string, appID: string }): Promise<void>;
}
