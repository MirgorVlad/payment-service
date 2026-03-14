import client from './client';
import type { Snapshot } from '../types/snapshot';

export const getAll = (workspaceId?: number) => {
  const params = workspaceId ? { workspaceId } : {};
  return client.get<Snapshot[]>('/snapshots', { params }).then((r) => r.data);
};

export const create = (data: Snapshot) =>
  client.post<Snapshot>('/snapshots', data).then((r) => r.data);

export const update = (id: number, data: Snapshot) =>
  client.put<Snapshot>(`/snapshots/${id}`, data).then((r) => r.data);

export const remove = (id: number) =>
  client.delete(`/snapshots/${id}`);
