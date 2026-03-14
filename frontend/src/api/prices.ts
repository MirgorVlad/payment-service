import client from './client';
import type { Price } from '../types/price';

export const getAll = (workspaceId?: number) => {
  const params = workspaceId ? { workspaceId } : {};
  return client.get<Price[]>('/prices', { params }).then((r) => r.data);
};

export const create = (data: Price) =>
  client.post<Price>('/prices', data).then((r) => r.data);

export const update = (id: number, data: Price) =>
  client.put<Price>(`/prices/${id}`, data).then((r) => r.data);

export const remove = (id: number) =>
  client.delete(`/prices/${id}`);
