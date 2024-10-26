export interface ShareInstagramPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
