import type { WorkspaceEntityType } from './snapshot';

export interface Price {
  id?: number;
  workspaceId: number;
  workspaceEntityType: WorkspaceEntityType;
  price: number;
}
