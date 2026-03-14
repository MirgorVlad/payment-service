import client from './client';
import type { BillingRecord, TimeInterval } from '../types/billing';

export const getAll = (workspaceId?: number) => {
  const params = workspaceId ? { workspaceId } : {};
  return client.get<BillingRecord[]>('/billing', { params }).then((r) => r.data);
};

export const generate = (workspaceId: number, interval: TimeInterval) =>
  client.post<BillingRecord>(`/billing/generate/${workspaceId}`, interval).then((r) => r.data);

export const remove = (id: number) =>
  client.delete(`/billing/${id}`);
