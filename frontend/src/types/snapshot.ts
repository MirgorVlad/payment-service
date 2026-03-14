export type WorkspaceEntityType = 'DEVICE' | 'ASSET' | 'CUSTOMER';

export interface Snapshot {
  id?: number;
  syncId?: string;
  workspaceId: number;
  workspaceEntityType: WorkspaceEntityType;
  count: number;
  snapshotTime?: string;
}
