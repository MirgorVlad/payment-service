import client from './client';
import type { Workspace } from '../types/workspace';
import type { Snapshot } from '../types/snapshot';

export const getAll = () =>
  client.get<Workspace[]>('/workspaces').then((r) => r.data);

export const getById = (id: number) =>
  client.get<Workspace>(`/workspaces/${id}`).then((r) => r.data);

export const create = (data: Workspace) =>
  client.post<Workspace>('/workspaces', data).then((r) => r.data);

export const update = (id: number, data: Workspace) =>
  client.put<Workspace>(`/workspaces/${id}`, data).then((r) => r.data);

export const remove = (id: number) =>
  client.delete(`/workspaces/${id}`);

export const syncAll = () =>
  client.post('/workspaces/sync').then((r) => r.data);

export const takeSnapshot = () =>
  client.post<Snapshot[]>('/workspaces/snapshot').then((r) => r.data);
